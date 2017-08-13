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

import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataCell;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataRow;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * SheetDataItem extension for ExchangeRate.
 * @author Tony Washer
 */
public class SheetExchangeRate
        extends PrometheusSheetDataItem<ExchangeRate, MoneyWiseDataType> {
    /**
     * NamedArea for Rates.
     */
    protected static final String AREA_XCHGRATES = ExchangeRate.LIST_NAME;

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
    protected SheetExchangeRate(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_XCHGRATES);

        /* Access the Rates list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getExchangeRates());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetExchangeRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_XCHGRATES);

        /* Access the Rates list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getExchangeRates());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(ExchangeRate.OBJECT_NAME);
        myValues.addValue(ExchangeRate.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(ExchangeRate.FIELD_FROM, loadInteger(COL_FROM));
        myValues.addValue(ExchangeRate.FIELD_TO, loadInteger(COL_TO));
        myValues.addValue(ExchangeRate.FIELD_RATE, loadRatio(COL_RATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final ExchangeRate pItem) throws OceanusException {
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
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final MetisDataWorkBook pWorkBook,
                                      final MoneyWiseData pData,
                                      final ArchiveLoader pLoader) throws OceanusException {
        /* Access the list of rates */
        final ExchangeRateList myList = pData.getExchangeRates();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final MetisDataView myView = pWorkBook.getRangeView(AREA_XCHGRATES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_XCHGRATES);

            /* Count the number of Rates */
            final int myRows = myView.getRowCount();
            final int myCols = myView.getColumnCount();
            final int myTotal = (myRows - 1) * (myCols - 1);

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Obtain the default currency */
            final String myDefCurrency = pData.getDefaultCurrency().getName();

            /* Loop through the rows of the table */
            final MetisDataRow myActRow = myView.getRowByIndex(0);
            for (int i = myRows - 1; i > 0; i--) {
                /* Access the cell by reference */
                final MetisDataRow myRow = myView.getRowByIndex(i);

                /* Access date */
                MetisDataCell myCell = myView.getRowCellByIndex(myRow, 0);
                final TethysDate myDate = myCell.getDateValue();

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
                    final String myCurrency = myCell.getStringValue();

                    /* Handle rate which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, j);
                    if (myCell != null) {
                        /* Access the formatted cell */
                        final String myRate = myCell.getStringValue();

                        /* Build data values */
                        final DataValues<MoneyWiseDataType> myValues = new DataValues<>(ExchangeRate.OBJECT_NAME);
                        myValues.addValue(ExchangeRate.FIELD_FROM, myDefCurrency);
                        myValues.addValue(ExchangeRate.FIELD_TO, myCurrency);
                        myValues.addValue(ExchangeRate.FIELD_DATE, myDate);
                        myValues.addValue(ExchangeRate.FIELD_RATE, myRate);

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
        } catch (MetisThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
