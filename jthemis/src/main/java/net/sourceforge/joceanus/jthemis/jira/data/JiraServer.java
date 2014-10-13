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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.JThemisLogicException;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraGroup;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraUser;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.IssuelinksType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

/**
 * Represents a Jira server.
 * @author Tony Washer
 */
public class JiraServer {
    /**
     * The preference manager.
     */
    private static final String ERROR_SERVICE = "Failed to contact server";

    /**
     * The Rest Client.
     */
    private final JiraRestClient theClient;

    /**
     * The MetaData Client.
     */
    private final MetadataRestClient theDataClient;

    /**
     * The Security.
     */
    private final JiraSecurity theSecurity;

    /**
     * The Active User.
     */
    private final JiraUser theActive;

    /**
     * Projects.
     */
    private final List<JiraProject> theProjects;

    /**
     * IssueTypes.
     */
    private final List<JiraIssueType> theIssueTypes;

    /**
     * IssueLinkTypes.
     */
    private final List<JiraIssueLinks> theIssueLinks;

    /**
     * Statuses.
     */
    private final List<JiraStatus> theStatuses;

    /**
     * Resolutions.
     */
    private final List<JiraResolution> theResolutions;

    /**
     * Priorities.
     */
    private final List<JiraPriority> thePriorities;

    /**
     * Filters.
     */
    private final List<JiraFilter> theFilters;

    /**
     * Obtain the service.
     * @return the service
     */
    protected JiraRestClient getClient() {
        return theClient;
    }

    /**
     * Obtain the active User.
     * @return the user
     */
    protected JiraUser getActiveUser() {
        return theActive;
    }

    /**
     * Obtain the project iterator.
     * @return the iterator
     */
    public Iterator<JiraProject> projectIterator() {
        return theProjects.iterator();
    }

    /**
     * Obtain the issue links iterator.
     * @return the iterator
     */
    public Iterator<JiraIssueLinks> issueLinksIterator() {
        return theIssueLinks.iterator();
    }

    /**
     * Obtain the issueTypes iterator.
     * @return the iterator
     */
    public Iterator<JiraIssueType> issueTypeIterator() {
        return theIssueTypes.iterator();
    }

    /**
     * Obtain the resolutions iterator.
     * @return the iterator
     */
    public Iterator<JiraResolution> resolutionIterator() {
        return theResolutions.iterator();
    }

    /**
     * Obtain the priority iterator.
     * @return the iterator
     */
    public Iterator<JiraPriority> priorityIterator() {
        return thePriorities.iterator();
    }

    /**
     * Obtain the filter iterator.
     * @return the iterator
     */
    public Iterator<JiraFilter> filterIterator() {
        return theFilters.iterator();
    }

    /**
     * Constructor.
     * @param pManager the preference manager
     * @throws JOceanusException on error
     */
    public JiraServer(final PreferenceManager pManager) throws JOceanusException {
        /* Allocate the lists */
        theProjects = new ArrayList<JiraProject>();
        theFilters = new ArrayList<JiraFilter>();
        theIssueTypes = new ArrayList<JiraIssueType>();
        theIssueLinks = new ArrayList<JiraIssueLinks>();
        theStatuses = new ArrayList<JiraStatus>();
        theResolutions = new ArrayList<JiraResolution>();
        thePriorities = new ArrayList<JiraPriority>();

        /* Protect against exceptions */
        try {
            /* Access the Jira preferences */
            JiraPreferences myPreferences = pManager.getPreferenceSet(JiraPreferences.class);
            String baseUrl = myPreferences.getStringValue(JiraPreferences.NAME_SERVER);

            /* Access the Rest Client */
            JiraRestClientFactory myFactory = new AsynchronousJiraRestClientFactory();
            URI myUri = new URI(baseUrl);
            String myUser = myPreferences.getStringValue(JiraPreferences.NAME_USER);
            String myPass = myPreferences.getStringValue(JiraPreferences.NAME_PASS);
            theClient = myFactory.createWithBasicHttpAuthentication(myUri, myUser, myPass);
            theDataClient = theClient.getMetadataClient();

            /* Allocate the security class */
            theSecurity = new JiraSecurity(this);
            theActive = getUser(myUser);

            /* Load constants */
            loadIssueTypes();
            loadIssueLinks();
            loadResolutions();
            loadPriorities();

            /* Load projects and filters */
            loadProjects();
            loadFilters();

        } catch (URISyntaxException e) {
            /* Pass the exception on */
            throw new JThemisIOException(ERROR_SERVICE, e);
        }
    }

