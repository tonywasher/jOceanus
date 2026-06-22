/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheet.PrometheusSheetSheetCtl;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheet.PrometheusSheetWorkBookCtl;

import java.util.ListIterator;

/**
 * Class representing a sheet within a workBook.
 */
public abstract class PrometheusSheetSheet
        implements PrometheusSheetSheetCtl {
    /**
     * The WorkBook.
     */
    private final PrometheusSheetWorkBookCtl theWorkBook;

    /**
     * Name of sheet.
     */
    private final String theSheetName;

    /**
     * Is the sheet readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor for Excel Sheet.
     *
     * @param pWorkBook the workBook
     * @param pName     the sheet name
     * @param pReadOnly is the sheet readOnly?
     */
    protected PrometheusSheetSheet(final PrometheusSheetWorkBookCtl pWorkBook,
                                   final String pName,
                                   final boolean pReadOnly) {
        /* Store parameters */
        theWorkBook = pWorkBook;
        theSheetName = pName;
        isReadOnly = pReadOnly;
    }

    /**
     * Obtain the workBook.
     *
     * @return the workBook
     */
    public PrometheusSheetWorkBookCtl getWorkBook() {
        return theWorkBook;
    }

    /**
     * Obtain the name of the sheet.
     *
     * @return the name
     */
    public String getName() {
        return theSheetName;
    }

    /**
     * Is the sheet readOnly?
     *
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Get row count.
     *
     * @return the count of rows
     */
    public abstract int getRowCount();

    /**
     * Obtain an iterator of non-null rows for the view.
     *
     * @param pFirstIndex the first row in the view
     * @param pLastIndex  the last row in the view
     * @return the iterator
     */
    public abstract ListIterator<PrometheusSheetRow> iteratorForRange(int pFirstIndex,
                                                                      int pLastIndex);

    /**
     * Name a single cell as a range.
     *
     * @param pName       the name of the range
     * @param pSingleCell the cell to name
     * @throws OceanusException on error
     */
    public void declareRange(final String pName,
                             final PrometheusSheetCellPosition pSingleCell) throws OceanusException {
        /* declare the range */
        declareRange(pName, pSingleCell, pSingleCell);
    }
}
