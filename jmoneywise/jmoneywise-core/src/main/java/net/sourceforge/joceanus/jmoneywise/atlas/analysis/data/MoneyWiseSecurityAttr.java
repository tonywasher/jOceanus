/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisAttribute;

/**
 * Security attributes.
 */
public enum MoneyWiseSecurityAttr
        implements MoneyWiseAnalysisAttribute {
    /**
     * Units.
     */
    UNITS,

    /**
     * Valuation.
     */
    VALUATION,

    /**
     * Local Valuation.
     */
    LOCALVALUE,

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
     * Invested.
     */
    INVESTED,

    /**
     * Dividend.
     */
    DIVIDEND,

    /**
     * Profit.
     */
    PROFIT,

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
            theName = MoneyWiseAnalysisDataResource.getKeyForSecurityAttr(this).getValue();
        }

        /* return the name */
        return theName;
    }

    @Override
    public MetisDataType getDataType() {
        switch (this) {
            case UNITS:
                return MetisDataType.UNITS;
            case PRICE:
                return MetisDataType.PRICE;
            case EXCHANGERATE:
                return MetisDataType.RATIO;
            case VALUATION:
            case LOCALVALUE:
            case RESIDUALCOST:
            case REALISEDGAINS:
            case INVESTED:
            case DIVIDEND:
            case PROFIT:
            default:
                return MetisDataType.MONEY;
        }
    }

    @Override
    public boolean isForeign() {
        return this == LOCALVALUE;
    }
}
