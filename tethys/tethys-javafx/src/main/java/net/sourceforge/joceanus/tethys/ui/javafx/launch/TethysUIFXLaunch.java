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
package net.sourceforge.joceanus.tethys.ui.javafx.launch;

import javafx.application.Application;
import javafx.stage.Stage;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUILaunchProgram;
import net.sourceforge.joceanus.tethys.ui.javafx.launch.util.TethysUIFXLaunchState;
import net.sourceforge.joceanus.tethys.ui.javafx.launch.util.TethysUIFXLaunchState.TethysUIFXSetState;

/**
 * javaFX StartUp.
 */
public abstract class TethysUIFXLaunch
        extends Application
        implements TethysUIFXSetState {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(TethysUIFXLaunch.class);

    /**
     * javaFXState.
     */
    private TethysUIFXLaunchState theState;

    /**
     * Obtain program info.
     * @return the info
     */
    protected abstract TethysUILaunchProgram getProgramInfo();

    @Override
    public void setProgramState(final TethysUIFXLaunchState pState) {
        theState = pState;
    }

    @Override
    public void init() {
        /* Protect against exceptions */
        try {
            /* Ensure that the state is created */
            if (theState == null) {
                theState = new TethysUIFXLaunchState(getProgramInfo());
            }

            /* Create the main panel */
            theState.createMain();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            LOGGER.error("createMain didn't complete successfully", e);
            System.exit(1);
        }
    }

    @Override
    public void start(final Stage pStage) {
        /* Start the program */
        theState.startMain(pStage);
    }
}
