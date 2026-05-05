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

import io.github.tonywasher.joceanus.metis.field.MetisFieldRequired;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolioInfo;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolioInfoSet;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInfoClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.validate.PrometheusValidateInfoSet;

/**
 * Validate PortfolioInfoSet.
 */
public class MoneyWiseValidatePortfolioInfoSet
        extends PrometheusValidateInfoSet<MoneyWisePortfolioInfo> {
    @Override
    public MoneyWisePortfolio getOwner() {
        return (MoneyWisePortfolio) super.getOwner();
    }

    @Override
    public MoneyWisePortfolioInfoSet getInfoSet() {
        return (MoneyWisePortfolioInfoSet) super.getInfoSet();
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        return switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES, SORTCODE, ACCOUNT, REFERENCE, WEBSITE, CUSTOMERNO, USERID, PASSWORD ->
                    MetisFieldRequired.CANEXIST;

            /* Not Allowed */
            default -> MetisFieldRequired.NOTALLOWED;
        };
    }

    @Override
    public void validateClass(final MoneyWisePortfolioInfo pInfo,
                              final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case WEBSITE, CUSTOMERNO, USERID, PASSWORD, SORTCODE, ACCOUNT, NOTES, REFERENCE:
                validateInfoLength(pInfo);
                break;
            default:
                break;
        }
    }

    /**
     * Validate the info length.
     *
     * @param pInfo the info
     */
    private void validateInfoLength(final MoneyWisePortfolioInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        final MoneyWiseAccountInfoClass myClass = pInfo.getInfoClass();
        if (myArray.length > myClass.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWisePortfolioInfoSet.getFieldForClass(myClass));
        }
    }
}
