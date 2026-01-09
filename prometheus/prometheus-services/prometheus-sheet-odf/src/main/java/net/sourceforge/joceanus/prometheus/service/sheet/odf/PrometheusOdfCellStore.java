/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellAddress;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellPosition;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Hold cells as a list of values.
 */
class PrometheusOdfCellStore {
    /**
     * Cell Expansion.
     */
    private static final int CELL_EXPAND = 20;

    /**
     * The Null Element.
     */
    private static final PrometheusCellElement NULL_CELL = new PrometheusCellElement();

    /**
     * The bad value error text.
     */
    private static final String ERROR_VALUE = "Bad value at cell ";

    /**
     * Underlying row.
     */
    private final PrometheusOdfRowStore theOasisRow;

    /**
     * Underlying sheet.
     */
    private final PrometheusOdfSheetCore theSheet;

    /**
     * The Parser.
     */
    private final PrometheusOdfParser theParser;

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
    private PrometheusCellElement[] theElements;

    /**
     * The row index.
     */
    private final int theRowIndex;

    /**
     * Number of Cells.
     */
    private int theNumCells;

    /**
     * Max valued Cell.
     */
    private int theMaxValuedCell;

    /**
     * ReadOnly Constructor.
     * @param pRow the owning row.
     * @param pRowIndex the row index
     * @param pElement the row element
     * @throws OceanusException on error
     */
    PrometheusOdfCellStore(final PrometheusOdfRowStore pRow,
                           final int pRowIndex,
                           final Element pElement) throws OceanusException {
        /* Store details */
        theOasisRow = pRow;
        theSheet = theOasisRow.getSheet();
        theParser = theSheet.getParser();
        theRowIndex = pRowIndex;
        isReadOnly = true;

        /* Allocate the element array */
        theElements = new PrometheusCellElement[CELL_EXPAND];

        /* Process the children of the row */
        processRowChildren(pElement);
    }

    /**
     * Mutable Constructor.
     * @param pRow the owning row.
     * @param pRowIndex the row index
     * @param pNumCells the initial number of cells
     */
    PrometheusOdfCellStore(final PrometheusOdfRowStore pRow,
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
     * Obtain an iterator of non-null cells for the range.
     * @param pRow the row for the cell
     * @param pFirstCell the index of the first cell.
     * @param pLastCell the index of the last cell.
     * @return the iterator
     */
    ListIterator<PrometheusSheetCell> iteratorForRange(final PrometheusOdfRow pRow,
                                                       final int pFirstCell,
                                                       final int pLastCell) {
        /* Determine upper bound for search */
        final int myBound = Math.min(pLastCell, theNumCells);

        /* Create a list of cells */
        final List<PrometheusSheetCell> myList = new ArrayList<>();
        for (int iIndex = pFirstCell; iIndex <= myBound; iIndex++) {
            /* Only return a cell if a value is present */
            final Object myValue = isReadOnly
                                   ? getElementAtIndex(iIndex)
                                   : getValueAtIndex(iIndex);
            if (myValue != null) {
                myList.add(new PrometheusOdfCell(this, pRow, iIndex, true));
            }
        }

        /* Return the iterator */
        return myList.listIterator();
    }

    /**
     * Obtain a readOnly cell by its index.
     * @param pRow the row for the cell
     * @param pCellIndex the index of the cell.
     * @return the column
     */
    PrometheusOdfCell getReadOnlyCellByIndex(final PrometheusOdfRow pRow,
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
               : new PrometheusOdfCell(this, pRow, pCellIndex, true);
    }

    /**
     * Obtain a mutable cell by its index.
     * @param pRow the row for the cell
     * @param pCellIndex the index of the cell.
     * @return the column
     */
    PrometheusOdfCell getMutableCellByIndex(final PrometheusOdfRow pRow,
                                            final int pCellIndex) {
        /* Handle index out of range */
        return pCellIndex < 0 || pCellIndex >= theNumCells
                ? null
                : new PrometheusOdfCell(this, pRow, pCellIndex, false);
    }

    /**
     * Obtain the index of the max valued cell.
     * @return the index
     */
    int getMaxValuedIndex() {
        return theMaxValuedCell;
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
    private PrometheusCellElement getElementAtIndex(final int pIndex) {
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
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getBooleanValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof Boolean b
               ? b
               : null;
    }

    /**
     * Access the value as Date.
     * @param pIndex the index
     * @return the date
     */
    OceanusDate getDateValueAtIndex(final int pIndex) {
        if (isReadOnly) {
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getDateValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof OceanusDate d
               ? d
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
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getIntegerValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof Integer i
               ? i
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
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getLongValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof Long l
               ? l
               : null;
    }

    /**
     * Access the value as Rate.
     * @param pIndex the index
     * @return the rate
     * @throws OceanusException on error
     */
    OceanusRate getRateValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getRateValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof OceanusRate r
               ? r
               : null;
    }

    /**
     * Access the value as Units.
     * @param pIndex the index
     * @return the units
     * @throws OceanusException on error
     */
    OceanusUnits getUnitsValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getUnitsValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof OceanusUnits u
               ? u
               : null;
    }

    /**
     * Access the value as Ratio.
     * @param pIndex the index
     * @return the ratio
     * @throws OceanusException on error
     */
    OceanusRatio getRatioValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getRatioValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof OceanusRatio r
               ? r
               : null;
    }

