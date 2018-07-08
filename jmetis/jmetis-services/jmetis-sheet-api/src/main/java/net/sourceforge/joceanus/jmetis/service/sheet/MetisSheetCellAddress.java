/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet;

/**
 * Utility class to parse/format CellAddresses.
 */
public class MetisSheetCellAddress {
    /**
     * Apostrophe character.
     */
    private static final char CHAR_APOS = '\'';

    /**
     * Dollar character.
     */
    private static final char CHAR_DOLLAR = '$';

    /**
     * Colon character.
     */
    protected static final char CHAR_COLON = ':';

    /**
     * Period character.
     */
    protected static final char CHAR_PERIOD = '.';

    /**
     * Column base character.
     */
    private static final char CHAR_CBASE = 'A';

    /**
     * Row base character.
     */
    private static final char CHAR_RBASE = '0';

    /**
     * Apostrophe search string.
     */
    private static final String STR_APOS = Character.toString(CHAR_APOS);

    /**
     * Period search string.
     */
    private static final String STR_PERIOD = Character.toString(CHAR_PERIOD);

    /**
     * Row radix.
     */
    private static final int RADIX_ROW = 10;

    /**
     * Column radix.
     */
    private static final int RADIX_COLUMN = 26;

    /**
     * Sheet name.
     */
    private final MetisSheetName theSheetName;

    /**
     * Position.
     */
    private final MetisSheetCellPosition thePosition;

    /**
     * Constructor.
     * @param pSheetName the sheet name
     * @param pPosition the cell location
     */
    public MetisSheetCellAddress(final String pSheetName,
                                 final MetisSheetCellPosition pPosition) {
        /* Store parameters */
        theSheetName = new MetisSheetName(pSheetName, false);
        thePosition = pPosition;
    }

    /**
     * Constructor.
     * @param pAddress the cell address
     */
    public MetisSheetCellAddress(final String pAddress) {
        /* Store parameters */
        final int iPos = pAddress.indexOf(CHAR_PERIOD);
        thePosition = parsePosition(pAddress.substring(iPos + 1));
        final String myName = pAddress.substring(pAddress.charAt(0) == CHAR_DOLLAR
                                                                                   ? 1
                                                                                   : 0, iPos);
        theSheetName = new MetisSheetName(myName, true);
    }

    /**
     * Constructor.
     * @param pSheetName the sheet name
     * @param pAddress the cell address
     */
    public MetisSheetCellAddress(final String pSheetName,
                                 final String pAddress) {
        /* Store parameters */
        thePosition = parsePosition(pAddress);
        theSheetName = new MetisSheetName(pSheetName, false);
    }

    /**
     * Obtain the SheetName.
     * @return the name
     */
    public String getSheetName() {
        return theSheetName.getName();
    }

    /**
     * Obtain the Location.
     * @return the location
     */
    public MetisSheetCellPosition getPosition() {
        return thePosition;
    }

    /**
     * Obtain the Column.
     * @return the column
     */
    public int getColumn() {
        return thePosition.getColumnIndex();
    }

    /**
     * Obtain the Row.
     * @return the row
     */
    public int getRow() {
        return thePosition.getRowIndex();
    }

    @Override
    public String toString() {
        /* Build the name */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(CHAR_DOLLAR);
        myBuilder.append(theSheetName.getEscapedName());
        myBuilder.append(CHAR_PERIOD);
        formatPosition(myBuilder);

        /* Format the string */
        return myBuilder.toString();
    }

    /**
     * Obtain the column name.
     * @param pBuilder the string builder
     */
    protected void formatPosition(final StringBuilder pBuilder) {
        /* Calculate indexes */
        int col = thePosition.getColumnIndex();
        final int topCol = col
                           / RADIX_COLUMN;
        col = col
              % RADIX_COLUMN;

        /* Build the name */
        pBuilder.append(CHAR_DOLLAR);
        if (topCol > 0) {
            pBuilder.append((char) (CHAR_CBASE
                                    + topCol - 1));
        }
        pBuilder.append((char) (CHAR_CBASE + col));
        pBuilder.append(CHAR_DOLLAR);
        pBuilder.append(thePosition.getRowIndex() + 1);
    }

