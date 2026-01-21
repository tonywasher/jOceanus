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
package io.github.tonywasher.joceanus.moneywise.sheets;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.sheets.PrometheusSheetStaticData;

/**
 * SheetStaticData extension for SecurityType.
 *
 * @author Tony Washer
 */
public final class MoneyWiseSheetSecurityType
        extends PrometheusSheetStaticData<MoneyWiseSecurityType> {
    /**
     * NamedArea for SecurityTypes.
     */
    private static final String AREA_SECURITYTYPES = MoneyWiseSecurityType.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     *
     * @param pReader the spreadsheet reader
     */
    MoneyWiseSheetSecurityType(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_SECURITYTYPES);

        /* Access the Security Type list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getSecurityTypes());
    }

    /**
     * Constructor for creating a spreadsheet.
     *
     * @param pWriter the spreadsheet writer
     */
    MoneyWiseSheetSecurityType(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_SECURITYTYPES);

        /* Access the Security Type list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getSecurityTypes());
    }

    @Override
    public PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseSecurityType.OBJECT_NAME);
    }
}
