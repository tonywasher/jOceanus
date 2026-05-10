/*
 * Tethys: GUI Utilities
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.tethys.javafx.launch.util;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIProgram;
import javafx.application.Preloader.StateChangeNotification;
import javafx.stage.Stage;

/**
 * Base State for javaFX program.
 */
public interface TethysUIFXBaseState {
    /**
     * Obtain the program definitions.
     *
     * @return the program definitions
     */
    TethysUIProgram getProgramDefinitions();
    
    /**
     * Create the main panel.
     *
     * @throws OceanusException on error
     */
    void createMain() throws OceanusException;

    /**
     * Create an FXSplash.
     */
    void createSplash();

    /**
     * Start the preLoader.
     *
     * @param pStage the preLoader stage
     */
    void startPreLoader(Stage pStage);

    /**
     * Start the main panel.
     *
     * @param pStage the main stage
     */
    void startMain(Stage pStage);

    /**
     * handle the state notification change.
     *
     * @param pEvent the event
     */
    void handleStateChangeNotification(StateChangeNotification pEvent);
}
