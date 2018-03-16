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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.sheet;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.jopendocument.dom.ODValueType;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * JOpenDocument Cell.
 */
public class MetisJOpenCell
        extends MetisDataCell {
    /**
     * The underlying row.
     */
    private final MetisJOpenRow theJOpenRow;

    /**
     * The Excel Cell.
     */
    private final MutableCell<SpreadSheet> theJOpenCell;

    /**
     * Is the cell readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pJOpenCell the JOpen Cell
     * @param pColIndex the column index
     * @param pReadOnly is the cell readOnly?
     */
    protected MetisJOpenCell(final MetisJOpenRow pRow,
                             final MutableCell<SpreadSheet> pJOpenCell,
                             final int pColIndex,
                             final boolean pReadOnly) {
        /* Store parameters */
        super(pRow, pColIndex);
        theJOpenRow = pRow;
        theJOpenCell = pJOpenCell;
        isReadOnly = pReadOnly;
    }

    /**
     * Parse a value.
     * @param <T> the value type to parse
     * @param pSource the string to parse.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws OceanusException on error
     */
    private <T> T parseValue(final String pSource,
                             final Class<T> pClass) throws OceanusException {
        try {
            return theJOpenRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            final MetisOasisCellAddress myAddress = new MetisOasisCellAddress(theJOpenRow.getSheet().getName(), getPosition());
            throw new MetisDataException(pSource, "Bad Value at Cell "
                                                  + myAddress, e);
        }
    }

    @Override
    public Boolean getBooleanValue() {
        return theJOpenCell.isEmpty()
               || ODValueType.BOOLEAN != theJOpenCell.getValueType()
                                                                     ? null
                                                                     : (Boolean) theJOpenCell.getValue();
    }

    @Override
    public TethysDate getDateValue() {
        return theJOpenCell.isEmpty()
               || ODValueType.DATE != theJOpenCell.getValueType()
                                                                  ? null
                                                                  : new TethysDate((Date) theJOpenCell.getValue());
    }

    @Override
    public Integer getIntegerValue() {
        return theJOpenCell.isEmpty()
               || ODValueType.FLOAT != theJOpenCell.getValueType()
                                                                   ? null
                                                                   : ((BigDecimal) theJOpenCell.getValue()).intValue();
    }

    @Override
    public Long getLongValue() {
        return theJOpenCell.isEmpty()
               || ODValueType.FLOAT != theJOpenCell.getValueType()
                                                                   ? null
                                                                   : ((BigDecimal) theJOpenCell.getValue()).longValue();
    }

    @Override
    public String getStringValue() {
        return theJOpenCell.isEmpty()
                                      ? null
                                      : theJOpenCell.getTextValue();
    }

    @Override
    public TethysMoney getMoneyValue() throws OceanusException {
        return parseValue(getStringValue(), TethysMoney.class);
    }

    @Override
    public TethysPrice getPriceValue() throws OceanusException {
        return parseValue(getStringValue(), TethysPrice.class);
    }

    @Override
    public TethysRate getRateValue() throws OceanusException {
        return parseValue(getStringValue(), TethysRate.class);
    }

    @Override
    public TethysUnits getUnitsValue() throws OceanusException {
        return parseValue(getStringValue(), TethysUnits.class);
    }

    @Override
    public TethysDilution getDilutionValue() throws OceanusException {
        return parseValue(getStringValue(), TethysDilution.class);
    }

    @Override
    public TethysRatio getRatioValue() throws OceanusException {
        return parseValue(getStringValue(), TethysRatio.class);
    }

    @Override
    public void setNullValue() throws OceanusException {
        if (!isReadOnly) {
            theJOpenCell.clearValue();
        }
    }

    @Override
    protected void setBoolean(final Boolean pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theJOpenCell.setValue(pValue);

            /* Set the style for the cell */
            theJOpenRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setDate(final TethysDate pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theJOpenCell.setValue(pValue.toDate());

            /* Set the style for the cell */
            theJOpenRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setInteger(final Integer pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theJOpenCell.setValue(pValue.doubleValue());

            /* Set the style for the cell */
            theJOpenRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setLong(final Long pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theJOpenCell.setValue(pValue.doubleValue());

            /* Set the style for the cell */
            theJOpenRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setString(final String pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theJOpenCell.setValue(pValue);

            /* Set the style for the cell */
            theJOpenRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setHeader(final String pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set as string value */
            theJOpenCell.setValue(pValue);

            /* Adjust the style for the cell */
            theJOpenRow.setAlternateCellStyle(this, pValue);
        }
    }

    @Override
    protected void setDecimal(final TethysDecimal pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theJOpenCell.setValue(pValue.toBigDecimal());

            /* Set the style for the cell */
            theJOpenRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setMonetary(final TethysMoney pValue) throws OceanusException {
        /* Pass through as decimal */
        setDecimal(pValue);
    }

    /**
     * Set cell style.
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final HSSFCellStyle pStyle) {
        // theJOpenCell.setCellStyle(pStyle);
    }
}
