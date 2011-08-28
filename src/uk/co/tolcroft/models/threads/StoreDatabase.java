package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;

public class StoreDatabase<T extends DataSet<?>>	extends WorkerThread<Void> {
	/* Task description */
	private static String  	theTask		= "DataBase Store";

	/* Properties */
	private DataControl<T>	theControl	= null;
	private ThreadStatus<T>	theStatus	= null;

	/* Constructor (Event Thread)*/
	public StoreDatabase(DataControl<T> pControl) {
		/* Call super-constructor */
		super(theTask, pControl.getStatusBar());
		
		/* Store passed parameters */
		theControl	  = pControl;

		/* Create the status */
		theStatus = new ThreadStatus<T>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Storing to Database");
	}

	/* Background task (Worker Thread)*/
	public Void performTask() throws Throwable {
		Database<T>	myDatabase	= null;
		T			myData;
		DataSet<?>	myDiff;

		/* Create interface */
		myDatabase = theControl.getDatabase();

		/* Store database */
		myDatabase.updateDatabase(theStatus, theControl.getUpdates());

		/* Re-initialise the status window */
		initStatusBar("Verifying Store");

		/* Load database */
		myData	= myDatabase.loadDatabase(theStatus);

		/* Create a difference set between the two data copies */
		myDiff 	= myData.getDifferenceSet(theControl.getData());

		/* If the difference set is non-empty */
		if (!myDiff.isEmpty()) {
			/* Throw an exception */
			throw new Exception(ExceptionClass.DATA,
								myDiff,
								"DataStore is inconsistent");
		}
		
		/* Return null */
		return null;
	}
}
