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

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.http.JiraClient;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.JThemisLogicException;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraGroup;
import net.sourceforge.joceanus.jthemis.jira.data.JiraSecurity.JiraUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Jira server.
 * @author Tony Washer
 */
public class JiraServer {
    /**
     * Create date time formatter.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * The Http Client.
     */
    private final JiraClient theClient;

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
    private final Map<String, JiraProject> theProjects;

    /**
     * IssueTypes.
     */
    private final Map<String, JiraIssueType> theIssueTypes;

    /**
     * StatusCategories.
     */
    private final Map<String, JiraStatusCategory> theStatusCategories;

    /**
     * Statuses.
     */
    private final Map<String, JiraStatus> theStatuses;

    /**
     * Resolutions.
     */
    private final Map<String, JiraResolution> theResolutions;

    /**
     * Priorities.
     */
    private final Map<String, JiraPriority> thePriorities;

    /**
     * IssueLinkTypes.
     */
    private final Map<String, JiraIssueLinkType> theIssueLinkTypes;

    /**
     * Constructor.
     * @param pManager the preference manager
     * @throws JOceanusException on error
     */
    public JiraServer(final PreferenceManager pManager) throws JOceanusException {
        /* Allocate the maps */
        theProjects = new HashMap<String, JiraProject>();
        theIssueLinkTypes = new HashMap<String, JiraIssueLinkType>();
        theIssueTypes = new HashMap<String, JiraIssueType>();
        theStatusCategories = new HashMap<String, JiraStatusCategory>();
        theStatuses = new HashMap<String, JiraStatus>();
        theResolutions = new HashMap<String, JiraResolution>();
        thePriorities = new HashMap<String, JiraPriority>();

        /* Access the Jira preferences */
        JiraPreferences myPreferences = pManager.getPreferenceSet(JiraPreferences.class);
        String myBaseUrl = myPreferences.getStringValue(JiraPreferences.NAME_SERVER);
        String myUser = myPreferences.getStringValue(JiraPreferences.NAME_USER);
        String myPass = myPreferences.getStringValue(JiraPreferences.NAME_PASS);

        /* Access the Jira Client */
        String myAuth = myUser + ":" + myPass;
        theClient = new JiraClient(myBaseUrl, myAuth);

        /* Allocate the security class */
        theSecurity = new JiraSecurity(this);
        theActive = getUser(myUser);

        /* Load constants */
        loadIssueTypes();
        loadIssueLinkTypes();
        loadResolutions();
        loadPriorities();
        loadStatusCategories();
        loadStatuses();

        /* Load projects */
        loadProjects();
    }

    /**
     * Obtain the client.
     * @return the client
     */
    protected JiraClient getClient() {
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
        return theProjects.values().iterator();
    }

    /**
     * Obtain the issue links iterator.
     * @return the iterator
     */
    public Iterator<JiraIssueLinkType> issueLinkTypeIterator() {
        return theIssueLinkTypes.values().iterator();
    }

    /**
     * Obtain the issueTypes iterator.
     * @return the iterator
     */
    public Iterator<JiraIssueType> issueTypeIterator() {
        return theIssueTypes.values().iterator();
    }

    /**
     * Obtain the resolutions iterator.
     * @return the iterator
     */
    public Iterator<JiraResolution> resolutionIterator() {
        return theResolutions.values().iterator();
    }

    /**
     * Obtain the priority iterator.
     * @return the iterator
     */
    public Iterator<JiraPriority> priorityIterator() {
        return thePriorities.values().iterator();
    }

    /**
     * Obtain the status iterator.
     * @return the iterator
     */
    public Iterator<JiraStatus> statusIterator() {
        return theStatuses.values().iterator();
    }

    /**
     * Parse DateTime object.
     * @param pSource the source string
     * @return the parsed dateTime (or null)
     */
    public static LocalDateTime parseJiraDateTime(final String pSource) {
        /* Protect against exceptions */
        try {
            /* If we have source to parse */
            LocalDateTime myResult = null;
            if (pSource != null) {
                TemporalAccessor myParsed = DATE_FORMATTER.parse(pSource);
                myResult = LocalDateTime.from(myParsed);
            }

            return myResult;
        } catch (DateTimeException e) {
            return null;
        }
    }

