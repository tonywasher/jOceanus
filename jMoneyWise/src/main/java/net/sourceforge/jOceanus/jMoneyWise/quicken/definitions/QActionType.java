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
package net.sourceforge.jOceanus.jMoneyWise.quicken.definitions;

/**
 * Quicken Action Types.
 */
public enum QActionType {
    /**
     * Buy.
     */
    Buy,

    /**
     * BuyX.
     */
    BuyX,

    /**
     * Sell.
     */
    Sell,

    /**
     * SellX.
     */
    SellX,

    /**
     * StockSplit.
     */
    StkSplit,

    /**
     * SharesIn.
     */
    ShrsIn,

    /**
     * SharesOut.
     */
    ShrsOut,

    /**
     * Dividend.
     */
    Div,

    /**
     * DividendX.
     */
    DivX,

    /**
     * Reinvested Dividend.
     */
    ReinvDiv,

    /**
     * Return of Capital.
     */
    RtrnCap,

    /**
     * Return of CapitalX.
     */
    RtrnCapX,

    /**
     * Transfer In.
     */
    XIn,

    /**
     * Transfer Out.
     */
    XOut,

    /**
     * Miscellaneous Income.
     */
    MiscInc,

    /**
     * Miscellaneous IncomeX.
     */
    MiscIncX,

    /**
     * Miscellaneous Expense.
     */
    MiscExp,

    /**
     * Miscellaneous ExpenseX.
     */
    MiscExpX,

    /**
     * Cash/Miscellaneous Expense.
     */
    Cash,

    /**
     * Options Grant.
     */
    Grant,

    /**
     * Options Vest.
     */
    Vest,

    /**
     * Options Exercise.
     */
    Exercise,

    /**
     * Options ExercisX.
     */
    ExercisX,

    /**
     * Options Expire.
     */
    Expire,

    /**
     * Short Sell.
     */
    ShtSell,

    /**
     * Short SellX.
     */
    ShtSellX,

    /**
     * Cover ShortSell.
     */
    CvrShrt,

    /**
     * Cover ShortSellX.
     */
    CvrShrtX;

    /**
     * Parse a line to find the portfolio action type.
     * @param pLine the line to parse
     * @return the Portfolio Action type (or null if no match)
     */
    public static QActionType parseLine(final String pLine) {
        /* Loop through the values */
        for (QActionType myType : values()) {
            /* Look for match */
            if (pLine.equals(myType.toString())) {
                /* Return match if found */
                return myType;
            }
        }

        /* Return no match */
        return null;
    }
}
