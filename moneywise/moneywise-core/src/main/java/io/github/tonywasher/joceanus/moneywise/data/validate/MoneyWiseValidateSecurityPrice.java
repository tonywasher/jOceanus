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
package io.github.tonywasher.joceanus.moneywise.data.validate;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceBaseList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusData.PrometheusDataItemCtl;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusData.PrometheusDataValidator;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;

/**
 * Validator for SecurityPrice.
 */
public class MoneyWiseValidateSecurityPrice
        implements PrometheusDataValidator {

    @Override
    public void validate(final PrometheusDataItemCtl pPrice) {
        final MoneyWiseSecurityPrice myPrice = (MoneyWiseSecurityPrice) pPrice;
        final MoneyWiseSecurity mySecurity = myPrice.getSecurity();
        final OceanusDate myDate = myPrice.getDate();
        final OceanusPrice mySecPrice = myPrice.getPrice();
        final MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice> myList = myPrice.getList();
        final MoneyWiseDataSet mySet = (MoneyWiseDataSet) myPrice.getDataSet();

        /* The security must be non-null */
        if (mySecurity == null) {
            myPrice.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicDataType.SECURITY);

            /* The security must not be an option */
        } else if (mySecurity.getCategoryClass().isOption()) {
            myPrice.addError("Options are priced by the underlying stock", MoneyWiseBasicDataType.SECURITY);
        }

        /* The date must be non-null */
        if (myDate == null) {
            myPrice.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this security */
            final MoneyWiseSecurityPriceDataMap myMap = myList.getDataMap();
            if (!myMap.validPriceCount(myPrice)) {
                myPrice.addError(PrometheusDataItem.ERROR_DUPLICATE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareToDate(myDate) != 0) {
                myPrice.addError(PrometheusDataItem.ERROR_RANGE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }
        }

        /* The Price must be non-zero and greater than zero */
        if (mySecPrice == null) {
            myPrice.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else if (mySecPrice.isZero()) {
            myPrice.addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else if (!mySecPrice.isPositive()) {
            myPrice.addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else {
            /* Ensure that currency is correct */
            final MoneyWiseCurrency myCurrency = mySecurity == null
                    ? null
                    : mySecurity.getAssetCurrency();
            if (myCurrency != null
                    && !mySecPrice.getCurrency().equals(myCurrency.getCurrency())) {
                myPrice.addError(MoneyWiseSecurityPrice.ERROR_CURRENCY, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
            }
        }

        /* Set validation flag */
        if (!myPrice.hasErrors()) {
            myPrice.setValidEdit();
        }
    }
}
