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

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.DataConverter;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.WorkBookType;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;

/**
 * Class representing a cell within a sheet or a view.
 */
public class DataCell {
    /**
     * Sheet type.
     */
    private final WorkBookType theBookType;

    /**
     * Is cell readOnly?
     */
    private final boolean isReadOnly;

    /**
     * The underlying row.
     */
    private final DataRow theRow;

    /**
     * The underlying view.
     */
    private final DataView theView;

    /**
     * The position of the cell.
     */
    private final CellPosition thePosition;

    /**
     * The Excel Cell.
     */
    private final HSSFCell theExcelCell;

    /**
     * The Oasis Cell.
     */
    private final OdfTableCell theOasisCell;

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public DataSheet getSheet() {
        return theRow.getSheet();
    }

    /**
     * Obtain the underlying row.
     * @return the underlying row
     */
    public DataRow getRow() {
        return theRow;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public DataView getView() {
        return theView;
    }

    /**
     * Obtain the cell position.
     * @return position
     */
    public CellPosition getPosition() {
        return thePosition;
    }

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pExcelCell the Excel Cell
     */
    protected DataCell(final DataRow pRow,
                       final HSSFCell pExcelCell) {
        /* Store parameters */
        theRow = pRow;
        theExcelCell = pExcelCell;
        theOasisCell = null;
        theBookType = WorkBookType.ExcelXLS;
        theView = pRow.getView();
        int myIndex = pExcelCell.getColumnIndex();
        if (theView != null) {
            myIndex -= theView.getFirstCell().getColumnIndex();
        }
        thePosition = new CellPosition(pRow.getRowIndex(), myIndex);
        isReadOnly = pRow.isReadOnly();
    }

    /**
     * Constructor.
     * @param pRow the row for the cell
     * @param pOasisCell the Oasis Cell
     */
    protected DataCell(final DataRow pRow,
                       final OdfTableCell pOasisCell) {
        /* Store parameters */
        theRow = pRow;
        theExcelCell = null;
        theOasisCell = pOasisCell;
        theBookType = WorkBookType.OasisODS;
        theView = pRow.getView();
        int myIndex = pOasisCell.getColumnIndex();
        if (theView != null) {
            myIndex -= theView.getFirstCell().getColumnIndex();
        }
        thePosition = new CellPosition(pRow.getRowIndex(), myIndex);
        isReadOnly = pRow.isReadOnly();
    }

    /**
     * Check for readOnly.
     * @throws JDataException on error
     */
    private void checkReadOnly() throws JDataException {
        if (isReadOnly) {
            throw new JDataException(ExceptionClass.LOGIC, "Attempt to modify readOnly Book");
        }
    }

    /**
     * Obtain boolean value of the cell.
     * @return the boolean value
     */
    public Boolean getBooleanValue() {
        switch (theBookType) {
            case ExcelXLS:
                return theExcelCell.getBooleanCellValue();
            case OasisODS:
                return theOasisCell.getBooleanValue();
            default:
                return null;
        }
    }

    /**
     * Obtain date value of the cell.
     * @return the date value
     */
    public Date getDateValue() {
        switch (theBookType) {
            case ExcelXLS:
                return theExcelCell.getDateCellValue();
            case OasisODS:
                Calendar myCalendar = theOasisCell.getDateValue();
                return (myCalendar == null) ? null : myCalendar.getTime();
            default:
                return null;
        }
    }

    /**
     * Obtain integer value of the cell.
     * @return the integer value
     */
    public Integer getIntegerValue() {
        Double myValue;
        switch (theBookType) {
            case ExcelXLS:
                myValue = theExcelCell.getNumericCellValue();
                return (myValue == null) ? null : myValue.intValue();
            case OasisODS:
                myValue = theOasisCell.getDoubleValue();
                return (myValue == null) ? null : myValue.intValue();
            default:
                return null;
        }
    }

    /**
     * Obtain string value of the cell.
     * @return the string value
     */
    public String getStringValue() {
        switch (theBookType) {
            case ExcelXLS:
                /* Handle the various field types */
                switch (theExcelCell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                    default:
                        return theExcelCell.getStringCellValue();

                    case Cell.CELL_TYPE_NUMERIC:
                        /* Pick up the formatted value */
                        return Double.toString(theExcelCell.getNumericCellValue());

                    case Cell.CELL_TYPE_BOOLEAN:
                        /* Pick up the formatted value */
                        return theRow.formatCellValue(theExcelCell);

                    case Cell.CELL_TYPE_FORMULA:
                        /* Pick up the formatted value */
                        CellValue myValue = theRow.evaluateFormula(theExcelCell);
                        return myValue.formatAsString();
                }
            case OasisODS:
                return theOasisCell.getDisplayText();
            default:
                return null;
        }
    }

    /**
     * Obtain byte array value of the cell.
     * @return the byte array value
     * @throws JDataException on error
     */
    public byte[] getBytesValue() throws JDataException {
        String myValue = getStringValue();
        return (myValue == null) ? null : DataConverter.hexStringToBytes(myValue);
    }

    /**
     * Obtain char array value of the cell.
     * @return the char array value
     * @throws JDataException on error
     */
    public char[] getCharArrayValue() throws JDataException {
        byte[] myValue = getBytesValue();
        return (myValue == null) ? null : DataConverter.bytesToCharArray(myValue);
    }

    /**
     * Set null value for the cell.
     * @throws JDataException on error
     */
    public void setNullValue() throws JDataException {
        /* Check for readOnly */
        checkReadOnly();

        switch (theBookType) {
            case ExcelXLS:
                theExcelCell.setCellValue((String) null);
                break;
            case OasisODS:
                theOasisCell.removeContent();
                break;
            default:
                break;
        }
    }

    /**
     * Set boolean value of the cell.
     * @param pValue the integer value
     * @throws JDataException on error
     */
    public void setBooleanValue(final Boolean pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Check for readOnly */
            checkReadOnly();

            switch (theBookType) {
                case ExcelXLS:
                    theExcelCell.setCellValue(pValue);
                    break;
                case OasisODS:
                    theOasisCell.setBooleanValue(pValue);
                    break;
                default:
                    break;
            }

            /* Set the style for the cell */
            theRow.setCellStyle(this, CellStyleType.Boolean);
        }
    }

