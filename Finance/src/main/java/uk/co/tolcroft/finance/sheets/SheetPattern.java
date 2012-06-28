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
import uk.co.tolcroft.models.data.TaskControl;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

/**
 * SheetDataItem extension for Pattern.
 * @author Tony Washer
 */
public class SheetPattern extends SheetDataItem<Event> {
    /**
     * NamedArea for Patterns.
     */
    private static final String AREA_PATTERNS = Pattern.LIST_NAME;

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 10;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = 2;

    /**
     * Date column.
     */
    private static final int COL_DATE = 3;

    /**
     * Description column.
     */
    private static final int COL_DESC = 4;

    /**
     * isCredit column.
     */
    private static final int COL_CREDIT = 5;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = 6;

    /**
     * Partner column.
     */
    private static final int COL_PARTNER = 7;

    /**
     * TransType column.
     */
    private static final int COL_TRAN = 8;

    /**
     * Frequency column.
     */
    private static final int COL_FREQ = 9;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one.
     */
    private final boolean isBackup;

    /**
     * Patterns data list.
     */
    private final PatternList theList;

    /**
     * Accounts data list.
     */
    private Account.AccountList theAccounts = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPattern(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PATTERNS);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theAccounts = myData.getAccounts();
        theList = myData.getPatterns();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetPattern(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PATTERNS);

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
            int myID = loadInteger(COL_ID);
            int myControlId = loadInteger(COL_CONTROL);
            int myActId = loadInteger(COL_ACCOUNT);
            int myPartId = loadInteger(COL_PARTNER);
            int myTranId = loadInteger(COL_TRAN);
            int myFreqId = loadInteger(COL_FREQ);

            /* Access the date and credit flag */
            Date myDate = loadDate(COL_DATE);
            boolean isCredit = loadBoolean(COL_CREDIT);

            /* Access the binary values */
            byte[] myDesc = loadBytes(COL_DESC);
            byte[] myAmount = loadBytes(COL_AMOUNT);

            /* Load the item */
            theList.addItem(myID, myControlId, myDate, myDesc, myAmount, myActId, myPartId, myTranId,
                            myFreqId, isCredit);

