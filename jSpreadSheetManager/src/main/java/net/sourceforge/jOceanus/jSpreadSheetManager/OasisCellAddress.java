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
     * Sheet name
     */
    private final String theSheetName;

    /**
     * Position
     */
    private final CellPosition thePosition;

    /**
     * Do we need to wrap name with apostrophe?
     */
    // private boolean useApostrophes;

    /**
     * Obtain the SheetName.
     * @return the name
     */
    public String getSheetName() {
        return theSheetName;
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
        theSheetName = pSheetName;
        thePosition = pPosition;
    }

    /**
     * Constructor.
     * @param pAddress the cell address
     */
    protected OasisCellAddress(final String pAddress) {
        /* Store parameters */
        int iPos = pAddress.indexOf('.');
        thePosition = parsePosition(pAddress.substring(iPos + 1));
        String myName = pAddress.substring((pAddress.charAt(0) == '$') ? 1 : 0, iPos);
        iPos = myName.length();
        if ((myName.charAt(0) == '\'')
            && (myName.charAt(iPos - 1) == '\'')) {
            myName = myName.substring(1, iPos - 1);
            // useApostrophes = true;
        }
        theSheetName = myName;
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
        theSheetName = pSheetName;
    }

    @Override
    public String toString() {
        /* Build the name */
        StringBuilder myBuilder = new StringBuilder(32);
        myBuilder.append('$');
        myBuilder.append(theSheetName);
        myBuilder.append('.');
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
        int topCol = col / 26;
        col = col % 26;

        /* Build the name */
        pBuilder.append('$');
        if (topCol > 0) {
            pBuilder.append('A' + topCol);
        }
        pBuilder.append((char) ('A' + col));
        pBuilder.append('$');
        pBuilder.append(thePosition.getRowIndex() + 1);
    }

    /**
     * Parse position.
     * @param the position to parse
     * @return the parsed position
     */
    private CellPosition parsePosition(final String pAddress) {
        /* Initialise the results */
        int iCol = 0;
        int iRow = 0;

        /* Loop through the characters */
        for (char myChar : pAddress.toCharArray()) {
            /* Ignore $ */
            if (myChar == '$') {
                continue;
            }

            /* If we have reached decimals */
            if (Character.isDigit(myChar)) {
                /* Add into row */
                iRow *= 10;
                iRow += myChar - '0';

                /* else character */
            } else {
                /* Add into column */
                iCol *= 26;
                iCol += (myChar - 'A' + 1);
            }
        }

        /* return the calculated position */
        return new CellPosition(iCol - 1, iRow - 1);
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
         * @param pBottomRight the last cell
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
         * @param pTopLeft the top left cell
         * @param pBottomRight the bottom right cell
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
            int iSplit = pAddress.indexOf(':');
            isSingleCell = (iSplit == -1);
            if (isSingleCell) {
                theFirstCell = new OasisCellAddress(pAddress);
                theLastCell = theFirstCell;
                isSingleSheet = true;
            } else {
                theFirstCell = new OasisCellAddress(pAddress.substring(0, iSplit));
                if (pAddress.charAt(iSplit + 1) == '.') {
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
            StringBuilder myBuilder = new StringBuilder(32);
            myBuilder.append(theFirstCell.toString());

            /* If we are not single cell */
            if (!isSingleCell) {
                /* Format other corner */
                myBuilder.append(':');
                if (!isSingleSheet) {
                    myBuilder.append(theLastCell.toString());
                } else {
                    myBuilder.append('.');
                    theLastCell.formatPosition(myBuilder);
                }
            }

            /* Format the string */
            return myBuilder.toString();
        }
    }
}
