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

import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLoader the archive loader
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final ArchiveLoader pLoader) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(SHEET_AREA);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(SHEET_AREA)) {
                return false;
            }

            /* Count the number of accounts */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps (*2) */
            if (!pTask.setNumSteps(myTotal << 1)) {
                return false;
            }

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                DataRow myRow = myView.getRowByIndex(i);

                /* Process payee account */
                processPayee(pLoader, pData, myView, myRow);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve Payee lists */
            resolvePayeeLists(pData);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the row by reference */
                DataRow myRow = myView.getRowByIndex(i);

                /* Process account */
                processAccount(pLoader, pData, myView, myRow);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve Account lists */
            resolveAccountLists(pData);

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + SHEET_AREA, e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Process account row.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws JOceanusException on error
     */
    private static void processPayee(final ArchiveLoader pLoader,
                                     final MoneyWiseData pData,
                                     final DataView pView,
                                     final DataRow pRow) throws JOceanusException {
        /* Skip name and type column */
        int iAdjust = 0;
        iAdjust++;
        iAdjust++;

        /* Access account class */
        String myClass = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

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
     * @throws JOceanusException on error
     */
    private static void processAccount(final ArchiveLoader pLoader,
                                       final MoneyWiseData pData,
                                       final DataView pView,
                                       final DataRow pRow) throws JOceanusException {
        /* Skip name and type column */
        int iAdjust = 0;
        iAdjust++;
        iAdjust++;

        /* Access account class */
        String myClass = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

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
            throw new JMoneyWiseLogicException("Unexpected Account Class " + myClass);
        }
    }

    /**
     * Resolve payee account lists.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private static void resolvePayeeLists(final MoneyWiseData pData) throws JOceanusException {
        /* PostProcess the Payees */
        PayeeList myPayeeList = pData.getPayees();
        PayeeInfoList myPayeeInfoList = pData.getPayeeInfo();
        myPayeeList.postProcessOnLoad();
        myPayeeInfoList.postProcessOnLoad();
    }

    /**
     * Resolve non-payee account lists.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private static void resolveAccountLists(final MoneyWiseData pData) throws JOceanusException {
        /* PostProcess the securities */
        SecurityList mySecurityList = pData.getSecurities();
        SecurityInfoList mySecInfoList = pData.getSecurityInfo();
        mySecurityList.postProcessOnLoad();
        mySecInfoList.postProcessOnLoad();

        /* PostProcess the deposits */
        DepositList myDepositList = pData.getDeposits();
        DepositInfoList myDepInfoList = pData.getDepositInfo();
        myDepositList.postProcessOnLoad();
        myDepInfoList.postProcessOnLoad();

        /* PostProcess the cash */
        CashList myCashList = pData.getCash();
        CashInfoList myCashInfoList = pData.getCashInfo();
        myCashList.postProcessOnLoad();
        myCashInfoList.postProcessOnLoad();

        /* PostProcess the loans */
        LoanList myLoanList = pData.getLoans();
        LoanInfoList myLoanInfoList = pData.getLoanInfo();
        myLoanList.postProcessOnLoad();
        myLoanInfoList.postProcessOnLoad();

        /* PostProcess the portfolios */
        PortfolioList myPortfolioList = pData.getPortfolios();
        PortfolioInfoList myPortInfoList = pData.getPortfolioInfo();
        myPortfolioList.postProcessOnLoad();
        myPortInfoList.postProcessOnLoad();
    }
}
