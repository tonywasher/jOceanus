/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jcoeus.data.CoeusCalendar;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.jcoeus.ui.report.CoeusReportType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Report Select.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class CoeusReportSelect<N, I>
        implements TethysEventProvider<CoeusDataEvent>, TethysNode<N> {
    /**
     * Text for Report Label.
     */
    private static final String NLS_REPORT = CoeusUIResource.REPORT_PROMPT.getValue();

    /**
     * Text for Market Label.
     */
    private static final String NLS_MARKET = CoeusUIResource.MARKET_PROMPT.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = CoeusUIResource.REPORT_TITLE.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<CoeusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * Reports scroll button.
     */
    private final TethysScrollButtonManager<CoeusReportType, N, I> theReportButton;

    /**
     * Market scroll button.
     */
    private final TethysScrollButtonManager<CoeusMarketProvider, N, I> theMarketButton;

    /**
     * Date select.
     */
    private final TethysDateButtonManager<N, I> theDateButton;

    /**
     * Print button.
     */
    private final TethysButton<N, I> thePrintButton;

    /**
     * Save button.
     */
    private final TethysButton<N, I> theSaveButton;

    /**
     * Calendar.
     */
    private CoeusCalendar theCalendar;

    /**
     * Current state.
     */
    private ReportState theState;

    /**
     * Saved state.
     */
    private ReportState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pCalendar the calendar
     */
    public CoeusReportSelect(final TethysGuiFactory<N, I> pFactory,
                             final CoeusCalendar pCalendar) {
        /* Store parameters */
        theCalendar = pCalendar;

        /* Create the report button */
        theReportButton = pFactory.newScrollButton();
        buildReportMenu();

        /* Create the market button */
        theMarketButton = pFactory.newScrollButton();
        buildMarketMenu();

        /* Create the DateButton */
        theDateButton = pFactory.newDateButton();
        theDateButton.setSelectedDate(new TethysDate());

        /* Create initial state */
        theState = new ReportState();

        /* Create the labels */
        TethysLabel<N, I> myRepLabel = pFactory.newLabel(NLS_REPORT);
        TethysLabel<N, I> myMktLabel = pFactory.newLabel(NLS_MARKET);

        /* Create the print button */
        thePrintButton = pFactory.newButton();
        MetisIcon.configurePrintIconButton(thePrintButton);

        /* Create the save button */
        theSaveButton = pFactory.newButton();
        MetisIcon.configureSaveIconButton(theSaveButton);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the selection panel */
        thePanel = pFactory.newHBoxPane();
        thePanel.setBorderTitle(NLS_TITLE);

        /* Define the layout */
        thePanel.addNode(myRepLabel);
        thePanel.addNode(theReportButton);
        thePanel.addSpacer();
        thePanel.addNode(myMktLabel);
        thePanel.addNode(theMarketButton);
        thePanel.addSpacer();
        thePanel.addNode(theDateButton);
        thePanel.addSpacer();
        thePanel.addNode(thePrintButton);
        thePanel.addNode(theSaveButton);

        /* Initialise the state */
        theState.setType(CoeusReportType.BALANCESHEET);
        theState.setProvider(CoeusMarketProvider.FUNDINGCIRCLE);
        theState.setDate(theDateButton);
        theState.applyState();

        /* Add the listeners */
        thePrintButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(CoeusDataEvent.PRINT));
        theSaveButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(CoeusDataEvent.SAVETOFILE));
        theReportButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewReport());
        theMarketButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewMarket());
        theDateButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDate());
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
     * Obtain the report type.
     * @return the report type
     */
    public CoeusReportType getReportType() {
        return theState.getType();
    }

    /**
     * Obtain the market.
     * @return the market
     */
    public CoeusMarketProvider getProvider() {
        return theState.getProvider();
    }

    /**
     * Obtain the selected date.
     * @return the date
     */
    public TethysDate getDate() {
        return theState.getActualDate();
    }

    /**
     * Build report menu.
     */
    private void buildReportMenu() {
        /* Create builder */
        TethysScrollMenu<CoeusReportType, ?> myBuilder = theReportButton.getMenu();

        /* Loop through the reports */
        for (CoeusReportType myType : CoeusReportType.values()) {
            /* Create a new MenuItem for the report type */
            myBuilder.addItem(myType);
        }
    }

    /**
     * Build market menu.
     */
    private void buildMarketMenu() {
        /* Create builder */
        TethysScrollMenu<CoeusMarketProvider, ?> myBuilder = theMarketButton.getMenu();

        /* Loop through the markets */
        for (CoeusMarketProvider myMarket : CoeusMarketProvider.values()) {
            /* Create a new MenuItem for the market */
            myBuilder.addItem(myMarket);
        }
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
        theDateButton.setEnabled(bEnable);
        theMarketButton.setEnabled(bEnable);
        theReportButton.setEnabled(bEnable);
        thePrintButton.setEnabled(bEnable);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Set the calendar.
     * @param pCalendar the calendar
     */
    protected void setCalendar(final CoeusCalendar pCalendar) {
        theCalendar = pCalendar;
        theState.determineActualDate();
    }

    /**
     * Handle new report.
     */
    private void handleNewReport() {
        /* Look for a changed report type */
        if (theState.setType(theReportButton.getValue())) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(CoeusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new market.
     */
    private void handleNewMarket() {
        /* Look for a changed market provider */
        if (theState.setProvider(theMarketButton.getValue())) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(CoeusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new date.
     */
    private void handleNewDate() {
        /* if we have a changed date and are not changing report */
        if (theState.setDate(theDateButton)) {
            /* Notify that the state has changed */
            theEventManager.fireEvent(CoeusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class ReportState {
        /**
         * The market provider.
         */
        private CoeusMarketProvider theProvider;

        /**
         * The actual date.
         */
        private TethysDate theActualDate;

        /**
         * The selected date.
         */
        private TethysDate theSelectedDate;

        /**
         * The report type.
         */
        private CoeusReportType theType;

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
            theProvider = pState.getProvider();
            theSelectedDate = pState.getSelectedDate();
            theActualDate = pState.getActualDate();
            theType = pState.getType();
        }

        /**
         * Obtain the selected market provider.
         * @return the provider
         */
        private CoeusMarketProvider getProvider() {
            return theProvider;
        }

        /**
         * Obtain the selected date.
         * @return the date
         */
        private TethysDate getSelectedDate() {
            return theSelectedDate;
        }

        /**
         * Obtain the actual date.
         * @return the date
         */
        private TethysDate getActualDate() {
            return theActualDate;
        }

        /**
         * Obtain the selected report type.
         * @return the report type
         */
        private CoeusReportType getType() {
            return theType;
        }

        /**
         * Set new Date.
         * @param pSelect the Panel with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final TethysDateButtonManager<N, I> pSelect) {
            /* Obtain the date and adjust it */
            TethysDate mySelected = pSelect.getSelectedDate();
            TethysDate myDate = mySelected == null
                                                   ? null
                                                   : new TethysDate(mySelected);

            /* Record any change and report change */
            if (!MetisDifference.isEqual(myDate, theSelectedDate)) {
                theSelectedDate = myDate;
                determineActualDate();
                return true;
            }
            return false;
        }

        /**
         * Set new Market Provider.
         * @param pProvider the new provider
         * @return true/false did a change occur
         */
        private boolean setProvider(final CoeusMarketProvider pProvider) {
            if (!pProvider.equals(theProvider)) {
                /* Store the new provider */
                theProvider = pProvider;
                return true;
            }
            return false;
        }

        /**
         * Set new Report Type.
         * @param pType the new type
         * @return true/false did a change occur
         */
        private boolean setType(final CoeusReportType pType) {
            if (!pType.equals(theType)) {
                /* Store the new type */
                theType = pType;
                determineActualDate();
                return true;
            }
            return false;
        }

        /**
         * Determine actual date.
         */
        private void determineActualDate() {
            theActualDate = theType.useAnnualDate()
                                                    ? theCalendar.getEndOfYear(theSelectedDate)
                                                    : theSelectedDate;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            theReportButton.setValue(theType);
            theMarketButton.setValue(theProvider);
            theDateButton.setSelectedDate(theSelectedDate);
        }
    }
}
