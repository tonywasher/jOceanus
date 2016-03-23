/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.reports.ReportBuilder;
import net.sourceforge.joceanus.jmoneywise.reports.ReportType;
import net.sourceforge.joceanus.jmoneywise.reports.swing.SwingReportManager;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.ReportSelect;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusSwingErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportTab.class);

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
    private final JPanel thePanel;

    /**
     * The Scroll Pane.
     */
    private final JScrollPane theScroll;

    /**
     * The display version of the report.
     */
    private final JEditorPane theEditor;

    /**
     * The Report selection Panel.
     */
    private final ReportSelect theSelect;

    /**
     * The Spot Analysis Entry.
     */
    private final MetisViewerEntry theSpotEntry;

    /**
     * The Error Panel.
     */
    private final PrometheusSwingErrorPanel theError;

    /**
     * The Report Manager.
     */
    private final SwingReportManager theManager;

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

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = new TethysSwingEnablePanel();

        /* Create the top level debug entry for this view */
        MetisViewerManager myDataMgr = theView.getViewerManager();
        MetisViewerEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        MetisViewerEntry myDataReport = myDataMgr.newEntry(NLS_DATAENTRY);
        myDataReport.addAsChildOf(mySection);
        theSpotEntry = myDataMgr.newEntry(DataControl.DATA_ANALYSIS);
        theSpotEntry.addAsChildOf(myDataReport);
        theSpotEntry.hideEntry();

        /* Create the editor pane as non-editable */
        theEditor = new JEditorPane();
        theEditor.setEditable(false);

        /* Create Report Manager */
        theManager = new SwingReportManager(theView, theEditor);

        /* Create the report builder */
        theBuilder = new ReportBuilder(theManager);

        /* Create a scroll-pane for the editor */
        theScroll = new JScrollPane();
        theScroll.setViewportView(theEditor);

        /* Create the Report Selection panel */
        theSelect = new ReportSelect();

        /* Create the error panel for this view */
        theError = new PrometheusSwingErrorPanel(myDataMgr, myDataReport);

        /* Create the header panel */
        JPanel myHeader = new TethysSwingEnablePanel();
        myHeader.setLayout(new BorderLayout());
        myHeader.add(theSelect.getNode(), BorderLayout.CENTER);
        myHeader.add(theError.getNode(), BorderLayout.PAGE_START);

        /* Now define the panel */
        thePanel.setLayout(new BorderLayout());
        thePanel.add(myHeader, BorderLayout.PAGE_START);
        thePanel.add(theScroll, BorderLayout.CENTER);

        /* Create listener */
        theView.getEventRegistrar().addEventListener(e -> refreshData());
        theManager.getEventRegistrar().addEventListener(this::handleGoToRequest);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theSelect.getEventRegistrar().addEventListener(e -> handleReportRequest());
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.PRINT, e -> printIt());
        theEditor.addMouseListener(new ReportMouseListener());
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* Pass on to important elements */
        theSelect.setEnabled(pEnabled);
        theError.setEnabled(pEnabled);
        theScroll.setEnabled(pEnabled);
        theEditor.setEnabled(pEnabled);
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
            theSpotEntry.hideEntry();

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
     * Print the report.
     */
    private void printIt() {
        /* Print the current report */
        try {
            /* Print the data */
            theEditor.print();

        } catch (PrinterAbortException e) {
            return;
        } catch (PrinterException e) {
            LOGGER.error("Failed to print", e);
        }
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
        theSpotEntry.showEntry();

        /* Declare the document */
        theManager.setDocument(myDoc);

        /* Create initial display version */
        String myText = theManager.formatXML();
        theEditor.setText(myText);

        /* Initialise the window */
        theEditor.setCaretPosition(0);
        theEditor.requestFocusInWindow();
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelect.setVisible(!isError);

        /* Lock scroll area */
        theScroll.setEnabled(!isError);
    }

    /**
     * handleGoToRequest.
     * @param pEvent the event
     */
    private void handleGoToRequest(final TethysEvent<PrometheusDataEvent> pEvent) {
        /* Create the details of the report */
        TethysSwingDateRangeSelector mySelect = theSelect.getDateRangeSelector();
        AnalysisFilter<?, ?> myFilter = pEvent.getDetails(AnalysisFilter.class);
        StatementSelect myStatement = new StatementSelect(mySelect, myFilter);

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
            OceanusException myError = new JMoneyWiseDataException("Failed to change selection", e);

            /* Show the error */
            theError.addError(myError);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }

    /**
     * Listener class.
     */
    private final class ReportMouseListener
            extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent evt) {
            /* If this is a left click event */
            if (evt.getButton() == MouseEvent.BUTTON1) {
                /* Determine the document element that we right clicked on */
                Element myElement = getHyperlinkElement(evt);
                if (myElement != null) {
                    /* Obtain the attributes */
                    Object myAttrs = myElement.getAttributes().getAttribute(HTML.Tag.A);
                    if (myAttrs instanceof AttributeSet) {
                        AttributeSet mySet = (AttributeSet) myAttrs;

                        /* Access the reference name */
                        String myRef = (String) mySet.getAttribute(HTML.Attribute.HREF);
                        if (myRef != null) {
                            /* Process the link reference */
                            theManager.processReference(myRef, theEditor);
                        }
                    }
                }
            }
        }

        /**
         * Work out which element the mouse event relates to.
         * @param pEvent the mouse event
         * @return the element
         */
        private Element getHyperlinkElement(final MouseEvent pEvent) {
            /* Access the editor and locate mouse position */
            JEditorPane myEditor = (JEditorPane) pEvent.getSource();
            int myPos = myEditor.getUI().viewToModel(myEditor, pEvent.getPoint());

            /* If all looks OK so far */
            if ((myPos >= 0) && (myEditor.getDocument() instanceof HTMLDocument)) {

                /* Access the document element for the position */
                HTMLDocument myDoc = (HTMLDocument) myEditor.getDocument();
                Element myElem = myDoc.getCharacterElement(myPos);

                /* If there is an anchor reference, return the element */
                if (myElem.getAttributes().getAttribute(HTML.Tag.A) != null) {
                    return myElem;
                }
            }

            /* Return no element */
            return null;
        }

    }
}
