/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.sheets;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.sheets.PrometheusSheetEncrypted;

/**
 * SheetDataItem extension for SecurityPrice.
 *
 * @author Tony Washer
 */
public final class MoneyWiseSheetSecurityPrice
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
     *
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetSecurityPrice(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PRICES);

        /* Access the Prices list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getSecurityPrices());
    }

    /**
     * Constructor for creating a spreadsheet.
     *
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetSecurityPrice(final MoneyWiseWriter pWriter) {
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
}
