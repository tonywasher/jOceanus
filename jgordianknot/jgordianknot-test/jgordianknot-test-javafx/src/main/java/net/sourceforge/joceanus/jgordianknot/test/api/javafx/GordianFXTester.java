/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.test.api.javafx;

import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.javafx.GordianFXSecurityManager;
import net.sourceforge.joceanus.jgordianknot.test.api.GordianTestSuite;
import net.sourceforge.joceanus.jgordianknot.test.api.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Security Test suite.
 */
public class GordianFXTester
        extends Application
        implements SecurityManagerCreator {
    /**
     * Create a logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianFXTester.class);

    /**
     * The Test suite.
     */
    private final GordianTestSuite theTests;

    /**
     * The GUI Factory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * Constructor.
     */
    public GordianFXTester() {
        theTests = new GordianTestSuite(this);
        theGuiFactory = new TethysFXGuiFactory();
    }

    @Override
    public GordianSecurityManager newSecureManager() throws OceanusException {
        final GordianFXSecurityManager myManager = new GordianFXSecurityManager(theGuiFactory);
        return myManager;
    }

    @Override
    public GordianSecurityManager newSecureManager(final GordianParameters pParams) throws OceanusException {
        final GordianFXSecurityManager myManager = new GordianFXSecurityManager(theGuiFactory, pParams);
        return myManager;
    }

    /**
     * Constructor.
     * @param pArgs the parameters
     * @throws OceanusException on error
     */
    public void runTests(final List<String> pArgs) throws OceanusException {
        theTests.runTests(pArgs);
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
        final Scene myScene = new Scene(new Group());
        final BorderPane myPane = new BorderPane();
        ((Group) myScene.getRoot()).getChildren().addAll(myPane);
        pStage.setTitle("JavaFXSecurity Demo");
        pStage.setScene(myScene);
        pStage.show();
        theGuiFactory.setStage(pStage);

        final Parameters myParams = getParameters();
        final List<String> myArgs = myParams.getRaw();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    runTests(myArgs);
                } catch (Exception e) {
                    LOGGER.fatal("Help", e);
                }
                System.exit(0);
            }
        });
    }
}
