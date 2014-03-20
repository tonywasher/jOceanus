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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.WorkBookType;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseCancelException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * Class to load an archive SpreadSheet.
 */
public class ArchiveLoader {
    /**
     * Number of base archive load areas. 11xStatic,EventClasses,2*Category,Pattern,Rate,Price,Account,TaxYear,Range+Event.
     */
    private static final int NUM_ARCHIVE_AREAS = 22;

    /**
     * Year boundary.
     */
    private static final int YEAR_BDY = 50;

    /**
     * Year constant.
     */
    private static final int YEAR_1900 = 1900;

    /**
     * Year constant.
     */
    private static final int YEAR_2000 = 2000;

    /**
     * NamedRange for Static.
     */
    private static final String AREA_YEARRANGE = "AssetsYears";

    /**
     * The list of years.
     */
    private final List<ArchiveYear> theYears;

    /**
     * The map of names to assets.
     */
    private final Map<String, Object> theNameMap;

    /**
     * The ParentCache.
     */
    private ParentCache theParentCache;

    /**
     * Get the iterator.
     * @return the iterator
     */
    protected ListIterator<ArchiveYear> getIterator() {
        return theYears.listIterator();
    }

    /**
     * Get the reverse iterator.
     * @return the iterator
     */
    protected ListIterator<ArchiveYear> getReverseIterator() {
        return theYears.listIterator(theYears.size());
    }

    /**
     * Get the number of years.
     * @return the number of years
     */
    protected int getNumYears() {
        return theYears.size();
    }

    /**
     * Get the parent cache.
     * @return the parent cache
     */
    protected ParentCache getParentCache() {
        return theParentCache;
    }

    /**
     * Add a year to the front of the list.
     * @param pName the range name
     */
    private void addYear(final String pName) {
        ArchiveYear myYear = new ArchiveYear(pName);
        theYears.add(myYear);
    }

    /**
     * Constructor.
     */
    public ArchiveLoader() {
        /* Create the Years Array */
        theYears = new ArrayList<ArchiveYear>();

        /* Create the names map */
        theNameMap = new HashMap<String, Object>();
    }

    /**
     * Load an Archive Workbook.
     * @param pTask Task Control for task
     * @param pPreferences the backup preferences
     * @return the newly loaded data
     * @throws JOceanusException on error
     */
    public MoneyWiseData loadArchive(final TaskControl<MoneyWiseData> pTask,
                                     final BackupPreferences pPreferences) throws JOceanusException {
        /* Determine the archive name */
        String myName = pPreferences.getStringValue(BackupPreferences.NAME_ARCHIVE_FILE);
        JDateDay myLastEvent = pPreferences.getDateValue(BackupPreferences.NAME_LAST_EVENT);
        File myArchive = new File(myName);

        /* Protect the workbook retrieval */
        try (FileInputStream myInFile = new FileInputStream(myArchive);
             InputStream myStream = new BufferedInputStream(myInFile)) {
            /* Determine the WorkBookType */
            WorkBookType myType = WorkBookType.determineType(myName);

            /* Load the data from the stream */
            MoneyWiseData myData = loadArchiveStream(pTask, myStream, myType, myLastEvent);

            /* Close the Stream to force out errors */
            myStream.close();
            return myData;

        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to load Workbook: " + myArchive.getName(), e);
        }
    }

    /**
     * Load the Static from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    private boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                final DataWorkBook pWorkBook,
                                final MoneyWiseData pData) throws JOceanusException {
        /* Find the range of cells */
        DataView myView = pWorkBook.getRangeView(AREA_YEARRANGE);

        /* Loop through the cells */
        for (int myIndex = 0; myIndex < myView.getColumnCount(); myIndex++) {
            /* Access the cell and add year to the list */
            DataCell myCell = myView.getCellByPosition(myIndex, 0);
            addYear(myCell.getStringValue());
        }

        /* Access the static */
        ControlDataList myStatic = pData.getControlData();

        /* Add the value into the finance tables (with no security as yet) */
        myStatic.addNewControl(0);

        /* Calculate the number of stages */
        int myStages = NUM_ARCHIVE_AREAS + getNumYears();

