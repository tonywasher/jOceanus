/* *****************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;

/**
 * FieldSet Tab pane.
 */
public class PrometheusFieldSetTabs {
    /**
     * The tabPane.
     */
    private final TethysTabPaneManager theTabs;

    /**
     * The list of panels.
     */
    private final Map<String, PrometheusFieldSetPanel<?>> thePanels;

    /**
     * Constructor.
     * @param pFactory the gui factory.
     */
    PrometheusFieldSetTabs(final TethysGuiFactory pFactory) {
        theTabs = pFactory.newTabPane();
        thePanels = new HashMap<>();
    }

    /**
     * Add panel as tab.
     * @param pName the tab name
     * @param pPanel the panel
     */
    void addPanel(final String pName,
                  final PrometheusFieldSetPanel<?> pPanel) {
        theTabs.addTabItem(pName, pPanel);
        thePanels.put(pName, pPanel);
    }

    /**
     * Obtain the component.
     * @return the component
     */
    TethysComponent getComponent() {
        return theTabs;
    }

    /**
     * Adjust visibility.
     */
    void adjustVisibilty() {
        /* Update visibility for all the panels */
        for (Map.Entry<String, PrometheusFieldSetPanel<?>> myEntry : thePanels.entrySet()) {
            final TethysTabItem myItem = theTabs.findItemByName(myEntry.getKey());
            final PrometheusFieldSetPanel<?> myPanel = myEntry.getValue();
            myItem.setVisible(myPanel.isVisible());
        }
    }
}
