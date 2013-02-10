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

import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;

/**
 * Class representing a column in Oasis.
 * @author Tony Washer
 */
public class OasisColumn {
    /**
     * The list of columns.
     */
    private final OasisColumnMap theColumnMap;

    /**
     * The underlying ODFDOM column.
     */
    private TableTableColumnElement theOasisColumn;

    /**
     * The previous column.
     */
    private final OasisColumn thePreviousColumn;

    /**
     * The underlying ODFDOM column.
     */
    private OasisColumn theNextColumn;

    /**
     * The column index.
     */
    private final int theColIndex;

    /**
     * The repeat index of the column
     */
    private int theColInstance;

    /**
     * Constructor.
     * @param pMap the column map
     * @param pPrevious the previous column.
     * @param pColumn the Oasis column
     * @param pIndex the index
     * @param pInstance the repeat instance
     */
    protected OasisColumn(final OasisColumnMap pMap,
                          final OasisColumn pPrevious,
                          final TableTableColumnElement pColumn,
                          final int pIndex,
                          final int pInstance) {
        /* Store parameters */
        theColumnMap = pMap;
        thePreviousColumn = pPrevious;
        theNextColumn = null;
        theOasisColumn = pColumn;
        theColIndex = pIndex;
        theColInstance = pInstance;

        /* If we have a previous column */
        if (thePreviousColumn != null) {
            /* Link it */
            thePreviousColumn.theNextColumn = this;
        }
    }

    /**
     * Obtain the underlying table element.
     * @return the element
     */
    protected TableTableColumnElement getColumnElement() {
        return theOasisColumn;
    }

    /**
     * Set the underlying table element.
     * @param pElement the element
     */
    protected void setColumnElement(final TableTableColumnElement pElement) {
        theOasisColumn = pElement;
    }

    /**
     * Obtain the next column.
     * @return the next column
     */
    protected OasisColumn getNextColumn() {
        return theNextColumn;
    }

    /**
     * Obtain the previous column.
     * @return the previous column
     */
    protected OasisColumn getPreviousColumn() {
        return thePreviousColumn;
    }

    /**
     * Obtain the index of the column.
     * @return the index
     */
    protected int getIndex() {
        return theColIndex;
    }

    /**
     * Obtain the instance of the column.
     * @return the instance
     */
    protected int getInstance() {
        return theColInstance;
    }

    /**
     * Set the instance of the column.
     * @param pInstance the instance
     */
    protected void setInstance(final int pInstance) {
        theColInstance = pInstance;
    }

    /**
     * Is the column a virtual column.
     * @return true/false
     */
    protected boolean isVirtual() {
        return (theColInstance != 0);
    }

    /**
     * Is the column the final column in the sequence.
     * @return true/false
     */
    protected boolean isLastCol() {
        return (theColInstance + 1 == getRepeatCount());
    }

    /**
     * Obtain the repeat count of the column.
     * @return true/false
     */
    protected Integer getRepeatCount() {
        Integer myCount = theOasisColumn.getTableNumberColumnsRepeatedAttribute();
        return (myCount != null)
                ? myCount
                : null;
    }

    /**
     * Obtain the column style name
     * @return the column style name
     */
    protected String getColumnStyle() {
        return theOasisColumn.getTableStyleNameAttribute();
    }

    /**
     * Obtain the default cell style name
     * @return the column style name
     */
    protected String getDefaultCellStyle() {
        return theOasisColumn.getTableDefaultCellStyleNameAttribute();
    }

    /**
     * Is the column hidden?
     * @return true/false
     */
    protected boolean isHidden() {
        String myString = theOasisColumn.getTableVisibilityAttribute();
        return (myString == null)
                ? false
                : myString.equals(TableVisibilityAttribute.Value.COLLAPSE.toString());
    }

    /**
     * Set the column style
     * @param pStyle the column style
     */
    protected void setColumnStyle(final String pStyle) {
        /* ensure that the column is individual */
        ensureIndividual();

        /* Set the column style */
        theOasisColumn.setTableStyleNameAttribute(pStyle);
    }

    /**
     * Set the default column style
     * @param pStyle the default column style
     */
    protected void setDefaultCellStyle(final String pStyle) {
        /* ensure that the column is individual */
        ensureIndividual();

        /* Set the default cell style */
        theOasisColumn.setTableDefaultCellStyleNameAttribute(pStyle);
    }

    /**
     * Set the hidden property
     * @param isHidden true/false
     */
    protected void setHidden(final boolean isHidden) {
        /* ensure that the column is individual */
        ensureIndividual();

        /* Set the visibility attribute */
        theOasisColumn.setTableVisibilityAttribute(isHidden
                ? TableVisibilityAttribute.Value.COLLAPSE.toString()
                : TableVisibilityAttribute.Value.VISIBLE.toString());
    }

    /**
     * Ensure that the column is an individual column.
     */
    private void ensureIndividual() {
        /* If the repeat count is greater than one */
        if (getRepeatCount() > 1) {
            /* We need to make this item an individual */
            theColumnMap.makeIndividual(this);
        }
    }
}