    /**
     * Obtain Project.
     * @param pName the name of the project
     * @return the Project
     * @throws JOceanusException on error
     */
    public JiraProject getProject(final String pName) throws JOceanusException {
        /* Return an existing project if found in list */
        Iterator<JiraProject> myIterator = theProjects.iterator();
        while (myIterator.hasNext()) {
            JiraProject myProject = myIterator.next();
            if (pName.equals(myProject.getName())) {
                return myProject;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid Project: " + pName);
    }

    /**
     * Obtain Project.
     * @param pProject the basic project
     * @return the Project
     * @throws JOceanusException on error
     */
    protected JiraProject getProject(final BasicProject pProject) throws JOceanusException {
        /* Use name to search */
        return getProject(pProject.getName());
    }

    /**
     * Obtain Filter.
     * @param pName the name of the filter
     * @return the Filter
     * @throws JOceanusException on error
     */
    public JiraFilter getFilter(final String pName) throws JOceanusException {
        /* Return an existing filter if found in list */
        Iterator<JiraFilter> myIterator = theFilters.iterator();
        while (myIterator.hasNext()) {
            JiraFilter myFilter = myIterator.next();
            if (pName.equals(myFilter.getName())) {
                return myFilter;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid Filter: " + pName);
    }

    /**
     * Obtain Issues from Filter.
     * @param pFilter the filter
     * @return the Issues
     * @throws JOceanusException on error
     */
    public Iterator<JiraIssue> getIssuesFromFilter(final JiraFilter pFilter) throws JOceanusException {
        /* Create a new list */
        List<JiraIssue> myList = new ArrayList<JiraIssue>();

        /* Build a list of the issues */
        for (Issue myIssue : pFilter.getIssues()) {
            myList.add(getIssue(myIssue.getKey()));
        }

        /* return the iterator */
        return myList.iterator();
    }

    /**
     * Obtain Issue for key.
     * @param pKey the issue key
     * @return the issue
     * @throws JOceanusException on error
     */
    public JiraIssue getIssue(final String pKey) throws JOceanusException {
        /* Determine the project key for issue */
        int iPos = pKey.indexOf('-');
        if (iPos != -1) {
            /* Access project key */
            String myKey = pKey.substring(0, iPos);

            /* Look for relevant project */
            Iterator<JiraProject> myIterator = projectIterator();
            while (myIterator.hasNext()) {
                JiraProject myProject = myIterator.next();
                if (myKey.equals(myProject.getKey())) {
                    /* Load issue from project */
                    return myProject.getIssue(pKey);
                }
            }
        }

        /* Pass the exception on */
        throw new JThemisIOException("Failed to load issue: " + pKey);
    }

    /**
     * Obtain User.
     * @param pName the name of the user
     * @return the User
     * @throws JOceanusException on error
     */
    public final JiraUser getUser(final String pName) throws JOceanusException {
        /* Pass the call to the security service */
        return theSecurity.getUser(pName);
    }

    /**
     * Obtain User.
     * @param pUser the basic user
     * @return the User
     * @throws JOceanusException on error
     */
    protected final JiraUser getUser(final BasicUser pUser) throws JOceanusException {
        /* Pass the call to the security service */
        return theSecurity.getUser(pUser);
    }

    /**
     * Obtain Group.
     * @param pName the name of the group
     * @return the Group
     * @throws JOceanusException on error
     */
    public JiraGroup getGroup(final String pName) throws JOceanusException {
        /* Pass the call to the security service */
        return theSecurity.getGroup(pName);
    }

    /**
     * Obtain IssueType.
     * @param pName the name of the IssueType
     * @return the IssueType
     * @throws JOceanusException on error
     */
    public JiraIssueType getIssueType(final String pName) throws JOceanusException {
        /* Return an existing issue type if found in list */
        Iterator<JiraIssueType> myIterator = theIssueTypes.iterator();
        while (myIterator.hasNext()) {
            JiraIssueType myType = myIterator.next();
            if (pName.equals(myType.getName())) {
                return myType;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid IssueType: " + pName);
    }

    /**
     * Obtain IssueType.
     * @param pType the basic issue type
     * @return the IssueType
     * @throws JOceanusException on error
     */
    protected JiraIssueType getIssueType(final IssueType pType) throws JOceanusException {
        /* Use name to search */
        return getIssueType(pType.getName());
    }

    /**
     * Obtain Status.
     * @param pName the name of the Status
     * @return the Status
     * @throws JOceanusException on error
     */
    public JiraStatus getStatus(final String pName) throws JOceanusException {
        /* Return an existing status if found in list */
        Iterator<JiraStatus> myIterator = theStatuses.iterator();
        while (myIterator.hasNext()) {
            JiraStatus myStatus = myIterator.next();
            if (pName.equals(myStatus.getName())) {
                return myStatus;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid Status: " + pName);
    }

    /**
     * Obtain Status.
     * @param pStatus the Basic Status
     * @return the Status
     * @throws JOceanusException on error
     */
    protected JiraStatus getStatus(final Status pStatus) throws JOceanusException {
        /* Return an existing status if found in list */
        String myName = pStatus.getName();
        Iterator<JiraStatus> myIterator = theStatuses.iterator();
        while (myIterator.hasNext()) {
            JiraStatus myStatus = myIterator.next();
            if (myName.equals(myStatus.getName())) {
                return myStatus;
            }
        }

        /* Protect against exceptions */
        try {
            /* Access the status details */
            Status myStatusDtl = theDataClient.getStatus(pStatus.getSelf()).claim();

            /* Create Status object and add to list */
            JiraStatus myStatus = new JiraStatus(myStatusDtl);
            theStatuses.add(myStatus);

            /* Return it */
            return myStatus;
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load status " + myName, e);
        }
    }

    /**
     * Obtain Resolution.
     * @param pName the name of the Resolution
     * @return the Resolution
     * @throws JOceanusException on error
     */
    public JiraResolution getResolution(final String pName) throws JOceanusException {
        /* Return an existing resolution if found in list */
        Iterator<JiraResolution> myIterator = theResolutions.iterator();
        while (myIterator.hasNext()) {
            JiraResolution myRes = myIterator.next();
            if (pName.equals(myRes.getName())) {
                return myRes;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid Resolution: " + pName);
    }

    /**
     * Obtain Resolution.
     * @param pResolution the basic resolution
     * @return the resolution
     * @throws JOceanusException on error
     */
    protected JiraResolution getResolution(final Resolution pResolution) throws JOceanusException {
        /* Use name to search */
        return getResolution(pResolution.getName());
    }

    /**
     * Obtain Priority.
     * @param pName the name of the Priority
     * @return the Priority
     * @throws JOceanusException on error
     */
    public JiraPriority getPriority(final String pName) throws JOceanusException {
        /* Return an existing priority if found in list */
        Iterator<JiraPriority> myIterator = thePriorities.iterator();
        while (myIterator.hasNext()) {
            JiraPriority myPriority = myIterator.next();
            if (pName.equals(myPriority.getName())) {
                return myPriority;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid Priority: " + pName);
    }

    /**
     * Obtain Priority.
     * @param pPriority the basic priority
     * @return the priority
     * @throws JOceanusException on error
     */
    protected JiraPriority getPriority(final BasicPriority pPriority) throws JOceanusException {
        /* Use name to search */
        return getPriority(pPriority.getName());
    }

    /**
     * Load Projects.
     * @throws JOceanusException on error
     */
    private void loadProjects() throws JOceanusException {
        /* Access client */
        ProjectRestClient myClient = theClient.getProjectClient();

        /* Protect against exceptions */
        try {
            /* Loop through all projects */
            for (BasicProject myProj : myClient.getAllProjects().claim()) {
                /* Access parameters */
                Project myProject = myClient.getProject(myProj.getSelf()).claim();

                /* Add new project to list */
                theProjects.add(new JiraProject(this, myProject));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load projects", e);
        }
    }

    /**
     * Load Filters.
     * @throws JOceanusException on error
     */
    private void loadFilters() throws JOceanusException {
        /* Access client */
        SearchRestClient myClient = theClient.getSearchClient();

        /* Protect against exceptions */
        try {
            /* Loop through all projects */
            for (Filter myFilt : myClient.getFavouriteFilters().claim()) {
                /* Access parameters */
                Filter myFilter = myClient.getFilter(myFilt.getSelf()).claim();

                /* Add new filter to list */
                theFilters.add(new JiraFilter(myFilter));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load filters", e);
        }
    }

    /**
     * Load IssueTypes.
     * @throws JOceanusException on error
     */
    private void loadIssueTypes() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Loop through all issueTypes */
            for (IssueType myType : theDataClient.getIssueTypes().claim()) {
                /* Access the issue type details */
                IssueType myTypeDtl = theDataClient.getIssueType(myType.getSelf()).claim();

                /* Add new type to list */
                theIssueTypes.add(new JiraIssueType(myTypeDtl));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issue types", e);
        }
    }

    /**
     * Load IssueLinks.
     * @throws JOceanusException on error
     */
    private void loadIssueLinks() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Loop through all issueLinkTypes */
            for (IssuelinksType myType : theDataClient.getIssueLinkTypes().claim()) {
                /* Add new type to list */
                theIssueLinks.add(new JiraIssueLinks(myType));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issue links", e);
        }
    }

    /**
     * Load Resolutions.
     * @throws JOceanusException on error
     */
    private void loadResolutions() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Loop through all resolutions */
            for (Resolution myRes : theDataClient.getResolutions().claim()) {
                /* Access the resolution details */
                Resolution myResolution = theDataClient.getResolution(myRes.getSelf()).claim();

                /* Add new resolution to list */
                theResolutions.add(new JiraResolution(myResolution));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load resolutions", e);
        }
    }

    /**
     * Load Priorities.
     * @throws JOceanusException on error
     */
    private void loadPriorities() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Loop through all priorities */
            for (Priority myPri : theDataClient.getPriorities().claim()) {
                /* Access the priority details */
                Priority myPriority = theDataClient.getPriority(myPri.getSelf()).claim();

                /* Add new priority to list */
                thePriorities.add(new JiraPriority(myPriority));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load priorities", e);
        }
    }

    /**
     * Generic Jira object type.
     * @param <T> the object type
     */
    public abstract static class JiraEntity<T extends AddressableEntity> {
        /**
         * The underlying entity.
         */
        private T theEntity;

        /**
         * The name of the object.
         */
        private final String theName;

        /**
         * Object URI.
         */
        private final URI theURI;

        /**
         * Obtain the underlying object.
         * @return the name
         */
        protected T getUnderlying() {
            return theEntity;
        }

        /**
         * Get the name of the object.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get the URI of the object.
         * @return the URI
         */
        public URI getURI() {
            return theURI;
        }

        /**
         * Constructor.
         * @param pEntity the underlying entity
         * @param pName the entity name
         */
        protected JiraEntity(final T pEntity,
                             final String pName) {
            /* Access the details */
            theEntity = pEntity;
            theName = pName;
            theURI = pEntity.getSelf();
        }

        /**
         * Adjust the entity.
         * @param pEntity the updated entity
         */
        protected void adjustEntity(final T pEntity) {
            theEntity = pEntity;
        }
    }

    /**
     * IssueType class.
     */
    public final class JiraIssueType
            extends JiraEntity<IssueType> {
        /**
         * The description of the issueType.
         */
        private final String theDesc;

        /**
         * Is this a subTask?
         */
        private final boolean isSubTask;

        /**
         * Get the description of the issueType.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

        /**
         * Is this issue type a subTask?
         * @return true/false
         */
        public boolean isSubTask() {
            return isSubTask;
        }

        /**
         * Constructor.
         * @param pType the underlying issueType
         */
        private JiraIssueType(final IssueType pType) {
            /* Access the details */
            super(pType, pType.getName());
            theDesc = pType.getDescription();
            isSubTask = pType.isSubtask();
        }
    }

    /**
     * Status class.
     */
    public final class JiraStatus
            extends JiraEntity<Status> {
        /**
         * The description of the status.
         */
        private final String theDesc;

        /**
         * Get the description of the status.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

        /**
         * Constructor.
         * @param pStatus the underlying status
         */
        private JiraStatus(final Status pStatus) {
            /* Access the details */
            super(pStatus, pStatus.getName());
            theDesc = pStatus.getDescription();
        }
    }

    /**
     * Resolution class.
     */
    public final class JiraResolution
            extends JiraEntity<Resolution> {
        /**
         * The description of the resolution.
         */
        private final String theDesc;

        /**
         * Get the description of the resolution.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

        /**
         * Constructor.
         * @param pResolution the underlying resolution
         */
        private JiraResolution(final Resolution pResolution) {
            /* Access the details */
            super(pResolution, pResolution.getName());
            theDesc = pResolution.getDescription();
        }
    }

    /**
     * Priority class.
     */
    public final class JiraPriority
            extends JiraEntity<Priority> {
        /**
         * The description of the priority.
         */
        private final String theDesc;

        /**
         * The colour for this priority.
         */
        private final Color theColor;

        /**
         * Get the description of the priority.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

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
        private JiraPriority(final Priority pPriority) {
            /* Access the details */
            super(pPriority, pPriority.getName());
            theDesc = pPriority.getDescription();
            theColor = Color.decode(pPriority.getStatusColor());
        }
    }

    /**
     * IssueLinks class.
     */
    public final class JiraIssueLinks
            extends JiraEntity<IssuelinksType> {
        /**
         * The inward link name.
         */
        private final String theInward;

        /**
         * The outward link name.
         */
        private final String theOutward;

        /**
         * Get the inward link name.
         * @return the name
         */
        public String getInward() {
            return theInward;
        }

        /**
         * Get the outward link name.
         * @return the name
         */
        public String getOutward() {
            return theOutward;
        }

        /**
         * Constructor.
         * @param pLink the underlying link
         * @throws JOceanusException on error
         */
        private JiraIssueLinks(final IssuelinksType pLink) throws JOceanusException {
            /* Access the details */
            super(pLink, pLink.getName());
            theInward = pLink.getInward();
            theOutward = pLink.getOutward();
        }
    }

    /**
     * Filter class.
     */
    public final class JiraFilter
            extends JiraEntity<Filter> {
        /**
         * The description of the filter.
         */
        private final String theDesc;

        /**
         * The owner of the filter.
         */
        private final JiraUser theOwner;

        /**
         * The query for this filter.
         */
        private final String theQuery;

        /**
         * Get the description of the priority.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

        /**
         * Obtain the owner for this filter.
         * @return the owner
         */
        public JiraUser getOwner() {
            return theOwner;
        }

        /**
         * Obtain the iterator for the results of this query .
         * @return the colour
         */
        protected Iterable<Issue> getIssues() {
            return theClient.getSearchClient().searchJql(theQuery).claim().getIssues();
        }

        /**
         * Constructor.
         * @param pFilter the underlying filter
         * @throws JOceanusException on error
         */
        private JiraFilter(final Filter pFilter) throws JOceanusException {
            /* Access the details */
            super(pFilter, pFilter.getName());
            theDesc = pFilter.getDescription();
            theOwner = getUser(pFilter.getOwner());
            theQuery = pFilter.getJql();
        }
    }
}
