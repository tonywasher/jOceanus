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

package io.github.tonywasher.joceanus.prometheus.service.sheet;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.io.OutputStream;

/**
 * Prometheus Sheet interfaces.
 */
public interface PrometheusSheet {
    /**
     * Interface for workBook.
     */
    interface PrometheusSheetWorkBookCtl {
        /**
         * Is the WorkBook readOnly?
         *
         * @return true/false
         */
        boolean isReadOnly();

        /**
         * Save the workBook to output stream.
         *
         * @param pOutput the output stream
         * @throws OceanusException on error
         */
        void saveToStream(OutputStream pOutput) throws OceanusException;

        /**
         * Create a new Sheet with the given name.
         *
         * @param pName the name of the new sheet
         * @return the new sheet
         * @throws OceanusException on error
         */
        PrometheusSheetSheetCtl newSheet(String pName) throws OceanusException;

        /**
         * Create a new Sheet with the given name.
         *
         * @param pName    the name of the new sheet
         * @param pNumRows the number of rows to allocate
         * @param pNumCols the number of columns to allocate
         * @return the new sheet
         * @throws OceanusException on error
         */
        PrometheusSheetSheetCtl newSheet(String pName,
                                         int pNumRows,
                                         int pNumCols) throws OceanusException;

        /**
         * Access an existing Sheet with the given name.
         *
         * @param pName the name of the sheet
         * @return the sheet (or null if no such sheet)
         * @throws OceanusException on error
         */
        PrometheusSheetSheetCtl getSheet(String pName) throws OceanusException;

        /**
         * Obtain a view of the named range.
         *
         * @param pName the name of the range
         * @return the view of the range
         * @throws OceanusException on error
         */
        PrometheusSheetViewCtl getRangeView(String pName) throws OceanusException;

        /**
         * Create data formatter.
         *
         * @return the new formatter
         */
        default OceanusDataFormatter createFormatter() {
            /* Allocate the formatter and set date format */
            final OceanusDataFormatter myFormatter = new OceanusDataFormatter();
            myFormatter.setFormat(PrometheusSheetFormats.OASIS_DATE);
            myFormatter.setAccountingWidth(PrometheusSheetFormats.ACCOUNTING_WIDTH);

            /* return the formatter */
            return myFormatter;
        }
    }

    /**
     * Interface for a view of a range of cells.
     */
    interface PrometheusSheetViewCtl {
        /**
         * Obtain the underlying sheet.
         *
         * @return the underlying sheet
         */
        PrometheusSheetSheetCtl getSheet();
    }

    /**
     * Interface representing a sheet within a workBook.
     */
    interface PrometheusSheetSheetCtl {
        /**
         * Obtain the name of the sheet.
         *
         * @return the name
         */
        String getName();

        /**
         * Get sheet index.
         *
         * @return the index of the sheet
         */
        int getSheetIndex();

        /**
         * Is the sheet hidden?
         *
         * @return true/false
         */
        boolean isHidden();

        /**
         * Set sheet hidden status.
         *
         * @param isHidden true/false
         */
        void setHidden(boolean isHidden);

        /**
         * Obtain the row at required index within the sheet, if it exists.
         *
         * @param pRowIndex the requested row index
         * @return the requested row.
         */
        PrometheusSheetRowCtl getReadOnlyRowByIndex(int pRowIndex);

        /**
         * Obtain the row at required index within the sheet, create it if it does not exist.
         *
         * @param pRowIndex the requested row index
         * @return the requested row.
         */
        PrometheusSheetRowCtl getMutableRowByIndex(int pRowIndex);

        /**
         * Obtain the column by index.
         *
         * @param pColIndex the column index
         * @return the column
         */
        PrometheusSheetColumnCtl getReadOnlyColumnByIndex(int pColIndex);

        /**
         * Obtain the column by index, creating column if it does not exist.
         *
         * @param pColIndex the column index
         * @return the column
         */
        PrometheusSheetColumnCtl getMutableColumnByIndex(int pColIndex);

        /**
         * Name a range.
         *
         * @param pName      the name of the range
         * @param pFirstCell the first cell in the range
         * @param pLastCell  the last cell in the range
         * @throws OceanusException on error
         */
        void declareRange(String pName,
                          PrometheusSheetCellPosition pFirstCell,
                          PrometheusSheetCellPosition pLastCell) throws OceanusException;

        /**
         * Apply data validation to a range of cells.
         *
         * @param pFirstCell the first cell in the range
         * @param pLastCell  the last cell in the range
         * @param pName      the name of the validation range list
         * @throws OceanusException on error
         */
        void applyDataValidation(PrometheusSheetCellPosition pFirstCell,
                                 PrometheusSheetCellPosition pLastCell,
                                 String pName) throws OceanusException;

        /**
         * Apply data validation to a range of cells.
         *
         * @param pBaseCell the first cell in the range
         * @param pNumRows  the number of rows in the filter
         * @throws OceanusException on error
         */
        void applyDataFilter(PrometheusSheetCellPosition pBaseCell,
                             int pNumRows) throws OceanusException;

        /**
         * Create freeze panes.
         *
         * @param pFreezeCell the cell to freeze at
         */
        void createFreezePane(PrometheusSheetCellPosition pFreezeCell);
    }

    /**
     * Interface representing a row.
     */
    interface PrometheusSheetRowCtl {
    }

    /**
     * Interface representing a column.
     */
    interface PrometheusSheetColumnCtl {
    }
}
