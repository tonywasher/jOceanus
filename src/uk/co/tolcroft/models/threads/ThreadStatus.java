package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.Properties;
import uk.co.tolcroft.models.views.DataControl;

public class ThreadStatus<T extends DataSet<T>> implements StatusControl {
	private WorkerThread<?>	theThread		= null;
	private StatusData 		theStatus		= null;
	private DataControl<T>	theControl		= null;
	private Properties		theProperties	= null;
	private int          	theSteps    	= 50;
	
	/* Access methods */
	public int 				getReportingSteps() { return theSteps; }
	public Properties 		getProperties() 	{ return theProperties; }
	public DataControl<T>	getControl() 		{ return theControl; }
		
	/* Constructor */
	public ThreadStatus(WorkerThread<?>	pThread,
			         	DataControl<T>	pControl) {
		/* Store parameter */
		theThread 		= pThread;
		theControl		= pControl;
		theProperties 	= pControl.getProperties();
		
		/* Create the status */
		theStatus = new StatusData();
	}
	
	/* Publish task 0 (Worker Thread)*/
	public boolean initTask(String pTask) {
		StatusData myStatus;
		
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Record task and stage */
		theStatus.setTask(pTask);
		theStatus.setStage("");
		
		/* Set number of Stages and set Stages done to -1 */
		theStatus.setNumStages(100);
		theStatus.setStagesDone(-1);
		
		/* Set number of Steps and set Steps done to -1 */
		theStatus.setNumSteps(100);
		theStatus.setStepsDone(-1);
		
		/* Create a new Status */
		myStatus = new StatusData(theStatus);
		
		/* Publish it */
		theThread.publish(myStatus);
		
		/* Return to caller */
		return true;
	}
	
	/* Publish task 1 (Worker Thread)*/
	public boolean setNumStages(int pNumStages) {
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Initialise the number of stages and Stages done */
		theStatus.setNumStages(pNumStages);
		theStatus.setStagesDone(-1);
		
		/* Return to caller */
		return true;
	}
	
	/* Publish task 2 (Worker Thread)*/
	public boolean setNewStage(String pStage) {
		StatusData myStatus;
		
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Store the stage and increment stages done */
		theStatus.setStage(pStage);
		theStatus.setStagesDone(theStatus.getStagesDone()+1);
		theStatus.setNumSteps(100);
		if (theStatus.getStagesDone() < theStatus.getNumStages()) 
			theStatus.setStepsDone(0);
		else 
			theStatus.setStepsDone(100);
		
		/* Create a new Status */
		myStatus = new StatusData(theStatus);
		
		/* Publish it */
		theThread.publish(myStatus);
		
		/* Return to caller */
		return true;
	}
	
	/* Publish task 3 (Worker Thread)*/
	public boolean setNumSteps(int pNumSteps) {
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Set number of Steps */
		theStatus.setNumSteps(pNumSteps);
		
		/* Return to caller */
		return true;
	}
	
	/* Publish task 4 (Worker Thread)*/
	public boolean setStepsDone(int pStepsDone) {
		StatusData myStatus;
		
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Set Steps done */
		theStatus.setStepsDone(pStepsDone);
		
		/* Create a new Status */
		myStatus = new StatusData(theStatus);
		
		/* Publish it */
		theThread.publish(myStatus);
		
		/* Return to caller */
		return true;
	}
}
