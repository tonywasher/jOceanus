package uk.co.tolcroft.finance.sheets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		/* Create an input sheet object */
		InputSheet mySheet = new InputSheet(pThread);
			
		/* Create the backup */
		DataSet myData = mySheet.loadBackup(pFile);
		
		/* Return the data */
		return myData;
	}
		
	/**
	 *  Create a Backup Workbook
	 *  @param pThread Thread Control for task
	 *  @param pData Data to write out
	 *  @param pFile the backup file to write to
	 */
	public static void createBackup(statusCtl 	pThread,
							 		DataSet		pData,
							 		File 		pFile) throws Exception {
		/* Create an output sheet object */
		OutputSheet mySheet = new OutputSheet(pThread);
			
		/* Create the backup */
		mySheet.createBackup(pData, pFile);
	}
		
	/**
	 *  Load an Edit-able Workbook
	 *  @param pThread Thread Control for task
	 *  @param pFile the edit-able file to load from
	 *  @return the newly loaded data
	 */
	public static DataSet loadEditable(statusCtl 	pThread,
	  		 				  		   File 		pFile) throws Exception {
		/* Create an input sheet object */
		InputSheet mySheet = new InputSheet(pThread);
			
		/* Load the edit-able file */
		DataSet myData = mySheet.loadEditable(pFile);
		
		/* Return the data */
		return myData;
	}
		
	/**
	 *  Create a Backup Workbook
	 *  @param pThread Thread Control for task
	 *  @param pData Data to write out
	 *  @param pFile the edit-able file to write to
	 */
	public static void createEditable(statusCtl pThread,
							 		  DataSet	pData,
							 		  File 		pFile) throws Exception {
		/* Create an output sheet object */
		OutputSheet mySheet = new OutputSheet(pThread);
			
		/* Create the Edit-able file */
		mySheet.createEditable(pData, pFile);
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
		SheetControl.YearRange	myRange 	= null;
		DilutionEvent.List		myDilution	= null;
		
		/* Protect the workbook retrieval */
		try {
			/* Create the Data */
			myView = pThread.getView();
			myData = new DataSet(myView.getSecurity());
			
			/* Access the workbook from the stream */
			myWorkbook = Workbook.getWorkbook(pStream);

			/* Create a YearRange */
			myRange = new SheetControl.YearRange();
			
			/* Create the dilution event list */
			myDilution = new DilutionEvent.List(myData);
			
			/* Determine Year Range */
			bContinue = SheetControl.loadArchive(pThread, myWorkbook, myData, myRange);
			
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
			if (bContinue) myData.getAccounts().validateLoadedAccounts();
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
	 * Input Spreadsheet class
	 */
	protected static class InputSheet {
		/**
		 * Thread control
		 */
		private statusCtl				theThread	= null;
		
		/**
		 * Spreadsheet
		 */
		private Workbook				theWorkBook	= null;
		
		/**
		 * The DataSet
		 */
		private DataSet					theData		= null;
		
		/**
		 * The WorkSheets
		 */
		private List<SheetDataItem<?>>	theSheets	= null;
		
		/**
		 * Class of output sheet
		 */
		private SheetType				theType		= null;
		
		/* Access methods */
		protected statusCtl			getThread()		{ return theThread; }
		protected Workbook			getWorkBook()	{ return theWorkBook; }
		protected DataSet			getData()		{ return theData; }
		protected SheetType			getType()		{ return theType; }
		
		/**
		 * Constructor
		 * @param pThread the Thread control
		 */
		public InputSheet(statusCtl pThread) { theThread = pThread; }
		
		/**
		 *  Load a Backup Workbook
		 *  @param pFile the backup file to write to
		 *  @returns the loaded DataSet
		 */
		public DataSet loadBackup(File 		pFile) throws Exception {
			InputStream 		myStream  	= null;
			ZipFile.Input 		myFile		= null;
			View				myView;
			String				mySecurityKey;
			SecurityControl		myControl;
			SecureManager		mySecurity;
			
			/* Protect the workbook retrieval */
			try {
				/* Note the type of file */
				theType	 = SheetType.BACKUP;

				/* Access the zip file */
				myFile = new ZipFile.Input(pFile);
					
				/* Obtain the security key from the file */
				mySecurityKey = myFile.getSecurityKey();

				/* Access the Security manager */
				myView 		= theThread.getView();
				mySecurity 	= myView.getSecurity();
					
				/* Obtain the initialised security control */
				myControl = mySecurity.getSecurityControl(mySecurityKey, pFile.getName());
					
				/* Associate this control with the Zip file */
				myFile.setSecurityControl(myControl);
					
				/* Access the input stream for the first file */
				myStream = myFile.getInputStream(myFile.getFiles());
				
				/* Initialise the workbook */
				boolean bContinue = initialiseWorkBook(myStream);
				
				/* Load the workbook */
				if (bContinue) bContinue = loadWorkBook();
				
				/* Close the Stream to force out errors */
				myStream.close();
				
				/* Check for cancellation */
				if (!bContinue) 
					throw new Exception(ExceptionClass.EXCEL,
										"Operation Cancelled");
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
						  			"Failed to load Backup Workbook: " + pFile.getName(),
						  			e);				
			}
			
			/* Return the new DataSet */
			return theData;			
		}

		/**
		 *  Load an Edit-able Workbook
		 *  @param pFile the Edit-able file to load from
		 *  @returns the loaded DataSet
		 */
		public DataSet loadEditable(File 		pFile) throws Exception {
			InputStream 		myStream  	= null;
			FileInputStream		myInFile	= null;
			
			/* Protect the workbook retrieval */
			try {
				/* Note the type of file */
				theType	 = SheetType.EDITABLE;

				/* Create an input stream to the file */
				myInFile = new FileInputStream(pFile);
				myStream = new BufferedInputStream(myInFile);

				/* Initialise the workbook */
				boolean bContinue = initialiseWorkBook(myStream);
				
				/* Load the workbook */
				if (bContinue) bContinue = loadWorkBook();
				
				/* Close the Stream to force out errors */
				myStream.close();
				
				/* Check for cancellation */
				if (!bContinue) 
					throw new Exception(ExceptionClass.EXCEL,
										"Operation Cancelled");
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
						  			"Failed to load Edit-able Workbook: " + pFile.getName(),
						  			e);				
			}
			
			/* Return the new DataSet */
			return theData;			
		}

		/**
		 * Create the list of sheets to load
		 * @param the input stream
		 */
		private boolean initialiseWorkBook(InputStream pStream) throws Throwable {
			View				myView;
			
			/* Create the new DataSet */
			myView = theThread.getView();
			theData = new DataSet(myView.getSecurity());
			
			/* Initialise the list */
			theSheets = new ArrayList<SheetDataItem<?>>();

			/* If this is a backup */
			if (theType == SheetType.BACKUP) {
				/* Add security details */
				theSheets.add(new SheetControlKey(this));
				theSheets.add(new SheetDataKey(this));				
			}
			
			/* Add the items */
			theSheets.add(new SheetControl(this));
			theSheets.add(new SheetAccountType(this));
			theSheets.add(new SheetTransactionType(this));
			theSheets.add(new SheetTaxType(this));
			theSheets.add(new SheetTaxRegime(this));
			theSheets.add(new SheetFrequency(this));
			theSheets.add(new SheetTaxYear(this));
			theSheets.add(new SheetAccount(this));
			theSheets.add(new SheetRate(this));
			theSheets.add(new SheetPrice(this));
			theSheets.add(new SheetPattern(this));
			theSheets.add(new SheetEvent(this));
			
			/* Declare the number of stages */
			boolean bContinue = theThread.setNumStages(theSheets.size()+2);

			/* Note the stage */
			if (bContinue) bContinue = theThread.setNewStage("Loading");

			/* Access the workbook from the stream */
			if (bContinue) theWorkBook = Workbook.getWorkbook(pStream);
			
			/* Return continue status */
			return bContinue;
		}
		
		/**
		 * Load the WorkBook
		 */
		private boolean loadWorkBook() throws Throwable {
			SheetDataItem<?> mySheet;
			
			/* Access the iterator for the list */
			Iterator<SheetDataItem<?>> myIterator = theSheets.iterator();
			
			/* Declare the number of stages */
			boolean bContinue = theThread.setNumStages(theSheets.size()+1);

			/* Loop through the sheets */
			while ((bContinue) &&
				   (myIterator.hasNext())) {
				/* Access the next sheet */
				mySheet = myIterator.next();
				
				/* Write data for the sheet */
				bContinue = mySheet.loadSpreadSheet();
			}
			
			/* Close the work book (and the input stream) */
			theWorkBook.close();		
		
			/* Analyse the data */
			if (!theThread.setNewStage("Refreshing data")) bContinue = false;
							
			/* Return continue status */
			return bContinue;
		}
	}
	
	/**
	 * Output Spreadsheet class
	 */
	protected static class OutputSheet {
		/**
		 * Thread control
		 */
		private statusCtl				theThread	= null;
		
		/**
		 * Writable spreadsheet
		 */
		private WritableWorkbook		theWorkBook	= null;
		
		/**
		 * The DataSet
		 */
		private DataSet					theData		= null;
		
		/**
		 * The WorkSheets
		 */
		private List<SheetDataItem<?>>	theSheets	= null;
		
		/**
		 * Class of output sheet
		 */
		private SheetType				theType		= null;
		
		/* Access methods */
		protected statusCtl			getThread()		{ return theThread; }
		protected WritableWorkbook	getWorkBook()	{ return theWorkBook; }
		protected DataSet			getData()		{ return theData; }
		protected SheetType			getType()		{ return theType; }
		
		/**
		 * Constructor
		 * @param pThread the Thread control
		 */
		public OutputSheet(statusCtl pThread) { theThread = pThread; }
		
		/**
		 *  Create a Backup Workbook
		 *  @param pData Data to write out
		 *  @param pFile the backup file to write to
		 */
		public void createBackup(DataSet	pData,
								 File 		pFile) throws Exception {
			OutputStream		myStream	= null;
			File				myTgtFile	= null;
			ZipFile.Output 		myZipFile   = null;
			SecurityControl		myControl;
			
			/* Protect the workbook access */
			try {
				/* Note the type of file */
				theType	 = SheetType.BACKUP;

				/* Record the DataSet */
				theData	= pData;
				
				/* The Target file has ".zip" appended */
				myTgtFile 	= new File(pFile.getPath() + ".zip");

				/* Create a clone of the security control */
				myControl	= new SecurityControl(pData.getSecurityControl());
					
				/* Create the new output Zip file */
				myZipFile 	= new ZipFile.Output(myControl,
												 myTgtFile);
				myStream 	= myZipFile.getOutputStream(new File(ZipFile.fileData), 
														ZipEntryMode.getRandomTrioMode(myControl.getRandom()));
				
				/* Initialise the WorkBook */
				initialiseWorkBook(myStream);									
			
				/* Write the data to the work book */
				writeWorkBook();
				
				/* Close the Stream to force out errors */
				myStream.close();
				
				/* Close the Zip file */
				myZipFile.close();
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
						  			"Failed to create Backup Workbook: " + pFile.getName(),
						  			e);				
			}
		}
		
		/**
		 *  Create an Edit-able Workbook
		 *  @param pData Data to write out
		 *  @param pFile the backup file to write to
		 */
		public void createEditable(DataSet	pData,
								   File 	pFile) throws Exception {
			OutputStream		myStream	= null;
			FileOutputStream    myOutFile  	= null;
			File				myTgtFile	= null;
			
			/* Protect the workbook access */
			try {
				/* Note the type of file */
				theType	 = SheetType.EDITABLE;

				/* Record the DataSet */
				theData	= pData;
				
				/* The Target file has ".xls" appended */
				myTgtFile 	= new File(pFile.getPath() + ".xls");

				/* The Target file is the named file */
				myTgtFile = pFile;
					
				/* Create an output stream to the file */
				myOutFile = new FileOutputStream(myTgtFile);
				myStream  = new BufferedOutputStream(myOutFile);

				/* Initialise the WorkBook */
				initialiseWorkBook(myStream);									
			
				/* Write the data to the work book */
				writeWorkBook();
				
				/* Close the Stream to force out errors */
				myStream.close();				
			}

			catch (Throwable e) {			
				/* Protect while cleaning up */
				try { 
					/* Close the output stream */
					if (myStream != null) myStream.close();
				} 
				
				/* Ignore errors */
				catch (Throwable ex) {}
				
				/* Delete the file on error */
				if (myTgtFile != null) myTgtFile.delete();			
				
				/* Report the error */
				throw new Exception(ExceptionClass.EXCEL, 
						  			"Failed to create Editable Workbook: " + pFile.getName(),
						  			e);				
			}
		}
		
		/**
		 * Create the list of sheets to write
		 * @param the output stream
		 */
		private void initialiseWorkBook(OutputStream pStream) throws Throwable {
			/* Create the workbook attached to the output stream */
			theWorkBook = Workbook.createWorkbook(pStream);									
		
			/* Initialise the list */
			theSheets = new ArrayList<SheetDataItem<?>>();
			
			/* If this is a backup */
			if (theType == SheetType.BACKUP) {
				/* Add security details */
				theSheets.add(new SheetControlKey(this));
				theSheets.add(new SheetDataKey(this));				
			}
			
			/* Add the items */
			theSheets.add(new SheetControl(this));
			theSheets.add(new SheetAccountType(this));
			theSheets.add(new SheetTransactionType(this));
			theSheets.add(new SheetTaxType(this));
			theSheets.add(new SheetTaxRegime(this));
			theSheets.add(new SheetFrequency(this));
			theSheets.add(new SheetTaxYear(this));
			theSheets.add(new SheetAccount(this));
			theSheets.add(new SheetRate(this));
			theSheets.add(new SheetPrice(this));
			theSheets.add(new SheetPattern(this));
			theSheets.add(new SheetEvent(this));
		}
		
		/**
		 * Write the WorkBook
		 */
		private void writeWorkBook() throws Throwable {
			SheetDataItem<?> mySheet;
			
			/* Access the iterator for the list */
			Iterator<SheetDataItem<?>> myIterator = theSheets.iterator();
			
			/* Declare the number of stages */
			boolean bContinue = theThread.setNumStages(theSheets.size()+1);

			/* Loop through the sheets */
			while ((bContinue) &&
				   (myIterator.hasNext())) {
				/* Access the next sheet */
				mySheet = myIterator.next();
				
				/* Write data for the sheet */
				bContinue = mySheet.writeSpreadSheet();
			}
			
			/* If we have built all the sheets */
			if (bContinue) bContinue = theThread.setNewStage("Writing");
			
			/* If we have created the workbook OK */
			if (bContinue) {
				/* Write it out to disk and close the stream */
				theWorkBook.write();
				theWorkBook.close();
			}
			
			/* Check for cancellation */
			if (!bContinue) 
				throw new Exception(ExceptionClass.EXCEL,
									"Operation Cancelled");
		}
 	}
	
	/**
	 * Spreadsheet types
	 */
	protected enum SheetType {
		BACKUP,
		EDITABLE;
	}
}
