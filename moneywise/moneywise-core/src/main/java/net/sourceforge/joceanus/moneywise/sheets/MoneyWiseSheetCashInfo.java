/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetDataInfo;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * SheetDataInfo extension for CashInfo.
 * @author Tony Washer
 */
public class MoneyWiseSheetCashInfo
        extends PrometheusSheetDataInfo<MoneyWiseCashInfo> {
    /**
     * NamedArea for CashInfo.
     */
    private static final String AREA_CASHINFO = MoneyWiseCashInfo.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetCashInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_CASHINFO);

        /* Access the InfoType list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getCashInfo());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetCashInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_CASHINFO);

        /* Access the InfoType list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getCashInfo());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseCashInfo.OBJECT_NAME);
    }
}
