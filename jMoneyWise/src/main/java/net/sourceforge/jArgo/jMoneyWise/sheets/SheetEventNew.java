/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jArgo.jMoneyWise.sheets;

import java.util.Date;

import net.sourceforge.jArgo.jDataManager.JDataException;
import net.sourceforge.jArgo.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jArgo.jDataModels.data.StaticData;
import net.sourceforge.jArgo.jDataModels.data.TaskControl;
import net.sourceforge.jArgo.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jArgo.jDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.jArgo.jMoneyWise.data.Account;
import net.sourceforge.jArgo.jMoneyWise.data.Event;
import net.sourceforge.jArgo.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jArgo.jMoneyWise.data.EventNew;
import net.sourceforge.jArgo.jMoneyWise.data.EventNew.EventNewList;
import net.sourceforge.jArgo.jMoneyWise.data.FinanceData;
import net.sourceforge.jArgo.jMoneyWise.data.statics.EventInfoClass;
import net.sourceforge.jArgo.jMoneyWise.sheets.FinanceSheet.YearRange;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for Event.
 * @author Tony Washer
 */
public class SheetEventNew extends SheetDataItem<EventNew> {
    /**
     * NamedArea for Events.
     */
    private static final String AREA_EVENTS = Event.LIST_NAME;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_CONTROLID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_DATE + 1;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = COL_DESC + 1;

    /**
     * Debit column.
     */
    private static final int COL_DEBIT = COL_AMOUNT + 1;

    /**
     * Credit column.
     */
    private static final int COL_CREDIT = COL_DEBIT + 1;

    /**
     * TransType column.
     */
    private static final int COL_TRAN = COL_CREDIT + 1;

    /**
     * Events data list.
     */
    private final EventNewList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventNew(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTS);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getNewEvents();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventNew(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTS);

        /* Access the Events list */
        theList = pWriter.getData().getNewEvents();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myDebitId = loadInteger(COL_DEBIT);
        Integer myCreditId = loadInteger(COL_CREDIT);
        Integer myTranId = loadInteger(COL_TRAN);

        /* Access the date and years */
        Date myDate = loadDate(COL_DATE);

        /* Access the binary values */
        byte[] myDesc = loadBytes(COL_DESC);
        byte[] myAmount = loadBytes(COL_AMOUNT);

        /* Load the item */
        theList.addSecureItem(myID, myControlId, myDate, myDesc, myAmount, myDebitId, myCreditId, myTranId);
    }

    @Override
    protected void loadOpenItem() throws JDataException {
        /* Access the Account */
        Integer myID = loadInteger(COL_ID);
        String myDebit = loadString(COL_DEBIT);
        String myCredit = loadString(COL_CREDIT);
        String myTransType = loadString(COL_TRAN);

        /* Access the date and name and description bytes */
        Date myDate = loadDate(COL_DATE);

        /* Access the binary values */
        String myDesc = loadString(COL_DESC);
        String myAmount = loadString(COL_AMOUNT);

        /* Load the item */
        theList.addOpenItem(myID, myDate, myDesc, myAmount, myDebit, myCredit, myTransType);
    }

    @Override
    protected void insertSecureItem(final EventNew pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROLID, pItem.getControlKey().getId());
        writeDate(COL_DATE, pItem.getDate());
        writeInteger(COL_DEBIT, pItem.getDebit().getId());
        writeInteger(COL_CREDIT, pItem.getCredit().getId());
        writeInteger(COL_TRAN, pItem.getTransType().getId());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
    }

    @Override
    protected void insertOpenItem(final EventNew pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeDate(COL_DATE, pItem.getDate());
        writeString(COL_DESC, pItem.getDesc());
        writeNumber(COL_AMOUNT, pItem.getAmount());
        writeString(COL_DEBIT, pItem.getDebit().getName());
        writeString(COL_CREDIT, pItem.getCredit().getName());
        writeString(COL_TRAN, pItem.getTransType().getName());
    }

    @Override
    protected void formatSheetHeader() throws JDataException {
        /* Write titles */
        writeHeader(COL_DATE, Event.FIELD_DATE.getName());
        writeHeader(COL_DESC, Event.FIELD_DESC.getName());
        writeHeader(COL_AMOUNT, Event.FIELD_AMOUNT.getName());
        writeHeader(COL_DEBIT, Event.FIELD_DEBIT.getName());
        writeHeader(COL_CREDIT, Event.FIELD_CREDIT.getName());
        writeHeader(COL_TRAN, Event.FIELD_TRNTYP.getName());

        /* Set the Account column width */
        setColumnWidth(COL_DESC, Event.DESCLEN);
        setColumnWidth(COL_DEBIT, Account.NAMELEN);
        setColumnWidth(COL_CREDIT, Account.NAMELEN);
        setColumnWidth(COL_TRAN, StaticData.NAMELEN);

        /* Set Number columns */
        setDateColumn(COL_DATE);
        setMoneyColumn(COL_AMOUNT);
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_TRAN);

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Apply validation */
            applyDataValidation(COL_DEBIT, SheetAccount.AREA_ACCOUNTNAMES);
            applyDataValidation(COL_CREDIT, SheetAccount.AREA_ACCOUNTNAMES);
            applyDataValidation(COL_TRAN, SheetTransactionType.AREA_TRANSTYPENAMES);
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
            EventNewList myList = pData.getNewEvents();
            EventInfoList myInfoList = pData.getEventInfo();

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
                        EventNew myEvent = myList.addOpenItem(0, myDate, myDesc, myAmount, myDebit, myCredit,
                                                              myTranType);

                        /* Add information relating to the account */
                        myInfoList.addOpenItem(0, myEvent, EventInfoClass.DebitUnits, myUnits);
                        myInfoList.addOpenItem(0, myEvent, EventInfoClass.TaxCredit, myTaxCredit);
                        myInfoList.addOpenItem(0, myEvent, EventInfoClass.Dilution, myDilution);
                        myInfoList.addOpenItem(0, myEvent, EventInfoClass.QualifyYears, myYears);

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
