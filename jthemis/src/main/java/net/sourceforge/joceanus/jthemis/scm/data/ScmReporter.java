/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.scm.data;

import net.sourceforge.joceanus.jthemis.scm.tasks.ScmStatus;

/**
 * Report Subversion events.
 * @author Tony Washer
 */
public final class ScmReporter {
    /**
     * Private constructor.
     */
    private ScmReporter() {
    }

    /**
     * Report status.
     */
    public interface ReportStatus {
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
         * Set new step.
         * @param pStep the new step
         * @return continue? true/false
         */
        boolean setNewStep(final String pStep);

        /**
         * Is the task cancelled?
         * @return true/false
         */
        boolean isCancelled();
    }

    /**
     * Report Task.
     */
    public interface ReportTask {
        /**
         * Set new status for thread.
         * @param pStatus the new status.
         */
        void setNewStatus(final ScmStatus pStatus);

        /**
         * Complete Task.
         * @param pTask the task that has completed.
         */
        void completeTask(final Object pTask);
    }
}
