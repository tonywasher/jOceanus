/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.AccountBase;
import net.sourceforge.joceanus.jmoneywise.data.AccountInfo;
import net.sourceforge.joceanus.jmoneywise.data.AccountInfo.AccountInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfoSet;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for Account.
 * @author Tony Washer
 */
public class SheetAccount
        extends SheetDataItem<Account, MoneyWiseList> {
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
     * Gross Interest column.
     */
    private static final int COL_GROSS = COL_TAXFREE + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_GROSS + 1;

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
    protected SheetAccount(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_ACCOUNTS);

        /* Access the Lists */
        MoneyWiseData myData = pReader.getData();
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
    protected SheetAccount(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_ACCOUNTS);

        /* Access the Accounts list */
        MoneyWiseData myData = pWriter.getData();
        theList = myData.getAccounts();
        theInfoList = myData.getAccountInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup()
                                 ? null
                                 : new SheetAccountInfoSet(AccountInfoClass.class, this, COL_CURRENCY);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myCategoryId = loadInteger(COL_ACCOUNTCAT);
        Integer myCurrencyId = loadInteger(COL_CURRENCY);

        /* Access the flags */
        Boolean isClosed = loadBoolean(COL_CLOSED);
        Boolean isTaxFree = loadBoolean(COL_TAXFREE);
        Boolean isGross = loadBoolean(COL_GROSS);

        /* Access the binary values */
        byte[] myName = loadBytes(COL_NAME);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myName, myCategoryId, isClosed, isTaxFree, isGross, myCurrencyId);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the Account */
        String myName = loadString(COL_NAME);
        String myCategory = loadString(COL_ACCOUNTCAT);
        String myCurrency = loadString(COL_CURRENCY);

        /* Access the flags */
        Boolean isClosed = loadBoolean(COL_CLOSED);
        Boolean isTaxFree = loadBoolean(COL_TAXFREE);
        Boolean isGross = loadBoolean(COL_GROSS);

        /* Load the item */
        theList.addOpenItem(pId, myName, myCategory, isClosed, isTaxFree, isGross, myCurrency);
    }

    @Override
    protected void loadSecondPass(final Integer pId) throws JOceanusException {
        /* Access the account */
        Account myAccount = theList.findItemById(pId);

        /* Load infoSet items */
        theInfoSheet.loadDataInfoSet(theInfoList, myAccount);
    }

    @Override
    protected void insertSecureItem(final Account pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_ACCOUNTCAT, pItem.getAccountCategoryId());
        writeBoolean(COL_CLOSED, pItem.isClosed());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBoolean(COL_GROSS, pItem.isGrossInterest());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeInteger(COL_CURRENCY, pItem.getAccountCurrencyId());
    }

    @Override
    protected void insertOpenItem(final Account pItem) throws JOceanusException {
        /* Set the fields */
        writeString(COL_NAME, pItem.getName());
        writeString(COL_ACCOUNTCAT, pItem.getAccountCategoryName());
        writeBoolean(COL_CLOSED, pItem.isClosed());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBoolean(COL_GROSS, pItem.isGrossInterest());
        writeString(COL_CURRENCY, pItem.getAccountCurrencyName());

        /* Write infoSet fields */
        theInfoSheet.writeDataInfoSet(pItem.getInfoSet());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_NAME, AccountBase.FIELD_NAME.getName());
        writeHeader(COL_ACCOUNTCAT, AccountBase.FIELD_CATEGORY.getName());
        writeHeader(COL_CLOSED, AccountBase.FIELD_CLOSED.getName());
        writeHeader(COL_TAXFREE, AccountBase.FIELD_TAXFREE.getName());
        writeHeader(COL_GROSS, AccountBase.FIELD_GROSS.getName());
        writeHeader(COL_CURRENCY, AccountBase.FIELD_CURRENCY.getName());

        /* prepare infoSet sheet */
        theInfoSheet.prepareSheet();
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
        setStringColumn(COL_NAME);
        setStringColumn(COL_ACCOUNTCAT);
        setBooleanColumn(COL_CLOSED);
        setBooleanColumn(COL_TAXFREE);
        setBooleanColumn(COL_GROSS);
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
    protected void postProcessOnLoad() throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
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

                /* Handle gross which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isGross = Boolean.FALSE;
                if (myCell != null) {
                    isGross = myCell.getBooleanValue();
                }

                /* Handle closed which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isClosed = Boolean.FALSE;
                if (myCell != null) {
                    isClosed = myCell.getBooleanValue();
                }

                /* Add the value into the finance tables */
                myList.addOpenItem(0, myName, myAcType, isClosed, isTaxFree, isGross, myCurrency.getName());

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
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

                /* Skip four columns */
                iAdjust++;
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

                /* Handle portfolio account which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myPortfolio = null;
                if (myCell != null) {
                    myPortfolio = myCell.getStringValue();
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
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.MATURITY, myMaturity);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.PARENT, myParent);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.ALIAS, myAlias);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.PORTFOLIO, myPortfolio);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.HOLDING, myHolding);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.SYMBOL, mySymbol);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.OPENINGBALANCE, myBalance);
                myInfoList.addOpenItem(0, myAccount, AccountInfoClass.AUTOEXPENSE, myAutoExpense);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Sort the lists */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load Accounts", e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * AccountInfoSet sheet.
     */
    private static class SheetAccountInfoSet
            extends SheetDataInfoSet<AccountInfo, Account, AccountInfoType, AccountInfoClass, MoneyWiseList> {

        /**
         * Constructor.
         * @param pClass the info type class
         * @param pOwner the Owner
         * @param pBaseCol the base column
         */
        public SheetAccountInfoSet(final Class<AccountInfoClass> pClass,
                                   final SheetDataItem<Account, MoneyWiseList> pOwner,
                                   final int pBaseCol) {
            super(pClass, pOwner, pBaseCol);
        }

        @Override
        public void formatSheet() throws JOceanusException {
            /* Apply basic formatting */
            super.formatSheet();

            /* Set the Validations */
            applyDataValidation(AccountInfoClass.PARENT, AREA_ACCOUNTNAMES);
            applyDataValidation(AccountInfoClass.ALIAS, AREA_ACCOUNTNAMES);
        }
    }
}
