/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jthemis.ui.javafx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Preloader;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.atlas.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.atlas.ui.javafx.MetisFXSplash;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ui.ThemisApp;

/**
 * Themis javaFX preLoader.
 */
public class ThemisFXpreLoader
        extends Preloader {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThemisFXpreLoader.class);

    /**
     * The splashPane.
     */
    private MetisFXSplash theSplash;

    @Override
    public void init() {
        try {
            /* Access program info */
            MetisProgram myInfo = new MetisProgram(ThemisApp.class);

            /* Create a StackPane */
            theSplash = new MetisFXSplash(myInfo);

        } catch (OceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    @Override
    public void start(final Stage pStage) {
        /* If we have a pane */
        if (theSplash != null) {
            /* Configure the stage */
            theSplash.attachToStage(pStage);
            pStage.show();
        }
    }

    @Override
    public void handleStateChangeNotification(final StateChangeNotification pEvent) {
        if (theSplash != null) {
            theSplash.handleStateChange(pEvent);
        }
    }
}
