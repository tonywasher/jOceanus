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

import net.sourceforge.joceanus.coeus.data.CoeusCalendar;
import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.coeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.coeus.ui.CoeusUIResource;
import net.sourceforge.joceanus.coeus.ui.report.CoeusReportType;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

/**
 * Report Select.
 */
public class CoeusReportSelect
        implements TethysEventProvider<CoeusDataEvent>, TethysUIComponent {
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
    private final TethysUIBoxPaneManager thePanel;

    /**
     * Reports scroll button.
     */
    private final TethysUIScrollButtonManager<CoeusReportType> theReportButton;

    /**
     * Market scroll button.
     */
    private final TethysUIScrollButtonManager<CoeusMarketProvider> theMarketButton;

    /**
     * Date select.
     */
    private final TethysUIDateButtonManager theDateButton;

    /**
     * Print button.
     */
    private final TethysUIButton thePrintButton;

    /**
     * Current state.
     */
    private CoeusReportState theState;

    /**
     * Saved state.
     */
    private CoeusReportState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pCalendar the calendar
     */
    public CoeusReportSelect(final TethysUIFactory<?> pFactory,
                             final CoeusCalendar pCalendar) {
        /* Create the report button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theReportButton = myButtons.newScrollButton(CoeusReportType.class);
        buildReportMenu();

        /* Create the market button */
        theMarketButton = myButtons.newScrollButton(CoeusMarketProvider.class);
        buildMarketMenu();

        /* Create the DateButton */
        theDateButton = myButtons.newDateButton();
        theDateButton.setSelectedDate(new TethysDate());

        /* Create initial state */
        theState = new CoeusReportState(this, pCalendar);

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myRepLabel = myControls.newLabel(NLS_REPORT);
        final TethysUILabel myMktLabel = myControls.newLabel(NLS_MARKET);

        /* Create the print button */
        thePrintButton = myButtons.newButton();
        MetisIcon.configurePrintIconButton(thePrintButton);

        /* Create the save button */
        final TethysUIButton mySaveButton = myButtons.newButton();
        MetisIcon.configureSaveIconButton(mySaveButton);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the selection panel */
        thePanel = pFactory.paneFactory().newHBoxPane();
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
        thePanel.addNode(mySaveButton);

        /* Initialise the state */
        theState.setType(CoeusReportType.BALANCESHEET);
        theState.setProvider(CoeusMarketProvider.FUNDINGCIRCLE);
        theState.setDate(theDateButton);
        applyState();

        /* Add the listeners */
        thePrintButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(CoeusDataEvent.PRINT));
        mySaveButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(CoeusDataEvent.SAVETOFILE));
        theReportButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewReport());
        theMarketButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewMarket());
        theDateButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDate());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
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
        final TethysUIScrollMenu<CoeusReportType> myBuilder = theReportButton.getMenu();

        /* Loop through the reports */
        for (final CoeusReportType myType : CoeusReportType.values()) {
            /* Create a new MenuItem for the report type */
            myBuilder.addItem(myType);
        }
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
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new CoeusReportState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new CoeusReportState(theSavePoint);

        /* Apply the state */
        applyState();
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
        theState.setCalendar(pCalendar);
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
     * Apply the State.
     */
    final void applyState() {
        theReportButton.setValue(theState.getType());
        theMarketButton.setValue(theState.getProvider());
        theDateButton.setSelectedDate(theState.getSelectedDate());
    }
}
