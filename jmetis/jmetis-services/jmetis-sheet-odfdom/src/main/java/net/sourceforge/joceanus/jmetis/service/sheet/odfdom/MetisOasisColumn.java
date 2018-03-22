/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/service/sheet/odfdom/MetisOasisColumn.java $
 * $Revision: 923 $
 * $Author: Tony $
 * $Date: 2018-03-22 09:07:36 +0000 (Thu, 22 Mar 2018) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.service.sheet.odfdom;

import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetColumn;

/**
 * Class representing a column in Oasis.
 * @author Tony Washer
 */
public class MetisOasisColumn
        extends MetisSheetColumn {
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
        final String myString = theOasisColumn.getTableVisibilityAttribute();
        return myString != null
               && myString.equals(TableVisibilityAttribute.Value.COLLAPSE.toString());
    }

    @Override
    public void setDefaultCellStyle(final MetisSheetCellStyleType pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the default cell style and the column style */
            final MetisOasisSheet mySheet = theColumnMap.getSheet();
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
