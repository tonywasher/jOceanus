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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/sheet/MetisWorkBookType.java $
 * $Revision: 655 $
 * $Author: Tony $
 * $Date: 2015-12-02 14:34:04 +0000 (Wed, 02 Dec 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads.javafx;

import javafx.concurrent.Task;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * javaFX Thread wrapper.
 * @param <T> the thread result
 */
public class MetisFXThread<T>
        extends Task<Integer> {
    /**
     * The ThreadManager.
     */
    private final MetisFXThreadManager theManager;

    /**
     * The wrapped thread.
     */
    private final MetisThread<T> theThread;

    /**
     * The task name.
     */
    private final String theTask;

    /**
     * The status counter.
     */
    private Integer theCounter;

    /**
     * The result.
     */
    private T theResult;

    /**
     * Constructor.
     * @param pManager the thread manager
     * @param pThread the thread to wrap
     */
    protected MetisFXThread(final MetisFXThreadManager pManager,
                            final MetisThread<T> pThread) {
        /* Store parameters */
        theManager = pManager;
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
        theResult = theThread.performTask(theManager);
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
