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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.values;

import java.util.Currency;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisValues;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * CategoryValues class.
 */
public final class MoneyWiseXAnalysisTransValues
        extends MoneyWiseXAnalysisValues<MoneyWiseXAnalysisTransValues, MoneyWiseXAnalysisTransAttr> {
    /**
     * Constructor.
     * @param pCurrency the reporting currency
     */
    public MoneyWiseXAnalysisTransValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseXAnalysisTransAttr.class);

        /* Create all possible values */
        super.setValue(MoneyWiseXAnalysisTransAttr.INCOME, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseXAnalysisTransAttr.EXPENSE, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     */
    public MoneyWiseXAnalysisTransValues(final MoneyWiseXAnalysisTransValues pSource) {
        /* Initialise class */
        super(pSource);
    }

    @Override
    protected MoneyWiseXAnalysisTransValues newSnapShot() {
        return new MoneyWiseXAnalysisTransValues(this);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseXAnalysisTransValues pBase) {
        /* Adjust income/expense values */
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisTransAttr.INCOME);
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisTransAttr.EXPENSE);
        calculateDelta();
    }

    /**
     * Calculate delta.
     */
    public void calculateDelta() {
        /* Obtain a copy of the value */
        OceanusMoney myDelta = getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME);
        myDelta = new OceanusMoney(myDelta);

        /* Subtract the expense value */
        final OceanusMoney myExpense = getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE);
        myDelta.subtractAmount(myExpense);

        /* Set the delta */
        super.setValue(MoneyWiseXAnalysisTransAttr.PROFIT, myDelta);
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        OceanusMoney myValue = getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME);
        myValue = new OceanusMoney(myValue);
        myValue.setZero();

        /* Reset Income and expense values */
        super.setValue(MoneyWiseXAnalysisTransAttr.INCOME, myValue);
        super.setValue(MoneyWiseXAnalysisTransAttr.EXPENSE, new OceanusMoney(myValue));
        super.setValue(MoneyWiseXAnalysisTransAttr.PROFIT, new OceanusMoney(myValue));
    }

    /**
     * Are the values?
     * @return true/false
     */
    public boolean isActive() {
        final OceanusMoney myIncome = getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME);
        final OceanusMoney myExpense = getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE);
        return myIncome.isNonZero() || myExpense.isNonZero();
    }
}
