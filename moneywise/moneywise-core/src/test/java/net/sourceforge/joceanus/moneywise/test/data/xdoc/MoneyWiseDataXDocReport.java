/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.test.data.xdoc;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * XDoc Report Utilities.
 */
public class MoneyWiseDataXDocReport {
    /**
     * The class attribute.
     */
    private static final String ATTR_CLASS = "class";

    /**
     * The name attribute.
     */
    private static final String ATTR_NAME = "name";

    /**
     * The formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * The document builder.
     */
    private final DocumentBuilder theBuilder;

    /**
     * The Transformer.
     */
    private final Transformer theXformer;

    /**
     * The document.
     */
    private Document theDocument;

    /**
     * The document body.
     */
    private Element theBody;

    /**
     * The section.
     */
    private Element theSection;

    /**
     * The detail.
     */
    private Element theDetail;

    /**
     * The subdetail.
     */
    private Element theSubDetail;

    /**
     * The table.
     */
    private Element theTable;

    /**
     * The row.
     */
    private Element theRow;

    /**
     * The cell.
     */
    private Element theCell;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MoneyWiseDataXDocReport() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Store the formatter */
            theFormatter = new OceanusDataFormatter();

            /* Create the document builder */
            final DocumentBuilderFactory myDocFactory = DocumentBuilderFactory.newInstance();
            myDocFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myDocFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myDocFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            theBuilder = myDocFactory.newDocumentBuilder();

            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            myXformFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            myXformFactory.setAttribute("indent-number", 2);
            theXformer = myXformFactory.newTransformer();
            theXformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            theXformer.setOutputProperty(OutputKeys.INDENT, "yes");

        } catch (Exception e) {
            throw new MoneyWiseIOException("Failed to create", e);
        }
    }

    /**
     * Start Report.
     * @param pTest the testcase
     */
    public void startReport(final MoneyWiseDataTestCase pTest) {
        /* Create the new document */
        theDocument = theBuilder.newDocument();

        /* Create the initial document */
        final Element myDoc = theDocument.createElement("document");
        theDocument.appendChild(myDoc);

        /* Create the properties */
        final Element myProp = theDocument.createElement("properties");
        myDoc.appendChild(myProp);
        final Element myTitle = theDocument.createElement("title");
        myProp.appendChild(myTitle);
        myTitle.setTextContent(pTest.getTitle());
        final Element myAuth = theDocument.createElement("author");
        myProp.appendChild(myAuth);
        myAuth.setTextContent("Tony Washer");
        myAuth.setAttribute("email", "tony.washer@yahoo.co.uk");

        /* Create the body and initial section */
        final Element myBody = theDocument.createElement("body");
        myDoc.appendChild(myBody);
        theBody = theDocument.createElement("section");
        theBody.setAttribute(ATTR_NAME, pTest.getTitle());
        myBody.appendChild(theBody);

        /* Add initial paragraph */
        final Element myPara = theDocument.createElement("p");
        myPara.setTextContent(pTest.getDesc());
        theBody.appendChild(myPara);
    }

    /**
     * Create New section.
     * @param pTitle the title
     */
    void newSection(final String pTitle) {
        theSection = theDocument.createElement("subsection");
        theSection.setAttribute(ATTR_NAME, pTitle);
        theBody.appendChild(theSection);
    }

    /**
     * Add paragraph.
     * @param pText the text
     */
    void addParagraph(final String pText) {
        final Element myPara = theDocument.createElement("p");
        myPara.setTextContent(pText);
        theSection.appendChild(myPara);
    }

    /**
     * Create New detail for table.
     * @param pGroup the group
     * @param pSummary the summary
     */
    void newDetail(final String pGroup,
                   final String pSummary) {
        theDetail = theDocument.createElement("details");
        theDetail.setAttribute(ATTR_NAME, pGroup);
        final Element mySummary = theDocument.createElement("summary");
        mySummary.setTextContent(pSummary);
        mySummary.setAttribute(ATTR_CLASS, "mainDtl");
        theDetail.appendChild(mySummary);
        theSection.appendChild(theDetail);
        theSubDetail = theDetail;
    }

    /**
     * Create New open detail for table.
     * @param pGroup the group
     * @param pSummary the summary
     */
    void newOpenDetail(final String pGroup,
                       final String pSummary) {
        newDetail(pGroup, pSummary);
        theDetail.setAttribute("open", "true");
    }

    /**
     * Create New subDetail for table.
     * @param pGroup the group
     * @param pSummary the summary
     */
    void newSubDetail(final String pGroup,
                      final String pSummary) {
        theSubDetail = theDocument.createElement("details");
        theSubDetail.setAttribute(ATTR_NAME, pGroup);
        final Element mySummary = theDocument.createElement("summary");
        mySummary.setAttribute(ATTR_CLASS, "subDtl");
        mySummary.setTextContent(pSummary);
        theSubDetail.appendChild(mySummary);
        theDetail.appendChild(theSubDetail);
    }

    /**
     * Create New open subDetail for table.
     * @param pGroup the group
     * @param pSummary the summary
     */
    void newOpenSubDetail(final String pGroup,
                          final String pSummary) {
        newSubDetail(pGroup, pSummary);
        theSubDetail.setAttribute("open", "true");
    }

    /**
     * Add line break.
     */
    void newLine() {
        theSection.appendChild(theDocument.createElement("br"));
    }

    /**
     * Create New Table.
     */
    void newTable() {
        theTable = theDocument.createElement("table");
        theTable.setAttribute(ATTR_CLASS, "defTable");
        theSubDetail.appendChild(theTable);
    }

    /**
     * Create New Table Row.
     */
    void newRow() {
        theRow = theDocument.createElement("tr");
    }

    /**
     * Create New Table Row.
     */
    void addRowToTable() {
        theTable.appendChild(theRow);
    }

    /**
     * Create New Table Header.
     */
    void newHeader() {
        theCell = theDocument.createElement("th");
        theCell.setAttribute(ATTR_CLASS, "defHdr");
        theRow.appendChild(theCell);
    }

    /**
     * newColumnSpanHeader.
     * @param pSpan the columns to span
     */
    void newColSpanHeader(final int pSpan) {
        newHeader();
        if (pSpan > 1) {
            theCell.setAttribute("colspan", theFormatter.formatObject(pSpan));
        }
    }

    /**
     * newRowSpanHeader.
     * @param pSpan the rows to span
     */
    void newRowSpanHeader(final int pSpan) {
        newHeader();
        if (pSpan > 1) {
            theCell.setAttribute("rowspan", theFormatter.formatObject(pSpan));
        }
    }

    /**
     * newCell.
     */
    void newCell() {
        theCell = theDocument.createElement("td");
        theRow.appendChild(theCell);
    }

    /**
     * newColumnSpanCell.
     * @param pSpan the columns to span
     */
    void newColumnSpanCell(final int pSpan) {
        newCell();
        if (pSpan > 1) {
            theCell.setAttribute("colspan", theFormatter.formatObject(pSpan));
        }
    }

    /**
     * newBoldSpanCell.
     * @param pSpan the columns to span
     */
    void newBoldSpanCell(final int pSpan) {
        theCell = theDocument.createElement("th");
        theRow.appendChild(theCell);
        if (pSpan > 1) {
            theCell.setAttribute("colspan", theFormatter.formatObject(pSpan));
        }
    }

    /**
     * Set a split cell value.
     * @param pValue the value for the cell
     */
    void setSplitCellValue(final String pValue) {
        /* Split the text on : */
        final int iIndex = pValue.indexOf(':');
        if (iIndex != -1) {
            theCell.appendChild(theDocument.createTextNode(pValue.substring(0, iIndex + 1)));
            theCell.appendChild(theDocument.createElement("br"));
            theCell.appendChild(theDocument.createTextNode(pValue.substring(iIndex + 1)));
        } else {
            setCellValue(pValue);
        }
    }

    /**
     * Set a cell value.
     * @param pValue the value for the cell
     */
    void setCellValue(final String pValue) {
        theCell.setTextContent(pValue);
    }

    /**
     * Set a cell value.
     * @param pValue the value for the cell
     */
    void setCellValue(final OceanusDate pValue) {
        theCell.setTextContent(theFormatter.formatObject(pValue));
    }

    /**
     * Set a cell value.
     * @param pValue the value for the cell
     */
    void setCellValue(final OceanusDecimal pValue) {
        Object myValue = pValue;
        String myClass = "dataValue";

        /* Ignore value if zero */
        if (pValue == null || pValue.isZero()) {
            myValue = null;

        /* Switch class if negative */
        } else if (!pValue.isPositive()) {
            myClass = "negValue";
        }

        /* Set class of cell */
        theCell.setAttribute(ATTR_CLASS, myClass);

        /* Set value of cell */
        if (myValue != null) {
            theCell.setTextContent(theFormatter.formatObject(myValue));
        }
    }

    /**
     * Format XML.
     * @return the formatted XML
     * @throws OceanusException on error
     */
    public String formatXML() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Transform the new document */
            final StringWriter myWriter = new StringWriter();
            theXformer.transform(new DOMSource(theDocument), new StreamResult(myWriter));
            return myWriter.toString();

        } catch (TransformerException e) {
            throw new MoneyWiseIOException("Failed to format", e);
        }
    }
}
