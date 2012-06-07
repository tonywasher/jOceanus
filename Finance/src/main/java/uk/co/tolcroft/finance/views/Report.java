/*******************************************************************************
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

public class Report {
    /* Report Types */
    public static enum ReportType {
        ASSET, INCOME, BREAKDOWN, TAX, TRANSACTION, INSTANT, MARKET;
    }

    protected static StringBuilder makeMoneyItem(Money pAmount) {
        return makeMoneyCell(pAmount, false, 1);
    }

    protected static StringBuilder makeMoneyTotal(Money pAmount) {
        return makeMoneyCell(pAmount, true, 1);
    }

    protected static StringBuilder makeMoneyProfit(Money pAmount) {
        return makeMoneyCell(pAmount, true, 2);
    }

    protected static StringBuilder makeMoneyCell(Money pAmount,
                                                 boolean isHighlighted,
                                                 int numCols) {
        StringBuilder myOutput = new StringBuilder(100);
        String myColour;
        String myHighlight = (isHighlighted) ? "h" : "d";

        /* Determine the colour of the cell */
        myColour = ((pAmount != null) && (pAmount.isPositive())) ? "blue" : "red";

        /* Build the cell */
        myOutput.append("<t");
        ;
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
        if ((pAmount != null) && (pAmount.isNonZero()))
            myOutput.append(pAmount.format(true));
        myOutput.append("</t");
        myOutput.append(myHighlight);
        myOutput.append(">");

        /* Return the detail */
        return myOutput;
    }

    protected static StringBuilder makeUnitsItem(Units pUnits) {
        StringBuilder myOutput = new StringBuilder(100);

        /* Build the cell */
        myOutput.append("<td align=\"right\" color=\"blue\">");
        if ((pUnits != null) && (pUnits.isNonZero()))
            myOutput.append(pUnits.format(true));
        myOutput.append("</td>");

        /* Return the detail */
        return myOutput;
    }

    protected static StringBuilder makePriceItem(Price pPrice) {
        StringBuilder myOutput = new StringBuilder(100);

        /* Build the cell */
        myOutput.append("<td align=\"right\" color=\"blue\">");
        if (pPrice.isNonZero())
            myOutput.append(pPrice.format(true));
        myOutput.append("</td>");

        /* Return the detail */
        return myOutput;
    }

    protected static StringBuilder makeRateItem(Rate pRate) {
        StringBuilder myOutput = new StringBuilder(100);

        /* Build the cell */
        myOutput.append("<td align=\"right\" color=\"blue\">");
        if ((pRate != null) && (pRate.isNonZero()))
            myOutput.append(pRate.format(true));
        myOutput.append("</td>");

        /* Return the detail */
        return myOutput;
    }

    protected static StringBuilder makeDateItem(DateDay pDate) {
        StringBuilder myOutput = new StringBuilder(100);

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
