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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.EventCategoryFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PayeeFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AnalysisSelect.class.getName());

    /**
     * Text for DateRange Label.
     */
    private static final String NLS_RANGE = NLS_BUNDLE.getString("DateRange");

    /**
     * Text for Filter Label.
     */
    private static final String NLS_FILTER = NLS_BUNDLE.getString("Filter");

    /**
     * Text for FilterType Label.
     */
    private static final String NLS_FILTERTYPE = NLS_BUNDLE.getString("FilterType");

    /**
     * Text for BucketType Label.
     */
    private static final String NLS_BUCKET = NLS_BUNDLE.getString("Bucket");

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("Title");

    /**
     * Text for Title.
     */
    private static final String NLS_FILTERTITLE = NLS_BUNDLE.getString("FilterTitle");

    /**
     * Analysis Manager.
     */
    private AnalysisManager theManager;

    /**
     * Analysis State.
     */
    private AnalysisState theState;

    /**
     * The savePoint.
     */
    private AnalysisState theSavePoint;

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
    private final JButton theFilterTypeButton;

    /**
     * Bucket Type Button.
     */
    private final JButton theBucketButton;

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
     * Payee Select Panel.
     */
    private final PayeeAnalysisSelect thePayeeSelect;

    /**
     * EventCategory Select Panel.
     */
    private final EventCategoryAnalysisSelect theEventSelect;

    /**
     * TaxBasis Select Panel.
     */
    private final TaxBasisAnalysisSelect theTaxBasisSelect;

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
    private final Map<AnalysisType, AnalysisFilterSelection> theMap;

    /**
     * Obtain the DateDayRange.
     * @return the range.
     */
    public JDateDayRange getRange() {
        return theState.getRange();
    }

    /**
     * Obtain the Filter.
     * @return the filter.
     */
    public AnalysisFilter<?> getFilter() {
        return theState.getFilter();
    }

    /**
     * Constructor.
     */
    public AnalysisSelect() {
        /* Create the range button */
        theRangeButton = new JButton(ArrowIcon.DOWN);
        theRangeButton.setVerticalTextPosition(AbstractButton.CENTER);
        theRangeButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the filter button */
        theFilterButton = new JButton(ArrowIcon.DOWN);
        theFilterButton.setVerticalTextPosition(AbstractButton.CENTER);
        theFilterButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the filter type button */
        theFilterTypeButton = new JButton(ArrowIcon.DOWN);
        theFilterTypeButton.setVerticalTextPosition(AbstractButton.CENTER);
        theFilterTypeButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the bucket button */
        theBucketButton = new JButton(ArrowIcon.DOWN);
        theBucketButton.setVerticalTextPosition(AbstractButton.CENTER);
        theBucketButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the Range Select panel */
        theRangeSelect = new JDateDayRangeSelect();

        /* Create the panel map */
        theMap = new EnumMap<AnalysisType, AnalysisFilterSelection>(AnalysisType.class);

        /* Create the filter selection panels */
        theDepositSelect = new DepositAnalysisSelect();
        theCashSelect = new CashAnalysisSelect();
        theLoanSelect = new LoanAnalysisSelect();
        theSecuritySelect = new SecurityAnalysisSelect();
        thePayeeSelect = new PayeeAnalysisSelect();
        theEventSelect = new EventCategoryAnalysisSelect();
        theTaxBasisSelect = new TaxBasisAnalysisSelect();

        /* Create the card panel */
        theCardPanel = new JEnablePanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Create the filter detail panel */
        theFilterDetail = buildFilterDetail();

        /* Create the filter selection panel */
        theFilterSelect = buildFilterSelect();

        /* Create the control panel */
        JPanel myPanel = buildControlPanel();

        /* Create the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(myPanel);
        add(Box.createRigidArea(new Dimension(0, STRUT_SIZE)));
        add(theRangeSelect);
        add(Box.createRigidArea(new Dimension(0, STRUT_SIZE)));
        add(theFilterSelect);

        /* Initially hide the select boxes */
        theRangeSelect.setVisible(false);
        theFilterSelect.setVisible(false);

        /* Create initial state */
        theState = new AnalysisState();
        theState.setRange(theRangeSelect);
        theState.applyState();

        /* Create the listener */
        AnalysisListener myListener = new AnalysisListener();
        theRangeButton.addActionListener(myListener);
        theRangeSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, myListener);
        theFilterButton.addActionListener(myListener);
        theFilterTypeButton.addActionListener(myListener);
        theBucketButton.addActionListener(myListener);
        theDepositSelect.addChangeListener(myListener);
        theCashSelect.addChangeListener(myListener);
        theLoanSelect.addChangeListener(myListener);
        theSecuritySelect.addChangeListener(myListener);
        thePayeeSelect.addChangeListener(myListener);
        theEventSelect.addChangeListener(myListener);
        theTaxBasisSelect.addChangeListener(myListener);
    }

    /**
     * Create control panel.
     * @return the panel
     */
    private JPanel buildControlPanel() {
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
        JLabel myBucketLabel = new JLabel(NLS_BUCKET);

        /* Create the panel */
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(myFilterLabel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(theFilterButton);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(Box.createHorizontalGlue());
        myPanel.add(myBucketLabel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(theBucketButton);
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
        theCardPanel.add(thePayeeSelect, AnalysisType.PAYEE.name());
        theCardPanel.add(theEventSelect, AnalysisType.CATEGORY.name());
        theCardPanel.add(theTaxBasisSelect, AnalysisType.TAXBASIS.name());

        /* Build the map */
        theMap.put(AnalysisType.DEPOSIT, theDepositSelect);
        theMap.put(AnalysisType.CASH, theCashSelect);
        theMap.put(AnalysisType.LOAN, theLoanSelect);
        theMap.put(AnalysisType.SECURITY, theSecuritySelect);
        theMap.put(AnalysisType.PAYEE, thePayeeSelect);
        theMap.put(AnalysisType.CATEGORY, theEventSelect);
        theMap.put(AnalysisType.TAXBASIS, theTaxBasisSelect);

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
        theRangeSelect.setSelection(mySelect);
        theRangeSelect.lockPeriod(false);

        /* Update analysis for filter panels */
        setAnalysis(mySelect.getRange());

        /* Access the filter and the selection panel */
        AnalysisFilter<?> myFilter = pSelect.getFilter();
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
     * @param pView the view
     */
    public void refreshData(final View pView) {
        /* Set refreshing flag */
        isRefreshing = true;

        /* Access the analysis manager */
        theManager = pView.getAnalysisManager();

        /* Update the range selection */
        JDateDayRange myRange = pView.getRange();
        theRangeSelect.setOverallRange(myRange);

        /* Update the filter selection */
        setAnalysis(getRange());
        checkType();

        /* Clear refreshing flag */
        isRefreshing = false;
    }

    /**
     * Declare analysis.
     * @param pRange the range
     */
    private void setAnalysis(final JDateDayRange pRange) {
        /* Update the filter selection */
        Analysis myAnalysis = theManager.getAnalysis(pRange);
        theDepositSelect.setAnalysis(myAnalysis);
        theCashSelect.setAnalysis(myAnalysis);
        theLoanSelect.setAnalysis(myAnalysis);
        theSecuritySelect.setAnalysis(myAnalysis);
        theEventSelect.setAnalysis(myAnalysis);
        thePayeeSelect.setAnalysis(myAnalysis);
        theTaxBasisSelect.setAnalysis(myAnalysis);
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
                AnalysisFilter<?> myFilter = myPanel.getFilter();
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
                AnalysisFilter<?> myFilter = myPanel.getFilter();
                myFilter.setCurrentAttribute(myType.getDefaultValue());

                /* Set new bucket type and apply state */
                theState.setAnalysisType(myType);
                theState.setFilter(myFilter);
                theState.setBucket(myFilter.getCurrentAttribute());
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
         * Show AnalysisType menu.
         */
        private void showAnalysisTypeMenu() {
            /* Create a new popUp menu */
            JPopupMenu myPopUp = new JPopupMenu();

            /* Loop through the panels */
            Iterator<Map.Entry<AnalysisType, AnalysisFilterSelection>> myIterator = theMap.entrySet().iterator();
            while (myIterator.hasNext()) {
                Map.Entry<AnalysisType, AnalysisFilterSelection> myEntry = myIterator.next();

                /* If the filter is possible */
                if (myEntry.getValue().isAvailable()) {
                    /* Create a new JMenuItem and add it to the popUp */
                    AnalysisAction myAction = new AnalysisAction(myEntry.getKey());
                    JMenuItem myItem = new JMenuItem(myAction);
                    myPopUp.add(myItem);
                }
            }

            /* Show the AnalysisType menu in the correct place */
            Rectangle myLoc = theFilterTypeButton.getBounds();
            myPopUp.show(theFilterTypeButton, 0, myLoc.height);
        }

        /**
         * Show Bucket menu.
         */
        private void showBucketMenu() {
            /* Create a new popUp menu */
            JPopupMenu myPopUp = new JPopupMenu();

            /* Loop through the buckets */
            for (BucketAttribute myAttr : theState.getType().getValues()) {
                /* If the value is a counter */
                if (myAttr.isCounter()) {
                    /* Create a new JMenuItem and add it to the popUp */
                    BucketAction myAction = new BucketAction(myAttr);
                    JMenuItem myItem = new JMenuItem(myAction);
                    myPopUp.add(myItem);
                }
            }

            /* Show the Bucket menu in the correct place */
            Rectangle myLoc = theBucketButton.getBounds();
            myPopUp.show(theBucketButton, 0, myLoc.height);
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();

            /* If this event relates to the RangeButton */
            if (theRangeButton.equals(o)) {
                /* Toggle visibility of range selection */
                boolean isVisible = theRangeSelect.isVisible();
                theRangeSelect.setVisible(!isVisible);
            }

            /* If this event relates to the FilterButton */
            if (theFilterButton.equals(o)) {
                /* Toggle visibility of filter selection */
                boolean isVisible = theFilterSelect.isVisible();
                theFilterSelect.setVisible(!isVisible);
            }

            /* If this event relates to the FilterTypeButton */
            if (theFilterTypeButton.equals(o)) {
                /* Show the analysis type menu */
                showAnalysisTypeMenu();
            }

            /* If this event relates to the BucketTypeButton */
            if (theBucketButton.equals(o)) {
                /* Show the bucket type menu */
                showBucketMenu();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Ignore if we are refreshing */
            if (isRefreshing) {
                return;
            }

            /* If this is the range select panel */
            if (theRangeSelect.equals(pEvent.getSource())) {
                /* If we have a change to the range */
                if (theState.setRange(theRangeSelect)) {
                    /* Declare new analysis */
                    setAnalysis(getRange());
                    checkType();
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

            /* If this is the DepositSelect */
            if (theDepositSelect.equals(o)) {
                /* Create the new filter */
                DepositFilter myFilter = theDepositSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }

            /* If this is the CashSelect */
            if (theCashSelect.equals(o)) {
                /* Create the new filter */
                CashFilter myFilter = theCashSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }

            /* If this is the LoanSelect */
            if (theLoanSelect.equals(o)) {
                /* Create the new filter */
                LoanFilter myFilter = theLoanSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }

            /* If this is the security select */
            if (theSecuritySelect.equals(o)) {
                /* Create the new filter */
                SecurityFilter myFilter = theSecuritySelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }

            /* If this is the Payee select */
            if (thePayeeSelect.equals(o)) {
                /* Create the new filter */
                PayeeFilter myFilter = thePayeeSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }

            /* If this is the category select */
            if (theEventSelect.equals(o)) {
                /* Create the new filter */
                EventCategoryFilter myFilter = theEventSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }

            /* If this is the tax basis select */
            if (theTaxBasisSelect.equals(o)) {
                /* Create the new filter */
                TaxBasisFilter myFilter = theTaxBasisSelect.getFilter();
                myFilter.setCurrentAttribute(theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * Analysis action class.
     */
    private final class AnalysisAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6340602588329110592L;

        /**
         * Analysis Type.
         */
        private final AnalysisType theType;

        /**
         * Constructor.
         * @param pType the analysis type
         */
        private AnalysisAction(final AnalysisType pType) {
            super(pType.toString());
            theType = pType;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If the type has changed */
            if (theState.setAnalysisType(theType)) {
                /* Move correct card to front */
                theLayout.show(theCardPanel, theType.name());

                /* Obtain the relevant filter */
                AnalysisFilterSelection myPanel = theMap.get(theType);
                AnalysisFilter<?> myFilter = myPanel.getFilter();
                myFilter.setCurrentAttribute(theType.getDefaultValue());

                /* Set new bucket type and apply state */
                theState.setFilter(myFilter);
                theState.setBucket(myFilter.getCurrentAttribute());
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * Bucket action class.
     */
    private final class BucketAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -3522898133205693822L;

        /**
         * Bucket Type.
         */
        private final BucketAttribute theBucket;

        /**
         * Constructor.
         * @param pBucket the bucket
         */
        private BucketAction(final BucketAttribute pBucket) {
            super(pBucket.toString());
            theBucket = pBucket;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Record the bucket */
            if (theState.setBucket(theBucket)) {
                AnalysisFilter<?> myFilter = theState.getFilter();
                myFilter.setCurrentAttribute(theBucket);
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
         * The filter.
         */
        private AnalysisFilter<?> theFilter;

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
         * Obtain the Filter.
         * @return the filter.
         */
        private AnalysisFilter<?> getFilter() {
            return theFilter;
        }

        /**
         * Constructor.
         */
        private AnalysisState() {
            theRange = null;
            theFilter = null;
            theType = null;
            theBucket = null;
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
                theBucket = pBucket;
                return true;
            }
            return false;
        }

        /**
         * Set filter.
         * @param pFilter the filter
         */
        private void setFilter(final AnalysisFilter<?> pFilter) {
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
            theFilterTypeButton.setText((theType == null)
                                                         ? null
                                                         : theType.toString());
            theBucketButton.setText((theBucket == null)
                                                       ? null
                                                       : theBucket.toString());
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
        private final AnalysisFilter<?> theFilter;

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
        public AnalysisFilter<?> getFilter() {
            return theFilter;
        }

        /**
         * Constructor.
         * @param pRangeSelect the range selection
         * @param pFilter the analysis filter
         */
        public StatementSelect(final JDateDayRangeSelect pRangeSelect,
                               final AnalysisFilter<?> pFilter) {
            /* Store parameters */
            theRangeSelect = pRangeSelect;
            theFilter = pFilter;
        }
    }
}
