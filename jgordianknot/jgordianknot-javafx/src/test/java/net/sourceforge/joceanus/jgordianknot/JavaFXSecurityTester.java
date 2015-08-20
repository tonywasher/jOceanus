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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-swing/src/test/java/net/sourceforge/joceanus/jgordianknot/SecurityTest.java $
 * $Revision: 640 $
 * $Author: Tony $
 * $Date: 2015-08-03 12:06:53 +0100 (Mon, 03 Aug 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jgordianknot.SecurityTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityProvider;
import net.sourceforge.joceanus.jgordianknot.manager.SecureManager;
import net.sourceforge.joceanus.jgordianknot.manager.javafx.JavaFXSecureManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Security Test suite.
 */
public class JavaFXSecurityTester
        extends Application
        implements SecurityManagerCreator {
    /**
     * The Test suite.
     */
    private final SecurityTestSuite theTests;

    /**
     * The stage.
     */
    private Stage theStage;

    /**
     * Constructor.
     */
    public JavaFXSecurityTester() {
        theTests = new SecurityTestSuite(this);
    }

    @Override
    public SecureManager newSecureManager() throws JOceanusException {
        JavaFXSecureManager myManager = new JavaFXSecureManager();
        myManager.setStage(theStage);
        return myManager;
    }

    @Override
    public SecureManager newSecureManager(final SecurityParameters pParams) throws JOceanusException {
        JavaFXSecureManager myManager = new JavaFXSecureManager(pParams);
        myManager.setStage(theStage);
        return myManager;
    }

    /**
     * Constructor.
     * @param pArg the parameter
     * @throws JOceanusException on error
     */
    public void runTests(final String pArg) throws JOceanusException {
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
            SecurityTestSuite.listAlgorithms(SecurityProvider.BC);
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
        theStage = pStage;

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
