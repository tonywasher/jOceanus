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

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;

import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;

/**
 * Class representing a row in Oasis.
 * @author Tony Washer
 */
public class OasisRow
        extends DataRow {
    /**
     * The list of rows.
     */
    private final OasisRowMap theRowMap;

    /**
     * The list of cells.
     */
    private final OasisCellMap theCellMap;

    /**
     * The underlying ODFDOM row.
     */
    private final TableTableRowElement theOasisRow;

    /**
     * Is the row readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pMap the row map
     * @param pRow the Oasis row
     * @param pIndex the index
     * @param pReadOnly is the row readOnly?
     */
    protected OasisRow(final OasisRowMap pMap,
                       final TableTableRowElement pRow,
                       final int pIndex,
                       final boolean pReadOnly) {
        /* Store parameters */
        super(pMap.getSheet(), pIndex);
        theRowMap = pMap;
        theOasisRow = pRow;
        isReadOnly = pReadOnly;

        /* Create the cell map */
        theCellMap = new OasisCellMap(this);
    }

    @Override
    public OasisSheet getSheet() {
        return (OasisSheet) super.getSheet();
    }

    @Override
    public OasisRow getNextRow() {
        return theRowMap.getReadOnlyRowByIndex(getRowIndex() + 1);
    }

    @Override
    public OasisRow getPreviousRow() {
        return theRowMap.getReadOnlyRowByIndex(getRowIndex() - 1);
    }

    /**
     * Obtain the row element.
     * @return the row element
     */
    protected TableTableRowElement getRowElement() {
        return theOasisRow;
    }

    /**
     * Obtain the row style name.
     * @return the row style name
     */
    protected String getRowStyle() {
        return theOasisRow.getTableStyleNameAttribute();
    }

    /**
     * Is the row hidden?
     * @return true/false
     */
    protected boolean isHidden() {
        String myString = theOasisRow.getTableVisibilityAttribute();
        return (myString == null)
                ? false
                : myString.equals(TableVisibilityAttribute.Value.COLLAPSE.toString());
    }

    /**
     * Set the row style.
     * @param pStyle the row style
     */
    protected void setRowStyle(final String pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the row style */
            theOasisRow.setTableStyleNameAttribute(pStyle);
        }
    }

    /**
     * Set the hidden property.
     * @param isHidden true/false
     */
    protected void setHidden(final boolean isHidden) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the visibility attribute */
            theOasisRow.setTableVisibilityAttribute(isHidden
                    ? TableVisibilityAttribute.Value.COLLAPSE.toString()
                    : TableVisibilityAttribute.Value.VISIBLE.toString());
        }
    }

    @Override
    public int getCellCount() {
        return theCellMap.getCellCount();
    }

    @Override
    public OasisCell getReadOnlyCellByIndex(final int pIndex) {
        return theCellMap.getReadOnlyCellByIndex(pIndex);
    }

    @Override
    public OasisCell getMutableCellByIndex(final int pIndex) {
        return (isReadOnly)
                ? null
                : theCellMap.getMutableCellByIndex(pIndex);
    }

    /**
     * Add extra columns to row.
     * @param pNumNewCols the number of new columns to add
     */
    protected void addColumnsToRow(final int pNumNewCols) {
        /* Pass call to CellMap */
        theCellMap.addAdditionalCells(pNumNewCols);
    }

    /**
     * Format object value.
     * @param pValue the value
     * @return the formatted value
     */
    protected String formatValue(final Object pValue) {
        JDataFormatter myFormatter = theRowMap.getFormatter();
        return myFormatter.formatObject(pValue);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     */
    protected <T> T parseValue(final String pSource,
                               final Class<T> pClass) {
        JDataFormatter myFormatter = theRowMap.getFormatter();
        return myFormatter.parseValue(pSource, pClass);
    }
}
