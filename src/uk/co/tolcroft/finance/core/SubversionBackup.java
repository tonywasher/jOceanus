package uk.co.tolcroft.finance.core;

import uk.co.tolcroft.backup.Subversion;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.security.SecureManager;
import uk.co.tolcroft.models.security.SecurityControl;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.threads.WorkerThread;
import uk.co.tolcroft.models.views.DataControl;

public class SubversionBackup extends WorkerThread<Void> {
	/* Task description */
	private static String  	theTask		= "Subversion Backup Creation";

	/* Properties */
	private DataControl<FinanceData>	theControl	= null;
	private ThreadStatus<FinanceData>	theStatus	= null;

	/* Constructor (Event Thread)*/
	public SubversionBackup(DataControl<FinanceData> pControl) {
		/* Call super-constructor */
		super(theTask, pControl.getStatusBar());
		
		/* Store passed parameters */
		theControl	= pControl;

		/* Create the status */
		theStatus = new ThreadStatus<FinanceData>(this, theControl);

		/* Show the status window */
		showStatusBar();
	}

	/* Background task (Worker Thread)*/
	public Void performTask() throws Throwable {
		Subversion		myAccess	= null;
		
		try {
			/* Initialise the status window */
			theStatus.initTask("Creating Subversion Backup");

			/* Create a clone of the security control */
			FinanceData		myData		= theControl.getData();
			SecureManager 	mySecure	= myData.getSecurity();
			SecurityControl	myBase		= myData.getSecurityControl();
			SecurityControl myControl	= mySecure.cloneSecurityControl(myBase);
				
			/* Create backup */
			myAccess = new Subversion(theStatus);
			myAccess.backUpRepositories(myControl);
		}	

		/* Catch any exceptions */
		catch (Throwable e) { throw e;	}	

		/* Return nothing */
		return null;
	}
}
