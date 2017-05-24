/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraNamedObject;

/**
 * Handles security for a jira server.
 * @author Tony Washer
 */
public class ThemisJiraSecurity {
    /**
     * HTTP Client.
     */
    private final ThemisHTTPJiraClient theClient;

    /**
     * Users.
     */
    private final Map<String, JiraUser> theUsers;

    /**
     * Groups.
     */
    private final Map<String, JiraGroup> theGroups;

    /**
     * Constructor.
     * @param pServer the server
     * @throws OceanusException on error
     */
    protected ThemisJiraSecurity(final ThemisJiraServer pServer) throws OceanusException {
        /* Access clients */
        theClient = pServer.getClient();

        /* Allocate the lists */
        theUsers = new HashMap<>();
        theGroups = new HashMap<>();
    }

    /**
     * Obtain User.
     * @param pName the name of the user
     * @return the User
     * @throws OceanusException on error
     */
    public JiraUser getUser(final String pName) throws OceanusException {
        /* Look up user in the cache */
        JiraUser myUser = theUsers.get(pName);

        /* If not in the cache */
        if (myUser == null) {
            /* Access the user details */
            JSONObject myUserDtl = theClient.getUser(pName);

            /* Create User object and add to list */
            myUser = new JiraUser(myUserDtl);
            theUsers.put(pName, myUser);
        }

        /* Return the user */
        return myUser;
    }

    /**
     * Obtain Group.
     * @param pName the name of the group
     * @return the Group
     * @throws OceanusException on error
     */
    public JiraGroup getGroup(final String pName) throws OceanusException {
        /* Look up group in the cache */
        JiraGroup myGroup = theGroups.get(pName);

        /* If not in the cache */
        if (myGroup == null) {
            /* Access the group details */
            JSONObject myGroupDtl = theClient.getGroup(pName);

            /* Create Group object and add to list */
            myGroup = new JiraGroup(myGroupDtl);
            theGroups.put(pName, myGroup);
        }

        /* Return the group */
        return myGroup;
    }

    /**
     * User class.
     */
    public final class JiraUser
            extends JiraNamedObject {
        /**
         * The full name of the user.
         */
        private final String theFullName;

        /**
         * The eMail address of the user.
         */
        private final String theEMail;

        /**
         * The list of groups.
         */
        private final List<JiraGroup> theGroups;

        /**
         * Resolved groups.
         */
        private boolean isResolved;

        /**
         * Constructor.
         * @param pUser the underlying user
         * @throws OceanusException on error
         */
        private JiraUser(final JSONObject pUser) throws OceanusException {
            /* Parse base details */
            super(pUser);

            /* Initialise the group list */
            isResolved = false;
            theGroups = new ArrayList<>();

            /* Protect against exceptions */
            try {
                /* Access the details */
                theFullName = pUser.getString("displayName");
                theEMail = pUser.optString("emailAddress");

            } catch (JSONException e) {
                /* Pass the exception on */
                throw new ThemisIOException("Failed to parse user", e);
            }
        }

        /**
         * Get the full name of the user.
         * @return the full name
         */
        public String getFullname() {
            return theFullName;
        }

        /**
         * Get the eMail of the user.
         * @return the eMail
         */
        public String getEMail() {
            return theEMail;
        }

        /**
         * Obtain the group iterator.
         * @return the iterator
         * @throws OceanusException on error
         */
        public Iterator<JiraGroup> groupIterator() throws OceanusException {
            /* If we have not resolved the groups */
            if (!isResolved) {
                /* Protect against exceptions */
                try {
                    /* Access the users */
                    JSONObject mySelf = theClient.getUserGroups(getName());
                    JSONObject myGroups = mySelf.getJSONObject("groups");
                    JSONArray myItems = myGroups.getJSONArray("items");
                    int myNumGroups = myGroups.getInt("size");
                    for (int i = 0; i < myNumGroups; i++) {
                        /* Access the group and record it */
                        JSONObject myGroupDtl = myItems.getJSONObject(i);
                        JiraGroup myGroup = getGroup(myGroupDtl.getString("name"));
                        theGroups.add(myGroup);
                    }

                } catch (JSONException e) {
                    /* Pass the exception on */
                    throw new ThemisIOException("Failed to parse user groups", e);
                }

                /* Set resolved flag */
                isResolved = true;
            }

            /* return the iterator */
            return theGroups.iterator();
        }
    }

    /**
     * Group class.
     */
    public final class JiraGroup
            extends JiraNamedObject {
        /**
         * The list of members.
         */
        private final List<JiraUser> theMembers;

        /**
         * Resolved members.
         */
        private boolean isResolved;

        /**
         * Constructor.
         * @param pGroup the base group object
         * @throws OceanusException on error
         */
        private JiraGroup(final JSONObject pGroup) throws OceanusException {
            /* Parse base details */
            super(pGroup);

            /* Initialise the member list */
            isResolved = false;
            theMembers = new ArrayList<>();
        }

        /**
         * Obtain the member iterator.
         * @return the iterator
         * @throws OceanusException on error
         */
        public Iterator<JiraUser> memberIterator() throws OceanusException {
            /* If we have not resolved the users */
            if (!isResolved) {
                /* Protect against exceptions */
                try {
                    /* Access the users */
                    JSONObject mySelf = theClient.getGroupUsers(getName());
                    JSONObject myUsers = mySelf.getJSONObject("users");
                    JSONArray myItems = myUsers.getJSONArray("items");
                    int myNumUsers = myUsers.getInt("size");
                    for (int i = 0; i < myNumUsers; i++) {
                        /* Access the user and record it */
                        JSONObject myUserDtl = myItems.getJSONObject(i);
                        JiraUser myUser = getUser(myUserDtl.getString("name"));
                        theMembers.add(myUser);
                    }

                } catch (JSONException e) {
                    /* Pass the exception on */
                    throw new ThemisIOException("Failed to parse group users", e);
                }

                /* Set resolved flag */
                isResolved = true;
            }

            /* return the iterator */
            return theMembers.iterator();
        }
    }
}
