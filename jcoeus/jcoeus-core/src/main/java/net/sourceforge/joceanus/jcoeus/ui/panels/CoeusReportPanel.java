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

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jcoeus.ui.CoeusDataEvent;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusMarketCache;
import net.sourceforge.joceanus.jcoeus.ui.report.CoeusReportBuilder;
import net.sourceforge.joceanus.jcoeus.ui.report.CoeusReportResource;
import net.sourceforge.joceanus.jcoeus.ui.report.CoeusReportType;
import net.sourceforge.joceanus.jmetis.report.MetisReportEvent;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Report Panel.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class CoeusReportPanel<N, I>
        implements TethysEventProvider<CoeusDataEvent>, TethysNode<N> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<CoeusDataEvent> theEventManager;

    /**
     * The Toolkit.
     */
    private final MetisToolkit<N, I> theToolkit;

    /**
     * The MarketCache.
     */
    private final CoeusMarketCache theCache;

    /**
     * The Panel.
     */
    private final TethysBorderPaneManager<N, I> thePanel;

    /**
     * The HTML pane.
     */
    private final TethysHTMLManager<N, I> theHTMLPane;

    /**
     * The Report selection Panel.
     */
    private final CoeusReportSelect<N, I> theSelect;

    /**
     * The Market Entry.
     */
    private final MetisViewerEntry theMarketEntry;

    /**
     * The Error Panel.
     */
    private final MetisErrorPanel<N, I> theError;

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
    public CoeusReportPanel(final MetisToolkit<N, I> pToolkit,
                            final CoeusMarketCache pCache) throws OceanusException {
        /* Store the parameters */
        theToolkit = pToolkit;
        theCache = pCache;

        /* Access the GUI factory */
        TethysGuiFactory<N, I> myFactory = pToolkit.getGuiFactory();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = myFactory.newBorderPane();

        /* Create the viewer entries */
        MetisViewerManager myDataMgr = pToolkit.getViewerManager();
        MetisViewerEntry mySection = myDataMgr.getStandardEntry(MetisViewerStandardEntry.VIEW);
        MetisViewerEntry myReport = myDataMgr.newEntry(mySection, "Report");
        theMarketEntry = myDataMgr.newEntry(myReport, CoeusResource.DATA_MARKET.getValue());
        theMarketEntry.setVisible(false);

        /* Create the HTML Pane */
        theHTMLPane = myFactory.newHTMLManager();

        /* Create Report Manager */
        theManager = new MetisReportManager<>(new MetisReportHTMLBuilder(pToolkit.getFormatter()));

        /* Create the report builder */
        theBuilder = new CoeusReportBuilder(theManager);

        /* Create the Report Selection panel */
        theSelect = new CoeusReportSelect<>(myFactory);
        theSelect.setCalendar(theCache.getCalendar());

        /* Create the error panel for this view */
        theError = theToolkit.newErrorPanel(myReport);

        /* Create a scroll pane */
        TethysScrollPaneManager<N, I> myHTMLScroll = myFactory.newScrollPane();
        myHTMLScroll.setContent(theHTMLPane);

        /* Now define the panel */
        thePanel.setNorth(theSelect);
        thePanel.setCentre(myHTMLScroll);

        /* Load the CSS */
        loadCSS("CoeusReports.css");

        /* Create listeners */
        theCache.getEventRegistrar().addEventListener(e -> refreshData());
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theManager.getEventRegistrar().addEventListener(this::handleGoToRequest);
        TethysEventRegistrar<CoeusDataEvent> myRegistrar = theSelect.getEventRegistrar();
        myRegistrar.addEventListener(CoeusDataEvent.SELECTIONCHANGED, e -> handleReportRequest());
        myRegistrar.addEventListener(CoeusDataEvent.PRINT, e -> theHTMLPane.printIt());
        theHTMLPane.getEventRegistrar().addEventListener(TethysUIEvent.BUILDPAGE, e -> {
            theManager.processReference(e.getDetails(String.class), theHTMLPane);
            e.consume();
        });
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysEventRegistrar<CoeusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public N getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSelect.setEnabled(pEnabled);
        theError.setEnabled(pEnabled);
        theHTMLPane.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Load CSS.
     * @param pName the name of the CSS
     * @throws OceanusException on error
     */
    private void loadCSS(final String pName) throws OceanusException {
        String myCSS = TethysResourceBuilder.loadResourceToString(CoeusReportResource.class, pName);
        theHTMLPane.setCSSContent(myCSS);
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
        /* Access the values from the selection */
        CoeusReportType myReportType = theSelect.getReportType();
        CoeusMarketProvider myMarket = theSelect.getMarket();
        TethysDate myDate = theSelect.getDate();
        Document myDoc;

        /* set lockDown of selection */
        theSelect.setEnabled(true);

        /* Skip if we have no markets */
        if (theCache.isIdle()) {
            return;
        }

        /* Switch on report type */
        switch (myReportType) {
            case ANNUAL:
                CoeusMarketAnnual myAnnual = theCache.getAnnual(myMarket, myDate);
                theMarketEntry.setObject(myAnnual);
                myDoc = theBuilder.createReport(myReportType, myAnnual);
                break;

            case BALANCESHEET:
            case LOANBOOK:
                CoeusMarketSnapShot mySnapShot = theCache.getSnapShot(myMarket, myDate);
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
        String myText = theManager.formatXML();
        theHTMLPane.setHTMLContent(myText, "");
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelect.setVisible(!isError);

        /* Lock HTML area */
        theHTMLPane.setEnabled(!isError);
    }

    /**
     * handleGoToRequest.
     * @param pEvent the event
     */
    private void handleGoToRequest(final TethysEvent<MetisReportEvent> pEvent) {
        /* Create the details of the request */
        CoeusFilter myFilter = pEvent.getDetails(CoeusFilter.class);

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
            OceanusException myError = new CoeusDataException("Failed to change selection", e);

            /* Show the error */
            theError.addError(myError);
            handleErrorPane();

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }
}
