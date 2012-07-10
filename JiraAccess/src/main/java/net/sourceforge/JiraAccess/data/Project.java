/*******************************************************************************
 * Jira: Java Jira Link
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JiraAccess.data;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JiraAccess.data.Security.User;
import net.sourceforge.JiraAccess.data.Server.IssueType;
import net.sourceforge.JiraAccess.soap.JiraSoapService;
import net.sourceforge.JiraAccess.soap.RemoteComponent;
import net.sourceforge.JiraAccess.soap.RemoteIssueType;
import net.sourceforge.JiraAccess.soap.RemoteProject;
import net.sourceforge.JiraAccess.soap.RemoteVersion;

/**
 * Represents a Jira project.
 * @author Tony Washer
 */
public class Project {
    /**
     * Self reference.
     */
    private final Project theSelf = this;

    /**
     * Server.
     */
    private final Server theServer;

    /**
     * Service.
     */
    private final JiraSoapService theService;

    /**
     * Project.
     */
    private final RemoteProject theProject;

    /**
     * Id of Project.
     */
    private final String theId;

    /**
     * Key of Project.
     */
    private final String theKey;

    /**
     * Name of Project.
     */
    private final String theName;

    /**
     * Description of Project.
     */
    private final String theDesc;

    /**
     * Project Lead.
     */
    private final User theLead;

    /**
     * Project URL.
     */
    private final String theURL;

    /**
     * IssueTypes.
     */
    private final List<IssueType> theIssueTypes;

    /**
     * Components.
     */
    private final List<Component> theComponents;

    /**
     * Versions.
     */
    private final List<Version> theVersions;

    /**
     * Get the underlying project.
     * @return the project
     */
    public RemoteProject getProject() {
        return theProject;
    }

    /**
     * Get the id of the project.
     * @return the id
     */
    public String getId() {
        return theId;
    }

    /**
     * Get the key of the project.
     * @return the key
     */
    public String getKey() {
        return theKey;
    }

    /**
     * Get the name of the project.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Get the description of the project.
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Get the lead of the project.
     * @return the lead
     */
    public User getLead() {
        return theLead;
    }

    /**
     * Get the URL of the project.
     * @return the URL
     */
    public String getUrl() {
        return theURL;
    }

    /**
     * Constructor.
     * @param pServer the server
     * @param pProject the underlying project
     * @throws JDataException on error
     */
    protected Project(final Server pServer,
                      final RemoteProject pProject) throws JDataException {
        /* Store parameters */
        theServer = pServer;
        theService = theServer.getService();

        /* Access parameters */
        theProject = pProject;
        theId = pProject.getId();
        theKey = pProject.getKey();
        theName = pProject.getName();
        theDesc = pProject.getDescription();
        theURL = pProject.getUrl();

        /* Determine the project lead */
        theLead = theServer.getUser(pProject.getLead());

        /* Allocate the lists */
        theIssueTypes = new ArrayList<IssueType>();
        theComponents = new ArrayList<Component>();
        theVersions = new ArrayList<Version>();

        /* Load IssueTypes etc */
        loadIssueTypes();
        loadComponents();
        loadVersions();
    }

    /**
     * Obtain IssueType.
     * @param pId the id of the IssueType
     * @return the IssueType
     * @throws JDataException on error
     */
    public IssueType getIssueType(final String pId) throws JDataException {
        /* Return an existing issue type if found in list */
        Iterator<IssueType> myIterator = theIssueTypes.iterator();
        while (myIterator.hasNext()) {
            IssueType myType = myIterator.next();
            if (myType.getId().equals(pId)) {
                return myType;
            }
        }

        /* throw exception */
        throw new JDataException(ExceptionClass.JIRA, "Invalid IssueTypeId " + pId);
    }

