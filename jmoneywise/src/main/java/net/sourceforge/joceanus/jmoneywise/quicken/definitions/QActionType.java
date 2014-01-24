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
package net.sourceforge.joceanus.jmoneywise.quicken.definitions;

/**
 * Quicken Action Types.
 */
public enum QActionType implements QLineType {
    /**
     * Buy.
     */
    BUY("Buy"),

    /**
     * BuyX.
     */
    BUYX("BuyX"),

    /**
     * Sell.
     */
    SELL("Sell"),

    /**
     * SellX.
     */
    SELLX("SellX"),

    /**
     * StockSplit.
     */
    STKSPLIT("StkSplit"),

    /**
     * SharesIn.
     */
    SHRSIN("ShrsIn"),

    /**
     * SharesOut.
     */
    SHRSOUT("ShrsOut"),

    /**
     * Dividend.
     */
    DIV("Div"),

    /**
     * DividendX.
     */
    DIVX("DivX"),

    /**
     * Reinvested Dividend.
     */
    REINVDIV("ReinvDiv"),

    /**
     * Return of Capital.
     */
    RTRNCAP("RtrnCap"),

    /**
     * Return of CapitalX.
     */
    RTRNCAPX("RtrnCapX"),

    /**
     * Transfer In.
     */
    XIN("XIn"),

    /**
     * Transfer Out.
     */
    XOUT("XOut"),

    /**
     * Miscellaneous Income.
     */
    MISCINC("MiscInc"),

    /**
     * Miscellaneous IncomeX.
     */
    MISCINCX("MiscIncX"),

    /**
     * Miscellaneous Expense.
     */
    MISCEXP("MiscExp"),

    /**
     * Miscellaneous ExpenseX.
     */
    MISCEXPX("MiscExpX"),

    /**
     * Cash/Miscellaneous Expense.
     */
    CASH("Cash"),

    /**
     * Options Grant.
     */
    GRANT("Grant"),

    /**
     * Options Vest.
     */
    VEST("Vest"),

    /**
     * Options Exercise.
     */
    EXERCISE("Exercise"),

    /**
     * Options ExercisX.
     */
    EXERCISEX("ExercisX"),

    /**
     * Options Expire.
     */
    EXPIRE("Expire"),

    /**
     * Short Sell.
     */
    SHTSELL("ShtSell"),

    /**
     * Cover ShortSell.
     */
    CVRSHRT("CvrShrt"),

    /**
     * Cover ShortSellX.
     */
    CVRSHRTX("CvrShrtX");

    /**
     * The symbol.
     */
    private final String theSymbol;

    @Override
    public String getSymbol() {
        return theSymbol;
    }

    /**
     * Constructor.
     * @param pSymbol the symbol
     */
    private QActionType(final String pSymbol) {
        /* Store symbol */
        theSymbol = pSymbol;
    }

    /**
     * Parse a line to find the portfolio action type.
     * @param pLine the line to parse
     * @return the Portfolio Action type (or null if no match)
     */
    public static QActionType parseLine(final String pLine) {
        /* Loop through the values */
        for (QActionType myType : values()) {
            /* Look for match */
            if (pLine.equals(myType.getSymbol())) {
                /* Return match if found */
                return myType;
            }
        }

        /* Return no match */
        return null;
    }
}