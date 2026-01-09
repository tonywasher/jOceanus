/*******************************************************************************
 * Oceanus: Java Utilities
 * Copyright 2012-2026 Tony Washer
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

import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for TethysDate package.
 */
public enum OceanusDateResource implements OceanusBundleId {
    /**
     * Null Date disallowed.
     */
    ERROR_NULLDATE("error.nullDate"),

    /**
     * Null Locale disallowed.
     */
    ERROR_NULLLOCALE("error.nullLocale"),

    /**
     * Invalid Date String.
     */
    ERROR_BADFORMAT("error.badFormat"),

    /**
     * Period OneWeek.
     */
    PERIOD_ONEWEEK("period.oneWeek"),

    /**
     * Period Fortnight.
     */
    PERIOD_FORTNIGHT("period.fortnight"),

    /**
     * Period OneMonth.
     */
    PERIOD_ONEMONTH("period.oneMonth"),

    /**
     * Period QuarterYear.
     */
    PERIOD_QUARTERYEAR("period.quarterYear"),

    /**
     * Period HalfYear.
     */
    PERIOD_HALFYEAR("period.halfYear"),

    /**
     * Period OneYear.
     */
    PERIOD_ONEYEAR("period.oneYear"),

    /**
     * Period CalendarMonth.
     */
    PERIOD_CALENDARMONTH("period.calendarMonth"),

    /**
     * Period CalendarQuarter.
     */
    PERIOD_CALENDARQUARTER("period.calendarQuarter"),

    /**
     * Period CalendarYear.
     */
    PERIOD_CALENDARYEAR("period.calendarYear"),

    /**
     * Period FiscalYear.
     */
    PERIOD_FISCALYEAR("period.fiscalYear"),

    /**
     * Period DatesUpTo.
     */
    PERIOD_DATESUPTO("period.datesUpTo"),

    /**
     * Period Custom.
     */
    PERIOD_CUSTOM("period.custom"),

    /**
     * Period AllDates.
     */
    PERIOD_ALLDATES("period.allDates"),

    /**
     * Range unbounded.
     */
    RANGE_UNBOUNDED("range.unbounded"),

    /**
     * Range To.
     */
    RANGE_TO("range.to"),

    /**
     * Title Box.
     */
    TITLE_BOX("title.box");

    /**
     * The Period Map.
     */
    private static final Map<OceanusDatePeriod, OceanusBundleId> PERIOD_MAP = buildPeriodMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(OceanusDateResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    OceanusDateResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "date";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build period map.
     * @return the map
     */
    private static Map<OceanusDatePeriod, OceanusBundleId> buildPeriodMap() {
        /* Create the map and return it */
        final Map<OceanusDatePeriod, OceanusBundleId> myMap = new EnumMap<>(OceanusDatePeriod.class);
        myMap.put(OceanusDatePeriod.ONEWEEK, PERIOD_ONEWEEK);
        myMap.put(OceanusDatePeriod.FORTNIGHT, PERIOD_FORTNIGHT);
        myMap.put(OceanusDatePeriod.ONEMONTH, PERIOD_ONEMONTH);
        myMap.put(OceanusDatePeriod.QUARTERYEAR, PERIOD_QUARTERYEAR);
        myMap.put(OceanusDatePeriod.HALFYEAR, PERIOD_HALFYEAR);
        myMap.put(OceanusDatePeriod.ONEYEAR, PERIOD_ONEYEAR);
        myMap.put(OceanusDatePeriod.CALENDARMONTH, PERIOD_CALENDARMONTH);
        myMap.put(OceanusDatePeriod.CALENDARQUARTER, PERIOD_CALENDARQUARTER);
        myMap.put(OceanusDatePeriod.CALENDARYEAR, PERIOD_CALENDARYEAR);
        myMap.put(OceanusDatePeriod.FISCALYEAR, PERIOD_FISCALYEAR);
        myMap.put(OceanusDatePeriod.DATESUPTO, PERIOD_DATESUPTO);
        myMap.put(OceanusDatePeriod.CUSTOM, PERIOD_CUSTOM);
        myMap.put(OceanusDatePeriod.ALLDATES, PERIOD_ALLDATES);
        return myMap;
    }

    /**
     * Obtain key for period.
     * @param pPeriod the period
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForPeriod(final OceanusDatePeriod pPeriod) {
        return OceanusBundleLoader.getKeyForEnum(PERIOD_MAP, pPeriod);
    }
}
