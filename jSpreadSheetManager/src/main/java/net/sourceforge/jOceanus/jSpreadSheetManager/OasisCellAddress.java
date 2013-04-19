/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2013 Tony Washer
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
package net.sourceforge.jOceanus.jSpreadSheetManager;

/**
 * Utility class to parse/format ODS CellAddresses.
 * @author Tony Washer
 */
public class OasisCellAddress {
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
    private static final char CHAR_COLON = ':';

    /**
     * Period character.
     */
    private static final char CHAR_PERIOD = '.';

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
    private static final String STR_APOS = ""
                                           + CHAR_APOS;

    /**
     * Period search string.
     */
    private static final String STR_PERIOD = ""
                                             + CHAR_PERIOD;

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
    private final OasisSheetName theSheetName;

    /**
     * Position.
     */
    private final CellPosition thePosition;

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
    protected CellPosition getPosition() {
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

    /**
     * Constructor.
     * @param pSheetName the sheet name
     * @param pPosition the cell location
     */
    protected OasisCellAddress(final String pSheetName,
                               final CellPosition pPosition) {
        /* Store parameters */
        theSheetName = new OasisSheetName(pSheetName, false);
        thePosition = pPosition;
    }

    /**
     * Constructor.
     * @param pAddress the cell address
     */
    protected OasisCellAddress(final String pAddress) {
        /* Store parameters */
        int iPos = pAddress.indexOf(CHAR_PERIOD);
        thePosition = parsePosition(pAddress.substring(iPos + 1));
        String myName = pAddress.substring((pAddress.charAt(0) == CHAR_DOLLAR)
                ? 1
                : 0, iPos);
        theSheetName = new OasisSheetName(myName, true);
    }

    /**
     * Constructor.
     * @param pSheetName the sheet name
     * @param pAddress the cell address
     */
    protected OasisCellAddress(final String pSheetName,
                               final String pAddress) {
        /* Store parameters */
        thePosition = parsePosition(pAddress);
        theSheetName = new OasisSheetName(pSheetName, false);
    }

    @Override
    public String toString() {
        /* Build the name */
        StringBuilder myBuilder = new StringBuilder();
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
    private void formatPosition(final StringBuilder pBuilder) {
        /* Calculate indexes */
        int col = thePosition.getColumnIndex();
        int topCol = col
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
    private CellPosition parsePosition(final String pAddress) {
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
        return new CellPosition(iCol - 1, iRow - 1);
    }

    /**
     * Utility class to handle parsing and escaping of sheet names in ranges.
     */
    private static final class OasisSheetName {
        /**
         * The sheet name.
         */
        private final String theSheetName;

        /**
         * The escaped sheet name.
         */
        private final String theEscapedName;

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
         * Constructor.
         * @param pName the name
         * @param isEscaped is the name escaped or not
         */
        private OasisSheetName(final String pName,
                               final boolean isEscaped) {
            /* Access the length of the name */
            int myLen = pName.length();

            /* If the name is escaped */
            if (isEscaped) {
                /* Store the escaped name and prepare to edit the name */
                theEscapedName = pName;

                /* If the name is escaped */
                if ((pName.charAt(0) == CHAR_APOS)
                    && (pName.charAt(myLen - 1) == CHAR_APOS)) {
                    /* Create editor for name */
                    StringBuilder myBuilder = new StringBuilder(pName);

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
                /* Store the escaped name and prepare to edit the name */
                theSheetName = pName;
                StringBuilder myBuilder = new StringBuilder(pName);

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
                theEscapedName = myBuilder.toString();

                /* else standard name */
            } else {
                /* store values */
                theSheetName = pName;
                theEscapedName = pName;
            }
        }

        /**
         * Does the name need escaping?
         * @param pName the name to test
         * @return true/false
         */
        private boolean needsEscaping(final String pName) {
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

            /* If the name contains characters */
            int iLen = pName.length();
            for (int i = 0; i < iLen; i++) {
                if (!Character.isDigit(pName.charAt(i))) {
                    return false;
                }
            }

            /* Need to escape */
            return true;
        }
    }

    /**
     * Utility class to parse/format ODF CellRanges.
     */
    public static class OasisCellRange {
        /**
         * The first cell.
         */
        private final OasisCellAddress theFirstCell;

        /**
         * The last cell.
         */
        private final OasisCellAddress theLastCell;

        /**
         * Is Single Cell.
         */
        private final boolean isSingleCell;

        /**
         * Is Single Sheet.
         */
        private final boolean isSingleSheet;

        /**
         * Obtain the first cell Address.
         * @return the address
         */
        protected OasisCellAddress getFirstCell() {
            return theFirstCell;
        }

        /**
         * Obtain the BottomRight Address.
         * @return the address
         */
        protected OasisCellAddress getLastCell() {
            return theLastCell;
        }

        /**
         * Is this range a single cell address?
         * @return true/false
         */
        protected boolean isSingleCell() {
            return isSingleCell;
        }

        /**
         * Is this range a single sheet range?
         * @return true/false
         */
        protected boolean isSingleSheet() {
            return isSingleSheet;
        }

        /**
         * Constructor.
         * @param pSheetName the sheet name
         * @param pFirstCell the first cell
         * @param pLastCell the last cell
         */
        protected OasisCellRange(final String pSheetName,
                                 final CellPosition pFirstCell,
                                 final CellPosition pLastCell) {
            /* Store parameters */
            theFirstCell = new OasisCellAddress(pSheetName, pFirstCell);
            theLastCell = new OasisCellAddress(pSheetName, pLastCell);
            isSingleCell = false;
            isSingleSheet = true;
        }

        /**
         * Constructor.
         * @param pSheetName the sheet name
         * @param pSingleCell the single cell
         */
        protected OasisCellRange(final String pSheetName,
                                 final CellPosition pSingleCell) {
            /* Store parameters */
            theFirstCell = new OasisCellAddress(pSheetName, pSingleCell);
            theLastCell = theFirstCell;
            isSingleCell = true;
            isSingleSheet = true;
        }

        /**
         * Constructor.
         * @param pFirstCell the first cell
         * @param pLastCell the last cell
         */
        protected OasisCellRange(final OasisCellAddress pFirstCell,
                                 final OasisCellAddress pLastCell) {
            /* Store parameters */
            theFirstCell = pFirstCell;
            theLastCell = pLastCell;
            isSingleCell = false;
            isSingleSheet = (theFirstCell.getSheetName().equals(theLastCell.getSheetName()));
        }

        /**
         * Constructor.
         * @param pSingleCell the cell
         */
        protected OasisCellRange(final OasisCellAddress pSingleCell) {
            /* Store parameters */
            theFirstCell = pSingleCell;
            theLastCell = pSingleCell;
            isSingleCell = true;
            isSingleSheet = true;
        }

        /**
         * Constructor.
         * @param pAddress the cell address
         */
        protected OasisCellRange(final String pAddress) {
            /* Locate the split */
            int iSplit = pAddress.indexOf(CHAR_COLON);
            isSingleCell = (iSplit == -1);
            if (isSingleCell) {
                theFirstCell = new OasisCellAddress(pAddress);
                theLastCell = theFirstCell;
                isSingleSheet = true;
            } else {
                theFirstCell = new OasisCellAddress(pAddress.substring(0, iSplit));
                if (pAddress.charAt(iSplit + 1) == CHAR_PERIOD) {
                    theLastCell = new OasisCellAddress(theFirstCell.getSheetName(), pAddress.substring(iSplit + 2));
                    isSingleSheet = true;
                } else {
                    theLastCell = new OasisCellAddress(pAddress.substring(iSplit + 1));
                    isSingleSheet = false;
                }
            }
        }

        @Override
        public String toString() {
            /* Build the name */
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theFirstCell.toString());

            /* If we are not single cell */
            if (!isSingleCell) {
                /* Format other corner */
                myBuilder.append(CHAR_COLON);
                if (!isSingleSheet) {
                    myBuilder.append(theLastCell.toString());
                } else {
                    myBuilder.append(CHAR_PERIOD);
                    theLastCell.formatPosition(myBuilder);
                }
            }

            /* Format the string */
            return myBuilder.toString();
        }
    }
}
