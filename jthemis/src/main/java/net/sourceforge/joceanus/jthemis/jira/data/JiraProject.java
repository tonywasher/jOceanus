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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.JThemisLogicException;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraGroup;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraUser;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraEntity;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraIssueType;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraPriority;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.ComponentRestClient;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.ProjectRolesRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.VersionRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProjectRole;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.ProjectRole;
import com.atlassian.jira.rest.client.api.domain.RoleActor;
import com.atlassian.jira.rest.client.api.domain.StandardOperation;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.ComponentInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.VersionInput;
import com.atlassian.jira.rest.client.api.domain.input.VersionInputBuilder;

/**
 * Represents a Jira project.
 * @author Tony Washer
 */
public class JiraProject
        extends JiraEntity<Project> {
    /**
     * Archive failure error text.
     */
    private static final String ERROR_ARCH = "Failed to archive version";

    /**
     * Group role name (should be public from underlying file).
     */
    private static final String TYPE_ATLASSIAN_GROUP_ROLE = "atlassian-group-role-actor";

    /**
     * Server.
     */
    private final JiraServer theServer;

    /**
     * The client.
     */
    private final JiraRestClient theClient;

    /**
     * Key of Project.
     */
    private final String theKey;

    /**
     * Description of Project.
     */
    private final String theDesc;

    /**
     * Project Lead.
     */
    private final JiraUser theLead;

    /**
     * Issues.
     */
    private final List<JiraIssue> theIssues;

    /**
     * IssueTypes.
     */
    private final List<JiraIssueType> theIssueTypes;

    /**
     * Components.
     */
    private final List<JiraComponent> theComponents;

    /**
     * Versions.
     */
    private final List<JiraVersion> theVersions;

    /**
     * Roles.
     */
    private final List<JiraProjectRole> theRoles;

    /**
     * The create issue field map.
     */
    private final Map<JiraIssueType, JiraCimIssueType> theIssueFields;

    /**
     * Get the key of the project.
     * @return the key
     */
    public String getKey() {
        return theKey;
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
    public JiraUser getLead() {
        return theLead;
    }

    /**
     * Obtain the issue iterator.
     * @return the iterator
     */
    public Iterator<JiraIssue> issueIterator() {
        return theIssues.iterator();
    }

    /**
     * Obtain the issue types iterator.
     * @return the iterator
     */
    public Iterator<JiraIssueType> issueTypeIterator() {
        return theIssueTypes.iterator();
    }

    /**
     * Get the components iterator.
     * @return the iterator
     */
    public Iterator<JiraComponent> componentIterator() {
        return theComponents.iterator();
    }

    /**
     * Get the versions iterator.
     * @return the iterator
     */
    public Iterator<JiraVersion> labelIterator() {
        return theVersions.iterator();
    }

    /**
     * Get the roles iterator.
     * @return the iterator
     */
    public Iterator<JiraProjectRole> roleIterator() {
        return theRoles.iterator();
    }

    /**
     * Constructor.
     * @param pServer the server
     * @param pProject the underlying project
     * @throws JOceanusException on error
     */
    protected JiraProject(final JiraServer pServer,
                          final Project pProject) throws JOceanusException {
        /* Store parameters */
        super(pProject, pProject.getName());
        theServer = pServer;
        theClient = theServer.getClient();

        /* Access parameters */
        theKey = pProject.getKey();
        theDesc = pProject.getDescription();

        /* Determine the project lead */
        theLead = theServer.getUser(pProject.getLead());

        /* Allocate the lists */
        theIssues = new ArrayList<JiraIssue>();
        theIssueTypes = new ArrayList<JiraIssueType>();
        theComponents = new ArrayList<JiraComponent>();
        theVersions = new ArrayList<JiraVersion>();
        theRoles = new ArrayList<JiraProjectRole>();

        /* Create the issue fields map */
        theIssueFields = new HashMap<JiraIssueType, JiraCimIssueType>();

        /* Load IssueTypes etc */
        loadIssueTypes();
        loadComponents();
        loadVersions();
        loadRoles();
    }

    /**
     * Create issue.
     * @param pSummary the issue summary
     * @param pDesc the issue description
     * @param pIssueType the issue type
     * @param pPriority the issue priority
     * @return the new issue
     * @throws JOceanusException on error
     */
    public JiraIssue createIssue(final String pSummary,
                                 final String pDesc,
                                 final JiraIssueType pIssueType,
                                 final JiraPriority pPriority) throws JOceanusException {
        /* Access client */
        IssueRestClient myClient = theClient.getIssueClient();

        /* Protect against exceptions */
        try {
            /* Build issue details */
            IssueInputBuilder myBuilder = new IssueInputBuilder(getUnderlying(), pIssueType.getUnderlying());
            myBuilder.setSummary(pSummary);
            myBuilder.setDescription(pDesc);
            myBuilder.setReporter(theServer.getActiveUser().getUnderlying());
            myBuilder.setPriority(pPriority.getUnderlying());
            IssueInput myInput = myBuilder.build();

            /* Create the issue */
            BasicIssue myIssue = myClient.createIssue(myInput).claim();
            return getIssue(myIssue.getKey());

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to create issue", e);
        }
    }

    /**
     * Obtain field details for the issue type.
     * @param pIssueType the issue type
     * @return the issue field details
     * @throws JOceanusException on error
     */
    protected JiraCimIssueType getIssueFields(final JiraIssueType pIssueType) throws JOceanusException {
        /* Look for an already resolved type */
        JiraCimIssueType myIssueType = theIssueFields.get(pIssueType);
        if (myIssueType != null) {
            return myIssueType;
        }

        /* Access client */
        IssueRestClient myClient = theClient.getIssueClient();

        /* Protect against exceptions */
        try {
            /* Create the options */
            GetCreateIssueMetadataOptionsBuilder myBuilder = new GetCreateIssueMetadataOptionsBuilder();
            myBuilder = myBuilder.withProjectKeys(getKey());
            myBuilder = myBuilder.withIssueTypeNames(pIssueType.getName());
            myBuilder = myBuilder.withExpandedIssueTypesFields();
            GetCreateIssueMetadataOptions myOptions = myBuilder.build();

            /* Process the details */
            for (CimProject myMetadata : myClient.getCreateIssueMetadata(myOptions).claim()) {
                for (CimIssueType myType : myMetadata.getIssueTypes()) {
                    /* Create the issue type */
                    myIssueType = new JiraCimIssueType(myType);
                    theIssueFields.put(pIssueType, myIssueType);
                    return myIssueType;
                }
            }

            /* Throw and exception */
            throw new JThemisIOException("Cannot create issue with Issue type");

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to determine fields for issue type", e);
        }
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
     * Obtain Component.
     * @param pName the name of the Component
     * @return the Component
     * @throws JOceanusException on error
     */
    public JiraComponent getComponent(final String pName) throws JOceanusException {
        /* Return an existing component if found in list */
        Iterator<JiraComponent> myIterator = theComponents.iterator();
        while (myIterator.hasNext()) {
            JiraComponent myComp = myIterator.next();
            if (pName.equals(myComp.getName())) {
                return myComp;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid Component: " + pName);
    }

    /**
     * Obtain Component.
     * @param pComponent the basic component
     * @return the Component
     * @throws JOceanusException on error
     */
    protected JiraComponent getComponent(final BasicComponent pComponent) throws JOceanusException {
        /* Use name to search */
        return getComponent(pComponent.getName());
    }

    /**
     * Obtain Version.
     * @param pName the name of the Version
     * @return the Version
     * @throws JOceanusException on error
     */
    public JiraVersion getVersion(final String pName) throws JOceanusException {
        /* Return an existing version if found in list */
        Iterator<JiraVersion> myIterator = theVersions.iterator();
        while (myIterator.hasNext()) {
            JiraVersion myVers = myIterator.next();
            if (pName.equals(myVers.getName())) {
                return myVers;
            }
        }

        /* throw exception */
        throw new JThemisLogicException("Invalid Version: " + pName);
    }

    /**
     * Obtain Version.
     * @param pVersion the version
     * @return the Version
     * @throws JOceanusException on error
     */
    protected JiraVersion getVersion(final Version pVersion) throws JOceanusException {
        /* Use name to search */
        return getVersion(pVersion.getName());
    }

    /**
     * Load Issues.
     * @throws JOceanusException on error
     */
    public void loadAllIssues() throws JOceanusException {
        /* Access clients */
        IssueRestClient myIssueClient = theClient.getIssueClient();
        SearchRestClient mySearchClient = theClient.getSearchClient();

        /* Protect against exceptions */
        try {
            /* Loop through all issues in the project */
            for (BasicIssue myIss : mySearchClient.searchJql("project = \"" + getName() + "\"").claim().getIssues()) {
                /* Access full details of the issue */
                Issue myIssue = myIssueClient.getIssue(myIss.getKey()).claim();

                /* Add to the list */
                theIssues.add(new JiraIssue(theServer, myIssue));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issues", e);
        }
    }

    /**
     * Obtain Issue for key.
     * @param pKey the issue key
     * @return the issue
     * @throws JOceanusException on error
     */
    public JiraIssue getIssue(final String pKey) throws JOceanusException {
        /* Return an existing project if found in list */
        Iterator<JiraIssue> myIterator = issueIterator();
        while (myIterator.hasNext()) {
            JiraIssue myIssue = myIterator.next();
            if (pKey.equals(myIssue.getKey())) {
                return myIssue;
            }
        }

        /* Access client */
        IssueRestClient myIssueClient = theClient.getIssueClient();

        /* Protect against exceptions */
        try {
            /* Access full details of the issue */
            Issue myIss = myIssueClient.getIssue(pKey).claim();

            /* Add to the list */
            JiraIssue myIssue = new JiraIssue(theServer, myIss);
            theIssues.add(myIssue);
            return myIssue;

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issue: " + pKey, e);
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
            for (IssueType myType : getUnderlying().getIssueTypes()) {
                /* Add to the list */
                theIssueTypes.add(theServer.getIssueType(myType));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issue types", e);
        }
    }

    /**
     * Load Components.
     * @throws JOceanusException on error
     */
    private void loadComponents() throws JOceanusException {
        /* Access client */
        ComponentRestClient myClient = theClient.getComponentClient();

        /* Protect against exceptions */
        try {
            /* Loop through all components */
            for (BasicComponent myComp : getUnderlying().getComponents()) {
                /* Access component details */
                Component myComponent = myClient.getComponent(myComp.getSelf()).claim();

                /* Add to the list */
                theComponents.add(new JiraComponent(myComponent));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load components", e);
        }
    }

    /**
     * Load Versions.
     * @throws JOceanusException on error
     */
    private void loadVersions() throws JOceanusException {
        /* Access client */
        VersionRestClient myClient = theClient.getVersionRestClient();

        /* Protect against exceptions */
        try {
            /* Loop through all versions */
            for (Version myVers : getUnderlying().getVersions()) {
                /* Access version details */
                Version myVersion = myClient.getVersion(myVers.getSelf()).claim();

                /* Add to the list */
                theVersions.add(new JiraVersion(myVersion));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load versions", e);
        }
    }

    /**
     * Load Roles.
     * @throws JOceanusException on error
     */
    private void loadRoles() throws JOceanusException {
        /* Access client */
        ProjectRolesRestClient myClient = theClient.getProjectRolesRestClient();

        /* Protect against exceptions */
        try {
            /* Loop through all versions */
            for (BasicProjectRole myBasicRole : getUnderlying().getProjectRoles()) {
                /* Access version details */
                ProjectRole myRole = myClient.getRole(myBasicRole.getSelf()).claim();

                /* Add to the list */
                theRoles.add(new JiraProjectRole(myRole));
            }
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load roles", e);
        }
    }

    /**
     * Add new Component.
     * @param pComponent the component to add
     * @throws JOceanusException on error
     */
    public void addComponent(final String pComponent) throws JOceanusException {
        /* Check whether the component is already linked */
        Iterator<JiraComponent> myIterator = theComponents.iterator();
        while (myIterator.hasNext()) {
            JiraComponent myComp = myIterator.next();
            if (pComponent.equals(myComp.getName())) {
                return;
            }
        }

        /* Access client */
        ComponentRestClient myClient = theClient.getComponentClient();

        /* Protect against exceptions */
        try {
            /* Build component details */
            ComponentInput myInput = new ComponentInput(pComponent, null, theLead.getName(), null);

            /* Create the component for the project */
            Component myComp = myClient.createComponent(theKey, myInput).claim();

            /* Add the Version */
            theComponents.add(new JiraComponent(myComp));

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to add component", e);
        }
    }

    /**
     * Add new Version.
     * @param pVersion the version to add
     * @throws JOceanusException on error
     */
    public void addVersion(final String pVersion) throws JOceanusException {
        /* Check whether the version is already linked */
        Iterator<JiraVersion> myIterator = theVersions.iterator();
        while (myIterator.hasNext()) {
            JiraVersion myVers = myIterator.next();
            if (pVersion.equals(myVers.getName())) {
                return;
            }
        }

        /* Access client */
        VersionRestClient myClient = theClient.getVersionRestClient();

        /* Protect against exceptions */
        try {
            /* Build version details */
            VersionInputBuilder myBuilder = new VersionInputBuilder(theKey);
            myBuilder.setName(pVersion);
            myBuilder.setArchived(false);
            myBuilder.setReleased(false);
            VersionInput myInput = myBuilder.build();

            /* Create the version for the project */
            Version myVersion = myClient.createVersion(myInput).claim();

            /* Add the Version */
            theVersions.add(new JiraVersion(myVersion));

        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to add version", e);
        }
    }

    /**
     * Component class.
     */
    public final class JiraComponent
            extends JiraEntity<Component> {
        /**
         * The description of the component.
         */
        private final String theDesc;

        /**
         * Project Lead.
         */
        private final JiraUser theLead;

        /**
         * Get the description of the component.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

        /**
         * Get the lead of the component.
         * @return the lead
         */
        public JiraUser getLead() {
            return theLead;
        }

        /**
         * Constructor.
         * @param pComponent the underlying component
         * @throws JOceanusException on error
         */
        private JiraComponent(final Component pComponent) throws JOceanusException {
            /* Access the details */
            super(pComponent, pComponent.getName());
            theDesc = pComponent.getDescription();

            /* Determine the component lead */
            theLead = theServer.getUser(pComponent.getLead());
        }
    }

    /**
     * Version class.
     */
    public final class JiraVersion
            extends JiraEntity<Version> {
        /**
         * The description of the version.
         */
        private final String theDesc;

        /**
         * Release Date of version.
         */
        private DateTime theReleaseDate;

        /**
         * is the version archived.
         */
        private boolean isArchived;

        /**
         * is the version released.
         */
        private boolean isReleased;

        /**
         * Get the description of the version.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

        /**
         * Get the releaseDate of the version.
         * @return the releaseDate
         */
        public DateTime getReleaseDate() {
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
         * @param pVersion the underlying version
         */
        private JiraVersion(final Version pVersion) {
            /* Access the details */
            super(pVersion, pVersion.getName());
            theDesc = pVersion.getDescription();
            theReleaseDate = pVersion.getReleaseDate();
            isArchived = pVersion.isArchived();
            isReleased = pVersion.isReleased();
        }

        /**
         * Archive the version.
         * @param doArchive archive/restore version
         * @throws JOceanusException on error
         */
        public void setArchive(final boolean doArchive) throws JOceanusException {
            /* Access client */
            VersionRestClient myClient = theClient.getVersionRestClient();

            /* Protect against exceptions */
            try {
                /* Ignore if already in correct state */
                if (doArchive == isArchived) {
                    return;
                }

                /* Build version details */
                VersionInputBuilder myBuilder = new VersionInputBuilder(theKey);
                myBuilder.setArchived(doArchive);
                myBuilder.setReleased(isReleased);
                VersionInput myInput = myBuilder.build();

                /* Apply details to version */
                Version myVersion = myClient.updateVersion(getURI(), myInput).claim();

                /* Update status */
                isArchived = myVersion.isArchived();
                adjustEntity(myVersion);

            } catch (RestClientException e) {
                /* Pass the exception on */
                throw new JThemisIOException(ERROR_ARCH, e);
            }
        }

        /**
         * Release the version.
         * @throws JOceanusException on error
         */
        public void setReleased() throws JOceanusException {
            /* Access client */
            VersionRestClient myClient = theClient.getVersionRestClient();

            /* Protect against exceptions */
            try {
                /* Ignore if already in correct state */
                if (isReleased) {
                    return;
                }

                /* Build version details */
                VersionInputBuilder myBuilder = new VersionInputBuilder(theKey);
                myBuilder.setReleaseDate(new DateTime());
                myBuilder.setArchived(false);
                myBuilder.setReleased(true);
                VersionInput myInput = myBuilder.build();

                /* Apply details to version */
                Version myVersion = myClient.updateVersion(getURI(), myInput).claim();

                /* Adjust values */
                theReleaseDate = myVersion.getReleaseDate();
                isArchived = myVersion.isArchived();
                isReleased = myVersion.isReleased();
                adjustEntity(myVersion);

            } catch (RestClientException e) {
                /* Pass the exception on */
                throw new JThemisIOException(ERROR_ARCH, e);
            }
        }
    }

    /**
     * ProjectRole class.
     */
    public final class JiraProjectRole
            extends JiraEntity<ProjectRole> {
        /**
         * The description of the project role.
         */
        private final String theDesc;

        /**
         * List of actors.
         */
        private List<JiraRoleActor> theActors;

        /**
         * Get the description of the project role.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }

        /**
         * Get the list of actors.
         * @return the iterator
         */
        public Iterator<JiraRoleActor> actorIterator() {
            return theActors.iterator();
        }

        /**
         * Constructor.
         * @param pRole the underlying role
         * @throws JOceanusException on error
         */
        private JiraProjectRole(final ProjectRole pRole) throws JOceanusException {
            /* Access the details */
            super(pRole, pRole.getName());
            theDesc = pRole.getDescription();
            theActors = new ArrayList<JiraRoleActor>();

            /* Process the actors */
            for (RoleActor myActor : pRole.getActors()) {
                /* Add to the list */
                theActors.add(new JiraRoleActor(myActor));
            }
        }
    }

    /**
     * RoleActor class.
     */
    public final class JiraRoleActor {
        /**
         * The underlying role actor.
         */
        private final RoleActor theUnderlying;

        /**
         * The name of the actor.
         */
        private final String theName;

        /**
         * The full name of the actor.
         */
        private final String theFullName;

        /**
         * The actor of the actor.
         */
        private final Object theActor;

        /**
         * Get the underlying role actor.
         * @return the actor
         */
        protected RoleActor getUnderlying() {
            return theUnderlying;
        }

        /**
         * Get the name of the actor.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get the full name of the actor.
         * @return the full name
         */
        public String getFullName() {
            return theFullName;
        }

        /**
         * Is the actor a group.
         * @return true/false
         */
        public boolean isGroup() {
            return theActor instanceof JiraGroup;
        }

        /**
         * Get the actor user.
         * @return the user
         */
        public Object getActorUser() {
            return (theActor instanceof JiraUser)
                                                 ? (JiraUser) theActor
                                                 : null;
        }

        /**
         * Get the actor group.
         * @return the group
         */
        public Object getActorGroup() {
            return (theActor instanceof JiraGroup)
                                                  ? (JiraGroup) theActor
                                                  : null;
        }

        /**
         * Constructor.
         * @param pActor the underlying role
         * @throws JOceanusException on error
         */
        private JiraRoleActor(final RoleActor pActor) throws JOceanusException {
            /* Access the details */
            theUnderlying = pActor;
            theName = pActor.getName();
            theFullName = pActor.getDisplayName();

            /* Resolve the actor */
            String myType = pActor.getType();
            theActor = myType.equals(TYPE_ATLASSIAN_GROUP_ROLE)
                                                               ? theServer.getGroup(theName)
                                                               : theServer.getUser(theName);
        }
    }

    /**
     * Create Issue Status Field Availability.
     */
    public final class JiraCimIssueType {
        /**
         * The underlying issueType info.
         */
        private final CimIssueType theUnderlying;

        /**
         * The Issue type.
         */
        private final JiraIssueType theType;

        /**
         * The field info.
         */
        private Map<String, JiraCimFieldInfo> theFieldMap;

        /**
         * Get the underlying issue type.
         * @return the issue type
         */
        protected CimIssueType getUnderlying() {
            return theUnderlying;
        }

        /**
         * Get the issue type.
         * @return the issue type
         */
        public JiraIssueType getIssueType() {
            return theType;
        }

        /**
         * Get the field info for the field.
         * @param pField the field name
         * @return the info
         */
        public JiraCimFieldInfo getFieldInfo(final String pField) {
            return theFieldMap.get(pField);
        }

        /**
         * Get the field info list iterator.
         * @return the iterator
         */
        public Iterator<JiraCimFieldInfo> fieldIterator() {
            return theFieldMap.values().iterator();
        }

        /**
         * Constructor.
         * @param pType the underlying issue type
         * @throws JOceanusException on error
         */
        private JiraCimIssueType(final CimIssueType pType) throws JOceanusException {
            /* Access the details */
            theUnderlying = pType;
            theType = theServer.getIssueType(pType.getName());
            theFieldMap = new LinkedHashMap<String, JiraCimFieldInfo>();

            /* Process the fields */
            for (CimFieldInfo myInfo : pType.getFields().values()) {
                /* Add to the map */
                theFieldMap.put(myInfo.getName(), new JiraCimFieldInfo(myInfo));
            }
        }
    }

    /**
     * Create Issue Field Availability.
     */
    public static final class JiraCimFieldInfo {
        /**
         * The underlying field info.
         */
        private final CimFieldInfo theUnderlying;

        /**
         * The name of the field.
         */
        private final String theName;

        /**
         * The schema of the field.
         */
        private final FieldSchema theSchema;

        /**
         * Get the underlying field info.
         * @return the filed info
         */
        protected CimFieldInfo getUnderlying() {
            return theUnderlying;
        }

        /**
         * Get the field schema.
         * @return the schema
         */
        public FieldSchema getSchema() {
            return theSchema;
        }

        /**
         * Get the name of the field.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Is the field required?
         * @return true/false
         */
        public boolean isRequired() {
            return theUnderlying.isRequired();
        }

        /**
         * Get the list of allowed values.
         * @return the iterator
         */
        public Iterator<Object> valueIterator() {
            return theUnderlying.getAllowedValues().iterator();
        }

        /**
         * Get the list of allowed operations.
         * @return the iterator
         */
        public Iterator<StandardOperation> operationIterator() {
            return theUnderlying.getOperations().iterator();
        }

        /**
         * Constructor.
         * @param pInfo the underlying field info
         * @throws JOceanusException on error
         */
        private JiraCimFieldInfo(final CimFieldInfo pInfo) throws JOceanusException {
            /* Access the details */
            theUnderlying = pInfo;
            theName = pInfo.getName();
            theSchema = pInfo.getSchema();
        }
    }
}
