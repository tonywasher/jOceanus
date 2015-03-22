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

import java.time.LocalDate;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.jdatebutton.swing.JDateConfig;

/**
 * Class that extends {@link JDateConfig} to handle {@link JDateDay} objects.
 * @author Tony Washer
 */
public class JDateDayConfig
        extends JDateConfig {
    /**
     * Currently selected date (LocalDate).
     */
    private LocalDate theSelectedDate = null;

    /**
     * Currently selected date (DateDay).
     */
    private JDateDay theSelectedDateDay = null;

    /**
     * Current earliest date (LocalDate).
     */
    private LocalDate theEarliestDate = null;

    /**
     * Current earliest date (DateDay).
     */
    private JDateDay theEarliestDateDay = null;

    /**
     * Current latest date (LocalDate).
     */
    private LocalDate theLatestDate = null;

    /**
     * Current latest date (DateDay).
     */
    private JDateDay theLatestDateDay = null;

    /**
     * Constructor.
     */
    public JDateDayConfig() {
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public JDateDayConfig(final JDateDayFormatter pFormatter) {
        super(pFormatter);
        pFormatter.addChangeListener(new LocaleListener(pFormatter));
    }

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public JDateDay getSelectedDateDay() {
        /* Access selected date */
        LocalDate myDate = getSelectedDate();

        /* If we have changed selected date */
        if (isDateChanged(theSelectedDate, myDate)) {
            /* Store the selected date and create the DateDay version */
            theSelectedDate = myDate;
            theSelectedDateDay = (myDate == null)
                                                 ? null
                                                 : new JDateDay(myDate, getLocale());
        }

        /* Return the Selected DateDay */
        return theSelectedDateDay;
    }

    /**
     * Set selected DateDay.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final JDateDay pDate) {
        /* Set the selected DateDay */
        if (pDate == null) {
            setSelectedDate(null);
        } else {
            setSelectedDate(pDate.getDate());
        }
    }

    /**
     * Obtain the earliest DateDay.
     * @return the earliest DateDay
     */
    public JDateDay getEarliestDateDay() {
        /* Access earliest date */
        LocalDate myDate = getEarliestDate();

        /* If we have changed earliest date */
        if (isDateChanged(theEarliestDate, myDate)) {
            /* Store the earliest date and create the DateDay version */
            theEarliestDate = myDate;
            theEarliestDateDay = (myDate == null)
                                                 ? null
                                                 : new JDateDay(myDate, getLocale());
        }

        /* Return the Earliest DateDay */
        return theEarliestDateDay;
    }

    /**
     * Set earliest DateDay.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final JDateDay pDate) {
        /* Set the earliest DateDay */
        if (pDate == null) {
            setEarliestDate(null);
        } else {
            setEarliestDate(pDate.getDate());
        }
    }

    /**
     * Obtain the latest DateDay.
     * @return the latest DateDay
     */
    public JDateDay getLatestDateDay() {
        /* Access latest date */
        LocalDate myDate = getLatestDate();

        /* If we have changed latest date */
        if (isDateChanged(theLatestDate, myDate)) {
            /* Store the latest date and create the DateDay version */
            theLatestDate = myDate;
            theLatestDateDay = (myDate == null)
                                               ? null
                                               : new JDateDay(myDate, getLocale());
        }

        /* Return the Latest DateDay */
        return theLatestDateDay;
    }

    /**
     * Set latest DateDay.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final JDateDay pDate) {
        /* Set the latest DateDay */
        if (pDate == null) {
            setLatestDate(null);
        } else {
            setLatestDate(pDate.getDate());
        }
    }

    /**
     * Locale Listener class.
     */
    private final class LocaleListener
            implements ChangeListener {
        /**
         * The formatter.
         */
        private final JDateDayFormatter theFormatter;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        private LocaleListener(final JDateDayFormatter pFormatter) {
            theFormatter = pFormatter;
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            setTheLocale(theFormatter.getLocale());
            refreshText();
        }
    }
}
