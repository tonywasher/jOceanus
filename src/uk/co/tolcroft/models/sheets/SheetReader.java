package uk.co.tolcroft.models.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jxl.Workbook;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.security.SecureManager;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.security.SecuritySignature;
import uk.co.tolcroft.models.security.ZipFile;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.views.DataControl;

public abstract class SheetReader<T extends DataSet<T>> {
	/**
	 * Thread control
	 */
	private ThreadStatus<T>			theThread	= null;
	
	/**
	 * Spreadsheet
	 */
	private Workbook				theWorkBook	= null;
	
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
	protected ThreadStatus<T>		getThread()		{ return theThread; }
	protected Workbook				getWorkBook()	{ return theWorkBook; }
	public	  T						getData()		{ return theData; }
	public 	  SheetType				getType()		{ return theType; }
	
	/**
	 * Constructor
	 * @param pThread the Thread control
	 */
	public SheetReader(ThreadStatus<T> pThread) { theThread = pThread; }

	/**
	 * Add Sheet to list
	 */
	protected void addSheet(SheetDataItem<?> pTable) {
		theSheets.add(pTable);
	}
	
	/**
	 *  Load a Backup Workbook
	 *  @param pFile the backup file to write to
	 *  @return the loaded DataSet
	 */
	public T	loadBackup(File 		pFile) throws Exception {
		InputStream 		myStream  	= null;
		ZipFile.Input 		myFile		= null;
		DataControl<T>		myControl;
		String				mySecurityKey;
		SecurityControl		mySecControl;
		SecureManager		mySecurity;
		SecuritySignature	mySignature;
		
		/* Protect the workbook retrieval */
		try {
			/* Note the type of file */
			theType	 = SheetType.BACKUP;

			/* Access the zip file */
			myFile = new ZipFile.Input(pFile);
				
			/* Obtain the security signature from the file */
			mySecurityKey = myFile.getSecurityKey();
			mySignature	  = new SecuritySignature(mySecurityKey);

			/* Access the Security manager */
			myControl	= theThread.getControl();
			mySecurity 	= myControl.getSecurity();
				
			/* Obtain the initialised security control */
			mySecControl = mySecurity.getSecurityControl(mySignature, pFile.getName());
				
			/* Associate this control with the Zip file */
			myFile.setSecurityControl(mySecControl);
				
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
	 *  Load an Extract Workbook
	 *  @param pFile the Extract file to load from
	 *  @return the loaded DataSet
	 */
	public T loadExtract(File pFile) throws Exception {
		InputStream 		myStream  	= null;
		FileInputStream		myInFile	= null;
		
		/* Protect the workbook retrieval */
		try {
			/* Note the type of file */
			theType	 = SheetType.EXTRACT;

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
	 * Register sheets
	 */
	protected abstract void registerSheets(); 

	/**
	 * Obtain empty DataSet
	 */
	protected abstract T	newDataSet();

	/**
	 * Create the list of sheets to load
	 * @param the input stream
	 */
	private boolean initialiseWorkBook(InputStream pStream) throws Throwable {
		/* Create the new DataSet */
		theData = newDataSet();
		
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
