/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jspreadsheetmanager;

import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * @throws JOceanusException on error
     */
    public abstract JDateDay getDateValue() throws JOceanusException;

    /**
     * Obtain integer value of the cell.
     * @return the integer value
     * @throws JOceanusException on error
     */
    public abstract Integer getIntegerValue() throws JOceanusException;

    /**
     * Obtain money value of the cell.
     * @return the money value
     * @throws JOceanusException on error
     */
    public abstract JMoney getMoneyValue() throws JOceanusException;

    /**
     * Obtain price value of the cell.
     * @return the price value
     * @throws JOceanusException on error
     */
    public abstract JPrice getPriceValue() throws JOceanusException;

    /**
     * Obtain rate value of the cell.
     * @return the rate value
     * @throws JOceanusException on error
     */
    public abstract JRate getRateValue() throws JOceanusException;

    /**
     * Obtain units value of the cell.
     * @return the units value
     * @throws JOceanusException on error
     */
    public abstract JUnits getUnitsValue() throws JOceanusException;

    /**
     * Obtain dilution value of the cell.
     * @return the dilution value
     * @throws JOceanusException on error
     */
    public abstract JDilution getDilutionValue() throws JOceanusException;

    /**
     * Obtain ratio value of the cell.
     * @return the ratio value
     * @throws JOceanusException on error
     */
    public abstract JRatio getRatioValue() throws JOceanusException;

    /**
     * Obtain string value of the cell.
     * @return the string value
     */
    public abstract String getStringValue();

    /**
     * Obtain byte array value of the cell.
     * @return the byte array value
     * @throws JOceanusException on error
     */
    public byte[] getBytesValue() throws JOceanusException {
        String myValue = getStringValue();
        return (myValue == null)
                ? null
                : DataConverter.base64ToByteArray(myValue);
    }

    /**
     * Obtain char array value of the cell.
     * @return the char array value
     * @throws JOceanusException on error
     */
    public char[] getCharArrayValue() throws JOceanusException {
        byte[] myValue = getBytesValue();
        return (myValue == null)
                ? null
                : DataConverter.bytesToCharArray(myValue);
    }

    /**
     * Set null value for the cell.
     * @throws JOceanusException on error
     */
    public abstract void setNullValue() throws JOceanusException;

    /**
     * Set boolean value of the cell.
     * @param pValue the integer value
     * @throws JOceanusException on error
     */
    public void setBooleanValue(final Boolean pValue) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected abstract void setBoolean(final Boolean pValue) throws JOceanusException;

    /**
     * Set non-null date value of the cell.
     * @param pValue the integer value
     * @throws JOceanusException on error
     */
    protected abstract void setDate(final JDateDay pValue) throws JOceanusException;

    /**
     * Set date value of the cell.
     * @param pValue the date value
     * @throws JOceanusException on error
     */
    public void setDateValue(final JDateDay pValue) throws JOceanusException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setDate(pValue);
        }
    }

    /**
     * Set integer value of the cell.
     * @param pValue the integer value
     * @throws JOceanusException on error
     */
    public void setIntegerValue(final Integer pValue) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected abstract void setInteger(final Integer pValue) throws JOceanusException;

    /**
     * Set string value of the cell.
     * @param pValue the string value
     * @throws JOceanusException on error
     */
    public void setStringValue(final String pValue) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected abstract void setString(final String pValue) throws JOceanusException;

    /**
     * Set decimal value of the cell.
     * @param pValue the decimal value
     * @throws JOceanusException on error
     */
    public void setDecimalValue(final JDecimal pValue) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected abstract void setDecimal(final JDecimal pValue) throws JOceanusException;

    /**
     * Set monetary value of the cell.
     * @param pValue the monetary value
     * @throws JOceanusException on error
     */
    public void setMonetaryValue(final JMoney pValue) throws JOceanusException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setMonetary(pValue);
        }
    }

    /**
     * Set non-null monetary value of the cell.
     * @param pValue the monetary value
     * @throws JOceanusException on error
     */
    protected abstract void setMonetary(final JMoney pValue) throws JOceanusException;

    /**
     * Set header value of the cell.
     * @param pValue the string value
     * @throws JOceanusException on error
     */
    public void setHeaderValue(final String pValue) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected abstract void setHeader(final String pValue) throws JOceanusException;

    /**
     * Set byte array value of the cell.
     * @param pValue the byte array value
     * @throws JOceanusException on error
     */
    public void setBytesValue(final byte[] pValue) throws JOceanusException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Convert value to string */
            setStringValue(DataConverter.byteArrayToBase64(pValue));
        }
    }

    /**
     * Set char array value of the cell.
     * @param pValue the byte array value
     * @throws JOceanusException on error
     */
    public void setCharArrayValue(final char[] pValue) throws JOceanusException {
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
            return CellStyleType.PRICE;
        }
        if (pValue instanceof JMoney) {
            return CellStyleType.MONEY;
        }
        if (pValue instanceof JUnits) {
            return CellStyleType.UNITS;
        }
        if (pValue instanceof JRate) {
            return CellStyleType.RATE;
        }
        if (pValue instanceof JDilution) {
            return CellStyleType.DILUTION;
        }
        if (pValue instanceof JRatio) {
            return CellStyleType.RATIO;
        }
        return null;
    }
}