    /**
     * Obtain Project.
     * @param pProjectKey the project key
     * @return the Project
     * @throws JOceanusException on error
     */
    public JiraProject getProject(final String pProjectKey) throws JOceanusException {
        /* Look up project in the cache */
        JiraProject myProject = theProjects.get(pProjectKey);

        /* If not in the cache */
        if (myProject == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid Project: " + pProjectKey);
        }

        /* Return the project */
        return myProject;
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

            /* Look for project and load issue */
            JiraProject myProject = getProject(myKey);
            return myProject.getIssue(pKey);
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
    public JiraUser getUser(final String pName) throws JOceanusException {
        /* Pass the call to the security service */
        return theSecurity.getUser(pName);
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
     * Obtain IssueLinkType.
     * @param pName the name of the IssueLinkType
     * @return the IssueLinkType
     * @throws JOceanusException on error
     */
    public JiraIssueLinkType getIssueLinkType(final String pName) throws JOceanusException {
        /* Look up issueType in the cache */
        JiraIssueLinkType myType = theIssueLinkTypes.get(pName);

        /* If not in the cache */
        if (myType == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid IssueLinkType: " + pName);
        }

        /* Return the type */
        return myType;
    }

    /**
     * Obtain IssueType.
     * @param pName the name of the IssueType
     * @return the IssueType
     * @throws JOceanusException on error
     */
    public JiraIssueType getIssueType(final String pName) throws JOceanusException {
        /* Look up issueType in the cache */
        JiraIssueType myType = theIssueTypes.get(pName);

        /* If not in the cache */
        if (myType == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid IssueType: " + pName);
        }

        /* Return the type */
        return myType;
    }

    /**
     * Obtain Status.
     * @param pName the name of the Status
     * @return the Status
     * @throws JOceanusException on error
     */
    public JiraStatusCategory getStatusCategory(final String pName) throws JOceanusException {
        /* Look up status in the cache */
        JiraStatusCategory myCategory = theStatusCategories.get(pName);

        /* If not in the cache */
        if (myCategory == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid StatusCategory: " + pName);
        }

        /* Return the statusCategory */
        return myCategory;
    }

    /**
     * Obtain Status.
     * @param pName the name of the Status
     * @return the Status
     * @throws JOceanusException on error
     */
    public JiraStatus getStatus(final String pName) throws JOceanusException {
        /* Look up status in the cache */
        JiraStatus myStatus = theStatuses.get(pName);

        /* If not in the cache */
        if (myStatus == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid Status: " + pName);
        }

        /* Return the status */
        return myStatus;
    }

    /**
     * Obtain Resolution.
     * @param pName the name of the Resolution
     * @return the Resolution
     * @throws JOceanusException on error
     */
    public JiraResolution getResolution(final String pName) throws JOceanusException {
        /* Look up resolution in the cache */
        JiraResolution myResolution = theResolutions.get(pName);

        /* If not in the cache */
        if (myResolution == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid Resolution: " + pName);
        }

        /* Return the resolution */
        return myResolution;
    }

    /**
     * Obtain Priority.
     * @param pName the name of the Priority
     * @return the Priority
     * @throws JOceanusException on error
     */
    public JiraPriority getPriority(final String pName) throws JOceanusException {
        /* Look up priority in the cache */
        JiraPriority myPriority = thePriorities.get(pName);

        /* If not in the cache */
        if (myPriority == null) {
            /* throw exception */
            throw new JThemisLogicException("Invalid Priority: " + pName);
        }

        /* Return the priority */
        return myPriority;
    }

    /**
     * Load Projects.
     * @throws JOceanusException on error
     */
    private void loadProjects() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the project keys */
            JSONArray myProjects = theClient.getProjects();
            int myNumTypes = myProjects.length();
            for (int i = 0; i < myNumTypes; i++) {
                /* Access the type and register it */
                JSONObject myProjDtl = myProjects.getJSONObject(i);
                String myKey = myProjDtl.getString(JiraProject.FIELD_KEY);
                myProjDtl = theClient.getProject(myKey);
                JiraProject myProject = new JiraProject(this, myProjDtl);
                theProjects.put(myKey, myProject);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load projects", e);
        }
    }

    /**
     * Load IssueLinkTypes.
     * @throws JOceanusException on error
     */
    private void loadIssueLinkTypes() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the issue link types */
            JSONObject myTypes = theClient.getIssueLinkTypes();
            JSONArray myEntries = myTypes.getJSONArray("issueLinkTypes");
            int myNumTypes = myEntries.length();
            for (int i = 0; i < myNumTypes; i++) {
                /* Access the type and register it */
                JSONObject myTypeDtl = myEntries.getJSONObject(i);
                JiraIssueLinkType myType = new JiraIssueLinkType(myTypeDtl);
                theIssueLinkTypes.put(myType.getName(), myType);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issueLinkTypes", e);
        }
    }

