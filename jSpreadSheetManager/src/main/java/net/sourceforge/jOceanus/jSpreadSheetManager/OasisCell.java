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

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;

import org.odftoolkit.simple.table.Cell;

/**
 * Class representing a cell within a sheet or a view.
 */
public class OasisCell
        extends DataCell {
    /**
     * The underlying row.
     */
    private final OasisRow theOasisRow;

    /**
     * The Oasis Cell.
     */
    private final Cell theOasisCell;

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pOasisCell the Oasis Cell
     */
    protected OasisCell(final OasisRow pRow,
                        final Cell pOasisCell,
                        final int pColIndex) {
        /* Store parameters */
        super(pRow, pColIndex);
        theOasisRow = pRow;
        theOasisCell = pOasisCell;
    }

    @Override
    public Boolean getBooleanValue() {
        return theOasisCell.getBooleanValue();
    }

    @Override
    public Date getDateValue() {
        Calendar myCalendar = theOasisCell.getDateValue();
        return (myCalendar == null) ? null : myCalendar.getTime();
    }

    @Override
    public Integer getIntegerValue() {
        Double myValue = theOasisCell.getDoubleValue();
        return (myValue == null) ? null : myValue.intValue();
    }

    @Override
    public String getStringValue() {
        if (theOasisCell.getValueType().equals("percentage")) {
            return theOasisCell.getPercentageValue().toString();
        }
        return theOasisCell.getDisplayText();
    }

    @Override
    public void setNullValue() throws JDataException {
        theOasisCell.removeContent();
    }

    @Override
    protected void setBoolean(final Boolean pValue) throws JDataException {
        /* Set the value */
        theOasisCell.setBooleanValue(pValue);

        /* Set the style for the cell */
        theOasisRow.setCellStyle(this, CellStyleType.Boolean);
    }

    @Override
    protected void setDate(final Date pValue) throws JDataException {
        /* Set the value */
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTime(pValue);
        theOasisCell.setDateValue(myCalendar);

        /* Set the style for the cell */
        theOasisRow.setCellStyle(this, CellStyleType.Date);
    }

    @Override
    protected void setInteger(final Integer pValue) throws JDataException {
        /* Set the value */
        theOasisCell.setDoubleValue(pValue.doubleValue());

        /* Set the style for the cell */
        theOasisRow.setCellStyle(this, CellStyleType.Integer);
    }

    @Override
    protected void setString(final String pValue) throws JDataException {
        /* Set the value */
        theOasisCell.setStringValue(pValue);

        /* Set the style for the cell */
        theOasisRow.setCellStyle(this, CellStyleType.String);
    }

    @Override
    protected void setHeader(final String pValue) throws JDataException {
        /* Set the value */
        theOasisCell.setStringValue(pValue);

        /* Set the style for the cell */
        theOasisRow.setCellStyle(this, CellStyleType.Header);
    }

    @Override
    protected void setDecimal(final JDecimal pValue) throws JDataException {
        /* Set the value */
        if (pValue instanceof JRate) {
            theOasisCell.setPercentageValue(pValue.doubleValue());
        } else {
            theOasisCell.setDoubleValue(pValue.doubleValue());
        }

        /* Set the style for the cell */
        theOasisRow.setCellStyle(this, getCellStyle(pValue));
    }

    /**
     * Set cell style.
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final String pStyle) {
        theOasisCell.getOdfElement().setTableStyleNameAttribute(pStyle);
    }
}
