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

import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet.MoneyWiseXTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The taxBands factory.
 */
public final class MoneyWiseXUKTaxBandsFactory {
    /**
     * The 1981-82 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1981_82 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(11250), getRate(30)),
            new MoneyWiseXTaxBand(getAmount(2000), getRate(40)),
            new MoneyWiseXTaxBand(getAmount(3500), getRate(45)),
            new MoneyWiseXTaxBand(getAmount(5500), getRate(50)),
            new MoneyWiseXTaxBand(getAmount(5500), getRate(55)),
            new MoneyWiseXTaxBand(getRate(60))));

    /**
     * The 1983 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1983 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(12800), getRate(30)),
            new MoneyWiseXTaxBand(getAmount(2300), getRate(40)),
            new MoneyWiseXTaxBand(getAmount(4000), getRate(45)),
            new MoneyWiseXTaxBand(getAmount(6200), getRate(50)),
            new MoneyWiseXTaxBand(getAmount(6200), getRate(55)),
            new MoneyWiseXTaxBand(getRate(60))));

    /**
     * The 1984 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1984 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(14600), getRate(30)),
            new MoneyWiseXTaxBand(getAmount(2600), getRate(40)),
            new MoneyWiseXTaxBand(getAmount(4600), getRate(45)),
            new MoneyWiseXTaxBand(getAmount(7100), getRate(50)),
            new MoneyWiseXTaxBand(getAmount(7100), getRate(55)),
            new MoneyWiseXTaxBand(getRate(60))));

    /**
     * The 1985 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1985 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(15400), getRate(30)),
            new MoneyWiseXTaxBand(getAmount(2800), getRate(40)),
            new MoneyWiseXTaxBand(getAmount(4900), getRate(45)),
            new MoneyWiseXTaxBand(getAmount(7500), getRate(50)),
            new MoneyWiseXTaxBand(getAmount(7500), getRate(55)),
            new MoneyWiseXTaxBand(getRate(60))));

    /**
     * The 1986 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1986 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(16200), getRate(30)),
            new MoneyWiseXTaxBand(getAmount(3000), getRate(40)),
            new MoneyWiseXTaxBand(getAmount(5200), getRate(45)),
            new MoneyWiseXTaxBand(getAmount(7900), getRate(50)),
            new MoneyWiseXTaxBand(getAmount(7900), getRate(55)),
            new MoneyWiseXTaxBand(getRate(60))));

    /**
     * The 1987 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1987 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(17200), getRate(29)),
            new MoneyWiseXTaxBand(getAmount(3000), getRate(40)),
            new MoneyWiseXTaxBand(getAmount(5200), getRate(45)),
            new MoneyWiseXTaxBand(getAmount(7900), getRate(50)),
            new MoneyWiseXTaxBand(getAmount(7900), getRate(55)),
            new MoneyWiseXTaxBand(getRate(60))));

    /**
     * The 1988 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1988 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(17900), getRate(27)),
            new MoneyWiseXTaxBand(getAmount(2500), getRate(40)),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(45)),
            new MoneyWiseXTaxBand(getAmount(7900), getRate(50)),
            new MoneyWiseXTaxBand(getAmount(7900), getRate(55)),
            new MoneyWiseXTaxBand(getRate(60))));

    /**
     * The 1989 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1989 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(19300), getRate(25)),
            new MoneyWiseXTaxBand(getRate(40))));

    /**
     * The 1990-91 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1990_91 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(20700), getRate(25)),
            new MoneyWiseXTaxBand(getRate(40))));

    /**
     * The 1992 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1992 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(23700), getRate(25)),
            new MoneyWiseXTaxBand(getRate(40))));

    /**
     * The 1993 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1993 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(2000), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(21700), getRate(25)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 1994 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1994 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(2500), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(21200), getRate(25)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 1995 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1995 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(3000), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(20700), getRate(25)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 1996 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1996 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(3200), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(21100), getRate(25)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 1997 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1997 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(3900), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(21600), getRate(24)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 1998 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1998 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(4100), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(22000), getRate(23)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 1999 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_1999 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(4300), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(22800), getRate(23)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2000 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2000 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(1500), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(26500), getRate(23)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2001 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2001 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(1520), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(26880), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2002 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2002 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(1880), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(27520), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2003 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2003 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(1920), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(27980), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2004 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2004 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(1960), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(28540), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2005 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2005 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(2020), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(29380), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2006 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2006 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(2090), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(30310), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2007 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2007 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(2150), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(31150), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2008 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2008 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(2230), getRate(10)),
            new MoneyWiseXTaxBand(getAmount(32370), getRate(22)),
            new MoneyWiseXTaxBand(getRate(40))), Boolean.TRUE);

    /**
     * The 2009 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2009 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(34800), getRate(20)),
            new MoneyWiseXTaxBand(getRate(40))),
            new MoneyWiseXTaxBand(getAmount(2320), getRate(10)));

    /**
     * The 2010 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2010 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(37400), getRate(20)),
            new MoneyWiseXTaxBand(getRate(40))),
            new MoneyWiseXTaxBand(getAmount(2440), getRate(10)));

    /**
     * The 2011 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2011 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(37400), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(112600), getRate(40)),
            new MoneyWiseXTaxBand(getRate(50))),
            new MoneyWiseXTaxBand(getAmount(2440), getRate(10)));

    /**
     * The 2012 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2012 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(35000), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(115000), getRate(40)),
            new MoneyWiseXTaxBand(getRate(50))),
            new MoneyWiseXTaxBand(getAmount(2560), getRate(10)));

    /**
     * The 2013 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2013 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(34370), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(115630), getRate(40)),
            new MoneyWiseXTaxBand(getRate(50))),
            new MoneyWiseXTaxBand(getAmount(2710), getRate(10)));

    /**
     * The 2014 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2014 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(32010), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(117990), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(10)));

    /**
     * The 2015 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2015 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(31865), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(118135), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(2790), getRate(10)));

    /**
     * The 2016 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2016 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(31765), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(118235), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(0)));

    /**
     * The 2017 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2017 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(32000), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(118000), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(0)));

    /**
     * The 2018 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2018 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(33500), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(116500), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(0)));

    /**
     * The 2019 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2019 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(34500), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(115500), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(0)));

    /**
     * The 2020 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2020 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(37500), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(112500), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(0)));

    /**
     * The 2022 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2022 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(37700), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(112300), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(0)));

    /**
     * The 2024-2028 TaxBands.
     */
    static final MoneyWiseXUKTaxBands BANDS_2024_2028 = new MoneyWiseXUKTaxBands(new MoneyWiseXTaxBandSet(
            new MoneyWiseXTaxBand(getAmount(37700), getRate(20)),
            new MoneyWiseXTaxBand(getAmount(87440), getRate(40)),
            new MoneyWiseXTaxBand(getRate(45))),
            new MoneyWiseXTaxBand(getAmount(5000), getRate(0)));

    /**
     * Private Constructor.
     */
    private MoneyWiseXUKTaxBandsFactory() {
    }

    /**
     * Create a currency amount.
     * @param pUnits the number of whole units
     * @return the amount
     */
    private static TethysMoney getAmount(final int pUnits) {
        return MoneyWiseXUKAllowanceFactory.getAmount(pUnits);
    }

    /**
     * Create a rate.
     * @param pUnits the number of whole units
     * @return the amount
     */
    static TethysRate getRate(final int pUnits) {
        return TethysRate.getWholePercentage(pUnits);
    }
}