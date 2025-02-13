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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.values;

import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisAttribute;

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
            theName = MoneyWiseAnalysisValuesResource.getKeyForSecurityAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isCounter() {
        switch (this) {
            case UNITS:
            case RESIDUALCOST:
            case REALISEDGAINS:
            case GROWTHADJUST:
            case DIVIDEND:
            case INVESTED:
            case FOREIGNINVESTED:
                return true;
            case MARKETGROWTH:
            case FOREIGNMARKETGROWTH:
            case LOCALMARKETGROWTH:
            case CURRENCYFLUCT:
            case PROFIT:
            case MARKETPROFIT:
            case VALUATION:
            case FOREIGNVALUE:
            case EXCHANGERATE:
            case PRICE:
            case VALUEDELTA:
            case FOREIGNVALUEDELTA:
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
            case VALUATION:
            case FOREIGNVALUE:
            case VALUEDELTA:
            case FOREIGNVALUEDELTA:
            case RESIDUALCOST:
            case REALISEDGAINS:
            case GROWTHADJUST:
            case INVESTED:
            case FOREIGNINVESTED:
            case DIVIDEND:
            case MARKETGROWTH:
            case FOREIGNMARKETGROWTH:
            case LOCALMARKETGROWTH:
            case CURRENCYFLUCT:
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
