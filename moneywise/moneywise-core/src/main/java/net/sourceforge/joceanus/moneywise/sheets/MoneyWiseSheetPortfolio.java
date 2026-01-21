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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.sheets.PrometheusSheetEncrypted;

/**
 * SheetDataItem extension for Portfolio.
 *
 * @author Tony Washer
 */
public final class MoneyWiseSheetPortfolio
        extends PrometheusSheetEncrypted<MoneyWisePortfolio> {
    /**
     * NamedArea for Portfolios.
     */
    private static final String AREA_PORTFOLIOS = MoneyWisePortfolio.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_NAME + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_DESC + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_PARENT + 1;

    /**
     * TaxFree column.
     */
    private static final int COL_TAXFREE = COL_CURRENCY + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_TAXFREE + 1;

    /**
     * Constructor for loading a spreadsheet.
     *
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetPortfolio(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PORTFOLIOS);

        /* Access the Portfolios list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getPortfolios());
    }

    /**
     * Constructor for creating a spreadsheet.
     *
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetPortfolio(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PORTFOLIOS);

        /* Access the Portfolios list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getPortfolios());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWisePortfolio.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, loadInteger(COL_TYPE));
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWisePortfolio pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getCategoryId());
        writeInteger(COL_PARENT, pItem.getParentId());
        writeInteger(COL_CURRENCY, pItem.getAssetCurrencyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }
}
