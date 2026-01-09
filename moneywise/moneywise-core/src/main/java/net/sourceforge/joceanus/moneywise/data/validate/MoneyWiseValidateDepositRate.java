/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateList;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;

/**
 * Validator for DepositRate.
 */
public class MoneyWiseValidateDepositRate
        implements PrometheusDataValidator {

    @Override
    public void validate(final PrometheusDataItem pRate) {
        final MoneyWiseDepositRate myRate = (MoneyWiseDepositRate) pRate;
        final MoneyWiseDepositRateList myList = myRate.getList();
        final OceanusDate myDate = myRate.getEndDate();
        final OceanusRate myDepRate = myRate.getRate();
        final OceanusRate myBonus = myRate.getBonus();

        /* Count instances of this date for the account */
        final MoneyWiseDepositRateDataMap myMap = myList.getDataMap();
        if (!myMap.validRateCount(myRate)) {
            /* Each date must be unique for deposit (even null) */
            pRate.addError(myDate == null
                    ? MoneyWiseDepositRate.ERROR_NULLDATE
                    : PrometheusDataItem.ERROR_DUPLICATE, MoneyWiseBasicResource.DEPOSITRATE_ENDDATE);
        }

        /* The Rate must be non-zero and greater than zero */
        if (myDepRate == null) {
            pRate.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
        } else if (!myDepRate.isPositive()) {
            pRate.addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE);
        }

        /* The bonus rate must be non-zero if it exists */
        if (myBonus != null) {
            if (myBonus.isZero()) {
                pRate.addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseBasicResource.DEPOSITRATE_BONUS);
            } else if (!myBonus.isPositive()) {
                pRate.addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseBasicResource.DEPOSITRATE_BONUS);
            }
        }

        /* Set validation flag */
        if (!pRate.hasErrors()) {
            pRate.setValidEdit();
        }
    }
}
