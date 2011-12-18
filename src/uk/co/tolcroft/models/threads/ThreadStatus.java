package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.PropertySet;
import uk.co.tolcroft.models.PropertySet.PropertyManager;
import uk.co.tolcroft.models.PropertySet.PropertySetChooser;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public class ThreadStatus<T extends DataSet<T>> implements StatusControl,
														   PropertySetChooser {
	private WorkerThread<?>	theThread		= null;
	private StatusData 		theStatus		= null;
	private DataControl<T>	theControl		= null;
	private int          	theSteps;
	
	/**
	 * ThreadStatus Properties
	 */
	private ThreadStatusProperties	theTProperties	= null;
	
	/* Access methods */
	public int 				getReportingSteps() { return theSteps; }
	public DataControl<T>	getControl() 		{ return theControl; }
	public boolean			isCancelled() 		{ return theThread.isCancelled(); }
		
	/* Constructor */
	public ThreadStatus(WorkerThread<?>	pThread,
			         	DataControl<T>	pControl) {
		/* Store parameter */
		theThread 		= pThread;
		theControl		= pControl;
		
		/* Access the threadStatus properties */
		theTProperties 	= (ThreadStatusProperties)PropertyManager.getPropertySet(this);
		theSteps		= theTProperties.getIntegerValue(ThreadStatusProperties.nameRepSteps);
		
		/* Create the status */
		theStatus = new StatusData();
	}
	
	@Override
	public Class<? extends PropertySet> getPropertySetClass() { return ThreadStatusProperties.class; }
	
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
	
	/**
	 * ThreadStatus Properties
	 */
	public static class ThreadStatusProperties extends PropertySet {
		/**
		 * Registry name for Reporting Steps
		 */
		protected final static String 	nameRepSteps	= "ReportingSteps";

		/**
		 * Display name for Reporting Steps
		 */
		protected final static String 	dispRepSteps	= "Reporting Steps";

		/**
		 * Default Reporting Steps
		 */
		private final static Integer	defRepSteps		= 10;		

		/**
		 * Constructor
		 * @throws ModelException
		 */
		public ThreadStatusProperties() throws ModelException { super();	}

		@Override
		protected void defineProperties() {
			/* Define the properties */
			defineProperty(nameRepSteps, PropertyType.Integer);
		}

		@Override
		protected Object getDefaultValue(String pName) {
			/* Handle default values */
			if (pName.equals(nameRepSteps))	return defRepSteps;
			return null;
		}
		
		@Override
		protected String getDisplayName(String pName) {
			/* Handle default values */
			if (pName.equals(nameRepSteps)) 	return dispRepSteps;
			return null;
		}
	}	
}
