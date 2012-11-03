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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.ISVNAdminEventHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEvent;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEventAction;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.security.ZipEntryMode;
import uk.co.tolcroft.models.security.ZipFile;
import uk.co.tolcroft.models.sheets.BackupProperties;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.subversion.data.SubVersionProperties;

public class Backup {
	/**
	 * The Subversion properties
	 */
	private SubVersionProperties		theProperties	= null;

	/**
	 * The Authentication manager
	 */
	private ISVNAuthenticationManager	theAuth 		= null;

	/**
	 * The Client Manager
	 */
	private SVNClientManager			theManager 		= null;

	/**
	 * The Administration Client
	 */
	private SVNAdminClient 				theAdminClient 	= null;
	
	/**
	 * The Thread Status
	 */
	private ThreadStatus<?>				theStatus		= null;
	
	/**
	 * Constructor
	 */
	public Backup(ThreadStatus<?> pStatus) {
		/* Store parameters */
		theStatus = pStatus;
		
		/* Access the SubVersion properties */
		theProperties = (SubVersionProperties)PropertyManager.getPropertySet(SubVersionProperties.class);

		/* Access a default client manager */
		theAuth = SVNWCUtil.createDefaultAuthenticationManager(
					theProperties.getStringValue(SubVersionProperties.nameSubVersionUser),
					theProperties.getStringValue(SubVersionProperties.nameSubVersionPass));
		
		/* Access a default client manager */
		theManager = SVNClientManager.newInstance();
		theManager.setAuthenticationManager(theAuth);

		/* Access Administration and Look clients */
		theAdminClient = theManager.getAdminClient();
	}
	
	/**
	 * Load a repository from the input stream
	 * @param pRepository the repository directory
	 * @param pStream the input stream
	 */
	public void loadRepository(File 		pRepository,
							   InputStream	pStream) throws SVNException {
		/* Re-create the repository */
		theAdminClient.doCreateRepository(pRepository, null, true, true);
		
		/* Read the data from the input stream */
		theAdminClient.doLoad(pRepository, 
							  pStream);

		/* Return to caller */
		return;
	}

	/**
	 * Dump a repository to a Backup directory
	 * @param pControl the security control
	 * @param pRepository the repository directory
	 * @param pBackupDir the backup directory
	 */
	private void backUpRepository(SecurityControl	pControl,
								  File 				pRepository,
								  File				pBackupDir) throws ModelException {
		ZipFile.Output	myZipFile	= null;
		OutputStream 	myStream 	= null;
		boolean			bSuccess	= true;
		String			myName;
		File			myEntryName;
		File			myZipName	= null;
		
		/* Protect against exceptions */
		try {
			/* Access the name of the repository */
			myName 		= pRepository.getName();
			myEntryName	= new File(ZipFile.fileData);
			
			/* Determine the prefix for backups */
			String myPrefix = theProperties.getStringValue(SubVersionProperties.nameRepoPfix);
			
			/* Determine the repository name */
			String myRepoName	= theProperties.getStringValue(SubVersionProperties.nameSubVersionRepo) + "/" + myName;
			SVNURL myURL		= SVNURL.parseURIDecoded(myRepoName);
			
			/* Access the repository */
			SVNRepository myRepo = SVNRepositoryFactory.create(myURL);
			myRepo.setAuthenticationManager(theAuth);

			/* Determine the most recent revision # in the repository */
		    long revLast = myRepo.getDatedRevision(new Date());
		    
			/* Determine the name of the zip file */
			myZipName 	= new File(pBackupDir.getPath(), myPrefix + myName + ".zip");

			/* If the backup file exists */
			if (myZipName.exists()) {
				/* Access the last modified time of the backup */
			    Date myDate = new Date();
			    myDate.setTime(myZipName.lastModified());
			    
			    /* Access the revision for the zip file */
			    long revZip = myRepo.getDatedRevision(myDate);
			    
			    /* If the Backup date is later than the repository date */
			    if (revZip >= revLast) {
			    	/* No need to backup the repository so just return */
			    	return;
			    }
			}
			
			/* Determine the number of revisions */
			int myNumRevisions = (int)revLast;
			
			/* Declare the number of revisions */
			if (!theStatus.setNumSteps(myNumRevisions)) return;
			
			/* Note presumption of failure */
			bSuccess = false;
			
			/* Create a clone of the security control */
			SecurityControl myControl	= new SecurityControl(pControl);
			
			/* Create the new zip file */
			myZipFile 	= new ZipFile.Output(myControl,
					 						 myZipName);

			/* Create the output stream to the zip file */
			myStream = myZipFile.getOutputStream(myEntryName, 
												 ZipEntryMode.getRandomTrioMode(myControl.getRandom()));

			/* Dump the data to the zip file */
			theAdminClient.doDump(pRepository, 
							  	  myStream, 
							  	  SVNRevision.UNDEFINED, 
							  	  SVNRevision.create(revLast), 
							  	  false, 
							  	  true);
			
			/* Close the stream */
			myStream.close();
			myStream = null;
						
			/* Close the Zip file */
			myZipFile.close();
			myZipFile = null;
			
			/* Note success */
			bSuccess = true;
		}
		
		/* Handle standard exceptions */
		catch (ModelException e) { throw e; }
		
		/* Handle other exceptions */
		catch (Throwable e) { 
			throw new ModelException(ExceptionClass.SUBVERSION,
								"Failed to dump repository to zipfile",
								e);
		}
		
		/* Clean up on exit */
		finally {
			/* Protect while cleaning up */
			try { 
				/* Close the output stream */
				if (myStream != null) myStream.close();

				/* Close the Zip file */
				if (myZipFile != null) myZipFile.close();
			} 
			
			/* Ignore errors */
			catch (Throwable ex) {}
			
			/* Delete the file on error */
			if ((!bSuccess) && (myZipName != null))
				myZipName.delete();
		}
	}
	
