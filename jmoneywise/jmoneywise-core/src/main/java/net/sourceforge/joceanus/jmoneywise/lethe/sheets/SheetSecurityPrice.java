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

import net.sourceforge.joceanus.jmetis.atlas.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.atlas.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataCell;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataRow;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * SheetDataItem extension for SecurityPrice.
 * @author Tony Washer
 */
public class SheetSecurityPrice
        extends PrometheusSheetEncrypted<SecurityPrice, MoneyWiseDataType> {
    /**
     * NamedArea for Prices.
     */
    private static final String AREA_PRICES = SecurityPrice.LIST_NAME;

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
    protected SheetSecurityPrice(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PRICES);

        /* Access the Prices list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getSecurityPrices());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetSecurityPrice(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PRICES);

        /* Access the Prices list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getSecurityPrices());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(SecurityPrice.OBJECT_NAME);
        myValues.addValue(SecurityPrice.FIELD_SECURITY, loadInteger(COL_SECURITY));
        myValues.addValue(SecurityPrice.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(SecurityPrice.FIELD_PRICE, loadBytes(COL_PRICE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final SecurityPrice pItem) throws OceanusException {
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
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final MetisDataWorkBook pWorkBook,
                                      final MoneyWiseData pData,
                                      final ArchiveLoader pLoader) throws OceanusException {
        /* Access the list of prices */
        final SecurityPriceList myList = pData.getSecurityPrices();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final MetisDataView myView = pWorkBook.getRangeView(AREA_PRICES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_PRICES);

            /* Count the number of Prices */
            final int myRows = myView.getRowCount();
            final int myCols = myView.getColumnCount();
            final int myTotal = (myRows - 1) * (myCols - 1);

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Loop through the rows of the table */
            final MetisDataRow myActRow = myView.getRowByIndex(0);
            for (int i = myRows - 1; i > 0; i--) {
                /* Access the cell by reference */
                final MetisDataRow myRow = myView.getRowByIndex(i);

                /* Access date */
                MetisDataCell myCell = myView.getRowCellByIndex(myRow, 0);
                final TethysDate myDate = myCell.getDateValue();

                /* If the price is too late */
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
                    final String mySecurity = myCell.getStringValue();

                    /* Handle price which may be missing */
                    myCell = myView.getRowCellByIndex(myRow, j);
                    if (myCell != null) {
                        /* Access the formatted cell */
                        final String myPrice = myCell.getStringValue();

                        /* Build data values */
                        final DataValues<MoneyWiseDataType> myValues = new DataValues<>(SecurityPrice.OBJECT_NAME);
                        myValues.addValue(SecurityPrice.FIELD_SECURITY, mySecurity);
                        myValues.addValue(SecurityPrice.FIELD_DATE, myDate);
                        myValues.addValue(SecurityPrice.FIELD_PRICE, myPrice);

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
