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

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Class representing a cell within a sheet or a view.
 */
public abstract class MetisDataCell {
    /**
     * The underlying row.
     */
    private final MetisDataRow theRow;

    /**
     * The underlying view.
     */
    private final MetisDataView theView;

    /**
     * The position of the cell.
     */
    private final MetisCellPosition thePosition;

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pColIndex the column index
     */
    protected MetisDataCell(final MetisDataRow pRow,
                            final int pColIndex) {
        /* Store parameters */
        theRow = pRow;
        theView = pRow.getView();
        thePosition = new MetisCellPosition(pColIndex, pRow.getRowIndex());
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public MetisDataSheet getSheet() {
        return theRow.getSheet();
    }

    /**
     * Obtain the underlying row.
     * @return the underlying row
     */
    public MetisDataRow getRow() {
        return theRow;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public MetisDataView getView() {
        return theView;
    }

    /**
     * Obtain the cell position.
     * @return position
     */
    public MetisCellPosition getPosition() {
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
     * Obtain boolean value of the cell.
     * @return the boolean value
     */
    public abstract Boolean getBooleanValue();

    /**
     * Obtain date value of the cell.
     * @return the date value
     * @throws OceanusException on error
     */
    public abstract TethysDate getDateValue() throws OceanusException;

    /**
     * Obtain integer value of the cell.
     * @return the integer value
     * @throws OceanusException on error
     */
    public abstract Integer getIntegerValue() throws OceanusException;

    /**
     * Obtain long value of the cell.
     * @return the long value
     * @throws OceanusException on error
     */
    public abstract Long getLongValue() throws OceanusException;

    /**
     * Obtain money value of the cell.
     * @return the money value
     * @throws OceanusException on error
     */
    public abstract TethysMoney getMoneyValue() throws OceanusException;

    /**
     * Obtain price value of the cell.
     * @return the price value
     * @throws OceanusException on error
     */
    public abstract TethysPrice getPriceValue() throws OceanusException;

    /**
     * Obtain rate value of the cell.
     * @return the rate value
     * @throws OceanusException on error
     */
    public abstract TethysRate getRateValue() throws OceanusException;

    /**
     * Obtain units value of the cell.
     * @return the units value
     * @throws OceanusException on error
     */
    public abstract TethysUnits getUnitsValue() throws OceanusException;

    /**
     * Obtain dilution value of the cell.
     * @return the dilution value
     * @throws OceanusException on error
     */
    public abstract TethysDilution getDilutionValue() throws OceanusException;

    /**
     * Obtain ratio value of the cell.
     * @return the ratio value
     * @throws OceanusException on error
     */
    public abstract TethysRatio getRatioValue() throws OceanusException;

    /**
     * Obtain string value of the cell.
     * @return the string value
     */
    public abstract String getStringValue();

    /**
     * Obtain byte array value of the cell.
     * @return the byte array value
     * @throws OceanusException on error
     */
    public byte[] getBytesValue() throws OceanusException {
        String myValue = getStringValue();
        return (myValue == null)
                                 ? null
                                 : TethysDataConverter.base64ToByteArray(myValue);
    }

    /**
     * Obtain char array value of the cell.
     * @return the char array value
     * @throws OceanusException on error
     */
    public char[] getCharArrayValue() throws OceanusException {
        byte[] myValue = getBytesValue();
        return (myValue == null)
                                 ? null
                                 : TethysDataConverter.bytesToCharArray(myValue);
    }

    /**
     * Set null value for the cell.
     * @throws OceanusException on error
     */
    public abstract void setNullValue() throws OceanusException;

    /**
     * Set boolean value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    public void setBooleanValue(final Boolean pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    protected abstract void setBoolean(Boolean pValue) throws OceanusException;

    /**
     * Set non-null date value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    protected abstract void setDate(TethysDate pValue) throws OceanusException;

    /**
     * Set date value of the cell.
     * @param pValue the date value
     * @throws OceanusException on error
     */
    public void setDateValue(final TethysDate pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    public void setIntegerValue(final Integer pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    protected abstract void setInteger(Integer pValue) throws OceanusException;

    /**
     * Set long value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    public void setLongValue(final Long pValue) throws OceanusException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setLong(pValue);
        }
    }

    /**
     * Set non-null long value of the cell.
     * @param pValue the long value
     * @throws OceanusException on error
     */
    protected abstract void setLong(Long pValue) throws OceanusException;

    /**
     * Set string value of the cell.
     * @param pValue the string value
     * @throws OceanusException on error
     */
    public void setStringValue(final String pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    protected abstract void setString(String pValue) throws OceanusException;

    /**
     * Set decimal value of the cell.
     * @param pValue the decimal value
     * @throws OceanusException on error
     */
    public void setDecimalValue(final TethysDecimal pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    protected abstract void setDecimal(TethysDecimal pValue) throws OceanusException;

    /**
     * Set monetary value of the cell.
     * @param pValue the monetary value
     * @throws OceanusException on error
     */
    public void setMonetaryValue(final TethysMoney pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    protected abstract void setMonetary(TethysMoney pValue) throws OceanusException;

    /**
     * Set header value of the cell.
     * @param pValue the string value
     * @throws OceanusException on error
     */
    public void setHeaderValue(final String pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    protected abstract void setHeader(String pValue) throws OceanusException;

    /**
     * Set byte array value of the cell.
     * @param pValue the byte array value
     * @throws OceanusException on error
     */
    public void setBytesValue(final byte[] pValue) throws OceanusException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Convert value to string */
            setStringValue(TethysDataConverter.byteArrayToBase64(pValue));
        }
    }

    /**
     * Set char array value of the cell.
     * @param pValue the byte array value
     * @throws OceanusException on error
     */
    public void setCharArrayValue(final char[] pValue) throws OceanusException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Convert value to string */
            setBytesValue(TethysDataConverter.charsToByteArray(pValue));
        }
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the value
     * @return the required CellStyle
     */
    protected static MetisCellStyleType getCellStyle(final TethysDecimal pValue) {
        if (pValue instanceof TethysPrice) {
            return MetisCellStyleType.PRICE;
        }
        if (pValue instanceof TethysMoney) {
            return MetisCellStyleType.MONEY;
        }
        if (pValue instanceof TethysUnits) {
            return MetisCellStyleType.UNITS;
        }
        if (pValue instanceof TethysRate) {
            return MetisCellStyleType.RATE;
        }
        if (pValue instanceof TethysDilution) {
            return MetisCellStyleType.DILUTION;
        }
        if (pValue instanceof TethysRatio) {
            return MetisCellStyleType.RATIO;
        }
        return null;
    }
}
