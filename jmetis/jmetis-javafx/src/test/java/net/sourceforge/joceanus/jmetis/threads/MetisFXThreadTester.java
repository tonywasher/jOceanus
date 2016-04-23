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
package net.sourceforge.joceanus.jmetis.threads;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXThreadManager;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXThreadStatusManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Thread Manager Tester.
 */
public class MetisFXThreadTester
        extends Application {
    /**
     * ViewerManager.
     */
    // private final MetisFXViewerManager theViewerMgr;

    /**
     * GUI factory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * ThreadManager.
     */
    private final MetisFXThreadManager theThreadMgr;

    /**
     * Launch button.
     */
    private final TethysFXButton theLaunchButton;

    /**
     * Debug button.
     */
    private final TethysFXButton theDebugButton;

    /**
     * the status panel.
     */
    private final MetisFXThreadStatusManager theStatusPanel;

    /**
     * the main panel.
     */
    private final TethysFXBorderPaneManager theMainPanel;

    /**
     * the stage.
     */
    private Stage theStage;

    /**
     * Constructor.
     */
    public MetisFXThreadTester() {
        /* Create factory */
        theGuiFactory = new TethysFXGuiFactory();

        /* Create buttons */
        theLaunchButton = theGuiFactory.newButton();
        theLaunchButton.setTextOnly();
        theLaunchButton.setText("Launch");
        theDebugButton = theGuiFactory.newButton();
        theDebugButton.setTextOnly();
        theDebugButton.setText("Debug");

        /* Create the Managers */
        // MetisFieldManager myFieldMgr = new MetisFieldManager(new MetisFieldConfig());
        // theViewerMgr = new MetisFXViewerManager(myFieldMgr);
        theThreadMgr = new MetisFXThreadManager(null, theGuiFactory);
        theStatusPanel = theThreadMgr.getStatusManager();
        theMainPanel = theGuiFactory.newBorderPane();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage pStage) {
        /* Record the stage */
        theStage = pStage;

        /* Build the panel */
        buildPanel();

        /* Create scene */
        Scene myScene = new Scene(theMainPanel.getNode());
        pStage.setTitle("MetisFXThread Demo");
        pStage.setScene(myScene);

        /* Configure factory */
        theGuiFactory.setStage(pStage);
        theGuiFactory.applyStyleSheets(myScene);
        pStage.show();
    }

    /**
     * Build the panel.
     */
    private void buildPanel() {
        /* Create boxPane for the buttons */
        TethysFXBoxPaneManager myButtons = theGuiFactory.newHBoxPane();
        myButtons.addNode(theLaunchButton);
        myButtons.addSpacer();
        myButtons.addNode(theDebugButton);

        /* Create boxPane for the window */
        TethysFXBoxPaneManager myBox = theGuiFactory.newVBoxPane();
        myBox.addSpacer();
        myBox.addNode(myButtons);
        myBox.addSpacer();

        /* Create borderPane for the window */
        theMainPanel.setCentre(myBox);
        theMainPanel.setBorderPadding(5);

        /* Set the status panel */
        theMainPanel.setNorth(theStatusPanel);
        theStatusPanel.setVisible(false);

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
        theStage.sizeToScene();
    }

    /**
     * launch thread.
     */
    private void launchThread() {
        theThreadMgr.startThread(new MetisTestThread());
    }
}
