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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateList;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * Archive Loader for ExchangeRate.
 * @author Tony Washer
 */
public final class MoneyWiseArchiveExchangeRate {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_XCHGRATES = MoneyWiseExchangeRate.LIST_NAME;

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
     * Store.
     */
    private final MoneyWiseArchiveStore theStore;

    /**
     * Constructor.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pStore the archive store
     */
    MoneyWiseArchiveExchangeRate(final TethysUIThreadStatusReport pReport,
                                 final PrometheusSheetWorkBook pWorkBook,
                                 final MoneyWiseDataSet pData,
                                 final MoneyWiseArchiveStore pStore) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
        theStore = pStore;
    }

    /**
     * Load the ExchangeRates from an archive.
     * @param pStage the stage
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage) throws OceanusException {
        /* Access the list of rates */
        pStage.startTask(AREA_XCHGRATES);
        final MoneyWiseExchangeRateList myList = theData.getExchangeRates();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = theWorkBook.getRangeView(AREA_XCHGRATES);

            /* Declare the new stage */
            theReport.setNewStage(AREA_XCHGRATES);

            /* Count the number of Rates */
            final int myRows = myView.getRowCount();
            final int myCols = myView.getColumnCount();
            final int myTotal = (myRows - 1) * (myCols - 1);

            /* Declare the number of steps */
            theReport.setNumSteps(myTotal);

            /* Obtain the default currency */
            final String myDefCurrency = theData.getReportingCurrency().getName();

            /* Loop through the rows of the table */
            final PrometheusSheetRow myActRow = myView.getRowByIndex(0);
            for (int i = myRows - 1; i > 0; i--) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Access date */
                PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, 0);
                final OceanusDate myDate = myCell.getDate();

                /* If the rate is too late */
                if (!theStore.checkDate(myDate)) {
                    /* Skip the row */
                    continue;
                }

                /* Loop through the columns of the table */
                for (int j = 1; j < myCols; j++) {
                    /* Access account */
                    myCell = myView.getRowCellByIndex(myActRow, j);
                    if (myCell == null) {
                        continue;
                    }
                    final String myCurrency = myCell.getString();

                    /* Handle rate which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, j);
                    if (myCell != null) {
                        /* Access the formatted cell */
                        final String myRate = myCell.getString();

                        /* Build data values */
                        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseExchangeRate.OBJECT_NAME);
                        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_FROM, myDefCurrency);
                        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_TO, myCurrency);
                        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, myDate);
                        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_RATE, myRate);

                        /* Add the value into the list */
                        myList.addValuesItem(myValues);
                    }

                    /* Report the progress */
                    theReport.setNextStep();
                }
            }

            /* Post process the prices */
            myList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
