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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.panel;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.report.MetisReportEvent;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.reports.MoneyWiseReportBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.reports.MoneyWiseReportStyleSheet;
import net.sourceforge.joceanus.jmoneywise.lethe.reports.MoneyWiseReportType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseReportSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Report panel.
 */
public class MioneyWiseReportTab
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.REPORT_DATAENTRY.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final MoneyWiseView theView;

    /**
     * The Panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The HTML pane.
     */
    private final TethysHTMLManager theHTMLPane;

    /**
     * The Report selection Panel.
     */
    private final MoneyWiseReportSelect theSelect;

    /**
     * The Spot Analysis Entry.
     */
    private final MetisViewerEntry theSpotEntry;

    /**
     * The Error Panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The Report Manager.
     */
    private final MetisReportManager<AnalysisFilter<?, ?>> theManager;

    /**
     * The ReportBuilder.
     */
    private final MoneyWiseReportBuilder theBuilder;

    /**
     * Constructor for Report Window.
     * @param pView the data view
     * @throws OceanusException on error
     */
    public MioneyWiseReportTab(final MoneyWiseView pView) throws OceanusException {
        /* Store the view */
        theView = pView;

        /* Access GUI Factory */
        final TethysGuiFactory myFactory = pView.getGuiFactory();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = myFactory.newBorderPane();

        /* Create the top level debug entry for this view */
        final MetisViewerManager myDataMgr = theView.getViewerManager();
        final MetisViewerEntry mySection = theView.getViewerEntry(PrometheusViewerEntryId.VIEW);
        final MetisViewerEntry myReport = myDataMgr.newEntry(mySection, NLS_DATAENTRY);
        theSpotEntry = myDataMgr.newEntry(myReport, PrometheusViewerEntryId.ANALYSIS.toString());
        theSpotEntry.setVisible(false);

        /* Create the HTML Pane */
        theHTMLPane = myFactory.newHTMLManager();

        /* Create Report Manager */
        theManager = new MetisReportManager<>(new MetisReportHTMLBuilder(pView.getDataFormatter()));

        /* Create the report builder */
        theBuilder = new MoneyWiseReportBuilder(theManager);

        /* Create the Report Selection panel */
        theSelect = new MoneyWiseReportSelect(myFactory);

        /* Create the error panel for this view */
        theError = theView.getToolkit().getToolkit().newErrorPanel(myReport);

        /* Create a scroll pane */
        final TethysScrollPaneManager myHTMLScroll = myFactory.newScrollPane();
        myHTMLScroll.setContent(theHTMLPane);

        /* Create the header panel */
        final TethysBorderPaneManager myHeader = myFactory.newBorderPane();
        myHeader.setCentre(theSelect);
        myHeader.setNorth(theError);

        /* Now define the panel */
        thePanel.setNorth(myHeader);
        thePanel.setCentre(myHTMLScroll);

        /* Load the CSS */
        theHTMLPane.setCSSContent(MoneyWiseReportStyleSheet.CSS_REPORT);

        /* Create listeners */
        theView.getEventRegistrar().addEventListener(e -> refreshData());
        theManager.getEventRegistrar().addEventListener(this::handleGoToRequest);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        final TethysEventRegistrar<PrometheusDataEvent> myRegistrar = theSelect.getEventRegistrar();
        myRegistrar.addEventListener(PrometheusDataEvent.SELECTIONCHANGED, e -> handleReportRequest());
        myRegistrar.addEventListener(PrometheusDataEvent.PRINT, e -> theHTMLPane.printIt());
        myRegistrar.addEventListener(PrometheusDataEvent.SAVETOFILE, e -> theHTMLPane.saveToFile());
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
    public TethysNode getNode() {
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
            theSelect.setSecurities(theView.hasActiveSecurities());
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
        final MoneyWiseReportType myReportType = theSelect.getReportType();
        final TethysDateRange myRange = theSelect.getDateRange();
        final AnalysisManager myManager = theView.getAnalysisManager();
        final SecurityBucket mySecurity = theSelect.getSecurity();

        /* set lockDown of selection */
        theSelect.setEnabled(true);

        /* Skip if we have no analysis */
        if (myManager.isIdle()) {
            theHTMLPane.setHTMLContent("", "");
            return;
        }

        /* Access the appropriate analysis */
        final Analysis myAnalysis = myReportType.isPointInTime()
                                                                 ? myManager.getAnalysis(myRange.getEnd())
                                                                 : myManager.getAnalysis(myRange);

        /* Record analysis and build report */
        theSelect.setAnalysis(myAnalysis);
        final Document myDoc = theBuilder.createReport(myAnalysis, myReportType, mySecurity);

        /* Declare to debugger */
        theSpotEntry.setObject(myAnalysis);
        theSpotEntry.setVisible(true);

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
    private void handleGoToRequest(final TethysEvent<MetisReportEvent> pEvent) {
        /* Access the filter */
        final AnalysisFilter<?, ?> myFilter = pEvent.getDetails(AnalysisFilter.class);

        /* If we are currently showing Asset Gains */
        if (MoneyWiseReportType.ASSETGAINS.equals(theSelect.getReportType())) {
            /* Select the capital gains report */
            final SecurityBucket myBucket = ((SecurityFilter) myFilter).getBucket();
            theSelect.setSecurity(myBucket);

            /* else we are selecting a statement */
        } else {
            /* Create the details of the report */
            final TethysDateRangeSelector mySelect = theSelect.getDateRangeSelector();
            final StatementSelect myStatement = new StatementSelect(mySelect, myFilter);

            /* Request the action */
            theEventManager.fireEvent(PrometheusDataEvent.GOTOWINDOW, new PrometheusGoToEvent<>(MoneyWiseGoToId.STATEMENT, myStatement));
        }
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
            final OceanusException myError = new MoneyWiseDataException("Failed to change selection", e);

            /* Show the error */
            theError.addError(myError);
            handleErrorPane();

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }
}
