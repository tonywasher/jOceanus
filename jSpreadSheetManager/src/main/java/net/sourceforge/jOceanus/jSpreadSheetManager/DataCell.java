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

import net.sourceforge.jOceanus.jDataManager.DataConverter;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;

/**
 * Class representing a cell within a sheet or a view.
 */
public abstract class DataCell {
    /**
     * The underlying row.
     */
    private final DataRow theRow;

    /**
     * The underlying view.
     */
    private final DataView theView;

    /**
     * The position of the cell.
     */
    private final CellPosition thePosition;

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public DataSheet getSheet() {
        return theRow.getSheet();
    }

    /**
     * Obtain the underlying row.
     * @return the underlying row
     */
    public DataRow getRow() {
        return theRow;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public DataView getView() {
        return theView;
    }

    /**
     * Obtain the cell position.
     * @return position
     */
    public CellPosition getPosition() {
        return thePosition;
    }

    /**
     * Obtain the cell index.
     * @return the index
     */
    public int getCellIndex() {
        return thePosition.getColumnIndex();
    }

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pColIndex the column index
     */
    protected DataCell(final DataRow pRow,
                       final int pColIndex) {
        /* Store parameters */
        theRow = pRow;
        theView = pRow.getView();
        thePosition = new CellPosition(pColIndex, pRow.getRowIndex());
    }

    /**
     * Obtain boolean value of the cell.
     * @return the boolean value
     */
    public abstract Boolean getBooleanValue();

    /**
     * Obtain date value of the cell.
     * @return the date value
     */
    public abstract Date getDateValue();

    /**
     * Obtain integer value of the cell.
     * @return the integer value
     */
    public abstract Integer getIntegerValue();

    /**
     * Obtain string value of the cell.
     * @return the string value
     */
    public abstract String getStringValue();

    /**
     * Obtain byte array value of the cell.
     * @return the byte array value
     * @throws JDataException on error
     */
    public byte[] getBytesValue() throws JDataException {
        String myValue = getStringValue();
        return (myValue == null)
                ? null
                : DataConverter.hexStringToBytes(myValue);
    }

    /**
     * Obtain char array value of the cell.
     * @return the char array value
     * @throws JDataException on error
     */
    public char[] getCharArrayValue() throws JDataException {
        byte[] myValue = getBytesValue();
        return (myValue == null)
                ? null
                : DataConverter.bytesToCharArray(myValue);
    }

    /**
     * Set null value for the cell.
     * @throws JDataException on error
     */
    public abstract void setNullValue() throws JDataException;

    /**
     * Set boolean value of the cell.
     * @param pValue the integer value
     * @throws JDataException on error
     */
    public void setBooleanValue(final Boolean pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setBoolean(pValue);
        }
    }

    /**
     * Set non-null boolean value of the cell.
     * @param pValue the integer value
     * @throws JDataException on error
     */
    protected abstract void setBoolean(final Boolean pValue) throws JDataException;

    /**
     * Set date value of the cell.
     * @param pValue the date value
     * @throws JDataException on error
     */
    public void setDateValue(final Date pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setDate(pValue);
        }
    }

    /**
     * Set non-null date value of the cell.
     * @param pValue the integer value
     * @throws JDataException on error
     */
    protected abstract void setDate(final Date pValue) throws JDataException;

    /**
     * Set integer value of the cell.
     * @param pValue the integer value
     * @throws JDataException on error
     */
    public void setIntegerValue(final Integer pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setInteger(pValue);
        }
    }

    /**
     * Set non-null integer value of the cell.
     * @param pValue the integer value
     * @throws JDataException on error
     */
    protected abstract void setInteger(final Integer pValue) throws JDataException;

    /**
     * Set string value of the cell.
     * @param pValue the string value
     * @throws JDataException on error
     */
    public void setStringValue(final String pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setString(pValue);
        }
    }

    /**
     * Set non-null string value of the cell.
     * @param pValue the string value
     * @throws JDataException on error
     */
    protected abstract void setString(final String pValue) throws JDataException;

    /**
     * Set decimal value of the cell.
     * @param pValue the decimal value
     * @throws JDataException on error
     */
    public void setDecimalValue(final JDecimal pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setDecimal(pValue);
        }
    }

    /**
     * Set non-null decimal value of the cell.
     * @param pValue the decimal value
     * @throws JDataException on error
     */
    protected abstract void setDecimal(final JDecimal pValue) throws JDataException;

    /**
     * Set header value of the cell.
     * @param pValue the string value
     * @throws JDataException on error
     */
    public void setHeaderValue(final String pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setHeader(pValue);
        }
    }

    /**
     * Set non-null header value of the cell.
     * @param pValue the header value
     * @throws JDataException on error
     */
    protected abstract void setHeader(final String pValue) throws JDataException;

    /**
     * Set byte array value of the cell.
     * @param pValue the byte array value
     * @throws JDataException on error
     */
    public void setBytesValue(final byte[] pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Convert value to string */
            setStringValue(DataConverter.bytesToHexString(pValue));
        }
    }

    /**
     * Set char array value of the cell.
     * @param pValue the byte array value
     * @throws JDataException on error
     */
    public void setCharArrayValue(final char[] pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Convert value to string */
            setBytesValue(DataConverter.charsToByteArray(pValue));
        }
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the value
     * @return the required CellStyle
     */
    protected static CellStyleType getCellStyle(final JDecimal pValue) {
        if (pValue instanceof JPrice) {
            return CellStyleType.Price;
        }
        if (pValue instanceof JMoney) {
            return CellStyleType.Money;
        }
        if (pValue instanceof JUnits) {
            return CellStyleType.Units;
        }
        if (pValue instanceof JRate) {
            return CellStyleType.Rate;
        }
        if (pValue instanceof JDilution) {
            return CellStyleType.Dilution;
        }
        return null;
    }
}
