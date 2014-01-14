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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jdecimal.JUnits;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellValue;

/**
 * Class representing a cell within a sheet or a view.
 */
public class ExcelCell
        extends DataCell {
    /**
     * The underlying row.
     */
    private final ExcelRow theExcelRow;

    /**
     * The Excel Cell.
     */
    private final HSSFCell theExcelCell;

    /**
     * Is the cell readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pExcelCell the Excel Cell
     * @param pColIndex the column index
     * @param pReadOnly is the cell readOnly?
     */
    protected ExcelCell(final ExcelRow pRow,
                        final HSSFCell pExcelCell,
                        final int pColIndex,
                        final boolean pReadOnly) {
        /* Store parameters */
        super(pRow, pColIndex);
        theExcelRow = pRow;
        theExcelCell = pExcelCell;
        isReadOnly = pReadOnly;
    }

    /**
     * Parse a value.
     * @param <T> the value type to parse
     * @param pSource the string to parse.
     * @param pClass the value type class.
     * @return the parsed value
     * @throws JDataException on error
     */
    private <T> T parseValue(final String pSource,
                             final Class<T> pClass) throws JDataException {
        try {
            return theExcelRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            OasisCellAddress myAddress = new OasisCellAddress(theExcelRow.getSheet().getName(), getPosition());
            throw new JDataException(ExceptionClass.DATA, pSource, "Bad Value at Cell "
                                                                   + myAddress, e);
        }
    }

    @Override
    public Boolean getBooleanValue() {
        switch (theExcelCell.getCellType()) {
            case HSSFCell.CELL_TYPE_BOOLEAN:
                return theExcelCell.getBooleanCellValue();
            case HSSFCell.CELL_TYPE_FORMULA:
                CellValue myValue = theExcelRow.evaluateFormula(theExcelCell);
                switch (myValue.getCellType()) {
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        return myValue.getBooleanValue();
                    default:
                        break;
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public JDateDay getDateValue() {
        switch (theExcelCell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                return new JDateDay(theExcelCell.getDateCellValue());
            default:
                return null;
        }
    }

    @Override
    public Integer getIntegerValue() {
        switch (theExcelCell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                Double myValue = theExcelCell.getNumericCellValue();
                return myValue.intValue();
            case HSSFCell.CELL_TYPE_FORMULA:
                CellValue myCellValue = theExcelRow.evaluateFormula(theExcelCell);
                switch (myCellValue.getCellType()) {
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        Double myDouble = myCellValue.getNumberValue();
                        return myDouble.intValue();
                    default:
                        break;
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public String getStringValue() {
        switch (theExcelCell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
            case HSSFCell.CELL_TYPE_BOOLEAN:
                /* Pick up the formatted value */
                return theExcelRow.formatCellValue(theExcelCell);

            case HSSFCell.CELL_TYPE_FORMULA:
                /* Pick up the formatted value */
                CellValue myValue = theExcelRow.evaluateFormula(theExcelCell);
                switch (myValue.getCellType()) {
                    case HSSFCell.CELL_TYPE_STRING:
                    case HSSFCell.CELL_TYPE_NUMERIC:
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        return myValue.formatAsString();
                    default:
                        break;
                }
                return null;

            case HSSFCell.CELL_TYPE_STRING:
            default:
                return theExcelCell.getStringCellValue();
        }
    }

    @Override
    public JMoney getMoneyValue() throws JDataException {
        return parseValue(getStringValue(), JMoney.class);
    }

    @Override
    public JPrice getPriceValue() throws JDataException {
        return parseValue(getStringValue(), JPrice.class);
    }

    @Override
    public JRate getRateValue() throws JDataException {
        return parseValue(getStringValue(), JRate.class);
    }

    @Override
    public JUnits getUnitsValue() throws JDataException {
        return parseValue(getStringValue(), JUnits.class);
    }

    @Override
    public JDilution getDilutionValue() throws JDataException {
        return parseValue(getStringValue(), JDilution.class);
    }

    @Override
    public JRatio getRatioValue() throws JDataException {
        return parseValue(getStringValue(), JRatio.class);
    }

    @Override
    public void setNullValue() throws JDataException {
        if (!isReadOnly) {
            theExcelCell.setCellValue((String) null);
        }
    }

    @Override
    protected void setBoolean(final Boolean pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue);

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setDate(final JDateDay pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue.getDate());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setInteger(final Integer pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue.doubleValue());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setString(final String pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue);

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setHeader(final String pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set as string value */
            theExcelCell.setCellValue(pValue);

            /* Adjust the style for the cell */
            theExcelRow.setAlternateCellStyle(this, pValue);
        }
    }

    @Override
    protected void setDecimal(final JDecimal pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue.doubleValue());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setMonetary(final JMoney pValue) throws JDataException {
        /* Pass through as decimal */
        setDecimal(pValue);
    }

    /**
     * Set cell style.
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final HSSFCellStyle pStyle) {
        theExcelCell.setCellStyle(pStyle);
    }
}
