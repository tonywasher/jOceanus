/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseCapitalScheme.MoneyWiseCapitalAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseCapitalScheme.MoneyWiseCapitalFlatRateScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseCapitalScheme.MoneyWiseCapitalResidentialScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseCapitalScheme.MoneyWiseCapitalSplitRateScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseDividendScheme.MoneyWiseDividendAdditionalRateScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseDividendScheme.MoneyWiseDividendAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseDividendScheme.MoneyWiseDividendBaseRateScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseDividendScheme.MoneyWiseDividendHigherRateScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseInterestScheme.MoneyWiseInterestAsIncomeScheme;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseInterestScheme.MoneyWiseInterestBaseRateScheme;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The scheme factory.
 */
public final class MoneyWiseSchemeFactory {
    /**
     * The Dividends asIncome Scheme.
     */
    protected static final MoneyWiseDividendScheme DIVIDEND_ASINCOME = new MoneyWiseDividendAsIncomeScheme();

    /**
     * The Dividends TaxCredit Scheme.
     */
    protected static final MoneyWiseDividendScheme DIVIDEND_TAXCREDIT = new MoneyWiseDividendBaseRateScheme(getRate(20));

    /**
     * The Dividends Non-refund-able TaxCredit Scheme.
     */
    protected static final MoneyWiseDividendScheme DIVIDEND_FIXEDTAXCREDIT = new MoneyWiseDividendBaseRateScheme(getRate(20), false);

    /**
     * The Dividends Low TaxCredit Scheme.
     */
    protected static final MoneyWiseDividendScheme DIVIDEND_LOTAXCREDIT = new MoneyWiseDividendHigherRateScheme(getRate(10), getRate(325));

    /**
     * The Dividends Additional Rate Scheme.
     */
    protected static final MoneyWiseDividendScheme DIVIDEND_ADDTAXCREDIT = new MoneyWiseDividendAdditionalRateScheme(getRate(10), getFractionalRate(325), getFractionalRate(425));

    /**
     * The Dividends Additional Rate Scheme.
     */
    protected static final MoneyWiseDividendScheme DIVIDEND_ADDTAXCREDIT2 = new MoneyWiseDividendAdditionalRateScheme(getRate(10), getFractionalRate(325), getFractionalRate(375));

    /**
     * The Dividends Additional Rate Scheme.
     */
    protected static final MoneyWiseDividendScheme DIVIDEND_NOTAXCREDIT = new MoneyWiseDividendAdditionalRateScheme(getFractionalRate(75), getFractionalRate(325), getFractionalRate(381));

    /**
     * The Interest asIncome Scheme.
     */
    protected static final MoneyWiseInterestScheme INTEREST_ASINCOME = new MoneyWiseInterestAsIncomeScheme();

    /**
     * The Interest base Scheme.
     */
    protected static final MoneyWiseInterestScheme INTEREST_BASE = new MoneyWiseInterestBaseRateScheme(getRate(20));

    /**
     * The Old Capital Gains Scheme.
     */
    protected static final MoneyWiseCapitalScheme CAPITAL_OLD = new MoneyWiseCapitalFlatRateScheme(getRate(30));

    /**
     * The Capital Gains asIncome Scheme.
     */
    protected static final MoneyWiseCapitalScheme CAPITAL_ASINCOME = new MoneyWiseCapitalAsIncomeScheme();

    /**
     * The New Capital Gains Scheme.
     */
    protected static final MoneyWiseCapitalScheme CAPITAL_NEW = new MoneyWiseCapitalFlatRateScheme(getRate(18));

    /**
     * The Split Capital Gains Scheme.
     */
    protected static final MoneyWiseCapitalScheme CAPITAL_SPLIT = new MoneyWiseCapitalSplitRateScheme(getRate(18), getRate(28));

    /**
     * The Residential Capital Gains Scheme.
     */
    protected static final MoneyWiseCapitalScheme CAPITAL_RESIDENTIAL = new MoneyWiseCapitalResidentialScheme(getRate(10), getRate(20), getRate(18), getRate(28));

    /**
     * Constructor.
     */
    private MoneyWiseSchemeFactory() {
    }

    /**
     * Create a rate.
     * @param pUnits the number of whole units
     * @return the amount
     */
    private static TethysRate getRate(final int pUnits) {
        return MoneyWiseTaxBandsFactory.getRate(pUnits);
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
