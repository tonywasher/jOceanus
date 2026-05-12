/*
 * Oceanus: Java Utilities
 * Copyright 2012-2026. Tony Washer
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

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

import java.time.temporal.ChronoUnit;

/**
 * DatePeriod class representing standard date ranges.
 *
 * @author Tony Washer
 */
public enum OceanusDatePeriod {
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
    private final String theName;

    /**
     * The calendar field.
     */
    private final transient ChronoUnit theField;

    /**
     * The adjustments amount.
     */
    private final int theAmount;

    /**
     * Constructor.
     *
     * @param pField  the Calendar field
     * @param pAmount the adjustment value
     */
    OceanusDatePeriod(final ChronoUnit pField,
                      final int pAmount) {
        /* Store values */
        theField = pField;
        theAmount = pAmount;
        theName = bundleIdForPeriod(this).getValue();
    }

    /**
     * Obtain field.
     *
     * @return the field
     */
    public ChronoUnit getField() {
        return theField;
    }

    /**
     * Obtain amount.
     *
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
        return theName;
    }

    /**
     * Is period next/previous available?
     *
     * @return true/false
     */
    public boolean adjustPeriod() {
        return theField != null;
    }

    /**
     * Is period DatesUpTo?
     *
     * @return true/false
     */
    public boolean datesUpTo() {
        return this == DATESUPTO;
    }

    /**
     * Is period a containing period?
     *
     * @return true/false
     */
    public boolean isContaining() {
        return switch (this) {
            case CALENDARMONTH, CALENDARQUARTER, CALENDARYEAR, FISCALYEAR -> true;
            default -> false;
        };
    }

    /**
     * Obtain the resource bundleId for the period.
     *
     * @param pPeriod the period
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForPeriod(final OceanusDatePeriod pPeriod) {
        /* Create the map and return it */
        return switch (pPeriod) {
            case ONEWEEK -> OceanusDateResource.PERIOD_ONEWEEK;
            case FORTNIGHT -> OceanusDateResource.PERIOD_FORTNIGHT;
            case ONEMONTH -> OceanusDateResource.PERIOD_ONEMONTH;
            case QUARTERYEAR -> OceanusDateResource.PERIOD_QUARTERYEAR;
            case HALFYEAR -> OceanusDateResource.PERIOD_HALFYEAR;
            case ONEYEAR -> OceanusDateResource.PERIOD_ONEYEAR;
            case CALENDARMONTH -> OceanusDateResource.PERIOD_CALENDARMONTH;
            case CALENDARQUARTER -> OceanusDateResource.PERIOD_CALENDARQUARTER;
            case CALENDARYEAR -> OceanusDateResource.PERIOD_CALENDARYEAR;
            case FISCALYEAR -> OceanusDateResource.PERIOD_FISCALYEAR;
            case DATESUPTO -> OceanusDateResource.PERIOD_DATESUPTO;
            case CUSTOM -> OceanusDateResource.PERIOD_CUSTOM;
            case ALLDATES -> OceanusDateResource.PERIOD_ALLDATES;
            default -> throw new IllegalArgumentException();
        };
    }
}
