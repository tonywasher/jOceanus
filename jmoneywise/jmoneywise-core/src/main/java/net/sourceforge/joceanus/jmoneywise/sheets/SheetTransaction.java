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

import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.sheets.ArchiveLoader.ArchiveYear;
import net.sourceforge.joceanus.jmoneywise.sheets.ArchiveLoader.ParentCache;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for Transaction.
 * @author Tony Washer
 */
public class SheetTransaction
        extends SheetEncrypted<Transaction, MoneyWiseDataType> {
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
     * Split column.
     */
    private static final int COL_SPLIT = COL_RECONCILED + 1;

    /**
     * Reconciled column.
     */
    private static final int COL_PARENT = COL_SPLIT + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader
     * the spreadsheet reader
     */
    protected SheetTransaction(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TRANS);

        /* Access the Lists */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getTransactions());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter
     * the spreadsheet writer
     */
    protected SheetTransaction(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TRANS);

        /* Access the Transactions list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getTransactions());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Transaction.OBJECT_NAME);
        myValues.addValue(Transaction.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(Transaction.FIELD_PAIR, loadInteger(COL_PAIR));
        myValues.addValue(Transaction.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Transaction.FIELD_ACCOUNT, loadInteger(COL_ACCOUNT));
        myValues.addValue(Transaction.FIELD_PARTNER, loadInteger(COL_PARTNER));
        myValues.addValue(Transaction.FIELD_AMOUNT, loadBytes(COL_AMOUNT));
        myValues.addValue(Transaction.FIELD_RECONCILED, loadBoolean(COL_RECONCILED));
        myValues.addValue(Transaction.FIELD_SPLIT, loadBoolean(COL_SPLIT));
        myValues.addValue(Transaction.FIELD_PARENT, loadInteger(COL_PARENT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Transaction pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeDate(COL_DATE, pItem.getDate());
        writeInteger(COL_PAIR, pItem.getAssetPairId());
        writeInteger(COL_ACCOUNT, pItem.getAccountId());
        writeInteger(COL_PARTNER, pItem.getPartnerId());
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeBoolean(COL_RECONCILED, pItem.isReconciled());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
        writeBoolean(COL_SPLIT, pItem.isSplit());
        writeInteger(COL_PARENT, pItem.getParentId());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_PARENT;
    }

    /**
     * Load the Events from an archive.
     * @param pTask
     * the task control
     * @param pWorkBook
     * the workbook
     * @param pData
     * the data set to load into
     * @param pLoader
     * the archive loader
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException
     * on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final ArchiveLoader pLoader) throws JOceanusException {
        /* Access the list of transactions */
        TransactionList myList = pData.getTransactions();
        TransactionInfoList myInfoList = pData.getTransactionInfo();

        /* Protect against exceptions */
        try {
            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Obtain the range iterator */
            ListIterator<ArchiveYear> myIterator = pLoader.getReverseIterator();

            /* Loop through the individual year ranges */
            while (myIterator.hasPrevious()) {
                /* Access year */
                ArchiveYear myYear = myIterator.previous();

                /* Find the range of cells */
                DataView myView = pWorkBook.getRangeView(myYear.getRangeName());

                /* Declare the new stage */
                if (!pTask.setNewStage("Events from " + myYear.getDate().getYear())) {
                    return false;
                }

                /* Count the number of Transactions */
                int myTotal = myView.getRowCount();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table */
                for (int i = 0; i < myTotal; i++) {
                    /* Access the row */
                    DataRow myRow = myView.getRowByIndex(i);

                    /* Process transaction and break loop if requested */
                    if (!processTransaction(pLoader, pData, myView, myRow)) {
                        break;
                    }

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
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
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Process transaction row from archive.
     * @param pLoader
     * the archive loader
     * @param pData
     * the DataSet
     * @param pView
     * the spreadsheet view
     * @param pRow
     * the spreadsheet row
     * @return continue true/false
     * @throws JOceanusException
     * on error
     */
    private static boolean processTransaction(final ArchiveLoader pLoader,
                                              final MoneyWiseData pData,
                                              final DataView pView,
                                              final DataRow pRow) throws JOceanusException {
        /* Access parent cache */
        ParentCache myCache = pLoader.getParentCache();
        int iAdjust = 0;

        /* Access date */
        DataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        JDateDay myDate = (myCell != null)
                                          ? myCell.getDateValue()
                                          : null;

        /* Access the values */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myDebit = (myCell != null)
                                         ? myCell.getStringValue()
                                         : null;
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myCredit = (myCell != null)
                                          ? myCell.getStringValue()
                                          : null;
        String myAmount = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        String myCategory = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Handle Reconciled which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean myReconciled = Boolean.FALSE;
        if (myCell != null) {
            myReconciled = Boolean.TRUE;
        }

        /* Set defaults */
        if (!myCache.resolveValues(myDate, myDebit, myCredit, myCategory)) {
            return false;
        }

        /* Build transaction */
        Transaction myTrans = myCache.buildTransaction(myAmount, myReconciled);

        /* Handle Description which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myDesc = null;
        if (myCell != null) {
            myDesc = myCell.getStringValue();
        }

        /* Handle Tax Credit which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myTaxCredit = null;
        if (myCell != null) {
            myTaxCredit = myCell.getStringValue();
        }

        /* Handle NatInsurance which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myNatInsurance = null;
        if (myCell != null) {
            myNatInsurance = myCell.getStringValue();
        }

        /* Handle Benefit which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myBenefit = null;
        if (myCell != null) {
            myBenefit = myCell.getStringValue();
        }

        /* Handle DebitUnits which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myDebitUnits = null;
        if (myCell != null) {
            myDebitUnits = myCell.getStringValue();
        }

        /* Handle CreditUnits which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myCreditUnits = null;
        if (myCell != null) {
            myCreditUnits = myCell.getStringValue();
        }

        /* Handle Dilution which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myDilution = null;
        if (myCell != null) {
            myDilution = myCell.getStringValue();
            if (!myDilution.startsWith("0.")) {
                myDilution = null;
            }
        }

        /* Handle Reference which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myReference = null;
        if (myCell != null) {
            myReference = myCell.getStringValue();
        }

        /* Handle Years which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Integer myYears = null;
        if (myCell != null) {
            myYears = myCell.getIntegerValue();
        }

        /* Handle Donation which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myDonation = null;
        if (myCell != null) {
            myDonation = myCell.getStringValue();
        }

        /* Handle ThirdParty which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myThirdParty = null;
        if (myCell != null) {
            myThirdParty = myCell.getStringValue();
        }

        /* Handle TagList which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myTagList = null;
        if (myCell != null) {
            myTagList = myCell.getStringValue();
        }

        /* Add information relating to the account */
        TransactionInfoList myInfoList = pData.getTransactionInfo();
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.COMMENTS, myDesc);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.TAXCREDIT, myTaxCredit);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.NATINSURANCE, myNatInsurance);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.DEEMEDBENEFIT, myBenefit);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.DEBITUNITS, myDebitUnits);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.CREDITUNITS, myCreditUnits);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.DILUTION, myDilution);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.REFERENCE, myReference);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.QUALIFYYEARS, myYears);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.CHARITYDONATION, myDonation);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.THIRDPARTY, myThirdParty);
        myInfoList.addInfoItem(null, myTrans, TransactionInfoClass.TRANSTAG, myTagList);

        /* Continue */
        return true;
    }
}