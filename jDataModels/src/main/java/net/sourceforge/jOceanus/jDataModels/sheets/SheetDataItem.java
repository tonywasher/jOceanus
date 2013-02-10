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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem.EncryptedList;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jSpreadSheetManager.CellPosition;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataCell;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataRow;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataSheet;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;

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
     * The workbook.
     */
    private DataWorkBook theWorkBook = null;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one?
     */
    private final boolean isBackup;

    /**
     * Do we need two passes to load the spreadSheet?
     */
    private boolean isDoubleLoad = false;

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
    private final String theRangeName;

    /**
     * The WorkSheet of the range.
     */
    private DataSheet theWorkSheet = null;

    /**
     * The Active row.
     */
    private DataRow theActiveRow = null;

    /**
     * The Row number of the current row.
     */
    private int theCurrRow = 0;

    /**
     * The Row number of the base row.
     */
    private int theBaseRow = 0;

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

        /* Note whether this is a backup */
        isBackup = pReader.isBackup();
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
        theWorkBook = pWriter.getWorkBook();
        theRangeName = pRange;

        /* Note whether this is a backup */
        isBackup = pWriter.isBackup();
    }

    /**
     * Set the DataList.
     * @param pList the Data list
     */
    protected void setDataList(final DataList<T> pList) {
        /* Store parameters */
        theList = pList;
        isAdjusted = (!isBackup)
                     && (theList instanceof EncryptedList);
    }

    /**
     * Request double load.
     */
    protected void requestDoubleLoad() {
        /* Store request */
        isDoubleLoad = true;
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

            /* Access the view of the range */
            DataView myView = theWorkBook.getRangeView(theRangeName);
            Iterator<DataRow> myIterator = myView.iterator();

            /* Declare the new stage */
            if (!theTask.setNewStage(theRangeName)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = theTask.getReportingSteps();
            int myCount = 0;

            /* Determine count of rows */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            if (!theTask.setNumSteps((isDoubleLoad) ? myTotal << 1 : myTotal)) {
                return false;
            }

            /* Loop through the rows of the range */
            for (theCurrRow = 0; myIterator.hasNext(); theCurrRow++) {
                /* Access the row */
                theActiveRow = myIterator.next();

                /* Access the ID */
                Integer myID = loadInteger(COL_ID);

                /* load the item */
                if (isBackup) {
                    loadSecureItem(myID);
                } else {
                    loadOpenItem(myID);
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!theTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* If we need a second pass */
            if (isDoubleLoad) {
                /* Loop through the rows of the range */
                myIterator = myView.iterator();
                for (theCurrRow = 0; myIterator.hasNext(); theCurrRow++) {
                    /* Access the row */
                    theActiveRow = myIterator.next();

                    /* Access the ID */
                    Integer myID = loadInteger(COL_ID);

                    /* load the item */
                    loadSecondPass(myID);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0)
                        && (!theTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Sort the list */
            theList.reSort();

            /* Post process the load */
            postProcessOnLoad();

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load "
                                                           + theRangeName, e);
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

            /* Access the number of reporting steps */
            int mySteps = theTask.getReportingSteps();

            /* Count the number of items */
            int myTotal = theList.size();

            /* Declare the number of steps */
            if (!theTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Determine size of sheet */
            int myNumRows = (isBackup) ? myTotal : myTotal + 1;
            int myNumCols = getLastColumn() + 1;

            /* Create the sheet */
            theWorkSheet = theWorkBook.newSheet(theRangeName, myNumRows, myNumCols);
            Iterator<DataRow> myRowIterator = theWorkSheet.iterator();

            /* Initialise counts */
            theBaseRow = 0;
            theCurrRow = theBaseRow;
            int myCount = 0;

            /* If this is an open write */
            if (!isBackup) {
                /* Create a new row */
                theActiveRow = myRowIterator.next();

                /* Format Header */
                formatHeader();
            }

            /* Access the iterator */
            Iterator<T> myItemIterator = theList.iterator();

            /* Loop through the data items */
            while (myItemIterator.hasNext()) {
                T myCurr = myItemIterator.next();

                /* Create the new row */
                theActiveRow = myRowIterator.next();

                /* Write the id */
                writeInteger(COL_ID, myCurr.getId());

                /* insert the item */
                if (isBackup) {
                    insertSecureItem(myCurr);
                } else {
                    insertOpenItem(myCurr);
                }

                /* Report the progress */
                myCount++;
                theCurrRow++;
                if (((myCount % mySteps) == 0)
                    && (!theTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* If this is an open write */
            if (!isBackup) {
                /* format the data */
                formatData();
            }

            /* If data was written then name the range */
            if (theCurrRow > theBaseRow) {
                nameRange();
            }
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to create "
                                                           + theRangeName, e);
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

        /* If we should adjust the column */
        if ((isAdjusted)
            && (myCol > COL_CONTROLID)) {
            /* Decrement column */
            myCol--;
        }

        /* return the adjusted column */
        return myCol;
    }

    /**
     * Load secure item from spreadsheet.
     * @param pId the id
     * @throws JDataException on error
     */
    protected abstract void loadSecureItem(final Integer pId) throws JDataException;

    /**
     * Load open item from spreadsheet.
     * @param pId the id
     * @throws JDataException on error
     */
    protected void loadOpenItem(final Integer pId) throws JDataException {
    }

    /**
     * Load second pass.
     * @param pId the id
     * @throws JDataException on error
     */
    protected void loadSecondPass(final Integer pId) throws JDataException {
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
     * Prepare sheet for writing.
     * @throws JDataException on error
     */
    protected void prepareSheet() throws JDataException {
    }

    /**
     * Format sheet after writing.
     * @throws JDataException on error
     */
    protected void formatSheet() throws JDataException {
    }

    /**
     * Determine last active column.
     * @return the last active column
     */
    protected abstract int getLastColumn();

    /**
     * Adjust for header.
     * @throws JDataException on error
     */
    private void formatHeader() throws JDataException {
        /* Write the Id header */
        writeHeader(COL_ID, DataItem.FIELD_ID.getName());

        /* Prepare sheet */
        prepareSheet();

        /* Adjust rows */
        theCurrRow++;
        theBaseRow++;
    }

    /**
     * Format sheet after data has been written.
     * @throws JDataException on error
     */
    private void formatData() throws JDataException {
        /* Hide the ID column */
        setIntegerColumn(COL_ID);
        setHiddenColumn(COL_ID);

        /* Freeze the titles */
        freezeTitles();

        /* Format the sheet data */
        formatSheet();
    }

    /**
     * Create a new row.
     */
    protected void newRow() {
        /* Create the new row */
        theActiveRow = theWorkSheet.getRowByIndex(theCurrRow);
    }

    /**
     * Name the basic range.
     * @throws JDataException on error
     */
    protected void nameRange() throws JDataException {
        /* Adjust column if necessary */
        int myCol = getLastColumn();
        myCol = adjustColumn(myCol);

        /* Name the range */
        CellPosition myFirst = new CellPosition(0, theBaseRow);
        CellPosition myLast = new CellPosition(myCol, theCurrRow - 1);
        theWorkSheet.declareRange(theRangeName, myFirst, myLast);
        // writeString(pNumCols-1, "EndOfData");
    }

    /**
     * Name the column range.
     * @param pOffset offset of column
     * @param pName name of range
     * @throws JDataException on error
     */
    protected void nameColumnRange(final int pOffset,
                                   final String pName) throws JDataException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Name the range */
        CellPosition myFirst = new CellPosition(myCol, theBaseRow);
        CellPosition myLast = new CellPosition(myCol, theCurrRow - 1);
        theWorkSheet.declareRange(pName, myFirst, myLast);
    }

    /**
     * Apply Data Validation.
     * @param pOffset offset of column
     * @param pList name of validation range
     * @throws JDataException on error
     */
    public void applyDataValidation(final int pOffset,
                                    final String pList) throws JDataException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Name the range */
        CellPosition myFirst = new CellPosition(myCol, theBaseRow);
        CellPosition myLast = new CellPosition(myCol, theCurrRow - 1);
        theWorkSheet.applyDataValidation(myFirst, myLast, pList);
    }

    /**
     * Freeze titles.
     */
    protected void freezeTitles() {
        /* Freeze the top row */
        CellPosition myPoint = new CellPosition(2, theBaseRow);
        theWorkSheet.createFreezePane(myPoint);
    }

    /**
     * Freeze titles.
     */
    protected void applyDataFilter(final int pOffset) throws JDataException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Freeze the top row */
        CellPosition myPoint = new CellPosition(myCol, 0);
        theWorkSheet.applyDataFilter(myPoint, theCurrRow);
    }

    /**
     * Set Hidden column.
     * @param pOffset the offset of the column
     */
    protected void setHiddenColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply to the sheet */
        theWorkSheet.setColumnHidden(myCol, true);
    }

    /**
     * Set Date column.
     * @param pOffset the offset of the column
     */
    protected void setDateColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Date);
        theWorkSheet.setColumnWidth(myCol, WIDTH_DATE);
    }

    /**
     * Set Money column.
     * @param pOffset the offset of the column
     */
    protected void setMoneyColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Money);
        theWorkSheet.setColumnWidth(myCol, WIDTH_MONEY);
    }

    /**
     * Set Price column.
     * @param pOffset the offset of the column
     */
    protected void setPriceColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Price);
        theWorkSheet.setColumnWidth(myCol, WIDTH_PRICE);
    }

    /**
     * Set Units column.
     * @param pOffset the offset of the column
     */
    protected void setUnitsColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Units);
        theWorkSheet.setColumnWidth(myCol, WIDTH_UNITS);
    }

    /**
     * Set Rate column.
     * @param pOffset the offset of the column
     */
    protected void setRateColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Rate);
        theWorkSheet.setColumnWidth(myCol, WIDTH_RATE);
    }

    /**
     * Set Dilution column.
     * @param pOffset the offset of the column
     */
    protected void setDilutionColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Dilution);
        theWorkSheet.setColumnWidth(myCol, WIDTH_DILUTION);
    }

    /**
     * Set Boolean column.
     * @param pOffset the offset of the column
     */
    protected void setBooleanColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Boolean);
        theWorkSheet.setColumnWidth(myCol, WIDTH_BOOL);
    }

    /**
     * Set Integer column.
     * @param pOffset the offset of the column
     */
    protected void setIntegerColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.setDefaultColumnStyle(myCol, CellStyleType.Integer);
        theWorkSheet.setColumnWidth(myCol, WIDTH_INT);
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
        theWorkSheet.setColumnWidth(myCol, pNumChars);
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
        DataCell myCell = theActiveRow.getCellByIndex(myCol);

        /* Return the value */
        return (myCell != null) ? myCell.getIntegerValue() : null;
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
        DataCell myCell = theActiveRow.getCellByIndex(myCol);

        /* Return the value */
        return (myCell != null) ? myCell.getBooleanValue() : null;
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
        DataCell myCell = theActiveRow.getCellByIndex(myCol);

        /* Return the value */
        return (myCell != null) ? myCell.getDateValue() : null;
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
        DataCell myCell = theActiveRow.getCellByIndex(myCol);

        /* Return the value */
        return (myCell != null) ? myCell.getStringValue() : null;
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
        DataCell myCell = theActiveRow.getCellByIndex(myCol);

        /* Return the value */
        return (myCell != null) ? myCell.getBytesValue() : null;
    }

    /**
     * Access a char array from the WorkSheet.
     * @param pOffset the column offset
     * @return the char array
     * @throws JDataException on error
     */
    protected char[] loadChars(final int pOffset) throws JDataException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveRow.getCellByIndex(myCol);

        /* Return the value */
        return (myCell != null) ? myCell.getCharArrayValue() : null;
    }

    /**
     * Write an integer to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the integer
     * @throws JDataException on error
     */
    protected void writeInteger(final int pOffset,
                                final Integer pValue) throws JDataException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setIntegerValue(pValue);
        }
    }

    /**
     * Write a boolean to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the boolean
     * @throws JDataException on error
     */
    protected void writeBoolean(final int pOffset,
                                final Boolean pValue) throws JDataException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setBooleanValue(pValue);
        }
    }

    /**
     * Write a date to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the date
     * @throws JDataException on error
     */
    protected void writeDate(final int pOffset,
                             final JDateDay pValue) throws JDataException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setDateValue(pValue.getDate());
        }
    }

    /**
     * Write a decimal to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the number
     * @throws JDataException on error
     */
    protected void writeDecimal(final int pOffset,
                                final JDecimal pValue) throws JDataException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setDecimalValue(pValue);
        }
    }

    /**
     * Write a Header to the WorkSheet.
     * @param pOffset the column offset
     * @param pHeader the header text
     * @throws JDataException on error
     */
    protected void writeHeader(final int pOffset,
                               final String pHeader) throws JDataException {
        /* If we have non-null value */
        if (pHeader != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setHeaderValue(pHeader);
        }
    }

    /**
     * Write a string to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the string
     * @throws JDataException on error
     */
    protected void writeString(final int pOffset,
                               final String pValue) throws JDataException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setStringValue(pValue);
        }
    }

    /**
     * Write a byte array to the WorkSheet.
     * @param pOffset the column offset
     * @param pBytes the byte array
     * @throws JDataException on error
     */
    protected void writeBytes(final int pOffset,
                              final byte[] pBytes) throws JDataException {
        /* If we have non-null bytes */
        if (pBytes != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setBytesValue(pBytes);
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
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.createCellByIndex(myCol);
            myCell.setCharArrayValue(pChars);
        }
    }
}
