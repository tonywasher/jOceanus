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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetEncrypted;

/**
 * SheetDataItem extension for CashCategory.
 *
 * @author Tony Washer
 */
public final class MoneyWiseSheetCashCategory
        extends PrometheusSheetEncrypted<MoneyWiseCashCategory> {
    /**
     * NamedArea for Categories.
     */
    static final String AREA_CASHCATEGORIES = MoneyWiseCashCategory.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

    /**
     * Constructor for loading a spreadsheet.
     *
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetCashCategory(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_CASHCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getCashCategories());
    }

    /**
     * Constructor for creating a spreadsheet.
     *
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetCashCategory(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_CASHCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getCashCategories());
    }

    @Override
    public PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseCashCategory.OBJECT_NAME);
        myValues.addValue(MoneyWiseStaticDataType.CASHTYPE, loadInteger(COL_TYPE));
        myValues.addValue(PrometheusDataResource.DATAGROUP_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    public void insertSecureItem(final MoneyWiseCashCategory pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getCategoryTypeId());
        writeInteger(COL_PARENT, pItem.getParentCategoryId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    public int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }
}
