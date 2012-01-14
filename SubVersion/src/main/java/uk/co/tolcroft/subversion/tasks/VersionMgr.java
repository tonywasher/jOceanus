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
package uk.co.tolcroft.subversion.tasks;

import java.io.File;

//import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
//import org.tmatesoft.svn.core.wc.SVNCommitClient;
//import org.tmatesoft.svn.core.wc.SVNCommitPacket;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAdapter;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.admin.ISVNAdminEventHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEvent;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEventAction;

import uk.co.tolcroft.jira.data.Issue;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.subversion.data.Branch;
import uk.co.tolcroft.subversion.data.Branch.BranchList;
import uk.co.tolcroft.subversion.data.Component;
import uk.co.tolcroft.subversion.data.Repository;
import uk.co.tolcroft.subversion.data.SubVersionProperties;
import uk.co.tolcroft.subversion.data.Tag;
import uk.co.tolcroft.subversion.data.Tag.TagList;

public class VersionMgr {
	/**
	 * Subversion Work Directory
	 */
	private static final String theSvnWorkDir = "svnTempWork";
	
	/**
	 * The Client Manager
	 */
	private final SVNClientManager	theMgr;
	
	/**
	 * Repository
	 */
	private final Repository theRepository;
	
	/**
	 * Constructor
	 * @param pRepository
	 */
	public VersionMgr(Repository pRepository) {
		/* Store Parameters */
		theRepository 	= pRepository;
		theMgr 			= theRepository.getClientManager();
	}

	/**
	 * Create major branch
	 * @param pSource the tag to base the branch on
	 * @param pIssue the issue to make changes against
	 */
	protected Branch createMajorBranch(Tag 		pSource,
									   Issue 	pIssue) throws ModelException {
		/* Obtain the component that we are creating the branch in */
		Component 	myComponent = pSource.getComponent();
		BranchList 	myList		= myComponent.getBranchList();

		/* Determine the branch to create */
		Branch myBranch = myList.nextMajorBranch();
		
		/* Create the branch  */
		createBranch(myBranch, pSource, pIssue);
		
		/* re-discover branches */
		myList.discover();
		
		/* Return the newly created branch */
		return myList.locateBranch(myBranch);
	}

	/**
	 * Create minor branch
	 * @param pSource the tag to base the branch on
	 * @param pIssue the issue to make changes against
	 */
	protected Branch createMinorBranch(Tag		pSource,
			   						   Issue 	pIssue) throws ModelException {
		/* Obtain the component that we are creating the branch in */
		Component 	myComponent = pSource.getComponent();
		BranchList 	myList		= myComponent.getBranchList();

		/* Determine the branch to create */
		Branch myBranch = myList.nextMinorBranch(pSource.getBranch());
		
		/* Create the branch  */
		createBranch(myBranch, pSource, pIssue);
		
		/* re-discover branches */
		myList.discover();
		
		/* Return the newly created branch */
		return myList.locateBranch(myBranch);
	}

	/**
	 * Create revision branch
	 * @param pSource the tag to base the branch on
	 * @param pIssue the issue to make changes against
	 */
	protected Branch createRevisionBranch(Tag 	pSource,
			   							  Issue pIssue) throws ModelException {
		/* Obtain the component that we are creating the branch in */
		Component 	myComponent = pSource.getComponent();
		BranchList 	myList		= myComponent.getBranchList();

		/* Determine the branch to create */
		Branch myBranch = myList.nextRevisionBranch(pSource.getBranch());
		
		/* Create the branch  */
		createBranch(myBranch, pSource, pIssue);
		
		/* re-discover branches */
		myList.discover();
		
		/* Return the newly created branch */
		return myList.locateBranch(myBranch);
	}

