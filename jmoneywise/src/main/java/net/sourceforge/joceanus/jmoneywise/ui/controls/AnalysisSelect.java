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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisType;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketAttribute;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.DepositFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PayeeFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PortfolioCashFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TagFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TransactionCategoryFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Selection panel for Analysis Statement.
 */
public class AnalysisSelect
        extends JEventPanel {
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
    private static final String NLS_RANGE = MoneyWiseUIControlResource.ANALYSIS_PROMPT_RANGE.getValue();

    /**
     * Text for Filter Label.
     */
    private static final String NLS_FILTER = MoneyWiseUIControlResource.ANALYSIS_PROMPT_FILTER.getValue();

    /**
     * Text for FilterType Label.
     */
    private static final String NLS_FILTERTYPE = MoneyWiseUIControlResource.ANALYSIS_PROMPT_FILTERTYPE.getValue();

    /**
     * Text for ColumnSet Label.
     */
    private static final String NLS_COLUMNS = MoneyWiseUIControlResource.ANALYSIS_PROMPT_COLUMNSET.getValue();

    /**
     * Text for BucketType Label.
     */
    private static final String NLS_BUCKET = MoneyWiseUIControlResource.ANALYSIS_PROMPT_BUCKET.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIControlResource.ANALYSIS_TITLE.getValue();

    /**
     * Text for NoBucket.
     */
    private static final String NLS_NONE = MoneyWiseUIControlResource.ANALYSIS_BUCKET_NONE.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_FILTERTITLE = MoneyWiseUIControlResource.ANALYSIS_FILTER_TITLE.getValue();

    /**
     * View.
     */
    private final transient View theView;

    /**
     * Analysis Manager.
     */
    private AnalysisManager theManager;

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
    private final JDateDayRangeSelect theRangeSelect;

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
    private final JEnablePanel theCardPanel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * Is the control refreshing?
     */
    private boolean isRefreshing = false;

    /**
     * Select panel map.
     */
    private final transient Map<AnalysisType, AnalysisFilterSelection> theMap;

    /**
     * Constructor.
     * @param pView the view
     * @param pNewButton the new button
     */
    public AnalysisSelect(final View pView,
                          final JButton pNewButton) {
        /* Access the analysis manager */
        theView = pView;
        theManager = theView.getAnalysisManager();

        /* Create the range button */
        theRangeButton = new JButton(ArrowIcon.DOWN);
        theRangeButton.setVerticalTextPosition(AbstractButton.CENTER);
        theRangeButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the filter button */
        theFilterButton = new JButton(ArrowIcon.DOWN);
        theFilterButton.setVerticalTextPosition(AbstractButton.CENTER);
        theFilterButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the filter type button */
        theFilterTypeButton = new JScrollButton<AnalysisType>();

        /* Create the columnSet button */
        theColumnLabel = new JLabel(NLS_COLUMNS);
        theColumnButton = new JScrollButton<AnalysisColumnSet>();

        /* Create the bucket button */
        theBucketLabel = new JLabel(NLS_BUCKET);
        theBucketButton = new JScrollButton<BucketAttribute>();

        /* Create the Range Select panel */
        theRangeSelect = new JDateDayRangeSelect();

        /* Create the panel map */
        theMap = new EnumMap<AnalysisType, AnalysisFilterSelection>(AnalysisType.class);

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
        theCardPanel = new JEnablePanel();
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

        /* Create the listener */
        new AnalysisListener();
    }

    /**
     * Obtain the DateDayRange.
     * @return the range.
     */
    public JDateDayRange getRange() {
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
        JPanel myPanel = new JPanel();

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
        JDateDayRangeSelect mySelect = pSelect.getRangeSelect();
        if (mySelect != null) {
            theRangeSelect.setSelection(mySelect);
            theRangeSelect.lockPeriod(false);

            /* Update analysis for filter panels */
            setAnalysisRange(mySelect.getRange());
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
        /* Set refreshing flag */
        isRefreshing = true;

        /* Access the analysis manager */
        theManager = theView.getAnalysisManager();

        /* Update the range selection */
        JDateDayRange myRange = theView.getRange();
        theRangeSelect.setOverallRange(myRange);

        /* Update the filter selection */
        setAnalysisRange(getRange());
        checkType();

        /* Clear refreshing flag */
        isRefreshing = false;
    }

    /**
     * Declare analysis.
     * @param pRange the range
     */
    private void setAnalysisRange(final JDateDayRange pRange) {
        /* Update the filter selection */
        theAnalysis = theManager.getAnalysis(pRange);
        theDepositSelect.setAnalysis(theAnalysis);
        theCashSelect.setAnalysis(theAnalysis);
        theLoanSelect.setAnalysis(theAnalysis);
        theSecuritySelect.setAnalysis(theAnalysis);
        thePortfolioSelect.setAnalysis(theAnalysis);
        theCategorySelect.setAnalysis(theAnalysis);
        thePayeeSelect.setAnalysis(theAnalysis);
        theTaxBasisSelect.setAnalysis(theAnalysis);
        theTagSelect.setAnalysis(theAnalysis);
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
     * Listener.
     */
    private final class AnalysisListener
            implements PropertyChangeListener, ActionListener, ChangeListener {
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
         */
        private AnalysisListener() {
            /* Listen to the objects */
            theRangeButton.addActionListener(this);
            theRangeSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, this);
            theFilterButton.addActionListener(this);
            theFilterTypeButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            theBucketButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            theColumnButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            theDepositSelect.addChangeListener(this);
            theCashSelect.addChangeListener(this);
            theLoanSelect.addChangeListener(this);
            theSecuritySelect.addChangeListener(this);
            thePortfolioSelect.addChangeListener(this);
            thePayeeSelect.addChangeListener(this);
            theCategorySelect.addChangeListener(this);
            theTaxBasisSelect.addChangeListener(this);
            theTagSelect.addChangeListener(this);

            /* Access builders */
            theTypeMenuBuilder = theFilterTypeButton.getMenuBuilder();
            theTypeMenuBuilder.addChangeListener(this);
            theBucketMenuBuilder = theBucketButton.getMenuBuilder();
            theBucketMenuBuilder.addChangeListener(this);
            theColumnMenuBuilder = theColumnButton.getMenuBuilder();
            theColumnMenuBuilder.addChangeListener(this);
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
            for (BucketAttribute myAttr : theState.getType().getValues()) {
                /* If the value is a counter */
                if (myAttr.isCounter()) {
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

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();

            /* If this event relates to the RangeButton */
            if (theRangeButton.equals(o)) {
                /* Toggle visibility of range selection */
                boolean isVisible = theRangeSelect.isVisible();
                theRangeButton.setIcon(isVisible
                                                ? ArrowIcon.DOWN
                                                : ArrowIcon.UP);
                theRangeSelect.setVisible(!isVisible);
            }

            /* If this event relates to the FilterButton */
            if (theFilterButton.equals(o)) {
                /* Toggle visibility of filter selection */
                boolean isVisible = theFilterSelect.isVisible();
                theFilterButton.setIcon(isVisible
                                                 ? ArrowIcon.DOWN
                                                 : ArrowIcon.UP);
                theFilterSelect.setVisible(!isVisible);
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Ignore if we are refreshing */
            if (isRefreshing) {
                return;
            }

            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the range select panel */
            if (theRangeSelect.equals(o)) {
                /* If we have a change to the range */
                if (theState.setRange(theRangeSelect)) {
                    /* Declare new analysis */
                    setAnalysisRange(getRange());
                    checkType();
                    theState.applyState();
                    fireStateChanged();
                }

                /* If this is the filter type button */
            } else if (theFilterTypeButton.equals(o)) {
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
                    fireStateChanged();
                }

                /* If this is the bucket attribute button */
            } else if (theBucketButton.equals(o)) {
                /* Record the bucket */
                BucketAttribute myBucket = theBucketButton.getValue();
                if (theState.setBucket(myBucket)) {
                    AnalysisFilter<?, ?> myFilter = theState.getFilter();
                    if (myBucket != null) {
                        myFilter.setCurrentAttribute(myBucket);
                    }
                    theState.applyState();
                    fireStateChanged();
                }

                /* If this is the column set button */
            } else if (theColumnButton.equals(o)) {
                /* Record the columns */
                AnalysisColumnSet mySet = theColumnButton.getValue();
                if (theState.setColumns(mySet)) {
                    theState.applyState();
                    fireStateChanged();
                }
            }
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Ignore if we are refreshing */
            if (isRefreshing) {
                return;
            }

            /* Obtain source */
            Object o = pEvent.getSource();

            /* If this event relates to the FilterTypeMenuBuilder */
            if (theTypeMenuBuilder.equals(o)) {
                /* Build the analysis type menu */
                buildAnalysisTypeMenu();

                /* If this is the BucketMenuBuilder */
            } else if (theBucketMenuBuilder.equals(o)) {
                /* Build the bucket type menu */
                buildBucketMenu();

                /* If this is the ColumnSetMenuBuilder */
            } else if (theColumnMenuBuilder.equals(o)) {
                /* Build the columns menu */
                buildColumnsMenu();

                /* If this is the DepositSelect */
            } else if (theDepositSelect.equals(o)) {
                /* Create the new filter */
                DepositFilter myFilter = theDepositSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the CashSelect */
            } else if (theCashSelect.equals(o)) {
                /* Create the new filter */
                CashFilter myFilter = theCashSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the LoanSelect */
            } else if (theLoanSelect.equals(o)) {
                /* Create the new filter */
                LoanFilter myFilter = theLoanSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the security select */
            } else if (theSecuritySelect.equals(o)) {
                /* Create the new filter */
                SecurityFilter myFilter = theSecuritySelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the portfolio select */
            } else if (thePortfolioSelect.equals(o)) {
                /* Create the new filter */
                PortfolioCashFilter myFilter = thePortfolioSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the Payee select */
            } else if (thePayeeSelect.equals(o)) {
                /* Create the new filter */
                PayeeFilter myFilter = thePayeeSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the category select */
            } else if (theCategorySelect.equals(o)) {
                /* Create the new filter */
                TransactionCategoryFilter myFilter = theCategorySelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the tax basis select */
            } else if (theTaxBasisSelect.equals(o)) {
                /* Create the new filter */
                TaxBasisFilter myFilter = theTaxBasisSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();

                /* If this is the tag select */
            } else if (theTagSelect.equals(o)) {
                /* Create the new filter */
                TagFilter myFilter = theTagSelect.getFilter();
                myFilter.setCurrentAttribute(null);

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class AnalysisState {
        /**
         * The Range.
         */
        private JDateDayRange theRange;

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
        private JDateDayRange getRange() {
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
        private boolean setRange(final JDateDayRangeSelect pSelect) {
            /* Adjust the selected account */
            JDateDayRange myRange = pSelect.getRange();
            if (!Difference.isEqual(myRange, theRange)) {
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
            if (!Difference.isEqual(pType, theType)) {
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
            if (!Difference.isEqual(pBucket, theBucket)) {
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
            if (!Difference.isEqual(pColumnSet, theColumns)) {
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
        private final JDateDayRangeSelect theRangeSelect;

        /**
         * The AnalysisFilter.
         */
        private final AnalysisFilter<?, ?> theFilter;

        /**
         * Constructor.
         * @param pRangeSelect the range selection
         * @param pFilter the analysis filter
         */
        public StatementSelect(final JDateDayRangeSelect pRangeSelect,
                               final AnalysisFilter<?, ?> pFilter) {
            /* Store parameters */
            theRangeSelect = pRangeSelect;
            theFilter = pFilter;
        }

        /**
         * Obtain the RangeSelection.
         * @return the filter
         */
        public JDateDayRangeSelect getRangeSelect() {
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
