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

import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetSheet;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellValue;

public abstract class PrometheusExcelSheet
        extends PrometheusSheetSheet {
    /**
     * Constructor for Excel Sheet.
     *
     * @param pBook     the WorkBook
     * @param pSheet    the Excel sheet
     * @param pReadOnly is the sheet readOnly?
     */
    PrometheusExcelSheet(final PrometheusSheetWorkBook pBook,
                         final HSSFSheet pSheet,
                         final boolean pReadOnly) {
        /* Construct super-class */
        super(pBook, pSheet.getSheetName(), pReadOnly);
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
     * Obtain the data formatter.
     *
     * @return the formatter
     */
    abstract OceanusDataFormatter getDataFormatter();

    /**
     * Set the column's hidden status.
     *
     * @param pColIndex the column index
     * @param isHidden  true/false
     */
    abstract void setColumnHidden(int pColIndex,
                                  boolean isHidden);

    /**
     * Is the column hidden?
     *
     * @param pColIndex the column
     * @return true/false
     */
    abstract boolean isColumnHidden(int pColIndex);

    /**
     * Set the default style for the column.
     *
     * @param pColIndex the column index
     * @param pStyle    the style
     */
    abstract void setDefaultCellStyle(int pColIndex,
                                      PrometheusSheetCellStyleType pStyle);

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
