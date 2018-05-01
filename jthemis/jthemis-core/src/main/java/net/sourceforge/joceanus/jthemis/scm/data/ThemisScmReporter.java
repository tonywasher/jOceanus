/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jthemis.scm.data;

import javax.swing.JFrame;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jthemis.scm.tasks.ThemisScmStatus;

/**
 * Report Subversion events.
 * @author Tony Washer
 */
public final class ThemisScmReporter {
    /**
     * Private constructor.
     */
    private ThemisScmReporter() {
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
        boolean initTask(String pTask);

        /**
         * Set number of stages.
         * @param pNumStages the number of stages
         * @return continue? true/false
         */
        boolean setNumStages(int pNumStages);

        /**
         * Set new stage.
         * @param pStage the new stage
         * @return continue? true/false
         */
        boolean setNewStage(String pStage);

        /**
         * Set number of steps.
         * @param pNumSteps the number of steps
         * @return continue? true/false
         */
        boolean setNumSteps(int pNumSteps);

        /**
         * Set new step.
         * @param pStep the new step
         * @return continue? true/false
         */
        boolean setNewStep(String pStep);

        /**
         * Set number of steps done .
         * @param pNumSteps the number of steps
         * @return continue? true/false
         */
        boolean setStepsDone(int pNumSteps);

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
         * Obtain preference manager.
         * @return the preference manager
         */
        MetisPreferenceManager getPreferenceMgr();

        /**
         * Obtain secure manager.
         * @return the secure manager
         */
        GordianHashManager getSecureMgr();

        /**
         * Obtain frame.
         * @return the frame
         */
        JFrame getFrame();

        /**
         * Set new status for thread.
         * @param pStatus the new status.
         */
        void setNewStatus(ThemisScmStatus pStatus);

        /**
         * Complete Task.
         * @param pTask the task that has completed.
         */
        void completeTask(Object pTask);
    }
}
