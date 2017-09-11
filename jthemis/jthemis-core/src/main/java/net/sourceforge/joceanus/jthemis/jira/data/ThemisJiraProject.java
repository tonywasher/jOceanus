/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2017 Tony Washer
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
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisLogicException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraSecurity.JiraGroup;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraSecurity.JiraUser;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraIssueType;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraNamedDescIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraNamedDescObject;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraNamedKeyedIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraNamedObject;

/**
 * Represents a Jira project.
 * @author Tony Washer
 */
public class ThemisJiraProject
        extends JiraNamedKeyedIdObject {
    /**
     * Group role name.
     */
    static final String TYPE_ATLASSIAN_GROUP_ROLE = "atlassian-group-role-actor";

    /**
     * Server.
     */
    private final ThemisJiraServer theServer;

    /**
     * The client.
     */
    private final ThemisHTTPJiraClient theClient;

    /**
     * Description of Project.
     */
    private final String theDesc;

    /**
     * Project Lead.
     */
    private final JiraUser theLead;

    /**
     * Issue Keys.
     */
    private final List<String> theIssueKeys;

    /**
     * Issues.
     */
    private final Map<String, ThemisJiraIssue> theIssues;

    /**
     * IssueTypes.
     */
    private final List<JiraIssueType> theIssueTypes;

    /**
     * Components.
     */
    private final Map<String, JiraComponent> theComponents;

    /**
     * Versions.
     */
    private final Map<String, JiraVersion> theVersions;

    /**
     * Roles.
     */
    private final Map<String, JiraProjectRole> theRoles;

    /**
     * Constructor.
     * @param pServer the server
     * @param pProject the underlying project
     * @throws OceanusException on error
     */
    protected ThemisJiraProject(final ThemisJiraServer pServer,
                                final JSONObject pProject) throws OceanusException {
        /* Store parameters */
        super(pProject);
        theServer = pServer;
        theClient = theServer.getClient();

        /* Protect against exceptions */
        try {
            /* Access parameters */
            theDesc = pProject.getString(JiraNamedDescIdObject.FIELD_DESC);

            /* Determine the project lead */
            final JSONObject myLeadDtl = pProject.getJSONObject("lead");
            theLead = theServer.getUser(myLeadDtl.getString(JiraUser.FIELD_NAME));

            /* Allocate the maps */
            theIssues = new HashMap<>();
            theIssueTypes = new ArrayList<>();
            theComponents = new HashMap<>();
            theVersions = new HashMap<>();
            theRoles = new HashMap<>();

            /* Load project details */
            loadIssueTypes(pProject);
            loadComponents();
            loadVersions();
            loadRoles(pProject);

            /* Load the issue keys */
            theIssueKeys = theClient.getIssueKeysForProject(getKey());
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new ThemisIOException("Failed to parse project", e);
        }
    }

    /**
     * Get the server.
     * @return the server
     */
    ThemisJiraServer getServer() {
        return theServer;
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
        return theComponents.values().iterator();
    }

    /**
     * Get the versions iterator.
     * @return the iterator
     */
    public Iterator<JiraVersion> versionIterator() {
        return theVersions.values().iterator();
    }

    /**
     * Get the roles iterator.
     * @return the iterator
     */
    public Iterator<JiraProjectRole> roleIterator() {
        return theRoles.values().iterator();
    }

    /**
     * Get the issueKeys iterator.
     * @return the iterator
     */
    public Iterator<String> issueKeyIterator() {
        return theIssueKeys.iterator();
    }

    /**
     * Obtain Component.
     * @param pName the name of the Component
     * @return the Component
     * @throws OceanusException on error
     */
    protected JiraComponent getComponent(final String pName) throws OceanusException {
        /* Look up component in the cache */
        final JiraComponent myComp = theComponents.get(pName);

        /* If not in the cache */
        if (myComp == null) {
            /* throw exception */
            throw new ThemisLogicException("Invalid Component: " + pName);
        }

        /* Return the component */
        return myComp;
    }

    /**
     * Obtain Version.
     * @param pName the name of the Version
     * @return the Version
     * @throws OceanusException on error
     */
    protected JiraVersion getVersion(final String pName) throws OceanusException {
        /* Look up version in the cache */
        final JiraVersion myVers = theVersions.get(pName);

        /* If not in the cache */
        if (myVers == null) {
            /* throw exception */
            throw new ThemisLogicException("Invalid Version: " + pName);
        }

        /* Return the version */
        return myVers;
    }

    /**
     * Obtain Issue for key.
     * @param pKey the issue key
     * @return the issue
     * @throws OceanusException on error
     */
    public ThemisJiraIssue getIssue(final String pKey) throws OceanusException {
        /* Look up issue in the cache */
        ThemisJiraIssue myIssue = theIssues.get(pKey);

        /* If not in the cache */
        if (myIssue == null) {
            /* Check that it is a valid issue */
            if (!theIssueKeys.contains(pKey)) {
                /* throw exception */
                throw new ThemisLogicException("Invalid Issue: " + pKey);
            }

            /* Obtain the issue and register it */
            final JSONObject myIssueDtl = theClient.getIssue(pKey);
            myIssue = new ThemisJiraIssue(theServer, myIssueDtl);
            theIssues.put(pKey, myIssue);
        }

        /* Return the issue */
        return myIssue;
    }

    /**
     * Load IssueTypes.
     * @param pProject the project details
     * @throws OceanusException on error
     */
    private void loadIssueTypes(final JSONObject pProject) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the issue types */
            final JSONArray myTypes = pProject.getJSONArray("issueTypes");
            final int myNumTypes = myTypes.length();
            for (int i = 0; i < myNumTypes; i++) {
                /* Access the type and register it */
                final JSONObject myTypeDtl = myTypes.getJSONObject(i);
                final String myName = myTypeDtl.getString("name");
                final JiraIssueType myType = theServer.getIssueType(myName);
                theIssueTypes.add(myType);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new ThemisIOException("Failed to load issueTypes", e);
        }
    }

    /**
     * Load Components.
     * @throws OceanusException on error
     */
    private void loadComponents() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the components */
            final JSONArray myComps = theClient.getComponentsForProject(getKey());
            final int myNumComps = myComps.length();
            for (int i = 0; i < myNumComps; i++) {
                /* Access the component and register it */
                final JSONObject myCompDtl = myComps.getJSONObject(i);
                final JiraComponent myComp = new JiraComponent(myCompDtl);
                theComponents.put(myComp.getName(), myComp);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new ThemisIOException("Failed to load components", e);
        }
    }

    /**
     * Load Versions.
     * @throws OceanusException on error
     */
    private void loadVersions() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the versions */
            final JSONArray myVers = theClient.getVersionsForProject(getKey());
            final int myNumVers = myVers.length();
            for (int i = 0; i < myNumVers; i++) {
                /* Access the version and register it */
                final JSONObject myVersDtl = myVers.getJSONObject(i);
                final JiraVersion myVersion = new JiraVersion(myVersDtl);
                theVersions.put(myVersion.getName(), myVersion);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new ThemisIOException("Failed to load versions", e);
        }
    }

    /**
     * Load Roles.
     * @param pProject the project details
     * @throws OceanusException on error
     */
    private void loadRoles(final JSONObject pProject) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the roles */
            final JSONObject myRoles = pProject.getJSONObject("roles");
            final Iterator<String> myIterator = myRoles.keys();
            while (myIterator.hasNext()) {
                /* Access the role */
                final String myKey = myIterator.next();
                final String myURL = myRoles.getString(myKey);
                final JSONObject myRoleDtl = theClient.getProjectRoleFromURL(myURL);
                final JiraProjectRole myRole = new JiraProjectRole(myRoleDtl);
                theRoles.put(myKey, myRole);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new ThemisIOException("Failed to load project roles", e);
        }
    }

    /**
     * Component class.
     */
    public final class JiraComponent
            extends JiraNamedDescIdObject {
        /**
         * Project Lead.
         */
        private final JiraUser theLead;

        /**
         * Constructor.
         * @param pComponent the underlying component
         * @throws OceanusException on error
         */
        JiraComponent(final JSONObject pComponent) throws OceanusException {
            /* Access the details */
            super(pComponent);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theLead = getServer().getUser(pComponent.getString("leadUserName"));

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new ThemisIOException("Failed to parse component", e);
            }
        }

        /**
         * Get the lead of the component.
         * @return the lead
         */
        public JiraUser getLead() {
            return theLead;
        }
    }

    /**
     * Version class.
     */
    public final class JiraVersion
            extends JiraNamedDescIdObject {
        /**
         * Release Date of version.
         */
        private String theReleaseDate;

        /**
         * is the version archived.
         */
        private boolean isArchived;

        /**
         * is the version released.
         */
        private boolean isReleased;

        /**
         * Constructor.
         * @param pVersion the underlying version
         * @throws OceanusException on error
         */
        JiraVersion(final JSONObject pVersion) throws OceanusException {
            /* Access the details */
            super(pVersion);

            /* Protect against exceptions */
            try {
                /* Access the details */
                isArchived = pVersion.getBoolean("archived");
                isReleased = pVersion.getBoolean("released");
                theReleaseDate = pVersion.getString("releaseDate");

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new ThemisIOException("Failed to parse version", e);
            }
        }

        /**
         * Get the releaseDate of the version.
         * @return the releaseDate
         */
        public String getReleaseDate() {
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
    }

    /**
     * ProjectRole class.
     */
    public final class JiraProjectRole
            extends JiraNamedDescObject {
        /**
         * List of actors.
         */
        private List<JiraRoleActor> theActors;

        /**
         * Constructor.
         * @param pRole the underlying role
         * @throws OceanusException on error
         */
        JiraProjectRole(final JSONObject pRole) throws OceanusException {
            /* Access the details */
            super(pRole);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theActors = new ArrayList<>();
                final JSONArray myActors = pRole.getJSONArray("actors");
                final int myNumActors = myActors.length();
                for (int i = 0; i < myNumActors; i++) {
                    /* Access the actor and register it */
                    final JSONObject myActorDtl = myActors.getJSONObject(i);
                    final JiraRoleActor myActor = new JiraRoleActor(myActorDtl);
                    theActors.add(myActor);
                }

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new ThemisIOException("Failed to parse role", e);
            }
        }

        /**
         * Get the list of actors.
         * @return the iterator
         */
        public Iterator<JiraRoleActor> actorIterator() {
            return theActors.iterator();
        }
    }

    /**
     * RoleActor class.
     */
    public final class JiraRoleActor {
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
         * Constructor.
         * @param pActor the underlying role
         * @throws OceanusException on error
         */
        JiraRoleActor(final JSONObject pActor) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Access the details */
                theName = pActor.getString(JiraNamedObject.FIELD_NAME);
                theFullName = pActor.getString("displayName");

                /* Resolve the actor */
                final String myType = pActor.getString("type");
                theActor = myType.equals(TYPE_ATLASSIAN_GROUP_ROLE)
                                                                    ? getServer().getGroup(theName)
                                                                    : getServer().getUser(theName);
            } catch (JSONException e) {
                /* Pass the exception on */
                throw new ThemisIOException("Failed to parse roleActor", e);
            }
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
            return theActor instanceof JiraUser
                                                ? (JiraUser) theActor
                                                : null;
        }

        /**
         * Get the actor group.
         * @return the group
         */
        public Object getActorGroup() {
            return theActor instanceof JiraGroup
                                                 ? (JiraGroup) theActor
                                                 : null;
        }
    }
}
