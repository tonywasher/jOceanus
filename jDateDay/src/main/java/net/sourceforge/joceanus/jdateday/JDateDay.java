/*******************************************************************************
 * jDateDay: Java Date Day
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdateday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.jdatebutton.JDateConfig;

/**
 * Represents a Date object that is fixed to a particular day. There is no concept of time within the day Calendar objects that are built to represent the Date
 * are set to noon on the day in question.
 */
public class JDateDay
        implements Comparable<JDateDay> {
    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 17;

    /**
     * The Year shift for DateDay Id. This is 9 corresponding to (1 shiftLeft 9 places) = 512
     */
    protected static final int SHIFT_ID_YEAR = 9;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(JDateDay.class.getName());

    /**
     * Text for Null Date Error.
     */
    private static final String ERROR_NULLDATE = NLS_BUNDLE.getString("ErrorNullDate");

    /**
     * Text for Null Locale Error.
     */
    private static final String ERROR_NULLLOCALE = NLS_BUNDLE.getString("ErrorNullLocale");

    /**
     * Text for Bad Format Error.
     */
    private static final String ERROR_BADFORMAT = NLS_BUNDLE.getString("ErrorBadFormat");

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
    private SimpleDateFormat theDateFormat = null;

    /**
     * The Date format.
     */
    private String theFormattedDate = null;

    /**
     * The Date object in underlying Java form.
     */
    private Calendar theDate = null;

    /**
     * The year of the date.
     */
    private int theYear = 0;

    /**
     * The month of the date.
     */
    private int theMonth = 0;

    /**
     * The day of the date.
     */
    private int theDay = 0;

    /**
     * The day id.
     */
    private int theId = 0;

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
     * Get the id of the date. This is a unique integer representation of the date usable as an id for the date.
     * @return the id of the date
     */
    public int getId() {
        return theId;
    }

    /**
     * Get the java date associated with this object.
     * @return the java date
     */
    public Date getDate() {
        return theDate.getTime();
    }

    /**
     * Get the java Calendar associated with this object.
     * @return the java calendar
     */
    public Calendar getCalendar() {
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
     * Construct a new Date and initialise with todays date.
     */
    public JDateDay() {
        this(Locale.getDefault());
    }

    /**
     * Construct a new Date and initialise with todays date.
     * @param pLocale the locale
     */
    public JDateDay(final Locale pLocale) {
        this(Calendar.getInstance(pLocale).getTime());
    }

    /**
     * Construct a new Date and initialise from a java date.
     * @param pDate the java date to initialise from
     */
    public JDateDay(final Date pDate) {
        this(pDate, Locale.getDefault());
    }

    /**
     * Construct a new Date and initialise from a java date.
     * @param pDate the java date to initialise from
     * @param pLocale the locale for this date
     */
    public JDateDay(final Date pDate,
                    final Locale pLocale) {
        buildDateDay(pDate, pLocale);
    }

    /**
     * Construct a new DateDay and initialise from a java Calendar.
     * @param pDate the java date to initialise from
     */
    public JDateDay(final Calendar pDate) {
        this(pDate, Locale.getDefault());
    }

    /**
     * Construct a new DateDay and initialise from a java Calendar.
     * @param pDate the java date to initialise from
     * @param pLocale the locale for this date
     */
    public JDateDay(final Calendar pDate,
                    final Locale pLocale) {
        /* Null dates not allowed */
        if (pDate == null) {
            throw new IllegalArgumentException(ERROR_NULLDATE);
        }

        /* Create the Date */
        buildDateDay(pDate.get(Calendar.YEAR), pDate.get(Calendar.MONTH), pDate.get(Calendar.DAY_OF_MONTH), pLocale);
    }

    /**
     * Construct a new Date and initialise from a finance date.
     * @param pDate the finance date to initialise from
     */
    public JDateDay(final JDateDay pDate) {
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
     * @param pMonth the month (Calendar.JUNE etc)
     * @param pDay the day of the month
     */
    public JDateDay(final int pYear,
                    final int pMonth,
                    final int pDay) {
        this(pYear, pMonth, pDay, Locale.getDefault());
    }

    /**
     * Construct an explicit Date for a locale.
     * @param pYear the year
     * @param pMonth the month (Calendar.JUNE etc)
     * @param pDay the day of the month
     * @param pLocale the locale for this date
     */
    public JDateDay(final int pYear,
                    final int pMonth,
                    final int pDay,
                    final Locale pLocale) {
        buildDateDay(pYear, pMonth, pDay, pLocale);
    }

    /**
     * Construct a Date from a formatted string.
     * @param pValue the formatted string
     */
    public JDateDay(final String pValue) {
        this(pValue, Locale.getDefault());
    }

    /**
     * Construct a Date from a formatted string.
     * @param pValue the formatted string
     * @param pLocale the locale for this date
     */
    public JDateDay(final String pValue,
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
    public JDateDay(final String pValue,
                    final Locale pLocale,
                    final String pFormat) {
        try {
            /* Access the date format */
            theFormat = pFormat;
            theDateFormat = new SimpleDateFormat(theFormat, pLocale);

            /* Parse and build the date */
            Date myDate = theDateFormat.parse(pValue);
            buildDateDay(myDate, pLocale);
        } catch (ParseException e) {
            throw new IllegalArgumentException(ERROR_BADFORMAT
                                               + " "
                                               + pValue, e);
        }
    }

    /**
     * Construct a date from a java date.
     * @param pDate the java date to initialise from
     * @param pLocale the locale for this date
     */
    private void buildDateDay(final Date pDate,
                              final Locale pLocale) {
        /* Null dates not allowed */
        if (pDate == null) {
            throw new IllegalArgumentException(ERROR_NULLDATE);
        }

        /* Access Date within calendar */
        Calendar myDate = Calendar.getInstance(pLocale);
        myDate.setTime(pDate);

        /* Create the Date */
        buildDateDay(myDate.get(Calendar.YEAR), myDate.get(Calendar.MONTH), myDate.get(Calendar.DAY_OF_MONTH), pLocale);
    }

    /**
     * Construct an explicit Date for a locale.
     * @param pYear the year
     * @param pMonth the month (Calendar.JUNE etc)
     * @param pDay the day of the month
     * @param pLocale the locale for this date
     */
    private void buildDateDay(final int pYear,
                              final int pMonth,
                              final int pDay,
                              final Locale pLocale) {
        /* Null locale not allowed */
        if (pLocale == null) {
            throw new IllegalArgumentException(ERROR_NULLLOCALE);
        }

        /* Build date values */
        theLocale = pLocale;
        theDate = Calendar.getInstance(theLocale);
        theDate.set(pYear, pMonth, pDay, JDateConfig.NOON_HOUR, 0, 0);
        theDate.set(Calendar.MILLISECOND, 0);
        obtainValues();
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
        theDate.add(Calendar.YEAR, iYear);
        obtainValues();
    }

    /**
     * Adjust the date by a number of months.
     * @param iMonth the number of months to adjust by
     */
    public void adjustMonth(final int iMonth) {
        theDate.add(Calendar.MONTH, iMonth);
        obtainValues();
    }

    /**
     * Adjust the date by a number of days.
     * @param iDay the number of days to adjust by
     */
    public void adjustDay(final int iDay) {
        theDate.add(Calendar.DAY_OF_MONTH, iDay);
        obtainValues();
    }

    /**
     * Adjust the date by a determined amount.
     * @param iField the field to adjust
     * @param iUnits the number of units to adjust by
     */
    public void adjustField(final int iField,
                            final int iUnits) {
        theDate.add(iField, iUnits);
        obtainValues();
    }

    /**
     * Adjust the date by a period in a forward direction.
     * @param pPeriod the period to adjust by
     */
    public void adjustForwardByPeriod(final JDatePeriod pPeriod) {
        if (pPeriod == JDatePeriod.ALLDATES) {
            return;
        }
        adjustField(pPeriod.getField(), pPeriod.getAmount(true));
    }

    /**
     * Adjust the date by a period in a backward direction.
     * @param pPeriod the period to adjust by
     */
    public void adjustBackwardByPeriod(final JDatePeriod pPeriod) {
        if (pPeriod == JDatePeriod.ALLDATES) {
            return;
        }
        adjustField(pPeriod.getField(), pPeriod.getAmount(false));
    }

    /**
     * Adjust the date to the end of the following month.
     */
    public void endNextMonth() {
        /* Move to the first of the current month */
        theDate.add(Calendar.DAY_OF_MONTH, 1 - theDay);

        /* Add two months and move back a day */
        theDate.add(Calendar.MONTH, 2);
        theDate.add(Calendar.DAY_OF_MONTH, -1);
        obtainValues();
    }

    /**
     * Calculate the age that someone born on this date will be on a given date.
     * @param pDate the date for which to calculate the age
     * @return the age on that date
     */
    public int ageOn(final JDateDay pDate) {
        int myAge = -1;

        /* Calculate the initial age assuming same date in year */
        myAge = pDate.theDate.get(Calendar.YEAR);
        myAge -= theDate.get(Calendar.YEAR);

        /* If we are a later day in the year subtract 1 year */
        if (theDate.get(Calendar.DAY_OF_YEAR) > pDate.theDate.get(Calendar.DAY_OF_YEAR)) {
            myAge--;
        }

        /* Return to caller */
        return myAge;
    }

    /**
     * Copy a date from another DateDay.
     * @param pDate the date to copy from
     */
    public void copyDate(final JDateDay pDate) {
        buildDateDay(pDate.getYear(), pDate.getMonth(), pDate.getDay(), theLocale);
        obtainValues();
    }

    /**
     * Obtain the year,month and day values from the date.
     */
    private void obtainValues() {
        /* Access date details */
        theYear = theDate.get(Calendar.YEAR);
        theMonth = theDate.get(Calendar.MONTH);
        theDay = theDate.get(Calendar.DAY_OF_MONTH);

        /* Calculate the id (512*year + dayofYear) */
        theId = (theYear << SHIFT_ID_YEAR)
                + theDate.get(Calendar.DAY_OF_YEAR);

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
            theDateFormat = new SimpleDateFormat(theFormat, theLocale);
        }

        /* Format the date */
        theFormattedDate = theDateFormat.format(theDate.getTime());

        /* Return the date */
        return theFormattedDate;
    }

    @Override
    public int compareTo(final JDateDay pThat) {
        /* Handle trivial compares */
        if (this == pThat) {
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
        return (theDay - pThat.theDay);
    }

    /**
     * Compare this date to a range
     * @param pRange the range to compare to
     * @return -1 if date is before range, 0 if date is within range, 1 if date is after range
     */
    public int compareTo(final JDateDayRange pRange) {
        /* Check start of range */
        JDateDay myStart = pRange.getStart();
        if ((myStart != null)
            && (compareTo(myStart) < 0)) {
            return -1;
        }

        /* Check end of range */
        JDateDay myEnd = pRange.getEnd();
        if ((myEnd != null)
            && (compareTo(myEnd) > 0)) {
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

        /* Make sure that the object is a JDateDay */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the object as a JDateDay */
        JDateDay myThat = (JDateDay) pThat;

        /* Check components */
        if (theYear != myThat.theYear) {
            return false;
        } else if (theMonth != myThat.theMonth) {
            return false;
        } else if (theDay != myThat.theDay) {
            return false;
        }
        return true;
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
     * Determine whether two DateDay objects differ.
     * @param pCurr The current Date
     * @param pNew The new Date
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static boolean isDifferent(final JDateDay pCurr,
                                      final JDateDay pNew) {
        /* Handle case where current value is null */
        if (pCurr == null) {
            return (pNew != null);
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return true;
        }

        /* Handle Standard cases */
        return !pCurr.equals(pNew);
    }
}
