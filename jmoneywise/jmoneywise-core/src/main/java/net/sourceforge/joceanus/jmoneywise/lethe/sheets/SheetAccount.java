/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataRow;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ArchiveLoader extension for Accounts.
 * @author Tony Washer
 */
public final class SheetAccount {
    /**
     * Sheet Area name.
     */
    private static final String SHEET_AREA = "AccountInfo";

    /**
     * Private constructor.
     */
    private SheetAccount() {
    }

    /**
     * Load the Accounts from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLoader the archive loader
     * @throws OceanusException on error
     */
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final MetisDataWorkBook pWorkBook,
                                      final MoneyWiseData pData,
                                      final ArchiveLoader pLoader) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final MetisDataView myView = pWorkBook.getRangeView(SHEET_AREA);

            /* Declare the new stage */
            pReport.setNewStage(SHEET_AREA);

            /* Count the number of accounts */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps (*2) */
            pReport.setNumSteps(myTotal << 1);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                final MetisDataRow myRow = myView.getRowByIndex(i);

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
                final MetisDataRow myRow = myView.getRowByIndex(i);

                /* Process account */
                processAccount(pLoader, pData, myView, myRow);

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Resolve Account lists */
            resolveAccountLists(pLoader, pData);

            /* Handle exceptions */
        } catch (MetisThreadCancelException e) {
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
    private static void processPayee(final ArchiveLoader pLoader,
                                     final MoneyWiseData pData,
                                     final MetisDataView pView,
                                     final MetisDataRow pRow) throws OceanusException {
        /* Skip name and type column */
        int iAdjust = -1;
        ++iAdjust;
        ++iAdjust;

        /* Access account class */
        final String myClass = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();

        /* If this is a Payee */
        if (myClass.equals(MoneyWiseDataType.PAYEE.toString())) {
            /* Process as a payee */
            SheetPayee.processPayee(pLoader, pData, pView, pRow);

            /* If this is a cash */
        } else if (myClass.equals(MoneyWiseDataType.CASH.toString())) {
            /* Process as a cash payee */
            SheetCash.processCashPayee(pLoader, pData, pView, pRow);
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
    private static void processAccount(final ArchiveLoader pLoader,
                                       final MoneyWiseData pData,
                                       final MetisDataView pView,
                                       final MetisDataRow pRow) throws OceanusException {
        /* Skip name and type column */
        int iAdjust = -1;
        ++iAdjust;
        ++iAdjust;

        /* Access account class */
        final String myClass = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();

        /* If this is a deposit */
        if (myClass.equals(MoneyWiseDataType.DEPOSIT.toString())) {
            /* Process as a deposit */
            SheetDeposit.processDeposit(pLoader, pData, pView, pRow);

            /* If this is a cash */
        } else if (myClass.equals(MoneyWiseDataType.CASH.toString())) {
            /* Process as a cash */
            SheetCash.processCash(pLoader, pData, pView, pRow);

            /* If this is a loan */
        } else if (myClass.equals(MoneyWiseDataType.LOAN.toString())) {
            /* Process as a loan */
            SheetLoan.processLoan(pLoader, pData, pView, pRow);

            /* If this is a security */
        } else if (myClass.equals(MoneyWiseDataType.SECURITY.toString())) {
            /* Process as a security */
            SheetSecurity.processSecurity(pLoader, pData, pView, pRow);

            /* If this is a portfolio */
        } else if (myClass.equals(MoneyWiseDataType.PORTFOLIO.toString())) {
            /* Process as a portfolio */
            SheetPortfolio.processPortfolio(pLoader, pData, pView, pRow);

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
    private static void resolvePayeeLists(final MoneyWiseData pData) throws OceanusException {
        /* PostProcess the Payees */
        final PayeeList myPayeeList = pData.getPayees();
        final PayeeInfoList myPayeeInfoList = pData.getPayeeInfo();
        myPayeeList.postProcessOnLoad();
        myPayeeInfoList.postProcessOnLoad();
    }

    /**
     * Resolve non-payee account lists.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @throws OceanusException on error
     */
    private static void resolveAccountLists(final ArchiveLoader pLoader,
                                            final MoneyWiseData pData) throws OceanusException {
        /* PostProcess the securities */
        final SecurityList mySecurityList = pData.getSecurities();
        final SecurityInfoList mySecInfoList = pData.getSecurityInfo();
        mySecurityList.postProcessOnLoad();
        mySecInfoList.postProcessOnLoad();

        /* PostProcess the deposits */
        final DepositList myDepositList = pData.getDeposits();
        final DepositInfoList myDepInfoList = pData.getDepositInfo();
        myDepositList.postProcessOnLoad();
        myDepInfoList.postProcessOnLoad();

        /* PostProcess the cash */
        final CashList myCashList = pData.getCash();
        final CashInfoList myCashInfoList = pData.getCashInfo();
        myCashList.postProcessOnLoad();
        myCashInfoList.postProcessOnLoad();

        /* PostProcess the loans */
        final LoanList myLoanList = pData.getLoans();
        final LoanInfoList myLoanInfoList = pData.getLoanInfo();
        myLoanList.postProcessOnLoad();
        myLoanInfoList.postProcessOnLoad();

        /* PostProcess the portfolios */
        final PortfolioList myPortfolioList = pData.getPortfolios();
        final PortfolioInfoList myPortInfoList = pData.getPortfolioInfo();
        myPortfolioList.postProcessOnLoad();
        myPortInfoList.postProcessOnLoad();

        /* Resolve Security Holdings */
        pLoader.resolveSecurityHoldings(pData);
    }
}
