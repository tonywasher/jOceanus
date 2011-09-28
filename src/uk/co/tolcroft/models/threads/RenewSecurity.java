package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public class RenewSecurity<T extends DataSet<T,?>> extends LoaderThread<T> {
	/* Task description */
	private static String  	theTask		= "ReNew Security";

	/* Properties */
	private DataControl<T>	theControl	 = null;
	private ThreadStatus<T>	theStatus    = null;

	/* Constructor (Event Thread)*/
	public RenewSecurity(DataControl<T> pControl) {
		/* Call super-constructor */
		super(theTask, pControl);
		
		/* Store passed parameters */
		theControl	  = pControl;

		/* Create the status */
		theStatus = new ThreadStatus<T>(this, theControl);

		/* Initialise the status window */
		initStatusBar("Renewing Security");
	}


	/* Background task (Worker Thread)*/
	public T performTask() throws Throwable {
		T			myData;

		/* Access Data */
		myData	= theControl.getData();
		myData	= myData.getDeepCopy();
		
		/* ReNew Security */
		myData.renewSecurity(theStatus);

		/* Return null */
		return myData;
	}
}
