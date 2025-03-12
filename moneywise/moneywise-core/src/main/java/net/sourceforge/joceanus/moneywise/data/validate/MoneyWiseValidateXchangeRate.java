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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateBaseList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateDataMap;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;

/**
 * Validator for region.
 */
public class MoneyWiseValidateXchangeRate
        implements PrometheusDataValidator<MoneyWiseExchangeRate> {

    @Override
    public void validate(final MoneyWiseExchangeRate pRate) {
        final MoneyWiseExchangeRateBaseList<? extends MoneyWiseExchangeRate> myList = pRate.getList();
        final MoneyWiseCurrency myFrom = pRate.getFromCurrency();
        final MoneyWiseCurrency myTo = pRate.getToCurrency();
        final OceanusDate myDate = pRate.getDate();
        final OceanusRatio myRate = pRate.getExchangeRate();
        final OceanusDateRange myRange = pRate.getDataSet().getDateRange();

        /* Date must be non-null */
        if (myDate == null) {
            pRate.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this currency */
            final MoneyWiseExchangeRateDataMap myMap = myList.getDataMap();
            if (!myMap.validRateCount(pRate)) {
                pRate.addError(PrometheusDataItem.ERROR_DUPLICATE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }

            /* The date must be in-range */
            if (myRange.compareToDate(myDate) != 0) {
                pRate.addError(PrometheusDataItem.ERROR_RANGE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            }
        }

        /* FromCurrency must be non-null and enabled */
        if (myFrom == null) {
            pRate.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.XCHGRATE_FROM);
        } else if (!myFrom.getEnabled()) {
            pRate.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseBasicResource.XCHGRATE_FROM);
        }

        /* ToCurrency must be non-null and enabled */
        if (myTo == null) {
            pRate.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.XCHGRATE_TO);
        } else if (!myTo.getEnabled()) {
            pRate.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseBasicResource.XCHGRATE_TO);
        }

        /* Check currency combination */
        if (myFrom != null && myTo != null) {
            /* Must be different */
            if (myFrom.equals(myTo)) {
                pRate.addError(MoneyWiseExchangeRate.ERROR_CIRCLE, MoneyWiseBasicResource.XCHGRATE_TO);
            }

            /* From currency must be the reporting currency */
            final MoneyWiseCurrency myDefault = pRate.getDataSet().getReportingCurrency();
            if (!myFrom.equals(myDefault)) {
                pRate.addError(MoneyWiseExchangeRate.ERROR_DEF, MoneyWiseBasicResource.XCHGRATE_FROM);
            }
        }

        /* Rate must be non-null and positive non-zero */
        if (myRate == null) {
            pRate.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.XCHGRATE_RATE);
        } else if (!myRate.isNonZero()) {
            pRate.addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseBasicResource.XCHGRATE_RATE);
        } else if (!myRate.isPositive()) {
            pRate.addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseBasicResource.XCHGRATE_RATE);
        }

        /* Set validation flag */
        if (!pRate.hasErrors()) {
            pRate.setValidEdit();
        }
    }
}
