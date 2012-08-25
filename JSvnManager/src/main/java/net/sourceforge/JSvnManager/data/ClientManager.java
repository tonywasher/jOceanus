/*******************************************************************************
 * Subversion: Java SubVersion Management
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
package net.sourceforge.JSvnManager.data;

import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Manages connections to the server.
 * @author Tony Washer
 */
public class ClientManager {
    /**
     * Available pool of connections.
     */
    private final List<SVNClientManager> thePool;

    /**
     * Subversion User name.
     */
    private final String theUser;

    /**
     * Subversion Password.
     */
    private final String thePass;

    /**
     * Constructor.
     * @param pPreferences the preferences
     */
    public ClientManager(final SubVersionPreferences pPreferences) {
        /* Allocate the pool */
        thePool = new ArrayList<SVNClientManager>();

        /* Access UserId and password */
        theUser = pPreferences.getStringValue(SubVersionPreferences.NAME_SVN_USER);
        thePass = pPreferences.getStringValue(SubVersionPreferences.NAME_SVN_PASS);
    }

    /**
     * Allocate new Client Manager instance.
     * @return the instance
     */
    public SVNClientManager getClientMgr() {
        /* If we have an already allocated client manager in the pool */
        if (thePool.size() > 0) {
            /* Access the most recent item and remove it from the pool */
            SVNClientManager myMgr = thePool.get(thePool.size() - 1);
            thePool.remove(myMgr);

            /* return it */
            return myMgr;
        }

        /* Allocate an entirely new one */
        return allocateClientMgr();
    }

    /**
     * Return an allocated Client Manager instance to the pool.
     * @param pMgr the instance to return
     */
    public void releaseClientMgr(final SVNClientManager pMgr) {
        /* Set event handler to null */
        pMgr.setEventHandler(null);

        /* Add it back into the pool */
        thePool.add(pMgr);
    }

    /**
     * Allocate new Client Manager instance.
     * @return the instance
     */
    private SVNClientManager allocateClientMgr() {
        /* Access a default client manager */
        SVNClientManager myMgr = SVNClientManager.newInstance();
        ISVNAuthenticationManager myAuth = SVNWCUtil.createDefaultAuthenticationManager(theUser, thePass);
        myMgr.setAuthenticationManager(myAuth);

        /* Return the new instance */
        return myMgr;
    }
}
