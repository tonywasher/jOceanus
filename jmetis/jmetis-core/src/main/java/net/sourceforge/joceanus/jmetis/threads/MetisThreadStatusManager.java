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
package net.sourceforge.joceanus.jmetis.threads;

/**
 * Thread Status Manager.
 * @param <N> the node type
 */
public abstract class MetisThreadStatusManager<N> {
    /**
     * Timer duration.
     */
    protected static final int TIMER_DURATION = 5000;

    /**
     * Cancel button text.
     */
    protected static final String NLS_CANCEL = MetisThreadResource.STATUSBAR_BUTTON_CANCEL.getValue();

    /**
     * Clear button text.
     */
    protected static final String NLS_CLEAR = MetisThreadResource.STATUSBAR_BUTTON_CLEAR.getValue();

    /**
     * Succeeded message.
     */
    protected static final String NLS_SUCCEEDED = MetisThreadResource.STATUSBAR_STATUS_SUCCESS.getValue();

    /**
     * Cancelled message.
     */
    protected static final String NLS_CANCELLED = MetisThreadResource.STATUSBAR_STATUS_CANCEL.getValue();

    /**
     * Failed message.
     */
    protected static final String NLS_FAILED = MetisThreadResource.STATUSBAR_STATUS_FAIL.getValue();

    /**
     * Progress title.
     */
    protected static final String NLS_PROGRESS = MetisThreadResource.STATUSBAR_TITLE_PROGRESS.getValue();

    /**
     * Status title.
     */
    protected static final String NLS_STATUS = MetisThreadResource.STATUSBAR_TITLE_STATUS.getValue();

    /**
     * The Thread Manager.
     */
    private MetisThreadManager<N> theThreadManager;

    /**
     * Obtain the node.
     * @return the node
     */
    public abstract N getNode();

    /**
     * set Thread Manager.
     * @param pManager the thread manager
     */
    protected void setThreadManager(final MetisThreadManager<N> pManager) {
        theThreadManager = pManager;
    }

    /**
     * get Thread Manager.
     * @return the thread manager
     */
    protected MetisThreadManager<N> getThreadManager() {
        return theThreadManager;
    }

    /**
     * set Progress.
     * @param pStatus the status to apply
     */
    protected abstract void setProgress(final MetisThreadStatus pStatus);

    /**
     * set Completion.
     */
    protected abstract void setCompletion();

    /**
     * set Failure.
     * @param pException the exception
     */
    protected abstract void setFailure(final Throwable pException);

    /**
     * set Cancelled.
     */
    protected abstract void setCancelled();

    /**
     * handle cancel.
     */
    protected void handleCancel() {
        theThreadManager.cancelWorker();
    }
}
