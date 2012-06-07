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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetAccount extends SheetDataItem<Account> {
    /**
     * NamedArea for Accounts
     */
    private static final String Accounts = Account.LIST_NAME;

    /**
     * NameList for Accounts
     */
    protected static final String AccountNames = Account.OBJECT_NAME + "Names";

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one
     */
    private boolean isBackup = false;

    /**
     * Account data list
     */
    private AccountList theList = null;

    /**
     * Constructor for loading a spreadsheet
     * @param pReader the input spreadsheet
     */
    protected SheetAccount(FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, Accounts);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getAccounts();
    }

    /**
     * Constructor for creating a spreadsheet
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccount(FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, Accounts);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Accounts list */
        theList = pWriter.getData().getAccounts();
        setDataList(theList);
    }

    /**
     * Load an item from the spreadsheet
     * @throws JDataException
     */
    @Override
    protected void loadItem() throws JDataException {

        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(0);
            int myControlId = loadInteger(1);
            int myActTypeId = loadInteger(2);
            Integer myParentId = loadInteger(3);
            Integer myAliasId = loadInteger(4);

            /* Access the dates */
            Date myClose = loadDate(5);
            Date myMaturity = loadDate(6);

            /* Access the binary values */
            byte[] myName = loadBytes(7);
            byte[] myDesc = loadBytes(8);
            byte[] myWebSite = loadBytes(9);
            byte[] myCustNo = loadBytes(10);
            byte[] myUserId = loadBytes(11);
            byte[] myPassword = loadBytes(12);
            byte[] myAccount = loadBytes(13);
            byte[] myNotes = loadBytes(14);

            /* Load the item */
            theList.addItem(myID, myControlId, myName, myActTypeId, myDesc, myMaturity, myClose, myParentId,
                            myAliasId, myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
        }

        /* else this is a load from an edit-able spreadsheet */
        else {
            /* Access the Account */
            int myID = loadInteger(0);
            String myName = loadString(1);
            String myActType = loadString(2);
            String myDesc = loadString(3);
            String myParent = loadString(4);
            String myAlias = loadString(5);

            /* Access the date and name and description bytes */
            Date myClose = loadDate(6);
            Date myMaturity = loadDate(7);

            /* Access the binary values */
            char[] myWebSite = loadChars(8);
            char[] myCustNo = loadChars(9);
            char[] myUserId = loadChars(10);
            char[] myPassword = loadChars(11);
            char[] myAccount = loadChars(12);
            char[] myNotes = loadChars(13);

            /* Load the item */
            theList.addItem(myID, myName, myActType, myDesc, myMaturity, myClose, myParent, myAlias,
                            myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
        }
    }

    /**
     * Insert a item into the spreadsheet
     * @param pItem the Item to insert
     * @throws JDataException
     */
    @Override
    protected void insertItem(Account pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeInteger(1, pItem.getControlKey().getId());
            writeInteger(2, pItem.getActType().getId());
            if (pItem.getParent() != null)
                writeInteger(3, pItem.getParent().getId());
            if (pItem.getAlias() != null)
                writeInteger(4, pItem.getAlias().getId());
            writeDate(5, pItem.getClose());
            writeDate(6, pItem.getMaturity());
            writeBytes(7, pItem.getNameBytes());
            writeBytes(8, pItem.getDescBytes());
            writeBytes(9, pItem.getWebSiteBytes());
            writeBytes(10, pItem.getCustNoBytes());
            writeBytes(11, pItem.getUserIdBytes());
            writeBytes(12, pItem.getPasswordBytes());
            writeBytes(13, pItem.getAccountBytes());
            writeBytes(14, pItem.getNotesBytes());
        }

        /* else we are creating an edit-able spreadsheet */
        else {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeString(1, pItem.getName());
            writeString(2, pItem.getActType().getName());
            writeString(3, pItem.getDesc());
            if (pItem.getParent() != null)
                writeString(4, pItem.getParent().getName());
            if (pItem.getAlias() != null)
                writeString(5, pItem.getAlias().getName());
            writeDate(6, pItem.getClose());
            writeDate(7, pItem.getMaturity());
            writeChars(8, pItem.getWebSite());
            writeChars(9, pItem.getCustNo());
            writeChars(10, pItem.getUserId());
            writeChars(11, pItem.getPassword());
            writeChars(12, pItem.getAccount());
            writeChars(13, pItem.getNotes());
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
        writeHeader(1, Account.FIELD_NAME.getName());
        writeHeader(2, Account.FIELD_TYPE.getName());
        writeHeader(3, Account.FIELD_DESC.getName());
        writeHeader(4, Account.FIELD_PARENT.getName());
        writeHeader(5, Account.FIELD_ALIAS.getName());
        writeHeader(6, Account.FIELD_CLOSE.getName());
        writeHeader(7, Account.FIELD_MATURITY.getName());
        writeHeader(8, Account.FIELD_WEBSITE.getName());
        writeHeader(9, Account.FIELD_CUSTNO.getName());
        writeHeader(10, Account.FIELD_USERID.getName());
        writeHeader(11, Account.FIELD_PASSWORD.getName());
        writeHeader(12, Account.FIELD_ACCOUNT.getName());
        writeHeader(13, Account.FIELD_NOTES.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fifteen columns as the range */
            nameRange(15);
        }

        /* else this is an edit-able spreadsheet */
        else {
            /* Set the fourteen columns as the range */
            nameRange(14);

            /* Set the Id column as hidden */
            setHiddenColumn(0);
            setIntegerColumn(0);

            /* Set the name column width and range */
            nameColumnRange(1, AccountNames);

            /* Set the Account column width */
            setColumnWidth(1, Account.NAMELEN);
            setColumnWidth(2, StaticData.NAMELEN);
            applyDataValidation(2, SheetAccountType.ActTypeNames);
            setColumnWidth(3, Account.DESCLEN);
            setColumnWidth(4, Account.NAMELEN);
            applyDataValidation(4, AccountNames);
            setColumnWidth(5, Account.NAMELEN);
            applyDataValidation(5, AccountNames);

            /* Set Date columns */
            setDateColumn(6);
            setDateColumn(7);
        }
    }

    /**
     * Load the Accounts from an archive
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
        AccountList myList;
        AreaReference myRange;
        Sheet mySheet;
        CellReference myTop;
        CellReference myBottom;
        int myCol;
        String myAccount;
        String myAcType;
        String myParent;
        String myAlias;
        Date myMaturity;
        Date myClosed;
        Cell myCell;
        int myTotal;
        int mySteps;
        int myCount = 0;

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            myRange = pHelper.resolveAreaReference(Accounts);

            /* Access the number of reporting steps */
            mySteps = pThread.getReportingSteps();

            /* Declare the new stage */
            if (!pThread.setNewStage(Accounts))
                return false;

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                myTop = myRange.getFirstCell();
                myBottom = myRange.getLastCell();
                mySheet = pHelper.getSheetByName(myTop.getSheetName());
                myCol = myTop.getCol();

                /* Count the number of accounts */
                myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of accounts */
                myList = pData.getAccounts();

                /* Declare the number of steps */
                if (!pThread.setNumSteps(myTotal))
                    return false;

                /* Loop through the rows of the table in reverse order */
                for (int i = myBottom.getRow(); i >= myTop.getRow(); i--) {
                    /* Access the row */
                    Row myRow = mySheet.getRow(i);

                    /* Access account and account type */
                    myAccount = myRow.getCell(myCol).getStringCellValue();
                    myAcType = myRow.getCell(myCol + 1).getStringCellValue();

                    /* Handle maturity which may be missing */
                    myCell = myRow.getCell(myCol + 2);
                    myMaturity = null;
                    if (myCell != null) {
                        myMaturity = myCell.getDateCellValue();
                    }

                    /* Handle parent which may be missing */
                    myCell = myRow.getCell(myCol + 3);
                    myParent = null;
                    if (myCell != null) {
                        myParent = myCell.getStringCellValue();
                    }

                    /* Handle alias which may be missing */
                    myCell = myRow.getCell(myCol + 4);
                    myAlias = null;
                    if (myCell != null) {
                        myAlias = myCell.getStringCellValue();
                    }

                    /* Handle closed which may be missing */
                    myCell = myRow.getCell(myCol + 5);
                    myClosed = null;
                    if (myCell != null) {
                        myClosed = myCell.getDateCellValue();
                    }

                    /* Add the value into the finance tables */
                    myList.addItem(0, myAccount, myAcType, null, myMaturity, myClosed, myParent, myAlias,
                                   null, null, null, null, null, null);

                    /* Report the progress */
                    myCount++;
                    if ((myCount % mySteps) == 0)
                        if (!pThread.setStepsDone(myCount))
                            return false;
                }
            }
        }

        /* Handle exceptions */
        catch (Throwable e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Accounts", e);
        }

        /* Return to caller */
        return true;
    }
}
