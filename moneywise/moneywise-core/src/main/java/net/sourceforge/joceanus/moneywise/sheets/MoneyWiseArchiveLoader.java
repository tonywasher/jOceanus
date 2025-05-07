/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.sheets;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusFiscalYear;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusControlData.PrometheusControlDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetProvider;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class to load an archive SpreadSheet.
 */
public class MoneyWiseArchiveLoader {
    /**
     * Number of base archive load areas.
     * 11xStatic,TransactionTags,Regions,2*Category,XchgRate,Rate,Price,Account.
     */
    private static final int NUM_ARCHIVE_AREAS = 19;

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
     * The list of years.
     */
    private final List<MoneyWiseArchiveYear> theYears;

    /**
     * The map of names to assets.
     */
    private final Map<String, Object> theNameMap;

    /**
     * The map of names to categories.
     */
    private final Map<String, MoneyWiseTransCategory> theCategoryMap;

    /**
     * The last event.
     */
    private OceanusDate theLastEvent;

    /**
     * The ParentCache.
     */
    private MoneyWiseParentCache theParentCache;

    /**
     * Have we hit the lastEvent limit.
     */
    private boolean hitEventLimit;

    /**
     * Are we filtering?.
     */
    private boolean enableFiltering;

    /**
     * Constructor.
     */
    public MoneyWiseArchiveLoader() {
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
    protected ListIterator<MoneyWiseArchiveYear> getIterator() {
        return theYears.listIterator();
    }

    /**
     * Get the reverse iterator.
     * @return the iterator
     */
    protected ListIterator<MoneyWiseArchiveYear> getReverseIterator() {
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
    protected MoneyWiseParentCache getParentCache() {
        return theParentCache;
    }

    /**
     * Add a year to the front of the list.
     * @param pName the range name
     */
    private void addYear(final String pName) {
        final MoneyWiseArchiveYear myYear = new MoneyWiseArchiveYear(pName);
        theYears.add(myYear);
    }

    /**
     * Check whether date is in range.
     * @param pDate the date to check
     * @return in range true/false
     */
    protected boolean checkDate(final OceanusDate pDate) {
        return theLastEvent.compareTo(pDate) >= 0;
    }

    /**
     * Enable filtering.
     */
    public void enableFiltering() {
        enableFiltering = true;
    }

    /**
     * Set lastEvent.
     * @param pLastEvent the last event date
     */
    public void setLastEvent(final OceanusDate pLastEvent) {
        theLastEvent = pLastEvent;
    }

    /**
     * Load an Archive Workbook.
     * @param pReport the report
     * @param pData the data to load into
     * @param pPreferences the backup preferences
     * @throws OceanusException on error
     */
    public void loadArchive(final TethysUIThreadStatusReport pReport,
                            final MoneyWiseDataSet pData,
                            final PrometheusBackupPreferences pPreferences) throws OceanusException {
        /* Determine the archive name */
        final String myName = pPreferences.getStringValue(PrometheusBackupPreferenceKey.ARCHIVE);
        final File myArchive = new File(myName);

        /* Look up last event if it has not been explicitly set */
        if (theLastEvent == null) {
            theLastEvent = pPreferences.getDateValue(PrometheusBackupPreferenceKey.LASTEVENT);
        }

        /* Protect the workbook retrieval */
        try (FileInputStream myInFile = new FileInputStream(myArchive);
             InputStream myStream = new BufferedInputStream(myInFile)) {
            /* Determine the WorkBookType */
            final PrometheusSheetWorkBookType myType = PrometheusSheetWorkBookType.determineType(myName);

            /* Load the data from the stream */
            loadArchiveStream(pReport, pData, myStream, myType);

            /* If we hit the lastEvent limit */
            if (hitEventLimit) {
                /* Note the fact in the data */
                pData.hitEventLimit();
            }

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
    private void loadArchive(final TethysUIThreadStatusReport pReport,
                             final PrometheusSheetWorkBook pWorkBook,
                             final MoneyWiseDataSet pData) throws OceanusException {
        /* Find the range of cells */
        final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_YEARRANGE);

        /* Loop through the cells */
        for (int myIndex = 0; myIndex < myView.getColumnCount(); myIndex++) {
            /* Access the cell and add year to the list */
            final PrometheusSheetCell myCell = myView.getCellByPosition(myIndex, 0);
            addYear(myCell.getString());
        }

        /* Access the static */
        final PrometheusControlDataList myStatic = pData.getControlData();

        /* Add the value into the finance tables (with no security as yet) */
        myStatic.addNewControl(0);

        /* Calculate the number of stages */
        final int myStages = NUM_ARCHIVE_AREAS + getNumYears();

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
    private void loadArchiveStream(final TethysUIThreadStatusReport pReport,
                                   final MoneyWiseDataSet pData,
                                   final InputStream pStream,
                                   final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Protect the workbook retrieval */
        try {
            /* Access current profile */
            OceanusProfile myTask = pReport.getActiveTask();
            myTask = myTask.startTask("LoadArchive");
            myTask.startTask("ParseWorkBook");

            /* Create the Data */
            theParentCache = new MoneyWiseParentCache(this, pData);

            /* Access the workbook from the stream */
            final PrometheusSheetWorkBook myWorkbook = PrometheusSheetProvider.loadFromStream(pType, pStream);
            pReport.checkForCancellation();

            /* Determine Year Range */
            final OceanusProfile myStage = myTask.startTask("LoadSheets");
            myStage.startTask("Range");
            loadArchive(pReport, myWorkbook, pData);

            /* Load Static Tables */
            new MoneyWiseArchiveDepositCategoryType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveCashCategoryType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveLoanCategoryType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchivePortfolioType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveSecurityType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchivePayeeType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveTransCategoryType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveTaxBasis(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveCurrency(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveAccountInfoType(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveTransInfoType(pReport, myWorkbook, pData).loadArchive(myStage);

            /* Load Tags and Regions */
            new MoneyWiseArchiveTransTag(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveRegion(pReport, myWorkbook, pData).loadArchive(myStage);

            /* Load Categories */
            new MoneyWiseArchiveAccountCategory(pReport, myWorkbook, pData).loadArchive(myStage);
            new MoneyWiseArchiveTransCategory(pReport, myWorkbook, pData).loadArchive(myStage, this);

            /* Load ExchangeRates */
            new MoneyWiseArchiveExchangeRate(pReport, myWorkbook, pData).loadArchive(myStage, this);

            /* Load Accounts */
            new MoneyWiseArchiveAccount(pReport, myWorkbook, pData).loadArchive(myStage, this);
            new MoneyWiseArchiveSecurityPrice(pReport, myWorkbook, pData).loadArchive(myStage, this);
            new MoneyWiseArchiveDepositRate(pReport, myWorkbook, pData).loadArchive(myStage);

            /* Load Transactions */
            new MoneyWiseArchiveTransaction(pReport, myWorkbook, pData).loadArchive(myStage, this);

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
     * should we filter this transaction?
     * @param pTrans the transaction
     * @return true/false
     */
    boolean filterTransaction(final PrometheusDataValues pTrans) {
        return enableFiltering
                && (filterAsset(pTrans, MoneyWiseBasicResource.TRANSACTION_ACCOUNT)
                    || filterAsset(pTrans, MoneyWiseBasicResource.TRANSACTION_PARTNER));
    }

    /**
     * Should we filter this asset?
     * @param pTrans the transaction values
     * @param pAsset the asset
     * @return true/false
     */
    private boolean filterAsset(final PrometheusDataValues pTrans,
                                final MetisDataFieldId pAsset) {
        final MoneyWiseTransAsset myAsset = pTrans.getValue(pAsset, MoneyWiseTransAsset.class);
        switch (myAsset.getAssetType()) {
            case DEPOSIT:
            case CASH:
            case PAYEE:
            case LOAN:
                return false;
            default:
                return true;
        }
    }

    /**
     * Declare asset.
     * @param pAsset the asset to declare.
     * @throws OceanusException on error
     */
    protected void declareAsset(final MoneyWiseAssetBase pAsset) throws OceanusException {
        /* Access the asset name */
        final String myName = pAsset.getName();

        /* Check for name already exists */
        if (theNameMap.get(myName) != null) {
            throw new MoneyWiseDataException(pAsset, PrometheusDataItem.ERROR_DUPLICATE);
        }

        /* Store the asset */
        theNameMap.put(myName, pAsset);
    }

    /**
     * Declare category.
     * @param pCategory the category to declare.
     * @throws OceanusException on error
     */
    protected void declareCategory(final MoneyWiseTransCategory pCategory) throws OceanusException {
        /* Access the asset name */
        final String myName = pCategory.getName();

        /* Check for name already exists */
        if (theCategoryMap.get(myName) != null) {
            throw new MoneyWiseDataException(pCategory, PrometheusDataItem.ERROR_DUPLICATE);
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
    protected void declareSecurityHolding(final MoneyWiseSecurity pSecurity,
                                          final String pPortfolio) throws OceanusException {
        /* Access the name */
        final String myName = pSecurity.getName();

        /* Check for name already exists */
        if (theNameMap.get(myName) != null) {
            throw new MoneyWiseDataException(pSecurity, PrometheusDataItem.ERROR_DUPLICATE);
        }

        /* Store the asset */
        theNameMap.put(myName, new MoneyWiseSecurityHoldingDef(pSecurity, pPortfolio));
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
        final Object myHolding = theNameMap.get(pAlias);
        if (!(myHolding instanceof MoneyWiseSecurityHoldingDef myAliased)) {
            throw new MoneyWiseDataException(pAlias, "Aliased security not found");
        }

        /* Store the asset */
        theNameMap.put(pName, new MoneyWiseSecurityHoldingDef(myAliased.getSecurity(), pPortfolio));
    }

    /**
     * Resolve security holdings.
     * @param pData the dataSet
     */
    protected void resolveSecurityHoldings(final MoneyWiseDataSet pData) {
        /* Access securityHoldingsMap and Portfolio list */
        final MoneyWiseSecurityHoldingMap myMap = pData.getPortfolios().getSecurityHoldingsMap();
        final MoneyWisePortfolioList myPortfolios = pData.getPortfolios();

        /* Loop through the name map */
        for (Entry<String, Object> myEntry : theNameMap.entrySet()) {
            /* If this is a security holding definition */
            final Object myValue = myEntry.getValue();
            if (myValue instanceof MoneyWiseSecurityHoldingDef myDef) {
                /* Access security holding */
                final MoneyWisePortfolio myPortfolio = myPortfolios.findItemByName(myDef.getPortfolio());
                final MoneyWiseSecurityHolding myHolding = myMap.declareHolding(myPortfolio, myDef.getSecurity());

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
    protected void resolvePortfolioXfer(final MoneyWiseDataSet pData,
                                        final Object pSource,
                                        final Object pTarget) throws OceanusException {
        /* Target must be portfolio */
        if (!(pTarget instanceof MoneyWisePortfolio myPortfolio)) {
            throw new MoneyWiseDataException(pTarget, "Inconsistent portfolios");
        }

        final MoneyWiseSecurityHoldingMap myMap = pData.getPortfolios().getSecurityHoldingsMap();

        /* Loop through the name map */
        for (Entry<String, Object> myEntry : theNameMap.entrySet()) {
            /* If this is a security holding definition */
            final Object myValue = myEntry.getValue();
            if (myValue instanceof MoneyWiseSecurityHolding myHolding) {
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
    protected static final class MoneyWiseArchiveYear {
        /**
         * The date.
         */
        private final OceanusDate theDate;

        /**
         * The range name.
         */
        private final String theRangeName;

        /**
         * Constructor.
         * @param pName the range name
         */
        private MoneyWiseArchiveYear(final String pName) {
            /* Store parameters */
            theRangeName = pName;

            /* Isolate the year part */
            final int myLen = pName.length();
            int myYear = Integer.parseInt(pName.substring(myLen - 2));

            /* Calculate the actual year */
            if (myYear < YEAR_BDY) {
                myYear += YEAR_2000;
            } else {
                myYear += YEAR_1900;
            }

            /* Create the date */
            final OceanusFiscalYear myFiscal = OceanusFiscalYear.UK;
            theDate = new OceanusDate(myYear, myFiscal.getFirstMonth(), myFiscal.getFirstDay());
            theDate.adjustDay(-1);
        }

        /**
         * Get the date.
         * @return the date
         */
        protected OceanusDate getDate() {
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
    public static final class MoneyWiseSecurityHoldingDef {
        /**
         * Security.
         */
        private final MoneyWiseSecurity theSecurity;

        /**
         * Portfolio.
         */
        private final String thePortfolio;

        /**
         * Constructor.
         * @param pSecurity the security
         * @param pPortfolio the portfolio
         */
        private MoneyWiseSecurityHoldingDef(final MoneyWiseSecurity pSecurity,
                                            final String pPortfolio) {
            /* Store parameters */
            theSecurity = pSecurity;
            thePortfolio = pPortfolio;
        }

        /**
         * Obtain security.
         * @return the security
         */
        public MoneyWiseSecurity getSecurity() {
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
    public class MoneyWiseParentCache {
        /**
         * Loader.
         */
        private final MoneyWiseArchiveLoader theLoader;

        /**
         * DataSet.
         */
        private final MoneyWiseDataSet theData;

        /**
         * TransactionList.
         */
        private final MoneyWiseTransactionList theList;

        /**
         * Last Parent.
         */
        private MoneyWiseTransaction theLastParent;

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
        private MoneyWiseTransaction theParent;

        /**
         * Split Status.
         */
        private boolean isSplit;

        /**
         * Resolved Date.
         */
        private OceanusDate theDate;

        /**
         * AssetPair Id.
         */
        private MoneyWiseAssetDirection theDirection;

        /**
         * Resolved Account.
         */
        private MoneyWiseTransAsset theAccount;

        /**
         * Resolved Partner.
         */
        private MoneyWiseTransAsset thePartner;

        /**
         * Resolved Transaction Category.
         */
        private MoneyWiseTransCategory theCategory;

        /**
         * Resolved Portfolio.
         */
        private MoneyWisePortfolio thePortfolio;

        /**
         * Is the Debit reversed?
         */
        private boolean isDebitReversed;

        /**
         * Constructor.
         * @param pLoader the loader
         * @param pData the dataSet
         */
        protected MoneyWiseParentCache(final MoneyWiseArchiveLoader pLoader,
                                       final MoneyWiseDataSet pData) {
            /* Store lists */
            theLoader = pLoader;
            theData = pData;
            theList = theData.getTransactions();
        }

        /**
         * Build transaction.
         * @param pAmount the amount
         * @param pReconciled is the transaction reconciled?
         * @return the new transaction
         * @throws OceanusException on error
         */
        protected MoneyWiseTransaction buildTransaction(final String pAmount,
                                                        final boolean pReconciled) throws OceanusException {
            /* Build data values */
            final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseTransaction.OBJECT_NAME);
            myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, theDate);
            myValues.addValue(MoneyWiseBasicDataType.TRANSCATEGORY, theCategory);
            myValues.addValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION, theDirection);
            myValues.addValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, theAccount);
            myValues.addValue(MoneyWiseBasicResource.TRANSACTION_PARTNER, thePartner);
            myValues.addValue(MoneyWiseBasicResource.TRANSACTION_RECONCILED, pReconciled);
            if (pAmount != null) {
                myValues.addValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT, pAmount);
            }

            if (theLoader.filterTransaction(myValues)) {
                return null;
            }

            /* Add the value into the list */
            final MoneyWiseTransaction myTrans = theList.addValuesItem(myValues);

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
        protected boolean resolveValues(final OceanusDate pDate,
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

            /* Check resolution */
            checkResolution(pDebit, pCredit, pCategory);

            /* If the category is portfolio transfer */
            if (theCategory.isCategoryClass(MoneyWiseTransCategoryClass.PORTFOLIOXFER)) {
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
            final Object myDebit = pDebit == null
                    ? theLastDebit
                    : theNameMap.get(pDebit);
            final Object myCredit = pCredit == null
                    ? theLastCredit
                    : theNameMap.get(pCredit);

            /* Store last credit and debit */
            theLastDebit = myDebit;
            theLastCredit = myCredit;

            /* Resolve the category */
            theCategory = theCategoryMap.get(pCategory);

            /* Check resolution */
            checkResolution(pDebit, pCredit, pCategory);

            /* Resolve assets */
            resolveAssets();
        }

        /**
         * Resolve assets.
         * @throws OceanusException on error
         */
        private void resolveAssets() throws OceanusException {
            final boolean isDebitHolding = theLastDebit instanceof MoneyWiseSecurityHolding;
            final boolean isCreditHolding = theLastCredit instanceof MoneyWiseSecurityHolding;

            /* Resolve debit and credit */
            final MoneyWiseTransAsset myDebit = (MoneyWiseTransAsset) theLastDebit;
            final MoneyWiseTransAsset myCredit = (MoneyWiseTransAsset) theLastCredit;

            /* Access asset types */
            final MoneyWiseAssetType myDebitType = myDebit.getAssetType();
            final MoneyWiseAssetType myCreditType = myCredit.getAssetType();

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
                        isDebitReversed = !theData.newValidityChecks();
                        break;
                    case LOANINTERESTCHARGED:
                    case WRITEOFF:
                        /* Use credit as account */
                        isDebitReversed = theData.newValidityChecks();
                        break;
                    default:
                        /* Use debit as account */
                        isDebitReversed = false;
                        break;
                }
            } else {
                /* Access parent assets */
                final MoneyWiseTransAsset myParAccount = theParent.getAccount();
                final MoneyWiseTransAsset myParPartner = theParent.getPartner();

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

                    /* Determine if debit is reversed */
                    isDebitReversed = !myDebit.equals(myParPartner);
                }
            }

            /* Set up values */
            if (!isDebitReversed) {
                /* Use debit as account */
                theAccount = myDebit;
                thePartner = myCredit;
                theDirection = MoneyWiseAssetDirection.TO;
            } else {
                /* Use credit as account */
                theAccount = myCredit;
                thePartner = myDebit;
                theDirection = MoneyWiseAssetDirection.FROM;
            }

            /* Resolve portfolio */
            thePortfolio = null;
            if (isDebitHolding) {
                thePortfolio = ((MoneyWiseSecurityHolding) theLastDebit).getPortfolio();
                if (isCreditHolding) {
                    final MoneyWisePortfolio myPortfolio = ((MoneyWiseSecurityHolding) theLastCredit).getPortfolio();
                    if (!thePortfolio.equals(myPortfolio)) {
                        throw new MoneyWiseDataException(theDate, "Inconsistent portfolios");
                    }
                }
            } else if (isCreditHolding) {
                thePortfolio = ((MoneyWiseSecurityHolding) theLastCredit).getPortfolio();
            }
        }

        /**
         * Check resolution.
         * @param pDebit the name of the debit object
         * @param pCredit the name of the credit object
         * @param pCategory the name of the category object
         * @throws OceanusException on error
         */
        private void checkResolution(final String pDebit,
                                     final String pCredit,
                                     final String pCategory) throws OceanusException {
            /* Check debit resolution */
            if (theLastDebit == null) {
                throw new MoneyWiseDataException(pDebit, "Failed to resolve debit account on " + theDate);
            }

            /* Check credit resolution */
            if (theLastCredit == null) {
                throw new MoneyWiseDataException(pCredit, "Failed to resolve credit account on " + theDate);
            }

            /* Check category resolution */
            if (theCategory == null) {
                throw new MoneyWiseDataException(pCategory, "Failed to resolve category on " + theDate);
            }
        }
    }
}
