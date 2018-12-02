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

import java.util.Arrays;
import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellAddress;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
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
 * Hold cells as a list of values.
 */
class MetisOdfCellStore {
    /**
     * The Null Element.
     */
    private static final MetisCellElement NULL_CELL = new MetisCellElement();

    /**
     * The bad value error text.
     */
    private static final String ERROR_VALUE = "Bad value at cell ";

    /**
     * Underlying row.
     */
    private final MetisOdfRowStore theOasisRow;

    /**
     * Underlying sheet.
     */
    private final MetisOdfSheetCore theSheet;

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * Is the store readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Array of Alternate format values.
     */
    private Boolean[] theAlternates;

    /**
     * Array of Validations.
     */
    private String[] theValidations;

    /**
     * Array of values.
     */
    private Object[] theValues;

    /**
     * Array of Cell Element values.
     */
    private MetisCellElement[] theElements;

    /**
     * The row index.
     */
    private final int theRowIndex;

    /**
     * Number of Cells.
     */
    private int theNumCells;

    /**
     * ReadOnly Constructor.
     * @param pRow the owning row.
     * @param pRowIndex the row index
     * @param pElement the row element
     * @throws OceanusException on error
     */
    MetisOdfCellStore(final MetisOdfRowStore pRow,
                      final int pRowIndex,
                      final Element pElement) throws OceanusException {
        /* Store details */
        theOasisRow = pRow;
        theSheet = theOasisRow.getSheet();
        theParser = theSheet.getParser();
        theRowIndex = pRowIndex;
        isReadOnly = true;

        /* Allocate the element array */
        theElements = new MetisCellElement[0];

        /* Process the children of the row */
        processRowChildren(pElement);
    }

    /**
     * Mutable Constructor.
     * @param pRow the owning row.
     * @param pRowIndex the row index
     * @param pNumCells the initial number of cells
     */
    MetisOdfCellStore(final MetisOdfRowStore pRow,
                      final int pRowIndex,
                      final int pNumCells)  {
        /* Store details */
        theOasisRow = pRow;
        theSheet = theOasisRow.getSheet();
        theParser = theSheet.getParser();
        theRowIndex = pRowIndex;
        theNumCells = pNumCells;
        isReadOnly = false;

        /* Allocate the arrays */
        theValues = new Object[0];
        theAlternates = new Boolean[0];
        theValidations = new String[0];
    }

    /**
     * Obtain a readOnly cell by its index.
     * @param pRow the row for the cell
     * @param pCellIndex the index of the cell.
     * @return the column
     */
    MetisOdfCellNew getReadOnlyCellByIndex(final MetisOdfRowNew pRow,
                                           final int pCellIndex) {
        /* Handle index out of range */
        if (pCellIndex < 0 || pCellIndex >= theNumCells) {
            return null;
        }

        /* Only return a cell if a value is present */
        final Object myValue = isReadOnly
                               ? getElementAtIndex(pCellIndex)
                               : getValueAtIndex(pCellIndex);
        return myValue == null
               ? null
               : new MetisOdfCellNew(this, pRow, pCellIndex, true);
    }

    /**
     * Obtain a mutable cell by its index.
     * @param pRow the row for the cell
     * @param pCellIndex the index of the cell.
     * @return the column
     */
    MetisOdfCellNew getMutableCellByIndex(final MetisOdfRowNew pRow,
                                          final int pCellIndex) {
        /* Handle index out of range */
        return pCellIndex < 0 || pCellIndex >= theNumCells
                ? null
                : new MetisOdfCellNew(this, pRow, pCellIndex, false);
    }

    /**
     * Add additional cells to table.
     * @param pXtraCells the number of cells to add.
     */
    void addAdditionalCells(final int pXtraCells) {
        theNumCells += pXtraCells;
    }

