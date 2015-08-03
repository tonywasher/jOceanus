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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/dateday/swing/JDateDayCellEditor.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.dateday.javafx;

import java.util.Locale;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import net.sourceforge.jdatebutton.javafx.JDateCellEditor;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * Cell editor for a {@link JDateDay} object extending {@link JDateCellEditor}.
 * @author Tony Washer
 * @param <T> the table data type
 */
public class JDateDayCell<T>
        extends TableCell<T, JDateDay>
        implements JDateCellEditor {
    /**
     * the button used to edit the date.
     */
    private final JDateDayButton theButton;

    /**
     * The Underlying Date Configuration.
     */
    private final JDateDayConfig theConfig;

    /**
     * Constructor.
     */
    public JDateDayCell() {
        /* Create the button */
        this(new JDateDayButton());
    }

    /**
     * Constructor.
     * @param pConfig the configuration
     */
    public JDateDayCell(final JDateDayConfig pConfig) {
        /* Create the button */
        this(new JDateDayButton(pConfig));
    }

    /**
     * Constructor.
     * @param pButton the underlying button
     */
    protected JDateDayCell(final JDateDayButton pButton) {
        /* Store the button and access the Dialog */
        theButton = pButton;
        theConfig = theButton.getDateConfig();
        theButton.setEditor(this);
    }

    /**
     * Obtain Date Configuration.
     * @return the date configuration
     */
    public JDateDayConfig getDateDayConfig() {
        return theConfig;
    }

    /**
     * Obtain Date Button.
     * @return the date button
     */
    public JDateDayButton getDateDayButton() {
        return theButton;
    }

    /**
     * Obtain SelectedDate.
     * @return the selected date
     */
    public JDateDay getSelectedDateDay() {
        return theButton.getSelectedDateDay();
    }

    /**
     * Set SelectedDate.
     * @param pDate the selected date
     */
    public void setSelectedDateDay(final JDateDay pDate) {
        theButton.setSelectedDateDay(pDate);
    }

    /**
     * Set EarliestDate.
     * @param pDate the earliest date
     */
    public void setEarliestDateDay(final JDateDay pDate) {
        theButton.setEarliestDateDay(pDate);
    }

    /**
     * Set LatestDate.
     * @param pDate the latest date
     */
    public void setLatestDateDay(final JDateDay pDate) {
        theButton.setLatestDateDay(pDate);
    }

    /**
     * Set Locale.
     * @param pLocale the Locale
     */
    public void setLocale(final Locale pLocale) {
        theButton.setLocale(pLocale);
    }

    /**
     * Set the date format.
     * @param pFormat the format string
     */
    public void setFormat(final String pFormat) {
        theButton.setFormat(pFormat);
    }

    @Override
    public void startEdit() {
        /* Perform preEdit tasks */
        if (preEditHook(getCurrentRow())) {
            /* Start the edit */
            super.startEdit();

            /* Prime the button with the correct value */
            theButton.setSelectedDateDay(getItem());

            /* Fire the button */
            theButton.fire();
        }
    }

    /**
     * obtain the current row.
     * @return the row (or null)
     */
    private T getCurrentRow() {
        /* Access list and determine size */
        ObservableList<T> myItems = getTableView().getItems();
        int mySize = myItems == null
                                     ? 0
                                     : myItems.size();

        /* Access list and determine size */
        TableRow<?> myRow = getTableRow();
        int myIndex = myRow == null
                                    ? -1
                                    : myRow.getIndex();

        /* Access explicit item */
        return (myIndex < 0) || (myIndex >= mySize)
                                                    ? null
                                                    : myItems.get(myIndex);
    }

    @Override
    public void fireEditingCanceledEvent() {
        cancelEdit();
    }

    @Override
    public void fireEditingStoppedEvent() {
        commitEdit(theButton.getSelectedDateDay());
        postCommitHook(getCurrentRow());
    }

    @Override
    public void updateItem(final JDateDay pValue,
                           final boolean pEmpty) {
        /* Update correctly */
        super.updateItem(pValue, pEmpty);

        /* Defaults for empty cell */
        if (pEmpty) {
            setText(null);

            /* Format the cell */
        } else {
            /* Set Text details */
            setText(pValue != null
                                   ? theConfig.formatDate(pValue.getDate())
                                   : null);
        }
    }

    /**
     * Method that should be overridden to provide control over editing on a row by row basis.
     * <p>
     * The configuration of the Cell can be set according to data within the row, and the edit can
     * be rejected by returning false
     * @param pRow the row
     * @return continue edit true/false
     */
    protected boolean preEditHook(final T pRow) {
        /* Allow edit to take place */
        return true;
    }

    /**
     * Method that should be overridden to provide control over postProcessing of a commit.
     * @param pRow the row
     */
    protected void postCommitHook(final T pRow) {
        /* No action unless overridden */
    }
}
