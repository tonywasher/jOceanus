/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.quicken.definitions;

/**
 * Quicken Account Line Types.
 */
public enum MoneyWiseQAccountLineType implements MoneyWiseQLineType {
    /**
     * Name.
     */
    NAME("N"),

    /**
     * Account Type.
     */
    TYPE("T"),

    /**
     * Description.
     */
    DESCRIPTION("D"),

    /**
     * Credit Limit.
     */
    CREDITLIMIT("L");

    /**
     * The symbol.
     */
    private final String theSymbol;

    /**
     * Constructor.
     * @param pSymbol the symbol
     */
    MoneyWiseQAccountLineType(final String pSymbol) {
        /* Store symbol */
        theSymbol = pSymbol;
    }

    @Override
    public String getSymbol() {
        return theSymbol;
    }

    /**
     * Parse a line to find the account line type.
     * @param pLine the line to parse
     * @return the Account Line type (or null if no match)
     */
    public static MoneyWiseQAccountLineType parseLine(final String pLine) {
        /* Loop through the values */
        for (MoneyWiseQAccountLineType myType : values()) {
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