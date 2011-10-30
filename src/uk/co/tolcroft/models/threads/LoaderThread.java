package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public abstract class LoaderThread<T extends DataSet<T>> extends WorkerThread<T> { 
	private DataControl<T>	theControl	= null;

	/**
	 * Constructor	
	 */
	protected LoaderThread(String			pTask,
						   DataControl<T>	pControl) {
		/* Record the parameters */
		super(pTask, pControl.getStatusBar());
		theControl = pControl;
	}

	/* Completion task (Event Thread)*/
	public void done() {
		T 			myData;

		try {
			/* If we are not cancelled */
			if (!isCancelled()) {				
				/* Get the newly loaded data */
				myData = get();

				/* If we have new data */
				if (myData != null) {
					/* Activate the data and obtain any error */
					theControl.setData(myData);
					setError(theControl.getError());
				}
			}

			/* Update the Status Bar */
			completeStatusBar();
		}	 	
		catch (Throwable e) {
			/* Report the failure */
			setError(new Exception(ExceptionClass.DATA,
								   "Failed to obtain and activate new data",
								   e));
			completeStatusBar();
		}
	}			
}
