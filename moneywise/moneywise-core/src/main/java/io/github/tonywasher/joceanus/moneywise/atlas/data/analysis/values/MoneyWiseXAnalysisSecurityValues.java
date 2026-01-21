/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisValues;

import java.util.Currency;

/**
 * SecurityValues class.
 */
public final class MoneyWiseXAnalysisSecurityValues
        extends MoneyWiseXAnalysisValues<MoneyWiseXAnalysisSecurityValues, MoneyWiseXAnalysisSecurityAttr> {
    /**
     * Constructor.
     *
     * @param pCurrency the account currency
     */
    public MoneyWiseXAnalysisSecurityValues(final Currency pCurrency) {
        this(pCurrency, pCurrency);
    }

    /**
     * Constructor.
     *
     * @param pCurrency          the account currency
     * @param pReportingCurrency the reporting currency
     */
    public MoneyWiseXAnalysisSecurityValues(final Currency pCurrency,
                                            final Currency pReportingCurrency) {
        /* Initialise class */
        super(MoneyWiseXAnalysisSecurityAttr.class);

        /* Initialise units etc. to zero */
        setValue(MoneyWiseXAnalysisSecurityAttr.UNITS, new OceanusUnits());
        setValue(MoneyWiseXAnalysisSecurityAttr.VALUE, new OceanusMoney(pCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.VALUATION, new OceanusMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, new OceanusMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, new OceanusMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS, new OceanusMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.GAINSADJUST, new OceanusMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND, new OceanusMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.FUNDED, new OceanusMoney(pReportingCurrency));
    }

    /**
     * Constructor.
     *
     * @param pSource the source map.
     */
    public MoneyWiseXAnalysisSecurityValues(final MoneyWiseXAnalysisSecurityValues pSource) {
        /* Initialise class */
        super(pSource);
    }

    @Override
    protected MoneyWiseXAnalysisSecurityValues newSnapShot() {
        return new MoneyWiseXAnalysisSecurityValues(this);
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseXAnalysisSecurityValues pBase) {
        /* Adjust invested/gains values */
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS);
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS);
        adjustMoneyToBase(pBase, MoneyWiseXAnalysisSecurityAttr.DIVIDEND);
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        OceanusMoney myValue = getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        myValue = new OceanusMoney(myValue);
        myValue.setZero();

        /* Reset Growth Adjust values */
        setValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS, myValue);
        setValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, new OceanusMoney(myValue));
        setValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND, new OceanusMoney(myValue));
    }

    /**
     * Set zero money.
     *
     * @param pAttr the attribute
     */
    public void setZeroMoney(final MoneyWiseXAnalysisSecurityAttr pAttr) {
        OceanusMoney myValue = getMoneyValue(pAttr);
        myValue = new OceanusMoney(myValue);
        myValue.setZero();
        setValue(pAttr, myValue);
    }

    /**
     * Set zero money.
     *
     * @param pAttr the attribute
     */
    public void setZeroUnits(final MoneyWiseXAnalysisSecurityAttr pAttr) {
        setValue(pAttr, new OceanusUnits());
    }

    /**
     * Are the values active?
     *
     * @return true/false
     */
    public boolean isActive() {
        final OceanusUnits myUnits = getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        return myUnits != null && myUnits.isNonZero();
    }
}
