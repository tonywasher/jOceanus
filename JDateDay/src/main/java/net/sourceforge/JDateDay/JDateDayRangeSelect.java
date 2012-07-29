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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 * @author Tony Washer
 */
public class JDateDayRangeSelect extends JPanel {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = -1844318324805283367L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Period Box width.
     */
    private static final int PERIOD_WIDTH = 150;

    /**
     * Period Box height.
     */
    private static final int PERIOD_HEIGHT = 25;

    /**
     * Name of the Range property.
     */
    public static final String PROPERTY_RANGE = "SelectedDateDayRange";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(JDateDayRangeSelect.class
            .getName());

    /**
     * Text for Next Button.
     */
    private static final String NLS_NEXT = NLS_BUNDLE.getString("NextButton");

    /**
     * Text for Previous Button.
     */
    private static final String NLS_PREV = NLS_BUNDLE.getString("PrevButton");

    /**
     * Text for Start Label.
     */
    private static final String NLS_START = NLS_BUNDLE.getString("StartLabel");

    /**
     * Text for Period Label.
     */
    private static final String NLS_PERIOD = NLS_BUNDLE.getString("PeriodLabel");

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("BoxTitle");

    /**
     * The Date button.
     */
    private final JDateDayButton theDateButton;

    /**
     * The Period ComboBox.
     */
    private final JComboBox thePeriodBox;

    /**
     * The Next button.
     */
    private final JButton theNextButton;

    /**
     * The Previous button.
     */
    private final JButton thePrevButton;

    /**
     * The published range.
     */
    private transient JDateDayRange thePublishedRange = null;

    /**
     * The First select-able date.
     */
    private transient JDateDay theFirstDate = null;

    /**
     * The Last select-able date.
     */
    private transient JDateDay theFinalDate = null;

    /**
     * The Active state.
     */
    private transient JDateRangeState theState = null;

    /**
     * The Saved state.
     */
    private transient JDateRangeState theSavePoint = null;

    /**
     * Are we refreshing data.
     */
    private boolean refreshingData = false;

    /**
     * The Locale.
     */
    private Locale theLocale = null;

    /**
     * Obtain selected DateRange.
     * @return the selected date range
     */
    public JDateDayRange getRange() {
        return theState.getRange();
    }

    /**
     * Constructor.
     */
    public JDateDayRangeSelect() {
        /* Create the listener */
        DateListener myListener = new DateListener();

        /* Create the boxes */
        thePeriodBox = new JComboBox();
        thePeriodBox.setMaximumSize(new Dimension(PERIOD_WIDTH, PERIOD_HEIGHT));

        /* Create the DateButton */
        theDateButton = new JDateDayButton();

        /* Set the locale */
        setLocale(Locale.getDefault());

        /* Add the PeriodTypes to the period box */
        for (JDatePeriod myPeriod : JDatePeriod.values()) {
            thePeriodBox.addItem(myPeriod);
        }

        /* Create the labels */
        JLabel myStartLabel = new JLabel(NLS_START);
        JLabel myPeriodLabel = new JLabel(NLS_PERIOD);

        /* Create the buttons */
        theNextButton = new JButton(NLS_NEXT);
        thePrevButton = new JButton(NLS_PREV);

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myStartLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDateButton);
        add(Box.createHorizontalGlue());
        add(myPeriodLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePeriodBox);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theNextButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePrevButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create initial state and limit the selection to the Range */
        theState = new JDateRangeState();
        setInitialRange();
        theState.buildRange();

