/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.awt.Color;

import javax.swing.text.html.StyleSheet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.jOceanus.jDataManager.DataConverter;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDecimal.JDecimal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
     * The blank separator.
     */
    private static final String SEP_BLANK = " ";

    /**
     * The end rule separator.
     */
    private static final String SEP_ENDRULE = "; }";

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
     * Name of category table row class.
     */
    private static final String CLASS_CATROW = "catRow";

    /**
     * Name of alternate category table row class.
     */
    private static final String CLASS_ALTCATROW = "altCatRow";

    /**
     * Name of subcategory row class.
     */
    private static final String CLASS_SUBCATROW = "subCatRow";

    /**
     * Name of alternate subcategory row class.
     */
    private static final String CLASS_ALTSUBCATROW = "altSubCatRow";

    /**
     * Name of detail row class.
     */
    private static final String CLASS_DTLROW = "detailRow";

    /**
     * Name of alternate detail row class.
     */
    private static final String CLASS_ALTDTLROW = "altDetailRow";

    /**
     * The show table class.
     */
    protected static final String CLASS_SHOW = "showTable";

    /**
     * The hide table class.
     */
    protected static final String CLASS_HIDE = "hideTable";

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
     * Constructor.
     * @throws JDataException on error
     */
    public HTMLBuilder() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create the formatter */
            theFormatter = new JDataFormatter();

            /* Create the document builder */
            DocumentBuilderFactory myDocFactory = DocumentBuilderFactory.newInstance();
            theBuilder = myDocFactory.newDocumentBuilder();

        } catch (Exception e) {
            throw new JDataException(ExceptionClass.XML, "Failed to create", e);
        }
    }

    /**
     * Build display styleSheet.
     * @param pSheet the styleSheet
     */
    public static void buildDisplayStyleSheet(final StyleSheet pSheet) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Define standard font for body and table contents */
        myBuilder.append(ELEMENT_BODY);
        myBuilder.append(" { font-family: Verdana, sans-serif; font-size: 1em; color: black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define standard alignment for headers */
        myBuilder.append(ELEMENT_TITLE);
        myBuilder.append(" { text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append(ELEMENT_SUBTITLE);
        myBuilder.append(" { text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define tables */
        myBuilder.append(ELEMENT_TABLE);
        myBuilder.append(" { width: 90%; align: center; border-spacing: 1px; border-collapse: collapse; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define table headers as bold */
        myBuilder.append(ELEMENT_TOTAL);
        myBuilder.append(" { font-weight: bold;");
        myBuilder.append(" border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append(ELEMENT_CELL);
        myBuilder.append(" { border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for title row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_TOTROW);
        myBuilder.append(" { background-color: #c58917; }"); // Cinnamon
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for category row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_CATROW);
        myBuilder.append(" { background-color: #b7ceec; }"); // BlueAngel
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for alternate category row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_ALTCATROW);
        myBuilder.append(" { background-color: #e3e4fa; }"); // Lavender
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for subcategory row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_SUBCATROW);
        myBuilder.append(" { background-color: #89c35c; }"); // GreenPeas
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for alternate subcategory row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_ALTSUBCATROW);
        myBuilder.append(" { background-color: #c3fdb8; }"); // LightJade
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define font size for detail row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_DTLROW);
        myBuilder.append(" { background-color: #c9be62; }"); // GingerBrown
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define font size for alternate detail row */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_ALTDTLROW);
        myBuilder.append(" { background-color: #f3e5ab; }"); // Vanilla
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for data values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_DATAVALUE);
        myBuilder.append(" { text-align: right; color: ");
        myBuilder.append(DataConverter.colorToHexString(Color.blue));
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for negative values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_NEGVALUE);
        myBuilder.append(" { text-align: right; color: ");
        myBuilder.append(DataConverter.colorToHexString(Color.red));
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for title values */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_TITLEVALUE);
        myBuilder.append(" { text-align: center; color: ");
        myBuilder.append(DataConverter.colorToHexString(Color.black));
        myBuilder.append(SEP_ENDRULE);
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define standard display section */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_SHOW);
        myBuilder.append(SEP_BLANK);
        myBuilder.append(ELEMENT_TABLE);
        myBuilder.append(" { display: block; width: 98%; align: right; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define standard hide section */
        myBuilder.append(SEP_DOT);
        myBuilder.append(CLASS_HIDE);
        myBuilder.append(SEP_BLANK);
        myBuilder.append(ELEMENT_TABLE);
        myBuilder.append(" { display: none; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set link definition */
        myBuilder.append(ELEMENT_LINK);
        myBuilder.append(" { font-weight: bold; text-decoration: none; color: ");
        myBuilder.append(DataConverter.colorToHexString(Color.blue));
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
        myBuilder.append("body { font-family: Verdana, sans-serif; font-size: 8px; color: black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define standard alignment for headers */
        myBuilder.append("h1 { text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append("h2 { text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define tables */
        myBuilder.append("table { width: 100%; border-collapse: collapse; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append("td { text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define table headers */
        myBuilder.append("th { font-weight:bold; text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alignment for data values */
        myBuilder.append(".");
        myBuilder.append(CLASS_DATAVALUE);
        myBuilder.append(" { text-align: right; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alignment for negative values */
        myBuilder.append(".");
        myBuilder.append(CLASS_NEGVALUE);
        myBuilder.append(" { text-align: right; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set link definition */
        myBuilder.append("a { font-weight: bold; text-decoration: none; color: black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Add a standard empty cell.
     * @param pParent the parent element
     * @return the new cell
     */
    protected Element makeValueCell(final Element pParent) {
        Element myCell = theDocument.createElement(ELEMENT_CELL);
        pParent.appendChild(myCell);
        return myCell;
    }

    /**
     * Add a standard empty total cell.
     * @param pParent the parent element
     * @return the new cell
     */
    protected Element makeTotalCell(final Element pParent) {
        Element myCell = theDocument.createElement(ELEMENT_TOTAL);
        pParent.appendChild(myCell);
        return myCell;
    }

    /**
     * Add a standard cell.
     * @param pParent the parent element
     * @param pValue the value
     * @return the new cell
     */
    protected Element makeValueCell(final Element pParent,
                                    final Object pValue) {
        Element myCell = makeValueCell(pParent);
        setCellValue(myCell, pValue);
        return myCell;
    }

    /**
     * Add a standard total cell.
     * @param pParent the parent element
     * @param pValue the value
     * @return the new cell
     */
    protected Element makeTotalCell(final Element pParent,
                                    final Object pValue) {
        Element myCell = makeTotalCell(pParent);
        setCellValue(myCell, pValue);
        return myCell;
    }

    /**
     * Add a standard title cell.
     * @param pParent the parent element
     * @param pTitle the title
     * @return the new cell
     */
    protected Element makeTitleCell(final Element pParent,
                                    final String pTitle) {
        Element myCell = makeTotalCell(pParent);
        setCellTitle(myCell, pTitle);
        return myCell;
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
     * Start a table data row.
     * @param pParent the parent element
     * @param pClass the class of the row
     * @return the new row
     */
    private Element startRow(final Element pParent,
                             final String pClass) {
        /* Create the row */
        Element myRow = theDocument.createElement(ELEMENT_ROW);
        pParent.appendChild(myRow);
        myRow.setAttribute(ATTR_CLASS, pClass);
        return myRow;
    }

    /**
     * Start a total row.
     * @param pParent the parent element
     * @return the new row
     */
    protected Element startTotalRow(final Element pParent) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_TOTROW);
        makeTotalCell(myRow);
        return myRow;
    }

    /**
     * Start a total row.
     * @param pParent the parent element
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startTotalRow(final Element pParent,
                                    final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_TOTROW);
        makeTitleCell(myRow, pTitle);
        return myRow;
    }

    /**
     * Start a category row.
     * @param pParent the parent element
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startCategoryRow(final Element pParent,
                                       final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_CATROW);
        Element myCell = makeValueCell(myRow);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_NAME, REF_ID
                                       + pTitle);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_HREF, pTitle);
        myLink.setTextContent(pTitle);
        return myRow;
    }

    /**
     * Start an alternate category row.
     * @param pParent the parent element
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startAlternateCatRow(final Element pParent,
                                           final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_ALTCATROW);
        Element myCell = makeValueCell(myRow);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_NAME, REF_ID
                                       + pTitle);
        myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_HREF, pTitle);
        myLink.setTextContent(pTitle);
        return myRow;
    }

    /**
     * Start a subCategory row.
     * @param pParent the parent element
     * @param pValue the cell contents
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startSubCategoryRow(final Element pParent,
                                          final String pValue,
                                          final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_SUBCATROW);
        Element myCell = makeValueCell(myRow);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_NAME, REF_ID
                                       + pTitle);
        myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_HREF, pTitle);
        myLink.setTextContent(pValue);
        return myRow;
    }

    /**
     * Start a subCategory row.
     * @param pParent the parent element
     * @param pValue the cell contents
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startAlternateSubCatRow(final Element pParent,
                                              final String pValue,
                                              final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_ALTSUBCATROW);
        Element myCell = makeValueCell(myRow);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_NAME, REF_ID
                                       + pTitle);
        myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_HREF, pTitle);
        myLink.setTextContent(pValue);
        return myRow;
    }

    /**
     * Start a detail title row.
     * @param pParent the parent element
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startDetailTitleRow(final Element pParent,
                                          final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_DTLROW);
        makeTitleCell(myRow, pTitle);
        return myRow;
    }

    /**
     * Start a detail row.
     * @param pParent the parent element
     * @param pValue the cell contents
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startDetailRow(final Element pParent,
                                     final String pValue,
                                     final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_DTLROW);
        Element myCell = makeValueCell(myRow);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_HREF, pTitle);
        myLink.setTextContent(pValue);
        return myRow;
    }

    /**
     * Start a detail row.
     * @param pParent the parent element
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startDetailRow(final Element pParent,
                                     final String pTitle) {
        /* Create the row */
        return startDetailRow(pParent, pTitle, pTitle);
    }

    /**
     * Start an alternate detail row.
     * @param pParent the parent element
     * @param pValue the cell contents
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startAlternateRow(final Element pParent,
                                        final String pValue,
                                        final String pTitle) {
        /* Create the row */
        Element myRow = startRow(pParent, CLASS_ALTDTLROW);
        Element myCell = makeValueCell(myRow);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myCell.appendChild(myLink);
        myLink.setAttribute(ATTR_HREF, pTitle);
        myLink.setTextContent(pValue);
        return myRow;
    }

    /**
     * Start a detail row.
     * @param pParent the parent element
     * @param pTitle the title of the row
     * @return the new row
     */
    protected Element startAlternateRow(final Element pParent,
                                        final String pTitle) {
        /* Create the row */
        return startAlternateRow(pParent, pTitle, pTitle);
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
     * @return the table
     */
    protected Element startTable(final Element pBody) {
        /* Create the standard structure */
        Element myTable = theDocument.createElement(ELEMENT_TABLE);
        pBody.appendChild(myTable);
        myTable.setAttribute(ATTR_ALIGN, ALIGN_CENTER);
        myTable.setAttribute(ATTR_WIDTH, WIDTH_MAIN);
        return myTable;
    }

    /**
     * Start an embedded table.
     * @param pParent the parent element
     * @param pTitle the title of the table
     * @param pColumns the number of columns
     * @param bShow initially display the table?
     * @return the new table
     */
    protected Element startEmbeddedTable(final Element pParent,
                                         final String pTitle,
                                         final Integer pColumns,
                                         final boolean bShow) {
        /* Create the row */
        Element myRow = theDocument.createElement(ELEMENT_ROW);
        pParent.appendChild(myRow);
        myRow.setAttribute(ATTR_ID, REF_TAB
                                    + pTitle);
        myRow.setAttribute(ATTR_CLASS, bShow
                ? CLASS_SHOW
                : CLASS_HIDE);
        Element myCell = theDocument.createElement(ELEMENT_CELL);
        myRow.appendChild(myCell);
        myCell.setAttribute(ATTR_COLSPAN, pColumns.toString());
        Element myTable = theDocument.createElement(ELEMENT_TABLE);
        myCell.appendChild(myTable);
        myTable.setAttribute(ATTR_ALIGN, ALIGN_RIGHT);
        myTable.setAttribute(ATTR_WIDTH, WIDTH_EMBED);
        return myTable;
    }

    /**
     * Start Table Header.
     * @param pTable the table
     * @return the table header
     */
    protected Element startTableHeader(final Element pTable) {
        /* Create the standard structure */
        Element myHeader = theDocument.createElement(ELEMENT_THDR);
        pTable.appendChild(myHeader);
        return myHeader;
    }

    /**
     * Start Table Body.
     * @param pTable the table
     * @return the table body
     */
    protected Element startTableBody(final Element pTable) {
        /* Create the standard structure */
        Element myBody = theDocument.createElement(ELEMENT_TBODY);
        pTable.appendChild(myBody);
        return myBody;
    }
}
