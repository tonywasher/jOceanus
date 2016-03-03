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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXThreadManager;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;

/**
 * Thread Manager Tester.
 */
public class MetisFXThreadTester
        extends Application {
    /**
     * The default height.
     */
    private static final int DEFAULT_HEIGHT = 620;

    /**
     * The default width.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisFXThreadTester.class);

    /**
     * ViewerManager.
     */
    // private final MetisFXViewerManager theViewerMgr;

    /**
     * ThreadManager.
     */
    private final MetisFXThreadManager theThreadMgr;

    /**
     * Launch button.
     */
    private final Button theLaunchButton;

    /**
     * Debug button.
     */
    private final Button theDebugButton;

    /**
     * the status panel.
     */
    private final Node theStatusPanel;

    /**
     * the main panel.
     */
    private final BorderPane theMainPanel;

    /**
     * Constructor.
     */
    public MetisFXThreadTester() {
        /* Create button */
        theLaunchButton = new Button("Launch");
        theDebugButton = new Button("Debug");

        /* Create the Managers */
        // MetisFieldManager myFieldMgr = new MetisFieldManager(new MetisFieldConfig());
        // theViewerMgr = new MetisFXViewerManager(myFieldMgr);
        theThreadMgr = null; // new MetisFXThreadManager(theViewerMgr);
        theStatusPanel = null; // theThreadMgr.getNode();
        theMainPanel = new BorderPane();
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
        /* Build the panel */
        theThreadMgr.setStage(pStage);
        buildPanel();

        /* Create scene */
        Scene myScene = new Scene(new Group());
        ((Group) myScene.getRoot()).getChildren().addAll(theMainPanel);
        pStage.setTitle("MetisFXThread Demo");
        TethysFXGuiUtils.addStyleSheet(myScene);
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Build the panel.
     */
    private void buildPanel() {
        /* Create borderPane for the window */
        BorderPane myButtons = new BorderPane();
        myButtons.setLeft(theLaunchButton);
        myButtons.setRight(theDebugButton);

        /* Create borderPane for the window */
        theMainPanel.setCenter(myButtons);
        theMainPanel.setPrefWidth(DEFAULT_WIDTH);
        theMainPanel.setPrefHeight(DEFAULT_HEIGHT);

        /* Create thread status change handler */
        theThreadMgr.getEventRegistrar().addEventListener(e -> handleThreadChange());

        /* handle launch thread */
        theLaunchButton.setOnAction(e -> launchThread());
    }

    /**
     * handle ThreadStatus change.
     */
    private void handleThreadChange() {
        if (theThreadMgr.hasWorker()) {
            theLaunchButton.setDisable(true);
            theMainPanel.setTop(theStatusPanel);
        } else {
            theLaunchButton.setDisable(false);
            theStatusPanel.setOnTouchPressed(null);
        }
    }

    /**
     * launch thread.
     */
    private void launchThread() {
        theThreadMgr.startThread(new MetisTestThread());
    }
}
