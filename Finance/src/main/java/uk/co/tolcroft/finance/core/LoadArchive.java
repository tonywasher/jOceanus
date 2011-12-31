package uk.co.tolcroft.finance.core;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.sheets.FinanceSheet;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.threads.LoaderThread;
import uk.co.tolcroft.models.threads.ThreadStatus;
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

		/* Show the status window */
		showStatusBar();
	}

	/* Background task (Worker Thread)*/
	public FinanceData performTask() throws ModelException {
		FinanceData				myData   = null;
		FinanceData				myStore;
		Database<FinanceData>	myDatabase;

		/* Initialise the status window */
		theStatus.initTask("Loading Extract");

		/* Load workbook */
		myData   = FinanceSheet.loadArchive(theStatus);

		/* Initialise the status window */
		theStatus.initTask("Accessing DataStore");

		/* Create interface */
		myDatabase = theControl.getDatabase();

		/* Load underlying database */
		myStore	= myDatabase.loadDatabase(theStatus);

		/* Initialise the status window */
		theStatus.initTask("Applying Security");
	
		/* Initialise the security, either from database or with a new security control */
		myData.initialiseSecurity(theStatus, myStore);
			
		/* Initialise the status window */
		theStatus.initTask("Analysing Data");
	
		/* Analyse the Data to ensure that close dates are updated */
		myData.analyseData(theControl);
			
		/* Re-base the loaded spreadsheet onto the database image */
		myData.reBase(myStore);

		/* Return the loaded data */
		return myData;
	}
}
