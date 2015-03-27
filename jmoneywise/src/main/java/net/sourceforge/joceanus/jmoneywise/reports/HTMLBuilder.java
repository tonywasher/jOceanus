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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.awt.Color;

import javax.swing.text.html.StyleSheet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Build a report document.
 */
public class HTMLBuilder {
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
     * The class attribute.
     */
    protected static final String ATTR_CLASS = "class";

    /**
     * The id attribute.
     */
    protected static final String ATTR_ID = "id";

    /**
     * The name attribute.
     */
    protected static final String ATTR_NAME = "name";

    /**
     * The href attribute.
     */
    private static final String ATTR_HREF = "href";

    /**
     * The colspan attribute.
     */
    private static final String ATTR_COLSPAN = "colspan";

    /**
     * The align attribute.
     */
    private static final String ATTR_ALIGN = "align";

    /**
     * The width attribute.
     */
    private static final String ATTR_WIDTH = "width";

    /**
     * The align centre value.
     */
    private static final String ALIGN_CENTER = "center";

    /**
     * The align right value.
     */
    private static final String ALIGN_RIGHT = "right";

    /**
     * The main table width value.
     */
    private static final String WIDTH_MAIN = "90%";

    /**
     * The embedded table width value.
     */
    private static final String WIDTH_EMBED = "99%";

    /**
     * Name of total table row class.
     */
    private static final String CLASS_TOTROW = "totalRow";

    /**
     * Name of summary table row class.
     */
    private static final String CLASS_SUMMROW = "summRow";

    /**
     * Name of alternate summary table row class.
     */
    private static final String CLASS_ALTSUMMROW = "altSummRow";

    /**
     * Name of detailed summary row class.
     */
    private static final String CLASS_DTLSUMMROW = "dtlSummRow";

    /**
     * Name of alternate detailed summary row class.
     */
    private static final String CLASS_ALTDTLSUMMROW = "altDtlSummRow";

    /**
     * Name of detail row class.
     */
    private static final String CLASS_DTLROW = "detailRow";

    /**
     * Name of alternate detail row class.
     */
    private static final String CLASS_ALTDTLROW = "altDetailRow";

    /**
     * Name of titleValue class.
     */
    private static final String CLASS_TITLEVALUE = "titleValue";

    /**
     * Name of dataValue class.
     */
    private static final String CLASS_DATAVALUE = "dataValue";

    /**
     * Name of negativeValue class.
     */
    private static final String CLASS_NEGVALUE = "negValue";

    /**
     * The HTML element.
     */
    private static final String ELEMENT_HTML = "html";

    /**
     * The body element.
     */
    private static final String ELEMENT_BODY = "body";

    /**
     * The title element.
     */
    private static final String ELEMENT_TITLE = "h1";

    /**
     * The subtitle element.
     */
    private static final String ELEMENT_SUBTITLE = "h2";

    /**
     * The break element.
     */
    private static final String ELEMENT_BREAK = "br";

    /**
     * The table element.
     */
    private static final String ELEMENT_TABLE = "table";

    /**
     * The table header element.
     */
    private static final String ELEMENT_THDR = "thead";

    /**
     * The table body element.
     */
    private static final String ELEMENT_TBODY = "tbody";

    /**
     * The row element.
     */
    private static final String ELEMENT_ROW = "tr";

    /**
     * The cell element.
     */
    private static final String ELEMENT_CELL = "td";

    /**
     * The total cell element.
     */
    private static final String ELEMENT_TOTAL = "th";

    /**
     * The link element.
     */
    private static final String ELEMENT_LINK = "a";

    /**
     * The table reference header.
     */
    protected static final String REF_TAB = ELEMENT_TABLE;

    /**
     * The id reference header.
     */
    protected static final String REF_ID = ATTR_ID;

    /**
     * The filter reference header.
     */
    protected static final String REF_FILTER = "filter";

    /**
     * The delayed reference header.
     */
    protected static final String REF_DELAY = "delay";

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
     * The bold font attribute.
     */
    private static final String CSS_FONTBOLD = " font-weight: bold;";

    /**
     * The document builder.
     */
    private final DocumentBuilder theBuilder;

    /**
     * The document.
     */
    private Document theDocument = null;

    /**
     * The data formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * The field manager.
     */
    private final JFieldManager theFieldManager;

