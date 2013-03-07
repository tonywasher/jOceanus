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
 * Class representing and Excel column.
 * @author Tony Washer
 */
public class ExcelColumn
        extends DataColumn {
    /**
     * The Excel Sheet.
     */
    private final ExcelSheet theExcelSheet;

    /**
     * Is the column readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pSheet the excel sheet
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    protected ExcelColumn(final ExcelSheet pSheet,
                          final int pIndex,
                          final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pIndex);
        theExcelSheet = pSheet;
        isReadOnly = pReadOnly;
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
    public void setHidden(final boolean isHidden) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            theExcelSheet.setColumnHidden(getIndex(), isHidden);
        }
    }

    @Override
    public boolean isHidden() {
        return theExcelSheet.isColumnHidden(getIndex());
    }

    @Override
    public void setDefaultCellStyle(final CellStyleType pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            theExcelSheet.setDefaultCellStyle(getIndex(), pStyle);
        }
    }
}
