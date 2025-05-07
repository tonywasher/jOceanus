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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
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
 * ArchiveLoader for SecurityPrice.
 * @author Tony Washer
 */
public final class MoneyWiseArchiveSecurityPrice {
    /**
     * NamedArea for Prices.
     */
    private static final String AREA_PRICES = MoneyWiseSecurityPrice.LIST_NAME;

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
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     */
    MoneyWiseArchiveSecurityPrice(final TethysUIThreadStatusReport pReport,
                                  final PrometheusSheetWorkBook pWorkBook,
                                  final MoneyWiseDataSet pData) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
    }

    /**
     * Load the SecurityPrices from an archive.
     * @param pStage the stage
     * @param pLoader the archive loader
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage,
                     final MoneyWiseArchiveLoader pLoader) throws OceanusException {
        /* Access the list of prices */
        pStage.startTask(AREA_PRICES);
        final MoneyWiseSecurityPriceList myList = theData.getSecurityPrices();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = theWorkBook.getRangeView(AREA_PRICES);

            /* Declare the new stage */
            theReport.setNewStage(AREA_PRICES);

            /* Count the number of Prices */
            final int myRows = myView.getRowCount();
            final int myCols = myView.getColumnCount();
            final int myTotal = myRows - 1;
            final String[] mySecurities = new String[myCols];

            /* Declare the number of steps */
            theReport.setNumSteps(myTotal);

            /* Load the securities */
            final PrometheusSheetRow myActRow = myView.getRowByIndex(0);
            for (int j = 1; j < myCols; j++) {
                /* Access account */
                final PrometheusSheetCell myAct = myView.getRowCellByIndex(myActRow, j);
                if (myAct != null) {
                    mySecurities[j] = myAct.getString();
                }
            }

            /* Loop through the rows of the table */
            for (int i = myRows - 1; i > 0; i--) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Access date */
                PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, 0);
                final OceanusDate myDate = myCell.getDate();

                /* If the price is too late */
                if (!pLoader.checkDate(myDate)) {
                    /* Skip the row */
                    continue;
                }

                /* Loop through the columns of the table */
                final int myLast = myRow.getMaxValuedCellIndex();
                for (int j = 1; j <= myLast; j++) {
                    /* Handle price which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, j);
                    if (myCell != null) {
                        /* Build data values */
                        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseSecurityPrice.OBJECT_NAME);
                        myValues.addValue(MoneyWiseBasicDataType.SECURITY, mySecurities[j]);
                        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, myDate);
                        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, myCell.getString());

                        /* Add the value into the list */
                        myList.addValuesItem(myValues);
                    }
                }

                /* Report the progress */
                theReport.setNextStep();
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
