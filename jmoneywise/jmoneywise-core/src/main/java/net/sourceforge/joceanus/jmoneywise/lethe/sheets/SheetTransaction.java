/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCell;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetRow;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetView;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBook;
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
                                      final MetisSheetWorkBook pWorkBook,
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
                final MetisSheetView myView = pWorkBook.getRangeView(myYear.getRangeName());

                /* Declare the new stage */
                pReport.setNewStage("Events from " + myYear.getDate().getYear());

                /* Count the number of Transactions */
                final int myTotal = myView.getRowCount();

                /* Declare the number of steps */
                pReport.setNumSteps(myTotal);

                /* Loop through the rows of the table */
                for (int i = 0; i < myTotal; i++) {
                    /* Access the row */
                    final MetisSheetRow myRow = myView.getRowByIndex(i);

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
                                              final MetisSheetView pView,
                                              final MetisSheetRow pRow) throws OceanusException {
        /* Access parent cache */
        final ParentCache myCache = pLoader.getParentCache();
        int iAdjust = -1;

        /* Access date */
        MetisSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final TethysDate myDate = (myCell != null)
                                                   ? myCell.getDateValue()
                                                   : null;

        /* Access the values */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final String myDebit = (myCell != null)
                                                ? myCell.getStringValue()
                                                : null;
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final String myCredit = (myCell != null)
                                                 ? myCell.getStringValue()
                                                 : null;
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        final String myAmount = (myCell != null)
                                                 ? myCell.getStringValue()
                                                 : null;
        final String myCategory = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();

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

        /* Handle Description which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myDesc = null;
        if (myCell != null) {
            myDesc = myCell.getStringValue();
        }

        /* Handle Tax Credit which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myTaxCredit = null;
        if (myCell != null) {
            myTaxCredit = myCell.getStringValue();
        }

        /* Handle EmployeeNatIns which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myEmployeeNatIns = null;
        if (myCell != null) {
            myEmployeeNatIns = myCell.getStringValue();
        }

        /* Handle EmployerNatIns which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myEmployerNatIns = null;
        if (myCell != null) {
            myEmployerNatIns = myCell.getStringValue();
        }

        /* Handle Benefit which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myBenefit = null;
        if (myCell != null) {
            myBenefit = myCell.getStringValue();
        }

        /* Handle DebitUnits which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myDebitUnits = null;
        if (myCell != null) {
            myDebitUnits = myCell.getStringValue();
        }

        /* Handle CreditUnits which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myCreditUnits = null;
        if (myCell != null) {
            myCreditUnits = myCell.getStringValue();
        }

        /* Handle Dilution which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myDilution = null;
        if (myCell != null) {
            myDilution = myCell.getStringValue();
            if (!myDilution.startsWith("0.")) {
                myDilution = null;
            }
        }

        /* Handle Reference which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myReference = null;
        if (myCell != null) {
            myReference = myCell.getStringValue();
        }

        /* Handle Years which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Integer myYears = null;
        if (myCell != null) {
            myYears = myCell.getIntegerValue();
        }

        /* Handle Withheld which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myWithheld = null;
        if (myCell != null) {
            myWithheld = myCell.getStringValue();
        }

        /* Handle ReturnedCashAccount which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myReturnedCashAccount = null;
        if (myCell != null) {
            myReturnedCashAccount = myCell.getStringValue();
        }

        /* Handle ReturnedCash which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myReturnedCash = null;
        if (myCell != null) {
            myReturnedCash = myCell.getStringValue();
        }

        /* Handle PartnerAmount which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myPartnerAmount = null;
        if (myCell != null) {
            myPartnerAmount = myCell.getStringValue();
        }

        /* Handle XchangeRate which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myXchangeRate = null;
        if (myCell != null) {
            myXchangeRate = myCell.getStringValue();
        }

        /* Handle TagList which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myTagList = null;
        if (myCell != null) {
            myTagList = myCell.getStringValue();
        }

        /* If the debit was reversed */
        if (myCache.isDebitReversed()) {
            /* Flip the Debit and credit values */
            final String myTemp = myDebitUnits;
            myDebitUnits = myCreditUnits;
            myCreditUnits = myTemp;
            if (myCreditUnits != null) {
                myCreditUnits = "-" + myCreditUnits;
            }
        } else if (myDebitUnits != null) {
            myDebitUnits = "-" + myDebitUnits;
        } else if (myCache.isRecursive()) {
            myDebitUnits = myCreditUnits;
            myCreditUnits = null;
        }

        /* Add information relating to the account */
        final TransactionInfoList myInfoList = pData.getTransactionInfo();
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.COMMENTS, myDesc);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.TAXCREDIT, myTaxCredit);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.EMPLOYEENATINS, myEmployeeNatIns);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.EMPLOYERNATINS, myEmployerNatIns);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.DEEMEDBENEFIT, myBenefit);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.ACCOUNTDELTAUNITS, myDebitUnits);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.PARTNERDELTAUNITS, myCreditUnits);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.DILUTION, myDilution);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.REFERENCE, myReference);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.QUALIFYYEARS, myYears);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.WITHHELD, myWithheld);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.RETURNEDCASHACCOUNT, myReturnedCashAccount);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.RETURNEDCASH, myReturnedCash);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.PARTNERAMOUNT, myPartnerAmount);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.XCHANGERATE, myXchangeRate);

        /* If we have a TagList */
        if (myTagList != null) {
            /* Process any separated items */
            int iIndex = myTagList.indexOf(TethysListButtonManager.ITEM_SEP);
            while (iIndex != -1) {
                myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.TRANSTAG, myTagList.substring(0, iIndex));
                myTagList = myTagList.substring(iIndex + 1);
                iIndex = myTagList.indexOf(TethysListButtonManager.ITEM_SEP);
            }

            /* Process the single remaining item */
            myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.TRANSTAG, myTagList);
        }

        /* Continue */
        return true;
    }
}
