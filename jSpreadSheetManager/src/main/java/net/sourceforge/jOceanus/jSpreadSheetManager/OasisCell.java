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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisWorkBook.OasisStyle;

import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
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
     */
    private <T> T parseValue(final String pSource,
                             final Class<T> pClass) {
        OasisRow myRow = theCellMap.getRow();
        return myRow.parseValue(pSource, pClass);
    }

    @Override
    public Boolean getBooleanValue() {
        String myType = theOasisCell.getOfficeValueTypeAttribute();
        if ((myType != null)
            && (OfficeValueTypeAttribute.Value.enumValueOf(myType) == OfficeValueTypeAttribute.Value.BOOLEAN)) {
            return theOasisCell.getOfficeBooleanValueAttribute();
        }
        return null;
    }

    @Override
    public Date getDateValue() {
        String myType = theOasisCell.getOfficeValueTypeAttribute();
        if ((myType != null)
            && (OfficeValueTypeAttribute.Value.enumValueOf(myType) == OfficeValueTypeAttribute.Value.DATE)) {
            String myDate = theOasisCell.getOfficeDateValueAttribute();
            return parseValue(myDate, Date.class);
        }
        return null;
    }

    @Override
    public Integer getIntegerValue() {
        String myType = theOasisCell.getOfficeValueTypeAttribute();
        if ((myType != null)
            && (OfficeValueTypeAttribute.Value.enumValueOf(myType) == OfficeValueTypeAttribute.Value.FLOAT)) {
            Double myValue = theOasisCell.getOfficeValueAttribute();
            return myValue.intValue();
        }
        return null;
    }

    @Override
    public String getStringValue() {
        String myType = theOasisCell.getOfficeValueTypeAttribute();
        if ((myType != null)
            && ((OfficeValueTypeAttribute.Value.enumValueOf(myType) == OfficeValueTypeAttribute.Value.STRING)
                || (OfficeValueTypeAttribute.Value.enumValueOf(myType) == OfficeValueTypeAttribute.Value.PERCENTAGE) || (OfficeValueTypeAttribute.Value
                    .enumValueOf(myType) == OfficeValueTypeAttribute.Value.FLOAT))) {
            return theOasisCell.getTextContent();
        }
        return null;
    }

    @Override
    public void setNullValue() throws JDataException {
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
    protected void setBoolean(final Boolean pValue) throws JDataException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.BOOLEAN.toString());
            theOasisCell.setOfficeBooleanValueAttribute(pValue);
            theOasisCell.setTableStyleNameAttribute(OasisWorkBook.getStyleName(OasisStyle.BooleanCell));
        }
    }

    @Override
    protected void setDate(final Date pValue) throws JDataException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.DATE.toString());
            theOasisCell.setOfficeDateValueAttribute(formatValue(pValue));
            theOasisCell.setTableStyleNameAttribute(OasisWorkBook.getStyleName(OasisStyle.DateCell));
        }
    }

    @Override
    protected void setInteger(final Integer pValue) throws JDataException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.FLOAT.toString());
            theOasisCell.setOfficeValueAttribute(pValue.doubleValue());
            theOasisCell.setTableStyleNameAttribute(OasisWorkBook.getStyleName(OasisStyle.IntegerCell));
        }
    }

    @Override
    protected void setString(final String pValue) throws JDataException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
            setTextContent(pValue);
            theOasisCell.setTableStyleNameAttribute(OasisWorkBook.getStyleName(OasisStyle.StringCell));
        }
    }

    @Override
    protected void setDecimal(final JDecimal pValue) throws JDataException {
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
            CellStyleType myStyleType = getCellStyle(pValue);
            OasisStyle myStyle = OasisWorkBook.getOasisCellStyle(myStyleType);
            theOasisCell.setTableStyleNameAttribute(OasisWorkBook.getStyleName(myStyle));
        }
    }

    @Override
    protected void setHeader(final String pValue) throws JDataException {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Remove existing content */
            removeCellContent();

            /* Set value type and value */
            theOasisCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
            setTextContent(pValue);
            theOasisCell.setTableStyleNameAttribute(OasisWorkBook.getStyleName(OasisStyle.HeaderCell));
        }
    }
}
