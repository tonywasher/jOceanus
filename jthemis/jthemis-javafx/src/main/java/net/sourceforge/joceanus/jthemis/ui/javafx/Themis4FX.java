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

import javafx.application.Application;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.atlas.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.atlas.profile.MetisProgram.MetisApplication;
import net.sourceforge.joceanus.jmetis.atlas.threads.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ui.ThemisApp;

/**
 * Themis javaFX entryPoint.
 */
public class Themis4FX
        extends Application
        implements MetisApplication {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Themis4FX.class);

    /**
     * Main panel.
     */
    private ThemisFXSvnManager thePanel;

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
                theInfo = new MetisProgram(ThemisApp.class);
            }

            /* Create the Toolkit */
            MetisFXToolkit myToolkit = new MetisFXToolkit(theInfo, false);

            /* Create the main panel */
            thePanel = new ThemisFXSvnManager(myToolkit);

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
