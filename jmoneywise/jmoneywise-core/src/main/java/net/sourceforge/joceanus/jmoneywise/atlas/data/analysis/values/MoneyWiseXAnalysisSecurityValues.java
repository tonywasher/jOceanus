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
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * SecurityValues class.
 */
public final class MoneyWiseXAnalysisSecurityValues
        extends MoneyWiseXAnalysisValues<MoneyWiseXAnalysisSecurityValues, MoneyWiseXAnalysisSecurityAttr> {
    /**
     * Constructor.
     * @param pCurrency the account currency
     * @param pReportingCurrency the reporting currency
     */
    public MoneyWiseXAnalysisSecurityValues(final Currency pCurrency,
                                            final Currency pReportingCurrency) {
        /* Initialise class */
        super(MoneyWiseXAnalysisSecurityAttr.class);

        /* Initialise units etc. to zero */
        setValue(MoneyWiseXAnalysisSecurityAttr.UNITS, new TethysUnits());
        setValue(MoneyWiseXAnalysisSecurityAttr.VALUE, new TethysMoney(pCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST, new TethysMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, new TethysMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS, new TethysMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND, new TethysMoney(pReportingCurrency));
        setValue(MoneyWiseXAnalysisSecurityAttr.FUNDED, new TethysMoney(pReportingCurrency));
    }

    /**
     * Constructor.
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
        TethysMoney myValue = getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST);
        myValue = new TethysMoney(myValue);
        myValue.setZero();

        /* Reset Growth Adjust values */
        setValue(MoneyWiseXAnalysisSecurityAttr.UNREALISEDGAINS, myValue);
        setValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS, new TethysMoney(myValue));
        setValue(MoneyWiseXAnalysisSecurityAttr.DIVIDEND, new TethysMoney(myValue));
    }

    /**
     * Set zero money.
     * @param pAttr the attribute
     */
    public void setZeroMoney(final MoneyWiseXAnalysisSecurityAttr pAttr) {
        TethysMoney myValue = getMoneyValue(pAttr);
        myValue = new TethysMoney(myValue);
        myValue.setZero();
        setValue(pAttr, myValue);
    }

    /**
     * Set zero money.
     * @param pAttr the attribute
     */
    public void setZeroUnits(final MoneyWiseXAnalysisSecurityAttr pAttr) {
         setValue(pAttr, new TethysUnits());
    }

    /**
     * Are the values active?
     * @return true/false
     */
    public boolean isActive() {
        final TethysUnits myUnits = getUnitsValue(MoneyWiseXAnalysisSecurityAttr.UNITS);
        return myUnits != null && myUnits.isNonZero();
    }
}
