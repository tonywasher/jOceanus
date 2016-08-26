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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * Tax Year cache.
 */
public class MoneyWiseTaxYearCache {
    /**
     * The cache.
     */
    private final Map<TethysDate, MoneyWiseTaxYear> theCache;

    /**
     * Constructor.
     */
    protected MoneyWiseTaxYearCache() {
        /* Create the map */
        theCache = new HashMap<>();

        /* Build the map */
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1981);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1982);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1983);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1984);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1985);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1986);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1987);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1988);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1989);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1990);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1991);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1992);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1993);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1994);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1995);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1996);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1997);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1998);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_1999);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2000);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2001);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2002);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2003);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2004);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2005);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2006);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2007);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2008);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2009);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2010);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2011);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2012);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2013);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2014);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2015);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2016);
        setTaxYearInCache(MoneyWiseTaxYearFactory.YEAR_2017);
    }

    /**
     * Set into cache.
     * @param pTaxYear the taxYear
     */
    private void setTaxYearInCache(final MoneyWiseTaxYear pTaxYear) {
        theCache.put(pTaxYear.getYear(), pTaxYear);
    }

    /**
     * Obtain the taxYear for the date.
     * @param pDate the date
     * @return the amount
     */
    public MoneyWiseTaxYear getTaxYearForDate(final TethysDate pDate) {
        TethysDate myDate = TethysFiscalYear.UK.endOfYear(pDate);
        return theCache.get(myDate);
    }
}
