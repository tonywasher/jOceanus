/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.jira.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.http.JiraClient;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.jira.data.JiraProject.JiraComponent;
import net.sourceforge.joceanus.jthemis.jira.data.JiraProject.JiraVersion;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraUser;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraIssueLinkType;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraIssueType;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraKeyedIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraNamedDescIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraPriority;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraResolution;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Jira issue.
 * @author Tony Washer
 */
public class JiraIssue
        extends JiraKeyedIdObject {
    /**
     * Server.
     */
    private final JiraServer theServer;

    /**
     * Project of Issue.
     */
    private final JiraProject theProject;

    /**
     * Parent of Issue.
     */
    private final JiraIssueReference theParent;

    /**
     * Summary of Issue.
     */
    private final String theSummary;

    /**
     * Description of Issue.
     */
    private final String theDesc;

    /**
     * Type of Issue.
     */
    private final JiraIssueType theIssueType;

    /**
     * Status of Issue.
     */
    private final JiraStatus theStatus;

    /**
     * Resolution of Issue.
     */
    private final JiraResolution theResolution;

    /**
     * Priority of Issue.
     */
    private final JiraPriority thePriority;

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
     * @param pIssue the underlying issue
     * @throws JOceanusException on error
     */
    protected JiraIssue(final JiraServer pServer,
                        final JSONObject pIssue) throws JOceanusException {
        /* Store parameters */
        super(pIssue);
        theServer = pServer;

        /* Protect against exceptions */
        try {
            /* Access fields */
            JSONObject myFields = pIssue.getJSONObject("fields");
            theSummary = myFields.getString("summary");
            theDesc = myFields.optString(JiraNamedDescIdObject.FIELD_DESC, null);
            theEnvironment = myFields.optString("environment", null);
            theCreated = JiraServer.parseJiraDateTime(myFields.getString("created"));
            theDueDate = JiraServer.parseJiraDateTime(myFields.optString("duedate", null));
            theUpdated = JiraServer.parseJiraDateTime(myFields.getString("updated"));
            theResolved = JiraServer.parseJiraDateTime(myFields.optString("resolutiondate", null));

            /* Determine the project */
            JSONObject myObject = myFields.getJSONObject(JiraClient.JIRANAME_PROJECT);
            theProject = theServer.getProject(myObject.getString(JiraProject.FIELD_KEY));

            /* Determine Status etc */
            myObject = myFields.getJSONObject(JiraClient.JIRANAME_ISSUETYPE);
            theIssueType = theServer.getIssueType(myObject.getString(JiraIssueType.FIELD_NAME));
            myObject = myFields.getJSONObject(JiraClient.JIRANAME_STATUS);
            theStatus = theServer.getStatus(myObject.getString(JiraStatus.FIELD_NAME));
            myObject = myFields.getJSONObject(JiraClient.JIRANAME_PRIORITY);
            thePriority = theServer.getPriority(myObject.getString(JiraStatus.FIELD_NAME));
            myObject = myFields.optJSONObject(JiraClient.JIRANAME_RESOLUTION);
            theResolution = (myObject == null)
                                              ? null
                                              : theServer.getResolution(myObject.getString(JiraResolution.FIELD_NAME));

            /* Determine the assignee and reporter */
            myObject = myFields.getJSONObject("creator");
            theCreator = theServer.getUser(myObject.getString(JiraUser.FIELD_NAME));
            myObject = myFields.getJSONObject("reporter");
            theReporter = theServer.getUser(myObject.getString(JiraUser.FIELD_NAME));
            myObject = myFields.optJSONObject("assignee");
            theAssignee = (myObject == null)
                                            ? null
                                            : theServer.getUser(myObject.getString(JiraUser.FIELD_NAME));

            /* Create the lists */
            theIssueLinks = new ArrayList<JiraIssueLink>();
            theSubTasks = new ArrayList<JiraIssueReference>();
            theComponents = new ArrayList<JiraComponent>();
            theFixVers = new ArrayList<JiraVersion>();
            theAffectsVers = new ArrayList<JiraVersion>();
            theLabels = new ArrayList<String>();

            /* Populate the issue link fields */
            JSONArray myArray = myFields.getJSONArray("issuelinks");
            int iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                JSONObject myLinkDtl = myArray.getJSONObject(i);
                JiraIssueLink myLink = new JiraIssueLink(myLinkDtl);
                theIssueLinks.add(myLink);
            }

            /* Populate the subTask fields */
            myArray = myFields.getJSONArray("subtasks");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                JSONObject myTaskDtl = myArray.getJSONObject(i);
                JiraIssueReference myRef = new JiraIssueReference(myTaskDtl.getString(FIELD_KEY));
                theSubTasks.add(myRef);
            }

            /* Populate the labels fields */
            myArray = myFields.getJSONArray("labels");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                String myLabel = myArray.getString(i);
                theLabels.add(myLabel);
            }

            /* Populate the components field */
            myArray = myFields.getJSONArray("components");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                JSONObject myCompDtl = myArray.getJSONObject(i);
                JiraComponent myComp = theProject.getComponent(myCompDtl.getString(JiraComponent.FIELD_NAME));
                theComponents.add(myComp);
            }

            /* Populate the affects versions field */
            myArray = myFields.getJSONArray("versions");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                JSONObject myVersDtl = myArray.getJSONObject(i);
                JiraVersion myVers = theProject.getVersion(myVersDtl.getString(JiraVersion.FIELD_NAME));
                theAffectsVers.add(myVers);
            }

            /* Populate the fix versions field */
            myArray = myFields.getJSONArray("fixVersions");
            iNumEntries = myArray.length();
            for (int i = 0; i < iNumEntries; i++) {
                JSONObject myVersDtl = myArray.getJSONObject(i);
                JiraVersion myVers = theProject.getVersion(myVersDtl.getString(JiraVersion.FIELD_NAME));
                theFixVers.add(myVers);
            }

            /* Resolve parent */
            if (theIssueType.isSubTask()) {
                JSONObject myParDtl = myFields.getJSONObject("parent");
                theParent = new JiraIssueReference(myParDtl.getString(FIELD_KEY));
            } else {
                theParent = null;
            }

        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to parse issue", e);
        }
    }

    /**
     * Get the summary.
     * @return the summary
     */
    public String getSummary() {
        return theSummary;
    }

    /**
     * Get the description of the issue.
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Get the environment of the issue.
     * @return the environment
     */
    public String getEnvironment() {
        return theEnvironment;
    }

    /**
     * Get the project of the issue.
     * @return the project
     */
    public JiraProject getProject() {
        return theProject;
    }

    /**
     * Get the parent of the issue.
     * @return the parent
     * @throws JOceanusException on error
     */
    public JiraIssue getParent() throws JOceanusException {
        return theParent == null
                                ? null
                                : theParent.getIssue();
    }

    /**
     * Get the issue type of the issue.
     * @return the issue type
     */
    public JiraIssueType getIssueType() {
        return theIssueType;
    }

    /**
     * Get the status of the issue.
     * @return the status
     */
    public JiraStatus getStatus() {
        return theStatus;
    }

    /**
     * Get the resolution of the issue.
     * @return the resolution
     */
    public JiraResolution getResolution() {
        return theResolution;
    }

    /**
     * Get the priority of the issue.
     * @return the priority
     */
    public JiraPriority getPriority() {
        return thePriority;
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
        private JiraIssue theIssue;

        /**
         * Constructor.
         * @param pKey the issue key
         * @throws JOceanusException on error
         */
        private JiraIssueReference(final String pKey) throws JOceanusException {
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
         * @throws JOceanusException on error
         */
        public JiraIssue getIssue() throws JOceanusException {
            if (theIssue == null) {
                theIssue = theServer.getIssue(theKey);
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
         * @throws JOceanusException on error
         */
        private JiraIssueLink(final JSONObject pLink) throws JOceanusException {
            /* Access the details */
            JSONObject myLinkDtl = pLink.getJSONObject("issuelinktype");
            theType = theServer.getIssueLinkType(myLinkDtl.getString(JiraIssueLinkType.FIELD_NAME));

            /* Determine the target issue */
            JSONObject myIssueDtl = pLink.getJSONObject("issue");
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
         * @throws JOceanusException on error
         */
        public JiraIssue getIssue() throws JOceanusException {
            return theReference.getIssue();
        }
    }
}
