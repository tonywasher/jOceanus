/*******************************************************************************
 * jDateDay: Java Date Day
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdateday;

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
public class JDateDayRangeSelect
        extends JPanel {
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
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(JDateDayRangeSelect.class.getName());

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
     * Text for End Label.
     */
    private static final String NLS_END = NLS_BUNDLE.getString("EndLabel");

    /**
     * Text for Period Label.
     */
    private static final String NLS_PERIOD = NLS_BUNDLE.getString("PeriodLabel");

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("BoxTitle");

    /**
     * The Start Date button.
     */
    private final JDateDayButton theStartButton;

    /**
     * The End Date button.
     */
    private final JDateDayButton theEndButton;

    /**
     * The Start Label.
     */
    private final JLabel theStartLabel;

    /**
     * The End Label.
     */
    private final JLabel theEndLabel;

    /**
     * The Period ComboBox.
     */
    private final JComboBox<JDatePeriod> thePeriodBox;

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
     * Should we use start button for periods.
     */
    private boolean useStartButtonForPeriod = true;

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
        /* Call standard constructor */
        this(false);
    }

    /**
     * Constructor.
     * @param useStartForPeriod true/false
     */
    public JDateDayRangeSelect(final boolean useStartForPeriod) {
        /* Call standard constructor */
        this(new JDateDayFormatter(), useStartForPeriod);
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param useStartForPeriod true/false
     */
    public JDateDayRangeSelect(final JDateDayFormatter pFormatter,
                               final boolean useStartForPeriod) {
        /* Create the listener */
        DateListener myListener = new DateListener();

        /* Determine the locale */
        theLocale = pFormatter.getLocale();

        /* Record whether we are using start for periods */
        useStartButtonForPeriod = useStartForPeriod;

        /* Create the boxes */
        thePeriodBox = new JComboBox<JDatePeriod>();
        thePeriodBox.setMaximumSize(new Dimension(PERIOD_WIDTH, PERIOD_HEIGHT));

        /* Create the DateButtons */
        theStartButton = new JDateDayButton(pFormatter);
        theEndButton = new JDateDayButton(pFormatter);

        /* Add the PeriodTypes to the period box */
        for (JDatePeriod myPeriod : JDatePeriod.values()) {
            thePeriodBox.addItem(myPeriod);
        }

        /* Create the labels */
        theStartLabel = new JLabel(NLS_START);
        theEndLabel = new JLabel(NLS_END);
        JLabel myPeriodLabel = new JLabel(NLS_PERIOD);

        /* Create the buttons */
        theNextButton = new JButton(NLS_NEXT);
        thePrevButton = new JButton(NLS_PREV);

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theStartLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theStartButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theEndLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theEndButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
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

        /* Add the listeners for item changes */
        thePeriodBox.addItemListener(myListener);
        theNextButton.addActionListener(myListener);
        thePrevButton.addActionListener(myListener);
        theStartButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
        theEndButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
    }

    /**
     * Obtain the date configuration.
     * @return the Date configuration
     */
    public JDateDayConfig getDateConfig() {
        return theEndButton.getDateConfig();
    }

    /**
     * Set the initial range for the control.
     */
    private void setInitialRange() {
        /* Record total possible range */
        theFirstDate = null;
        theFinalDate = null;

        /* Set range into DateButton */
        theStartButton.setEarliestDateDay(theFirstDate);
        theStartButton.setLatestDateDay(theFinalDate);
        theEndButton.setEarliestDateDay(theFirstDate);
        theEndButton.setLatestDateDay(theFinalDate);

        /* Adjust the overall range */
        theState.adjustOverallRange();
        theState.applyState();
    }

    /**
     * Set the overall range for the control.
     * @param pRange the range
     */
    public final void setOverallRange(final JDateDayRange pRange) {
        /* Record total possible range */
        theFirstDate = (pRange == null)
                ? null
                : pRange.getStart();
        theFinalDate = (pRange == null)
                ? null
                : pRange.getEnd();

        /* Set range into DateButton */
        theStartButton.setEarliestDateDay(theFirstDate);
        theStartButton.setLatestDateDay(theFinalDate);
        theEndButton.setEarliestDateDay(theFirstDate);
        theEndButton.setLatestDateDay(theFinalDate);

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
        /* Restore the savePoint */
        theState = new JDateRangeState(theSavePoint);
        notifyChangedRange();
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        /* Lock/Unlock the selection */
        theStartButton.setEnabled(pEnable);
        theEndButton.setEnabled(pEnable);
        thePeriodBox.setEnabled(pEnable);
        theNextButton.setEnabled(pEnable
                                 && theState.isNextOK());
        thePrevButton.setEnabled(pEnable
                                 && theState.isPrevOK());

        /* Handle custom changes */
        boolean isCustom = theState.isCustom();
        theNextButton.setVisible(!isCustom);
        thePrevButton.setVisible(!isCustom);
        if (useStartButtonForPeriod) {
            theEndLabel.setVisible(isCustom);
            theEndButton.setVisible(isCustom);
        } else {
            theStartLabel.setVisible(isCustom);
            theStartButton.setVisible(isCustom);
        }
    }

    /**
     * Notify changes to selected range.
     */
    private void notifyChangedRange() {
        /* Set the refreshing data flag */
        refreshingData = true;

        /* Make sure that the state has been applied */
        theState.applyState();

        /* Reset the refreshing data flag */
        refreshingData = false;

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
    private final class DateListener
            implements ActionListener, PropertyChangeListener, ItemListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            JDatePeriod myPeriod = null;

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the period box */
            if ((thePeriodBox.equals(evt.getSource()))
                && (evt.getStateChange() == ItemEvent.SELECTED)) {
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
            /* if this date relates to the Start Date button */
            if (theStartButton.equals(evt.getSource())) {
                /* Access the value */
                theState.setStartDate(theStartButton);
                notifyChangedRange();
                /* if this date relates to the Start Date button */
            } else if (theEndButton.equals(evt.getSource())) {
                /* Access the value */
                theState.setEndDate(theEndButton);
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
         * The end Date.
         */
        private JDateDay theEndDate = null;

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
         * Get the end date.
         * @return the end date
         */
        private JDateDay getEndDate() {
            return theEndDate;
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
         * Is this a custom range.
         * @return true/false
         */
        private boolean isCustom() {
            return (thePeriod == JDatePeriod.Custom);
        }

        /**
         * Constructor.
         */
        private JDateRangeState() {
            /* Default the period */
            thePeriod = JDatePeriod.OneMonth;

            /* Determine initial date */
            JDateDay myDate = new JDateDay(theLocale);

            /* Make sure that we do not go beyond the date range */
            if ((theFinalDate != null)
                && (myDate.compareTo(theFinalDate) > 0)) {
                myDate = theFinalDate;
            }
            if ((theFirstDate != null)
                && (myDate.compareTo(theFirstDate) < 0)) {
                myDate = theFirstDate;
            }

            /* Set appropriate date */
            if (useStartButtonForPeriod) {
                /* Initialise start date */
                theStartDate = myDate;
                /* else we are using the end date for periods */
            } else {
                /* Initialise end date */
                theEndDate = myDate;
            }

            /* build the range */
            buildRange();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private JDateRangeState(final JDateRangeState pState) {
            JDateDay myStart = pState.getStartDate();
            JDateDay myEnd = pState.getEndDate();
            theStartDate = (myStart == null)
                    ? null
                    : new JDateDay(myStart);
            theEndDate = (myEnd == null)
                    ? null
                    : new JDateDay(myEnd);
            thePeriod = pState.getPeriod();
            buildRange();
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
         * Set new Start Date.
         * @param pButton the Button with the new date
         */
        private void setStartDate(final JDateDayButton pButton) {
            /* Adjust the date and build the new range */
            JDateDay myDate = pButton.getSelectedDateDay();
            theStartDate = myDate;
            buildRange();
        }

        /**
         * Set new End Date.
         * @param pButton the Button with the new date
         */
        private void setEndDate(final JDateDayButton pButton) {
            /* Adjust the date and build the new range */
            JDateDay myDate = pButton.getSelectedDateDay();
            theEndDate = myDate;
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
            if ((theFinalDate != null)
                && (theStartDate.compareTo(theFinalDate) > 0)) {
                theStartDate = theFinalDate;
            }
            if ((theFirstDate != null)
                && (theStartDate.compareTo(theFirstDate) < 0)) {
                theStartDate = theFirstDate;
            }
            if ((theFinalDate != null)
                && (theEndDate.compareTo(theFinalDate) > 0)) {
                theEndDate = theFinalDate;
            }
            if ((theFirstDate != null)
                && (theEndDate.compareTo(theFirstDate) < 0)) {
                theEndDate = theFirstDate;
            }

            /* Build the range */
            buildRange();
        }

        /**
         * Build the range represented by the selection.
         */
        private void buildRange() {
            /* Previous is only allowed if we have not hit the first date */
            isPrevOK = (theFirstDate == null)
                       || (theFirstDate.compareTo(theStartDate) != 0);

            /* If we are unlimited */
            switch (thePeriod) {
                case AllDates:
                    /* Previous and next are not allowed */
                    isPrevOK = false;
                    isNextOK = false;

                    /* Set range to complete range */
                    theStartDate = theFirstDate;
                    theEndDate = theFinalDate;
                    break;

                /* If we are custom */
                case Custom:
                    /* Previous and next are disallowed */
                    isPrevOK = false;
                    isNextOK = false;

                    /* If the EndDate is earlier than the startDate, reset it */
                    if (theEndDate.compareTo(theStartDate) < 0) {
                        theEndDate = theStartDate;
                    }

                    /* Limit the buttons */
                    theStartButton.setLatestDateDay(theEndDate);
                    theEndButton.setEarliestDateDay(theStartDate);
                    break;

                /* else we have to calculate the date */
                default:
                    if (useStartButtonForPeriod) {
                        /* Previous is only allowed if we have not hit the first date */
                        isPrevOK = (theFirstDate == null)
                                   || (theFirstDate.compareTo(theStartDate) != 0);

                        /* Initialise the end date */
                        theEndDate = new JDateDay(theStartDate);

                        /* Adjust the date to cover the relevant period */
                        theEndDate.adjustForwardByPeriod(thePeriod);
                        theEndDate.adjustDay(-1);

                        /* Assume that next is OK */
                        isNextOK = true;

                        /* If we have passed the final date */
                        if ((theFinalDate != null)
                            && (theEndDate.compareTo(theFinalDate) >= 0)) {
                            /* Reset the end date and disable next button */
                            theEndDate = theFinalDate;
                            isNextOK = false;
                        }
                    } else {
                        /* Next is only allowed if we have not hit the last date */
                        isNextOK = (theFinalDate == null)
                                   || (theFinalDate.compareTo(theEndDate) != 0);

                        /* Initialise the end date */
                        theStartDate = new JDateDay(theEndDate);

                        /* Adjust the date to cover the relevant period */
                        theStartDate.adjustBackwardByPeriod(thePeriod);
                        theStartDate.adjustDay(1);

                        /* Assume that previous is OK */
                        isPrevOK = true;

                        /* If we have passed the final date */
                        if ((theFirstDate != null)
                            && (theStartDate.compareTo(theFirstDate) <= 0)) {
                            /* Reset the start date and disable previous button */
                            theStartDate = theFirstDate;
                            isPrevOK = false;
                        }
                    }
                    break;
            }

            /* Create the range */
            theRange = new JDateDayRange(theStartDate, theEndDate);
        }

        /**
         * Adjust forwards to the next period.
         */
        private void nextPeriod() {
            /* Initialise the date */
            JDateDay myDate = new JDateDay(useStartButtonForPeriod
                    ? theStartDate
                    : theEndDate);

            /* Adjust the date appropriately */
            myDate.adjustForwardByPeriod(thePeriod);

            /* Make sure that we do not go beyond the date range */
            if ((theFinalDate != null)
                && (myDate.compareTo(theFinalDate) > 0)) {
                myDate = theFinalDate;
            }

            /* Store the date */
            if (useStartButtonForPeriod) {
                theStartDate = myDate;
            } else {
                theEndDate = myDate;
            }
        }

        /**
         * Adjust backwards to the previous period.
         */
        private void previousPeriod() {
            /* Initialise the date */
            JDateDay myDate = new JDateDay(useStartButtonForPeriod
                    ? theStartDate
                    : theEndDate);

            /* Adjust the date appropriately */
            myDate.adjustBackwardByPeriod(thePeriod);

            /* Make sure that we do not go beyond the date range */
            if ((theFirstDate != null)
                && (myDate.compareTo(theFirstDate) < 0)) {
                myDate = theFirstDate;
            }

            /* Store the date */
            if (useStartButtonForPeriod) {
                theStartDate = myDate;
            } else {
                theEndDate = myDate;
            }
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theStartButton.setSelectedDateDay(theStartDate);
            theEndButton.setSelectedDateDay(theEndDate);
            thePeriodBox.setSelectedItem(thePeriod);
        }
    }
}
