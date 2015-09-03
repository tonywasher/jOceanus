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

import java.time.temporal.ChronoUnit;

/**
 * DatePeriod class representing standard date ranges.
 * @author Tony Washer
 */
public enum JDatePeriod {
    /**
     * OneWeek.
     */
    ONEWEEK(ChronoUnit.WEEKS, 1),

    /**
     * Two Weeks.
     */
    FORTNIGHT(ChronoUnit.WEEKS, 2),

    /**
     * One Month.
     */
    ONEMONTH(ChronoUnit.MONTHS, 1),

    /**
     * Three Months.
     */
    QUARTERYEAR(ChronoUnit.MONTHS, 3),

    /**
     * Six Months.
     */
    HALFYEAR(ChronoUnit.MONTHS, 6),

    /**
     * One Year.
     */
    ONEYEAR(ChronoUnit.YEARS, 1),

    /**
     * Calendar Month.
     */
    CALENDARMONTH(ChronoUnit.MONTHS, 1),

    /**
     * Calendar Quarter.
     */
    CALENDARQUARTER(ChronoUnit.MONTHS, 3),

    /**
     * Calendar Year.
     */
    CALENDARYEAR(ChronoUnit.YEARS, 1),

    /**
     * Fiscal Year.
     */
    FISCALYEAR(ChronoUnit.YEARS, 1),

    /**
     * Dates Up to.
     */
    DATESUPTO(null, -1),

    /**
     * Custom.
     */
    CUSTOM(null, -1),

    /**
     * All.
     */
    ALLDATES(null, -1);

    /**
     * The String name.
     */
    private String theName;

    /**
     * The calendar field.
     */
    private final ChronoUnit theField;

    /**
     * The adjustments amount.
     */
    private final int theAmount;

    /**
     * Constructor.
     * @param pField the Calendar field
     * @param pAmount the adjustment value
     */
    JDatePeriod(final ChronoUnit pField,
                final int pAmount) {
        /* Store values */
        theField = pField;
        theAmount = pAmount;
    }

    /**
     * Obtain field.
     * @return the field
     */
    public ChronoUnit getField() {
        return theField;
    }

    /**
     * Obtain amount.
     * @param bForward forward/backward amount
     * @return the amount
     */
    public int getAmount(final boolean bForward) {
        return bForward
                        ? theAmount
                        : -theAmount;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = JDateDayResource.getKeyForPeriod(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Is period next/previous available?
     * @return true/false
     */
    public boolean adjustPeriod() {
        return theField != null;
    }

    /**
     * Is period DatesUpTo?
     * @return true/false
     */
    public boolean datesUpTo() {
        return this == DATESUPTO;
    }

    /**
     * Is period a containing period?
     * @return true/false
     */
    public boolean isContaining() {
        switch (this) {
            case CALENDARMONTH:
            case CALENDARQUARTER:
            case CALENDARYEAR:
            case FISCALYEAR:
                return true;
            default:
                return false;
        }
    }
}
