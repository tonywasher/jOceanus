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

package io.github.tonywasher.joceanus.prometheus.service.sheet.odf;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellPosition;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellRange;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetSheetCtl;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import org.w3c.dom.Element;

import java.util.ListIterator;

/**
 * Odf Interfaces.
 */
public interface PrometheusOdf {
    /**
     * Interface for Odf WorkBook.
     */
    interface PrometheusOdfWorkBookCtl
            extends PrometheusSheetWorkBook {
        /**
         * Obtain the parser.
         *
         * @return the parser
         */
        PrometheusOdfParser getParser();

        /**
         * Obtain the styler.
         *
         * @return the styler
         */
        PrometheusOdfStyler getStyler();

        /**
         * Obtain the data formatter.
         *
         * @return the formatter
         */
        OceanusDataFormatter getFormatter();
    }

    /**
     * Table storage interface.
     */
    interface PrometheusOdfTableStoreCtl {
        /**
         * Obtain the workBook.
         *
         * @return the WorkBook
         */
        PrometheusOdfWorkBookCtl getWorkBook();

        /**
         * Declare the named range.
         *
         * @param pName  the name of the range
         * @param pRange the range to declare
         * @throws OceanusException on error
         */
        void declareRange(String pName,
                          PrometheusSheetCellRange pRange) throws OceanusException;

        /**
         * Apply Data Validation.
         *
         * @param pSheet      the workSheet containing the cells
         * @param pFirstCell  the first cell in the range
         * @param pLastCell   the last cell in the range
         * @param pValidRange the name of the validation range
         */
        void applyDataValidation(PrometheusOdfSheetCoreCtl pSheet,
                                 PrometheusSheetCellPosition pFirstCell,
                                 PrometheusSheetCellPosition pLastCell,
                                 String pValidRange);

        /**
         * Apply Data Validation.
         *
         * @param pSheet     the workSheet containing the cells
         * @param pFirstCell the first cell in the range
         * @param pLastCell  the last cell in the range
         * @param pValueList the value list
         */
        void applyDataValidation(PrometheusOdfSheetCoreCtl pSheet,
                                 PrometheusSheetCellPosition pFirstCell,
                                 PrometheusSheetCellPosition pLastCell,
                                 String[] pValueList);

        /**
         * Apply Data Filter.
         *
         * @param pRange the range
         */
        void applyDataFilter(PrometheusSheetCellRange pRange);
    }

    /**
     * Sheet Interface.
     */
    interface PrometheusOdfSheetCoreCtl {
        /**
         * Obtain the name.
         *
         * @return the name
         */
        String getName();

        /**
         * Obtain the formatter.
         *
         * @return the formatter
         */
        OceanusDataFormatter getFormatter();

        /**
         * Obtain the rowCount.
         *
         * @return the rowCount
         */
        int getRowCount();

        /**
         * Obtain the parser.
         *
         * @return the parser
         */
        PrometheusOdfParser getParser();

        /**
         * Is the sheet hidden?
         *
         * @return true/false
         */
        boolean isHidden();

        /**
         * Set the sheet hidden status.
         *
         * @param pHidden true/false
         */
        void setHidden(boolean pHidden);

        /**
         * Obtain an iterator of non-null rows for the range.
         *
         * @param pSheet    the sheet for the rows
         * @param pFirstRow the index of the first row.
         * @param pLastRow  the index of the last row.
         * @return the iterator
         */
        ListIterator<PrometheusSheetRow> iteratorForRange(PrometheusOdfSheetCtl pSheet,
                                                          int pFirstRow,
                                                          int pLastRow);

        /**
         * Obtain a readOnly row by its index.
         *
         * @param pSheet    the owning sheet
         * @param pRowIndex the index of the row.
         * @return the row
         */
        PrometheusOdfRowCtl getReadOnlyRowByIndex(PrometheusOdfSheetCtl pSheet,
                                                  int pRowIndex);

        /**
         * Obtain a mutable row by its index, creating row if it does not exist.
         *
         * @param pSheet    the owning sheet
         * @param pRowIndex the index of the row.
         * @return the row
         */
        PrometheusOdfRowCtl getMutableRowByIndex(PrometheusOdfSheetCtl pSheet,
                                                 int pRowIndex);

        /**
         * Obtain a readOnly column by its index.
         *
         * @param pSheet    the owning sheet
         * @param pColIndex the index of the row.
         * @return the column
         */
        PrometheusOdfColumnCtl getReadOnlyColumnByIndex(PrometheusOdfSheetCtl pSheet,
                                                        int pColIndex);

        /**
         * Obtain a mutable column by its index, creating column if it does not exist.
         *
         * @param pSheet    the owning sheet
         * @param pColIndex the index of the column.
         * @return the column
         */
        PrometheusOdfColumnCtl getMutableColumnByIndex(PrometheusOdfSheetCtl pSheet,
                                                       int pColIndex);

