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
import net.sourceforge.joceanus.jthemis.jira.data.JiraServer.JiraEntity;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.UserRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.User;

/**
 * Handles security for a jira server.
 * @author Tony Washer
 */
public class JiraSecurity {
    /**
     * User Client.
     */
    private final UserRestClient theUserClient;

    /**
     * Users.
     */
    private final List<JiraUser> theUsers;

    /**
     * Groups.
     */
    private final List<JiraGroup> theGroups;

    /**
     * Constructor.
     * @param pServer the server
     * @throws JOceanusException on error
     */
    protected JiraSecurity(final JiraServer pServer) throws JOceanusException {
        /* Access clients */
        JiraRestClient myClient = pServer.getClient();
        theUserClient = myClient.getUserClient();

        /* Allocate the lists */
        theUsers = new ArrayList<JiraUser>();
        theGroups = new ArrayList<JiraGroup>();
    }

    /**
     * Obtain User.
     * @param pName the name of the user
     * @return the User
     * @throws JOceanusException on error
     */
    public JiraUser getUser(final String pName) throws JOceanusException {
        /* Return an existing user if found in list */
        Iterator<JiraUser> myIterator = theUsers.iterator();
        while (myIterator.hasNext()) {
            JiraUser myUser = myIterator.next();
            if (pName.equals(myUser.getName())) {
                return myUser;
            }
        }

        /* Protect against exceptions */
        try {
            /* Access the user details */
            User myUserDtl = theUserClient.getUser(pName).claim();

            /* Create User object and add to list */
            JiraUser myUser = new JiraUser(myUserDtl);
            theUsers.add(myUser);

            /* Return it */
            return myUser;
        } catch (RestClientException e) {
            /* Pass the exception on */
            throw new JThemisIOException("Failed to load user " + pName, e);
        }
    }

    /**
     * Obtain User.
     * @param pUser the basic user
     * @return the User
     * @throws JOceanusException on error
     */
    protected JiraUser getUser(final BasicUser pUser) throws JOceanusException {
        /* Use name to search */
        return getUser(pUser.getName());
    }

    /**
     * Obtain Group.
     * @param pName the name of the group
     * @return the Group
     */
    public JiraGroup getGroup(final String pName) {
        /* Return an existing group if found in list */
        Iterator<JiraGroup> myIterator = theGroups.iterator();
        while (myIterator.hasNext()) {
            JiraGroup myGroup = myIterator.next();
            if (pName.equals(myGroup.getName())) {
                return myGroup;
            }
        }

        /* Create Group object and add to list */
        JiraGroup myGroup = new JiraGroup(pName);
        theGroups.add(myGroup);

        /* Return it */
        return myGroup;
    }

    /**
     * User class.
     */
    public final class JiraUser
            extends JiraEntity<User> {
        /**
         * The full name of the user.
         */
        private final String theFullName;

        /**
         * Get the full name of the user.
         * @return the full name
         */
        public String getFullname() {
            return theFullName;
        }

        /**
         * Constructor.
         * @param pUser the underlying user
         */
        private JiraUser(final User pUser) {
            /* Access the details */
            super(pUser, pUser.getName());
            theFullName = pUser.getDisplayName();

            /* Loop through the groups */
            for (String myGroupName : pUser.getGroups().getItems()) {
                /* Access the group and register with it */
                JiraGroup myGroup = getGroup(myGroupName);
                myGroup.registerUser(this);
            }
        }
    }

    /**
     * Group class.
     */
    public static final class JiraGroup {
        /**
         * The name of the group.
         */
        private final String theName;

        /**
         * The list of members.
         */
        private final List<JiraUser> theMembers;

        /**
         * Get the name of the group.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Constructor.
         * @param pName the group name
         */
        private JiraGroup(final String pName) {
            /* Access the details */
            theName = pName;

            /* Create the user list */
            theMembers = new ArrayList<JiraUser>();
        }

        /**
         * Register user.
         * @param pUser the user to register
         */
        private void registerUser(final JiraUser pUser) {
            /* If the user is not registered */
            if (!theMembers.contains(pUser)) {
                /* Add the user to the members */
                theMembers.add(pUser);
            }
        }
    }
}
