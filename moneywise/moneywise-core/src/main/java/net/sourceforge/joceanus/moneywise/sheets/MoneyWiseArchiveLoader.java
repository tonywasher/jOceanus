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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusControlData.PrometheusControlDataList;
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
import java.util.List;
import java.util.ListIterator;

/**
 * Class to load an archive SpreadSheet.
 */
public class MoneyWiseArchiveLoader
        implements MoneyWiseArchiveStore {
    /**
     * Number of base archive load areas.
     * 11xStatic,TransactionTags,Regions,2*Category,XchgRate,Rate,Price,Account.
     */
    private static final int NUM_ARCHIVE_AREAS = 19;

    /**
     * NamedRange for Static.
     */
    private static final String AREA_YEARRANGE = "AssetsYears";

    /**
     * The list of years.
     */
    private final List<MoneyWiseArchiveYear> theYears;

    /**
     * The Data.
     */
    private MoneyWiseDataSet theData;

    /**
     * The ParentCache.
     */
    private MoneyWiseArchiveCache theParentCache;

    /**
     * Are we filtering?.
     */
    private boolean enableFiltering;

    /**
     * Constructor.
     * @param pData the data to load into
     */
    public MoneyWiseArchiveLoader(final MoneyWiseDataSet pData) {
        theData = pData;
        theParentCache = new MoneyWiseArchiveCache(this, theData);
        theYears = new ArrayList<>();
    }

    /**
     * Get the iterator.
     * @return the iterator
     */
    protected ListIterator<MoneyWiseArchiveYear> getIterator() {
        return theYears.listIterator();
    }

    @Override
    public ListIterator<MoneyWiseArchiveYear> reverseIterator() {
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
    protected MoneyWiseArchiveCache getParentCache() {
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

    @Override
    public boolean checkDate(final OceanusDate pDate) {
        return theParentCache.checkDate(pDate);
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
        theParentCache.setLastEvent(pLastEvent);
    }

    /**
     * Load an Archive Workbook.
     * @param pReport the report
     * @param pPreferences the backup preferences
     * @throws OceanusException on error
     */
    public void loadArchive(final TethysUIThreadStatusReport pReport,
                            final PrometheusBackupPreferences pPreferences) throws OceanusException {
        /* Determine the archive name */
        final String myName = pPreferences.getStringValue(PrometheusBackupPreferenceKey.ARCHIVE);
        final File myArchive = new File(myName);

        /* Protect the workbook retrieval */
        try (FileInputStream myInFile = new FileInputStream(myArchive);
             InputStream myStream = new BufferedInputStream(myInFile)) {
            /* Determine the WorkBookType */
            final PrometheusSheetWorkBookType myType = PrometheusSheetWorkBookType.determineType(myName);

            /* Load the data from the stream */
            loadArchiveStream(pReport, myStream, myType);

            /* If we hit the lastEvent limit */
            if (theParentCache.hitEventLimit()) {
                /* Note the fact in the data */
                theData.hitEventLimit();
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
     * @throws OceanusException on error
     */
    private void loadArchive(final TethysUIThreadStatusReport pReport,
                             final PrometheusSheetWorkBook pWorkBook) throws OceanusException {
        /* Find the range of cells */
        final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_YEARRANGE);

        /* Loop through the cells */
        for (int myIndex = 0; myIndex < myView.getColumnCount(); myIndex++) {
            /* Access the cell and add year to the list */
            final PrometheusSheetCell myCell = myView.getCellByPosition(myIndex, 0);
            addYear(myCell.getString());
        }

        /* Access the static */
        final PrometheusControlDataList myStatic = theData.getControlData();

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
     * @param pStream Input stream to load from
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    private void loadArchiveStream(final TethysUIThreadStatusReport pReport,
                                   final InputStream pStream,
                                   final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Protect the workbook retrieval */
        try {
            /* Access current profile */
            OceanusProfile myTask = pReport.getActiveTask();
            myTask = myTask.startTask("LoadArchive");
            myTask.startTask("ParseWorkBook");

            /* Access the workbook from the stream */
            final PrometheusSheetWorkBook myWorkbook = PrometheusSheetProvider.loadFromStream(pType, pStream);
            pReport.checkForCancellation();

            /* Determine Year Range */
            final OceanusProfile myStage = myTask.startTask("LoadSheets");
            myStage.startTask("Range");
            loadArchive(pReport, myWorkbook);

            /* Load Static Tables */
            new MoneyWiseArchiveDepositCategoryType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveCashCategoryType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveLoanCategoryType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchivePortfolioType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveSecurityType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchivePayeeType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveTransCategoryType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveTaxBasis(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveCurrency(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveAccountInfoType(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveTransInfoType(pReport, myWorkbook, theData).loadArchive(myStage);

            /* Load Tags and Regions */
            new MoneyWiseArchiveTransTag(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveRegion(pReport, myWorkbook, theData).loadArchive(myStage);

            /* Load Categories */
            new MoneyWiseArchiveAccountCategory(pReport, myWorkbook, theData).loadArchive(myStage);
            new MoneyWiseArchiveTransCategory(pReport, myWorkbook, theData, this).loadArchive(myStage);

            /* Load ExchangeRates */
            new MoneyWiseArchiveExchangeRate(pReport, myWorkbook, theData, this).loadArchive(myStage);

            /* Load Accounts */
            new MoneyWiseArchiveAccount(pReport, myWorkbook, theData, this).loadArchive(myStage);
            new MoneyWiseArchiveSecurityPrice(pReport, myWorkbook, theData, this).loadArchive(myStage);
            new MoneyWiseArchiveDepositRate(pReport, myWorkbook, theData).loadArchive(myStage);

            /* Load Transactions */
            new MoneyWiseArchiveTransaction(pReport, myWorkbook, theData, this).loadArchive(myStage);

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

    @Override
    public void declareAsset(final MoneyWiseAssetBase pAsset) throws OceanusException {
        theParentCache.declareAsset(pAsset);
    }

    @Override
    public void declareCategory(final MoneyWiseTransCategory pCategory) throws OceanusException {
        theParentCache.declareCategory(pCategory);
    }

    @Override
    public void declareSecurityHolding(final MoneyWiseSecurity pSecurity,
                                       final String pPortfolio) throws OceanusException {
        theParentCache.declareSecurityHolding(pSecurity, pPortfolio);
    }

    @Override
    public void declareAliasHolding(final String pName,
                                    final String pAlias,
                                    final String pPortfolio) throws OceanusException {
        theParentCache.declareAliasHolding(pName, pAlias, pPortfolio);
        }

    @Override
    public void resolveSecurityHoldings(final MoneyWiseDataSet pData) {
        theParentCache.resolveSecurityHoldings(pData);
    }
}