        /**
         * Add additional columns to rows.
         *
         * @param pXtraCols the number of columns to add.
         */
        void addAdditionalCols(int pXtraCols);

        /**
         * Apply validation.
         *
         * @param pValidation the validation name
         * @param pFirstCell  the first cell
         * @param pLastCell   the last cell
         */
        void applyValidation(String pValidation,
                             PrometheusSheetCellPosition pFirstCell,
                             PrometheusSheetCellPosition pLastCell);

        /**
         * Set the column style for the column.
         *
         * @param pColumn the column
         * @param pStyle  the style
         */
        void setColumnStyle(Element pColumn,
                            PrometheusSheetCellStyleType pStyle);

        /**
         * Set the default style for the column.
         *
         * @param pColumn the column index
         * @param pStyle  the style
         */
        void setDefaultCellStyle(Element pColumn,
                                 PrometheusSheetCellStyleType pStyle);

        /**
         * Set cell style.
         *
         * @param pCell  the cell to style
         * @param pValue the cell value
         */
        void setCellStyle(Element pCell,
                          Object pValue);

        /**
         * Set alternate cell style.
         *
         * @param pCell  the cell to style
         * @param pValue the cell value
         */
        void setAlternateCellStyle(Element pCell,
                                   Object pValue);

        /**
         * Declare the named range.
         *
         * @param pName  the name of the range
         * @param pRange the range to declare
         * @throws OceanusException on error
         */
        void declareRange(String pName,
                          PrometheusSheetCellRange pRange) throws OceanusException;

        /**
         * Apply Data Validation.
         *
         * @param pFirstCell  the the first cell in the range
         * @param pLastCell   the last cell in the range
         * @param pValidRange the name of the validation range
         */
        void applyDataValidation(PrometheusSheetCellPosition pFirstCell,
                                 PrometheusSheetCellPosition pLastCell,
                                 String pValidRange);

        /**
         * Apply Data Filter.
         *
         * @param pRange the range
         */
        void applyDataFilter(PrometheusSheetCellRange pRange);
    }

    /**
     * Sheet Interface.
     */
    interface PrometheusOdfSheetCtl
            extends PrometheusSheetSheetCtl {
    }

    /**
     * Interface for Row Store.
     */
    interface PrometheusOdfRowStoreCtl {
        /**
         * Obtain OasisSheet.
         *
         * @return the row.
         */
        PrometheusOdfSheetCoreCtl getSheet();

        /**
         * Format object value.
         *
         * @param pValue the value
         * @return the formatted value
         */
        String formatValue(Object pValue);

        /**
         * Parse object value.
         *
         * @param <T>     the value type
         * @param pSource the source value
         * @param pClass  the value type class
         * @return the formatted value
         */
        <T> T parseValue(String pSource,
                         Class<T> pClass);

        /**
         * Parse object value.
         *
         * @param <T>       the value type
         * @param pSource   the source value
         * @param pCurrCode the currency code
         * @param pClass    the value type class
         * @return the formatted value
         */
        <T> T parseValue(Double pSource,
                         String pCurrCode,
                         Class<T> pClass);

        /**
         * Ensure and determine the cell style.
         *
         * @param pCell  the cell to style
         * @param pValue the cell value
         */
        void setCellStyle(Element pCell,
                          Object pValue);

        /**
         * Ensure and determine the alternate cell style.
         *
         * @param pCell  the cell to style
         * @param pValue the cell value
         */
        void setAlternateCellStyle(Element pCell,
                                   Object pValue);

        /**
         * Obtain a readOnly row by its index.
         *
         * @param pSheet    the owning sheet
         * @param pRowIndex the index of the row.
         * @return the row
         */
        PrometheusOdfRowCtl getReadOnlyRowByIndex(PrometheusOdfSheetCtl pSheet,
                                                  int pRowIndex);

        /**
         * Obtain an iterator of non-null cells for the range.
         *
         * @param pRow        the row for the cell
         * @param pFirstIndex the index of the first cell.
         * @param pLastIndex  the index of the last cell.
         * @return the iterator
         */
        ListIterator<PrometheusSheetCell> iteratorForRange(PrometheusOdfRowCtl pRow,
                                                           int pFirstIndex,
                                                           int pLastIndex);

        /**
         * Obtain a readOnly cell by its index.
         *
         * @param pRow       the row containing the cell.
         * @param pCellIndex the index of the cell.
         * @return the row
         */
        PrometheusOdfCellCtl getReadOnlyCellByIndex(PrometheusOdfRowCtl pRow,
                                                    int pCellIndex);

        /**
         * Obtain a mutable cell by its index.
         *
         * @param pRow       the row containing the cell.
         * @param pCellIndex the index of the cell.
         * @return the row
         */
        PrometheusOdfCellCtl getMutableCellByIndex(PrometheusOdfRowCtl pRow,
                                                   int pCellIndex);

