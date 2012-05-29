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

import java.util.Calendar;

import net.sourceforge.JDateButton.JDateConfig;

/**
 * Class that extends {@link JDateConfig} to handle {@link DateDay} objects.
 * @author Tony Washer
 */
public class DateDayConfig extends JDateConfig {
    /**
     * Currently selected date (Calendar).
     */
    private Calendar theSelectedDate = null;

    /**
     * Currently selected date (DateDay).
     */
    private DateDay theSelectedDateDay = null;

    /**
     * Current earliest date (Calendar).
     */
    private Calendar theEarliestDate = null;

    /**
     * Current earliest date (DateDay).
     */
    private DateDay theEarliestDateDay = null;

    /**
     * Current latest date (Calendar).
     */
    private Calendar theLatestDate = null;

    /**
     * Current latest date (DateDay).
     */
    private DateDay theLatestDateDay = null;

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public DateDay getSelectedDateDay() {
        /* Access selected date */
        Calendar myDate = getSelectedDate();

        /* If we have changed selected date */
        if (isDateChanged(theSelectedDate, myDate)) {
            /* Store the selected date and create the DateDay version */
            theSelectedDate = myDate;
            theSelectedDateDay = (myDate == null) ? null : new DateDay(myDate, getLocale());
        }

        /* Return the Selected DateDay */
        return theSelectedDateDay;
    }

    /**
     * Set selected DateDay.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final DateDay pDate) {
        /* Set the selected DateDay */
        if (pDate == null) {
            setSelectedDate(null);
        } else {
            setSelectedDate(pDate.getCalendar());
        }
    }

    /**
     * Obtain the earliest DateDay.
     * @return the earliest DateDay
     */
    public DateDay getEarliestDateDay() {
        /* Access earliest date */
        Calendar myDate = getEarliestDate();

        /* If we have changed earliest date */
        if (isDateChanged(theEarliestDate, myDate)) {
            /* Store the earliest date and create the DateDay version */
            theEarliestDate = myDate;
            theEarliestDateDay = (myDate == null) ? null : new DateDay(myDate, getLocale());
        }

        /* Return the Earliest DateDay */
        return theEarliestDateDay;
    }

    /**
     * Set earliest DateDay.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final DateDay pDate) {
        /* Set the earliest DateDay */
        if (pDate == null) {
            setEarliestDate(null);
        } else {
            setEarliestDate(pDate.getCalendar());
        }
    }

    /**
     * Obtain the latest DateDay.
     * @return the latest DateDay
     */
    public DateDay getLatestDateDay() {
        /* Access latest date */
        Calendar myDate = getLatestDate();

        /* If we have changed latest date */
        if (isDateChanged(theLatestDate, myDate)) {
            /* Store the latest date and create the DateDay version */
            theLatestDate = myDate;
            theLatestDateDay = (myDate == null) ? null : new DateDay(myDate, getLocale());
        }

        /* Return the Latest DateDay */
        return theLatestDateDay;
    }

    /**
     * Set latest DateDay.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final DateDay pDate) {
        /* Set the latest DateDay */
        if (pDate == null) {
            setLatestDate(null);
        } else {
            setLatestDate(pDate.getCalendar());
        }
    }
}
