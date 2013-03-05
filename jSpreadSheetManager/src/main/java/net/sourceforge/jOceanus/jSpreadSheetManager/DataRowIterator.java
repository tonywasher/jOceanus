/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2013 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.util.Iterator;

/**
 * Iterator class for iterating through rows in a sheet or view.
 * @author Tony Washer
 */
public class DataRowIterator
        implements Iterator<DataRow> {
    /**
     * The last row.
     */
    private DataRow theLastRow = null;

    /**
     * Is this derived from a view.
     */
    private final boolean isView;

    /**
     * The sheet that this iterator is based on.
     */
    private final DataSheet theSheet;

    /**
     * The view that this iterator is based on.
     */
    private final DataView theView;

    /**
     * Constructor.
     * @param pSheet the underlying sheet.
     */
    protected DataRowIterator(final DataSheet pSheet) {
        theSheet = pSheet;
        theView = null;
        isView = false;
    }

    /**
     * Constructor.
     * @param pView the underlying view.
     */
    protected DataRowIterator(final DataView pView) {
        theView = pView;
        theSheet = null;
        isView = true;
    }

    @Override
    public boolean hasNext() {
        /* Calculate the next index */
        int iIndex = (theLastRow != null)
                ? theLastRow.getRowIndex() + 1
                : 0;

        /* If this is based on a view */
        if (isView) {
            /* Check that the row is within the view */
            return (theView.convertRowIndex(iIndex) >= 0);
        }

        /* Always capable of new row for sheet */
        return true;
    }

    @Override
    public DataRow next() {
        /* If we are a new iterator */
        if (theLastRow == null) {
            /* If this is based on a view */
            if (isView) {
                /* Access the first element of the view */
                theLastRow = theView.getRowByIndex(0);

                /* else we are based on a sheet */
            } else {
                /* Access the first element of the sheet */
                theLastRow = theSheet.createRowByIndex(0);
            }
        } else {
            /* Return the next row */
            theLastRow = theLastRow.getNextRow();
        }

        /* Return the next row */
        return theLastRow;
    }

    @Override
    public void remove() {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }
}
