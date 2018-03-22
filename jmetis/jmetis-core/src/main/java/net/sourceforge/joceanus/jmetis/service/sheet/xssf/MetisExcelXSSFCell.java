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
package net.sourceforge.joceanus.jmetis.service.sheet.xssf;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCell;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellAddress;
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
 * Class representing a cell within a sheet or a view.
 */
public class MetisExcelXSSFCell
        extends MetisSheetCell {
    /**
     * The underlying row.
     */
    private final MetisExcelXSSFRow theExcelRow;

    /**
     * The Excel Cell.
     */
    private final XSSFCell theExcelCell;

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
    protected MetisExcelXSSFCell(final MetisExcelXSSFRow pRow,
                                 final XSSFCell pExcelCell,
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
            final MetisSheetCellAddress myAddress = new MetisSheetCellAddress(theExcelRow.getSheet().getName(), getPosition());
            throw new MetisDataException(pSource, "Bad Value at Cell "
                                                  + myAddress, e);
        }
    }

    @Override
    public Boolean getBooleanValue() {
        switch (theExcelCell.getCellTypeEnum()) {
            case BOOLEAN:
                return theExcelCell.getBooleanCellValue();
            case FORMULA:
                final CellValue myValue = theExcelRow.evaluateFormula(theExcelCell);
                return CellType.BOOLEAN == myValue.getCellTypeEnum()
                                                                     ? myValue.getBooleanValue()
                                                                     : null;
            default:
                return null;
        }
    }

    @Override
    public TethysDate getDateValue() {
        return CellType.NUMERIC == theExcelCell.getCellTypeEnum()
                                                                  ? new TethysDate(theExcelCell.getDateCellValue())
                                                                  : null;
    }

    @Override
    public Integer getIntegerValue() {
        switch (theExcelCell.getCellTypeEnum()) {
            case NUMERIC:
                final Double myValue = theExcelCell.getNumericCellValue();
                return myValue.intValue();
            case FORMULA:
                final CellValue myCellValue = theExcelRow.evaluateFormula(theExcelCell);
                return CellType.NUMERIC == myCellValue.getCellTypeEnum()
                                                                         ? ((Double) myCellValue.getNumberValue()).intValue()
                                                                         : null;
            default:
                return null;
        }
    }

    @Override
    public Long getLongValue() {
        switch (theExcelCell.getCellTypeEnum()) {
            case NUMERIC:
                final Double myValue = theExcelCell.getNumericCellValue();
                return myValue.longValue();
            case FORMULA:
                final CellValue myCellValue = theExcelRow.evaluateFormula(theExcelCell);
                return CellType.NUMERIC == myCellValue.getCellTypeEnum()
                                                                         ? ((Double) myCellValue.getNumberValue()).longValue()
                                                                         : null;
            default:
                return null;
        }
    }

    @Override
    public String getStringValue() {
        switch (theExcelCell.getCellTypeEnum()) {
            case NUMERIC:
            case BOOLEAN:
                /* Pick up the formatted value */
                return theExcelRow.formatCellValue(theExcelCell);

            case FORMULA:
                return getStringFormulaValue();

            case STRING:
            default:
                return theExcelCell.getStringCellValue();
        }
    }

    /**
     * Resolve the formula value as string.
     * @return the resolved value
     */
    private String getStringFormulaValue() {
        final CellValue myValue = theExcelRow.evaluateFormula(theExcelCell);
        switch (myValue.getCellTypeEnum()) {
            case STRING:
            case NUMERIC:
            case BOOLEAN:
                return myValue.formatAsString();
            default:
                return null;
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
    protected void setLong(final Long pValue) throws OceanusException {
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
    protected void setCellStyle(final XSSFCellStyle pStyle) {
        theExcelCell.setCellStyle(pStyle);
    }
}
