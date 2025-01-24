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
package net.sourceforge.joceanus.moneywise.lethe.ui.controls;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.moneywise.lethe.reports.MoneyWiseReportType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.oceanus.date.OceanusDatePeriod;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateRangeSelector;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

/**
 * Report selection panel.
 */
public class MoneyWiseReportSelect
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Text for Report Label.
     */
    private static final String NLS_REPORT = MoneyWiseUIResource.REPORT_PROMPT.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIResource.REPORT_TITLE.getValue();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * Reports scroll button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseReportType> theReportButton;

    /**
     * Holding scroll button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisSecurityBucket> theHoldingButton;

    /**
     * Range select.
     */
    private final TethysUIDateRangeSelector theRangeSelect;

    /**
     * Print button.
     */
    private final TethysUIButton thePrintButton;

    /**
     * Save button.
     */
    private final TethysUIButton theSaveButton;

    /**
     * Current state.
     */
    private MoneyWiseReportState theState;

    /**
     * Saved state.
     */
    private MoneyWiseReportState theSavePoint;

    /**
     * Active flag.
     */
    private boolean isActive;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public MoneyWiseReportSelect(final TethysUIFactory<?> pFactory) {
        /* Create the buttons */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theReportButton = myButtons.newScrollButton(MoneyWiseReportType.class);
        theHoldingButton = myButtons.newScrollButton(MoneyWiseAnalysisSecurityBucket.class);

        /* Create the Range Select and disable its border */
        theRangeSelect = myButtons.newDateRangeSelector();

        /* Create the labels */
        final TethysUILabel myRepLabel = pFactory.controlFactory().newLabel(NLS_REPORT);

        /* Create the print button */
        thePrintButton = myButtons.newButton();
        MetisIcon.configurePrintIconButton(thePrintButton);

        /* Create the save button */
        theSaveButton = myButtons.newButton();
        MetisIcon.configureSaveIconButton(theSaveButton);

        /* Create initial state */
        theState = new MoneyWiseReportState();
        theState.setRange(theRangeSelect);
        theState.setType(MoneyWiseReportType.getDefault());

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the selection panel */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.setBorderTitle(NLS_TITLE);

        /* Define the layout */
        thePanel.addNode(myRepLabel);
        thePanel.addNode(theReportButton);
        thePanel.addSpacer();
        thePanel.addNode(theHoldingButton);
        thePanel.addSpacer();
        thePanel.addNode(theRangeSelect);
        thePanel.addSpacer();
        thePanel.addNode(thePrintButton);
        thePanel.addNode(theSaveButton);

        /* Add the listeners */
        theReportButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewReport());
        theReportButton.setMenuConfigurator(e -> buildReportMenu());
        theHoldingButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewSecurity());
        theHoldingButton.setMenuConfigurator(e -> buildHoldingMenu());
        thePrintButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusDataEvent.PRINT));
        theSaveButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusDataEvent.SAVETOFILE));
        theRangeSelect.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewRange());
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
     * Obtain the report type.
     * @return the report type
     */
    public MoneyWiseReportType getReportType() {
        return theState.getType();
    }

    /**
     * Obtain the selected date range.
     * @return the date range
     */
    public OceanusDateRange getDateRange() {
        return theState.getRange();
    }

    /**
     * Obtain the securityBucket.
     * @return the security
     */
    public MoneyWiseAnalysisSecurityBucket getSecurity() {
        return theState.getSecurity();
    }

    /**
     * Obtain the date range selection control.
     * @return the date range selection
     */
    public TethysUIDateRangeSelector getDateRangeSelector() {
        return theRangeSelect;
    }

    /**
     * Build report menu.
     */
    private void buildReportMenu() {
        /* Access builder */
        final boolean hasSecurities = theState.hasSecurities();
        final TethysUIScrollMenu<MoneyWiseReportType> myBuilder = theReportButton.getMenu();
        myBuilder.removeAllItems();

        /* Loop through the reports */
        for (MoneyWiseReportType myType : MoneyWiseReportType.values()) {
            /* If we can produce the report */
            if (hasSecurities
                    || !myType.needSecurities()) {
                /* Create a new MenuItem for the report type */
                myBuilder.addItem(myType);
            }
        }
    }

    /**
     * Build holding menu.
     */
    private void buildHoldingMenu() {
        /* Access state details */
        final MoneyWiseAnalysis myAnalysis = theState.getAnalysis();
        final MoneyWiseAnalysisSecurityBucket mySecurity = theState.getSecurity();
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = myAnalysis.getPortfolios();

        /* Access builder */
        final TethysUIScrollMenu<MoneyWiseAnalysisSecurityBucket> myBuilder = theHoldingButton.getMenu();
        myBuilder.removeAllItems();
        TethysUIScrollItem<MoneyWiseAnalysisSecurityBucket> myActive = null;

        /* Loop through the Portfolio Buckets */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myPortBucket = myPortIterator.next();

            /* Create subMenu */
            final String myName = myPortBucket.getName();
            final TethysUIScrollSubMenu<MoneyWiseAnalysisSecurityBucket> myMenu = myBuilder.addSubMenu(myName);

            /* Loop through the Security Buckets */
            final MoneyWiseAnalysisSecurityBucketList mySecurities = myPortBucket.getSecurities();
            final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = mySecurities.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisSecurityBucket myBucket = myIterator.next();

                /* Add menuItem */
                final TethysUIScrollItem<MoneyWiseAnalysisSecurityBucket> myItem = myMenu.getSubMenu().addItem(myBucket, myBucket.getSecurityName());

                /* Record active item */
                if (myBucket.equals(mySecurity)) {
                    myActive = myItem;
                }
            }
        }

        /* Ensure that active item is displayed */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Set the range for the date box.
     * @param pRange the date range
     */
    public final void setRange(final OceanusDateRange pRange) {
        /* Set up range */
        theRangeSelect.setOverallRange(pRange);
    }

    /**
     * Set securities flag.
     * @param pSecurities do we have securities?
     */
    public void setSecurities(final boolean pSecurities) {
        theState.setSecurities(pSecurities);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
        /* Record the analysis */
        theState.setAnalysis(pAnalysis);

        /* Access the portfolios */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = pAnalysis.getPortfolios();

        /* If we have an existing security bucket */
        MoneyWiseAnalysisSecurityBucket mySecurity = theState.getSecurity();

        /* If we have a selected Security */
        if (mySecurity != null) {
            /* Look for the equivalent bucket */
            mySecurity = myPortfolios.getMatchingSecurityHolding(mySecurity.getSecurityHolding());
        }

        /* If we no longer have a selected security */
        if (mySecurity == null) {
            /* Obtain the default security holding */
            mySecurity = myPortfolios.getDefaultSecurityHolding();
        }

        /* Set the selected security */
        theState.setSecurity(mySecurity);
    }

    /**
     * Set security.
     * @param pSecurity the security.
     */
    public void setSecurity(final MoneyWiseAnalysisSecurityBucket pSecurity) {
        /* Set the selected security */
        theState.setSecurity(pSecurity);
        theState.setType(MoneyWiseReportType.CAPITALGAINS);

        /* Notify that the state has changed */
        theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWiseReportState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseReportState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnable) {
        theRangeSelect.setEnabled(bEnable);
        theReportButton.setEnabled(bEnable);
        theHoldingButton.setEnabled(bEnable);
        thePrintButton.setEnabled(bEnable);
        theSaveButton.setEnabled(bEnable);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Handle new report.
     */
    private void handleNewReport() {
        /* Set active flag */
        isActive = true;

        /* Look for a changed report type */
        if (theState.setType(theReportButton.getValue())) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }

        /* Clear active flag */
        isActive = false;
    }

    /**
     * Handle new holding.
     */
    private void handleNewSecurity() {
        /* Set active flag */
        isActive = true;

        /* Look for a changed report type */
        if (theState.setSecurity(theHoldingButton.getValue())) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }

        /* Clear active flag */
        isActive = false;
    }

    /**
     * Handle new range.
     */
    private void handleNewRange() {
        /* if we have a changed range and are not changing report */
        if (theState.setRange(theRangeSelect)
                && !isActive) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class MoneyWiseReportState {
        /**
         * The analysis.
         */
        private MoneyWiseAnalysis theAnalysis;

        /**
         * The selected range.
         */
        private OceanusDateRange theRange;

        /**
         * Do we have securities?
         */
        private boolean hasSecurities;

        /**
         * The securityBucket.
         */
        private MoneyWiseAnalysisSecurityBucket theSecurity;

        /**
         * The selected report type.
         */
        private MoneyWiseReportType theType;

        /**
         * Constructor.
         */
        private MoneyWiseReportState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseReportState(final MoneyWiseReportState pState) {
            theAnalysis = pState.getAnalysis();
            theRange = pState.getRange();
            hasSecurities = pState.hasSecurities();
            theSecurity = pState.getSecurity();
            theType = pState.getType();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        private MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the selected range.
         * @return the range
         */
        private OceanusDateRange getRange() {
            return theRange;
        }

        /**
         * Do we have securities?
         * @return true/false
         */
        private boolean hasSecurities() {
            return hasSecurities;
        }

        /**
         * Obtain the security bucket.
         * @return the bucket
         */
        private MoneyWiseAnalysisSecurityBucket getSecurity() {
            return theSecurity;
        }

        /**
         * Obtain the selected report type.
         * @return the report type
         */
        private MoneyWiseReportType getType() {
            return theType;
        }

        /**
         * Set new Range.
         * @param pSelect the Panel with the new range
         * @return true/false did a change occur
         */
        private boolean setRange(final TethysUIDateRangeSelector pSelect) {
            /* Adjust the date and build the new range */
            final OceanusDateRange myRange = pSelect.getRange();
            if (!MetisDataDifference.isEqual(myRange, theRange)) {
                theRange = new OceanusDateRange(myRange);
                return true;
            }
            return false;
        }

        /**
         * Set securities flag.
         * @param pSecurities do we have securities?
         */
        private void setSecurities(final boolean pSecurities) {
            /* Adjust the flag */
            if (pSecurities != hasSecurities) {
                hasSecurities = pSecurities;
                if (!hasSecurities
                        && theType.needSecurities()) {
                    theType = MoneyWiseReportType.getDefault();
                }
            }
        }

        /**
         * Set analysis.
         * @param pAnalysis the analysis
         */
        private void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
            theAnalysis = pAnalysis;
        }

        /**
         * Set security.
         * @param pSecurity the security
         * @return true/false did a change occur
         */
        private boolean setSecurity(final MoneyWiseAnalysisSecurityBucket pSecurity) {
            if (!pSecurity.equals(theSecurity)) {
                theSecurity = pSecurity;
                applyState();
                return true;
            }
            return false;
        }

        /**
         * Set new Report Type.
         * @param pType the new type
         * @return true/false did a change occur
         */
        private boolean setType(final MoneyWiseReportType pType) {
            if (!pType.equals(theType)) {
                /* Are we currently point in time */
                final boolean isPointInTime = theType != null
                        && theType.isPointInTime();

                /* Store the new type */
                theType = pType;

                /* If we need to switch point in time */
                if (theType.isPointInTime() != isPointInTime) {
                    /* Switch it appropriately */
                    theRangeSelect.setPeriod(isPointInTime
                            ? OceanusDatePeriod.FISCALYEAR
                            : OceanusDatePeriod.DATESUPTO);
                    theRangeSelect.lockPeriod(!isPointInTime);

                    /* else if we are switching to tax calculation */
                } else if (theType == MoneyWiseReportType.TAXCALC) {
                    /* Switch explicitly to Fiscal Year */
                    theRangeSelect.setPeriod(OceanusDatePeriod.FISCALYEAR);
                }

                /* Apply the state */
                applyState();
                return true;
            }
            return false;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theReportButton.setFixedText(theType == null
                    ? null
                    : theType.toString());
            theHoldingButton.setValue(theSecurity);
            theHoldingButton.setVisible(MoneyWiseReportType.CAPITALGAINS.equals(theType));
        }
    }
}
