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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.concurrent.Task;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysThread;
import net.sourceforge.joceanus.jtethys.ui.TethysThreadData;

/**
 * javaFX Thread wrapper.
 *
 * @param <T> the thread result
 */
public class TethysFXThread<T>
        extends Task<Integer> {
    /**
     * The ThreadData.
     */
    private final TethysThreadData theThreadData;

    /**
     * The wrapped thread.
     */
    private final TethysThread<T> theThread;

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
     * @param pThreadData the threadData
     * @param pThread  the thread to wrap
     */
    TethysFXThread(final TethysThreadData pThreadData,
                   final TethysThread<T> pThread) {
        /* Store parameters */
        theThreadData = pThreadData;
        theThread = pThread;
        theTask = pThread.getTaskName();
    }

    /**
     * obtain the task name.
     *
     * @return the task name
     */
    protected String getTaskName() {
        return theTask;
    }

    /**
     * Interrupt for cancel.
     */
    protected void interruptForCancel() {
        theThread.interruptForCancel();
    }

    @Override
    protected Integer call() throws Exception {
        theResult = theThread.performTask(theThreadData);
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
