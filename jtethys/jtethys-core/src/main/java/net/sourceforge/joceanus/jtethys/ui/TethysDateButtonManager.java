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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JIconButton.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.time.LocalDate;

import net.sourceforge.jdatebutton.JDateBaseButton;
import net.sourceforge.jdatebutton.JDateBaseConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * DateButton Manager.
 * @param <B> the button type
 */
public abstract class TethysDateButtonManager<B extends JDateBaseButton>
        implements TethysEventProvider {
    /**
     * Value updated.
     */
    public static final int ACTION_NEW_VALUE = TethysScrollButtonManager.ACTION_NEW_VALUE;

    /**
     * Dialog prepare.
     */
    public static final int ACTION_DIALOG_PREPARE = TethysScrollButtonManager.ACTION_MENU_BUILD;

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * The Configuration.
     */
    private final JDateBaseConfig<B> theConfig;

    /**
     * Constructor.
     */
    protected TethysDateButtonManager(final JDateBaseConfig<B> pConfig,
                                      final TethysDateFormatter pFormatter) {
        /* Store parameters */
        theConfig = pConfig;

        /* Create event manager */
        theEventManager = new TethysEventManager();

        /* Add listener for locale changes */
        pFormatter.getEventRegistrar().addChangeListener(new LocaleListener(pFormatter));
    }

    /**
     * Obtain button.
     * @return the button
     */
    public abstract B getButton();

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Convert a localDate to a tethysDate.
     * @param pDate the localDate
     * @return the tethysDate
     */
    private TethysDate localToTethysDate(final LocalDate pDate) {
        return pDate == null
                             ? null
                             : new TethysDate(pDate, theConfig.getLocale());
    }

    /**
     * Convert a tethysDate to a localDate.
     * @param pDate the tethysDate
     * @return the localDate
     */
    private static LocalDate tethysToLocalDate(final TethysDate pDate) {
        return pDate == null
                             ? null
                             : pDate.getDate();
    }

    /**
     * Obtain the selected Date.
     * @return the selected Date
     */
    public TethysDate getSelectedDate() {
        return localToTethysDate(theConfig.getSelectedDate());
    }

    /**
     * Obtain the earliest Date.
     * @return the earliest Date
     */
    public TethysDate getEarliestDate() {
        return localToTethysDate(theConfig.getEarliestDate());
    }

    /**
     * Obtain the latest Date.
     * @return the latest Date
     */
    public TethysDate getLatestDate() {
        return localToTethysDate(theConfig.getLatestDate());
    }

    /**
     * Set selected Date.
     * @param pDate the selected date
     */
    public void setSelectedDate(final TethysDate pDate) {
        theConfig.setSelectedDate(tethysToLocalDate(pDate));
    }

    /**
     * Set earliest Date.
     * @param pDate the earliest date
     */
    public void setEarliestDate(final TethysDate pDate) {
        theConfig.setEarliestDate(tethysToLocalDate(pDate));
    }

    /**
     * Set latest Date.
     * @param pDate the latest date
     */
    public void setLatestDate(final TethysDate pDate) {
        theConfig.setLatestDate(tethysToLocalDate(pDate));
    }

    /**
     * Allow Null Date selection.
     * @return true/false
     */
    public boolean allowNullDateSelection() {
        return theConfig.allowNullDateSelection();
    }

    /**
     * Allow null date selection. If this flag is set an additional button will be displayed
     * allowing the user to explicitly select no date, thus setting the SelectedDate to null.
     * @param pAllowNullDateSelection true/false
     */
    public void setAllowNullDateSelection(final boolean pAllowNullDateSelection) {
        theConfig.setAllowNullDateSelection(pAllowNullDateSelection);
    }

    /**
     * handleDialogRequest.
     */
    public void handleDialogRequest() {
        theEventManager.fireActionEvent(ACTION_DIALOG_PREPARE, theConfig);
    }

    /**
     * handleNewValue.
     */
    public void handleNewValue() {
        theEventManager.fireActionEvent(ACTION_NEW_VALUE, getSelectedDate());
    }

    /**
     * Locale Listener class.
     */
    private final class LocaleListener
            implements TethysChangeEventListener {
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
        public void processChange(final TethysChangeEvent e) {
            theConfig.setTheLocale(theFormatter.getLocale());
            getButton().refreshText();
        }
    }
}
