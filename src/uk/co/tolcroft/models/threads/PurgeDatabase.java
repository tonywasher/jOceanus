package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.views.DataControl;

public class PurgeDatabase<T extends DataSet<T>> extends WorkerThread<Void> {
	/* Task description */
	private static String  	theTask		= "DataBase Purge";

	/* Properties */
	private DataControl<T>	theControl	 = null;
	private ThreadStatus<T>	theStatus    = null;

	/* Constructor (Event Thread)*/
	public PurgeDatabase(DataControl<T> pControl) {
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
		Database<T>	myDatabase	= null;
		T			myData;
		T			myNull;

		/* Initialise the status window */
		theStatus.initTask("Purging Database");

		/* Create interface */
		myDatabase = theControl.getDatabase();

		/* Load database */
		myDatabase.purgeTables(theStatus);

		/* Re-base this set on a null set */
		myNull = theControl.getNewData();
		myData = theControl.getData();
		myData.reBase(myNull);
		
		/* Return null */
		return null;
	}
}
