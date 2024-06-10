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
 * PayeeValues class.
 */
public final class MoneyWiseXAnalysisPayeeValues
        extends MoneyWiseXAnalysisValues<MoneyWiseXAnalysisPayeeValues, MoneyWiseXAnalysisPayeeAttr> {
    /**
     * Constructor.
     * @param pCurrency the reporting currency
     */
    public MoneyWiseXAnalysisPayeeValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseXAnalysisPayeeAttr.class);

        /* Initialise income/expense to zero */
        super.setValue(MoneyWiseXAnalysisPayeeAttr.INCOME, new TethysMoney(pCurrency));
        super.setValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE, new TethysMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    public MoneyWiseXAnalysisPayeeValues(final MoneyWiseXAnalysisPayeeValues pSource,
                                         final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    protected MoneyWiseXAnalysisPayeeValues getCounterSnapShot() {
        return new MoneyWiseXAnalysisPayeeValues(this, true);
    }

    @Override
    protected MoneyWiseXAnalysisPayeeValues getFullSnapShot() {
        return new MoneyWiseXAnalysisPayeeValues(this, false);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseXAnalysisPayeeValues pBase) {
        /* Adjust income/expense values */
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisPayeeAttr.INCOME);
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisPayeeAttr.EXPENSE);
        calculateDelta();
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        TethysMoney myValue = getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME);
        myValue = new TethysMoney(myValue);
        myValue.setZero();

        /* Reset Income and expense values */
        super.setValue(MoneyWiseXAnalysisPayeeAttr.INCOME, myValue);
        super.setValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE, new TethysMoney(myValue));
        super.setValue(MoneyWiseXAnalysisPayeeAttr.PROFIT, new TethysMoney(myValue));
    }

    /**
     * Calculate delta.
     */
    public void calculateDelta() {
        /* Obtain a copy of the value */
        TethysMoney myDelta = getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME);
        myDelta = new TethysMoney(myDelta);

        /* Subtract the expense value */
        final TethysMoney myExpense = getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE);
        myDelta.subtractAmount(myExpense);

        /* Set the delta */
        super.setValue(MoneyWiseXAnalysisPayeeAttr.PROFIT, myDelta);
    }

    /**
     * Are the values?
     * @return true/false
     */
    public boolean isActive() {
        final TethysMoney myIncome = getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME);
        final TethysMoney myExpense = getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE);
        return myIncome.isNonZero() || myExpense.isNonZero();
    }
}
