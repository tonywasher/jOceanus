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

import java.util.Currency;

import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * CreditCardValues class.
 */
public final class MoneyWiseAnalysisCreditCardValues
        extends MoneyWiseAnalysisAccountValues {
    /**
     * Constructor.
     * @param pCurrency the account currency
     */
    public MoneyWiseAnalysisCreditCardValues(final Currency pCurrency) {
        /* Initialise class */
        super(pCurrency);

        /* Initialise spend to zero */
        setValue(MoneyWiseAnalysisAccountAttr.SPEND, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pCurrency the account currency
     * @param pReportingCurrency the reporting currency
     */
    public MoneyWiseAnalysisCreditCardValues(final Currency pCurrency,
                                             final Currency pReportingCurrency) {
        /* Initialise class */
        super(pCurrency, pReportingCurrency);

        /* Initialise spend to zero */
        setValue(MoneyWiseAnalysisAccountAttr.SPEND, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    private MoneyWiseAnalysisCreditCardValues(final MoneyWiseAnalysisCreditCardValues pSource,
                                              final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    public MoneyWiseAnalysisCreditCardValues getCounterSnapShot() {
        return new MoneyWiseAnalysisCreditCardValues(this, true);
    }

    @Override
    public MoneyWiseAnalysisCreditCardValues getFullSnapShot() {
        return new MoneyWiseAnalysisCreditCardValues(this, false);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseAnalysisAccountValues pBase) {
        /* Adjust spend values */
        adjustMoneyToBase(pBase, MoneyWiseAnalysisAccountAttr.SPEND);
    }

    @Override
    public void resetBaseValues() {
        /* Reset spend values */
        OceanusMoney mySpend = getMoneyValue(MoneyWiseAnalysisAccountAttr.SPEND);
        if (mySpend.isNonZero()) {
            mySpend = new OceanusMoney(mySpend);
            mySpend.setZero();
            setValue(MoneyWiseAnalysisAccountAttr.SPEND, mySpend);
        }
    }
}


