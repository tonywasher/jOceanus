/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.lethe.date.swing;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.jdatebutton.swing.JDateCellEditor;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;

/**
 * Cell editor for a {@link TethysDate} object extending {@link JDateCellEditor}.
 * @author Tony Washer
 * @deprecated as of 1.5.0 use
 * {@link net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableDateCell}
 */
@Deprecated
public class TethysSwingDateCellEditor
        extends JDateCellEditor {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5684093400307795179L;

    /**
     * The Date Button.
     */
    private final transient TethysSwingDateConfig theConfig;

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
        super(new TethysSwingDateConfig(pFormatter));
        theConfig = getDateConfig();
    }

    @Override
    public TethysSwingDateConfig getDateConfig() {
        return (TethysSwingDateConfig) super.getDateConfig();
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
    public Object getCellEditorValue() {
        return theConfig.getSelectedDateDay();
    }

    @Override
    public JComponent getTableCellEditorComponent(final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final int row,
                                                  final int column) {
        /* Convert value */
        Object myValue = value;
        if (myValue instanceof TethysDate) {
            TethysDate myDate = (TethysDate) myValue;
            myValue = myDate.getDate();
        }

        /* Return the button as the component */
        return super.getTableCellEditorComponent(table, myValue, isSelected, row, column);
    }
}
