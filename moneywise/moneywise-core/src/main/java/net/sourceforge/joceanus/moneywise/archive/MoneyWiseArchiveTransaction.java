/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.archive;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfo.MoneyWiseTransInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetView;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.util.ListIterator;

/**
 * ArchiveLoader for Transaction.
 *
 * @author Tony Washer
 */
public final class MoneyWiseArchiveTransaction {
    /**
     * NamedArea for Transactions.
     */
    private static final String AREA_TRANS = MoneyWiseTransaction.LIST_NAME;

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
     * Cache.
     */
    private final MoneyWiseArchiveCache theCache;

    /**
     * Constructor.
     *
     * @param pReport   the report
     * @param pWorkBook the workbook
     * @param pData     the data set to load into
     * @param pCache    the cache
     */
    MoneyWiseArchiveTransaction(final TethysUIThreadStatusReport pReport,
                                final PrometheusSheetWorkBook pWorkBook,
                                final MoneyWiseDataSet pData,
                                final MoneyWiseArchiveCache pCache) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
        theCache = pCache;
    }

    /**
     * Load the ExchangeRates from an archive.
     *
     * @param pStage the stage
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage) throws OceanusException {
        /* Access the list of transactions */
        pStage.startTask(AREA_TRANS);
        final MoneyWiseTransactionList myList = theData.getTransactions();
        final MoneyWiseTransInfoList myInfoList = theData.getTransactionInfo();

        /* Protect against exceptions */
        try {
            /* Obtain the range iterator */
            final ListIterator<MoneyWiseArchiveYear> myIterator = theCache.reverseIterator();

            /* Loop through the individual year ranges */
            while (myIterator.hasPrevious()) {
                /* Access year */
                final MoneyWiseArchiveYear myYear = myIterator.previous();

                /* Find the range of cells */
                final PrometheusSheetView myView = theWorkBook.getRangeView(myYear.getRangeName());

                /* Declare the new stage */
                theReport.setNewStage("Events from " + myYear.getDate().getYear());

                /* Count the number of Transactions */
                final int myTotal = myView.getRowCount();

                /* Declare the number of steps */
                theReport.setNumSteps(myTotal);

                /* Loop through the rows of the table */
                for (int i = 0; i < myTotal; i++) {
                    /* Access the row */
                    final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                    /* Process transaction and break loop if requested */
                    if (!processTransaction(myView, myRow)) {
                        break;
                    }

                    /* Report the progress */
                    theReport.setNextStep();
                }

                /* If we have finished */
                if (!theCache.checkDate(myYear.getDate())) {
                    /* Break the loop */
                    break;
                }
            }

            /* Resolve ValueLinks */
            myInfoList.resolveValueLinks();

            /* Sort the list */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Validate the list */
            myList.validateOnLoad();

            /* Handle Exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to load " + myList.getItemType().getListName(), e);
        }
    }

    /**
     * Process transaction row from archive.
     *
     * @param pView the spreadsheet view
     * @param pRow  the spreadsheet row
     * @return continue true/false
     * @throws OceanusException on error
     */
    private boolean processTransaction(final PrometheusSheetView pView,
                                       final PrometheusSheetRow pRow) throws OceanusException {
        /* Access cache */
        int iAdjust = -1;

        /* Access date */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final OceanusDate myDate = (myCell != null)
                ? myCell.getDate()
                : null;

        /* Access the values */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final String myDebit = (myCell != null)
                ? myCell.getString()
                : null;
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final String myCredit = (myCell != null)
                ? myCell.getString()
                : null;
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final String myAmount = (myCell != null)
                ? myCell.getString()
                : null;
        final String myCategory = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Handle Reconciled which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean myReconciled = Boolean.FALSE;
        if (myCell != null) {
            myReconciled = Boolean.TRUE;
        }

        /* Set defaults */
        if (!theCache.resolveValues(myDate, myDebit, myCredit, myCategory)) {
            return false;
        }

        /* Build transaction */
        final MoneyWiseTransaction myTrans = theCache.buildTransaction(myAmount, myReconciled);
        if (myTrans == null) {
            return true;
        }

        /* Process TransactionInfo */
        final int myLast = pRow.getMaxValuedCellIndex();
        if (iAdjust < myLast) {
            processTransInfo(pView, pRow, myTrans, iAdjust);
        }

        /* Continue */
        return true;
    }

    /**
     * Process transaction row from archive.
     *
     * @param pView   the spreadsheet view
     * @param pRow    the spreadsheet row
     * @param pTrans  the transaction
     * @param pAdjust the cell#
     * @throws OceanusException on error
     */
    private void processTransInfo(final PrometheusSheetView pView,
                                  final PrometheusSheetRow pRow,
                                  final MoneyWiseTransaction pTrans,
                                  final int pAdjust) throws OceanusException {
        /* Handle Description which may be missing */
        int iAdjust = pAdjust;
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myDesc = null;
        if (myCell != null) {
            myDesc = myCell.getString();
        }

        /* Handle Tax Credit which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myTaxCredit = null;
        if (myCell != null) {
            myTaxCredit = myCell.getString();
        }

        /* Handle EmployeeNatIns which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myEmployeeNatIns = null;
        if (myCell != null) {
            myEmployeeNatIns = myCell.getString();
        }

        /* Handle EmployerNatIns which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myEmployerNatIns = null;
        if (myCell != null) {
            myEmployerNatIns = myCell.getString();
        }

        /* Handle Benefit which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myBenefit = null;
        if (myCell != null) {
            myBenefit = myCell.getString();
        }

        /* Handle DebitUnits which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myDebitUnits = null;
        if (myCell != null) {
            myDebitUnits = myCell.getString();
        }

        /* Handle CreditUnits which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myCreditUnits = null;
        if (myCell != null) {
            myCreditUnits = myCell.getString();
        }

        /* Handle Dilution which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myDilution = null;
        if (myCell != null) {
            myDilution = myCell.getString();
        }

        /* Handle Reference which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myReference = null;
        if (myCell != null) {
            myReference = myCell.getString();
        }

        /* Handle Years which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Integer myYears = null;
        if (myCell != null) {
            myYears = myCell.getInteger();
        }

        /* Handle Withheld which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myWithheld = null;
        if (myCell != null) {
            myWithheld = myCell.getString();
        }

        /* Handle ReturnedCashAccount which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myReturnedCashAccount = null;
        if (myCell != null) {
            myReturnedCashAccount = myCell.getString();
        }

        /* Handle ReturnedCash which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myReturnedCash = null;
        if (myCell != null) {
            myReturnedCash = myCell.getString();
        }

        /* Handle PartnerAmount which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myPartnerAmount = null;
        if (myCell != null) {
            myPartnerAmount = myCell.getString();
        }

        /* Handle XchangeRate which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myXchangeRate = null;
        if (myCell != null) {
            myXchangeRate = myCell.getString();
        }

        /* Handle TagList which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myTagList = null;
        if (myCell != null) {
            myTagList = myCell.getString();
        }

        /* If the debit was reversed */
        if (theCache.isDebitReversed()) {
            /* Flip the Debit and credit values */
            final String myTemp = myDebitUnits;
            myDebitUnits = myCreditUnits;
            myCreditUnits = myTemp;
            if (myCreditUnits != null) {
                myCreditUnits = "-" + myCreditUnits;
            }
        } else if (myDebitUnits != null) {
            myDebitUnits = "-" + myDebitUnits;
        } else if (theCache.isRecursive()) {
            myDebitUnits = myCreditUnits;
            myCreditUnits = null;
        }

        /* Add information relating to the account */
        final MoneyWiseTransInfoList myInfoList = theData.getTransactionInfo();
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.COMMENTS, myDesc);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.TAXCREDIT, myTaxCredit);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.EMPLOYEENATINS, myEmployeeNatIns);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.EMPLOYERNATINS, myEmployerNatIns);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.DEEMEDBENEFIT, myBenefit);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, myDebitUnits);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.PARTNERDELTAUNITS, myCreditUnits);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.DILUTION, myDilution);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.REFERENCE, myReference);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.QUALIFYYEARS, myYears);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.WITHHELD, myWithheld);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT, myReturnedCashAccount);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.RETURNEDCASH, myReturnedCash);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.PARTNERAMOUNT, myPartnerAmount);
        myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.XCHANGERATE, myXchangeRate);

        /* If we have a TagList */
        if (myTagList != null) {
            /* Process any separated items */
            int iIndex = myTagList.indexOf(TethysUIConstant.LIST_SEP);
            while (iIndex != -1) {
                myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.TRANSTAG, myTagList.substring(0, iIndex));
                myTagList = myTagList.substring(iIndex + 1);
                iIndex = myTagList.indexOf(TethysUIConstant.LIST_SEP);
            }

            /* Process the single remaining item */
            myInfoList.addInfoItem(null, pTrans, MoneyWiseTransInfoClass.TRANSTAG, myTagList);
        }
    }
}
