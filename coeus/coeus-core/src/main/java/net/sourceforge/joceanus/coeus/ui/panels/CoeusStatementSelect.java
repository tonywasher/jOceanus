/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.coeus.ui.panels;

import java.time.Month;

import net.sourceforge.joceanus.coeus.data.CoeusLoan;
import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.coeus.data.CoeusMarketType;
import net.sourceforge.joceanus.coeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.coeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.coeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUICardPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIPaneFactory;

/**
 * Filter Select.
 */
public final class CoeusStatementSelect
        implements TethysEventProvider<CoeusDataEvent>, TethysUIComponent {
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
    private final OceanusEventManager<CoeusDataEvent> theEventManager;

    /**
     * The market cache.
     */
    private final CoeusMarketCache theCache;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * Totals scroll button.
     */
    private final TethysUIScrollButtonManager<CoeusTotalSet> theTotalsButton;

    /**
     * Market scroll button.
     */
    private final TethysUIScrollButtonManager<CoeusMarketProvider> theMarketButton;

    /**
     * Market type scroll button.
     */
    private final TethysUIScrollButtonManager<CoeusMarketType> theMarketTypeButton;

    /**
     * Loan scroll button.
     */
    private final TethysUIScrollButtonManager<CoeusLoan> theLoanButton;

    /**
     * Month scroll button.
     */
    private final TethysUIScrollButtonManager<Month> theMonthButton;

    /**
     * Date select.
     */
    private final TethysUIDateButtonManager theDateButton;

    /**
     * Card Panel.
     */
    private final TethysUICardPaneManager<TethysUIBoxPaneManager> theCardPane;

    /**
     * Save button.
     */
    private final TethysUIButton theSaveButton;

    /**
     * Current state.
     */
    private CoeusStatementState theState;

    /**
     * Saved state.
     */
    private CoeusStatementState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pCache the cache
     */
    public CoeusStatementSelect(final TethysUIFactory<?> pFactory,
                                final CoeusMarketCache pCache) {
        /* Store parameters */
        theCache = pCache;

        /* Create the totals button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theTotalsButton = myButtons.newScrollButton(CoeusTotalSet.class);

        /* Create the market button */
        theMarketButton = myButtons.newScrollButton(CoeusMarketProvider.class);
        buildMarketMenu();

        /* Create the market type button */
        theMarketTypeButton = myButtons.newScrollButton(CoeusMarketType.class);
        buildMarketTypeMenu();

        /* Create the loan button */
        theLoanButton = myButtons.newScrollButton(CoeusLoan.class);

        /* Create the month button */
        theMonthButton = myButtons.newScrollButton(Month.class);

        /* Create the DateButton */
        theDateButton = myButtons.newDateButton();

        /* Create the save button */
        theSaveButton = myButtons.newButton();
        MetisIcon.configureSaveIconButton(theSaveButton);

        /* Create initial state */
        theState = new CoeusStatementState(this, pCache.getCalendar());

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myTotLabel = myControls.newLabel(NLS_TOTALS);
        final TethysUILabel myMktLabel = myControls.newLabel(NLS_MARKET);
        final TethysUILabel myTypLabel = myControls.newLabel(NLS_TYPE);
        final TethysUILabel myMonLabel = myControls.newLabel(NLS_MONTH);
        final TethysUILabel myLonLabel = myControls.newLabel(NLS_LOAN);

        /* Create Card Pane */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        theCardPane = myPanes.newCardPane();
        TethysUIBoxPaneManager myBox = myPanes.newHBoxPane();
        myBox.addNode(myLonLabel);
        myBox.addNode(theLoanButton);
        theCardPane.addCard(CoeusMarketType.SNAPSHOT.toString(), myBox);
        myBox = myPanes.newHBoxPane();
        myBox.addNode(myMonLabel);
        myBox.addNode(theMonthButton);
        theCardPane.addCard(CoeusMarketType.ANNUAL.toString(), myBox);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the selection panel */
        thePanel = myPanes.newHBoxPane();
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
        thePanel.addSpacer();
        thePanel.addNode(theSaveButton);

        /* Initialise the current state */
        theState.setProvider(CoeusMarketProvider.FUNDINGCIRCLE);
        theState.setMarketType(CoeusMarketType.SNAPSHOT);
        theState.setDate(new OceanusDate());
        theState.setTotalSet(CoeusTotalSet.LOANBOOK);
        applyState();

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
        theSaveButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(CoeusDataEvent.SAVETOFILE));

        /* Add the cache listener */
        theCache.getEventRegistrar().addEventListener(e -> refreshView());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<CoeusDataEvent> getEventRegistrar() {
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
     * Obtain the cache.
     * @return the cache
     */
    CoeusMarketCache getCache() {
        return theCache;
    }

    /**
     * Set new MarketType.
     * @param pType the new marketType
     */
    void setMarketType(final CoeusMarketType pType) {
        theCardPane.selectCard(pType.toString());
    }

    /**
     * Build totals menu.
     * @param pMenu the menu to build
     */
    private void handleTotalSetMenu(final TethysUIScrollMenu<CoeusTotalSet> pMenu) {
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
    private void handleMonthMenu(final TethysUIScrollMenu<Month> pMenu) {
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
    private void handleLoanMenu(final TethysUIScrollMenu<CoeusLoan> pMenu) {
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
        final TethysUIScrollMenu<CoeusMarketProvider> myBuilder = theMarketButton.getMenu();

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
        final TethysUIScrollMenu<CoeusMarketType> myBuilder = theMarketTypeButton.getMenu();

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
        theSavePoint = new CoeusStatementState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new CoeusStatementState(theSavePoint);

        /* Apply the state */
        applyState();
    }

    @Override
    public void setEnabled(final boolean bEnable) {
        theDateButton.setEnabled(bEnable);
        theMarketButton.setEnabled(bEnable);
        theTotalsButton.setEnabled(bEnable);
        theSaveButton.setEnabled(bEnable);
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
        applyState();
    }

    /**
     * Refresh the view.
     */
    private void refreshView() {
        theState.setCalendar(theCache.getCalendar());
        theState.allocateNewFilter();
        fireSelectionChanged();
    }

    /**
     * Apply the State.
     */
    void applyState() {
        /* Set standard values */
        theTotalsButton.setValue(theState.getTotalSet());
        theMarketButton.setValue(theState.getProvider());
        theDateButton.setSelectedDate(theState.getSelectedDate());

        /* Handle MarketType */
        final CoeusMarketType myType = theState.getMarketType();
        theMarketTypeButton.setValue(myType);
        theCardPane.selectCard(myType.toString());

        /* Handle month */
        final Month myMonth = theState.getMonth();
        if (myMonth == null) {
            theMonthButton.setValue(null, NLS_ALL);
        } else {
            theMonthButton.setValue(myMonth);
        }

        /* Handle loan */
        final CoeusLoan myLoan = theState.getLoan();
        if (myLoan == null) {
            theLoanButton.setValue(null, NLS_ALL);
        } else {
            theLoanButton.setValue(myLoan);
        }
    }
}