	/**
	 * Create next tag for branch
	 * @param pSource the branch to tag
	 * @param pIssue the issue to make changes against
	 */
	protected Tag createNextTag(Branch 	pSource,
			   					Issue 	pIssue) throws ModelException {
		/* Access tag list */
		TagList myList	= pSource.getTagList();
		
		/* Determine the tag to create */
		Tag myTag = myList.nextTag();
		
		/* Create the branch  */
		createTag(myTag, pSource, pIssue);
		
		/* re-discover tags */
		myList.discover();
		
		/* Return the newly created tag */
		return myList.locateTag(myTag);
	}
	
	/**
	 * Count objects in URL
	 */
	private Integer countObjects(SVNURL pURL) {
		/* Access a LogClient */
		SVNLogClient 	myClient = theMgr.getLogClient();
		Integer			myCount	 = new Integer(0);
	
		/* Protect against exceptions */
		try { 
			/* List the objects recursively */
			myClient.doList(pURL, 
							SVNRevision.HEAD, 
							SVNRevision.HEAD, 
							false, 
							SVNDepth.INFINITY, 
							SVNDirEntry.DIRENT_ALL,
							new CountHandler(myCount));		

			/* Return the count */
			return myCount;
		}
		catch (SVNException e) { return null;}
	}
	
	/**
	 * The Count Handler
	 */
	private class CountHandler implements ISVNDirEntryHandler {
		/**
		 * The object count 
		 */
		private Integer theCount;
		
		/**
		 * Constructor
		 * @param pCount the count integer
		 */
		private CountHandler(Integer pCount) { theCount = pCount; }
		
		@Override
		public void handleDirEntry(SVNDirEntry pEntry) throws SVNException {
			/* Ignore if is top-level */
			if (pEntry.getRelativePath().length() == 0) return;
		
			/* Increment the count  */
			theCount++;
		}
	}
	
	/**
	 * Create branch in temporary working copy
	 * @param pTarget the branch to create
	 * @param pSource the tag to base the branch on
	 * @param pIssue the issue to make changes against
	 */
	private void createBranch(Branch	pTarget,
							  Tag 		pSource,
   							  Issue 	pIssue) throws ModelException {
		/* Access details */
		Component			myComp		= pTarget.getComponent();
		
		/* Access clients */
		SVNUpdateClient 	myUpdate 	= theMgr.getUpdateClient();
		SVNCopyClient 		myCopy 		= theMgr.getCopyClient();
		//SVNCommitClient 	myCommit	= theMgr.getCommitClient();
		
		/* Protect against exceptions */
		try { 
			/* Access the work and target */
			File myWork 	= prepareWorkDir();
			File myTarget 	= new File(myWork.getAbsolutePath() + File.separator + pTarget.getBranchName());
		
			/* Determine the URL for the branches path */
			String	myBase	 = myComp.getBranchesPath();
			SVNURL 	myURL	 = SVNURL.parseURIDecoded(myBase);
		
			/* Check out the path */
			myUpdate.doCheckout(myURL, 
								myWork, 
								SVNRevision.HEAD, 
								SVNRevision.HEAD, 
								SVNDepth.EMPTY, 
								false);
		
			/* Determine the source to copy from */
			SVNURL 			mySrcURL = pSource.getURL();
			SVNCopySource 	mySource = new SVNCopySource(SVNRevision.HEAD,
												     	 SVNRevision.HEAD,
												     	 mySrcURL);
			SVNCopySource[] mySrcs 	 = new SVNCopySource[] { mySource };
		
			/* Count the objects that we are to copy */
			Integer myCount = countObjects(mySrcURL);
			
			/* Copy the source tag to the new branch */
			myCopy.doCopy(mySrcs, 
					  	  myTarget, 
					  	  false, 
					  	  false, 
					  	  true);
		
			/* Update pom.xml */
		
			/* Commit the changes */
			//SVNCommitPacket myPacket = myCommit.doCollectCommitItems(new File[] { myWork }, false, false, SVNDepth.INFINITY, null);
			//SVNCommitInfo 	myResult = myCommit.doCommit(myPacket, false, "Created branch " + pTarget.getBranchName() +
			//															  " from tag " + pSource.getTagName());
		
			/* Remove work directory */
			//Utils.removeDirectory(myWork);
		}
		
		catch (SVNException e) {
			throw new ModelException(ExceptionClass.SUBVERSION,
									 "Failed to create branch " + pTarget.getBranchName(),
									 e);
		}
	}

