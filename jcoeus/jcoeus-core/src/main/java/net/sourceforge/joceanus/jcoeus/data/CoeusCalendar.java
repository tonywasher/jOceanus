/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data;

import java.util.Locale;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * Calendar adjuster.
 */
public class CoeusCalendar {
    /**
     * FiscalYear.
     */
    private final TethysFiscalYear theFiscalYear;

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
        theFiscalYear = TethysFiscalYear.determineFiscalYear(pLocale);
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
    public TethysDate getEndOfYear(final TethysDate pDate) {
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
    TethysDate getEndOfMonth(final TethysDate pDate) {
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
    TethysDate getStartOfMonth(final TethysDate pDate) {
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
    private static TethysDate getStartOfCalendarMonth(final TethysDate pDate) {
        /* Determine the start of the calendar month */
        final TethysDate myDate = new TethysDate(pDate);
        myDate.startCalendarMonth();
        return myDate;
    }

    /**
     * Obtain end of calendar month.
     * @param pDate the date
     * @return the end of the calendar month
     */
    private static TethysDate getEndOfCalendarMonth(final TethysDate pDate) {
        /* Determine the end of the calendar month */
        final TethysDate myDate = new TethysDate(pDate);
        myDate.endCalendarMonth();
        return myDate;
    }

    /**
     * Obtain end of calendar month.
     * @param pDate the date
     * @return the end of the calendar month
     */
    private static TethysDate getEndOfCalendarYear(final TethysDate pDate) {
        /* Determine the end of the calendar year */
        final TethysDate myDate = new TethysDate(pDate);
        myDate.endCalendarYear();
        return myDate;
    }
}
