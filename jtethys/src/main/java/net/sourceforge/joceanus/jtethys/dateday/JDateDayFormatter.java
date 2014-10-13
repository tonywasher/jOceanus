/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jtethys.dateday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.jdatebutton.JDateFormatter;
import net.sourceforge.joceanus.jtethys.event.JEventObject;

/**
 * Formatter for Date objects.
 * @author Tony Washer
 */
public class JDateDayFormatter
        extends JEventObject
        implements JDateFormatter {
    /**
     * The default format.
     */
    private static final String DEFAULT_FORMAT = "dd-MMM-yyyy";

    /**
     * One hundred years.
     */
    private static final int YEARS_CENTURY = 100;

    /**
     * The locale.
     */
    private Locale theLocale;

    /**
     * The Simple Date format for the locale and format string.
     */
    private String theFormat = null;

    /**
     * The Simple Date format for the locale and format string.
     */
    private SimpleDateFormat theDateFormat = null;

    /**
     * The DateTime format for the locale and format string.
     */
    private DateTimeFormatter theLocalDateFormat = null;

    /**
     * Constructor.
     */
    public JDateDayFormatter() {
        /* Use default locale */
        this(Locale.getDefault());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public JDateDayFormatter(final Locale pLocale) {
        /* Store locale */
        theLocale = pLocale;
        setFormat(DEFAULT_FORMAT);
    }

    /**
     * Set the date format.
     * @param pFormat the format string
     */
    public final void setFormat(final String pFormat) {
        /* If the format is the same */
        if (pFormat.equals(theFormat)) {
            /* Ignore */
            return;
        }

        /* Create the simple date format */
        theFormat = pFormat;
        theDateFormat = new SimpleDateFormat(theFormat, theLocale);
        theLocalDateFormat = DateTimeFormatter.ofPattern(theFormat, theLocale);

        /* Notify of the change */
        fireStateChanged();
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public final void setLocale(final Locale pLocale) {
        /* If the locale is the same */
        if (theLocale.equals(pLocale)) {
            /* Ignore */
            return;
        }

        /* Store the locale */
        theLocale = pLocale;
        String pFormat = theFormat;
        theFormat = null;
        setFormat(pFormat);
    }

    /**
     * Format a Date.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatCalendarDay(final Calendar pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return formatDate(pDate.getTime());
    }

    @Override
    public String formatLocalDate(final LocalDate pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return pDate.format(theLocalDateFormat);
    }

    /**
     * Format a Date.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatDate(final Date pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return theDateFormat.format(pDate);
    }

    /**
     * Format a DateDay.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatDateDay(final JDateDay pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return formatLocalDate(pDate.getDate());
    }

    /**
     * Format a DateDayRange.
     * @param pRange the range to format
     * @return the formatted date
     */
    public String formatDateDayRange(final JDateDayRange pRange) {
        /* Handle null */
        if (pRange == null) {
            return null;
        }

        /* Access components */
        JDateDay myStart = pRange.getStart();
        JDateDay myEnd = pRange.getEnd();

        /* Build range description */
        String myFormat = (myStart == null)
                                           ? JDateDayRange.DESC_UNBOUNDED
                                           : formatDateDay(myStart);
        myFormat += JDateDayRange.DESC_LINK;
        myFormat += (myEnd == null)
                                   ? JDateDayRange.DESC_UNBOUNDED
                                   : formatDateDay(myEnd);

        /* return the format */
        return myFormat;
    }

    /**
     * Parse Date.
     * @param pValue Formatted Date
     * @return the Date
     * @throws IllegalArgumentException on error
     */
    public Date parseDate(final String pValue) {
        /* Parse the date */
        try {
            return theDateFormat.parse(pValue);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date: "
                                               + pValue, e);
        }
    }

    /**
     * Parse LocalDate.
     * @param pValue Formatted Date
     * @return the Date
     * @throws IllegalArgumentException on error
     */
    public LocalDate parseLocalDate(final String pValue) {
        /* Parse the date */
        try {
            return LocalDate.parse(pValue, theLocalDateFormat);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date: "
                                               + pValue, e);
        }
    }

    /**
     * Parse CalendarDay.
     * @param pValue Formatted CalendarDay
     * @return the CalendarDay
     * @throws IllegalArgumentException on error
     */
    public Calendar parseCalendarDay(final String pValue) {
        Date myDate = parseDate(pValue);
        Calendar myCalendar = Calendar.getInstance(theLocale);
        myCalendar.setTime(myDate);
        return myCalendar;
    }

    /**
     * Parse DateDay.
     * @param pValue Formatted DateDay
     * @return the DateDay
     * @throws IllegalArgumentException on error
     */
    public JDateDay parseDateDay(final String pValue) {
        LocalDate myDate = parseLocalDate(pValue);
        return new JDateDay(myDate);
    }

    /**
     * Parse DateDay using the passed year as base date.
     * <p>
     * This is used when a two digit year is utilised
     * @param pValue Formatted DateDay
     * @param pBaseYear the base year
     * @return the DateDay
     * @throws IllegalArgumentException on error
     */
    public JDateDay parseDateDayBase(final String pValue,
                                     final int pBaseYear) {
        LocalDate myDate = parseLocalDate(pValue);
        if (myDate.getYear() >= pBaseYear + YEARS_CENTURY) {
            myDate = myDate.minusYears(YEARS_CENTURY);
        }
        return new JDateDay(myDate);
    }

    @Override
    public Locale getLocale() {
        return theLocale;
    }
}
