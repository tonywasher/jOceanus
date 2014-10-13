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
 * Quicken Event Line Types.
 */
public enum QEventLineType implements QLineType {
    /**
     * Date.
     */
    DATE("D"),

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
     * Reference.
     */
    REFERENCE("N"),

    /**
     * Address.
     */
    ADDRESS("A"),

    /**
     * Payee.
     */
    PAYEE("P"),

    /**
     * Category.
     */
    CATEGORY("L"),

    /**
     * SplitCategory.
     */
    SPLITCATEGORY("S"),

    /**
     * SplitComment.
     */
    SPLITCOMMENT("E"),

    /**
     * SplitAmount.
     */
    SPLITAMOUNT("$"),

    /**
     * SplitPercent.
     */
    SPLITPERCENT("%");

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
    private QEventLineType(final String pSymbol) {
        /* Store symbol */
        theSymbol = pSymbol;
    }

    /**
     * Parse a line to find the event line type.
     * @param pLine the line to parse
     * @return the Event Line type (or null if no match)
     */
    public static QEventLineType parseLine(final String pLine) {
        /* Loop through the values */
        for (QEventLineType myType : values()) {
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
