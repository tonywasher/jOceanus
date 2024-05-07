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
package net.sourceforge.joceanus.jmoneywise.data.analysis.values;

import java.util.Currency;

import net.sourceforge.joceanus.jmoneywise.data.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * CategoryValues class.
 */
public final class MoneyWiseAnalysisCategoryValues
        extends MoneyWiseAnalysisValues<MoneyWiseAnalysisCategoryValues, MoneyWiseAnalysisTransAttr> {
    /**
     * Constructor.
     * @param pCurrency the reporting currency
     */
    public MoneyWiseAnalysisCategoryValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseAnalysisTransAttr.class);

        /* Create all possible values */
        super.setValue(MoneyWiseAnalysisTransAttr.INCOME, new TethysMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisTransAttr.EXPENSE, new TethysMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    public MoneyWiseAnalysisCategoryValues(final MoneyWiseAnalysisCategoryValues pSource,
                                           final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    protected MoneyWiseAnalysisCategoryValues getCounterSnapShot() {
        return new MoneyWiseAnalysisCategoryValues(this, true);
    }

    @Override
    protected MoneyWiseAnalysisCategoryValues getFullSnapShot() {
        return new MoneyWiseAnalysisCategoryValues(this, false);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseAnalysisCategoryValues pBase) {
        /* Adjust income/expense values */
        adjustMoneyToBase(pBase, MoneyWiseAnalysisTransAttr.INCOME);
        adjustMoneyToBase(pBase, MoneyWiseAnalysisTransAttr.EXPENSE);
        calculateDelta();
    }

    /**
     * Calculate delta.
     */
    public void calculateDelta() {
        /* Obtain a copy of the value */
        TethysMoney myDelta = getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME);
        myDelta = new TethysMoney(myDelta);

        /* Subtract the expense value */
        final TethysMoney myExpense = getMoneyValue(MoneyWiseAnalysisTransAttr.EXPENSE);
        myDelta.subtractAmount(myExpense);

        /* Set the delta */
        super.setValue(MoneyWiseAnalysisTransAttr.PROFIT, myDelta);
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        TethysMoney myValue = getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME);
        myValue = new TethysMoney(myValue);
        myValue.setZero();

        /* Reset Income and expense values */
        super.setValue(MoneyWiseAnalysisTransAttr.INCOME, myValue);
        super.setValue(MoneyWiseAnalysisTransAttr.EXPENSE, new TethysMoney(myValue));
        super.setValue(MoneyWiseAnalysisTransAttr.PROFIT, new TethysMoney(myValue));
    }

    /**
     * Are the values?
     * @return true/false
     */
    public boolean isActive() {
        final TethysMoney myIncome = getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME);
        final TethysMoney myExpense = getMoneyValue(MoneyWiseAnalysisTransAttr.EXPENSE);
        return myIncome.isNonZero() || myExpense.isNonZero();
    }
}

