/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.threads;

import java.util.List;
import javax.swing.SwingWorker;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
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
	private ModelException 	theError		= null;

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
	protected void setError(ModelException pError) {
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
	
	@Override
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
			if (e instanceof ModelException)
				setError((ModelException)e);
			
			/* Else wrap the failure */
			else setError(new ModelException(ExceptionClass.DATA,
								   		"Failed " + theTask,
								   		e));
			return null;
		}	
	}

	@Override
	public void done() {
		/* Update the Status Bar */
		completeStatusBar();
	}		

	@Override
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
