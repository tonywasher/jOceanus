/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.archive;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseLogicException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfo.MoneyWiseCashInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfo.MoneyWiseDepositInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanInfo.MoneyWiseLoanInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayeeInfo.MoneyWisePayeeInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolioInfo.MoneyWisePortfolioInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfo.MoneyWiseSecurityInfoList;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;

/**
 * ArchiveLoader extension for Accounts.
 * @author Tony Washer
 */
public final class MoneyWiseArchiveAccount {
    /**
     * Sheet Area name.
     */
    private static final String SHEET_AREA = "AccountInfo";

    /**
     * Report processor.
     */
    private final TethysUIThreadStatusReport theReport;

    /**
     * Workbook.
     */
    private final PrometheusSheetWorkBook theWorkBook;

    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theData;

    /**
     * Store.
     */
    private final MoneyWiseArchiveLoader theStore;

    /**
     * Constructor.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pStore the archive store
     */
    MoneyWiseArchiveAccount(final TethysUIThreadStatusReport pReport,
                            final PrometheusSheetWorkBook pWorkBook,
                            final MoneyWiseDataSet pData,
                            final MoneyWiseArchiveLoader pStore) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
        theStore = pStore;
    }

    /**
     * Load the AccountCategories from an archive.
     * @param pStage the stage
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            pStage.startTask("Accounts");
            final PrometheusSheetView myView = theWorkBook.getRangeView(SHEET_AREA);

            /* Declare the new stage */
            theReport.setNewStage(SHEET_AREA);

            /* Count the number of accounts */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps (*2) */
            theReport.setNumSteps(myTotal << 1);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Process payee account */
                processPayee(myView, myRow);

                /* Report the progress */
                theReport.setNextStep();
            }

            /* Resolve Payee lists */
            resolvePayeeLists();

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Process account */
                processAccount(myView, myRow);

                /* Report the progress */
                theReport.setNextStep();
            }

            /* Resolve Account lists */
            resolveAccountLists();

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + SHEET_AREA, e);
        }
    }

    /**
     * Process account row.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processPayee(final PrometheusSheetView pView,
                              final PrometheusSheetRow pRow) throws OceanusException {
        /* Skip name and type column */
        int iAdjust = -1;
        ++iAdjust;
        ++iAdjust;

        /* Access account class */
        final String myClass = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* If this is a Payee */
        if (myClass.equals(MoneyWiseBasicDataType.PAYEE.toString())) {
            /* Process as a payee */
            processStdPayee(pView, pRow);

            /* If this is a cash */
        } else if (myClass.equals(MoneyWiseBasicDataType.CASH.toString())) {
            /* Process as a cash payee */
            processCashPayee(pView, pRow);
        }
    }

    /**
     * Process account row.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processAccount(final PrometheusSheetView pView,
                                final PrometheusSheetRow pRow) throws OceanusException {
        /* Skip name and type column */
        int iAdjust = -1;
        ++iAdjust;
        ++iAdjust;

        /* Access account class */
        final String myClass = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* If this is a deposit */
        if (myClass.equals(MoneyWiseBasicDataType.DEPOSIT.toString())) {
            /* Process as a deposit */
            processDeposit(pView, pRow);

            /* If this is a cash */
        } else if (myClass.equals(MoneyWiseBasicDataType.CASH.toString())) {
            /* Process as a cash */
            processCash(pView, pRow);

            /* If this is a loan */
        } else if (myClass.equals(MoneyWiseBasicDataType.LOAN.toString())) {
            /* Process as a loan */
            processLoan(pView, pRow);

            /* If this is a security */
        } else if (myClass.equals(MoneyWiseBasicDataType.SECURITY.toString())) {
            /* Process as a security */
            processSecurity(pView, pRow);

            /* If this is a portfolio */
        } else if (myClass.equals(MoneyWiseBasicDataType.PORTFOLIO.toString())) {
            /* Process as a portfolio */
            processPortfolio(pView, pRow);

            /* else reject if not payee */
        } else if (!myClass.equals(MoneyWiseBasicDataType.PAYEE.toString())) {
            throw new MoneyWiseLogicException("Unexpected Account Class " + myClass);
        }
    }

    /**
     * Resolve payee account lists.
     * @throws OceanusException on error
     */
    private void resolvePayeeLists() throws OceanusException {
        /* PostProcess the Payees */
        final MoneyWisePayeeList myPayeeList = theData.getPayees();
        final MoneyWisePayeeInfoList myPayeeInfoList = theData.getPayeeInfo();
        myPayeeList.postProcessOnLoad();
        myPayeeInfoList.postProcessOnLoad();
    }

    /**
     * Resolve non-payee account lists.
     * @throws OceanusException on error
     */
    private void resolveAccountLists() throws OceanusException {
        /* PostProcess the securities */
        final MoneyWiseSecurityList mySecurityList = theData.getSecurities();
        final MoneyWiseSecurityInfoList mySecInfoList = theData.getSecurityInfo();
        mySecurityList.postProcessOnLoad();
        mySecInfoList.postProcessOnLoad();

        /* PostProcess the deposits */
        final MoneyWiseDepositList myDepositList = theData.getDeposits();
        final MoneyWiseDepositInfoList myDepInfoList = theData.getDepositInfo();
        myDepositList.postProcessOnLoad();
        myDepInfoList.postProcessOnLoad();

        /* PostProcess the cash */
        final MoneyWiseCashList myCashList = theData.getCash();
        final MoneyWiseCashInfoList myCashInfoList = theData.getCashInfo();
        myCashList.postProcessOnLoad();
        myCashInfoList.postProcessOnLoad();

        /* PostProcess the loans */
        final MoneyWiseLoanList myLoanList = theData.getLoans();
        final MoneyWiseLoanInfoList myLoanInfoList = theData.getLoanInfo();
        myLoanList.postProcessOnLoad();
        myLoanInfoList.postProcessOnLoad();

        /* PostProcess the portfolios */
        final MoneyWisePortfolioList myPortfolioList = theData.getPortfolios();
        final MoneyWisePortfolioInfoList myPortInfoList = theData.getPortfolioInfo();
        myPortfolioList.postProcessOnLoad();
        myPortInfoList.postProcessOnLoad();

        /* Resolve Security Holdings */
        theStore.resolveSecurityHoldings(theData);
    }

    /**
     * Process payee row from archive.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processStdPayee(final PrometheusSheetView pView,
                                 final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        final PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWisePayee.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myType);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWisePayeeList myList = theData.getPayees();
        final MoneyWisePayee myPayee = myList.addValuesItem(myValues);

        /* Declare the payee */
        theStore.declareAsset(myPayee);
    }

    /**
     * Process cashPayee row from archive.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processCashPayee(final PrometheusSheetView pView,
                                  final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip type, class */
        ++iAdjust;
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /*
         * Skip parent, alias, portfolio, maturity, openingBalance, symbol, region and currency
         * columns
         */
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;

        /* Handle autoExpense which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        if (myCell != null) {
            final String myAutoPayee = myName + "Expense";

            /* Build values */
            final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWisePayee.OBJECT_NAME);
            myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myAutoPayee);
            myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWisePayeeClass.PAYEE.toString());
            myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

            /* Add the value into the list */
            final MoneyWisePayeeList myPayeeList = theData.getPayees();
            myPayeeList.addValuesItem(myValues);
        }
    }

    /**
     * Process cash row from archive.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processCash(final PrometheusSheetView pView,
                             final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Skip parent, alias, portfolio, and maturity columns */
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;

        /* Handle opening balance which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myBalance = null;
        if (myCell != null) {
            myBalance = myCell.getString();
        }

        /* Skip symbol and region columns */
        ++iAdjust;
        ++iAdjust;

        /* Handle currency which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        MoneyWiseCurrency myCurrency = theData.getReportingCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = theData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Handle autoExpense which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myAutoExpense = null;
        String myAutoPayee = null;
        if (myCell != null) {
            myAutoExpense = myCell.getString();
            myAutoPayee = myName + "Expense";
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseCash.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myType);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWiseCashList myList = theData.getCash();
        final MoneyWiseCash myCash = myList.addValuesItem(myValues);

        /* Add information relating to the cash */
        final MoneyWiseCashInfoList myInfoList = theData.getCashInfo();
        myInfoList.addInfoItem(null, myCash, MoneyWiseAccountInfoClass.AUTOEXPENSE, myAutoExpense);
        myInfoList.addInfoItem(null, myCash, MoneyWiseAccountInfoClass.AUTOPAYEE, myAutoPayee);
        myInfoList.addInfoItem(null, myCash, MoneyWiseAccountInfoClass.OPENINGBALANCE, myBalance);

        /* Declare the cash */
        theStore.declareAsset(myCash);
    }

    /**
     * Process deposit row from archive.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processDeposit(final PrometheusSheetView pView,
                                final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip alias and portfolio columns */
        ++iAdjust;
        ++iAdjust;

        /* Handle maturity which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        OceanusDate myMaturity = null;
        if (myCell != null) {
            myMaturity = myCell.getDate();
        }

        /* Handle opening balance which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myBalance = null;
        if (myCell != null) {
            myBalance = myCell.getString();
        }

        /* Skip symbol and region columns */
        ++iAdjust;
        ++iAdjust;

        /* Handle currency which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        MoneyWiseCurrency myCurrency = theData.getReportingCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = theData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseDeposit.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myType);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, myParent);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWiseDepositList myList = theData.getDeposits();
        final MoneyWiseDeposit myDeposit = myList.addValuesItem(myValues);

        /* Add information relating to the deposit */
        final MoneyWiseDepositInfoList myInfoList = theData.getDepositInfo();
        myInfoList.addInfoItem(null, myDeposit, MoneyWiseAccountInfoClass.MATURITY, myMaturity);
        myInfoList.addInfoItem(null, myDeposit, MoneyWiseAccountInfoClass.OPENINGBALANCE, myBalance);

        /* Declare the deposit */
        theStore.declareAsset(myDeposit);
    }

    /**
     * Process loan row from archive.
      * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processLoan(final PrometheusSheetView pView,
                             final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip alias, portfolio, maturity, openingBalance, symbol and region columns */
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;

        /* Handle currency which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        MoneyWiseCurrency myCurrency = theData.getReportingCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = theData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseLoan.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myType);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, myParent);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWiseLoanList myList = theData.getLoans();
        final MoneyWiseLoan myLoan = myList.addValuesItem(myValues);

        /* Declare the loan */
        theStore.declareAsset(myLoan);
    }

    /**
     * Process portfolio row from archive.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processPortfolio(final PrometheusSheetView pView,
                                  final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Access portfolio type */
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Look for separator in category */
        final int iIndex = myType.indexOf(MoneyWiseCategoryBase.STR_SEP);
        if (iIndex == -1) {
            throw new MoneyWiseLogicException("Unexpected Portfolio Class " + myType);
        }

        /* Access subCategory as portfolio type */
        final String myPortType = myType.substring(iIndex + 1);

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip alias, portfolio, maturity, openingBalance, symbol and region columns */
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;

        /* Handle currency which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        MoneyWiseCurrency myCurrency = theData.getReportingCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = theData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWisePortfolio.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myPortType);
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, myParent);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWisePortfolioList myList = theData.getPortfolios();
        final MoneyWisePortfolio myPortfolio = myList.addValuesItem(myValues);

        /* Declare the portfolio */
        theStore.declareAsset(myPortfolio);
    }

    /**
     * Process security row from archive.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processSecurity(final PrometheusSheetView pView,
                                 final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Look for separator in category */
        final int iIndex = myType.indexOf(MoneyWiseCategoryBase.STR_SEP);
        if (iIndex == -1) {
            throw new MoneyWiseLogicException("Unexpected Security Class " + myType);
        }

        /* Access subCategory as security type */
        final String mySecType = myType.substring(iIndex + 1);

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Access the list */
        final MoneyWiseSecurityList myList = theData.getSecurities();

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Access the alias account */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myAlias = null;
        if (myCell != null) {
            myAlias = myCell.getString();
        }

        /* Access Portfolio */
        final String myPortfolio = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* If we have an alias */
        if (myAlias != null) {
            /* Declare the security alias */
            theStore.declareAliasHolding(myName, myAlias, myPortfolio);

            /* return */
            return;
        }

        /* Skip maturity and opening columns */
        ++iAdjust;
        ++iAdjust;

        /* Access Symbol which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String mySymbol = null;
        if (myCell != null) {
            mySymbol = myCell.getString();
        }

        /* Handle region which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myRegion = null;
        if (myCell != null) {
            myRegion = myCell.getString();
        }

        /* Handle currency which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        MoneyWiseCurrency myCurrency = theData.getReportingCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = theData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseSecurity.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, mySecType);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, myParent);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWiseSecurity mySecurity = myList.addValuesItem(myValues);

        /* Add information relating to the security */
        final MoneyWiseSecurityInfoList myInfoList = theData.getSecurityInfo();
        myInfoList.addInfoItem(null, mySecurity, MoneyWiseAccountInfoClass.SYMBOL, mySymbol);
        myInfoList.addInfoItem(null, mySecurity, MoneyWiseAccountInfoClass.REGION, myRegion);

        /* Declare the security holding */
        theStore.declareSecurityHolding(mySecurity, myPortfolio);
    }
}
