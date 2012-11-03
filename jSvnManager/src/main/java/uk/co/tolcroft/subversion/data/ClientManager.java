/*******************************************************************************
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
package uk.co.tolcroft.subversion.data;

import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import uk.co.tolcroft.models.PropertySet.PropertyManager;

public class ClientManager {
	/**
	 * Available pool of connections
	 */
	private List<SVNClientManager>	thePool	= null;
	
	/**
	 * Subversion User name
	 */
	private String					theUser	= null;

	/**
	 * Subversion Password
	 */
	private String					thePass	= null;
	
	/**
	 * Constructor
	 */
	protected ClientManager() {
		/* Allocate the pool */
		thePool = new ArrayList<SVNClientManager>();

		/* Access the SubVersion properties */
		SubVersionProperties myProperties = 
				(SubVersionProperties)PropertyManager.getPropertySet(SubVersionProperties.class);

		/* Access UserId and password */
		theUser = myProperties.getStringValue(SubVersionProperties.nameSubVersionUser);
		thePass = myProperties.getStringValue(SubVersionProperties.nameSubVersionPass);
	}
	
	/**
	 * Allocate new Client Manager instance
	 * @return the instance
	 */
	public SVNClientManager getClientMgr() {
		/* If we have an already allocated client manager in the pool */
		if (thePool.size() > 0) {
			/* Access the most recent item and remove it from the pool */
			SVNClientManager myMgr = thePool.get(thePool.size()-1);
			thePool.remove(myMgr);
			
			/* return it */
			return myMgr;
		}
		
		/* Allocate an entirely new one */
		return allocateClientMgr();
	}

	/**
	 * Return an allocated Client Manager instance to the pool 
	 * @param pMgr the instance to return 
	 */
	public void releaseClientMgr(SVNClientManager pMgr) {
		/* Set event handler to null */
		pMgr.setEventHandler(null);
		
		/* Add it back into the pool */
		thePool.add(pMgr);
	}

	/**
	 * Allocate new Client Manager instance
	 * @return the instance
	 */
	private SVNClientManager allocateClientMgr() {
		/* Access a default client manager */
		SVNClientManager 			myMgr 	= SVNClientManager.newInstance();
		ISVNAuthenticationManager	myAuth 	= SVNWCUtil.createDefaultAuthenticationManager(theUser, thePass);
		myMgr.setAuthenticationManager(myAuth);

		/* Return the new instance */
		return myMgr;
	}
}
