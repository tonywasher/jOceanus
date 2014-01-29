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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.JThemisLogicException;
import net.sourceforge.joceanus.jthemis.jira.data.JiraProject.JiraComponent;
import net.sourceforge.joceanus.jthemis.jira.data.JiraProject.JiraVersion;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraUser;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraEntity;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraIssueLinks;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraIssueType;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraPriority;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraResolution;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraStatus;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType.Direction;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;

/**
 * Represents a Jira issue.
 * @author Tony Washer
 */
public class JiraIssue
        extends JiraEntity<Issue> {
    /**
     * Comment Link error text.
     */
    private static final String ERROR_ADDCOMM = "Failed to add comment";

    /**
     * Component Link error text.
     */
    private static final String ERROR_LINKCOMP = "Failed to link component";

    /**
     * Version Link error text.
     */
    private static final String ERROR_LINKVERS = "Failed to link version";

    /**
     * Invalid Component error text.
     */
    private static final String ERROR_COMP = "Component does not exist ";

    /**
     * Invalid Version error text.
     */
    private static final String ERROR_VERS = "Version does not exist ";

    /**
     * Server.
     */
    private final JiraServer theServer;

    /**
     * Issue Client.
     */
    private final IssueRestClient theClient;

    /**
     * Project of Issue.
     */
    private final JiraProject theProject;

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
     * Creation Date of Issue.
     */
    private final DateTime theCreated;

    /**
     * Due Date of Issue.
     */
    private final DateTime theDueDate;

    /**
     * Last Updated Date of Issue.
     */
    private final DateTime theUpdated;

    /**
     * IssueLinks.
     */
    private final List<JiraIssueLink> theIssueLinks;

    /**
     * SubTasks.
     */
    private final List<JiraSubTask> theSubTasks;

    /**
     * Components.
     */
    private final List<JiraComponent> theComponents;

    /**
     * Versions.
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
     * Get the key of the issue.
     * @return the key
     */
    public String getKey() {
        return getName();
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
     * Get the project of the issue.
     * @return the project
     */
    public JiraProject getProject() {
        return theProject;
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
    public DateTime getCreateOn() {
        return theCreated;
    }

    /**
     * Get the updatedDate of the issue.
     * @return the updatedDate
     */
    public DateTime getLastUpdated() {
        return theUpdated;
    }

    /**
     * Get the dueDate of the issue.
     * @return the dueDate
     */
    public DateTime getDueDate() {
        return theDueDate;
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
    public Iterator<JiraSubTask> subTaskIterator() {
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
     * Constructor.
     * @param pServer the server
     * @param pIssue the underlying issue
     * @throws JOceanusException on error
     */
    protected JiraIssue(final JiraServer pServer,
                        final Issue pIssue) throws JOceanusException {
        /* Store parameters */
        super(pIssue, pIssue.getKey());
        theServer = pServer;

        /* Access clients */
        JiraRestClient myClient = theServer.getClient();
        theClient = myClient.getIssueClient();

        /* Access parameter */
        theSummary = pIssue.getSummary();
        theDesc = pIssue.getDescription();
        theCreated = pIssue.getCreationDate();
        theDueDate = pIssue.getDueDate();
        theUpdated = pIssue.getUpdateDate();

        /* Determine the project */
        theProject = theServer.getProject(pIssue.getProject());

        /* Determine Status etc */
        theIssueType = theServer.getIssueType(pIssue.getIssueType());
        theStatus = theServer.getStatus(pIssue.getStatus());
        thePriority = theServer.getPriority(pIssue.getPriority());
        theResolution = (pIssue.getResolution() == null)
                                                        ? null
                                                        : theServer.getResolution(pIssue.getResolution());

        /* Determine the assignee and reporter */
        theAssignee = theServer.getUser(pIssue.getAssignee());
        theReporter = theServer.getUser(pIssue.getReporter());

        /* Create the lists */
        theIssueLinks = new ArrayList<JiraIssueLink>();
        theSubTasks = new ArrayList<JiraSubTask>();
        theComponents = new ArrayList<JiraComponent>();
        theFixVers = new ArrayList<JiraVersion>();
        theAffectsVers = new ArrayList<JiraVersion>();
        theLabels = new ArrayList<String>();

        /* Populate the issue link fields */
        for (IssueLink myLink : pIssue.getIssueLinks()) {
            theIssueLinks.add(new JiraIssueLink(myLink));
        }

        /* Populate the subTask fields */
        for (Subtask myTask : pIssue.getSubtasks()) {
            theSubTasks.add(new JiraSubTask(myTask));
        }

        /* Populate the labels fields */
        for (String myLabel : pIssue.getLabels()) {
            theLabels.add(myLabel);
        }

        /* Populate the components field */
        for (BasicComponent myComp : pIssue.getComponents()) {
            /* Add the component */
            theComponents.add(theProject.getComponent(myComp));
        }

        /* Populate the affects versions field */
        for (Version myVers : pIssue.getAffectedVersions()) {
            /* Add the component */
            theAffectsVers.add(theProject.getVersion(myVers));
        }

        /* Populate the fix versions field */
        for (Version myVers : pIssue.getFixVersions()) {
            /* Add the component */
            theFixVers.add(theProject.getVersion(myVers));
        }
    }

    /**
     * Add Comment to issue.
     * @param pComment the comment to add
     * @throws JOceanusException on error
     */
    public void addComment(final String pComment) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Add the new comment */
            Comment myComment = Comment.valueOf(pComment);
            theClient.addComment(getURI(), myComment);

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException(ERROR_ADDCOMM, e);
        }
    }

    /**
     * Resolve issue.
     * @param pResolution the resolution
     * @throws JOceanusException on error
     */
    public void resolveIssue(final JiraResolution pResolution) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Create the update list */
            List<FieldInput> myUpdates = new ArrayList<FieldInput>();
            myUpdates.add(new FieldInput(IssueFieldId.RESOLUTION_FIELD, pResolution.getName()));

            /* Create the Transition change */
            TransitionInput myInput = new TransitionInput(0, myUpdates);
            theClient.transition(getUnderlying(), myInput).claim();

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to resolve issue", e);
        }
    }

    /**
     * Resolve issue.
     * @param pTarget the target issue
     * @param pLinkType the linkType
     * @param pDirection the direction of the link
     * @throws JOceanusException on error
     */
    public void linkToIssue(final JiraIssue pTarget,
                            final JiraIssueLinks pLinkType,
                            final Direction pDirection) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Create the Link */
            String myLinkType = (pDirection == Direction.INBOUND)
                                                                 ? pLinkType.getInward()
                                                                 : pLinkType.getOutward();
            LinkIssuesInput myInput = new LinkIssuesInput(getKey(), pTarget.getKey(), myLinkType);
            theClient.linkIssue(myInput);

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to link issues", e);
        }
    }

    /**
     * Link Component to issue.
     * @param pComponent the component to link
     * @return the component
     * @throws JOceanusException on error
     */
    public JiraComponent linkComponent(final String pComponent) throws JOceanusException {
        /* Check whether the component is already linked */
        Iterator<JiraComponent> myIterator = componentIterator();
        while (myIterator.hasNext()) {
            JiraComponent myComp = myIterator.next();
            if (myComp.getName().equals(pComponent)) {
                return myComp;
            }
        }

        /* Access component to link */
        JiraComponent myNewComp = theProject.getComponent(pComponent);
        if (myNewComp == null) {
            /* Pass the exception on */
            throw new JThemisLogicException(ERROR_COMP + pComponent);
        }

        /* Protect against exceptions */
        try {
            /* Create the new version list */
            List<String> myComponents = new ArrayList<String>();
            myIterator = componentIterator();
            while (myIterator.hasNext()) {
                JiraComponent myComp = myIterator.next();
                myComponents.add(myComp.getName());
            }
            myComponents.add(myNewComp.getName());

            /* Create the update list */
            List<FieldInput> myUpdates = new ArrayList<FieldInput>();
            myUpdates.add(new FieldInput(IssueFieldId.COMPONENTS_FIELD, myComponents));
            // theClient.update(getUnderlying(), myUpdates);

            /* Add the component */
            theComponents.add(myNewComp);
            return myNewComp;
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException(ERROR_LINKCOMP, e);
        }
    }

    /**
     * Link Affects Version to issue.
     * @param pVersion the version to link
     * @return the version
     * @throws JOceanusException on error
     */
    public JiraVersion linkAffectsVersion(final String pVersion) throws JOceanusException {
        /* Check whether the version is already linked */
        Iterator<JiraVersion> myIterator = affectVersionIterator();
        while (myIterator.hasNext()) {
            JiraVersion myVers = myIterator.next();
            if (myVers.getName().equals(pVersion)) {
                return myVers;
            }
        }

        /* Access version to link */
        JiraVersion myNewVers = theProject.getVersion(pVersion);
        if (myNewVers == null) {
            /* Pass the exception on */
            throw new JThemisLogicException(ERROR_VERS + pVersion);
        }

        /* Protect against exceptions */
        try {
            /* Create the new version list */
            List<String> myVersions = new ArrayList<String>();
            myIterator = affectVersionIterator();
            while (myIterator.hasNext()) {
                JiraVersion myVers = myIterator.next();
                myVersions.add(myVers.getName());
            }
            myVersions.add(myNewVers.getName());

            /* Create the update list */
            List<FieldInput> myUpdates = new ArrayList<FieldInput>();
            myUpdates.add(new FieldInput(IssueFieldId.AFFECTS_VERSIONS_FIELD, myVersions));
            // theClient.update(getUnderlying(), myUpdates);

            /* Add the affects Version */
            theAffectsVers.add(myNewVers);
            return myNewVers;
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException(ERROR_LINKVERS, e);
        }
    }

    /**
     * Link Fix Version to issue.
     * @param pVersion the version to link
     * @return the version
     * @throws JOceanusException on error
     */
    public JiraVersion linkFixVersion(final String pVersion) throws JOceanusException {
        /* Check whether the version is already linked */
        Iterator<JiraVersion> myIterator = fixVersionIterator();
        while (myIterator.hasNext()) {
            JiraVersion myVers = myIterator.next();
            if (myVers.getName().equals(pVersion)) {
                return myVers;
            }
        }

        /* Access version to link */
        JiraVersion myNewVers = theProject.getVersion(pVersion);
        if (myNewVers == null) {
            /* Pass the exception on */
            throw new JThemisLogicException(ERROR_VERS + pVersion);
        }

        /* Protect against exceptions */
        try {
            /* Create the new version list */
            List<String> myVersions = new ArrayList<String>();
            myIterator = fixVersionIterator();
            while (myIterator.hasNext()) {
                JiraVersion myVers = myIterator.next();
                myVersions.add(myVers.getName());
            }
            myVersions.add(myNewVers.getName());

            /* Create the update list */
            List<FieldInput> myUpdates = new ArrayList<FieldInput>();
            myUpdates.add(new FieldInput(IssueFieldId.FIX_VERSIONS_FIELD, myVersions));
            // theClient.update(getUnderlying(), myUpdates);

            /* Add the fix Version */
            theFixVers.add(myNewVers);
            return myNewVers;
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException(ERROR_LINKVERS, e);
        }
    }

    /**
     * Issue Links.
     */
    public final class JiraIssueLink {
        /**
         * The type of the link.
         */
        private final IssueLinkType theType;

        /**
         * Linked IssueKey.
         */
        private final String theTargetKey;

        /**
         * Linked Issue.
         */
        private JiraIssue theTarget;

        /**
         * Get the link type.
         * @return the link type
         */
        public IssueLinkType getLinkType() {
            return theType;
        }

        /**
         * Get the target issue.
         * @return the issue
         * @throws JOceanusException on error
         */
        public JiraIssue getTarget() throws JOceanusException {
            /* If we have not resolved the issue */
            if (theTarget == null) {
                /* Resolve the issue */
                theTarget = theServer.getIssue(theTargetKey);
            }

            /* Return it */
            return theTarget;
        }

        /**
         * Constructor.
         * @param pLink the underlying link
         * @throws JOceanusException on error
         */
        private JiraIssueLink(final IssueLink pLink) throws JOceanusException {
            /* Access the details */
            theType = pLink.getIssueLinkType();

            /* Determine the target issue */
            theTargetKey = pLink.getTargetIssueKey();
            theTarget = null;
        }
    }

    /**
     * SubTasks.
     */
    public final class JiraSubTask {
        /**
         * The type of the sub task issue.
         */
        private final JiraIssueType theType;

        /**
         * Linked IssueKey.
         */
        private final String theTargetKey;

        /**
         * The status of the issue.
         */
        private final JiraStatus theStatus;

        /**
         * Linked Issue.
         */
        private JiraIssue theTarget;

        /**
         * Get the issue type.
         * @return the issue type
         */
        public JiraIssueType getIssueType() {
            return theType;
        }

        /**
         * Get the issue status.
         * @return the issue status
         */
        public JiraStatus getIssueStatus() {
            return theStatus;
        }

        /**
         * Get the target issue.
         * @return the issue
         * @throws JOceanusException on error
         */
        public JiraIssue getTarget() throws JOceanusException {
            /* If we have not resolved the issue */
            if (theTarget == null) {
                /* Resolve the issue */
                theTarget = theServer.getIssue(theTargetKey);
            }

            /* Return it */
            return theTarget;
        }

        /**
         * Constructor.
         * @param pSubTask the underlying subTask
         * @throws JOceanusException on error
         */
        private JiraSubTask(final Subtask pSubTask) throws JOceanusException {
            /* Access the details */
            theType = theServer.getIssueType(pSubTask.getIssueType());
            theStatus = theServer.getStatus(pSubTask.getStatus());

            /* Determine the target issue */
            theTargetKey = pSubTask.getIssueKey();
            theTarget = null;
        }
    }
}