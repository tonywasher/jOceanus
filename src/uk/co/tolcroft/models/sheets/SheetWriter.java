package uk.co.tolcroft.models.sheets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.security.SecureManager;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.security.ZipEntryMode;
import uk.co.tolcroft.models.security.ZipFile;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public abstract class SheetWriter<T extends DataSet<T,?>> {
	/**
	 * Thread control
	 */
	private ThreadStatus<T>			theThread	= null;
	
	/**
	 * Writable spreadsheet
	 */
	private WritableWorkbook		theWorkBook	= null;
	
	/**
	 * The DataSet
	 */
	private T						theData		= null;
	
	/**
	 * The WorkSheets
	 */
	private List<SheetDataItem<?>>	theSheets	= null;
	
	/**
	 * Class of output sheet
	 */
	private SheetType				theType		= null;
	
	/* Access methods */
	protected ThreadStatus<T>	getThread()		{ return theThread; }
	protected WritableWorkbook	getWorkBook()	{ return theWorkBook; }
	public 	  T					getData()		{ return theData; }
	public 	  SheetType			getType()		{ return theType; }
	
	/**
	 * Constructor
	 * @param pThread the Thread control
	 */
	protected SheetWriter(ThreadStatus<T> pThread) { theThread = pThread; }
	
	/**
	 * Add Sheet to list
	 */
	protected void addSheet(SheetDataItem<?> pTable) {
		theSheets.add(pTable);
	}
	
	/**
	 *  Create a Backup Workbook
	 *  @param pData Data to write out
	 *  @param pFile the backup file to write to
	 */
	public void createBackup(T			pData,
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
			SecureManager 	mySecure	= pData.getSecurity();
			SecurityControl	myBase		= pData.getSecurityControl();
			myControl	= mySecure.cloneSecurityControl(myBase);
				
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
	 *  Create an Extract Workbook
	 *  @param pData Data to write out
	 *  @param pFile the backup file to write to
	 */
	public void createExtract(T				pData,
							  File 			pFile) throws Exception {
		OutputStream		myStream	= null;
		FileOutputStream    myOutFile  	= null;
		File				myTgtFile	= null;
		
		/* Protect the workbook access */
		try {
			/* Note the type of file */
			theType	 = SheetType.EXTRACT;

			/* Record the DataSet */
			theData	= pData;
			
			/* The Target file has ".xls" appended */
			myTgtFile 	= new File(pFile.getPath() + ".xls");

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
					  			"Failed to create Editable Workbook: " + myTgtFile.getName(),
					  			e);				
		}
	}
	
	/**
	 * Register sheets
	 */
	protected abstract void registerSheets(); 

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

		/* register additional sheets */
		registerSheets();
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
