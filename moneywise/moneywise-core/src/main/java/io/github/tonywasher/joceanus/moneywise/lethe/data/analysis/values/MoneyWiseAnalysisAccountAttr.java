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
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values;

import io.github.tonywasher.joceanus.metis.data.MetisDataType;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisAttribute;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * AccountAttribute enumeration.
 */
public enum MoneyWiseAnalysisAccountAttr implements MoneyWiseAnalysisAttribute {
    /**
     * Valuation.
     */
    VALUATION,

    /**
     * Foreign Valuation.
     */
    FOREIGNVALUE,

    /**
     * Local Valuation.
     */
    LOCALVALUE,

    /**
     * Currency Fluctuation.
     */
    CURRENCYFLUCT,

    /**
     * Deposit Rate.
     */
    DEPOSITRATE,

    /**
     * Exchange Rate.
     */
    EXCHANGERATE,

    /**
     * Valuation Delta.
     */
    VALUEDELTA,

    /**
     * Maturity.
     */
    MATURITY,

    /**
     * Spend.
     */
    SPEND,

    /**
     * BadDebtCapital.
     */
    BADDEBTCAPITAL,

    /**
     * BadDebtInterest.
     */
    BADDEBTINTEREST;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForAccountAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isCounter() {
        return switch (this) {
            case VALUATION, FOREIGNVALUE, LOCALVALUE, CURRENCYFLUCT, SPEND, BADDEBTCAPITAL, BADDEBTINTEREST -> true;
            default -> false;
        };
    }

    @Override
    public MetisDataType getDataType() {
        return switch (this) {
            case DEPOSITRATE -> MetisDataType.RATE;
            case EXCHANGERATE -> MetisDataType.RATIO;
            case MATURITY -> MetisDataType.DATE;
            default -> MetisDataType.MONEY;
        };
    }

    /**
     * Obtain the resource bundleId for the attribute.
     *
     * @param pAttr the attribute
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForAccountAttr(final MoneyWiseAnalysisAccountAttr pAttr) {
        /* Create the map and return it */
        return switch (pAttr) {
            case VALUATION -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_VALUATION;
            case FOREIGNVALUE -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_FOREIGNVALUE;
            case LOCALVALUE -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_LOCALVALUE;
            case CURRENCYFLUCT -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_CURRENCYFLUCT;
            case DEPOSITRATE -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_DEPOSITRATE;
            case EXCHANGERATE -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_EXCHANGERATE;
            case VALUEDELTA -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_VALUEDELTA;
            case MATURITY -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_MATURITY;
            case SPEND -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_SPEND;
            case BADDEBTCAPITAL -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_BADDEBTCAPITAL;
            case BADDEBTINTEREST -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_BADDEBTINTEREST;
            default -> throw new IllegalArgumentException();
        };
    }
}
