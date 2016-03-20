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
package net.sourceforge.joceanus.jtethys.date.javafx;

import javafx.beans.property.ObjectProperty;
import net.sourceforge.jdatebutton.javafx.JDateButton;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;

/**
 * Extension class for a {@link JDateButton} to handle {@link TethysDate} objects.
 * @author Tony Washer
 * @deprecated as of 1.5.0 use
 * {@link net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateButtonManager}
 */
@Deprecated
public class TethysFXDateButton
        extends JDateButton {
    /**
     * Name of the Date property.
     */
    public static final String PROPERTY_DATEDAY = "SelectedDateDay";

    /**
     * DateDayConfig.
     */
    private final transient TethysFXDateConfig theConfig;

    /**
     * Constructor.
     */
    public TethysFXDateButton() {
        /* Use default configuration */
        this(new TethysFXDateConfig());
    }

    /**
     * Constructor.
     * @param pConfig the configuration
     */
    public TethysFXDateButton(final TethysFXDateConfig pConfig) {
        /* Call super constructor */
        super(pConfig);

        /* Store reference for this configuration */
        theConfig = pConfig;
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public TethysFXDateButton(final TethysDateFormatter pFormatter) {
        /* Create configuration for this formatter */
        this(new TethysFXDateConfig(pFormatter));
    }

    @Override
    public TethysFXDateConfig getDateConfig() {
        return theConfig;
    }

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public TethysDate getSelectedDateDay() {
        return theConfig.getSelectedDateDay();
    }

    /**
     * Obtain SelectedDateDay property.
     * @return the selected date
     */
    public ObjectProperty<TethysDate> selectedDateDayProperty() {
        return theConfig.selectedDateDayProperty();
    }

    /**
     * Set selected DateDay.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final TethysDate pDate) {
        theConfig.setSelectedDateDay(pDate);
    }

    /**
     * Set earliest DateDay.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final TethysDate pDate) {
        theConfig.setEarliestDateDay(pDate);
    }

    /**
     * Set latest DateDay.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final TethysDate pDate) {
        theConfig.setLatestDateDay(pDate);
    }
}
