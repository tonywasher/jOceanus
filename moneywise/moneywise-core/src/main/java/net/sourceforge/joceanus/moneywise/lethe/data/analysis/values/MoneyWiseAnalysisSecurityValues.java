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
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;

/**
 * SecurityValues class.
 */
public final class MoneyWiseAnalysisSecurityValues
        extends MoneyWiseAnalysisValues<MoneyWiseAnalysisSecurityValues, MoneyWiseAnalysisSecurityAttr> {
    /**
     * Constructor.
     * @param pCurrency the account currency
     */
    public MoneyWiseAnalysisSecurityValues(final Currency pCurrency) {
        /* Initialise class */
        super(MoneyWiseAnalysisSecurityAttr.class);

        /* Initialise units etc. to zero */
        super.setValue(MoneyWiseAnalysisSecurityAttr.UNITS, new OceanusUnits());
        super.setValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisSecurityAttr.INVESTED, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, new OceanusMoney(pCurrency));
        super.setValue(MoneyWiseAnalysisSecurityAttr.DIVIDEND, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pCurrency the account currency
     * @param pReportingCurrency the reporting currency
     */
    public MoneyWiseAnalysisSecurityValues(final Currency pCurrency,
                                           final Currency pReportingCurrency) {
        /* Initialise class */
        this(pReportingCurrency);

        /* Initialise additional values to zero */
        super.setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, new OceanusMoney(pCurrency));
    }

    /**
     * Constructor.
     * @param pSource the source map.
     * @param pCountersOnly only copy counters
     */
    public MoneyWiseAnalysisSecurityValues(final MoneyWiseAnalysisSecurityValues pSource,
                                           final boolean pCountersOnly) {
        /* Initialise class */
        super(pSource, pCountersOnly);
    }

    @Override
    protected MoneyWiseAnalysisSecurityValues getCounterSnapShot() {
        return new MoneyWiseAnalysisSecurityValues(this, true);
    }

    @Override
    protected MoneyWiseAnalysisSecurityValues getFullSnapShot() {
        return new MoneyWiseAnalysisSecurityValues(this, false);
    }

    /**
     * Is this a foreign security?
     * @return true/false
     */
    private boolean isForeignSecurity() {
        return getValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED) != null;
    }

    @Override
    public void adjustToBaseValues(final MoneyWiseAnalysisSecurityValues pBase) {
        /* Adjust invested/gains values */
        adjustMoneyToBase(pBase, MoneyWiseAnalysisSecurityAttr.INVESTED);
        adjustMoneyToBase(pBase, MoneyWiseAnalysisSecurityAttr.REALISEDGAINS);
        adjustMoneyToBase(pBase, MoneyWiseAnalysisSecurityAttr.GROWTHADJUST);
        adjustMoneyToBase(pBase, MoneyWiseAnalysisSecurityAttr.DIVIDEND);

        /* If we are a foreign security */
        if (isForeignSecurity()) {
            adjustMoneyToBase(pBase, MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED);
        }
    }

    @Override
    public void resetBaseValues() {
        /* Create a zero value in the correct currency */
        OceanusMoney myValue = getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        myValue = new OceanusMoney(myValue);
        myValue.setZero();

        /* Reset Growth Adjust values */
        super.setValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myValue);
        super.setValue(MoneyWiseAnalysisSecurityAttr.INVESTED, new OceanusMoney(myValue));
        super.setValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, new OceanusMoney(myValue));
        super.setValue(MoneyWiseAnalysisSecurityAttr.DIVIDEND, new OceanusMoney(myValue));

        /* If we are a foreign security */
        if (isForeignSecurity()) {
            /* Create a zero value in the correct currency */
            myValue = getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED);
            myValue = new OceanusMoney(myValue);
            myValue.setZero();

            /* Reset Invested values */
            super.setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myValue);
        }
    }

    /**
     * Are the values active?
     * @return true/false
     */
    public boolean isActive() {
        final OceanusUnits myUnits = getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        return myUnits != null && myUnits.isNonZero();
    }
}


