/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateList;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetDataItem;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadCancelException;

/**
 * SheetDataItem extension for ExchangeRate.
 * @author Tony Washer
 */
public class MoneyWiseSheetExchangeRate
        extends PrometheusSheetDataItem<MoneyWiseExchangeRate> {
    /**
     * NamedArea for Rates.
     */
    protected static final String AREA_XCHGRATES = MoneyWiseExchangeRate.LIST_NAME;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_ID + 1;

    /**
     * From column.
     */
    private static final int COL_FROM = COL_DATE + 1;

    /**
     * To column.
     */
    private static final int COL_TO = COL_FROM + 1;

    /**
     * Rate column.
     */
    private static final int COL_RATE = COL_TO + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetExchangeRate(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_XCHGRATES);

        /* Access the Rates list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getExchangeRates());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetExchangeRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_XCHGRATES);

        /* Access the Rates list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getExchangeRates());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseExchangeRate.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_FROM, loadInteger(COL_FROM));
        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_TO, loadInteger(COL_TO));
        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_RATE, loadRatio(COL_RATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseExchangeRate pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_FROM, pItem.getFromCurrencyId());
        writeInteger(COL_TO, pItem.getToCurrencyId());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_RATE, pItem.getExchangeRate());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_RATE;
    }

    /**
     * Load the ExchangeRates from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pLoader the archive loader
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final TethysUIThreadStatusReport pReport,
                                      final PrometheusSheetWorkBook pWorkBook,
                                      final MoneyWiseDataSet pData,
                                      final MoneyWiseArchiveLoader pLoader) throws OceanusException {
        /* Access the list of rates */
        final MoneyWiseExchangeRateList myList = pData.getExchangeRates();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_XCHGRATES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_XCHGRATES);

            /* Count the number of Rates */
            final int myRows = myView.getRowCount();
            final int myCols = myView.getColumnCount();
            final int myTotal = (myRows - 1) * (myCols - 1);

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Obtain the default currency */
            final String myDefCurrency = pData.getReportingCurrency().getName();

            /* Loop through the rows of the table */
            final PrometheusSheetRow myActRow = myView.getRowByIndex(0);
            for (int i = myRows - 1; i > 0; i--) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Access date */
                PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, 0);
                final TethysDate myDate = myCell.getDate();

                /* If the rate is too late */
                if (!pLoader.checkDate(myDate)) {
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
                    pReport.setNextStep();
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
