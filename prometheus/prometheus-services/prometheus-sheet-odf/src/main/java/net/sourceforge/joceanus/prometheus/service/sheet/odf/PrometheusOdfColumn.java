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
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetColumn;

/**
 * Column class.
 */
public class PrometheusOdfColumn
        extends PrometheusSheetColumn {
    /**
     * The column storage.
     */
    private final PrometheusOdfColumnStore theStore;

    /**
     * Constructor.
     * @param pStore the column store
     * @param pSheet the owning sheet
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    PrometheusOdfColumn(final PrometheusOdfColumnStore pStore,
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
    public PrometheusOdfColumn getNextColumn() {
        return theStore.getReadOnlyColumnByIndex(getSheet(), getColumnIndex() + 1);
    }

    @Override
    public PrometheusOdfColumn getPreviousColumn() {
        return theStore.getReadOnlyColumnByIndex(getSheet(), getColumnIndex() - 1);
    }

    @Override
    public boolean isHidden() {
        return theStore.getHiddenAtIndex(getColumnIndex());
    }

    @Override
    protected void setDefaultCellStyleValue(final PrometheusSheetCellStyleType pStyle) {
        theStore.setStyleAtIndex(getColumnIndex(), pStyle);
    }

    @Override
    protected void setHiddenValue(final boolean isHidden) {
        theStore.setHiddenAtIndex(getColumnIndex(), isHidden);
    }
}