	/**
	 * Create tag in temporary working copy
	 * @param pTarget the tag to create
	 * @param pSource the branch to create the tag for
	 * @param pIssue the issue to make changes against
	 */
	private void createTag(Tag		pTarget,
						   Branch	pSource,
						   Issue	pIssue) throws ModelException {
		/* Access details */
		Component			myComp		= pTarget.getComponent();

		/* Access clients */
		SVNUpdateClient 	myUpdate 	= theMgr.getUpdateClient();
		SVNCopyClient 		myCopy 		= theMgr.getCopyClient();
		//SVNCommitClient 	myCommit	= theMgr.getCommitClient();
		
		/* Protect against exceptions */
		try { 
			/* Access the work and target */
			File myWork 	= prepareWorkDir();
			File myTarget 	= new File(myWork.getAbsolutePath() + File.separator + pTarget.getTagName());
		
			/* Determine the URL for the tags path */
			String	myBase	 = myComp.getTagsPath();
			SVNURL 	myURL	 = SVNURL.parseURIDecoded(myBase);
		
			/* Check out the path */
			myUpdate.doCheckout(myURL, 
								myWork, 
								SVNRevision.HEAD, 
								SVNRevision.HEAD, 
								SVNDepth.EMPTY, 
								false);	

			/* Determine the source to copy from */
			SVNURL 			mySrcURL = pSource.getURL();
			SVNCopySource 	mySource = new SVNCopySource(SVNRevision.HEAD,
												     	 SVNRevision.HEAD,
												     	 mySrcURL);
			SVNCopySource[] mySrcs 	 = new SVNCopySource[] { mySource };
				
			/* Count the objects that we are to copy */
			Integer myCount = countObjects(mySrcURL);
			myCopy.setEventHandler(new CopyHandler());
			
			/* Copy the source branch to the new tag */
			myCopy.doCopy(mySrcs, 
					  	  myTarget, 
					  	  false, 
					  	  false, 
					  	  true);
		
			/* Update pom.xml */
		
			/* Commit the changes */
			//SVNCommitPacket myPacket = myCommit.doCollectCommitItems(new File[] { myWork }, false, false, SVNDepth.INFINITY, null);
			//SVNCommitInfo 	myResult = myCommit.doCommit(myPacket, false, "Created tag " + pTarget.getTagName());
		
			/* Remove work directory */
			//Utils.removeDirectory(myWork);
		}
		
		catch (SVNException e) {
			throw new ModelException(ExceptionClass.SUBVERSION,
									 "Failed to create tag " + pTarget.getTagName(),
									 e);
		}
	}
	
	/**
	 * Prepare temporary work directory
	 * @return the work directory
	 */
	private File prepareWorkDir() {
		/* Access the SubVersion properties */
		SubVersionProperties myProperties = 
				(SubVersionProperties)PropertyManager.getPropertySet(SubVersionProperties.class);

		/* Determine the name of the work directory */
		String 	myBase 	= myProperties.getStringValue(SubVersionProperties.nameSubVersionBuild);
		File	myWork	= new File(myBase + File.separator + theSvnWorkDir);

		/* Remove the directory */
		if (!Utils.removeDirectory(myWork)) return null;

		/* Create the directory */
		if (!myWork.mkdir()) return null;
		
		/* Return the directory */
		return myWork;
	}

	/**
	 * Copy Event Handler class
	 */
	private class CopyHandler extends SVNEventAdapter {

		@Override
		public void checkCancelled() throws SVNCancelException {
			//if (theStatus.isCancelled())
				//throw new SVNCancelException();
		}

		@Override
		public void handleEvent(SVNEvent arg0, double arg1) throws SVNException {
		}		
	}
}
