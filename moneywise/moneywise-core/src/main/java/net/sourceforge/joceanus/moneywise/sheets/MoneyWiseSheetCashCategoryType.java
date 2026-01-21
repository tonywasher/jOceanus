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
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.sheets.PrometheusSheetStaticData;

/**
 * SheetStaticData extension for CashCategoryType.
 *
 * @author Tony Washer
 */
public final class MoneyWiseSheetCashCategoryType
        extends PrometheusSheetStaticData<MoneyWiseCashCategoryType> {
    /**
     * NamedArea for CashCategoryTypes.
     */
    private static final String AREA_CASHCATTYPES = MoneyWiseCashCategoryType.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     *
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetCashCategoryType(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_CASHCATTYPES);

        /* Access the Cash Type list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getCashCategoryTypes());
    }

    /**
     * Constructor for creating a spreadsheet.
     *
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetCashCategoryType(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_CASHCATTYPES);

        /* Access the Cash Type list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getCashCategoryTypes());
    }

    @Override
    public PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseCashCategoryType.OBJECT_NAME);
    }
}
