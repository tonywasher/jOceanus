/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.coeus.data;

import java.util.Locale;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusFiscalYear;

/**
 * Calendar adjuster.
 */
public class CoeusCalendar {
    /**
     * FiscalYear.
     */
    private final OceanusFiscalYear theFiscalYear;

    /**
     * Use calendar totals.
     */
    private final Boolean makeCalendarTotals;

    /**
     * Constructor.
     * @param pLocale the locale
     * @param pCalendar use calendar totals true/false
     */
    public CoeusCalendar(final Locale pLocale,
                         final Boolean pCalendar) {
        /* Determine the fiscal year */
        theFiscalYear = OceanusFiscalYear.determineFiscalYear(pLocale);
        makeCalendarTotals = pCalendar;
    }

    /**
     * Use Calendar totals?
     * @return true/false
     */
    public Boolean useCalendarTotals() {
        return makeCalendarTotals;
    }

    /**
     * Obtain end of year.
     * @param pDate the date
     * @return the end of the year
     */
    public OceanusDate getEndOfYear(final OceanusDate pDate) {
        /* Determine the end of the year */
        return Boolean.TRUE.equals(makeCalendarTotals)
                                  ? getEndOfCalendarYear(pDate)
                                  : theFiscalYear.endOfYear(pDate);
    }

    /**
     * Obtain end of month.
     * @param pDate the date
     * @return the end of the month
     */
    OceanusDate getEndOfMonth(final OceanusDate pDate) {
        /* Determine the end of the month */
        return Boolean.TRUE.equals(makeCalendarTotals)
                                  ? getEndOfCalendarMonth(pDate)
                                  : theFiscalYear.endOfMonth(pDate);
    }

    /**
     * Obtain start of month.
     * @param pDate the date
     * @return the start of the month
     */
    OceanusDate getStartOfMonth(final OceanusDate pDate) {
        /* Determine the end of the month */
        return Boolean.TRUE.equals(makeCalendarTotals)
               ? getStartOfCalendarMonth(pDate)
               : theFiscalYear.startOfMonth(pDate);
    }

    /**
     * Obtain start of calendar month.
     * @param pDate the date
     * @return the end of the calendar month
     */
    private static OceanusDate getStartOfCalendarMonth(final OceanusDate pDate) {
        /* Determine the start of the calendar month */
        final OceanusDate myDate = new OceanusDate(pDate);
        myDate.startCalendarMonth();
        return myDate;
    }

    /**
     * Obtain end of calendar month.
     * @param pDate the date
     * @return the end of the calendar month
     */
    private static OceanusDate getEndOfCalendarMonth(final OceanusDate pDate) {
        /* Determine the end of the calendar month */
        final OceanusDate myDate = new OceanusDate(pDate);
        myDate.endCalendarMonth();
        return myDate;
    }

    /**
     * Obtain end of calendar month.
     * @param pDate the date
     * @return the end of the calendar month
     */
    private static OceanusDate getEndOfCalendarYear(final OceanusDate pDate) {
        /* Determine the end of the calendar year */
        final OceanusDate myDate = new OceanusDate(pDate);
        myDate.endCalendarYear();
        return myDate;
    }
}
