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
package net.sourceforge.joceanus.jmetis.threads.swing;

import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisSwingViewerManager;

/**
 * Swing Thread manager.
 */
public class MetisSwingThreadManager
        extends MetisThreadManager<JComponent> {
    /**
     * The Active worker.
     */
    private MetisSwingThread<?> theWorker;

    /**
     * Constructor.
     * @param pViewerManager the viewer manager
     */
    public MetisSwingThreadManager(final MetisSwingViewerManager pViewerManager) {
        super(pViewerManager, new MetisSwingThreadStatusManager());
    }

    @Override
    public String getTaskName() {
        return theWorker == null
                                 ? null
                                 : theWorker.getTaskName();
    }

    @Override
    protected MetisSwingThreadStatusManager getStatusManager() {
        return (MetisSwingThreadStatusManager) super.getStatusManager();
    }

    @Override
    protected <T> Runnable wrapThread(final MetisThread<T> pThread) {
        /* Create the wrapped thread and listen to state transition */
        theWorker = new MetisSwingThread<>(this, pThread);

        /* Return the worker to the caller */
        return theWorker;
    }

    @Override
    protected void threadCompleted() {
        /* Remove reference */
        theWorker = null;

        /* Pass call on */
        super.threadCompleted();
    }

    @Override
    public boolean isCancelled() {
        return theWorker == null
               || theWorker.isCancelled();
    }

    @Override
    protected void publishStatus(final MetisThreadStatus pStatus) {
        if (!isCancelled()) {
            theWorker.publishStatus(new MetisThreadStatus(pStatus));
        }
    }

    /**
     * Handle completion.
     */
    protected void handleCompletion() {
        endTask();
        getStatusManager().setCompletion();
    }

    /**
     * Handle cancellation.
     */
    protected void handleCancellation() {
        endTask();
        getStatusManager().setCancelled();
    }

    /**
     * Handle failure.
     * @param pException the exception
     */
    protected void handleFailure(final Throwable pException) {
        endTask();
        setError(pException);
        getStatusManager().setFailure(pException);
    }

    @Override
    public void cancelWorker() {
        /* cancel the thread */
        theWorker.cancel(true);
        theWorker.interruptForCancel();
    }
}
