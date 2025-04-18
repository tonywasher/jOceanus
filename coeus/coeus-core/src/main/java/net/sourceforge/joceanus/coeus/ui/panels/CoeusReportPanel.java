/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import org.w3c.dom.Document;

import net.sourceforge.joceanus.coeus.exc.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.coeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.coeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.coeus.ui.report.CoeusReportBuilder;
import net.sourceforge.joceanus.coeus.ui.report.CoeusReportStyleSheet;
import net.sourceforge.joceanus.coeus.ui.report.CoeusReportType;
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.report.MetisReportEvent;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.metis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIScrollPaneManager;

/**
 * Report Panel.
 */
public class CoeusReportPanel
        implements OceanusEventProvider<CoeusDataEvent>, TethysUIComponent {
    /**
     * The Event Manager.
     */
    private final OceanusEventManager<CoeusDataEvent> theEventManager;

    /**
     * The MarketCache.
     */
    private final CoeusMarketCache theCache;

    /**
     * The Panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The HTML pane.
     */
    private final TethysUIHTMLManager theHTMLPane;

    /**
     * The Report selection Panel.
     */
    private final CoeusReportSelect theSelect;

    /**
     * The Market Entry.
     */
    private final MetisViewerEntry theMarketEntry;

    /**
     * The Error Panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The Report Manager.
     */
    private final MetisReportManager<CoeusFilter> theManager;

    /**
     * The ReportBuilder.
     */
    private final CoeusReportBuilder theBuilder;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @param pCache the market cache
     * @throws OceanusException on error
     */
    public CoeusReportPanel(final MetisToolkit pToolkit,
                            final CoeusMarketCache pCache) throws OceanusException {
        /* Store the parameters */
        theCache = pCache;

        /* Access the GUI factory */
        final TethysUIFactory<?> myFactory = pToolkit.getGuiFactory();

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the Panel */
        thePanel = myFactory.paneFactory().newBorderPane();

        /* Create the viewer entries */
        final MetisViewerManager myDataMgr = pToolkit.getViewerManager();
        final MetisViewerEntry mySection = myDataMgr.getStandardEntry(MetisViewerStandardEntry.VIEW);
        final MetisViewerEntry myReport = myDataMgr.newEntry(mySection, "Report");
        theMarketEntry = myDataMgr.newEntry(myReport, CoeusResource.DATA_MARKET.getValue());
        theMarketEntry.setVisible(false);

        /* Create the HTML Pane */
        theHTMLPane = myFactory.controlFactory().newHTMLManager();

        /* Create Report Manager */
        theManager = new MetisReportManager<>(new MetisReportHTMLBuilder(pToolkit.getFormatter()));

        /* Create the report builder */
        theBuilder = new CoeusReportBuilder(theManager);

        /* Create the Report Selection panel */
        theSelect = new CoeusReportSelect(myFactory, theCache.getCalendar());

        /* Create the error panel for this view */
        theError = pToolkit.newErrorPanel(myReport);

        /* Create a scroll pane */
        final TethysUIScrollPaneManager myHTMLScroll = myFactory.paneFactory().newScrollPane();
        myHTMLScroll.setContent(theHTMLPane);

        /* Now define the panel */
        thePanel.setNorth(theSelect);
        thePanel.setCentre(myHTMLScroll);

        /* Load the CSS */
        theHTMLPane.setCSSContent(CoeusReportStyleSheet.CSS_REPORT);

        /* Create listeners */
        theCache.getEventRegistrar().addEventListener(e -> refreshData());
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theManager.getEventRegistrar().addEventListener(this::handleGoToRequest);
        final OceanusEventRegistrar<CoeusDataEvent> myRegistrar = theSelect.getEventRegistrar();
        myRegistrar.addEventListener(CoeusDataEvent.SELECTIONCHANGED, e -> handleReportRequest());
        myRegistrar.addEventListener(CoeusDataEvent.PRINT, e -> theHTMLPane.printIt());
        myRegistrar.addEventListener(CoeusDataEvent.SAVETOFILE, e -> theHTMLPane.saveToFile());
        theHTMLPane.getEventRegistrar().addEventListener(TethysUIEvent.BUILDPAGE, e -> {
            theManager.processReference(e.getDetails(String.class), theHTMLPane);
            e.consume();
        });
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<CoeusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSelect.setEnabled(pEnabled);
        theError.setEnabled(pEnabled);
        theHTMLPane.setEnabled(pEnabled);
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Protect against exceptions */
        try {
            /* Hide the instant debug since it is now invalid */
            theMarketEntry.setVisible(false);

            /* Refresh the data */
            theSelect.setCalendar(theCache.getCalendar());
            buildReport();

            /* Create SavePoint */
            theSelect.createSavePoint();
        } catch (OceanusException e) {
            /* Show the error */
            theError.addError(e);
            handleErrorPane();

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }

    /**
     * Build the report.
     * @throws OceanusException on error
     */
    private void buildReport() throws OceanusException {
        /* set lockDown of selection */
        theSelect.setEnabled(true);

        /* Skip if we have no markets */
        if (theCache.isIdle()) {
            return;
        }

        /* Access the values from the selection */
        final CoeusReportType myReportType = theSelect.getReportType();
        final CoeusMarketProvider myProvider = theSelect.getProvider();
        final OceanusDate myDate = theSelect.getDate();
        final Document myDoc;

        /* Switch on report type */
        switch (myReportType) {
            case ANNUAL:
                final CoeusMarketAnnual myAnnual = theCache.getAnnual(myProvider, myDate);
                theMarketEntry.setObject(myAnnual);
                myDoc = theBuilder.createReport(myReportType, myAnnual);
                break;

            case BALANCESHEET:
            case LOANBOOK:
                final CoeusMarketSnapShot mySnapShot = theCache.getSnapShot(myProvider, myDate);
                theMarketEntry.setObject(mySnapShot);
                myDoc = theBuilder.createReport(myReportType, mySnapShot);
                break;

            default:
                return;
        }

        /* Declare to debugger */
        theMarketEntry.setVisible(true);

        /* Declare the document */
        theManager.setDocument(myDoc);

        /* Create initial display version */
        final String myText = theManager.formatXML();
        theHTMLPane.setHTMLContent(myText, "");
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        final boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelect.setVisible(!isError);

        /* Lock HTML area */
        theHTMLPane.setEnabled(!isError);
    }

    /**
     * handleGoToRequest.
     * @param pEvent the event
     */
    private void handleGoToRequest(final OceanusEvent<MetisReportEvent> pEvent) {
        /* Create the details of the request */
        final CoeusFilter myFilter = pEvent.getDetails(CoeusFilter.class);

        /* Request the action */
        theEventManager.fireEvent(CoeusDataEvent.GOTOSTATEMENT, myFilter);
    }

    /**
     * handleReportRequest.
     */
    private void handleReportRequest() {
        /* Protect against exceptions */
        try {
            /* build the report */
            buildReport();

            /* Create SavePoint */
            theSelect.createSavePoint();

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new CoeusDataException("Failed to change selection", e);

            /* Show the error */
            theError.addError(myError);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }
}
