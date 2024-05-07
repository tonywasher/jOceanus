/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * SheetDataItem extension for SecurityPrice.
 * @author Tony Washer
 */
public class MoneyWiseSheetSecurityPrice
        extends PrometheusSheetEncrypted<MoneyWiseSecurityPrice> {
    /**
     * NamedArea for Prices.
     */
    private static final String AREA_PRICES = MoneyWiseSecurityPrice.LIST_NAME;

    /**
     * Security column.
     */
    private static final int COL_SECURITY = COL_KEYSETID + 1;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_SECURITY + 1;

    /**
     * Price column.
     */
    private static final int COL_PRICE = COL_DATE + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetSecurityPrice(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PRICES);

        /* Access the Prices list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getSecurityPrices());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetSecurityPrice(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PRICES);

        /* Access the Prices list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getSecurityPrices());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseSecurityPrice.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicDataType.SECURITY, loadInteger(COL_SECURITY));
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, loadBytes(COL_PRICE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseSecurityPrice pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_SECURITY, pItem.getSecurityId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_PRICE, pItem.getPriceBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_PRICE;
    }

    /**
     * Load the Prices from an archive.
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
        /* Access the list of prices */
        final MoneyWiseSecurityPriceList myList = pData.getSecurityPrices();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_PRICES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_PRICES);

            /* Count the number of Prices */
            final int myRows = myView.getRowCount();
            final int myCols = myView.getColumnCount();
            final int myTotal = myRows - 1;
            final String[] mySecurities = new String[myCols];

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

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
                final TethysDate myDate = myCell.getDate();

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
                pReport.setNextStep();
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
