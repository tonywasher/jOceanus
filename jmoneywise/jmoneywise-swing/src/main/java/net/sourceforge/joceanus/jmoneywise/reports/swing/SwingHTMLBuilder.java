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
package net.sourceforge.joceanus.jmoneywise.reports.swing;

import java.awt.Color;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;

/**
 * Build a report document.
 */
public class SwingHTMLBuilder
        extends HTMLBuilder {
    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * The dot separator.
     */
    private static final String SEP_DOT = ".";

    /**
     * The attribute separator.
     */
    private static final String SEP_ENDATTR = ";";

    /**
     * The start rule separator.
     */
    private static final String SEP_STARTRULE = " {";

    /**
     * The end rule separator.
     */
    private static final String SEP_ENDRULE = " }";

    /**
     * The colour indicator.
     */
    private static final String CSS_COLOR = " color: ";

    /**
     * The background colour indicator.
     */
    private static final String CSS_BACKCOLOR = " background-color: ";

    /**
     * The align centre attribute.
     */
    private static final String CSS_ALIGNCENTRE = " text-align: center;";

    /**
     * The align right attribute.
     */
    private static final String CSS_ALIGNRIGHT = " text-align: right;";

    /**
     * The align left attribute.
     */
    private static final String CSS_ALIGNLEFT = " text-align: left;";

    /**
     * The bold font attribute.
     */
    private static final String CSS_FONTBOLD = " font-weight: bold;";

    /**
     * The editor pane.
     */
    private final JEditorPane theEditor;

    /**
     * The field manager.
     */
    private final JFieldManager theFieldManager;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditor the editor pane
     * @throws JOceanusException on error
     */
    public SwingHTMLBuilder(final SwingView pView,
                            final JEditorPane pEditor) throws JOceanusException {
        /* Call super constructor */
        super(pView, pView.getUtilitySet());

        /* Store the editor pane */
        theEditor = pEditor;

        /* Store the field manager */
        theFieldManager = pView.getFieldManager();

        /* Initialise from field manager */
        processFieldConfig();

        /* Create listener */
        new ReportListener();
    }

    /**
     * Build display styleSheet.
     * @param pSheet the styleSheet
     */
    private void buildDisplayStyleSheet(final StyleSheet pSheet) {
        /* Create builder and access zebra colour */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        Color myZebra = theFieldManager.getZebraColor();
        String myZebraText = DataConverter.colorToHexString(myZebra);

        /* Define standard font for body and table contents */
        myBuilder.append(ELEMENT_BODY);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(" font-family: Verdana, sans-serif; font-size: 1em; color: black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define standard alignment for headers */
        myBuilder.append(ELEMENT_TITLE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNCENTRE);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append(ELEMENT_SUBTITLE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNCENTRE);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define tables */
        myBuilder.append(ELEMENT_TABLE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(" width: 90%; align: center; border-spacing: 1px; border-collapse: collapse;");
        myBuilder.append(" border-top: solid; border-bottom: solid; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define table headers as bold */
        myBuilder.append(ELEMENT_TOTAL);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_FONTBOLD);
        myBuilder.append(" border: 1px solid white;");
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append(ELEMENT_CELL);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(" border: 1px solid white;");
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for title row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_TOTROW);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_BACKCOLOR);
        myBuilder.append(myZebraText);
        myBuilder.append("; border-top: solid;");
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for category row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_SUMMROW);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_BACKCOLOR);
        myBuilder.append(myZebraText);
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for alternate category row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_ALTSUMMROW);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_BACKCOLOR);
        myBuilder.append(DataConverter.colorToHexString(Color.white));
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for subCategory row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_DTLSUMMROW);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_BACKCOLOR);
        myBuilder.append(myZebraText);
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for alternate subCategory row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_ALTDTLSUMMROW);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_BACKCOLOR);
        myBuilder.append(DataConverter.colorToHexString(Color.white));
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define font size for detail row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_DTLROW);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_BACKCOLOR);
        myBuilder.append(myZebraText);
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define font size for alternate detail row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_ALTDTLROW);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_BACKCOLOR);
        myBuilder.append(DataConverter.colorToHexString(Color.white));
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alignment for link values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_LINKVALUE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNLEFT);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for data values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_DATAVALUE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNRIGHT);
        myBuilder.append(CSS_COLOR);
        myBuilder.append(DataConverter.colorToHexString(Color.blue));
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for negative values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_NEGVALUE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNRIGHT);
        myBuilder.append(CSS_COLOR);
        myBuilder.append(DataConverter.colorToHexString(Color.red));
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for title values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_TITLEVALUE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNCENTRE);
        myBuilder.append(CSS_COLOR);
        myBuilder.append(DataConverter.colorToHexString(Color.black));
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set link definition */
        myBuilder.append(ELEMENT_LINK);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_FONTBOLD);
        myBuilder.append(" text-decoration: none;");
        myBuilder.append(CSS_COLOR);
        myBuilder.append(DataConverter.colorToHexString(Color.blue));
        myBuilder.append(SEP_ENDATTR);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Build print styleSheet.
     * @param pSheet the styleSheet
     */
    private static void buildPrintStyleSheet(final StyleSheet pSheet) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Define standard font for body and table contents */
        myBuilder.append(ELEMENT_BODY);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(" font-family: Verdana, sans-serif; font-size: 8px; color: black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define standard alignment for headers */
        myBuilder.append(ELEMENT_TITLE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNCENTRE);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append(ELEMENT_SUBTITLE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNCENTRE);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define tables */
        myBuilder.append(ELEMENT_TABLE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(" width: 100%; border-collapse: collapse; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append(ELEMENT_CELL);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNCENTRE);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define table headers */
        myBuilder.append(ELEMENT_TOTAL);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_FONTBOLD);
        myBuilder.append(CSS_ALIGNCENTRE);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alignment for data values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_DATAVALUE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNRIGHT);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alignment for negative values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_NEGVALUE);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_ALIGNRIGHT);
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set link definition */
        myBuilder.append(ELEMENT_LINK);
        myBuilder.append(SEP_STARTRULE);
        myBuilder.append(CSS_FONTBOLD);
        myBuilder.append(" text-decoration: none; color: black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Process the field configuration.
     */
    private void processFieldConfig() {
        /* Save the active text */
        String myText = theEditor.getText();

        /* Create display editorKit and styleSheet */
        HTMLEditorKit myDisplayKit = new HTMLEditorKit();
        StyleSheet myDisplayStyle = new StyleSheet();
        myDisplayStyle.addStyleSheet(myDisplayKit.getStyleSheet());
        buildDisplayStyleSheet(myDisplayStyle);

        /* Apply styleSheet to display window */
        myDisplayKit.setStyleSheet(myDisplayStyle);
        theEditor.setEditorKit(myDisplayKit);
        Document myDoc = myDisplayKit.createDefaultDocument();
        theEditor.setDocument(myDoc);

        /* Restore the text */
        theEditor.setText(myText);
    }

    /**
     * Build print configuration.
     * @param pPrintPane the print pane
     */
    public static void configurePrintPane(final JEditorPane pPrintPane) {
        /* Create print editorKit and styleSheet */
        HTMLEditorKit myPrintKit = new HTMLEditorKit();
        StyleSheet myPrintStyle = new StyleSheet();
        myPrintStyle.addStyleSheet(myPrintKit.getStyleSheet());
        buildPrintStyleSheet(myPrintStyle);

        /* Apply styleSheet to print window */
        myPrintKit.setStyleSheet(myPrintStyle);
        pPrintPane.setEditorKit(myPrintKit);
        Document myDoc = myPrintKit.createDefaultDocument();
        pPrintPane.setDocument(myDoc);
    }

    /**
     * Listener class.
     */
    private final class ReportListener
            implements JOceanusChangeEventListener {
        /**
         * Constructor.
         */
        private ReportListener() {
            theFieldManager.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            processFieldConfig();
        }
    }
}