    /**
     * Constructor.
     * @param pView the view
     * @throws JOceanusException on error
     */
    public HTMLBuilder(final View pView) throws JOceanusException {
        /* Store the field manager */
        theFieldManager = pView.getFieldMgr();

        /* Protect against exceptions */
        try {
            /* Create the formatter */
            theFormatter = new JDataFormatter();

            /* Create the document builder */
            DocumentBuilderFactory myDocFactory = DocumentBuilderFactory.newInstance();
            theBuilder = myDocFactory.newDocumentBuilder();

        } catch (Exception e) {
            throw new JMoneyWiseIOException("Failed to create", e);
        }
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the document.
     * @return the document
     */
    public Document getDocument() {
        return theDocument;
    }

    /**
     * Build display styleSheet.
     * @param pSheet the styleSheet
     */
    public void buildDisplayStyleSheet(final StyleSheet pSheet) {
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
    public static void buildPrintStyleSheet(final StyleSheet pSheet) {
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
     * Create and append a standard empty cell.
     * @param pControl the table control
     */
    protected void makeValueCell(final HTMLTable pControl) {
        pControl.createNewCell(false);
    }

    /**
     * Create and append a standard empty total cell.
     * @param pControl the table control
     */
    protected void makeTotalCell(final HTMLTable pControl) {
        pControl.createNewCell(true);
    }

    /**
     * Create and append a standard empty title cell.
     * @param pControl the table control
     */
    protected void makeTitleCell(final HTMLTable pControl) {
        pControl.createNewCell(true);
    }

    /**
     * Create and append a standard cell with value.
     * @param pControl the table control
     * @param pValue the value
     */
    protected void makeValueCell(final HTMLTable pControl,
                                 final Object pValue) {
        Element myCell = pControl.createNewCell(false);
        setCellValue(myCell, pValue);
    }

    /**
     * Create and append a total cell with value.
     * @param pControl the table control
     * @param pValue the value
     */
    protected void makeTotalCell(final HTMLTable pControl,
                                 final Object pValue) {
        Element myCell = pControl.createNewCell(true);
        setCellValue(myCell, pValue);
    }

    /**
     * Create and append a title cell.
     * @param pControl the table control
     * @param pTitle the title
     */
    protected void makeTitleCell(final HTMLTable pControl,
                                 final String pTitle) {
        Element myCell = pControl.createNewCell(true);
        setCellTitle(myCell, pTitle);
    }

    /**
     * Make Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     */
    protected void makeTableLinkCell(final HTMLTable pControl,
                                     final String pLink) {
        makeTableLinkCell(pControl, pLink, pLink);
    }

    /**
     * Make Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     * @param pName the link table display name
     */
    protected void makeTableLinkCell(final HTMLTable pControl,
                                     final String pLink,
                                     final String pName) {
        /* Determine the id of the link */
        String myId = REF_ID
                      + pLink;

        /* Create the cell */
        Element myCell = pControl.createNewCell(false);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_ID, myId);
        myLink.setIdAttribute(ATTR_ID, true);
        myLink.setAttribute(ATTR_NAME, myId);
        myLink.setAttribute(ATTR_HREF, REF_TAB
                                       + pLink);
        myLink.setTextContent(pName);
    }

    /**
     * Make Delayed Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     */
    protected void makeDelayLinkCell(final HTMLTable pControl,
                                     final String pLink) {
        makeDelayLinkCell(pControl, pLink, pLink);
    }

    /**
     * Make Delayed Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     * @param pName the link table display name
     */
    protected void makeDelayLinkCell(final HTMLTable pControl,
                                     final String pLink,
                                     final String pName) {
        /* Determine the id of the link */
        String myId = REF_ID
                      + pLink;

        /* Create the cell */
        Element myCell = pControl.createNewCell(false);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_ID, myId);
        myLink.setIdAttribute(ATTR_ID, true);
        myLink.setAttribute(ATTR_NAME, myId);
        myLink.setAttribute(ATTR_HREF, REF_DELAY
                                       + pLink);
        myLink.setTextContent(pName);
    }

    /**
     * Make Filter link cell.
     * @param pControl the table control
     * @param pLink the link table name
     */
    protected void makeFilterLinkCell(final HTMLTable pControl,
                                      final String pLink) {
        makeFilterLinkCell(pControl, pLink, pLink);
    }

    /**
     * Make Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     * @param pName the link table display name
     */
    protected void makeFilterLinkCell(final HTMLTable pControl,
                                      final String pLink,
                                      final String pName) {
        Element myCell = pControl.createNewCell(false);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_HREF, REF_FILTER
                                       + pLink);
        myLink.setTextContent(pName);
    }

