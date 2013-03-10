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

import net.sourceforge.jOceanus.jSpreadSheetManager.OasisWorkBook.OasisStyle;

import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;

/**
 * Class representing a column in Oasis.
 * @author Tony Washer
 */
public class OasisColumn
        extends DataColumn {
    /**
     * The list of columns.
     */
    private final OasisColumnMap theColumnMap;

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
    protected OasisColumn(final OasisColumnMap pMap,
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
    public OasisColumn getNextColumn() {
        return theColumnMap.getReadOnlyColumnByIndex(getColumnIndex() + 1);
    }

    @Override
    public OasisColumn getPreviousColumn() {
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
        return (myString == null)
                ? false
                : myString.equals(TableVisibilityAttribute.Value.COLLAPSE.toString());
    }

    @Override
    public void setDefaultCellStyle(final CellStyleType pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the default cell style and the column style */
            OasisStyle myStyle = OasisWorkBook.getOasisCellStyle(pStyle);
            OasisStyle myColStyle = OasisWorkBook.getOasisColumnStyle(myStyle);
            theOasisColumn.setTableDefaultCellStyleNameAttribute(OasisWorkBook.getStyleName(myStyle));
            theOasisColumn.setTableStyleNameAttribute(OasisWorkBook.getStyleName(myColStyle));
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