    /**
     * Obtain Component.
     * @param pId the id of the Component
     * @return the Component
     * @throws JDataException on error
     */
    public Component getComponent(final String pId) throws JDataException {
        /* Return an existing component if found in list */
        Iterator<Component> myIterator = theComponents.iterator();
        while (myIterator.hasNext()) {
            Component myComp = myIterator.next();
            if (myComp.getId().equals(pId)) {
                return myComp;
            }
        }

        /* throw exception */
        throw new JDataException(ExceptionClass.JIRA, "Invalid ComponentId " + pId);
    }

    /**
     * Obtain Component by Name.
     * @param pName the name of the Component
     * @return the Component
     */
    public Component getComponentByName(final String pName) {
        /* Return an existing component if found in list */
        Iterator<Component> myIterator = theComponents.iterator();
        while (myIterator.hasNext()) {
            Component myComp = myIterator.next();
            if (myComp.getName().equals(pName)) {
                return myComp;
            }
        }
        return null;
    }

    /**
     * Obtain Version.
     * @param pId the id of the Version
     * @return the Version
     * @throws JDataException on error
     */
    public Version getVersion(final String pId) throws JDataException {
        /* Return an existing version if found in list */
        Iterator<Version> myIterator = theVersions.iterator();
        while (myIterator.hasNext()) {
            Version myVers = myIterator.next();
            if (myVers.getId().equals(pId)) {
                return myVers;
            }
        }

        /* throw exception */
        throw new JDataException(ExceptionClass.JIRA, "Invalid VersionId " + pId);
    }

    /**
     * Obtain Version by Name.
     * @param pName the name of the Version
     * @return the Version
     */
    public Version getVersionByName(final String pName) {
        /* Return an existing component if found in list */
        Iterator<Version> myIterator = theVersions.iterator();
        while (myIterator.hasNext()) {
            Version myVers = myIterator.next();
            if (myVers.getName().equals(pName)) {
                return myVers;
            }
        }
        return null;
    }

    /**
     * Load IssueTypes.
     * @throws JDataException on error
     */
    private void loadIssueTypes() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Access the issue types */
            RemoteIssueType[] myTypes = theService.getIssueTypesForProject(myToken, theId);

            /* Loop through the issue types */
            for (RemoteIssueType myType : myTypes) {
                /* Add new type to list */
                theIssueTypes.add(theServer.getIssueType(myType.getId()));
            }

            /* Access the sub issue types */
            myTypes = theService.getSubTaskIssueTypesForProject(myToken, theId);

