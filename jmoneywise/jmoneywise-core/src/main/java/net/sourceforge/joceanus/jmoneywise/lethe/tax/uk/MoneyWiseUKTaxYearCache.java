/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxYearCache;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * UK Tax Year cache.
 */
public class MoneyWiseUKTaxYearCache
        extends MoneyWiseTaxYearCache
        implements MetisDataMap<TethysDate, MoneyWiseUKTaxYear>, MetisDataObjectFormat {
    /**
     * The cache.
     */
    private final Map<TethysDate, MoneyWiseUKTaxYear> theCache;

    /**
     * The date range.
     */
    private final TethysDateRange theDateRange;

    /**
     * Constructor.
     */
    public MoneyWiseUKTaxYearCache() {
        /* Initialise underlying class */
        super(TethysFiscalYear.UK);

        /* Create the map */
        theCache = new LinkedHashMap<>();

        /* Build the map */
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1981);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1982);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1983);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1984);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1985);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1986);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1987);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1988);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1989);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1990);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1991);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1992);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1993);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1994);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1995);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1996);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1997);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1998);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_1999);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2000);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2001);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2002);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2003);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2004);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2005);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2006);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2007);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2008);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2009);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2010);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2011);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2012);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2013);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2014);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2015);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2016);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2017);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2018);
        setTaxYearInCache(MoneyWiseUKTaxYearFactory.YEAR_2019);

        /* Determine the dateRange */
        final TethysDate myEnd = MoneyWiseUKTaxYearFactory.YEAR_2019.getYearEnd();
        TethysDate myStart = MoneyWiseUKTaxYearFactory.YEAR_1981.getYearEnd();
        myStart = new TethysDate(myStart);
        myStart.adjustDay(1);
        myStart.adjustYear(-1);
        theDateRange = new TethysDateRange(myStart, myEnd);
    }

    /**
     * Set into cache.
     * @param pTaxYear the taxYear
     */
    private void setTaxYearInCache(final MoneyWiseUKTaxYear pTaxYear) {
        theCache.put(pTaxYear.getYearEnd(), pTaxYear);
    }

    @Override
    public TethysDateRange getDateRange() {
        return theDateRange;
    }

    @Override
    public MoneyWiseUKTaxYear findTaxYearForRange(final TethysDateRange pRange) {
        return checkTaxYearRange(pRange)
                                         ? findTaxYearForDate(pRange.getEnd())
                                         : null;
    }

    @Override
    public MoneyWiseUKTaxYear findTaxYearForDate(final TethysDate pDate) {
        final TethysDate myDate = getTaxYearDate(pDate);
        return theCache.get(myDate);
    }

    @Override
    public Map<TethysDate, MoneyWiseUKTaxYear> getUnderlyingMap() {
        return theCache;
    }

    @Override
    public String toString() {
        return MoneyWiseUKTaxYearCache.class.getSimpleName();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }
}
