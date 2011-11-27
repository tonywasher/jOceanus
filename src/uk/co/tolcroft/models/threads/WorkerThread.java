package uk.co.tolcroft.models.threads;

import java.util.List;
import javax.swing.SwingWorker;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.ui.StatusBar;

public abstract class WorkerThread<T> extends SwingWorker <T, StatusData> {
	/**
	 * The Status Bar
	 */
	private StatusBar	theStatusBar	= null;
	
	/**
	 * The description of the operation
	 */
	private String 		theTask			= null;

	/**
	 * The error for the operation
	 */
	private Exception 	theError		= null;

	/**
	 * Constructor	
	 */
	protected WorkerThread(String		pTask,
						   StatusBar 	pStatusBar) {
		/* Record the parameters */
		theTask		 = pTask;
		theStatusBar = pStatusBar;
	}
	
	/**
	 *  Set Error
	 *  @param pError the Error for the task  
	 */
	protected void setError(Exception pError) {
		/* Store the error */
		theError = pError;
	}
	
	/**
	 *  Show StatusBar
	 */
	protected void showStatusBar() {
		theStatusBar.getProgressPanel().setVisible(true);		
	}
	
	/**
	 *  Complete Data Load operation
	 *  @param pTask the Task for the StatusBar
	 *  @param pError the Error (if any) for the task  
	 */
	protected void completeStatusBar() {
		/* If we are not cancelled and have no error */
		if ((!isCancelled()) && (theError == null)) {
			/* Set success */
			theStatusBar.setSuccess(theTask);
		}

		/* Else report the cancellation/failure */
		else theStatusBar.setFailure(theTask, theError);
	}
	
	/* Task for worker thread */
	protected abstract T performTask() throws Throwable;
	
	/* Background task (Worker Thread)*/
	public T doInBackground() {
		T myResult;
		
		try {
			/* Call work function */
			myResult = performTask();

			/* Return result */
			return myResult;
		}	

		/* Catch any exceptions */
		catch (Throwable e) {
			/* If this is an Exception */
			if (e instanceof Exception)
				setError((Exception)e);
			
			/* Else wrap the failure */
			else setError(new Exception(ExceptionClass.DATA,
								   		"Failed " + theTask,
								   		e));
			return null;
		}	
	}

	/* Completion task (Event Thread)*/
	public void done() {
		/* Update the Status Bar */
		completeStatusBar();
	}		

	/**
	 * Process task (Event Thread)
	 * @param pStatus list of recently published Status Events
	 */
	protected void process(List<StatusData> pStatus) {
		/* Access the latest status */
		StatusData myStatus = pStatus.get(pStatus.size() - 1);

		/* Update the status window */
		theStatusBar.updateStatusBar(myStatus);
	}

	/**
	 *  Publish status
	 *  @param pStatus the Status to publish
	 */
	public void publish(StatusData pStatus) {
		super.publish(pStatus);
	}
}