    /**
     * Set a cell value.
     * @param pCell the cell to set the value for
     * @param pValue the value for the cell
     */
    private void setCellValue(final Element pCell,
                              final Object pValue) {
        Object myValue = pValue;
        String myClass = CLASS_DATAVALUE;

        /* If this is an instance of JDecimal */
        if (myValue instanceof JDecimal) {
            /* Access as decimal */
            JDecimal myDec = (JDecimal) myValue;

            /* Ignore value if zero */
            if (myDec.isZero()) {
                myValue = null;

                /* Switch class if negative */
            } else if (!myDec.isPositive()) {
                myClass = CLASS_NEGVALUE;
            }
        }

        /* Set class of cell */
        pCell.setAttribute(ATTR_CLASS, myClass);

        /* Set value of cell */
        if (myValue != null) {
            pCell.setTextContent(theFormatter.formatObject(myValue));
        }
    }

    /**
     * Set a cell title.
     * @param pCell the cell to set the value for
     * @param pTitle the title for the cell
     */
    private void setCellTitle(final Element pCell,
                              final String pTitle) {
        /* Set class and content of cell */
        pCell.setAttribute(ATTR_CLASS, CLASS_TITLEVALUE);
        pCell.setTextContent(pTitle);
    }

    /**
     * Start a table header row.
     * @param pControl the table control
     */
    protected void startHdrRow(final HTMLTable pControl) {
        /* Create the row */
        pControl.createNewRow(true);
    }

    /**
     * Start a table data row.
     * @param pControl the table control
     */
    protected void startRow(final HTMLTable pControl) {
        /* Create the row */
        pControl.createNewRow(false);
    }

    /**
     * Start a table total row.
     * @param pControl the table control
     */
    protected void startTotalRow(final HTMLTable pControl) {
        /* Create the row */
        pControl.createTotalRow();
    }

    /**
     * Start Report.
     * @return the body
     */
    protected Element startReport() {
        /* Create the new document */
        theDocument = theBuilder.newDocument();

        /* Create the standard structure */
        Element myHtml = theDocument.createElement(ELEMENT_HTML);
        theDocument.appendChild(myHtml);
        Element myBody = theDocument.createElement(ELEMENT_BODY);
        myHtml.appendChild(myBody);
        return myBody;
    }

    /**
     * Make title.
     * @param pBody the document body
     * @param pTitle the title
     */
    protected void makeTitle(final Element pBody,
                             final String pTitle) {
        /* Create the title */
        Element myTitle = theDocument.createElement(ELEMENT_TITLE);
        pBody.appendChild(myTitle);
        myTitle.setTextContent(pTitle);
    }

    /**
     * Make two line title.
     * @param pBody the document body
     * @param pTitle1 the first title
     * @param pTitle2 the second title
     */
    protected void makeTitle(final Element pBody,
                             final String pTitle1,
                             final String pTitle2) {
        /* Create the title */
        Element myTitle = theDocument.createElement(ELEMENT_TITLE);
        pBody.appendChild(myTitle);
        Node myText = theDocument.createTextNode(pTitle1);
        myTitle.appendChild(myText);
        Element myBreak = theDocument.createElement(ELEMENT_BREAK);
        myTitle.appendChild(myBreak);
        myText = theDocument.createTextNode(pTitle2);
        myTitle.appendChild(myText);
    }

    /**
     * Make subtitle.
     * @param pBody the document body
     * @param pTitle the title
     */
    protected void makeSubTitle(final Element pBody,
                                final String pTitle) {
        /* Create the title */
        Element myTitle = theDocument.createElement(ELEMENT_SUBTITLE);
        pBody.appendChild(myTitle);
        myTitle.setTextContent(pTitle);
    }

