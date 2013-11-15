/*******************************************************************************
 * jDateDay: Java Date Day
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdateday;

import java.util.Calendar;
import java.util.Locale;

/**
 * Fiscal Year representation.
 */
public enum JFiscalYear {
    /**
     * Fiscal Year based on UK Model.
     */
    UK(6, Calendar.APRIL),

    /**
     * April based start of year.
     */
    MARCH(Calendar.MARCH),

    /**
     * April based start of year.
     */
    APRIL(Calendar.APRIL),

    /**
     * July based start of year.
     */
    JULY(Calendar.JULY),

    /**
     * October based start of year.
     */
    OCTOBER(Calendar.OCTOBER),

    /**
     * Fiscal year based on the CalendarYear.
     */
    CALENDAR;

    /**
     * The day of the first day of fiscal year.
     */
    private final int theDay;

    /**
     * The month of the first day of fiscal year.
     */
    private final int theMonth;

    /**
     * Obtain the day.
     * @return the day
     */
    public int getFirstDay() {
        return theDay;
    }

    /**
     * Obtain the month.
     * @return the month
     */
    public int getFirstMonth() {
        return theMonth;
    }

    /**
     * Constructor.
     */
    private JFiscalYear() {
        this(Calendar.JANUARY);
    }

    /**
     * Constructor.
     * @param pMonth the first month of fiscal year
     */
    private JFiscalYear(final int pMonth) {
        this(1, pMonth);
    }

    /**
     * Constructor.
     * @param pDay the first day of fiscal year
     * @param pMonth the first month of fiscal year
     */
    private JFiscalYear(final int pDay,
                        final int pMonth) {
        theDay = pDay;
        theMonth = pMonth;
    }

    /**
     * Determine Fiscal Year for locale.
     * @param pLocale the locale
     * @return the fiscal year type
     */
    public static JFiscalYear determineFiscalYear(final Locale pLocale) {
        switch (pLocale.getCountry()) {
            case "GB":
                return UK;
            case "ZA":
                return MARCH;
            case "NZ":
                return APRIL;
            case "AU":
            case "EG":
                return JULY;
            case "CR":
                return OCTOBER;
            default:
                return CALENDAR;
        }
    }
}
