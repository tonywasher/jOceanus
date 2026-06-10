/*
 * Prometheus: Application Framework
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.prometheus.service.sheet.hssf;

import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetSheet;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellValue;

/**
 * Class representing a row within a sheet or a view.
 */

public abstract class PrometheusExcelRow
        extends PrometheusSheetRow {
    /**
     * Constructor.
     *
     * @param pSheet    the sheet for the row
     * @param pRowIndex the RowIndex
     * @param pReadOnly is the row readOnly?
     */
    PrometheusExcelRow(final PrometheusSheetSheet pSheet,
                       final int pRowIndex,
                       final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pRowIndex, pReadOnly);
    }

    /**
     * evaluate the formula for a cell.
     *
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    abstract CellValue evaluateFormula(HSSFCell pCell);

    /**
     * Format the cell value.
     *
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    abstract String formatCellValue(HSSFCell pCell);

    /**
     * Parse object value.
     *
     * @param <T>     the value type
     * @param pSource the source value
     * @param pClass  the value type class
     * @return the formatted value
     */
    abstract <T> T parseValue(String pSource,
                              Class<T> pClass);

    /**
     * Set cell style.
     *
     * @param pCell  the cell to style
     * @param pValue the cell value
     */
    abstract void setCellStyle(PrometheusExcelCell pCell,
                               Object pValue);

    /**
     * Set alternate cell style.
     *
     * @param pCell  the cell to style
     * @param pValue the cell value
     */
    abstract void setAlternateCellStyle(PrometheusExcelCell pCell,
                                        Object pValue);
}
