/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.sheets;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.StaticData;
import net.sourceforge.JDataModels.data.TaskControl;
import net.sourceforge.JDataModels.sheets.SheetDataItem;
import net.sourceforge.JDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.JDataModels.sheets.SpreadSheet.SheetType;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.Event.EventList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.sheets.FinanceSheet.YearRange;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for Event.
 * @author Tony Washer
 */
public class SheetEvent extends SheetDataItem<Event> {
    /**
     * NamedArea for Events.
     */
    private static final String AREA_EVENTS = Event.LIST_NAME;

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 10;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * Date column.
     */
    private static final int COL_DATE = 2;

    /**
     * Description column.
     */
    private static final int COL_DESC = 3;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = 4;

    /**
     * Debit column.
     */
    private static final int COL_DEBIT = 5;

    /**
     * Credit column.
     */
    private static final int COL_CREDIT = 6;

    /**
     * Units column.
     */
    private static final int COL_UNITS = 7;

    /**
     * Dilution column.
     */
    private static final int COL_DILUTION = 8;

    /**
     * TransType column.
     */
    private static final int COL_TRAN = 9;

    /**
     * TaxCredit column.
     */
    private static final int COL_TAXCRED = 10;

    /**
     * Years column.
     */
    private static final int COL_YEARS = 11;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one.
     */
    private final boolean isBackup;

