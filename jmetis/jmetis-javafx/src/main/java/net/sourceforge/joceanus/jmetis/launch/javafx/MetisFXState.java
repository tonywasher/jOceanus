/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmetis.launch.javafx;

import java.util.Arrays;
import javafx.application.Platform;
import javafx.application.Preloader.StateChangeNotification;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jmetis.launch.MetisMainPanel;
import net.sourceforge.joceanus.jmetis.launch.MetisProgram;
import net.sourceforge.joceanus.jmetis.launch.MetisToolkit;
import net.sourceforge.joceanus.jmetis.profile.MetisState;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXNode;

/**
 * State for javaFX program.
 */
public class MetisFXState {
    /**
     * The Program definition.
     */
    private final MetisState theInfo;

    /**
     * The splashPane.
     */
    private MetisFXSplash theSplash;

    /**
     * The toolkit.
     */
    private MetisToolkit theToolkit;

    /**
     * The Panel.
     */
    private BorderPane thePane;

    /**
     * The Panel.
     */
    private MetisMainPanel theMain;

    /**
     * Constructor.
     * @param pInfo the program info
     * @throws OceanusException on error
     */
    public MetisFXState(final TethysProgram pInfo) throws OceanusException {
        /* Create the program class. */
        theInfo = new MetisState(new TethysFXGuiFactory(pInfo));
    }

    /**
     * Obtain the program definitions.
     * @return the program definitions
     */
    public TethysProgram getProgramDefinitions() {
        return theInfo.getProgramDefinitions();
    }

    /**
     * Obtain the state.
     * @return the state
     */
    public MetisState getState() {
        return theInfo;
    }

    /**
     * Create an FXSplash.
     */
    public void createSplash() {
        /* Create a Splash if required */
        final TethysIconId mySplashId = theInfo.getProgramDefinitions().getSplash();
        theSplash = mySplashId != null
                    ? new MetisFXSplash(this)
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
        /* Create the Toolkit */
        final TethysProgram myApp = theInfo.getProgramDefinitions();
        final MetisProgram myDef = (MetisProgram) myApp;
        theToolkit = new MetisToolkit(getState());

        /* Create the main panel */
        theMain = createMain(myDef, theToolkit);

        /* Create the borderPane */
        thePane = new BorderPane();
        thePane.setCenter(TethysFXNode.getNode(theMain.getComponent()));
        final TethysMenuBarManager myMenuBar = theMain.getMenuBar();
        if (myMenuBar != null) {
            thePane.setTop(((TethysFXMenuBarManager) myMenuBar).getNode());
        }
     }

    /**
     * Create the main panel.
     * @param pProgram the program state
     * @param pToolkit the toolkit
     * @return the main panel
     * @throws OceanusException on error
     */
    private static MetisMainPanel createMain(final MetisProgram pProgram,
                                             final MetisToolkit pToolkit) throws OceanusException {
        /* Create the main panel */
        return pProgram.createMainPanel(pToolkit);
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
            theToolkit.getGuiFactory().activateLogSink();
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
        /* Access the GUI factory and program definitions */
        final TethysFXGuiFactory myFactory = (TethysFXGuiFactory) theToolkit.getGuiFactory();
        final TethysProgram myApp = theInfo.getProgramDefinitions();

        /* Create the scene */
        final int[] myDim = myApp.getPanelDimensions();
        final Scene myScene = myDim == null
                              ? new Scene(thePane)
                              : new Scene(thePane, myDim[0], myDim[1]);
        myFactory.registerScene(myScene);

        /* Configure the stage */
        myFactory.setStage(pStage);
        pStage.setTitle(myApp.getName());
        pStage.setScene(myScene);

        /* Add the icons to the frame */
        final TethysIconId[] myIds = myApp.getIcons();
        final Image[] myIcons = TethysFXGuiUtils.getIcons(myIds);
        pStage.getIcons().addAll(Arrays.asList(myIcons));

        /* Record startUp completion */
        theToolkit.getActiveProfile().end();
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
