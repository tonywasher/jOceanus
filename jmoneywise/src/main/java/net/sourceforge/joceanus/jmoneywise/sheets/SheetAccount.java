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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.AccountInfo.AccountInfoList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for Account.
 * @author Tony Washer
 */
public class SheetAccount
        extends SheetEncrypted<Account, MoneyWiseDataType> {
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
     * Constructor for loading a spreadsheet.
     * @param pReader the input spreadsheet
     */
    protected SheetAccount(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_ACCOUNTS);

        /* Access the Lists */
        MoneyWiseData myData = pReader.getData();
        theList = myData.getAccounts();
        setDataList(theList);
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
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Account.OBJECT_NAME);
        myValues.addValue(Account.FIELD_CATEGORY, loadInteger(COL_ACCOUNTCAT));
        myValues.addValue(Account.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Account.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Account.FIELD_GROSS, loadBoolean(COL_GROSS));
        myValues.addValue(Account.FIELD_TAXFREE, loadBoolean(COL_TAXFREE));
        myValues.addValue(Account.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Account pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_ACCOUNTCAT, pItem.getAccountCategoryId());
        writeBoolean(COL_CLOSED, pItem.isClosed());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBoolean(COL_GROSS, pItem.isGrossInterest());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeInteger(COL_CURRENCY, pItem.getAccountCurrencyId());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CURRENCY;
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
        /* Access the list of accounts */
        AccountList myList = pData.getAccounts();
        AccountInfoList myInfoList = pData.getAccountInfo();

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

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Account.OBJECT_NAME);
                myValues.addValue(Account.FIELD_NAME, myName);
                myValues.addValue(Account.FIELD_CATEGORY, myAcType);
                myValues.addValue(Account.FIELD_CURRENCY, myCurrency);
                myValues.addValue(Account.FIELD_GROSS, isGross);
                myValues.addValue(Account.FIELD_TAXFREE, isTaxFree);
                myValues.addValue(Account.FIELD_CLOSED, isClosed);

                /* Add the value into the list */
                myList.addValuesItem(myValues);

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
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.MATURITY, myMaturity);
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.PARENT, myParent);
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.ALIAS, myAlias);
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.PORTFOLIO, myPortfolio);
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.HOLDING, myHolding);
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.SYMBOL, mySymbol);
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.OPENINGBALANCE, myBalance);
                myInfoList.addInfoItem(null, myAccount, AccountInfoClass.AUTOEXPENSE, myAutoExpense);

                /* Process alternate view */
                processAlternate(pData, myView, myRow);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Sort the lists */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Resolve ValueLinks */
            myInfoList.resolveValueLinks();

            /* Resolve Alternate lists */
            resolveAlternate(pData);

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Process row into alternate form.
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws JOceanusException on error
     */
    private static void processAlternate(final MoneyWiseData pData,
                                         final DataView pView,
                                         final DataRow pRow) throws JOceanusException {
        /* Access lists */
        PayeeTypeList myPayeeTypeList = pData.getPayeeTypes();
        SecurityTypeList mySecTypeList = pData.getSecurityTypes();
        SecurityList mySecurityList = pData.getSecurities();
        PayeeList myPayeeList = pData.getPayees();
        PortfolioList myPortfolioList = pData.getPortfolios();

        /* Access name and type */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        String myType = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Handle taxFree which may be missing */
        DataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isTaxFree = Boolean.FALSE;
        if (myCell != null) {
            isTaxFree = myCell.getBooleanValue();
        }

        /* Skip gross */
        iAdjust++;

        /* Handle closed which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* If this is a portfolio */
        if (myType.equals(MoneyWiseDataType.PORTFOLIO.toString())) {
            /* Skip three columns */
            iAdjust++;
            iAdjust++;
            iAdjust++;

            /* Access Holding account */
            String myHolding = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Portfolio.OBJECT_NAME);
            myValues.addValue(Portfolio.FIELD_NAME, myName);
            myValues.addValue(Portfolio.FIELD_HOLDING, myHolding);
            myValues.addValue(Portfolio.FIELD_TAXFREE, isTaxFree);
            myValues.addValue(Portfolio.FIELD_CLOSED, isClosed);

            /* Add the value into the list */
            myPortfolioList.addValuesItem(myValues);

            /* If this is a payee */
        } else if (myPayeeTypeList.findItemByName(myType) != null) {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Payee.OBJECT_NAME);
            myValues.addValue(Payee.FIELD_NAME, myName);
            myValues.addValue(Payee.FIELD_PAYEETYPE, myType);
            myValues.addValue(Payee.FIELD_CLOSED, isClosed);

            /* Add the value into the list */
            myPayeeList.addValuesItem(myValues);
        } else {
            /* Look for separator in category */
            int iIndex = myType.indexOf(EventCategory.STR_SEP);
            if (iIndex != -1) {
                /* Access subCategory as security type */
                String mySecType = myType.substring(iIndex + 1);
                if (mySecTypeList.findItemByName(mySecType) != null) {
                    /* Access Parent account */
                    String myParent = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
                    if (pView.getRowCellByIndex(pRow, iAdjust++) != null) {
                        return;
                    }

                    /* Skip four columns */
                    iAdjust++;
                    iAdjust++;
                    iAdjust++;
                    iAdjust++;

                    /* Access Symbol */
                    String mySymbol = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

                    /* Build data values */
                    DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Security.OBJECT_NAME);
                    myValues.addValue(Security.FIELD_NAME, myName);
                    myValues.addValue(Security.FIELD_SECTYPE, mySecType);
                    myValues.addValue(Security.FIELD_CURRENCY, pData.getDefaultCurrency());
                    myValues.addValue(Security.FIELD_PARENT, myParent);
                    myValues.addValue(Security.FIELD_SYMBOL, mySymbol);
                    myValues.addValue(Security.FIELD_CLOSED, isClosed);

                    /* Add the value into the list */
                    mySecurityList.addValuesItem(myValues);
                }
            }
        }
    }

    /**
     * Resolve alternate lists.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private static void resolveAlternate(final MoneyWiseData pData) throws JOceanusException {
        /* Access lists */
        SecurityList mySecurityList = pData.getSecurities();
        PayeeList myPayeeList = pData.getPayees();
        PortfolioList myPortfolioList = pData.getPortfolios();

        /* Sort the payee list and validate */
        myPayeeList.resolveDataSetLinks();
        myPayeeList.reSort();
        myPayeeList.validateOnLoad();

        /* Sort the security list and validate */
        mySecurityList.resolveDataSetLinks();
        mySecurityList.reSort();
        mySecurityList.validateOnLoad();

        /* Sort the portfolio list and validate */
        myPortfolioList.resolveDataSetLinks();
        myPortfolioList.reSort();
        myPortfolioList.validateOnLoad();
    }
}
