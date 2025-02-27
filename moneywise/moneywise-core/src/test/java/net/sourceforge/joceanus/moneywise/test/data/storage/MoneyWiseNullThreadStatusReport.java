/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.test.data.storage;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * Report stub.
 */
public class MoneyWiseNullThreadStatusReport
        implements TethysUIThreadStatusReport {
    /**
     * The active task.
     */
    private final OceanusProfile theProfile;

    /**
     * Constructor.
     */
    MoneyWiseNullThreadStatusReport() {
        theProfile = new OceanusProfile("Dummy");
    }

    @Override
    public void initTask(String pTask) throws OceanusException {
    }

    @Override
    public void setNumStages(int pNumStages) throws OceanusException {
    }

    @Override
    public void setNewStage(String pStage) throws OceanusException {
    }

    @Override
    public void setNumSteps(int pNumSteps) throws OceanusException {
    }

    @Override
    public void setStepsDone(int pSteps) throws OceanusException {
    }

    @Override
    public void setNextStep() throws OceanusException {
    }

    @Override
    public void setNextStep(String pStep) throws OceanusException {
    }

    @Override
    public void setCompletion() throws OceanusException {
    }

    @Override
    public void checkForCancellation() throws OceanusException {
    }

    @Override
    public void throwCancelException() throws OceanusException {
    }

    @Override
    public OceanusProfile getActiveTask() {
        return theProfile;
    }
}
