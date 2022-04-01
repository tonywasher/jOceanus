/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.sheets.ArchiveLoader.ArchiveYear;
import net.sourceforge.joceanus.jmoneywise.lethe.sheets.ArchiveLoader.ParentCache;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;

/**
 * SheetDataItem extension for Transaction.
 * @author Tony Washer
 */
public class SheetTransaction
        extends PrometheusSheetEncrypted<Transaction, MoneyWiseDataType> {
    /**
     * NamedArea for Transactions.
     */
    private static final String AREA_TRANS = Transaction.LIST_NAME;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_KEYSETID + 1;

    /**
     * Pair column.
     */
    private static final int COL_PAIR = COL_DATE + 1;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = COL_PAIR + 1;

    /**
     * Partner column.
     */
    private static final int COL_PARTNER = COL_ACCOUNT + 1;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = COL_PARTNER + 1;

    /**
     * Category column.
     */
    private static final int COL_CATEGORY = COL_AMOUNT + 1;

    /**
     * Reconciled column.
     */
    private static final int COL_RECONCILED = COL_CATEGORY + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTransaction(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TRANS);

        /* Access the Lists */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getTransactions());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTransaction(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TRANS);

        /* Access the Transactions list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getTransactions());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Transaction.OBJECT_NAME);
        myValues.addValue(Transaction.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(Transaction.FIELD_PAIR, loadInteger(COL_PAIR));
        myValues.addValue(Transaction.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Transaction.FIELD_ACCOUNT, loadInteger(COL_ACCOUNT));
        myValues.addValue(Transaction.FIELD_PARTNER, loadInteger(COL_PARTNER));
        myValues.addValue(Transaction.FIELD_AMOUNT, loadBytes(COL_AMOUNT));
        myValues.addValue(Transaction.FIELD_RECONCILED, loadBoolean(COL_RECONCILED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Transaction pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeDate(COL_DATE, pItem.getDate());
        writeInteger(COL_PAIR, pItem.getAssetPairId());
        writeInteger(COL_ACCOUNT, pItem.getAccountId());
        writeInteger(COL_PARTNER, pItem.getPartnerId());
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeBoolean(COL_RECONCILED, pItem.isReconciled());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_RECONCILED;
    }

    /**
     * Load the Events from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLoader the archive loader
     * @throws OceanusException on error
     */
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final PrometheusSheetWorkBook pWorkBook,
                                      final MoneyWiseData pData,
                                      final ArchiveLoader pLoader) throws OceanusException {
        /* Access the list of transactions */
        final TransactionList myList = pData.getTransactions();
        final TransactionInfoList myInfoList = pData.getTransactionInfo();

        /* Protect against exceptions */
        try {
            /* Obtain the range iterator */
            final ListIterator<ArchiveYear> myIterator = pLoader.getReverseIterator();

            /* Loop through the individual year ranges */
            while (myIterator.hasPrevious()) {
                /* Access year */
                final ArchiveYear myYear = myIterator.previous();

                /* Find the range of cells */
                final PrometheusSheetView myView = pWorkBook.getRangeView(myYear.getRangeName());

                /* Declare the new stage */
                pReport.setNewStage("Events from " + myYear.getDate().getYear());

                /* Count the number of Transactions */
                final int myTotal = myView.getRowCount();

                /* Declare the number of steps */
                pReport.setNumSteps(myTotal);

                /* Loop through the rows of the table */
                for (int i = 0; i < myTotal; i++) {
                    /* Access the row */
                    final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                    /* Process transaction and break loop if requested */
                    if (!processTransaction(pLoader, pData, myView, myRow)) {
                        break;
                    }

                    /* Report the progress */
                    pReport.setNextStep();
                }

                /* If we have finished */
                if (!pLoader.checkDate(myYear.getDate())) {
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
        } catch (MetisThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to load " + myList.getItemType().getListName(), e);
        }
    }

    /**
     * Process transaction row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @return continue true/false
     * @throws OceanusException on error
     */
    private static boolean processTransaction(final ArchiveLoader pLoader,
                                              final MoneyWiseData pData,
                                              final PrometheusSheetView pView,
                                              final PrometheusSheetRow pRow) throws OceanusException {
        /* Access parent cache */
        final ParentCache myCache = pLoader.getParentCache();
        int iAdjust = -1;

        /* Access date */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final TethysDate myDate = (myCell != null)
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
        if (!myCache.resolveValues(myDate, myDebit, myCredit, myCategory)) {
            return false;
        }

        /* Build transaction */
        final Transaction myTrans = myCache.buildTransaction(myAmount, myReconciled);

        /* Process TransactionInfo */
        final int myLast = pRow.getMaxValuedCellIndex();
        if (iAdjust < myLast) {
            processTransInfo(pData, myCache, pView, pRow, myTrans, iAdjust);
        }

        /* Continue */
        return true;
    }

    /**
     * Process transaction row from archive.
     * @param pData the DataSet
     * @param pCache the parent cache
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @param pTrans the transaction
     * @param pAdjust the cell#
     * @throws OceanusException on error
     */
    private static void processTransInfo(final MoneyWiseData pData,
                                         final ParentCache pCache,
                                         final PrometheusSheetView pView,
                                         final PrometheusSheetRow pRow,
                                         final Transaction pTrans,
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
            if (!myDilution.startsWith("0.")) {
                myDilution = null;
            }
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
        if (pCache.isDebitReversed()) {
            /* Flip the Debit and credit values */
            final String myTemp = myDebitUnits;
            myDebitUnits = myCreditUnits;
            myCreditUnits = myTemp;
            if (myCreditUnits != null) {
                myCreditUnits = "-" + myCreditUnits;
            }
        } else if (myDebitUnits != null) {
            myDebitUnits = "-" + myDebitUnits;
        } else if (pCache.isRecursive()) {
            myDebitUnits = myCreditUnits;
            myCreditUnits = null;
        }

        /* Add information relating to the account */
        final TransactionInfoList myInfoList = pData.getTransactionInfo();
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.COMMENTS, myDesc);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.TAXCREDIT, myTaxCredit);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.EMPLOYEENATINS, myEmployeeNatIns);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.EMPLOYERNATINS, myEmployerNatIns);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.DEEMEDBENEFIT, myBenefit);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.ACCOUNTDELTAUNITS, myDebitUnits);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.PARTNERDELTAUNITS, myCreditUnits);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.DILUTION, myDilution);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.REFERENCE, myReference);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.QUALIFYYEARS, myYears);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.WITHHELD, myWithheld);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.RETURNEDCASHACCOUNT, myReturnedCashAccount);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.RETURNEDCASH, myReturnedCash);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.PARTNERAMOUNT, myPartnerAmount);
        myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.XCHANGERATE, myXchangeRate);

        /* If we have a TagList */
        if (myTagList != null) {
            /* Process any separated items */
            int iIndex = myTagList.indexOf(TethysListButtonManager.ITEM_SEP);
            while (iIndex != -1) {
                myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.TRANSTAG, myTagList.substring(0, iIndex));
                myTagList = myTagList.substring(iIndex + 1);
                iIndex = myTagList.indexOf(TethysListButtonManager.ITEM_SEP);
            }

            /* Process the single remaining item */
            myInfoList.addInfoItem(null, pTrans, TransactionInfoClass.TRANSTAG, myTagList);
        }
    }
}
