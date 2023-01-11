/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.button;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIDateButton;

/**
 * DateButton Manager.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *   <li>TethysUIEvent.NEWVALUE is fired when a new date value is selected.
 *   <li>TethysUIEvent.EDITFOCUSLOST is fired when the dialog is cancelled without a value being selected.
 * </ul>
 */
public interface TethysUIDateButtonManager
        extends TethysUIDateButton, TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain the configuration.
     * @return the configuration
     */
    TethysDateConfig getConfig();

    /**
     * Obtain the selected Date.
     * @return the selected Date
     */
    TethysDate getSelectedDate();

    /**
     * Obtain the earliest Date.
     * @return the earliest Date
     */
    TethysDate getEarliestDate();

    /**
     * Obtain the latest Date.
     * @return the latest Date
     */
    TethysDate getLatestDate();

    /**
     * Set selected Date.
     * @param pDate the selected date
     */
    void setSelectedDate(TethysDate pDate);

    /**
     * Get button text.
     * @return the text
     */
    String getText();

    /**
     * Set earliest Date.
     * @param pDate the earliest date
     */
    void setEarliestDate(TethysDate pDate);

    /**
     * Set latest Date.
     * @param pDate the latest date
     */
    void setLatestDate(TethysDate pDate);

    /**
     * Allow Null Date selection.
     * @return true/false
     */
    boolean allowNullDateSelection();

    /**
     * Allow null date selection. If this flag is set an additional button will be displayed
     * allowing the user to explicitly select no date, thus setting the SelectedDate to null.
     * @param pAllowNullDateSelection true/false
     */
    void setAllowNullDateSelection(boolean pAllowNullDateSelection);

    /**
     * Show Narrow Days. If this flag is set Days are show in narrow rather than short form.
     * @param pShowNarrowDays true/false
     */
    void setShowNarrowDays(boolean pShowNarrowDays);
}
