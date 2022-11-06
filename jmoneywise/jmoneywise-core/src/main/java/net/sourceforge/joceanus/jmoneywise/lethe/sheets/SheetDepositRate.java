/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadCancelException;

/**
 * SheetDataItem extension for AccountRate.
 * @author Tony Washer
 */
public class SheetDepositRate
        extends PrometheusSheetEncrypted<DepositRate> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = DepositRate.LIST_NAME;

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
    protected SheetDepositRate(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_RATES);

        /* Access the Rates list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getDepositRates());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDepositRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_RATES);

        /* Access the Rates list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getDepositRates());
    }

    @Override
    protected DataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues myValues = getRowValues(DepositRate.OBJECT_NAME);
        myValues.addValue(DepositRate.FIELD_DEPOSIT, loadInteger(COL_DEPOSIT));
        myValues.addValue(DepositRate.FIELD_RATE, loadBytes(COL_RATE));
        myValues.addValue(DepositRate.FIELD_BONUS, loadBytes(COL_BONUS));
        myValues.addValue(DepositRate.FIELD_ENDDATE, loadDate(COL_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final DepositRate pItem) throws OceanusException {
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
                                      final MoneyWiseData pData) throws OceanusException {
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
            throw new MoneyWiseIOException("Failed to Load " + MoneyWiseDataType.DEPOSITRATE.getListName(), e);
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
                                          final MoneyWiseData pData,
                                          final PrometheusSheetView pView) throws OceanusException {
        /* Access the list of rates */
        final DepositRateList myList = pData.getDepositRates();

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
            TethysDate myExpiry = null;
            if (myCell != null) {
                myExpiry = myCell.getDate();
            }

            /* Build data values */
            final DataValues myValues = new DataValues(DepositRate.OBJECT_NAME);
            myValues.addValue(DepositRate.FIELD_DEPOSIT, myDeposit);
            myValues.addValue(DepositRate.FIELD_RATE, myRate);
            myValues.addValue(DepositRate.FIELD_BONUS, myBonus);
            myValues.addValue(DepositRate.FIELD_ENDDATE, myExpiry);

            /* Add the value into the list */
            myList.addValuesItem(myValues);

            /* Report the progress */
            pReport.setNextStep();
        }

        /* PostProcess the rates */
        myList.postProcessOnLoad();
    }
}
