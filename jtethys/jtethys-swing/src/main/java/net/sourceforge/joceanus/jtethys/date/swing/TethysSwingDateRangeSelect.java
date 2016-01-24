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
package net.sourceforge.joceanus.jtethys.date.swing;

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

import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDatePeriod;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysDateRangeState;
import net.sourceforge.joceanus.jtethys.date.TethysDateResource;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 * @author Tony Washer
 * @deprecated as of 1.5.0 use {@link TethysSwingDateRangeSelector}
 */
@Deprecated
public class TethysSwingDateRangeSelect
        extends TethysSwingEnablePanel {
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
    private static final String NLS_NEXTTIP = TethysDateResource.TIP_NEXTDATE.getValue();

    /**
     * ToolTip for Previous Button.
     */
    private static final String NLS_PREVTIP = TethysDateResource.TIP_PREVDATE.getValue();

    /**
     * Text for Start Label.
     */
    private static final String NLS_START = TethysDateResource.LABEL_STARTING.getValue();

    /**
     * Text for End Label.
     */
    private static final String NLS_END = TethysDateResource.LABEL_ENDING.getValue();

    /**
     * Text for Containing Label.
     */
    private static final String NLS_CONTAIN = TethysDateResource.LABEL_CONTAINING.getValue();

    /**
     * Text for Period Label.
     */
    private static final String NLS_PERIOD = TethysDateResource.LABEL_PERIOD.getValue();

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = TethysDateResource.TITLE_BOX.getValue();

    /**
     * The formatter.
     */
    private final transient TethysDateFormatter theFormatter;

    /**
     * The Start Date button.
     */
    private final TethysSwingDateButton theStartButton;

    /**
     * The End Date button.
     */
    private final TethysSwingDateButton theEndButton;

    /**
     * The Base Date button.
     */
    private final TethysSwingDateButton theBaseButton;

    /**
     * The Period Button.
     */
    private final JScrollButton<TethysDatePeriod> thePeriodButton;

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
    private transient TethysDateRange thePublishedRange = null;

    /**
     * The Active state.
     */
    private transient TethysDateRangeState theState = null;

    /**
     * The Saved state.
     */
    private transient TethysDateRangeState theSavePoint = null;

    /**
     * Constructor.
     */
    public TethysSwingDateRangeSelect() {
        /* Call standard constructor */
        this(false);
    }

    /**
     * Constructor.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysSwingDateRangeSelect(final boolean pBaseIsStart) {
        /* Call standard constructor */
        this(new TethysDateFormatter(), pBaseIsStart);
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysSwingDateRangeSelect(final TethysDateFormatter pFormatter,
                                      final boolean pBaseIsStart) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Create initial state */
        theState = new TethysDateRangeState(pBaseIsStart);

        /* Create the period button */
        thePeriodButton = new JScrollButton<>();
        buildPeriodMenu();

        /* Create the period panel */
        JLabel myPeriodLabel = new JLabel(NLS_PERIOD);
        thePeriodPane = new TethysSwingEnablePanel();
        thePeriodPane.setLayout(new BoxLayout(thePeriodPane, BoxLayout.X_AXIS));
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePeriodPane.add(myPeriodLabel);
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePeriodPane.add(thePeriodButton);
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the DateButtons */
        theStartButton = new TethysSwingDateButton(theFormatter);
        theEndButton = new TethysSwingDateButton(theFormatter);
        theBaseButton = new TethysSwingDateButton(theFormatter);

        /* Create the buttons */
        theNextButton = new JButton(TethysSwingArrowIcon.RIGHT);
        thePrevButton = new JButton(TethysSwingArrowIcon.LEFT);
        theNextButton.setToolTipText(NLS_NEXTTIP);
        thePrevButton.setToolTipText(NLS_PREVTIP);

        /* Create the Custom Pane */
        theCustomPane = new TethysSwingEnablePanel();
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
        theStandardPane = new TethysSwingEnablePanel();
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
        theStartButton.addPropertyChangeListener(TethysSwingDateButton.PROPERTY_DATEDAY, myListener);
        theEndButton.addPropertyChangeListener(TethysSwingDateButton.PROPERTY_DATEDAY, myListener);
        theBaseButton.addPropertyChangeListener(TethysSwingDateButton.PROPERTY_DATEDAY, myListener);
    }

    /**
     * Obtain selected DateRange.
     * @return the selected date range
     */
    public TethysDateRange getRange() {
        return theState.getRange();
    }

    /**
     * Obtain current state.
     * @return the current state
     */
    private TethysDateRangeState getState() {
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
        JScrollMenuBuilder<TethysDatePeriod> myBuilder = thePeriodButton.getMenuBuilder();

        /* Loop through the periods */
        for (TethysDatePeriod myPeriod : TethysDatePeriod.values()) {
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
    public final void setOverallRange(final TethysDateRange pRange) {
        /* Adjust the overall range */
        theState.adjustOverallRange(pRange);
        notifyChangedRange();
    }

    /**
     * Set period.
     * @param pPeriod the period to select.
     */
    public void setPeriod(final TethysDatePeriod pPeriod) {
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
    public void setSelection(final TethysSwingDateRangeSelect pSource) {
        /* Access the state */
        TethysDateRangeState myState = pSource.getState();

        /* Accept this state */
        theState = new TethysDateRangeState(myState);

        /* Build the range */
        notifyChangedRange();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new TethysDateRangeState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new TethysDateRangeState(theSavePoint);
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
        TethysDateRange myNew = getRange();
        if (!TethysDateRange.isDifferent(thePublishedRange, myNew)) {
            return;
        }

        /* Record the new range and create a copy */
        TethysDateRange myOld = thePublishedRange;
        thePublishedRange = myNew;
        myNew = new TethysDateRange(myNew);

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
    @Deprecated
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