    /**
     * Set date value of the cell.
     * @param pValue the date value
     * @throws JDataException on error
     */
    public void setDateValue(final Date pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Check for readOnly */
            checkReadOnly();

            switch (theBookType) {
                case ExcelXLS:
                    theExcelCell.setCellValue(pValue);
                    break;
                case OasisODS:
                    Calendar myCalendar = Calendar.getInstance();
                    myCalendar.setTime(pValue);
                    theOasisCell.setDateValue(myCalendar);
                    break;
                default:
                    break;
            }

            /* Set the style for the cell */
            theRow.setCellStyle(this, CellStyleType.Date);
        }
    }

    /**
     * Set integer value of the cell.
     * @param pValue the integer value
     * @throws JDataException on error
     */
    public void setIntegerValue(final Integer pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Check for readOnly */
            checkReadOnly();

            /* Convert to string */
            Double myValue = pValue.doubleValue();
            switch (theBookType) {
                case ExcelXLS:
                    theExcelCell.setCellValue(myValue);
                    break;
                case OasisODS:
                    theOasisCell.setDoubleValue(myValue);
                    break;
                default:
                    break;
            }

            /* Set the style for the cell */
            theRow.setCellStyle(this, CellStyleType.Integer);
        }
    }

    /**
     * Set string value of the cell.
     * @param pValue the string value
     * @throws JDataException on error
     */
    public void setStringValue(final String pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Check for readOnly */
            checkReadOnly();

            switch (theBookType) {
                case ExcelXLS:
                    theExcelCell.setCellValue(pValue);
                    break;
                case OasisODS:
                    theOasisCell.setStringValue(pValue);
                    break;
                default:
                    break;
            }

            /* Set the style for the cell */
            theRow.setCellStyle(this, CellStyleType.String);
        }
    }

    /**
     * Set decimal value of the cell.
     * @param pValue the decimal value
     * @throws JDataException on error
     */
    public void setDecimalValue(final JDecimal pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Check for readOnly */
            checkReadOnly();

            switch (theBookType) {
                case ExcelXLS:
                    theExcelCell.setCellValue(pValue.doubleValue());
                    break;
                case OasisODS:
                    if (pValue instanceof JRate) {
                        theOasisCell.setPercentageValue(pValue.doubleValue());
                        theOasisCell.setDisplayText(pValue.toString());
                    } else {
                        theOasisCell.setDoubleValue(pValue.doubleValue());
                    }
                    break;
                default:
                    break;
            }

            /* Set the style for the cell */
            theRow.setCellStyle(this, getCellStyle(pValue));
        }
    }

    /**
     * Set header value of the cell.
     * @param pValue the string value
     * @throws JDataException on error
     */
    public void setHeaderValue(final String pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Set as string value */
            setStringValue(pValue);

            /* Adjust the style for the cell */
            theRow.setCellStyle(this, CellStyleType.Header);
        }
    }

    /**
     * Set byte array value of the cell.
     * @param pValue the byte array value
     * @throws JDataException on error
     */
    public void setBytesValue(final byte[] pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Convert value to string */
            setStringValue(DataConverter.bytesToHexString(pValue));
        }
    }

    /**
     * Set char array value of the cell.
     * @param pValue the byte array value
     * @throws JDataException on error
     */
    public void setCharArrayValue(final char[] pValue) throws JDataException {
        /* Handle null values */
        if (pValue == null) {
            setNullValue();
        } else {
            /* Convert value to string */
            setBytesValue(DataConverter.charsToByteArray(pValue));
        }
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the value
     * @return the required CellStyle
     */
    protected static CellStyleType getCellStyle(final JDecimal pValue) {
        if (pValue instanceof JPrice) {
            return CellStyleType.Price;
        }
        if (pValue instanceof JMoney) {
            return CellStyleType.Money;
        }
        if (pValue instanceof JUnits) {
            return CellStyleType.Units;
        }
        if (pValue instanceof JRate) {
            return CellStyleType.Rate;
        }
        if (pValue instanceof JDilution) {
            return CellStyleType.Dilution;
        }
        return null;
    }

    /**
     * Set cell style.
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final HSSFCellStyle pStyle) {
        theExcelCell.setCellStyle(pStyle);
    }

    /**
     * Set cell style.
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final String pStyle) {
        theOasisCell.getOdfElement().setTableStyleNameAttribute(pStyle);
    }
}
