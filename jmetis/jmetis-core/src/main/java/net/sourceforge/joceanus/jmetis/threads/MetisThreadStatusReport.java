/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.threads;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Report status.
 */
public interface MetisThreadStatusReport {
    /**
     * Initialise Task.
     * @param pTask the task
     * @throws OceanusException on cancellation
     */
    void initTask(final String pTask) throws OceanusException;

    /**
     * Set number of stages.
     * @param pNumStages the number of stages
     * @throws OceanusException on cancellation
     */
    void setNumStages(final int pNumStages) throws OceanusException;

    /**
     * Set new stage.
     * @param pStage the new stage
     * @throws OceanusException on cancellation
     */
    void setNewStage(final String pStage) throws OceanusException;

    /**
     * Set number of steps.
     * @param pNumSteps the number of steps
     * @throws OceanusException on cancellation
     */
    void setNumSteps(final int pNumSteps) throws OceanusException;

    /**
     * Set steps done.
     * @param pSteps the number of steps done
     * @throws OceanusException on cancellation
     */
    void setStepsDone(final int pSteps) throws OceanusException;

    /**
     * Set next step.
     * @throws OceanusException on cancellation
     */
    void setNextStep() throws OceanusException;

    /**
     * Set next step.
     * @param pStep the step to set
     * @throws OceanusException on cancellation
     */
    void setNextStep(final String pStep) throws OceanusException;

    /**
     * Set Completion.
     * @throws OceanusException on cancellation
     */
    void setCompletion() throws OceanusException;

    /**
     * Is the task cancelled?
     * @throws OceanusException on cancellation
     */
    void checkForCancellation() throws OceanusException;

    /**
     * Throw cancellation exception.
     * @throws OceanusException on error
     */
    void throwCancelException() throws OceanusException;

    /**
     * Obtain the active task.
     * @return the active task
     */
    MetisProfile getActiveTask();
}
