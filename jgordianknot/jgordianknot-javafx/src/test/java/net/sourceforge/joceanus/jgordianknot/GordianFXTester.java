/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jgordianknot.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.manager.javafx.GordianFXHashManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Security Test suite.
 */
public class GordianFXTester
        extends Application
        implements SecurityManagerCreator {
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
    public GordianHashManager newSecureManager() throws OceanusException {
        GordianFXHashManager myManager = new GordianFXHashManager(theGuiFactory);
        return myManager;
    }

    @Override
    public GordianHashManager newSecureManager(final GordianParameters pParams) throws OceanusException {
        GordianFXHashManager myManager = new GordianFXHashManager(theGuiFactory, pParams);
        return myManager;
    }

    /**
     * Constructor.
     * @param pArg the parameter
     * @throws OceanusException on error
     */
    public void runTests(final String pArg) throws OceanusException {
        if (pArg != null) {
            /* handle check algorithms */
            if ("check".equals(pArg)) {
                theTests.checkAlgorithms();

                /* handle test security */
            } else if ("test".equals(pArg)) {
                theTests.testSecurity();

                /* handle zip file creation */
            } else if ("zip".equals(pArg)) {
                File myZipFile = new File("c:\\Users\\Tony\\TestStdZip.zip");
                theTests.createZipFile(myZipFile, new File("c:\\Users\\Tony\\tester"), true);
                theTests.extractZipFile(myZipFile, new File("c:\\Users\\Tony\\testcomp"));
            }
        } else {
            GordianTestSuite.listAlgorithms();
        }
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
        Scene myScene = new Scene(new Group());
        BorderPane myPane = new BorderPane();
        ((Group) myScene.getRoot()).getChildren().addAll(myPane);
        pStage.setTitle("JavaFXSecurity Demo");
        pStage.setScene(myScene);
        pStage.show();
        theGuiFactory.setStage(pStage);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    runTests("test");
                } catch (Exception e) {
                    System.out.println("Help");
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }
}
