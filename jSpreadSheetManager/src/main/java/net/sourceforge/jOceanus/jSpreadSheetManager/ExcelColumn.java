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
 * Class representing and Exvel column
 * @author Tony Washer
 */
public class ExcelColumn
        extends DataColumn {
    /**
     * The Excel Sheet.
     */
    private final ExcelSheet theExcelSheet;

    /**
     * Constructor.
     * @param pMap the column map
     * @param pPrevious the previous column.
     * @param pColumn the Oasis column
     * @param pIndex the index
     * @param pInstance the repeat instance
     */
    protected ExcelColumn(final ExcelSheet pSheet,
                          final int pIndex) {
        /* Store parameters */
        super(pSheet, pIndex);
        theExcelSheet = pSheet;
    }

    @Override
    public ExcelColumn getNextColumn() {
        /* Determine the required index */
        int myIndex = getIndex() + 1;

        /* Return the next row */
        return theExcelSheet.createColumnByIndex(myIndex);
    }

    @Override
    public ExcelColumn getPreviousColumn() {
        /* Determine the required index */
        int myIndex = getIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous row */
        return theExcelSheet.getColumnByIndex(myIndex);
    }

    @Override
    public void setHidden(boolean isHidden) {
        theExcelSheet.setColumnHidden(getIndex(), isHidden);
    }

    @Override
    public boolean isHidden() {
        return theExcelSheet.isColumnHidden(getIndex());
    }

    @Override
    public void setDefaultCellStyle(CellStyleType pStyle) {
        theExcelSheet.setDefaultCellStyle(getIndex(), pStyle);
    }
}
