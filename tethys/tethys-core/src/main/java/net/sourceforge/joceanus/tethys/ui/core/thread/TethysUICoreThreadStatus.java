/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.core.thread;

import net.sourceforge.joceanus.tethys.logger.TethysLogManager;
import net.sourceforge.joceanus.tethys.logger.TethysLogger;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadStatus;

/**
 * Thread Status.
 */
public class TethysUICoreThreadStatus
    implements TethysUIThreadStatus {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysUICoreThreadStatus.class);

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
    public TethysUICoreThreadStatus() {
        setTask(null);
    }

    /**
     * Constructor.
     * @param pStatus the source status
     */
    public TethysUICoreThreadStatus(final TethysUICoreThreadStatus pStatus) {
        theNumSteps = pStatus.getNumSteps();
        theNumStages = pStatus.getNumStages();
        theStepsDone = pStatus.getStepsDone();
        theStagesDone = pStatus.getStagesDone();
        theStep = pStatus.getStep();
        theStage = pStatus.getStage();
        theTask = pStatus.getTask();
    }

    @Override
    public String getStep() {
        return theStep;
    }

    @Override
    public int getNumSteps() {
        return theNumSteps;
    }

    @Override
    public int getStepsDone() {
        return theStepsDone;
    }

    @Override
    public double getStageProgress() {
        return ((double) theStepsDone) / theNumSteps;
    }

    @Override
    public int getNumStages() {
        return theNumStages;
    }

    @Override
    public int getStagesDone() {
        return theStagesDone;
    }

    @Override
    public double getTaskProgress() {
        return ((double) theStagesDone) / theNumStages;
    }

    @Override
    public String getStage() {
        return theStage;
    }

    @Override
    public String getTask() {
        return theTask;
    }

    @Override
    public void setNumSteps(final int pValue) {
        theNumSteps = pValue;
        theStepsDone = -1;
    }

    @Override
    public void setNextStep(final String pStep) {
        theStep = pStep;
        theStepsDone++;
    }

    @Override
    public void setStepsDone(final int pSteps) {
        theStepsDone += pSteps;
    }

    @Override
    public void setNextStep() {
        setNextStep(null);
    }

    @Override
    public void setNumStages(final int pValue) {
        theNumStages = pValue;
        theStagesDone = -1;
    }

    @Override
    public void setStagesDone(final int pValue) {
        theStagesDone = pValue;
    }

    @Override
    public void setStage(final String pValue) {
        theStage = pValue;
        theStagesDone++;
        theStep = null;
        theNumSteps = DEFAULT_NUMBER;
        theStepsDone = -1;
    }

    @Override
    public void setTask(final String pValue) {
        theTask = pValue;
        theStep = null;
        theStage = null;
        theNumStages = DEFAULT_NUMBER;
        theStagesDone = -1;
        theNumSteps = DEFAULT_NUMBER;
        theStepsDone = -1;
    }

    @Override
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
