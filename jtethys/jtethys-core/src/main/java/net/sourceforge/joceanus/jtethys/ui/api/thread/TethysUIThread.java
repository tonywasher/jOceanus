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
package net.sourceforge.joceanus.jtethys.ui.api.thread;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Metis Thread.
 * @param <T> the thread result
 */
public interface TethysUIThread<T> {
    /**
     * obtain the task name.
     * @return the task name
     */
    String getTaskName();

    /**
     * prepare task.
     * @param pManager the thread manager
     * @throws OceanusException on error
     */
    default void prepareTask(final TethysUIThreadManager pManager) throws OceanusException {
        /*
         * Overridden as needed
         */
    }

    /**
     * Perform the task.
     * @param pManager the thread manager
     * @return the result
     * @throws OceanusException on error
     */
    T performTask(TethysUIThreadManager pManager) throws OceanusException;

    /**
     * process result.
     * @param pResult the result
     * @throws OceanusException on error
     */
    default void processResult(final T pResult) throws OceanusException {
        /*
         * Overridden as needed
         */
    }

    /**
     * interruptForCancel.
     * <p>
     * Hook to allow threads on a long-running read to close the file and force an interrupt
     */
    default void interruptForCancel() {
        /*
         * Overridden as needed
         */
    }
}

