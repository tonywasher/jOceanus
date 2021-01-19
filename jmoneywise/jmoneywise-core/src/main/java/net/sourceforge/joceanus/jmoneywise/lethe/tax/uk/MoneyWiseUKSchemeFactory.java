/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalFlatRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalResidentialScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKCapitalScheme.MoneyWiseUKCapitalSplitRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendAdditionalRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendBaseRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendHigherRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKDividendScheme.MoneyWiseUKDividendLoHigherRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKInterestScheme.MoneyWiseUKInterestAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKInterestScheme.MoneyWiseUKInterestBaseRateScheme;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKInterestScheme.MoneyWiseUKInterestLoBaseRateScheme;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The scheme factory.
 */
public final class MoneyWiseUKSchemeFactory {
    /**
     * The Dividends asIncome Scheme.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_ASINCOME = new MoneyWiseUKDividendAsIncomeScheme();

    /**
     * The Dividends TaxCredit Scheme.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_TAXCREDIT = new MoneyWiseUKDividendBaseRateScheme(getRate(20));

    /**
     * The Dividends Non-refund-able TaxCredit Scheme.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_FIXEDTAXCREDIT = new MoneyWiseUKDividendBaseRateScheme(getRate(20), false);

    /**
     * The Dividends Low TaxCredit Scheme.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_LOTAXCREDIT = new MoneyWiseUKDividendLoHigherRateScheme(getRate(10), getFractionalRate(325));

    /**
     * The Dividends Low TaxCredit Scheme Mark 2.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_LOTAXCREDIT2 = new MoneyWiseUKDividendHigherRateScheme(getRate(10), getFractionalRate(325));

    /**
     * The Dividends Additional Rate Scheme.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_ADDTAXCREDIT = new MoneyWiseUKDividendAdditionalRateScheme(getRate(10), getFractionalRate(325), getFractionalRate(425));

    /**
     * The Dividends Additional Rate Scheme Mark 2.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_ADDTAXCREDIT2 = new MoneyWiseUKDividendAdditionalRateScheme(getRate(10), getFractionalRate(325), getFractionalRate(375));

    /**
     * The Dividends Additional Rate Scheme with no TaxCredit.
     */
    protected static final MoneyWiseUKDividendScheme DIVIDEND_NOTAXCREDIT = new MoneyWiseUKDividendAdditionalRateScheme(getFractionalRate(75), getFractionalRate(325), getFractionalRate(381),
            Boolean.TRUE);

    /**
     * The Interest asIncome Scheme.
     */
    protected static final MoneyWiseUKInterestScheme INTEREST_ASINCOME = new MoneyWiseUKInterestAsIncomeScheme();

    /**
     * The Interest base Scheme.
     */
    protected static final MoneyWiseUKInterestScheme INTEREST_BASE = new MoneyWiseUKInterestBaseRateScheme(getRate(20));

    /**
     * The LowInterest base Scheme.
     */
    protected static final MoneyWiseUKInterestScheme INTEREST_LOBASE = new MoneyWiseUKInterestLoBaseRateScheme(getRate(20));

    /**
     * The Old Capital Gains Scheme.
     */
    protected static final MoneyWiseUKCapitalScheme CAPITAL_OLD = new MoneyWiseUKCapitalFlatRateScheme(getRate(30));

    /**
     * The Capital Gains asIncome Scheme.
     */
    protected static final MoneyWiseUKCapitalScheme CAPITAL_ASINCOME = new MoneyWiseUKCapitalAsIncomeScheme();

    /**
     * The New Capital Gains Scheme.
     */
    protected static final MoneyWiseUKCapitalScheme CAPITAL_NEW = new MoneyWiseUKCapitalFlatRateScheme(getRate(18));

    /**
     * The Split Capital Gains Scheme.
     */
    protected static final MoneyWiseUKCapitalScheme CAPITAL_SPLIT = new MoneyWiseUKCapitalSplitRateScheme(getRate(18), getRate(28));

    /**
     * The Residential Capital Gains Scheme.
     */
    protected static final MoneyWiseUKCapitalScheme CAPITAL_RESIDENTIAL = new MoneyWiseUKCapitalResidentialScheme(getRate(10), getRate(20), getRate(18), getRate(28));

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
    private static TethysRate getRate(final int pUnits) {
        return MoneyWiseUKTaxBandsFactory.getRate(pUnits);
    }

    /**
     * Create a fractional rate.
     * @param pUnits the number of whole units
     * @return the amount
     */
    private static TethysRate getFractionalRate(final int pUnits) {
        return TethysRate.getWholePermille(pUnits);
    }
}
