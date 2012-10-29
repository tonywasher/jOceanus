/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.sheets;

import java.util.Date;
import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.DataConverter;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem.EncryptedList;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetWriter.CellStyleType;
import net.sourceforge.jOceanus.jDataModels.sheets.SpreadSheet.SheetType;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem class for accessing a sheet that is related to a data type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class SheetDataItem<T extends DataItem & Comparable<? super T>> {
    /**
     * ID column.
     */
    protected static final int COL_ID = 0;

    /**
     * ControlId column.
     */
    protected static final int COL_CONTROLID = COL_ID + 1;

    /**
     * Character width.
     */
    protected static final int WIDTH_CHAR = 256;

    /**
     * Date width.
     */
    protected static final int WIDTH_DATE = 11;

    /**
     * Integer width.
     */
    protected static final int WIDTH_INT = 8;

    /**
     * Boolean width.
     */
    protected static final int WIDTH_BOOL = 8;

    /**
     * Money width.
     */
    protected static final int WIDTH_MONEY = 13;

    /**
     * Units width.
     */
    protected static final int WIDTH_UNITS = 13;

    /**
     * Rate width.
     */
    protected static final int WIDTH_RATE = 13;

    /**
     * Dilution width.
     */
    protected static final int WIDTH_DILUTION = 13;

    /**
     * Price width.
     */
    protected static final int WIDTH_PRICE = 15;

    /**
     * The task control.
     */
    private final TaskControl<?> theTask;

    /**
     * The input sheet.
     */
    private SheetReader<?> theReader = null;

    /**
     * The output sheet.
     */
    private SheetWriter<?> theWriter = null;

    /**
     * The workbook.
     */
    private Workbook theWorkBook = null;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one?
     */
    private final boolean isBackup;

    /**
     * Do we adjust columns in the spreadsheet for an encrypted data item?
     */
    private boolean isAdjusted = false;

    /**
     * The DataList.
     */
    private DataList<T> theList = null;

    /**
     * The name of the related range.
     */
    private String theRangeName = null;

    /**
     * The WorkSheet of the range.
     */
    private Sheet theWorkSheet = null;

    /**
     * DataFormatter.
     */
    private DataFormatter theFormatter = null;

    /**
     * The Active row.
     */
    private Row theActiveRow = null;

    /**
     * The Row number of the current row.
     */
    private int theCurrRow = 0;

    /**
     * The Row number of the base row.
     */
    private int theBaseRow = 0;

    /**
     * The Column number of the base column.
     */
    private int theBaseCol = 0;

    /**
     * Is the sheet a backup or editable sheet.
     * @return true/false
     */
    protected boolean isBackup() {
        return isBackup;
    }

    /**
     * Constructor for a load operation.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected SheetDataItem(final SheetReader<?> pReader,
                            final String pRange) {
        /* Store parameters */
        theTask = pReader.getTask();
        theReader = pReader;
        theRangeName = pRange;
        theFormatter = new DataFormatter();

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);
    }

    /**
     * Constructor for a write operation.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected SheetDataItem(final SheetWriter<?> pWriter,
                            final String pRange) {
        /* Store parameters */
        theTask = pWriter.getTask();
        theWriter = pWriter;
        theWorkBook = pWriter.getWorkBook();
        theRangeName = pRange;

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);
    }

    /**
     * Set the DataList.
     * @param pList the Data list
     */
    protected void setDataList(final DataList<T> pList) {
        /* Store parameters */
        theList = pList;
        isAdjusted = (!isBackup) && (theList instanceof EncryptedList);
    }

    /**
     * Load the DataItems from a spreadsheet.
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    public boolean loadSpreadSheet() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the workbook */
            theWorkBook = theReader.getWorkBook();

            /* Find the range of cells */
            AreaReference myRange = null;
            Name myName = theWorkBook.getName(theRangeName);
            if (myName != null) {
                myRange = new AreaReference(myName.getRefersToFormula());
            }

            /* Declare the new stage */
            if (!theTask.setNewStage(theRangeName)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = theTask.getReportingSteps();
            int myCount = 0;

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                theWorkSheet = theWorkBook.getSheet(myTop.getSheetName());
                theBaseCol = myTop.getCol();

                /* Count the number of data items */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Declare the number of steps */
                if (!theTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the range */
                for (theCurrRow = myTop.getRow(); theCurrRow <= myBottom.getRow(); theCurrRow++) {
                    /* Access the row */
                    theActiveRow = theWorkSheet.getRow(theCurrRow);

                    /* load the item */
                    if (isBackup) {
                        loadSecureItem();
                    } else {
                        loadOpenItem();
                    }

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!theTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* Sort the list */
                theList.reSort();

                /* Post process the load */
                postProcessOnLoad();
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load " + theRangeName, e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Write the DataItems to a spreadsheet.
     * @return continue to write <code>true/false</code>
     * @throws JDataException on error
     */
    protected boolean writeSpreadSheet() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Declare the new stage */
            if (!theTask.setNewStage(theRangeName)) {
                return false;
            }

            /* Create the sheet */
            theWorkSheet = theWorkBook.createSheet(theRangeName);

            /* Access the number of reporting steps */
            int mySteps = theTask.getReportingSteps();

            /* Count the number of items */
            int myTotal = theList.size();

            /* Declare the number of steps */
            if (!theTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Initialise counts */
            theBaseRow = 0;
            theBaseCol = 0;
            theCurrRow = theBaseRow;
            int myCount = 0;

            /* If this is an open write */
            if (!isBackup) {
                /* Format Header */
                formatHeader();
            }

            /* Access the iterator */
            Iterator<T> myIterator = theList.iterator();

            /* Loop through the data items */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Create the new row */
                newRow();

                /* insert the item */
                if (isBackup) {
                    insertSecureItem(myCurr);
                } else {
                    insertOpenItem(myCurr);
                }

                /* Report the progress */
                myCount++;
                theCurrRow++;
                if (((myCount % mySteps) == 0) && (!theTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* If this is an open write */
            if (!isBackup) {
                /* Freeze the titles */
                freezeTitles();
            }

            /* If data was written then post-process */
            if (theCurrRow > theBaseRow) {
                postProcessOnWrite();
            }
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to create " + theRangeName, e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Adjust column.
     * @param pColumn the initial column
     * @return the adjusted column
     */
    private int adjustColumn(final int pColumn) {
        /* Initialise the result */
        int myCol = pColumn;

        /* If we are should adjust the column */
        if ((isAdjusted) && (myCol > COL_CONTROLID)) {
            /* Decrement column */
            myCol--;
        }

        /* return the adjusted column */
        return myCol;
    }

    /**
     * Load secure item from spreadsheet.
     * @throws JDataException on error
     */
    protected abstract void loadSecureItem() throws JDataException;

    /**
     * Load open item from spreadsheet.
     * @throws JDataException on error
     */
    protected void loadOpenItem() throws JDataException {
    }

    /**
     * Insert secure item into spreadsheet.
     * @param pItem the item
     * @throws JDataException on error
     */
    protected abstract void insertSecureItem(final T pItem) throws JDataException;

    /**
     * Insert open item into spreadsheet.
     * @param pItem the item
     * @throws JDataException on error
     */
    protected void insertOpenItem(final T pItem) throws JDataException {
    }

    /**
     * PostProcess on load.
     * @throws JDataException on error
     */
    protected void postProcessOnLoad() throws JDataException {
    }

    /**
     * Format Sheet Header and Columns prior to writing.
     * @throws JDataException on error
     */
    protected void formatSheetHeader() throws JDataException {
    }

    /**
     * PostProcess on write.
     * @throws JDataException on error
     */
    protected abstract void postProcessOnWrite() throws JDataException;

    /**
     * Adjust for header.
     * @throws JDataException on error
     */
    private void formatHeader() throws JDataException {
        /* Create a new row */
        newRow();

        /* Write the Id header */
        writeHeader(COL_ID, DataItem.FIELD_ID.getName());

        /* Hide the ID column */
        setHiddenColumn(COL_ID);
        setIntegerColumn(COL_ID);

        /* Format Sheet Header */
        formatSheetHeader();

        /* Adjust rows */
        theCurrRow++;
        theBaseRow++;
    }

    /**
     * Create a new row.
     */
    protected void newRow() {
        /* Create the new row */
        theActiveRow = theWorkSheet.createRow(theCurrRow);
    }

    /**
     * Name the basic range.
     * @param pLastCol the last column in the range
     */
    protected void nameRange(final int pLastCol) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pLastCol);

        /* Build the basic name */
        Name myName = theWorkBook.createName();
        String mySheet = theWorkSheet.getSheetName();
        myName.setNameName(theRangeName);

        /* Build the area reference */
        CellReference myFirst = new CellReference(mySheet, theBaseRow, theBaseCol, true, true);
        CellReference myLast = new CellReference(mySheet, theCurrRow - 1, theBaseCol + myCol, true, true);
        AreaReference myArea = new AreaReference(myFirst, myLast);
        String myRef = myArea.formatAsString();

        /* Set into Name */
        myName.setRefersToFormula(myRef);
        // writeString(pNumCols-1, "EndOfData");
    }

    /**
     * Name the column range.
     * @param pOffset offset of column
     * @param pName name of range
     */
    protected void nameColumnRange(final int pOffset,
                                   final String pName) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Build the basic name */
        Name myName = theWorkBook.createName();
        String mySheet = theWorkSheet.getSheetName();
        myName.setNameName(pName);

        /* Build the area reference */
        CellReference myFirst = new CellReference(mySheet, theBaseRow, theBaseCol + myCol, true, true);
        CellReference myLast = new CellReference(mySheet, theCurrRow - 1, theBaseCol + myCol, true, true);
        AreaReference myArea = new AreaReference(myFirst, myLast);
        String myRef = myArea.formatAsString();

        /* Set into Name */
        myName.setRefersToFormula(myRef);
    }

    /**
     * Apply Data Validation.
     * @param pOffset offset of column
     * @param pList name of validation range
     */
    public void applyDataValidation(final int pOffset,
                                    final String pList) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Create the CellAddressList */
        CellRangeAddressList myRange = new CellRangeAddressList(theBaseRow, theCurrRow - 1, myCol, myCol);

        /* Create the constraint */
        DVConstraint myConstraint = DVConstraint.createFormulaListConstraint(pList);

        /* Link the two and use drip down arrow */
        DataValidation myValidation = new HSSFDataValidation(myRange, myConstraint);
        myValidation.setSuppressDropDownArrow(false);

        /* Apply to the sheet */
        theWorkSheet.addValidationData(myValidation);
    }

    /**
     * Freeze titles.
     */
    protected void freezeTitles() {
        /* Freeze the top row */
        theWorkSheet.createFreezePane(theBaseCol + 2, theBaseRow);
    }

    /**
     * Set Hidden column.
     * @param pOffset the offset of the column
     */
    protected void setHiddenColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply to the sheet */
        theWorkSheet.setColumnHidden(theBaseCol + myCol, true);
    }

    /**
     * Set Date column.
     * @param pOffset the offset of the column
     */
    protected void setDateColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Date));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_DATE * WIDTH_CHAR);
    }

    /**
     * Set Money column.
     * @param pOffset the offset of the column
     */
    protected void setMoneyColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Money));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_MONEY * WIDTH_CHAR);
    }

    /**
     * Set Price column.
     * @param pOffset the offset of the column
     */
    protected void setPriceColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Price));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_PRICE * WIDTH_CHAR);
    }

    /**
     * Set Units column.
     * @param pOffset the offset of the column
     */
    protected void setUnitsColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Units));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_UNITS * WIDTH_CHAR);
    }

    /**
     * Set Rate column.
     * @param pOffset the offset of the column
     */
    protected void setRateColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Rate));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_RATE * WIDTH_CHAR);
    }

    /**
     * Set Dilution column.
     * @param pOffset the offset of the column
     */
    protected void setDilutionColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet
                .setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Dilution));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_DILUTION * WIDTH_CHAR);
    }

    /**
     * Set Boolean column.
     * @param pOffset the offset of the column
     */
    protected void setBooleanColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Boolean));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_BOOL * WIDTH_CHAR);
    }

    /**
     * Set Integer column.
     * @param pOffset the offset of the column
     */
    protected void setIntegerColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(theBaseCol + myCol, theWriter.getCellStyle(CellStyleType.Integer));
        theWorkSheet.setColumnWidth(theBaseCol + myCol, WIDTH_INT * WIDTH_CHAR);
    }

    /**
     * Set Column width.
     * @param pOffset the offset of the column
     * @param pNumChars the number of characters
     */
    protected void setColumnWidth(final int pOffset,
                                  final int pNumChars) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply to the sheet */
        theWorkSheet.setColumnWidth(theBaseCol + myCol, pNumChars * WIDTH_CHAR);
    }

    /**
     * Access an integer from the WorkSheet.
     * @param pOffset the column offset
     * @return the integer
     */
    protected Integer loadInteger(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        Cell myCell = theActiveRow.getCell(theBaseCol + myCol);
        Integer myInt = null;
        if (myCell != null) {
            myInt = Integer.parseInt(myCell.getStringCellValue());
        }

        /* Return the value */
        return myInt;
    }

    /**
     * Access a boolean from the WorkSheet.
     * @param pOffset the column offset
     * @return the date
     */
    protected Boolean loadBoolean(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        Cell myCell = theActiveRow.getCell(theBaseCol + myCol);
        Boolean myValue = null;
        if (myCell != null) {
            myValue = myCell.getBooleanCellValue();
        }

        /* Return the value */
        return myValue;
    }

    /**
     * Access a date from the WorkSheet.
     * @param pOffset the column offset
     * @return the date
     */
    protected Date loadDate(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        Cell myCell = theActiveRow.getCell(theBaseCol + myCol);
        Date myDate = null;
        if (myCell != null) {
            myDate = myCell.getDateCellValue();
        }

        /* Return the value */
        return myDate;
    }

    /**
     * Access a string from the WorkSheet.
     * @param pOffset the column offset
     * @return the string
     */
    protected String loadString(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        Cell myCell = theActiveRow.getCell(theBaseCol + myCol);
        String myValue = null;
        if (myCell != null) {
            /* If we are trying for a string representation of a non-string field */
            if (myCell.getCellType() != Cell.CELL_TYPE_STRING) {
                /* Pick up the formatted value */
                myValue = theFormatter.formatCellValue(myCell);

                /* Else pick up the standard value */
            } else {
                myValue = myCell.getStringCellValue();
            }
        }

        /* Return the value */
        return myValue;
    }

    /**
     * Access a byte array from the WorkSheet.
     * @param pOffset the column offset
     * @return the byte array
     * @throws JDataException on error
     */
    protected byte[] loadBytes(final int pOffset) throws JDataException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        Cell myCell = theActiveRow.getCell(theBaseCol + myCol);
        byte[] myBytes = null;
        if (myCell != null) {
            myBytes = DataConverter.hexStringToBytes(myCell.getStringCellValue());
        }

        /* Return the value */
        return myBytes;
    }

    /**
     * Access a char array from the WorkSheet.
     * @param pOffset the column offset
     * @return the char array
     * @throws JDataException on error
     */
    protected char[] loadChars(final int pOffset) throws JDataException {
        /* Access the bytes */
        byte[] myBytes = loadBytes(pOffset);
        char[] myChars = null;
        if (myBytes != null) {
            myChars = DataConverter.bytesToCharArray(myBytes);
        }

        /* Return the value */
        return myChars;
    }

    /**
     * Write an integer to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the integer
     */
    protected void writeInteger(final int pOffset,
                                final Integer pValue) {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            Cell myCell = theActiveRow.createCell(theBaseCol + myCol);
            myCell.setCellValue(pValue.toString());
            myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Integer));
        }
    }

    /**
     * Write a boolean to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the boolean
     */
    protected void writeBoolean(final int pOffset,
                                final Boolean pValue) {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            Cell myCell = theActiveRow.createCell(theBaseCol + myCol);
            myCell.setCellValue(pValue.booleanValue());
            myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Boolean));
        }
    }

    /**
     * Write a date to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the date
     */
    protected void writeDate(final int pOffset,
                             final JDateDay pValue) {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            Cell myCell = theActiveRow.createCell(theBaseCol + myCol);
            myCell.setCellValue(pValue.getDate());
            myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Date));
        }
    }

    /**
     * Write a number to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the number
     */
    protected void writeNumber(final int pOffset,
                               final JDecimal pValue) {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            Cell myCell = theActiveRow.createCell(theBaseCol + myCol);
            myCell.setCellValue(pValue.doubleValue());
            myCell.setCellStyle(theWriter.getCellStyle(pValue));
        }
    }

    /**
     * Write a Header to the WorkSheet.
     * @param pOffset the column offset
     * @param pHeader the header text
     */
    protected void writeHeader(final int pOffset,
                               final String pHeader) {
        /* If we have non-null value */
        if (pHeader != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            Cell myCell = theActiveRow.createCell(theBaseCol + myCol);
            myCell.setCellValue(pHeader);
            myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Header));
        }
    }

    /**
     * Write a string to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the string
     */
    protected void writeString(final int pOffset,
                               final String pValue) {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            Cell myCell = theActiveRow.createCell(theBaseCol + myCol);
            myCell.setCellValue(pValue);
            myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.String));
        }
    }

    /**
     * Write a byte array to the WorkSheet.
     * @param pOffset the column offset
     * @param pBytes the byte array
     */
    protected void writeBytes(final int pOffset,
                              final byte[] pBytes) {
        /* If we have non-null bytes */
        if (pBytes != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            Cell myCell = theActiveRow.createCell(theBaseCol + myCol);
            myCell.setCellValue(DataConverter.bytesToHexString(pBytes));
            myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.String));
        }
    }

    /**
     * Write a char array to the WorkSheet.
     * @param pOffset the column offset
     * @param pChars the char array
     * @throws JDataException on error
     */
    protected void writeChars(final int pOffset,
                              final char[] pChars) throws JDataException {
        /* If we have non-null chars */
        if (pChars != null) {
            /* Create the cell and add to the sheet */
            byte[] myBytes = DataConverter.charsToByteArray(pChars);
            writeBytes(pOffset, myBytes);
        }
    }
}
