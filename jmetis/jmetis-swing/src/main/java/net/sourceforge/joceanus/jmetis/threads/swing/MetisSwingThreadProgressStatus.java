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
package net.sourceforge.joceanus.jmetis.threads.swing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.Timer;

import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadProgressStatus;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Swing Thread ProgressBar Status Manager.
 */
public class MetisSwingThreadProgressStatus
        extends MetisThreadProgressStatus<JComponent, Icon> {
    /**
     * Timer.
     */
    private final Timer theTimer;

    /**
     * Constructor.
     * @param pManager the thread manager
     * @param pFactory the GUI factory
     */
    protected MetisSwingThreadProgressStatus(final MetisThreadManager<JComponent, Icon> pManager,
                                             final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pManager, pFactory);

        /* Create the timer */
        theTimer = new Timer(TIMER_DURATION, e -> handleClear());
        theTimer.setRepeats(false);
    }

    @Override
    protected MetisSwingThreadManager getThreadManager() {
        return (MetisSwingThreadManager) super.getThreadManager();
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