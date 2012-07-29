/*******************************************************************************
 * JDateDay: Java Date Day
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDateDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.sourceforge.JDateButton.JDateFormatter;

/**
 * Formatter for Date objects.
 * @author Tony Washer
 */
public class JDateDayFormatter implements JDateFormatter {
    /**
     * The default format.
     */
    private static final String DEFAULT_FORMAT = "dd-MMM-yyyy";

    /**
     * The locale.
     */
    private final Locale theLocale;

    /**
     * The Simple Date format for the locale and format string.
     */
    private SimpleDateFormat theDateFormat = null;

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
        /* Create the simple date format */
        theDateFormat = new SimpleDateFormat(pFormat, theLocale);
    }

    @Override
    public String formatCalendarDay(final Calendar pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return formatDate(pDate.getTime());
    }

    /**
     * Format a Date
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
     * Format a DateDay
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatDateDay(final JDateDay pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return formatDate(pDate.getDate());
    }

    /**
     * Format a DateDayRange
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
        String myFormat = (myStart == null) ? JDateDayRange.DESC_UNBOUNDED : formatDateDay(myStart);
        myFormat += JDateDayRange.DESC_LINK;
        myFormat += (myEnd == null) ? JDateDayRange.DESC_UNBOUNDED : formatDateDay(myEnd);

        /* return the format */
        return myFormat;
    }

    @Override
    public Locale getLocale() {
        return theLocale;
    }
}
