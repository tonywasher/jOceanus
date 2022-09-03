/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Thread Status.
 */
public class TethysThreadStatus {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysThreadStatus.class);

    /**
     * Default Number of steps/stages.
     */
    private static final int DEFAULT_NUMBER = 100;

    /**
     * Step name.
     */
    private String theStep;

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
    public TethysThreadStatus() {
        setTask(null);
    }

    /**
     * Constructor.
     * @param pStatus the source status
     */
    public TethysThreadStatus(final TethysThreadStatus pStatus) {
        theNumSteps = pStatus.getNumSteps();
        theNumStages = pStatus.getNumStages();
        theStepsDone = pStatus.getStepsDone();
        theStagesDone = pStatus.getStagesDone();
        theStep = pStatus.getStep();
        theStage = pStatus.getStage();
        theTask = pStatus.getTask();
    }

    /**
     * Get step.
     * @return the step name
     */
    public String getStep() {
        return theStep;
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
     * @param pStep the next step
     */
    public void setNextStep(final String pStep) {
        theStep = pStep;
        theStepsDone++;
    }

    /**
     * Set StepsDone.
     * @param pSteps the # of steps done
     */
    public void setStepsDone(final int pSteps) {
        theStepsDone += pSteps;
    }

    /**
     * Set Next step.
     */
    public void setNextStep() {
        setNextStep(null);
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
        theStep = null;
        theNumSteps = DEFAULT_NUMBER;
        theStepsDone = -1;
    }

    /**
     * Set Name of task.
     * @param pValue the value
     */
    public void setTask(final String pValue) {
        theTask = pValue;
        theStep = null;
        theStage = null;
        theNumStages = DEFAULT_NUMBER;
        theStagesDone = -1;
        theNumSteps = DEFAULT_NUMBER;
        theStepsDone = -1;
    }

    /**
     * Set Completion.
     */
    public void setCompletion() {
        theStagesDone++;
        theNumSteps = -1;
        theStepsDone = -1;
        if (theTask != null
                && theStagesDone != 0
                && theStagesDone != theNumStages) {
            LOGGER.info("Incorrect # of stages for Task: <%s> (%d/%d)", theTask, theStagesDone, theNumStages);
        }
    }
}
