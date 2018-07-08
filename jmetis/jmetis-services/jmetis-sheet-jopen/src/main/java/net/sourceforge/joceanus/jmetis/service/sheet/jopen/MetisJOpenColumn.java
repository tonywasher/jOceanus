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
package net.sourceforge.joceanus.jmetis.service.sheet.jopen;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetColumn;

/**
 * JOpenDocument Column.
 */
public class MetisJOpenColumn
        extends MetisSheetColumn {
    /**
     * The JOpen Sheet.
     */
    private final MetisJOpenSheet theJOpenSheet;

    /**
     * Is the column readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pSheet the jOpen sheet
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    protected MetisJOpenColumn(final MetisJOpenSheet pSheet,
                               final int pIndex,
                               final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pIndex);
        theJOpenSheet = pSheet;
        isReadOnly = pReadOnly;
    }

    @Override
    public MetisJOpenColumn getNextColumn() {
        /* Determine the required index */
        final int myIndex = getColumnIndex() + 1;

        /* Return the next column */
        return theJOpenSheet.getReadOnlyColumnByIndex(myIndex);
    }

    @Override
    public MetisJOpenColumn getPreviousColumn() {
        /* Determine the required index */
        final int myIndex = getColumnIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous column */
        return theJOpenSheet.getReadOnlyColumnByIndex(myIndex);
    }

    @Override
    public void setHidden(final boolean isHidden) {
        /* NoOp */
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setDefaultCellStyle(final MetisSheetCellStyleType pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            theJOpenSheet.setDefaultCellStyle(getColumnIndex(), pStyle);
        }
    }
}
