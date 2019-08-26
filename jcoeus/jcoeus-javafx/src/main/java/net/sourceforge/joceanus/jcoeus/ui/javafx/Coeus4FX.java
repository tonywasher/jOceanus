/*******************************************************************************
 * Coeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.ui.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jcoeus.ui.CoeusApp;
import net.sourceforge.joceanus.jmetis.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.profile.MetisProgram.MetisApplication;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Coeus javaFX StartUp.
 */
public class Coeus4FX
        extends Application
        implements MetisApplication {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(Coeus4FX.class);

    /**
     * Main panel.
     */
    private CoeusFXMainPanel thePanel;

    /**
     * Program information.
     */
    private MetisProgram theInfo;

    @Override
    public void setProgramInfo(final MetisProgram pInfo) {
        theInfo = pInfo;
    }

    @Override
    public void init() {
        /* Protect against exceptions */
        try {
            /* Create a timer */
            if (theInfo == null) {
                theInfo = new MetisProgram(CoeusApp.class);
            }

            /* Create the Toolkit */
            final MetisFXToolkit myToolkit = new MetisFXToolkit(theInfo, false);

            /* Create the main panel */
            thePanel = new CoeusFXMainPanel(myToolkit);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            LOGGER.error("createPanel didn't complete successfully", e);
            System.exit(1);
        }
    }

    @Override
    public void start(final Stage pStage) {
        /* If we have a panel */
        if (thePanel != null) {
            /* Attach to the stage and show */
            thePanel.attachToStage(pStage);
            pStage.show();

            /* Close child windows on close */
            pStage.setOnCloseRequest(e -> thePanel.handleAppClose());
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
