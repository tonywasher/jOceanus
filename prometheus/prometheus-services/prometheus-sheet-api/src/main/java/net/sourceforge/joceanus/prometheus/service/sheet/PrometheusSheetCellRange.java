/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.service.sheet;

/**
 * Utility class to parse/format CellRanges.
 */
public class PrometheusSheetCellRange {
    /**
     * The first cell.
     */
    private final PrometheusSheetCellAddress theFirstCell;

    /**
     * The last cell.
     */
    private final PrometheusSheetCellAddress theLastCell;

    /**
     * Is Single Cell.
     */
    private final boolean isSingleCell;

    /**
     * Is Single Sheet.
     */
    private final boolean isSingleSheet;

    /**
     * Constructor.
     * @param pSheetName the sheet name
     * @param pFirstCell the first cell
     * @param pLastCell the last cell
     */
    public PrometheusSheetCellRange(final String pSheetName,
                                    final PrometheusSheetCellPosition pFirstCell,
                                    final PrometheusSheetCellPosition pLastCell) {
        /* Store parameters */
        theFirstCell = new PrometheusSheetCellAddress(pSheetName, pFirstCell);
        theLastCell = new PrometheusSheetCellAddress(pSheetName, pLastCell);
        isSingleCell = false;
        isSingleSheet = true;
    }

    /**
     * Constructor.
     * @param pSheetName the sheet name
     * @param pSingleCell the single cell
     */
    public PrometheusSheetCellRange(final String pSheetName,
                                    final PrometheusSheetCellPosition pSingleCell) {
        /* Store parameters */
        theFirstCell = new PrometheusSheetCellAddress(pSheetName, pSingleCell);
        theLastCell = theFirstCell;
        isSingleCell = true;
        isSingleSheet = true;
    }

    /**
     * Constructor.
     * @param pFirstCell the first cell
     * @param pLastCell the last cell
     */
    public PrometheusSheetCellRange(final PrometheusSheetCellAddress pFirstCell,
                                    final PrometheusSheetCellAddress pLastCell) {
        /* Store parameters */
        theFirstCell = pFirstCell;
        theLastCell = pLastCell;
        isSingleCell = false;
        isSingleSheet = theFirstCell.getSheetName().equals(theLastCell.getSheetName());
    }

    /**
     * Constructor.
     * @param pSingleCell the cell
     */
    public PrometheusSheetCellRange(final PrometheusSheetCellAddress pSingleCell) {
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
    public PrometheusSheetCellRange(final String pAddress) {
        /* Locate the split */
        final int iSplit = pAddress.indexOf(PrometheusSheetCellAddress.CHAR_COLON);
        isSingleCell = iSplit == -1;
        if (isSingleCell) {
            theFirstCell = new PrometheusSheetCellAddress(pAddress);
            theLastCell = theFirstCell;
            isSingleSheet = true;
        } else {
            theFirstCell = new PrometheusSheetCellAddress(pAddress.substring(0, iSplit));
            if (pAddress.charAt(iSplit + 1) == PrometheusSheetCellAddress.CHAR_PERIOD) {
                theLastCell = new PrometheusSheetCellAddress(theFirstCell.getSheetName(), pAddress.substring(iSplit + 2));
                isSingleSheet = true;
            } else {
                theLastCell = new PrometheusSheetCellAddress(pAddress.substring(iSplit + 1));
                isSingleSheet = false;
            }
        }
    }

    /**
     * Obtain the first cell Address.
     * @return the address
     */
    public PrometheusSheetCellAddress getFirstCell() {
        return theFirstCell;
    }

    /**
     * Obtain the BottomRight Address.
     * @return the address
     */
    public PrometheusSheetCellAddress getLastCell() {
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

    @Override
    public String toString() {
        /* Build the name */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theFirstCell.toString());

        /* If we are not single cell */
        if (!isSingleCell) {
            /* Format other corner */
            myBuilder.append(PrometheusSheetCellAddress.CHAR_COLON);
            if (!isSingleSheet) {
                myBuilder.append(theLastCell.toString());
            } else {
                myBuilder.append(PrometheusSheetCellAddress.CHAR_PERIOD);
                theLastCell.formatPosition(myBuilder);
            }
        }

        /* Format the string */
        return myBuilder.toString();
    }
}
