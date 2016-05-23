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

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Metis Thread.
 * @param <T> the thread result
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public interface MetisThread<T, N, I> {
    /**
     * obtain the task name.
     * @return the task name
     */
    String getTaskName();

    /**
     * Perform the task.
     * @param pToolkit the toolkit
     * @return the result
     * @throws OceanusException on error
     */
    T performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException;

    /**
     * process result.
     * @param pResult the result
     * @throws OceanusException on error
     */
    void processResult(final T pResult) throws OceanusException;

    /**
     * interruptForCancel.
     * <p>
     * Hook to allow threads on a long running read to close the file and force an interrupt
     */
    default void interruptForCancel() {
        /*
         * Overridden as needed
         */
    }
}
