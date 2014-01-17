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

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.jira.data.Security.Group;
import net.sourceforge.joceanus.jthemis.jira.data.Security.Role;
import net.sourceforge.joceanus.jthemis.jira.data.Security.User;
import net.sourceforge.joceanus.jthemis.jira.soap.AbstractRemoteConstant;
import net.sourceforge.joceanus.jthemis.jira.soap.JiraSoapService;
import net.sourceforge.joceanus.jthemis.jira.soap.JiraSoapServiceServiceLocator;
import net.sourceforge.joceanus.jthemis.jira.soap.RemoteFilter;
import net.sourceforge.joceanus.jthemis.jira.soap.RemoteIssue;
import net.sourceforge.joceanus.jthemis.jira.soap.RemoteIssueType;
import net.sourceforge.joceanus.jthemis.jira.soap.RemotePriority;
import net.sourceforge.joceanus.jthemis.jira.soap.RemoteProject;
import net.sourceforge.joceanus.jthemis.jira.soap.RemoteResolution;
import net.sourceforge.joceanus.jthemis.jira.soap.RemoteStatus;

import org.apache.log4j.PropertyConfigurator;

/**
 * Represents a Jira server.
 * @author Tony Washer
 */
public class Server {
    /**
     * Jira Soap location.
     */
    private static final String LOCATION = "/rpc/soap/jirasoapservice-v2";

    /**
     * Service locate error text.
     */
    private static final String ERROR_SERVICE = "Failed to locate service";

    /**
     * The preference manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * Authorization Token.
     */
    private String theAuthToken;

    /**
     * The Soap Service.
     */
    private final JiraSoapService theService;

    /**
     * The Security.
     */
    private final Security theSecurity;

    /**
     * Issues.
     */
    private final List<Issue> theIssues;

    /**
     * Projects.
     */
    private final List<Project> theProjects;

    /**
     * Filters.
     */
    private final List<Filter> theFilters;

    /**
     * IssueTypes.
     */
    private final List<IssueType> theIssueTypes;

    /**
     * Statuses.
     */
    private final List<Status> theStatuses;

    /**
     * Resolutions.
     */
    private final List<Resolution> theResolutions;

    /**
     * Priorities.
     */
    private final List<Priority> thePriorities;

    /**
     * Obtain the service.
     * @return the service
     */
    public JiraSoapService getService() {
        return theService;
    }

    /**
     * Constructor.
     * @param pManager the preference manager
     * @throws JOceanusException on error
     */
    public Server(final PreferenceManager pManager) throws JOceanusException {
        /* Store parameters */
        thePreferenceMgr = pManager;

        /* Configure log4j */
        Properties myLogProp = new Properties();
        myLogProp.setProperty("log4j.rootLogger", "ERROR, A1");
        myLogProp.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
        myLogProp.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
        myLogProp.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
        PropertyConfigurator.configure(myLogProp);

        /* Allocate the lists */
        theIssues = new ArrayList<Issue>();
        theProjects = new ArrayList<Project>();
        theFilters = new ArrayList<Filter>();
        theIssueTypes = new ArrayList<IssueType>();
        theStatuses = new ArrayList<Status>();
        theResolutions = new ArrayList<Resolution>();
        thePriorities = new ArrayList<Priority>();

        /* Allocate a service locator */
        JiraSoapServiceServiceLocator myLocator = new JiraSoapServiceServiceLocator();

        /* Protect against exceptions */
        try {
            /* Access the Jira preferences */
            JiraPreferences myPreferences = thePreferenceMgr.getPreferenceSet(JiraPreferences.class);
            String baseUrl = myPreferences.getStringValue(JiraPreferences.NAME_SERVER)
                             + LOCATION;

            /* Locate the service */
            theService = myLocator.getJirasoapserviceV2(new URL(baseUrl));

            /* Allocate the security class */
            theSecurity = new Security(this);

            /* Load constants */
            loadFilters();
            loadIssueTypes();
            loadStatuses();
            loadResolutions();
            loadPriorities();
        } catch (MalformedURLException e) {
            /* Pass the exception on */
            throw new JOceanusException(ERROR_SERVICE, e);

        } catch (ServiceException e) {
            /* Pass the exception on */
            throw new JOceanusException(ERROR_SERVICE, e);
        }
    }

