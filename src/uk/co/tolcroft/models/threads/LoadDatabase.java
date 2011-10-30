package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.views.DataControl;

public class LoadDatabase<T extends DataSet<T>> extends LoaderThread<T> {
	/* Task description */
	private static String  	theTask		= "DataBase Load";

	/* Properties */
	private DataControl<T>	theControl	= null;
	private ThreadStatus<T>	theStatus	= null;

	/* Constructor (Event Thread)*/
	public LoadDatabase(DataControl<T>	pControl) {
		/* Call super-constructor */
		super(theTask, pControl);
		
		/* Store passed parameters */
		theControl	= pControl;

		/* Create the status */
		theStatus = new ThreadStatus<T>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Loading Database");
	}

	/* Background task (Worker Thread)*/
	public T performTask() throws Throwable {
		T			myData	   	= null;
		Database<T>	myDatabase	= null;

		/* Access database */
		myDatabase = theControl.getDatabase();

		/* Load database */
		myData    = myDatabase.loadDatabase(theStatus);

		/* Return the loaded data */
		return myData;
	}
}
