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
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * PayeeValues class.
 */
public final class MoneyWiseAnalysisPayeeValues
        extends MoneyWiseAnalysisValues<MoneyWiseAnalysisPayeeValues, MoneyWiseAnalysisPayeeAttr> {
    /**
     * Constructor.
     * @param pCurrency the reporting currency
     */
    public MoneyWiseAnalysisPayeeValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseAnalysisPayeeAttr.class);

        /* Initialise income/expense to zero */
        super.setValue(MoneyWiseAnalysisPayeeAttr.INCOME, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisPayeeAttr.EXPENSE, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    public MoneyWiseAnalysisPayeeValues(final MoneyWiseAnalysisPayeeValues pSource,
                                         final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    protected MoneyWiseAnalysisPayeeValues getCounterSnapShot() {
        return new MoneyWiseAnalysisPayeeValues(this, true);
    }

    @Override
    protected MoneyWiseAnalysisPayeeValues getFullSnapShot() {
        return new MoneyWiseAnalysisPayeeValues(this, false);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseAnalysisPayeeValues pBase) {
        /* Adjust income/expense values */
        adjustMoneyToBase(pBase, MoneyWiseAnalysisPayeeAttr.INCOME);
        adjustMoneyToBase(pBase, MoneyWiseAnalysisPayeeAttr.EXPENSE);
        calculateDelta();
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        OceanusMoney myValue = getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        myValue = new OceanusMoney(myValue);
        myValue.setZero();

        /* Reset Income and expense values */
        super.setValue(MoneyWiseAnalysisPayeeAttr.INCOME, myValue);
        super.setValue(MoneyWiseAnalysisPayeeAttr.EXPENSE, new OceanusMoney(myValue));
        super.setValue(MoneyWiseAnalysisPayeeAttr.PROFIT, new OceanusMoney(myValue));
    }

    /**
     * Calculate delta.
     */
    public void calculateDelta() {
        /* Obtain a copy of the value */
        OceanusMoney myDelta = getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        myDelta = new OceanusMoney(myDelta);

        /* Subtract the expense value */
        final OceanusMoney myExpense = getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE);
        myDelta.subtractAmount(myExpense);

        /* Set the delta */
        super.setValue(MoneyWiseAnalysisPayeeAttr.PROFIT, myDelta);
    }

    /**
     * Are the values?
     * @return true/false
     */
    public boolean isActive() {
        final OceanusMoney myIncome = getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        final OceanusMoney myExpense = getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE);
        return myIncome.isNonZero() || myExpense.isNonZero();
    }
}

