package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.finance.data.Properties;
import uk.co.tolcroft.models.data.DataSet;

public class ThreadStatus<T extends DataSet<?>> implements StatusControl {
	/* Status Data regarding a thread */
	protected static class StatusData {
		/* Properties */
		private int	   theNumSteps   = 0;;
		private int    theStepsDone  = 0;
		private int    theNumStages  = 0;
		private int    theStagesDone = 0;;
		private String theStage		 = null;
		
		/* Access methods */
		public int	  getNumSteps()   { return theNumSteps; }
		public int	  getStepsDone()  { return theStepsDone; }
		public int	  getNumStages()  { return theNumStages; }
		public int	  getStagesDone() { return theStagesDone; }
		public String getStage()      { return theStage; }
		
		/* Constructors */
		public StatusData() {}
		public StatusData(StatusData pStatus) {
			theNumSteps   = pStatus.theNumSteps;
			theNumStages  = pStatus.theNumStages;
			theStepsDone  = pStatus.theStepsDone;
			theStagesDone = pStatus.theStagesDone;
			theStage      = pStatus.theStage;
		}
	}

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
	public boolean setNumStages(int pNumStages) {
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Set number of Stages and set Stages done to -1 */
		theStatus.theNumStages  = pNumStages;
		theStatus.theStagesDone = -1;
		
		/* Return to caller */
		return true;
	}
	
	/* Publish task 1 (Worker Thread)*/
	public boolean setNewStage(String pStage) {
		StatusData myStatus;
		
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Store the stage and increment stages done */
		theStatus.theStage = pStage;
		theStatus.theStagesDone++;
		theStatus.theNumSteps  = 100;
		if (theStatus.theStagesDone < theStatus.theNumStages) 
			theStatus.theStepsDone = 0;
		else 
			theStatus.theStepsDone = 100;
		
		/* Create a new Status */
		myStatus = new StatusData(theStatus);
		
		/* Publish it */
		theThread.publish(myStatus);
		
		/* Return to caller */
		return true;
	}
	
	/* Publish task 2 (Worker Thread)*/
	public boolean setNumSteps(int pNumSteps) {
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Set number of Steps */
		theStatus.theNumSteps  = pNumSteps;
		
		/* Return to caller */
		return true;
	}
	
	/* Publish task 3 (Worker Thread)*/
	public boolean setStepsDone(int pStepsDone) {
		StatusData myStatus;
		
		/* Check for cancellation */
		if (theThread.isCancelled()) return false;
		
		/* Set Steps done */
		theStatus.theStepsDone  = pStepsDone;
		
		/* Create a new Status */
		myStatus = new StatusData(theStatus);
		
		/* Publish it */
		theThread.publish(myStatus);
		
		/* Return to caller */
		return true;
	}
}