	/**
	 * Backup repositories
	 * @param pControl the security control
	 */
	public void backUpRepositories(SecurityControl	pControl) throws ModelException {
		int iNumStages = 0;
		
		/* Install an event handler */
		theAdminClient.setEventHandler(new SubversionHandler());

		/* Access the BackUp properties */
		BackupProperties 		myBUProperties 	= 
				(BackupProperties)PropertyManager.getPropertySet(BackupProperties.class);

		/* Determine the repository and backup directories directory */
		File myRepo 	= new File(theProperties.getStringValue(SubVersionProperties.nameSubVersionDir));
		File myBackup 	= new File(myBUProperties.getStringValue(BackupProperties.nameBackupDir));
		
		/* Loop through the repository directories */
		for (File myRepository: myRepo.listFiles()) {
			/* Count if its is a directory */
			if (myRepository.isDirectory()) iNumStages++;
		}

		/* Declare the number of stages */
		boolean bContinue = theStatus.setNumStages(iNumStages);

		/* Ignore if cancelled */
		if (!bContinue) return;
		
		/* Loop through the repository directories */
		for (File myRepository: myRepo.listFiles()) {
			/* Ignore if its is not a directory */
			if (!myRepository.isDirectory()) continue;
			
			/* Set new stage and break if cancelled */
			if (!theStatus.setNewStage(myRepository.getName())) break;
			
			/* Backup the repositories */
			backUpRepository(pControl,
							 myRepository,
							 myBackup);
		}
	}
	
	/**
	 * Event Handler class
	 */
	private class SubversionHandler implements ISVNAdminEventHandler {

		@Override
		public void checkCancelled() throws SVNCancelException {
			if (theStatus.isCancelled())
				throw new SVNCancelException();
		}

		@Override
		public void handleAdminEvent(SVNAdminEvent pEvent, double arg1) throws SVNException {
			/* Ignore if not an interesting event */
			if (pEvent.getAction() != SVNAdminEventAction.REVISION_DUMPED) return;
			
			/* Set steps done value */
			theStatus.setStepsDone((int)pEvent.getRevision());
		}

		@Override
		public void handleEvent(SVNEvent arg0, double arg1) throws SVNException {
		}
		
	}
}
