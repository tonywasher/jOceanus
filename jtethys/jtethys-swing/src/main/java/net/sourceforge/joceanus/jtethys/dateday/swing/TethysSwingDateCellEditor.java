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
package net.sourceforge.joceanus.jtethys.dateday.swing;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.jdatebutton.swing.JDateCellEditor;
import net.sourceforge.joceanus.jtethys.dateday.TethysDate;
import net.sourceforge.joceanus.jtethys.dateday.TethysDateFormatter;

/**
 * Cell editor for a {@link TethysDate} object extending {@link JDateCellEditor}.
 * @author Tony Washer
 */
public class TethysSwingDateCellEditor
        extends JDateCellEditor {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5684093400307795179L;

    /**
     * The Date Button.
     */
    private final TethysSwingDateButton theButton;

    /**
     * Constructor.
     */
    public TethysSwingDateCellEditor() {
        /* Create a new configuration */
        this(new TethysDateFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public TethysSwingDateCellEditor(final TethysDateFormatter pFormatter) {
        /* Create a new configuration */
        super(new TethysSwingDateButton(pFormatter));
        theButton = (TethysSwingDateButton) super.getDateButton();
    }

    @Override
    public TethysSwingDateConfig getDateConfig() {
        return theButton.getDateConfig();
    }

    /**
     * Obtain the selected DateDay.
     * @return the selected DateDay
     */
    public TethysDate getSelectedDateDay() {
        return theButton.getSelectedDateDay();
    }

    /**
     * Set selected DateDay.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final TethysDate pDate) {
        theButton.setSelectedDateDay(pDate);
    }

    /**
     * Set earliest DateDay.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final TethysDate pDate) {
        theButton.setEarliestDateDay(pDate);
    }

    /**
     * Set latest DateDay.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final TethysDate pDate) {
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
        if (value instanceof TethysDate) {
            TethysDate myDate = (TethysDate) value;
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
