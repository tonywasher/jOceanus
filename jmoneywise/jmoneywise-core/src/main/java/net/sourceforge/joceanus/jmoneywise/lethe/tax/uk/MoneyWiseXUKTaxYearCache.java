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

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxYearCache;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * UK Tax Year cache.
 */
public class MoneyWiseXUKTaxYearCache
        extends MoneyWiseXTaxYearCache
        implements MetisDataMap<TethysDate, MoneyWiseXUKTaxYear>, MetisDataObjectFormat {
    /**
     * The cache.
     */
    private final Map<TethysDate, MoneyWiseXUKTaxYear> theCache;

    /**
     * The date range.
     */
    private final TethysDateRange theDateRange;

    /**
     * Constructor.
     */
    public MoneyWiseXUKTaxYearCache() {
        /* Initialise underlying class */
        super(TethysFiscalYear.UK);

        /* Create the map */
        theCache = new LinkedHashMap<>();

        /* Build the map */
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1981);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1982);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1983);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1984);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1985);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1986);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1987);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1988);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1989);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1990);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1991);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1992);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1993);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1994);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1995);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1996);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1997);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1998);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_1999);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2000);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2001);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2002);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2003);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2004);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2005);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2006);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2007);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2008);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2009);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2010);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2011);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2012);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2013);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2014);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2015);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2016);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2017);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2018);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2019);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2020);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2021);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2022);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2023);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2024);
        setTaxYearInCache(MoneyWiseXUKTaxYearFactory.YEAR_2025);

        /* Determine the dateRange */
        final TethysDate myEnd = MoneyWiseXUKTaxYearFactory.YEAR_2025.getYearEnd();
        TethysDate myStart = MoneyWiseXUKTaxYearFactory.YEAR_1981.getYearEnd();
        myStart = new TethysDate(myStart);
        myStart.adjustDay(1);
        myStart.adjustYear(-1);
        theDateRange = new TethysDateRange(myStart, myEnd);
    }

    /**
     * Set into cache.
     * @param pTaxYear the taxYear
     */
    private void setTaxYearInCache(final MoneyWiseXUKTaxYear pTaxYear) {
        theCache.put(pTaxYear.getYearEnd(), pTaxYear);
    }

    @Override
    public TethysDateRange getDateRange() {
        return theDateRange;
    }

    @Override
    public MoneyWiseXUKTaxYear findTaxYearForRange(final TethysDateRange pRange) {
        return checkTaxYearRange(pRange)
                                         ? findTaxYearForDate(pRange.getEnd())
                                         : null;
    }

    @Override
    public MoneyWiseXUKTaxYear findTaxYearForDate(final TethysDate pDate) {
        final TethysDate myDate = getTaxYearDate(pDate);
        return theCache.get(myDate);
    }

    @Override
    public Map<TethysDate, MoneyWiseXUKTaxYear> getUnderlyingMap() {
        return theCache;
    }

    @Override
    public String toString() {
        return MoneyWiseXUKTaxYearCache.class.getSimpleName();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }
}
