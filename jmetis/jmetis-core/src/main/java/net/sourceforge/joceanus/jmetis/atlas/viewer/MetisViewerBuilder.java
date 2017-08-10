/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.viewer;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.MetisIOException;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionDelta.MetisDataDelta;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Data Viewer Builder.
 */
public class MetisViewerBuilder {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisViewerBuilder.class);

    /**
     * Wrap for hex string.
     */
    private static final int WRAP_HEXSTRING = 60;

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
    private static final String ELEMENT_TITLE = "h2";

    /**
     * The link element.
     */
    private static final String ELEMENT_LINK = "a";

    /**
     * The table element.
     */
    private static final String ELEMENT_TABLE = "table";

    /**
     * The tableHead element.
     */
    private static final String ELEMENT_THEAD = "thead";

    /**
     * The tableBody element.
     */
    private static final String ELEMENT_TBODY = "tbody";

    /**
     * The tableRow element.
     */
    private static final String ELEMENT_TROW = "tr";

    /**
     * The tableHdr element.
     */
    private static final String ELEMENT_THDR = "th";

    /**
     * The tableCell element.
     */
    private static final String ELEMENT_TCELL = "td";

    /**
     * The class attribute.
     */
    private static final String ATTR_CLASS = "class";

    /**
     * The hRef attribute.
     */
    private static final String ATTR_HREF = "href";

    /**
     * Name of table class.
     */
    private static final String CLASS_VIEWER = "-metis-viewer";

    /**
     * Name of odd table row class.
     */
    private static final String CLASS_ODDROW = "-metis-oddrow";

    /**
     * Name of even table row class.
     */
    private static final String CLASS_EVENROW = "-metis-evenrow";

    /**
     * Name of changed cell class.
     */
    private static final String CLASS_CHANGED = "-metis-changed";

    /**
     * Name of security changed cell class.
     */
    private static final String CLASS_SECCHANGED = "-metis-security";

    /**
     * The data formatter.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * The document builder.
     */
    private final DocumentBuilder theBuilder;

    /**
     * Transformer.
     */
    private final Transformer theXformer;

    /**
     * The document.
     */
    private final Document theDocument;

    /**
     * The document body.
     */
    private final Element theBody;

    /**
     * The page.
     */
    private MetisViewerPage thePage;

    /**
     * The table Header row.
     */
    private Element theTblHdr;

    /**
     * The table body.
     */
    private Element theTblBody;

    /**
     * The current table row.
     */
    private Element theTblRow;

    /**
     * The number of rows.
     */
    private int theNumRows;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @throws OceanusException on error
     */
    protected MetisViewerBuilder(final TethysDataFormatter pFormatter) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Store parameters */
            theFormatter = pFormatter;

            /* Create the document builder */
            final DocumentBuilderFactory myDocFactory = DocumentBuilderFactory.newInstance();
            theBuilder = myDocFactory.newDocumentBuilder();

            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            theXformer = myXformFactory.newTransformer();

            /* Create the document */
            theDocument = theBuilder.newDocument();

            /* Create the standard structure */
            final Element myHtml = theDocument.createElement(ELEMENT_HTML);
            theDocument.appendChild(myHtml);
            theBody = theDocument.createElement(ELEMENT_BODY);
            myHtml.appendChild(theBody);

        } catch (Exception e) {
            throw new MetisIOException("Failed to create", e);
        }
    }

    /**
     * Format document.
     */
    protected void formatDocument() {
        /* protect against exceptions */
        try {
            /* Format the XML and write to stream */
            final StringWriter myWriter = new StringWriter();
            theXformer.transform(new DOMSource(theDocument), new StreamResult(myWriter));

            /* Convert result to string */
            thePage.setHtml(myWriter.toString());

        } catch (TransformerException e) {
            LOGGER.error("Failed to format document", e);
        }
    }

    /**
     * Clear an element.
     * @param pElement the element to clear
     */
    private static void clearElement(final Element pElement) {
        /* Clear the existing elements */
        while (pElement.hasChildNodes()) {
            pElement.removeChild(pElement.getFirstChild());
        }
    }

    /**
     * Reset the document.
     * @param pPage the associated page
     */
    protected void resetDocument(final MetisViewerPage pPage) {
        /* Reset the page */
        thePage = pPage;
        thePage.resetPage();

        /* Reset the document */
        clearElement(theBody);
    }

    /**
     * Make title.
     * @param pTitle the title
     */
    protected void newTitle(final String pTitle) {
        /* Create the title */
        final Element myTitle = theDocument.createElement(ELEMENT_TITLE);
        theBody.appendChild(myTitle);
        myTitle.setTextContent(pTitle);
    }

    /**
     * Make new table.
     */
    protected void newTable() {
        /* Create the table */
        final Element myTable = theDocument.createElement(ELEMENT_TABLE);
        myTable.setAttribute(ATTR_CLASS, CLASS_VIEWER);
        theBody.appendChild(myTable);
        final Element myHead = theDocument.createElement(ELEMENT_THEAD);
        myTable.appendChild(myHead);
        theTblHdr = theDocument.createElement(ELEMENT_TROW);
        myHead.appendChild(theTblHdr);
        theTblBody = theDocument.createElement(ELEMENT_TBODY);
        myTable.appendChild(theTblBody);

        /* Set counters */
        theTblRow = null;
        theNumRows = 0;
    }

    /**
     * Make new title cell.
     * @param pTitle the title
     */
    protected void newTitleCell(final String pTitle) {
        /* Create the title cell */
        final Element myCell = theDocument.createElement(ELEMENT_THDR);
        theTblHdr.appendChild(myCell);
        myCell.setTextContent(pTitle);
    }

    /**
     * Make new table row.
     */
    protected void newTableRow() {
        /* Create the table row */
        theTblRow = theDocument.createElement(ELEMENT_TROW);
        theTblBody.appendChild(theTblRow);

        /* Set correct class */
        theTblRow.setAttribute(ATTR_CLASS, (theNumRows % 2 == 0)
                                                                 ? CLASS_EVENROW
                                                                 : CLASS_ODDROW);
        theNumRows++;
    }

    /**
     * Make new data cell.
     * @param pData the data
     */
    protected void newDataCell(final Object pData) {
        newDataCell(pData, false);
    }

    /**
     * Make new data cell.
     * @param pData the data
     * @param pChanged is this a changed field true/false
     */
    protected void newDataCell(final Object pData,
                               final boolean pChanged) {
        /* Create the data cell */
        final Element myCell = theDocument.createElement(ELEMENT_TCELL);
        theTblRow.appendChild(myCell);

        /* Determine the text */
        final String myText = formatValue(pData);

        /* If the Object is a data delta */
        if (pData instanceof MetisDataDelta) {
            /* Access the difference */
            final MetisDataDifference myDiff = ((MetisDataDelta) pData).getDifference();

            /* If there is a difference */
            if (!myDiff.isIdentical()) {
                myCell.setAttribute(ATTR_CLASS, myDiff.isValueChanged()
                                                                        ? CLASS_CHANGED
                                                                        : CLASS_SECCHANGED);
            }
        } else if (pChanged) {
            myCell.setAttribute(ATTR_CLASS, CLASS_CHANGED);
        }

        /* If the object is link-able */
        if (MetisViewerPage.isLinkable(pData)) {
            /* Create the link */
            final Element myLink = theDocument.createElement(ELEMENT_LINK);
            myCell.appendChild(myLink);
            myLink.setAttribute(ATTR_HREF, thePage.newLink(pData));
            myLink.setTextContent(myText);

            /* else just record the formatted text */
        } else {
            myCell.setTextContent(myText);
        }
    }

    /**
     * Format a value.
     * @param pValue the value to format
     * @return the formatted value
     */
    private String formatValue(final Object pValue) {
        /* Format the value */
        String myFormat = theFormatter.formatObject(pValue);

        /* Perform special formatting for a long byte[] */
        if (needsWrapping(pValue)
            && (myFormat.length() > WRAP_HEXSTRING)) {
            final StringBuilder myBuffer = new StringBuilder(myFormat.length() << 1);

            /* Format the buffer */
            myBuffer.append(myFormat);

            /* Insert new lines */
            int iCount = myFormat.length()
                         / WRAP_HEXSTRING;
            while (iCount > 0) {
                myBuffer.insert(WRAP_HEXSTRING
                                * iCount--, '\n');
            }

            /* Obtain new format */
            myFormat = myBuffer.toString();
        }

        /* Return the formatted value */
        return myFormat;
    }

    /**
     * does the object format need wrapping?
     * @param pObject the object
     * @return true/false
     */
    private static boolean needsWrapping(final Object pObject) {
        /* Determine whether we need wrapping */
        Object myObject = pObject;
        if (MetisDataDelta.class.isInstance(myObject)) {
            myObject = ((MetisDataDelta) pObject).getObject();
        }
        return byte[].class.isInstance(myObject);
    }
}
