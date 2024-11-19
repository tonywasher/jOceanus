/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.service.sheet.hssf;

import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetColumn;

/**
 * Class representing and Excel column.
 * @author Tony Washer
 */
public class PrometheusExcelHSSFColumn
        extends PrometheusSheetColumn {
    /**
     * The Excel Sheet.
     */
    private final PrometheusExcelHSSFSheet theExcelSheet;

   /**
     * Constructor.
     * @param pSheet the excel sheet
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    PrometheusExcelHSSFColumn(final PrometheusExcelHSSFSheet pSheet,
                              final int pIndex,
                              final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pIndex, pReadOnly);
        theExcelSheet = pSheet;
    }

    @Override
    public PrometheusExcelHSSFColumn getNextColumn() {
        /* Determine the required index */
        final int myIndex = getColumnIndex() + 1;

        /* Return the next column */
        return theExcelSheet.getReadOnlyColumnByIndex(myIndex);
    }

    @Override
    public PrometheusExcelHSSFColumn getPreviousColumn() {
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
    protected void setDefaultCellStyleValue(final PrometheusSheetCellStyleType pStyle) {
        theExcelSheet.setDefaultCellStyle(getColumnIndex(), pStyle);
    }
}
