/*******************************************************************************
 * JDateDay: Java Date Day
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
package net.sourceforge.JDateDay;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.JDateButton.JDateCellEditor;

/**
 * Cell editor for a {@link JDateDay} object extending {@link JDateCellEditor}.
 * @author Tony Washer
 */
public class JDateDayCellEditor extends JDateCellEditor {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5684093400307795179L;

    /**
     * The Date Button.
     */
    private final JDateDayButton theButton;

    /**
     * Constructor.
     */
    public JDateDayCellEditor() {
        /* Create a new configuration */
        super(new JDateDayButton());
        theButton = (JDateDayButton) super.getDateButton();
    }

    @Override
    public JDateDayConfig getDateConfig() {
        return theButton.getDateConfig();
    }

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public JDateDay getSelectedDateDay() {
        return theButton.getSelectedDateDay();
    }

    /**
     * Set selected DateDay.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final JDateDay pDate) {
        theButton.setSelectedDateDay(pDate);
    }

    /**
     * Set earliest DateDay.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final JDateDay pDate) {
        theButton.setEarliestDateDay(pDate);
    }

    /**
     * Set latest DateDay.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final JDateDay pDate) {
        theButton.setLatestDateDay(pDate);
    }

    @Override
    public Object getCellEditorValue() {
        return theButton.getSelectedDateDay();
    }

    @Override
    public JComponent getTableCellEditorComponent(final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final int row,
                                                  final int column) {
        /* If the value is the date */
        if (value instanceof JDateDay) {
            JDateDay myDate = (JDateDay) value;
            /* Set the selected date */
            theButton.setSelectedDateDay(myDate);

            /* else set the selected date to null */
        } else {
            theButton.setSelectedDate(null);
        }

        /* Return the button as the component */
        return theButton;
    }
}
