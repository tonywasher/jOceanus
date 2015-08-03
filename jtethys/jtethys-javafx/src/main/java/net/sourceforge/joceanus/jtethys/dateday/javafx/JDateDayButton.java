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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/dateday/swing/JDateDayButton.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.dateday.javafx;

import javafx.beans.property.ObjectProperty;
import net.sourceforge.jdatebutton.javafx.JDateButton;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;

/**
 * Extension class for a {@link JDateButton} to handle {@link JDateDay} objects.
 * @author Tony Washer
 */
public class JDateDayButton
        extends JDateButton {
    /**
     * Name of the Date property.
     */
    public static final String PROPERTY_DATE = "SelectedDateDay";

    /**
     * DateDayConfig.
     */
    private final transient JDateDayConfig theConfig;

    /**
     * Constructor.
     */
    public JDateDayButton() {
        /* Use default configuration */
        this(new JDateDayConfig());
    }

    /**
     * Constructor.
     * @param pConfig the configuration
     */
    public JDateDayButton(final JDateDayConfig pConfig) {
        /* Call super constructor */
        super(pConfig);

        /* Store reference for this configuration */
        theConfig = pConfig;
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public JDateDayButton(final JDateDayFormatter pFormatter) {
        /* Create configuration for this formatter */
        this(new JDateDayConfig(pFormatter));
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
     * Obtain SelectedDateDay property.
     * @return the selected date
     */
    public ObjectProperty<JDateDay> selectedDateDayProperty() {
        return theConfig.selectedDateDayProperty();
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
}
