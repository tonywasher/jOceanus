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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceBaseList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;

/**
 * Validator for SecurityPrice.
 */
public class MoneyWiseValidateSecurityPrice
        implements PrometheusDataValidator {

    @Override
    public void validate(final PrometheusDataItem pPrice) {
        final MoneyWiseSecurityPrice myPrice = (MoneyWiseSecurityPrice) pPrice;
        final MoneyWiseSecurity mySecurity = myPrice.getSecurity();
        final OceanusDate myDate = myPrice.getDate();
        final OceanusPrice mySecPrice = myPrice.getPrice();
        final MoneyWiseSecurityPriceBaseList<? extends MoneyWiseSecurityPrice> myList = myPrice.getList();
        final MoneyWiseDataSet mySet = myPrice.getDataSet();

        /* The security must be non-null */
        if (mySecurity == null) {
            pPrice.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicDataType.SECURITY);

            /* The security must not be an option */
        } else if (mySecurity.getCategoryClass().isOption()) {
            pPrice.addError("Options are priced by the underlying stock", MoneyWiseBasicDataType.SECURITY);
        }

        /* The date must be non-null */
        if (myDate == null) {
            pPrice.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this security */
            final MoneyWiseSecurityPriceDataMap myMap = myList.getDataMap();
            if (!myMap.validPriceCount(myPrice)) {
                pPrice.addError(PrometheusDataItem.ERROR_DUPLICATE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareToDate(myDate) != 0) {
                pPrice.addError(PrometheusDataItem.ERROR_RANGE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }
        }

        /* The Price must be non-zero and greater than zero */
        if (mySecPrice == null) {
            pPrice.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else if (mySecPrice.isZero()) {
            pPrice.addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else if (!mySecPrice.isPositive()) {
            pPrice.addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
        } else {
            /* Ensure that currency is correct */
            final MoneyWiseCurrency myCurrency = mySecurity == null
                    ? null
                    : mySecurity.getAssetCurrency();
            if (myCurrency != null
                    && !mySecPrice.getCurrency().equals(myCurrency.getCurrency())) {
                pPrice.addError(MoneyWiseSecurityPrice.ERROR_CURRENCY, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE);
            }
        }

        /* Set validation flag */
        if (!pPrice.hasErrors()) {
            pPrice.setValidEdit();
        }
    }
}
