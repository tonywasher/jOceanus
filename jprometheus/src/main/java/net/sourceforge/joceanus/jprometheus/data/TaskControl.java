/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.logging.Logger;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;

/**
 * Status control interface for use by threads to report status and to detect cancellation.
 * @author Tony Washer
 * @param <T> the dataset type
 */
public interface TaskControl<T extends DataSet<T, ?>> {
    /**
     * Initialise task.
     * @param pTask task name
     * @return continue true/false
     */
    boolean initTask(final String pTask);

    /**
     * Set Number of stages.
     * @param pNumStages number of stages
     * @return continue true/false
     */
    boolean setNumStages(final int pNumStages);

    /**
     * Set New stage.
     * @param pStage the stage
     * @return continue true/false
     */
    boolean setNewStage(final String pStage);

    /**
     * Set Number of steps in this stage.
     * @param pNumSteps number of steps
     * @return continue true/false
     */
    boolean setNumSteps(final int pNumSteps);

    /**
     * Set Number of steps completed.
     * @param pStepsDone steps completed
     * @return continue true/false
     */
    boolean setStepsDone(final int pStepsDone);

    /**
     * Get the number of reporting steps.
     * @return the number of steps
     */
    int getReportingSteps();

    /**
     * Obtain a new, empty DataSet.
     * @return the new DataSet
     */
    T getNewDataSet();

    /**
     * Access the security manager.
     * @return the security manager
     */
    SecureManager getSecurity();

    /**
     * Access the data formatter.
     * @return the data formatter
     */
    JDataFormatter getDataFormatter();

    /**
     * Is the task cancelled?
     * @return true/false
     */
    boolean isCancelled();

    /**
     * Obtain logger.
     * @return the logger
     */
    Logger getLogger();
}
