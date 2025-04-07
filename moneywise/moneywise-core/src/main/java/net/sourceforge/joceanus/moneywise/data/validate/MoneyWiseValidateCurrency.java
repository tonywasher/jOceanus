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
package net.sourceforge.joceanus.moneywise.data.validate;

import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyDataMap;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateStatic;

/**
 * Validator for Currency.
 */
public class MoneyWiseValidateCurrency
        extends PrometheusValidateStatic {

    @Override
    public void validate(final PrometheusDataItem pCurrency) {
        final MoneyWiseCurrency myCurrency = (MoneyWiseCurrency) pCurrency;
        final MoneyWiseCurrencyList myList = myCurrency.getList();
        final MoneyWiseCurrencyDataMap myMap = myList.getDataMap();

        /* Check that reporting is non-null */
        if (myCurrency.isReporting() == null) {
            pCurrency.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticResource.CURRENCY_REPORTING);

            /* else check various things for a reporting currency */
        } else if (Boolean.TRUE.equals(myCurrency.isReporting())) {
            /* Check that default is enabled */
            if (!myCurrency.getEnabled()) {
                pCurrency.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticResource.CURRENCY_REPORTING);
            }

            /* Check for multiple reports */
            if (!myMap.validReportCount()) {
                pCurrency.addError("Multiple reporting currencies", MoneyWiseStaticResource.CURRENCY_REPORTING);
            }
        }

        /* Validate it */
        super.validate(pCurrency);
    }
}
