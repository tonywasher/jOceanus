package uk.co.tolcroft.models.threads;

import uk.co.tolcroft.models.Utils;

public class StatusData {
	/* Properties */
	private int	   theNumSteps   = 100;
	private int    theStepsDone  = 0;
	private int    theNumStages  = 100;
	private int    theStagesDone = 0;
	private String theStage		 = "";
	private String theTask		 = "";
	
	/* Access methods */
	public int	  getNumSteps()   { return theNumSteps; }
	public int	  getStepsDone()  { return theStepsDone; }
	public int	  getNumStages()  { return theNumStages; }
	public int	  getStagesDone() { return theStagesDone; }
	public String getStage()      { return theStage; }
	public String getTask()       { return theTask; }

	/* Set fields */
	public void setNumSteps(int pValue)		{ theNumSteps = pValue; }
	public void setStepsDone(int pValue)  	{ theStepsDone = pValue; }
	public void setNumStages(int pValue ) 	{ theNumStages = pValue; }
	public void setStagesDone(int pValue) 	{ theStagesDone = pValue; }
	public void setStage(String pValue)   	{ theStage = pValue; }
	public void	setTask(String pValue)    	{ theTask = pValue; }

	/* Difference testers */
	public boolean differNumSteps(StatusData pData) 	{ return (pData == null) || (theNumSteps   != pData.theNumSteps); }
	public boolean differNumStages(StatusData pData) 	{ return (pData == null) || (theNumStages  != pData.theNumStages); }
	public boolean differStepsDone(StatusData pData) 	{ return (pData == null) || (theStepsDone  != pData.theStepsDone); }
	public boolean differStagesDone(StatusData pData)	{ return (pData == null) || (theStagesDone != pData.theStagesDone); }
	public boolean differStage(StatusData pData) 		{ return (pData == null) || Utils.differs(theStage, pData.theStage).isDifferent(); }
	public boolean differTask(StatusData pData) 		{ return (pData == null) || Utils.differs(theTask,  pData.theTask).isDifferent(); }

	/* Constructors */
	public StatusData() {}
	public StatusData(StatusData pStatus) {
		theNumSteps   = pStatus.theNumSteps;
		theNumStages  = pStatus.theNumStages;
		theStepsDone  = pStatus.theStepsDone;
		theStagesDone = pStatus.theStagesDone;
		theStage      = pStatus.theStage;
		theTask       = pStatus.theTask;
	}
}