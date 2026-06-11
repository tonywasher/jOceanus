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

import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;

public abstract class PrometheusExcelCell
        extends PrometheusSheetCell {
    /**
     * Constructor.
     *
     * @param pRow      the row for the cell
     * @param pColIndex the column index
     * @param pReadOnly is the cell readOnly?
     */
    PrometheusExcelCell(final PrometheusSheetRow pRow,
                        final int pColIndex,
                        final boolean pReadOnly) {
        /* Store parameters */
        super(pRow.getSheet(), pRow.getRowIndex(), pColIndex, pReadOnly);
    }

    /**
     * Set cell style.
     *
     * @param pStyle the style type to use
     */
    abstract void setCellStyle(HSSFCellStyle pStyle);
}
