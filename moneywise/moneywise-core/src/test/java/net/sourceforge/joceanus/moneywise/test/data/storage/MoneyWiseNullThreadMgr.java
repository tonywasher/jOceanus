/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.test.data.storage;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusManager;

/**
 * ThreadManager stub.
 */
public class MoneyWiseNullThreadMgr
        implements TethysUIThreadManager {
    /**
     * The active task.
     */
    private OceanusProfile theProfile;

    /**
     * Constructor.
     */
    public MoneyWiseNullThreadMgr() {
        setNewProfile("Dummy");
    }

    @Override
    public void setNewProfile(final String pTask) {
        theProfile = new OceanusProfile(pTask);
    }

    @Override
    public OceanusEventRegistrar<TethysUIThreadEvent> getEventRegistrar() {
        return null;
    }

    @Override
    public TethysUIThreadStatusManager getStatusManager() {
        return null;
    }

    @Override
    public String getTaskName() {
        return null;
    }

    @Override
    public boolean hasWorker() {
        return false;
    }

    @Override
    public void setReportingSteps(final int pSteps) {
    }

    @Override
    public Throwable getError() {
        return null;
    }

    @Override
    public void setThreadData(final Object pThreadData) {
    }

    @Override
    public Object getThreadData() {
        return null;
    }

    @Override
    public <T> void startThread(final TethysUIThread<T> pThread) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void cancelWorker() {
    }

    @Override
    public OceanusProfile getActiveProfile() {
        return null;
    }

    @Override
    public void initTask(final String pTask) {
    }

    @Override
    public void setNumStages(final int pNumStages) {
    }

    @Override
    public void setNewStage(final String pStage) {
    }

    @Override
    public void setNumSteps(final int pNumSteps) {
    }

    @Override
    public void setStepsDone(final int pSteps) {
    }

    @Override
    public void setNextStep() {
    }

    @Override
    public void setNextStep(final String pStep) {
    }

    @Override
    public void setCompletion() {
    }

    @Override
    public void checkForCancellation() {
    }

    @Override
    public void throwCancelException() {
    }

    @Override
    public OceanusProfile getActiveTask() {
        return theProfile;
    }
}
