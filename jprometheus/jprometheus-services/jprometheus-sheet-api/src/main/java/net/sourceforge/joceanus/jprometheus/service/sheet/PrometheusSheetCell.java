/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.service.sheet;

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
public abstract class PrometheusSheetCell {
    /**
     * The underlying row.
     */
    private final PrometheusSheetRow theRow;

    /**
     * The underlying view.
     */
    private final PrometheusSheetView theView;

    /**
     * The position of the cell.
     */
    private final PrometheusSheetCellPosition thePosition;

    /**
     * Is the cell readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pColIndex the column index
     * @param pReadOnly is the cell readOnly?
     */
    protected PrometheusSheetCell(final PrometheusSheetRow pRow,
                                  final int pColIndex,
                                  final boolean pReadOnly) {
        /* Store parameters */
        theRow = pRow;
        theView = pRow.getView();
        thePosition = new PrometheusSheetCellPosition(pColIndex, pRow.getRowIndex());
        isReadOnly = pReadOnly;
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public PrometheusSheetSheet getSheet() {
        return theRow.getSheet();
    }

    /**
     * Obtain the underlying row.
     * @return the underlying row
     */
    public PrometheusSheetRow getRow() {
        return theRow;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public PrometheusSheetView getView() {
        return theView;
    }

    /**
     * Obtain the cell position.
     * @return position
     */
    public PrometheusSheetCellPosition getPosition() {
        return thePosition;
    }

    /**
     * Is the cell readOnly?
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
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
    public abstract Boolean getBoolean();

    /**
     * Obtain date value of the cell.
     * @return the date value
     * @throws OceanusException on error
     */
    public abstract TethysDate getDate() throws OceanusException;

    /**
     * Obtain integer value of the cell.
     * @return the integer value
     * @throws OceanusException on error
     */
    public abstract Integer getInteger() throws OceanusException;

    /**
     * Obtain long value of the cell.
     * @return the long value
     * @throws OceanusException on error
     */
    public abstract Long getLong() throws OceanusException;

    /**
     * Obtain money value of the cell.
     * @return the money value
     * @throws OceanusException on error
     */
    public abstract TethysMoney getMoney() throws OceanusException;

    /**
     * Obtain price value of the cell.
     * @return the price value
     * @throws OceanusException on error
     */
    public abstract TethysPrice getPrice() throws OceanusException;

    /**
     * Obtain rate value of the cell.
     * @return the rate value
     * @throws OceanusException on error
     */
    public abstract TethysRate getRate() throws OceanusException;

    /**
     * Obtain units value of the cell.
     * @return the units value
     * @throws OceanusException on error
     */
    public abstract TethysUnits getUnits() throws OceanusException;

    /**
     * Obtain dilution value of the cell.
     * @return the dilution value
     * @throws OceanusException on error
     */
    public abstract TethysDilution getDilution() throws OceanusException;

    /**
     * Obtain ratio value of the cell.
     * @return the ratio value
     * @throws OceanusException on error
     */
    public abstract TethysRatio getRatio() throws OceanusException;

    /**
     * Obtain string value of the cell.
     * @return the string value
     */
    public abstract String getString();

    /**
     * Obtain byte array value of the cell.
     * @return the byte array value
     */
    public byte[] getBytes() {
        final String myValue = getString();
        return myValue == null
                               ? null
                               : TethysDataConverter.base64ToByteArray(myValue);
    }

    /**
     * Obtain char array value of the cell.
     * @return the char array value
     * @throws OceanusException on error
     */
    public char[] getCharArray() throws OceanusException {
        final byte[] myValue = getBytes();
        return myValue == null
                               ? null
                               : TethysDataConverter.bytesToCharArray(myValue);
    }

    /**
     * Set null value for the cell.
     * @throws OceanusException on error
     */
    public void setNull() throws OceanusException {
        /* Ignore readOnly */
        if (!isReadOnly) {
            setNullValue();
        }
    }

    /**
     * Set null value for the cell.
     * @throws OceanusException on error
     */
    protected abstract void setNullValue() throws OceanusException;

    /**
     * Set boolean value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    public void setBoolean(final Boolean pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

         /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setBooleanValue(pValue);
        }
    }

    /**
     * Set non-null boolean value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    protected abstract void setBooleanValue(Boolean pValue) throws OceanusException;

    /**
     * Set date value of the cell.
     * @param pValue the date value
     * @throws OceanusException on error
     */
    public void setDate(final TethysDate pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setDateValue(pValue);
        }
    }

    /**
     * Set non-null date value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    protected abstract void setDateValue(TethysDate pValue) throws OceanusException;

    /**
     * Set integer value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    public void setInteger(final Integer pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setIntegerValue(pValue);
        }
    }

    /**
     * Set non-null integer value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    protected abstract void setIntegerValue(Integer pValue) throws OceanusException;

    /**
     * Set long value of the cell.
     * @param pValue the integer value
     * @throws OceanusException on error
     */
    public void setLong(final Long pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setLongValue(pValue);
        }
    }

