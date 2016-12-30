/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.ui.javafx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysLogConfig;

/**
 * Coeus javaFX StartUp.
 */
public class Coeus4FX
        extends Application {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Coeus4FX.class);

    @Override
    public void start(final Stage pStage) {
        try {
            /* Configure log4j */
            TethysLogConfig.configureLog4j();

            /* Create the Toolkit */
            MetisFXToolkit myToolkit = new MetisFXToolkit(null, false);

            /* Create the main panel */
            new CoeusFXMainPanel(pStage, myToolkit);

            /* Show the stage */
            pStage.show();
        } catch (OceanusException e) {
            LOGGER.error("createStage didn't complete successfully", e);
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
