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
package net.sourceforge.jOceanus.jMoneyWise.quicken.file;

/**
 * Quicken Category Line Types.
 */
public enum QCategoryLineType implements QLineType {
    /**
     * Name.
     */
    Name("N"),

    /**
     * Description.
     */
    Description("D"),

    /**
     * Income flag.
     */
    Income("I"),

    /**
     * Expense flag.
     */
    Expense("E"),

    /**
     * Tax flag.
     */
    Tax("T");

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
    private QCategoryLineType(final String pSymbol) {
        /* Store symbol */
        theSymbol = pSymbol;
    }

    /**
     * Parse a line to find the category line type.
     * @param pLine the line to parse
     * @return the Category Line type (or null if no match)
     */
    public static QCategoryLineType parseLine(final String pLine) {
        /* Access first character of line */
        String myChar = pLine.substring(0, 1);

        /* Loop through the values */
        for (QCategoryLineType myType : values()) {
            /* Look for match */
            if (myChar.equals(myType)) {
                /* Return match if found */
                return myType;
            }
        }

        /* Return no match */
        return null;
    }
}
