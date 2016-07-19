/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jthemis.scm.tasks;

/**
 * Status for a thread operation.
 */
public class ThemisScmStatus {
    /**
     * The task.
     */
    private String theTask;

    /**
     * The number of stages.
     */
    private int theNumStages;

    /**
     * The number of stages done.
     */
    private int theStagesDone;

    /**
     * The active stage.
     */
    private String theStage;

    /**
     * The number of steps.
     */
    private int theNumSteps;

    /**
     * The number of steps done.
     */
    private int theStepsDone;

    /**
     * The active step.
     */
    private String theStep;

    /**
     * Constructor.
     */
    public ThemisScmStatus() {
    }

    /**
     * Constructor.
     * @param pStatus the source status
     */
    public ThemisScmStatus(final ThemisScmStatus pStatus) {
        theNumSteps = pStatus.getNumSteps();
        theNumStages = pStatus.getNumStages();
        theStepsDone = pStatus.getStepsDone();
        theStagesDone = pStatus.getStagesDone();
        theTask = pStatus.getTask();
        theStage = pStatus.getStage();
        theStep = pStatus.getStep();
    }

    /**
     * Get current task.
     * @return the task
     */
    public String getTask() {
        return theTask;
    }

    /**
     * Get current stage.
     * @return the stage
     */
    public String getStage() {
        return theStage;
    }

    /**
     * Get current step.
     * @return the step
     */
    public String getStep() {
        return theStep;
    }

    /**
     * Get number of stages.
     * @return number of stages
     */
    public int getNumStages() {
        return theNumStages;
    }

    /**
     * Get number of stages done.
     * @return number of stages done
     */
    public int getStagesDone() {
        return theStagesDone;
    }

    /**
     * Get number of steps.
     * @return number of steps
     */
    public int getNumSteps() {
        return theNumSteps;
    }

    /**
     * Get number of steps done.
     * @return number of steps done
     */
    public int getStepsDone() {
        return theStepsDone;
    }

    /**
     * Set Name of task.
     * @param pValue the value
     */
    public void setTask(final String pValue) {
        theTask = pValue;
        theStagesDone = -1;
        theStepsDone = -1;
    }

    /**
     * Set Number of stages in this task.
     * @param pValue the value
     */
    public void setNumStages(final int pValue) {
        theNumStages = pValue;
        theStagesDone = -1;
        theStepsDone = -1;
    }

    /**
     * Set new Stage.
     * @param pValue the value
     */
    public void setNewStage(final String pValue) {
        theStage = pValue;
        theStagesDone++;
        theStepsDone++;
    }

    /**
     * Set Number of steps in this stage.
     * @param pValue the value
     */
    public void setNumSteps(final int pValue) {
        theNumSteps = pValue;
        theStepsDone = -1;
    }

    /**
     * Set new Step.
     * @param pValue the value
     */
    public void setNewStep(final String pValue) {
        theStep = pValue;
        theStepsDone++;
    }

    /**
     * Set number of steps done.
     * @param pDone the value
     */
    public void setStepsDone(final int pDone) {
        theStepsDone = pDone;
    }
}
