/*******************************************************************************
 * jTethys: Java Utilities
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.help;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.javafx.TethysFXHelpManager;

/**
 * Help Window.
 */
public class TestFXHelpWindow
        extends Application {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestFXHelpWindow.class);

    /**
     * The HelpButton.
     */
    private final Button theButton;

    /**
     * The HelpManager.
     */
    private final TethysFXHelpManager theHelpWindow;

    /**
     * Constructor.
     */
    public TestFXHelpWindow() {
        theButton = new Button("Help");
        theHelpWindow = new TethysFXHelpManager();
    }

    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage pStage) {
        /* Create the panel */
        Node myMain = buildPanel();

        /* Create scene */
        Scene myScene = new Scene(new Group());
        ((Group) myScene.getRoot()).getChildren().addAll(myMain);
        pStage.setTitle("JavaFXHelp Demo");
        pStage.setScene(myScene);
        pStage.show();

        /* Declare stage to the HelpWindow */
        theHelpWindow.setStage(pStage);
    }

    /**
     * Build the panel.
     * @return the panel
     */
    private Node buildPanel() {
        /* Create a BorderPane for the fields */
        BorderPane myPane = new BorderPane();
        myPane.setLeft(theButton);

        /* Add listener for the button */
        theButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                theHelpWindow.showDialog();
            }
        });

        /* Protect against exceptions */
        try {
            theHelpWindow.setModule(new TestHelp());
        } catch (OceanusException e) {
            LOGGER.error("failed to build HelpModule", e);
        }

        return myPane;
    }

    /**
     * Help system.
     */
    public class TestHelp
            extends TethysHelpModule {
        /**
         * Constructor.
         * @throws TethysHelpException on error
         */
        public TestHelp() throws TethysHelpException {
            /* Initialise the underlying module */
            super(TestFXHelpWindow.class, "Test Help System");

            /* Create accounts tree */
            TethysHelpEntry myAccounts = addRootEntry(defineContentsEntry("Accounts"));
            myAccounts.addChildEntry(defineHelpEntry("Deposits", "Deposits.html"));
            myAccounts.addChildEntry(defineHelpEntry("Loans", "Loans.html"));

            /* Create static tree */
            TethysHelpEntry myStatic = addRootEntry(defineContentsEntry("StaticData"));
            myStatic.addChildEntry(defineHelpEntry("AccountTypes", "AccountTypes.html"));
            myStatic.addChildEntry(defineHelpEntry("TransactionTypes", "TransactionTypes.html"));

            /* Load help pages */
            loadHelpPages();
        }
    }
}
