/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * ThemisDSM javaFX entryPoint.
 */
public class ThemisDSM4FX
        extends Application {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(ThemisDSM4FX.class);

    /**
     * Main panel.
     */
    private ThemisFXDSMPanel thePanel;

    @Override
    public void init() {
        /* Protect against exceptions */
        try {
            /* Create the Toolkit */
            final TethysFXGuiFactory myFactory = new TethysFXGuiFactory();

            /* Create the main panel */
            thePanel = new ThemisFXDSMPanel(myFactory);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            LOGGER.error("createPanel didn't complete successfully", e);
        }
    }

    @Override
    public void start(final Stage pStage) {
        /* If we have a panel */
        if (thePanel != null) {
            /* Attach to the stage and show */
            thePanel.attachToStage(pStage);
            pStage.show();
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
