/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet;

/**
 * Class representing position of a cell within a sheet.
 */
public class MetisSheetCellPosition {
    /**
     * Column index.
     */
    private final int theColumn;

    /**
     * Row index.
     */
    private final int theRow;

    /**
     * Constructor.
     * @param pColumnIndex the column index
     * @param pRowIndex the row index
     */
    public MetisSheetCellPosition(final int pColumnIndex,
                                  final int pRowIndex) {
        /* Store values */
        theColumn = pColumnIndex;
        theRow = pRowIndex;
    }

    /**
     * Constructor.
     * @param pSource the source position
     */
    public MetisSheetCellPosition(final MetisSheetCellPosition pSource) {
        /* Store values */
        theColumn = pSource.getColumnIndex();
        theRow = pSource.getRowIndex();
    }

    /**
     * Obtain Column index.
     * @return the column index
     */
    public int getColumnIndex() {
        return theColumn;
    }

    /**
     * Obtain Row index.
     * @return the row index
     */
    public int getRowIndex() {
        return theRow;
    }
}
