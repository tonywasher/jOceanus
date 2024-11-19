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

import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Predicate;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;

/**
 * Date Configuration.
 */
public class OceanusDateConfig
        implements TethysEventProvider<OceanusDateEvent> {
    /**
     * The Event Manager.
     */
    private final OceanusEventManager<OceanusDateEvent> theEventManager;

    /**
     * The formatter.
     */
    private final OceanusDateFormatter theFormatter;

    /**
     * The Locale.
     */
    private Locale theLocale;

    /**
     * Show Narrow days.
     */
    private boolean doShowNarrowDays;

    /**
     * Allow null date selection.
     */
    private boolean allowNullDateSelection;

    /**
     * The selected date.
     */
    private OceanusDate theSelected;

    /**
     * The earliest select-able date (or null if no lower bound).
     */
    private OceanusDate theEarliest;

    /**
     * The latest select-able date (or null if no upper bound).
     */
    private OceanusDate theLatest;

    /**
     * The list of disallowed dates.
     */
    private Predicate<OceanusDate> theAllowed;

    /**
     * The active display month.
     */
    private OceanusDate theMonth;

    /**
     * Constructor.
     * @param pFormatter the date formatter
     */
    public OceanusDateConfig(final OceanusDateFormatter pFormatter) {
        /* Create resources */
        theFormatter = pFormatter;
        theEventManager = new OceanusEventManager<>();
        setLocale(pFormatter.getLocale());

        /* Initialise the allowed predicate */
        theAllowed = d -> true;

        /* Listen to locale changes */
        theFormatter.getEventRegistrar().addEventListener(e -> setLocale(theFormatter.getLocale()));
    }

    @Override
    public OceanusEventRegistrar<OceanusDateEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Get the locale.
     * @return the locale
     */
    public Locale getLocale() {
        return theLocale;
    }

    /**
     * Get the selected date.
     * @return the Selected date
     */
    public OceanusDate getSelectedDate() {
        return theSelected;
    }

    /**
     * Get the earliest select-able date.
     * @return the Earliest date
     */
    public OceanusDate getEarliestDate() {
        return theEarliest;
    }

    /**
     * Get the latest select-able date.
     * @return the Latest date
     */
    public OceanusDate getLatestDate() {
        return theLatest;
    }

    /**
     * Get the current month.
     * @return the current month
     */
    public OceanusDate getCurrentMonth() {
        return theMonth;
    }

    /**
     * Allow Null Date selection.
     * @return true/false
     */
    public boolean allowNullDateSelection() {
        return allowNullDateSelection;
    }

    /**
     * Show Narrow Days.
     * @return true/false
     */
    public boolean showNarrowDays() {
        return doShowNarrowDays;
    }

    /**
     * Set the Allowed predicate.
     * @param pAllowed the predicate
     */
    public void setAllowed(final Predicate<OceanusDate> pAllowed) {
        theAllowed = pAllowed;
    }

    /**
     * Determine whether the day in the month is allowed.
     * @param pDay the day of the current month
     * @return true/false
     */
    public boolean isAllowed(final int pDay) {
        final OceanusDate myDate = new OceanusDate(theMonth);
        myDate.adjustDay(pDay - 1);
        return theAllowed.test(myDate);
    }

    /**
     * Set the Locale.
     * @param pLocale the Locale
     */
    public final void setLocale(final Locale pLocale) {
        /* Store locale */
        theLocale = pLocale;

        /* Request rebuild of names */
        rebuildNames();
    }

    /**
     * Allow null date selection. If this flag is set an additional button will be displayed
     * allowing the user to explicitly select no date, thus setting the SelectedDate to null.
     * @param pAllowNullDateSelection true/false
     */
    public void setAllowNullDateSelection(final boolean pAllowNullDateSelection) {
        allowNullDateSelection = pAllowNullDateSelection;
    }

    /**
     * Show Narrow Days. If this flag is set Days are show in narrow rather than short form.
     * @param pShowNarrowDays true/false
     */
    public void setShowNarrowDays(final boolean pShowNarrowDays) {
        /* Set options */
        doShowNarrowDays = pShowNarrowDays;

        /* Request rebuild of names */
        rebuildNames();
    }

    /**
     * Rebuild names.
     */
    protected final void rebuildNames() {
        /* Fire event */
        theEventManager.fireEvent(OceanusDateEvent.FORMATCHANGED);
    }

    /**
     * Set the earliest date. This is the earliest date that may be selected. If the configured
     * latest date is earlier than this date, it will be set to this date to ensure a valid range.
     * @param pEarliest the Earliest select-able date (or null if unlimited)
     */
    public final void setEarliestDate(final OceanusDate pEarliest) {
        /* Default the field to null */
        theEarliest = null;

        /* If we have an earliest */
        if (pEarliest != null) {
            /* Store the date */
            theEarliest = pEarliest;

            /* If we have a latest date, reset if necessary */
            if (theLatest != null
                && theLatest.compareTo(theEarliest) < 0) {
                theLatest = theEarliest;
            }
        }
    }

    /**
     * Set the latest date. This is the latest date that may be selected. If the configured earliest
     * date is later than this date, it will be set to this date to ensure a valid range.
     * @param pLatest the Latest select-able date (or null if unlimited)
     */
    public final void setLatestDate(final OceanusDate pLatest) {
        /* Null the field */
        theLatest = null;

        /* If we have an earliest */
        if (pLatest != null) {
            /* Store the date */
            theLatest = pLatest;

            /* If we have an earliest date, reset if necessary */
            if (theEarliest != null
                && theLatest.compareTo(theEarliest) < 0) {
                theEarliest = theLatest;
            }
        }
    }

    /**
     * Format a date according to configured rules.
     * @param pDate the date to format
     * @return the formatted date
     */
    public String formatDate(final LocalDate pDate) {
        /* Handle null */
        if (pDate == null) {
            return null;
        }

        /* Format the date */
        return theFormatter.formatLocalDate(pDate);
    }

    /**
     * Capitalise first letter of string.
     * @param pValue the string to capitalise the first letter of
     * @return the capitalised string
     */
    public String capitaliseString(final String pValue) {
        String myValue = pValue;

        /* If the first UniCode item is lowerCase */
        if (Character.isLowerCase(pValue.codePointAt(0))) {
            /* Locate the length of the first character */
            final int iCharLen = pValue.offsetByCodePoints(0, 1);

            /* UpperCase the first iCharLen letters */
            myValue = pValue.substring(0, iCharLen).toUpperCase(theLocale)
                      + pValue.substring(iCharLen);
        }

        /* Return the capitalised value */
        return myValue;
    }

    /**
     * Obtain current date.
     * @return the current date
     */
    public OceanusDate currentDate() {
        return new OceanusDate();
    }

    /**
     * Obtain current day of month or zero if not current month.
     * @return the current month day
     */
    public int getCurrentDay() {
        final OceanusDate myDate = currentDate();
        return isSameMonth(myDate, theMonth)
                                             ? myDate.getDay()
                                             : 0;
    }

    /**
     * Obtain Selected day of month or zero if not current month.
     * @return the selected month day
     */
    public int getSelectedDay() {
        final OceanusDate myDate = getSelectedDate();
        return isSameMonth(myDate, theMonth)
                                             ? myDate.getDay()
                                             : 0;
    }

    /**
     * Obtain Earliest day of month or zero if not current month.
     * @return the earliest month day
     */
    public int getEarliestDay() {
        return isSameMonth(theEarliest, theMonth)
                                                  ? theEarliest.getDay()
                                                  : 0;
    }

    /**
     * Obtain Latest day of month or zero if not current month.
     * @return the latest month day
     */
    public int getLatestDay() {
        return isSameMonth(theLatest, theMonth)
                                                ? theLatest.getDay()
                                                : 0;
    }

    /**
     * Adjust current month to previous month.
     */
    public void previousMonth() {
        theMonth.adjustMonth(-1);
    }

    /**
     * Adjust current month to next month.
     */
    public void nextMonth() {
        theMonth.adjustMonth(1);
    }

    /**
     * Adjust current month to previous year.
     */
    public void previousYear() {
        theMonth.adjustYear(-1);
        if (theEarliest != null
            && theMonth.compareTo(theEarliest) < 0) {
            theMonth = new OceanusDate(theEarliest);
            theMonth.startCalendarMonth();
        }
    }

    /**
     * Adjust current month to next year.
     */
    public void nextYear() {
        theMonth.adjustYear(1);
        if (theLatest != null
            && theMonth.compareTo(theLatest) > 0) {
            theMonth = new OceanusDate(theLatest);
            theMonth.startCalendarMonth();
        }
    }

    /**
     * Set the selected date.
     * @param pDate the Selected date
     */
    public final void setSelectedDate(final OceanusDate pDate) {
        /* Store the date */
        theSelected = pDate;
    }

    /**
     * Set selected day in current month (called from dialog).
     * @param pDay the selected day
     */
    public void setSelectedDay(final int pDay) {
        final OceanusDate myOld = getSelectedDate();
        OceanusDate myNew = null;

        /* If we are selecting a proper date */
        if (pDay > 0) {
            /* Build the new selected date */
            myNew = new OceanusDate(theMonth);
            myNew.adjustDay(pDay - 1);
        }

        /* Ignore if there is no change */
        if (!isDateChanged(myOld, myNew)) {
            return;
        }

        /* Store the explicitly selected date */
        theSelected = myNew;
    }

    /**
     * Initialise the current month.
     */
    public void initialiseCurrent() {
        /* Access Selected Date */
        OceanusDate myDate = theSelected;
        if (myDate == null) {
            myDate = currentDate();
        }

        /* Move to start date if we are earlier */
        if (theEarliest != null
            && myDate.compareTo(theEarliest) < 0) {
            myDate = theEarliest;
        }

        /* Move to end date if we are later */
        if (theLatest != null
            && myDate.compareTo(theLatest) > 0) {
            myDate = theLatest;
        }

        /* Set to 1st of month and record it */
        theMonth = new OceanusDate(myDate);
        theMonth.startCalendarMonth();
    }

    /**
     * Has the date changed?
     * @param pFirst the first date
     * @param pSecond the second date
     * @return <code>true/false</code>
     */
    public static boolean isDateChanged(final OceanusDate pFirst,
                                        final OceanusDate pSecond) {
        if (pFirst == null) {
            return pSecond != null;
        } else {
            return !pFirst.equals(pSecond);
        }
    }

    /**
     * Are the dates in the same month.
     * @param pFirst the first date (maybe null)
     * @param pSecond the second date
     * @return true/false
     */
    public static boolean isSameMonth(final OceanusDate pFirst,
                                      final OceanusDate pSecond) {
        if (!isSameYear(pFirst, pSecond)) {
            return false;
        } else {
            return pFirst.getMonth() == pSecond.getMonth();
        }
    }

    /**
     * Are the dates in the same year.
     * @param pFirst the first date (maybe null)
     * @param pSecond the second date
     * @return true/false
     */
    public static boolean isSameYear(final OceanusDate pFirst,
                                     final OceanusDate pSecond) {
        if (pFirst == null) {
            return false;
        }
        return pFirst.getYear() == pSecond.getYear();
    }
}
