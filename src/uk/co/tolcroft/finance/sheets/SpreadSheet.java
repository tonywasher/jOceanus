package uk.co.tolcroft.finance.sheets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.security.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SpreadSheet {
	/**
	 *  Load an Archive Workbook
	 *  @param pThread Thread Control for task
	 *  @param pFile the archive file to load from
	 *  @return the newly loaded data
	 */
	public static DataSet loadArchive(statusCtl pThread,
	  		 				   		  File 	 	pFile) throws Exception {
		InputStream 		myStream  	= null;
		FileInputStream     myInFile  	= null;
		DataSet				myData;
		
		/* Protect the workbook retrieval */
		try {
			/* Create an input stream to the file */
			myInFile = new FileInputStream(pFile);
			myStream = new BufferedInputStream(myInFile);
								
			/* Load the data from the stream */
			myData = loadArchiveStream(pThread, myStream);
			
			/* Close the Stream to force out errors */
			myStream.close();
		}

		catch (Throwable e) {
			/* Report the error */
			throw new Exception(ExceptionClass.EXCEL, 
					  			"Failed to load Workbook: " + pFile.getName(),
					  			e);				
		}
		
		/* Return the new DataSet */
		return myData;
	}
		
	/**
	 *  Load a Backup Workbook
	 *  @param pThread Thread Control for task
	 *  @param pFile the backup file to load from
	 *  @return the newly loaded data
	 */
	public static DataSet loadBackup(statusCtl 	pThread,
	  		 				  		 File 		pFile) throws Exception {
		InputStream 		myStream  	= null;
		FileInputStream     myInFile  	= null;
		ZipFile.Input 		myFile		= null;
		DataSet				myData;
		View				myView;
		boolean				isEncrypted = false;
		String				mySecurityKey;
		SecurityControl		myControl;
		SecureManager		mySecurity;
		
		/* Protect the workbook retrieval */
		try {
			/* The backup is encrypted if the filename ends in .zip */
			if (pFile.getName().endsWith(".zip")) isEncrypted = true;
			
			/* If we are not encrypted */
			if (!isEncrypted) {
				/* Create an input stream to the file */
				myInFile = new FileInputStream(pFile);
				myStream = new BufferedInputStream(myInFile);
			}
								
			/* else we are encrypted */
			else {
				/* Access the zip file */
				myFile = new ZipFile.Input(pFile);
				
				/* Obtain the security key from the file */
				mySecurityKey = myFile.getSecurityKey();

				/* Access the Security manager */
				myView 		= pThread.getView();
				mySecurity 	= myView.getSecurity();
				
				/* Obtain the initialised security control */
				myControl = mySecurity.getSecurityControl(mySecurityKey, pFile.getName());
				
				/* Associate this control with the Zip file */
				myFile.setSecurityControl(myControl);
				
				/* Access the input stream for the first file */
				myStream = myFile.getInputStream(myFile.getFiles());
			}
				
			/* Load the data from the stream */
			myData = loadBackupStream(pThread, myStream);
			
			/* Close the Stream to force out errors */
			myStream.close();
		}

		catch (Throwable e) {
			/* Protect while cleaning up */
			try { 
				/* Close the input stream */
				if (myStream != null) myStream.close();
			} 
			
			/* Ignore errors */
			catch (Throwable ex) {}
			
			/* Report the error */
			throw new Exception(ExceptionClass.EXCEL, 
					  			"Failed to load Workbook: " + pFile.getName(),
					  			e);				
		}
		
		/* Return the new DataSet */
		return myData;
	}
		
	/**
	 *  Create a Backup Workbook
	 *  @param pThread Thread Control for task
	 *  @param pData Data to write out
	 *  @param pFile the backup file to write to
	 *  @param isEncrypted should we encrypt the file
	 */
	public static void createBackup(statusCtl 	pThread,
							 		DataSet		pData,
							 		File 		pFile,
							 		boolean		isEncrypted) throws Exception {
		OutputStream 		myStream  	= null;
		FileOutputStream    myOutFile  	= null;
		File				myTgtFile	= null;
		ZipFile.Output 		myZipFile   = null;
		SecurityControl		myControl;
		
		/* Protect the workbook retrieval */
		try {
			/* If we are not encrypted */
			if (!isEncrypted) {
				/* The Target file has .xls appended */
				myTgtFile 	= new File(pFile.getPath() + ".xls");

				/* The Target file is the named file */
				myTgtFile = pFile;
				
				/* Create an output stream to the file */
				myOutFile = new FileOutputStream(myTgtFile);
				myStream  = new BufferedOutputStream(myOutFile);
			}
		
			/* else we are encrypted */
			else {
				/* The Target file has .zip appended */
				myTgtFile 	= new File(pFile.getPath() + ".zip");

				/* Create a clone of the security control */
				myControl	= new SecurityControl(pData.getSecurityControl());
				
				/* Create the new output Zip file */
				myZipFile 	= new ZipFile.Output(myControl,
												 myTgtFile);
				myStream 	= myZipFile.getOutputStream(new File(ZipFile.fileData), 
														ZipEntryMode.getRandomTrioMode(myControl.getRandom()));
			}

			/* Write the data from the stream */
			createBackupStream(pThread, pData, myStream);
			
			/* Close the Stream to force out errors */
			myStream.close();
			
			/* If we are encrypted close the Zip file */
			if (myZipFile != null) myZipFile.close();
		}

		catch (Throwable e) {			
			/* Protect while cleaning up */
			try { 
				/* Close the output stream */
				if (myStream != null) myStream.close();

				/* If we are encrypted close the Zip file */
				if (myZipFile != null) myZipFile.close();
			} 
			
			/* Ignore errors */
			catch (Throwable ex) {}
			
			/* Delete the file on error */
			if (myTgtFile != null) myTgtFile.delete();			
			
			/* Report the error */
			throw new Exception(ExceptionClass.EXCEL, 
					  			"Failed to create Workbook: " + pFile.getName(),
					  			e);				
		}
	}
		
	/**
	 *  Load an Archive Workbook from a stream
	 *  @param pThread Thread Control for task
	 *  @param pStream Input stream to load from
	 *  @return the newly loaded data
	 */
	private static DataSet loadArchiveStream(statusCtl 		pThread,
			 						  		 InputStream	pStream) throws Exception {
		boolean             	bContinue;
		Workbook        		myWorkbook 	= null;
		DataSet					myData		= null;
		View					myView		= null;
		SheetStatic.YearRange	myRange 	= null;
		DilutionEvent.List		myDilution	= null;
		
		/* Protect the workbook retrieval */
		try {
			/* Create the Data */
			myView = pThread.getView();
			myData = new DataSet(myView.getSecurity());
			
			/* Access the workbook from the stream */
			myWorkbook = Workbook.getWorkbook(pStream);

			/* Create a YearRange */
			myRange = new SheetStatic.YearRange();
			
			/* Create the dilution event list */
			myDilution = new DilutionEvent.List(myData);
			
			/* Determine Year Range */
			bContinue = SheetStatic.loadArchive(pThread, myWorkbook, myData, myRange);
			
			/* Load Tables */
			if (bContinue) bContinue = SheetAccountType.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTransactionType.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTaxType.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTaxRegime.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetFrequency.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTaxYear.loadArchive(pThread, myWorkbook, myData, myRange);
			if (bContinue) myData.calculateDateRange();
			if (bContinue) bContinue = SheetAccount.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetRate.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetDilution.loadArchive(pThread, myWorkbook, myData, myDilution);
			if (bContinue) bContinue = SheetPrice.loadArchive(pThread, myWorkbook, myData, myDilution);
			if (bContinue) bContinue = SheetPattern.loadArchive(pThread, myWorkbook, myData);
			if (bContinue) myData.getAccounts().validateAccounts();
			if (bContinue) bContinue = SheetEvent.loadArchive(pThread, myWorkbook, myData, myRange);
		
			/* Close the work book (and the input stream) */
			myWorkbook.close();		
		
			/* Set the next stage */
			if (!pThread.setNewStage("Refreshing data")) bContinue = false;
							
			/* Check for cancellation */
			if (!bContinue) 
				throw new Exception(ExceptionClass.EXCEL,
									"Operation Cancelled");
		}
		
		catch (Throwable e) {
			/* Show we are cancelled */
			bContinue = false;
			
			/* Report the error */
			throw new Exception(ExceptionClass.EXCEL, 
					  			"Failed to load Workbook",
					  			e);				
		}
					
		/* Return the new data */
		return (bContinue) ? myData : null;
	}
	
	/**
	 *  Load a Backup Workbook from a stream
	 *  @param pThread Thread Control for task
	 *  @param pStream Input stream to load from
	 *  @return the newly loaded data
	 */
	private static DataSet loadBackupStream(statusCtl 	pThread,
			 						 	    InputStream	pStream) throws Exception {
		boolean             bContinue;
		Workbook        	myWorkbook 	= null;
		DataSet				myData		= null;
		View				myView		= null;
		
		/* Protect the workbook retrieval */
		try {
			/* Declare the number of stages */
			bContinue = pThread.setNumStages(14);

			/* Note the stage */
			if (bContinue) bContinue = pThread.setNewStage("Loading");

			/* Create the Data */
			myView = pThread.getView();
			myData = new DataSet(myView.getSecurity());
			
			/* Access the workbook from the stream */
			if (bContinue) myWorkbook = Workbook.getWorkbook(pStream);

			/* Load tables */
			if (bContinue) bContinue = SheetStatic.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetAccountType.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTransactionType.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTaxType.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTaxRegime.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetFrequency.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetTaxYear.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) myData.calculateDateRange();
			if (bContinue) bContinue = SheetAccount.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetRate.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetPrice.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) bContinue = SheetPattern.loadBackup(pThread, myWorkbook, myData);
			if (bContinue) myData.getAccounts().validateAccounts();
			if (bContinue) bContinue = SheetEvent.loadBackup(pThread, myWorkbook, myData);
		
			/* Close the work book (and the input stream) */
			myWorkbook.close();		
		
			/* Analyse the data */
			if (!pThread.setNewStage("Refreshing data")) bContinue = false;
							
			/* Check for cancellation */
			if (!bContinue) 
				throw new Exception(ExceptionClass.EXCEL,
									"Operation Cancelled");
		}
		
		catch (Throwable e) {
			/* Show we are cancelled */
			bContinue = false;
			
			/* Report the error */
			throw new Exception(ExceptionClass.EXCEL, 
					  			"Failed to load Workbook",
					  			e);				
		}
					
		/* Return the new data */
		return (bContinue) ? myData : null;
	}
	
	/**
	 *  Write a Backup Workbook to a stream
	 *  @param pThread Thread Control for task
	 *  @param pData Data to write out
	 *  @param pStream Output stream to write to
	 *  @return the newly loaded data
	 */
	private static void createBackupStream(statusCtl 	pThread,
										   DataSet		pData,
										   OutputStream	pStream) throws Exception {
		boolean             bContinue;
		WritableWorkbook 	myWorkbook = null;
				
		/* Protect the workbook creation */
		try {
			/* Declare the number of stages */
			bContinue = pThread.setNumStages(13);

			/* Create the workbook attached to the output stream */
			if (bContinue) myWorkbook = Workbook.createWorkbook(pStream);									
		
			/* build the workbook */
			if (bContinue) bContinue = SheetStatic.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetAccountType.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetTransactionType.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetTaxType.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetTaxRegime.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetFrequency.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetTaxYear.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetAccount.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetRate.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetPrice.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetPattern.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = SheetEvent.writeBackup(pThread, myWorkbook, pData);
			if (bContinue) bContinue = pThread.setNewStage("Writing");
			
			/* If we have created the workbook OK */
			if (bContinue) {
				/* Write it out to disk and close the stream */
				myWorkbook.write();
				myWorkbook.close();
			}
			
			/* Close the buffers */
			pStream.close();
			
			/* Check for cancellation */
			if (!bContinue) 
				throw new Exception(ExceptionClass.EXCEL,
									"Operation Cancelled");
		} 
		
		catch (Throwable e) {
			/* Report the error */
			throw new Exception(ExceptionClass.EXCEL, 
					  			"Failed to create Workbook",
					  			e);				
		}
	}	
}
