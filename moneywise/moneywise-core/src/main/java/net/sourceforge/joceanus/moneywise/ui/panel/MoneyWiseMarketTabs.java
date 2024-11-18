/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.panel;

import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.ui.panel.MoneyWiseMarketPricesTable.MoneyWiseSpotPricesPanel;
import net.sourceforge.joceanus.moneywise.ui.panel.MoneyWiseMarketRatesTable.MoneyWiseSpotRatesPanel;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUITabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUITabPaneManager.TethysUITabItem;

/**
 * Market Tab panel.
 * @author Tony Washer
 */
public class MoneyWiseMarketTabs
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Prices tab title.
     */
    private static final String TITLE_PRICES = MoneyWiseUIResource.MARKET_PRICES.getValue();

    /**
     * Rates tab title.
     */
    private static final String TITLE_RATES = MoneyWiseUIResource.MARKET_RATES.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final MoneyWiseView theView;

    /**
     * The Tabs.
     */
    private final TethysUITabPaneManager theTabs;

    /**
     * The PricesPanel.
     */
    private final MoneyWiseSpotPricesPanel thePrices;

    /**
     * The RatesPanel.
     */
    private final MoneyWiseSpotRatesPanel theRates;

    /**
     * Constructor.
     * @param pView the view
     */
    MoneyWiseMarketTabs(final MoneyWiseView pView) {
        /* Store details */
        theView = pView;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Tabbed Pane */
        theTabs = theView.getGuiFactory().paneFactory().newTabPane();

        /* Create the Prices Tab */
        thePrices = new MoneyWiseSpotPricesPanel(theView);
        theTabs.addTabItem(TITLE_PRICES, thePrices);

        /* Create the Rates Tab */
        theRates = new MoneyWiseSpotRatesPanel(theView);
        theTabs.addTabItem(TITLE_RATES, theRates);

        /* Create a listeners */
        theTabs.getEventRegistrar().addEventListener(e -> determineFocus());
        thePrices.getEventRegistrar().addEventListener(e -> setVisibility());
        theRates.getEventRegistrar().addEventListener(e -> setVisibility());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return theTabs;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the view.
     * @return the view
     */
    protected MoneyWiseView getView() {
        return theView;
    }

    /**
     * Has this set of tables got updates.
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        boolean hasUpdates = thePrices.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theRates.hasUpdates();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        /* Determine whether we have focus */
        boolean hasUpdates = thePrices.hasSession();
        if (!hasUpdates) {
            hasUpdates = theRates.hasSession();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Set visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have any locked session */
        final boolean hasSession = hasSession();

        /* Enable/Disable the Prices tab */
        boolean doEnabled = !hasSession || thePrices.hasSession();
        theTabs.enableItemByName(TITLE_PRICES, doEnabled);

        /* Enable/Disable the Rates tab */
        doEnabled = !hasSession || theRates.hasSession();
        theTabs.enableItemByName(TITLE_RATES, doEnabled);

        /* Update the top level tabs */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Access the selected component */
        final TethysUITabItem myItem = theTabs.getSelectedTab();
        final Integer myId = myItem.getId();

        /* If the selected component is Prices */
        if (myId.equals(thePrices.getId())) {
            /* Set the debug focus */
            thePrices.determineFocus();

            /* If the selected component is Rates */
        } else if (myId.equals(theRates.getId())) {
            /* Set the debug focus */
            theRates.determineFocus();
        }
    }
}
