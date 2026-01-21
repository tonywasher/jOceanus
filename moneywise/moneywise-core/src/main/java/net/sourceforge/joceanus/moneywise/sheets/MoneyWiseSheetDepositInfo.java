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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfo;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetDataInfo;

/**
 * SheetDataInfo extension for DepositInfo.
 *
 * @author Tony Washer
 */
public final class MoneyWiseSheetDepositInfo
        extends PrometheusSheetDataInfo<MoneyWiseDepositInfo> {
    /**
     * NamedArea for DepositInfo.
     */
    private static final String AREA_DEPOSITINFO = MoneyWiseDepositInfo.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     *
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetDepositInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_DEPOSITINFO);

        /* Access the InfoType list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getDepositInfo());
    }

    /**
     * Constructor for creating a spreadsheet.
     *
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetDepositInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_DEPOSITINFO);

        /* Access the InfoType list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getDepositInfo());
    }

    @Override
    public PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseDepositInfo.OBJECT_NAME);
    }
}
