/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayeeInfo;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetDataInfo;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataInfo extension for PayeeInfo.
 * @author Tony Washer
 */
public class MoneyWiseSheetPayeeInfo
        extends PrometheusSheetDataInfo<MoneyWisePayeeInfo> {
    /**
     * NamedArea for PayeeInfo.
     */
    private static final String AREA_PAYEEINFO = MoneyWisePayeeInfo.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetPayeeInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_PAYEEINFO);

        /* Access the InfoType list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getPayeeInfo());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetPayeeInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_PAYEEINFO);

        /* Access the InfoType list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getPayeeInfo());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWisePayeeInfo.OBJECT_NAME);
    }
}
