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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataCell;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetPairManager;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * Class to load an archive SpreadSheet.
 */
public class ArchiveLoader {
    /**
     * Number of base archive load areas.
     * 12xStatic,TransactionTags,Regions,2*Category,Schedule,Rate,Price,Account,Range+Transaction.
     */
    private static final int NUM_ARCHIVE_AREAS = 22;

    /**
     * Year boundary.
     */
    private static final int YEAR_BDY = 60;

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
    private TethysDate theLastEvent;

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
     * Have we hit the lastEvent limit.
     */
    private boolean hitEventLimit;

    /**
     * Constructor.
     */
    public ArchiveLoader() {
        /* Create the Years Array */
        theYears = new ArrayList<>();

        /* Create the maps */
        theNameMap = new HashMap<>();
        theCategoryMap = new HashMap<>();
    }

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
    protected boolean checkDate(final TethysDate pDate) {
        return theLastEvent.compareTo(pDate) >= 0;
    }

    /**
     * Load an Archive Workbook.
     * @param pReport the report
     * @param pData the data to load into
     * @param pPreferences the backup preferences
     * @throws OceanusException on error
     */
    public void loadArchive(final MetisThreadStatusReport pReport,
                            final MoneyWiseData pData,
                            final PrometheusBackupPreferences pPreferences) throws OceanusException {
        /* Determine the archive name */
        String myName = pPreferences.getStringValue(PrometheusBackupPreferenceKey.ARCHIVE);
        theLastEvent = pPreferences.getDateValue(PrometheusBackupPreferenceKey.LASTEVENT);
        File myArchive = new File(myName);

        /* Protect the workbook retrieval */
        try (FileInputStream myInFile = new FileInputStream(myArchive);
             InputStream myStream = new BufferedInputStream(myInFile)) {
            /* Determine the WorkBookType */
            MetisWorkBookType myType = MetisWorkBookType.determineType(myName);

            /* Load the data from the stream */
            loadArchiveStream(pReport, pData, myStream, myType);

            /* If we hit the lastEvent limit */
            if (hitEventLimit) {
                /* Note the fact in the data */
                pData.hitEventLimit();
            }

            /* Close the Stream to force out errors */
            myStream.close();

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to load Workbook: " + myArchive.getName(), e);
        }
    }

    /**
     * Load the Static from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    private void loadArchive(final MetisThreadStatusReport pReport,
                             final MetisDataWorkBook pWorkBook,
                             final MoneyWiseData pData) throws OceanusException {
        /* Find the range of cells */
        MetisDataView myView = pWorkBook.getRangeView(AREA_YEARRANGE);

        /* Loop through the cells */
        for (int myIndex = 0; myIndex < myView.getColumnCount(); myIndex++) {
            /* Access the cell and add year to the list */
            MetisDataCell myCell = myView.getCellByPosition(myIndex, 0);
            addYear(myCell.getStringValue());
        }

        /* Access the static */
        ControlDataList myStatic = pData.getControlData();

        /* Add the value into the finance tables (with no security as yet) */
        myStatic.addNewControl(0);

        /* Calculate the number of stages */
        int myStages = NUM_ARCHIVE_AREAS + getNumYears();

