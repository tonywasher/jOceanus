/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.sheet;

import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;

/**
 * Class representing a column in Oasis.
 * @author Tony Washer
 */
public class MetisOasisColumn
        extends MetisDataColumn {
    /**
     * The list of columns.
     */
    private final MetisOasisColumnMap theColumnMap;

    /**
     * The underlying ODFDOM column.
     */
    private final TableTableColumnElement theOasisColumn;

    /**
     * Is the column readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pMap the column map
     * @param pColumn the Oasis column
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    protected MetisOasisColumn(final MetisOasisColumnMap pMap,
                               final TableTableColumnElement pColumn,
                               final int pIndex,
                               final boolean pReadOnly) {
        /* Store parameters */
        super(pMap.getSheet(), pIndex);
        theColumnMap = pMap;
        theOasisColumn = pColumn;
        isReadOnly = pReadOnly;
    }

    @Override
    public MetisOasisColumn getNextColumn() {
        return theColumnMap.getReadOnlyColumnByIndex(getColumnIndex() + 1);
    }

    @Override
    public MetisOasisColumn getPreviousColumn() {
        return theColumnMap.getReadOnlyColumnByIndex(getColumnIndex() - 1);
    }

    /**
     * Obtain the column style name.
     * @return the column style name
     */
    protected String getColumnStyle() {
        return theOasisColumn.getTableStyleNameAttribute();
    }

    @Override
    public boolean isHidden() {
        String myString = theOasisColumn.getTableVisibilityAttribute();
        return myString != null
               && myString.equals(TableVisibilityAttribute.Value.COLLAPSE.toString());
    }

    @Override
    public void setDefaultCellStyle(final MetisCellStyleType pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the default cell style and the column style */
            MetisOasisSheet mySheet = theColumnMap.getSheet();
            mySheet.setColumnStyle(theOasisColumn, pStyle);
            mySheet.setDefaultCellStyle(theOasisColumn, pStyle);
        }
    }

    @Override
    public void setHidden(final boolean isHidden) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the visibility attribute */
            theOasisColumn.setTableVisibilityAttribute(isHidden
                                                                ? TableVisibilityAttribute.Value.COLLAPSE.toString()
                                                                : TableVisibilityAttribute.Value.VISIBLE.toString());
        }
    }
}
