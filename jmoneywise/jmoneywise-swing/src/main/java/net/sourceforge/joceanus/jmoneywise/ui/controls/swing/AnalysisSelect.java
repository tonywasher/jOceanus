/**
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls.swing;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisType;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketAttribute;
import net.sourceforge.joceanus.jmoneywise.ui.AnalysisColumnSet;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisView;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.swing.TethysSwingDateRangeSelect;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;

/**
 * Selection panel for Analysis Statement.
 */
public class AnalysisSelect
        extends TethysSwingEnablePanel
        implements TethysEventProvider<PrometheusDataEvent> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2389274012934107451L;

    /**
     * Strut size.
     */
    protected static final int STRUT_SIZE = 10;

    /**
     * Maximum height.
     */
    protected static final int MAX_HEIGHT = 150;

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
     * The Event Manager.
     */
    private final transient TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * View.
     */
    private final transient View theView;

    /**
     * Analysis.
     */
    private transient Analysis theAnalysis;

    /**
     * Analysis State.
     */
    private transient AnalysisState theState;

    /**
     * The savePoint.
     */
    private transient AnalysisState theSavePoint;

    /**
     * Range Button.
     */
    private final JButton theRangeButton;

    /**
     * Filter Button.
     */
    private final JButton theFilterButton;

    /**
     * Filter Type Button.
     */
    private final JScrollButton<AnalysisType> theFilterTypeButton;

    /**
     * Bucket Type Button.
     */
    private final JScrollButton<BucketAttribute> theBucketButton;

    /**
     * ColumnSet Button.
     */
    private final JScrollButton<AnalysisColumnSet> theColumnButton;

    /**
     * The bucket label.
     */
    private final JLabel theBucketLabel;

    /**
     * The column label.
     */
    private final JLabel theColumnLabel;

    /**
     * The filter detail panel.
     */
    private final JPanel theFilterDetail;

    /**
     * DateDayRange Select Panel.
     */
    private final TethysSwingDateRangeSelect theRangeSelect;

    /**
     * Filter Select Panel.
     */
    private final JPanel theFilterSelect;

    /**
     * Deposit Select Panel.
     */
    private final DepositAnalysisSelect theDepositSelect;

    /**
     * Cash Select Panel.
     */
    private final CashAnalysisSelect theCashSelect;

    /**
     * Loan Select Panel.
     */
    private final LoanAnalysisSelect theLoanSelect;

    /**
     * Security Select Panel.
     */
    private final SecurityAnalysisSelect theSecuritySelect;

    /**
     * Portfolio Select Panel.
     */
    private final PortfolioAnalysisSelect thePortfolioSelect;

    /**
     * Payee Select Panel.
     */
    private final PayeeAnalysisSelect thePayeeSelect;

    /**
     * TransCategory Select Panel.
     */
    private final TransCategoryAnalysisSelect theCategorySelect;

    /**
     * TaxBasis Select Panel.
     */
    private final TaxBasisAnalysisSelect theTaxBasisSelect;

    /**
     * TransactionTag Select Panel.
     */
    private final TransactionTagSelect theTagSelect;

    /**
     * All Select Panel.
     */
    private final AllSelect theAllSelect;

    /**
     * The card panel.
     */
    private final TethysSwingEnablePanel theCardPanel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * The analysis view.
     */
    private final transient AnalysisView theAnalysisView;

    /**
     * Is the control refreshing?
     */
    private boolean isRefreshing = false;

    /**
     * Select panel map.
     */
    private final transient Map<AnalysisType, AnalysisFilterSelection> theMap;

    /**
     * AnalysisType menu builder.
     */
    private final JScrollMenuBuilder<AnalysisType> theTypeMenuBuilder;

    /**
     * Bucket menu builder.
     */
    private final JScrollMenuBuilder<BucketAttribute> theBucketMenuBuilder;

    /**
     * Column menu builder.
     */
    private final JScrollMenuBuilder<AnalysisColumnSet> theColumnMenuBuilder;

    /**
     * Constructor.
     * @param pView the view
     * @param pAnalysisView the analysis view
     * @param pNewButton the new button
     */
    public AnalysisSelect(final View pView,
                          final AnalysisView pAnalysisView,
                          final JButton pNewButton) {
        /* Access the analysis manager */
        theView = pView;
        theAnalysisView = pAnalysisView;

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the range button */
        theRangeButton = new JButton(TethysSwingArrowIcon.DOWN);
        theRangeButton.setVerticalTextPosition(AbstractButton.CENTER);
        theRangeButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the filter button */
        theFilterButton = new JButton(TethysSwingArrowIcon.DOWN);
        theFilterButton.setVerticalTextPosition(AbstractButton.CENTER);
        theFilterButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the filter type button */
        theFilterTypeButton = new JScrollButton<>();

        /* Create the columnSet button */
        theColumnLabel = new JLabel(NLS_COLUMNS);
        theColumnButton = new JScrollButton<>();

        /* Create the bucket button */
        theBucketLabel = new JLabel(NLS_BUCKET);
        theBucketButton = new JScrollButton<>();

        /* Create the Range Select panel */
        theRangeSelect = new TethysSwingDateRangeSelect();

        /* Create the panel map */
        theMap = new EnumMap<>(AnalysisType.class);

        /* Create the filter selection panels */
        theDepositSelect = new DepositAnalysisSelect();
        theCashSelect = new CashAnalysisSelect();
        theLoanSelect = new LoanAnalysisSelect();
        theSecuritySelect = new SecurityAnalysisSelect();
        thePortfolioSelect = new PortfolioAnalysisSelect();
        thePayeeSelect = new PayeeAnalysisSelect();
        theCategorySelect = new TransCategoryAnalysisSelect();
        theTaxBasisSelect = new TaxBasisAnalysisSelect();
        theTagSelect = new TransactionTagSelect();
        theAllSelect = new AllSelect();

        /* Create the card panel */
        theCardPanel = new TethysSwingEnablePanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Create the filter detail panel */
        theFilterDetail = buildFilterDetail();

        /* Create the filter selection panel */
        theFilterSelect = buildFilterSelect();

        /* Create the control panel */
        JPanel myPanel = buildControlPanel(pNewButton);

        /* Create the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(myPanel);
        add(theRangeSelect);
        add(theFilterSelect);

        /* Initially hide the select boxes */
        theRangeSelect.setVisible(false);
        theFilterSelect.setVisible(false);

        /* Create initial state */
        theState = new AnalysisState();
        theState.showColumns(true);
        StatementSelect mySelect = new StatementSelect(theRangeSelect, AnalysisFilter.FILTER_ALL);
        selectStatement(mySelect);

        /* set maximum size */
        setMaximumSize(new Dimension(Integer.MAX_VALUE, MAX_HEIGHT));

        /* Create the listeners */
        theTypeMenuBuilder = theFilterTypeButton.getMenuBuilder();
        theTypeMenuBuilder.getEventRegistrar().addEventListener(e -> buildAnalysisTypeMenu());
        theBucketMenuBuilder = theBucketButton.getMenuBuilder();
        theBucketMenuBuilder.getEventRegistrar().addEventListener(e -> buildBucketMenu());
        theColumnMenuBuilder = theColumnButton.getMenuBuilder();
        theColumnMenuBuilder.getEventRegistrar().addEventListener(e -> buildColumnsMenu());
        theAnalysisView.getEventRegistrar().addEventListener(e -> setAnalysisView());

        /* Handle buttons */
        theRangeButton.addActionListener(e -> handleRangeButton());
        theRangeSelect.addPropertyChangeListener(TethysSwingDateRangeSelect.PROPERTY_RANGE, e -> handleNewRange());
        theFilterButton.addActionListener(e -> handleFilterButton());
        theFilterTypeButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, e -> handleFilterType());
        theBucketButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, e -> handleNewBucket());
        theColumnButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, e -> handleNewColumns());

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
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the DateDayRange.
     * @return the range.
     */
    public TethysDateRange getRange() {
        return theState.getRange();
    }

    /**
     * Obtain the analysis.
     * @return the range.
     */
    public Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the Filter.
     * @return the filter.
     */
    public AnalysisFilter<?, ?> getFilter() {
        return theState.getFilter();
    }

    /**
     * Obtain the ColumnSet.
     * @return the columnSet.
     */
    public AnalysisColumnSet getColumns() {
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
     * @param pNewButton the new button
     * @return the panel
     */
    private JPanel buildControlPanel(final JButton pNewButton) {
        /* Create the control panel */
        JPanel myPanel = new TethysSwingEnablePanel();

        /* Create the labels */
        JLabel myRangeLabel = new JLabel(NLS_RANGE);

        /* Create the panel */
        myPanel.setBorder(BorderFactory.createTitledBorder(NLS_TITLE));
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(myRangeLabel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(theRangeButton);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(Box.createHorizontalGlue());
        myPanel.add(theFilterDetail);
        myPanel.add(Box.createHorizontalGlue());
        myPanel.add(pNewButton);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));

        /* Return the panel */
        return myPanel;
    }

    /**
     * Create filter detail panel.
     * @return the panel
     */
    private JPanel buildFilterDetail() {
        /* Create the control panel */
        JPanel myPanel = new JPanel();

        /* Create the labels */
        JLabel myFilterLabel = new JLabel(NLS_FILTER);

        /* Create the panel */
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(myFilterLabel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(theFilterButton);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(Box.createHorizontalGlue());
        myPanel.add(theBucketLabel);
        myPanel.add(theColumnLabel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(theBucketButton);
        myPanel.add(theColumnButton);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));

        /* Return the panel */
        return myPanel;
    }

    /**
     * Create filter select panel.
     * @return the panel
     */
    private JPanel buildFilterSelect() {
        /* Create the filter panel */
        JPanel myPanel = new JPanel();

        /* Create the labels */
        JLabel myTypeLabel = new JLabel(NLS_FILTERTYPE);

        /* Add to the card panels */
        theCardPanel.add(theDepositSelect, AnalysisType.DEPOSIT.name());
        theCardPanel.add(theCashSelect, AnalysisType.CASH.name());
        theCardPanel.add(theLoanSelect, AnalysisType.LOAN.name());
        theCardPanel.add(theSecuritySelect, AnalysisType.SECURITY.name());
        theCardPanel.add(thePortfolioSelect, AnalysisType.PORTFOLIO.name());
        theCardPanel.add(thePayeeSelect, AnalysisType.PAYEE.name());
        theCardPanel.add(theCategorySelect, AnalysisType.CATEGORY.name());
        theCardPanel.add(theTaxBasisSelect, AnalysisType.TAXBASIS.name());
        theCardPanel.add(theTagSelect, AnalysisType.TRANSTAG.name());
        theCardPanel.add(theAllSelect, AnalysisType.ALL.name());

        /* Build the map */
        theMap.put(AnalysisType.DEPOSIT, theDepositSelect);
        theMap.put(AnalysisType.CASH, theCashSelect);
        theMap.put(AnalysisType.LOAN, theLoanSelect);
        theMap.put(AnalysisType.SECURITY, theSecuritySelect);
        theMap.put(AnalysisType.PORTFOLIO, thePortfolioSelect);
        theMap.put(AnalysisType.PAYEE, thePayeeSelect);
        theMap.put(AnalysisType.CATEGORY, theCategorySelect);
        theMap.put(AnalysisType.TAXBASIS, theTaxBasisSelect);
        theMap.put(AnalysisType.TRANSTAG, theTagSelect);
        theMap.put(AnalysisType.ALL, theAllSelect);

        /* Create the panel */
        myPanel.setBorder(BorderFactory.createTitledBorder(NLS_FILTERTITLE));
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(myTypeLabel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(theFilterTypeButton);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(Box.createHorizontalGlue());
        myPanel.add(theCardPanel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));

        /* Return the panel */
        return myPanel;
    }

    /**
     * Select Statement.
     * @param pSelect the selection
     */
    public void selectStatement(final StatementSelect pSelect) {
        /* Set refreshing flag */
        isRefreshing = true;

        /* Update the range */
        TethysSwingDateRangeSelect mySelect = pSelect.getRangeSelect();
        if (mySelect != null) {
            theRangeSelect.setSelection(mySelect);
            theRangeSelect.lockPeriod(false);
            theState.setRange(mySelect);

            /* Update the analysis */
            theAnalysisView.setRange(mySelect.getRange());
            theAnalysis = theAnalysisView.getAnalysis();
            setAnalysis();
        }

        /* Access the filter and the selection panel */
        AnalysisFilter<?, ?> myFilter = pSelect.getFilter();
        AnalysisType myType = myFilter.getAnalysisType();
        AnalysisFilterSelection myPanel = theMap.get(myType);

        /* Move correct card to front and update it */
        theLayout.show(theCardPanel, myType.name());
        myPanel.setFilter(myFilter);

        /* Determine the state */
        theState.determineState(myPanel);

        /* Clear refreshing flag */
        isRefreshing = false;
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Update the range selection */
        TethysDateRange myRange = theView.getRange();
        theRangeSelect.setOverallRange(myRange);

        /* Refresh the analysisView */
        theAnalysisView.refreshData();

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

            /* Update the filter */
            updateFilter();
        }
    }

    /**
     * Update the filter.
     */
    private void updateFilter() {
        /* Access the active panel */
        AnalysisType myType = theState.getType();
        AnalysisFilterSelection myPanel = theMap.get(myType);

        /* Update filters */
        if (myPanel != null) {
            AnalysisFilter<?, ?> myFilter = myPanel.getFilter();
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
        theSavePoint = new AnalysisState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new AnalysisState(theSavePoint);

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
                theRangeSelect.setVisible(false);
                theFilterSelect.setVisible(false);
            }

            /* else no filters available */
        } else {
            /* Enabled disable range selection */
            theRangeButton.setEnabled(false);

            /* Hide panels */
            theRangeSelect.setVisible(false);
            theFilterDetail.setVisible(false);
            theFilterSelect.setVisible(false);
            theRangeSelect.setEnabled(bEnabled);
        }
    }

    /**
     * Is there any filter available?
     * @return true/false
     */
    private boolean isAvailable() {
        /* Loop through the panels */
        Iterator<Map.Entry<AnalysisType, AnalysisFilterSelection>> myIterator = theMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<AnalysisType, AnalysisFilterSelection> myEntry = myIterator.next();

            /* If the filter is possible */
            if (myEntry.getValue().isAvailable()) {
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
        AnalysisType myType = theState.getType();

        /* If the type is selected */
        if (myType != null) {
            /* Check that the filter is appropriate */
            AnalysisFilterSelection myPanel = theMap.get(myType);
            if (myPanel.isAvailable()) {
                /* We are OK */
                AnalysisFilter<?, ?> myFilter = myPanel.getFilter();
                theState.setFilter(myFilter);
                return;
            }
        }

        /* Loop through the panels */
        Iterator<Map.Entry<AnalysisType, AnalysisFilterSelection>> myIterator = theMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<AnalysisType, AnalysisFilterSelection> myEntry = myIterator.next();

            /* If the filter is possible */
            AnalysisFilterSelection myPanel = myEntry.getValue();
            if (myPanel.isAvailable()) {
                /* Access Analysis type */
                myType = myEntry.getKey();

                /* Move correct card to front */
                theLayout.show(theCardPanel, myType.name());

                /* Obtain the relevant filter */
                AnalysisFilter<?, ?> myFilter = myPanel.getFilter();
                BucketAttribute myDefault = myType.getDefaultValue();
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
        theTypeMenuBuilder.clearMenu();

        /* Loop through the panels */
        Iterator<Map.Entry<AnalysisType, AnalysisFilterSelection>> myIterator = theMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<AnalysisType, AnalysisFilterSelection> myEntry = myIterator.next();

            /* If the filter is possible */
            if (myEntry.getValue().isAvailable()) {
                /* Create a new JMenuItem and add it to the popUp */
                theTypeMenuBuilder.addItem(myEntry.getKey());
            }
        }
    }

    /**
     * Build Bucket menu.
     */
    private void buildBucketMenu() {
        /* Reset the popUp menu */
        theBucketMenuBuilder.clearMenu();

        /* Loop through the buckets */
        AnalysisFilter<?, ?> myFilter = theState.getFilter();
        for (BucketAttribute myAttr : theState.getType().getValues()) {
            /* If the value is a counter */
            if (myAttr.isCounter() && myFilter.isRelevantCounter(myAttr)) {
                /* Create a new JMenuItem and add it to the popUp */
                theBucketMenuBuilder.addItem(myAttr);
            }
        }

        /* Add the entry for null bucket */
        theBucketMenuBuilder.addNullItem(NLS_NONE);
    }

    /**
     * Build Columns menu.
     */
    private void buildColumnsMenu() {
        /* Reset the popUp menu */
        theColumnMenuBuilder.clearMenu();

        /* Determine whether we have balances */
        boolean hasBalances = theState.getType().hasBalances();

        /* Loop through the sets */
        for (AnalysisColumnSet mySet : AnalysisColumnSet.values()) {
            /* if we have balances or this is not the balance set */
            if (hasBalances || !mySet.isBalance()) {
                /* Add the item */
                theColumnMenuBuilder.addItem(mySet);
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
    private void applyFilter(final AnalysisFilter<?, ?> pFilter) {
        /* Ignore if we are refreshing */
        if (!isRefreshing) {
            pFilter.setCurrentAttribute(theState.getBucket());
            theState.setFilter(pFilter);
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Set AnalysisView.
     */
    private void setAnalysisView() {
        /* Ignore if we are refreshing */
        if (!isRefreshing) {
            /* Declare the analysis */
            theAnalysis = theAnalysisView.getAnalysis();
            setAnalysis();

            /* Validate state and apply */
            checkType();
            theState.applyState();

            /* Notify listeners */
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
            theAnalysisView.setRange(getRange());
            theAnalysis = theAnalysisView.getAnalysis();
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
     * Handle RangeButton.
     */
    private void handleRangeButton() {
        /* Toggle visibility of range selection */
        boolean isVisible = theRangeSelect.isVisible();
        theRangeButton.setIcon(isVisible
                                         ? TethysSwingArrowIcon.DOWN
                                         : TethysSwingArrowIcon.UP);
        theRangeSelect.setVisible(!isVisible);
    }

    /**
     * Handle FilterButton.
     */
    private void handleFilterButton() {
        /* Toggle visibility of filter selection */
        boolean isVisible = theFilterSelect.isVisible();
        theFilterButton.setIcon(isVisible
                                          ? TethysSwingArrowIcon.DOWN
                                          : TethysSwingArrowIcon.UP);
        theFilterSelect.setVisible(!isVisible);
    }

    /**
     * Handle New Bucket.
     */
    private void handleNewBucket() {
        /* Ignore if we are refreshing */
        if (isRefreshing) {
            return;
        }

        BucketAttribute myBucket = theBucketButton.getValue();
        if (theState.setBucket(myBucket)) {
            AnalysisFilter<?, ?> myFilter = theState.getFilter();
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
        AnalysisColumnSet mySet = theColumnButton.getValue();
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
        AnalysisType myType = theFilterTypeButton.getValue();
        if (theState.setAnalysisType(myType)) {
            /* Determine whether we are showing balances */
            boolean showingBalances = !theState.showColumns();
            if (showingBalances && !myType.hasBalances()) {
                /* Move to columns if we have no balances */
                theState.showColumns(true);
            }

            /* Move correct card to front */
            theLayout.show(theCardPanel, myType.name());

            /* Obtain the relevant filter */
            AnalysisFilterSelection myPanel = theMap.get(myType);
            AnalysisFilter<?, ?> myFilter = myPanel.getFilter();
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
    private final class AnalysisState {
        /**
         * The Range.
         */
        private TethysDateRange theRange;

        /**
         * The AnalysisType.
         */
        private AnalysisType theType;

        /**
         * The BucketAttribute.
         */
        private BucketAttribute theBucket;

        /**
         * The ColumnSet.
         */
        private AnalysisColumnSet theColumns;

        /**
         * The filter.
         */
        private AnalysisFilter<?, ?> theFilter;

        /**
         * Are we showing Columns?
         */
        private boolean showColumns;

        /**
         * Constructor.
         */
        private AnalysisState() {
            theRange = null;
            theFilter = null;
            theType = null;
            theBucket = null;
            theColumns = AnalysisColumnSet.STANDARD;
            showColumns = true;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private AnalysisState(final AnalysisState pState) {
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
        private TethysDateRange getRange() {
            return theRange;
        }

        /**
         * Obtain the AnalysisType.
         * @return the analysis type.
         */
        private AnalysisType getType() {
            return theType;
        }

        /**
         * Obtain the BucketType.
         * @return the bucket type.
         */
        private BucketAttribute getBucket() {
            return theBucket;
        }

        /**
         * Obtain the Columns.
         * @return the columns.
         */
        private AnalysisColumnSet getColumns() {
            return theColumns;
        }

        /**
         * Obtain the Filter.
         * @return the filter.
         */
        private AnalysisFilter<?, ?> getFilter() {
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
        private void determineState(final AnalysisFilterSelection pFilter) {
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
        private boolean setRange(final TethysSwingDateRangeSelect pSelect) {
            /* Adjust the selected account */
            TethysDateRange myRange = pSelect.getRange();
            if (!MetisDifference.isEqual(myRange, theRange)) {
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
        private boolean setAnalysisType(final AnalysisType pType) {
            if (!MetisDifference.isEqual(pType, theType)) {
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
        private boolean setBucket(final BucketAttribute pBucket) {
            if (!MetisDifference.isEqual(pBucket, theBucket)) {
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
        private boolean setColumns(final AnalysisColumnSet pColumnSet) {
            if (!MetisDifference.isEqual(pColumnSet, theColumns)) {
                /* If this is the balance bucket */
                if (pColumnSet.equals(AnalysisColumnSet.BALANCE)) {
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
        private void setFilter(final AnalysisFilter<?, ?> pFilter) {
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
    public static final class StatementSelect {
        /**
         * The Range Selection.
         */
        private final TethysSwingDateRangeSelect theRangeSelect;

        /**
         * The AnalysisFilter.
         */
        private final AnalysisFilter<?, ?> theFilter;

        /**
         * Constructor.
         * @param pRangeSelect the range selection
         * @param pFilter the analysis filter
         */
        public StatementSelect(final TethysSwingDateRangeSelect pRangeSelect,
                               final AnalysisFilter<?, ?> pFilter) {
            /* Store parameters */
            theRangeSelect = pRangeSelect;
            theFilter = pFilter;
        }

        /**
         * Obtain the RangeSelection.
         * @return the filter
         */
        public TethysSwingDateRangeSelect getRangeSelect() {
            return theRangeSelect;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        public AnalysisFilter<?, ?> getFilter() {
            return theFilter;
        }
    }
}
