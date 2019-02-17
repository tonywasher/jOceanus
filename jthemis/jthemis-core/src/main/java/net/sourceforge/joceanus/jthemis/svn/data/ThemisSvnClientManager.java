/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;

/**
 * Manages connections to the server.
 * @author Tony Washer
 */
public class ThemisSvnClientManager {
    /**
     * Available pool of connections.
     */
    private final List<SVNClientManager> thePool;

    /**
     * Authentication manager.
     */
    private final ISVNAuthenticationManager theAuth;

    /**
     * Null Event Handler.
     */
    private final NullEventHandler theHandler = new NullEventHandler();

    /**
     * Constructor.
     * @param pPreferences the preferences
     */
    public ThemisSvnClientManager(final ThemisSvnPreferences pPreferences) {
        /* Allocate the pool */
        thePool = new ArrayList<>();

        /* Access UserId and password */
        final String myUser = pPreferences.getStringValue(ThemisSvnPreferenceKey.USER);
        final char[] myPass = pPreferences.getCharArrayValue(ThemisSvnPreferenceKey.PASS);
        theAuth = SVNWCUtil.createDefaultAuthenticationManager(myUser, myPass);
    }

    /**
     * Allocate new Client Manager instance.
     * @return the instance
     */
    public SVNClientManager getClientMgr() {
        /* If we have an already allocated client manager in the pool */
        if (!thePool.isEmpty()) {
            /* Access the most recent item and remove it from the pool */
            final SVNClientManager myMgr = thePool.get(thePool.size() - 1);
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
        /* Set event handler to default */
        pMgr.setEventHandler(theHandler);

        /* Add it back into the pool */
        thePool.add(pMgr);
    }

    /**
     * Allocate new Client Manager instance.
     * @return the instance
     */
    private SVNClientManager allocateClientMgr() {
        /* Access a default client manager */
        final SVNClientManager myMgr = SVNClientManager.newInstance();
        myMgr.setAuthenticationManager(theAuth);

        /* Initialise handler */
        myMgr.setEventHandler(theHandler);

        /* Return the new instance */
        return myMgr;
    }

    /**
     * Dispose of all Client Manager instances.
     */
    public void dispose() {
        /* Allocate an iterator */
        final Iterator<SVNClientManager> myIterator = thePool.iterator();

        /* Loop through the list */
        while (myIterator.hasNext()) {
            final SVNClientManager myMgr = myIterator.next();

            /* Dispose of the manager */
            myMgr.dispose();
            myIterator.remove();
        }
    }

    /**
     * EventHandler.
     */
    private static final class NullEventHandler
            implements ISVNEventHandler {

        @Override
        public void checkCancelled() throws SVNCancelException {
            /* Note needed */
        }

        @Override
        public void handleEvent(final SVNEvent pEvent,
                                final double pProgress) throws SVNException {
            /* Note needed */
        }
    }
}
