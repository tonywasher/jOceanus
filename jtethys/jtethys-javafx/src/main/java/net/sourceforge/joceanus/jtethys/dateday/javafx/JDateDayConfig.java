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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/dateday/swing/JDateDayConfig.java $
 * $Revision: 585 $
 * $Author: Tony $
 * $Date: 2015-03-30 06:24:29 +0100 (Mon, 30 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.dateday.javafx;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.sourceforge.jdatebutton.javafx.JDateConfig;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;

/**
 * Class that extends {@link JDateConfig} to handle {@link JDateDay} objects.
 * @author Tony Washer
 */
public class JDateDayConfig
        extends JDateConfig {
    /**
     * Currently selected date (DateDay).
     */
    private final ObjectProperty<JDateDay> theSelectedDateDay;

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
        theSelectedDateDay = new SimpleObjectProperty<JDateDay>(this, JDateDayButton.PROPERTY_DATE);
        addListener();
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public JDateDayConfig(final JDateDayFormatter pFormatter) {
        super(pFormatter);
        theSelectedDateDay = new SimpleObjectProperty<JDateDay>(this, JDateDayButton.PROPERTY_DATE);
        pFormatter.getEventRegistrar().addChangeListener(new LocaleListener(pFormatter));
        addListener();
    }

    /**
     * Add listener.
     */
    private void addListener() {
        /* Access selected date */
        ObjectProperty<LocalDate> myProperty = selectedDateProperty();
        myProperty.addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(final ObservableValue<? extends LocalDate> pProperty,
                                final LocalDate pOldValue,
                                final LocalDate pNewValue) {
                JDateDay myDate = pNewValue == null
                                                    ? null
                                                    : new JDateDay(pNewValue, getLocale());
                theSelectedDateDay.set(myDate);
            }
        });
    }

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public JDateDay getSelectedDateDay() {
        return theSelectedDateDay.get();
    }

    /**
     * Obtain SelectedDate.
     * @return the selected date
     */
    public ObjectProperty<JDateDay> selectedDateDayProperty() {
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
            implements JOceanusChangeEventListener {
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
        public void processChangeEvent(final JOceanusChangeEvent e) {
            setTheLocale(theFormatter.getLocale());
            refreshText();
        }
    }
}
