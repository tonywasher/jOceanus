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

import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;

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
    private TableTableRowElement theOasisRow;

    /**
     * The previous row.
     */
    private final OasisRow thePreviousRow;

    /**
     * The next row.
     */
    private OasisRow theNextRow;

    /**
     * The row index.
     */
    private final int theRowIndex;

    /**
     * The repeat index of the row.
     */
    private int theRowInstance;

    /**
     * Constructor.
     * @param pMap the row map
     * @param pPrevious the previous row.
     * @param pRow the Oasis row
     * @param pIndex the index
     * @param pInstance the repeat instance
     */
    protected OasisRow(final OasisRowMap pMap,
                       final OasisRow pPrevious,
                       final TableTableRowElement pRow,
                       final int pIndex,
                       final int pInstance) {
        /* Store parameters */
        super(pMap.getSheet(), pIndex);
        theRowMap = pMap;
        thePreviousRow = pPrevious;
        theNextRow = null;
        theOasisRow = pRow;
        theRowIndex = pIndex;
        theRowInstance = pInstance;

        /* Create the cell map */
        theCellMap = new OasisCellMap(this);

        /* If we have a previous column */
        if (thePreviousRow != null) {
            /* Link it */
            thePreviousRow.theNextRow = this;
        }
    }

    @Override
    public OasisSheet getSheet() {
        return (OasisSheet) super.getSheet();
    }

    /**
     * Obtain the underlying table element.
     * @return the element
     */
    protected TableTableRowElement getRowElement() {
        return theOasisRow;
    }

    /**
     * Set the underlying table element.
     * @param pElement the element
     */
    protected void setRowElement(final TableTableRowElement pElement) {
        theOasisRow = pElement;
    }

    @Override
    public OasisRow getNextRow() {
        return theNextRow;
    }

    @Override
    public OasisRow getPreviousRow() {
        return thePreviousRow;
    }

    /**
     * Obtain the index of the row.
     * @return the index
     */
    protected int getIndex() {
        return theRowIndex;
    }

    /**
     * Obtain the instance of the row.
     * @return the instance
     */
    protected int getInstance() {
        return theRowInstance;
    }

    /**
     * Set the instance of the row.
     * @param pInstance the instance
     */
    protected void setInstance(final int pInstance) {
        theRowInstance = pInstance;
    }

    /**
     * Is the row a virtual row?
     * @return true/false
     */
    protected boolean isVirtual() {
        return (theRowInstance != 0);
    }

    /**
     * Is the column the final row in the sequence.
     * @return true/false
     */
    protected boolean isLastRow() {
        return (theRowInstance + 1 == getRepeatCount());
    }

    /**
     * Obtain the repeat count of the row.
     * @return true/false
     */
    protected Integer getRepeatCount() {
        Integer myCount = theOasisRow.getTableNumberRowsRepeatedAttribute();
        return (myCount != null)
                ? myCount
                : null;
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
        /* ensure that the column is individual */
        ensureIndividual();

        /* Set the row style */
        theOasisRow.setTableStyleNameAttribute(pStyle);
    }

    /**
     * Set the hidden property.
     * @param isHidden true/false
     */
    protected void setHidden(final boolean isHidden) {
        /* ensure that the row is individual */
        ensureIndividual();

        /* Set the visibility attribute */
        theOasisRow.setTableVisibilityAttribute(isHidden
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
            theRowMap.makeIndividual(this);
        }
    }

    @Override
    public int getCellCount() {
        return theCellMap.getCellCount();
    }

    @Override
    public OasisCell getCellByIndex(final int pIndex) {
        return theCellMap.getCellByIndex(pIndex);
    }

    @Override
    public OasisCell createCellByIndex(final int pIndex) {
        return theCellMap.getCellByIndex(pIndex);
    }

    /**
     * Format object value.
     * @param pValue the value
     * @return the formatted value
     */
    protected String formatValue(final Object pValue) {
        JDataFormatter myFormatter = theRowMap.getFormatter();
        if (pValue instanceof JDecimal) {
            /* Format the decimal */
            JDecimal myDecimal = (JDecimal) pValue;
            return myFormatter.getDecimalFormatter().formatDecimal(myDecimal);
        }
        if (pValue instanceof Date) {
            /* Format the date */
            Date myDate = (Date) pValue;
            return myFormatter.getDateFormatter().formatDate(myDate);
        }
        return null;
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
        if (pClass == Date.class) {
            /* Parse the date */
            return pClass.cast(myFormatter.getDateFormatter().parseDate(pSource));
        }
        if (pClass == JPrice.class) {
            /* Parse the price */
            return pClass.cast(myFormatter.getDecimalParser().parsePriceValue(pSource));
        }
        if (pClass == JMoney.class) {
            /* Parse the money */
            return pClass.cast(myFormatter.getDecimalParser().parseMoneyValue(pSource));
        }
        if (pClass == JRate.class) {
            /* Parse the rate */
            return pClass.cast(myFormatter.getDecimalParser().parseRateValue(pSource));
        }
        if (pClass == JUnits.class) {
            /* Parse the units */
            return pClass.cast(myFormatter.getDecimalParser().parseUnitsValue(pSource));
        }
        if (pClass == JDilution.class) {
            /* Parse the dilution */
            return pClass.cast(myFormatter.getDecimalParser().parseDilutionValue(pSource));
        }
        return null;
    }
}
