/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.controls;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.reports.MoneyWiseReportType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDatePeriod;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Report selection panel.
 */
public class MoneyWiseReportSelect
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
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
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager thePanel;

    /**
     * Reports scroll button.
     */
    private final TethysScrollButtonManager<MoneyWiseReportType> theReportButton;

    /**
     * Holding scroll button.
     */
    private final TethysScrollButtonManager<SecurityBucket> theHoldingButton;

    /**
     * Range select.
     */
    private final TethysDateRangeSelector theRangeSelect;

    /**
     * Print button.
     */
    private final TethysButton thePrintButton;

    /**
     * Save button.
     */
    private final TethysButton theSaveButton;

    /**
     * Current state.
     */
    private ReportState theState;

    /**
     * Saved state.
     */
    private ReportState theSavePoint;

    /**
     * Active flag.
     */
    private boolean isActive;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public MoneyWiseReportSelect(final TethysGuiFactory pFactory) {
        /* Create the buttons */
        theReportButton = pFactory.newScrollButton();
        theHoldingButton = pFactory.newScrollButton();

        /* Create the Range Select and disable its border */
        theRangeSelect = pFactory.newDateRangeSelector();

        /* Create the labels */
        final TethysLabel myRepLabel = pFactory.newLabel(NLS_REPORT);

        /* Create the print button */
        thePrintButton = pFactory.newButton();
        MetisIcon.configurePrintIconButton(thePrintButton);

        /* Create the save button */
        theSaveButton = pFactory.newButton();
        MetisIcon.configureSaveIconButton(theSaveButton);

        /* Create initial state */
        theState = new ReportState();
        theState.setRange(theRangeSelect);
        theState.setType(MoneyWiseReportType.getDefault());

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the selection panel */
        thePanel = pFactory.newHBoxPane();
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
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
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
    public TethysDateRange getDateRange() {
        return theState.getRange();
    }

    /**
     * Obtain the securityBucket.
     * @return the security
     */
    public SecurityBucket getSecurity() {
        return theState.getSecurity();
    }

    /**
     * Obtain the date range selection control.
     * @return the date range selection
     */
    public TethysDateRangeSelector getDateRangeSelector() {
        return theRangeSelect;
    }

    /**
     * Build report menu.
     */
    private void buildReportMenu() {
        /* Access builder */
        final boolean hasSecurities = theState.hasSecurities();
        final TethysScrollMenu<MoneyWiseReportType> myBuilder = theReportButton.getMenu();
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
        final Analysis myAnalysis = theState.getAnalysis();
        final SecurityBucket mySecurity = theState.getSecurity();
        final PortfolioBucketList myPortfolios = myAnalysis.getPortfolios();

        /* Access builder */
        final TethysScrollMenu<SecurityBucket> myBuilder = theHoldingButton.getMenu();
        myBuilder.removeAllItems();
        TethysScrollMenuItem<SecurityBucket> myActive = null;

        /* Loop through the Portfolio Buckets */
        final Iterator<PortfolioBucket> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final PortfolioBucket myPortBucket = myPortIterator.next();

            /* Create subMenu */
            final String myName = myPortBucket.getName();
            final TethysScrollSubMenu<SecurityBucket> myMenu = myBuilder.addSubMenu(myName);

            /* Loop through the Security Buckets */
            final SecurityBucketList mySecurities = myPortBucket.getSecurities();
            final Iterator<SecurityBucket> myIterator = mySecurities.iterator();
            while (myIterator.hasNext()) {
                final SecurityBucket myBucket = myIterator.next();

                /* Add menuItem */
                final TethysScrollMenuItem<SecurityBucket> myItem = myMenu.getSubMenu().addItem(myBucket, myBucket.getSecurityName());

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
    public final void setRange(final TethysDateRange pRange) {
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
    public void setAnalysis(final Analysis pAnalysis) {
        /* Record the analysis */
        theState.setAnalysis(pAnalysis);

        /* Access the portfolios */
        final PortfolioBucketList myPortfolios = pAnalysis.getPortfolios();

        /* If we have an existing security bucket */
        SecurityBucket mySecurity = theState.getSecurity();

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
    public void setSecurity(final SecurityBucket pSecurity) {
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
        theSavePoint = new ReportState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new ReportState(theSavePoint);

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
    private final class ReportState {
        /**
         * The analysis.
         */
        private Analysis theAnalysis;

        /**
         * The selected range.
         */
        private TethysDateRange theRange;

        /**
         * Do we have securities?
         */
        private boolean hasSecurities;

        /**
         * The securityBucket.
         */
        private SecurityBucket theSecurity;

        /**
         * The selected report type.
         */
        private MoneyWiseReportType theType;

        /**
         * Constructor.
         */
        private ReportState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private ReportState(final ReportState pState) {
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
        private Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the selected range.
         * @return the range
         */
        private TethysDateRange getRange() {
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
        private SecurityBucket getSecurity() {
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
        private boolean setRange(final TethysDateRangeSelector pSelect) {
            /* Adjust the date and build the new range */
            final TethysDateRange myRange = pSelect.getRange();
            if (!MetisDataDifference.isEqual(myRange, theRange)) {
                theRange = new TethysDateRange(myRange);
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
        private void setAnalysis(final Analysis pAnalysis) {
            theAnalysis = pAnalysis;
        }

        /**
         * Set security.
         * @param pSecurity the security
         * @return true/false did a change occur
         */
        private boolean setSecurity(final SecurityBucket pSecurity) {
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
                                                           ? TethysDatePeriod.FISCALYEAR
                                                           : TethysDatePeriod.DATESUPTO);
                    theRangeSelect.lockPeriod(!isPointInTime);

                    /* else if we are switching to tax calculation */
                } else if (theType == MoneyWiseReportType.TAXCALC) {
                    /* Switch explicitly to Fiscal Year */
                    theRangeSelect.setPeriod(TethysDatePeriod.FISCALYEAR);
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
