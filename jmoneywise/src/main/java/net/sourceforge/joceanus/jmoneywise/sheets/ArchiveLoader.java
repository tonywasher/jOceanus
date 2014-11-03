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
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.WorkBookType;
import net.sourceforge.joceanus.jmetis.viewer.JDataProfile;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseCancelException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetPairManager;
import net.sourceforge.joceanus.jmoneywise.data.AssetType;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Schedule;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoType;
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
     * Number of base archive load areas. 14xStatic,TransactionTags,2*Category,Schedule,Rate,Price,Account,TaxYear,Range+Transaction.
     */
    private static final int NUM_ARCHIVE_AREAS = 24;

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
     * The last event.
     */
    private JDateDay theLastEvent;

    /**
     * The list of years.
     */
    private final List<ArchiveYear> theYears;

    /**
     * The map of names to assets.
     */
    private final Map<String, Object> theNameMap;

    /**
     * The map of names to categories.
     */
    private final Map<String, TransactionCategory> theCategoryMap;

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
     * Check whether date is in range.
     * @param pDate the date to check
     * @return in range true/false
     */
    protected boolean checkDate(final JDateDay pDate) {
        return theLastEvent.compareTo(pDate) >= 0;
    }

    /**
     * Constructor.
     */
    public ArchiveLoader() {
        /* Create the Years Array */
        theYears = new ArrayList<ArchiveYear>();

        /* Create the maps */
        theNameMap = new HashMap<String, Object>();
        theCategoryMap = new HashMap<String, TransactionCategory>();
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
        theLastEvent = pPreferences.getDateValue(BackupPreferences.NAME_LAST_EVENT);
        File myArchive = new File(myName);

        /* Protect the workbook retrieval */
        try (FileInputStream myInFile = new FileInputStream(myArchive);
             InputStream myStream = new BufferedInputStream(myInFile)) {
            /* Determine the WorkBookType */
            WorkBookType myType = WorkBookType.determineType(myName);

            /* Load the data from the stream */
            MoneyWiseData myData = loadArchiveStream(pTask, myStream, myType);

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
     * @return the newly loaded data
     * @throws JOceanusException on error
     */
    private MoneyWiseData loadArchiveStream(final TaskControl<MoneyWiseData> pTask,
                                            final InputStream pStream,
                                            final WorkBookType pType) throws JOceanusException {
        /* Protect the workbook retrieval */
        try {
            /* Access current profile */
            JDataProfile myTask = pTask.getActiveTask();
            myTask = myTask.startTask("LoadArchive");
            myTask.startTask("ParseWorkBook");

            /* Create the Data */
            MoneyWiseData myData = pTask.getNewDataSet();
            theParentCache = new ParentCache(myData);

            /* Access the workbook from the stream */
            DataWorkBook myWorkbook = new DataWorkBook(pStream, pType);

            /* Determine Year Range */
            JDataProfile myStage = myTask.startTask("LoadSheets");
            myStage.startTask("Range");
            boolean bContinue = loadArchive(pTask, myWorkbook, myData);

            /* Load Tables */
            if (bContinue) {
                myStage.startTask(DepositCategoryType.LIST_NAME);
                bContinue = SheetDepositCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(CashCategoryType.LIST_NAME);
                bContinue = SheetCashCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(LoanCategoryType.LIST_NAME);
                bContinue = SheetLoanCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(SecurityType.LIST_NAME);
                bContinue = SheetSecurityType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(PayeeType.LIST_NAME);
                bContinue = SheetPayeeType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(TransactionCategoryType.LIST_NAME);
                bContinue = SheetTransCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(TaxBasis.LIST_NAME);
                bContinue = SheetTaxBasis.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(TaxCategory.LIST_NAME);
                bContinue = SheetTaxCategory.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(AccountCurrency.LIST_NAME);
                bContinue = SheetAccountCurrency.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(TaxRegime.LIST_NAME);
                bContinue = SheetTaxRegime.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(Frequency.LIST_NAME);
                bContinue = SheetFrequency.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(TaxYearInfoType.LIST_NAME);
                bContinue = SheetTaxYearInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(AccountInfoType.LIST_NAME);
                bContinue = SheetAccountInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(TransactionInfoType.LIST_NAME);
                bContinue = SheetTransInfoType.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                myStage.startTask(TransactionTag.LIST_NAME);
                bContinue = SheetTransTag.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                myStage.startTask("AccountCategories");
                bContinue = SheetAccountCategory.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(TransactionCategory.LIST_NAME);
                bContinue = SheetTransCategory.loadArchive(pTask, myWorkbook, myData, this);
            }

            if (bContinue) {
                myStage.startTask(TaxYear.LIST_NAME);
                bContinue = SheetTaxYear.loadArchive(pTask, myWorkbook, myData, this);
            }
            if (bContinue) {
                myData.calculateDateRange();
            }

            if (bContinue) {
                myStage.startTask("Accounts");
                bContinue = SheetAccount.loadArchive(pTask, myWorkbook, myData, this);
            }
            if (bContinue) {
                myStage.startTask(DepositRate.LIST_NAME);
                bContinue = SheetDepositRate.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myStage.startTask(SecurityPrice.LIST_NAME);
                bContinue = SheetSecurityPrice.loadArchive(pTask, myWorkbook, myData, this);
            }

            if (bContinue) {
                myStage.startTask(Transaction.LIST_NAME);
                bContinue = SheetTransaction.loadArchive(pTask, myWorkbook, myData, this);
            }

            /* Close the stream */
            pStream.close();

            /* Set the next stage */
            if (!pTask.setNewStage("Refreshing data")) {
                bContinue = false;
            }

            /* Complete task */
            myTask.end();

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
     * Declare category.
     * @param pCategory the category to declare.
     * @throws JOceanusException on error
     */
    protected void declareCategory(final TransactionCategory pCategory) throws JOceanusException {
        /* Access the asset name */
        String myName = pCategory.getName();

        /* Check for name already exists */
        if (theCategoryMap.get(myName) != null) {
            throw new JPrometheusDataException(pCategory, DataItem.ERROR_DUPLICATE);
        }

        /* Store the category */
        theCategoryMap.put(myName, pCategory);
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
        theNameMap.put(myName, new SecurityHoldingDef(pSecurity, pPortfolio));
    }

    /**
     * Declare security holding.
     * @param pName the security holding name
     * @param pAlias the alias name.
     * @param pPortfolio the portfolio
     * @throws JOceanusException on error
     */
    protected void declareAliasHolding(final String pName,
                                       final String pAlias,
                                       final String pPortfolio) throws JOceanusException {
        /* Check for name already exists */
        Object myHolding = theNameMap.get(pAlias);
        if (!(myHolding instanceof SecurityHoldingDef)) {
            throw new JPrometheusDataException(pAlias, "Aliased security not found");
        }

        /* Store the asset */
        SecurityHoldingDef myAliased = (SecurityHoldingDef) myHolding;
        theNameMap.put(pName, new SecurityHoldingDef(myAliased.getSecurity(), pPortfolio));
    }

    /**
     * Resolve security holdings.
     * @param pData the dataSet
     */
    protected void resolveSecurityHoldings(final MoneyWiseData pData) {
        /* Access securityHoldingsMap and Portfolio list */
        SecurityHoldingMap myMap = pData.getSecurityHoldingsMap();
        PortfolioList myPortfolios = pData.getPortfolios();

        /* Loop through the name map */
        Iterator<Map.Entry<String, Object>> myIterator = theNameMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<String, Object> myEntry = myIterator.next();

            /* If this is a security holding definition */
            Object myValue = myEntry.getValue();
            if (myValue instanceof SecurityHoldingDef) {
                SecurityHoldingDef myDef = (SecurityHoldingDef) myValue;

                /* Access security holding */
                Portfolio myPortfolio = myPortfolios.findItemByName(myDef.getPortfolio());
                SecurityHolding myHolding = myMap.declareHolding(myPortfolio, myDef.getSecurity());

                /* Replace definition in map */
                myEntry.setValue(myHolding);
            }
        }
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
            theDate = new JDateDay(myYear, Month.APRIL, Schedule.END_OF_MONTH_DAY);
        }
    }

    /**
     * Security Holding Definition.
     */
    public static final class SecurityHoldingDef {
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
        private SecurityHoldingDef(final Security pSecurity,
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
         * AssetPair manager.
         */
        private final AssetPairManager theAssetPairManager;

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
        private AssetPair thePair;

        /**
         * Resolved Account.
         */
        private TransactionAsset theAccount;

        /**
         * Resolved Partner.
         */
        private TransactionAsset thePartner;

        /**
         * Resolved Transaction Category.
         */
        private TransactionCategory theCategory;

        /**
         * Resolved Portfolio.
         */
        private Portfolio thePortfolio;

        /**
         * Constructor.
         * @param pData the dataSet
         */
        protected ParentCache(final MoneyWiseData pData) {
            /* Store lists */
            theList = pData.getTransactions();
            theAssetPairManager = theList.getAssetPairManager();
        }

        /**
         * Build transaction.
         * @param pAmount the amount
         * @param pReconciled is the transaction reconciled?
         * @return the new transaction
         * @throws JOceanusException on error
         */
        protected Transaction buildTransaction(final String pAmount,
                                               final boolean pReconciled) throws JOceanusException {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Transaction.OBJECT_NAME);
            myValues.addValue(Transaction.FIELD_DATE, theDate);
            myValues.addValue(Transaction.FIELD_CATEGORY, theCategory);
            myValues.addValue(Transaction.FIELD_PAIR, thePair);
            myValues.addValue(Transaction.FIELD_ACCOUNT, theAccount);
            myValues.addValue(Transaction.FIELD_PARTNER, thePartner);
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

            /* return the new transaction */
            return myTrans;
        }

        /**
         * Resolve Values.
         * @param pDate the date of the transaction
         * @param pDebit the name of the debit object
         * @param pCredit the name of the credit object
         * @param pCategory the name of the category object
         * @return continue true/false
         * @throws JOceanusException on error
         */
        protected boolean resolveValues(final JDateDay pDate,
                                        final String pDebit,
                                        final String pCredit,
                                        final String pCategory) throws JOceanusException {
            /* If the Date is null */
            if (pDate == null) {
                /* Resolve child values */
                resolveChildValues(pDebit, pCredit, pCategory);
                return true;
            }

            /* If the price is too late */
            if (!checkDate(pDate)) {
                /* reject the transaction */
                return false;
            }

            /* Note that there is no split */
            isSplit = Boolean.FALSE;
            theParent = null;

            /* Store the Date */
            theDate = pDate;

            /* Resolve the names */
            theLastDebit = theNameMap.get(pDebit);
            theLastCredit = theNameMap.get(pCredit);
            theCategory = theCategoryMap.get(pCategory);

            /* Resolve assets */
            resolveAssets();
            return true;
        }

        /**
         * Resolve Child Values.
         * @param pDebit the name of the debit object
         * @param pCredit the name of the credit object
         * @param pCategory the name of the category object
         * @throws JOceanusException on error
         */
        private void resolveChildValues(final String pDebit,
                                        final String pCredit,
                                        final String pCategory) throws JOceanusException {
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

            /* Resolve the category */
            theCategory = theCategoryMap.get(pCategory);

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

            /* Resolve debit and credit */
            TransactionAsset myDebit = TransactionAsset.class.cast(theLastDebit);
            TransactionAsset myCredit = TransactionAsset.class.cast(theLastCredit);

            /* Access asset types */
            AssetType myDebitType = myDebit.getAssetType();
            AssetType myCreditType = myCredit.getAssetType();
            boolean bDebitIsAccount;

            /* Handle non-Asset debit */
            if (!myDebitType.isBaseAccount()) {
                /* Use credit as account */
                bDebitIsAccount = false;

                /* Handle non-Asset credit */
            } else if (!myCreditType.isBaseAccount()) {
                /* Use debit as account */
                bDebitIsAccount = true;

                /* Handle non-child transfer */
            } else if (!isSplit) {
                /* Flip values for StockRightsTaken and LoanInterest */
                switch (theCategory.getCategoryTypeClass()) {
                    case STOCKRIGHTSTAKEN:
                    case LOANINTERESTEARNED:
                        /* Use credit as account */
                        bDebitIsAccount = false;
                        break;
                    default:
                        /* Use debit as account */
                        bDebitIsAccount = true;
                        break;
                }
            } else {
                /* Access parent assets */
                TransactionAsset myParAccount = theParent.getAccount();
                TransactionAsset myParPartner = theParent.getPartner();

                /* If we match the parent on debit */
                if (myDebit.equals(myParAccount)) {
                    /* Use debit as account */
                    bDebitIsAccount = true;

                    /* else if we match credit account */
                } else if (myCredit.equals(myParAccount)) {
                    /* Use credit as account */
                    bDebitIsAccount = false;

                    /* else don't match the parent account, so parent must be wrong */
                } else {
                    /* Flip parent assets */
                    theParent.flipAssets();

                    /* If we match the partner on debit */
                    if (myDebit.equals(myParPartner)) {
                        /* Use debit as account */
                        bDebitIsAccount = true;

                    } else {
                        /* Use credit as account */
                        bDebitIsAccount = false;
                    }
                }
            }

            /* Set up values */
            if (bDebitIsAccount) {
                /* Use debit as account */
                theAccount = myDebit;
                thePartner = myCredit;
                Integer myPairId = AssetPair.getEncodedId(myDebitType, myCreditType, AssetDirection.TO);
                thePair = theAssetPairManager.lookUpPair(myPairId);
            } else {
                /* Use credit as account */
                theAccount = myCredit;
                thePartner = myDebit;
                Integer myPairId = AssetPair.getEncodedId(myCreditType, myDebitType, AssetDirection.FROM);
                thePair = theAssetPairManager.lookUpPair(myPairId);
            }

            /* Resolve portfolio */
            thePortfolio = null;
            if (isDebitHolding) {
                thePortfolio = ((SecurityHolding) theLastDebit).getPortfolio();
                if (isCreditHolding) {
                    Portfolio myPortfolio = ((SecurityHolding) theLastCredit).getPortfolio();
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
