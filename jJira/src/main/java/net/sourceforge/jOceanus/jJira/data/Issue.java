/*******************************************************************************
 * jJira: Java Jira Link
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jJira.data;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jJira.data.Project.Component;
import net.sourceforge.jOceanus.jJira.data.Project.Version;
import net.sourceforge.jOceanus.jJira.data.Security.User;
import net.sourceforge.jOceanus.jJira.data.Server.IssueType;
import net.sourceforge.jOceanus.jJira.data.Server.Priority;
import net.sourceforge.jOceanus.jJira.data.Server.Resolution;
import net.sourceforge.jOceanus.jJira.data.Server.Status;
import net.sourceforge.jOceanus.jJira.soap.JiraSoapService;
import net.sourceforge.jOceanus.jJira.soap.RemoteComment;
import net.sourceforge.jOceanus.jJira.soap.RemoteComponent;
import net.sourceforge.jOceanus.jJira.soap.RemoteFieldValue;
import net.sourceforge.jOceanus.jJira.soap.RemoteIssue;
import net.sourceforge.jOceanus.jJira.soap.RemoteVersion;

/**
 * Represents a Jira issue.
 * @author Tony Washer
 */
public class Issue {
    /**
     * Server.
     */
    private final Server theServer;

    /**
     * Jira Soap service.
     */
    private final JiraSoapService theService;

    /**
     * Underlying Issue.
     */
    private final RemoteIssue theIssue;

    /**
     * Id of Issue.
     */
    private final String theId;

    /**
     * Key of Issue.
     */
    private final String theKey;

    /**
     * Project of Issue.
     */
    private final Project theProject;

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
    private final IssueType theIssueType;

    /**
     * Status of Issue.
     */
    private final Status theStatus;

    /**
     * Resolution of Issue.
     */
    private final Resolution theResolution;

    /**
     * Priority of Issue.
     */
    private final Priority thePriority;

    /**
     * Environment of Issue.
     */
    private final String theEnv;

    /**
     * Assignee of Issue.
     */
    private final User theAssignee;

    /**
     * Reporter of Issue.
     */
    private final User theReporter;

    /**
     * Creation Date of Issue.
     */
    private final Calendar theCreated;

    /**
     * Due Date of Issue.
     */
    private final Calendar theDueDate;

    /**
     * Last Updated Date of Issue.
     */
    private final Calendar theUpdated;

    /**
     * Components.
     */
    private final List<Component> theComponents;

    /**
     * Versions.
     */
    private final List<Version> theAffectsVers;

    /**
     * Fix Versions.
     */
    private final List<Version> theFixVers;

    /**
     * Get the underlying issue.
     * @return the issue
     */
    public RemoteIssue getIssue() {
        return theIssue;
    }

    /**
     * Get the id of the issue.
     * @return the id
     */
    public String getId() {
        return theId;
    }

    /**
     * Get the key of the issue.
     * @return the key
     */
    public String getKey() {
        return theKey;
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
        return theEnv;
    }

    /**
     * Get the project of the issue.
     * @return the project
     */
    public Project getProject() {
        return theProject;
    }

    /**
     * Get the issue type of the issue.
     * @return the issue type
     */
    public IssueType getIssueType() {
        return theIssueType;
    }

    /**
     * Get the status of the issue.
     * @return the status
     */
    public Status getStatus() {
        return theStatus;
    }

    /**
     * Get the resolution of the issue.
     * @return the resolution
     */
    public Resolution getResolution() {
        return theResolution;
    }

    /**
     * Get the priority of the issue.
     * @return the priority
     */
    public Priority getPriority() {
        return thePriority;
    }

    /**
     * Get the reporter.
     * @return the reporter
     */
    public User getReporter() {
        return theReporter;
    }

    /**
     * Get the assignee.
     * @return the assignee
     */
    public User getAssignee() {
        return theAssignee;
    }

    /**
     * Get the createdDate of the issue.
     * @return the createdDate
     */
    public Calendar getCreateOn() {
        return theCreated;
    }

    /**
     * Get the updatedDate of the issue.
     * @return the updatedDate
     */
    public Calendar getLastUpdated() {
        return theUpdated;
    }

    /**
     * Get the dueDate of the issue.
     * @return the dueDate
     */
    public Calendar getDuedate() {
        return theDueDate;
    }

    /**
     * Get the linked components.
     * @return the linked components
     */
    public List<Component> getComponents() {
        return theComponents;
    }

    /**
     * Get the linked affected Versions.
     * @return the linked affected Versions
     */
    public List<Version> getAffectsVersions() {
        return theAffectsVers;
    }

    /**
     * Get the linked fix Versions.
     * @return the linked fix Versions
     */
    public List<Version> getFixVersions() {
        return theFixVers;
    }

