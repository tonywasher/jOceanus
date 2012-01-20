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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.subversion.data.Component.ComponentList;
import uk.co.tolcroft.subversion.data.WorkingCopy.WorkingCopySet;

/**
 * Represents a repository
 * @author Tony Washer
 */
public class Repository {
	/**
	 * URL separator character
	 */
	public static final char		theURLSep	= '/';
	
	/**
	 * Repository Name
	 */
	private final String			theName;
	
	/**
	 * Repository Base
	 */
	private final String			theBase;
	
	/**
	 * The Client Manager
	 */
	private final ClientManager		theClientMgrPool;
	
	/**
	 * ComponentList 
	 */
	private final ComponentList		theComponents;
	
	/**
	 * WorkingCopySet 
	 */
	private final WorkingCopySet	theWorkingSet;
	
	/**
	 * Obtain the repository name
	 * @return the name
	 */
	public String			getName() 			{ return theName; }
	
	/**
	 * Obtain a client manager
	 * @return the manager
	 */
	public SVNClientManager	getClientManager() 	{ return theClientMgrPool.getClientMgr(); }
	
	/**
	 * Release a client manager
	 * @param pMgr the manager
	 */
	public void	releaseClientManager(SVNClientManager pMgr) 	{ theClientMgrPool.releaseClientMgr(pMgr); }
	
	/**
	 * Get the component list for this repository
	 * @return the component list
	 */
	public ComponentList 	getComponentList()	{ return theComponents; }
	
	/**
	 * Get the WorkingSet list for this repository
	 * @return the component list
	 */
	public WorkingCopySet 	getWorkingSet()		{ return theWorkingSet; }
	
	/**
	 * Constructor
	 * @param pName the Name of the repository
	 */
	public Repository(String pName) throws ModelException {
		/* Store the name */
		theName = pName;

		/* Access the SubVersion properties */
		SubVersionProperties myProperties = 
				(SubVersionProperties)PropertyManager.getPropertySet(SubVersionProperties.class);

		/* Access the Repository base */
		theBase = myProperties.getStringValue(SubVersionProperties.nameSubVersionRepo);

		/* Create a client manager pool */
		theClientMgrPool = new ClientManager();

		/* Create component list */
		theComponents = new ComponentList(this);

		/* Discover components */
		theComponents.discover();
		
		/* Build the WorkingCopySet */
		theWorkingSet = new WorkingCopySet(this, 
				myProperties.getStringValue(SubVersionProperties.nameSubVersionWork));
	}

	/**
	 * Build URL 
	 * @return the Repository path
	 */
	public String getPath() {
		/* Build the underlying string */
		StringBuilder myBuilder = new StringBuilder(100);
		
		/* Build the repository */
		myBuilder.append(theBase);
		
		/* Build the component directory */
		myBuilder.append(theURLSep);
		myBuilder.append(getName());

		/* Return the path */
		return myBuilder.toString();
	}	

	/**
	 * Compare this repository to another repository 
	 * @param pThat the other repository
	 * @return -1 if earlier repository, 0 if equal repository, 1 if later repository
	 */
	public int compareTo(Repository pThat) {
		/* Handle trivial cases */
        if (this == pThat) return 0;
        if (pThat == null) return -1;
        
        /* Compare bases */
        int iResult = theBase.compareTo(pThat.theBase);
        if (iResult != 0) return iResult;

        /* Compare names */
        return theName.compareTo(pThat.theName);
 	}
	
	/**
	 * Locate branch
	 * @param pURL the URL to locate
	 * @return the relevant branch or Null 
	 */ 
	protected Branch locateBranch(SVNURL pURL) {
		/* Locate branch in component list */
		return theComponents.locateBranch(pURL);
	}
	
	/**
	 * Get FileURL as input stream
	 * @param pURL the URL to stream
	 * @return the stream of null if file does not exists
	 */
	public InputStream getFileURLasInputStream(SVNURL pURL) throws ModelException {
		/* Access client */
		SVNClientManager		myMgr		= getClientManager();
		SVNWCClient 			myClient 	= myMgr.getWCClient();
		ByteArrayInputStream	myStream	= null;
		
		/* Create the byte array stream */
		ByteArrayOutputStream myBaos = new ByteArrayOutputStream(1000); 

		/* Protect against exceptions */
		try {
			/* Read the entry into the outputStream and create an input stream from it */
			myClient.doGetFileContents(pURL, SVNRevision.HEAD, SVNRevision.HEAD, true, myBaos);
			myStream = new ByteArrayInputStream(myBaos.toByteArray());
		}
		catch (SVNException e) {
			/* Access the error code */
			SVNErrorCode myCode = e.getErrorMessage().getErrorCode();
			
			/* Allow file/directory exists but is not WC */
			if ((myCode != SVNErrorCode.WC_NOT_FILE) && 
				(myCode != SVNErrorCode.WC_NOT_DIRECTORY))
				throw new ModelException(ExceptionClass.SUBVERSION, 
										 "Unable to read File URL",
										 e);
								
			/* Set stream to null */
			myStream = null;
		}
		
		/* Release the client manager */
		releaseClientManager(myMgr);
		
		/* Return the stream */
		return myStream;
	}
}
