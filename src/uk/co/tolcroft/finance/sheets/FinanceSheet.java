package uk.co.tolcroft.finance.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.Properties;
import uk.co.tolcroft.finance.views.DilutionEvent;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.ControlData;
import uk.co.tolcroft.models.sheets.SheetReader;
import uk.co.tolcroft.models.sheets.SheetWriter;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.threads.DataControl;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class FinanceSheet extends SpreadSheet<FinanceData> {
	/**
	 *  Obtain a sheet reader
	 *  @param pThread Thread Control for task
	 *  @return the sheet reader
	 */
	protected SheetReader<FinanceData> getSheetReader(ThreadStatus<FinanceData> pThread) {
		/* Create a Finance Reader object and return it */
		return new FinanceReader(pThread);
	}
		
	/**
	 *  Obtain a sheet writer
	 *  @param pThread Thread Control for task
	 *  @return the sheet writer
	 */
	protected SheetWriter<FinanceData> getSheetWriter(ThreadStatus<FinanceData> pThread) {
		/* Create a Finance Writer object and return it */
		return new FinanceWriter(pThread);
	}
	
	/**
	 * NamedRange for Static
	 */
	private static final String YearRange			= "YearRange";

	/**
	 * Simple class to hold YearRange 
	 */
	protected static class YearRange {
		private int theMinYear = 0;
		private int theMaxYear = 0;
		protected int getMinYear() { return theMinYear; }
		protected int getMaxYear() { return theMaxYear; }
		protected void setMinYear(int pYear) { theMinYear = pYear; }
		protected void setMaxYear(int pYear) { theMaxYear = pYear; }
	}
	
	/**
	 *  Load the Static from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @param pRange the range of tax years
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
			 					  		 Workbook					pWorkbook,
			 					  		 FinanceData				pData,
			 					  		 YearRange					pRange) throws Exception {
		/* Local variables */
		Range[] 		myRange;
		Sheet   		mySheet;
		Cell    		myTop;
		Cell    		myCell;
		int     		myCol;
		int     		myRow;
		int     		myStages;
		ControlData.List 	myStatic;

		/* Find the range of cells */
		myRange = pWorkbook.findByName(YearRange);
	
		/* If we found the range OK */
		if ((myRange != null) && (myRange.length == 1)) {
			
			/* Access the relevant sheet and Cell references */
			mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
			
			myTop    = myRange[0].getTopLeft();
			myRow    = myTop.getRow();
			myCol    = myTop.getColumn();
		
			/* Access the Year Range */
			myCell = mySheet.getCell(myCol, myRow+1);
			pRange.setMinYear(Integer.parseInt(myCell.getContents()));
			myCell = mySheet.getCell(myCol+1, myRow+1);
			pRange.setMaxYear(Integer.parseInt(myCell.getContents()));
			
			/* Access the static */
			myStatic = pData.getControlData();
		
			/* Add the value into the finance tables (with no security as yet) */
			myStatic.addItem(0, Properties.CURRENTVERSION);
		}

		/* Calculate the number of stages */
		myStages = 14 + pRange.getMaxYear() - pRange.getMinYear();
		
		/* Declare the number of stages */
		return pThread.setNumStages(myStages);
	}
	
	/**
	 *  Load an Archive Workbook
	 *  @param pThread Thread Control for task
	 *  @param pFile the archive file to load from
	 *  @return the newly loaded data
	 */
	public static FinanceData loadArchive(ThreadStatus<FinanceData>	pThread,
	  		 				   	   		  File 	 					pFile) throws Exception {
		InputStream 		myStream  	= null;
		FileInputStream     myInFile  	= null;
		FinanceData			myData;
		
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
	 *  Load an Archive Workbook from a stream
	 *  @param pThread Thread Control for task
	 *  @param pStream Input stream to load from
	 *  @return the newly loaded data
	 */
	private static FinanceData loadArchiveStream(ThreadStatus<FinanceData>	pThread,
			 						  	  		 InputStream				pStream) throws Exception {
		boolean             		bContinue;
		Workbook        			myWorkbook 	= null;
		FinanceData					myData		= null;
		DataControl<FinanceData>	myControl	= null;
		YearRange					myRange 	= null;
		DilutionEvent.List			myDilution	= null;
		
		/* Protect the workbook retrieval */
		try {
			/* Create the Data */
			myControl = pThread.getControl();
			myData 	  = myControl.getNewData();
			
			/* Access the workbook from the stream */
			myWorkbook = Workbook.getWorkbook(pStream);

			/* Create a YearRange */
			myRange = new YearRange();
			
			/* Create the dilution event list */
			myDilution = new DilutionEvent.List(myData);
			
			/* Determine Year Range */
			bContinue = loadArchive(pThread, myWorkbook, myData, myRange);
			
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
}
