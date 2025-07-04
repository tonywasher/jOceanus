/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.launch;

import javafx.application.Preloader;
import javafx.stage.Stage;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.tethys.api.factory.TethysUILaunchProgram;
import net.sourceforge.joceanus.tethys.javafx.launch.util.TethysUIFXLaunchState;

/**
 * javaFX preLoader.
 */
public abstract class TethysUIFXPreLoader
        extends Preloader {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(TethysUIFXPreLoader.class);

    /**
     * javaFXState.
     */
    private TethysUIFXLaunchState theState;

    /**
     * Constructor.
     */
    public TethysUIFXPreLoader() {
    }

    /**
     * Obtain program info.
     * @return the info
     */
    protected abstract TethysUILaunchProgram getProgramInfo();

    @Override
    public void init() {
        /* Protect against exceptions */
        try {
            /* Access program info */
            theState = new TethysUIFXLaunchState(getProgramInfo());

            /* Create a splash panel */
            theState.createSplash();

        } catch (OceanusException e) {
            LOGGER.error("createSplash didn't complete successfully", e);
        }
    }

    @Override
    public void start(final Stage pStage) {
        theState.startPreLoader(pStage);
    }

    @Override
    public void handleStateChangeNotification(final StateChangeNotification pEvent) {
        theState.handleStateChangeNotification(pEvent);
    }
}