        /* Add the listeners for item changes */
        thePeriodBox.addItemListener(myListener);
        theNextButton.addActionListener(myListener);
        thePrevButton.addActionListener(myListener);
        theDateButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
    }

    @Override
    public final void setLocale(final Locale pLocale) {
        /* Record the locale */
        theLocale = pLocale;
        theDateButton.setLocale(pLocale);

        /* Pass the call onwards */
        super.setLocale(pLocale);
    }

    /**
     * Set the date format.
     * @param pFormat the format string
     */
    public void setFormat(final String pFormat) {
        theDateButton.setFormat(pFormat);
    }

    /**
     * Set the initial range for the control.
     */
    private void setInitialRange() {
        /* Record total possible range */
        theFirstDate = null;
        theFinalDate = null;

        /* Set range into DateButton */
        theDateButton.setEarliestDateDay(theFirstDate);
        theDateButton.setLatestDateDay(theFinalDate);

        /* Adjust the overall range */
        theState.adjustOverallRange();
    }

    /**
     * Set the overall range for the control.
     * @param pRange the range
     */
    public final void setOverallRange(final JDateDayRange pRange) {
        /* Record total possible range */
        theFirstDate = (pRange == null) ? null : pRange.getStart();
        theFinalDate = (pRange == null) ? null : pRange.getEnd();

        /* Set range into DateButton */
        theDateButton.setEarliestDateDay(theFirstDate);
        theDateButton.setLatestDateDay(theFinalDate);

        /* Adjust the overall range */
        theState.adjustOverallRange();
        notifyChangedRange();
    }

    /**
     * Copy date selection from other box.
     * @param pSource the source box
     */
    public void setSelection(final JDateDayRangeSelect pSource) {
        /* Access the state */
        JDateRangeState myState = pSource.theState;

        /* Set the refreshing data flag */
        refreshingData = true;

        /* Accept this state */
        theState = new JDateRangeState(myState);

        /* Build the range */
        theState.buildRange();
        notifyChangedRange();

        /* Reset the refreshing data flag */
        refreshingData = false;
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new JDateRangeState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Set the refreshing data flag */
        refreshingData = true;

        /* Restore the savePoint */
        theState = new JDateRangeState(theSavePoint);

        /* Build the range and apply the state */
        theState.buildRange();
        notifyChangedRange();

        /* Reset the refreshing data flag */
        refreshingData = false;
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        /* Lock/Unlock the selection */
        theDateButton.setEnabled(pEnable);
        thePeriodBox.setEnabled(pEnable);
        theNextButton.setEnabled(pEnable && theState.isNextOK());
        thePrevButton.setEnabled(pEnable && theState.isPrevOK());
    }

    /**
     * Notify changes to selected range.
     */
    private void notifyChangedRange() {
        /* Determine whether a change has occurred */
        JDateDayRange myNew = getRange();
        if (!JDateDayRange.isDifferent(thePublishedRange, myNew)) {
            return;
        }

        /* Record the new range and create a copy */
        JDateDayRange myOld = thePublishedRange;
        thePublishedRange = myNew;
        myNew = new JDateDayRange(myNew);

        /* Fire the property change */
        firePropertyChange(PROPERTY_RANGE, myOld, myNew);
    }

    /**
     * The Date Listener.
     */
    private final class DateListener implements ActionListener, PropertyChangeListener, ItemListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            JDatePeriod myPeriod = null;

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the period box */
            if ((thePeriodBox.equals(evt.getSource())) && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Determine the new period */
                myPeriod = (JDatePeriod) evt.getItem();

                /* Apply period and build the range */
                theState.setPeriod(myPeriod);
                notifyChangedRange();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object src = evt.getSource();

            /* If this event relates to the next button */
            if (theNextButton.equals(src)) {
                /* Set the next date */
                theState.setNextDate();
                notifyChangedRange();

                /* If this event relates to the previous button */
            } else if (thePrevButton.equals(src)) {
                /* Set the previous date */
                theState.setPreviousDate();
                notifyChangedRange();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            /* if this date relates to the Date button */
            if (theDateButton.equals(evt.getSource())) {
                /* Access the value */
                theState.setDate(theDateButton);
                notifyChangedRange();
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class JDateRangeState {
        /**
         * The start Date.
         */
        private JDateDay theStartDate = null;

        /**
         * The Date Period.
         */
        private JDatePeriod thePeriod = null;

        /**
         * The calculated Range.
         */
        private JDateDayRange theRange = null;

        /**
         * Can we select a next period within the available range.
         */
        private boolean isNextOK = false;

        /**
         * Can we select a previous period within the available range.
         */
        private boolean isPrevOK = false;

        /**
         * Get the start date.
         * @return the start date
         */
        private JDateDay getStartDate() {
            return theStartDate;
        }

        /**
         * Get the DatePeriod.
         * @return the date period
         */
        private JDatePeriod getPeriod() {
            return thePeriod;
        }

        /**
         * Get the selected range.
         * @return the selected range
         */
        private JDateDayRange getRange() {
            return theRange;
        }

        /**
         * Can we select a next period within the available range.
         * @return true/false
         */
        private boolean isNextOK() {
            return isNextOK;
        }

        /**
         * Can we select a previous period within the available range.
         * @return true/false
         */
        private boolean isPrevOK() {
            return isPrevOK;
        }

        /**
         * Constructor.
         */
        private JDateRangeState() {
            theStartDate = new JDateDay(theLocale);
            thePeriod = JDatePeriod.OneMonth;

            /* Make sure that we do not go beyond the date range */
            if ((theFinalDate != null) && (theStartDate.compareTo(theFinalDate) > 0)) {
                theStartDate = theFinalDate;
            }
            if ((theFirstDate != null) && (theStartDate.compareTo(theFirstDate) < 0)) {
                theStartDate = theFirstDate;
            }
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private JDateRangeState(final JDateRangeState pState) {
            theStartDate = new JDateDay(pState.getStartDate());
            thePeriod = pState.getPeriod();
        }

        /**
         * Set new Period.
         * @param pPeriod the new period
         */
        private void setPeriod(final JDatePeriod pPeriod) {
            /* Adjust the period and build the new range */
            thePeriod = pPeriod;
            buildRange();
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         */
        private void setDate(final JDateDayButton pButton) {
            /* Adjust the date and build the new range */
            JDateDay myDate = pButton.getSelectedDateDay();
            theStartDate = myDate;
            buildRange();
        }

        /**
         * Set next Date.
         */
        private void setNextDate() {
            /* Adjust the date and build the new range */
            nextPeriod();
            buildRange();
        }

        /**
         * Set previous Date.
         */
        private void setPreviousDate() {
            /* Adjust the date and build the new range */
            previousPeriod();
            buildRange();
        }

        /**
         * adjust the overall range.
         */
        private void adjustOverallRange() {
            /* Make sure that we are within the range */
            if ((theFinalDate != null) && (theStartDate.compareTo(theFinalDate) > 0)) {
                theStartDate = theFinalDate;
            }
            if ((theFirstDate != null) && (theStartDate.compareTo(theFirstDate) < 0)) {
                theStartDate = theFirstDate;
            }

            /* Build the range */
            buildRange();
        }

        /**
         * Build the range represented by the selection.
         */
        private void buildRange() {
            JDateDay myEnd;

            /* Previous is only allowed if we have not hit the first date */
            isPrevOK = (theFirstDate == null) || (theFirstDate.compareTo(theStartDate) != 0);

            /* If we are unlimited */
            if (thePeriod == JDatePeriod.Unlimited) {
                /* Set end date as last possible date */
                myEnd = theFinalDate;

                /* Note that next is not allowed */
                isNextOK = false;

                /* else we have to calculate the date */
            } else {
                /* Initialise the end date */
                myEnd = new JDateDay(theStartDate);

                /* Adjust the date to cover the relevant period */
                myEnd.adjustForwardByPeriod(thePeriod);
                myEnd.adjustDay(-1);

                /* Assume that next is OK */
                isNextOK = true;

                /* If we have passed the final date */
                if ((theFinalDate != null) && (myEnd.compareTo(theFinalDate) >= 0)) {
                    /* Reset the end date and disable next button */
                    myEnd = theFinalDate;
                    isNextOK = false;
                }
            }

            /* Create the range */
            theRange = new JDateDayRange(theStartDate, myEnd);

            /* Apply the state */
            applyState();
        }

        /**
         * Adjust forwards to the next period.
         */
        private void nextPeriod() {
            /* Initialise the date */
            JDateDay myDate = new JDateDay(theStartDate);

            /* Adjust the date appropriately */
            myDate.adjustForwardByPeriod(thePeriod);

            /* Make sure that we do not go beyond the date range */
            if ((theFinalDate != null) && (myDate.compareTo(theFinalDate) > 0)) {
                myDate = theFinalDate;
            }

            /* Store the date */
            theStartDate = myDate;
        }

        /**
         * Adjust backwards to the previous period.
         */
        private void previousPeriod() {
            /* Initialise the date */
            JDateDay myDate = new JDateDay(theStartDate);

            /* If the period is unlimited */
            if (thePeriod == JDatePeriod.Unlimited) {
                /* Shift back to first date if required */
                myDate = theFirstDate;

                /* else we should adjust the date */
            } else {
                /* Adjust the date appropriately */
                myDate.adjustBackwardByPeriod(thePeriod);

                /* Make sure that we do not go beyond the date range */
                if ((theFirstDate != null) && (myDate.compareTo(theFirstDate) < 0)) {
                    myDate = theFirstDate;
                }
            }

            /* Store the date */
            theStartDate = myDate;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theDateButton.setSelectedDateDay(theStartDate);
            thePeriodBox.setSelectedItem(thePeriod);
        }
    }
}
