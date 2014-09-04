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

import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for JDateDay package.
 */
public enum JDateDayResource implements ResourceId {
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
    PERIOD_ALLDATES("period.allDates");

    /**
     * The Bundle name.
     */
    private final static String BUNDLE_NAME = ResourceMgr.getPackageBundle(JOceanusException.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private JDateDayResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "JDateDay";
    }

    @Override
    public String getBundleName() {
        return BUNDLE_NAME;
    }

    /**
     * Obtain key for period.
     * @param pPeriod the period
     * @return the resource key
     */
    protected static JDateDayResource getKeyForPeriod(final JDatePeriod pPeriod) {
        switch (pPeriod) {
            case ONEWEEK:
                return PERIOD_ONEWEEK;
            case FORTNIGHT:
                return PERIOD_FORTNIGHT;
            case ONEMONTH:
                return PERIOD_ONEMONTH;
            case QUARTERYEAR:
                return PERIOD_QUARTERYEAR;
            case HALFYEAR:
                return PERIOD_HALFYEAR;
            case ONEYEAR:
                return PERIOD_ONEYEAR;
            case CALENDARMONTH:
                return PERIOD_CALENDARMONTH;
            case CALENDARQUARTER:
                return PERIOD_CALENDARQUARTER;
            case CALENDARYEAR:
                return PERIOD_CALENDARYEAR;
            case FISCALYEAR:
                return PERIOD_FISCALYEAR;
            case DATESUPTO:
                return PERIOD_DATESUPTO;
            case CUSTOM:
                return PERIOD_CUSTOM;
            case ALLDATES:
                return PERIOD_ALLDATES;
            default:
                return null;
        }
    }
}
