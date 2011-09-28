package uk.co.tolcroft.finance.core;

import java.io.File;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.sheets.FinanceSheet;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.threads.LoaderThread;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.ui.FileSelector.ArchiveLoad;
import uk.co.tolcroft.models.views.DataControl;

public class LoadArchive extends LoaderThread<FinanceData> {
	/* Task description */
	private static String  	theTask		= "Archive Load";

	/* Properties */
	private DataControl<FinanceData>	theControl 	= null;
	private ThreadStatus<FinanceData>	theStatus    	= null;

	/* Constructor (Event Thread)*/
	public LoadArchive(DataControl<FinanceData> pControl) {
		/* Call super-constructor */
		super(theTask, pControl);
		
		/* Store passed parameters */
		theControl	= pControl;

		/* Create the status */
		theStatus = new ThreadStatus<FinanceData>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Loading Extract");
	}

	/* Background task (Worker Thread)*/
	public FinanceData performTask() throws Exception {
		FinanceData				myData   = null;
		FinanceData				myStore;
		Database<FinanceData>	myDatabase;
		File					myFile;

		/* Determine the name of the file to load */
		ArchiveLoad myDialog = new ArchiveLoad(theControl);
		myDialog.selectFile();
		myFile = myDialog.getSelectedFile();
		
		/* If we did not select a file */
		if (myFile == null) {
			/* Throw cancelled exception */
			throw new Exception(ExceptionClass.EXCEL,
								"Operation Cancelled");					
		}
			
		/* Load workbook */
		myData   = FinanceSheet.loadArchive(theStatus, myFile);

		/* Re-initialise the status window */
		initStatusBar("Accessing DataStore");

		/* Create interface */
		myDatabase = theControl.getDatabase();

		/* Load underlying database */
		myStore	= myDatabase.loadDatabase(theStatus);

		/* Re-initialise the status window */
		initStatusBar("Applying Security");
	
		/* Initialise the security, either from database or with a new security control */
		myData.initialiseSecurity(theStatus, myStore);
			
		/* Re-initialise the status window */
		initStatusBar("Analysing Data");
	
		/* Analyse the Data to ensure that close dates are updated */
		myData.analyseData(theControl);
			
		/* Re-base the loaded spreadsheet onto the database image */
		myData.reBase(myStore);

		/* Return the loaded data */
		return myData;
	}
}
