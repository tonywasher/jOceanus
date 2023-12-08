/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashInfo.MoneyWiseCashInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDepositInfo.MoneyWiseDepositInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseLoanInfo.MoneyWiseLoanInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayeeInfo.MoneyWisePayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePortfolioInfo.MoneyWisePortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityInfo.MoneyWiseSecurityInfoList;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadCancelException;

/**
 * ArchiveLoader extension for Accounts.
 * @author Tony Washer
 */
public final class MoneyWiseSheetAccount {
    /**
     * Sheet Area name.
     */
    private static final String SHEET_AREA = "AccountInfo";

    /**
     * Private constructor.
     */
    private MoneyWiseSheetAccount() {
    }

    /**
     * Load the Accounts from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLoader the archive loader
     * @throws OceanusException on error
     */
    static void loadArchive(final TethysUIThreadStatusReport pReport,
                            final PrometheusSheetWorkBook pWorkBook,
                            final MoneyWiseDataSet pData,
                            final MoneyWiseArchiveLoader pLoader) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = pWorkBook.getRangeView(SHEET_AREA);

            /* Declare the new stage */
            pReport.setNewStage(SHEET_AREA);

            /* Count the number of accounts */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps (*2) */
            pReport.setNumSteps(myTotal << 1);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Process payee account */
                processPayee(pLoader, pData, myView, myRow);

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Resolve Payee lists */
            resolvePayeeLists(pData);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Process account */
                processAccount(pLoader, pData, myView, myRow);

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Resolve Account lists */
            resolveAccountLists(pLoader, pData);

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + SHEET_AREA, e);
        }
    }

    /**
     * Process account row.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private static void processPayee(final MoneyWiseArchiveLoader pLoader,
                                     final MoneyWiseDataSet pData,
                                     final PrometheusSheetView pView,
                                     final PrometheusSheetRow pRow) throws OceanusException {
        /* Skip name and type column */
        int iAdjust = -1;
        ++iAdjust;
        ++iAdjust;

        /* Access account class */
        final String myClass = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* If this is a Payee */
        if (myClass.equals(MoneyWiseDataType.PAYEE.toString())) {
            /* Process as a payee */
            MoneyWiseSheetPayee.processPayee(pLoader, pData, pView, pRow);

            /* If this is a cash */
        } else if (myClass.equals(MoneyWiseDataType.CASH.toString())) {
            /* Process as a cash payee */
            MoneyWiseSheetCash.processCashPayee(pLoader, pData, pView, pRow);
        }
    }

    /**
     * Process account row.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private static void processAccount(final MoneyWiseArchiveLoader pLoader,
                                       final MoneyWiseDataSet pData,
                                       final PrometheusSheetView pView,
                                       final PrometheusSheetRow pRow) throws OceanusException {
        /* Skip name and type column */
        int iAdjust = -1;
        ++iAdjust;
        ++iAdjust;

        /* Access account class */
        final String myClass = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* If this is a deposit */
        if (myClass.equals(MoneyWiseDataType.DEPOSIT.toString())) {
            /* Process as a deposit */
            MoneyWiseSheetDeposit.processDeposit(pLoader, pData, pView, pRow);

            /* If this is a cash */
        } else if (myClass.equals(MoneyWiseDataType.CASH.toString())) {
            /* Process as a cash */
            MoneyWiseSheetCash.processCash(pLoader, pData, pView, pRow);

            /* If this is a loan */
        } else if (myClass.equals(MoneyWiseDataType.LOAN.toString())) {
            /* Process as a loan */
            MoneyWiseSheetLoan.processLoan(pLoader, pData, pView, pRow);

            /* If this is a security */
        } else if (myClass.equals(MoneyWiseDataType.SECURITY.toString())) {
            /* Process as a security */
            MoneyWiseSheetSecurity.processSecurity(pLoader, pData, pView, pRow);

            /* If this is a portfolio */
        } else if (myClass.equals(MoneyWiseDataType.PORTFOLIO.toString())) {
            /* Process as a portfolio */
            MoneyWiseSheetPortfolio.processPortfolio(pLoader, pData, pView, pRow);

            /* else reject if not payee */
        } else if (!myClass.equals(MoneyWiseDataType.PAYEE.toString())) {
            throw new MoneyWiseLogicException("Unexpected Account Class " + myClass);
        }
    }

    /**
     * Resolve payee account lists.
     * @param pData the DataSet
     * @throws OceanusException on error
     */
    private static void resolvePayeeLists(final MoneyWiseDataSet pData) throws OceanusException {
        /* PostProcess the Payees */
        final MoneyWisePayeeList myPayeeList = pData.getPayees();
        final MoneyWisePayeeInfoList myPayeeInfoList = pData.getPayeeInfo();
        myPayeeList.postProcessOnLoad();
        myPayeeInfoList.postProcessOnLoad();
    }

    /**
     * Resolve non-payee account lists.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @throws OceanusException on error
     */
    private static void resolveAccountLists(final MoneyWiseArchiveLoader pLoader,
                                            final MoneyWiseDataSet pData) throws OceanusException {
        /* PostProcess the securities */
        final MoneyWiseSecurityList mySecurityList = pData.getSecurities();
        final MoneyWiseSecurityInfoList mySecInfoList = pData.getSecurityInfo();
        mySecurityList.postProcessOnLoad();
        mySecInfoList.postProcessOnLoad();

        /* PostProcess the deposits */
        final MoneyWiseDepositList myDepositList = pData.getDeposits();
        final MoneyWiseDepositInfoList myDepInfoList = pData.getDepositInfo();
        myDepositList.postProcessOnLoad();
        myDepInfoList.postProcessOnLoad();

        /* PostProcess the cash */
        final MoneyWiseCashList myCashList = pData.getCash();
        final MoneyWiseCashInfoList myCashInfoList = pData.getCashInfo();
        myCashList.postProcessOnLoad();
        myCashInfoList.postProcessOnLoad();

        /* PostProcess the loans */
        final MoneyWiseLoanList myLoanList = pData.getLoans();
        final MoneyWiseLoanInfoList myLoanInfoList = pData.getLoanInfo();
        myLoanList.postProcessOnLoad();
        myLoanInfoList.postProcessOnLoad();

        /* PostProcess the portfolios */
        final MoneyWisePortfolioList myPortfolioList = pData.getPortfolios();
        final MoneyWisePortfolioInfoList myPortInfoList = pData.getPortfolioInfo();
        myPortfolioList.postProcessOnLoad();
        myPortInfoList.postProcessOnLoad();

        /* Resolve Security Holdings */
        pLoader.resolveSecurityHoldings(pData);
    }
}
