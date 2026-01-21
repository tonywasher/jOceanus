/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.prometheus.ui.fieldset;

import java.util.HashMap;
import java.util.Map;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUITabPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUITabPaneManager.TethysUITabItem;

/**
 * FieldSet Tab pane.
 */
public class PrometheusFieldSetTabs {
    /**
     * The tabPane.
     */
    private final TethysUITabPaneManager theTabs;

    /**
     * The list of panels.
     */
    private final Map<String, PrometheusFieldSetPanel<?>> thePanels;

    /**
     * Constructor.
     *
     * @param pFactory the gui factory.
     */
    PrometheusFieldSetTabs(final TethysUIFactory<?> pFactory) {
        theTabs = pFactory.paneFactory().newTabPane();
        thePanels = new HashMap<>();
    }

    /**
     * Add panel as tab.
     *
     * @param pName  the tab name
     * @param pPanel the panel
     */
    void addPanel(final String pName,
                  final PrometheusFieldSetPanel<?> pPanel) {
        theTabs.addTabItem(pName, pPanel);
        thePanels.put(pName, pPanel);
    }

    /**
     * Obtain the component.
     *
     * @return the component
     */
    TethysUIComponent getComponent() {
        return theTabs;
    }

    /**
     * Adjust visibility.
     */
    void adjustVisibility() {
        /* Determine whether any panels are visible */
        boolean anyVisible = false;

        /* Update visibility for all the panels */
        for (Map.Entry<String, PrometheusFieldSetPanel<?>> myEntry : thePanels.entrySet()) {
            final TethysUITabItem myItem = theTabs.findItemByName(myEntry.getKey());
            final PrometheusFieldSetPanel<?> myPanel = myEntry.getValue();
            final boolean isVisible = myPanel.isVisible();
            myItem.setVisible(isVisible);
            anyVisible |= isVisible;
        }

        /* Hide the tabs if there are none visible */
        theTabs.setVisible(anyVisible);
    }

    /**
     * Set all visible.
     */
    void setAllVisible() {
        /* Update visibility for all the panels */
        for (Map.Entry<String, PrometheusFieldSetPanel<?>> myEntry : thePanels.entrySet()) {
            final TethysUITabItem myItem = theTabs.findItemByName(myEntry.getKey());
            myItem.setVisible(true);
        }

        /* Set th tabs as visible */
        theTabs.setVisible(true);
    }
}