    /**
     * Apply validation.
     * @param pValidation the validation name
     * @param pFirstCol the first column
     * @param pLastCol the last column
     */
    void applyValidation(final String pValidation,
                         final int pFirstCol,
                         final int pLastCol) {
        /* Loop through the cells in reverse order */
        for (int iIndex = pLastCol;
             iIndex >= pFirstCol; iIndex--) {
            /* Set the validation */
            setValidationAtIndex(pValidation, iIndex);
        }
    }

    /**
     * Get value at index.
     * @param pIndex the index
     * @return the value
     */
    private MetisCellElement getElementAtIndex(final int pIndex) {
        return pIndex < 0 || pIndex >= theElements.length
               ? null
               : theElements[pIndex];
    }

    /**
     * Get value at index.
     * @param pIndex the index
     * @return the value
     */
    private Object getValueAtIndex(final int pIndex) {
        return pIndex < 0 || pIndex >= theValues.length
                ? null
                : theValues[pIndex];
    }

    /**
     * Get alternate at index.
     * @param pIndex the index
     * @return the alternate flag
     */
    private Boolean getAlternateAtIndex(final int pIndex) {
        return pIndex < 0 || pIndex >= theAlternates.length
               ? null
               : theAlternates[pIndex];
    }

    /**
     * Get validation at index.
     * @param pIndex the index
     * @return the validation
     */
    private String getValidationAtIndex(final int pIndex) {
        return pIndex < 0 || pIndex >= theAlternates.length
               ? null
               : theValidations[pIndex];
    }

    /**
     * Access the value as Boolean.
     * @param pIndex the index
     * @return the boolean
     */
    Boolean getBooleanValueAtIndex(final int pIndex) {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getBooleanValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof Boolean
               ? (Boolean) myValue
               : null;
    }

    /**
     * Access the value as Date.
     * @param pIndex the index
     * @return the date
     */
    TethysDate getDateValueAtIndex(final int pIndex) {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getDateValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof TethysDate
               ? (TethysDate) myValue
               : null;
    }

