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
 * TaxBasisValues class.
 */
public final class MoneyWiseAnalysisTaxBasisValues
        extends MoneyWiseAnalysisValues<MoneyWiseAnalysisTaxBasisValues, MoneyWiseAnalysisTaxBasisAttr> {
    /**
     * Constructor.
     * @param pCurrency the reporting currency
     */
    public MoneyWiseAnalysisTaxBasisValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseAnalysisTaxBasisAttr.class);

        /* Create all possible values */
        super.setValue(MoneyWiseAnalysisTaxBasisAttr.GROSS, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisTaxBasisAttr.NETT, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    private MoneyWiseAnalysisTaxBasisValues(final MoneyWiseAnalysisTaxBasisValues pSource,
                                            final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    protected MoneyWiseAnalysisTaxBasisValues getCounterSnapShot() {
        return new MoneyWiseAnalysisTaxBasisValues(this, true);
    }

    @Override
    protected MoneyWiseAnalysisTaxBasisValues getFullSnapShot() {
        return new MoneyWiseAnalysisTaxBasisValues(this, false);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseAnalysisTaxBasisValues pBase) {
        /* Adjust gross/net/tax values */
        adjustMoneyToBase(pBase, MoneyWiseAnalysisTaxBasisAttr.GROSS);
        adjustMoneyToBase(pBase, MoneyWiseAnalysisTaxBasisAttr.NETT);
        adjustMoneyToBase(pBase, MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT);
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        OceanusMoney myValue = super.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        myValue = new OceanusMoney(myValue);
        myValue.setZero();

        /* Reset Income and expense values */
        super.setValue(MoneyWiseAnalysisTaxBasisAttr.GROSS, myValue);
        super.setValue(MoneyWiseAnalysisTaxBasisAttr.NETT, new OceanusMoney(myValue));
        super.setValue(MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT, new OceanusMoney(myValue));
    }

    /**
     * Are the values?
     * @return true/false
     */
    public boolean isActive() {
        final OceanusMoney myGross = super.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        final OceanusMoney myNet = super.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT);
        final OceanusMoney myTax = super.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT);
        return myGross.isNonZero() || myNet.isNonZero() || myTax.isNonZero();
    }
}
