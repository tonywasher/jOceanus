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
package net.sourceforge.joceanus.jmetis.threads.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerDataManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Swing Thread manager.
 */
public class MetisSwingThreadManager
        extends MetisThreadManager<JComponent, Icon> {
    /**
     * The Active worker.
     */
    private MetisSwingThread<?> theWorker;

    /**
     * Constructor.
     * @param pViewerManager the viewer manager
     * @param pFactory the GUI factory
     */
    public MetisSwingThreadManager(final MetisViewerDataManager pViewerManager,
                                   final TethysSwingGuiFactory pFactory) {
        super(pViewerManager, new MetisSwingThreadStatusManager(pFactory));
    }

    @Override
    public String getTaskName() {
        return theWorker == null
                                 ? null
                                 : theWorker.getTaskName();
    }

    @Override
    public MetisSwingThreadStatusManager getStatusManager() {
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
