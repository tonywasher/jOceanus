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
package net.sourceforge.joceanus.jtethys.test.ui;


import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusManager;

/**
 * Thread Tester panel.
 */
public class TethysTestThreadPanel {
    /**
     * Status height.
     */
    private static final int STATUS_HEIGHT = 300;

    /**
     * GUI factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * ThreadManager.
     */
    private final TethysUIThreadManager theThreadMgr;

    /**
     * Launch button.
     */
    private final TethysUIButton theLaunchButton;

    /**
     * the status panel.
     */
    private final TethysUIThreadStatusManager theStatusPanel;

    /**
     * the main panel.
     */
    private final TethysUIBorderPaneManager theMainPanel;

    /**
     * Constructor.
     * @param pFactory the factoy
     */
    TethysTestThreadPanel(final TethysUIFactory<?> pFactory) {
        /* Access components */
        theGuiFactory = pFactory;
        theThreadMgr = theGuiFactory.threadFactory().newThreadManager();

        /* Create button */
        theLaunchButton = theGuiFactory.buttonFactory().newButton();
        theLaunchButton.setTextOnly();
        theLaunchButton.setText("Launch");

        /* Create the Panels */
        theStatusPanel = theThreadMgr.getStatusManager();
        theMainPanel = theGuiFactory.paneFactory().newBorderPane();

        /* build the panel */
        buildPanel();
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return theMainPanel;
    }

    /**
     * Build the panel.
     */
    private void buildPanel() {
        /* Create boxPane for the buttons */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        final TethysUIBoxPaneManager myButtons = myPanes.newHBoxPane();
        myButtons.addNode(theLaunchButton);

        /* Create boxPane for the window */
        final TethysUIBoxPaneManager myBox = myPanes.newVBoxPane();
        myBox.addSpacer();
        myBox.addNode(myButtons);
        myBox.addSpacer();

        /* Create borderPane for the window */
        theMainPanel.setCentre(myBox);
        theMainPanel.setBorderPadding(5);

        /* Set the status panel */
        theMainPanel.setNorth(theStatusPanel);
        theStatusPanel.setVisible(false);
        theStatusPanel.setPreferredHeight(STATUS_HEIGHT);

        /* Create thread status change handler */
        theThreadMgr.getEventRegistrar().addEventListener(e -> handleThreadChange());

        /* handle launch thread */
        theLaunchButton.getEventRegistrar().addEventListener(e -> launchThread());
    }

    /**
     * handle ThreadStatus change.
     */
    private void handleThreadChange() {
        if (theThreadMgr.hasWorker()) {
            theLaunchButton.setEnabled(false);
            theStatusPanel.setVisible(true);
        } else {
            theLaunchButton.setEnabled(true);
            theStatusPanel.setVisible(false);
        }
    }

    /**
     * launch thread.
     */
    private void launchThread() {
        theThreadMgr.startThread(new TethysTestThread());
    }
}
