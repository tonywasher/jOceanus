/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.sheets;

import java.util.Iterator;

import net.sourceforge.joceanus.jprometheus.JPrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem.EncryptedList;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jspreadsheetmanager.CellPosition;
import net.sourceforge.joceanus.jspreadsheetmanager.CellStyleType;
import net.sourceforge.joceanus.jspreadsheetmanager.DataCell;
import net.sourceforge.joceanus.jspreadsheetmanager.DataRow;
import net.sourceforge.joceanus.jspreadsheetmanager.DataSheet;
import net.sourceforge.joceanus.jspreadsheetmanager.DataView;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * The Active view.
     */
    private DataView theActiveView = null;

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
     * @throws JOceanusException on error
     */
    public boolean loadSpreadSheet() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the workbook */
            theWorkBook = theReader.getWorkBook();

            /* Access the view of the range */
            theActiveView = theWorkBook.getRangeView(theRangeName);
            if (theActiveView == null) {
                return true;
            }
            Iterator<DataRow> myIterator = theActiveView.iterator();

            /* Declare the new stage */
            if (!theTask.setNewStage(theRangeName)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = theTask.getReportingSteps();
            int myCount = 0;

            /* Determine count of rows */
            int myTotal = theActiveView.getRowCount();

            /* Declare the number of steps */
            if (!theTask.setNumSteps((isDoubleLoad)
                    ? myTotal << 1
                    : myTotal)) {
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
                myIterator = theActiveView.iterator();
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

            /* Post process the load */
            postProcessOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JPrometheusIOException("Failed to Load "
                                             + theRangeName, e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Write the DataItems to a spreadsheet.
     * @return continue to write <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected boolean writeSpreadSheet() throws JOceanusException {
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
            int myNumRows = (isBackup)
                    ? myTotal
                    : myTotal + 1;
            int myNumCols = getLastColumn() + 1;

            /* Create the sheet */
            theWorkSheet = theWorkBook.newSheet(theRangeName, myNumRows, myNumCols);

            /* Initialise counts */
            theBaseRow = 0;
            theCurrRow = theBaseRow;
            int myCount = 0;

            /* If this is an open write */
            if (!isBackup) {
                /* Create a new row */
                newRow();

                /* Format Header */
                formatHeader();
            }

            /* Access the iterator */
            Iterator<T> myItemIterator = theList.iterator();

            /* Loop through the data items */
            while (myItemIterator.hasNext()) {
                T myCurr = myItemIterator.next();

                /* Create the new row */
                newRow();

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
        } catch (JOceanusException e) {
            throw new JPrometheusIOException("Failed to create "
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
     * @throws JOceanusException on error
     */
    protected abstract void loadSecureItem(final Integer pId) throws JOceanusException;

    /**
     * Load open item from spreadsheet.
     * @param pId the id
     * @throws JOceanusException on error
     */
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
    }

    /**
     * Load second pass.
     * @param pId the id
     * @throws JOceanusException on error
     */
    protected void loadSecondPass(final Integer pId) throws JOceanusException {
    }

    /**
     * Insert secure item into spreadsheet.
     * @param pItem the item
     * @throws JOceanusException on error
     */
    protected abstract void insertSecureItem(final T pItem) throws JOceanusException;

    /**
     * Insert open item into spreadsheet.
     * @param pItem the item
     * @throws JOceanusException on error
     */
    protected void insertOpenItem(final T pItem) throws JOceanusException {
    }

    /**
     * PostProcess on load.
     * @throws JOceanusException on error
     */
    protected void postProcessOnLoad() throws JOceanusException {
        /* Sort the list */
        theList.reSort();
    }

    /**
     * Prepare sheet for writing.
     * @throws JOceanusException on error
     */
    protected void prepareSheet() throws JOceanusException {
    }

    /**
     * Format sheet after writing.
     * @throws JOceanusException on error
     */
    protected void formatSheet() throws JOceanusException {
    }

    /**
     * Determine last active column.
     * @return the last active column
     */
    protected abstract int getLastColumn();

    /**
     * Adjust for header.
     * @throws JOceanusException on error
     */
    private void formatHeader() throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    private void formatData() throws JOceanusException {
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
        theActiveRow = theWorkSheet.getMutableRowByIndex(theCurrRow);
    }

    /**
     * Name the basic range.
     * @throws JOceanusException on error
     */
    protected void nameRange() throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = getLastColumn();
        myCol = adjustColumn(myCol);

        /* Name the range */
        CellPosition myFirst = new CellPosition(0, theBaseRow);
        CellPosition myLast = new CellPosition(myCol, theCurrRow - 1);
        theWorkSheet.declareRange(theRangeName, myFirst, myLast);
    }

    /**
     * Name the column range.
     * @param pOffset offset of column
     * @param pName name of range
     * @throws JOceanusException on error
     */
    protected void nameColumnRange(final int pOffset,
                                   final String pName) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public void applyDataValidation(final int pOffset,
                                    final String pList) throws JOceanusException {
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
     * @param pOffset column offset to freeze at
     * @throws JOceanusException on error
     */
    protected void applyDataFilter(final int pOffset) throws JOceanusException {
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
        theWorkSheet.getMutableColumnByIndex(myCol).setHidden(true);
    }

    /**
     * Set Date column.
     * @param pOffset the offset of the column
     */
    protected void setDateColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.DATE);
    }

    /**
     * Set String column.
     * @param pOffset the offset of the column
     */
    protected void setStringColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.STRING);
    }

    /**
     * Set Money column.
     * @param pOffset the offset of the column
     */
    protected void setMoneyColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.MONEY);
    }

    /**
     * Set Price column.
     * @param pOffset the offset of the column
     */
    protected void setPriceColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.PRICE);
    }

    /**
     * Set Units column.
     * @param pOffset the offset of the column
     */
    protected void setUnitsColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.UNITS);
    }

    /**
     * Set Rate column.
     * @param pOffset the offset of the column
     */
    protected void setRateColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.RATE);
    }

    /**
     * Set Dilution column.
     * @param pOffset the offset of the column
     */
    protected void setDilutionColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.DILUTION);
    }

    /**
     * Set Ratio column.
     * @param pOffset the offset of the column
     */
    protected void setRatioColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.RATIO);
    }

    /**
     * Set Boolean column.
     * @param pOffset the offset of the column
     */
    protected void setBooleanColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.BOOLEAN);
    }

    /**
     * Set Integer column.
     * @param pOffset the offset of the column
     */
    protected void setIntegerColumn(final int pOffset) {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(myCol).setDefaultCellStyle(CellStyleType.INTEGER);
    }

    /**
     * Access an integer from the WorkSheet.
     * @param pOffset the column offset
     * @return the integer
     * @throws JOceanusException on error
     */
    protected Integer loadInteger(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getIntegerValue()
                : null;
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
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getBooleanValue()
                : null;
    }

    /**
     * Access a date from the WorkSheet.
     * @param pOffset the column offset
     * @return the date
     * @throws JOceanusException on error
     */
    protected JDateDay loadDate(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getDateValue()
                : null;
    }

    /**
     * Access a money value from the WorkSheet.
     * @param pOffset the column offset
     * @return the money
     * @throws JOceanusException on error
     */
    protected JMoney loadMoney(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getMoneyValue()
                : null;
    }

    /**
     * Access a price value from the WorkSheet.
     * @param pOffset the column offset
     * @return the price
     * @throws JOceanusException on error
     */
    protected JPrice loadPrice(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getPriceValue()
                : null;
    }

    /**
     * Access a rate value from the WorkSheet.
     * @param pOffset the column offset
     * @return the rate
     * @throws JOceanusException on error
     */
    protected JRate loadRate(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getRateValue()
                : null;
    }

    /**
     * Access a units value from the WorkSheet.
     * @param pOffset the column offset
     * @return the units
     * @throws JOceanusException on error
     */
    protected JUnits loadUnits(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getUnitsValue()
                : null;
    }

    /**
     * Access a dilution value from the WorkSheet.
     * @param pOffset the column offset
     * @return the dilution
     * @throws JOceanusException on error
     */
    protected JDilution loadDilution(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getDilutionValue()
                : null;
    }

    /**
     * Access a ratio value from the WorkSheet.
     * @param pOffset the column offset
     * @return the ratio
     * @throws JOceanusException on error
     */
    protected JRatio loadRatio(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getRatioValue()
                : null;
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
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getStringValue()
                : null;
    }

    /**
     * Access a byte array from the WorkSheet.
     * @param pOffset the column offset
     * @return the byte array
     * @throws JOceanusException on error
     */
    protected byte[] loadBytes(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getBytesValue()
                : null;
    }

    /**
     * Access a char array from the WorkSheet.
     * @param pOffset the column offset
     * @return the char array
     * @throws JOceanusException on error
     */
    protected char[] loadChars(final int pOffset) throws JOceanusException {
        /* Adjust column if necessary */
        int myCol = adjustColumn(pOffset);

        /* Access the cells by reference */
        DataCell myCell = theActiveView.getRowCellByIndex(theActiveRow, myCol);

        /* Return the value */
        return (myCell != null)
                ? myCell.getCharArrayValue()
                : null;
    }

    /**
     * Write an integer to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the integer
     * @throws JOceanusException on error
     */
    protected void writeInteger(final int pOffset,
                                final Integer pValue) throws JOceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setIntegerValue(pValue);
        }
    }

    /**
     * Write a boolean to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the boolean
     * @throws JOceanusException on error
     */
    protected void writeBoolean(final int pOffset,
                                final Boolean pValue) throws JOceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setBooleanValue(pValue);
        }
    }

    /**
     * Write a date to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the date
     * @throws JOceanusException on error
     */
    protected void writeDate(final int pOffset,
                             final JDateDay pValue) throws JOceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setDateValue(pValue);
        }
    }

    /**
     * Write a decimal to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the number
     * @throws JOceanusException on error
     */
    protected void writeDecimal(final int pOffset,
                                final JDecimal pValue) throws JOceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setDecimalValue(pValue);
        }
    }

    /**
     * Write a Header to the WorkSheet.
     * @param pOffset the column offset
     * @param pHeader the header text
     * @throws JOceanusException on error
     */
    protected void writeHeader(final int pOffset,
                               final String pHeader) throws JOceanusException {
        /* If we have non-null value */
        if (pHeader != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setHeaderValue(pHeader);
        }
    }

    /**
     * Write a string to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the string
     * @throws JOceanusException on error
     */
    protected void writeString(final int pOffset,
                               final String pValue) throws JOceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setStringValue(pValue);
        }
    }

    /**
     * Write a byte array to the WorkSheet.
     * @param pOffset the column offset
     * @param pBytes the byte array
     * @throws JOceanusException on error
     */
    protected void writeBytes(final int pOffset,
                              final byte[] pBytes) throws JOceanusException {
        /* If we have non-null bytes */
        if (pBytes != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setBytesValue(pBytes);
        }
    }

    /**
     * Write a char array to the WorkSheet.
     * @param pOffset the column offset
     * @param pChars the char array
     * @throws JOceanusException on error
     */
    protected void writeChars(final int pOffset,
                              final char[] pChars) throws JOceanusException {
        /* If we have non-null chars */
        if (pChars != null) {
            /* Adjust column if necessary */
            int myCol = adjustColumn(pOffset);

            /* Create the cell and set its value */
            DataCell myCell = theActiveRow.getMutableCellByIndex(myCol);
            myCell.setCharArrayValue(pChars);
        }
    }
}
