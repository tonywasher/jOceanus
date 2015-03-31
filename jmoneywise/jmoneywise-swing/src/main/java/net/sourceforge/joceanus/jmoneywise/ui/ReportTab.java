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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.viewer.swing.ViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.ViewerManager.ViewerEntry;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder;
import net.sourceforge.joceanus.jmoneywise.reports.ReportBuilder;
import net.sourceforge.joceanus.jmoneywise.reports.ReportManager;
import net.sourceforge.joceanus.jmoneywise.reports.ReportType;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.ReportSelect;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.swing.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusActionRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnableScroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Report panel.
 */
public class ReportTab
        extends JPanel
        implements JOceanusEventProvider {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2219752518436850014L;

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
    private final transient JOceanusEventManager theEventManager;

    /**
     * The Data View.
     */
    private final transient View theView;

    /**
     * The Scroll Pane.
     */
    private final JEnableScroll theScroll;

    /**
     * The display version of the report.
     */
    private final JEditorPane theEditor;

    /**
     * The print version of the report.
     */
    private final JEditorPane thePrint;

    /**
     * The Report selection Panel.
     */
    private final ReportSelect theSelect;

    /**
     * The Spot Analysis Entry.
     */
    private final transient ViewerEntry theSpotEntry;

    /**
     * The Error Panel.
     */
    private final ErrorPanel theError;

    /**
     * The Report Manager.
     */
    private final transient ReportManager theManager;

    /**
     * The ReportBuilder.
     */
    private final transient ReportBuilder theBuilder;

    /**
     * Constructor for Report Window.
     * @param pView the data view
     * @throws JOceanusException on error
     */
    public ReportTab(final View pView) throws JOceanusException {
        /* Store the view */
        theView = pView;

        /* Create the event manager */
        theEventManager = new JOceanusEventManager();

        /* Create the top level debug entry for this view */
        ViewerManager myDataMgr = theView.getDataMgr();
        ViewerEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        ViewerEntry myDataReport = myDataMgr.new ViewerEntry(NLS_DATAENTRY);
        myDataReport.addAsChildOf(mySection);
        theSpotEntry = myDataMgr.new ViewerEntry(DataControl.DATA_ANALYSIS);
        theSpotEntry.addAsChildOf(myDataReport);
        theSpotEntry.hideEntry();

        /* Create Report Manager */
        theManager = new ReportManager(theView);

        /* Create the report builder */
        theBuilder = new ReportBuilder(theManager);

        /* Create the editor pane as non-editable */
        theEditor = new JEditorPane();
        theEditor.setEditable(false);

        /* Create the print pane for the window */
        thePrint = new JEditorPane();
        thePrint.setEditable(false);

        /* Create a scroll-pane for the editor */
        theScroll = new JEnableScroll();
        theScroll.setViewportView(theEditor);

        /* Create display editorKit and styleSheet */
        HTMLEditorKit myDisplayKit = new HTMLEditorKit();
        StyleSheet myDisplayStyle = new StyleSheet();
        myDisplayStyle.addStyleSheet(myDisplayKit.getStyleSheet());
        theManager.buildDisplayStyleSheet(myDisplayStyle);

        /* Create print editorKit and styleSheet */
        HTMLEditorKit myPrintKit = new HTMLEditorKit();
        StyleSheet myPrintStyle = new StyleSheet();
        myPrintStyle.addStyleSheet(myPrintKit.getStyleSheet());
        HTMLBuilder.buildPrintStyleSheet(myPrintStyle);

        /* Apply styleSheet to display window */
        myDisplayKit.setStyleSheet(myDisplayStyle);
        theEditor.setEditorKit(myDisplayKit);
        javax.swing.text.Document myDoc = myDisplayKit.createDefaultDocument();
        theEditor.setDocument(myDoc);

        /* Apply styleSheet to print window */
        myPrintKit.setStyleSheet(myPrintStyle);
        thePrint.setEditorKit(myPrintKit);
        myDoc = myPrintKit.createDefaultDocument();
        thePrint.setDocument(myDoc);

        /* Create the Report Selection panel */
        theSelect = new ReportSelect();

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, myDataReport);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theError);
        add(theSelect);
        add(theScroll);

        /* Create listener */
        new ReportListener();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theSelect.setEnabled(bEnabled);
        theError.setEnabled(bEnabled);
        theScroll.setEnabled(bEnabled);
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
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
        } catch (JOceanusException e) {
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
            /* Copy text from editor */
            String myText = theEditor.getText();
            thePrint.setText(myText);

            /* Print the report */
            thePrint.print();
        } catch (PrinterException e) {
            LOGGER.error("Failed to print", e);
        }
    }

    /**
     * Build the report.
     * @throws JOceanusException on error
     */
    private void buildReport() throws JOceanusException {

        /* Access the values from the selection */
        ReportType myReportType = theSelect.getReportType();
        JDateDayRange myRange = theSelect.getDateRange();
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
     * Listener class.
     */
    private final class ReportListener
            extends MouseAdapter
            implements JOceanusChangeEventListener, JOceanusActionEventListener {
        /**
         * View Registration.
         */
        private final JOceanusChangeRegistration theViewReg;

        /**
         * Manager Registration.
         */
        private final JOceanusActionRegistration theManagerReg;

        /**
         * Error Registration.
         */
        private final JOceanusChangeRegistration theErrorReg;

        /**
         * Select Registration.
         */
        private final JOceanusChangeRegistration theSelectReg;

        /**
         * Print Registration.
         */
        private final JOceanusActionRegistration thePrintReg;

        /**
         * Constructor.
         */
        private ReportListener() {
            /* Add swing listeners */
            theEditor.addMouseListener(this);

            /* Listen to correct events */
            theViewReg = theView.getEventRegistrar().addChangeListener(this);
            theManagerReg = theManager.getEventRegistrar().addActionListener(this);
            theErrorReg = theError.getEventRegistrar().addChangeListener(this);
            JOceanusEventRegistrar myRegistrar = theSelect.getEventRegistrar();
            theSelectReg = myRegistrar.addChangeListener(this);
            thePrintReg = myRegistrar.addActionListener(this);
        }

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

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the data view */
            if (theViewReg.isRelevant(pEvent)) {
                /* Refresh Data */
                refreshData();

                /* else if this is the error panel */
            } else if (theErrorReg.isRelevant(pEvent)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelect.setVisible(!isError);

                /* Lock scroll area */
                theScroll.setEnabled(!isError);

                /* else if this is the selection panel */
            } else if (theSelectReg.isRelevant(pEvent)) {
                /* Protect against exceptions */
                try {
                    /* build the report */
                    buildReport();

                    /* Create SavePoint */
                    theSelect.createSavePoint();

                    /* Catch Exceptions */
                } catch (JOceanusException e) {
                    /* Build the error */
                    JOceanusException myError = new JMoneyWiseDataException("Failed to change selection", e);

                    /* Show the error */
                    theError.addError(myError);

                    /* Restore SavePoint */
                    theSelect.restoreSavePoint();
                }
            }
        }

        @Override
        public void processActionEvent(final JOceanusActionEvent pEvent) {
            /* If this is the report manager */
            if (theManagerReg.isRelevant(pEvent)) {
                /* Create the details of the report */
                JDateDayRangeSelect mySelect = theSelect.getDateRangeSelect();
                AnalysisFilter<?, ?> myFilter = pEvent.getDetails(AnalysisFilter.class);
                StatementSelect myStatement = new StatementSelect(mySelect, myFilter);

                /* Request the action */
                theEventManager.fireActionEvent(MainTab.ACTION_VIEWSTATEMENT, myStatement);

                /* If this is the print request */
            } else if (thePrintReg.isRelevant(pEvent)) {
                /* Print the report */
                printIt();
            }
        }
    }
}
