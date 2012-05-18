/*******************************************************************************
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

public class DateDayRangeSelect extends JPanel {
    private static final long serialVersionUID = -1844318324805283367L;

    /**
     * Name of the Range property
     */
    public static final String propertyRANGE = "SelectedDateDayRange";

    /* Members */
    private final DateDayButton theDateButton;
    private final JComboBox thePeriodBox;
    private final JButton theNextButton;
    private final JButton thePrevButton;
    private final JLabel theStartLabel;
    private final JLabel thePeriodLabel;
    private DateDayRange thePublishedRange = null;
    private DateDay theFirstDate = null;
    private DateDay theFinalDate = null;
    private DateRangeState theState = null;
    private DateRangeState theSavePoint = null;
    private boolean refreshingData = false;
    private Locale theLocale = null;

    /* Access methods */
    public DateDayRange getRange() {
        return theState.getRange();
    }

    /* Constructor */
    public DateDayRangeSelect() {
        /* Create the listener */
        DateListener myListener = new DateListener();

        /* Create the boxes */
        thePeriodBox = new JComboBox();

        /* Create the DateButton */
        theDateButton = new DateDayButton();

        /* Set the locale */
        setLocale(Locale.getDefault());

        /* Add the PeriodTypes to the period box */
        for (DatePeriod myPeriod : DatePeriod.values()) {
            thePeriodBox.addItem(myPeriod);
        }

        /* Create the labels */
        theStartLabel = new JLabel("Start Date:");
        thePeriodLabel = new JLabel("Period:");

        /* Create the buttons */
        theNextButton = new JButton("Next");
        thePrevButton = new JButton("Prev");

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder("Date Range Selection"));

        /* Create the layout for the panel */
        GroupLayout panelLayout = new GroupLayout(this);
        setLayout(panelLayout);

        /* Set the layout */
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        panelLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addComponent(theStartLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(thePeriodLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(theNextButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thePrevButton).addContainerGap()));
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                panelLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(theStartLabel)
                        .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addComponent(thePeriodLabel)
                        .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addComponent(theNextButton)
                        .addComponent(thePrevButton)));

        /* Create initial state and limit the selection to the Range */
        theState = new DateRangeState();
        setOverallRange(null);
        theState.buildRange();

        /* Add the listeners for item changes */
        thePeriodBox.addItemListener(myListener);
        theNextButton.addActionListener(myListener);
        thePrevButton.addActionListener(myListener);
        theDateButton.addPropertyChangeListener(DateDayButton.propertyDATE, myListener);
    }

    @Override
    public void setLocale(Locale pLocale) {
        /* Record the locale */
        theLocale = pLocale;
        theDateButton.setLocale(pLocale);

        /* Pass the call onwards */
        super.setLocale(pLocale);
    }

    /**
     * Set the date format
     * @param pFormat the format string
     */
    public void setFormat(String pFormat) {
        theDateButton.setFormat(pFormat);
    }

    /**
     * Set the overall range for the control
     * @param pRange
     */
    public void setOverallRange(DateDayRange pRange) {
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
     * Copy date selection from other box
     * @param pSource the source box
     */
    public void setSelection(DateDayRangeSelect pSource) {
        DateRangeState myState = pSource.theState;

        /* Set the refreshing data flag */
        refreshingData = true;

        /* Accept this state */
        theState = new DateRangeState(myState);

        /* Build the range */
        theState.buildRange();
        notifyChangedRange();

        /* Reset the refreshing data flag */
        refreshingData = false;
    }

    /**
     * Create SavePoint
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new DateRangeState(theState);
    }

    /**
     * Restore SavePoint
     */
    public void restoreSavePoint() {
        /* Set the refreshing data flag */
        refreshingData = true;

        /* Restore the savePoint */
        theState = new DateRangeState(theSavePoint);

        /* Build the range and apply the state */
        theState.buildRange();
        notifyChangedRange();

        /* Reset the refreshing data flag */
        refreshingData = false;
    }

    /**
     * Lock/Unlock the selection
     * @param parentalLock is the parent locked
     */
    public void setLockDown(boolean parentalLock) {
        /* Lock/Unlock the selection */
        theDateButton.setEnabled(!parentalLock);
        thePeriodBox.setEnabled(!parentalLock);
        theNextButton.setEnabled((!parentalLock) && theState.isNextOK());
        thePrevButton.setEnabled((!parentalLock) && theState.isPrevOK());
    }

    /**
     * Notify changes to selected range
     */
    private void notifyChangedRange() {
        /* Determine whether a change has occurred */
        DateDayRange myNew = getRange();
        if (!DateDayRange.isDifferent(thePublishedRange, myNew))
            return;

        /* Record the new range and create a copy */
        DateDayRange myOld = thePublishedRange;
        thePublishedRange = myNew;
        myNew = new DateDayRange(myNew);

        /* Fire the property change */
        firePropertyChange(propertyRANGE, myOld, myNew);
    }

    /**
     * The Date Listener
     */
    private class DateListener implements ActionListener, PropertyChangeListener, ItemListener {
        @Override
        public void itemStateChanged(ItemEvent evt) {
            DatePeriod myPeriod = null;

            /* Ignore selection if refreshing data */
            if (refreshingData)
                return;

            /* If this event relates to the period box */
            if (evt.getSource() == thePeriodBox) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    /* Determine the new period */
                    myPeriod = (DatePeriod) evt.getItem();

                    /* Apply period and build the range */
                    theState.setPeriod(myPeriod);
                    notifyChangedRange();
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the next button */
            if (o == theNextButton) {
                /* Set the next date */
                theState.setNextDate();
                notifyChangedRange();
            }

            /* If this event relates to the previous button */
            else if (o == thePrevButton) {
                /* Set the previous date */
                theState.setPreviousDate();
                notifyChangedRange();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            /* if this date relates to the Date button */
            if (evt.getSource() == theDateButton) {
                /* Access the value */
                theState.setDate(theDateButton);
                notifyChangedRange();
            }
        }
    }

    /* SavePoint values */
    private class DateRangeState {
        /* Members */
        private DateDay theStartDate = null;
        private DatePeriod thePeriod = null;
        private DateDayRange theRange = null;
        private boolean isNextOK = false;
        private boolean isPrevOK = false;

        /* Access methods */
        private DateDay getStartDate() {
            return theStartDate;
        }

        private DatePeriod getPeriod() {
            return thePeriod;
        }

        private DateDayRange getRange() {
            return theRange;
        }

        private boolean isNextOK() {
            return isNextOK;
        }

        private boolean isPrevOK() {
            return isPrevOK;
        }

        /**
         * Constructor
         */
        private DateRangeState() {
            theStartDate = new DateDay(theLocale);
            thePeriod = DatePeriod.OneMonth;

            /* Make sure that we do not go beyond the date range */
            if ((theFinalDate != null) && (theStartDate.compareTo(theFinalDate) > 0))
                theStartDate = theFinalDate;
            if ((theFirstDate != null) && (theStartDate.compareTo(theFirstDate) < 0))
                theStartDate = theFirstDate;
        }

        /**
         * Constructor
         * @param pState state to copy from
         */
        private DateRangeState(DateRangeState pState) {
            theStartDate = new DateDay(pState.getStartDate());
            thePeriod = pState.getPeriod();
        }

        /**
         * Set new Period
         * @param pPeriod the new period
         */
        private void setPeriod(DatePeriod pPeriod) {
            /* Adjust the period and build the new range */
            thePeriod = pPeriod;
            buildRange();
        }

        /**
         * Set new Date
         * @param pButton the Button with the new date
         */
        private void setDate(DateDayButton pButton) {
            /* Adjust the date and build the new range */
            DateDay myDate = pButton.getSelectedDateDay();
            theStartDate = myDate;
            buildRange();
        }

        /**
         * Set next Date
         */
        private void setNextDate() {
            /* Adjust the date and build the new range */
            nextPeriod();
            buildRange();
        }

        /**
         * Set previous Date
         */
        private void setPreviousDate() {
            /* Adjust the date and build the new range */
            previousPeriod();
            buildRange();
        }

        /**
         * adjust the overall range
         */
        private void adjustOverallRange() {
            /* Make sure that we are within the range */
            if ((theFinalDate != null) && (theStartDate.compareTo(theFinalDate) > 0))
                theStartDate = theFinalDate;
            if ((theFirstDate != null) && (theStartDate.compareTo(theFirstDate) < 0))
                theStartDate = theFirstDate;

            /* Build the range */
            buildRange();
        }

        /**
         * Build the range represented by the selection
         */
        private void buildRange() {
            DateDay myEnd;

            /* Previous is only allowed if we have not hit the first date */
            isPrevOK = (theFirstDate == null) || (theFirstDate.compareTo(theStartDate) != 0);

            /* If we are unlimited */
            if (thePeriod == DatePeriod.Unlimited) {
                /* Set end date as last possible date */
                myEnd = theFinalDate;

                /* Note that next is not allowed */
                isNextOK = false;
            }

            /* else we have to calculate the date */
            else {
                /* Initialise the end date */
                myEnd = new DateDay(theStartDate);

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
            theRange = new DateDayRange(theStartDate, myEnd);

            /* Apply the state */
            applyState();
        }

        /* Adjust a date forward by the period */
        private void nextPeriod() {
            /* Initialise the date */
            DateDay myDate = new DateDay(theStartDate);

            /* Adjust the date appropriately */
            myDate.adjustForwardByPeriod(thePeriod);

            /* Make sure that we do not go beyond the date range */
            if ((theFinalDate != null) && (myDate.compareTo(theFinalDate) > 0))
                myDate = theFinalDate;

            /* Store the date */
            theStartDate = myDate;
        }

        /* Adjust a date forward by a period */
        private void previousPeriod() {
            /* Initialise the date */
            DateDay myDate = new DateDay(theStartDate);

            /* If the period is unlimited */
            if (thePeriod == DatePeriod.Unlimited) {
                /* Shift back to first date if required */
                myDate = theFirstDate;
            }

            /* else we should adjust the date */
            else {
                /* Adjust the date appropriately */
                myDate.adjustBackwardByPeriod(thePeriod);

                /* Make sure that we do not go beyond the date range */
                if ((theFirstDate != null) && (myDate.compareTo(theFirstDate) < 0))
                    myDate = theFirstDate;
            }

            /* Store the date */
            theStartDate = myDate;
        }

        /**
         * Apply the State
         */
        private void applyState() {
            /* Adjust the lock-down */
            setLockDown(false);
            theDateButton.setSelectedDateDay(theStartDate);
            thePeriodBox.setSelectedItem(thePeriod);
        }
    }
}
