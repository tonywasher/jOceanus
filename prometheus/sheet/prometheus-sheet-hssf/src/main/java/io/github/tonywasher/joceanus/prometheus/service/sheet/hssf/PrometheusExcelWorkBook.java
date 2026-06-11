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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * The Excel WorkBook Interface.
 */
public interface PrometheusExcelWorkBook
        extends PrometheusSheetWorkBook {
    /**
     * Obtain the data formatter.
     *
     * @return the formatter
     */
    OceanusDataFormatter getDataFormatter();

    /**
     * evaluate the formula for a cell.
     *
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    CellValue evaluateFormula(HSSFCell pCell);

    /**
     * Format the cell value.
     *
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    String formatCellValue(HSSFCell pCell);

    /**
     * Is the sheet hidden?
     *
     * @param pSheetIndex the sheet index
     * @return true/false
     */
    boolean isSheetHidden(int pSheetIndex);

    /**
     * Set the sheet's hidden status.
     *
     * @param pSheetIndex the sheet index
     * @param isHidden    true/false
     */
    void setSheetHidden(int pSheetIndex,
                        boolean isHidden);

    /**
     * Declare the named range.
     *
     * @param pName  the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    void declareRange(String pName,
                      AreaReference pRange) throws OceanusException;

    /**
     * Apply Data Validation.
     *
     * @param pSheet      the workSheet containing the cells
     * @param pCells      the Cells to apply validation to
     * @param pValidRange the name of the validation range
     */
    void applyDataValidation(HSSFSheet pSheet,
                             CellRangeAddressList pCells,
                             String pValidRange);

    /**
     * Apply Data Filter.
     *
     * @param pSheet the sheet to filter
     * @param pRange the range to apply the filter to
     */
    void applyDataFilter(Sheet pSheet,
                         CellRangeAddressList pRange);

    /**
     * Obtain the required CellStyle.
     *
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    HSSFCellStyle getCellStyle(Object pValue);

    /**
     * Obtain the required alternate CellStyle.
     *
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    HSSFCellStyle getAlternateCellStyle(Object pValue);
}
