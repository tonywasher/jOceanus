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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetStaticData;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadCancelException;

/**
 * SheetStaticData extension for AccountCurrency.
 * @author Tony Washer
 */
public class SheetAssetCurrency
        extends PrometheusSheetStaticData<AssetCurrency> {
    /**
     * NamedArea for AccountCurrencies.
     */
    private static final String AREA_ACCOUNTCURRENCIES = AssetCurrency.LIST_NAME;

    /**
     * Default column.
     */
    private static final int COL_DEFAULT = COL_DESC + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAssetCurrency(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_ACCOUNTCURRENCIES);

        /* Access the list */
        final MoneyWiseData myData = (MoneyWiseData) pReader.getData();
        setDataList(myData.getAccountCurrencies());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAssetCurrency(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_ACCOUNTCURRENCIES);

        /* Access the list */
        final MoneyWiseData myData = (MoneyWiseData) pWriter.getData();
        setDataList(myData.getAccountCurrencies());
    }

    @Override
    protected DataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues myValues = getRowValues(AssetCurrency.OBJECT_NAME);
        myValues.addValue(AssetCurrency.FIELD_DEFAULT, loadBoolean(COL_DEFAULT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final AssetCurrency pItem) throws OceanusException {
        /* Insert standard fields */
        super.insertSecureItem(pItem);

        /* Set default indication */
        writeBoolean(COL_DEFAULT, pItem.isDefault());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DEFAULT;
    }

    /**
     * Load the Account Currencies from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final TethysUIThreadStatusReport pReport,
                                      final PrometheusSheetWorkBook pWorkBook,
                                      final MoneyWiseData pData) throws OceanusException {
        /* Access the list of account currencies */
        final AssetCurrencyList myList = pData.getAccountCurrencies();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_ACCOUNTCURRENCIES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_ACCOUNTCURRENCIES);

            /* Count the number of AssetCurrencies */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Loop through the rows of the single column range */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);
                final PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, 0);

                /* Add the value into the tables */
                myList.addBasicItem(myCell.getString());

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Initialise the default currency */
            myList.initialiseDefault();

            /* PostProcess the list */
            myList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
