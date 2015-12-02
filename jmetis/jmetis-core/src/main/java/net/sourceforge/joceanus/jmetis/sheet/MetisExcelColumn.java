/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.sheet;

/**
 * Class representing and Excel column.
 * @author Tony Washer
 */
public class MetisExcelColumn
        extends MetisDataColumn {
    /**
     * The Excel Sheet.
     */
    private final MetisExcelSheet theExcelSheet;

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
    protected MetisExcelColumn(final MetisExcelSheet pSheet,
                               final int pIndex,
                               final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pIndex);
        theExcelSheet = pSheet;
        isReadOnly = pReadOnly;
    }

    @Override
    public MetisExcelColumn getNextColumn() {
        /* Determine the required index */
        int myIndex = getColumnIndex() + 1;

        /* Return the next row */
        return theExcelSheet.getReadOnlyColumnByIndex(myIndex);
    }

    @Override
    public MetisExcelColumn getPreviousColumn() {
        /* Determine the required index */
        int myIndex = getColumnIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous row */
        return theExcelSheet.getReadOnlyColumnByIndex(myIndex);
    }

    @Override
    public void setHidden(final boolean isHidden) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            theExcelSheet.setColumnHidden(getColumnIndex(), isHidden);
        }
    }

    @Override
    public boolean isHidden() {
        return theExcelSheet.isColumnHidden(getColumnIndex());
    }

    @Override
    public void setDefaultCellStyle(final MetisCellStyleType pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            theExcelSheet.setDefaultCellStyle(getColumnIndex(), pStyle);
        }
    }
}
