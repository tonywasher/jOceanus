/*******************************************************************************
 * jDateDay: Java Date Day
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
package net.sourceforge.jOceanus.jDateDay;

import java.util.Calendar;

import net.sourceforge.JDateButton.JDateButton;

/**
 * Extension class for a {@link JDateButton} to handle {@link JDateDay} objects.
 * @author Tony Washer
 */
public class JDateDayButton extends JDateButton {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 8996849681932697833L;

    /**
     * Name of the Date property.
     */
    public static final String PROPERTY_DATE = "SelectedDateDay";

    /**
     * DateDayConfig.
     */
    private transient JDateDayConfig theConfig = new JDateDayConfig();

    /**
     * Published DateDay value.
     */
    private transient JDateDay thePublishedDate = null;

    /**
     * Constructor.
     */
    public JDateDayButton() {
        /* Call super constructor */
        super(new JDateDayConfig());

        /* Access reference for the configuration */
        theConfig = (JDateDayConfig) super.getDateConfig();
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public JDateDayButton(final JDateDayFormatter pFormatter) {
        /* Call super constructor */
        super(new JDateDayConfig(pFormatter));

        /* Access reference for the configuration */
        theConfig = (JDateDayConfig) super.getDateConfig();
    }

    @Override
    public JDateDayConfig getDateConfig() {
        return theConfig;
    }

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public JDateDay getSelectedDateDay() {
        return theConfig.getSelectedDateDay();
    }

    /**
     * Set selected DateDay.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final JDateDay pDate) {
        theConfig.setSelectedDateDay(pDate);
    }

    /**
     * Set earliest DateDay.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final JDateDay pDate) {
        theConfig.setEarliestDateDay(pDate);
    }

    /**
     * Set latest DateDay.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final JDateDay pDate) {
        theConfig.setLatestDateDay(pDate);
    }

    @Override
    protected void fireDatePropertyChange(final Calendar pOldValue,
                                          final Calendar pNewValue) {
        /* Access new value as DateDay */
        JDateDay myNew = null;
        if (pNewValue != null) {
            myNew = new JDateDay(pNewValue, getLocale());
        }

        /* Determine whether a change has occurred */
        if (!JDateDay.isDifferent(thePublishedDate, myNew)) {
            return;
        }

        /* Record the new date and create a copy */
        JDateDay myOld = thePublishedDate;
        thePublishedDate = myNew;

        /* Fire the property change */
        firePropertyChange(PROPERTY_DATE, myOld, myNew);
    }
}
