/*******************************************************************************
 * JDataModel: Data models
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

import net.sourceforge.JDataManager.Difference;

/**
 * Status Data object that is used to report the status of a thread.
 * @author Tony Washer
 */
public class StatusData {
    /**
     * Default Number of steps/stages.
     */
    private static final int DEFAULT_NUMBER = 100;

    /**
     * Number of steps.
     */
    private int theNumSteps = DEFAULT_NUMBER;

    /**
     * Steps performed.
     */
    private int theStepsDone = 0;

    /**
     * Number of stages.
     */
    private int theNumStages = DEFAULT_NUMBER;

    /**
     * Stages performed.
     */
    private int theStagesDone = 0;

    /**
     * Current stage.
     */
    private String theStage = "";

    /**
     * Current task.
     */
    private String theTask = "";

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
    }

    /**
     * Set Number of steps completed in this stage.
     * @param pValue the value
     */
    public void setStepsDone(final int pValue) {
        theStepsDone = pValue;
    }

    /**
     * Set Number of stages in this task.
     * @param pValue the value
     */
    public void setNumStages(final int pValue) {
        theNumStages = pValue;
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
    }

    /**
     * Set Name of task.
     * @param pValue the value
     */
    public void setTask(final String pValue) {
        theTask = pValue;
    }

    /**
     * Has the number of steps changed?
     * @param pData the new status
     * @return true/false
     */
    public boolean differNumSteps(final StatusData pData) {
        return (pData == null) || (theNumSteps != pData.getNumSteps());
    }

    /**
     * Has the number of stages changed?
     * @param pData the new status
     * @return true/false
     */
    public boolean differNumStages(final StatusData pData) {
        return (pData == null) || (theNumStages != pData.getNumStages());
    }

    /**
     * Has the number of steps completed changed?
     * @param pData the new status
     * @return true/false
     */
    public boolean differStepsDone(final StatusData pData) {
        return (pData == null) || (theStepsDone != pData.getStepsDone());
    }

    /**
     * Has the number of stages completed changed?
     * @param pData the new status
     * @return true/false
     */
    public boolean differStagesDone(final StatusData pData) {
        return (pData == null) || (theStagesDone != pData.getStagesDone());
    }

    /**
     * Has the stage name changed?
     * @param pData the new status
     * @return true/false
     */
    public boolean differStage(final StatusData pData) {
        return (pData == null) || !Difference.isEqual(theStage, pData.getStage());
    }

    /**
     * Has the task name changed?
     * @param pData the new status
     * @return true/false
     */
    public boolean differTask(final StatusData pData) {
        return (pData == null) || !Difference.isEqual(theTask, pData.getTask());
    }

    /**
     * Constructor.
     */
    public StatusData() {
    }

    /**
     * Constructor.
     * @param pStatus the source status
     */
    public StatusData(final StatusData pStatus) {
        theNumSteps = pStatus.getNumSteps();
        theNumStages = pStatus.getNumStages();
        theStepsDone = pStatus.getStepsDone();
        theStagesDone = pStatus.getStagesDone();
        theStage = pStatus.getStage();
        theTask = pStatus.getTask();
    }
}
