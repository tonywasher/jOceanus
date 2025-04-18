/*******************************************************************************
 * Oceanus: Java Utilities
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.oceanus.base.OceanusLocale;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a Date object that is fixed to a particular day. There is no concept of time within
 * the day Calendar objects that are built to represent the Date are set to noon on the day in
 * question.
 */
public class OceanusDate
        implements Comparable<OceanusDate> {
    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 17;

    /**
     * The Year shift for DateDay Id. This is 9 corresponding to (1 shiftLeft 9 places) = 512
     */
    protected static final int SHIFT_ID_YEAR = 9;

    /**
     * The Number of months in a Quarter.
     */
    protected static final int MONTHS_IN_QUARTER = 3;

    /**
     * Text for Null Date Error.
     */
    private static final String ERROR_NULLDATE = OceanusDateResource.ERROR_NULLDATE.getValue();

    /**
     * Text for Null Locale Error.
     */
    private static final String ERROR_NULLLOCALE = OceanusDateResource.ERROR_NULLLOCALE.getValue();

    /**
     * Text for Bad Format Error.
     */
    private static final String ERROR_BADFORMAT = OceanusDateResource.ERROR_BADFORMAT.getValue();

    /**
     * The format to be used.
     */
    private static final String FORMAT_DEFAULT = "dd-MMM-yyyy";

    /**
     * The locale to be used.
     */
    private Locale theLocale;

    /**
     * The format to be used.
     */
    private String theFormat = FORMAT_DEFAULT;

    /**
     * The Simple Date format for the locale and format string.
     */
    private DateTimeFormatter theDateFormat;

    /**
     * The Date format.
     */
    private String theFormattedDate;

    /**
     * The Date object in underlying Java form.
     */
    private LocalDate theDate;

    /**
     * The year of the date.
     */
    private int theYear;

    /**
     * The month of the date.
     */
    private int theMonth;

    /**
     * The day of the date.
     */
    private int theDay;

    /**
     * The day id.
     */
    private int theId;

    /**
     * Construct a new Date and initialise with today's date.
     */
    public OceanusDate() {
        this(OceanusLocale.getDefaultLocale());
    }

    /**
     * Construct a new Date and initialise with todays date.
     * @param pLocale the locale
     */
    public OceanusDate(final Locale pLocale) {
        this(LocalDate.now(), pLocale);
    }

    /**
     * Construct a new Date and initialise from a java date.
     * @param pDate the java date to initialise from
     */
    public OceanusDate(final LocalDate pDate) {
        this(pDate, OceanusLocale.getDefaultLocale());
    }

    /**
     * Construct a new Date and initialise from a java date.
     * @param pDate the java date to initialise from
     * @param pLocale the locale for this date
     */
    public OceanusDate(final LocalDate pDate,
                       final Locale pLocale) {
        buildDateDay(pDate, pLocale);
    }

    /**
     * Construct a new Date and initialise from a java date.
     * @param pDate the java calendar to initialise from
     */
    public OceanusDate(final Date pDate) {
        this(pDate, OceanusLocale.getDefaultLocale());
    }

    /**
     * Construct a new Date and initialise from a java date.
     * @param pDate the java date to initialise from
     * @param pLocale the locale for this date
     */
    public OceanusDate(final Date pDate,
                       final Locale pLocale) {
        /* Null dates not allowed */
        if (pDate == null) {
            throw new IllegalArgumentException(ERROR_NULLDATE);
        }

        /* Create the Date */
        final Instant myInstant = Instant.ofEpochMilli(pDate.getTime());
        final LocalDateTime myDateTime = LocalDateTime.ofInstant(myInstant, ZoneId.systemDefault());
        buildDateDay(myDateTime.toLocalDate(), pLocale);
    }

    /**
     * Construct a new Date and initialise from a date.
     * @param pDate the finance date to initialise from
     */
    public OceanusDate(final OceanusDate pDate) {
        /* Null dates not allowed */
        if (pDate == null) {
            throw new IllegalArgumentException(ERROR_NULLDATE);
        }

        /* Create the Date */
        buildDateDay(pDate.getYear(), pDate.getMonth(), pDate.getDay(), pDate.getLocale());
    }

    /**
     * Construct an explicit Date.
     * @param pYear the year
     * @param pMonth the month (1 to 12 etc)
     * @param pDay the day of the month
     */
    public OceanusDate(final int pYear,
                       final int pMonth,
                       final int pDay) {
        this(pYear, pMonth, pDay, OceanusLocale.getDefaultLocale());
    }

    /**
     * Construct an explicit Date.
     * @param pYear the year
     * @param pMonth the month (Month.JUNE etc)
     * @param pDay the day of the month
     */
    public OceanusDate(final int pYear,
                       final Month pMonth,
                       final int pDay) {
        this(pYear, pMonth.getValue(), pDay);
    }

    /**
     * Construct an explicit Date for a locale.
     * @param pYear the year
     * @param pMonth the month (1 to 12 etc)
     * @param pDay the day of the month
     * @param pLocale the locale for this date
     */
    public OceanusDate(final int pYear,
                       final int pMonth,
                       final int pDay,
                       final Locale pLocale) {
        buildDateDay(pYear, pMonth, pDay, pLocale);
    }

    /**
     * Construct an explicit Date for a locale.
     * @param pYear the year
     * @param pMonth the month (Month.JUNE etc)
     * @param pDay the day of the month
     * @param pLocale the locale for this date
     */
    public OceanusDate(final int pYear,
                       final Month pMonth,
                       final int pDay,
                       final Locale pLocale) {
        this(pYear, pMonth.getValue(), pDay, pLocale);
    }

    /**
     * Construct a Date from a formatted string.
     * @param pValue the formatted string
     */
    public OceanusDate(final String pValue) {
        this(pValue, OceanusLocale.getDefaultLocale());
    }

    /**
     * Construct a Date from a formatted string.
     * @param pValue the formatted string
     * @param pLocale the locale for this date
     */
    public OceanusDate(final String pValue,
                       final Locale pLocale) {
        /* Parse using default format */
        this(pValue, pLocale, FORMAT_DEFAULT);
    }

    /**
     * Construct a Date from a formatted string.
     * @param pValue the formatted string
     * @param pLocale the locale for this date
     * @param pFormat the format to use for parsing
     */
    public OceanusDate(final String pValue,
                       final Locale pLocale,
                       final String pFormat) {
        /* Null dates not allowed */
        if (pValue == null) {
            throw new IllegalArgumentException(ERROR_NULLDATE);
        }

        try {
            /* Access the date format */
            theFormat = pFormat;
            theDateFormat = DateTimeFormatter.ofPattern(theFormat, pLocale);

            /* Parse and build the date */
            final LocalDate myDate = LocalDate.parse(pValue, theDateFormat);
            buildDateDay(myDate, pLocale);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(ERROR_BADFORMAT
                                               + " "
                                               + pValue, e);
        }
    }

    /**
     * Get the year of the date.
     * @return the year of the date
     */
    public int getYear() {
        return theYear;
    }

    /**
     * Get the month of the date.
     * @return the month of the date
     */
    public int getMonth() {
        return theMonth;
    }

    /**
     * Get the day of the date.
     * @return the day of the date
     */
    public int getDay() {
        return theDay;
    }

    /**
     * Get the day of the week.
     * @return the day of the week
     */
    public DayOfWeek getDayOfWeek() {
        return theDate.getDayOfWeek();
    }

    /**
     * Get the month value.
     * @return the month value
     */
    public Month getMonthValue() {
        return theDate.getMonth();
    }

    /**
     * Get the id of the date. This is a unique integer representation of the date usable as an id
     * for the date.
     * @return the id of the date
     */
    public int getId() {
        return theId;
    }

    /**
     * Get the Date associated with this object.
     * @return the date
     */
    public LocalDate getDate() {
        return theDate;
    }

    /**
     * Get the locale associated with this object.
     * @return the java locale
     */
    public Locale getLocale() {
        return theLocale;
    }

    /**
     * Construct a date from a java date.
     * @param pDate the java date to initialise from
     * @param pLocale the locale for this date
     */
    private void buildDateDay(final LocalDate pDate,
                              final Locale pLocale) {
        /* Null dates not allowed */
        if (pDate == null) {
            throw new IllegalArgumentException(ERROR_NULLDATE);
        }

        /* Null locale not allowed */
        if (pLocale == null) {
            throw new IllegalArgumentException(ERROR_NULLLOCALE);
        }

        /* Build date values */
        theLocale = pLocale;
        theDate = pDate;
        obtainValues();
    }

    /**
     * Construct an explicit Date for a locale.
     * @param pYear the year
     * @param pMonth the month (1 to 12)
     * @param pDay the day of the month
     * @param pLocale the locale for this date
     */
    private void buildDateDay(final int pYear,
                              final int pMonth,
                              final int pDay,
                              final Locale pLocale) {
        /* Build the date day */
        buildDateDay(LocalDate.of(pYear, pMonth, pDay), pLocale);
    }

    /**
     * Set locale for the DateDay.
     * @param pLocale the locale
     */
    public void setLocale(final Locale pLocale) {
        /* Record the locale */
        theLocale = pLocale;

        /* rebuild the date into the new locale */
        buildDateDay(theYear, theMonth, theDay, pLocale);

        /* Reset the date format */
        theDateFormat = null;
    }

    /**
     * Set the date format.
     * @param pFormat the format string
     */
    public void setFormat(final String pFormat) {
        /* Store the format string */
        theFormat = pFormat;

        /* Reset the date format */
        theDateFormat = null;
        theFormattedDate = null;
    }

    /**
     * Adjust the date by a number of years.
     * @param iYear the number of years to adjust by
     */
    public void adjustYear(final int iYear) {
        theDate = theDate.plusYears(iYear);
        obtainValues();
    }

    /**
     * Adjust the date by a number of months.
     * @param iMonth the number of months to adjust by
     */
    public void adjustMonth(final int iMonth) {
        theDate = theDate.plusMonths(iMonth);
        obtainValues();
    }

    /**
     * Adjust the date by a number of days.
     * @param iDay the number of days to adjust by
     */
    public void adjustDay(final int iDay) {
        theDate = theDate.plusDays(iDay);
        obtainValues();
    }

    /**
     * Adjust the date by a determined amount.
     * @param iField the field to adjust
     * @param iUnits the number of units to adjust by
     */
    public void adjustField(final TemporalUnit iField,
                            final int iUnits) {
        theDate = theDate.plus(iUnits, iField);
        obtainValues();
    }

    /**
     * Adjust the date by a period in a forward direction.
     * @param pPeriod the period to adjust by
     */
    public void adjustForwardByPeriod(final OceanusDatePeriod pPeriod) {
        if (pPeriod == OceanusDatePeriod.ALLDATES) {
            return;
        }
        adjustField(pPeriod.getField(), pPeriod.getAmount(true));
    }

    /**
     * Adjust the date by a period in a backward direction.
     * @param pPeriod the period to adjust by
     */
    public void adjustBackwardByPeriod(final OceanusDatePeriod pPeriod) {
        if (pPeriod == OceanusDatePeriod.ALLDATES) {
            return;
        }
        adjustField(pPeriod.getField(), pPeriod.getAmount(false));
    }

    /**
     * Adjust the date to the start of the period.
     * @param pPeriod the period to adjust by
     */
    public void startPeriod(final OceanusDatePeriod pPeriod) {
        switch (pPeriod) {
            case CALENDARMONTH:
                startCalendarMonth();
                break;
            case CALENDARQUARTER:
                startCalendarQuarter();
                break;
            case CALENDARYEAR:
                startCalendarYear();
                break;
            case FISCALYEAR:
                startFiscalYear();
                break;
            default:
                break;
        }
    }

    /**
     * Adjust the date to the end of the following month.
     */
    public void endNextMonth() {
        /* Move to the first of the current month */
        theDate = theDate.withDayOfMonth(1);

        /* Add two months and move back a day */
        theDate = theDate.plusMonths(2);
        theDate = theDate.minusDays(1);
        obtainValues();
    }

    /**
     * Adjust the date to the start of the month.
     */
    public void startCalendarMonth() {
        /* Move to the first of the current month */
        theDate = theDate.withDayOfMonth(1);
        obtainValues();
    }

    /**
     * Adjust the date to the end of the month.
     */
    public void endCalendarMonth() {
        /* Move to the first of the next month and then one day before */
        theDate = theDate.withDayOfMonth(1);
        theDate = theDate.plusMonths(1);
        theDate = theDate.minusDays(1);
        obtainValues();
    }

    /**
     * Adjust the date to the start of the quarter.
     */
    public void startCalendarQuarter() {
        /* Determine the month in quarter */
        final int myMiQ = (theMonth - 1)
                          % MONTHS_IN_QUARTER;

        /* Move to the first of the current month */
        theDate = theDate.withDayOfMonth(1);

        /* Move to the first of the quarter */
        theDate = theDate.minusMonths(myMiQ);
        obtainValues();
    }

    /**
     * Adjust the date to the start of the year.
     */
    public void startCalendarYear() {
        /* Move to the first of the current year */
        theDate = theDate.withDayOfMonth(1);
        theDate = theDate.withMonth(Month.JANUARY.getValue());
        obtainValues();
    }

    /**
     * Adjust the date to the end of the year.
     */
    public void endCalendarYear() {
        /* Move to the first of the current year */
        theDate = theDate.plusYears(1);
        theDate = theDate.withDayOfMonth(1);
        theDate = theDate.withMonth(Month.JANUARY.getValue());
        theDate = theDate.minusDays(1);
        obtainValues();
    }

    /**
     * Adjust the date to the start of the fiscal year.
     */
    public void startFiscalYear() {
        /* Determine Fiscal year type */
        final OceanusFiscalYear myFiscal = OceanusFiscalYear.determineFiscalYear(theLocale);
        final int myMonth = myFiscal.getFirstMonth().getValue();
        final int myDay = myFiscal.getFirstDay();

        /* Determine which year we are in */
        if (theMonth < myMonth
            || (theMonth == myMonth && theDay < myDay)) {
            theDate = theDate.minusYears(1);
        }

        /* Move to the first of the current year */
        theDate = theDate.withDayOfMonth(myDay);
        theDate = theDate.withMonth(myMonth);
        obtainValues();
    }

    /**
     * Calculate the age that someone born on this date will be on a given date.
     * @param pDate the date for which to calculate the age
     * @return the age on that date
     */
    public int ageOn(final OceanusDate pDate) {
        /* Calculate the initial age assuming same date in year */
        int myAge = pDate.theDate.getYear();
        myAge -= theDate.getYear();

        /* Check whether we are later in the year */
        int myDelta = theDate.getMonthValue()
                      - pDate.theDate.getMonthValue();
        if (myDelta == 0) {
            myDelta = theDate.getDayOfMonth()
                      - pDate.theDate.getDayOfMonth();
        }

        /* If so then subtract one from the year */
        if (myDelta > 0) {
            myAge--;
        }

        /* Return to caller */
        return myAge;
    }

    /**
     * Calculate the days until the specified date.
     * @param pDate the date for which to days until
     * @return the days until that date
     */
    public long daysUntil(final OceanusDate pDate) {
        /* Calculate the initial age assuming same date in year */
        return theDate.until(pDate.theDate, ChronoUnit.DAYS);
    }

    /**
     * Copy a date from another DateDay.
     * @param pDate the date to copy from
     */
    public void copyDate(final OceanusDate pDate) {
        buildDateDay(pDate.getDate(), theLocale);
        obtainValues();
    }

    /**
     * Obtain the year,month and day values from the date.
     */
    private void obtainValues() {
        /* Access date details */
        theYear = theDate.getYear();
        theMonth = theDate.getMonthValue();
        theDay = theDate.getDayOfMonth();

        /* Calculate the id (512*year + dayofYear) */
        theId = (theYear << SHIFT_ID_YEAR)
                + theDate.getDayOfYear();

        /* Reset formatted date */
        theFormattedDate = null;
    }

    @Override
    public String toString() {
        /* If we already have a formatted date */
        if (theFormattedDate != null) {
            return theFormattedDate;
        }

        /* If we have not obtained the date format */
        if (theDateFormat == null) {
            /* Create the simple date format */
            theDateFormat = DateTimeFormatter.ofPattern(theFormat, theLocale);
        }

        /* Format the date */
        theFormattedDate = theDate.format(theDateFormat);

        /* Return the date */
        return theFormattedDate;
    }

    @Override
    public int compareTo(final OceanusDate pThat) {
        /* Handle trivial compares */
        if (this.equals(pThat)) {
            return 0;
        } else if (pThat == null) {
            return -1;
        }

        /* Compare the year, month and date */
        int iDiff = theYear
                    - pThat.theYear;
        if (iDiff != 0) {
            return iDiff;
        }
        iDiff = theMonth
                - pThat.theMonth;
        if (iDiff != 0) {
            return iDiff;
        }
        return theDay
               - pThat.theDay;
    }

    /**
     * Compare this date to a range.
     * @param pRange the range to compare to
     * @return -1 if date is before range, 0 if date is within range, 1 if date is after range
     */
    public int compareToRange(final OceanusDateRange pRange) {
        /* Check start of range */
        final OceanusDate myStart = pRange.getStart();
        if (myStart != null
            && compareTo(myStart) < 0) {
            return -1;
        }

        /* Check end of range */
        final OceanusDate myEnd = pRange.getEnd();
        if (myEnd != null
            && compareTo(myEnd) > 0) {
            return 1;
        }

        /* Must be within range */
        return 0;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a TethysDate */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the object as a TethysDate */
        final OceanusDate myThat = (OceanusDate) pThat;

        /* Check components */
        return theYear == myThat.theYear
               && theMonth == myThat.theMonth
               && theDay == myThat.theDay;
    }

    @Override
    public int hashCode() {
        /* Calculate hash based on Year/Month/Day */
        int iHash = theYear;
        iHash *= HASH_PRIME;
        iHash += theMonth + 1;
        iHash *= HASH_PRIME;
        iHash += theDay;
        return iHash;
    }

    /**
     * Convert the LocalDate to a Date.
     * @return the associated date
     */
    public Date toDate() {
        final Instant myInstant = theDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(myInstant);
    }

    /**
     * Convert the LocalDate to a Calendar.
     * @return the Calendar
     */
    public Calendar toCalendar() {
        final Calendar myCalendar = Calendar.getInstance(theLocale);
        myCalendar.setTime(toDate());
        return myCalendar;
    }

    /**
     * Determine whether two DateDay objects differ.
     * @param pCurr The current Date
     * @param pNew The new Date
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static boolean isDifferent(final OceanusDate pCurr,
                                      final OceanusDate pNew) {
        /* Handle case where current value is null */
        if (pCurr == null) {
            return pNew != null;
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return true;
        }

        /* Handle Standard cases */
        return !pCurr.equals(pNew);
    }
}
