/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.analysis;

import net.sourceforge.joceanus.jmetis.data.DataType;

/**
 * SecurityAttribute enumeration.
 */
public enum SecurityAttribute implements BucketAttribute {
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
     * Cost.
     */
    COST,

    /**
     * Gains.
     */
    GAINS,

    /**
     * Local Gains.
     */
    LOCALGAINS,

    /**
     * Foreign Gains.
     */
    FOREIGNGAINS,

    /**
     * GrowthAdjust.
     */
    GROWTHADJUST,

    /**
     * Invested.
     */
    INVESTED,

    /**
     * Local Invested Amount.
     */
    LOCALINVESTED,

    /**
     * Foreign Invested Amount.
     */
    FOREIGNINVESTED,

    /**
     * Dividend.
     */
    DIVIDEND,

    /**
     * Local Dividend Amount.
     */
    LOCALDIVIDEND,

    /**
     * Foreign Dividend Amount.
     */
    FOREIGNDIVIDEND,

    /**
     * Market.
     */
    MARKET,

    /**
     * Profit.
     */
    PROFIT,

    /**
     * Profit.
     */
    MARKETPROFIT,

    /**
     * Price.
     */
    PRICE;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = AnalysisResource.getKeyForSecurityAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isCounter() {
        switch (this) {
            case UNITS:
            case COST:
            case GAINS:
            case LOCALGAINS:
            case FOREIGNGAINS:
            case GROWTHADJUST:
            case DIVIDEND:
            case LOCALDIVIDEND:
            case FOREIGNDIVIDEND:
            case INVESTED:
            case LOCALINVESTED:
            case FOREIGNINVESTED:
                return true;
            case MARKET:
            case PROFIT:
            case MARKETPROFIT:
            case VALUATION:
            case FOREIGNVALUE:
            case EXCHANGERATE:
            case PRICE:
            case VALUEDELTA:
            case FOREIGNVALUEDELTA:
            default:
                return false;
        }
    }

    @Override
    public DataType getDataType() {
        switch (this) {
            case UNITS:
                return DataType.UNITS;
            case PRICE:
                return DataType.PRICE;
            case EXCHANGERATE:
                return DataType.RATIO;
            case VALUATION:
            case FOREIGNVALUE:
            case VALUEDELTA:
            case FOREIGNVALUEDELTA:
            case COST:
            case GAINS:
            case LOCALGAINS:
            case FOREIGNGAINS:
            case GROWTHADJUST:
            case INVESTED:
            case LOCALINVESTED:
            case FOREIGNINVESTED:
            case DIVIDEND:
            case LOCALDIVIDEND:
            case FOREIGNDIVIDEND:
            case MARKET:
            case MARKETPROFIT:
            case PROFIT:
            default:
                return DataType.MONEY;
        }
    }
}