    /**
     * Set non-null long value of the cell.
     * @param pValue the long value
     * @throws OceanusException on error
     */
    protected abstract void setLongValue(Long pValue) throws OceanusException;

    /**
     * Set string value of the cell.
     * @param pValue the string value
     * @throws OceanusException on error
     */
    public void setString(final String pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setStringValue(pValue);
        }
    }

    /**
     * Set non-null string value of the cell.
     * @param pValue the string value
     * @throws OceanusException on error
     */
    protected abstract void setStringValue(String pValue) throws OceanusException;

    /**
     * Set decimal value of the cell.
     * @param pValue the decimal value
     * @throws OceanusException on error
     */
    public void setDecimal(final TethysDecimal pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setDecimalValue(pValue);
        }
    }

    /**
     * Set non-null decimal value of the cell.
     * @param pValue the decimal value
     * @throws OceanusException on error
     */
    protected abstract void setDecimalValue(TethysDecimal pValue) throws OceanusException;

    /**
     * Set monetary value of the cell.
     * @param pValue the monetary value
     * @throws OceanusException on error
     */
    public void setMonetary(final TethysMoney pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setMonetaryValue(pValue);
        }
    }

    /**
     * Set non-null monetary value of the cell.
     * @param pValue the monetary value
     * @throws OceanusException on error
     */
    protected abstract void setMonetaryValue(TethysMoney pValue) throws OceanusException;

    /**
     * Set header value of the cell.
     * @param pValue the string value
     * @throws OceanusException on error
     */
    public void setHeader(final String pValue) throws OceanusException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set value */
            setHeaderValue(pValue);
        }
    }

    /**
     * Set non-null header value of the cell.
     * @param pValue the header value
     * @throws OceanusException on error
     */
    protected abstract void setHeaderValue(String pValue) throws OceanusException;

    /**
     * Set byte array value of the cell.
     * @param pValue the byte array value
     * @throws OceanusException on error
     */
    public void setBytes(final byte[] pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            setStringValue(TethysDataConverter.byteArrayToBase64(pValue));
        }
    }

    /**
     * Set char array value of the cell.
     * @param pValue the byte array value
     * @throws OceanusException on error
     */
    public void setCharArray(final char[] pValue) throws OceanusException {
        /* Ignore readOnly */
        if (isReadOnly) {
            return;
        }

        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            final byte[] myBytes = TethysDataConverter.charsToByteArray(pValue);
            setStringValue(TethysDataConverter.byteArrayToBase64(myBytes));
        }
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the value
     * @return the required CellStyle
     */
    protected static PrometheusSheetCellStyleType getCellStyle(final Object pValue) {
        if (pValue instanceof TethysPrice) {
            return PrometheusSheetCellStyleType.PRICE;
        }
        if (pValue instanceof TethysMoney) {
            return PrometheusSheetCellStyleType.MONEY;
        }
        if (pValue instanceof TethysUnits) {
            return PrometheusSheetCellStyleType.UNITS;
        }
        if (pValue instanceof TethysRate) {
            return PrometheusSheetCellStyleType.RATE;
        }
        if (pValue instanceof TethysDilution) {
            return PrometheusSheetCellStyleType.DILUTION;
        }
        if (pValue instanceof TethysRatio) {
            return PrometheusSheetCellStyleType.RATIO;
        }
        if (pValue instanceof Boolean) {
            return PrometheusSheetCellStyleType.BOOLEAN;
        }
        if (pValue instanceof Number) {
            return PrometheusSheetCellStyleType.INTEGER;
        }
        if (pValue instanceof TethysDate) {
            return PrometheusSheetCellStyleType.DATE;
        }
        if (pValue instanceof String) {
            return PrometheusSheetCellStyleType.STRING;
        }
        return null;
    }
}