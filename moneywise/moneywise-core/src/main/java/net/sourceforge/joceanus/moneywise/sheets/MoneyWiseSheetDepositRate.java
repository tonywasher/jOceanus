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

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;

/**
 * SheetDataItem extension for AccountRate.
 * @author Tony Washer
 */
public class MoneyWiseSheetDepositRate
        extends PrometheusSheetEncrypted<MoneyWiseDepositRate> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = MoneyWiseDepositRate.LIST_NAME;

    /**
     * Deposit column.
     */
    private static final int COL_DEPOSIT = COL_KEYSETID + 1;

    /**
     * Rate column.
     */
    private static final int COL_RATE = COL_DEPOSIT + 1;

    /**
     * Bonus column.
     */
    private static final int COL_BONUS = COL_RATE + 1;

    /**
     * EndDate column.
     */
    private static final int COL_ENDDATE = COL_BONUS + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetDepositRate(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_RATES);

        /* Access the Rates list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getDepositRates());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetDepositRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_RATES);

        /* Access the Rates list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getDepositRates());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseDepositRate.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicDataType.DEPOSIT, loadInteger(COL_DEPOSIT));
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, loadBytes(COL_RATE));
        myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, loadBytes(COL_BONUS));
        myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE, loadDate(COL_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseDepositRate pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_DEPOSIT, pItem.getDepositId());
        writeBytes(COL_RATE, pItem.getRateBytes());
        writeBytes(COL_BONUS, pItem.getBonusBytes());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_ENDDATE;
    }

    /**
     * Load the DepositRates from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final TethysUIThreadStatusReport pReport,
                                      final PrometheusSheetWorkBook pWorkBook,
                                      final MoneyWiseDataSet pData) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_RATES);

            /* If the view is present */
            if (myView != null) {
                /* Load from it */
                loadArchiveRows(pReport, pData, myView);
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
     * @param pReport the report
     * @param pData the data set to load into
     * @param pView the view to load from
     * @throws OceanusException on error
     */
    protected static void loadArchiveRows(final TethysUIThreadStatusReport pReport,
                                          final MoneyWiseDataSet pData,
                                          final PrometheusSheetView pView) throws OceanusException {
        /* Access the list of rates */
        final MoneyWiseDepositRateList myList = pData.getDepositRates();

        /* Declare the new stage */
        pReport.setNewStage(AREA_RATES);

        /* Count the number of Rates */
        final int myTotal = pView.getRowCount();

        /* Declare the number of steps */
        pReport.setNumSteps(myTotal);

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
            pReport.setNextStep();
        }

        /* PostProcess the rates */
        myList.postProcessOnLoad();
    }
}