    /**
     * Parse position.
     * @param pAddress the position to parse
     * @return the parsed position
     */
    private static MetisSheetCellPosition parsePosition(final String pAddress) {
        /* Initialise the results */
        int iCol = 0;
        int iRow = 0;

        /* Loop through the characters */
        for (char myChar : pAddress.toCharArray()) {
            /* Ignore $ */
            if (myChar == CHAR_DOLLAR) {
                continue;
            }

            /* If we have reached decimals */
            if (Character.isDigit(myChar)) {
                /* Add into row */
                iRow *= RADIX_ROW;
                iRow += myChar
                        - CHAR_RBASE;

                /* else character */
            } else {
                /* Add into column */
                iCol *= RADIX_COLUMN;
                iCol += (myChar
                         - CHAR_CBASE + 1);
            }
        }

        /* return the calculated position */
        return new MetisSheetCellPosition(iCol - 1, iRow - 1);
    }

    /**
     * Escape apostrophes in a string.
     * @param pSource the string to escape
     * @return the escaped string
     */
    public static String escapeApostrophes(final String pSource) {
        /* Create a builder around the name */
        final StringBuilder myBuilder = new StringBuilder(pSource);

        /* Escape all instances of apostrophe */
        int iLoc = myBuilder.indexOf(STR_APOS);
        while (iLoc != -1) {
            /* Insert additional apostrophe and search for further characters */
            myBuilder.insert(iLoc, CHAR_APOS);
            iLoc = myBuilder.indexOf(STR_APOS, iLoc + 2);
        }

        /* Add wrapping apostrophes and format */
        myBuilder.insert(0, CHAR_APOS);
        myBuilder.append(CHAR_APOS);
        return myBuilder.toString();
    }

    /**
     * Utility class to handle parsing and escaping of sheet names in ranges.
     */
    private static final class MetisSheetName {
        /**
         * The sheet name.
         */
        private final String theSheetName;

        /**
         * The escaped sheet name.
         */
        private final String theEscapedName;

        /**
         * Constructor.
         * @param pName the name
         * @param isEscaped is the name escaped or not
         */
        private MetisSheetName(final String pName,
                               final boolean isEscaped) {
            /* Access the length of the name */
            final int myLen = pName.length();

            /* If the name is escaped */
            if (isEscaped) {
                /* Store the escaped name and prepare to edit the name */
                theEscapedName = pName;

                /* If the name is escaped */
                if (pName.charAt(0) == CHAR_APOS
                    && pName.charAt(myLen - 1) == CHAR_APOS) {
                    /* Create editor for name */
                    final StringBuilder myBuilder = new StringBuilder(pName);

                    /* Delete the wrapping apostrophes */
                    myBuilder.deleteCharAt(myLen - 1);
                    myBuilder.deleteCharAt(0);

                    /* Unescape all instances of double apostrophe */
                    int iLoc = myBuilder.indexOf(STR_APOS
                                                 + STR_APOS);
                    while (iLoc != -1) {
                        /* Delete escaped apostrophe and search for further characters */
                        myBuilder.deleteCharAt(iLoc);
                        iLoc = myBuilder.indexOf(STR_APOS
                                                 + STR_APOS, iLoc + 1);
                    }

                    theSheetName = myBuilder.toString();
                } else {
                    theSheetName = pName;
                }

                /* else if the name needs to be escaped */
            } else if (needsEscaping(pName)) {
                /* Store the original name */
                theSheetName = pName;

                /* Escape the name */
                theEscapedName = escapeApostrophes(pName);

                /* else standard name */
            } else {
                /* store values */
                theSheetName = pName;
                theEscapedName = pName;
            }
        }

        /**
         * Obtain the sheet name.
         * @return the name
         */
        private String getName() {
            return theSheetName;
        }

        /**
         * Obtain the escaped sheet name.
         * @return the name
         */
        private String getEscapedName() {
            return theEscapedName;
        }

        /**
         * Does the name need escaping?
         * @param pName the name to test
         * @return true/false
         */
        private static boolean needsEscaping(final String pName) {
            /* Need to escape if there is a blank in the name */
            if (pName.contains(" ")) {
                return true;
            }

            /* Need to escape if there is an apostrophe in the name */
            if (pName.contains(STR_APOS)) {
                return true;
            }

            /* Need to escape if there is a period in the name */
            if (pName.contains(STR_PERIOD)) {
                return true;
            }

            /* OK if the name contains characters */
            final int iLen = pName.length();
            for (int i = 0; i < iLen; i++) {
                if (!Character.isDigit(pName.charAt(i))) {
                    return false;
                }
            }

            /* Need to escape */
            return true;
        }
    }
}
