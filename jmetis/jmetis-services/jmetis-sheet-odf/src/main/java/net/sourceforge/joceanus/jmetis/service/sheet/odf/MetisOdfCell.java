/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCell;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellAddress;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetException;
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
public class MetisOdfCell
        extends MetisSheetCell {
    /**
     * The bad value error text.
     */
    private static final String ERROR_VALUE = "Bad value at cell ";

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The map of cells.
     */
    private final MetisOdfCellMap theCellMap;

    /**
     * The underlying element.
     */
    private Element theOasisCell;

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
    MetisOdfCell(final MetisOdfCellMap pMap,
                 final Element pCell,
                 final int pIndex,
                 final boolean pReadOnly) {
        /* Store parameters */
        super(pMap.getRow(), pIndex);
        theCellMap = pMap;
        theOasisCell = pCell;
        theParser = getSheet().getParser();
        isReadOnly = pReadOnly;
    }

    @Override
    public MetisOdfSheet getSheet() {
        return (MetisOdfSheet) super.getSheet();
    }

    /**
     * Obtain the next cell.
     * @return the next cell
     */
    protected MetisOdfCell getNextCell() {
        return theCellMap.getReadOnlyCellByIndex(getCellIndex() + 1);
    }

    /**
     * Obtain the previous cell.
     * @return the previous cell
     */
    protected MetisOdfCell getPreviousCell() {
        return theCellMap.getReadOnlyCellByIndex(getCellIndex() - 1);
    }

    /**
     * Obtain the row style name.
     * @return the row style name
     */
    protected String getCellStyle() {
        return theParser.getAttribute(theOasisCell, MetisOdfTableItem.STYLENAME);
    }

    /**
     * Set the row style.
     * @param pStyle the row style
     */
    protected void setCellStyle(final String pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the cell style */
            theParser.setAttribute(theOasisCell, MetisOdfTableItem.STYLENAME, pStyle);
        }
    }

    /**
     * Set the cell validation name.
     * @param pValidation the validation name
     */
    void setValidationName(final String pValidation) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the content validation name */
            theParser.setAttribute(theOasisCell, MetisOdfTableItem.VALIDATIONNAME, pValidation);
        }
    }

    /**
     * Format a value.
     * @param pValue the value to format.
     * @return the formatted value
     */
    private String formatValue(final Object pValue) {
        final MetisOdfRow myRow = theCellMap.getRow();
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
        final MetisOdfRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            final MetisSheetCellAddress myAddress = new MetisSheetCellAddress(myRow.getSheet().getName(), getPosition());
            throw new MetisSheetException(pSource, ERROR_VALUE
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
        final MetisOdfRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            final MetisSheetCellAddress myAddress = new MetisSheetCellAddress(myRow.getSheet().getName(), getPosition());
            throw new MetisSheetException(pSource, ERROR_VALUE
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
        final MetisOdfRow myRow = theCellMap.getRow();
        try {
            return myRow.parseValue(pSource, pCurrCode, pClass);
        } catch (IllegalArgumentException e) {
            final MetisSheetCellAddress myAddress = new MetisSheetCellAddress(myRow.getSheet().getName(), getPosition());
            throw new MetisSheetException(pSource, ERROR_VALUE
                    + myAddress, e);
        }
    }

    /**
     * Obtain value attribute type.
     * @return the value attribute type
     */
    private MetisOdfValue getValueType() {
        final String myType = theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE);
        return MetisOdfValue.findValueType(myType);
    }

    @Override
    public Boolean getBooleanValue() {
        return MetisOdfValue.BOOLEAN.equals(getValueType())
               ? theParser.getBooleanAttribute(theOasisCell, MetisOdfOfficeItem.BOOLEANVALUE)
               : null;
    }

    @Override
    public TethysDate getDateValue() throws OceanusException {
        return MetisOdfValue.DATE.equals(getValueType())
               ? parseValue(theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.DATEVALUE), TethysDate.class)
               : null;
    }

    @Override
    public Integer getIntegerValue() {
        return MetisOdfValue.FLOAT.equals(getValueType())
               ? theParser.getDoubleAttribute(theOasisCell, MetisOdfOfficeItem.VALUE).intValue()
               : null;
    }

    @Override
    public Long getLongValue() throws OceanusException {
        return MetisOdfValue.FLOAT.equals(getValueType())
               ? theParser.getDoubleAttribute(theOasisCell, MetisOdfOfficeItem.VALUE).longValue()
               : null;
    }

    @Override
    public TethysMoney getMoneyValue() throws OceanusException {
        switch (getValueType()) {
            case CURRENCY:
                return parseValue(theParser.getDoubleAttribute(theOasisCell, MetisOdfOfficeItem.VALUE),
                        theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.CURRENCY), TethysMoney.class);
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
                return parseValue(theParser.getDoubleAttribute(theOasisCell, MetisOdfOfficeItem.VALUE),
                        theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.CURRENCY), TethysPrice.class);
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
                return parseValue(theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.VALUE), TethysRate.class);
            default:
                return null;
        }
    }

    @Override
    public TethysUnits getUnitsValue() throws OceanusException {
        return MetisOdfValue.FLOAT.equals(getValueType())
               ? parseValue(theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.VALUE), TethysUnits.class)
               : null;
    }

    @Override
    public TethysDilution getDilutionValue() throws OceanusException {
        return MetisOdfValue.FLOAT.equals(getValueType())
               ? parseValue(theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.VALUE), TethysDilution.class)
               : null;
    }

    @Override
    public TethysRatio getRatioValue() throws OceanusException {
        return MetisOdfValue.FLOAT.equals(getValueType())
               ? parseValue(theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.VALUE), TethysRatio.class)
               : null;
    }

    @Override
    public String getStringValue() {
        final MetisOdfValue myType = getValueType();
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
                myRes = theParser.getAttribute(theOasisCell, MetisOdfOfficeItem.VALUE);
        }
        return myRes;
    }

    @Override
    public void setNullValue() {
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
        final Element myText = theParser.newElement(MetisOdfTableItem.TEXT);
        myText.setTextContent(pText);
        theOasisCell.appendChild(myText);
    }

    @Override
    protected void setBoolean(final Boolean pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.BOOLEAN);
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.BOOLEANVALUE, pValue);
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setDate(final TethysDate pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.DATE);
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.DATEVALUE, formatValue(pValue));
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setInteger(final Integer pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.FLOAT);
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUE, pValue.doubleValue());
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setLong(final Long pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.FLOAT);
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUE, pValue.doubleValue());
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setString(final String pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.STRING);
            setTextContent(pValue);
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setDecimal(final TethysDecimal pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE,
                    pValue instanceof TethysRate
                                            ? MetisOdfValue.PERCENTAGE
                                            : MetisOdfValue.FLOAT);
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUE, pValue.doubleValue());
            setTextContent(formatValue(pValue));

            /* Set the style for the cell */
            theCellMap.getRow().setCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setMonetary(final TethysMoney pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.CURRENCY);
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUE, pValue.doubleValue());
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.CURRENCY, pValue.getCurrency().getCurrencyCode());
            setTextContent(formatValue(pValue));

            /* Set the style for the cell */
            theCellMap.getRow().setAlternateCellStyle(theOasisCell, pValue);
        }
    }

    @Override
    protected void setHeader(final String pValue) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theParser.setAttribute(theOasisCell, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.STRING);
            setTextContent(pValue);
            theCellMap.getRow().setAlternateCellStyle(theOasisCell, pValue);
        }
    }
}
