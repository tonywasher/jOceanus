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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRangeState;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayResource;
import net.sourceforge.joceanus.jtethys.dateday.JDatePeriod;
import net.sourceforge.joceanus.jtethys.ui.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 * @author Tony Washer
 */
public class JDateDayRangeSelect
        extends JEnablePanel {
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
     * ToolTip for Next Button.
     */
    private static final String NLS_NEXTTIP = JDateDayResource.TIP_NEXTDATE.getValue();

    /**
     * ToolTip for Previous Button.
     */
    private static final String NLS_PREVTIP = JDateDayResource.TIP_PREVDATE.getValue();

    /**
     * Text for Start Label.
     */
    private static final String NLS_START = JDateDayResource.LABEL_STARTING.getValue();

    /**
     * Text for End Label.
     */
    private static final String NLS_END = JDateDayResource.LABEL_ENDING.getValue();

    /**
     * Text for Containing Label.
     */
    private static final String NLS_CONTAIN = JDateDayResource.LABEL_CONTAINING.getValue();

    /**
     * Text for Period Label.
     */
    private static final String NLS_PERIOD = JDateDayResource.LABEL_PERIOD.getValue();

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = JDateDayResource.TITLE_BOX.getValue();

    /**
     * The formatter.
     */
    private final transient JDateDayFormatter theFormatter;

    /**
     * The Start Date button.
     */
    private final JDateDayButton theStartButton;

    /**
     * The End Date button.
     */
    private final JDateDayButton theEndButton;

    /**
     * The Base Date button.
     */
    private final JDateDayButton theBaseButton;

    /**
     * The Period Button.
     */
    private final JScrollButton<JDatePeriod> thePeriodButton;

    /**
     * The Period Panel.
     */
    private final JPanel thePeriodPane;

    /**
     * The Custom Panel.
     */
    private final JPanel theCustomPane;

    /**
     * The Standard Panel.
     */
    private final JPanel theStandardPane;

    /**
     * The Standard Label.
     */
    private final JLabel theStandardLabel;

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
     * The Active state.
     */
    private transient JDateDayRangeState theState = null;

    /**
     * The Saved state.
     */
    private transient JDateDayRangeState theSavePoint = null;

    /**
     * Constructor.
     */
    public JDateDayRangeSelect() {
        /* Call standard constructor */
        this(false);
    }

    /**
     * Constructor.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public JDateDayRangeSelect(final boolean pBaseIsStart) {
        /* Call standard constructor */
        this(new JDateDayFormatter(), pBaseIsStart);
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public JDateDayRangeSelect(final JDateDayFormatter pFormatter,
                               final boolean pBaseIsStart) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Create initial state */
        theState = new JDateDayRangeState(pBaseIsStart);

        /* Create the period button */
        thePeriodButton = new JScrollButton<JDatePeriod>();
        buildPeriodMenu();

        /* Create the period panel */
        JLabel myPeriodLabel = new JLabel(NLS_PERIOD);
        thePeriodPane = new JEnablePanel();
        thePeriodPane.setLayout(new BoxLayout(thePeriodPane, BoxLayout.X_AXIS));
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePeriodPane.add(myPeriodLabel);
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePeriodPane.add(thePeriodButton);
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the DateButtons */
        theStartButton = new JDateDayButton(theFormatter);
        theEndButton = new JDateDayButton(theFormatter);
        theBaseButton = new JDateDayButton(theFormatter);

        /* Create the buttons */
        theNextButton = new JButton(ArrowIcon.RIGHT);
        thePrevButton = new JButton(ArrowIcon.LEFT);
        theNextButton.setToolTipText(NLS_NEXTTIP);
        thePrevButton.setToolTipText(NLS_PREVTIP);

        /* Create the Custom Pane */
        theCustomPane = new JEnablePanel();
        theCustomPane.setLayout(new BoxLayout(theCustomPane, BoxLayout.X_AXIS));
        JLabel myStartLabel = new JLabel(NLS_START);
        JLabel myEndLabel = new JLabel(NLS_END);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(myStartLabel);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(theStartButton);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(myEndLabel);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(theEndButton);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the Standard Pane */
        theStandardPane = new JEnablePanel();
        theStandardPane.setLayout(new BoxLayout(theStandardPane, BoxLayout.X_AXIS));
        theStandardLabel = new JLabel();
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(theStandardLabel);
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(thePrevButton);
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(theBaseButton);
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(theNextButton);
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(thePeriodPane);
        add(Box.createHorizontalGlue());
        add(theStandardPane);
        add(theCustomPane);

        /* Add the listeners for item changes */
        DateListener myListener = new DateListener();
        theNextButton.addActionListener(myListener);
        thePrevButton.addActionListener(myListener);
        thePeriodButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theStartButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATEDAY, myListener);
        theEndButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATEDAY, myListener);
        theBaseButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATEDAY, myListener);
    }

    /**
     * Obtain selected DateRange.
     * @return the selected date range
     */
    public JDateDayRange getRange() {
        return theState.getRange();
    }

    /**
     * Obtain current state.
     * @return the current state
     */
    private JDateDayRangeState getState() {
        return theState;
    }

    @Override
    public void setLocale(final Locale pLocale) {
        super.setLocale(pLocale);
        theFormatter.setLocale(pLocale);
        theState.setLocale(pLocale);
        applyState();
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
     * Set the overall range for the control.
     * @param pRange the range
     */
    public final void setOverallRange(final JDateDayRange pRange) {
        /* Adjust the overall range */
        theState.adjustOverallRange(pRange);
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
        applyState();
    }

    /**
     * Copy date selection from other box.
     * @param pSource the source box
     */
    public void setSelection(final JDateDayRangeSelect pSource) {
        /* Access the state */
        JDateDayRangeState myState = pSource.getState();

        /* Accept this state */
        theState = new JDateDayRangeState(myState);

        /* Build the range */
        notifyChangedRange();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new JDateDayRangeState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new JDateDayRangeState(theSavePoint);
        notifyChangedRange();
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        /* Pass call on */
        super.setEnabled(pEnable);

        /* If we are enabling */
        if (pEnable) {
            /* Ensure correct values */
            applyState();
        }
    }

    /**
     * Notify changes to selected range.
     */
    private void notifyChangedRange() {
        /* Make sure that the state has been applied */
        applyState();

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
     * Apply the state.
     */
    private void applyState() {
        /* Determine flags */
        boolean isUpTo = theState.isUpTo()
                         && theState.isLocked();
        boolean isAdjust = theState.isAdjustable();
        boolean isFull = theState.isFull();
        boolean isContaining = theState.isContaining();
        boolean isBaseStartOfPeriod = theState.isBaseStartOfPeriod();

        /* Set the period value */
        thePeriodButton.setValue(theState.getPeriod());
        thePeriodPane.setVisible(!isUpTo);

        /* If this is a custom state */
        if (theState.isCustom()) {
            /* Set values for buttons */
            theStartButton.setSelectedDateDay(theState.getStartDate());
            theEndButton.setSelectedDateDay(theState.getEndDate());

            /* Show the custom box */
            theStandardPane.setVisible(false);
            theCustomPane.setVisible(true);

            /* else is this is a full dates state */
        } else if (theState.isFull()) {
            /* Hide custom and standard boxes */
            theStandardPane.setVisible(false);
            theCustomPane.setVisible(false);

            /* else this is a standard state */
        } else {
            /* Set value for button */
            theBaseButton.setSelectedDateDay(theState.getBaseDate());

            /* Enable/disable the adjustment buttons */
            theNextButton.setEnabled(theState.isNextOK());
            thePrevButton.setEnabled(theState.isPrevOK());

            /* Hide Next/Previous if necessary */
            theNextButton.setVisible(isAdjust);
            thePrevButton.setVisible(isAdjust);

            /* Label is hidden for Full and UpTo range */
            theStandardLabel.setVisible(!isFull
                                        && !isUpTo);

            /* Set correct text for label */
            theStandardLabel.setText(isContaining
                                                  ? NLS_CONTAIN
                                                  : isBaseStartOfPeriod
                                                                        ? NLS_START
                                                                        : NLS_END);

            /* Show the standard box */
            theCustomPane.setVisible(false);
            theStandardPane.setVisible(true);
        }
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
                theState.setStartDate(theStartButton.getSelectedDateDay());
                notifyChangedRange();

                /* if this event relates to the End Date button */
            } else if (theEndButton.equals(src)) {
                /* Access the value */
                theState.setEndDate(theEndButton.getSelectedDateDay());
                notifyChangedRange();

                /* if this event relates to the Base Date button */
            } else if (theBaseButton.equals(src)) {
                /* Access the value */
                theState.setBaseDate(theBaseButton.getSelectedDateDay());
                notifyChangedRange();

                /* if this event relates to the Period button */
            } else if (thePeriodButton.equals(src)) {
                /* Access the value */
                theState.setPeriod(thePeriodButton.getValue());
                notifyChangedRange();
            }
        }
    }
}
