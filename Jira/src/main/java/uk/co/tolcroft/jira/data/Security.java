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
package uk.co.tolcroft.jira.data;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import uk.co.tolcroft.jira.soap.JiraSoapService;
import uk.co.tolcroft.jira.soap.RemoteGroup;
import uk.co.tolcroft.jira.soap.RemoteProjectRole;
import uk.co.tolcroft.jira.soap.RemoteUser;

/**
 * Handles security for a jira server.
 * @author Tony Washer
 */
public class Security {
    /**
     * Server.
     */
    private final Server theServer;

    /**
     * Service.
     */
    private final JiraSoapService theService;

    /**
     * Users.
     */
    private final List<User> theUsers;

    /**
     * Groups.
     */
    private final List<Group> theGroups;

    /**
     * Roles.
     */
    private final List<Role> theRoles;

    /**
     * Constructor.
     * @param pServer the server
     * @throws JDataException on error
     */
    protected Security(final Server pServer) throws JDataException {
        /* Store parameters */
        theServer = pServer;
        theService = theServer.getService();

        /* Allocate the lists */
        theUsers = new ArrayList<User>();
        theGroups = new ArrayList<Group>();
        theRoles = new ArrayList<Role>();

        /* Load Roles */
        loadRoles();
    }

    /**
     * Obtain User.
     * @param pName the name of the user
     * @return the User
     * @throws JDataException on error
     */
    public User getUser(final String pName) throws JDataException {
        /* Return an existing user if found in list */
        Iterator<User> myIterator = theUsers.iterator();
        while (myIterator.hasNext()) {
            User myUser = myIterator.next();
            if (myUser.getName().equals(pName)) {
                return myUser;
            }
        }

        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Access the user and add to list */
            RemoteUser myUserDtl = theService.getUser(myToken, pName);
            User myUser = new User(myUserDtl);
            theUsers.add(myUser);
            return myUser;
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to load user " + pName, e);
        }
    }

    /**
     * Obtain User.
     * @param pUser the Remote User definition
     * @return the User
     */
    private User getUser(final RemoteUser pUser) {
        /* Return an existing user if found in list */
        Iterator<User> myIterator = theUsers.iterator();
        while (myIterator.hasNext()) {
            User myUser = myIterator.next();
            if (myUser.getName().equals(pUser.getName())) {
                return myUser;
            }
        }

        /* Access the user and add to list */
        User myUser = new User(pUser);
        theUsers.add(myUser);
        return myUser;
    }

    /**
     * Obtain Group.
     * @param pName the name of the group
     * @return the Group
     * @throws JDataException on error
     */
    public Group getGroup(final String pName) throws JDataException {
        /* Return an existing group if found in list */
        Iterator<Group> myIterator = theGroups.iterator();
        while (myIterator.hasNext()) {
            Group myGroup = myIterator.next();
            if (myGroup.getName().equals(pName)) {
                return myGroup;
            }
        }

        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Access the group and add to list */
            RemoteGroup myGroupDtl = theService.getGroup(myToken, pName);
            Group myGroup = new Group(myGroupDtl);
            theGroups.add(myGroup);
            return myGroup;
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to load group " + pName, e);
        }
    }

    /**
     * Obtain Role.
     * @param pId the id of the Role
     * @return the Role
     * @throws JDataException on error
     */
    public Role getRole(final long pId) throws JDataException {
        /* Return an existing role if found in list */
        Iterator<Role> myIterator = theRoles.iterator();
        while (myIterator.hasNext()) {
            Role myRole = myIterator.next();
            if (myRole.getId() == pId) {
                return myRole;
            }
        }

        /* throw exception */
        throw new JDataException(ExceptionClass.JIRA, "Invalid RoleId " + pId);
    }

    /**
     * Load Roles.
     * @throws JDataException on error
     */
    private void loadRoles() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the authorization token */
            String myToken = theServer.getAuthToken();

            /* Access the roles */
            RemoteProjectRole[] myRoles = theService.getProjectRoles(myToken);

            /* Loop through the roles */
            for (RemoteProjectRole myRole : myRoles) {
                /* Add new role to list */
                theRoles.add(new Role(myRole));
            }
        } catch (RemoteException e) {
            /* Pass the exception on */
            throw new JDataException(ExceptionClass.JIRA, "Failed to load project roles", e);
        }
    }

    /**
     * User class.
     */
    public final class User {
        /**
         * The underlying remote user.
         */
        private final RemoteUser theUser;

        /**
         * The name of the user.
         */
        private final String theName;

        /**
         * The full name of the user.
         */
        private final String theFullName;

        /**
         * Get the underlying user.
         * @return the user
         */
        public RemoteUser getUser() {
            return theUser;
        }

        /**
         * Get the name of the user.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get the full name of the user.
         * @return the full name
         */
        public String getFullName() {
            return theFullName;
        }

        /**
         * Constructor.
         * @param pUser the underlying user
         */
        private User(final RemoteUser pUser) {
            /* Access the details */
            theUser = pUser;
            theName = pUser.getName();
            theFullName = pUser.getFullname();
        }
    }

    /**
     * Group class.
     */
    public final class Group {
        /**
         * The underlying remote group.
         */
        private final RemoteGroup theGroup;

        /**
         * The name of the group.
         */
        private final String theName;

        /**
         * The list of members.
         */
        private final List<User> theMembers;

        /**
         * Get the underlying user.
         * @return the user
         */
        public RemoteGroup getGroup() {
            return theGroup;
        }

        /**
         * Get the name of the group.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Constructor.
         * @param pGroup the underlying group
         * @throws JDataException on error
         */
        private Group(final RemoteGroup pGroup) throws JDataException {
            /* Access the details */
            theGroup = pGroup;
            theName = pGroup.getName();

            /* Allocate the member list */
            theMembers = new ArrayList<User>();

            /* Loop through the members */
            for (RemoteUser myUser : pGroup.getUsers()) {
                /* Access the cached user and add it */
                theMembers.add(getUser(myUser));
            }
        }
    }

    /**
     * Role class.
     */
    public final class Role {
        /**
         * The underlying remote role.
         */
        private final RemoteProjectRole theRole;

        /**
         * The id of the role.
         */
        private final long theId;

        /**
         * The name of the role.
         */
        private final String theName;

        /**
         * The description of the role.
         */
        private final String theDesc;

        /**
         * Get the underlying role.
         * @return the role
         */
        public RemoteProjectRole getRole() {
            return theRole;
        }

        /**
         * Get the id of the role.
         * @return the id
         */
        public long getId() {
            return theId;
        }

        /**
         * Get the name of the role.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get the description of the role.
         * @return the description
         */
        public String getDesc() {
            return theDesc;
        }

        /**
         * Constructor.
         * @param pRole the underlying role
         */
        private Role(final RemoteProjectRole pRole) {
            /* Access the details */
            theRole = pRole;
            theId = pRole.getId();
            theName = pRole.getName();
            theDesc = pRole.getDescription();
        }
    }
}
