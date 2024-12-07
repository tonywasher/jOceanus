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
package net.sourceforge.joceanus.tethys.ui.javafx.launch.util;

import java.util.Arrays;
import javafx.application.Platform;
import javafx.application.Preloader.StateChangeNotification;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUILaunchProgram;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIMainPanel;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIMenuBarManager;
import net.sourceforge.joceanus.tethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.tethys.ui.javafx.base.TethysUIFXUtils;
import net.sourceforge.joceanus.tethys.ui.javafx.factory.TethysUIFXFactory;
import net.sourceforge.joceanus.tethys.ui.javafx.menu.TethysUIFXMenuBarManager;

/**
 * State for javaFX program.
 */
public class TethysUIFXLaunchState {
    /**
     * Set state interface.
     */
    public interface TethysUIFXSetState {
        /**
         * Set program state.
         * @param pState the state
         */
        void setProgramState(TethysUIFXLaunchState pState);
    }

    /**
     * The Program definition.
     */
    private final TethysUILaunchProgram theApp;

    /**
     * The factory.
     */
    private final TethysUIFXFactory theFactory;

    /**
     * The splashPane.
     */
    private TethysUIFXSplash theSplash;

    /**
     * The Panel.
     */
    private BorderPane thePane;

    /**
     * The Panel.
     */
    private TethysUIMainPanel theMain;

    /**
     * Constructor.
     * @param pProgram the program
     * @throws OceanusException on error
     */
    public TethysUIFXLaunchState(final TethysUILaunchProgram pProgram) throws OceanusException {
        /* Create the program class. */
        theApp = pProgram;
        theFactory = new TethysUIFXFactory(pProgram);
    }

    /**
     * Obtain the program definitions.
     * @return the program definitions
     */
    TethysUIProgram getProgramDefinitions() {
        return theApp;
    }

    /**
     * Create an FXSplash.
     */
    public void createSplash() {
        /* Create a Splash if required */
        final TethysUIIconId mySplashId = theApp.getSplash();
        theSplash = mySplashId != null
                ? new TethysUIFXSplash(this)
                : null;
    }

    /**
     * Start the preLoader.
     * @param pStage the preLoader stage
     */
    public void startPreLoader(final Stage pStage) {
        /* If we have a pane */
        if (theSplash != null) {
            /* Configure the stage */
            theSplash.attachToStage(pStage);
            pStage.show();
        }
    }

    /**
     * Initialise the main program.
     * @throws OceanusException on error
     */
    public void createMain() throws OceanusException {
        /* Create the main panel */
        theMain = createMain(theApp, theFactory);

        /* Create the borderPane */
        thePane = new BorderPane();
        thePane.setCenter(TethysUIFXNode.getNode(theMain.getComponent()));
        final TethysUIMenuBarManager myMenuBar = theMain.getMenuBar();
        if (myMenuBar != null) {
            thePane.setTop(((TethysUIFXMenuBarManager) myMenuBar).getNode());
        }
    }

    /**
     * Create the main panel.
     * @param pProgram the program state
     * @param pFactory the factory
     * @return the main panel
     * @throws OceanusException on error
     */
    private static TethysUIMainPanel createMain(final TethysUILaunchProgram pProgram,
                                                final TethysUIFXFactory pFactory) throws OceanusException {
        /* Create the main panel */
        return pProgram.createMainPanel(pFactory);
    }

    /**
     * Start the main panel.
     * @param pStage the main stage
     */
    public void startMain(final Stage pStage) {
        /* If we have a panel */
        if (theMain != null) {
            /* Attach to the stage and show */
            attachToStage(pStage);
            theFactory.activateLogSink();
            Platform.setImplicitExit(true);
            pStage.setOnCloseRequest(ae -> {
                if (theMain.handleAppClose()) {
                    Platform.exit();
                    System.exit(0);
                } else {
                    ae.consume();
                }
            });
            pStage.show();
        }
    }

    /**
     * Attach to stage.
     * @param pStage the stage
     */
    protected void attachToStage(final Stage pStage) {
        /* Create the scene */
        final int[] myDim = theApp.getPanelDimensions();
        final Scene myScene = myDim == null
                ? new Scene(thePane)
                : new Scene(thePane, myDim[0], myDim[1]);
        theFactory.registerScene(myScene);

        /* Configure the stage */
        theFactory.setStage(pStage);
        pStage.setTitle(theApp.getName());
        pStage.setScene(myScene);

        /* Add the icons to the frame */
        final TethysUIIconId[] myIds = theApp.getIcons();
        final Image[] myIcons = TethysUIFXUtils.getIcons(myIds);
        pStage.getIcons().addAll(Arrays.asList(myIcons));

        /* Record startUp completion */
        theFactory.getActiveProfile().end();
    }

    /**
     * handle the state notification change.
     * @param pEvent the event
     */
    public void handleStateChangeNotification(final StateChangeNotification pEvent) {
        if (theSplash != null) {
            theSplash.handleStateChange(pEvent);
        }
    }
}