    /**
     * Load IssueTypes.
     * @throws JOceanusException on error
     */
    private void loadIssueTypes() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the issue types */
            JSONArray myTypes = theClient.getIssueTypes();
            int myNumTypes = myTypes.length();
            for (int i = 0; i < myNumTypes; i++) {
                /* Access the type and register it */
                JSONObject myTypeDtl = myTypes.getJSONObject(i);
                JiraIssueType myType = new JiraIssueType(myTypeDtl);
                theIssueTypes.put(myType.getName(), myType);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load issue types", e);
        }
    }

    /**
     * Load StatusCategories.
     * @throws JOceanusException on error
     */
    private void loadStatusCategories() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the categories */
            JSONArray myCategories = theClient.getStatusCategories();
            int myNumCats = myCategories.length();
            for (int i = 0; i < myNumCats; i++) {
                /* Access the category and register it */
                JSONObject myCatDtl = myCategories.getJSONObject(i);
                JiraStatusCategory myCategory = new JiraStatusCategory(myCatDtl);
                theStatusCategories.put(myCategory.getName(), myCategory);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load statusCategories", e);
        }
    }

    /**
     * Load Statuses.
     * @throws JOceanusException on error
     */
    private void loadStatuses() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the statuses */
            JSONArray myStatuses = theClient.getStatuses();
            int myNumStatuses = myStatuses.length();
            for (int i = 0; i < myNumStatuses; i++) {
                /* Access the status and register it */
                JSONObject myStatusDtl = myStatuses.getJSONObject(i);
                JiraStatus myStatus = new JiraStatus(myStatusDtl);
                theStatuses.put(myStatus.getName(), myStatus);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load statuses", e);
        }
    }

    /**
     * Load Resolutions.
     * @throws JOceanusException on error
     */
    private void loadResolutions() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the resolutions */
            JSONArray myResolutions = theClient.getResolutions();
            int myNumRes = myResolutions.length();
            for (int i = 0; i < myNumRes; i++) {
                /* Access the resolution and register it */
                JSONObject myResDtl = myResolutions.getJSONObject(i);
                JiraResolution myRes = new JiraResolution(myResDtl);
                theResolutions.put(myRes.getName(), myRes);
            }
        } catch (JSONException e) {
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
            /* Access the priorities */
            JSONArray myPriorities = theClient.getPriorities();
            int myNumPri = myPriorities.length();
            for (int i = 0; i < myNumPri; i++) {
                /* Access the priority and register it */
                JSONObject myPriDtl = myPriorities.getJSONObject(i);
                JiraPriority myPri = new JiraPriority(myPriDtl);
                thePriorities.put(myPri.getName(), myPri);
            }
        } catch (JSONException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load priorities", e);
        }
    }

    /**
     * Generic Jira object.
     */
    public abstract static class JiraObject {
        /**
         * Self reference.
         */
        private final String theSelf;

        /**
         * Base object.
         */
        private final JSONObject theBase;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraObject(final JSONObject pBase) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Access the details */
                theBase = pBase;
                theSelf = pBase.getString("self");

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse object", e);
            }
        }

        /**
         * Get the Self reference for the object.
         * @return the reference
         */
        public String getSelf() {
            return theSelf;
        }

        /**
         * Get the Base object.
         * @return the base
         */
        public JSONObject getBase() {
            return theBase;
        }

        @Override
        public String toString() {
            return theBase.toString();
        }
    }

    /**
     * Generic Jira Id object.
     */
    public abstract static class JiraIdObject
            extends JiraObject {
        /**
         * The name field.
         */
        private static final String FIELD_ID = "id";

        /**
         * Id.
         */
        private final String theId;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraIdObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theId = pBase.getString(FIELD_ID);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse id object", e);
            }
        }

        /**
         * Get the id of the object.
         * @return the name
         */
        public String getId() {
            return theId;
        }
    }

    /**
     * Generic Jira Named Id object.
     */
    public abstract static class JiraNamedObject
            extends JiraObject {
        /**
         * The name field.
         */
        public static final String FIELD_NAME = "name";

        /**
         * The name of the object.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraNamedObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theName = pBase.getString(FIELD_NAME);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse named object", e);
            }
        }

        /**
         * Get the name of the object.
         * @return the name
         */
        public String getName() {
            return theName;
        }
    }

    /**
     * Generic Jira Named Id object.
     */
    public abstract static class JiraKeyedObject
            extends JiraObject {
        /**
         * The key field.
         */
        public static final String FIELD_KEY = "key";

        /**
         * The key of the object.
         */
        private final String theKey;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraKeyedObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theKey = pBase.getString(FIELD_KEY);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse keyed object", e);
            }
        }

        /**
         * Get the key of the object.
         * @return the key
         */
        public String getKey() {
            return theKey;
        }
    }

    /**
     * Generic Jira Named Id object.
     */
    public abstract static class JiraNamedIdObject
            extends JiraIdObject {
        /**
         * The name field.
         */
        public static final String FIELD_NAME = JiraNamedObject.FIELD_NAME;

        /**
         * The name of the object.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraNamedIdObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theName = pBase.getString(FIELD_NAME);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse named id object", e);
            }
        }

        /**
         * Get the name of the object.
         * @return the name
         */
        public String getName() {
            return theName;
        }
    }

    /**
     * Generic Jira Named Id object.
     */
    public abstract static class JiraNamedDescObject
            extends JiraNamedObject {
        /**
         * The name field.
         */
        public static final String FIELD_DESC = "description";

        /**
         * The description of the object.
         */
        private final String theDesc;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraNamedDescObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theDesc = pBase.getString(FIELD_DESC);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse named description object", e);
            }
        }

        /**
         * Get the description of the object.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }
    }

    /**
     * Generic Jira Named Id object.
     */
    public abstract static class JiraNamedDescIdObject
            extends JiraNamedIdObject {
        /**
         * The description field.
         */
        public static final String FIELD_DESC = JiraNamedDescObject.FIELD_DESC;

        /**
         * The description of the object.
         */
        private final String theDesc;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraNamedDescIdObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theDesc = pBase.getString(FIELD_DESC);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse named described id object", e);
            }
        }

        /**
         * Get the description of the object.
         * @return the description
         */
        public String getDescription() {
            return theDesc;
        }
    }

    /**
     * Generic Jira Keyed Id object.
     */
    public abstract static class JiraKeyedIdObject
            extends JiraIdObject {
        /**
         * The key field.
         */
        public static final String FIELD_KEY = JiraKeyedObject.FIELD_KEY;

        /**
         * The key of the object.
         */
        private final String theKey;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraKeyedIdObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theKey = pBase.getString(FIELD_KEY);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse keyed id object", e);
            }
        }

        /**
         * Get the name of the object.
         * @return the name
         */
        public String getKey() {
            return theKey;
        }
    }

    /**
     * Generic Jira Named Keyed object.
     */
    public abstract static class JiraNamedKeyedObject
            extends JiraNamedObject {
        /**
         * The key field.
         */
        public static final String FIELD_KEY = JiraKeyedIdObject.FIELD_KEY;

        /**
         * The key of the object.
         */
        private final String theKey;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraNamedKeyedObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theKey = pBase.getString(FIELD_KEY);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse named keyed object", e);
            }
        }

        /**
         * Get the key of the object.
         * @return the key
         */
        public String getKey() {
            return theKey;
        }
    }

    /**
     * Generic Jira Named Keyed object.
     */
    public abstract static class JiraNamedKeyedIdObject
            extends JiraNamedIdObject {
        /**
         * The key field.
         */
        public static final String FIELD_KEY = JiraKeyedIdObject.FIELD_KEY;

        /**
         * The key of the object.
         */
        private final String theKey;

        /**
         * Constructor.
         * @param pBase the base object
         * @throws JOceanusException on error
         */
        protected JiraNamedKeyedIdObject(final JSONObject pBase) throws JOceanusException {
            /* Parse base details */
            super(pBase);

            /* Protect against exceptions */
            try {
                /* Access the details */
                theKey = pBase.getString(FIELD_KEY);

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse named keyed id object", e);
            }
        }

        /**
         * Get the key of the object.
         * @return the key
         */
        public String getKey() {
            return theKey;
        }
    }

    /**
     * IssueType class.
     */
    public final class JiraIssueType
            extends JiraNamedDescIdObject {
        /**
         * Is this a subTask?
         */
        private final boolean isSubTask;

        /**
         * Constructor.
         * @param pType the base issueType
         * @throws JOceanusException on error
         */
        private JiraIssueType(final JSONObject pType) throws JOceanusException {
            /* Parse base details */
            super(pType);

            /* Protect against exceptions */
            try {
                /* Access the details */
                isSubTask = pType.getBoolean("subtask");

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse issueType", e);
            }
        }

        /**
         * Is this issue type a subTask?
         * @return true/false
         */
        public boolean isSubTask() {
            return isSubTask;
        }
    }

    /**
     * StatusCategory class.
     */
    public final class JiraStatusCategory
            extends JiraNamedObject {
        /**
         * Constructor.
         * @param pCategory the underlying status category
         * @throws JOceanusException on error
         */
        private JiraStatusCategory(final JSONObject pCategory) throws JOceanusException {
            /* Parse base details */
            super(pCategory);
        }
    }

    /**
     * Status class.
     */
    public final class JiraStatus
            extends JiraNamedDescIdObject {
        /**
         * The statusCategory of the status.
         */
        private final JiraStatusCategory theCategory;

        /**
         * Constructor.
         * @param pStatus the underlying status
         * @throws JOceanusException on error
         */
        private JiraStatus(final JSONObject pStatus) throws JOceanusException {
            /* Parse base details */
            super(pStatus);

            /* Protect against exceptions */
            try {
                JSONObject myCat = pStatus.getJSONObject("statusCategory");
                theCategory = getStatusCategory(myCat.getString("name"));

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse status", e);
            }
        }

        /**
         * Get the statusCategory of the status.
         * @return the category
         */
        public JiraStatusCategory getCategory() {
            return theCategory;
        }
    }

    /**
     * Resolution class.
     */
    public final class JiraResolution
            extends JiraNamedDescIdObject {
        /**
         * Constructor.
         * @param pResolution the underlying resolution
         * @throws JOceanusException on error
         */
        private JiraResolution(final JSONObject pResolution) throws JOceanusException {
            /* Parse base details */
            super(pResolution);
        }
    }

    /**
     * Priority class.
     */
    public final class JiraPriority
            extends JiraNamedDescIdObject {
        /**
         * The colour for this priority.
         */
        private final String theColor;

        /**
         * Constructor.
         * @param pPriority the underlying priority
         * @throws JOceanusException on error
         */
        private JiraPriority(final JSONObject pPriority) throws JOceanusException {
            /* Parse base details */
            super(pPriority);

            /* Protect against exceptions */
            try {
                theColor = pPriority.getString("statusColor");

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse priority", e);
            }
        }

        /**
         * Obtain the colour for this priority.
         * @return the colour
         */
        public String getColor() {
            return theColor;
        }
    }

    /**
     * IssueLinks class.
     */
    public final class JiraIssueLinkType
            extends JiraNamedIdObject {
        /**
         * The inward link name.
         */
        private final String theInward;

        /**
         * The outward link name.
         */
        private final String theOutward;

        /**
         * Constructor.
         * @param pLinkType the underlying link
         * @throws JOceanusException on error
         */
        private JiraIssueLinkType(final JSONObject pLinkType) throws JOceanusException {
            /* Parse base details */
            super(pLinkType);

            /* Protect against exceptions */
            try {
                theInward = pLinkType.getString("inward");
                theOutward = pLinkType.getString("outward");

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new JThemisIOException("Failed to parse issueLinkType", e);
            }
        }

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
    }
}