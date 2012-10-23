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
package net.sourceforge.jArgo.jDateDay;

import java.util.Calendar;

/**
 * DatePeriod class representing standard date ranges.
 * @author Tony Washer
 */
public enum JDatePeriod {
    /**
     * OneWeek.
     */
    OneWeek(Calendar.DAY_OF_MONTH, 7),

    /**
     * Two Weeks.
     */
    Fortnight(Calendar.DAY_OF_MONTH, 14),

    /**
     * One Month.
     */
    OneMonth(Calendar.MONTH, 1),

    /**
     * Three Months.
     */
    QuarterYear(Calendar.MONTH, 3),

    /**
     * Six Months.
     */
    HalfYear(Calendar.MONTH, 6),

    /**
     * One Year.
     */
    OneYear(Calendar.YEAR, 1),

    /**
     * Unlimited.
     */
    Unlimited(-1, -1);

    /**
     * The calendar field.
     */
    private final int theField;

    /**
     * The adjustments amount.
     */
    private final int theAmount;

    /**
     * Obtain field.
     * @return the field
     */
    public int getField() {
        return theField;
    }

    /**
     * Obtain amount.
     * @param bForward forward/backward amount
     * @return the amount
     */
    public int getAmount(final boolean bForward) {
        return (bForward) ? theAmount : -theAmount;
    }

    /**
     * Constructor.
     * @param pField the Calendar field
     * @param pAmount the adjustment value
     */
    private JDatePeriod(final int pField,
                        final int pAmount) {
        /* Store values */
        theField = pField;
        theAmount = pAmount;
    }
}
