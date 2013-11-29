/**
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jeventmanager.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jeventmanager.JEventPanel;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.EventAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.AccountFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.EventCategoryFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PayeeFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;

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
     * Current Analysis.
     */
    private Analysis theAnalysis;

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
     * DateDayRange Select Panel.
     */
    private final JDateDayRangeSelect theRangeSelect;

    /**
     * Filter Select Panel.
     */
    private final JPanel theFilterSelect;

    /**
     * Account Select Panel.
     */
    private final AccountAnalysisSelect theAccountSelect;

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
        /* Create the Buttons */
        theRangeButton = new JButton();
        theFilterButton = new JButton();
        theFilterTypeButton = new JButton();
        theBucketButton = new JButton();

        /* Create the Range Select panel */
        theRangeSelect = new JDateDayRangeSelect();

        /* Create the panel map */
        theMap = new EnumMap<AnalysisType, AnalysisFilterSelection>(AnalysisType.class);

        /* Create the filter selection panels */
        theAccountSelect = new AccountAnalysisSelect();
        theSecuritySelect = new SecurityAnalysisSelect();
        thePayeeSelect = new PayeeAnalysisSelect();
        theEventSelect = new EventCategoryAnalysisSelect();
        theTaxBasisSelect = new TaxBasisAnalysisSelect();

        /* Create the card panel */
        theCardPanel = new JEnablePanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Create the control panel */
        JPanel myPanel = buildControlPanel();

        /* Create the filter selection panel */
        theFilterSelect = buildFilterPanel();

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
        theFilterButton.addActionListener(myListener);
        theFilterTypeButton.addActionListener(myListener);
        theBucketButton.addActionListener(myListener);
        theAccountSelect.addChangeListener(myListener);
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
        JLabel myFilterLabel = new JLabel(NLS_FILTER);
        JLabel myBucketLabel = new JLabel(NLS_BUCKET);

        /* Create the panel */
        myPanel.setBorder(BorderFactory.createTitledBorder(NLS_TITLE));
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(myRangeLabel);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(theRangeButton);
        myPanel.add(Box.createRigidArea(new Dimension(STRUT_SIZE, 0)));
        myPanel.add(Box.createHorizontalGlue());
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
     * Create filter panel.
     * @return the panel
     */
    private JPanel buildFilterPanel() {
        /* Create the filter panel */
        JPanel myPanel = new JPanel();

        /* Create the labels */
        JLabel myTypeLabel = new JLabel(NLS_FILTERTYPE);

        /* Add to the card panels */
        theCardPanel.add(theAccountSelect, AnalysisType.ACCOUNT.name());
        theCardPanel.add(theSecuritySelect, AnalysisType.SECURITY.name());
        theCardPanel.add(thePayeeSelect, AnalysisType.PAYEE.name());
        theCardPanel.add(theEventSelect, AnalysisType.CATEGORY.name());
        theCardPanel.add(theTaxBasisSelect, AnalysisType.TAXBASIS.name());

        /* Build the map */
        theMap.put(AnalysisType.ACCOUNT, theAccountSelect);
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
     * Refresh data.
     * @param pView the view
     */
    public void refreshData(final View pView) {
        /* Access the analysis manager */
        theManager = pView.getAnalysisManager();

        /* Update the range selection */
        JDateDayRange myRange = pView.getRange();
        theRangeSelect.setOverallRange(myRange);

        /* Update the filter selection */
        setAnalysis();
    }

    /**
     * Declare analysis.
     */
    private void setAnalysis() {
        /* Update the filter selection */
        theAnalysis = theManager.getAnalysis(getRange());
        theAccountSelect.setAnalysis(theAnalysis);
        theSecuritySelect.setAnalysis(theAnalysis);
        theEventSelect.setAnalysis(theAnalysis);
        thePayeeSelect.setAnalysis(theAnalysis);
        theTaxBasisSelect.setAnalysis(theAnalysis);
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
        /* Determine whether there are any Accounts to select */
        // boolean acAvailable = (theAccounts != null)
        // && !theAccounts.isEmpty();

        /* Pass call on to buttons */
        // theAccountButton.setEnabled(bEnabled
        // && acAvailable);
        // theCatButton.setEnabled(bEnabled
        // && acAvailable);
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
            /* If this is the range select panel */
            if (theRangeSelect.equals(pEvent.getSource())) {
                /* If we have a change to the range */
                if (theState.setRange(theRangeSelect)) {
                    /* Declare new analysis */
                    setAnalysis();
                    theState.applyState();
                }
            }
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Obtain source */
            Object o = pEvent.getSource();

            /* If this is the AccountSelect */
            if (theAccountSelect.equals(o)) {
                /* Create the new filter */
                AccountFilter myFilter = theAccountSelect.getFilter();
                myFilter.setCurrentAttribute((AccountAttribute) theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                fireStateChanged();
            }

            /* If this is the security select */
            if (theSecuritySelect.equals(o)) {
                /* Create the new filter */
                SecurityFilter myFilter = theSecuritySelect.getFilter();
                myFilter.setCurrentAttribute((SecurityAttribute) theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                fireStateChanged();
            }

            /* If this is the Payee select */
            if (thePayeeSelect.equals(o)) {
                /* Create the new filter */
                PayeeFilter myFilter = thePayeeSelect.getFilter();
                myFilter.setCurrentAttribute((PayeeAttribute) theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                fireStateChanged();
            }

            /* If this is the category select */
            if (theEventSelect.equals(o)) {
                /* Create the new filter */
                EventCategoryFilter myFilter = theEventSelect.getFilter();
                myFilter.setCurrentAttribute((EventAttribute) theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
                fireStateChanged();
            }

            /* If this is the tax basis select */
            if (theTaxBasisSelect.equals(o)) {
                /* Create the new filter */
                TaxBasisFilter myFilter = theTaxBasisSelect.getFilter();
                myFilter.setCurrentAttribute((TaxBasisAttribute) theState.getBucket());

                /* Apply filter and notify changes */
                theState.setFilter(myFilter);
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

                /* Set new bucket type and apply state */
                theState.setBucket(theType.getDefaultValue());
                theState.applyState();
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
                theState.applyState();
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
            theType = AnalysisType.ACCOUNT;
            theBucket = theType.getDefaultValue();
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
            theFilterButton.setText("Y");
            theFilterTypeButton.setText((theType == null)
                    ? null
                    : theType.toString());
            theBucketButton.setText((theBucket == null)
                    ? null
                    : theBucket.toString());
        }

        /**
         * Obtain filter name.
         * @return the filter name
         */
    }
}
