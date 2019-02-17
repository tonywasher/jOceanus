/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.xssf;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetColumn;

/**
 * Class representing an Excel column.
 * @author Tony Washer
 */
public class MetisExcelXSSFColumn
        extends MetisSheetColumn {
    /**
     * The Excel Sheet.
     */
    private final MetisExcelXSSFSheet theExcelSheet;

    /**
     * Constructor.
     * @param pSheet the excel sheet
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    MetisExcelXSSFColumn(final MetisExcelXSSFSheet pSheet,
                         final int pIndex,
                         final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pIndex, pReadOnly);
        theExcelSheet = pSheet;
    }

    @Override
    public MetisExcelXSSFColumn getNextColumn() {
        /* Determine the required index */
        final int myIndex = getColumnIndex() + 1;

        /* Return the next column */
        return theExcelSheet.getReadOnlyColumnByIndex(myIndex);
    }

    @Override
    public MetisExcelXSSFColumn getPreviousColumn() {
        /* Determine the required index */
        final int myIndex = getColumnIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous column */
        return theExcelSheet.getReadOnlyColumnByIndex(myIndex);
    }

    @Override
    protected void setHiddenValue(final boolean isHidden) {
        theExcelSheet.setColumnHidden(getColumnIndex(), isHidden);
    }

    @Override
    public boolean isHidden() {
        return theExcelSheet.isColumnHidden(getColumnIndex());
    }

    @Override
    protected void setDefaultCellStyleValue(final MetisSheetCellStyleType pStyle) {
        theExcelSheet.setDefaultCellStyle(getColumnIndex(), pStyle);
    }
}
