/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;

/**
 * AccountAttribute enumeration.
 */
public enum AccountAttribute implements BucketAttribute {
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
            theName = AnalysisResource.getKeyForAccountAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isCounter() {
        switch (this) {
            case VALUATION:
            case FOREIGNVALUE:
            case LOCALVALUE:
            case CURRENCYFLUCT:
            case SPEND:
            case BADDEBTCAPITAL:
            case BADDEBTINTEREST:
                return true;
            case DEPOSITRATE:
            case EXCHANGERATE:
            case MATURITY:
            case VALUEDELTA:
            default:
                return false;
        }
    }

    @Override
    public MetisDataType getDataType() {
        switch (this) {
            case DEPOSITRATE:
                return MetisDataType.RATE;
            case EXCHANGERATE:
                return MetisDataType.RATIO;
            case MATURITY:
                return MetisDataType.DATE;
            case VALUATION:
            case FOREIGNVALUE:
            case LOCALVALUE:
            case VALUEDELTA:
            case CURRENCYFLUCT:
            case SPEND:
            case BADDEBTCAPITAL:
            case BADDEBTINTEREST:
            default:
                return MetisDataType.MONEY;
        }
    }
}
