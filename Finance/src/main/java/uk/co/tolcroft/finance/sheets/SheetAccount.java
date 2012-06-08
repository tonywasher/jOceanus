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
import uk.co.tolcroft.models.data.TaskControl;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

/**
 * SheetStaticData extension for Account.
 * @author Tony Washer
 */
public class SheetAccount extends SheetDataItem<Account> {
    /**
     * NamedArea for Accounts.
     */
    private static final String AREA_ACCOUNTS = Account.LIST_NAME;

    /**
     * NameList for Accounts.
     */
    protected static final String AREA_ACCOUNTNAMES = Account.OBJECT_NAME + "Names";

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 15;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * Name column.
     */
    private static final int COL_NAME = 2;

    /**
     * AccountType column.
     */
    private static final int COL_ACCOUNTTYPE = 3;

    /**
     * Description column.
     */
    private static final int COL_DESC = 4;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = 5;

    /**
     * Alias column.
     */
    private static final int COL_ALIAS = 6;

    /**
     * EndDate column.
     */
    private static final int COL_CLOSE = 7;

    /**
     * Maturity column.
     */
    private static final int COL_MATURITY = 8;

    /**
     * WebSite column.
     */
    private static final int COL_WEBSITE = 9;

    /**
     * CustNo column.
     */
    private static final int COL_CUSTNO = 10;

    /**
     * UserId column.
     */
    private static final int COL_USERID = 11;

    /**
     * Password column.
     */
    private static final int COL_PASSWD = 12;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = 13;

    /**
     * Notes column.
     */
    private static final int COL_NOTES = 14;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one?
     */
    private final boolean isBackup;

    /**
     * Account data list.
     */
    private final AccountList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the input spreadsheet
     */
    protected SheetAccount(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_ACCOUNTS);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getAccounts();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccount(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_ACCOUNTS);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Accounts list */
        theList = pWriter.getData().getAccounts();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws JDataException {

        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            int myControlId = loadInteger(COL_CONTROL);
            int myActTypeId = loadInteger(COL_ACCOUNTTYPE);
            Integer myParentId = loadInteger(COL_PARENT);
            Integer myAliasId = loadInteger(COL_ALIAS);

            /* Access the dates */
            Date myClose = loadDate(COL_CLOSE);
            Date myMaturity = loadDate(COL_MATURITY);

            /* Access the binary values */
            byte[] myName = loadBytes(COL_NAME);
            byte[] myDesc = loadBytes(COL_DESC);
            byte[] myWebSite = loadBytes(COL_WEBSITE);
            byte[] myCustNo = loadBytes(COL_CUSTNO);
            byte[] myUserId = loadBytes(COL_USERID);
            byte[] myPassword = loadBytes(COL_PASSWD);
            byte[] myAccount = loadBytes(COL_ACCOUNT);
            byte[] myNotes = loadBytes(COL_NOTES);

            /* Load the item */
            theList.addItem(myID, myControlId, myName, myActTypeId, myDesc, myMaturity, myClose, myParentId,
                            myAliasId, myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);

            /* else this is a load from an edit-able spreadsheet */
        } else {
            /* Access the Account */
            int myID = loadInteger(COL_ID);
            String myName = loadString(COL_NAME - 1);
            String myActType = loadString(COL_ACCOUNTTYPE - 1);
            String myDesc = loadString(COL_DESC - 1);
            String myParent = loadString(COL_PARENT - 1);
            String myAlias = loadString(COL_ALIAS - 1);

            /* Access the date and name and description bytes */
            Date myClose = loadDate(COL_CLOSE - 1);
            Date myMaturity = loadDate(COL_MATURITY - 1);

            /* Access the binary values */
            char[] myWebSite = loadChars(COL_WEBSITE - 1);
            char[] myCustNo = loadChars(COL_CUSTNO - 1);
            char[] myUserId = loadChars(COL_USERID - 1);
            char[] myPassword = loadChars(COL_PASSWD - 1);
            char[] myAccount = loadChars(COL_ACCOUNT - 1);
            char[] myNotes = loadChars(COL_NOTES - 1);

            /* Load the item */
            theList.addItem(myID, myName, myActType, myDesc, myMaturity, myClose, myParent, myAlias,
                            myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
        }
    }

    @Override
    protected void insertItem(final Account pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());
            writeInteger(COL_ACCOUNTTYPE, pItem.getActType().getId());
            if (pItem.getParent() != null) {
                writeInteger(COL_PARENT, pItem.getParent().getId());
            }
            if (pItem.getAlias() != null) {
                writeInteger(COL_ALIAS, pItem.getAlias().getId());
            }
            writeDate(COL_CLOSE, pItem.getClose());
            writeDate(COL_MATURITY, pItem.getMaturity());
            writeBytes(COL_NAME, pItem.getNameBytes());
            writeBytes(COL_DESC, pItem.getDescBytes());
            writeBytes(COL_WEBSITE, pItem.getWebSiteBytes());
            writeBytes(COL_CUSTNO, pItem.getCustNoBytes());
            writeBytes(COL_USERID, pItem.getUserIdBytes());
            writeBytes(COL_PASSWD, pItem.getPasswordBytes());
            writeBytes(COL_ACCOUNT, pItem.getAccountBytes());
            writeBytes(COL_NOTES, pItem.getNotesBytes());