        /* Declare the number of stages */
        return pTask.setNumStages(myStages);
    }

    /**
     * Load an Archive Workbook from a stream.
     * @param pTask Task Control for task
     * @param pStream Input stream to load from
     * @param pType the workBookType
     * @param pLastEvent the last event
     * @return the newly loaded data
     * @throws JOceanusException on error
     */
    private MoneyWiseData loadArchiveStream(final TaskControl<MoneyWiseData> pTask,
                                            final InputStream pStream,
                                            final WorkBookType pType,
                                            final JDateDay pLastEvent) throws JOceanusException {
        /* Protect the workbook retrieval */
        try {
            /* Create the Data */
            MoneyWiseData myData = pTask.getNewDataSet();
            theParentCache = new ParentCache(myData);

            /* Access the workbook from the stream */
            DataWorkBook myWorkbook = new DataWorkBook(pStream, pType);

            /* Determine Year Range */
            boolean bContinue = loadArchive(pTask, myWorkbook, myData);

            /* Load Tables */
            if (bContinue) {
                bContinue = SheetDepositCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetCashCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetLoanCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetSecurityType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetPayeeType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetEventCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxBasis.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxCategory.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountCurrency.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxRegime.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetFrequency.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxYearInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetEventInfoType.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                bContinue = SheetEventTag.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                bContinue = SheetAccountCategory.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetEventCategory.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                bContinue = SheetTaxYear.loadArchive(pTask, myWorkbook, myData, this);
            }
            if (bContinue) {
                myData.calculateDateRange();
            }

            if (bContinue) {
                bContinue = SheetAccount.loadArchive(pTask, myWorkbook, myData, this);
            }
            if (bContinue) {
                bContinue = SheetDepositRate.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetSecurityPrice.loadArchive(pTask, myWorkbook, myData, pLastEvent);
            }

            if (bContinue) {
                bContinue = SheetTransaction.loadArchive(pTask, myWorkbook, myData, this, pLastEvent);
            }

            /* Close the stream */
            pStream.close();

            /* Set the next stage */
            if (!pTask.setNewStage("Refreshing data")) {
                bContinue = false;
            }

            /* Check for cancellation */
            if (!bContinue) {
                throw new JMoneyWiseCancelException("Operation Cancelled");
            }

            /* Return the data */
            return myData;
        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to load Workbook", e);
        }
    }

    /**
     * Declare asset.
     * @param pAsset the asset to declare.
     * @throws JOceanusException on error
     */
    protected void declareAsset(final AssetBase<?> pAsset) throws JOceanusException {
        /* Access the asset name */
        String myName = pAsset.getName();

        /* Check for name already exists */
        if (theNameMap.get(myName) != null) {
            throw new JPrometheusDataException(pAsset, DataItem.ERROR_DUPLICATE);
        }

        /* Store the asset */
        theNameMap.put(myName, pAsset);
    }

    /**
     * Declare security holding.
     * @param pSecurity the security.
     * @param pPortfolio the portfolio
     * @throws JOceanusException on error
     */
    protected void declareSecurityHolding(final Security pSecurity,
                                          final String pPortfolio) throws JOceanusException {
        /* Access the name */
        String myName = pSecurity.getName();

        /* Check for name already exists */
        if (theNameMap.get(myName) != null) {
            throw new JPrometheusDataException(pSecurity, DataItem.ERROR_DUPLICATE);
        }

        /* Store the asset */
        theNameMap.put(myName, new SecurityHolding(pSecurity, pPortfolio));
    }

    /**
     * Declare security holding.
     * @param pName the security holding name
     * @param pSecurity the security.
     * @param pPortfolio the portfolio
     * @throws JOceanusException on error
     */
    protected void declareSecurityHolding(final String pName,
                                          final Security pSecurity,
                                          final String pPortfolio) throws JOceanusException {
        /* Check for name already exists */
        if (theNameMap.get(pName) != null) {
            throw new JPrometheusDataException(pName, DataItem.ERROR_DUPLICATE);
        }

        /* Handle invalid security */
        if (pSecurity == null) {
            throw new JPrometheusDataException(pName, "Aliased security not found");
        }

        /* Store the asset */
        theNameMap.put(pName, new SecurityHolding(pSecurity, pPortfolio));
    }

    /**
     * Simple class to define an archive year.
     */
    protected static final class ArchiveYear {
        /**
         * The date.
         */
        private final JDateDay theDate;

        /**
         * The range name.
         */
        private final String theRangeName;

        /**
         * Get the date.
         * @return the date
         */
        protected JDateDay getDate() {
            return theDate;
        }

        /**
         * Get the range name.
         * @return the name
         */
        protected String getRangeName() {
            return theRangeName;
        }

        /**
         * Constructor.
         * @param pName the range name
         */
        private ArchiveYear(final String pName) {
            /* Store parameters */
            theRangeName = pName;

            /* Isolate the year part */
            int myLen = pName.length();
            int myYear = Integer.parseInt(pName.substring(myLen - 2));

            /* Calculate the actual year */
            if (myYear < YEAR_BDY) {
                myYear += YEAR_2000;
            } else {
                myYear += YEAR_1900;
            }

            /* Create the date */
            theDate = new JDateDay(myYear, Calendar.APRIL, TaxYear.END_OF_MONTH_DAY);
        }
    }

    /**
     * Security Holding.
     */
    public static final class SecurityHolding {
        /**
         * Security.
         */
        private final Security theSecurity;

        /**
         * Portfolio.
         */
        private final String thePortfolio;

        /**
         * Obtain security.
         * @return the security
         */
        public Security getSecurity() {
            return theSecurity;
        }

        /**
         * Obtain portfolio.
         * @return the portfolio
         */
        public String getPortfolio() {
            return thePortfolio;
        }

        /**
         * Constructor.
         * @param pSecurity the security
         * @param pPortfolio the portfolio
         */
        private SecurityHolding(final Security pSecurity,
                                final String pPortfolio) {
            /* Store parameters */
            theSecurity = pSecurity;
            thePortfolio = pPortfolio;
        }
    }

    /**
     * Parent Cache details.
     */
    public class ParentCache {
        /**
         * TransactionList.
         */
        private final TransactionList theList;

        /**
         * TransactionInfoList.
         */
        private final TransactionInfoList theInfoList;

        /**
         * Last Parent.
         */
        private Transaction theLastParent;

        /**
         * Last Debit.
         */
        private Object theLastDebit;

        /**
         * Last Credit.
         */
        private Object theLastCredit;

        /**
         * The parent.
         */
        private Transaction theParent;

        /**
         * Split Status.
         */
        private boolean isSplit;

        /**
         * Resolved Date.
         */
        private JDateDay theDate;

        /**
         * AssetPair Id.
         */
        private Integer thePairId;

        /**
         * Resolved Debit.
         */
        private AssetBase<?> theDebit;

        /**
         * Resolved Credit.
         */
        private AssetBase<?> theCredit;

        /**
         * Resolved Portfolio.
         */
        private String thePortfolio;

        /**
         * Constructor.
         * @param pData the dataSet
         */
        protected ParentCache(final MoneyWiseData pData) {
            /* Store lists */
            theList = pData.getTransactions();
            theInfoList = pData.getTransactionInfo();
        }

        /**
         * Build transaction.
         * @param pCategory the category
         * @param pAmount the amount
         * @param pReconciled is the transaction reconciled?
         * @return the new transaction
         * @throws JOceanusException on error
         */
        protected Transaction buildTransaction(final String pCategory,
                                               final String pAmount,
                                               final boolean pReconciled) throws JOceanusException {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Transaction.OBJECT_NAME);
            myValues.addValue(Transaction.FIELD_DATE, theDate);
            myValues.addValue(Transaction.FIELD_CATEGORY, pCategory);
            myValues.addValue(Transaction.FIELD_PAIR, thePairId);
            myValues.addValue(Transaction.FIELD_DEBIT, theDebit.getName());
            myValues.addValue(Transaction.FIELD_CREDIT, theCredit.getName());
            myValues.addValue(Transaction.FIELD_AMOUNT, pAmount);
            myValues.addValue(Transaction.FIELD_RECONCILED, pReconciled);
            myValues.addValue(Transaction.FIELD_SPLIT, isSplit);
            myValues.addValue(Transaction.FIELD_PARENT, theParent);

            /* Add the value into the list */
            Transaction myTrans = theList.addValuesItem(myValues);

            /* If we were not a child */
            if (!isSplit) {
                /* Note the last parent */
                theLastParent = myTrans;
            }

            /* If we have a portfolio */
            if (thePortfolio != null) {
                /* Add the item */
                theInfoList.addInfoItem(null, myTrans, EventInfoClass.PORTFOLIO, thePortfolio);
            }

            /* return the new transaction */
            return myTrans;
        }

        /**
         * Resolve Values.
         * @param pDate the date of the transaction
         * @param pDebit the name of the debit object
         * @param pCredit the name of the credit object
         * @throws JOceanusException on error
         */
        protected void resolveValues(final JDateDay pDate,
                                     final String pDebit,
                                     final String pCredit) throws JOceanusException {
            /* If the Date is null */
            if (pDate == null) {
                /* Resolve child values */
                resolveChildValues(pDebit, pCredit);
                return;
            }

            /* Note that there is no split */
            isSplit = Boolean.FALSE;
            theParent = null;

            /* Store the Date */
            theDate = pDate;

            /* Resolve the debit and credit */
            Object myDebit = theNameMap.get(pDebit);
            Object myCredit = theNameMap.get(pCredit);

            /* Store last credit and debit */
            theLastDebit = myDebit;
            theLastCredit = myCredit;

            /* Resolve assets */
            resolveAssets();
        }

        /**
         * Resolve Child Values.
         * @param pDebit the name of the debit object
         * @param pCredit the name of the credit object
         * @throws JOceanusException on error
         */
        private void resolveChildValues(final String pDebit,
                                        final String pCredit) throws JOceanusException {
            /* Handle no LastParent */
            if (theLastParent == null) {
                throw new JMoneyWiseDataException(theDate, "Missing parent transaction");
            }

            /* Note that there is a split */
            isSplit = Boolean.TRUE;
            theParent = theLastParent;
            theLastParent.setSplit(Boolean.TRUE);

            /* Resolve the debit and credit */
            Object myDebit = (pDebit == null)
                                             ? theLastDebit
                                             : theNameMap.get(pDebit);
            Object myCredit = (pCredit == null)
                                               ? theLastCredit
                                               : theNameMap.get(pCredit);

            /* Store last credit and debit */
            theLastDebit = myDebit;
            theLastCredit = myCredit;

            /* Resolve assets */
            resolveAssets();
        }

        /**
         * Resolve assets.
         * @throws JOceanusException on error
         */
        private void resolveAssets() throws JOceanusException {
            boolean isDebitHolding = theLastDebit instanceof SecurityHolding;
            boolean isCreditHolding = theLastCredit instanceof SecurityHolding;

            /* Resolve debit */
            theDebit = isDebitHolding
                                     ? ((SecurityHolding) theLastDebit).getSecurity()
                                     : AssetBase.class.cast(theLastDebit);
            /* Resolve credit */
            theCredit = isCreditHolding
                                       ? ((SecurityHolding) theLastCredit).getSecurity()
                                       : AssetBase.class.cast(theLastCredit);

            /* Resolve the pair */
            thePairId = AssetPair.getEncodedId(theDebit.getAssetType(), theCredit.getAssetType());

            /* Resolve portfolio */
            thePortfolio = null;
            if (isDebitHolding) {
                thePortfolio = ((SecurityHolding) theLastDebit).getPortfolio();
                if (isCreditHolding) {
                    String myPortfolio = ((SecurityHolding) theLastCredit).getPortfolio();
                    if (!thePortfolio.equals(myPortfolio)) {
                        throw new JMoneyWiseDataException(theDate, "Inconsistent portfolios");
                    }
                }
            } else if (isCreditHolding) {
                thePortfolio = ((SecurityHolding) theLastCredit).getPortfolio();
            }
        }
    }
}
