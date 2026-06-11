/*
 * Oceanus: Java Utilities
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.oceanus.date;

import java.time.LocalDate;
import java.time.Month;

/**
 * Date utilities.
 */
public final class OceanusDateUtils {
    /**
     * The Number of months in a Quarter.
     */
    private static final int MONTHS_IN_QUARTER = 3;

    /**
     * Private constructor.
     */
    private OceanusDateUtils() {
    }

    /**
     * Obtain the date of the start of the period.
     *
     * @param pDate   the date to adjust
     * @param pPeriod the period to adjust by
     * @return the adjusted date
     */
    public static OceanusDate startPeriod(final OceanusDate pDate,
                                          final OceanusDatePeriod pPeriod) {
        return switch (pPeriod) {
            case CALENDARMONTH -> startCalendarMonth(pDate);
            case CALENDARQUARTER -> startCalendarQuarter(pDate);
            case CALENDARYEAR -> startCalendarYear(pDate);
            case FISCALYEAR -> startFiscalYear(pDate);
            default -> pDate;
        };
    }

    /**
     * Obtain the date of the end of the following month.
     *
     * @param pDate the date to adjust
     * @return the date
     */
    public static OceanusDate endNextMonth(final OceanusDate pDate) {
        /* Move to the first of the current month */
        LocalDate myDate = pDate.getDate().withDayOfMonth(1);

        /* Add two months and move back a day */
        myDate = myDate.plusMonths(2);
        myDate = myDate.minusDays(1);
        return new OceanusDate(myDate);
    }

    /**
     * Obtain the date of the start of the month.
     *
     * @param pDate the date to adjust
     * @return the date
     */
    public static OceanusDate startCalendarMonth(final OceanusDate pDate) {
        /* Move to the first of the current month */
        final LocalDate myDate = pDate.getDate().withDayOfMonth(1);
        return new OceanusDate(myDate);
    }

    /**
     * Obtain the date of the end of the month.
     *
     * @param pDate the date to adjust
     * @return the date
     */
    public static OceanusDate endCalendarMonth(final OceanusDate pDate) {
        /* Move to the first of the next month and then one day before */
        LocalDate myDate = pDate.getDate().withDayOfMonth(1);
        myDate = myDate.plusMonths(1);
        myDate = myDate.minusDays(1);
        return new OceanusDate(myDate);
    }

    /**
     * Obtain the date of the start of the quarter.
     *
     * @param pDate the date to adjust
     * @return the date
     */
    public static OceanusDate startCalendarQuarter(final OceanusDate pDate) {
        /* Determine the month in quarter */
        LocalDate myDate = pDate.getDate();
        final int myMiQ = (pDate.getMonth() - 1)
                % MONTHS_IN_QUARTER;

        /* Move to the first of the current month */
        myDate = myDate.withDayOfMonth(1);

        /* Move to the first of the quarter */
        myDate = myDate.minusMonths(myMiQ);
        return new OceanusDate(myDate);
    }

    /**
     * Obtain the date of the start of the year.
     *
     * @param pDate the date to adjust
     * @return the date
     */
    public static OceanusDate startCalendarYear(final OceanusDate pDate) {
        /* Move to the first of the current year */
        LocalDate myDate = pDate.getDate().withDayOfMonth(1);
        myDate = myDate.withMonth(Month.JANUARY.getValue());
        return new OceanusDate(myDate);
    }

    /**
     * Obtain the date of the end of the year.
     *
     * @param pDate the date to adjust
     * @return the date
     */
    public static OceanusDate endCalendarYear(final OceanusDate pDate) {
        /* Move to the first of the current year */
        LocalDate myDate = pDate.getDate().plusYears(1);
        myDate = myDate.withDayOfMonth(1);
        myDate = myDate.withMonth(Month.JANUARY.getValue());
        myDate = myDate.minusDays(1);
        return new OceanusDate(myDate);
    }

    /**
     * Adjust the date to the start of the fiscal year.
     *
     * @param pDate the date to adjust
     * @return the date
     */
    public static OceanusDate startFiscalYear(final OceanusDate pDate) {
        /* Determine Fiscal year type */
        final OceanusFiscalYear myFiscal = OceanusFiscalYear.determineFiscalYear(pDate.getLocale());
        final int myFiscalMonth = myFiscal.getFirstMonth().getValue();
        final int myFiscalDay = myFiscal.getFirstDay();

        /* Access date details */
        LocalDate myDate = pDate.getDate();
        final int myMonth = pDate.getMonth();
        final int myDay = pDate.getDay();

        /* Determine which year we are in */
        if (myMonth < myFiscalMonth
                || (myMonth == myFiscalMonth && myDay < myFiscalDay)) {
            myDate = myDate.minusYears(1);
        }

        /* Move to the first of the current year */
        myDate = myDate.withDayOfMonth(myFiscalDay);
        myDate = myDate.withMonth(myFiscalMonth);
        return new OceanusDate(myDate);
    }

    /**
     * Calculate the age that someone born on a particular date will be on a given date.
     *
     * @param pBirthDate the birthDate
     * @param pDate      the date for which to calculate the age
     * @return the age on that date
     */
    public static int ageOn(final OceanusDate pBirthDate,
                            final OceanusDate pDate) {
        /* Access dates */
        final LocalDate myBirthDate = pBirthDate.getDate();
        final LocalDate myDate = pDate.getDate();

        /* Calculate the initial age assuming same date in year */
        int myAge = myDate.getYear();
        myAge -= myBirthDate.getYear();

        /* Check whether we are later in the year */
        int myDelta = myBirthDate.getMonthValue()
                - myDate.getMonthValue();
        if (myDelta == 0) {
            myDelta = myBirthDate.getDayOfMonth()
                    - myDate.getDayOfMonth();
        }

        /* If so then subtract one from the year */
        if (myDelta > 0) {
            myAge--;
        }

        /* Return to caller */
        return myAge;
    }
}