    /**
     * Events data list.
     */
    private final EventList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEvent(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTS);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getEvents();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEvent(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTS);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Events list */
        theList = pWriter.getData().getEvents();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws JDataException {

        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            int myControlId = loadInteger(COL_CONTROL);
            int myDebitId = loadInteger(COL_DEBIT);
            int myCreditId = loadInteger(COL_CREDIT);
            int myTranId = loadInteger(COL_TRAN);

            /* Access the date and years */
            Date myDate = loadDate(COL_DATE);
            Integer myYears = loadInteger(COL_YEARS);

            /* Access the binary values */
            byte[] myDesc = loadBytes(COL_DESC);
            byte[] myAmount = loadBytes(COL_AMOUNT);
            byte[] myTaxCredit = loadBytes(COL_TAXCRED);
            byte[] myUnits = loadBytes(COL_UNITS);
            byte[] myDilution = loadBytes(COL_DILUTION);

            /* Load the item */
            theList.addSecureItem(myID, myControlId, myDate, myDesc, myAmount, myDebitId, myCreditId,
                                  myUnits, myTranId, myTaxCredit, myDilution, myYears);

            /* else this is a load from an edit-able spreadsheet */
        } else {
            /* Access the Account */
            int myID = loadInteger(COL_ID);
            String myDebit = loadString(COL_DEBIT - 1);
            String myCredit = loadString(COL_CREDIT - 1);
            String myTransType = loadString(COL_TRAN - 1);

            /* Access the date and name and description bytes */
            Date myDate = loadDate(COL_DATE - 1);
            Integer myYears = loadInteger(COL_YEARS - 1);

            /* Access the binary values */
            String myDesc = loadString(COL_DESC - 1);
            String myAmount = loadString(COL_AMOUNT - 1);
            String myUnits = loadString(COL_UNITS - 1);
            String myTaxCredit = loadString(COL_TAXCRED - 1);
            String myDilution = loadString(COL_DILUTION - 1);

            /* Load the item */
            theList.addOpenItem(myID, myDate, myDesc, myAmount, myDebit, myCredit, myUnits, myTransType,
                                myTaxCredit, myDilution, myYears);
        }
    }

    @Override
    protected void insertItem(final Event pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());
            writeDate(COL_DATE, pItem.getDate());
            writeInteger(COL_DEBIT, pItem.getDebit().getId());
            writeInteger(COL_CREDIT, pItem.getCredit().getId());
            writeInteger(COL_TRAN, pItem.getTransType().getId());
            writeBytes(COL_DESC, pItem.getDescBytes());
            writeBytes(COL_AMOUNT, pItem.getAmountBytes());
            writeBytes(COL_UNITS, pItem.getUnitsBytes());
            writeBytes(COL_DILUTION, pItem.getDilutionBytes());
            writeBytes(COL_TAXCRED, pItem.getTaxCreditBytes());
            writeInteger(COL_YEARS, pItem.getYears());

            /* else we are creating an edit-able spreadsheet */
        } else {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeDate(COL_DATE - 1, pItem.getDate());
            writeString(COL_DESC - 1, pItem.getDesc());
            writeNumber(COL_AMOUNT - 1, pItem.getAmount());
            writeString(COL_DEBIT - 1, pItem.getDebit().getName());
            writeString(COL_CREDIT - 1, pItem.getCredit().getName());
            writeNumber(COL_UNITS - 1, pItem.getUnits());
            writeNumber(COL_DILUTION - 1, pItem.getDilution());
            writeString(COL_TRAN - 1, pItem.getTransType().getName());
            writeNumber(COL_TAXCRED - 1, pItem.getTaxCredit());
            writeInteger(COL_YEARS - 1, pItem.getYears());
        }
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
        /* Ignore if we are creating a backup */
        if (isBackup) {
            return;
        }

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(COL_ID, DataItem.FIELD_ID.getName());
        writeHeader(COL_DATE - 1, Event.FIELD_DATE.getName());
        writeHeader(COL_DESC - 1, Event.FIELD_DESC.getName());
        writeHeader(COL_AMOUNT - 1, Event.FIELD_AMOUNT.getName());
        writeHeader(COL_DEBIT - 1, Event.FIELD_DEBIT.getName());
        writeHeader(COL_CREDIT - 1, Event.FIELD_CREDIT.getName());
        writeHeader(COL_UNITS - 1, Event.FIELD_UNITS.getName());
        writeHeader(COL_DILUTION - 1, Event.FIELD_DILUTION.getName());
        writeHeader(COL_TRAN - 1, Event.FIELD_TRNTYP.getName());
        writeHeader(COL_TAXCRED - 1, Event.FIELD_TAXCREDIT.getName());
        writeHeader(COL_YEARS - 1, Event.FIELD_YEARS.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the twelve columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
        } else {
            /* Set the eleven columns as the range */
            nameRange(NUM_COLS - 1);

            /* Hide the ID column */
            setHiddenColumn(COL_ID);
            setIntegerColumn(COL_ID);

            /* Set the Account column width */
            setColumnWidth(COL_DESC - 1, Event.DESCLEN);
            setColumnWidth(COL_DEBIT - 1, Account.NAMELEN);
            applyDataValidation(COL_DEBIT - 1, SheetAccount.AREA_ACCOUNTNAMES);
            setColumnWidth(COL_CREDIT - 1, Account.NAMELEN);
            applyDataValidation(COL_CREDIT - 1, SheetAccount.AREA_ACCOUNTNAMES);
            setColumnWidth(COL_TRAN - 1, StaticData.NAMELEN);
            applyDataValidation(COL_TRAN - 1, SheetTransactionType.AREA_TRANSTYPENAMES);

            /* Set Number columns */
            setDateColumn(COL_DATE - 1);
            setMoneyColumn(COL_AMOUNT - 1);
            setUnitsColumn(COL_UNITS - 1);
            setDilutionColumn(COL_DILUTION - 1);
            setMoneyColumn(COL_TAXCRED - 1);
            setIntegerColumn(COL_YEARS - 1);
        }
    }

    /**
     * Load the Accounts from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData,
                                         final YearRange pRange) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Access the list of events */
            EventList myList = pData.getEvents();

            /* Loop through the columns of the table */
            for (Integer j = pRange.getMinYear(); j <= pRange.getMaxYear(); j++) {
                /* Find the range of cells */
                String myRangeName = j.toString();
                myRangeName = "Finance" + myRangeName.substring(2);
                AreaReference myRange = pHelper.resolveAreaReference(myRangeName);

                /* Declare the new stage */
                if (!pTask.setNewStage("Events from " + j)) {
                    return false;
                }

                /* If we found the range OK */
                if (myRange != null) {
                    /* Access the relevant sheet and Cell references */
                    CellReference myTop = myRange.getFirstCell();
                    CellReference myBottom = myRange.getLastCell();
                    Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                    int myCol = myTop.getCol();

                    /* Count the number of Events */
                    int myTotal = myBottom.getRow() - myTop.getRow();

                    /* Declare the number of steps */
                    if (!pTask.setNumSteps(myTotal)) {
                        return false;
                    }

                    /* Loop through the rows of the table */
                    for (int i = myTop.getRow() + 1; i <= myBottom.getRow(); i++) {
                        /* Access the row */
                        Row myRow = mySheet.getRow(i);
                        int iAdjust = 0;

                        /* Access date */
                        Date myDate = myRow.getCell(myCol + iAdjust++).getDateCellValue();

                        /* Access the values */
                        String myDesc = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                        String myAmount = pHelper.formatNumericCell(myRow.getCell(myCol + iAdjust++));
                        String myDebit = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                        String myCredit = myRow.getCell(myCol + iAdjust++).getStringCellValue();

                        /* Handle Dilution which may be missing */
                        Cell myCell = myRow.getCell(myCol + iAdjust++);
                        String myDilution = null;
                        if (myCell != null) {
                            myDilution = pHelper.formatNumericCell(myCell);
                            if (!myDilution.startsWith("0.")) {
                                myDilution = null;
                            }
                        }

                        /* Handle Units which may be missing */
                        myCell = myRow.getCell(myCol + iAdjust++);
                        String myUnits = null;
                        if (myCell != null) {
                            myUnits = pHelper.formatNumericCell(myCell);
                        }

                        /* Handle transaction type */
                        String myTranType = myRow.getCell(myCol + iAdjust++).getStringCellValue();

                        /* Handle Tax Credit which may be missing */
                        myCell = myRow.getCell(myCol + iAdjust++);
                        String myTaxCredit = null;
                        if (myCell != null) {
                            myTaxCredit = pHelper.formatNumericCell(myCell);
                        }

                        /* Handle Years which may be missing */
                        myCell = myRow.getCell(myCol + iAdjust++);
                        Integer myYears = null;
                        if (myCell != null) {
                            myYears = pHelper.parseIntegerCell(myCell);
                        }

                        /* Add the event */
                        myList.addOpenItem(0, myDate, myDesc, myAmount, myDebit, myCredit, myUnits,
                                           myTranType, myTaxCredit, myDilution, myYears);

                        /* Report the progress */
                        myCount++;
                        if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                            return false;
                        }
                    }

                    /* Sort the list */
                    myList.reSort();
                }
            }
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Events", e);
        }

        /* Return to caller */
        return true;
    }
}
