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
package net.sourceforge.joceanus.jtethys.date.swing;

import java.time.LocalDate;

import net.sourceforge.jdatebutton.swing.JDateButton;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;

/**
 * Extension class for a {@link JDateButton} to handle {@link TethysDate} objects.
 * @author Tony Washer
 */
public class TethysSwingDateButton
        extends JDateButton {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 8996849681932697833L;

    /**
     * Name of the Date property.
     */
    public static final String PROPERTY_DATEDAY = "SelectedDateDay";

    /**
     * DateDayConfig.
     */
    private transient TethysSwingDateConfig theConfig = new TethysSwingDateConfig();

    /**
     * Published DateDay value.
     */
    private transient TethysDate thePublishedDate = null;

    /**
     * Constructor.
     */
    public TethysSwingDateButton() {
        /* Call super constructor */
        super(new TethysSwingDateConfig());

        /* Access reference for the configuration */
        theConfig = (TethysSwingDateConfig) super.getDateConfig();
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public TethysSwingDateButton(final TethysDateFormatter pFormatter) {
        /* Call super constructor */
        super(new TethysSwingDateConfig(pFormatter));

        /* Access reference for the configuration */
        theConfig = (TethysSwingDateConfig) super.getDateConfig();
    }

    @Override
    public TethysSwingDateConfig getDateConfig() {
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

    @Override
    protected void fireDatePropertyChange(final LocalDate pOldValue,
                                          final LocalDate pNewValue) {
        /* Access new value as DateDay */
        TethysDate myNew = null;
        if (pNewValue != null) {
            myNew = new TethysDate(pNewValue, getLocale());
        }

        /* Determine whether a change has occurred */
        if (!TethysDate.isDifferent(thePublishedDate, myNew)) {
            return;
        }

        /* Record the new date and create a copy */
        TethysDate myOld = thePublishedDate;
        thePublishedDate = myNew;

        /* Fire the property change */
        firePropertyChange(PROPERTY_DATEDAY, myOld, myNew);
    }
}
