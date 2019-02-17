/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.jira.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraProject.JiraComponent;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraProject.JiraVersion;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraSecurity.JiraUser;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraIssueLinkType;

/**
 * Represents a Jira issue.
 * @author Tony Washer
 */
public class ThemisJiraIssue
        extends ThemisJiraShortIssue {
    /**
     * Parent of Issue.
     */
    private final JiraIssueReference theParent;

    /**
     * Assignee of Issue.
     */
    private final JiraUser theAssignee;

    /**
     * Reporter of Issue.
     */
    private final JiraUser theReporter;

    /**
     * Creator of Issue.
     */
    private final JiraUser theCreator;

    /**
     * Environment.
     */
    private final String theEnvironment;

    /**
     * Creation Date of Issue.
     */
    private final LocalDateTime theCreated;

    /**
     * Due Date of Issue.
     */
    private final LocalDateTime theDueDate;

    /**
     * Last Updated Date of Issue.
     */
    private final LocalDateTime theUpdated;

    /**
     * Resolved Date of Issue.
     */
    private final LocalDateTime theResolved;

    /**
     * IssueLinks.
     */
    private final List<JiraIssueLink> theIssueLinks;

    /**
     * SubTasks.
     */
    private final List<JiraIssueReference> theSubTasks;

    /**
     * Components.
     */
    private final List<JiraComponent> theComponents;

    /**
     * Affected Versions.
     */
    private final List<JiraVersion> theAffectsVers;

    /**
     * Fix Versions.
     */
    private final List<JiraVersion> theFixVers;

    /**
     * Labels.
     */
    private final List<String> theLabels;

    /**
     * Constructor.
     * @param pServer the server
     * @param pProject the project
     * @param pIssue the underlying issue
     * @throws OceanusException on error
     */
    protected ThemisJiraIssue(final ThemisJiraServer pServer,
                              final ThemisJiraProject pProject,
                              final JSONObject pIssue) throws OceanusException {
        /* Store parameters */
        super(pServer, pProject, pIssue);

        /* Protect against exceptions */
        try {
            /* Access fields */
            final JSONObject myFields = pIssue.getJSONObject("fields");
            theEnvironment = myFields.optString("environment", null);
            theCreated = ThemisJiraServer.parseJiraDateTime(myFields.getString("created"));
            theDueDate = ThemisJiraServer.parseJiraDateTime(myFields.optString("duedate", null));
            theUpdated = ThemisJiraServer.parseJiraDateTime(myFields.getString("updated"));
            theResolved = ThemisJiraServer.parseJiraDateTime(myFields.optString("resolutiondate", null));

            /* Determine the assignee and reporter */
            JSONObject myObject = myFields.getJSONObject("creator");
            theCreator = getServer().getUser(myObject.getString(JiraUser.FIELD_NAME));
            myObject = myFields.getJSONObject("reporter");
            theReporter = getServer().getUser(myObject.getString(JiraUser.FIELD_NAME));
            myObject = myFields.optJSONObject("assignee");
            theAssignee = myObject == null
                                           ? null
                                           : getServer().getUser(myObject.getString(JiraUser.FIELD_NAME));

            /* Create the lists */
            theIssueLinks = new ArrayList<>();
            theSubTasks = new ArrayList<>();
            theComponents = new ArrayList<>();
            theFixVers = new ArrayList<>();
            theAffectsVers = new ArrayList<>();
            theLabels = new ArrayList<>();

            /* Populate the issue link fields */
            JSONArray myArray = myFields.getJSONArray("issuelinks");
            int iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                final JSONObject myLinkDtl = myArray.getJSONObject(i);
                final JiraIssueLink myLink = new JiraIssueLink(myLinkDtl);
                theIssueLinks.add(myLink);
            }

            /* Populate the subTask fields */
            myArray = myFields.getJSONArray("subtasks");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                final JSONObject myTaskDtl = myArray.getJSONObject(i);
                final JiraIssueReference myRef = new JiraIssueReference(myTaskDtl.getString(FIELD_KEY));
                theSubTasks.add(myRef);
            }

            /* Populate the labels fields */
            myArray = myFields.getJSONArray("labels");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                final String myLabel = myArray.getString(i);
                theLabels.add(myLabel);
            }

            /* Populate the components field */
            myArray = myFields.getJSONArray("components");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                final JSONObject myCompDtl = myArray.getJSONObject(i);
                final JiraComponent myComp = getProject().getComponent(myCompDtl.getString(JiraComponent.FIELD_NAME));
                theComponents.add(myComp);
            }

            /* Populate the affects versions field */
            myArray = myFields.getJSONArray("versions");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                final JSONObject myVersDtl = myArray.getJSONObject(i);
                final JiraVersion myVers = getProject().getVersion(myVersDtl.getString(JiraVersion.FIELD_NAME));
                theAffectsVers.add(myVers);
            }

            /* Populate the fix versions field */
            myArray = myFields.getJSONArray("fixVersions");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                final JSONObject myVersDtl = myArray.getJSONObject(i);
                final JiraVersion myVers = getProject().getVersion(myVersDtl.getString(JiraVersion.FIELD_NAME));
                theFixVers.add(myVers);
            }

            /* Resolve parent */
            if (getIssueType().isSubTask()) {
                final JSONObject myParDtl = myFields.getJSONObject("parent");
                theParent = new JiraIssueReference(myParDtl.getString(FIELD_KEY));
            } else {
                theParent = null;
            }

        } catch (JSONException e) {
            /* Pass the exception on */
            throw new ThemisIOException("Failed to parse issue", e);
        }
    }

    /**
     * Get the environment of the issue.
     * @return the environment
     */
    public String getEnvironment() {
        return theEnvironment;
    }

    /**
     * Get the parent of the issue.
     * @return the parent
     * @throws OceanusException on error
     */
    public ThemisJiraIssue getParent() throws OceanusException {
        return theParent == null
                                 ? null
                                 : theParent.getIssue();
    }

    /**
     * Get the creator.
     * @return the creator
     */
    public JiraUser getCreator() {
        return theCreator;
    }

    /**
     * Get the reporter.
     * @return the reporter
     */
    public JiraUser getReporter() {
        return theReporter;
    }

    /**
     * Get the assignee.
     * @return the assignee
     */
    public JiraUser getAssignee() {
        return theAssignee;
    }

    /**
     * Get the createdDate of the issue.
     * @return the createdDate
     */
    public LocalDateTime getCreatedOn() {
        return theCreated;
    }

    /**
     * Get the updatedDate of the issue.
     * @return the updatedDate
     */
    public LocalDateTime getLastUpdated() {
        return theUpdated;
    }

    /**
     * Get the dueDate of the issue.
     * @return the dueDate
     */
    public LocalDateTime getDueDate() {
        return theDueDate;
    }

    /**
     * Get the resolved date of the issue.
     * @return the resolvedDate
     */
    public LocalDateTime getResolvedDate() {
        return theResolved;
    }

    /**
     * Get the issue links iterator.
     * @return the iterator
     */
    public Iterator<JiraIssueLink> issueLinkIterator() {
        return theIssueLinks.iterator();
    }

    /**
     * Get the subTasks iterator.
     * @return the iterator
     */
    public Iterator<JiraIssueReference> subTaskIterator() {
        return theSubTasks.iterator();
    }

    /**
     * Get the components iterator.
     * @return the iterator
     */
    public Iterator<JiraComponent> componentIterator() {
        return theComponents.iterator();
    }

    /**
     * Get the affected Versions iterator.
     * @return the iterator
     */
    public Iterator<JiraVersion> affectVersionIterator() {
        return theAffectsVers.iterator();
    }

    /**
     * Get the fix Versions iterator.
     * @return the iterator
     */
    public Iterator<JiraVersion> fixVersionIterator() {
        return theFixVers.iterator();
    }

    /**
     * Get the labels iterator.
     * @return the iterator
     */
    public Iterator<String> labelIterator() {
        return theLabels.iterator();
    }

    /**
     * Issue Reference.
     */
    public final class JiraIssueReference {
        /**
         * IssueKey.
         */
        private final String theKey;

        /**
         * Issue.
         */
        private ThemisJiraIssue theIssue;

        /**
         * Constructor.
         * @param pKey the issue key
         */
        JiraIssueReference(final String pKey) {
            /* record the details */
            theKey = pKey;
        }

        /**
         * Get the issue key.
         * @return the issue key
         */
        public String getKey() {
            return theKey;
        }

        /**
         * Get the issue.
         * @return the issue
         * @throws OceanusException on error
         */
        public ThemisJiraIssue getIssue() throws OceanusException {
            if (theIssue == null) {
                theIssue = getServer().getIssue(theKey);
            }
            return theIssue;
        }
    }

    /**
     * Issue Links.
     */
    public final class JiraIssueLink {
        /**
         * The type of the link.
         */
        private final JiraIssueLinkType theType;

        /**
         * Linked Issue.
         */
        private final JiraIssueReference theReference;

        /**
         * Constructor.
         * @param pLink the underlying link
         * @throws OceanusException on error
         */
        JiraIssueLink(final JSONObject pLink) throws OceanusException {
            /* Access the details */
            final JSONObject myLinkDtl = pLink.getJSONObject("issuelinktype");
            theType = getServer().getIssueLinkType(myLinkDtl.getString(JiraIssueLinkType.FIELD_NAME));

            /* Determine the target issue */
            final JSONObject myIssueDtl = pLink.getJSONObject("issue");
            theReference = new JiraIssueReference(myIssueDtl.getString(FIELD_KEY));
        }

        /**
         * Get the link type.
         * @return the link type
         */
        public JiraIssueLinkType getLinkType() {
            return theType;
        }

        /**
         * Get the issue key.
         * @return the issue
         */
        public String getIssueKey() {
            return theReference.getKey();
        }

        /**
         * Get the issue.
         * @return the issue
         * @throws OceanusException on error
         */
        public ThemisJiraIssue getIssue() throws OceanusException {
            return theReference.getIssue();
        }
    }
}
