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
package uk.co.tolcroft.finance.views;

import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;

/**
 * Report Classes.
 * @author Tony Washer
 */
public final class Report {
    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Disable instantiation.
     */
    private Report() {
    }

    /**
     * Add a standard Money Cell to report.
     * @param pAmount the money amount
     * @return the Cell
     */
    protected static StringBuilder makeMoneyItem(final Money pAmount) {
        return makeMoneyCell(pAmount, false, 1);
    }

    /**
     * Add a Money Total to report.
     * @param pAmount the money amount
     * @return the Cell
     */
    protected static StringBuilder makeMoneyTotal(final Money pAmount) {
        return makeMoneyCell(pAmount, true, 1);
    }

    /**
     * Add a Money Profit to report.
     * @param pAmount the money amount
     * @return the Cell
     */
    protected static StringBuilder makeMoneyProfit(final Money pAmount) {
        return makeMoneyCell(pAmount, true, 2);
    }

    /**
     * Add a Money Cell to report.
     * @param pAmount the money amount
     * @param isHighlighted is the cell highlighted
     * @param numCols number of columns to span
     * @return the Cell
     */
    protected static StringBuilder makeMoneyCell(final Money pAmount,
                                                 final boolean isHighlighted,
                                                 final int numCols) {
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);
        String myColour;
        String myHighlight = (isHighlighted) ? "h" : "d";

        /* Determine the colour of the cell */
        myColour = ((pAmount != null) && (pAmount.isPositive())) ? "blue" : "red";

        /* Build the cell */
        myOutput.append("<t");
        myOutput.append(myHighlight);
        myOutput.append(" align=\"right\" color=\"");
        myOutput.append(myColour);
        myOutput.append("\"");
        if (numCols > 1) {
            myOutput.append(" colspan=\"");
            myOutput.append(numCols);
            myOutput.append("\"");
        }
        myOutput.append(">");
        if ((pAmount != null) && (pAmount.isNonZero())) {
            myOutput.append(pAmount.format(true));
        }
        myOutput.append("</t");
        myOutput.append(myHighlight);
        myOutput.append(">");

        /* Return the detail */
        return myOutput;
    }

    /**
     * Add a Units Cell to report.
     * @param pUnits the units
     * @return the Cell
     */
    protected static StringBuilder makeUnitsItem(final Units pUnits) {
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Build the cell */
        myOutput.append("<td align=\"right\" color=\"blue\">");
        if ((pUnits != null) && (pUnits.isNonZero())) {
            myOutput.append(pUnits.format(true));
        }
        myOutput.append("</td>");

        /* Return the detail */
        return myOutput;
    }

    /**
     * Add a Price Cell to report.
     * @param pPrice the price
     * @return the Cell
     */
    protected static StringBuilder makePriceItem(final Price pPrice) {
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Build the cell */
        myOutput.append("<td align=\"right\" color=\"blue\">");
        if (pPrice.isNonZero()) {
            myOutput.append(pPrice.format(true));
        }
        myOutput.append("</td>");

        /* Return the detail */
        return myOutput;
    }

    /**
     * Add a Rate Cell to report.
     * @param pRate the rate
     * @return the Cell
     */
    protected static StringBuilder makeRateItem(final Rate pRate) {
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Build the cell */
        myOutput.append("<td align=\"right\" color=\"blue\">");
        if ((pRate != null) && (pRate.isNonZero())) {
            myOutput.append(pRate.format(true));
        }
        myOutput.append("</td>");

        /* Return the detail */
        return myOutput;
    }

    /**
     * Add a Date Cell to report.
     * @param pDate the date
     * @return the Cell
     */
    protected static StringBuilder makeDateItem(final DateDay pDate) {
        StringBuilder myOutput = new StringBuilder(BUFFER_LEN);

        /* Build the cell */
        myOutput.append("<td align=\"right\" color=\"blue\">");
        if (pDate != null) {
            myOutput.append(JDataObject.formatField(pDate));
        }
        myOutput.append("</td>");

        /* Return the detail */
        return myOutput;
    }
}
