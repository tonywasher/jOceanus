/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.reports.MoneyWiseReportType;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDatePeriod;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Report selection panel.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseReportSelect<N, I>
        implements TethysEventProvider<PrometheusDataEvent>, TethysNode<N> {
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
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * Reports scroll button.
     */
    private final TethysScrollButtonManager<MoneyWiseReportType, N, I> theReportButton;

    /**
     * Range select.
     */
    private final TethysDateRangeSelector<N, I> theRangeSelect;

    /**
     * Print button.
     */
    private final TethysButton<N, I> thePrintButton;

    /**
     * Save button.
     */
    private final TethysButton<N, I> theSaveButton;

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
    public MoneyWiseReportSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the report button */
        theReportButton = pFactory.newScrollButton();

        /* Create the Range Select and disable its border */
        theRangeSelect = pFactory.newDateRangeSelector();

        /* Create the labels */
        TethysLabel<N, I> myRepLabel = pFactory.newLabel(NLS_REPORT);

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
        thePanel.addNode(theRangeSelect);
        thePanel.addSpacer();
        thePanel.addNode(thePrintButton);
        thePanel.addNode(theSaveButton);

        /* Add the listeners */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theReportButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewReport());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildReportMenu());
        thePrintButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusDataEvent.PRINT));
        theSaveButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusDataEvent.SAVETOFILE));
        theRangeSelect.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewRange());
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
     * Obtain the date range selection control.
     * @return the date range selection
     */
    public TethysDateRangeSelector<N, I> getDateRangeSelector() {
        return theRangeSelect;
    }

    /**
     * Build report menu.
     */
    private void buildReportMenu() {
        /* Access builder */
        boolean hasSecurities = theState.hasSecurities();
        TethysScrollMenu<MoneyWiseReportType, ?> myBuilder = theReportButton.getMenu();
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
         * The selected range.
         */
        private TethysDateRange theRange;

        /**
         * Do we have securities?
         */
        private boolean hasSecurities;

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
            theRange = pState.getRange();
            hasSecurities = pState.hasSecurities();
            theType = pState.getType();
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
        private boolean setRange(final TethysDateRangeSelector<N, I> pSelect) {
            /* Adjust the date and build the new range */
            TethysDateRange myRange = pSelect.getRange();
            if (!MetisDifference.isEqual(myRange, theRange)) {
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
                if (!hasSecurities && theType.needSecurities()) {
                    theType = MoneyWiseReportType.getDefault();
                }
            }
        }

        /**
         * Set new Report Type.
         * @param pType the new type
         * @return true/false did a change occur
         */
        private boolean setType(final MoneyWiseReportType pType) {
            if (!pType.equals(theType)) {
                /* Are we currently point in time */
                boolean isPointInTime = (theType != null)
                                        && (theType.isPointInTime());

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
            theReportButton.setFixedText((theType == null)
                                                           ? null
                                                           : theType.toString());
        }
    }
}
