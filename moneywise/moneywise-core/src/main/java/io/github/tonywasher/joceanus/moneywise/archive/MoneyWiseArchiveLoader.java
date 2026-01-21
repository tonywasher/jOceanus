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
package io.github.tonywasher.joceanus.moneywise.archive;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseIOException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusControlData.PrometheusControlDataList;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetProvider;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetView;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to load an archive SpreadSheet.
 */
public final class MoneyWiseArchiveLoader {
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
     * The Data.
     */
    private final MoneyWiseDataSet theData;

    /**
     * The Cache.
     */
    private final MoneyWiseArchiveCache theCache;

    /**
     * Constructor.
     *
     * @param pData the data to load into
     */
    public MoneyWiseArchiveLoader(final MoneyWiseDataSet pData) {
        theData = pData;
        theCache = new MoneyWiseArchiveCache(theData);
    }

    /**
     * Enable filtering.
     */
    public void enableFiltering() {
        theCache.enableFiltering();
    }

    /**
     * Set lastEvent.
     *
     * @param pLastEvent the last event date
     */
    public void setLastEvent(final OceanusDate pLastEvent) {
        theCache.setLastEvent(pLastEvent);
    }

    /**
     * Load an Archive Workbook.
     *
     * @param pReport      the report
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

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to load Workbook: " + myArchive.getName(), e);
        }
    }

    /**
     * Load the Static from an archive.
     *
     * @param pReport   the report
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
            theCache.addYear(myCell.getString());
        }

        /* Access the static */
        final PrometheusControlDataList myStatic = theData.getControlData();

        /* Add the value into the finance tables (with no security as yet) */
        myStatic.addNewControl(0);

        /* Calculate the number of stages */
        final int myStages = NUM_ARCHIVE_AREAS + theCache.getNumYears();

        /* Declare the number of stages */
        pReport.setNumStages(myStages);
    }

    /**
     * Load an Archive Workbook from a stream.
     *
     * @param pReport the report
     * @param pStream Input stream to load from
     * @param pType   the workBookType
     * @throws OceanusException on error
     */
    public void loadArchiveStream(final TethysUIThreadStatusReport pReport,
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
            new MoneyWiseArchiveTransCategory(pReport, myWorkbook, theData, theCache).loadArchive(myStage);

            /* Load ExchangeRates */
            new MoneyWiseArchiveExchangeRate(pReport, myWorkbook, theData, theCache).loadArchive(myStage);

            /* Load Accounts */
            new MoneyWiseArchiveAccount(pReport, myWorkbook, theData, theCache).loadArchive(myStage);
            new MoneyWiseArchiveSecurityPrice(pReport, myWorkbook, theData, theCache).loadArchive(myStage);
            new MoneyWiseArchiveDepositRate(pReport, myWorkbook, theData).loadArchive(myStage);

            /* Load Transactions */
            new MoneyWiseArchiveTransaction(pReport, myWorkbook, theData, theCache).loadArchive(myStage);

            /* If we hit the lastEvent limit */
            if (theCache.hitEventLimit()) {
                /* Note the fact in the data */
                theData.hitEventLimit();
            }

            /* Close the stream */
            pStream.close();

            /* Complete task */
            myTask.end();

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to load Workbook", e);
        }
    }
}
