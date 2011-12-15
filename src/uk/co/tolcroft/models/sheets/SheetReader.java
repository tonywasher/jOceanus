package uk.co.tolcroft.models.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;

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
			myStream = null;
			
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
			myStream = null;
			
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
		if (bContinue) theWorkBook = new HSSFWorkbook(pStream);

		/* Set the missing Cell Policy */
		theWorkBook.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);
		
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
		
		/* Analyse the data */
		if (!theThread.setNewStage("Refreshing data")) bContinue = false;
						
		/* Return continue status */
		return bContinue;
	}
	
	/**
	 * SheetHelper class
	 */
	public static class SheetHelper {
		/**
		 * The Workbook
		 */
		private Workbook 			theWorkBook		= null;
		
		/**
		 * The FormulaEvaluator 
		 */
		private FormulaEvaluator	theEvaluator	= null;
		
		/**
		 * Constructor
		 * @param pWorkBook the workbook
		 */
		public SheetHelper(HSSFWorkbook pWorkbook) {
			/* Store the workbook */
			theWorkBook	= pWorkbook;
			
			/* Create the evaluator */
			theEvaluator = new HSSFFormulaEvaluator(pWorkbook);
		}
		
		/**
		 * Resolve reference
		 * @param pName the name of the range
		 * @return the AreaReference (or null)
		 */
		public AreaReference resolveAreaReference(String pName) {
			AreaReference myRef = null;
			Name myName = theWorkBook.getName(pName);
			if (myName != null) myRef = new AreaReference(myName.getRefersToFormula());
			return myRef;
		}

		/**
		 * getSheetByName
		 * @param pName the name of the sheet
		 * @return the Sheet
		 */
		public Sheet getSheetByName(String pName) {
			return theWorkBook.getSheet(pName);
		}
		
		/**
		 * Format numeric cell into decimal format
		 * @param pCell the cell to format
		 * @return the formatted string
		 */
		public String formatNumericCell(Cell pCell) {
			double myDouble;
			/* If this is a formula cell ensure that it is evaluated */
			if (pCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				CellValue myValue = theEvaluator.evaluate(pCell);
				myDouble = myValue.getNumberValue();					
			}
			/* else just extract the value directly */
			else myDouble = pCell.getNumericCellValue();
			
			/* return the formatted string */
			return Double.toString(myDouble);
		}
		
		/**
		 * Parse the cell to return an integer
		 * @param pCell the cell to parse
		 * @return the parsed cell
		 */
		public Integer parseIntegerCell(Cell pCell) {
			double myDouble;
			/* If this is a formula cell ensure that it is evaluated */
			if (pCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				CellValue myValue = theEvaluator.evaluate(pCell);
				myDouble = myValue.getNumberValue();					
			}
			
			/* else just extract the value directly */
			else myDouble = pCell.getNumericCellValue();
			
			/* Return the value as an integer */
			return (int)myDouble;
		}
		
		/**
		 * Parse the cell to return an integer
		 * @param pCell the cell to parse
		 * @return the parsed cell
		 */
		public String formatRateCell(Cell pCell) {
			double myDouble;
			/* If this is a formula cell ensure that it is evaluated */
			if (pCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				CellValue myValue = theEvaluator.evaluate(pCell);
				myDouble = myValue.getNumberValue();					
			}
			/* else just extract the value directly */
			else myDouble = pCell.getNumericCellValue();
			
			/* return the formatted string */
			return Double.toString(100*myDouble);
		}
	}
}
