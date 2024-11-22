/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.ui.api.thread;

/**
 * Thread Status.
 */
public interface TethysUIThreadStatus {
    /**
     * Get step.
     * @return the step name
     */
    String getStep();

    /**
     * Get number of steps.
     * @return number of steps
     */
    int getNumSteps();

    /**
     * Get number of steps done.
     * @return number of steps done
     */
    int getStepsDone();

    /**
     * Get stage progress done.
     * @return stage progress
     */
    double getStageProgress();

    /**
     * Get number of stages.
     * @return number of stages
     */
    int getNumStages();

    /**
     * Get number of stages done.
     * @return number of stages done
     */
    int getStagesDone();

    /**
     * Get task progress done.
     * @return task progress
     */
    double getTaskProgress();

    /**
     * Get name of stage.
     * @return name of stage
     */
    String getStage();

    /**
     * Get name of task.
     * @return name of task
     */
    String getTask();

    /**
     * Set Number of steps in this stage.
     * @param pValue the value
     */
    void setNumSteps(int pValue);

    /**
     * Set Next step.
     * @param pStep the next step
     */
    void setNextStep(String pStep);

    /**
     * Set StepsDone.
     * @param pSteps the # of steps done
     */
    void setStepsDone(int pSteps);

    /**
     * Set Next step.
     */
    void setNextStep();

    /**
     * Set Number of stages in this task.
     * @param pValue the value
     */
    void setNumStages(int pValue);

    /**
     * Set Number of stages completed in this task.
     * @param pValue the value
     */
    void setStagesDone(int pValue);

    /**
     * Set name of stage in this task.
     * @param pValue the value
     */
    void setStage(String pValue);

    /**
     * Set Name of task.
     * @param pValue the value
     */
    void setTask(String pValue);

    /**
     * Set Completion.
     */
    void setCompletion();
}
