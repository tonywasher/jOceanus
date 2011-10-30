package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public class UpdatePassword<T extends DataSet<T>> extends LoaderThread<T> {
	/* Task description */
	private static String  	theTask		= "Update Password";

	/* Properties */
	private DataControl<T>	theControl	 = null;
	private ThreadStatus<T>	theStatus    = null;

	/* Constructor (Event Thread)*/
	public UpdatePassword(DataControl<T> pControl) {
		/* Call super-constructor */
		super(theTask, pControl);
		
		/* Store passed parameters */
		theControl	  = pControl;

		/* Create the status */
		theStatus = new ThreadStatus<T>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Updating Password");
	}


	/* Background task (Worker Thread)*/
	public T performTask() throws Throwable {
		T			myData;

		/* Access Data */
		myData 	= theControl.getData();
		myData	= myData.getDeepCopy();

		/* Update password */
		myData.updateSecurityControl(theStatus, "Database");

		/* Return null */
		return myData;
	}
}