    /**
     * Constructor.
     * @param pServer the server
     * @param pIssue the underlying issue
     * @throws JDataException on error
     */
    public Issue(final Server pServer,
                 final RemoteIssue pIssue) throws JDataException {
        /* Store parameters */
        theServer = pServer;
        theService = theServer.getService();

        /* Access parameter */
        theIssue = pIssue;
        theId = pIssue.getId();
        theKey = pIssue.getKey();
        theSummary = pIssue.getSummary();
        theDesc = pIssue.getDescription();
        theEnv = pIssue.getEnvironment();
        theCreated = pIssue.getCreated();
        theDueDate = pIssue.getDuedate();
        theUpdated = pIssue.getUpdated();

        /* Determine the project */
        theProject = theServer.getProject(pIssue.getProject());

        /* Determine Status etc */
        theIssueType = theServer.getIssueType(pIssue.getType());
        theStatus = theServer.getStatus(pIssue.getStatus());
        thePriority = theServer.getPriority(pIssue.getPriority());
        theResolution = (pIssue.getResolution() == null)
                ? null
                : theServer.getResolution(pIssue.getResolution());

        /* Determine the assignee and reporter */
        theAssignee = theServer.getUser(pIssue.getAssignee());
        theReporter = theServer.getUser(pIssue.getReporter());

        /* Create the lists */
        theComponents = new ArrayList<Component>();
        theFixVers = new ArrayList<Version>();
        theAffectsVers = new ArrayList<Version>();

        /* Populate the components field */
        for (RemoteComponent myComp : pIssue.getComponents()) {
            /* Add the component */
            theComponents.add(theProject.getComponent(myComp.getId()));
        }

        /* Populate the affects versions field */
        for (RemoteVersion myVers : pIssue.getAffectsVersions()) {
            /* Add the component */
            theAffectsVers.add(theProject.getVersion(myVers.getId()));
        }

        /* Populate the fix versions field */
        for (RemoteVersion myVers : pIssue.getFixVersions()) {
            /* Add the component */
            theFixVers.add(theProject.getVersion(myVers.getId()));
        }
    }

    /**
     * Add Comment to issue.
     * @param pComment the comment to add
     * @throws JDataException on error
     */
    public void addComment(final String pComment) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create the remote comment */
            RemoteComment myComment = new RemoteComment();
            myComment.setBody(pComment);

            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Add it to the issue */
            theService.addComment(myToken, theId, myComment);
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to add comment", e);
        }
    }

    /**
     * Link Component to issue.
     * @param pComponent the component to link
     * @return the component
     * @throws JDataException on error
     */
    public Component linkComponent(final String pComponent) throws JDataException {
        /* Check whether the component is already linked */
        Iterator<Component> myIterator = theComponents.iterator();
        while (myIterator.hasNext()) {
            Component myComp = myIterator.next();
            if (myComp.getName().equals(pComponent)) {
                return myComp;
            }
        }

        /* Access component to link */
        Component myNewComp = theProject.getComponentByName(pComponent);
        if (myNewComp == null) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Component does not exists "
                                                          + pComponent);
        }

        /* Protect against exceptions */
        try {
            /* Create the new array */
            int i = 0;
            String[] myList = new String[theComponents.size() + 1];
            myIterator = theComponents.iterator();
            while (myIterator.hasNext()) {
                Component myComp = myIterator.next();
                myList[i++] = myComp.getId();
            }
            myList[i] = myNewComp.getId();

            /* Create the remote field array */
            RemoteFieldValue[] myValues = new RemoteFieldValue[1];
            myValues[0].setId("components");
            myValues[0].setValues(myList);

            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Add it to the issue */
            theService.updateIssue(myToken, theId, myValues);

            /* Add the component */
            theComponents.add(myNewComp);
            return myNewComp;
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to link component", e);
        }
    }

    /**
     * Link Affects Version to issue.
     * @param pVersion the version to link
     * @return the version
     * @throws JDataException on error
     */
    public Version linkAffectsVersion(final String pVersion) throws JDataException {
        /* Check whether the version is already linked */
        Iterator<Version> myIterator = theAffectsVers.iterator();
        while (myIterator.hasNext()) {
            Version myVers = myIterator.next();
            if (myVers.getName().equals(pVersion)) {
                return myVers;
            }
        }

        /* Access version to link */
        Version myNewVers = theProject.getVersionByName(pVersion);
        if (myNewVers == null) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Version does not exists "
                                                          + pVersion);
        }

        /* Protect against exceptions */
        try {
            /* Create the new array */
            int i = 0;
            String[] myList = new String[theAffectsVers.size() + 1];
            myIterator = theAffectsVers.iterator();
            while (myIterator.hasNext()) {
                Version myVers = myIterator.next();
                myList[i++] = myVers.getId();
            }
            myList[i] = myNewVers.getId();

            /* Create the remote field array */
            RemoteFieldValue[] myValues = new RemoteFieldValue[1];
            myValues[0].setId("affectsVersions");
            myValues[0].setValues(myList);

            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Add it to the issue */
            theService.updateIssue(myToken, theId, myValues);

            /* Add the affects Version */
            theAffectsVers.add(myNewVers);
            return myNewVers;
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to link component", e);
        }
    }

    /**
     * Link Fix Version to issue.
     * @param pVersion the version to link
     * @return the version
     * @throws JDataException on error
     */
    public Version linkFixVersion(final String pVersion) throws JDataException {
        /* Check whether the version is already linked */
        Iterator<Version> myIterator = theFixVers.iterator();
        while (myIterator.hasNext()) {
            Version myVers = myIterator.next();
            if (myVers.getName().equals(pVersion)) {
                return myVers;
            }
        }

        /* Access version to link */
        Version myNewVers = theProject.getVersionByName(pVersion);
        if (myNewVers == null) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Version does not exists "
                                                          + pVersion);
        }

        /* Protect against exceptions */
        try {
            /* Create the new array */
            int i = 0;
            String[] myList = new String[theFixVers.size() + 1];
            myIterator = theFixVers.iterator();
            while (myIterator.hasNext()) {
                Version myVers = myIterator.next();
                myList[i++] = myVers.getId();
            }
            myList[i] = myNewVers.getId();

            /* Create the remote field array */
            RemoteFieldValue[] myValues = new RemoteFieldValue[1];
            myValues[0].setId("fixVersions");
            myValues[0].setValues(myList);

            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Add it to the issue */
            theService.updateIssue(myToken, theId, myValues);

            /* Add the fix Version */
            theFixVers.add(myNewVers);
            return myNewVers;
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to link component", e);
        }
    }
}
