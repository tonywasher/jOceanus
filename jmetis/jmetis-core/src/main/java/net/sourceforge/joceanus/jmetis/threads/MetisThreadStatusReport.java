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

import net.sourceforge.joceanus.jmetis.data.MetisProfile;

/**
 * Report status.
 */
public interface MetisThreadStatusReport {
    /**
     * Initialise Task.
     * @param pTask the task
     * @return continue? true/false
     */
    boolean initTask(final String pTask);

    /**
     * Set number of stages.
     * @param pNumStages the number of stages
     * @return continue? true/false
     */
    boolean setNumStages(final int pNumStages);

    /**
     * Set new stage.
     * @param pStage the new stage
     * @return continue? true/false
     */
    boolean setNewStage(final String pStage);

    /**
     * Set number of steps.
     * @param pNumSteps the number of steps
     * @return continue? true/false
     */
    boolean setNumSteps(final int pNumSteps);

    /**
     * Set steps done.
     * @param pSteps the number of steps done
     * @return continue? true/false
     */
    boolean setStepsDone(final int pSteps);

    /**
     * Set next step.
     * @return continue true/false
     */
    boolean setNextStep();

    /**
     * Set next step.
     * @param pStep the step to set
     * @return continue true/false
     */
    boolean setNextStep(final String pStep);

    /**
     * Is the task cancelled?
     * @return true/false
     */
    boolean isCancelled();

    /**
     * Obtain the active task.
     * @return the active task
     */
    MetisProfile getActiveTask();
}
