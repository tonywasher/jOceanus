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

    @Override
    public Boolean getBooleanValue() {
        return theExcelCell.getBooleanCellValue();
    }

    @Override
    public Date getDateValue() {
        return theExcelCell.getDateCellValue();
    }

    @Override
    public Integer getIntegerValue() {
        Double myValue = theExcelCell.getNumericCellValue();
        return (myValue == null)
                ? null
                : myValue.intValue();
    }

    @Override
    public String getStringValue() {
        switch (theExcelCell.getCellType()) {
            case HSSFCell.CELL_TYPE_NUMERIC:
                /* Pick up the formatted value */
                return Double.toString(theExcelCell.getNumericCellValue());

            case HSSFCell.CELL_TYPE_BOOLEAN:
                /* Pick up the formatted value */
                return theExcelRow.formatCellValue(theExcelCell);

            case HSSFCell.CELL_TYPE_FORMULA:
                /* Pick up the formatted value */
                CellValue myValue = theExcelRow.evaluateFormula(theExcelCell);
                return myValue.formatAsString();

            case HSSFCell.CELL_TYPE_STRING:
            default:
                return theExcelCell.getStringCellValue();
        }
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
            theExcelRow.setCellStyle(this, CellStyleType.Boolean);
        }
    }

    @Override
    protected void setDate(final Date pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue);

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, CellStyleType.Date);
        }
    }

    @Override
    protected void setInteger(final Integer pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue((pValue == null)
                    ? null
                    : pValue.doubleValue());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, CellStyleType.Integer);
        }
    }

    @Override
    protected void setString(final String pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue);

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, CellStyleType.String);
        }
    }

    @Override
    protected void setHeader(final String pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set as string value */
            theExcelCell.setCellValue(pValue);

            /* Adjust the style for the cell */
            theExcelRow.setCellStyle(this, CellStyleType.Header);
        }
    }

    @Override
    protected void setDecimal(final JDecimal pValue) throws JDataException {
        if (!isReadOnly) {
            /* Set the value */
            theExcelCell.setCellValue(pValue.doubleValue());

            /* Set the style for the cell */
            theExcelRow.setCellStyle(this, getCellStyle(pValue));
        }
    }

    /**
     * Set cell style.
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final HSSFCellStyle pStyle) {
        theExcelCell.setCellStyle(pStyle);
    }
}
