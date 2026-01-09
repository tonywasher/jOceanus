/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
 * TaxBasisValues class.
 */
public final class MoneyWiseXAnalysisTaxBasisValues
        extends MoneyWiseXAnalysisValues<MoneyWiseXAnalysisTaxBasisValues, MoneyWiseXAnalysisTaxBasisAttr> {
    /**
     * Constructor.
     * @param pCurrency the reporting currency
     */
    public MoneyWiseXAnalysisTaxBasisValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseXAnalysisTaxBasisAttr.class);

        /* Create all possible values */
        super.setValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseXAnalysisTaxBasisAttr.NETT, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseXAnalysisTaxBasisAttr.TAXCREDIT, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     */
    private MoneyWiseXAnalysisTaxBasisValues(final MoneyWiseXAnalysisTaxBasisValues pSource) {
        /* Initialise class */
        super(pSource);
    }

    @Override
    protected MoneyWiseXAnalysisTaxBasisValues newSnapShot() {
        return new MoneyWiseXAnalysisTaxBasisValues(this);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseXAnalysisTaxBasisValues pBase) {
        /* Adjust gross/net/tax values */
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisTaxBasisAttr.NETT);
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisTaxBasisAttr.TAXCREDIT);
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        OceanusMoney myValue = super.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        myValue = new OceanusMoney(myValue);
        myValue.setZero();

        /* Reset Income and expense values */
        super.setValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS, myValue);
        super.setValue(MoneyWiseXAnalysisTaxBasisAttr.NETT, new OceanusMoney(myValue));
        super.setValue(MoneyWiseXAnalysisTaxBasisAttr.TAXCREDIT, new OceanusMoney(myValue));
    }

    /**
     * Are the values?
     * @return true/false
     */
    public boolean isActive() {
        final OceanusMoney myGross = super.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        final OceanusMoney myNet = super.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT);
        final OceanusMoney myTax = super.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.TAXCREDIT);
        return myGross.isNonZero() || myNet.isNonZero() || myTax.isNonZero();
    }
}
