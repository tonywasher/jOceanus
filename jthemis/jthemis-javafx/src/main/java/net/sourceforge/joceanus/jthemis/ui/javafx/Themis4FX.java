/* *****************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jthemis.ui.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jmetis.launch.javafx.MetisFXMain;
import net.sourceforge.joceanus.jmetis.launch.javafx.MetisFXState;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jthemis.ui.launch.ThemisApp;

/**
 * ThemisDSM javaFX entryPoint.
 */
public class Themis4FX
        extends Application
        implements MetisFXMain {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(Themis4FX.class);

    /**
     * javaFXState.
     */
    private MetisFXState theState;

    @Override
    public void setProgramInfo(final MetisFXState pState) {
        theState = pState;
    }

    @Override
    public void init() {
        /* Protect against exceptions */
        try {
            /* Create a timer */
            if (theState == null) {
                theState = new MetisFXState(ThemisApp.class);
            }

            /* Create the main panel */
            theState.createMain();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            LOGGER.error("createPanel didn't complete successfully", e);
        }
    }

    @Override
    public void start(final Stage pStage) {
        /* Start the program */
        theState.startMain(pStage);
    }
}
