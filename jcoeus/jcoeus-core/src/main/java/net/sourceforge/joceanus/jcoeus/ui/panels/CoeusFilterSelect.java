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
package net.sourceforge.joceanus.jcoeus.ui.panels;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Filter Select.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class CoeusFilterSelect<N, I>
        implements TethysEventProvider<CoeusDataEvent>, TethysNode<N> {
    /**
     * Text for Market Label.
     */
    private static final String NLS_MARKET = CoeusUIResource.MARKET_PROMPT.getValue();

    /**
     * Text for Totals Label.
     */
    private static final String NLS_TOTALS = CoeusUIResource.TOTALS_PROMPT.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = CoeusUIResource.FILTER_TITLE.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<CoeusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * Totals scroll button.
     */
    private final TethysScrollButtonManager<CoeusTotalSet, N, I> theTotalsButton;

    /**
     * Market scroll button.
     */
    private final TethysScrollButtonManager<CoeusMarketProvider, N, I> theMarketButton;

    /**
     * Date select.
     */
    private final TethysDateButtonManager<N, I> theDateButton;

    /**
     * Current state.
     */
    private FilterState theState;

    /**
     * Saved state.
     */
    private FilterState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public CoeusFilterSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the totals button */
        theTotalsButton = pFactory.newScrollButton();

        /* Create the market button */
        theMarketButton = pFactory.newScrollButton();
        buildMarketMenu();

        /* Create the DateButton */
        theDateButton = pFactory.newDateButton();
        theDateButton.setSelectedDate(new TethysDate());

        /* Create initial state */
        theState = new FilterState();
        theState.setDate(theDateButton);

        /* Create the labels */
        TethysLabel<N, I> myTotLabel = pFactory.newLabel(NLS_TOTALS);
        TethysLabel<N, I> myMktLabel = pFactory.newLabel(NLS_MARKET);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the selection panel */
        thePanel = pFactory.newHBoxPane();
        thePanel.setBorderTitle(NLS_TITLE);

        /* Define the layout */
        thePanel.addNode(myMktLabel);
        thePanel.addNode(theMarketButton);
        thePanel.addSpacer();
        thePanel.addNode(theDateButton);
        thePanel.addSpacer();
        thePanel.addNode(myTotLabel);
        thePanel.addNode(theTotalsButton);

        /* Initialise the current state */
        theState.setTotalSet(CoeusTotalSet.LOANBOOK);
        theState.setMarket(CoeusMarketProvider.FUNDINGCIRCLE);
        theState.setDate(theDateButton);
        theState.applyState();

        /* Add the listeners */
        theTotalsButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewTotalSet());
        theTotalsButton.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> handleTotalSetMenu());
        theMarketButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewMarket());
        theDateButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDate());
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public N getNode() {
        return thePanel.getNode();
    }

    @Override
    public TethysEventRegistrar<CoeusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the totals type.
     * @return the totals type
     */
    public CoeusTotalSet getTotalSet() {
        return theState.getTotalSet();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusMarketProvider getMarket() {
        return theState.getMarket();
    }

    /**
     * Obtain the selected date.
     * @return the date
     */
    public TethysDate getDate() {
        return theState.getSelectedDate();
    }

    /**
     * Build totals menu.
     */
    private void handleTotalSetMenu() {
        /* Create builder */
        TethysScrollMenu<CoeusTotalSet, ?> myBuilder = theTotalsButton.getMenu();
        myBuilder.removeAllItems();

        /* Access the selected market */
        CoeusMarketProvider myMarket = theState.getMarket();

        /* Loop through the totals */
        for (CoeusTotalSet myTotals : CoeusTotalSet.values()) {
            /* If the totalSet is supported */
            if (myMarket.supportsTotalSet(myTotals)) {
                /* Create a new MenuItem for the totalSet */
                myBuilder.addItem(myTotals);
            }
        }
    }

    /**
     * Build market menu.
     */
    private void buildMarketMenu() {
        /* Create builder */
        TethysScrollMenu<CoeusMarketProvider, ?> myBuilder = theMarketButton.getMenu();

        /* Loop through the markets */
        for (CoeusMarketProvider myMarket : CoeusMarketProvider.values()) {
            /* Create a new MenuItem for the market */
            myBuilder.addItem(myMarket);
        }
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new FilterState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new FilterState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnable) {
        theDateButton.setEnabled(bEnable);
        theMarketButton.setEnabled(bEnable);
        theTotalsButton.setEnabled(bEnable);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Handle new report.
     */
    private void handleNewTotalSet() {
        /* Look for a changed totalSet */
        if (theState.setTotalSet(theTotalsButton.getValue())) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(CoeusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new market.
     */
    private void handleNewMarket() {
        /* Look for a changed market type */
        if (theState.setMarket(theMarketButton.getValue())) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(CoeusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new date.
     */
    private void handleNewDate() {
        /* if we have a changed date and are not changing report */
        if (theState.setDate(theDateButton)) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(CoeusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class FilterState {
        /**
         * The selected market.
         */
        private CoeusMarketProvider theMarket;

        /**
         * The selected date.
         */
        private TethysDate theSelectedDate;

        /**
         * The selected totalSet.
         */
        private CoeusTotalSet theTotalSet;

        /**
         * Constructor.
         */
        private FilterState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private FilterState(final FilterState pState) {
            theMarket = pState.getMarket();
            theSelectedDate = pState.getSelectedDate();
            theTotalSet = pState.getTotalSet();
        }

        /**
         * Obtain the selected market provider.
         * @return the market
         */
        private CoeusMarketProvider getMarket() {
            return theMarket;
        }

        /**
         * Obtain the selected date.
         * @return the range
         */
        private TethysDate getSelectedDate() {
            return theSelectedDate;
        }

        /**
         * Obtain the selected totalSet.
         * @return the totalSet
         */
        private CoeusTotalSet getTotalSet() {
            return theTotalSet;
        }

        /**
         * Set new Date.
         * @param pSelect the Panel with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final TethysDateButtonManager<N, I> pSelect) {
            /* Obtain the date and adjust it */
            TethysDate mySelected = pSelect.getSelectedDate();
            TethysDate myDate = mySelected == null
                                                   ? null
                                                   : new TethysDate(mySelected);

            /* Record any change and report change */
            if (!MetisDifference.isEqual(myDate, theSelectedDate)) {
                theSelectedDate = myDate;
                return true;
            }
            return false;
        }

        /**
         * Set new Market.
         * @param pMarket the new market
         * @return true/false did a change occur
         */
        private boolean setMarket(final CoeusMarketProvider pMarket) {
            if (!pMarket.equals(theMarket)) {
                /* Store the new market */
                theMarket = pMarket;
                return true;
            }
            return false;
        }

        /**
         * Set new TotalSet.
         * @param pTotals the new totalSet
         * @return true/false did a change occur
         */
        private boolean setTotalSet(final CoeusTotalSet pTotals) {
            if (!pTotals.equals(theTotalSet)) {
                /* Store the new totals */
                theTotalSet = pTotals;
                return true;
            }
            return false;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            theTotalsButton.setValue(theTotalSet);
            theMarketButton.setValue(theMarket);
            theDateButton.setSelectedDate(theSelectedDate);
        }
    }
}
