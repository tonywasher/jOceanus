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
package net.sourceforge.joceanus.moneywise.tax.uk;

import java.util.Currency;
import java.util.Locale;

import net.sourceforge.joceanus.moneywise.tax.MoneyWiseMarginalReduction;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * The allowance factory.
 */
public final class MoneyWiseUKAllowanceFactory {
    /**
     * The Currency.
     */
    private static final Currency CURRENCY = Currency.getInstance(Locale.UK);

    /**
     * The Basic rental allowance.
     */
    private static final OceanusMoney BASE_RENTAL = getAmount(4250);

    /**
     * The New rental allowance.
     */
    private static final OceanusMoney NEW_RENTAL = getAmount(7500);

    /**
     * The Additional Income Threshold.
     */
    private static final OceanusMoney ADDITIONAL_THRESHOLD = getAmount(100000);

    /**
     * The 1981/82 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1981_82 = new MoneyWiseUKAgeAllowance(getAmount(1375), BASE_RENTAL,
            getAmount(3000), getAmount(1820), getAmount(5900));

    /**
     * The 1983 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1983 = new MoneyWiseUKAgeAllowance(getAmount(1565), BASE_RENTAL,
            getAmount(5000), getAmount(2070), getAmount(6700));

    /**
     * The 1984 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1984 = new MoneyWiseUKAgeAllowance(getAmount(1785), BASE_RENTAL,
            getAmount(5300), getAmount(2360), getAmount(7600));

    /**
     * The 1985 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1985 = new MoneyWiseUKAgeAllowance(getAmount(2005), BASE_RENTAL,
            getAmount(5600), getAmount(2490), getAmount(8100));

    /**
     * The 1986 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1986 = new MoneyWiseUKAgeAllowance(getAmount(2205), BASE_RENTAL,
            getAmount(5900), getAmount(2690), getAmount(8800));

    /**
     * The 1987 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1987 = new MoneyWiseUKAgeAllowance(getAmount(2335), BASE_RENTAL,
            getAmount(6300), getAmount(2850), getAmount(9400));

    /**
     * The 1988 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1988 = new MoneyWiseUKAgeAllowance(getAmount(2425), BASE_RENTAL,
            getAmount(6600), getAmount(2960), getAmount(3070), getAmount(9800), MoneyWiseMarginalReduction.TWOINTHREE);

    /**
     * The 1989 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1989 = new MoneyWiseUKAgeAllowance(getAmount(2605), BASE_RENTAL,
            getAmount(5000), getAmount(3180), getAmount(3310), getAmount(10600), MoneyWiseMarginalReduction.TWOINTHREE);

    /**
     * The 1990 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1990 = new MoneyWiseUKAgeAllowance(getAmount(2785), BASE_RENTAL,
            getAmount(5000), getAmount(3400), getAmount(3540), getAmount(11400));

    /**
     * The 1991 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1991 = new MoneyWiseUKAgeAllowance(getAmount(3005), BASE_RENTAL,
            getAmount(5000), getAmount(3670), getAmount(3820), getAmount(12300));

    /**
     * The 1992 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1992 = new MoneyWiseUKAgeAllowance(getAmount(3295), BASE_RENTAL,
            getAmount(5500), getAmount(4020), getAmount(4180), getAmount(13500));

    /**
     * The 1993-95 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1993_95 = new MoneyWiseUKAgeAllowance(getAmount(3445), BASE_RENTAL,
            getAmount(5800), getAmount(4200), getAmount(4370), getAmount(14200));

    /**
     * The 1996 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1996 = new MoneyWiseUKAgeAllowance(getAmount(3525), BASE_RENTAL,
            getAmount(6000), getAmount(4630), getAmount(4800), getAmount(14600));

    /**
     * The 1997 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1997 = new MoneyWiseUKAgeAllowance(getAmount(3765), BASE_RENTAL,
            getAmount(6300), getAmount(4910), getAmount(5090), getAmount(15200));

    /**
     * The 1998 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1998 = new MoneyWiseUKAgeAllowance(getAmount(4045), BASE_RENTAL,
            getAmount(6500), getAmount(5220), getAmount(5400), getAmount(15600));

    /**
     * The 1999 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_1999 = new MoneyWiseUKAgeAllowance(getAmount(4195), BASE_RENTAL,
            getAmount(6800), getAmount(5410), getAmount(5600), getAmount(16200));

    /**
     * The 2000 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2000 = new MoneyWiseUKAgeAllowance(getAmount(4335), BASE_RENTAL,
            getAmount(7100), getAmount(5720), getAmount(5980), getAmount(16800));

    /**
     * The 2001 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2001 = new MoneyWiseUKAgeAllowance(getAmount(4385), BASE_RENTAL,
            getAmount(7200), getAmount(5790), getAmount(6050), getAmount(17000));

    /**
     * The 2002 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2002 = new MoneyWiseUKAgeAllowance(getAmount(4535), BASE_RENTAL,
            getAmount(7500), getAmount(5990), getAmount(6260), getAmount(17600));

    /**
     * The 2003 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2003 = new MoneyWiseUKAgeAllowance(getAmount(4615), BASE_RENTAL,
            getAmount(7700), getAmount(6100), getAmount(6370), getAmount(17900));

    /**
     * The 2004 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2004 = new MoneyWiseUKAgeAllowance(getAmount(4615), BASE_RENTAL,
            getAmount(7900), getAmount(6610), getAmount(6720), getAmount(18200));

    /**
     * The 2005 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2005 = new MoneyWiseUKAgeAllowance(getAmount(4745), BASE_RENTAL,
            getAmount(8200), getAmount(6830), getAmount(6950), getAmount(18900));

    /**
     * The 2006 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2006 = new MoneyWiseUKAgeAllowance(getAmount(4895), BASE_RENTAL,
            getAmount(8500), getAmount(7090), getAmount(7220), getAmount(19500));

    /**
     * The 2007 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2007 = new MoneyWiseUKAgeAllowance(getAmount(5035), BASE_RENTAL,
            getAmount(8800), getAmount(7280), getAmount(7420), getAmount(20100));

    /**
     * The 2008 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2008 = new MoneyWiseUKAgeAllowance(getAmount(5225), BASE_RENTAL,
            getAmount(9200), getAmount(7550), getAmount(7690), getAmount(20900));

    /**
     * The 2009 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2009 = new MoneyWiseUKAgeAllowance(getAmount(6035), BASE_RENTAL,
            getAmount(9600), getAmount(9030), getAmount(9180), getAmount(21800));

    /**
     * The 2010 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2010 = new MoneyWiseUKAgeAllowance(getAmount(6475), BASE_RENTAL,
            getAmount(10100), getAmount(9490), getAmount(9640), getAmount(22900));

    /**
     * The 2011 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2011 = new MoneyWiseUKAdditionalAllowance(getAmount(6475), BASE_RENTAL,
            getAmount(10100), getAmount(9490), getAmount(9640), getAmount(22900), ADDITIONAL_THRESHOLD);

    /**
     * The 2012 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2012 = new MoneyWiseUKAdditionalAllowance(getAmount(7475), BASE_RENTAL,
            getAmount(10600), getAmount(9940), getAmount(10090), getAmount(24000), ADDITIONAL_THRESHOLD);

    /**
     * The 2013 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2013 = new MoneyWiseUKAdditionalAllowance(getAmount(8105), BASE_RENTAL,
            getAmount(10600), getAmount(10500), getAmount(10660), getAmount(25400), ADDITIONAL_THRESHOLD);

    /**
     * The 2014 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2014 = new MoneyWiseUKAdditionalAllowance(getAmount(9440), BASE_RENTAL,
            getAmount(10900), getAmount(10500), getAmount(10660), getAmount(26100), ADDITIONAL_THRESHOLD);

    /**
     * The 2015 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2015 = new MoneyWiseUKAdditionalAllowance(getAmount(10000), BASE_RENTAL,
            getAmount(11000), getAmount(10500), getAmount(10660), getAmount(27000), ADDITIONAL_THRESHOLD);

    /**
     * The 2016 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2016 = new MoneyWiseUKAdditionalAllowance(getAmount(10600), BASE_RENTAL,
            getAmount(11100), getAmount(10600), getAmount(10660), getAmount(27700), ADDITIONAL_THRESHOLD);

    /**
     * The 2017 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2017 = new MoneyWiseUKSavingsAllowance(getAmount(11000), NEW_RENTAL,
            getAmount(11100), getAmount(1000), getAmount(5000), ADDITIONAL_THRESHOLD);

    /**
     * The 2018 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2018 = new MoneyWiseUKSavingsAllowance(getAmount(11500), NEW_RENTAL,
            getAmount(11300), getAmount(1000), getAmount(5000), ADDITIONAL_THRESHOLD);

    /**
     * The 2019 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2019 = new MoneyWiseUKSavingsAllowance(getAmount(11850), NEW_RENTAL,
            getAmount(11700), getAmount(1000), getAmount(2000), ADDITIONAL_THRESHOLD);

    /**
     * The 2020 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2020 = new MoneyWiseUKSavingsAllowance(getAmount(12500), NEW_RENTAL,
            getAmount(12000), getAmount(1000), getAmount(2000), ADDITIONAL_THRESHOLD);

    /**
     * The 2021 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2021 = new MoneyWiseUKSavingsAllowance(getAmount(12500), NEW_RENTAL,
            getAmount(12300), getAmount(1000), getAmount(2000), ADDITIONAL_THRESHOLD);

    /**
     * The 2022 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2022 = new MoneyWiseUKSavingsAllowance(getAmount(12500), NEW_RENTAL,
            getAmount(12300), getAmount(1000), getAmount(2000), ADDITIONAL_THRESHOLD);

    /**
     * The 2023 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2023 = new MoneyWiseUKSavingsAllowance(getAmount(12570), NEW_RENTAL,
            getAmount(12300), getAmount(1000), getAmount(2000), ADDITIONAL_THRESHOLD);

    /**
     * The 2024 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2024 = new MoneyWiseUKSavingsAllowance(getAmount(12570), NEW_RENTAL,
            getAmount(6000), getAmount(1000), getAmount(1000), ADDITIONAL_THRESHOLD);

    /**
     * The 2025 Allowance.
     */
    static final MoneyWiseUKBasicAllowance ALLOWANCE_2025 = new MoneyWiseUKSavingsAllowance(getAmount(12570), NEW_RENTAL,
            getAmount(3000), getAmount(1000), getAmount(500), ADDITIONAL_THRESHOLD);

    /**
     * Constructor.
     */
    private MoneyWiseUKAllowanceFactory() {
    }

    /**
     * Create a currency amount.
     * @param pUnits the number of whole units
     * @return the amount
     */
    protected static OceanusMoney getAmount(final int pUnits) {
        return OceanusMoney.getWholeUnits(pUnits, CURRENCY);
    }
}
