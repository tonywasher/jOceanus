/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.threads.javafx;

import javafx.concurrent.Task;
import javafx.scene.Node;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThread;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * javaFX Thread wrapper.
 * @param <T> the thread result
 */
public class MetisFXThread<T>
        extends Task<Integer> {
    /**
     * The Toolkit.
     */
    private final MetisFXToolkit theToolkit;

    /**
     * The wrapped thread.
     */
    private final MetisThread<T, Node, Node> theThread;

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
     * @param pToolkit the toolkit
     * @param pThread the thread to wrap
     */
    protected MetisFXThread(final MetisFXToolkit pToolkit,
                            final MetisThread<T, Node, Node> pThread) {
        /* Store parameters */
        theToolkit = pToolkit;
        theThread = pThread;
        theTask = pThread.getTaskName();
    }

    /**
     * obtain the task name.
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
        theResult = theThread.performTask(theToolkit);
        return null;
    }

    /**
     * Process the result.
     * @throws OceanusException on error
     */
    protected void processResult() throws OceanusException {
        theThread.processResult(theResult);
    }

    /**
     * publish the status.
     */
    protected void publishStatus() {
        /* Increment the counter and publish it */
        theCounter++;
        updateValue(theCounter);
    }
}
