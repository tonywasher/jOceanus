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

import javafx.concurrent.Worker.State;
import javafx.scene.Node;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * javaFX Thread manager.
 */
public class MetisFXThreadManager
        extends MetisThreadManager<Node> {
    /**
     * The Active worker.
     */
    private MetisFXThread<?> theWorker;

    /**
     * The Active status.
     */
    private volatile MetisThreadStatus theActiveStatus;

    /**
     * Constructor.
     * @param pViewerManager the viewer manager
     */
    public MetisFXThreadManager(final MetisViewerManager pViewerManager) {
        super(pViewerManager, new MetisFXThreadStatusManager());
    }

    @Override
    protected MetisFXThreadStatusManager getStatusManager() {
        return (MetisFXThreadStatusManager) super.getStatusManager();
    }

    @Override
    public String getTaskName() {
        return theWorker == null
                                 ? null
                                 : theWorker.getTaskName();
    }

    @Override
    protected <T> Runnable wrapThread(final MetisThread<T> pThread) {
        /* Create the wrapped thread and listen to state transition */
        theWorker = new MetisFXThread<>(this, pThread);
        theWorker.stateProperty().addListener((v, o, n) -> handleThreadState(n));
        theWorker.valueProperty().addListener((v, o, n) -> processStatus());

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
        /* Take a copy as the active status */
        theActiveStatus = new MetisThreadStatus(pStatus);

        /* update status */
        theWorker.publishStatus();
    }

    protected void processStatus() {
        /* Pass to the status bar */
        getStatusManager().setProgress(theActiveStatus);
    }

    /**
     * Handle thread state.
     * @param pState the state
     */
    private void handleThreadState(final State pState) {
        /* Switch on the state */
        switch (pState) {
            case SUCCEEDED:
                handleCompletion();
                break;
            case FAILED:
                handleFailure(theWorker.getException());
                break;
            case CANCELLED:
                handleCancellation();
                break;
            default:
                break;
        }
    }

    /**
     * Handle completion.
     */
    private void handleCompletion() {
        /* Access the status manager */
        MetisFXThreadStatusManager myManager = getStatusManager();

        /* Handle exceptions */
        try {
            /* Complete the thread */
            theWorker.processResult();

            /* Record the completion */
            endTask();
            myManager.setCompletion();

            /* Catch exceptions */
        } catch (OceanusException e) {
            /* Convert to failure */
            handleFailure(e);
        }
    }

    /**
     * Handle cancellation.
     */
    private void handleCancellation() {
        endTask();
        getStatusManager().setCancelled();
    }

    /**
     * Handle failure.
     * @param pException the exception
     */
    private void handleFailure(final Throwable pException) {
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
