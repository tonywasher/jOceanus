/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.test.help.javafx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jmetis.help.MetisHelpEntry;
import net.sourceforge.joceanus.jmetis.help.MetisHelpModule;
import net.sourceforge.joceanus.jmetis.help.javafx.MetisFXHelpWindow;
import net.sourceforge.joceanus.jmetis.test.help.MetisTestHelpPage;
import net.sourceforge.joceanus.jmetis.test.help.MetisTestHelpStyleSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Help Window.
 */
public class TestFXHelpWindow
        extends Application {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TestFXHelpWindow.class);

    @Override
    public void start(final Stage pStage) {
        /* Create the panel */
        final Node myMain = buildPanel(pStage);

        /* Create scene */
        final Scene myScene = new Scene(new Group());
        ((Group) myScene.getRoot()).getChildren().addAll(myMain);
        pStage.setTitle("JavaFXHelp Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Build the panel.
     * @param pStage the stage
     * @return the panel
     */
    private static Node buildPanel(final Stage pStage) {
        /* Create the factory */
        final TethysFXGuiFactory myFactory = new TethysFXGuiFactory();
        myFactory.setStage(pStage);

        /* Create a button */
        final Button myButton = new Button("Help");

        /* Create the help window */
        final MetisFXHelpWindow myWindow = new MetisFXHelpWindow(myFactory);

        /* Create a BorderPane for the fields */
        final BorderPane myPane = new BorderPane();
        myPane.setLeft(myButton);

        /* Add listener for the button */
        myButton.setOnAction(event -> myWindow.showDialog());

        /* Protect against exceptions */
        try {
            myWindow.setModule(new TestHelp());
        } catch (OceanusException e) {
            LOGGER.error("failed to build HelpModule", e);
        }

        return myPane;
    }

    /**
     * Help system.
     */
    private static class TestHelp
            extends MetisHelpModule {
        /**
         * Constructor.
         * @throws OceanusException on error
         */
        TestHelp() throws OceanusException {
            /* Initialise the underlying module */
            super("Test Help System");

            /* Create accounts tree */
            final MetisHelpEntry myAccounts = addRootEntry(defineContentsEntry("Accounts"));
            myAccounts.addChildEntry(defineHelpEntry("Deposits", MetisTestHelpPage.HELP_DEPOSITS));
            myAccounts.addChildEntry(defineHelpEntry("Loans", MetisTestHelpPage.HELP_LOANS));

            /* Create static tree */
            final MetisHelpEntry myStatic = addRootEntry(defineContentsEntry("StaticData"));
            myStatic.addChildEntry(defineHelpEntry("AccountTypes", MetisTestHelpPage.HELP_ACCOUNTTYPES));
            myStatic.addChildEntry(defineHelpEntry("TransactionTypes", MetisTestHelpPage.HELP_TRANTYPES));

            /* Load help pages */
            loadHelpPages();

            /* Load the CSS */
            loadCSS(MetisTestHelpStyleSheet.CSS_HELP);
        }
    }
}
