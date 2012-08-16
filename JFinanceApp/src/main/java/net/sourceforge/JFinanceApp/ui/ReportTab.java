/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataModels.ui.ErrorPanel;
import net.sourceforge.JDataModels.views.DataControl;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JEventManager.JEventPanel;
import net.sourceforge.JFinanceApp.data.TaxYear;
import net.sourceforge.JFinanceApp.ui.controls.ReportSelect;
import net.sourceforge.JFinanceApp.ui.controls.ReportSelect.ReportType;
import net.sourceforge.JFinanceApp.views.AnalysisReport;
import net.sourceforge.JFinanceApp.views.EventAnalysis;
import net.sourceforge.JFinanceApp.views.EventAnalysis.AnalysisYear;
import net.sourceforge.JFinanceApp.views.View;

/**
 * Report Panel.
 * @author Tony Washer
 */
public class ReportTab extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6499559461558661107L;

    /**
     * The Data View.
     */
    private final transient View theView;

    /**
     * The Scroll Pane.
     */
    private final JScrollPane theScroll;

    /**
     * The Editor.
     */
    private final JEditorPane theEditor;

    /**
     * The Report selection Panel.
     */
    private final ReportSelect theSelect;

    /**
     * The Analysis.
     */
    private transient EventAnalysis theAnalysis = null;

    /**
     * The Report entry.
     */
    private final transient JDataEntry theDataReport;

    /**
     * The Spot Analysis Entry.
     */
    private final transient JDataEntry theSpotEntry;

    /**
     * The Error Panel.
     */
    private final ErrorPanel theError;

    /**
     * Constructor for Report Window.
     * @param pView the data view
     */
    public ReportTab(final View pView) {
        /* Store the view */
        theView = pView;

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataReport = myDataMgr.new JDataEntry("Report");
        theSpotEntry = myDataMgr.new JDataEntry("SpotAnalysis");
        theDataReport.addAsChildOf(mySection);
        theSpotEntry.addAsChildOf(theDataReport);
        theSpotEntry.hideEntry();

        /* Create listener */
        ReportListener myListener = new ReportListener();

        /* Create the editor pane as non-editable */
        theEditor = new JEditorPane();
        theEditor.setEditable(false);
        theEditor.addHyperlinkListener(myListener);

        /* Add an editor kit to the editor */
        HTMLEditorKit myKit = new HTMLEditorKit();
        theEditor.setEditorKit(myKit);

        /* Create a scroll-pane for the editor */
        theScroll = new JScrollPane(theEditor);

        /* Create the style-sheet for the window */
        StyleSheet myStyle = myKit.getStyleSheet();
        myStyle.addRule("body { color:#000; font-family:times; margins; 4px; }");
        myStyle.addRule("h1 { color: black; }");
        myStyle.addRule("h2 { color: black; }");

        /* Create the document for the window */
        Document myDoc = myKit.createDefaultDocument();
        theEditor.setDocument(myDoc);

        /* Create the Report Selection panel */
        theSelect = new ReportSelect(theView);
        theSelect.addChangeListener(myListener);
        theSelect.addActionListener(myListener);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataReport);
        theError.addChangeListener(myListener);

        /* Add listener to data */
        theView.addChangeListener(myListener);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theError);
        add(theSelect);
        add(theScroll);
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Protect against exceptions */
        try {
            /* Hide the instant debug since it is now invalid */
            theSpotEntry.hideEntry();

            /* Refresh the data */
            theAnalysis = theView.getAnalysis();
            theSelect.refreshData(theAnalysis);
            buildReport();

            /* Create SavePoint */
            theSelect.createSavePoint();
        } catch (JDataException e) {
            /* TODO Show the error */
            // setError(e);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }
    }

    /**
     * Print the report.
     */
    private void printIt() {
        /* Print the current report */
        try {
            theEditor.print();
        } catch (PrinterException e) {
            e = null;
        }
    }

    /**
     * Build the report.
     * @throws JDataException on error
     */
    private void buildReport() throws JDataException {
        AnalysisYear myAnalysis;
        EventAnalysis mySnapshot;
        AnalysisReport myReport;
        String myText = "";

        /* Access the values from the selection */
        ReportType myReportType = theSelect.getReportType();
        JDateDay myDate = theSelect.getReportDate();
        TaxYear myYear = theSelect.getTaxYear();

        /* set lockdown of selection */
        theSelect.setEnabled(true);

        /* Skip if year is null */
        if (myYear == null) {
            return;
        }

        /* Switch on report type */
        switch (myReportType) {
            case ASSET:
                myAnalysis = theAnalysis.getAnalysisYear(myYear);
                myReport = new AnalysisReport(myAnalysis);
                myText = myReport.getYearReport();
                break;

            case INCOME:
                myAnalysis = theAnalysis.getAnalysisYear(myYear);
                myReport = new AnalysisReport(myAnalysis);
                myText = myReport.getIncomeReport();
                break;

            case TRANSACTION:
                myAnalysis = theAnalysis.getAnalysisYear(myYear);
                myReport = new AnalysisReport(myAnalysis);
                myText = myReport.getTransReport();
                break;

            case TAX:
                myAnalysis = theAnalysis.getAnalysisYear(myYear);
                myReport = new AnalysisReport(myAnalysis);
                myText = myReport.getTaxReport();
                break;

            case BREAKDOWN:
                myAnalysis = theAnalysis.getAnalysisYear(myYear);
                myReport = new AnalysisReport(myAnalysis);
                myText = myReport.getBreakdownReport();
                break;

            case INSTANT:
                mySnapshot = new EventAnalysis(theView.getData(), myDate);
                myReport = new AnalysisReport(mySnapshot);
                myText = myReport.getInstantReport();
                theSpotEntry.setObject(mySnapshot);
                theSpotEntry.showEntry();
                break;

            case MARKET:
                mySnapshot = new EventAnalysis(theView.getData(), myDate);
                myReport = new AnalysisReport(mySnapshot);
                myText = myReport.getMarketReport();
                theSpotEntry.setObject(mySnapshot);
                theSpotEntry.showEntry();
                break;
            default:
                return;
        }

        /* Set the report text */
        theEditor.setText(myText);
        theEditor.setCaretPosition(0);
        theEditor.requestFocusInWindow();
    }

    /**
     * Listener class.
     */
    private class ReportListener implements ChangeListener, ActionListener, HyperlinkListener {
        @Override
        public void hyperlinkUpdate(final HyperlinkEvent e) {
            /* If this is an activated event */
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                    HTMLDocument doc = (HTMLDocument) theEditor.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    try {
                        URL url = e.getURL();
                        String desc = e.getDescription();
                        if ((url == null) && (desc.startsWith("#"))) {
                            theEditor.scrollToReference(desc.substring(1));
                        } else {
                            theEditor.setPage(e.getURL());
                        }
                    } catch (IOException t) {
                        t = null;
                    }
                }
            }
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            Object o = evt.getSource();

            /* If this is the error panel */
            if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelect.setVisible(!isError);

                /* Lock scroll area */
                theScroll.setEnabled(!isError);

                /* If this is the data view */
            } else if (theView.equals(o)) {
                /* Refresh Data */
                refreshData();

                /* If this is the select panel */
            } else if (theSelect.equals(o)) {
                /* Protect against exceptions */
                try {
                    /* build the report */
                    buildReport();

                    /* Create SavePoint */
                    theSelect.createSavePoint();

                    /* Catch Exceptions */
                } catch (JDataException e) {
                    /* Build the error */
                    JDataException myError = new JDataException(ExceptionClass.DATA,
                            "Failed to change selection", e);

                    /* Show the error */
                    theError.setError(myError);

                    /* Restore SavePoint */
                    theSelect.restoreSavePoint();
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* If this is the error panel */
            if (theSelect.equals(evt.getSource())) {
                /* Print the report */
                printIt();
            }
        }
    }
}
