package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.views.DataControl;

public class StoreDatabase<T extends DataSet<T>>	extends WorkerThread<Void> {
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

		/* Show the status window */
		showStatusBar();
	}

	/* Background task (Worker Thread)*/
	public Void performTask() throws Throwable {
		Database<T>		myDatabase	= null;
		T				myData;
		DataSet<T>		myDiff;

		/* Initialise the status window */
		theStatus.initTask("Storing to Database");

		/* Create interface */
		myDatabase = theControl.getDatabase();

		/* Store database */
		myDatabase.updateDatabase(theStatus, theControl.getUpdates());

		/* Initialise the status window */
		theStatus.initTask("Verifying Store");

		/* Load database */
		myData	= myDatabase.loadDatabase(theStatus);

		/* Create a difference set between the two data copies */
		myDiff 	= myData.getDifferenceSet(theControl.getData());

		/* If the difference set is non-empty */
		if (!myDiff.isEmpty()) {
			/* Throw an exception */
			throw new ModelException(ExceptionClass.DATA,
								myDiff,
								"DataStore is inconsistent");
		}
		
		/* Return null */
		return null;
	}
}
