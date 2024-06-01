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

import java.util.Currency;

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisValues;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * AccountValues class.
 */
public class MoneyWiseXAnalysisAccountValues
        extends MoneyWiseXAnalysisValues<MoneyWiseXAnalysisAccountValues, MoneyWiseXAnalysisAccountAttr> {
    /**
     * Constructor.
     * @param pCurrency the account currency
     */
    public MoneyWiseXAnalysisAccountValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseXAnalysisAccountAttr.class);

        /* Initialise valuation to zero */
        final TethysMoney myValue = new TethysMoney(pCurrency);
        setValue(MoneyWiseXAnalysisAccountAttr.BALANCE, myValue);
        setValue(MoneyWiseXAnalysisAccountAttr.VALUATION, myValue);
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    protected MoneyWiseXAnalysisAccountValues(final MoneyWiseXAnalysisAccountValues pSource,
                                              final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    protected MoneyWiseXAnalysisAccountValues getCounterSnapShot() {
        return new MoneyWiseXAnalysisAccountValues(this, true);
    }

    @Override
    protected MoneyWiseXAnalysisAccountValues getFullSnapShot() {
        return new MoneyWiseXAnalysisAccountValues(this, false);
    }

    /**
     * Are the values active?
     * @return true/false
     */
    public boolean isActive() {
        final TethysMoney myValuation = getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
        return myValuation != null && myValuation.isNonZero();
    }
}