    /**
     * Get Authentication Token.
     * @return the authentication token
     * @throws JOceanusException on error
     */
    public final String getAuthToken() throws JOceanusException {
        /* If we have a token, return it */
        if (theAuthToken != null) {
            return theAuthToken;
        }

        /* Protect against exceptions */
        try {
            /* Access the Jira preferences */
            JiraPreferences myPreferences = thePreferenceMgr.getPreferenceSet(JiraPreferences.class);

            /* Login to the service */
            theAuthToken = theService.login(myPreferences.getStringValue(JiraPreferences.NAME_USER), myPreferences.getStringValue(JiraPreferences.NAME_PASS));
            return theAuthToken;
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to login to server", e);
        }
    }

    /**
     * Obtain Project.
     * @param pKey the key for the project
     * @return the Project
     * @throws JOceanusException on error
     */
    public Project getProject(final String pKey) throws JOceanusException {
        /* Return an existing project if found in list */
        Iterator<Project> myIterator = theProjects.iterator();
        while (myIterator.hasNext()) {
            Project myProject = myIterator.next();
            if (myProject.getKey().equals(pKey)) {
                return myProject;
            }
        }

        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = getAuthToken();

            /* Access the project and add to list */
            RemoteProject myProjDtl = theService.getProjectByKey(myToken, pKey);
            Project myProject = new Project(this, myProjDtl);
            theProjects.add(myProject);
            return myProject;
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to load project "
                                        + pKey, e);
        }
    }

    /**
     * Load Issues from Filter.
     * @param pFilter the filter name
     * @throws JOceanusException on error
     */
    public void loadIssuesFromFilter(final String pFilter) throws JOceanusException {
        /* Access the filter */
        Filter myFilter = null;
        Iterator<Filter> myIterator = theFilters.iterator();
        while (myIterator.hasNext()) {
            myFilter = myIterator.next();
            if (myFilter.getName().equals(pFilter)) {
                break;
            }
            myFilter = null;
        }

        /* Handle filter not found */
        if (myFilter == null) {
            /* Pass the exception on */
            throw new JOceanusException("Filter does not exists "
                                        + pFilter);
        }

        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = getAuthToken();

            /* Access the issues */
            RemoteIssue[] myIssues = theService.getIssuesFromFilter(myToken, myFilter.getId());

            /* Loop through the issues */
            for (RemoteIssue myIssue : myIssues) {
                /* Add new issue to list */
                theIssues.add(new Issue(this, myIssue));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to load issues from filter", e);
        }

    }

    /**
     * Obtain User.
     * @param pName the name of the user
     * @return the User
     * @throws JOceanusException on error
     */
    public User getUser(final String pName) throws JOceanusException {
        /* Pass the call to the security service */
        return theSecurity.getUser(pName);
    }

    /**
     * Obtain Group.
     * @param pName the name of the group
     * @return the Group
     * @throws JOceanusException on error
     */
    public Group getGroup(final String pName) throws JOceanusException {
        /* Pass the call to the security service */
        return theSecurity.getGroup(pName);
    }

    /**
     * Obtain Role.
     * @param pId the Id of the role
     * @return the Role
     * @throws JOceanusException on error
     */
    public Role getRole(final long pId) throws JOceanusException {
        /* Pass the call to the security service */
        return theSecurity.getRole(pId);
    }

    /**
     * Obtain IssueType.
     * @param pId the id of the IssueType
     * @return the IssueType
     * @throws JOceanusException on error
     */
    public IssueType getIssueType(final String pId) throws JOceanusException {
        /* Return an existing issue type if found in list */
        Iterator<IssueType> myIterator = theIssueTypes.iterator();
        while (myIterator.hasNext()) {
            IssueType myType = myIterator.next();
            if (myType.getId().equals(pId)) {
                return myType;
            }
        }

        /* throw exception */
        throw new JOceanusException("Invalid IssueTypeId "
                                    + pId);
    }

    /**
     * Obtain Status.
     * @param pId the id of the Status
     * @return the Status
     * @throws JOceanusException on error
     */
    public Status getStatus(final String pId) throws JOceanusException {
        /* Return an existing status if found in list */
        Iterator<Status> myIterator = theStatuses.iterator();
        while (myIterator.hasNext()) {
            Status myStatus = myIterator.next();
            if (myStatus.getId().equals(pId)) {
                return myStatus;
            }
        }

        /* throw exception */
        throw new JOceanusException("Invalid StatusId "
                                    + pId);
    }

    /**
     * Obtain Resolution.
     * @param pId the id of the Resolution
     * @return the Resolution
     * @throws JOceanusException on error
     */
    public Resolution getResolution(final String pId) throws JOceanusException {
        /* Return an existing resolution if found in list */
        Iterator<Resolution> myIterator = theResolutions.iterator();
        while (myIterator.hasNext()) {
            Resolution myRes = myIterator.next();
            if (myRes.getId().equals(pId)) {
                return myRes;
            }
        }

        /* throw exception */
        throw new JOceanusException("Invalid ResolutionId "
                                    + pId);
    }

    /**
     * Obtain Priority.
     * @param pId the id of the Priority
     * @return the Priority
     * @throws JOceanusException on error
     */
    public Priority getPriority(final String pId) throws JOceanusException {
        /* Return an existing priority if found in list */
        Iterator<Priority> myIterator = thePriorities.iterator();
        while (myIterator.hasNext()) {
            Priority myPriority = myIterator.next();
            if (myPriority.getId().equals(pId)) {
                return myPriority;
            }
        }

        /* throw exception */
        throw new JOceanusException("Invalid PriorityId "
                                    + pId);
    }

    /**
     * Load Filters.
     * @throws JOceanusException on error
     */
    private void loadFilters() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = getAuthToken();

            /* Access the filters */
            RemoteFilter[] myFilters = theService.getSavedFilters(myToken);

            /* Loop through the filters */
            for (RemoteFilter myFilter : myFilters) {
                /* Add new filter to list */
                theFilters.add(new Filter(myFilter));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to load filters", e);
        }
    }

    /**
     * Load IssueTypes.
     * @throws JOceanusException on error
     */
    private void loadIssueTypes() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = getAuthToken();

            /* Access the issue types */
            RemoteIssueType[] myTypes = theService.getIssueTypes(myToken);

            /* Loop through the issue types */
            for (RemoteIssueType myType : myTypes) {
                /* Add new type to list */
                theIssueTypes.add(new IssueType(myType));
            }

            /* Access the sub issue types */
            myTypes = theService.getSubTaskIssueTypes(myToken);

            /* Loop through the issue types */
            for (RemoteIssueType myType : myTypes) {
                /* Add new type to list */
                theIssueTypes.add(new IssueType(myType));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to load issue types", e);
        }
    }

    /**
     * Load Statuses.
     * @throws JOceanusException on error
     */
    private void loadStatuses() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = getAuthToken();

            /* Access the statuses */
            RemoteStatus[] myStatuses = theService.getStatuses(myToken);

            /* Loop through the statuses */
            for (RemoteStatus myStatus : myStatuses) {
                /* Add new status to list */
                theStatuses.add(new Status(myStatus));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to load statuses", e);
        }
    }

    /**
     * Load Resolutions.
     * @throws JOceanusException on error
     */
    private void loadResolutions() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = getAuthToken();

            /* Access the resolutions */
            RemoteResolution[] myResolutions = theService.getResolutions(myToken);

            /* Loop through the resolutions */
            for (RemoteResolution myResolution : myResolutions) {
                /* Add new resolution to list */
                theResolutions.add(new Resolution(myResolution));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to load resolutions", e);
        }
    }

    /**
     * Load Priorities.
     * @throws JOceanusException on error
     */
    private void loadPriorities() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = getAuthToken();

            /* Access the priorities */
            RemotePriority[] myPriorities = theService.getPriorities(myToken);

            /* Loop through the priorities */
            for (RemotePriority myPriority : myPriorities) {
                /* Add new priority to list */
                thePriorities.add(new Priority(myPriority));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JOceanusException("Failed to load priorities", e);
        }
    }

    /**
     * Server constant class.
     */
    private abstract class ServerConstant {
        /**
         * The id of the constant.
         */
        private final String theId;

        /**
         * The name of the constant.
         */
        private final String theName;

        /**
         * The description of the constant.
         */
        private final String theDesc;

        /**
         * Get the id of the constant.
         * @return the id
         */
        public String getId() {
            return theId;
        }

        /**
         * Get the name of the constant.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get the description of the constant.
         * @return the description
         */
        public String getDesc() {
            return theDesc;
        }

        /**
         * Constructor.
         * @param pConstant the underlying constant
         */
        private ServerConstant(final AbstractRemoteConstant pConstant) {
            /* Access the details */
            theId = pConstant.getId();
            theName = pConstant.getName();
            theDesc = pConstant.getDescription();
        }
    }

    /**
     * IssueType class.
     */
    public final class IssueType
            extends ServerConstant {
        /**
         * Is this IssueType a sub-task.
         */
        private final boolean isSubTask;

        /**
         * Is the Issue type a subTask.
         * @return true/false
         */
        public boolean isSubTask() {
            return isSubTask;
        }

        /**
         * Constructor.
         * @param pIssueType the underlying issue type
         */
        private IssueType(final RemoteIssueType pIssueType) {
            /* Access the details */
            super(pIssueType);
            isSubTask = pIssueType.isSubTask();
        }
    }

    /**
     * Status class.
     */
    public final class Status
            extends ServerConstant {
        /**
         * Constructor.
         * @param pStatus the underlying status
         */
        private Status(final RemoteStatus pStatus) {
            /* Access the details */
            super(pStatus);
        }
    }

    /**
     * Resolution class.
     */
    public final class Resolution
            extends ServerConstant {
        /**
         * Constructor.
         * @param pResolution the underlying resolution
         */
        private Resolution(final RemoteResolution pResolution) {
            /* Access the details */
            super(pResolution);
        }
    }

    /**
     * Priority class.
     */
    public final class Priority
            extends ServerConstant {
        /**
         * The colour for this priority.
         */
        private final Color theColor;

        /**
         * Obtain the colour for this priority.
         * @return the colour
         */
        public Color getColor() {
            return theColor;
        }

        /**
         * Constructor.
         * @param pPriority the underlying priority
         */
        private Priority(final RemotePriority pPriority) {
            /* Access the details */
            super(pPriority);
            theColor = Color.decode(pPriority.getColor());
        }
    }

    /**
     * Filter class.
     */
    public static final class Filter {
        /**
         * The underlying remote filter.
         */
        private final RemoteFilter theFilter;

        /**
         * The id of the filter.
         */
        private final String theId;

        /**
         * The name of the filter.
         */
        private final String theName;

        /**
         * The description of the filter.
         */
        private final String theDesc;

        /**
         * Get the underlying filter.
         * @return the version
         */
        public RemoteFilter getFilter() {
            return theFilter;
        }

        /**
         * Get the id of the filter.
         * @return the id
         */
        public String getId() {
            return theId;
        }

        /**
         * Get the name of the filter.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get the description of the filter.
         * @return the description
         */
        public String getDesc() {
            return theDesc;
        }

        /**
         * Constructor.
         * @param pFilter the underlying filter
         */
        private Filter(final RemoteFilter pFilter) {
            /* Access the details */
            theFilter = pFilter;
            theId = pFilter.getId();
            theName = pFilter.getName();
            theDesc = pFilter.getDescription();
        }
    }
}