    /**
     * Access the value as Money.
     * @param pIndex the index
     * @return the rate
     * @throws OceanusException on error
     */
    OceanusMoney getMoneyValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getMoneyValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof OceanusMoney m
               ? m
               : null;
    }

    /**
     * Access the value as Price.
     * @param pIndex the index
     * @return the price
     * @throws OceanusException on error
     */
    OceanusPrice getPriceValueAtIndex(final int pIndex) throws OceanusException {
        if (isReadOnly) {
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getPriceValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        return myValue instanceof OceanusPrice p
               ? p
               : null;
    }

    /**
     * Access the value as String.
     * @param pIndex the index
     * @return the string
     */
    String getStringValueAtIndex(final int pIndex) {
        if (isReadOnly) {
            final PrometheusCellElement myElement = getElementAtIndex(pIndex);
            return myElement == null
                   ? null
                   : myElement.getStringValue();
        }
        final Object myValue = getValueAtIndex(pIndex);
        if (myValue == null) {
            return null;
        } else {
            return myValue instanceof String s
                   ? s
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
    private void setValidationAtIndex(final String pValidation,
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

        /* Adjust maximum cell */
        if (pIndex > theMaxValuedCell) {
            theMaxValuedCell = pIndex;
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
    private void setElementAtIndex(final PrometheusCellElement pElement,
                                   final int pIndex) {
        /* Check the index */
        if (pIndex < 0 || pIndex >= theNumCells) {
            throw new IllegalArgumentException();
        }

        /* Expand the elements array if required */
        if (pIndex >= theElements.length) {
            /* Determine the expansion length */
            final int myLen = (((pIndex + 1) / CELL_EXPAND) + 1)  * CELL_EXPAND;
            theElements = Arrays.copyOf(theElements, myLen);
        }

        /* Adjust maximum cell */
        if (pIndex > theMaxValuedCell
                && pElement != null) {
            theMaxValuedCell = pIndex;
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
            if (theParser.isElementOfType(myNode, PrometheusOdfTableItem.CELL)) {
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
        final String myRepeatStr = theParser.getAttribute(pCell, PrometheusOdfTableItem.COLUMNREPEAT);
        int myRepeat = myRepeatStr == null
                        ? 1
                        : Integer.parseInt(myRepeatStr);

        /* Parse the value */
        final PrometheusCellElement myValue = parseCellElement(pCell, theNumCells);

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
            final Element myCell = theParser.newElement(PrometheusOdfTableItem.CELL);
            pRow.appendChild(myCell);

            /* Populate the value */
            populateValue(myCell, iIndex, getValueAtIndex(iIndex));

            /* Set validation if required */
            final String myValidation = getValidationAtIndex(iIndex);
            if (myValidation != null) {
                theParser.setAttribute(myCell, PrometheusOdfTableItem.VALIDATIONNAME, myValidation);
            }

            /* Determine the repeat count */
            myRepeat = getRepeatCountForIndex(iIndex);
            if (myRepeat > 1) {
                /* Set attribute and adjust index */
                theParser.setAttribute(myCell, PrometheusOdfTableItem.COLUMNREPEAT, myRepeat);
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
        if (pValue instanceof Boolean b) {
            populateBoolean(pElement, b);
        } else if (pValue instanceof Number n) {
            populateNumber(pElement, n);
        } else if (pValue instanceof OceanusDate d) {
            populateDate(pElement, d);
        } else if (pValue instanceof String s) {
            populateString(pElement, pIndex, s);
        } else if (pValue instanceof OceanusMoney m) {
            populateMonetary(pElement, pIndex, m);
        } else if (pValue instanceof OceanusDecimal d) {
            populateDecimal(pElement, d);
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
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUETYPE, PrometheusOdfValue.BOOLEAN);
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.BOOLEANVALUE, pValue);
        theOasisRow.setCellStyle(pElement, pValue);
    }

    /**
     * Populate Date value.
     * @param pElement the element
     * @param pValue the date
     */
    private void populateDate(final Element pElement,
                              final OceanusDate pValue) {
        /* Set value type and value */
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUETYPE, PrometheusOdfValue.DATE);
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.DATEVALUE, theOasisRow.formatValue(pValue));
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
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUETYPE, PrometheusOdfValue.FLOAT);
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUE, pValue.doubleValue());
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
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUETYPE, PrometheusOdfValue.STRING);
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
                                  final OceanusMoney pValue) {
        /* Use decimal style if this is not an alt value */
        final Boolean isAlt = getAlternateAtIndex(pIndex);
        if (isAlt == null) {
            populateDecimal(pElement, pValue);
            return;
        }

        /* Set value type and value */
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUETYPE, PrometheusOdfValue.CURRENCY);
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUE, pValue.doubleValue());
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.CURRENCY, pValue.getCurrency().getCurrencyCode());
        setTextContent(pElement, theOasisRow.formatValue(pValue));
        theOasisRow.setCellStyle(pElement, pValue);
    }

    /**
     * Populate Decimal value.
     * @param pElement the element
     * @param pValue the string
     */
    private void populateDecimal(final Element pElement,
                                 final OceanusDecimal pValue) {
        /* Set value type and value */
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUETYPE,
                pValue instanceof OceanusRate
                                        ? PrometheusOdfValue.PERCENTAGE
                                        : PrometheusOdfValue.FLOAT);
        theParser.setAttribute(pElement, PrometheusOdfOfficeItem.VALUE, pValue.doubleValue());
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
        final Element myText = theParser.newElement(PrometheusOdfTableItem.TEXT);
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
    private PrometheusCellElement parseCellElement(final Element pElement,
                                                   final int pIndex) throws OceanusException {
        /* Access the value-type */
        final PrometheusOdfValue myType = PrometheusOdfValue.findValueType(theParser.getAttribute(pElement, PrometheusOdfOfficeItem.VALUETYPE));
        if (myType == null) {
            return NULL_CELL;
        }

        /* Switch on the valueType */
        switch (myType) {
            case BOOLEAN:
                return new PrometheusCellElement(this, pIndex, myType, theParser.getBooleanAttribute(pElement, PrometheusOdfOfficeItem.BOOLEANVALUE));
            case PERCENTAGE:
                String myValue = theParser.getAttribute(pElement, PrometheusOdfOfficeItem.VALUE);
                String myText = pElement.getTextContent();
                return new PrometheusCellElement(this, pIndex, myType, parseValue(pIndex, myValue, OceanusRate.class), myText);
            case DATE:
                myValue = theParser.getAttribute(pElement, PrometheusOdfOfficeItem.DATEVALUE);
                return new PrometheusCellElement(this, pIndex, myType, parseValue(pIndex, myValue, OceanusDate.class));
            case FLOAT:
                myValue = theParser.getAttribute(pElement, PrometheusOdfOfficeItem.VALUE);
                myText = pElement.getTextContent();
                return new PrometheusCellElement(this, pIndex, myType, myValue, myText);
            case CURRENCY:
                final Double myDouble = theParser.getDoubleAttribute(pElement, PrometheusOdfOfficeItem.VALUE);
                final String myCurr = theParser.getAttribute(pElement, PrometheusOdfOfficeItem.CURRENCY);
                myText = pElement.getTextContent();
                return new PrometheusCellElement(this, pIndex, myType, myDouble, myText, myCurr);
            case STRING:
                myText = pElement.getTextContent();
                return new PrometheusCellElement(this, pIndex, myType, myText, myText);
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
            final PrometheusSheetCellAddress myAddress = new PrometheusSheetCellAddress(theSheet.getName(),
                    new PrometheusSheetCellPosition(pIndex, theRowIndex));
            throw new PrometheusSheetException(pSource, ERROR_VALUE
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
            final PrometheusSheetCellAddress myAddress = new PrometheusSheetCellAddress(theSheet.getName(),
                    new PrometheusSheetCellPosition(pIndex, theRowIndex));
            throw new PrometheusSheetException(pSource, ERROR_VALUE
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
        if (!(pThat instanceof PrometheusOdfCellStore)) {
            return false;
        }
        final PrometheusOdfCellStore myThat = (PrometheusOdfCellStore) pThat;

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
    private static final class PrometheusCellElement {
        /**
         * The Store.
         */
        private final PrometheusOdfCellStore theStore;

        /**
         * The ValueType.
         */
        private final PrometheusOdfValue theValueType;

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
        private PrometheusCellElement() {
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
        private PrometheusCellElement(final PrometheusOdfCellStore pStore,
                                      final int pIndex,
                                      final PrometheusOdfValue pType,
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
        private PrometheusCellElement(final PrometheusOdfCellStore pStore,
                                      final int pIndex,
                                      final PrometheusOdfValue pType,
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
        private PrometheusCellElement(final PrometheusOdfCellStore pStore,
                                      final int pIndex,
                                      final PrometheusOdfValue pType,
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
            return PrometheusOdfValue.BOOLEAN.equals(theValueType)
                   ? (Boolean) theValue
                   : null;
        }

        /**
         * Access the value as Date.
         * @return the date
         */
        OceanusDate getDateValue() {
            return PrometheusOdfValue.DATE.equals(theValueType)
                   ? (OceanusDate) theValue
                   : null;
        }

        /**
         * Access the value as Integer.
         * @return the integer
         * @throws OceanusException on error
         */
        Integer getIntegerValue() throws OceanusException {
            return PrometheusOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, Double.class).intValue()
                   : null;
        }

        /**
         * Access the value as Long.
         * @return the long
         * @throws OceanusException on error
         */
        Long getLongValue() throws OceanusException {
            return PrometheusOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, Double.class).longValue()
                   : null;
        }

        /**
         * Access the value as Rate.
         * @return the rate
         * @throws OceanusException on error
         */
        OceanusRate getRateValue() throws OceanusException {
            switch (theValueType) {
                case PERCENTAGE:
                    return (OceanusRate) theValue;
                case FLOAT:
                    return theStore.parseValue(theIndex, (String) theValue, OceanusRate.class);
                default:
                    return null;
            }
        }

        /**
         * Access the value as Units.
         * @return the units
         * @throws OceanusException on error
         */
        OceanusUnits getUnitsValue() throws OceanusException {
            return PrometheusOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, OceanusUnits.class)
                : null;
        }

        /**
         * Access the value as Ratio.
         * @return the ratio
         * @throws OceanusException on error
         */
        OceanusRatio getRatioValue() throws OceanusException {
            return PrometheusOdfValue.FLOAT.equals(theValueType)
                   ? theStore.parseValue(theIndex, (String) theValue, OceanusRatio.class)
                   : null;
        }

        /**
         * Access the value as Money.
         * @return the money
         * @throws OceanusException on error
         */
        OceanusMoney getMoneyValue() throws OceanusException {
            switch (theValueType) {
                case CURRENCY:
                    return theStore.parseCurrency(theIndex, (Double) theValue, theCurrency, OceanusMoney.class);
                case FLOAT:
                    return theStore.parseValue(theIndex, theText, OceanusMoney.class);
                default:
                    return null;
            }
        }

        /**
         * Access the value as Price.
         * @return the price
         * @throws OceanusException on error
         */
        OceanusPrice getPriceValue() throws OceanusException {
            switch (theValueType) {
                case CURRENCY:
                    return theStore.parseCurrency(theIndex, (Double) theValue, theCurrency, OceanusPrice.class);
                case FLOAT:
                    return theStore.parseValue(theIndex, theText, OceanusPrice.class);
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
