/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.coeus.ui.panels;

import net.sourceforge.joceanus.coeus.data.CoeusCalendar;
import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.coeus.ui.report.CoeusReportType;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.tethys.date.TethysDate;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIDateButtonManager;

/**
 * Report Select State.
 */
public final class CoeusReportState {
    /**
     * ReportSelect.
     */
    private final CoeusReportSelect theSelect;

    /**
     * Calendar.
     */
    private CoeusCalendar theCalendar;

    /**
     * The market provider.
     */
    private CoeusMarketProvider theProvider;

    /**
     * The actual date.
     */
    private TethysDate theActualDate;

    /**
     * The selected date.
     */
    private TethysDate theSelectedDate;

    /**
     * The report type.
     */
    private CoeusReportType theType;

    /**
     * Constructor.
     * @param pSelect the selector
     * @param pCalendar the Calendar
     */
    CoeusReportState(final CoeusReportSelect pSelect,
                     final CoeusCalendar pCalendar) {
        /* Store parameters */
        theSelect = pSelect;
        theCalendar = pCalendar;
    }

    /**
     * Constructor.
     * @param pState state to copy from
     */
    CoeusReportState(final CoeusReportState pState) {
        theSelect = pState.theSelect;
        theCalendar = pState.theCalendar;
        theProvider = pState.getProvider();
        theSelectedDate = pState.getSelectedDate();
        theActualDate = pState.getActualDate();
        theType = pState.getType();
    }

    /**
     * Obtain the selected market provider.
     * @return the provider
     */
    CoeusMarketProvider getProvider() {
        return theProvider;
    }

    /**
     * Obtain the selected date.
     * @return the date
     */
    TethysDate getSelectedDate() {
        return theSelectedDate;
    }

    /**
     * Obtain the actual date.
     * @return the date
     */
    TethysDate getActualDate() {
        return theActualDate;
    }

    /**
     * Obtain the selected report type.
     * @return the report type
     */
    CoeusReportType getType() {
        return theType;
    }

    /**
     * Set the calendar.
     * @param pCalendar the calendar
     */
    void setCalendar(final CoeusCalendar pCalendar) {
        theCalendar = pCalendar;
        determineActualDate();
    }

    /**
     * Set new Date.
     * @param pSelect the Panel with the new date
     * @return true/false did a change occur
     */
    boolean setDate(final TethysUIDateButtonManager pSelect) {
        /* Obtain the date and adjust it */
        final TethysDate mySelected = pSelect.getSelectedDate();
        final TethysDate myDate = mySelected == null
                                                     ? null
                                                     : new TethysDate(mySelected);

        /* Record any change and report change */
        if (!MetisDataDifference.isEqual(myDate, theSelectedDate)) {
            theSelectedDate = myDate;
            determineActualDate();
            return true;
        }
        return false;
    }

    /**
     * Set new Market Provider.
     * @param pProvider the new provider
     * @return true/false did a change occur
     */
    boolean setProvider(final CoeusMarketProvider pProvider) {
        if (!pProvider.equals(theProvider)) {
            /* Store the new provider */
            theProvider = pProvider;
            return true;
        }
        return false;
    }

    /**
     * Set new Report Type.
     * @param pType the new type
     * @return true/false did a change occur
     */
    boolean setType(final CoeusReportType pType) {
        if (!pType.equals(theType)) {
            /* Store the new type */
            theType = pType;
            determineActualDate();
            return true;
        }
        return false;
    }

    /**
     * Determine actual date.
     */
    void determineActualDate() {
        theActualDate = theType.useAnnualDate()
                                                ? theCalendar.getEndOfYear(theSelectedDate)
                                                : theSelectedDate;
    }
}
