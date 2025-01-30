/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.ui.controls;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisAttribute;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalysisManager;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisType;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisAllFilter;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseAnalysisColumnSet;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.date.OceanusDateResource;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateRangeSelector;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUICardPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Selection panel for Analysis Statement.
 */
public class MoneyWiseXAnalysisSelect
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Text for DateRange Label.
     */
    private static final String NLS_RANGE = MoneyWiseUIResource.ANALYSIS_PROMPT_RANGE.getValue();

    /**
     * Text for Filter Label.
     */
    private static final String NLS_FILTER = MoneyWiseUIResource.ANALYSIS_PROMPT_FILTER.getValue();

    /**
     * Text for FilterType Label.
     */
    private static final String NLS_FILTERTYPE = MoneyWiseUIResource.ANALYSIS_PROMPT_FILTERTYPE.getValue();

    /**
     * Text for ColumnSet Label.
     */
    private static final String NLS_COLUMNS = MoneyWiseUIResource.ANALYSIS_PROMPT_COLUMNSET.getValue();

    /**
     * Text for BucketType Label.
     */
    private static final String NLS_BUCKET = MoneyWiseUIResource.ANALYSIS_PROMPT_BUCKET.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIResource.ANALYSIS_TITLE.getValue();

    /**
     * Text for NoBucket.
     */
    private static final String NLS_NONE = MoneyWiseUIResource.ANALYSIS_BUCKET_NONE.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_FILTERTITLE = MoneyWiseUIResource.ANALYSIS_FILTER_TITLE.getValue();

    /**
     * Text for Box Title.
     */
    private static final String NLS_RANGETITLE = OceanusDateResource.TITLE_BOX.getValue();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * View.
     */
    private final MoneyWiseView theView;

    /**
     * Panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * Range Button.
     */
    private final TethysUIButton theRangeButton;

    /**
     * Filter Button.
     */
    private final TethysUIButton theFilterButton;

    /**
     * Filter Type Button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisType> theFilterTypeButton;

    /**
     * Bucket Type Button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisAttribute> theBucketButton;

    /**
     * ColumnSet Button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisColumnSet> theColumnButton;

    /**
     * The bucket label.
     */
    private final TethysUILabel theBucketLabel;

    /**
     * The column label.
     */
    private final TethysUILabel theColumnLabel;

    /**
     * The filter detail panel.
     */
    private final TethysUIBoxPaneManager theFilterDetail;

    /**
     * DateRange Select Panel.
     */
    private final TethysUIDateRangeSelector theRangeSelect;

    /**
     * Filter Select Panel.
     */
    private final TethysUIBoxPaneManager theFilterSelect;

    /**
     * Deposit Select Panel.
     */
    private final MoneyWiseXDepositAnalysisSelect theDepositSelect;

    /**
     * Cash Select Panel.
     */
    private final MoneyWiseXCashAnalysisSelect theCashSelect;

    /**
     * Loan Select Panel.
     */
    private final MoneyWiseXLoanAnalysisSelect theLoanSelect;

    /**
     * Security Select Panel.
     */
    private final MoneyWiseXSecurityAnalysisSelect theSecuritySelect;

    /**
     * Portfolio Select Panel.
     */
    private final MoneyWiseXPortfolioAnalysisSelect thePortfolioSelect;

    /**
     * Payee Select Panel.
     */
    private final MoneyWiseXPayeeAnalysisSelect thePayeeSelect;

    /**
     * TransCategory Select Panel.
     */
    private final MoneyWiseXTransCategoryAnalysisSelect theCategorySelect;

    /**
     * TaxBasis Select Panel.
     */
    private final MoneyWiseXTaxBasisAnalysisSelect theTaxBasisSelect;

    /**
     * TransactionTag Select Panel.
     */
    private final MoneyWiseXTransTagSelect theTagSelect;

    /**
     * All Select Panel.
     */
    private final MoneyWiseXAllSelect theAllSelect;

    /**
     * The card panel.
     */
    private final TethysUICardPaneManager<MoneyWiseXAnalysisFilterSelection> theCardPanel;

    /**
     * The analysis manager.
     */
    private final MoneyWiseXAnalysisManager theAnalysisMgr;

    /**
     * Select panel map.
     */
    private final Map<MoneyWiseXAnalysisType, MoneyWiseXAnalysisFilterSelection> theMap;

    /**
     * AnalysisType menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisType> theTypeMenu;

    /**
     * Bucket menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisAttribute> theBucketMenu;

    /**
     * Column menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisColumnSet> theColumnMenu;

    /**
     * Analysis.
     */
    private MoneyWiseXAnalysis theAnalysis;

    /**
     * Analysis State.
     */
    private MoneyWiseAnalysisState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseAnalysisState theSavePoint;

    /**
     * Is the control refreshing?
     */
    private boolean isRefreshing;

    /**
     * Is the range visible?
     */
    private boolean isRangeVisible;

    /**
     * Is the filter visible?
     */
    private boolean isFilterVisible;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pView the view
     * @param pAnalysisMgr the analysis manager
     * @param pNewButton the new button
     */
    public MoneyWiseXAnalysisSelect(final TethysUIFactory<?> pFactory,
                                    final MoneyWiseView pView,
                                    final MoneyWiseXAnalysisManager pAnalysisMgr,
                                    final TethysUIButton pNewButton) {
        /* Access the analysis manager */
        theView = pView;
        theAnalysisMgr = pAnalysisMgr;

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the range button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theRangeButton = myButtons.newButton();
        theRangeButton.setIcon(TethysUIArrowIconId.DOWN);
        theRangeButton.setTextAndIcon();

        /* Create the filter button */
        theFilterButton = myButtons.newButton();
        theFilterButton.setIcon(TethysUIArrowIconId.DOWN);
        theFilterButton.setTextAndIcon();

        /* Create the filter type button */
        theFilterTypeButton = myButtons.newScrollButton(MoneyWiseXAnalysisType.class);

        /* Create the columnSet button */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        theColumnLabel = myControls.newLabel(NLS_COLUMNS);
        theColumnButton = myButtons.newScrollButton(MoneyWiseAnalysisColumnSet.class);

        /* Create the bucket button */
        theBucketLabel = myControls.newLabel(NLS_BUCKET);
        theBucketButton = myButtons.newScrollButton(MoneyWiseXAnalysisAttribute.class);

        /* Create the Range Select panel */
        theRangeSelect = myButtons.newDateRangeSelector();
        theRangeSelect.setBorderTitle(NLS_RANGETITLE);

        /* Create the panel map */
        theMap = new EnumMap<>(MoneyWiseXAnalysisType.class);

        /* Create the filter selection panels */
        theDepositSelect = new MoneyWiseXDepositAnalysisSelect(pFactory);
        theCashSelect = new MoneyWiseXCashAnalysisSelect(pFactory);
        theLoanSelect = new MoneyWiseXLoanAnalysisSelect(pFactory);
        theSecuritySelect = new MoneyWiseXSecurityAnalysisSelect(pFactory);
        thePortfolioSelect = new MoneyWiseXPortfolioAnalysisSelect(pFactory);
        thePayeeSelect = new MoneyWiseXPayeeAnalysisSelect(pFactory);
        theCategorySelect = new MoneyWiseXTransCategoryAnalysisSelect(pFactory);
        theTaxBasisSelect = new MoneyWiseXTaxBasisAnalysisSelect(pFactory);
        theTagSelect = new MoneyWiseXTransTagSelect(pFactory);
        theAllSelect = new MoneyWiseXAllSelect(pFactory);

        /* Create the card panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        theCardPanel = myPanes.newCardPane();

        /* Create the filter detail panel */
        theFilterDetail = buildFilterDetail(pFactory);

        /* Create the filter selection panel */
        theFilterSelect = buildFilterSelect(pFactory);

        /* Create the control panel */
        final TethysUIBoxPaneManager myPanel = buildControlPanel(pFactory, pNewButton);

        /* Create the panel */
        thePanel = myPanes.newVBoxPane();
        thePanel.addNode(myPanel);
        thePanel.addNode(theRangeSelect);
        thePanel.addNode(theFilterSelect);

        /* Initially hide the select boxes */
        theRangeSelect.setVisible(false);
        theFilterSelect.setVisible(false);

        /* Create initial state */
        theState = new MoneyWiseAnalysisState();
        theState.showColumns(true);
        final MoneyWiseXStatementSelect mySelect = new MoneyWiseXStatementSelect(theRangeSelect, new MoneyWiseXAnalysisAllFilter());
        selectStatement(mySelect);

        /* Access the menus */
        theTypeMenu = theFilterTypeButton.getMenu();
        theBucketMenu = theBucketButton.getMenu();
        theColumnMenu = theColumnButton.getMenu();

        /* Create the listeners */
        OceanusEventRegistrar<TethysUIEvent> myRegistrar = theFilterTypeButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleFilterType());
        theFilterTypeButton.setMenuConfigurator(e -> buildAnalysisTypeMenu());
        myRegistrar = theBucketButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewBucket());
        theBucketButton.setMenuConfigurator(e -> buildBucketMenu());
        myRegistrar = theColumnButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewColumns());
        theColumnButton.setMenuConfigurator(e -> buildColumnsMenu());

        /* Handle Analysis Manager */
        theAnalysisMgr.getEventRegistrar().addEventListener(e -> refreshData());

        /* Handle buttons */
        theRangeButton.getEventRegistrar().addEventListener(e -> setRangeVisibility(!isRangeVisible));
        theFilterButton.getEventRegistrar().addEventListener(e -> setFilterVisibility(!isFilterVisible));
        theRangeSelect.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewRange());

        /* handle sub-selections */
        theDepositSelect.getEventRegistrar().addEventListener(e -> buildDepositFilter());
        theCashSelect.getEventRegistrar().addEventListener(e -> buildCashFilter());
        theLoanSelect.getEventRegistrar().addEventListener(e -> buildLoanFilter());
        theSecuritySelect.getEventRegistrar().addEventListener(e -> buildSecurityFilter());
        thePortfolioSelect.getEventRegistrar().addEventListener(e -> buildPortfolioFilter());
        thePayeeSelect.getEventRegistrar().addEventListener(e -> buildPayeeFilter());
        theCategorySelect.getEventRegistrar().addEventListener(e -> buildCategoryFilter());
        theTaxBasisSelect.getEventRegistrar().addEventListener(e -> buildTaxBasisFilter());
        theTagSelect.getEventRegistrar().addEventListener(e -> buildTagFilter());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the DateDayRange.
     * @return the range.
     */
    public OceanusDateRange getRange() {
        return theState.getRange();
    }

    /**
     * Obtain the analysis.
     * @return the range.
     */
    public MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the Filter.
     * @return the filter.
     */
    public MoneyWiseXAnalysisFilter<?, ?> getFilter() {
        return theState.getFilter();
    }

    /**
     * Obtain the ColumnSet.
     * @return the columnSet.
     */
    public MoneyWiseAnalysisColumnSet getColumns() {
        return theState.getColumns();
    }

    /**
     * Are we showing columns?
     * @return true/false.
     */
    public boolean showColumns() {
        return theState.showColumns();
    }

    /**
     * Create control panel.
     * @param pFactory the GUI factory
     * @param pNewButton the new button
     * @return the panel
     */
    private TethysUIBoxPaneManager buildControlPanel(final TethysUIFactory<?> pFactory,
                                                     final TethysUIButton pNewButton) {
        /* Create the control panel */
        final TethysUIBoxPaneManager myPanel = pFactory.paneFactory().newHBoxPane();

        /* Create the labels */
        final TethysUILabel myRangeLabel = pFactory.controlFactory().newLabel(NLS_RANGE);

        /* Create save button */
        final TethysUIButton mySave = pFactory.buttonFactory().newButton();
        MetisIcon.configureSaveIconButton(mySave);

        /* Create the panel */
        myPanel.setBorderTitle(NLS_TITLE);
        myPanel.addNode(myRangeLabel);
        myPanel.addNode(theRangeButton);
        myPanel.addSpacer();
        myPanel.addNode(theFilterDetail);
        myPanel.addSpacer();
        myPanel.addNode(mySave);
        myPanel.addNode(pNewButton);

        /* Pass through the save event */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = mySave.getEventRegistrar();
        myRegistrar.addEventListener(e -> theEventManager.fireEvent(PrometheusDataEvent.SAVETOFILE));

        /* Return the panel */
        return myPanel;
    }

    /**
     * Create filter detail panel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private TethysUIBoxPaneManager buildFilterDetail(final TethysUIFactory<?> pFactory) {
        /* Create the control panel */
        final TethysUIBoxPaneManager myPanel = pFactory.paneFactory().newHBoxPane();

        /* Create the labels */
        final TethysUILabel myFilterLabel = pFactory.controlFactory().newLabel(NLS_FILTER);

        /* Create the panel */
        myPanel.addNode(myFilterLabel);
        myPanel.addNode(theFilterButton);
        myPanel.addSpacer();
        myPanel.addNode(theBucketLabel);
        myPanel.addNode(theColumnLabel);
        myPanel.addNode(theBucketButton);
        myPanel.addNode(theColumnButton);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Create filter select panel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private TethysUIBoxPaneManager buildFilterSelect(final TethysUIFactory<?> pFactory) {
        /* Create the filter panel */
        final TethysUIBoxPaneManager myPanel = pFactory.paneFactory().newHBoxPane();

        /* Create the labels */
        final TethysUILabel myTypeLabel = pFactory.controlFactory().newLabel(NLS_FILTERTYPE);

        /* Add to the card panels */
        theCardPanel.addCard(MoneyWiseXAnalysisType.DEPOSIT.name(), theDepositSelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.CASH.name(), theCashSelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.LOAN.name(), theLoanSelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.SECURITY.name(), theSecuritySelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.PORTFOLIO.name(), thePortfolioSelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.PAYEE.name(), thePayeeSelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.CATEGORY.name(), theCategorySelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.TAXBASIS.name(), theTaxBasisSelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.TRANSTAG.name(), theTagSelect);
        theCardPanel.addCard(MoneyWiseXAnalysisType.ALL.name(), theAllSelect);

        /* Build the map */
        theMap.put(MoneyWiseXAnalysisType.DEPOSIT, theDepositSelect);
        theMap.put(MoneyWiseXAnalysisType.CASH, theCashSelect);
        theMap.put(MoneyWiseXAnalysisType.LOAN, theLoanSelect);
        theMap.put(MoneyWiseXAnalysisType.SECURITY, theSecuritySelect);
        theMap.put(MoneyWiseXAnalysisType.PORTFOLIO, thePortfolioSelect);
        theMap.put(MoneyWiseXAnalysisType.PAYEE, thePayeeSelect);
        theMap.put(MoneyWiseXAnalysisType.TAXBASIS, theTaxBasisSelect);
        theMap.put(MoneyWiseXAnalysisType.TRANSTAG, theTagSelect);
        theMap.put(MoneyWiseXAnalysisType.ALL, theAllSelect);

        /* Create the panel */
        myPanel.setBorderTitle(NLS_FILTERTITLE);
        myPanel.addNode(myTypeLabel);
        myPanel.addNode(theFilterTypeButton);
        myPanel.addSpacer();
        myPanel.addNode(theCardPanel);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Select Statement.
     * @param pSelect the selection
     */
    public void selectStatement(final MoneyWiseXStatementSelect pSelect) {
        /* Set refreshing flag */
        isRefreshing = true;

        /* Update the range */
        final TethysUIDateRangeSelector mySelect = pSelect.getRangeSelect();
        if (mySelect != null) {
            theRangeSelect.setSelection(mySelect);
            theRangeSelect.lockPeriod(false);
            theState.setRange(mySelect);

            /* Update the analysis */
            theAnalysis = theAnalysisMgr.getRangedAnalysis(mySelect.getRange());
            setAnalysis();
        }

        /* Access the filter and the selection panel */
        final MoneyWiseXAnalysisFilter<?, ?> myFilter = pSelect.getFilter();
        final MoneyWiseXAnalysisType myType = myFilter.getAnalysisType();
        final MoneyWiseXAnalysisFilterSelection myPanel = theMap.get(myType);

        /* Move correct card to front and update it */
        theCardPanel.selectCard(myType.name());
        myPanel.setFilter(myFilter);

        /* Determine the state */
        theState.determineState(myPanel);

        /* Clear refreshing flag */
        isRefreshing = false;
    }

    /**
     * Refresh data.
     */
    private void refreshData() {
        /* Update the range selection */
        final OceanusDateRange myRange = theView.getRange();
        theRangeSelect.setOverallRange(myRange);

        /* Refresh the analysisView */
        theAnalysis = theAnalysisMgr.getRangedAnalysis(getRange());

        /* Update the filter selection */
        checkType();
    }

    /**
     * Declare analysis.
     */
    private void setAnalysis() {
        /* Only update if we have an analysis */
        if (theAnalysis != null) {
            /* Update filters */
            theDepositSelect.setAnalysis(theAnalysis);
            theCashSelect.setAnalysis(theAnalysis);
            theLoanSelect.setAnalysis(theAnalysis);
            theSecuritySelect.setAnalysis(theAnalysis);
            thePortfolioSelect.setAnalysis(theAnalysis);
            theCategorySelect.setAnalysis(theAnalysis);
            thePayeeSelect.setAnalysis(theAnalysis);
            theTaxBasisSelect.setAnalysis(theAnalysis);
            theTagSelect.setAnalysis(theAnalysis);
            theAllSelect.setAnalysis(theAnalysis);

            /* Update the filter */
            updateFilter();
        }
    }

    /**
     * Update the filter.
     */
    private void updateFilter() {
        /* Access the active panel */
        final MoneyWiseXAnalysisType myType = theState.getType();
        final MoneyWiseXAnalysisFilterSelection myPanel = theMap.get(myType);

        /* Update filters */
        if (myPanel != null) {
            final MoneyWiseXAnalysisFilter<?, ?> myFilter = myPanel.getFilter();
            myFilter.setCurrentAttribute(theState.getBucket());
            theState.setFilter(myFilter);
        }

        /* Notify updated filter */
        theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWiseAnalysisState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseAnalysisState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* If there are filters available */
        if (isAvailable()) {
            /* Enabled disable range selection */
            theRangeButton.setEnabled(bEnabled);

            /* Enable filter detail */
            theFilterDetail.setVisible(true);
            theFilterButton.setEnabled(bEnabled);
            theColumnButton.setEnabled(bEnabled);
            theBucketButton.setEnabled(bEnabled);

            /* If we are disabling */
            if (!bEnabled) {
                /* Hide panels */
                setRangeVisibility(false);
                setFilterVisibility(false);
            }

            /* else no filters available */
        } else {
            /* Enabled disable range selection */
            theRangeButton.setEnabled(false);

            /* Hide panels */
            setRangeVisibility(false);
            setFilterVisibility(false);
            theFilterDetail.setVisible(false);
            theRangeSelect.setEnabled(bEnabled);
        }
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Is there any filter available?
     * @return true/false
     */
    private boolean isAvailable() {
        /* Loop through the panels */
        for (MoneyWiseXAnalysisFilterSelection myEntry : theMap.values()) {
            /* If the filter is possible */
            if (myEntry.isAvailable()) {
                /* Filter available */
                return true;
            }
        }

        /* No available filters */
        return false;
    }

    /**
     * Check analysis type.
     */
    private void checkType() {
        /* If the type is not appropriate */
        MoneyWiseXAnalysisType myType = theState.getType();

        /* If the type is selected */
        if (myType != null) {
            /* Check that the filter is appropriate */
            final MoneyWiseXAnalysisFilterSelection myPanel = theMap.get(myType);
            if (myPanel.isAvailable()) {
                /* We are OK */
                final MoneyWiseXAnalysisFilter<?, ?> myFilter = myPanel.getFilter();
                theState.setFilter(myFilter);
                return;
            }
        }

        /* Loop through the panels */
        for (Entry<MoneyWiseXAnalysisType, MoneyWiseXAnalysisFilterSelection> myEntry : theMap.entrySet()) {
            /* If the filter is possible */
            final MoneyWiseXAnalysisFilterSelection myPanel = myEntry.getValue();
            if (myPanel.isAvailable()) {
                /* Access Analysis type */
                myType = myEntry.getKey();

                /* Move correct card to front */
                theCardPanel.selectCard(myType.name());

                /* Obtain the relevant filter */
                final MoneyWiseXAnalysisFilter<?, ?> myFilter = myPanel.getFilter();
                final MoneyWiseXAnalysisAttribute myDefault = myType.getDefaultValue();
                if (myFilter != null) {
                    myFilter.setCurrentAttribute(myDefault);
                }

                /* Set new bucket type and apply state */
                theState.setAnalysisType(myType);
                theState.setFilter(myFilter);
                theState.setBucket(myDefault);
                theState.applyState();

                /* Filter available */
                return;
            }
        }

        /* No available filters */
        theState.setFilter(null);
        theState.setAnalysisType(null);
        theState.setBucket(null);
    }

    /**
     * Build AnalysisType menu.
     */
    private void buildAnalysisTypeMenu() {
        /* Reset the popUp menu */
        theTypeMenu.removeAllItems();

        /* Loop through the panels */
        for (Entry<MoneyWiseXAnalysisType, MoneyWiseXAnalysisFilterSelection> myEntry : theMap.entrySet()) {
            /* If the filter is possible */
            if (myEntry.getValue().isAvailable()) {
                /* Create a new MenuItem and add it to the popUp */
                theTypeMenu.addItem(myEntry.getKey());
            }
        }
    }

    /**
     * Build Bucket menu.
     */
    private void buildBucketMenu() {
        /* Reset the popUp menu */
        theBucketMenu.removeAllItems();

        /* Loop through the buckets */
        final MoneyWiseXAnalysisFilter<?, ?> myFilter = theState.getFilter();
        for (MoneyWiseXAnalysisAttribute myAttr : theState.getType().getValues()) {
            /* If the value is a counter */
            if (!myAttr.isPreserved()
                    && myFilter.isRelevantCounter(myAttr)) {
                /* Create a new MenuItem and add it to the popUp */
                theBucketMenu.addItem(myAttr);
            }
        }

        /* Add the entry for null bucket */
        theBucketMenu.addNullItem(NLS_NONE);
    }

    /**
     * Build Columns menu.
     */
    private void buildColumnsMenu() {
        /* Reset the popUp menu */
        theColumnMenu.removeAllItems();

        /* Determine whether we have balances */
        final boolean hasBalances = theState.getType().hasBalances();

        /* Loop through the sets */
        for (MoneyWiseAnalysisColumnSet mySet : MoneyWiseAnalysisColumnSet.values()) {
            /* if we have balances or this is not the balance set */
            if (hasBalances
                    || !mySet.isBalance()) {
                /* Add the item */
                theColumnMenu.addItem(mySet);
            }
        }
    }

    /**
     * Build Deposit Filter.
     */
    private void buildDepositFilter() {
        applyFilter(theDepositSelect.getFilter());
    }

    /**
     * Build Cash Filter.
     */
    private void buildCashFilter() {
        applyFilter(theCashSelect.getFilter());
    }

    /**
     * Build Loan Filter.
     */
    private void buildLoanFilter() {
        applyFilter(theLoanSelect.getFilter());
    }

    /**
     * Build Security Filter.
     */
    private void buildSecurityFilter() {
        applyFilter(theSecuritySelect.getFilter());
    }

    /**
     * Build Portfolio Filter.
     */
    private void buildPortfolioFilter() {
        applyFilter(thePortfolioSelect.getFilter());
    }

    /**
     * Build Payee Filter.
     */
    private void buildPayeeFilter() {
        applyFilter(thePayeeSelect.getFilter());
    }

    /**
     * Build Category Filter.
     */
    private void buildCategoryFilter() {
        applyFilter(theCategorySelect.getFilter());
    }

    /**
     * Build TaxBasis Filter.
     */
    private void buildTaxBasisFilter() {
        applyFilter(theTaxBasisSelect.getFilter());
    }

    /**
     * Build Tag Filter.
     */
    private void buildTagFilter() {
        applyFilter(theTagSelect.getFilter());
    }

    /**
     * Apply Filter.
     * @param pFilter the filter
     */
    private void applyFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* Ignore if we are refreshing */
        if (!isRefreshing) {
            final MoneyWiseXAnalysisAttribute myBucket = theState.getBucket();
            if (pFilter.isRelevantCounter(myBucket)) {
                pFilter.setCurrentAttribute(theState.getBucket());
            } else {
                theState.setBucket(pFilter.getCurrentAttribute());
            }
            theState.setFilter(pFilter);
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle New Range.
     */
    private void handleNewRange() {
        /* Ignore if we are refreshing */
        if (isRefreshing) {
            return;
        }

        /* If we have a change to the range */
        if (theState.setRange(theRangeSelect)) {
            /* Note that we are refreshing */
            isRefreshing = true;

            /* Obtain new analysis */
            theAnalysis = theAnalysisMgr.getRangedAnalysis(getRange());
            setAnalysis();

            /* Validate state and apply */
            checkType();
            theState.applyState();

            /* Remove refreshing flag and notify listeners */
            isRefreshing = false;
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Set RangeSelect visibility.
     * @param pVisible the visibility setting
     */
    private void setRangeVisibility(final boolean pVisible) {
        theRangeButton.setIcon(pVisible
                ? TethysUIArrowIconId.UP
                : TethysUIArrowIconId.DOWN);
        theRangeSelect.setVisible(pVisible);
        isRangeVisible = pVisible;
    }

    /**
     * Set FilterSelect visibility.
     * @param pVisible the visibility setting
     */
    private void setFilterVisibility(final boolean pVisible) {
        theFilterButton.setIcon(pVisible
                ? TethysUIArrowIconId.UP
                : TethysUIArrowIconId.DOWN);
        theFilterSelect.setVisible(pVisible);
        isFilterVisible = pVisible;
    }

    /**
     * Handle New Bucket.
     */
    private void handleNewBucket() {
        /* Ignore if we are refreshing */
        if (isRefreshing) {
            return;
        }

        final MoneyWiseXAnalysisAttribute myBucket = theBucketButton.getValue();
        if (theState.setBucket(myBucket)) {
            final MoneyWiseXAnalysisFilter<?, ?> myFilter = theState.getFilter();
            if (myBucket != null) {
                myFilter.setCurrentAttribute(myBucket);
            }
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle New Columns.
     */
    private void handleNewColumns() {
        /* Ignore if we are refreshing */
        if (isRefreshing) {
            return;
        }

        /* Record the columns */
        final MoneyWiseAnalysisColumnSet mySet = theColumnButton.getValue();
        if (theState.setColumns(mySet)) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle FilterType.
     */
    private void handleFilterType() {
        /* Ignore if we are refreshing */
        if (isRefreshing) {
            return;
        }

        /* If the type has changed */
        final MoneyWiseXAnalysisType myType = theFilterTypeButton.getValue();
        if (theState.setAnalysisType(myType)) {
            /* Determine whether we are showing balances */
            final boolean showingBalances = !theState.showColumns();
            if (showingBalances && !myType.hasBalances()) {
                /* Move to columns if we have no balances */
                theState.showColumns(true);
            }

            /* Move correct card to front */
            theCardPanel.selectCard(myType.name());

            /* Obtain the relevant filter */
            final MoneyWiseXAnalysisFilterSelection myPanel = theMap.get(myType);
            final MoneyWiseXAnalysisFilter<?, ?> myFilter = myPanel.getFilter();
            myFilter.setCurrentAttribute(myType.getDefaultValue());

            /* Set new bucket type and apply state */
            theState.setFilter(myFilter);
            theState.setBucket(myFilter.getCurrentAttribute());
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class MoneyWiseAnalysisState {
        /**
         * The Range.
         */
        private OceanusDateRange theRange;

        /**
         * The AnalysisType.
         */
        private MoneyWiseXAnalysisType theType;

        /**
         * The BucketAttribute.
         */
        private MoneyWiseXAnalysisAttribute theBucket;

        /**
         * The ColumnSet.
         */
        private MoneyWiseAnalysisColumnSet theColumns;

        /**
         * The filter.
         */
        private MoneyWiseXAnalysisFilter<?, ?> theFilter;

        /**
         * Are we showing Columns?
         */
        private boolean showColumns;

        /**
         * Constructor.
         */
        private MoneyWiseAnalysisState() {
            theRange = null;
            theFilter = null;
            theType = null;
            theBucket = null;
            theColumns = MoneyWiseAnalysisColumnSet.STANDARD;
            showColumns = true;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseAnalysisState(final MoneyWiseAnalysisState pState) {
            theRange = pState.getRange();
            theFilter = pState.getFilter();
            theType = pState.getType();
            theBucket = pState.getBucket();
            theColumns = pState.getColumns();
            showColumns = pState.showColumns();
        }

        /**
         * Obtain the DateDayRange.
         * @return the range.
         */
        private OceanusDateRange getRange() {
            return theRange;
        }

        /**
         * Obtain the AnalysisType.
         * @return the analysis type.
         */
        private MoneyWiseXAnalysisType getType() {
            return theType;
        }

        /**
         * Obtain the BucketType.
         * @return the bucket type.
         */
        private MoneyWiseXAnalysisAttribute getBucket() {
            return theBucket;
        }

        /**
         * Obtain the Columns.
         * @return the columns.
         */
        private MoneyWiseAnalysisColumnSet getColumns() {
            return theColumns;
        }

        /**
         * Obtain the Filter.
         * @return the filter.
         */
        private MoneyWiseXAnalysisFilter<?, ?> getFilter() {
            return theFilter;
        }

        /**
         * Are we showing columns?
         * @return true/false.
         */
        private boolean showColumns() {
            return showColumns;
        }

        /**
         * Determine selection from panels.
         * @param pFilter selection panel
         */
        private void determineState(final MoneyWiseXAnalysisFilterSelection pFilter) {
            /* Update the selection panels */
            theRange = theRangeSelect.getRange();
            theFilter = pFilter.getFilter();
            theType = theFilter.getAnalysisType();
            theBucket = theFilter.getCurrentAttribute();
            showColumns = theBucket == null;
            applyState();
        }

        /**
         * Set new Range from select panel.
         * @param pSelect the selection panel
         * @return true/false did a change occur
         */
        private boolean setRange(final TethysUIDateRangeSelector pSelect) {
            /* Adjust the selected account */
            final OceanusDateRange myRange = pSelect.getRange();
            if (!MetisDataDifference.isEqual(myRange, theRange)) {
                theRange = myRange;
                return true;
            }
            return false;
        }

        /**
         * Set new analysis type.
         * @param pType the analysis type
         * @return true/false did a change occur
         */
        private boolean setAnalysisType(final MoneyWiseXAnalysisType pType) {
            if (!MetisDataDifference.isEqual(pType, theType)) {
                theType = pType;
                return true;
            }
            return false;
        }

        /**
         * Set new bucket type.
         * @param pBucket the bucket type
         * @return true/false did a change occur
         */
        private boolean setBucket(final MoneyWiseXAnalysisAttribute pBucket) {
            if (!MetisDataDifference.isEqual(pBucket, theBucket)) {
                /* If this is the null bucket */
                if (pBucket == null) {
                    showColumns = true;
                } else {
                    theBucket = pBucket;
                }
                return true;
            }
            return false;
        }

        /**
         * Set new column set.
         * @param pColumnSet the column set
         * @return true/false did a change occur
         */
        private boolean setColumns(final MoneyWiseAnalysisColumnSet pColumnSet) {
            if (!MetisDataDifference.isEqual(pColumnSet, theColumns)) {
                /* If this is the balance bucket */
                if (pColumnSet.equals(MoneyWiseAnalysisColumnSet.BALANCE)) {
                    showColumns = false;
                } else {
                    theColumns = pColumnSet;
                }
                return true;
            }
            return false;
        }

        /**
         * Set filter.
         * @param pFilter the filter
         */
        private void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
            theFilter = pFilter;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theRangeButton.setText(theRange.toString());
            theFilterButton.setText((theFilter == null)
                    ? null
                    : theFilter.getName());
            theFilterTypeButton.setValue(theType);
            theBucketButton.setValue(theBucket);
            theColumnButton.setValue(theColumns);
            showColumns(showColumns);
        }

        /**
         * Show Columns.
         * @param pShow true/false
         */
        private void showColumns(final boolean pShow) {
            /* Show columns */
            theColumnLabel.setVisible(pShow);
            theColumnButton.setVisible(pShow);

            /* Hide buckets */
            theBucketLabel.setVisible(!pShow);
            theBucketButton.setVisible(!pShow);

            /* Record details */
            showColumns = pShow;
        }
    }

    /**
     * The Statement Select class.
     */
    public static final class MoneyWiseXStatementSelect {
        /**
         * The Range Selection.
         */
        private final TethysUIDateRangeSelector theRangeSelect;

        /**
         * The AnalysisFilter.
         */
        private final MoneyWiseXAnalysisFilter<?, ?> theFilter;

        /**
         * Constructor.
         * @param pRangeSelect the range selection
         * @param pFilter the analysis filter
         */
        public MoneyWiseXStatementSelect(final TethysUIDateRangeSelector pRangeSelect,
                                         final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
            /* Store parameters */
            theRangeSelect = pRangeSelect;
            theFilter = pFilter;
        }

        /**
         * Obtain the RangeSelection.
         * @return the filter
         */
        public TethysUIDateRangeSelector getRangeSelect() {
            return theRangeSelect;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        public MoneyWiseXAnalysisFilter<?, ?> getFilter() {
            return theFilter;
        }
    }
}
