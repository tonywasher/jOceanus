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
package net.sourceforge.joceanus.prometheus.service.sheet;

/**
 * Class representing a column in Oasis.
 * @author Tony Washer
 */
public abstract class PrometheusSheetColumn {
    /**
     * The underlying sheet.
     */
    private final PrometheusSheetSheet theSheet;

    /**
     * The index of this column.
     */
    private final int theColIndex;

    /**
     * Is the column readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pSheet the sheet for the column
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    protected PrometheusSheetColumn(final PrometheusSheetSheet pSheet,
                                    final int pIndex,
                                    final boolean pReadOnly) {
        /* Store parameters */
        theSheet = pSheet;
        theColIndex = pIndex;
        isReadOnly = pReadOnly;
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public PrometheusSheetSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the column index.
     * @return column index
     */
    public int getColumnIndex() {
        return theColIndex;
    }

    /**
     * Is the column readOnly?
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Get the next column.
     * @return the next column
     */
    public abstract PrometheusSheetColumn getNextColumn();

    /**
     * Get the previous column.
     * @return the previous column
     */
    public abstract PrometheusSheetColumn getPreviousColumn();

    /**
     * Set hidden status.
     * @param isHidden is the column hidden?
     */
    public void setHidden(final boolean isHidden) {
        if (!isReadOnly) {
            setHiddenValue(isHidden);
        }
    }

    /**
     * Set hidden status.
     * @param isHidden is the column hidden?
     */
    protected abstract void setHiddenValue(boolean isHidden);

    /**
     * Is the column hidden?
     * @return true/false
     */
    public abstract boolean isHidden();

    /**
     * Set hidden status.
     * @param pStyle the cell style
     */
    public void setDefaultCellStyle(final PrometheusSheetCellStyleType pStyle) {
        if (!isReadOnly) {
            setDefaultCellStyleValue(pStyle);
        }
    }

    /**
     * Set the default cell style.
     * @param pStyle the cell style
     */
    protected abstract void setDefaultCellStyleValue(PrometheusSheetCellStyleType pStyle);
}