            /* else this is a load from an edit-able spreadsheet */
        } else {
            /* Access the Account */
            int myID = loadInteger(COL_ID);
            String myAccount = loadString(COL_ACCOUNT - 1);
            String myPartner = loadString(COL_PARTNER - 1);
            String myTransType = loadString(COL_TRAN - 1);
            String myFrequency = loadString(COL_FREQ - 1);

            /* Access the name and description bytes */
            Date myDate = loadDate(COL_DATE - 1);
            Boolean isCredit = loadBoolean(COL_CREDIT - 1);

            /* Access the binary values */
            String myDesc = loadString(COL_DESC - 1);
            String myAmount = loadString(COL_AMOUNT - 1);

            /* Load the item */
            theList.addItem(myID, myDate, myDesc, myAmount, myAccount, myPartner, myTransType, myFrequency,
                            isCredit);
        }
    }

    @Override
    protected void insertItem(final Event pItem) throws JDataException {
        /* Can only handle a pattern */
        if (!(pItem instanceof Pattern)) {
            return;
        }

        Pattern myItem = (Pattern) pItem;

        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());
            writeInteger(COL_ACCOUNT, myItem.getAccount().getId());
            writeInteger(COL_PARTNER, myItem.getPartner().getId());
            writeInteger(COL_TRAN, pItem.getTransType().getId());
            writeInteger(COL_FREQ, myItem.getFrequency().getId());
            writeDate(COL_DATE, pItem.getDate());
            writeBoolean(COL_CREDIT, myItem.isCredit());
            writeBytes(COL_DESC, pItem.getDescBytes());
            writeBytes(COL_AMOUNT, pItem.getAmountBytes());

            /* else we are creating an edit-able spreadsheet */
        } else {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeString(COL_ACCOUNT - 1, myItem.getAccount().getName());
            writeString(COL_PARTNER - 1, myItem.getPartner().getName());
            writeString(COL_TRAN - 1, pItem.getTransType().getName());
            writeString(COL_FREQ - 1, myItem.getFrequency().getName());
            writeDate(COL_DATE - 1, pItem.getDate());
            writeBoolean(COL_CREDIT - 1, myItem.isCredit());
            writeString(COL_DESC - 1, pItem.getDesc());
            writeNumber(COL_AMOUNT - 1, pItem.getAmount());
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
        writeHeader(COL_ACCOUNT - 1, Pattern.FIELD_ACCOUNT.getName());
        writeHeader(COL_DATE - 1, Event.FIELD_DATE.getName());
        writeHeader(COL_DESC - 1, Event.FIELD_DESC.getName());
        writeHeader(COL_CREDIT - 1, Pattern.FIELD_ISCREDIT.getName());
        writeHeader(COL_AMOUNT - 1, Event.FIELD_AMOUNT.getName());
        writeHeader(COL_PARTNER - 1, Pattern.FIELD_PARTNER.getName());
        writeHeader(COL_TRAN - 1, Event.FIELD_TRNTYP.getName());
        writeHeader(COL_FREQ - 1, Pattern.FIELD_FREQ.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the ten columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
        } else {
            /* Set the nine columns as the range */
            nameRange(NUM_COLS - 1);

            /* Hide the ID column */
            setHiddenColumn(COL_ID);
            setIntegerColumn(COL_ID);

            /* Set the Account column width */
            setColumnWidth(COL_ACCOUNT - 1, Account.NAMELEN);
            applyDataValidation(COL_ACCOUNT - 1, SheetAccount.AREA_ACCOUNTNAMES);
            setColumnWidth(COL_DESC - 1, Event.DESCLEN);
            setColumnWidth(COL_PARTNER - 1, Account.NAMELEN);
            applyDataValidation(COL_PARTNER - 1, SheetAccount.AREA_ACCOUNTNAMES);
            setColumnWidth(COL_TRAN - 1, StaticData.NAMELEN);
            applyDataValidation(COL_TRAN - 1, SheetTransactionType.AREA_TRANSTYPENAMES);
            setColumnWidth(COL_FREQ - 1, StaticData.NAMELEN);
            applyDataValidation(COL_FREQ - 1, SheetFrequency.AREA_FREQUENCYNAMES);

            /* Set Number columns */
            setDateColumn(COL_DATE - 1);
            setBooleanColumn(COL_CREDIT - 1);
            setMoneyColumn(COL_AMOUNT - 1);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        theAccounts.validateLoadedAccounts();
    }

    /**
     * Load the Patterns from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(AREA_PATTERNS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_PATTERNS)) {
                return false;
            }

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of patterns */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of patterns */
                PatternList myList = pData.getPatterns();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table */
                for (int i = myTop.getRow(); i <= myBottom.getRow(); i++) {
                    /* Access the row */
                    Row myRow = mySheet.getRow(i);
                    int iAdjust = 0;

                    /* Access strings */
                    String myAccount = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    Date myDate = myRow.getCell(myCol + iAdjust++).getDateCellValue();
                    String myDesc = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    String myAmount = pHelper.formatNumericCell(myRow.getCell(myCol + iAdjust++));
                    String myPartner = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    String myTransType = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    boolean isCredit = myRow.getCell(myCol + iAdjust++).getBooleanCellValue();
                    String myFrequency = myRow.getCell(myCol + iAdjust++).getStringCellValue();

                    /* Add the value into the finance tables */
                    myList.addItem(0, myDate, myDesc, myAmount, myAccount, myPartner, myTransType,
                                   myFrequency, isCredit);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Handle exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Patterns", e);
        }

        /* Return to caller */
        return true;
    }
}
