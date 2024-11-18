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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.values;

import java.util.Currency;

import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * AccountValues class.
 */
public class MoneyWiseAnalysisAccountValues
        extends MoneyWiseAnalysisValues<MoneyWiseAnalysisAccountValues, MoneyWiseAnalysisAccountAttr> {
    /**
     * Constructor.
     * @param pCurrency the account currency
     */
    public MoneyWiseAnalysisAccountValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseAnalysisAccountAttr.class);

        /* Initialise valuation to zero */
        super.setValue(MoneyWiseAnalysisAccountAttr.VALUATION, new TethysMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pCurrency the account currency
     * @param pReportingCurrency the reporting currency
     */
    public MoneyWiseAnalysisAccountValues(final Currency pCurrency,
                                          final Currency pReportingCurrency) {
        /* Initialise class */
        this(pReportingCurrency);

        /* Initialise valuation to zero */
        super.setValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE, new TethysMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisAccountAttr.LOCALVALUE, new TethysMoney(pReportingCurrency));
        super.setValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, new TethysMoney(pReportingCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    protected MoneyWiseAnalysisAccountValues(final MoneyWiseAnalysisAccountValues pSource,
                                             final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    protected MoneyWiseAnalysisAccountValues getCounterSnapShot() {
        return new MoneyWiseAnalysisAccountValues(this, true);
    }

    @Override
    protected MoneyWiseAnalysisAccountValues getFullSnapShot() {
        return new MoneyWiseAnalysisAccountValues(this, false);
    }

    /**
     * Are the values active?
     * @return true/false
     */
    public boolean isActive() {
        final TethysMoney myValuation = getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        return myValuation != null && myValuation.isNonZero();
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseAnalysisAccountValues pBase) {
        /* If we have a currency fluctuation */
        if (getMoneyValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT) != null) {
            /* Adjust currency fluctuation values */
            adjustMoneyToBase(pBase, MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT);
        }
    }

    @Override
    public void resetBaseValues() {
        /* If we have a currency fluctuation */
        TethysMoney myValue = getMoneyValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT);
        if (myValue != null) {
            /* Create zero value */
            myValue = new TethysMoney(myValue);
            myValue.setZero();

            /* Adjust currency fluctuation values */
            super.setValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, myValue);
        }
    }
}
