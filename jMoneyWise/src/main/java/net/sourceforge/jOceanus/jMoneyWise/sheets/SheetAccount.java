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
import net.sourceforge.jOceanus.jDataModels.data.StaticData;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataInfoSet;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountBase;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataCell;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataRow;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;

/**
 * SheetDataItem extension for Account.
 * @author Tony Washer
 */
public class SheetAccount
        extends SheetDataItem<Account> {
    /**
     * NamedArea for Accounts.
     */
    private static final String AREA_ACCOUNTS = Account.LIST_NAME;

    /**
     * NameList for Accounts.
     */
    protected static final String AREA_ACCOUNTNAMES = Account.OBJECT_NAME
                                                      + "Names";

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
    private static final int COL_CLOSED = COL_DESC + 1;

    /**
     * Account data list.
     */
    private final AccountList theList;

    /**
     * Account info list.
     */
    private final AccountInfoList theInfoList;

    /**
     * DataInfoSet Helper.
     */
    private final SheetAccountInfoSet theInfoSheet;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the input spreadsheet
     */
    protected SheetAccount(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_ACCOUNTS);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getAccounts();
        theInfoList = myData.getAccountInfo();
        setDataList(theList);

        /* If this is a backup load */
        if (isBackup()) {
            /* No need for info sheet */
            theInfoSheet = null;

            /* else extract load */
        } else {
            /* Set up info Sheet and ask for two-pass load */
            theInfoSheet = new SheetAccountInfoSet(AccountInfoClass.class, this, COL_CLOSED);
            requestDoubleLoad();
        }
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccount(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_ACCOUNTS);

        /* Access the Accounts list */
        FinanceData myData = pWriter.getData();
        theList = myData.getAccounts();
        theInfoList = myData.getAccountInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup() ? null : new SheetAccountInfoSet(AccountInfoClass.class, this, COL_CLOSED);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myActTypeId = loadInteger(COL_ACCOUNTTYPE);

        /* Access the dates */
        Boolean isClosed = loadBoolean(COL_CLOSED);

        /* Access the binary values */
        byte[] myName = loadBytes(COL_NAME);
        byte[] myDesc = loadBytes(COL_DESC);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myName, myActTypeId, myDesc, isClosed);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the Account */
        String myName = loadString(COL_NAME);
        String myActType = loadString(COL_ACCOUNTTYPE);
        String myDesc = loadString(COL_DESC);

        /* Access the date */
        Boolean isClosed = loadBoolean(COL_CLOSED);

        /* Load the item */
        theList.addOpenItem(pId, myName, myActType, myDesc, isClosed);
    }

    @Override
    protected void loadSecondPass(final Integer pId) throws JDataException {
        /* Access the account */
        Account myAccount = theList.findItemById(pId);

        /* Load infoSet items */
        theInfoSheet.loadDataInfoSet(theInfoList, myAccount);
    }

    @Override
    protected void insertSecureItem(final Account pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_ACCOUNTTYPE, pItem.getActTypeId());
        writeBoolean(COL_CLOSED, pItem.isClosed());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected void insertOpenItem(final Account pItem) throws JDataException {
        /* Set the fields */
        writeString(COL_NAME, pItem.getName());
        writeString(COL_ACCOUNTTYPE, pItem.getActTypeName());
        writeString(COL_DESC, pItem.getDesc());
        writeBoolean(COL_CLOSED, pItem.isClosed());

        /* Write infoSet fields */
        theInfoSheet.writeDataInfoSet(pItem.getInfoSet());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_NAME, AccountBase.FIELD_NAME.getName());
        writeHeader(COL_ACCOUNTTYPE, AccountBase.FIELD_TYPE.getName());
        writeHeader(COL_DESC, AccountBase.FIELD_DESC.getName());
        writeHeader(COL_CLOSED, AccountBase.FIELD_CLOSED.getName());

        /* prepare infoSet sheet */
        theInfoSheet.prepareSheet();
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the Account column width */
        setColumnWidth(COL_NAME, AccountBase.NAMELEN);
        setColumnWidth(COL_ACCOUNTTYPE, StaticData.NAMELEN);
        setColumnWidth(COL_DESC, AccountBase.DESCLEN);

        /* Set Boolean column */
        setBooleanColumn(COL_CLOSED);

        /* Set the name column range */
        nameColumnRange(COL_NAME, AREA_ACCOUNTNAMES);

        /* Set the Validations */
        applyDataValidation(COL_ACCOUNTTYPE, SheetAccountType.AREA_ACCOUNTTYPENAMES);

        /* Format the info sheet */
        theInfoSheet.formatSheet();
    }

    @Override
    protected int getLastColumn() {
        /* Set default */
        int myLastCol = COL_CLOSED;

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
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView("AccountsTable");

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACCOUNTS)) {
                return false;
            }

            /* Count the number of accounts */
            int myTotal = myView.getRowCount();

            /* Access the list of accounts */
            AccountList myList = pData.getAccounts();
            AccountInfoList myInfoList = pData.getAccountInfo();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Loop through the rows of the table in reverse order */
            for (int i = myTotal - 1; i >= 0; i--) {
                /* Access the row by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access account and account type */
                String myName = myRow.getCellByIndex(iAdjust++).getStringValue();
                String myAcType = myRow.getCellByIndex(iAdjust++).getStringValue();

                /* Handle maturity which may be missing */
                DataCell myCell = myRow.getCellByIndex(iAdjust++);
                Date myMaturity = null;
                if (myCell != null) {
                    myMaturity = myCell.getDateValue();
                }

                /* Handle parent which may be missing */
                myCell = myRow.getCellByIndex(iAdjust++);
                String myParent = null;
                if (myCell != null) {
                    myParent = myCell.getStringValue();
                }

                /* Handle alias which may be missing */
                myCell = myRow.getCellByIndex(iAdjust++);
                String myAlias = null;
                if (myCell != null) {
                    myAlias = myCell.getStringValue();
                }

                /* Handle closed which may be missing */
                myCell = myRow.getCellByIndex(iAdjust++);
                Boolean isClosed = Boolean.FALSE;
                if (myCell != null) {
                    isClosed = Boolean.TRUE;
                }

                /* Add the value into the finance tables */
                Account myAccount = myList.addOpenItem(0, myName, myAcType, null, isClosed);

                /* Add information relating to the account */
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Maturity, myMaturity);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Parent, myParent);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Alias, myAlias);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Sort the lists */
            myList.reSort();
            myInfoList.reSort();

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Accounts", e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * AccountInfoSet sheet.
     */
    private static class SheetAccountInfoSet
            extends SheetDataInfoSet<AccountInfo, Account, AccountInfoType, AccountInfoClass> {

        /**
         * Constructor.
         * @param pClass the info type class
         * @param pOwner the Owner
         * @param pBaseCol the base column
         */
        public SheetAccountInfoSet(final Class<AccountInfoClass> pClass,
                                   final SheetDataItem<Account> pOwner,
                                   final int pBaseCol) {
            super(pClass, pOwner, pBaseCol);
        }

        @Override
        public void formatSheet() throws JDataException {
            /* Apply basic formatting */
            super.formatSheet();

            /* Set the column widths */
            setColumnWidth(AccountInfoClass.Parent, AccountBase.NAMELEN);
            setColumnWidth(AccountInfoClass.Alias, AccountBase.NAMELEN);

            /* Set the Validations */
            applyDataValidation(AccountInfoClass.Parent, AREA_ACCOUNTNAMES);
            applyDataValidation(AccountInfoClass.Alias, AREA_ACCOUNTNAMES);
        }
    }
}