        /* Declare the number of stages */
        pReport.setNumStages(myStages);
    }

    /**
     * Load an Archive Workbook from a stream.
     * @param pReport the report
     * @param pData the data to load into
     * @param pStream Input stream to load from
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    private void loadArchiveStream(final MetisThreadStatusReport pReport,
                                   final MoneyWiseData pData,
                                   final InputStream pStream,
                                   final MetisWorkBookType pType) throws OceanusException {
        /* Protect the workbook retrieval */
        try {
            /* Access current profile */
            MetisProfile myTask = pReport.getActiveTask();
            myTask = myTask.startTask("LoadArchive");
            myTask.startTask("ParseWorkBook");

            /* Create the Data */
            theParentCache = new ParentCache(pData);

            /* Access the workbook from the stream */
            MetisDataWorkBook myWorkbook = new MetisDataWorkBook(pStream, pType);
            pReport.checkForCancellation();

            /* Determine Year Range */
            MetisProfile myStage = myTask.startTask("LoadSheets");
            myStage.startTask("Range");
            loadArchive(pReport, myWorkbook, pData);

            /* Load Static Tables */
            myStage.startTask(DepositCategoryType.LIST_NAME);
            SheetDepositCategoryType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(CashCategoryType.LIST_NAME);
            SheetCashCategoryType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(LoanCategoryType.LIST_NAME);
            SheetLoanCategoryType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(PortfolioType.LIST_NAME);
            SheetPortfolioType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(SecurityType.LIST_NAME);
            SheetSecurityType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(PayeeType.LIST_NAME);
            SheetPayeeType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(TransactionCategoryType.LIST_NAME);
            SheetTransCategoryType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(TaxBasis.LIST_NAME);
            SheetTaxBasis.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(AssetCurrency.LIST_NAME);
            SheetAssetCurrency.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(Frequency.LIST_NAME);
            SheetFrequency.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(AccountInfoType.LIST_NAME);
            SheetAccountInfoType.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(TransactionInfoType.LIST_NAME);
            SheetTransInfoType.loadArchive(pReport, myWorkbook, pData);

            /* Load Tags */
            myStage.startTask(TransactionTag.LIST_NAME);
            SheetTransTag.loadArchive(pReport, myWorkbook, pData);

            /* Load Regions */
            myStage.startTask(Region.LIST_NAME);
            SheetRegion.loadArchive(pReport, myWorkbook, pData);

            /* Load Categories */
            myStage.startTask("AccountCategories");
            SheetAccountCategory.loadArchive(pReport, myWorkbook, pData);
            myStage.startTask(TransactionCategory.LIST_NAME);
            SheetTransCategory.loadArchive(pReport, myWorkbook, pData, this);

            /* Load ExchangeRates */
            myStage.startTask(ExchangeRate.LIST_NAME);
            SheetExchangeRate.loadArchive(pReport, myWorkbook, pData, this);

            /* Load Accounts */
            myStage.startTask("Accounts");
            SheetAccount.loadArchive(pReport, myWorkbook, pData, this);
            myStage.startTask(SecurityPrice.LIST_NAME);
            SheetSecurityPrice.loadArchive(pReport, myWorkbook, pData, this);
            myStage.startTask(DepositRate.LIST_NAME);
            SheetDepositRate.loadArchive(pReport, myWorkbook, pData);

            /* Load Transactions */
            myStage.startTask(Transaction.LIST_NAME);
            SheetTransaction.loadArchive(pReport, myWorkbook, pData, this);

            /* Close the stream */
            pStream.close();

            /* Complete task */
            myTask.end();

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to load Workbook", e);
        }
    }

    /**
     * Declare asset.
     * @param pAsset the asset to declare.
     * @throws OceanusException on error
     */
    protected void declareAsset(final AssetBase<?> pAsset) throws OceanusException {
        /* Access the asset name */
        String myName = pAsset.getName();

        /* Check for name already exists */
        if (theNameMap.get(myName) != null) {
            throw new PrometheusDataException(pAsset, DataItem.ERROR_DUPLICATE);
        }

        /* Store the asset */
        theNameMap.put(myName, pAsset);
    }

    /**
     * Declare category.
     * @param pCategory the category to declare.
     * @throws OceanusException on error
     */
    protected void declareCategory(final TransactionCategory pCategory) throws OceanusException {
        /* Access the asset name */
        String myName = pCategory.getName();

        /* Check for name already exists */
        if (theCategoryMap.get(myName) != null) {
            throw new PrometheusDataException(pCategory, DataItem.ERROR_DUPLICATE);
        }

        /* Store the category */
        theCategoryMap.put(myName, pCategory);
    }

    /**
     * Declare security holding.
     * @param pSecurity the security.
     * @param pPortfolio the portfolio
     * @throws OceanusException on error
     */
    protected void declareSecurityHolding(final Security pSecurity,
                                          final String pPortfolio) throws OceanusException {
        /* Access the name */
        String myName = pSecurity.getName();

        /* Check for name already exists */
        if (theNameMap.get(myName) != null) {
            throw new PrometheusDataException(pSecurity, DataItem.ERROR_DUPLICATE);
        }

        /* Store the asset */
        theNameMap.put(myName, new SecurityHoldingDef(pSecurity, pPortfolio));
    }

    /**
     * Declare security holding.
     * @param pName the security holding name
     * @param pAlias the alias name.
     * @param pPortfolio the portfolio
     * @throws OceanusException on error
     */
    protected void declareAliasHolding(final String pName,
                                       final String pAlias,
                                       final String pPortfolio) throws OceanusException {
        /* Check for name already exists */
        Object myHolding = theNameMap.get(pAlias);
        if (!(myHolding instanceof SecurityHoldingDef)) {
            throw new PrometheusDataException(pAlias, "Aliased security not found");
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
     * Process portfolio transfer.
     * @param pData the dataSet
     * @param pSource the source asset
     * @param pTarget the target asset
     * @throws OceanusException on error
     */
    protected void resolvePortfolioXfer(final MoneyWiseData pData,
                                        final Object pSource,
                                        final Object pTarget) throws OceanusException {
        /* Target must be portfolio */
        if (!(pTarget instanceof Portfolio)) {
            throw new MoneyWiseDataException(pTarget, "Inconsistent portfolios");
        }
        Portfolio myPortfolio = (Portfolio) pTarget;

        SecurityHoldingMap myMap = pData.getSecurityHoldingsMap();

        /* Loop through the name map */
        Iterator<Map.Entry<String, Object>> myIterator = theNameMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<String, Object> myEntry = myIterator.next();

            /* If this is a security holding definition */
            Object myValue = myEntry.getValue();
            if (myValue instanceof SecurityHolding) {
                SecurityHolding myHolding = (SecurityHolding) myValue;

                /* If this holding needs updating */
                if (pSource.equals(myHolding) || pSource.equals(myHolding.getPortfolio())) {
                    /* Change the holding */
                    myHolding = myMap.declareHolding(myPortfolio, myHolding.getSecurity());

                    /* Replace definition in map */
                    myEntry.setValue(myHolding);
                }
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
        private final TethysDate theDate;

        /**
         * The range name.
         */
        private final String theRangeName;

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
            TethysFiscalYear myFiscal = TethysFiscalYear.UK;
            theDate = new TethysDate(myYear, myFiscal.getFirstMonth(), myFiscal.getFirstDay());
            theDate.adjustDay(-1);
        }

        /**
         * Get the date.
         * @return the date
         */
        protected TethysDate getDate() {
            return theDate;
        }

        /**
         * Get the range name.
         * @return the name
         */
        protected String getRangeName() {
            return theRangeName;
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
    }

    /**
     * Parent Cache details.
     */
    public class ParentCache {
        /**
         * DataSet.
         */
        private final MoneyWiseData theData;

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
        private TethysDate theDate;

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
         * Is the Debit reversed?
         */
        private boolean isDebitReversed;

        /**
         * Constructor.
         * @param pData the dataSet
         */
        protected ParentCache(final MoneyWiseData pData) {
            /* Store lists */
            theData = pData;
            theList = theData.getTransactions();
            theAssetPairManager = theList.getAssetPairManager();
        }

        /**
         * Build transaction.
         * @param pAmount the amount
         * @param pReconciled is the transaction reconciled?
         * @return the new transaction
         * @throws OceanusException on error
         */
        protected Transaction buildTransaction(final String pAmount,
                                               final boolean pReconciled) throws OceanusException {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<>(Transaction.OBJECT_NAME);
            myValues.addValue(Transaction.FIELD_DATE, theDate);
            myValues.addValue(Transaction.FIELD_CATEGORY, theCategory);
            myValues.addValue(Transaction.FIELD_PAIR, thePair);
            myValues.addValue(Transaction.FIELD_ACCOUNT, theAccount);
            myValues.addValue(Transaction.FIELD_PARTNER, thePartner);
            myValues.addValue(Transaction.FIELD_RECONCILED, pReconciled);
            if (pAmount != null) {
                myValues.addValue(Transaction.FIELD_AMOUNT, pAmount);
            }

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
         * Is the debit reversed?
         * @return true/false
         */
        protected boolean isDebitReversed() {
            return isDebitReversed;
        }

        /**
         * Is the transaction recursive?
         * @return true/false
         */
        protected boolean isRecursive() {
            return theLastDebit.equals(theLastCredit);
        }

        /**
         * Resolve Values.
         * @param pDate the date of the transaction
         * @param pDebit the name of the debit object
         * @param pCredit the name of the credit object
         * @param pCategory the name of the category object
         * @return continue true/false
         * @throws OceanusException on error
         */
        protected boolean resolveValues(final TethysDate pDate,
                                        final String pDebit,
                                        final String pCredit,
                                        final String pCategory) throws OceanusException {
            /* If the Date is null */
            if (pDate == null) {
                /* Resolve child values */
                resolveChildValues(pDebit, pCredit, pCategory);
                return true;
            }

            /* If the date is too late */
            if (!checkDate(pDate)) {
                /* reject the transaction */
                hitEventLimit = true;
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

            /* If the category is portfolio transfer */
            if (theCategory.isCategoryClass(TransactionCategoryClass.PORTFOLIOXFER)) {
                /* Adjust maps to reflect the transfer */
                resolvePortfolioXfer(theData, theLastDebit, theLastCredit);
            }

            /* Resolve assets */
            resolveAssets();
            return true;
        }

        /**
         * Resolve Child Values.
         * @param pDebit the name of the debit object
         * @param pCredit the name of the credit object
         * @param pCategory the name of the category object
         * @throws OceanusException on error
         */
        private void resolveChildValues(final String pDebit,
                                        final String pCredit,
                                        final String pCategory) throws OceanusException {
            /* Handle no LastParent */
            if (theLastParent == null) {
                throw new MoneyWiseDataException(theDate, "Missing parent transaction");
            }

            /* Note that there is a split */
            isSplit = Boolean.TRUE;
            theParent = theLastParent;

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
         * @throws OceanusException on error
         */
        private void resolveAssets() throws OceanusException {
            boolean isDebitHolding = theLastDebit instanceof SecurityHolding;
            boolean isCreditHolding = theLastCredit instanceof SecurityHolding;

            /* Resolve debit and credit */
            TransactionAsset myDebit = TransactionAsset.class.cast(theLastDebit);
            TransactionAsset myCredit = TransactionAsset.class.cast(theLastCredit);

            /* Access asset types */
            AssetType myDebitType = myDebit.getAssetType();
            AssetType myCreditType = myCredit.getAssetType();

            /* Handle non-Asset debit */
            if (!myDebitType.isBaseAccount()) {
                /* Use credit as account */
                isDebitReversed = true;

                /* Handle non-Asset credit */
            } else if (!myCreditType.isBaseAccount()) {
                /* Use debit as account */
                isDebitReversed = false;

                /* Handle non-child transfer */
            } else if (!isSplit) {
                /* Flip values for StockRightsTaken and LoanInterest */
                switch (theCategory.getCategoryTypeClass()) {
                    case STOCKRIGHTSISSUE:
                        /* Use securityHolding as account */
                        isDebitReversed = !myDebitType.isSecurityHolding();
                        break;
                    case LOANINTERESTEARNED:
                        /* Use credit as account */
                        isDebitReversed = true;
                        break;
                    default:
                        /* Use debit as account */
                        isDebitReversed = false;
                        break;
                }
            } else {
                /* Access parent assets */
                TransactionAsset myParAccount = theParent.getAccount();
                TransactionAsset myParPartner = theParent.getPartner();

                /* If we match the parent on debit */
                if (myDebit.equals(myParAccount)) {
                    /* Use debit as account */
                    isDebitReversed = false;

                    /* else if we match credit account */
                } else if (myCredit.equals(myParAccount)) {
                    /* Use credit as account */
                    isDebitReversed = true;

                    /* else don't match the parent account, so parent must be wrong */
                } else {
                    /* Flip parent assets */
                    theParent.flipAssets();

                    /* If we match the partner on debit */
                    if (myDebit.equals(myParPartner)) {
                        /* Use debit as account */
                        isDebitReversed = false;

                    } else {
                        /* Use credit as account */
                        isDebitReversed = true;
                    }
                }
            }

            /* Set up values */
            if (!isDebitReversed) {
                /* Use debit as account */
                theAccount = myDebit;
                thePartner = myCredit;
                Integer myPairId = AssetPair.getEncodedId(myDebitType, myCreditType, null, AssetDirection.TO);
                thePair = theAssetPairManager.lookUpPair(myPairId);
            } else {
                /* Use credit as account */
                theAccount = myCredit;
                thePartner = myDebit;
                Integer myPairId = AssetPair.getEncodedId(myCreditType, myDebitType, null, AssetDirection.FROM);
                thePair = theAssetPairManager.lookUpPair(myPairId);
            }

            /* Resolve portfolio */
            thePortfolio = null;
            if (isDebitHolding) {
                thePortfolio = ((SecurityHolding) theLastDebit).getPortfolio();
                if (isCreditHolding) {
                    Portfolio myPortfolio = ((SecurityHolding) theLastCredit).getPortfolio();
                    if (!thePortfolio.equals(myPortfolio)) {
                        throw new MoneyWiseDataException(theDate, "Inconsistent portfolios");
                    }
                }
            } else if (isCreditHolding) {
                thePortfolio = ((SecurityHolding) theLastCredit).getPortfolio();
            }
        }
    }
}
