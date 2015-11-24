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
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.dateday.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

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
     * @throws OceanusException on error
     */
    private <T> T parseValue(final String pSource,
                             final Class<T> pClass) throws OceanusException {
        try {
            return theExcelRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            OasisCellAddress myAddress = new OasisCellAddress(theExcelRow.getSheet().getName(), getPosition());
            throw new JMetisDataException(pSource, "Bad Value at Cell "
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
    public TethysDate getDateValue() {
        switch (theExcelCell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                return new TethysDate(theExcelCell.getDateCellValue());
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
            theExcelCell.setCellValue((String) null);
        }
    }

    @Override
    protected void setBoolean(final Boolean pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue);

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setDate(final TethysDate pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue.toDate());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setInteger(final Integer pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue.doubleValue());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setString(final String pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue);

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
        }
    }

    @Override
    protected void setHeader(final String pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set as string value */
            theExcelCell.setCellValue(pValue);

            /* Adjust the style for the cell */
            theExcelRow.setAlternateCellStyle(this, pValue);
        }
    }

    @Override
    protected void setDecimal(final TethysDecimal pValue) throws OceanusException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue.doubleValue());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, pValue);
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
        theExcelCell.setCellStyle(pStyle);
    }
}
