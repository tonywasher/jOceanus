/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataInfoSet;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountBase;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCurrency;
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
     * AccountCategory column.
     */
    private static final int COL_ACCOUNTCAT = COL_NAME + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_ACCOUNTCAT + 1;

    /**
     * TaxFree column.
     */
    private static final int COL_TAXFREE = COL_CLOSED + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_TAXFREE + 1;

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
            theInfoSheet = new SheetAccountInfoSet(AccountInfoClass.class, this, COL_CURRENCY);
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
        theInfoSheet = isBackup()
                ? null
                : new SheetAccountInfoSet(AccountInfoClass.class, this, COL_CURRENCY);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myCategoryId = loadInteger(COL_ACCOUNTCAT);
        Integer myCurrencyId = loadInteger(COL_CURRENCY);

        /* Access the flags */
        Boolean isClosed = loadBoolean(COL_CLOSED);
        Boolean isTaxFree = loadBoolean(COL_TAXFREE);

        /* Access the binary values */
        byte[] myName = loadBytes(COL_NAME);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myName, myCategoryId, isClosed, isTaxFree, myCurrencyId);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the Account */
        String myName = loadString(COL_NAME);
        String myCategory = loadString(COL_ACCOUNTCAT);
        String myCurrency = loadString(COL_CURRENCY);

        /* Access the flags */
        Boolean isClosed = loadBoolean(COL_CLOSED);
        Boolean isTaxFree = loadBoolean(COL_TAXFREE);

        /* Load the item */
        theList.addOpenItem(pId, myName, myCategory, isClosed, isTaxFree, myCurrency);
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
        writeInteger(COL_ACCOUNTCAT, pItem.getAccountCategoryId());
        writeBoolean(COL_CLOSED, pItem.isClosed());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeInteger(COL_CURRENCY, pItem.getAccountCurrencyId());
    }

    @Override
    protected void insertOpenItem(final Account pItem) throws JDataException {
        /* Set the fields */
        writeString(COL_NAME, pItem.getName());
        writeString(COL_ACCOUNTCAT, pItem.getAccountCategoryName());
        writeBoolean(COL_CLOSED, pItem.isClosed());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeString(COL_CURRENCY, pItem.getAccountCurrencyName());

        /* Write infoSet fields */
        theInfoSheet.writeDataInfoSet(pItem.getInfoSet());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_NAME, AccountBase.FIELD_NAME.getName());
        writeHeader(COL_ACCOUNTCAT, AccountBase.FIELD_CATEGORY.getName());
        writeHeader(COL_CLOSED, AccountBase.FIELD_CLOSED.getName());
        writeHeader(COL_TAXFREE, AccountBase.FIELD_TAXFREE.getName());
        writeHeader(COL_CURRENCY, AccountBase.FIELD_CURRENCY.getName());

        /* prepare infoSet sheet */
        theInfoSheet.prepareSheet();
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the column types */
        setStringColumn(COL_NAME);
        setStringColumn(COL_ACCOUNTCAT);
        setBooleanColumn(COL_CLOSED);
        setBooleanColumn(COL_TAXFREE);
        setStringColumn(COL_CURRENCY);

        /* Set the name column range */
        nameColumnRange(COL_NAME, AREA_ACCOUNTNAMES);

        /* Set the Validations */
        applyDataValidation(COL_ACCOUNTCAT, SheetAccountCategory.AREA_ACTCATEGORIES);
        applyDataValidation(COL_CURRENCY, SheetAccountCurrency.AREA_ACCOUNTCURRNAMES);

        /* Format the info sheet */
        theInfoSheet.formatSheet();
    }

    @Override
    protected int getLastColumn() {
        /* Set default */
        int myLastCol = COL_CURRENCY;

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Name range plus infoSet */
            myLastCol += theInfoSheet.getXtraColumnCount();
        }

        /* Return the last column */
        return myLastCol;
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();
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
            DataView myView = pWorkBook.getRangeView("AccountInfo");

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

            /* Declare the number of steps (*2) */
            if (!pTask.setNumSteps(myTotal << 1)) {
                return false;
            }

            /* Access default currency */
            AccountCurrency myCurrency = pData.getDefaultCurrency();

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access account and account type */
                String myName = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                String myAcType = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();

                /* Handle taxFree which may be missing */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isTaxFree = Boolean.FALSE;
                if (myCell != null) {
                    isTaxFree = myCell.getBooleanValue();
                }

                /* Handle closed which may be missing */
                // myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                // Boolean isClosed = Boolean.FALSE;
                // if (myCell != null) {
                // isClosed = myCell.getBooleanValue();
                // }
                /* Add the value into the finance tables */
                myList.addOpenItem(0, myName, myAcType, Boolean.FALSE/* TODO isClosed */, isTaxFree, myCurrency.getName());

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access account name */
                String myName = myView.getRowCellByIndex(myRow, iAdjust++).getStringValue();
                Account myAccount = myList.findItemByName(myName);

                /* Skip three columns */
                iAdjust++;
                iAdjust++;
                iAdjust++;

                /* Handle parent which may be missing */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myParent = null;
                if (myCell != null) {
                    myParent = myCell.getStringValue();
                }

                /* Handle alias which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myAlias = null;
                if (myCell != null) {
                    myAlias = myCell.getStringValue();
                }

                /* Handle holding account which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myHolding = null;
                if (myCell != null) {
                    myHolding = myCell.getStringValue();
                }

                /* Handle maturity which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                JDateDay myMaturity = null;
                if (myCell != null) {
                    myMaturity = myCell.getDateValue();
                }

                /* Handle opening balance which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myBalance = null;
                if (myCell != null) {
                    myBalance = myCell.getStringValue();
                }

                /* Handle symbol which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String mySymbol = null;
                if (myCell != null) {
                    mySymbol = myCell.getStringValue();
                }

                /* Handle autoExpense which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myAutoExpense = null;
                if (myCell != null) {
                    myAutoExpense = myCell.getStringValue();
                }

                /* Add information relating to the account */
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Maturity, myMaturity);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Parent, myParent);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Alias, myAlias);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Holding, myHolding);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.Symbol, mySymbol);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.OpeningBalance, myBalance);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.AutoExpense, myAutoExpense);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Sort the lists */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

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

            /* Set the Validations */
            applyDataValidation(AccountInfoClass.Parent, AREA_ACCOUNTNAMES);
            applyDataValidation(AccountInfoClass.Alias, AREA_ACCOUNTNAMES);
        }
    }
}
