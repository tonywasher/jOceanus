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
import java.util.ResourceBundle;

/**
 * DatePeriod class representing standard date ranges.
 * @author Tony Washer
 */
public enum JDatePeriod {
    /**
     * OneWeek.
     */
    ONEWEEK(Calendar.DAY_OF_MONTH, 7),

    /**
     * Two Weeks.
     */
    FORTNIGHT(Calendar.DAY_OF_MONTH, 14),

    /**
     * One Month.
     */
    ONEMONTH(Calendar.MONTH, 1),

    /**
     * Three Months.
     */
    QUARTERYEAR(Calendar.MONTH, 3),

    /**
     * Six Months.
     */
    HALFYEAR(Calendar.MONTH, 6),

    /**
     * One Year.
     */
    ONEYEAR(Calendar.YEAR, 1),

    /**
     * Financial Year.
     */
    // FinancialYear(Calendar.YEAR, 1),

    /**
     * Custom.
     */
    CUSTOM(-1, -1),

    /**
     * All.
     */
    ALLDATES(-1, -1);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(JDatePeriod.class.getName());

    /**
     * The String name.
     */
    private String theName;

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
        return (bForward)
                ? theAmount
                : -theAmount;
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

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }
}
