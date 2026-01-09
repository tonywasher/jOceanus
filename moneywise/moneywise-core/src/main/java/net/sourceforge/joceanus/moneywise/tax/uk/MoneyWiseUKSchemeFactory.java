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
package net.sourceforge.joceanus.moneywise.tax.uk;

import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalAsIncomeScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalFlatRateScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalResidentialScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalSplitRateScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendAdditionalRateScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendAsIncomeScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendBaseRateScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendHigherRateScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendLoHigherRateScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKInterestScheme.MoneyWiseUKInterestAsIncomeScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKInterestScheme.MoneyWiseUKInterestBaseRateScheme;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKInterestScheme.MoneyWiseUKInterestLoBaseRateScheme;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;

/**
 * The scheme factory.
 */
public final class MoneyWiseUKSchemeFactory {
    /**
     * The Dividends asIncome Scheme.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_ASINCOME = new MoneyWiseUKDividendAsIncomeScheme();

    /**
     * The Dividends TaxCredit Scheme.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_TAXCREDIT = new MoneyWiseUKDividendBaseRateScheme(getRate(20));

    /**
     * The Dividends Non-refund-able TaxCredit Scheme.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_FIXEDTAXCREDIT = new MoneyWiseUKDividendBaseRateScheme(getRate(20), false);

    /**
     * The Dividends Low TaxCredit Scheme.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_LOTAXCREDIT = new MoneyWiseUKDividendLoHigherRateScheme(getRate(10), getFractionalRate(325));

    /**
     * The Dividends Low TaxCredit Scheme Mark 2.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_LOTAXCREDIT2 = new MoneyWiseUKDividendHigherRateScheme(getRate(10), getFractionalRate(325));

    /**
     * The Dividends Additional Rate Scheme.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_ADDTAXCREDIT = new MoneyWiseUKDividendAdditionalRateScheme(getRate(10),
            getFractionalRate(325), getFractionalRate(425));

    /**
     * The Dividends Additional Rate Scheme Mark 2.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_ADDTAXCREDIT2 = new MoneyWiseUKDividendAdditionalRateScheme(getRate(10),
            getFractionalRate(325), getFractionalRate(375));

    /**
     * The Dividends Additional Rate Scheme with no TaxCredit.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_NOTAXCREDIT = new MoneyWiseUKDividendAdditionalRateScheme(getFractionalRate(75),
            getFractionalRate(325), getFractionalRate(381), Boolean.TRUE);

    /**
     * The Dividends Additional Rate Scheme with no TaxCredit and new rates.
     */
    static final MoneyWiseUKDividendScheme DIVIDEND_NOTAXCREDIT1 = new MoneyWiseUKDividendAdditionalRateScheme(getTenthFractionalRate(875),
            getTenthFractionalRate(3375), getTenthFractionalRate(3935), Boolean.TRUE);

    /**
     * The Interest asIncome Scheme.
     */
    static final MoneyWiseUKInterestScheme INTEREST_ASINCOME = new MoneyWiseUKInterestAsIncomeScheme();

    /**
     * The Interest base Scheme.
     */
    static final MoneyWiseUKInterestScheme INTEREST_BASE = new MoneyWiseUKInterestBaseRateScheme(getRate(20));

    /**
     * The LowInterest base Scheme.
     */
    static final MoneyWiseUKInterestScheme INTEREST_LOBASE = new MoneyWiseUKInterestLoBaseRateScheme(getRate(20));

    /**
     * The Old Capital Gains Scheme.
     */
    static final MoneyWiseUKCapitalScheme CAPITAL_OLD = new MoneyWiseUKCapitalFlatRateScheme(getRate(30));

    /**
     * The Capital Gains asIncome Scheme.
     */
    static final MoneyWiseUKCapitalScheme CAPITAL_ASINCOME = new MoneyWiseUKCapitalAsIncomeScheme();

    /**
     * The New Capital Gains Scheme.
     */
    static final MoneyWiseUKCapitalScheme CAPITAL_NEW = new MoneyWiseUKCapitalFlatRateScheme(getRate(18));

    /**
     * The Split Capital Gains Scheme.
     */
    static final MoneyWiseUKCapitalScheme CAPITAL_SPLIT = new MoneyWiseUKCapitalSplitRateScheme(getRate(18), getRate(28));

    /**
     * The Residential Capital Gains Scheme.
     */
    static final MoneyWiseUKCapitalScheme CAPITAL_RESIDENTIAL = new MoneyWiseUKCapitalResidentialScheme(getRate(10),
            getRate(20), getRate(18), getRate(28));

    /**
     * The Residential Capital Gains Scheme2.
     */
    static final MoneyWiseUKCapitalScheme CAPITAL_RESIDENTIAL2 = new MoneyWiseUKCapitalResidentialScheme(getRate(10),
            getRate(20), getRate(18), getRate(24));

    /**
     * The Split Capital Gains Scheme2.
     */
    static final MoneyWiseUKCapitalScheme CAPITAL_SPLIT2 = new MoneyWiseUKCapitalSplitRateScheme(getRate(18), getRate(24));

    /**
     * Constructor.
     */
    private MoneyWiseUKSchemeFactory() {
    }

    /**
     * Create a rate.
     * @param pUnits the number of whole units
     * @return the amount
     */
    private static OceanusRate getRate(final int pUnits) {
        return MoneyWiseUKTaxBandsFactory.getRate(pUnits);
    }

    /**
     * Create a fractional rate.
     * @param pUnits the number of whole units
     * @return the amount
     */
    private static OceanusRate getFractionalRate(final int pUnits) {
        return OceanusRate.getWholePermille(pUnits);
    }

    /**
     * Create a fractional rate.
     * @param pUnits the number of tenth units
     * @return the amount
     */
    private static OceanusRate getTenthFractionalRate(final int pUnits) {
        return OceanusRate.getTenthPermille(pUnits);
    }
}
