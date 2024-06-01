/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisAttribute;

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
     * Invested.
     */
    INVESTED,

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
        switch (this) {
            case UNITS:
            case DIVIDEND:
            case INVESTED:
            case RESIDUALCOST:
            case VALUATION:
            case EXCHANGERATE:
            case VALUE:
            case PRICE:
            case REALISEDGAINS:
            case UNREALISEDGAINS:
                return true;
            case PROFIT:
            case MARKETPROFIT:
            case VALUEDELTA:
            case XFERREDCOST:
            case COSTDILUTION:
            case CONSIDERATION:
            case RETURNEDCASH:
            case XFERREDVALUE:
            case CASHINVESTED:
            case CAPITALGAIN:
            case ALLOWEDCOST:
            case CASHTYPE:
            default:
                return false;
        }
    }

    @Override
    public MetisDataType getDataType() {
        switch (this) {
            case UNITS:
                return MetisDataType.UNITS;
            case PRICE:
                return MetisDataType.PRICE;
            case EXCHANGERATE:
            case COSTDILUTION:
                return MetisDataType.RATIO;
            case CASHTYPE:
                return MetisDataType.ENUM;
            case VALUE:
            case VALUATION:
            case VALUEDELTA:
            case RESIDUALCOST:
            case REALISEDGAINS:
            case UNREALISEDGAINS:
            case INVESTED:
            case DIVIDEND:
            case MARKETPROFIT:
            case PROFIT:
            case CONSIDERATION:
            case RETURNEDCASH:
            case XFERREDVALUE:
            case XFERREDCOST:
            case ALLOWEDCOST:
            case CASHINVESTED:
            case CAPITALGAIN:
            default:
                return MetisDataType.MONEY;
        }
    }
}
