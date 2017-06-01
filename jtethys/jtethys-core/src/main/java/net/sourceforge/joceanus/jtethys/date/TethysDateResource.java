/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jtethys.date;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for TethysDate package.
 */
public enum TethysDateResource implements TethysResourceId {
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
     * tip NextDate.
     */
    TIP_NEXTDATE("tooltip.nextDate"),

    /**
     * tip PreviousDate.
     */
    TIP_PREVDATE("tooltip.prevDate"),

    /**
     * Label Starting.
     */
    LABEL_STARTING("label.starting"),

    /**
     * Label Ending.
     */
    LABEL_ENDING("label.ending"),

    /**
     * Label Containing.
     */
    LABEL_CONTAINING("label.containing"),

    /**
     * Label Period.
     */
    LABEL_PERIOD("label.period"),

    /**
     * Title Box.
     */
    TITLE_BOX("title.box"),

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
     * CurrentDay.
     */
    DIALOG_CURRENT("dialog.CurrentDay"),

    /**
     * SelectedDay.
     */
    DIALOG_SELECTED("dialog.SelectedDay"),

    /**
     * NextMonth.
     */
    DIALOG_NEXTMONTH("dialog.NextMonth"),

    /**
     * PreviousMonth.
     */
    DIALOG_PREVMONTH("dialog.PreviousMonth"),

    /**
     * NextYear.
     */
    DIALOG_NEXTYEAR("dialog.NextYear"),

    /**
     * PreviousYear.
     */
    DIALOG_PREVYEAR("dialog.PreviousYear"),

    /**
     * NullSelect.
     */
    DIALOG_NULL("dialog.NullSelect");

    /**
     * The Period Map.
     */
    private static final Map<TethysDatePeriod, TethysResourceId> PERIOD_MAP = buildPeriodMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(OceanusException.class.getCanonicalName());

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
    TethysDateResource(final String pKeyName) {
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
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build period map.
     * @return the map
     */
    private static Map<TethysDatePeriod, TethysResourceId> buildPeriodMap() {
        /* Create the map and return it */
        Map<TethysDatePeriod, TethysResourceId> myMap = new EnumMap<>(TethysDatePeriod.class);
        myMap.put(TethysDatePeriod.ONEWEEK, PERIOD_ONEWEEK);
        myMap.put(TethysDatePeriod.FORTNIGHT, PERIOD_FORTNIGHT);
        myMap.put(TethysDatePeriod.ONEMONTH, PERIOD_ONEMONTH);
        myMap.put(TethysDatePeriod.QUARTERYEAR, PERIOD_QUARTERYEAR);
        myMap.put(TethysDatePeriod.HALFYEAR, PERIOD_HALFYEAR);
        myMap.put(TethysDatePeriod.ONEYEAR, PERIOD_ONEYEAR);
        myMap.put(TethysDatePeriod.CALENDARMONTH, PERIOD_CALENDARMONTH);
        myMap.put(TethysDatePeriod.CALENDARQUARTER, PERIOD_CALENDARQUARTER);
        myMap.put(TethysDatePeriod.CALENDARYEAR, PERIOD_CALENDARYEAR);
        myMap.put(TethysDatePeriod.FISCALYEAR, PERIOD_FISCALYEAR);
        myMap.put(TethysDatePeriod.DATESUPTO, PERIOD_DATESUPTO);
        myMap.put(TethysDatePeriod.CUSTOM, PERIOD_CUSTOM);
        myMap.put(TethysDatePeriod.ALLDATES, PERIOD_ALLDATES);
        return myMap;
    }

    /**
     * Obtain key for period.
     * @param pPeriod the period
     * @return the resource key
     */
    protected static TethysResourceId getKeyForPeriod(final TethysDatePeriod pPeriod) {
        return TethysResourceBuilder.getKeyForEnum(PERIOD_MAP, pPeriod);
    }
}
