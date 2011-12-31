package uk.co.tolcroft.backup;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.admin.ISVNAdminEventHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEvent;
import org.tmatesoft.svn.core.wc.admin.SVNAdminEventAction;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.security.ZipEntryMode;
import uk.co.tolcroft.models.security.ZipFile;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class Subversion {
	/**
	 * The Client Manager
	 */
	private SVNClientManager	theManager 		= null;

	/**
	 * The Admin Client
	 */
	private SVNAdminClient 		theAdminClient 	= null;
	
	/**
	 * The Look Client
	 */
	private SVNLookClient 		theLookClient	= null;
	
	/**
	 * The Thread Status
	 */
	private ThreadStatus<?>		theStatus		= null;
	
	/**
	 * Constructor
	 */
	public Subversion(ThreadStatus<?> pStatus) {
		/* Store parameters */
		theStatus = pStatus;
		
		/* Access a default client manager */
		theManager = SVNClientManager.newInstance();

		/* Access Admin and Look clients */
		theAdminClient = theManager.getAdminClient();
		theLookClient  = theManager.getLookClient();
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
			
			/* Access the Backup properties */
			BackupProperties myProperties = (BackupProperties)PropertyManager.getPropertySet(BackupProperties.class);

			/* Determine the prefix for backups */
			String myPrefix = myProperties.getStringValue(BackupProperties.nameRepoPfix);
			
			/* Determine the name of the zip file */
			myZipName 	= new File(pBackupDir.getPath() + File.separator + myPrefix + myName + ".zip");

			/* If the backup file exists */
			if (myZipName.exists()) {
				/* Access the last modified time of the backup */
			    Date myDate = new Date();
			    myDate.setTime(myZipName.lastModified());
			    
			    /* Access the last modified time of the repository */
			    Date myRepDate = theLookClient.doGetDate(pRepository, SVNRevision.HEAD);
			    
			    /* If the Backup date is later than the repository date */
			    if (myDate.compareTo(myRepDate) > 0) {
			    	/* No need to backup the repository so just return */
			    	return;
			    }
			}
			
			/* Determine the number of revisions */
			int myNumRevisions = (int)theLookClient.doGetYoungestRevision(pRepository);
			
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
							  	  SVNRevision.UNDEFINED, 
							  	  false, 
							  	  false);
			
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

		/* Access the Backup properties */
		BackupProperties myProperties = (BackupProperties)PropertyManager.getPropertySet(BackupProperties.class);

		/* Determine the repository and backup directories directory */
		File myRepo 	= new File(myProperties.getStringValue(BackupProperties.nameSubVersionRepo));
		File myBackup 	= new File(myProperties.getStringValue(BackupProperties.nameBackupDir));
		
		/* Loop through the repository directories */
		for (File myRepository: myRepo.listFiles()) {
			/* Count if its is a directory */
			if (!myRepository.isDirectory()) iNumStages++;
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
