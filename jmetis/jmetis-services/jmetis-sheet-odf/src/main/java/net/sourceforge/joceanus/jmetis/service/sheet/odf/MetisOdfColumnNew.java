/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetColumn;

/**
 * Column class.
 */
public class MetisOdfColumnNew
        extends MetisSheetColumn {
    /**
     * The column storage.
     */
    private final MetisOdfColumnStore theStore;

    /**
     * Constructor.
     * @param pStore the column store
     * @param pSheet the owning sheet
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    MetisOdfColumnNew(final MetisOdfColumnStore pStore,
                      final MetisOdfSheetNew pSheet,
                      final int pIndex,
                      final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pIndex, pReadOnly);
        theStore = pStore;
    }

    @Override
    public MetisOdfSheetNew getSheet() {
        return (MetisOdfSheetNew) super.getSheet();
    }

    @Override
    public MetisOdfColumnNew getNextColumn() {
        return theStore.getReadOnlyColumnByIndex(getSheet(), getColumnIndex() + 1);
    }

    @Override
    public MetisOdfColumnNew getPreviousColumn() {
        return theStore.getReadOnlyColumnByIndex(getSheet(), getColumnIndex() - 1);
    }

    @Override
    public boolean isHidden() {
        return theStore.getHiddenAtIndex(getColumnIndex());
    }

    @Override
    protected void setDefaultCellStyleValue(final MetisSheetCellStyleType pStyle) {
        theStore.setStyleAtIndex(getColumnIndex(), pStyle);
    }

    @Override
    protected void setHiddenValue(final boolean isHidden) {
        theStore.setHiddenAtIndex(getColumnIndex(), isHidden);
    }
}
