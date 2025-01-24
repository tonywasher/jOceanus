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
package net.sourceforge.joceanus.moneywise.ui.panel;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.report.MetisReportEvent;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.lethe.reports.MoneyWiseReportBuilder;
import net.sourceforge.joceanus.moneywise.lethe.reports.MoneyWiseReportStyleSheet;
import net.sourceforge.joceanus.moneywise.lethe.reports.MoneyWiseReportType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.MoneyWiseStatementSelect;
import net.sourceforge.joceanus.moneywise.lethe.ui.controls.MoneyWiseReportSelect;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateRangeSelector;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIScrollPaneManager;

/**
 * Report panel.
 */
public class MoneyWiseReportTab
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.REPORT_DATAENTRY.getValue();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final MoneyWiseView theView;

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
    private final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> theManager;

    /**
     * The ReportBuilder.
     */
    private final MoneyWiseReportBuilder theBuilder;

    /**
     * Constructor for Report Window.
     * @param pView the data view
     * @throws OceanusException on error
     */
    MoneyWiseReportTab(final MoneyWiseView pView) throws OceanusException {
        /* Store the view */
        theView = pView;

        /* Access GUI Factory */
        final TethysUIFactory<?> myFactory = pView.getGuiFactory();

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the Panel */
        final TethysUIPaneFactory myPanes = myFactory.paneFactory();
        thePanel = myPanes.newBorderPane();

        /* Create the top level debug entry for this view */
        final MetisViewerManager myDataMgr = theView.getViewerManager();
        final MetisViewerEntry mySection = theView.getViewerEntry(PrometheusViewerEntryId.VIEW);
        final MetisViewerEntry myReport = myDataMgr.newEntry(mySection, NLS_DATAENTRY);
        theSpotEntry = myDataMgr.newEntry(myReport, PrometheusViewerEntryId.ANALYSIS.toString());
        theSpotEntry.setVisible(false);

        /* Create the HTML Pane */
        theHTMLPane = myFactory.controlFactory().newHTMLManager();

        /* Create Report Manager */
        theManager = new MetisReportManager<>(new MetisReportHTMLBuilder(pView.getDataFormatter()));

        /* Create the report builder */
        theBuilder = new MoneyWiseReportBuilder(theManager);

        /* Create the Report Selection panel */
        theSelect = new MoneyWiseReportSelect(myFactory);

        /* Create the error panel for this view */
        theError = theView.getToolkit().getToolkit().newErrorPanel(myReport);

        /* Create a scroll pane */
        final TethysUIScrollPaneManager myHTMLScroll = myPanes.newScrollPane();
        myHTMLScroll.setContent(theHTMLPane);

        /* Create the header panel */
        final TethysUIBorderPaneManager myHeader = myPanes.newBorderPane();
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
        final OceanusEventRegistrar<PrometheusDataEvent> myRegistrar = theSelect.getEventRegistrar();
        myRegistrar.addEventListener(PrometheusDataEvent.SELECTIONCHANGED, e -> handleReportRequest());
        myRegistrar.addEventListener(PrometheusDataEvent.PRINT, e -> theHTMLPane.printIt());
        myRegistrar.addEventListener(PrometheusDataEvent.SAVETOFILE, e -> theHTMLPane.saveToFile());
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
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
        OceanusProfile myTask = theView.getActiveTask();
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
        final OceanusDateRange myRange = theSelect.getDateRange();
        final MoneyWiseAnalysisManager myManager = theView.getAnalysisManager();
        final MoneyWiseAnalysisSecurityBucket mySecurity = theSelect.getSecurity();

        /* set lockDown of selection */
        theSelect.setEnabled(true);

        /* Skip if we have no analysis */
        if (myManager.isIdle()) {
            theHTMLPane.setHTMLContent("", "");
            return;
        }

        /* Access the appropriate analysis */
        final MoneyWiseAnalysis myAnalysis = myReportType.isPointInTime()
                ? myManager.getDatedAnalysis(myRange.getEnd())
                : myManager.getRangedAnalysis(myRange);

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
    private void handleGoToRequest(final OceanusEvent<MetisReportEvent> pEvent) {
        /* Access the filter */
        final MoneyWiseAnalysisFilter<?, ?> myFilter = pEvent.getDetails(MoneyWiseAnalysisFilter.class);

        /* If we are currently showing Asset Gains */
        if (MoneyWiseReportType.ASSETGAINS.equals(theSelect.getReportType())) {
            /* Select the capital gains report */
            final MoneyWiseAnalysisSecurityBucket myBucket = ((MoneyWiseAnalysisSecurityFilter) myFilter).getBucket();
            theSelect.setSecurity(myBucket);

            /* else we are selecting a statement */
        } else {
            /* Create the details of the report */
            final TethysUIDateRangeSelector mySelect = theSelect.getDateRangeSelector();
            final MoneyWiseStatementSelect myStatement = new MoneyWiseStatementSelect(mySelect, myFilter);

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
