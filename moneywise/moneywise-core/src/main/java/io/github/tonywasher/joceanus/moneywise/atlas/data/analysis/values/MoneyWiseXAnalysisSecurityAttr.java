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
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values;

import io.github.tonywasher.joceanus.metis.data.MetisDataType;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisAttribute;

/**
 * SecurityAttribute enumeration.
 */
public enum MoneyWiseXAnalysisSecurityAttr
        implements MoneyWiseXAnalysisAttribute {
    /**
     * Units.
     */
    UNITS,

    /**
     * Price in local currency.
     */
    PRICE,

    /**
     * Value in local currency.
     */
    VALUE,

    /**
     * Exchange Rate.
     */
    EXCHANGERATE,

    /**
     * Reporting Valuation.
     */
    VALUATION,

    /**
     * Valuation Delta.
     */
    VALUEDELTA,

    /**
     * Residual Cost.
     */
    RESIDUALCOST,

    /**
     * RealisedGains.
     */
    REALISEDGAINS,

    /**
     * UnrealisedGains.
     */
    UNREALISEDGAINS,

    /**
     * UnrealisedGainsAdjust.
     */
    GAINSADJUST,

    /**
     * Dividend.
     */
    DIVIDEND,

    /**
     * Profit.
     */
    PROFIT,

    /**
     * Profit.
     */
    MARKETPROFIT,

    /**
     * CashInvested.
     */
    CASHINVESTED,

    /**
     * CashConsideration (returned cash).
     */
    RETURNEDCASH,

    /**
     * StockConsideration (transferred value).
     */
    XFERREDVALUE,

    /**
     * Capital Gain.
     */
    CAPITALGAIN,

    /**
     * AllowedCost.
     */
    ALLOWEDCOST,

    /**
     * XferredCost.
     */
    XFERREDCOST,

    /**
     * Funded.
     */
    FUNDED,

    /**
     * StartDate.
     */
    STARTDATE,

    /**
     * SliceYears.
     */
    SLICEYEARS,

    /**
     * SliceGain.
     */
    SLICEGAIN,

    /**
     * Consideration.
     */
    CONSIDERATION,

    /**
     * CostDilution.
     */
    COSTDILUTION,

    /**
     * CashType.
     */
    CASHTYPE;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseXAnalysisValuesResource.getKeyForSecurityAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isPreserved() {
        return switch (this) {
            case UNITS, DIVIDEND, RESIDUALCOST, VALUATION, EXCHANGERATE, VALUE, PRICE, REALISEDGAINS, UNREALISEDGAINS,
                 GAINSADJUST, FUNDED, STARTDATE -> true;
            default -> false;
        };
    }

    @Override
    public MetisDataType getDataType() {
        return switch (this) {
            case UNITS -> MetisDataType.UNITS;
            case PRICE -> MetisDataType.PRICE;
            case EXCHANGERATE, COSTDILUTION -> MetisDataType.RATIO;
            case CASHTYPE -> MetisDataType.ENUM;
            case STARTDATE -> MetisDataType.DATE;
            default -> MetisDataType.MONEY;
        };
    }
}
