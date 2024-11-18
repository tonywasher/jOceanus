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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.values;

import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisAttribute;

/**
 * AccountAttribute enumeration.
 */
public enum MoneyWiseXAnalysisAccountAttr
        implements MoneyWiseXAnalysisAttribute {
    /**
     * Local Currency Balance.
     */
    BALANCE,

    /**
     * Exchange Rate.
     */
    EXCHANGERATE,

    /**
     * Reported Valuation.
     */
    VALUATION,

    /**
     * Valuation Delta.
     */
    VALUEDELTA,

    /**
     * Deposit Rate.
     */
    DEPOSITRATE,

    /**
     * Maturity.
     */
    MATURITY;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseXAnalysisValuesResource.getKeyForAccountAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isPreserved() {
        switch (this) {
            case BALANCE:
            case MATURITY:
            case DEPOSITRATE:
            case EXCHANGERATE:
            case VALUATION:
                return true;
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
            case BALANCE:
            case VALUATION:
            case VALUEDELTA:
            default:
                return MetisDataType.MONEY;
        }
    }
}
