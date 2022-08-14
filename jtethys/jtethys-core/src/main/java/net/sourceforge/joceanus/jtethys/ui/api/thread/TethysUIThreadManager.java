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
package net.sourceforge.joceanus.jtethys.ui.api.thread;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;

/**
 * Thread Manager.
 */
public interface TethysUIThreadManager
        extends TethysEventProvider<TethysUIThreadEvent> {
    /**
     * obtain the task name.
     * @return the task name
     */
    String getTaskName();

    /**
     * Do we have a running thread.
     * @return true/false
     */
    boolean hasWorker();

    /**
     * Set the thread Data.
     * @param pSteps the reporting steps
     */
    void setReportingSteps(int pSteps);

    /**
     * Get error.
     * @return the error
     */
    Throwable getError();

    /**
     * Set the thread Data.
     * @param pThreadData the threadData
     */
    void setThreadData(TethysUIThreadData pThreadData);

    /**
     * Start a thread.
     * @param <T> the thread result
     * @param pThread the thread to start
     */
    <T> void startThread(TethysUIThread<T> pThread);

    /**
     * Shut down the thread manager.
     */
    void shutdown();

    /**
     * cancel the worker.
     */
    void cancelWorker();

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    TethysProfile getActiveProfile();
}