    /**
     * Start Table.
     * @param pBody the document body
     * @return the table control
     */
    protected HTMLTable startTable(final Element pBody) {
        /* Create the standard structure */
        Element myTable = theDocument.createElement(ELEMENT_TABLE);
        pBody.appendChild(myTable);
        myTable.setAttribute(ATTR_ALIGN, ALIGN_CENTER);
        myTable.setAttribute(ATTR_WIDTH, WIDTH_MAIN);

        /* Create the table control */
        return new HTMLTable(myTable);
    }

    /**
     * Create an embedded table.
     * @param pParent the parent element
     * @return the new table
     */
    protected HTMLTable createEmbeddedTable(final HTMLTable pParent) {
        /* Create the table */
        Element myTable = theDocument.createElement(ELEMENT_TABLE);
        myTable.setAttribute(ATTR_ALIGN, ALIGN_RIGHT);
        myTable.setAttribute(ATTR_WIDTH, WIDTH_EMBED);

        /* Create the table control */
        return new HTMLTable(myTable, pParent);
    }

    /**
     * Embed a table into the document.
     * @param pTable the table to embed
     * @param pTitle the title of the table
     */
    protected void embedTable(final HTMLTable pTable,
                              final String pTitle) {
        /* Access body element */
        HTMLTable myParent = pTable.getParent();
        Element myLink = getLinkRow(pTitle);
        Node myNextRow = myLink.getNextSibling();

        /* Create the row */
        Element myRow = theDocument.createElement(ELEMENT_ROW);
        myRow.setAttribute(ATTR_ID, REF_TAB
                                    + pTitle);
        myRow.setIdAttribute(ATTR_ID, true);
        Element myCell = theDocument.createElement(ELEMENT_CELL);
        myRow.appendChild(myCell);
        myCell.setAttribute(ATTR_COLSPAN, Integer.toString(myParent.getNumCols()));
        myCell.appendChild(pTable.getTable());

        /* Insert into the correct place in the document */
        Node myTop = myLink.getParentNode();
        if (myNextRow == null) {
            myTop.appendChild(myRow);
        } else {
            myTop.insertBefore(myRow, myNextRow);
        }
    }

    /**
     * Obtain the row that a link is in.
     * @param pTitle the title of the link
     * @return the row that contains the link
     */
    protected Element getLinkRow(final String pTitle) {
        /* Determine the id of the link */
        String myId = REF_ID
                      + pTitle;

        /* Locate the cell element */
        Element myLink = theDocument.getElementById(myId);
        Node myCell = myLink.getParentNode();
        Node myParent = myCell.getParentNode();

        /* Update the link to be a table reference */
        myLink.setAttribute(ATTR_HREF, REF_TAB
                                       + pTitle);

        /* Cast result to element */
        return (myParent instanceof Element)
                                            ? (Element) myParent
                                            : null;
    }

    /**
     * Table control class.
     */
    public final class HTMLTable {
        /**
         * Table class type.
         */
        private final TableClass theClass;

        /**
         * The table parent.
         */
        private final HTMLTable theParent;

        /**
         * The table element.
         */
        private final Element theTable;

        /**
         * The table header element.
         */
        private Element theHeader = null;

        /**
         * The table body element.
         */
        private Element theBody = null;

        /**
         * The current row.
         */
        private Element theRow = null;

        /**
         * Was the last row an odd row.
         */
        private boolean wasOdd = false;

        /**
         * Current # of columns.
         */
        private int numCols = -1;

        /**
         * Max columns.
         */
        private int maxCols = 0;

        /**
         * Constructor.
         * @param pTable the table element
         * @param pParent the parent table.
         */
        private HTMLTable(final Element pTable,
                          final HTMLTable pParent) {
            /* Store parameters */
            theTable = pTable;
            theParent = pParent;
            theClass = pParent.getNextTableClass();
        }

        /**
         * Constructor.
         * @param pTable the table element
         */
        private HTMLTable(final Element pTable) {
            /* Store parameters */
            theTable = pTable;
            theParent = null;
            theClass = TableClass.SUMMARY;
        }

        /**
         * Obtain the table header.
         * @return the header
         */
        private Element getTableHeader() {
            /* If we have not yet created the header */
            if (theHeader == null) {
                /* Create the header */
                theHeader = theDocument.createElement(ELEMENT_THDR);

                /* Insert before body */
                if (theBody == null) {
                    theTable.appendChild(theHeader);
                } else {
                    theTable.insertBefore(theHeader, theBody);
                }
            }
            return theHeader;
        }

