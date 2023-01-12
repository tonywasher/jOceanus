/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.thread;

import javafx.concurrent.Task;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * javaFX Thread wrapper.
 *
 * @param <T> the thread result
 */
public class TethysUIFXThread<T>
        extends Task<Integer> {
    /**
     * The ThreadManager.
     */
    private final TethysUIThreadManager theManager;

    /**
     * The wrapped thread.
     */
    private final TethysUIThread<T> theThread;

    /**
     * The task name.
     */
    private final String theTask;

    /**
     * The status counter.
     */
    private int theCounter;

    /**
     * The result.
     */
    private T theResult;

    /**
     * Constructor.
     *
     * @param pManager the threadManager
     * @param pThread  the thread to wrap
     */
    TethysUIFXThread(final TethysUIThreadManager pManager,
                     final TethysUIThread<T> pThread) {
        /* Store parameters */
        theManager = pManager;
        theThread = pThread;
        theTask = pThread.getTaskName();
    }

    /**
     * obtain the task name.
     *
     * @return the task name
     */
    String getTaskName() {
        return theTask;
    }

    /**
     * Interrupt for cancel.
     */
    void interruptForCancel() {
        theThread.interruptForCancel();
    }

    @Override
    protected Integer call() throws Exception {
        theResult = theThread.performTask(theManager);
        return null;
    }

    /**
     * Process the result.
     *
     * @throws OceanusException on error
     */
    void processResult() throws OceanusException {
        theThread.processResult(theResult);
    }

    /**
     * publish the status.
     */
    void publishStatus() {
        /* Increment the counter and publish it */
        theCounter++;
        updateValue(theCounter);
    }
}
