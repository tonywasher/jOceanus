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
package net.sourceforge.joceanus.jmetis.threads.javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * JavaFX Thread Status Manager.
 */
public class MetisFXThreadStatusManager
        extends MetisThreadStatusManager<Node, Node> {
    /**
     * Timer.
     */
    private final Timeline theTimer;

    /**
     * Constructor.
     * @param pManager the thread manager
     * @param pFactory the GUI factory
     */
    protected MetisFXThreadStatusManager(final MetisThreadManager<Node, Node> pManager,
                                         final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pManager, pFactory);

        /* Create the timer */
        theTimer = new Timeline(new KeyFrame(Duration.millis(TIMER_DURATION), e -> handleClear()));
    }

    @Override
    protected MetisFXThreadManager getThreadManager() {
        return (MetisFXThreadManager) super.getThreadManager();
    }

    @Override
    protected void setCompletion() {
        /* Pass call on */
        super.setCompletion();

        /* Start the timer */
        theTimer.play();
    }

    @Override
    protected void handleClear() {
        /* cancel any existing task */
        theTimer.stop();

        /* Pass call on */
        super.handleClear();
    }
}
