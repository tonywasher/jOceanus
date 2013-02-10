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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataInfoSet;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jDecimal.JDecimalParser;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountBase;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventBase;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType;
import net.sourceforge.jOceanus.jMoneyWise.sheets.FinanceSheet.YearRange;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataCell;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataRow;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;

/**
 * SheetDataItem extension for Event.
 * @author Tony Washer
 */
public class SheetEvent
        extends SheetDataItem<Event> {
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
    private final EventList theList;

    /**
     * Event info list.
     */
    private final EventInfoList theInfoList;

    /**
     * DataInfoSet Helper.
     */
    private final SheetEventInfoSet theInfoSheet;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEvent(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTS);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getEvents();
        theInfoList = myData.getEventInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup() ? null : new SheetEventInfoSet(EventInfoClass.class, this, COL_TRAN);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEvent(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTS);

        /* Access the Events list */
        FinanceData myData = pWriter.getData();
        theList = myData.getEvents();
        theInfoList = myData.getEventInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup() ? null : new SheetEventInfoSet(EventInfoClass.class, this, COL_TRAN);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
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
        theList.addSecureItem(pId, myControlId, myDate, myDesc, myAmount, myDebitId, myCreditId, myTranId);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the Account */
        String myDebit = loadString(COL_DEBIT);
        String myCredit = loadString(COL_CREDIT);
        String myTransType = loadString(COL_TRAN);

        /* Access the date and name and description bytes */
        Date myDate = loadDate(COL_DATE);

        /* Access the binary values */
        String myDesc = loadString(COL_DESC);
        String myAmount = loadString(COL_AMOUNT);

        /* Load the item */
        Event myEvent = theList.addOpenItem(pId, myDate, myDesc, myAmount, myDebit, myCredit, myTransType);

        /* Load infoSet items */
        theInfoSheet.loadDataInfoSet(theInfoList, myEvent);
    }

    @Override
    protected void insertSecureItem(final Event pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeDate(COL_DATE, pItem.getDate());
        writeInteger(COL_DEBIT, pItem.getDebitId());
        writeInteger(COL_CREDIT, pItem.getCreditId());
        writeInteger(COL_TRAN, pItem.getTransTypeId());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
    }

    @Override
    protected void insertOpenItem(final Event pItem) throws JDataException {
        /* Set the fields */
        writeDate(COL_DATE, pItem.getDate());
        writeString(COL_DESC, pItem.getDesc());
        writeDecimal(COL_AMOUNT, pItem.getAmount());
        writeString(COL_DEBIT, pItem.getDebitName());
        writeString(COL_CREDIT, pItem.getCreditName());
        writeString(COL_TRAN, pItem.getTransTypeName());

        /* Write infoSet fields */
        theInfoSheet.writeDataInfoSet(pItem.getInfoSet());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_DATE, EventBase.FIELD_DATE.getName());
        writeHeader(COL_DESC, EventBase.FIELD_DESC.getName());
        writeHeader(COL_AMOUNT, EventBase.FIELD_AMOUNT.getName());
        writeHeader(COL_DEBIT, EventBase.FIELD_DEBIT.getName());
        writeHeader(COL_CREDIT, EventBase.FIELD_CREDIT.getName());
        writeHeader(COL_TRAN, EventBase.FIELD_TRNTYP.getName());

        /* prepare the info sheet */
        theInfoSheet.prepareSheet();
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the Account column width */
        setColumnWidth(COL_DESC, EventBase.DESCLEN);
        setColumnWidth(COL_DEBIT, AccountBase.NAMELEN);
        setColumnWidth(COL_CREDIT, AccountBase.NAMELEN);
        setColumnWidth(COL_TRAN, StaticData.NAMELEN);

        /* Set Number columns */
        setDateColumn(COL_DATE);
        setMoneyColumn(COL_AMOUNT);

        /* Apply validation */
        applyDataValidation(COL_DEBIT, SheetAccount.AREA_ACCOUNTNAMES);
        applyDataValidation(COL_CREDIT, SheetAccount.AREA_ACCOUNTNAMES);
        applyDataValidation(COL_TRAN, SheetTransactionType.AREA_TRANSTYPENAMES);

        /* Format the info sheet */
        theInfoSheet.formatSheet();

        applyDataFilter(COL_DEBIT);
    }

    @Override
    protected int getLastColumn() {
        /* Set default */
        int myLastCol = COL_TRAN;

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Name range plus infoSet */
            myLastCol += theInfoSheet.getXtraColumnCount();
        }

        /* Return the last column */
        return myLastCol;
    }

    /**
     * Load the Accounts from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final FinanceData pData,
                                         final YearRange pRange) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Access the list of events */
            EventList myList = pData.getEvents();
            EventInfoList myInfoList = pData.getEventInfo();

            /* Access the parser */
            JDataFormatter myFormatter = pTask.getDataFormatter();
            JDecimalParser myParser = myFormatter.getDecimalParser();

            /* Loop through the columns of the table */
            for (Integer j = pRange.getMinYear(); j <= pRange.getMaxYear(); j++) {
                /* Find the range of cells */
                String myRangeName = j.toString();
                myRangeName = "Finance"
                              + myRangeName.substring(2);
                DataView myView = pWorkBook.getRangeView(myRangeName);

                /* Declare the new stage */
                if (!pTask.setNewStage("Events from "
                                       + j)) {
                    return false;
                }

                /* Count the number of Events */
                int myTotal = myView.getRowCount() - 1;

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table */
                for (int i = 1; i <= myTotal; i++) {
                    /* Access the row */
                    DataRow myRow = myView.getRowByIndex(i);
                    int iAdjust = 0;

                    /* Access date */
                    Date myDate = myRow.getCellByIndex(iAdjust++).getDateValue();

                    /* Access the values */
                    String myDesc = myRow.getCellByIndex(iAdjust++).getStringValue();
                    String myAmount = myRow.getCellByIndex(iAdjust++).getStringValue();
                    String myDebit = myRow.getCellByIndex(iAdjust++).getStringValue();
                    String myCredit = myRow.getCellByIndex(iAdjust++).getStringValue();

                    /* Handle Dilution which may be missing */
                    DataCell myCell = myRow.getCellByIndex(iAdjust++);
                    String myDilution = null;
                    if (myCell != null) {
                        myDilution = myCell.getStringValue();
                        if (!myDilution.startsWith("0.")) {
                            myDilution = null;
                        }
                    }

                    /* Handle Units which may be missing */
                    myCell = myRow.getCellByIndex(iAdjust++);
                    String myUnitsVal = null;
                    if (myCell != null) {
                        myUnitsVal = myCell.getStringValue();
                    }

                    /* Handle transaction type */
                    String myTranType = myRow.getCellByIndex(iAdjust++).getStringValue();

                    /* Handle Tax Credit which may be missing */
                    myCell = myRow.getCellByIndex(iAdjust++);
                    String myTaxCredit = null;
                    if (myCell != null) {
                        myTaxCredit = myCell.getStringValue();
                    }

                    /* Handle Years which may be missing */
                    myCell = myRow.getCellByIndex(iAdjust++);
                    Integer myYears = null;
                    if (myCell != null) {
                        myYears = myCell.getIntegerValue();
                    }

                    /* Add the event */
                    Event myEvent = myList.addOpenItem(0, myDate, myDesc, myAmount, myDebit, myCredit, myTranType);

                    /* If we have units */
                    if (myUnitsVal != null) {
                        JUnits myUnits = myParser.parseUnitsValue(myUnitsVal);
                        JUnits myValue = myUnits;
                        boolean isCredit = myEvent.getCredit().isPriced();
                        if ((myEvent.isStockSplit() || myEvent.isAdminCharge())
                            && (!myUnits.isPositive())) {
                            myValue = new JUnits(myValue);
                            myValue.negate();
                            isCredit = false;
                        }
                        myInfoList.addOpenItem(0, myEvent, isCredit ? EventInfoClass.CreditUnits : EventInfoClass.DebitUnits, myValue);
                    }

                    /* Add information relating to the account */
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.TaxCredit, myTaxCredit);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.Dilution, myDilution);
                    myInfoList.addOpenItem(0, myEvent, EventInfoClass.QualifyYears, myYears);

                    /* Validate the event */
                    myEvent.validate();
                    if (myEvent.hasErrors()) {
                        throw new JDataException(ExceptionClass.VALIDATE, myEvent, "Validation error");
                    }

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0)
                        && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* Sort the list */
                myList.reSort();
                myInfoList.reSort();
            }

            /* Handle Exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Events", e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * EventInfoSet sheet.
     */
    private static class SheetEventInfoSet
            extends SheetDataInfoSet<EventInfo, Event, EventInfoType, EventInfoClass> {

        /**
         * Constructor.
         * @param pClass the info type class
         * @param pOwner the Owner
         * @param pBaseCol the base column
         */
        public SheetEventInfoSet(final Class<EventInfoClass> pClass,
                                 final SheetDataItem<Event> pOwner,
                                 final int pBaseCol) {
            super(pClass, pOwner, pBaseCol);
        }

        @Override
        public void formatSheet() throws JDataException {
            /* Apply basic formatting */
            super.formatSheet();

            /* Set column width for ThirdParty */
            setColumnWidth(EventInfoClass.ThirdParty, AccountBase.NAMELEN);

            /* Set validation for ThirdParty */
            applyDataValidation(EventInfoClass.ThirdParty, SheetAccount.AREA_ACCOUNTNAMES);
        }
    }
}
