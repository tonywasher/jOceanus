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
 * Quicken Portfolio Event Line Types.
 */
public enum QPortfolioLineType implements QLineType {
    /**
     * Date.
     */
    DATE("D"),

    /**
     * Action.
     */
    ACTION("N"),

    /**
     * Security.
     */
    SECURITY("Y"),

    /**
     * Price.
     */
    PRICE("I"),

    /**
     * Quantity.
     */
    QUANTITY("Q"),

    /**
     * Amount.
     */
    AMOUNT("T"),

    /**
     * Cleared Status.
     */
    CLEARED("C"),

    /**
     * Comment.
     */
    COMMENT("M"),

    /**
     * Payee.
     */
    PAYEE("P"),

    /**
     * Commission.
     */
    COMMISSION("O"),

    /**
     * TransferAccount.
     */
    XFERACCOUNT("L"),

    /**
     * TransferAmount.
     */
    XFERAMOUNT("$");

    /**
     * The symbol.
     */
    private final String theSymbol;

    /**
     * Constructor.
     * @param pSymbol the symbol
     */
    private QPortfolioLineType(final String pSymbol) {
        /* Store symbol */
        theSymbol = pSymbol;
    }

    @Override
    public String getSymbol() {
        return theSymbol;
    }

    /**
     * Parse a line to find the portfolio line type.
     * @param pLine the line to parse
     * @return the Portfolio Line type (or null if no match)
     */
    public static QPortfolioLineType parseLine(final String pLine) {
        /* Loop through the values */
        for (QPortfolioLineType myType : values()) {
            /* Look for match */
            if (pLine.startsWith(myType.getSymbol())) {
                /* Return match if found */
                return myType;
            }
        }

        /* Return no match */
        return null;
    }
}
