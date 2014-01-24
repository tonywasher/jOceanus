/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.sheet;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute.Value;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.w3c.dom.Node;

/**
 * Class representing a cell in Oasis.
 * @author Tony Washer
 */
public class OasisCell
        extends DataCell {
    /**
     * The bad value error text.
     */
    private static final String ERROR_VALUE = "Bad value at cell ";

    /**
     * The list of cells.
     */
    private final OasisCellMap theCellMap;

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
    protected OasisCell(final OasisCellMap pMap,
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
    protected OasisCell getNextCell() {
        return theCellMap.getReadOnlyCellByIndex(getCellIndex() + 1);
    }

    /**
     * Obtain the previous cell.
     * @return the previous cell
     */
    protected OasisCell getPreviousCell() {
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
        OasisRow myRow = theCellMap.getRow();
        return myRow.formatValue(pValue);
    }

    /**
     * Parse a value.
     * @param <T> the value type to parse
     * @param pSource the string to parse.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws JOceanusException on error
     */
    private <T> T parseValue(final String pSource,
                             final Class<T> pClass) throws JOceanusException {
        OasisRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            OasisCellAddress myAddress = new OasisCellAddress(myRow.getSheet().getName(), getPosition());
            throw new JMetisDataException(pSource, ERROR_VALUE
                                                   + myAddress, e);
        }
    }

    /**
     * Parse a value.
     * @param <T> the value type to parse
     * @param pSource the double value.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws JOceanusException on error
     */
    private <T> T parseValue(final Double pSource,
                             final Class<T> pClass) throws JOceanusException {
        OasisRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            OasisCellAddress myAddress = new OasisCellAddress(myRow.getSheet().getName(), getPosition());
            throw new JMetisDataException(pSource, ERROR_VALUE
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
     * @throws JOceanusException on error
     */
    private <T> T parseValue(final Double pSource,
                             final String pCurrCode,
                             final Class<T> pClass) throws JOceanusException {
        OasisRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pCurrCode, pClass);
        } catch (IllegalArgumentException e) {
            OasisCellAddress myAddress = new OasisCellAddress(myRow.getSheet().getName(), getPosition());
            throw new JMetisDataException(pSource, ERROR_VALUE
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
                : null;
    }

    @Override
    public Boolean getBooleanValue() {
        switch (getValueType()) {
            case BOOLEAN:
                return theOasisCell.getOfficeBooleanValueAttribute();
            default:
                return null;
        }
    }

    @Override
    public JDateDay getDateValue() throws JOceanusException {
        switch (getValueType()) {
            case DATE:
                return parseValue(theOasisCell.getOfficeDateValueAttribute(), JDateDay.class);
            default:
                return null;
        }
    }

    @Override
    public Integer getIntegerValue() throws JOceanusException {
        switch (getValueType()) {
            case FLOAT:
                return theOasisCell.getOfficeValueAttribute().intValue();
            default:
                return null;
        }
    }

    @Override
    public JMoney getMoneyValue() throws JOceanusException {
        switch (getValueType()) {
            case CURRENCY:
                return parseValue(theOasisCell.getOfficeValueAttribute(), theOasisCell.getOfficeCurrencyAttribute(), JMoney.class);
            case FLOAT:
                return parseValue(theOasisCell.getTextContent(), JMoney.class);
            default:
                return null;
        }
    }

    @Override
    public JPrice getPriceValue() throws JOceanusException {
        switch (getValueType()) {
            case CURRENCY:
                return parseValue(theOasisCell.getOfficeValueAttribute(), theOasisCell.getOfficeCurrencyAttribute(), JPrice.class);
            case FLOAT:
                return parseValue(theOasisCell.getTextContent(), JPrice.class);
            default:
                return null;
        }
    }

    @Override
    public JRate getRateValue() throws JOceanusException {
        switch (getValueType()) {
            case PERCENTAGE:
            case FLOAT:
                return parseValue(theOasisCell.getOfficeValueAttribute(), JRate.class);
            default:
                return null;
        }
    }

    @Override
    public JUnits getUnitsValue() throws JOceanusException {
        switch (getValueType()) {
            case FLOAT:
                return parseValue(theOasisCell.getOfficeValueAttribute(), JUnits.class);
            default:
                return null;
        }
    }

    @Override
    public JDilution getDilutionValue() throws JOceanusException {
        switch (getValueType()) {
            case FLOAT:
                return parseValue(theOasisCell.getOfficeValueAttribute(), JDilution.class);
            default:
                return null;
        }
    }

    @Override
    public JRatio getRatioValue() throws JOceanusException {
        switch (getValueType()) {
            case FLOAT:
                return parseValue(theOasisCell.getOfficeValueAttribute(), JRatio.class);
            default:
                return null;
        }
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
                    return theOasisCell.getTextContent();
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public void setNullValue() throws JOceanusException {
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
    protected void setBoolean(final Boolean pValue) throws JOceanusException {
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
    protected void setDate(final JDateDay pValue) throws JOceanusException {
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
    protected void setInteger(final Integer pValue) throws JOceanusException {
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
    protected void setString(final String pValue) throws JOceanusException {
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
    protected void setDecimal(final JDecimal pValue) throws JOceanusException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute((pValue instanceof JRate)
                    ? OfficeValueTypeAttribute.Value.PERCENTAGE.toString()
                    : OfficeValueTypeAttribute.Value.FLOAT.toString());
            theOasisCell.setOfficeValueAttribute(pValue.doubleValue());
            setTextContent(formatValue(pValue));

            /* Set the style for the cell */
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setMonetary(final JMoney pValue) throws JOceanusException {
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
    protected void setHeader(final String pValue) throws JOceanusException {
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