            /* Loop through the issue types */
            for (RemoteIssueType myType : myTypes) {
                /* Add new type to list */
                theIssueTypes.add(theServer.getIssueType(myType.getId()));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to load issue types", e);
        }
    }

    /**
     * Load Components.
     * @throws JDataException on error
     */
    private void loadComponents() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Access the components */
            RemoteComponent[] myComps = theService.getComponents(myToken, theKey);

            /* Loop through the components */
            for (RemoteComponent myComp : myComps) {
                /* Add new component to list */
                theComponents.add(new Component(myComp));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to load components", e);
        }
    }

    /**
     * Load Versions.
     * @throws JDataException on error
     */
    private void loadVersions() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Access the versions */
            RemoteVersion[] myVers = theService.getVersions(myToken, theKey);

            /* Loop through the versions */
            for (RemoteVersion myVersion : myVers) {
                /* Add new version to list */
                theVersions.add(new Version(myVersion));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to load versions", e);
        }
    }

    /**
     * Add new Version.
     * @param pVersion the version to add
     * @throws JDataException on error
     */
    public void addVersion(final String pVersion) throws JDataException {
        /* Check whether the version is already linked */
        Iterator<Version> myIterator = theVersions.iterator();
        while (myIterator.hasNext()) {
            Version myVers = myIterator.next();
            if (myVers.getName().equals(pVersion)) {
                return;
            }
        }

        /* Protect against exceptions */
        try {
            /* Create the remote field array */
            RemoteVersion myVersion = new RemoteVersion();
            myVersion.setName(pVersion);

            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Add it to the issue */
            myVersion = theService.addVersion(myToken, theId, myVersion);

            /* Add the Version */
            theVersions.add(new Version(myVersion));
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to link component", e);
        }
    }

    /**
     * Component class.
     */
    public static final class Component {
        /**
         * The underlying remote component.
         */
        private final RemoteComponent theComp;

        /**
         * The id of the component.
         */
        private final String theId;

        /**
         * The name of the component.
         */
        private final String theName;

        /**
         * Get the underlying component.
         * @return the component
         */
        public RemoteComponent getComp() {
            return theComp;
        }

        /**
         * Get the id of the component.
         * @return the id
         */
        public String getId() {
            return theId;
        }

        /**
         * Get the name of the component.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Constructor.
         * @param pComp the underlying component
         */
        private Component(final RemoteComponent pComp) {
            /* Access the details */
            theComp = pComp;
            theId = pComp.getId();
            theName = pComp.getName();
        }
    }

    /**
     * Version class.
     */
    public final class Version {
        /**
         * The underlying remote version.
         */
        private final RemoteVersion theVers;

        /**
         * The id of the component.
         */
        private final String theId;

        /**
         * The name of the component.
         */
        private final String theName;

        /**
         * Release Date of version.
         */
        private Calendar theReleaseDate;

        /**
         * is the version archived.
         */
        private boolean isArchived;

        /**
         * is the version released.
         */
        private boolean isReleased;

        /**
         * Get the underlying version.
         * @return the version
         */
        public RemoteVersion getVersion() {
            return theVers;
        }

        /**
         * Get the id of the version.
         * @return the id
         */
        public String getId() {
            return theId;
        }

        /**
         * Get the name of the version.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get the releaseDate of the version.
         * @return the releaseDate
         */
        public Calendar getReleaseDate() {
            return theReleaseDate;
        }

        /**
         * Is the version archived?
         * @return true/false
         */
        public boolean isArchived() {
            return isArchived;
        }

        /**
         * Is the version released?
         * @return true/false
         */
        public boolean isReleased() {
            return isReleased;
        }

        /**
         * Constructor.
         * @param pVers the underlying version
         */
        private Version(final RemoteVersion pVers) {
            /* Access the details */
            theVers = pVers;
            theId = pVers.getId();
            theName = pVers.getName();
            theReleaseDate = pVers.getReleaseDate();
            isArchived = pVers.isArchived();
            isReleased = pVers.isReleased();
        }

        /**
         * Archive the version.
         * @param doArchive archive/restore version
         * @throws JDataException on error
         */
        public void setArchive(final boolean doArchive) throws JDataException {
            /* Protect against exceptions */
            try {
                /* Ignore if already in correct state */
                if (doArchive == isArchived) {
                    return;
                }

                /* Access the authorization token */
                String myToken = theServer.getAuthToken();

                /* Call the service */
                theService.archiveVersion(myToken, theSelf.getId(), theId, doArchive);

                /* Update status */
                isArchived = doArchive;
                theVers.setArchived(doArchive);
            } catch (RemoteException e) {
                /* Pass the exception on */
                throw new JDataException(ExceptionClass.JIRA, "Failed to archive version", e);
            }
        }

        /**
         * Release the version.
         * @throws JDataException on error
         */
        public void setReleased() throws JDataException {
            /* Protect against exceptions */
            try {
                /* Ignore if already in correct state */
                if (isReleased) {
                    return;
                }

                /* Access the authorization token */
                String myToken = theServer.getAuthToken();

                /* Update status */
                isReleased = true;
                isArchived = false;
                theReleaseDate = Calendar.getInstance();
                theVers.setReleased(true);
                theVers.setArchived(false);
                theVers.setReleaseDate(theReleaseDate);

                /* Call the service */
                theService.releaseVersion(myToken, theSelf.getId(), theVers);
            } catch (RemoteException e) {
                /* Pass the exception on */
                throw new JDataException(ExceptionClass.JIRA, "Failed to archive version", e);
            }
        }
    }
}
