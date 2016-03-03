/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.threads;

/**
 * Thread Status.
 */
public class MetisThreadStatus {
    /**
     * Default Number of steps/stages.
     */
    private static final int DEFAULT_NUMBER = 100;

    /**
     * Number of steps.
     */
    private int theNumSteps;

    /**
     * Steps performed.
     */
    private int theStepsDone;

    /**
     * Number of stages.
     */
    private int theNumStages;

    /**
     * Stages performed.
     */
    private int theStagesDone;

    /**
     * Current stage.
     */
    private String theStage;

    /**
     * Current task.
     */
    private String theTask;

    /**
     * Constructor.
     */
    public MetisThreadStatus() {
        setTask(null);
    }

    /**
     * Constructor.
     * @param pStatus the source status
     */
    public MetisThreadStatus(final MetisThreadStatus pStatus) {
        theNumSteps = pStatus.getNumSteps();
        theNumStages = pStatus.getNumStages();
        theStepsDone = pStatus.getStepsDone();
        theStagesDone = pStatus.getStagesDone();
        theStage = pStatus.getStage();
        theTask = pStatus.getTask();
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
     * Get stage progress done.
     * @return stage progress
     */
    public double getStageProgress() {
        return ((double) theStepsDone) / theNumSteps;
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
     * Get task progress done.
     * @return task progress
     */
    public double getTaskProgress() {
        return ((double) theStagesDone) / theNumStages;
    }

    /**
     * Get name of stage.
     * @return name of stage
     */
    public String getStage() {
        return theStage;
    }

    /**
     * Get name of task.
     * @return name of task
     */
    public String getTask() {
        return theTask;
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
     * Set Next step.
     */
    public void setNextStep() {
        theStepsDone++;
    }

    /**
     * Set Number of stages in this task.
     * @param pValue the value
     */
    public void setNumStages(final int pValue) {
        theNumStages = pValue;
        theStagesDone = -1;
    }

    /**
     * Set Number of stages completed in this task.
     * @param pValue the value
     */
    public void setStagesDone(final int pValue) {
        theStagesDone = pValue;
    }

    /**
     * Set name of stage in this task.
     * @param pValue the value
     */
    public void setStage(final String pValue) {
        theStage = pValue;
        theStagesDone++;
        theNumSteps = DEFAULT_NUMBER;
        theStepsDone = -1;
    }

    /**
     * Set Name of task.
     * @param pValue the value
     */
    public void setTask(final String pValue) {
        theTask = pValue;
        theStage = null;
        theNumStages = DEFAULT_NUMBER;
        theStagesDone = -1;
        theNumSteps = DEFAULT_NUMBER;
        theStepsDone = -1;
    }
}
