package uk.co.tolcroft.models.threads;

public interface StatusControl {
	public boolean initTask(String pTask);
	public boolean setNumStages(int pNumStages);
	public boolean setNewStage(String pStage);
	public boolean setNumSteps(int pNumSteps);
	public boolean setStepsDone(int pStepsDone);
}
