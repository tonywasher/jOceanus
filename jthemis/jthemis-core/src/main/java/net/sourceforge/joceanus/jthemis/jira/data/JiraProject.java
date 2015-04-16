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
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.http.JiraClient;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.JThemisLogicException;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraGroup;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraUser;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraIssueType;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraNamedDescIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraNamedDescObject;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraNamedKeyedIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraNamedObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Jira project.
 * @author Tony Washer
 */
public class JiraProject
        extends JiraNamedKeyedIdObject {
    /**
     * Group role name.
     */
    private static final String TYPE_ATLASSIAN_GROUP_ROLE = "atlassian-group-role-actor";

    /**
     * Server.
     */
    private final JiraServer theServer;

    /**
     * The client.
     */
    private final JiraClient theClient;

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
    private final Map<String, JiraIssue> theIssues;

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
     * @throws JOceanusException on error
     */
    protected JiraProject(final JiraServer pServer,
                          final JSONObject pProject) throws JOceanusException {
        /* Store parameters */
        super(pProject);
        theServer = pServer;
        theClient = theServer.getClient();

        /* Protect against exceptions */
        try {
            /* Access parameters */
            theDesc = pProject.getString(JiraNamedDescIdObject.FIELD_DESC);

            /* Determine the project lead */
            JSONObject myLeadDtl = pProject.getJSONObject("lead");
            theLead = theServer.getUser(myLeadDtl.getString(JiraUser.FIELD_NAME));

            /* Allocate the maps */
            theIssues = new HashMap<String, JiraIssue>();
            theIssueTypes = new ArrayList<JiraIssueType>();
            theComponents = new HashMap<String, JiraComponent>();
            theVersions = new HashMap<String, JiraVersion>();
            theRoles = new HashMap<String, JiraProjectRole>();

            /* Load project details */
            loadIssueTypes(pProject);
            loadComponents();
            loadVersions();
            loadRoles(pProject);

            /* Load the issue keys */
            theIssueKeys = theClient.getIssueKeysForProject(getKey());
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to parse project", e);
        }
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
     * @throws JOceanusException on error
     */
    protected JiraComponent getComponent(final String pName) throws JOceanusException {
        /* Look up component in the cache */
        JiraComponent myComp = theComponents.get(pName);

        /* If not in the cache */
        if (myComp == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid Component: " + pName);
        }

        /* Return the component */
        return myComp;
    }

    /**
     * Obtain Version.
     * @param pName the name of the Version
     * @return the Version
     * @throws JOceanusException on error
     */
    protected JiraVersion getVersion(final String pName) throws JOceanusException {
        /* Look up version in the cache */
        JiraVersion myVers = theVersions.get(pName);

        /* If not in the cache */
        if (myVers == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid Version: " + pName);
        }

        /* Return the version */
        return myVers;
    }

    /**
     * Obtain Issue for key.
     * @param pKey the issue key
     * @return the issue
     * @throws JOceanusException on error
     */
    public JiraIssue getIssue(final String pKey) throws JOceanusException {
        /* Look up issue in the cache */
        JiraIssue myIssue = theIssues.get(pKey);

        /* If not in the cache */
        if (myIssue == null) {
            /* Check that it is a valid issue */
            if (!theIssueKeys.contains(pKey)) {
                /* throw exception */
                throw new JThemisLogicException("Invalid Issue: " + pKey);
            }

            /* Obtain the issue and register it */
            JSONObject myIssueDtl = theClient.getIssue(pKey);
            myIssue = new JiraIssue(theServer, myIssueDtl);
            theIssues.put(pKey, myIssue);
        }

        /* Return the issue */
        return myIssue;
    }

    /**
     * Load IssueTypes.
     * @param pProject the project details
     * @throws JOceanusException on error
     */
    private void loadIssueTypes(final JSONObject pProject) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the issue types */
            JSONArray myTypes = pProject.getJSONArray("issueTypes");
            int myNumTypes = myTypes.length();
            for (int i = 0; i < myNumTypes; i++) {
                /* Access the type and register it */
                JSONObject myTypeDtl = myTypes.getJSONObject(i);
                String myName = myTypeDtl.getString("name");
                JiraIssueType myType = theServer.getIssueType(myName);
                theIssueTypes.add(myType);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issueTypes", e);
        }
    }

    /**
     * Load Components.
     * @throws JOceanusException on error
     */
    private void loadComponents() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the components */
            JSONArray myComps = theClient.getComponentsForProject(getKey());
            int myNumComps = myComps.length();
            for (int i = 0; i < myNumComps; i++) {
                /* Access the component and register it */
                JSONObject myCompDtl = myComps.getJSONObject(i);
                JiraComponent myComp = new JiraComponent(myCompDtl);
                theComponents.put(myComp.getName(), myComp);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load components", e);
        }
    }

    /**
     * Load Versions.
     * @throws JOceanusException on error
     */
    private void loadVersions() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the versions */
            JSONArray myVers = theClient.getVersionsForProject(getKey());
            int myNumVers = myVers.length();
            for (int i = 0; i < myNumVers; i++) {
                /* Access the version and register it */
                JSONObject myVersDtl = myVers.getJSONObject(i);
                JiraVersion myVersion = new JiraVersion(myVersDtl);
                theVersions.put(myVersion.getName(), myVersion);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load versions", e);
        }
    }

    /**
     * Load Roles.
     * @param pProject the project details
     * @throws JOceanusException on error
     */
    private void loadRoles(final JSONObject pProject) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the roles */
            JSONObject myRoles = pProject.getJSONObject("roles");
            Iterator<String> myIterator = myRoles.keys();
            while (myIterator.hasNext()) {
                /* Access the role */
                String myKey = myIterator.next();
                String myURL = myRoles.getString(myKey);
                JSONObject myRoleDtl = theClient.getProjectRoleFromURL(myURL);
                JiraProjectRole myRole = new JiraProjectRole(myRoleDtl);
                theRoles.put(myKey, myRole);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load project roles", e);
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
         * @throws JOceanusException on error
         */
        private JiraComponent(final JSONObject pComponent) throws JOceanusException {
            /* Access the details */
            super(pComponent);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theLead = theServer.getUser(pComponent.getString("leadUserName"));

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse component", e);
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
         * @throws JOceanusException on error
         */
        private JiraVersion(final JSONObject pVersion) throws JOceanusException {
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
                throw new JThemisIOException("Failed to parse version", e);
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
         * @throws JOceanusException on error
         */
        private JiraProjectRole(final JSONObject pRole) throws JOceanusException {
            /* Access the details */
            super(pRole);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theActors = new ArrayList<JiraRoleActor>();
                JSONArray myActors = pRole.getJSONArray("actors");
                int myNumActors = myActors.length();
                for (int i = 0; i < myNumActors; i++) {
                    /* Access the actor and register it */
                    JSONObject myActorDtl = myActors.getJSONObject(i);
                    JiraRoleActor myActor = new JiraRoleActor(myActorDtl);
                    theActors.add(myActor);
                }

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse role", e);
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
         * @throws JOceanusException on error
         */
        private JiraRoleActor(final JSONObject pActor) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Access the details */
                theName = pActor.getString(JiraNamedObject.FIELD_NAME);
                theFullName = pActor.getString("displayName");

                /* Resolve the actor */
                String myType = pActor.getString("type");
                theActor = myType.equals(TYPE_ATLASSIAN_GROUP_ROLE)
                                                                   ? theServer.getGroup(theName)
                                                                   : theServer.getUser(theName);
            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse roleActor", e);
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
    }
}