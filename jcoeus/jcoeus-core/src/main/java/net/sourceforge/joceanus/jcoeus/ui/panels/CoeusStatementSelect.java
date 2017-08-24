/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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

import java.time.Month;
import java.util.Iterator;

import net.sourceforge.joceanus.jcoeus.data.CoeusCalendar;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketType;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter.CoeusAnnualFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter.CoeusSnapShotFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.jcoeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
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
public class CoeusStatementSelect<N, I>
        implements TethysEventProvider<CoeusDataEvent>, TethysNode<N> {
    /**
     * Text for Market Label.
     */
    private static final String NLS_MARKET = CoeusUIResource.MARKET_PROMPT.getValue();

    /**
     * Text for Type Label.
     */
    private static final String NLS_TYPE = CoeusUIResource.TYPE_PROMPT.getValue();

    /**
     * Text for Totals Label.
     */
    private static final String NLS_TOTALS = CoeusUIResource.TOTALS_PROMPT.getValue();

    /**
     * Text for Month Label.
     */
    private static final String NLS_MONTH = CoeusUIResource.MONTH_PROMPT.getValue();

    /**
     * Text for Loan Label.
     */
    private static final String NLS_LOAN = CoeusUIResource.LOAN_PROMPT.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = CoeusUIResource.FILTER_TITLE.getValue();

    /**
     * Text for All Menu.
     */
    private static final String NLS_ALL = CoeusUIResource.MENU_ALL.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<CoeusDataEvent> theEventManager;

    /**
     * The market cache.
     */
    private final CoeusMarketCache theCache;

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
     * Market type scroll button.
     */
    private final TethysScrollButtonManager<CoeusMarketType, N, I> theMarketTypeButton;

    /**
     * Loan scroll button.
     */
    private final TethysScrollButtonManager<CoeusLoan, N, I> theLoanButton;

    /**
     * Month scroll button.
     */
    private final TethysScrollButtonManager<Month, N, I> theMonthButton;

    /**
     * Date select.
     */
    private final TethysDateButtonManager<N, I> theDateButton;

    /**
     * Card Panel.
     */
    private final TethysCardPaneManager<N, I, TethysBoxPaneManager<N, I>> theCardPane;

    /**
     * Calendar.
     */
    private CoeusCalendar theCalendar;

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
     * @param pCache the cache
     */
    public CoeusStatementSelect(final TethysGuiFactory<N, I> pFactory,
                                final CoeusMarketCache pCache) {
        /* Store parameters */
        theCache = pCache;
        theCalendar = pCache.getCalendar();

        /* Create the totals button */
        theTotalsButton = pFactory.newScrollButton();

        /* Create the market button */
        theMarketButton = pFactory.newScrollButton();
        buildMarketMenu();

        /* Create the market type button */
        theMarketTypeButton = pFactory.newScrollButton();
        buildMarketTypeMenu();

        /* Create the loan button */
        theLoanButton = pFactory.newScrollButton();

        /* Create the month button */
        theMonthButton = pFactory.newScrollButton();

        /* Create the DateButton */
        theDateButton = pFactory.newDateButton();

        /* Create initial state */
        theState = new FilterState();

        /* Create the labels */
        final TethysLabel<N, I> myTotLabel = pFactory.newLabel(NLS_TOTALS);
        final TethysLabel<N, I> myMktLabel = pFactory.newLabel(NLS_MARKET);
        final TethysLabel<N, I> myTypLabel = pFactory.newLabel(NLS_TYPE);
        final TethysLabel<N, I> myMonLabel = pFactory.newLabel(NLS_MONTH);
        final TethysLabel<N, I> myLonLabel = pFactory.newLabel(NLS_LOAN);

        /* Create Card Pane */
        theCardPane = pFactory.newCardPane();
        TethysBoxPaneManager<N, I> myBox = pFactory.newHBoxPane();
        myBox.addNode(myLonLabel);
        myBox.addNode(theLoanButton);
        theCardPane.addCard(CoeusMarketType.SNAPSHOT.toString(), myBox);
        myBox = pFactory.newHBoxPane();
        myBox.addNode(myMonLabel);
        myBox.addNode(theMonthButton);
        theCardPane.addCard(CoeusMarketType.ANNUAL.toString(), myBox);

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
        thePanel.addNode(myTypLabel);
        thePanel.addNode(theMarketTypeButton);
        thePanel.addSpacer();
        thePanel.addNode(theCardPane);
        thePanel.addSpacer();
        thePanel.addNode(myTotLabel);
        thePanel.addNode(theTotalsButton);

        /* Initialise the current state */
        theState.setProvider(CoeusMarketProvider.FUNDINGCIRCLE);
        theState.setMarketType(CoeusMarketType.SNAPSHOT);
        theState.setDate(new TethysDate());
        theState.setTotalSet(CoeusTotalSet.LOANBOOK);
        theState.applyState();

        /* Add the listeners */
        theTotalsButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewTotalSet());
        theTotalsButton.setMenuConfigurator(this::handleTotalSetMenu);
        theMarketButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewMarket());
        theMarketTypeButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewMarketType());
        theMonthButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewMonth());
        theMonthButton.setMenuConfigurator(this::handleMonthMenu);
        theLoanButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewLoan());
        theLoanButton.setMenuConfigurator(this::handleLoanMenu);
        theDateButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDate());

        /* Add the cache listener */
        theCache.getEventRegistrar().addEventListener(e -> refreshView());
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
     * Obtain the filter.
     * @return the filter
     */
    public CoeusFilter getFilter() {
        return theState.getFilter();
    }

    /**
     * Build totals menu.
     * @param pMenu the menu to build
     */
    private void handleTotalSetMenu(final TethysScrollMenu<CoeusTotalSet, I> pMenu) {
        /* Reset menu */
        pMenu.removeAllItems();

        /* Access the selected market */
        final CoeusMarketProvider myProvider = theState.getProvider();

        /* Loop through the totals */
        for (final CoeusTotalSet myTotals : CoeusTotalSet.values()) {
            /* If the totalSet is supported */
            if (myProvider.supportsTotalSet(myTotals)) {
                /* Create a new MenuItem for the totalSet */
                pMenu.addItem(myTotals);
            }
        }
    }

    /**
     * Build month menu.
     * @param pMenu the menu to build
     */
    private void handleMonthMenu(final TethysScrollMenu<Month, I> pMenu) {
        /* Reset menu */
        pMenu.removeAllItems();

        /* Add the AllMonths item */
        pMenu.addItem(null, NLS_ALL);

        /* Loop through the months */
        for (final Month myMonth : Month.values()) {
            /* If the month is available */
            if (theState.availableMonth(myMonth)) {
                /* Create a new MenuItem for the month */
                pMenu.addItem(myMonth);
            }
        }
    }

    /**
     * Build loan menu.
     * @param pMenu the menu to build
     */
    private void handleLoanMenu(final TethysScrollMenu<CoeusLoan, I> pMenu) {
        /* Reset menu */
        pMenu.removeAllItems();

        /* Add the AllLoans item */
        pMenu.addItem(null, NLS_ALL);

        /* Build the loans menu */
        theState.buildLoansMenu(pMenu);
    }

    /**
     * Build market menu.
     */
    private void buildMarketMenu() {
        /* Create builder */
        final TethysScrollMenu<CoeusMarketProvider, ?> myBuilder = theMarketButton.getMenu();

        /* Loop through the markets */
        for (final CoeusMarketProvider myMarket : CoeusMarketProvider.values()) {
            /* Create a new MenuItem for the market */
            myBuilder.addItem(myMarket);
        }
    }

    /**
     * Build market type menu.
     */
    private void buildMarketTypeMenu() {
        /* Create builder */
        final TethysScrollMenu<CoeusMarketType, ?> myBuilder = theMarketTypeButton.getMenu();

        /* Loop through the marketTypes */
        for (final CoeusMarketType myType : CoeusMarketType.values()) {
            /* Create a new MenuItem for the marketType */
            myBuilder.addItem(myType);
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
     * Handle new totalSet.
     */
    private void handleNewTotalSet() {
        /* Look for a changed totalSet */
        if (theState.setTotalSet(theTotalsButton.getValue())) {
            fireFilterChanged();
        }
    }

    /**
     * Handle new market provider.
     */
    private void handleNewMarket() {
        /* Look for a changed market provider */
        if (theState.setProvider(theMarketButton.getValue())) {
            fireSelectionChanged();
        }
    }

    /**
     * Handle new market type.
     */
    private void handleNewMarketType() {
        /* Look for a changed market type */
        if (theState.setMarketType(theMarketTypeButton.getValue())) {
            fireSelectionChanged();
        }
    }

    /**
     * Handle new date.
     */
    private void handleNewDate() {
        /* if we have a changed date */
        if (theState.setDate(theDateButton.getSelectedDate())) {
            fireSelectionChanged();
        }
    }

    /**
     * Handle new month.
     */
    private void handleNewMonth() {
        /* if we have a changed month */
        if (theState.setMonth(theMonthButton.getValue())) {
            fireSelectionChanged();
        }
    }

    /**
     * Handle new loan.
     */
    private void handleNewLoan() {
        /* if we have a changed loan */
        if (theState.setLoan(theLoanButton.getValue())) {
            fireSelectionChanged();
        }
    }

    /**
     * Fire selection changed.
     */
    private void fireSelectionChanged() {
        theEventManager.fireEvent(CoeusDataEvent.SELECTIONCHANGED);
    }

    /**
     * Fire filter changed.
     */
    private void fireFilterChanged() {
        theEventManager.fireEvent(CoeusDataEvent.FILTERCHANGED);
    }

    /**
     * Handle new loan.
     * @param pFilter the filter to set
     */
    public void setFilter(final CoeusFilter pFilter) {
        theState.setFilter(pFilter);
        theState.applyState();
    }

    /**
     * Refresh the view.
     */
    private void refreshView() {
        theCalendar = theCache.getCalendar();
        theState.allocateNewFilter();
        fireSelectionChanged();
    }

    /**
     * SavePoint values.
     */
    private final class FilterState {
        /**
         * The filter.
         */
        private CoeusFilter theFilter;

        /**
         * The marketProvider.
         */
        private CoeusMarketProvider theProvider;

        /**
         * The marketType.
         */
        private CoeusMarketType theMarketType;

        /**
         * The selectedDate.
         */
        private TethysDate theSelectedDate;

        /**
         * The loan.
         */
        private CoeusLoan theLoan;

        /**
         * The month.
         */
        private Month theMonth;

        /**
         * The totalSet.
         */
        private CoeusTotalSet theTotalSet;

        /**
         * Constructor.
         */
        FilterState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        FilterState(final FilterState pState) {
            setFilter(pState.getFilter());
        }

        /**
         * Set Filter.
         * @param pFilter the filter to set
         */
        void setFilter(final CoeusFilter pFilter) {
            theFilter = pFilter;
            theProvider = theFilter == null
                                            ? null
                                            : theFilter.getProvider();
            theMarketType = theFilter == null
                                              ? null
                                              : theFilter.getMarketType();
            theSelectedDate = theFilter == null
                                                ? null
                                                : theFilter.getSelectedDate();
            theTotalSet = theFilter == null
                                            ? null
                                            : theFilter.getTotalSet();
            if (pFilter instanceof CoeusAnnualFilter) {
                theMonth = ((CoeusAnnualFilter) pFilter).getMonth();
            }
            if (pFilter instanceof CoeusSnapShotFilter) {
                theLoan = ((CoeusSnapShotFilter) pFilter).getLoan();
            }
        }

        /**
         * Obtain the filter.
         * @return the market
         */
        CoeusFilter getFilter() {
            return theFilter;
        }

        /**
         * Obtain the selected market provider.
         * @return the market
         */
        CoeusMarketProvider getProvider() {
            return theProvider;
        }

        /**
         * Set new Date.
         * @param pDate the date
         * @return true/false did a change occur
         */
        boolean setDate(final TethysDate pDate) {
            /* Obtain the date and adjust it */
            final TethysDate myDate = pDate == null
                                                    ? null
                                                    : new TethysDate(pDate);

            /* Record any change and report change */
            if (!MetisDataDifference.isEqual(pDate, theSelectedDate)) {
                theSelectedDate = myDate;
                return allocateNewFilter();
            }
            return false;
        }

        /**
         * Set new MarketProvider.
         * @param pProvider the new market
         * @return true/false did a change occur
         */
        boolean setProvider(final CoeusMarketProvider pProvider) {
            if (!pProvider.equals(theProvider)) {
                /* Store the new provider */
                theProvider = pProvider;
                return allocateNewFilter();
            }
            return false;
        }

        /**
         * Set new MarketType.
         * @param pType the new marketType
         * @return true/false did a change occur
         */
        boolean setMarketType(final CoeusMarketType pType) {
            if (!pType.equals(theMarketType)) {
                /* Store the new marketType */
                theMarketType = pType;
                theCardPane.selectCard(theMarketType.toString());
                return allocateNewFilter();
            }
            return false;
        }

        /**
         * Set new TotalSet.
         * @param pTotals the new totalSet
         * @return true/false did a change occur
         */
        boolean setTotalSet(final CoeusTotalSet pTotals) {
            if (!pTotals.equals(theTotalSet)) {
                /* Adjust the filter */
                theTotalSet = pTotals;
                if (theFilter != null) {
                    theFilter.setTotalSet(pTotals);
                    return true;
                }
            }
            return false;
        }

        /**
         * Set new Loan.
         * @param pLoan the new loan
         * @return true/false did a change occur
         */
        boolean setLoan(final CoeusLoan pLoan) {
            if (!MetisDataDifference.isEqual(pLoan, theLoan)) {
                /* Adjust the filter */
                theLoan = pLoan;
                if (theFilter instanceof CoeusSnapShotFilter) {
                    ((CoeusSnapShotFilter) theFilter).setLoan(pLoan);
                    return true;
                }
            }
            return false;
        }

        /**
         * Set new Month.
         * @param pMonth the new month
         * @return true/false did a change occur
         */
        boolean setMonth(final Month pMonth) {
            if (!MetisDataDifference.isEqual(pMonth, theMonth)) {
                /* Adjust the filter */
                theMonth = pMonth;
                if (theFilter instanceof CoeusAnnualFilter) {
                    ((CoeusAnnualFilter) theFilter).setMonth(pMonth);
                    return true;
                }
            }
            return false;
        }

        /**
         * Apply the State.
         */
        void applyState() {
            /* Set standard values */
            theTotalsButton.setValue(theTotalSet);
            theMarketButton.setValue(theProvider);
            theMarketTypeButton.setValue(theMarketType);
            theDateButton.setSelectedDate(theSelectedDate);
            theCardPane.selectCard(theMarketType.toString());

            /* Handle month */
            if (theMonth == null) {
                theMonthButton.setValue(null, NLS_ALL);
            } else {
                theMonthButton.setValue(theMonth);
            }

            /* Handle loan */
            if (theLoan == null) {
                theLoanButton.setValue(null, NLS_ALL);
            } else {
                theLoanButton.setValue(theLoan);
            }
        }

        /**
         * Allocate new filter.
         * @return true/false did an allocation occur
         */
        boolean allocateNewFilter() {
            /* If there is an empty cache then no change */
            if (theCache.isIdle()) {
                return false;
            }

            /* Switch on market type */
            switch (theMarketType) {
                case ANNUAL:
                    allocateNewAnnualFilter();
                    return true;

                case SNAPSHOT:
                    allocateNewSnapShotFilter();
                    return true;

                default:
                    return false;
            }
        }

        /**
         * Allocate new annual filter.
         */
        void allocateNewAnnualFilter() {
            final TethysDate myAnnualDate = theCalendar.getEndOfYear(theSelectedDate);
            final CoeusAnnualFilter myFilter = new CoeusAnnualFilter(theCache.getAnnual(theProvider, myAnnualDate), theSelectedDate);
            if (theMonth != null
                && !myFilter.availableMonth(theMonth)) {
                theMonth = null;
            }
            myFilter.setMonth(theMonth);
            myFilter.setTotalSet(theTotalSet);
            theFilter = myFilter;
        }

        /**
         * available month?
         * @param pMonth the month
         * @return true/false
         */
        boolean availableMonth(final Month pMonth) {
            return theFilter instanceof CoeusAnnualFilter
                   && ((CoeusAnnualFilter) theFilter).availableMonth(pMonth);
        }

        /**
         * Allocate new snapShot filter.
         */
        void allocateNewSnapShotFilter() {
            final CoeusSnapShotFilter myFilter = new CoeusSnapShotFilter(theCache.getSnapShot(theProvider, theSelectedDate));
            if (theLoan != null
                && !myFilter.availableLoan(theLoan)) {
                theLoan = null;
            }
            myFilter.setLoan(theLoan);
            myFilter.setTotalSet(theTotalSet);
            theFilter = myFilter;
        }

        /**
         * Build loans menu.
         * @param pBuilder the menu builder
         */
        void buildLoansMenu(final TethysScrollMenu<CoeusLoan, ?> pBuilder) {
            /* Only perform for snapShots */
            if (!(theFilter instanceof CoeusSnapShotFilter)) {
                return;
            }

            /* Loop through the loans */
            final Iterator<CoeusLoan> myIterator = ((CoeusSnapShotFilter) theFilter).loanIterator();
            while (myIterator.hasNext()) {
                final CoeusLoan myLoan = myIterator.next();
                /* Create a new MenuItem for the loan */
                pBuilder.addItem(myLoan);
            }
        }
    }
}
