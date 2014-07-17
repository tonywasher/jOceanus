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
package net.sourceforge.joceanus.jtethys.dateday;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

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
     * Name of the Range property.
     */
    public static final String PROPERTY_RANGE = "SelectedDateDayRange";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(JDateDayRangeSelect.class.getName());

    /**
     * ToolTip for Next Button.
     */
    private static final String NLS_NEXTTIP = NLS_BUNDLE.getString("NextTip");

    /**
     * ToolTip for Previous Button.
     */
    private static final String NLS_PREVTIP = NLS_BUNDLE.getString("PrevTip");

    /**
     * Text for Start Label.
     */
    private static final String NLS_START = NLS_BUNDLE.getString("StartLabel");

    /**
     * Text for End Label.
     */
    private static final String NLS_END = NLS_BUNDLE.getString("EndLabel");

    /**
     * Text for Containing Label.
     */
    private static final String NLS_CONTAIN = NLS_BUNDLE.getString("ContainLabel");

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
     * The Period Label.
     */
    private final JLabel thePeriodLabel;

    /**
     * The Start Label.
     */
    private final JLabel theStartLabel;

    /**
     * The End Label.
     */
    private final JLabel theEndLabel;

    /**
     * The Period Button.
     */
    private final JScrollButton<JDatePeriod> thePeriodButton;

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
     * Should we use start button for periods.
     */
    private final boolean useStartButtonForPeriod;

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
        /* Determine the locale */
        theLocale = pFormatter.getLocale();

        /* Record whether we are using start for periods */
        useStartButtonForPeriod = useStartForPeriod;

        /* Create the period button */
        thePeriodButton = new JScrollButton<JDatePeriod>();
        buildPeriodMenu();

        /* Create the DateButtons */
        theStartButton = new JDateDayButton(pFormatter);
        theEndButton = new JDateDayButton(pFormatter);

        /* Create the labels */
        theStartLabel = new JLabel(NLS_START);
        theEndLabel = new JLabel(NLS_END);
        thePeriodLabel = new JLabel(NLS_PERIOD);

        /* Create the buttons */
        theNextButton = new JButton(ArrowIcon.RIGHT);
        thePrevButton = new JButton(ArrowIcon.LEFT);
        theNextButton.setToolTipText(NLS_NEXTTIP);
        thePrevButton.setToolTipText(NLS_PREVTIP);

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePeriodLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePeriodButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(Box.createHorizontalGlue());
        add(theStartLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        if (useStartButtonForPeriod) {
            add(thePrevButton);
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
            add(theStartButton);
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
            add(theNextButton);
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        } else {
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
            add(theStartButton);
        }
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theEndLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        if (!useStartButtonForPeriod) {
            add(thePrevButton);
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
            add(theEndButton);
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
            add(theNextButton);
        } else {
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
            add(theEndButton);
        }
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create initial state and limit the selection to the Range */
        theState = new JDateRangeState();
        setInitialRange();

        /* Add the listeners for item changes */
        DateListener myListener = new DateListener();
        theNextButton.addActionListener(myListener);
        thePrevButton.addActionListener(myListener);
        thePeriodButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theStartButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
        theEndButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
    }

    /**
     * Build period menu.
     */
    private void buildPeriodMenu() {
        /* Obtain builder */
        JScrollMenuBuilder<JDatePeriod> myBuilder = thePeriodButton.getMenuBuilder();

        /* Loop through the periods */
        for (JDatePeriod myPeriod : JDatePeriod.values()) {
            /* Add as long as it is not the datesUpTo period */
            if (!myPeriod.datesUpTo()) {
                /* Create a new JMenuItem for the period */
                myBuilder.addItem(myPeriod);
            }
        }
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
     * Set period.
     * @param pPeriod the period to select.
     */
    public void setPeriod(final JDatePeriod pPeriod) {
        /* Apply period and build the range */
        theState.setPeriod(pPeriod);
        notifyChangedRange();
    }

    /**
     * Lock period.
     * @param isLocked true/false.
     */
    public void lockPeriod(final boolean isLocked) {
        /* Apply period and build the range */
        theState.lockPeriod(isLocked);
        theState.applyState();
    }

    /**
     * Copy date selection from other box.
     * @param pSource the source box
     */
    public void setSelection(final JDateDayRangeSelect pSource) {
        /* Access the state */
        JDateRangeState myState = pSource.theState;

        /* Accept this state */
        theState = new JDateRangeState(myState);

        /* Build the range */
        theState.buildRange();
        notifyChangedRange();
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
        /* Determine state */
        boolean isAdjust = theState.isAdjustable();
        boolean isCustom = theState.isCustom();
        boolean isUpTo = theState.isUpTo()
                         && theState.isLocked();
        boolean isFull = theState.isFull();

        /* Lock/Unlock the components */
        theStartButton.setEnabled(pEnable);
        theEndButton.setEnabled(pEnable);
        thePeriodButton.setEnabled(pEnable);
        theNextButton.setEnabled(pEnable
                                 && theState.isNextOK());
        thePrevButton.setEnabled(pEnable
                                 && theState.isPrevOK());

        /* Hide Next/Previous if necessary */
        theNextButton.setVisible(isAdjust);
        thePrevButton.setVisible(isAdjust);

        /* Hide Period label and box for UpTo range */
        thePeriodLabel.setVisible(!isUpTo);
        thePeriodButton.setVisible(!isUpTo);

        /* If we are using start button for period */
        if (useStartButtonForPeriod) {
            /* End label and button is only visible for custom range */
            theEndLabel.setVisible(isCustom);
            theEndButton.setVisible(isCustom);

            /* Start label is hidden for Full and UpTo range */
            theStartLabel.setVisible(!isFull
                                     && !isUpTo);

            /* Start button is visible unless it is Full range */
            theStartButton.setVisible(!isFull);

            /* Set correct text for label */
            theStartLabel.setText(theState.isContaining()
                                                         ? NLS_CONTAIN
                                                         : NLS_START);

            /* else we are using end button for period */
        } else {
            /* Start label and button is only visible for custom range */
            theStartLabel.setVisible(isCustom);
            theStartButton.setVisible(isCustom);

            /* End label is hidden for Full and UpTo range */
            theEndLabel.setVisible(!isFull
                                   && !isUpTo);

            /* Start button is visible unless it is Full range */
            theEndButton.setVisible(!isFull);

            /* Set correct text for label */
            theEndLabel.setText(theState.isContaining()
                                                       ? NLS_CONTAIN
                                                       : NLS_END);
        }
    }

    /**
     * Notify changes to selected range.
     */
    private void notifyChangedRange() {
        /* Make sure that the state has been applied */
        theState.applyState();
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
            implements ActionListener, PropertyChangeListener {
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
            Object src = evt.getSource();

            /* if this event relates to the Start Date button */
            if (theStartButton.equals(src)) {
                /* Access the value */
                theState.setStartDate(theStartButton);
                notifyChangedRange();

                /* if this event relates to the Start Date button */
            } else if (theEndButton.equals(src)) {
                /* Access the value */
                theState.setEndDate(theEndButton);
                notifyChangedRange();

                /* if this event relates to the Period button */
            } else if (thePeriodButton.equals(src)) {
                /* Access the value */
                theState.setPeriod(thePeriodButton.getValue());
                theState.applyState();
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
         * Is the period locked?
         */
        private boolean isLocked = false;

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
         * Is the period locked?
         * @return true/false
         */
        private boolean isLocked() {
            return isLocked;
        }

        /**
         * Is this an UpTo range.
         * @return true/false
         */
        private boolean isUpTo() {
            return thePeriod == JDatePeriod.DATESUPTO;
        }

        /**
         * Is this a custom range.
         * @return true/false
         */
        private boolean isCustom() {
            return thePeriod == JDatePeriod.CUSTOM;
        }

        /**
         * Is this a full range.
         * @return true/false
         */
        private boolean isFull() {
            return thePeriod == JDatePeriod.ALLDATES;
        }

        /**
         * Is this an adjustable range.
         * @return true/false
         */
        private boolean isAdjustable() {
            return thePeriod.adjustPeriod();
        }

        /**
         * Is this a containing range.
         * @return true/false
         */
        private boolean isContaining() {
            return thePeriod.isContaining();
        }

        /**
         * Constructor.
         */
        private JDateRangeState() {
            /* Default the period */
            thePeriod = JDatePeriod.ONEMONTH;

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
            isLocked = pState.isLocked();
            buildRange();
        }

        /**
         * Lock the period.
         * @param pLocked true/false
         */
        private void lockPeriod(final boolean pLocked) {
            isLocked = pLocked;
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

            /* Determine action for period */
            switch (thePeriod) {
                case ALLDATES:
                    buildFullRange();
                    break;
                case CUSTOM:
                    buildCustomRange();
                    break;
                case DATESUPTO:
                    buildUpToRange();
                    break;
                case CALENDARMONTH:
                case CALENDARQUARTER:
                case CALENDARYEAR:
                case FISCALYEAR:
                    buildContainingRange();
                    break;
                default:
                    buildStandardRange();
                    break;
            }
        }

        /**
         * Build the full range.
         */
        private void buildFullRange() {
            /* Previous and next are not allowed */
            isPrevOK = false;
            isNextOK = false;

            /* Create the range */
            theRange = new JDateDayRange(theFirstDate, theFinalDate);
        }

        /**
         * Build the custom range.
         */
        private void buildCustomRange() {
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

            /* Create the range */
            theRange = new JDateDayRange(theStartDate, theEndDate);
        }

        /**
         * Build the upTo range.
         */
        private void buildUpToRange() {
            /* Previous and next are disallowed */
            isPrevOK = false;
            isNextOK = false;

            /* Limit the buttons */
            theStartButton.setEarliestDateDay(theFirstDate);
            theStartButton.setLatestDateDay(theFinalDate);
            theEndButton.setEarliestDateDay(theFirstDate);
            theEndButton.setLatestDateDay(theFinalDate);

            /* Create the range */
            theRange = new JDateDayRange(theFirstDate, (useStartButtonForPeriod)
                                                                                ? theStartDate
                                                                                : theEndDate);
        }

        /**
         * Build the standard range.
         */
        private void buildStandardRange() {
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

                /* If we have passed the first date */
                if ((theFirstDate != null)
                    && (theStartDate.compareTo(theFirstDate) <= 0)) {
                    /* Reset the start date and disable previous button */
                    theStartDate = theFirstDate;
                    isPrevOK = false;
                }
            }

            /* Create the range */
            theRange = new JDateDayRange(theStartDate, theEndDate);
        }

        /**
         * Build the containing range.
         */
        private void buildContainingRange() {
            /* Initialise the end date */
            JDateDay myStartDate = new JDateDay(theEndDate);

            /* Adjust the date to the start of the relevant period */
            myStartDate.startPeriod(thePeriod);

            /* Initialise the end date */
            JDateDay myEndDate = new JDateDay(myStartDate);

            /* Adjust the date to cover the relevant period */
            myEndDate.adjustForwardByPeriod(thePeriod);
            myEndDate.adjustDay(-1);

            /* If the StartDate is earlier than the firstDate, reset it */
            if (myStartDate.compareTo(theFirstDate) < 0) {
                myStartDate = theFirstDate;
            }

            /* If the EndDate is later than the finalDate, reset it */
            if (myEndDate.compareTo(theFinalDate) > 0) {
                myEndDate = theFinalDate;
            }

            /* Previous is only allowed if we have not hit the first date */
            isPrevOK = (theFirstDate == null)
                       || (theFirstDate.compareTo(theStartDate) != 0);

            /* Next is only allowed if we have not hit the last date */
            isNextOK = (theFinalDate == null)
                       || (theFinalDate.compareTo(theEndDate) != 0);

            /* Create the range */
            theRange = new JDateDayRange(myStartDate, myEndDate);
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
            thePeriodButton.setText((thePeriod == null)
                                                       ? null
                                                       : thePeriod.toString());
        }
    }
}
