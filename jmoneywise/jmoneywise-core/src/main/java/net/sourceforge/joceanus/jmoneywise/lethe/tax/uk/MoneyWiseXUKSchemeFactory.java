/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKCapitalScheme.MoneyWiseXUKCapitalAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKCapitalScheme.MoneyWiseXUKCapitalFlatRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKCapitalScheme.MoneyWiseXUKCapitalResidentialScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKCapitalScheme.MoneyWiseXUKCapitalSplitRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKDividendScheme.MoneyWiseXUKDividendAdditionalRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKDividendScheme.MoneyWiseXUKDividendAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKDividendScheme.MoneyWiseXUKDividendBaseRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKDividendScheme.MoneyWiseXUKDividendHigherRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKDividendScheme.MoneyWiseXUKDividendLoHigherRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKInterestScheme.MoneyWiseXUKInterestAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKInterestScheme.MoneyWiseXUKInterestBaseRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKInterestScheme.MoneyWiseXUKInterestLoBaseRateScheme;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The scheme factory.
 */
public final class MoneyWiseXUKSchemeFactory {
    /**
     * The Dividends asIncome Scheme.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_ASINCOME = new MoneyWiseXUKDividendAsIncomeScheme();

    /**
     * The Dividends TaxCredit Scheme.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_TAXCREDIT = new MoneyWiseXUKDividendBaseRateScheme(getRate(20));

    /**
     * The Dividends Non-refund-able TaxCredit Scheme.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_FIXEDTAXCREDIT = new MoneyWiseXUKDividendBaseRateScheme(getRate(20), false);

    /**
     * The Dividends Low TaxCredit Scheme.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_LOTAXCREDIT = new MoneyWiseXUKDividendLoHigherRateScheme(getRate(10), getFractionalRate(325));

    /**
     * The Dividends Low TaxCredit Scheme Mark 2.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_LOTAXCREDIT2 = new MoneyWiseXUKDividendHigherRateScheme(getRate(10), getFractionalRate(325));

    /**
     * The Dividends Additional Rate Scheme.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_ADDTAXCREDIT = new MoneyWiseXUKDividendAdditionalRateScheme(getRate(10),
            getFractionalRate(325), getFractionalRate(425));

    /**
     * The Dividends Additional Rate Scheme Mark 2.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_ADDTAXCREDIT2 = new MoneyWiseXUKDividendAdditionalRateScheme(getRate(10),
            getFractionalRate(325), getFractionalRate(375));

    /**
     * The Dividends Additional Rate Scheme with no TaxCredit.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_NOTAXCREDIT = new MoneyWiseXUKDividendAdditionalRateScheme(getFractionalRate(75),
            getFractionalRate(325), getFractionalRate(381), Boolean.TRUE);

    /**
     * The Dividends Additional Rate Scheme with no TaxCredit and new rates.
     */
    protected static final MoneyWiseXUKDividendScheme DIVIDEND_NOTAXCREDIT1 = new MoneyWiseXUKDividendAdditionalRateScheme(getTenthFractionalRate(875),
            getTenthFractionalRate(3375), getTenthFractionalRate(3935), Boolean.TRUE);

    /**
     * The Interest asIncome Scheme.
     */
    protected static final MoneyWiseXUKInterestScheme INTEREST_ASINCOME = new MoneyWiseXUKInterestAsIncomeScheme();

    /**
     * The Interest base Scheme.
     */
    protected static final MoneyWiseXUKInterestScheme INTEREST_BASE = new MoneyWiseXUKInterestBaseRateScheme(getRate(20));

    /**
     * The LowInterest base Scheme.
     */
    protected static final MoneyWiseXUKInterestScheme INTEREST_LOBASE = new MoneyWiseXUKInterestLoBaseRateScheme(getRate(20));

    /**
     * The Old Capital Gains Scheme.
     */
    protected static final MoneyWiseXUKCapitalScheme CAPITAL_OLD = new MoneyWiseXUKCapitalFlatRateScheme(getRate(30));

    /**
     * The Capital Gains asIncome Scheme.
     */
    protected static final MoneyWiseXUKCapitalScheme CAPITAL_ASINCOME = new MoneyWiseXUKCapitalAsIncomeScheme();

    /**
     * The New Capital Gains Scheme.
     */
    protected static final MoneyWiseXUKCapitalScheme CAPITAL_NEW = new MoneyWiseXUKCapitalFlatRateScheme(getRate(18));

    /**
     * The Split Capital Gains Scheme.
     */
    protected static final MoneyWiseXUKCapitalScheme CAPITAL_SPLIT = new MoneyWiseXUKCapitalSplitRateScheme(getRate(18), getRate(28));

    /**
     * The Residential Capital Gains Scheme.
     */
    protected static final MoneyWiseXUKCapitalScheme CAPITAL_RESIDENTIAL = new MoneyWiseXUKCapitalResidentialScheme(getRate(10),
            getRate(20), getRate(18), getRate(28));

    /**
     * Constructor.
     */
    private MoneyWiseXUKSchemeFactory() {
    }

    /**
     * Create a rate.
     * @param pUnits the number of whole units
     * @return the amount
     */
    private static TethysRate getRate(final int pUnits) {
        return MoneyWiseXUKTaxBandsFactory.getRate(pUnits);
    }

    /**
     * Create a fractional rate.
     * @param pUnits the number of whole units
     * @return the amount
     */
    private static TethysRate getFractionalRate(final int pUnits) {
        return TethysRate.getWholePermille(pUnits);
    }

    /**
     * Create a fractional rate.
     * @param pUnits the number of tenth units
     * @return the amount
     */
    private static TethysRate getTenthFractionalRate(final int pUnits) {
        return TethysRate.getTenthPermille(pUnits);
    }
}
