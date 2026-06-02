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
 * SecurityAttribute enumeration.
 */
public enum MoneyWiseAnalysisSecurityAttr implements MoneyWiseAnalysisAttribute {
    /**
     * Units.
     */
    UNITS,

    /**
     * Valuation.
     */
    VALUATION,

    /**
     * Foreign Valuation.
     */
    FOREIGNVALUE,

    /**
     * Valuation Delta.
     */
    VALUEDELTA,

    /**
     * Foreign Valuation Delta.
     */
    FOREIGNVALUEDELTA,

    /**
     * Exchange Rate.
     */
    EXCHANGERATE,

    /**
     * Residual Cost.
     */
    RESIDUALCOST,

    /**
     * Realised Gains.
     */
    REALISEDGAINS,

    /**
     * GrowthAdjust.
     */
    GROWTHADJUST,

    /**
     * Invested.
     */
    INVESTED,

    /**
     * Foreign Invested Amount.
     */
    FOREIGNINVESTED,

    /**
     * Dividend.
     */
    DIVIDEND,

    /**
     * MarketGrowth.
     */
    MARKETGROWTH,

    /**
     * ForeignMarketGrowth.
     */
    FOREIGNMARKETGROWTH,

    /**
     * LocalMarketGrowth.
     */
    LOCALMARKETGROWTH,

    /**
     * Currency Fluctuation.
     */
    CURRENCYFLUCT,

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
     * Consideration.
     */
    CONSIDERATION,

    /**
     * CashConsideration (returned cash).
     */
    RETURNEDCASH,

    /**
     * StockConsideration (transferred value).
     */
    XFERREDVALUE,

    /**
     * CostDilution.
     */
    COSTDILUTION,

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
     * Price.
     */
    PRICE,

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
            theName = bundleIdForSecurityAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isCounter() {
        return switch (this) {
            case UNITS, RESIDUALCOST, REALISEDGAINS, GROWTHADJUST, DIVIDEND, INVESTED, FOREIGNINVESTED -> true;
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
            default -> MetisDataType.MONEY;
        };
    }

    /**
     * Obtain the resource bundleId for the attribute.
     *
     * @param pAttr the attribute
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForSecurityAttr(final MoneyWiseAnalysisSecurityAttr pAttr) {
        /* Create the map and return it */
        return switch (pAttr) {
            case VALUATION -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_VALUATION;
            case FOREIGNVALUE -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_FOREIGNVALUE;
            case VALUEDELTA -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_VALUEDELTA;
            case FOREIGNVALUEDELTA -> MoneyWiseAnalysisValuesResource.SECURITYATTR_FOREIGNVALUEDELTA;
            case EXCHANGERATE -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_EXCHANGERATE;
            case UNITS -> MoneyWiseAnalysisValuesResource.SECURITYATTR_UNITS;
            case RESIDUALCOST -> MoneyWiseAnalysisValuesResource.SECURITYATTR_RESIDUALCOST;
            case REALISEDGAINS -> MoneyWiseAnalysisValuesResource.SECURITYATTR_REALISEDGAINS;
            case GROWTHADJUST -> MoneyWiseAnalysisValuesResource.SECURITYATTR_GROWTHADJUST;
            case INVESTED -> MoneyWiseAnalysisValuesResource.SECURITYATTR_INVESTED;
            case FOREIGNINVESTED -> MoneyWiseAnalysisValuesResource.SECURITYATTR_FOREIGNINVESTED;
            case DIVIDEND -> MoneyWiseAnalysisValuesResource.SECURITYATTR_DIVIDEND;
            case MARKETGROWTH -> MoneyWiseAnalysisValuesResource.SECURITYATTR_MARKETGROWTH;
            case FOREIGNMARKETGROWTH -> MoneyWiseAnalysisValuesResource.SECURITYATTR_FOREIGNMARKETGROWTH;
            case LOCALMARKETGROWTH -> MoneyWiseAnalysisValuesResource.SECURITYATTR_LOCALMARKETGROWTH;
            case CURRENCYFLUCT -> MoneyWiseAnalysisValuesResource.ACCOUNTATTR_CURRENCYFLUCT;
            case MARKETPROFIT -> MoneyWiseAnalysisValuesResource.SECURITYATTR_MARKETPROFIT;
            case PROFIT -> MoneyWiseAnalysisValuesResource.SECURITYATTR_PROFIT;
            case CONSIDERATION -> MoneyWiseAnalysisValuesResource.SECURITYATTR_CONSIDER;
            case RETURNEDCASH -> MoneyWiseAnalysisValuesResource.SECURITYATTR_RETURNEDCASH;
            case XFERREDVALUE -> MoneyWiseAnalysisValuesResource.SECURITYATTR_XFERREDVALUE;
            case XFERREDCOST -> MoneyWiseAnalysisValuesResource.SECURITYATTR_XFERREDCOST;
            case COSTDILUTION -> MoneyWiseAnalysisValuesResource.SECURITYATTR_COSTDILUTION;
            case CASHINVESTED -> MoneyWiseAnalysisValuesResource.SECURITYATTR_CASHINVESTED;
            case CAPITALGAIN -> MoneyWiseAnalysisValuesResource.SECURITYATTR_CAPITALGAIN;
            case ALLOWEDCOST -> MoneyWiseAnalysisValuesResource.SECURITYATTR_ALLOWEDCOST;
            case PRICE -> MoneyWiseAnalysisValuesResource.SECURITYATTR_PRICE;
            case CASHTYPE -> MoneyWiseAnalysisValuesResource.SECURITYATTR_CASHTYPE;
            default -> throw new IllegalArgumentException();
        };
    }
}
