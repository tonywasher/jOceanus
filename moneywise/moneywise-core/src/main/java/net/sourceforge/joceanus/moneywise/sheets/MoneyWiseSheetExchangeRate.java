/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetDataItem;

/**
 * SheetDataItem extension for ExchangeRate.
 * @author Tony Washer
 */
public final class MoneyWiseSheetExchangeRate
        extends PrometheusSheetDataItem<MoneyWiseExchangeRate> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_XCHGRATES = MoneyWiseExchangeRate.LIST_NAME;

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
    MoneyWiseSheetExchangeRate(final MoneyWiseReader pReader) {
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
    MoneyWiseSheetExchangeRate(final MoneyWiseWriter pWriter) {
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
}
