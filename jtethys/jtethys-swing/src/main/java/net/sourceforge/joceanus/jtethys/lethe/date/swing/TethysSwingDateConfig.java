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
package net.sourceforge.joceanus.jtethys.lethe.date.swing;

import java.time.LocalDate;

import net.sourceforge.jdatebutton.swing.JDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysEventListener;

/**
 * Class that extends {@link JDateConfig} to handle {@link TethysDate} objects.
 * @author Tony Washer
 * @deprecated as of 1.5.0 use
 * {@link net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager}
 */
@Deprecated
public class TethysSwingDateConfig
        extends JDateConfig {
    /**
     * Currently selected date (LocalDate).
     */
    private LocalDate theSelectedDate = null;

    /**
     * Currently selected date (DateDay).
     */
    private TethysDate theSelectedDateDay = null;

    /**
     * Current earliest date (LocalDate).
     */
    private LocalDate theEarliestDate = null;

    /**
     * Current earliest date (DateDay).
     */
    private TethysDate theEarliestDateDay = null;

    /**
     * Current latest date (LocalDate).
     */
    private LocalDate theLatestDate = null;

    /**
     * Current latest date (DateDay).
     */
    private TethysDate theLatestDateDay = null;

    /**
     * Constructor.
     */
    public TethysSwingDateConfig() {
        /* Nothing to do */
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public TethysSwingDateConfig(final TethysDateFormatter pFormatter) {
        super(pFormatter);
        pFormatter.getEventRegistrar().addEventListener(new LocaleListener(pFormatter));
    }

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public TethysDate getSelectedDateDay() {
        /* Access selected date */
        LocalDate myDate = getSelectedDate();

        /* If we have changed selected date */
        if (isDateChanged(theSelectedDate, myDate)) {
            /* Store the selected date and create the DateDay version */
            theSelectedDate = myDate;
            theSelectedDateDay = (myDate == null)
                                                  ? null
                                                  : new TethysDate(myDate, getLocale());
        }

        /* Return the Selected DateDay */
        return theSelectedDateDay;
    }

    /**
     * Set selected DateDay.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final TethysDate pDate) {
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
    public TethysDate getEarliestDateDay() {
        /* Access earliest date */
        LocalDate myDate = getEarliestDate();

        /* If we have changed earliest date */
        if (isDateChanged(theEarliestDate, myDate)) {
            /* Store the earliest date and create the DateDay version */
            theEarliestDate = myDate;
            theEarliestDateDay = (myDate == null)
                                                  ? null
                                                  : new TethysDate(myDate, getLocale());
        }

        /* Return the Earliest DateDay */
        return theEarliestDateDay;
    }

    /**
     * Set earliest DateDay.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final TethysDate pDate) {
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
    public TethysDate getLatestDateDay() {
        /* Access latest date */
        LocalDate myDate = getLatestDate();

        /* If we have changed latest date */
        if (isDateChanged(theLatestDate, myDate)) {
            /* Store the latest date and create the DateDay version */
            theLatestDate = myDate;
            theLatestDateDay = (myDate == null)
                                                ? null
                                                : new TethysDate(myDate, getLocale());
        }

        /* Return the Latest DateDay */
        return theLatestDateDay;
    }

    /**
     * Set latest DateDay.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final TethysDate pDate) {
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
    @Deprecated
    private final class LocaleListener
            implements TethysEventListener<TethysDateEvent> {
        /**
         * The formatter.
         */
        private final TethysDateFormatter theFormatter;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        private LocaleListener(final TethysDateFormatter pFormatter) {
            theFormatter = pFormatter;
        }

        @Override
        public void handleEvent(final TethysEvent<TethysDateEvent> e) {
            setTheLocale(theFormatter.getLocale());
            refreshText();
        }
    }
}
