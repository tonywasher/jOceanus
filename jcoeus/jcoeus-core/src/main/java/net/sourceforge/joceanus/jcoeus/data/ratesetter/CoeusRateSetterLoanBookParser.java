/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2019 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data.ratesetter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * RateSetter LoanBook Parser.
 */
public class CoeusRateSetterLoanBookParser {
    /**
     * Parsed fields.
     */
    private final List<CoeusRateSetterLoanBookItem> theLoans;

    /**
     * Date Parser.
     */
    private final TethysDateFormatter theDateParser;

    /**
     * Decimal Parser.
     */
    private final TethysDecimalParser theDecimalParser;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    protected CoeusRateSetterLoanBookParser(final MetisDataFormatter pFormatter) {
        /* Create the loan list */
        theLoans = new ArrayList<>();

        /* Access the formatters */
        theDateParser = pFormatter.getDateFormatter();
        theDecimalParser = pFormatter.getDecimalParser();
    }

    /**
     * Obtain the loans.
     * @return the loans
     */
    public Iterator<CoeusRateSetterLoanBookItem> loanIterator() {
        return theLoans.iterator();
    }

    /**
     * Parse a date value.
     * @param pInput the input string
     * @return the parsed date
     * @throws OceanusException on error
     */
    protected TethysDate parseDate(final String pInput) throws OceanusException {
        try {
            return theDateParser.parseDate(pInput);
        } catch (IllegalArgumentException e) {
            throw new CoeusDataException("Bad date", e);
        }
    }

    /**
     * Parse a money value.
     * @param pInput the input string
     * @return the parsed money
     * @throws OceanusException on error
     */
    protected TethysMoney parseMoney(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseMoneyValue(pInput);
        } catch (IllegalArgumentException e) {
            throw new CoeusDataException("Bad money", e);
        }
    }

    /**
     * Parse a rate.
     * @param pInput the input string
     * @return the parsed rate
     * @throws OceanusException on error
     */
    protected TethysRate parseRate(final String pInput) throws OceanusException {
        try {
            return theDecimalParser.parseRateValue(pInput);
        } catch (IllegalArgumentException e) {
            throw new CoeusDataException("Bad rate", e);
        }
    }

    /**
     * Parse file.
     * @param pInput the input file
     * @throws OceanusException on error
     */
    public void parseFile(final Path pInput) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Reset the list */
            theLoans.clear();

            /* Set the parser correctly */
            theDateParser.setFormat("dd/MM/yyyy");

            /* Read the document from the stream and parse it */
            final Document myDocument = Jsoup.parse(pInput.toFile(), StandardCharsets.UTF_8.name());

            /* Look for the two types of tables */
            final Elements myOldTables = myDocument.getElementsByClass("rsTable");
            final Elements myNewTables = myDocument.getElementsByClass("novo-table-content");

            /* Use old style tables if they are present */
            final Elements myTables = myOldTables.isEmpty() ? myNewTables : myOldTables;

            /* select the body of the last of the tables */
            final boolean isRepaid = myTables.size() > 1;
            final Element myBody = myTables.last().select("tbody").first();
            final Element myFirstCol = myTables.last().select("th").first();
            final boolean isNewStyle = !"Contract".equals(myFirstCol.text());

            /* Obtain a list of rows */
            final List<Element> myRows = new ArrayList<>();
            final List<Element> myCells = new ArrayList<>();
            listChildElements(myBody, "tr", myRows);

            /* Loop through the rows */
            for (final Element myRow : myRows) {
                /* Ignore if this is a summary row */
                if (!myRow.hasClass("rsTableSummaryRow")) {
                    /* Obtain the cells */
                    listChildElements(myRow, "td", myCells);

                    /* Skip if this is the final empty cell */
                    if (myCells.size() == 1
                        && myCells.get(0).hasClass("rsTableFinalEmptyCell")) {
                        continue;
                    }

                    /* Add the loan book item */
                    theLoans.add(new CoeusRateSetterLoanBookItem(this, isNewStyle, isRepaid, myCells));
                }
            }

            /* Catch exceptions */
        } catch (IOException e) {
            throw new CoeusDataException("Failed to parse file", e);
        }
    }

    /**
     * Extract all child elements of the required type.
     * @param pElement the parent element
     * @param pName the element name
     * @param pList the list of elements
     */
    private static void listChildElements(final Element pElement,
                                          final String pName,
                                          final List<Element> pList) {
        /* Reset the list */
        pList.clear();

        /* Loop through the childNodes */
        for (final Node myNode : pElement.childNodes()) {
            /* If this is an element */
            if (myNode instanceof Element) {
                /* If it is a required child */
                final Element myChild = (Element) myNode;
                if (pName.equals(myNode.nodeName())) {
                    pList.add(myChild);
                }
            }
        }
    }

    /**
     * Extract text from all children without adding blank between elements.
     * @param pElement the parent element
     * @return the text
     */
    protected String childElementText(final Element pElement) {
        /* Reset string builder */
        final StringBuilder myBuilder = new StringBuilder();
        final Element myRow = pElement.select("tr").first();

        /* Loop through the childNodes */
        for (final Node myNode : myRow.childNodes()) {
            /* If this is an element */
            if (myNode instanceof Element) {
                /* If it is a required child */
                final Element myChild = (Element) myNode;
                if ("td".equals(myNode.nodeName())) {
                    myBuilder.append(myChild.text());
                }
            }
        }

        /* return the accumulated text */
        return myBuilder.toString();
    }
}
