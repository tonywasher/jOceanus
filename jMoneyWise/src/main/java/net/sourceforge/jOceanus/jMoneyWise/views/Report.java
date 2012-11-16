/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.awt.Color;

import javax.swing.text.html.StyleSheet;

import net.sourceforge.jOceanus.jDataManager.DataConverter;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDecimal.JDecimal;

/**
 * Report Classes.
 * @author Tony Washer
 */
public final class Report {
    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * Name of odd table row class.
     */
    private static final String CLASS_ODDROW = "oddRow";

    /**
     * Name of even table row class.
     */
    private static final String CLASS_EVENROW = "evenRow";

    /**
     * Name of row header class.
     */
    private static final String CLASS_ROWHEADER = "rowHeader";

    /**
     * Name of dataValue class.
     */
    private static final String CLASS_DATAVALUE = "dataValue";

    /**
     * Name of negativeValue class.
     */
    private static final String CLASS_NEGVALUE = "negValue";

    /**
     * Name of top reference.
     */
    private static final String LINK_TOP = "Top";

    /**
     * Name of link reference.
     */
    private static final String LINK_REF = "Detail";

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
     * Constructor.
     */
    protected Report() {
        theFormatter = new JDataFormatter();
    }

    /**
     * Build display styleSheet.
     * @param pSheet the styleSheet
     */
    public static void buildDisplayStyleSheet(final StyleSheet pSheet) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Define standard font for body and table contents */
        myBuilder.append("body { font-family: Verdana, sans-serif; font-size: 1em; color: black; }");
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
        myBuilder.append("table { width: 90%; margin-left:5%; margin-right:5%; ");
        myBuilder.append("text-align: center; border-spacing: 1px; border-collapse: collapse; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for oddRow */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_ODDROW);
        myBuilder.append(" td { background-color: #dddddd; border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define background colour for evenRow */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_EVENROW);
        myBuilder.append(" td { background-color: #eeeeee; border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define table headers */
        myBuilder.append("th { text-align: center; background-color: #bbbbbb; font-weight: bold;");
        myBuilder.append(" color: white; border: 1px solid white; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define font weight for rowHeader */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_ROWHEADER);
        myBuilder.append(" { font-weight: bold; text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for data values */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_DATAVALUE);
        myBuilder.append(" { text-align: right; color: blue; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define colour and alignment for negative values */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_NEGVALUE);
        myBuilder.append(" { text-align: right; color: red; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set link definition */
        myBuilder.append("a { font-weight: bold; text-decoration: none; color: ");
        myBuilder.append(DataConverter.colorToHexString(Color.blue));
        myBuilder.append(";}");
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
        myBuilder.append("table { width: 100%; border-collapse: collapse; border: 1px solid black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
        myBuilder.append("td { border: 1px solid black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define table headers */
        myBuilder.append("th { font-weight:bold; text-align: center; border: 1px solid black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define row header */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_ROWHEADER);
        myBuilder.append(" { font-weight: bold; text-align: center; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alignment for data values */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_DATAVALUE);
        myBuilder.append(" { text-align: right; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Define alignment for negative values */
        myBuilder.append(".");
        myBuilder.append(Report.CLASS_NEGVALUE);
        myBuilder.append(" { text-align: right; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);

        /* Set link definition */
        myBuilder.append("a { font-weight: bold; text-decoration: none; color: black; }");
        pSheet.addRule(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Make a standard empty cell.
     * @param pBuilder the data builder
     */
    protected void makeValueCell(final StringBuilder pBuilder) {
        pBuilder.append("<td/>");
    }

    /**
     * Make a total empty cell.
     * @param pBuilder the data builder
     */
    protected void makeTotalCell(final StringBuilder pBuilder) {
        pBuilder.append("<th/>");
    }

    /**
     * Make a standard cell.
     * @param pBuilder the data builder
     * @param pValue the value for the cell
     */
    protected void makeValueCell(final StringBuilder pBuilder,
                                 final Object pValue) {
        makeCell(pBuilder, false, pValue);
    }

    /**
     * Make a total cell.
     * @param pBuilder the data builder
     * @param pValue the value for the cell
     */
    protected void makeTotalCell(final StringBuilder pBuilder,
                                 final Object pValue) {
        makeCell(pBuilder, true, pValue);
    }

    /**
     * Make a cell.
     * @param pBuilder the data builder
     * @param bTotal is this a total value
     * @param pValue the value for the cell
     */
    private void makeCell(final StringBuilder pBuilder,
                          final boolean bTotal,
                          final Object pValue) {
        Object myValue = pValue;
        String myClass = CLASS_DATAVALUE;
        String myType = (bTotal) ? "h" : "d";

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

        /* Build the cell */
        pBuilder.append("<t");
        pBuilder.append(myType);
        pBuilder.append(" class=\"");
        pBuilder.append(myClass);
        pBuilder.append("\">");
        if (myValue != null) {
            pBuilder.append(theFormatter.formatObject(myValue));
        }
        pBuilder.append("</t");
        pBuilder.append(myType);
        pBuilder.append(">");
    }

    /**
     * Start a table data row.
     * @param pBuilder the data builder
     * @param isOdd is the row odd or even
     */
    private void startDataRow(final StringBuilder pBuilder,
                              final boolean isOdd) {
        pBuilder.append("<tr class=\"");
        pBuilder.append(isOdd ? CLASS_ODDROW : CLASS_EVENROW);
        pBuilder.append("\"><td class=\"");
        pBuilder.append(CLASS_ROWHEADER);
        pBuilder.append("\">");
    }

    /**
     * Start a table data row.
     * @param pBuilder the data builder
     * @param isOdd is the row odd or even
     * @param pTitle the title of the row
     */
    protected void startDataRow(final StringBuilder pBuilder,
                                final boolean isOdd,
                                final String pTitle) {
        startDataRow(pBuilder, isOdd);
        pBuilder.append(pTitle);
        pBuilder.append("</td>");
    }

    /**
     * Start a link data row.
     * @param pBuilder the data builder
     * @param isOdd is the row odd or even
     * @param pLink the link name
     */
    protected void startLinkRow(final StringBuilder pBuilder,
                                final boolean isOdd,
                                final String pLink) {
        startLinkRow(pBuilder, isOdd, pLink, pLink);
    }

    /**
     * Start a link data row.
     * @param pBuilder the data builder
     * @param isOdd is the row odd or even
     * @param pLink the link name
     * @param pTitle the link title
     */
    protected void startLinkRow(final StringBuilder pBuilder,
                                final boolean isOdd,
                                final String pLink,
                                final String pTitle) {
        startDataRow(pBuilder, isOdd);
        pBuilder.append("<a href=\"#");
        pBuilder.append(LINK_REF);
        pBuilder.append(pLink);
        pBuilder.append("\">");
        pBuilder.append(pTitle);
        pBuilder.append("</a></td>");
    }

    /**
     * Start a dataLink row.
     * @param pBuilder the data builder
     * @param pLink the link name
     * @param pTitle the link title
     */
    protected void startTotalDataLinkRow(final StringBuilder pBuilder,
                                         final String pLink,
                                         final String pTitle) {
        pBuilder.append("<tr><th>");
        pBuilder.append("<a href=\"#");
        pBuilder.append(LINK_REF);
        pBuilder.append(pLink);
        pBuilder.append("\">");
        pBuilder.append(pTitle);
        pBuilder.append("</a></th>");
    }

    /**
     * Start a total data row.
     * @param pBuilder the data builder
     * @param pTitle the link title
     */
    protected void startTotalRow(final StringBuilder pBuilder,
                                 final String pTitle) {
        pBuilder.append("<tr><th>");
        pBuilder.append(pTitle);
        pBuilder.append("</th>");
    }

    /**
     * Start a total data row.
     * @param pBuilder the data builder
     * @param pTitle the link title
     */
    protected void startTotalLinkRow(final StringBuilder pBuilder,
                                     final String pTitle) {
        pBuilder.append("<tr><th>");
        pBuilder.append("<a href=\"#");
        pBuilder.append(LINK_TOP);
        pBuilder.append("\">");
        pBuilder.append(pTitle);
        pBuilder.append("</a></th>");
    }

    /**
     * Make subHeading.
     * @param pBuilder the data builder
     * @param pLink the link name
     */
    protected void makeLinkSubHeading(final StringBuilder pBuilder,
                                      final String pLink) {
        makeLinkSubHeading(pBuilder, pLink, pLink);
    }

    /**
     * Make subHeading.
     * @param pBuilder the data builder
     * @param pLink the link name
     * @param pTitle the link title
     */
    protected void makeLinkSubHeading(final StringBuilder pBuilder,
                                      final String pLink,
                                      final String pTitle) {
        pBuilder.append("<a name=\"");
        pBuilder.append(LINK_REF);
        pBuilder.append(pLink);
        pBuilder.append("\">");
        makeSubHeading(pBuilder, pTitle);
        pBuilder.append("</a>");
    }

    /**
     * Make subHeading.
     * @param pBuilder the data builder
     * @param pTitle the link title
     */
    protected void makeSubHeading(final StringBuilder pBuilder,
                                  final String pTitle) {
        pBuilder.append("<h2>");
        pBuilder.append(pTitle);
        pBuilder.append("</h2>");
    }

    /**
     * Make Heading.
     * @param pBuilder the data builder
     * @param pTitle the link title
     */
    protected void makeLinkHeading(final StringBuilder pBuilder,
                                   final String pTitle) {
        pBuilder.append("<a name=\"");
        pBuilder.append(LINK_TOP);
        pBuilder.append("\">");
        makeHeading(pBuilder, pTitle);
        pBuilder.append("</a>");
    }

    /**
     * Make subHeading.
     * @param pBuilder the data builder
     * @param pTitle the link title
     */
    protected void makeHeading(final StringBuilder pBuilder,
                               final String pTitle) {
        pBuilder.append("<h1>");
        pBuilder.append(pTitle);
        pBuilder.append("</h1>");
    }

    /**
     * Start Report.
     * @param pBuilder the data builder
     */
    protected void startReport(final StringBuilder pBuilder) {
        pBuilder.append("<html><body>");
    }

    /**
     * End Row.
     * @param pBuilder the data builder
     */
    protected void endRow(final StringBuilder pBuilder) {
        pBuilder.append("</tr>");
    }

    /**
     * End Table.
     * @param pBuilder the data builder
     */
    protected void endTable(final StringBuilder pBuilder) {
        pBuilder.append("</tbody></table>");
    }

    /**
     * End Report.
     * @param pBuilder the data builder
     */
    protected void endReport(final StringBuilder pBuilder) {
        pBuilder.append("</body></html>");
    }

    /**
     * Start Table.
     * @param pBuilder the data builder
     */
    protected void startTable(final StringBuilder pBuilder) {
        pBuilder.append("<table><thead><tr>");
    }

    /**
     * Make Table column.
     * @param pBuilder the data builder
     * @param pName the name of the column
     */
    protected void makeTableColumn(final StringBuilder pBuilder,
                                   final String pName) {
        pBuilder.append("<th>");
        pBuilder.append(pName);
        pBuilder.append("</th>");
    }

    /**
     * Make Table column spanning rows.
     * @param pBuilder the data builder
     * @param pName the name of the column
     * @param numRows the number of rows to span
     */
    protected void makeTableRowSpan(final StringBuilder pBuilder,
                                    final String pName,
                                    final int numRows) {
        pBuilder.append("<th rowspan=\"");
        pBuilder.append(numRows);
        pBuilder.append("\">");
        pBuilder.append(pName);
        pBuilder.append("</th>");
    }

    /**
     * Make Table column spanning columns.
     * @param pBuilder the data builder
     * @param pName the name of the column
     * @param numCols the number of columns to span
     */
    protected void makeTableColumnSpan(final StringBuilder pBuilder,
                                       final String pName,
                                       final int numCols) {
        pBuilder.append("<th colspan=\"");
        pBuilder.append(numCols);
        pBuilder.append("\">");
        pBuilder.append(pName);
        pBuilder.append("</th>");
    }

    /**
     * Make Table New Header row.
     * @param pBuilder the data builder
     */
    protected void makeTableNewRow(final StringBuilder pBuilder) {
        pBuilder.append("</tr><tr>");
    }

    /**
     * Start Table Body.
     * @param pBuilder the data builder
     */
    protected void startTableBody(final StringBuilder pBuilder) {
        pBuilder.append("</tr></thead><tbody>");
    }
}
