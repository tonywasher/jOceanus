/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCellPosition;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCellRange;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Sheet class.
 */
public class PrometheusOdfSheet
        extends PrometheusSheetSheet {
    /**
     * The underlying sheet.
     */
    private final PrometheusOdfSheetCore theSheet;

    /**
     * The index.
     */
    private final int theIndex;

    /**
     * Constructor.
     * @param pBook the workBook
     * @param pSheet the underlying sheet
     * @param pIndex the index
     * @param pReadOnly is the sheet readOnly?
     */
    PrometheusOdfSheet(final PrometheusOdfWorkBook pBook,
                       final PrometheusOdfSheetCore pSheet,
                       final int pIndex,
                       final boolean pReadOnly) {
        super(pBook, pSheet.getName(), pReadOnly);
        theSheet = pSheet;
        theIndex = pIndex;
    }

    @Override
    public int getRowCount() {
        return theSheet.getRowCount();
    }

    @Override
    public PrometheusOdfRow getReadOnlyRowByIndex(final int pRowIndex) {
        return theSheet.getReadOnlyRowByIndex(this, pRowIndex);
    }

    @Override
    protected ListIterator<PrometheusSheetRow> iteratorForRange(final int pFirstIndex,
                                                                final int pLastIndex) {
        return theSheet.iteratorForRange(this, pFirstIndex, pLastIndex);
    }

    @Override
    public PrometheusOdfRow getMutableRowByIndex(final int pRowIndex) {
        return isReadOnly()
               ? null
               : theSheet.getMutableRowByIndex(this, pRowIndex);
    }

    @Override
    public PrometheusOdfColumn getReadOnlyColumnByIndex(final int pColIndex) {
        return theSheet.getReadOnlyColumnByIndex(this, pColIndex);
    }

    @Override
    public PrometheusOdfColumn getMutableColumnByIndex(final int pColIndex) {
        return isReadOnly()
               ? null
               : theSheet.getMutableColumnByIndex(this, pColIndex);
    }

    @Override
    public int getSheetIndex() {
        return theIndex;
    }

    @Override
    public boolean isHidden() {
        return theSheet.isHidden();
    }

    @Override
    public void setHidden(final boolean isHidden) {
        if (!isReadOnly()) {
            theSheet.setHidden(isHidden);
        }
    }

    @Override
    public void declareRange(final String pName,
                             final PrometheusSheetCellPosition pFirstCell,
                             final PrometheusSheetCellPosition pLastCell) throws OceanusException {
        if (!isReadOnly()) {
            /* Build the range */
            final PrometheusSheetCellRange myRange = new PrometheusSheetCellRange(getName(), pFirstCell, pLastCell);

            /* Declare it */
            theSheet.declareRange(pName, myRange);
        }
    }

    @Override
    public void applyDataValidation(final PrometheusSheetCellPosition pFirstCell,
                                    final PrometheusSheetCellPosition pLastCell,
                                    final String pName) throws OceanusException {
        if (!isReadOnly()) {
            /* Declare the validation */
            theSheet.applyDataValidation(pFirstCell, pLastCell, pName);
        }
    }

    @Override
    public void applyDataFilter(final PrometheusSheetCellPosition pBaseCell,
                                final int pNumRows) throws OceanusException {
        if (!isReadOnly()) {
            /* Build the range */
            final PrometheusSheetCellPosition myEnd = new PrometheusSheetCellPosition(pBaseCell.getColumnIndex(), pNumRows - 1);
            final PrometheusSheetCellRange myRange = new PrometheusSheetCellRange(getName(), pBaseCell, myEnd);

            /* Declare it */
            theSheet.applyDataFilter(myRange);
        }
    }

    @Override
    public void createFreezePane(final PrometheusSheetCellPosition pFreezeCell) {
        /* NoOp */
    }
}
