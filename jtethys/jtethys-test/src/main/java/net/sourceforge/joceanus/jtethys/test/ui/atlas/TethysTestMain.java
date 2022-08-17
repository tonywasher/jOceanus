/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.test.ui.atlas;

import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIMainPanel;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUITabPaneManager;

/**
 * Test Main.
 */
public class TethysTestMain
    implements TethysUIMainPanel {
    /**
     * The main panel.
     */
    private final TethysUITabPaneManager theMain;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysTestMain(final TethysUIFactory<?> pFactory) {
        final TethysTestChart myCharts = new TethysTestChart(pFactory);
        final TethysTestButtons myButtons = new TethysTestButtons(pFactory);
        final TethysTestFields myFields = new TethysTestFields(pFactory);
        theMain = pFactory.paneFactory().newTabPane();
        theMain.addTabItem("Charts", myCharts.getComponent());
        theMain.addTabItem("Buttons", myButtons.getComponent());
        theMain.addTabItem("Fields", myFields.getComponent());
    }

    @Override
    public TethysUIComponent getComponent() {
        return theMain;
    }
}
