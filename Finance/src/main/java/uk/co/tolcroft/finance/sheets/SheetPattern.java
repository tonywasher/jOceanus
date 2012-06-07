/*******************************************************************************
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
package uk.co.tolcroft.finance.sheets;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.Pattern;
import uk.co.tolcroft.finance.data.Pattern.PatternList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetPattern extends SheetDataItem<Event> {
    /**
     * NamedArea for Patterns
     */
    private static final String Patterns = Pattern.LIST_NAME;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one
     */
    private boolean isBackup = false;

    /**
     * Patterns data list
     */
    private PatternList theList = null;

    /**
     * Accounts data list
     */
    private Account.AccountList theAccounts = null;

    /**
     * Constructor for loading a spreadsheet
     * @param pReader the spreadsheet reader
     */
    protected SheetPattern(FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, Patterns);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theAccounts = myData.getAccounts();
        theList = myData.getPatterns();
    }

    /**
     * Constructor for creating a spreadsheet
     * @param pWriter the spreadsheet writer
     */
    protected SheetPattern(FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, Patterns);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Patterns list */
        theList = pWriter.getData().getPatterns();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws JDataException {

        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(0);
            int myControlId = loadInteger(1);
            int myActId = loadInteger(2);
            int myPartId = loadInteger(3);
            int myTranId = loadInteger(6);
            int myFreqId = loadInteger(7);

            /* Access the date and credit flag */
            Date myDate = loadDate(4);
            boolean isCredit = loadBoolean(5);

            /* Access the binary values */
            byte[] myDesc = loadBytes(8);
            byte[] myAmount = loadBytes(9);

            /* Load the item */
            theList.addItem(myID, myControlId, myDate, myDesc, myAmount, myActId, myPartId, myTranId,
                            myFreqId, isCredit);
        }

        /* else this is a load from an edit-able spreadsheet */
        else {
            /* Access the Account */
            int myID = loadInteger(0);
            String myAccount = loadString(1);
            String myPartner = loadString(6);
            String myTransType = loadString(7);
            String myFrequency = loadString(8);

            /* Access the name and description bytes */
            Date myDate = loadDate(2);
            Boolean isCredit = loadBoolean(4);

            /* Access the binary values */
            String myDesc = loadString(3);
            String myAmount = loadString(5);

            /* Load the item */
            theList.addItem(myID, myDate, myDesc, myAmount, myAccount, myPartner, myTransType, myFrequency,
                            isCredit);
        }
    }

    @Override
    protected void insertItem(Event pItem) throws JDataException {
        Pattern myItem = (Pattern) pItem;

        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeInteger(1, pItem.getControlKey().getId());
            writeInteger(2, myItem.getAccount().getId());
            writeInteger(3, myItem.getPartner().getId());
            writeInteger(6, pItem.getTransType().getId());
            writeInteger(7, myItem.getFrequency().getId());
            writeDate(4, pItem.getDate());
            writeBoolean(5, myItem.isCredit());
            writeBytes(8, pItem.getDescBytes());
            writeBytes(9, pItem.getAmountBytes());
        }

        /* else we are creating an edit-able spreadsheet */
        else {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeString(1, myItem.getAccount().getName());
            writeString(6, myItem.getPartner().getName());
            writeString(7, pItem.getTransType().getName());
            writeString(8, myItem.getFrequency().getName());
            writeDate(2, pItem.getDate());
            writeBoolean(4, myItem.isCredit());
            writeString(3, pItem.getDesc());
            writeNumber(5, pItem.getAmount());
        }
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
        /* Ignore if we are creating a backup */
        if (isBackup)
            return;

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(0, DataItem.FIELD_ID.getName());
        writeHeader(1, Pattern.FIELD_ACCOUNT.getName());
        writeHeader(2, Event.FIELD_DATE.getName());
        writeHeader(3, Event.FIELD_DESC.getName());
        writeHeader(4, Pattern.FIELD_ISCREDIT.getName());
        writeHeader(5, Event.FIELD_AMOUNT.getName());
        writeHeader(6, Pattern.FIELD_PARTNER.getName());
        writeHeader(7, Event.FIELD_TRNTYP.getName());
        writeHeader(8, Pattern.FIELD_FREQ.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the ten columns as the range */
            nameRange(10);
        }

        /* else this is an edit-able spreadsheet */
        else {
            /* Set the nine columns as the range */
            nameRange(9);

            /* Hide the ID column */
            setHiddenColumn(0);
            setIntegerColumn(0);

            /* Set the Account column width */
            setColumnWidth(1, Account.NAMELEN);
            applyDataValidation(1, SheetAccount.AccountNames);
            setColumnWidth(3, Event.DESCLEN);
            setColumnWidth(6, Account.NAMELEN);
            applyDataValidation(6, SheetAccount.AccountNames);
            setColumnWidth(7, StaticData.NAMELEN);
            applyDataValidation(7, SheetTransactionType.AREA_TRANSTYPENAMES);
            setColumnWidth(8, StaticData.NAMELEN);
            applyDataValidation(8, SheetFrequency.FrequencyNames);

            /* Set Number columns */
            setDateColumn(2);
            setBooleanColumn(4);
            setMoneyColumn(5);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        theAccounts.validateLoadedAccounts();
    }

    /**
     * Load the Patterns from an archive
     * @param pThread the thread status control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException
     */
    protected static boolean loadArchive(ThreadStatus<FinanceData> pThread,
                                         SheetHelper pHelper,
                                         FinanceData pData) throws JDataException {
        /* Local variables */
        PatternList myList;
        AreaReference myRange = null;
        Sheet mySheet;
        CellReference myTop;
        CellReference myBottom;
        int myCol;
        Date myDate;
        String myAccount;
        String myDesc;
        String myPartner;
        String myTransType;
        String myAmount;
        String myFrequency;
        boolean isCredit;
        int myTotal;
        int mySteps;
        int myCount = 0;

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            myRange = pHelper.resolveAreaReference(Patterns);

            /* Access the number of reporting steps */
            mySteps = pThread.getReportingSteps();

            /* Declare the new stage */
            if (!pThread.setNewStage(Patterns))
                return false;

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                myTop = myRange.getFirstCell();
                myBottom = myRange.getLastCell();
                mySheet = pHelper.getSheetByName(myTop.getSheetName());
                myCol = myTop.getCol();

                /* Count the number of patterns */
                myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of patterns */
                myList = pData.getPatterns();

                /* Declare the number of steps */
                if (!pThread.setNumSteps(myTotal))
                    return false;

                /* Loop through the rows of the table */
                for (int i = myTop.getRow(); i <= myBottom.getRow(); i++) {
                    /* Access the row */
                    Row myRow = mySheet.getRow(i);

                    /* Access strings */
                    myAccount = myRow.getCell(myCol).getStringCellValue();
                    myDesc = myRow.getCell(myCol + 2).getStringCellValue();
                    myAmount = pHelper.formatNumericCell(myRow.getCell(myCol + 3));
                    myPartner = myRow.getCell(myCol + 4).getStringCellValue();
                    myTransType = myRow.getCell(myCol + 5).getStringCellValue();
                    myFrequency = myRow.getCell(myCol + 7).getStringCellValue();

                    /* Handle Date */
                    myDate = myRow.getCell(myCol + 1).getDateCellValue();

                    /* Handle isCredit */
                    isCredit = myRow.getCell(myCol + 6).getBooleanCellValue();

                    /* Add the value into the finance tables */
                    myList.addItem(0, myDate, myDesc, myAmount, myAccount, myPartner, myTransType,
                                   myFrequency, isCredit);

                    /* Report the progress */
                    myCount++;
                    if ((myCount % mySteps) == 0)
                        if (!pThread.setStepsDone(myCount))
                            return false;
                }
            }
        }

        /* Handle exceptions */
        catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Patterns", e);
        }

        /* Return to caller */
        return true;
    }
}
