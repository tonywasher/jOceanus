/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

import java.util.ListIterator;

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;

/**
 * Row implementation.
 */
public class PrometheusOdfRow
        extends PrometheusSheetRow {
    /**
     * The row store.
     */
    private final PrometheusOdfRowStore theStore;

    /**
     * Constructor .
     * @param pStore the row storage
     * @param pSheet the sheet
     * @param pIndex the index
     * @param pReadOnly is the cell readOnly?
     */
    PrometheusOdfRow(final PrometheusOdfRowStore pStore,
                     final PrometheusOdfSheet pSheet,
                     final int pIndex,
                     final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pIndex, pReadOnly);
        theStore = pStore;
    }

    @Override
    public PrometheusOdfSheet getSheet() {
        return (PrometheusOdfSheet) super.getSheet();
    }

    @Override
    public PrometheusOdfRow getNextRow() {
        return theStore.getReadOnlyRowByIndex(getSheet(), getRowIndex() + 1);
    }

    @Override
    public PrometheusOdfRow getPreviousRow() {
        return theStore.getReadOnlyRowByIndex(getSheet(), getRowIndex() - 1);
    }

    @Override
    protected ListIterator<PrometheusSheetCell> iteratorForRange(final int pFirstIndex,
                                                                 final int pLastIndex) {
        return theStore.iteratorForRange(this, pFirstIndex, pLastIndex);
    }

    @Override
    public int getCellCount() {
        return theStore.getCellCount();
    }

    @Override
    public int getMaxValuedCellIndex() {
        return theStore.getMaxValuedCellForRow(this);
    }

    @Override
    public boolean isHidden() {
        return theStore.getHiddenAtIndex(getRowIndex());
    }

    @Override
    protected void setHiddenValue(final boolean isHidden) {
        theStore.setHiddenAtIndex(getRowIndex(), isHidden);
    }

    @Override
    public PrometheusOdfCell getReadOnlyCellByIndex(final int pIndex) {
        return theStore.getReadOnlyCellByIndex(this, pIndex);
    }

    @Override
    protected PrometheusOdfCell getWriteableCellByIndex(final int pIndex) {
        return theStore.getMutableCellByIndex(this, pIndex);
    }
}
