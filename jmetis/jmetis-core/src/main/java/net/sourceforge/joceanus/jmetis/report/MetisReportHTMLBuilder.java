/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.report;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.joceanus.jmetis.MetisIOException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Build a report document.
 */
public class MetisReportHTMLBuilder {
    /**
     * The class attribute.
     */
    private static final String ATTR_CLASS = "class";

    /**
     * The id attribute.
     */
    protected static final String ATTR_ID = "id";

    /**
     * The name attribute.
     */
    private static final String ATTR_NAME = "name";

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
     * The align centre value.
     */
    private static final String ALIGN_CENTER = "center";

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
     * Name of subTable class.
     */
    private static final String CLASS_SUBTABLE = "subtable";

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
     * Name of accordianValue class.
     */
    private static final String CLASS_ACCORDIANVALUE = "accordianValue";

    /**
     * Name of linkValue class.
     */
    private static final String CLASS_LINKVALUE = "linkValue";

    /**
     * Name of linkObject class.
     */
    private static final String CLASS_LINKOBJECT = "linkObject";

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
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @throws OceanusException on error
     */
    public MetisReportHTMLBuilder(final MetisDataFormatter pFormatter) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Store the formatter */
            theFormatter = pFormatter;

            /* Create the document builder */
            DocumentBuilderFactory myDocFactory = DocumentBuilderFactory.newInstance();
            theBuilder = myDocFactory.newDocumentBuilder();

        } catch (Exception e) {
            throw new MetisIOException("Failed to create", e);
        }
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public MetisDataFormatter getDataFormatter() {
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
     * Create and append a standard empty cell.
     * @param pControl the table control
     */
    public void makeValueCell(final MetisHTMLTable pControl) {
        pControl.createNewCell(false);
    }

    /**
     * Create and append a standard empty total cell.
     * @param pControl the table control
     */
    public void makeTotalCell(final MetisHTMLTable pControl) {
        pControl.createNewCell(true);
    }

    /**
     * Create and append a standard empty title cell.
     * @param pControl the table control
     */
    public void makeTitleCell(final MetisHTMLTable pControl) {
        pControl.createNewCell(true);
    }

    /**
     * Create and append a standard cell with value.
     * @param pControl the table control
     * @param pValue the value
     */
    public void makeValueCell(final MetisHTMLTable pControl,
                              final Object pValue) {
        Element myCell = pControl.createNewCell(false);
        setCellValue(myCell, pValue);
    }

    /**
     * Create and append a standard cell with value spanning 2 columns.
     * @param pControl the table control
     * @param pValue the value
     */
    public void makeStretchedValueCell(final MetisHTMLTable pControl,
                                       final Object pValue) {
        Element myCell = pControl.createNewCell(false);
        setCellValue(myCell, pValue);
        myCell.setAttribute(ATTR_COLSPAN, Integer.toString(2));
    }

    /**
     * Create and append a total cell with value.
     * @param pControl the table control
     * @param pValue the value
     */
    public void makeTotalCell(final MetisHTMLTable pControl,
                              final Object pValue) {
        Element myCell = pControl.createNewCell(true);
        setCellValue(myCell, pValue);
    }

    /**
     * Create and append a standard total cell with value spanning 2 columns.
     * @param pControl the table control
     * @param pValue the value
     */
    public void makeStretchedTotalCell(final MetisHTMLTable pControl,
                                       final Object pValue) {
        Element myCell = pControl.createNewCell(true);
        setCellValue(myCell, pValue);
        myCell.setAttribute(ATTR_COLSPAN, Integer.toString(2));
    }

    /**
     * Create and append a title cell.
     * @param pControl the table control
     * @param pTitle the title
     */
    public void makeTitleCell(final MetisHTMLTable pControl,
                              final String pTitle) {
        Element myCell = pControl.createNewCell(true);
        setCellTitle(myCell, pTitle);
    }

    /**
     * Create and append a standard title cell with title spanning 2 columns.
     * @param pControl the table control
     * @param pTitle the title
     */
    public void makeStretchedTitleCell(final MetisHTMLTable pControl,
                                       final String pTitle) {
        Element myCell = pControl.createNewCell(true);
        setCellTitle(myCell, pTitle);
        myCell.setAttribute(ATTR_COLSPAN, Integer.toString(2));
    }

    /**
     * Make Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     */
    public void makeTableLinkCell(final MetisHTMLTable pControl,
                                  final String pLink) {
        makeTableLinkCell(pControl, pLink, pLink);
    }

    /**
     * Make Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     * @param pName the link table display name
     */
    public void makeTableLinkCell(final MetisHTMLTable pControl,
                                  final String pLink,
                                  final String pName) {
        /* Determine the id of the link */
        String myId = REF_ID
                      + pLink;

        /* Create the cell */
        Element myCell = pControl.createNewCell(false);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myLink.setAttribute(ATTR_CLASS, CLASS_ACCORDIANVALUE);
        myLink.setAttribute(ATTR_ID, myId);
        myLink.setIdAttribute(ATTR_ID, true);
        myLink.setAttribute(ATTR_NAME, myId);
        myLink.setAttribute(ATTR_HREF, REF_TAB
                                       + pLink);
        myLink.setTextContent(pName);
        myCell.appendChild(myLink);
    }

    /**
     * Make Delayed Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     */
    public void makeDelayLinkCell(final MetisHTMLTable pControl,
                                  final String pLink) {
        makeDelayLinkCell(pControl, pLink, pLink);
    }

    /**
     * Make Delayed Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     * @param pName the link table display name
     */
    public void makeDelayLinkCell(final MetisHTMLTable pControl,
                                  final String pLink,
                                  final String pName) {
        /* Determine the id of the link */
        String myId = REF_ID
                      + pLink;

        /* Create the cell */
        Element myCell = pControl.createNewCell(false);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myLink.setAttribute(ATTR_CLASS, CLASS_ACCORDIANVALUE);
        myLink.setAttribute(ATTR_ID, myId);
        myLink.setIdAttribute(ATTR_ID, true);
        myLink.setAttribute(ATTR_NAME, myId);
        myLink.setAttribute(ATTR_HREF, REF_DELAY
                                       + pLink);
        myLink.setTextContent(pName);
        myCell.appendChild(myLink);
    }

    /**
     * Make Filter link cell.
     * @param pControl the table control
     * @param pLink the link table name
     */
    public void makeFilterLinkCell(final MetisHTMLTable pControl,
                                   final String pLink) {
        makeFilterLinkCell(pControl, pLink, pLink);
    }

    /**
     * Make Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     * @param pName the link table display name
     */
    public void makeFilterLinkCell(final MetisHTMLTable pControl,
                                   final String pLink,
                                   final String pName) {
        Element myCell = pControl.createNewCell(false);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myLink.setAttribute(ATTR_CLASS, CLASS_LINKVALUE);
        myLink.setAttribute(ATTR_HREF, REF_FILTER
                                       + pLink);
        myLink.setTextContent(pName);
        myCell.appendChild(myLink);
    }

    /**
     * Make Table link cell.
     * @param pControl the table control
     * @param pLink the link table name
     * @param pValue the link table display value
     */
    public void makeFilterLinkCell(final MetisHTMLTable pControl,
                                   final String pLink,
                                   final Object pValue) {
        Element myCell = pControl.createNewCell(false);
        myCell.setAttribute(ATTR_CLASS, CLASS_LINKOBJECT);
        Element myLink = theDocument.createElement(ELEMENT_LINK);
        myLink.setAttribute(ATTR_HREF, REF_FILTER
                                       + pLink);
        setCellValue(myLink, pValue);
        myCell.appendChild(myLink);
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
        if (myValue instanceof TethysDecimal) {
            /* Access as decimal */
            TethysDecimal myDec = (TethysDecimal) myValue;

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
    private static void setCellTitle(final Element pCell,
                                     final String pTitle) {
        /* Set class and content of cell */
        pCell.setAttribute(ATTR_CLASS, CLASS_TITLEVALUE);
        pCell.setTextContent(pTitle);
    }

    /**
     * Start a table header row.
     * @param pControl the table control
     */
    public void startHdrRow(final MetisHTMLTable pControl) {
        /* Create the row */
        pControl.createNewRow(true);
    }

    /**
     * Start a table data row.
     * @param pControl the table control
     */
    public void startRow(final MetisHTMLTable pControl) {
        /* Create the row */
        pControl.createNewRow(false);
    }

    /**
     * Start a table total row.
     * @param pControl the table control
     */
    public void startTotalRow(final MetisHTMLTable pControl) {
        /* Create the row */
        pControl.createTotalRow();
    }

    /**
     * Start Report.
     * @return the body
     */
    public Element startReport() {
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
    public void makeTitle(final Element pBody,
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
    public void makeTitle(final Element pBody,
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
    public void makeSubTitle(final Element pBody,
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
    public MetisHTMLTable startTable(final Element pBody) {
        /* Create the standard structure */
        Element myTable = theDocument.createElement(ELEMENT_TABLE);
        pBody.appendChild(myTable);
        myTable.setAttribute(ATTR_ALIGN, ALIGN_CENTER);

        /* Create the table control */
        return new MetisHTMLTable(myTable);
    }

    /**
     * Create an embedded table.
     * @param pParent the parent element
     * @return the new table
     */
    public MetisHTMLTable createEmbeddedTable(final MetisHTMLTable pParent) {
        /* Create the table */
        Element myTable = theDocument.createElement(ELEMENT_TABLE);
        myTable.setAttribute(ATTR_CLASS, CLASS_SUBTABLE);
        myTable.setAttribute(ATTR_ALIGN, ALIGN_CENTER);

        /* Create the table control */
        return new MetisHTMLTable(myTable, pParent);
    }

    /**
     * Embed a table into the document.
     * @param pTable the table to embed
     * @param pTitle the title of the table
     */
    public void embedTable(final MetisHTMLTable pTable,
                           final String pTitle) {
        /* Access body element */
        MetisHTMLTable myParent = pTable.getParent();
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
     * Embed a table into the document.
     * @param pTable the table to embed
     */
    public void embedTable(final MetisHTMLTable pTable) {
        /* Access body element */
        MetisHTMLTable myParent = pTable.getParent();
        Element myBody = myParent.getTableBody();

        /* Create the row */
        Element myRow = theDocument.createElement(ELEMENT_ROW);
        Element myCell = theDocument.createElement(ELEMENT_CELL);
        myRow.appendChild(myCell);
        myCell.setAttribute(ATTR_COLSPAN, Integer.toString(myParent.getNumCols()));
        myCell.appendChild(pTable.getTable());

        /* Insert into the correct place in the document */
        myBody.appendChild(myRow);
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
    public final class MetisHTMLTable {
        /**
         * Table class type.
         */
        private final TableClass theClass;

        /**
         * The table parent.
         */
        private final MetisHTMLTable theParent;

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
        private MetisHTMLTable(final Element pTable,
                          final MetisHTMLTable pParent) {
            /* Store parameters */
            theTable = pTable;
            theParent = pParent;
            theClass = pParent.getNextTableClass();
        }

        /**
         * Constructor.
         * @param pTable the table element
         */
        private MetisHTMLTable(final Element pTable) {
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
        private MetisHTMLTable getParent() {
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
            Element myParent = bHdr
                                    ? getTableHeader()
                                    : getTableBody();

            /* Create the row */
            theRow = theDocument.createElement(ELEMENT_ROW);
            myParent.appendChild(theRow);
            theRow.setAttribute(ATTR_CLASS, bHdr
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
            String myCellType = bTotal
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