            /* else we are creating an edit-able spreadsheet */
        } else {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeString(COL_NAME - 1, pItem.getName());
            writeString(COL_ACCOUNTTYPE - 1, pItem.getActType().getName());
            writeString(COL_DESC - 1, pItem.getDesc());
            if (pItem.getParent() != null) {
                writeString(COL_PARENT - 1, pItem.getParent().getName());
            }
            if (pItem.getAlias() != null) {
                writeString(COL_ALIAS - 1, pItem.getAlias().getName());
            }
            writeDate(COL_CLOSE - 1, pItem.getClose());
            writeDate(COL_MATURITY - 1, pItem.getMaturity());
            writeChars(COL_WEBSITE - 1, pItem.getWebSite());
            writeChars(COL_CUSTNO - 1, pItem.getCustNo());
            writeChars(COL_USERID - 1, pItem.getUserId());
            writeChars(COL_PASSWD - 1, pItem.getPassword());
            writeChars(COL_ACCOUNT - 1, pItem.getAccount());
            writeChars(COL_NOTES - 1, pItem.getNotes());
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
        writeHeader(COL_NAME - 1, Account.FIELD_NAME.getName());
        writeHeader(COL_ACCOUNTTYPE - 1, Account.FIELD_TYPE.getName());
        writeHeader(COL_DESC - 1, Account.FIELD_DESC.getName());
        writeHeader(COL_PARENT - 1, Account.FIELD_PARENT.getName());
        writeHeader(COL_ALIAS - 1, Account.FIELD_ALIAS.getName());
        writeHeader(COL_CLOSE - 1, Account.FIELD_CLOSE.getName());
        writeHeader(COL_MATURITY - 1, Account.FIELD_MATURITY.getName());
        writeHeader(COL_WEBSITE - 1, Account.FIELD_WEBSITE.getName());
        writeHeader(COL_CUSTNO - 1, Account.FIELD_CUSTNO.getName());
        writeHeader(COL_USERID - 1, Account.FIELD_USERID.getName());
        writeHeader(COL_PASSWD - 1, Account.FIELD_PASSWORD.getName());
        writeHeader(COL_ACCOUNT - 1, Account.FIELD_ACCOUNT.getName());
        writeHeader(COL_NOTES - 1, Account.FIELD_NOTES.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fifteen columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
        } else {
            /* Set the fourteen columns as the range */
            nameRange(NUM_COLS - 1);

            /* Set the Id column as hidden */
            setHiddenColumn(COL_ID);
            setIntegerColumn(COL_ID);

            /* Set the name column width and range */
            nameColumnRange(COL_NAME - 1, AREA_ACCOUNTNAMES);

            /* Set the Account column width */
            setColumnWidth(COL_NAME - 1, Account.NAMELEN);
            setColumnWidth(COL_ACCOUNTTYPE - 1, StaticData.NAMELEN);
            applyDataValidation(COL_ACCOUNTTYPE - 1, SheetAccountType.AREA_ACCOUNTTYPENAMES);
            setColumnWidth(COL_DESC - 1, Account.DESCLEN);
            setColumnWidth(COL_PARENT - 1, Account.NAMELEN);
            applyDataValidation(COL_PARENT - 1, AREA_ACCOUNTNAMES);
            setColumnWidth(COL_ALIAS - 1, Account.NAMELEN);
            applyDataValidation(COL_ALIAS - 1, AREA_ACCOUNTNAMES);

            /* Set Date columns */
            setDateColumn(COL_CLOSE - 1);
            setDateColumn(COL_MATURITY - 1);
        }
    }

    /**
     * Load the Accounts from an archive.
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
            AreaReference myRange = pHelper.resolveAreaReference(AREA_ACCOUNTS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACCOUNTS)) {
                return false;
            }

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of accounts */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of accounts */
                AccountList myList = pData.getAccounts();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table in reverse order */
                for (int i = myBottom.getRow(); i >= myTop.getRow(); i--) {
                    /* Access the row */
                    Row myRow = mySheet.getRow(i);
                    int iAdjust = 0;

                    /* Access account and account type */
                    String myAccount = myRow.getCell(myCol + iAdjust++).getStringCellValue();
                    String myAcType = myRow.getCell(myCol + iAdjust++).getStringCellValue();

                    /* Handle maturity which may be missing */
                    Cell myCell = myRow.getCell(myCol + iAdjust++);
                    Date myMaturity = null;
                    if (myCell != null) {
                        myMaturity = myCell.getDateCellValue();
                    }

                    /* Handle parent which may be missing */
                    myCell = myRow.getCell(myCol + iAdjust++);
                    String myParent = null;
                    if (myCell != null) {
                        myParent = myCell.getStringCellValue();
                    }

                    /* Handle alias which may be missing */
                    myCell = myRow.getCell(myCol + iAdjust++);
                    String myAlias = null;
                    if (myCell != null) {
                        myAlias = myCell.getStringCellValue();
                    }

                    /* Handle closed which may be missing */
                    myCell = myRow.getCell(myCol + iAdjust++);
                    Date myClosed = null;
                    if (myCell != null) {
                        myClosed = myCell.getDateCellValue();
                    }

                    /* Add the value into the finance tables */
                    myList.addItem(0, myAccount, myAcType, null, myMaturity, myClosed, myParent, myAlias,
                                   null, null, null, null, null, null);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Handle exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Accounts", e);
        }

        /* Return to caller */
        return true;
    }
}
