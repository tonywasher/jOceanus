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
import net.sourceforge.JDataModels.data.StaticData;
import net.sourceforge.JDataModels.data.TaskControl;
import net.sourceforge.JDataModels.sheets.SheetDataItem;
import net.sourceforge.JDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.AccountInfo.AccountInfoList;
import net.sourceforge.JFinanceApp.data.AccountNew;
import net.sourceforge.JFinanceApp.data.AccountNew.AccountNewList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoClass;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for Account.
 * @author Tony Washer
 */
public class SheetAccountNew extends SheetDataItem<AccountNew> {
    /**
     * NamedArea for Accounts.
     */
    private static final String AREA_ACCOUNTS = Account.LIST_NAME;

    /**
     * NameList for Accounts.
     */
    protected static final String AREA_ACCOUNTNAMES = Account.OBJECT_NAME + "Names";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

    /**
     * AccountType column.
     */
    private static final int COL_ACCOUNTTYPE = COL_NAME + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_ACCOUNTTYPE + 1;

    /**
     * EndDate column.
     */
    private static final int COL_CLOSE = COL_DESC + 1;

    /**
     * Account data list.
     */
    private final AccountNewList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the input spreadsheet
     */
    protected SheetAccountNew(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_ACCOUNTS);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getNewAccounts();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountNew(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_ACCOUNTS);

        /* Access the Accounts list */
        theList = pWriter.getData().getNewAccounts();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myActTypeId = loadInteger(COL_ACCOUNTTYPE);

        /* Access the dates */
        Date myClose = loadDate(COL_CLOSE);

        /* Access the binary values */
        byte[] myName = loadBytes(COL_NAME);
        byte[] myDesc = loadBytes(COL_DESC);

        /* Load the item */
        theList.addSecureItem(myID, myControlId, myName, myActTypeId, myDesc, myClose);
    }

    @Override
    protected void loadOpenItem() throws JDataException {
        /* Access the Account */
        Integer myID = loadInteger(COL_ID);
        String myName = loadString(COL_NAME);
        String myActType = loadString(COL_ACCOUNTTYPE);
        String myDesc = loadString(COL_DESC);

        /* Access the date */
        Date myClose = loadDate(COL_CLOSE);

        /* Load the item */
        theList.addOpenItem(myID, myName, myActType, myDesc, myClose);
    }

    @Override
    protected void insertSecureItem(final AccountNew pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROLID, pItem.getControlKey().getId());
        writeInteger(COL_ACCOUNTTYPE, pItem.getActType().getId());
        writeDate(COL_CLOSE, pItem.getClose());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected void insertOpenItem(final AccountNew pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeString(COL_NAME, pItem.getName());
        writeString(COL_ACCOUNTTYPE, pItem.getActType().getName());
        writeString(COL_DESC, pItem.getDesc());
        // if (pItem.getParent() != null) {
        // writeString(COL_PARENT, pItem.getParent().getName());
        // }
        // if (pItem.getAlias() != null) {
        // writeString(COL_ALIAS, pItem.getAlias().getName());
        // }
        writeDate(COL_CLOSE, pItem.getClose());
        // writeDate(COL_MATURITY, pItem.getMaturity());
        // writeChars(COL_WEBSITE, pItem.getWebSite());
        // writeChars(COL_CUSTNO, pItem.getCustNo());
        // writeChars(COL_USERID, pItem.getUserId());
        // writeChars(COL_PASSWD, pItem.getPassword());
        // writeChars(COL_ACCOUNT, pItem.getAccount());
        // writeChars(COL_NOTES, pItem.getNotes());
    }

    @Override
    protected void formatSheetHeader() throws JDataException {
        /* Write titles */
        writeHeader(COL_NAME, Account.FIELD_NAME.getName());
        writeHeader(COL_ACCOUNTTYPE, Account.FIELD_TYPE.getName());
        writeHeader(COL_DESC, Account.FIELD_DESC.getName());
        // writeHeader(COL_PARENT, Account.FIELD_PARENT.getName());
        // writeHeader(COL_ALIAS, Account.FIELD_ALIAS.getName());
        writeHeader(COL_CLOSE, Account.FIELD_CLOSE.getName());
        // writeHeader(COL_MATURITY, Account.FIELD_MATURITY.getName());
        // writeHeader(COL_WEBSITE, Account.FIELD_WEBSITE.getName());
        // writeHeader(COL_CUSTNO, Account.FIELD_CUSTNO.getName());
        // writeHeader(COL_USERID, Account.FIELD_USERID.getName());
        // writeHeader(COL_PASSWD, Account.FIELD_PASSWORD.getName());
        // writeHeader(COL_ACCOUNT, Account.FIELD_ACCOUNT.getName());
        // writeHeader(COL_NOTES, Account.FIELD_NOTES.getName());

        /* Set the Account column width */
        setColumnWidth(COL_NAME, Account.NAMELEN);
        setColumnWidth(COL_ACCOUNTTYPE, StaticData.NAMELEN);
        setColumnWidth(COL_DESC, Account.DESCLEN);
        // setColumnWidth(COL_PARENT, Account.NAMELEN);
        // setColumnWidth(COL_ALIAS, Account.NAMELEN);

        /* Set Date columns */
        setDateColumn(COL_CLOSE);
        // setDateColumn(COL_MATURITY);
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_CLOSE);

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Set the name column range */
            nameColumnRange(COL_NAME, AREA_ACCOUNTNAMES);

            /* Set the Validations */
            applyDataValidation(COL_ACCOUNTTYPE, SheetAccountType.AREA_ACCOUNTTYPENAMES);
            // applyDataValidation(COL_PARENT, AREA_ACCOUNTNAMES);
            // applyDataValidation(COL_ALIAS, AREA_ACCOUNTNAMES);
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
                AccountNewList myList = pData.getNewAccounts();
                AccountInfoList myInfoList = pData.getAccountInfo();

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
                    String myName = myRow.getCell(myCol + iAdjust++).getStringCellValue();
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
                    AccountNew myAccount = myList.addOpenItem(0, myName, myAcType, null, myClosed);

                    /* Add information relating to the account */
                    myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Maturity, myMaturity);
                    myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Parent, myParent);
                    myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Alias, myAlias);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* Sort the list */
                myList.reSort();
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Accounts", e);
        }

        /* Return to caller */
        return true;
    }
}
