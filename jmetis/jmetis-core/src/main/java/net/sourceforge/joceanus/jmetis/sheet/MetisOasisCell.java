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

import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute.Value;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.w3c.dom.Node;

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
 * Class representing a cell in Oasis.
 * @author Tony Washer
 */
public class MetisOasisCell
        extends MetisDataCell {
    /**
     * The bad value error text.
     */
    private static final String ERROR_VALUE = "Bad value at cell ";

    /**
     * The list of cells.
     */
    private final MetisOasisCellMap theCellMap;

    /**
     * The underlying ODFDOM cell.
     */
    private TableTableCellElement theOasisCell;

    /**
     * Is the row readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pMap the cell map
     * @param pCell the Oasis cell
     * @param pIndex the index
     * @param pReadOnly is the cell readOnly?
     */
    protected MetisOasisCell(final MetisOasisCellMap pMap,
                             final TableTableCellElement pCell,
                             final int pIndex,
                             final boolean pReadOnly) {
        /* Store parameters */
        super(pMap.getRow(), pIndex);
        theCellMap = pMap;
        theOasisCell = pCell;
        isReadOnly = pReadOnly;
    }

    /**
     * Obtain the next cell.
     * @return the next cell
     */
    protected MetisOasisCell getNextCell() {
        return theCellMap.getReadOnlyCellByIndex(getCellIndex() + 1);
    }

    /**
     * Obtain the previous cell.
     * @return the previous cell
     */
    protected MetisOasisCell getPreviousCell() {
        return theCellMap.getReadOnlyCellByIndex(getCellIndex() - 1);
    }

    /**
     * Obtain the row style name.
     * @return the row style name
     */
    protected String getCellStyle() {
        return theOasisCell.getTableStyleNameAttribute();
    }

    /**
     * Set the row style.
     * @param pStyle the row style
     */
    protected void setCellStyle(final String pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the cell style */
            theOasisCell.setTableStyleNameAttribute(pStyle);
        }
    }

    /**
     * Set the cell validation name.
     * @param pValidation the validation name
     */
    protected void setValidationName(final String pValidation) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the content validation name */
            theOasisCell.setTableContentValidationNameAttribute(pValidation);
        }
    }

    /**
     * Format a value.
     * @param pValue the value to format.
     * @return the formatted value
     */
    private String formatValue(final Object pValue) {
        MetisOasisRow myRow = theCellMap.getRow();
        return myRow.formatValue(pValue);
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
        MetisOasisRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            MetisOasisCellAddress myAddress = new MetisOasisCellAddress(myRow.getSheet().getName(), getPosition());
            throw new MetisDataException(pSource, ERROR_VALUE
                                                  + myAddress, e);
        }
    }

    /**
     * Parse a value.
     * @param <T> the value type to parse
     * @param pSource the double value.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws OceanusException on error
     */
    private <T> T parseValue(final Double pSource,
                             final Class<T> pClass) throws OceanusException {
        MetisOasisRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            MetisOasisCellAddress myAddress = new MetisOasisCellAddress(myRow.getSheet().getName(), getPosition());
            throw new MetisDataException(pSource, ERROR_VALUE
                                                  + myAddress, e);
        }
    }

    /**
     * Parse a value.
     * @param <T> the value type to parse
     * @param pSource the double value.
     * @param pCurrCode the currency code.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws OceanusException on error
     */
    private <T> T parseValue(final Double pSource,
                             final String pCurrCode,
                             final Class<T> pClass) throws OceanusException {
        MetisOasisRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pCurrCode, pClass);
        } catch (IllegalArgumentException e) {
            MetisOasisCellAddress myAddress = new MetisOasisCellAddress(myRow.getSheet().getName(), getPosition());
            throw new MetisDataException(pSource, ERROR_VALUE
                                                  + myAddress, e);
        }
    }

    /**
     * Obtain value attribute type.
     * @return the value attribute type
     */
    private Value getValueType() {
        String myType = theOasisCell.getOfficeValueTypeAttribute();
        return (myType != null)
                                ? OfficeValueTypeAttribute.Value.enumValueOf(myType)
                                : Value.VOID;
    }

    @Override
    public Boolean getBooleanValue() {
        return Value.BOOLEAN.equals(getValueType())
                                                    ? theOasisCell.getOfficeBooleanValueAttribute()
                                                    : null;
    }

    @Override
    public TethysDate getDateValue() throws OceanusException {
        return Value.DATE.equals(getValueType())
                                                 ? parseValue(theOasisCell.getOfficeDateValueAttribute(), TethysDate.class)
                                                 : null;
    }

    @Override
    public Integer getIntegerValue() throws OceanusException {
        return Value.FLOAT.equals(getValueType())
                                                  ? theOasisCell.getOfficeValueAttribute().intValue()
                                                  : null;
    }

    @Override
    public Long getLongValue() throws OceanusException {
        return Value.FLOAT.equals(getValueType())
                                                  ? theOasisCell.getOfficeValueAttribute().longValue()
                                                  : null;
    }

    @Override
    public TethysMoney getMoneyValue() throws OceanusException {
        switch (getValueType()) {
            case CURRENCY:
                return parseValue(theOasisCell.getOfficeValueAttribute(), theOasisCell.getOfficeCurrencyAttribute(), TethysMoney.class);
            case FLOAT:
                return parseValue(getTextContent(), TethysMoney.class);
            default:
                return null;
        }
    }

    @Override
    public TethysPrice getPriceValue() throws OceanusException {
        switch (getValueType()) {
            case CURRENCY:
                return parseValue(theOasisCell.getOfficeValueAttribute(), theOasisCell.getOfficeCurrencyAttribute(), TethysPrice.class);
            case FLOAT:
                return parseValue(getTextContent(), TethysPrice.class);
            default:
                return null;
        }
    }

    @Override
    public TethysRate getRateValue() throws OceanusException {
        switch (getValueType()) {
            case PERCENTAGE:
            case FLOAT:
                return parseValue(theOasisCell.getOfficeValueAttribute(), TethysRate.class);
            default:
                return null;
        }
    }

    @Override
    public TethysUnits getUnitsValue() throws OceanusException {
        return Value.FLOAT.equals(getValueType())
                                                  ? parseValue(theOasisCell.getOfficeValueAttribute(), TethysUnits.class)
                                                  : null;
    }

    @Override
    public TethysDilution getDilutionValue() throws OceanusException {
        return Value.FLOAT.equals(getValueType())
                                                  ? parseValue(theOasisCell.getOfficeValueAttribute(), TethysDilution.class)
                                                  : null;
    }

    @Override
    public TethysRatio getRatioValue() throws OceanusException {
        return Value.FLOAT.equals(getValueType())
                                                  ? parseValue(theOasisCell.getOfficeValueAttribute(), TethysRatio.class)
                                                  : null;
    }

    @Override
    public String getStringValue() {
        Value myType = getValueType();
        if (myType != null) {
            switch (myType) {
                case STRING:
                case PERCENTAGE:
                case FLOAT:
                case CURRENCY:
                    return getTextContent();
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Obtain text content of cell, formatting the value if the Text content is missing.
     * @return the text
     */
    private String getTextContent() {
        String myRes = theOasisCell.getTextContent();
        if (myRes.length() == 0) {
            Double myValue = theOasisCell.getOfficeValueAttribute();
            if (myValue != null) {
                myRes = formatValue(myValue);
            }
        }
        return myRes;
    }

    @Override
    public void setNullValue() throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove Cell content */
            removeCellContent();
        }
    }

    /**
     * Remove cell content.
     */
    private void removeCellContent() {
        /* Clear all children of node */
        Node myChild = theOasisCell.getFirstChild();
        while (myChild != null) {
            theOasisCell.removeChild(myChild);
            myChild = theOasisCell.getFirstChild();
        }
    }

    /**
     * Set text content.
     * @param pText the text to set
     */
    private void setTextContent(final String pText) {
        TextPElement myText = theOasisCell.newTextPElement();
        myText.setTextContent(pText);
        theOasisCell.appendChild(myText);
    }

    @Override
    protected void setBoolean(final Boolean pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.BOOLEAN.toString());
            theOasisCell.setOfficeBooleanValueAttribute(pValue);
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setDate(final TethysDate pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.DATE.toString());
            theOasisCell.setOfficeDateValueAttribute(formatValue(pValue));
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setInteger(final Integer pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.FLOAT.toString());
            theOasisCell.setOfficeValueAttribute(pValue.doubleValue());
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setLong(final Long pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.FLOAT.toString());
            theOasisCell.setOfficeValueAttribute(pValue.doubleValue());
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setString(final String pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
            setTextContent(pValue);
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setDecimal(final TethysDecimal pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute((pValue instanceof TethysRate)
                                                                                    ? OfficeValueTypeAttribute.Value.PERCENTAGE.toString()
                                                                                    : OfficeValueTypeAttribute.Value.FLOAT.toString());
            theOasisCell.setOfficeValueAttribute(pValue.doubleValue());
            setTextContent(formatValue(pValue));

            /* Set the style for the cell */
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setMonetary(final TethysMoney pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.CURRENCY.toString());
            theOasisCell.setOfficeValueAttribute(pValue.doubleValue());
            theOasisCell.setOfficeCurrencyAttribute(pValue.getCurrency().getCurrencyCode());
            setTextContent(formatValue(pValue));

            /* Set the style for the cell */
            theCellMap.getRow().setAlternateCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setHeader(final String pValue) throws OceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
            setTextContent(pValue);
            theCellMap.getRow().setAlternateCellStyle(theOasisCell, pValue);
        }
    }
}
