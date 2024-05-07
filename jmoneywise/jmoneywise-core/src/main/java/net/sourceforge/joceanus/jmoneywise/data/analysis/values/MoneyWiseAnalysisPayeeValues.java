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
        super.setValue(MoneyWiseAnalysisPayeeAttr.INCOME, new TethysMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisPayeeAttr.EXPENSE, new TethysMoney(pCurrency));
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
        TethysMoney myValue = getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        myValue = new TethysMoney(myValue);
        myValue.setZero();

        /* Reset Income and expense values */
        super.setValue(MoneyWiseAnalysisPayeeAttr.INCOME, myValue);
        super.setValue(MoneyWiseAnalysisPayeeAttr.EXPENSE, new TethysMoney(myValue));
        super.setValue(MoneyWiseAnalysisPayeeAttr.PROFIT, new TethysMoney(myValue));
    }

    /**
     * Calculate delta.
     */
    public void calculateDelta() {
        /* Obtain a copy of the value */
        TethysMoney myDelta = getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        myDelta = new TethysMoney(myDelta);

        /* Subtract the expense value */
        final TethysMoney myExpense = getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE);
        myDelta.subtractAmount(myExpense);

        /* Set the delta */
        super.setValue(MoneyWiseAnalysisPayeeAttr.PROFIT, myDelta);
    }

    /**
     * Are the values?
     * @return true/false
     */
    public boolean isActive() {
        final TethysMoney myIncome = getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        final TethysMoney myExpense = getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE);
        return myIncome.isNonZero() || myExpense.isNonZero();
    }
}

