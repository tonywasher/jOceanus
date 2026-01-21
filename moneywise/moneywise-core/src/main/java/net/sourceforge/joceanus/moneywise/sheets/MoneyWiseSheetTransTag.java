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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetEncrypted;

/**
 * SheetDataItem extension for TransactionTag.
 *
 * @author Tony Washer
 */
public final class MoneyWiseSheetTransTag
        extends PrometheusSheetEncrypted<MoneyWiseTransTag> {
    /**
     * NamedArea for TransactionTags.
     */
    private static final String AREA_TRANSTAGS = MoneyWiseTransTag.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_NAME + 1;

    /**
     * Constructor for loading a spreadsheet.
     *
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetTransTag(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TRANSTAGS);

        /* Access the Class list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getTransactionTags());
    }

    /**
     * Constructor for creating a spreadsheet.
     *
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetTransTag(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TRANSTAGS);

        /* Access the Class list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getTransactionTags());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseTransTag.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseTransTag pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }
}
