/* *****************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.javafx;

import javafx.application.Preloader;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.launch.javafx.MetisFXState;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseApp;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * MoneyWise javaFX preLoader.
 */
public class MoneyWiseFXpreLoader
        extends Preloader {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MoneyWiseFXpreLoader.class);

    /**
     * javaFXState.
     */
    private MetisFXState theState;

    @Override
    public void init() {
        /* Protect against exceptions */
        try {
            /* Access program info */
            theState = new MetisFXState(new MoneyWiseApp());

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
