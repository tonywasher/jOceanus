/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.javafx.thread;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadManager;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadProgressStatus;
import net.sourceforge.joceanus.tethys.ui.javafx.base.TethysUIFXNode;

/**
 * JavaFX Thread ProgressBar Status Manager.
 */
public class TethysUIFXThreadProgressStatus
        extends TethysUICoreThreadProgressStatus {
    /**
     * Timer.
     */
    private final Timeline theTimer;

    /**
     * Constructor.
     *
     * @param pManager the thread manager
     * @param pFactory the GUI factory
     */
    TethysUIFXThreadProgressStatus(final TethysUICoreThreadManager pManager,
                                   final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pManager, pFactory);

        /* Create the timer */
        theTimer = new Timeline(new KeyFrame(Duration.millis(TIMER_DURATION), e -> handleClear()));
    }

    @Override
    protected TethysUIFXThreadManager getThreadManager() {
        return (TethysUIFXThreadManager) super.getThreadManager();
    }

    @Override
    public TethysUIFXNode getNode() {
        return (TethysUIFXNode) super.getNode();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPreferredHeight(pHeight);
    }

    @Override
    public void setCompletion() {
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
