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
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

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
            theName = bundleIdForAttribute(this).getValue();
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

    /**
     * Obtain the resource bundleId for the attribute.
     *
     * @param pAttr the attribute
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForAttribute(final MoneyWiseXAnalysisSecurityAttr pAttr) {
        return switch (pAttr) {
            case VALUE -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_VALUE;
            case VALUATION -> MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_VALUATION;
            case VALUEDELTA -> MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_VALUEDELTA;
            case EXCHANGERATE -> MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_EXCHANGERATE;
            case UNITS -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_UNITS;
            case RESIDUALCOST -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_RESIDUALCOST;
            case DIVIDEND -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_DIVIDEND;
            case REALISEDGAINS -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_REALISEDGAINS;
            case UNREALISEDGAINS -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_UNREALISEDGAINS;
            case GAINSADJUST -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_GAINSADJUST;
            case MARKETPROFIT -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_MARKETPROFIT;
            case PROFIT -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_PROFIT;
            case RETURNEDCASH -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_RETURNEDCASH;
            case XFERREDVALUE -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_XFERREDVALUE;
            case XFERREDCOST -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_XFERREDCOST;
            case CASHINVESTED -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_CASHINVESTED;
            case CAPITALGAIN -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_CAPITALGAIN;
            case ALLOWEDCOST -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_ALLOWEDCOST;
            case PRICE -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_PRICE;
            case STARTDATE -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_STARTDATE;
            case FUNDED -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_FUNDED;
            case SLICEGAIN -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_SLICEGAIN;
            case SLICEYEARS -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_SLICEYEARS;
            case CONSIDERATION -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_CONSIDERATION;
            case COSTDILUTION -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_COSTDILUTION;
            case CASHTYPE -> MoneyWiseXAnalysisValuesResource.SECURITYATTR_CASHTYPE;
            default -> throw new IllegalArgumentException();
        };
    }
}
