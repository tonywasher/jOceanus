/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.oceanus.date;

import java.time.Month;
import java.util.Locale;

/**
 * Fiscal Year representation.
 */
public enum OceanusFiscalYear {
    /**
     * Fiscal Year based on UK Model.
     */
    UK(6, Month.APRIL),

    /**
     * March based start of year.
     */
    MARCH(Month.MARCH),

    /**
     * April based start of year.
     */
    APRIL(Month.APRIL),

    /**
     * July based start of year.
     */
    JULY(Month.JULY),

    /**
     * October based start of year.
     */
    OCTOBER(Month.OCTOBER),

    /**
     * Fiscal year based on the CalendarYear.
     */
    CALENDAR;

    /**
     * The day of the first day of fiscal year.
     */
    private final int theDay;

    /**
     * The month of the first day of fiscal year.
     */
    private final transient Month theMonth;

    /**
     * Constructor.
     */
    OceanusFiscalYear() {
        this(Month.JANUARY);
    }

    /**
     * Constructor.
     * @param pMonth the first month of fiscal year
     */
    OceanusFiscalYear(final Month pMonth) {
        this(1, pMonth);
    }

    /**
     * Constructor.
     * @param pDay the first day of fiscal year
     * @param pMonth the first month of fiscal year
     */
    OceanusFiscalYear(final int pDay,
                      final Month pMonth) {
        theDay = pDay;
        theMonth = pMonth;
    }

    /**
     * Obtain the day.
     * @return the day
     */
    public int getFirstDay() {
        return theDay;
    }

    /**
     * Obtain the month.
     * @return the month
     */
    public Month getFirstMonth() {
        return theMonth;
    }

    /**
     * Determine Fiscal Year for locale.
     * @param pLocale the locale
     * @return the fiscal year type
     */
    public static OceanusFiscalYear determineFiscalYear(final Locale pLocale) {
        switch (pLocale.getCountry()) {
            case "GB":
                return UK;
            case "ZA":
                return MARCH;
            case "NZ":
                return APRIL;
            case "AU":
            case "EG":
                return JULY;
            case "CR":
                return OCTOBER;
            default:
                return CALENDAR;
        }
    }

    /**
     * Normalise date to end of FiscalYear.
     * @param pDate the date to normalise.
     * @return the normalised date
     */
    public OceanusDate endOfYear(final OceanusDate pDate) {
        /* Access constituent parts */
        final int myDay = pDate.getDay();
        final int myMonth = pDate.getMonth();
        final int myFiscalMonth = theMonth.getValue();

        /* See whether we are earlier in the year */
        final boolean bEarlier = myFiscalMonth == myMonth
                                                          ? myDay < theDay
                                                          : myMonth < myFiscalMonth;

        /* Build the basic taxYear */
        final OceanusDate myDate = new OceanusDate(pDate.getYear(), theMonth, theDay, pDate.getLocale());

        /* Adjust if we are later */
        if (!bEarlier) {
            myDate.adjustYear(1);
        }

        /* Move back a day */
        myDate.adjustDay(-1);

        /* Return the date */
        return myDate;
    }

    /**
     * Normalise date to end of FiscalMonth.
     * @param pDate the date to normalise.
     * @return the normalised date
     */
    public OceanusDate endOfMonth(final OceanusDate pDate) {
        /* Access constituent parts */
        final int myDay = pDate.getDay();

        /* See whether we are earlier in the month */
        final boolean bEarlier = myDay < theDay;

        /* Build the basic taxMonth */
        final OceanusDate myDate = new OceanusDate(pDate.getYear(), pDate.getMonth(), theDay, pDate.getLocale());

        /* Adjust if we are later */
        if (!bEarlier) {
            myDate.adjustMonth(1);
        }

        /* Move back a day */
        myDate.adjustDay(-1);

        /* Return the date */
        return myDate;
    }

    /**
     * Normalise date to start of FiscalMonth.
     * @param pDate the date to normalise.
     * @return the normalised date
     */
    public OceanusDate startOfMonth(final OceanusDate pDate) {
        /* Access constituent parts */
        final int myDay = pDate.getDay();

        /* See whether we are earlier in the month */
        final boolean bEarlier = myDay < theDay;

        /* Build the basic taxMonth */
        final OceanusDate myDate = new OceanusDate(pDate.getYear(), pDate.getMonth(), theDay, pDate.getLocale());

        /* Adjust if we are earlier */
        if (bEarlier) {
            myDate.adjustMonth(-1);
        }

        /* Return the date */
        return myDate;
    }
}