    /**
     * Access the value as Integer.
     * @param pIndex the index
     * @return the integer
     * @throws OceanusException on error
     */
    Integer getIntegerValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getIntegerValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof Integer
               ? (Integer) myValue
               : null;
    }

    /**
     * Access the value as Long.
     * @param pIndex the index
     * @return the long
     * @throws OceanusException on error
     */
    Long getLongValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getLongValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof Long
               ? (Long) myValue
               : null;
    }

    /**
     * Access the value as Rate.
     * @param pIndex the index
     * @return the rate
     * @throws OceanusException on error
     */
    TethysRate getRateValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getRateValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof TethysRate
               ? (TethysRate) myValue
               : null;
    }

    /**
     * Access the value as Units.
     * @param pIndex the index
     * @return the units
     * @throws OceanusException on error
     */
    TethysUnits getUnitsValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getUnitsValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof TethysUnits
               ? (TethysUnits) myValue
               : null;
    }

    /**
     * Access the value as Ratio.
     * @param pIndex the index
     * @return the ratio
     * @throws OceanusException on error
     */
    TethysRatio getRatioValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getRatioValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof TethysRatio
               ? (TethysRatio) myValue
               : null;
    }

    /**
     * Access the value as Dilution.
     * @param pIndex the index
     * @return the dilution
     * @throws OceanusException on error
     */
    TethysDilution getDilutionValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getDilutionValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof TethysDilution
               ? (TethysDilution) myValue
               : null;
    }

    /**
     * Access the value as Money.
     * @param pIndex the index
     * @return the rate
     * @throws OceanusException on error
     */
    TethysMoney getMoneyValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getMoneyValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof TethysMoney
               ? (TethysMoney) myValue
               : null;
    }

    /**
     * Access the value as Price.
     * @param pIndex the index
     * @return the price
     * @throws OceanusException on error
     */
    TethysPrice getPriceValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getPriceValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof TethysPrice
               ? (TethysPrice) myValue
               : null;
    }

    /**
     * Access the value as String.
     * @param pIndex the index
     * @return the string
     */
    String getStringValueAtIndex(final int pIndex) {
        if (isReadOnly) {
            final MetisCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getStringValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        if (myValue == null) {
            return null;
        } else {
            return myValue instanceof String
                   ? (String) myValue
                   : theOasisRow.formatValue(myValue);
        }
    }

    /**
     * Set value at index.
     * @param pValue the value
     * @param pIndex the index
     */
    void setValueAtIndex(final Object pValue,
                         final int pIndex) {
        /* Check the index */
        ensureIndex(pIndex);

        /* Set the value */
        theValues[pIndex] = pValue;
        theAlternates[pIndex] = null;
    }

    /**
     * Set alternate value at index.
     * @param pValue the value
     * @param pIndex the index
     */
    void setAlternateAtIndex(final Object pValue,
                             final int pIndex) {
        /* Check the index */
        ensureIndex(pIndex);

        /* Set the value */
        theValues[pIndex] = pValue;
        theAlternates[pIndex] = Boolean.TRUE;
    }

    /**
     * Set validation at index.
     * @param pValidation the validation
     * @param pIndex the index
     */
    void setValidationAtIndex(final String pValidation,
                              final int pIndex) {
        /* Check the index */
        ensureIndex(pIndex);

        /* Set the value */
        theValidations[pIndex] = pValidation;
    }

    /**
     * Adjust for index.
     * @param pIndex the index
     */
    private void ensureIndex(final int pIndex) {
        /* Check the index */
        if (pIndex < 0 || pIndex >= theNumCells) {
            throw new IllegalArgumentException();
        }

        /* Expand the value array if required */
        if (pIndex >= theValues.length) {
            theValues = Arrays.copyOf(theValues, pIndex + 1);
            theAlternates = Arrays.copyOf(theAlternates, pIndex + 1);
            theValidations = Arrays.copyOf(theValidations, pIndex + 1);
        }
    }

    /**
     * Set element at index.
     * @param pElement the element
     * @param pIndex the index
     */
    private void setElementAtIndex(final MetisCellElement pElement,
                                   final int pIndex) {
        /* Check the index */
        if (pIndex < 0 || pIndex >= theNumCells) {
            throw new IllegalArgumentException();
        }

        /* Expand the elements array if required */
        if (pIndex >= theElements.length) {
            theElements = Arrays.copyOf(theElements, pIndex + 1);
        }

        /* Set the element */
        theElements[pIndex] = pElement;
    }

    /**
     * Process Row children.
     * @param pRow the row element
     * @throws OceanusException on error
     */
    private void processRowChildren(final Element pRow) throws OceanusException {
        /* Loop through the children of the row */
        for (Node myNode = pRow.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a cell element */
            if (theParser.isElementOfType(myNode, MetisOdfTableItem.CELL)) {
                /* Add cell to list */
                processCell((Element) myNode);
            }
        }
    }

    /**
     * Process a cell Element.
     * @param pCell the cell to process
     * @throws OceanusException on error
     */
    private void processCell(final Element pCell) throws OceanusException {
        /* Determine the number of repeated cells */
        final String myRepeatStr = theParser.getAttribute(pCell, MetisOdfTableItem.COLUMNREPEAT);
        int myRepeat = myRepeatStr == null
                        ? 1
                        : Integer.parseInt(myRepeatStr);

        /* Parse the value */
        final MetisCellElement myValue = parseCellElement(pCell, theNumCells);

        /* Add the additional cells */
        addAdditionalCells(myRepeat);

        /* If we have a value */
        if (!myValue.isNull()) {
            /* Loop through the cells in reverse order */
            for (int iIndex = theNumCells - 1;
                 myRepeat > 0; iIndex--, myRepeat--) {
                /* Set the element */
                setElementAtIndex(myValue, iIndex);
            }
        }
    }

    /**
     * Populate Row children.
     * @param pRow the row element
     */
    void populateRowChildren(final Element pRow) {
        /* Loop through the cells */
        int myRepeat;
        for (int iIndex = 0; iIndex < theNumCells; iIndex += myRepeat) {
            /* Create a new cell element */
            final Element myCell = theParser.newElement(MetisOdfTableItem.CELL);
            pRow.appendChild(myCell);

            /* Populate the value */
            populateValue(myCell, iIndex, getValueAtIndex(iIndex));

            /* Set validation if required */
            final String myValidation = getValidationAtIndex(iIndex);
            if (myValidation != null) {
                theParser.setAttribute(myCell, MetisOdfTableItem.VALIDATIONNAME, myValidation);
            }

            /* Determine the repeat count */
            myRepeat = getRepeatCountForIndex(iIndex);
            if (myRepeat > 1) {
                /* Set attribute and adjust index */
                theParser.setAttribute(myCell, MetisOdfTableItem.COLUMNREPEAT, myRepeat);
            }
        }
    }

    /**
     * Populate value.
     * @param pElement the element
     * @param pIndex the index
     * @param pValue the boolean
     */
    private void populateValue(final Element pElement,
                               final int pIndex,
                               final Object pValue) {
        /* Handle different value types */
        if (pValue instanceof Boolean) {
            populateBoolean(pElement, (Boolean) pValue);
        } else if (pValue instanceof Number) {
            populateNumber(pElement, (Number) pValue);
        } else if (pValue instanceof TethysDate) {
            populateDate(pElement, (TethysDate) pValue);
        } else if (pValue instanceof String) {
            populateString(pElement, pIndex, (String) pValue);
        } else if (pValue instanceof TethysMoney) {
            populateMonetary(pElement, pIndex, (TethysMoney) pValue);
        } else if (pValue instanceof TethysDecimal) {
            populateDecimal(pElement, (TethysDecimal) pValue);
        }
    }

    /**
     * Populate Boolean value.
     * @param pElement the element
     * @param pValue the boolean
     */
    private void populateBoolean(final Element pElement,
                                 final Boolean pValue) {
        /* Set value type and value */
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.BOOLEAN);
        theParser.setAttribute(pElement, MetisOdfOfficeItem.BOOLEANVALUE, pValue);
        theOasisRow.setCellStyle(pElement, pValue);
    }

    /**
     * Populate Date value.
     * @param pElement the element
     * @param pValue the date
     */
    private void populateDate(final Element pElement,
                              final TethysDate pValue) {
        /* Set value type and value */
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.DATE);
        theParser.setAttribute(pElement, MetisOdfOfficeItem.DATEVALUE, theOasisRow.formatValue(pValue));
        theOasisRow.setCellStyle(pElement, pValue);
    }

    /**
     * Populate Number value.
     * @param pElement the element
     * @param pValue the number
     */
    private void populateNumber(final Element pElement,
                                final Number pValue) {
        /* Set value type and value */
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.FLOAT);
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUE, pValue.doubleValue());
        theOasisRow.setCellStyle(pElement, pValue);
    }

    /**
     * Populate String value.
     * @param pElement the element
     * @param pIndex the index
     * @param pValue the string
     */
    private void populateString(final Element pElement,
                                final int pIndex,
                                final String pValue) {
        /* Determine whether this needs the alternate style */
        final Boolean isAlt = getAlternateAtIndex(pIndex);
        if (isAlt != null) {
            theOasisRow.setAlternateCellStyle(pElement, pValue);
        } else {
            theOasisRow.setCellStyle(pElement, pValue);
        }

        /* Set value type and value */
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.STRING);
        setTextContent(pElement, pValue);
    }

    /**
     * Populate Monetary value.
     * @param pElement the element
     * @param pIndex the index
     * @param pValue the string
     */
    private void populateMonetary(final Element pElement,
                                  final int pIndex,
                                  final TethysMoney pValue) {
        /* Use decimal style if this is not an alt value */
        final Boolean isAlt = getAlternateAtIndex(pIndex);
        if (isAlt == null) {
            populateDecimal(pElement, pValue);
            return;
        }

        /* Set value type and value */
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUETYPE, MetisOdfValue.CURRENCY);
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUE, pValue.doubleValue());
        theParser.setAttribute(pElement, MetisOdfOfficeItem.CURRENCY, pValue.getCurrency().getCurrencyCode());
        setTextContent(pElement, theOasisRow.formatValue(pValue));
        theOasisRow.setCellStyle(pElement, pValue);
    }

    /**
     * Populate Decimal value.
     * @param pElement the element
     * @param pValue the string
     */
    private void populateDecimal(final Element pElement,
                                 final TethysDecimal pValue) {
        /* Set value type and value */
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUETYPE,
                                pValue instanceof TethysRate
                                        ? MetisOdfValue.PERCENTAGE
                                        : MetisOdfValue.FLOAT);
        theParser.setAttribute(pElement, MetisOdfOfficeItem.VALUE, pValue.doubleValue());
        setTextContent(pElement, theOasisRow.formatValue(pValue));
        theOasisRow.setCellStyle(pElement, pValue);
    }

    /**
     * Populate Number value.
     * @param pElement the element
     * @param pValue the date
     */
    private void setTextContent(final Element pElement,
                                final String pValue) {
        /* Set value type and value */
        final Element myText = theParser.newElement(MetisOdfTableItem.TEXT);
        myText.setTextContent(pValue);
        pElement.appendChild(myText);
    }

    /**
     * Obtain repeat count for index.
     * @param pIndex the index to start at
     * @return the repeat count
     */
    private int getRepeatCountForIndex(final int pIndex) {
        /* If the index is larger than the values */
        if (pIndex >= theValues.length) {
            /* Repeat count is the remaining cells */
            return theNumCells - pIndex;
        }

        /* Access the current value */
        final Object myValue = theValues[pIndex];
        final Boolean myAlt = theAlternates[pIndex];
        final String myValid = theValidations[pIndex];

        /* Loop through the remaining indices */
        for (int i = pIndex + 1; i < theValues.length; i++) {
            /* Access the next object */
            final Object nextValue = theValues[i];
            final Boolean nextAlt = theAlternates[i];
            final String nextValid = theValidations[i];

            /* Test for equality */
            if (!Objects.equals(myValue, nextValue)
                || !Objects.equals(myAlt, nextAlt)
                || !Objects.equals(myValid, nextValid)) {
                return i - pIndex;
            }
        }

        /* All remaining values are equal */
        return myValue == null
               ? theNumCells - pIndex
               : theValues.length - pIndex;
    }

    /**
     * parse Cell element.
     * @param pElement the element
     * @param pIndex the index
     * @return the Cell
     * @throws OceanusException on error
     */
    private MetisCellElement parseCellElement(final Element pElement,
                                              final int pIndex) throws OceanusException {
        /* Access the value-type */
        final MetisOdfValue myType = MetisOdfValue.findValueType(theParser.getAttribute(pElement, MetisOdfOfficeItem.VALUETYPE));
        if (myType == null) {
            return NULL_CELL;
        }

        /* Switch on the valueType */
        switch (myType) {
            case BOOLEAN:
                return new MetisCellElement(this, pIndex, myType, theParser.getBooleanAttribute(pElement, MetisOdfOfficeItem.BOOLEANVALUE));
            case PERCENTAGE:
                String myValue = theParser.getAttribute(pElement, MetisOdfOfficeItem.VALUE);
                String myText = pElement.getTextContent();
                return new MetisCellElement(this, pIndex, myType, parseValue(pIndex, myValue, TethysRate.class), myText);
            case DATE:
                myValue = theParser.getAttribute(pElement, MetisOdfOfficeItem.DATEVALUE);
                return new MetisCellElement(this, pIndex, myType, parseValue(pIndex, myValue, TethysDate.class));
            case FLOAT:
                myValue = theParser.getAttribute(pElement, MetisOdfOfficeItem.VALUE);
                myText = pElement.getTextContent();
                return new MetisCellElement(this, pIndex, myType, myValue, myText);
            case CURRENCY:
                final Double myDouble = theParser.getDoubleAttribute(pElement, MetisOdfOfficeItem.VALUE);
                final String myCurr = theParser.getAttribute(pElement, MetisOdfOfficeItem.CURRENCY);
                myText = pElement.getTextContent();
                return new MetisCellElement(this, pIndex, myType, myDouble, myText, myCurr);
            case STRING:
                myText = pElement.getTextContent();
                return new MetisCellElement(this, pIndex, myType, myText, myText);
            default:
                return NULL_CELL;
        }
    }

    /**
     * Parse a value.
     * @param <T> the value type to parse
     * @param pIndex the index of the cell
     * @param pSource the string to parse.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws OceanusException on error
     */
    private <T> T parseValue(final int pIndex,
                             final String pSource,
                             final Class<T> pClass) throws OceanusException {
        try {
            return theOasisRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            final MetisSheetCellAddress myAddress = new MetisSheetCellAddress(theSheet.getName(),
                    new MetisSheetCellPosition(pIndex, theRowIndex));
            throw new MetisSheetException(pSource, ERROR_VALUE
                    + myAddress, e);
        }
    }

    /**
     * Parse a currency value.
     * @param <T> the value type to parse
     * @param pIndex the index of the cell
     * @param pSource the double value.
     * @param pCurrCode the currency code.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws OceanusException on error
     */
    private <T> T parseCurrency(final int pIndex,
                                final Double pSource,
                                final String pCurrCode,
                                final Class<T> pClass) throws OceanusException {
        try {
            return theOasisRow.parseValue(pSource, pCurrCode, pClass);
        } catch (IllegalArgumentException e) {
            final MetisSheetCellAddress myAddress = new MetisSheetCellAddress(theSheet.getName(),
                    new MetisSheetCellPosition(pIndex, theRowIndex));
            throw new MetisSheetException(pSource, ERROR_VALUE
                    + myAddress, e);
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof MetisOdfCellStore)) {
            return false;
        }
        final MetisOdfCellStore myThat = (MetisOdfCellStore) pThat;

        /* Check readOnly and numCells */
        if (isReadOnly != myThat.isReadOnly
                || theNumCells != myThat.theNumCells) {
            return false;
        }

        /* Check the arrays */
        return isReadOnly
               ? Arrays.equals(theElements, myThat.theElements)
               : Arrays.equals(theValues, myThat.theValues)
                       && Arrays.equals(theAlternates, myThat.theAlternates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isReadOnly, theNumCells, theElements, theAlternates, theValues);
    }

    /**
     * CellElement.
     */
    private static final class MetisCellElement {
        /**
         * The Store.
         */
        private final MetisOdfCellStore theStore;

        /**
         * The ValueType.
         */
        private final MetisOdfValue theValueType;

        /**
         * The Value.
         */
        private final Object theValue;

        /**
         * The Currency.
         */
        private final String theCurrency;

        /**
         * The Text.
         */
        private final String theText;

        /**
         * The Index.
         */
        private final int theIndex;

        /**
         * Constructor.
         */
        private MetisCellElement() {
            theStore = null;
            theIndex = 0;
            theValueType = null;
            theValue = null;
            theText = null;
            theCurrency = null;
        }

        /**
         * Constructor.
         * @param pStore the store
         * @param pIndex the index
         * @param pType the type
         * @param pValue the value
         */
        private MetisCellElement(final MetisOdfCellStore pStore,
                                 final int pIndex,
                                 final MetisOdfValue pType,
                                 final Object pValue) {
            this(pStore, pIndex, pType, pValue, null, null);
        }

        /**
         * Constructor.
         * @param pStore the store
         * @param pIndex the index
         * @param pType the type
         * @param pText the text
         * @param pValue the value
         */
        private MetisCellElement(final MetisOdfCellStore pStore,
                                 final int pIndex,
                                 final MetisOdfValue pType,
                                 final Object pValue,
                                 final String pText) {
            this(pStore, pIndex, pType, pValue, pText, null);
        }

        /**
         * Constructor.
         * @param pStore the store
         * @param pIndex the index
         * @param pType the type
         * @param pText the text
         * @param pValue the value
         * @param pCurrency the currency
         */
        private MetisCellElement(final MetisOdfCellStore pStore,
                                 final int pIndex,
                                 final MetisOdfValue pType,
                                 final Object pValue,
                                 final String pText,
                                 final String pCurrency) {
            theStore = pStore;
            theIndex = pIndex;
            theValueType = pType;
            theValue = pValue;
            theText = pText;
            theCurrency = pCurrency;
        }

        /**
         * Is the Element null?
         * @return true/false
         */
        boolean isNull() {
            return theValueType == null;
        }

        /**
         * Access the value as Boolean.
         * @return the boolean
         */
        Boolean getBooleanValue() {
            return MetisOdfValue.BOOLEAN.equals(theValueType)
                   ? (Boolean) theValue
                   : null;
        }

        /**
         * Access the value as Date.
         * @return the date
         */
        TethysDate getDateValue() {
            return MetisOdfValue.DATE.equals(theValueType)
                   ? (TethysDate) theValue
                   : null;
        }

        /**
         * Access the value as Integer.
         * @return the integer
         * @throws OceanusException on error
         */
        Integer getIntegerValue() throws OceanusException {
            return MetisOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, Double.class).intValue()
                   : null;
        }

        /**
         * Access the value as Long.
         * @return the long
         * @throws OceanusException on error
         */
        Long getLongValue() throws OceanusException {
            return MetisOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, Double.class).longValue()
                   : null;
        }

        /**
         * Access the value as Rate.
         * @return the rate
         * @throws OceanusException on error
         */
        TethysRate getRateValue() throws OceanusException {
            switch (theValueType) {
                case PERCENTAGE:
                    return (TethysRate) theValue;
                case FLOAT:
                    return theStore.parseValue(theIndex, (String) theValue, TethysRate.class);
                default:
                    return null;
            }
        }

        /**
         * Access the value as Units.
         * @return the units
         * @throws OceanusException on error
         */
        TethysUnits getUnitsValue() throws OceanusException {
            return MetisOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, TethysUnits.class)
                : null;
        }

        /**
         * Access the value as Ratio.
         * @return the ratio
         * @throws OceanusException on error
         */
        TethysRatio getRatioValue() throws OceanusException {
            return MetisOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, TethysRatio.class)
                   : null;
        }

        /**
         * Access the value as Dilution.
         * @return the dilution
         * @throws OceanusException on error
         */
        TethysDilution getDilutionValue() throws OceanusException {
            return MetisOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, TethysDilution.class)
                   : null;
        }

        /**
         * Access the value as Money.
         * @return the money
         * @throws OceanusException on error
         */
        TethysMoney getMoneyValue() throws OceanusException {
            switch (theValueType) {
                case CURRENCY:
                    return theStore.parseCurrency(theIndex, (Double) theValue, theCurrency, TethysMoney.class);
                case FLOAT:
                    return theStore.parseValue(theIndex, theText, TethysMoney.class);
                default:
                    return null;
            }
        }

        /**
         * Access the value as Price.
         * @return the price
         * @throws OceanusException on error
         */
        TethysPrice getPriceValue() throws OceanusException {
            switch (theValueType) {
                case CURRENCY:
                    return theStore.parseCurrency(theIndex, (Double) theValue, theCurrency, TethysPrice.class);
                case FLOAT:
                    return theStore.parseValue(theIndex, theText, TethysPrice.class);
                default:
                    return null;
            }
        }

        /**
         * Access the value as String.
         * @return the string
          */
        String getStringValue() {
            switch (theValueType) {
                case STRING:
                case PERCENTAGE:
                case FLOAT:
                case CURRENCY:
                    return theText;
                default:
                    return null;
            }
        }
    }
}
