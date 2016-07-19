/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder;
import net.sourceforge.joceanus.jmoneywise.reports.ReportBuilder;
import net.sourceforge.joceanus.jmoneywise.reports.ReportManager;
import net.sourceforge.joceanus.jmoneywise.reports.ReportType;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseErrorPanel;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseReportSelect;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;

/**
 * Report panel.
 */
public class ReportTab
        implements TethysEventProvider<PrometheusDataEvent>, TethysNode<JComponent> {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.REPORT_DATAENTRY.getValue();

    /**
     * Logger.
     */
    // private static final Logger LOGGER = LoggerFactory.getLogger(ReportTab.class);

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final SwingView theView;

    /**
     * The Panel.
     */
    private final TethysSwingBorderPaneManager thePanel;

    /**
     * The HTML pane.
     */
    private final TethysSwingHTMLManager theHTMLPane;

    /**
     * The Report selection Panel.
     */
    private final MoneyWiseReportSelect<JComponent, Icon> theSelect;

    /**
     * The Spot Analysis Entry.
     */
    private final MetisViewerEntry theSpotEntry;

    /**
     * The Error Panel.
     */
    private final MoneyWiseErrorPanel<JComponent, Icon> theError;

    /**
     * The Report Manager.
     */
    private final ReportManager theManager;

    /**
     * The ReportBuilder.
     */
    private final ReportBuilder theBuilder;

    /**
     * Constructor for Report Window.
     * @param pView the data view
     * @throws OceanusException on error
     */
    public ReportTab(final SwingView pView) throws OceanusException {
        /* Store the view */
        theView = pView;

        /* Access GUI Factory */
        TethysSwingGuiFactory myFactory = pView.getGuiFactory();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = myFactory.newBorderPane();

        /* Create the top level debug entry for this view */
        MetisViewerManager myDataMgr = theView.getViewerManager();
        MetisViewerEntry mySection = theView.getViewerEntry(PrometheusViewerEntryId.VIEW);
        MetisViewerEntry myReport = myDataMgr.newEntry(mySection, NLS_DATAENTRY);
        theSpotEntry = myDataMgr.newEntry(myReport, PrometheusViewerEntryId.ANALYSIS.toString());
        theSpotEntry.setVisible(false);

        /* Create the HTML Pane */
        theHTMLPane = myFactory.newHTMLManager();

        /* Create Report Manager */
        theManager = new ReportManager(new HTMLBuilder(pView.getDataFormatter()));

        /* Create the report builder */
        theBuilder = new ReportBuilder(theManager);

        /* Create the Report Selection panel */
        theSelect = new MoneyWiseReportSelect<>(myFactory);

        /* Create the error panel for this view */
        theError = new MoneyWiseErrorPanel<>(theView, myReport);

        /* Create a scroll pane */
        TethysSwingScrollPaneManager myHTMLScroll = myFactory.newScrollPane();
        myHTMLScroll.setContent(theHTMLPane);

        /* Create the header panel */
        TethysSwingBorderPaneManager myHeader = myFactory.newBorderPane();
        myHeader.setCentre(theSelect);
        myHeader.setNorth(theError);

        /* Now define the panel */
        thePanel.setNorth(myHeader);
        thePanel.setCentre(myHTMLScroll);

        /* Load the CSS */
        loadCSS("MoneyWiseReports.css");

        /* Create listeners */
        theView.getEventRegistrar().addEventListener(e -> refreshData());
        theManager.getEventRegistrar().addEventListener(this::handleGoToRequest);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        TethysEventRegistrar<PrometheusDataEvent> myRegistrar = theSelect.getEventRegistrar();
        myRegistrar.addEventListener(PrometheusDataEvent.SELECTIONCHANGED, e -> handleReportRequest());
        myRegistrar.addEventListener(PrometheusDataEvent.PRINT, e -> theHTMLPane.printIt());
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
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public JComponent getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* Pass on to important elements */
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
        String myCSS = TethysResourceBuilder.loadResourceToString(ReportManager.class, pName);
        theHTMLPane.setCSSContent(myCSS);
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Reports");

        /* Protect against exceptions */
        try {
            /* Hide the instant debug since it is now invalid */
            theSpotEntry.setVisible(false);

            /* Refresh the data */
            theSelect.setRange(theView.getRange());
            buildReport();

            /* Create SavePoint */
            theSelect.createSavePoint();
        } catch (OceanusException e) {
            /* Show the error */
            theView.addError(e);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Build the report.
     * @throws OceanusException on error
     */
    private void buildReport() throws OceanusException {
        /* Access the values from the selection */
        ReportType myReportType = theSelect.getReportType();
        TethysDateRange myRange = theSelect.getDateRange();
        AnalysisManager myManager = theView.getAnalysisManager();
        Document myDoc;
        Analysis myAnalysis;

        /* set lockDown of selection */
        theSelect.setEnabled(true);

        /* Skip if we have no analysis */
        if (myManager.isIdle()) {
            return;
        }

        /* Switch on report type */
        switch (myReportType) {
            case NETWORTH:
            case PORTFOLIO:
                myAnalysis = myManager.getAnalysis(myRange.getEnd());
                myDoc = theBuilder.createReport(myAnalysis, myReportType);
                break;

            case BALANCESHEET:
            case CASHFLOW:
            case INCOMEEXPENSE:
            case MARKETGROWTH:
            case TAXBASIS:
            case TAXCALC:
                myAnalysis = myManager.getAnalysis(myRange);
                myDoc = theBuilder.createReport(myAnalysis, myReportType);
                break;

            default:
                return;
        }

        /* Declare to debugger */
        theSpotEntry.setObject(myAnalysis);
        theSpotEntry.setVisible(true);

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
    private void handleGoToRequest(final TethysEvent<PrometheusDataEvent> pEvent) {
        /* Create the details of the report */
        TethysDateRangeSelector<JComponent, Icon> mySelect = theSelect.getDateRangeSelector();
        AnalysisFilter<?, ?> myFilter = pEvent.getDetails(AnalysisFilter.class);
        StatementSelect<JComponent, Icon> myStatement = new StatementSelect<>(mySelect, myFilter);

        /* Request the action */
        theEventManager.fireEvent(PrometheusDataEvent.GOTOWINDOW, new PrometheusGoToEvent(MainTab.ACTION_VIEWSTATEMENT, myStatement));
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
            OceanusException myError = new MoneyWiseDataException("Failed to change selection", e);

            /* Show the error */
            theError.addError(myError);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }
}
