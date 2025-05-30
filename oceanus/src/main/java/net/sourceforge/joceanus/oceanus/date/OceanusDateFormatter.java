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
import net.sourceforge.joceanus.oceanus.convert.OceanusDataConverter;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Formatter for Date objects.
 * @author Tony Washer
 */
public class OceanusDateFormatter
        implements OceanusEventProvider<OceanusDateEvent> {
    /**
     * Date Byte length.
     */
    public static final int BYTE_LEN = Long.BYTES;

    /**
     * As of Java 16.0.2 the short format for September is Sept instead of Sep.
     */
    private static final int PATCH_JAVA_VER = 16;

    /**
     * As of Java 16.0.2 the short format for September is Sept instead of Sep.
     */
    private static final String PATCH_SEPT_NEW = "-Sept-";

    /**
     * As of Java 16.0.2 the short format for September is Sept instead of Sep.
     */
    private static final String PATCH_SEPT_OLD = "-Sep-";

    /**
     * The default format.
     */
    private static final String DEFAULT_FORMAT = "dd-MMM-yyyy";

    /**
     * One hundred years.
     */
    private static final int YEARS_CENTURY = 100;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<OceanusDateEvent> theEventManager;

    /**
     * The locale.
     */
    private Locale theLocale;

    /**
     * The Simple Date format for the locale and format string.
     */
    private String theFormat;

    /**
     * The Simple Date format for the locale and format string.
     */
    private SimpleDateFormat theDateFormat;

    /**
     * The DateTime format for the locale and format string.
     */
    private DateTimeFormatter theLocalDateFormat;

    /**
     * Constructor.
     */
    public OceanusDateFormatter() {
        /* Use default locale */
        this(OceanusLocale.getDefaultLocale());
    }

    /**
     * Constructor.
     * @param pLocale the locale
     */
    public OceanusDateFormatter(final Locale pLocale) {
        /* Store locale */
        theLocale = pLocale;
        theEventManager = new OceanusEventManager<>();
        setFormat(DEFAULT_FORMAT);
    }

    @Override
    public OceanusEventRegistrar<OceanusDateEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
        theEventManager.fireEvent(OceanusDateEvent.FORMATCHANGED);
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
        final String pFormat = theFormat;
        theFormat = null;
        setFormat(pFormat);
    }

    /**
     * Format a calendar Date.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatCalendarDay(final Calendar pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return formatJavaDate(pDate.getTime());
    }

    /**
     * Format a local Date.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatLocalDate(final LocalDate pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return pDate.format(theLocalDateFormat);
    }

    /**
     * Format a java Date.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatJavaDate(final Date pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return theDateFormat.format(pDate);
    }

    /**
     * Format a Date.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatDate(final OceanusDate pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return formatLocalDate(pDate.getDate());
    }

    /**
     * Format a DateRange.
     * @param pRange the range to format
     * @return the formatted date
     */
    public String formatDateRange(final OceanusDateRange pRange) {
        /* Handle null */
        if (pRange == null) {
            return null;
        }

        /* Access components */
        final OceanusDate myStart = pRange.getStart();
        final OceanusDate myEnd = pRange.getEnd();

        /* Build range description */
        return ((myStart == null)
                ? OceanusDateRange.DESC_UNBOUNDED
                : formatDate(myStart))
                    + OceanusDateRange.CHAR_BLANK
                    + OceanusDateRange.DESC_LINK
                    + OceanusDateRange.CHAR_BLANK
                    + ((myEnd == null)
                        ? OceanusDateRange.DESC_UNBOUNDED
                        : formatDate(myEnd));
    }

    /**
     * Parse Java Date.
     * @param pValue Formatted Date
     * @return the Date
     * @throws IllegalArgumentException on error
     */
    public Date parseJavaDate(final String pValue) {
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
            /* Handle Patch for Java 16.0.2 change for September */
            final int myVersion = Runtime.version().feature();
            if (pValue.contains(PATCH_SEPT_OLD)
                    && myVersion >= PATCH_JAVA_VER) {
                return parsePatchedLocalDate(pValue.replace(PATCH_SEPT_OLD, PATCH_SEPT_NEW));
            }
            if (pValue.contains(PATCH_SEPT_NEW)
                    && Runtime.version().feature() < PATCH_JAVA_VER) {
                return parsePatchedLocalDate(pValue.replace(PATCH_SEPT_NEW, PATCH_SEPT_OLD));
            }

            /* throw exception */
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
    private LocalDate parsePatchedLocalDate(final String pValue) {
        /* Parse the date */
        try {
            return LocalDate.parse(pValue, theLocalDateFormat);
        } catch (DateTimeParseException e) {
            /* throw exception */
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
        final Date myDate = parseJavaDate(pValue);
        final Calendar myCalendar = Calendar.getInstance(theLocale);
        myCalendar.setTime(myDate);
        return myCalendar;
    }

    /**
     * Parse Date.
     * @param pValue Formatted Date
     * @return the DateDay
     * @throws IllegalArgumentException on error
     */
    public OceanusDate parseDate(final String pValue) {
        final LocalDate myDate = parseLocalDate(pValue);
        return new OceanusDate(myDate);
    }

    /**
     * Parse Date using the passed year as base date.
     * <p>
     * This is used when a two digit year is utilised
     * @param pValue Formatted DateDay
     * @param pBaseYear the base year
     * @return the DateDay
     * @throws IllegalArgumentException on error
     */
    public OceanusDate parseDateBase(final String pValue,
                                     final int pBaseYear) {
        LocalDate myDate = parseLocalDate(pValue);
        if (myDate.getYear() >= pBaseYear + YEARS_CENTURY) {
            myDate = myDate.minusYears(YEARS_CENTURY);
        }
        return new OceanusDate(myDate);
    }

    /**
     * Obtain the locale.
     * @return the locale
     */
    public Locale getLocale() {
        return theLocale;
    }

    /**
     * Create a byte array representation of a date.
     * @param pDate the date to process
     * @return the processed date
     */
    public byte[] toBytes(final OceanusDate pDate) {
        final long myEpoch = pDate.getDate().toEpochDay();
        return OceanusDataConverter.longToByteArray(myEpoch);
    }

    /**
     * Parse a byte array representation of a date.
     * @param pBuffer the byte representation
     * @return the date
     */
    public OceanusDate fromBytes(final byte[] pBuffer) {
        if (pBuffer == null || pBuffer.length < Long.BYTES) {
            throw new IllegalArgumentException();
        }
        final long myEpoch = OceanusDataConverter.byteArrayToLong(pBuffer);
        final LocalDate myDate = LocalDate.ofEpochDay(myEpoch);
        return new OceanusDate(myDate);
    }
}
