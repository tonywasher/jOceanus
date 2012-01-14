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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import uk.co.tolcroft.models.ModelException;

public class WorkingCopy {
	/**
	 * The branch associated with the working copy
	 */
	private Branch 	theBranch	= null;

	/**
	 * The sub-path of the branch that is checked out 
	 */
	private String	theSubPath	= null;
	
	/**
	 * The path at which the branch is checked out
	 */
	private File 	theLocation	= null;
	
	/**
	 * Get branch
	 * @return the branch
	 */
	public Branch 	getBranch() 	{ return theBranch; }
	
	/**
	 * Get subPath
	 * @return the subPath
	 */
	public String 	getSubPath() 	{ return theSubPath; }
	
	/**
	 * Get Location
	 * @return the location
	 */
	public File 	getLocation() 	{ return theLocation; }
	
	/**
	 * Constructor
	 * @param pLocation the location
	 * @param pBranch the branch
	 * @param pSource the source URL
	 */
	protected WorkingCopy(File 		pLocation,
						  Branch	pBranch,
						  SVNURL	pSource) {
		/* Store parameters */
		theBranch 	= pBranch;
		theLocation	= pLocation;

		/* Access branch path and source path */
		String myPath = theBranch.getPath();
		String myURL  = pSource.toDecodedString();
		
		/* Access the SubPath */
		theSubPath = myURL.substring(myPath.length());
	}
	
	/**
	 * Working Copy Set
	 */
	public static class WorkingCopySet {
		/**
		 * The repository for which these are working sets
		 */
		private Repository 			theRepository	= null;
		
		/**
		 * The base location for the working sets
		 */
		private File				theLocation		= null;
		
		/**
		 * The list of WorkingCopys 
		 */
		private List<WorkingCopy>	theList			= null;
		
		/**
		 * The status client
		 */
		private SVNStatusClient 	theClient 		= null;

		/**
		 * Constructor
		 * @param pRepository the repository
		 * @param pLocation the location
		 */
		public WorkingCopySet(Repository pRepository,
							  String	 pLocation) throws ModelException {
			/* Store parameters */
			theRepository 	= pRepository;
			theLocation   	= new File(pLocation);

			/* Access the status client */
			theClient 		= theRepository.getClientManager().getStatusClient();

			/* Allocate the list */
			theList 		= new ArrayList<WorkingCopy>();
			
			/* Locate working directories */
			locateWorkingDirectories(theLocation);
		}
		
		/**
		 * Locate working copies
		 * @param pLocation location
		 */
		private void locateWorkingDirectories(File pLocation) throws ModelException {
			/* Return if file is not a directory */
			if (!pLocation.isDirectory()) return;
			
			/* Access underlying files */
			for (File myFile: pLocation.listFiles()) {
				/* Ignore if file is not a directory */
				if (!myFile.isDirectory()) continue;
				
				/* Ignore if file is special directory */
				if (myFile.getName().startsWith(".")) continue;
				
				/* Initialise the status */
				SVNStatus myStatus = null;
				
				/* Access status (Alternate Method) */
				try { myStatus = theClient.doStatus(myFile, false); }
				catch (SVNException e) {
					myStatus = null;
				}	
				
				/* If this is a working copy */
				if (myStatus != null) {
					/* Obtain the repository URL */
					SVNURL myURL = myStatus.getRemoteURL();
					
					/* Obtain the branch in the repository */
					Branch myBranch = theRepository.locateBranch(myURL);
					
					/* If we found a branch */
					if (myBranch != null) {
						/* Create the working copy */
						WorkingCopy myCopy = new WorkingCopy(myFile, myBranch, myURL);
					
						/* Add to the list */
						theList.add(myCopy);
					}
				}
				
				/* else try under this directory */
				else locateWorkingDirectories(myFile);
			}
		}
		
		/**
		 * Obtain locations array
		 * @return locations array
		 */
		private File[] getLocationsArray() {
			/* Allocate array */
			File[] 	myFiles = new File[theList.size()];
			int		myFile  = 0;
			
			/* Allocate the iterator */
			ListIterator<WorkingCopy> myIterator = theList.listIterator();
			
			/* While there are entries */
			while (myIterator.hasNext()) {
				/* Access copy and add to files */
				WorkingCopy myCopy = myIterator.next();
				myFiles[myFile++]  = myCopy.getLocation();
			}
			
			/* Return the array */
			return myFiles;
		}
		
		/**
		 * Revert changes across working set
		 */
		public void revertChanges() throws SVNException {
			/* Access the array of locations */
			File[] myLocations = getLocationsArray();
			
			/* Access WorkingCopy client */
			SVNWCClient myClient = theRepository.getClientManager().getWCClient();
			
			/* Revert changes */
			myClient.doRevert(myLocations, SVNDepth.INFINITY, null);
		}
		
		/**
		 * Access updates across working set
		 */
		public void updateWorkingSets() throws SVNException {
			/* Access the array of locations */
			File[] myLocations = getLocationsArray();
			
			/* Access Update client */
			SVNUpdateClient myClient = theRepository.getClientManager().getUpdateClient();
			
			/* Refresh changes */
			myClient.doUpdate(myLocations, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
		}
	}
}
