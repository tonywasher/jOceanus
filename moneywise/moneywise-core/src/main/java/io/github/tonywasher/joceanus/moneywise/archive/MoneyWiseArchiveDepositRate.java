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
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDepositRate;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateList;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseIOException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetView;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadCancelException;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * ArchiveLoader for DepositRate.
 *
 * @author Tony Washer
 */
public final class MoneyWiseArchiveDepositRate {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = MoneyWiseDepositRate.LIST_NAME;

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
     * Constructor.
     *
     * @param pReport   the report
     * @param pWorkBook the workbook
     * @param pData     the data set to load into
     */
    MoneyWiseArchiveDepositRate(final TethysUIThreadStatusReport pReport,
                                final PrometheusSheetWorkBook pWorkBook,
                                final MoneyWiseDataSet pData) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
    }

    /**
     * Load the Payee Types from an archive.
     *
     * @param pStage the stage
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            pStage.startTask(AREA_RATES);
            final PrometheusSheetView myView = theWorkBook.getRangeView(AREA_RATES);

            /* If the view is present */
            if (myView != null) {
                /* Load from it */
                loadArchiveRows(myView);
            }

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + MoneyWiseBasicDataType.DEPOSITRATE.getListName(), e);
        }
    }

    /**
     * Load the DepositRates from an archive.
     *
     * @param pView the view to load from
     * @throws OceanusException on error
     */
    private void loadArchiveRows(final PrometheusSheetView pView) throws OceanusException {
        /* Access the list of rates */
        final MoneyWiseDepositRateList myList = theData.getDepositRates();

        /* Declare the new stage */
        theReport.setNewStage(AREA_RATES);

        /* Count the number of Rates */
        final int myTotal = pView.getRowCount();

        /* Declare the number of steps */
        theReport.setNumSteps(myTotal);

        /* Loop through the rows of the table */
        for (int i = 0; i < myTotal; i++) {
            /* Access the cell by reference */
            final PrometheusSheetRow myRow = pView.getRowByIndex(i);
            int iAdjust = -1;

            /* Access deposit */
            PrometheusSheetCell myCell = pView.getRowCellByIndex(myRow, ++iAdjust);
            final String myDeposit = myCell.getString();

            /* Handle Rate */
            myCell = pView.getRowCellByIndex(myRow, ++iAdjust);
            final String myRate = myCell.getString();

            /* Handle bonus which may be missing */
            myCell = pView.getRowCellByIndex(myRow, ++iAdjust);
            String myBonus = null;
            if (myCell != null) {
                myBonus = myCell.getString();
            }

            /* Handle expiration which may be missing */
            myCell = pView.getRowCellByIndex(myRow, ++iAdjust);
            OceanusDate myExpiry = null;
            if (myCell != null) {
                myExpiry = myCell.getDate();
            }

            /* Build data values */
            final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseDepositRate.OBJECT_NAME);
            myValues.addValue(MoneyWiseBasicDataType.DEPOSIT, myDeposit);
            myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, myRate);
            myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, myBonus);
            myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE, myExpiry);

            /* Add the value into the list */
            myList.addValuesItem(myValues);

            /* Report the progress */
            theReport.setNextStep();
        }

        /* PostProcess the rates */
        myList.postProcessOnLoad();
    }
}