        /**
         * Obtain the index of the max valued cell.
         *
         * @param pRow the row containing the cell.
         * @return the index
         */
        int getMaxValuedCellForRow(PrometheusOdfRowCtl pRow);

        /**
         * Obtain Cell count.
         *
         * @return the cell count.
         */
        int getCellCount();

        /**
         * Get hidden flag at index.
         *
         * @param pIndex the index
         * @return the value
         */
        boolean getHiddenAtIndex(int pIndex);

        /**
         * Set hidden flag at index.
         *
         * @param pIndex  the index
         * @param pHidden true/false
         */
        void setHiddenAtIndex(int pIndex,
                              boolean pHidden);
    }

    /**
     * Row interface.
     */
    interface PrometheusOdfRowCtl {
        /**
         * Obtain the sheet.
         *
         * @return the sheet
         */
        PrometheusOdfSheetCtl getSheet();

        /**
         * Obtain the row index.
         *
         * @return the row index
         */
        int getRowIndex();
    }

    /**
     * Interface for ColumnStore.
     */
    interface PrometheusOdfColumnStoreCtl {
        /**
         * Obtain a readOnly column by its index.
         *
         * @param pSheet    the owning sheet
         * @param pColIndex the index of the column.
         * @return the column if it exists, else null
         */
        PrometheusOdfColumnCtl getReadOnlyColumnByIndex(PrometheusOdfSheetCtl pSheet,
                                                        int pColIndex);

        /**
         * Get hidden flag at index.
         *
         * @param pIndex the index
         * @return the value
         */
        boolean getHiddenAtIndex(int pIndex);

        /**
         * Set hidden flag at index.
         *
         * @param pIndex  the index
         * @param pHidden true/false
         */
        void setHiddenAtIndex(int pIndex,
                              boolean pHidden);

        /**
         * Set style at index.
         *
         * @param pIndex the index
         * @param pStyle the style
         */
        void setStyleAtIndex(int pIndex,
                             PrometheusSheetCellStyleType pStyle);
    }

    /**
     * Interface for Column.
     */
    interface PrometheusOdfColumnCtl {
    }

    /**
     * Interface for CellStore.
     */
    interface PrometheusOdfCellStoreCtl {
        /**
         * Access the value as Boolean.
         *
         * @param pIndex the index
         * @return the boolean
         */
        Boolean getBooleanValueAtIndex(int pIndex);

        /**
         * Access the value as Date.
         *
         * @param pIndex the index
         * @return the date
         */
        OceanusDate getDateValueAtIndex(int pIndex);

        /**
         * Access the value as Integer.
         *
         * @param pIndex the index
         * @return the integer
         * @throws OceanusException on error
         */
        Integer getIntegerValueAtIndex(int pIndex) throws OceanusException;

        /**
         * Access the value as Long.
         *
         * @param pIndex the index
         * @return the long
         * @throws OceanusException on error
         */
        Long getLongValueAtIndex(int pIndex) throws OceanusException;

        /**
         * Access the value as Rate.
         *
         * @param pIndex the index
         * @return the rate
         * @throws OceanusException on error
         */
        OceanusRate getRateValueAtIndex(int pIndex) throws OceanusException;

        /**
         * Access the value as Units.
         *
         * @param pIndex the index
         * @return the units
         * @throws OceanusException on error
         */
        OceanusUnits getUnitsValueAtIndex(int pIndex) throws OceanusException;

        /**
         * Access the value as Ratio.
         *
         * @param pIndex the index
         * @return the ratio
         * @throws OceanusException on error
         */
        OceanusRatio getRatioValueAtIndex(int pIndex) throws OceanusException;

        /**
         * Access the value as Money.
         *
         * @param pIndex the index
         * @return the rate
         * @throws OceanusException on error
         */
        OceanusMoney getMoneyValueAtIndex(int pIndex) throws OceanusException;

        /**
         * Access the value as Price.
         *
         * @param pIndex the index
         * @return the price
         * @throws OceanusException on error
         */
        OceanusPrice getPriceValueAtIndex(int pIndex) throws OceanusException;

        /**
         * Access the value as String.
         *
         * @param pIndex the index
         * @return the string
         */
        String getStringValueAtIndex(int pIndex);

        /**
         * Set value at index.
         *
         * @param pValue the value
         * @param pIndex the index
         */
        void setValueAtIndex(Object pValue,
                             int pIndex);

        /**
         * Set alternate value at index.
         *
         * @param pValue the value
         * @param pIndex the index
         */
        void setAlternateAtIndex(Object pValue,
                                 int pIndex);
    }

    /**
     * Interface for Cell.
     */
    interface PrometheusOdfCellCtl {
    }
}
