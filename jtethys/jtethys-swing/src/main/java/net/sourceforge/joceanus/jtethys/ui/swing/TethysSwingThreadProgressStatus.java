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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.Timer;

import net.sourceforge.joceanus.jtethys.ui.TethysThreadManager;
import net.sourceforge.joceanus.jtethys.ui.TethysThreadProgressStatus;

/**
 * Swing Thread ProgressBar Status Manager.
 */
public class TethysSwingThreadProgressStatus
        extends TethysThreadProgressStatus {
    /**
     * Timer.
     */
    private final Timer theTimer;

    /**
     * Constructor.
     * @param pManager the thread manager
     * @param pFactory the GUI factory
     */
    TethysSwingThreadProgressStatus(final TethysThreadManager pManager,
                                    final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pManager, pFactory);

        /* Create the timer */
        theTimer = new Timer(TIMER_DURATION, e -> handleClear());
        theTimer.setRepeats(false);
    }

    @Override
    protected TethysSwingThreadManager getThreadManager() {
        return (TethysSwingThreadManager) super.getThreadManager();
    }

    @Override
    public void setCompletion() {
        /* Pass call on */
        super.setCompletion();

        /* Start the timer */
        theTimer.restart();
    }

    @Override
    protected void handleClear() {
        /* cancel any existing task */
        theTimer.stop();

        /* Pass call on */
        super.handleClear();
    }
}
