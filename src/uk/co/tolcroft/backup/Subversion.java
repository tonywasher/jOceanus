package uk.co.tolcroft.backup;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.security.SecurityControl;
import uk.co.tolcroft.security.ZipEntryMode;
import uk.co.tolcroft.security.ZipFile;

public class Subversion {
	SVNClientManager	theManager = null;
	SVNAdminClient 		theAdminClient = null;
	SVNLookClient 		theLookClient = null;
	
	/**
	 * Constructor
	 */
	public Subversion() {
		/* Access a default client manager */
		theManager = SVNClientManager.newInstance();

		/* Access Admin and Look clients */
		theAdminClient = theManager.getAdminClient();
		theLookClient  = theManager.getLookClient();
	}
	
	/**
	 * Load a repository from the input stream
	 * @param the repository directory
	 * @param the input stream
	 */
	public void loadRepository(File 		pRepository,
								InputStream	pStream) throws SVNException {
		/* Dump the data to the output stream */
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
								  File				pBackupDir) throws Exception {
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
			
			/* Determine the name of the zip file */
			myZipName 	= new File(pBackupDir.getPath() + myName + "Repo.zip");

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
			
			/* Note success */
			bSuccess = true;
		}
		
		/* Handle standard exceptions */
		catch (Exception e) { throw e; }
		
		/* Handle other exceptions */
		catch (Throwable e) { 
			throw new Exception(ExceptionClass.SUBVERSION,
								"Failed to dump repository to zipfile",
								e);
		}
		
		/* Clean up on exit */
		finally {
			/* Close the stream if open */
			if (myStream != null) try { myStream.close(); } catch (Throwable ex) {}
			
			/* Delete the file on error */
			if ((!bSuccess) && (myZipName != null))
				myZipName.delete();
		}
	}
	
	/**
	 * Backup repositories
	 * @param pControl the security control
	 * @param pRepositories the repository directory
	 * @param pBackupDir the backup directory
	 */
	public void backUpRepositories(SecurityControl	pControl,
								   File 			pRepositories,
								   File				pBackupDir) throws Exception {
		
		/* Loop through the repository directories */
		for (File myRepository: pRepositories.listFiles()) {
			/* Ignore if its is not a directory */
			if (!myRepository.isDirectory()) continue;
			
			/* Backup the repositories */
			backUpRepository(pControl,
							 myRepository,
							 pBackupDir);
		}
	}
}