        /**
         * Obtain the table body.
         * @return the body
         */
        private Element getTableBody() {
            /* If we have not yet created the body */
            if (theBody == null) {
                /* Create the body */
                theBody = theDocument.createElement(ELEMENT_TBODY);
                theTable.appendChild(theBody);
            }
            return theBody;
        }

        /**
         * Obtain the number of columns.
         * @return the number of columns
         */
        private int getNumCols() {
            return maxCols;
        }

        /**
         * Obtain the parent table.
         * @return the parent
         */
        private HTMLTable getParent() {
            return theParent;
        }

        /**
         * Obtain the table.
         * @return the table
         */
        private Element getTable() {
            return theTable;
        }

        /**
         * Obtain the next class name.
         * @return the next class name.
         */
        private String getNextRowClass() {
            /* Switch flag */
            wasOdd = !wasOdd;

            /* Return the class name */
            return wasOdd
                         ? theClass.getOddClass()
                         : theClass.getEvenClass();
        }

        /**
         * Obtain the next table name.
         * @return the next class.
         */
        private TableClass getNextTableClass() {
            /* Return the class */
            return theClass.getNextClass();
        }

        /**
         * Create a new row.
         */
        private void createTotalRow() {
            /* Determine the parent */
            Element myParent = getTableBody();

            /* Create the row */
            theRow = theDocument.createElement(ELEMENT_ROW);
            myParent.appendChild(theRow);
            theRow.setAttribute(ATTR_CLASS, CLASS_TOTROW);

            /* Adjust # of columns */
            numCols = 0;
        }

        /**
         * Create a new row.
         * @param bHdr use header rather than body (true/false)
         */
        private void createNewRow(final boolean bHdr) {
            /* Determine the parent */
            Element myParent = (bHdr)
                                     ? getTableHeader()
                                     : getTableBody();

            /* Create the row */
            theRow = theDocument.createElement(ELEMENT_ROW);
            myParent.appendChild(theRow);
            theRow.setAttribute(ATTR_CLASS, (bHdr)
                                                  ? CLASS_TOTROW
                                                  : getNextRowClass());

            /* Adjust # of columns */
            numCols = 0;
        }

        /**
         * Create a new cell in the current row.
         * @param bTotal create total cell (true/false)
         * @return the new cell
         */
        private Element createNewCell(final boolean bTotal) {
            /* Determine the cell type */
            String myCellType = (bTotal)
                                        ? ELEMENT_TOTAL
                                        : ELEMENT_CELL;

            /* Adjust column # */
            numCols++;
            if (numCols >= maxCols) {
                maxCols = numCols;
            }

            /* Create the cell in the current row */
            Element myCell = theDocument.createElement(myCellType);
            theRow.appendChild(myCell);
            return myCell;
        }

    }

    /**
     * Table class.
     */
    public enum TableClass {
        /**
         * Summary.
         */
        SUMMARY,

        /**
         * DetailedSummary.
         */
        DETAILEDSUMMARY,

        /**
         * Detail.
         */
        DETAIL;

        /**
         * Obtain the odd class name.
         * @return the class name
         */
        private String getOddClass() {
            switch (this) {
                case SUMMARY:
                    return CLASS_SUMMROW;
                case DETAILEDSUMMARY:
                    return CLASS_DTLSUMMROW;
                case DETAIL:
                    return CLASS_DTLROW;
                default:
                    return null;
            }
        }

        /**
         * Obtain the even class name.
         * @return the class name
         */
        private String getEvenClass() {
            switch (this) {
                case SUMMARY:
                    return CLASS_ALTSUMMROW;
                case DETAILEDSUMMARY:
                    return CLASS_ALTDTLSUMMROW;
                case DETAIL:
                    return CLASS_ALTDTLROW;
                default:
                    return null;
            }
        }

        /**
         * Obtain the embedded TableClass.
         * @return the class name
         */
        private TableClass getNextClass() {
            switch (this) {
                case SUMMARY:
                    return DETAILEDSUMMARY;
                case DETAILEDSUMMARY:
                case DETAIL:
                    return DETAIL;
                default:
                    return null;
            }
        }
    }
}
