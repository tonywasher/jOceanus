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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateResource;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Swing Date Dialog.
 */
public class TethysSwingDateDialog
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * Standard font name.
     */
    private static final String FONT_NAME = "Courier";

    /**
     * Standard font size.
     */
    private static final int FONT_SIZE = 10;

    /**
     * Standard font.
     */
    private static final Font FONT_STANDARD = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE);

    /**
     * Inactive font.
     */
    private static final Font FONT_INACTIVE = new Font(FONT_NAME, Font.ITALIC, FONT_SIZE);

    /**
     * Selected font.
     */
    private static final Font FONT_SELECTED = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);

    /**
     * ToolTip for Current Day.
     */
    private static final String NLS_CURRENTDAY = TethysDateResource.DIALOG_CURRENT.getValue();

    /**
     * ToolTip for Selected Day.
     */
    private static final String NLS_SELECTEDDAY = TethysDateResource.DIALOG_SELECTED.getValue();

    /**
     * ToolTip for Next Month.
     */
    private static final String NLS_NEXTMONTH = TethysDateResource.DIALOG_NEXTMONTH.getValue();

    /**
     * ToolTip for Previous Month.
     */
    private static final String NLS_PREVMONTH = TethysDateResource.DIALOG_PREVMONTH.getValue();

    /**
     * ToolTip for Next Year.
     */
    private static final String NLS_NEXTYEAR = TethysDateResource.DIALOG_NEXTYEAR.getValue();

    /**
     * ToolTip for Previous Year.
     */
    private static final String NLS_PREVYEAR = TethysDateResource.DIALOG_PREVYEAR.getValue();

    /**
     * Null Date selection text.
     */
    private static final String NLS_NULLSELECT = TethysDateResource.DIALOG_NULL.getValue();

    /**
     * Escape action text.
     */
    private static final String ACTION_ESCAPE = "Escape";

    /**
     * The dialog.
     */
    private final JDialog theDialog;

    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The month array.
     */
    private final PanelMonth theDaysPanel;

    /**
     * The navigation.
     */
    private final PanelNavigation theNavigation;

    /**
     * The Null Select.
     */
    private final JButton theNullButton;

    /**
     * Is the Null button active.
     */
    private boolean isNullActive = false;

    /**
     * The Date Configuration.
     */
    private final TethysDateConfig theConfig;

    /**
     * Should we build names?
     */
    private boolean doBuildNames = true;

    /**
     * The container panel.
     */
    private final JPanel theContainer;

    /**
     * Have we selected a date?
     */
    private boolean haveSelected = false;

    /**
     * Constructor.
     * @param pConfig the configuration for the dialog
     */
    public TethysSwingDateDialog(final TethysDateConfig pConfig) {
        /* Initialise the dialog */
        theDialog = new JDialog();

        /* Set as undecorated */
        theDialog.setUndecorated(true);

        /* Store the DateConfig */
        theConfig = pConfig;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the panels */
        theDaysPanel = new PanelMonth(this);
        theNavigation = new PanelNavigation(this);

        /* Build the Null Select */
        theNullButton = new JButton(NLS_NULLSELECT);
        theNullButton.addActionListener(e -> setSelected(-1));

        /* Set this to be the main panel */
        theContainer = new JPanel(new BorderLayout());
        theContainer.setBorder(BorderFactory.createLineBorder(Color.black));
        theContainer.add(theNavigation, BorderLayout.NORTH);
        theContainer.add(theDaysPanel, BorderLayout.CENTER);
        theDialog.setContentPane(theContainer);
        theDialog.pack();

        /* Initialise the month */
        initialiseMonth();

        /* Handle Escape Key */
        handleEscapeKey(theContainer);

        /* Create focus listener */
        theDialog.addWindowFocusListener(new CalendarFocus());

        /* Add listener to Configuration to reBuild Names */
        theConfig.getEventRegistrar().addEventListener(e -> doBuildNames());
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Request a rebuild of panel names.
     */
    private void doBuildNames() {
        doBuildNames = true;
    }

    /**
     * Have we selected a date?
     * @return true/false
     */
    public boolean haveSelected() {
        return haveSelected;
    }

    /**
     * Obtain Date Configuration.
     * @return the date configuration
     */
    public TethysDateConfig getConfig() {
        return theConfig;
    }

    /**
     * Build the month.
     */
    private void buildMonth() {
        /* Build the month */
        theNavigation.buildMonth();
        theDaysPanel.buildMonth();
    }

    /**
     * Set Selected Date.
     * @param pDay the Selected day
     */
    private void setSelected(final int pDay) {
        /* Set the selected day */
        theConfig.setSelectedDay(pDay);

        /* Note that we have selected */
        haveSelected = true;

        /* Close the dialog */
        theDialog.setVisible(false);

        /* Note that selection has been made */
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, theConfig.getSelectedDate());
    }

    /**
     * Resize the dialog.
     */
    private void reSizeDialog() {
        theDialog.pack();
    }

    /**
     * InitialiseMonth.
     */
    private void initialiseMonth() {
        /* Initialise the current month */
        theConfig.initialiseCurrent();

        /* Build the day names if required */
        if (doBuildNames) {
            theDaysPanel.buildDayNames();
        }
        doBuildNames = false;

        /* Build detail */
        buildMonth();

        /* If we need to change the visibility of the null button */
        if (isNullActive != theConfig.allowNullDateSelection()) {
            /* Add/Remove the button */
            if (!isNullActive) {
                theContainer.add(theNullButton, BorderLayout.SOUTH);
            } else {
                theContainer.remove(theNullButton);
            }

            /* Record status and resize the dialog */
            isNullActive = theConfig.allowNullDateSelection();
            reSizeDialog();
        }
    }

    /**
     * Show the dialog.
     * @param pNode the node under which to show the dialog
     */
    public void showDialogUnderNode(final JComponent pNode) {
        /* Position the dialog just below the node */
        Point myLoc = pNode.getLocationOnScreen();
        theDialog.setLocation(myLoc.x, myLoc.y
                                       + pNode.getHeight());

        /* Show the dialog */
        showDialog();
    }

    /**
     * Show the dialog.
     * @param pRect the rectangle under which to show the dialog
     */
    public void showDialogUnderRectangle(final Rectangle pRect) {
        /* Position the dialog just below the rectangle */
        theDialog.setLocation(pRect.x, pRect.y + pRect.height);

        /* Show the dialog */
        showDialog();
    }

    /**
     * Show the dialog.
     */
    private void showDialog() {
        /* Allow configuration to be updated */
        theEventManager.fireEvent(TethysUIEvent.PREPAREDIALOG, theConfig);

        /* Note that we have not selected */
        haveSelected = false;

        /* Initialise the current month and show the dialog */
        initialiseMonth();
        theDialog.setVisible(true);
        theDialog.requestFocus();
    }

    /**
     * Handle Escape to close dialog.
     * @param pPane the panel to handle keys for
     */
    private void handleEscapeKey(final JPanel pPane) {
        /* Access Maps */
        ActionMap myAction = pPane.getActionMap();
        InputMap myInput = pPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        /* Build the maps */
        myInput.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ACTION_ESCAPE);
        myAction.put(ACTION_ESCAPE, new CalendarAction());
    }

    /**
     * Close non-Modal.
     */
    private void closeNonModal() {
        /* Hide the dialog */
        theDialog.setVisible(false);

        /* Note that no selection has been made */
        theEventManager.fireEvent(TethysUIEvent.WINDOWCLOSED);
    }

    /**
     * CalendarAction Handle escape action.
     */
    private class CalendarAction
            extends AbstractAction {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 5464442251457102478L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            closeNonModal();
        }
    }

    /**
     * CalendarFocus Handle loss of focus.
     */
    private class CalendarFocus
            extends WindowAdapter {
        @Override
        public void windowLostFocus(final WindowEvent e) {
            Window myOppo = e.getOppositeWindow();
            /*
             * Ignore loss of focus to unidentified window. This is to bypass a problem in browser
             * applets where a temporary loss of focus occurs immediately upon display of a dialog.
             */
            if (myOppo != null) {
                closeNonModal();
            }
        }
    }

    /**
     * PanelNavigation class allowing navigation between months.
     */
    private static final class PanelNavigation
            extends JPanel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -1190726977851862746L;

        /**
         * The owning dialog.
         */
        private final transient TethysSwingDateDialog theDialog;

        /**
         * The Date Configuration.
         */
        private final transient TethysDateConfig theConfig;

        /**
         * The Date Label.
         */
        private final JLabel theDateLabel;

        /**
         * The Previous Month Button.
         */
        private final JButton thePrevMonthButton;

        /**
         * The Next Month Button.
         */
        private final JButton theNextMonthButton;

        /**
         * The Previous Year Button.
         */
        private final JButton thePrevYearButton;

        /**
         * The Next Year Button.
         */
        private final JButton theNextYearButton;

        /**
         * Constructor.
         * @param pDialog the owning dialog
         */
        private PanelNavigation(final TethysSwingDateDialog pDialog) {
            /* Record the dialog */
            theDialog = pDialog;

            /* Store the Date Configuration */
            theConfig = pDialog.getConfig();

            /* Create the label */
            theDateLabel = new JLabel();

            /* Create the buttons */
            thePrevMonthButton = new JButton(TethysSwingArrowIcon.LEFT);
            theNextMonthButton = new JButton(TethysSwingArrowIcon.RIGHT);
            thePrevYearButton = new JButton(TethysSwingArrowIcon.DOUBLELEFT);
            theNextYearButton = new JButton(TethysSwingArrowIcon.DOUBLERIGHT);

            /* Add ToopTips */
            theNextMonthButton.setToolTipText(NLS_NEXTMONTH);
            thePrevMonthButton.setToolTipText(NLS_PREVMONTH);
            theNextYearButton.setToolTipText(NLS_NEXTYEAR);
            thePrevYearButton.setToolTipText(NLS_PREVYEAR);

            /* Listen for button events */
            NavigateListener myListener = new NavigateListener();
            thePrevMonthButton.addActionListener(myListener);
            theNextMonthButton.addActionListener(myListener);
            thePrevYearButton.addActionListener(myListener);
            theNextYearButton.addActionListener(myListener);

            /* Restrict the margins */
            thePrevMonthButton.setMargin(new Insets(1, 1, 1, 1));
            theNextMonthButton.setMargin(new Insets(1, 1, 1, 1));
            thePrevYearButton.setMargin(new Insets(1, 1, 1, 1));
            theNextYearButton.setMargin(new Insets(1, 1, 1, 1));

            /* Add these elements into a box */
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(thePrevYearButton);
            add(Box.createHorizontalStrut(1));
            add(thePrevMonthButton);
            add(Box.createHorizontalGlue());
            add(theDateLabel);
            add(Box.createHorizontalGlue());
            add(theNextMonthButton);
            add(Box.createHorizontalStrut(1));
            add(theNextYearButton);
        }

        /**
         * Build month details.
         */
        private void buildMonth() {
            /* Store the active month */
            TethysDate myBase = theConfig.getCurrentMonth();
            Locale myLocale = theConfig.getLocale();

            /* Determine the display for the label */
            String myMonth = myBase.getMonthValue().getDisplayName(TextStyle.FULL, myLocale);
            String myYear = Integer.toString(myBase.getYear());

            /* Set the label */
            theDateLabel.setText(myMonth
                                 + ", "
                                 + myYear);

            /* Access boundary dates */
            TethysDate myEarliest = theConfig.getEarliestDate();
            TethysDate myLatest = theConfig.getLatestDate();

            /* Enable/Disable buttons as required */
            thePrevMonthButton.setEnabled(!TethysDateConfig.isSameMonth(myEarliest, myBase));
            thePrevYearButton.setEnabled(!TethysDateConfig.isSameYear(myEarliest, myBase));
            theNextMonthButton.setEnabled(!TethysDateConfig.isSameMonth(myLatest, myBase));
            theNextYearButton.setEnabled(!TethysDateConfig.isSameYear(myLatest, myBase));
        }

        /**
         * Action Listener for buttons.
         */
        private class NavigateListener
                implements ActionListener {
            @Override
            public void actionPerformed(final ActionEvent e) {
                /* Access the event source */
                Object src = e.getSource();

                /* If the button is previous month */
                if (thePrevMonthButton.equals(src)) {
                    /* Adjust the month */
                    theConfig.previousMonth();
                    theDialog.buildMonth();
                } else if (theNextMonthButton.equals(src)) {
                    /* Adjust the month */
                    theConfig.nextMonth();
                    theDialog.buildMonth();
                } else if (thePrevYearButton.equals(src)) {
                    /* Adjust the month */
                    theConfig.previousYear();
                    theDialog.buildMonth();
                } else if (theNextYearButton.equals(src)) {
                    /* Adjust the month */
                    theConfig.nextYear();
                    theDialog.buildMonth();
                }
            }
        }
    }

    /**
     * PanelMonth class representing the set of PanelDay labels in a month.
     */
    private static final class PanelMonth
            extends JPanel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4924971642341121199L;

        /**
         * Number of days in week.
         */
        private static final int DAYS_IN_WEEK = 7;

        /**
         * Maximum # of weeks in month.
         */
        private static final int MAX_WEEKS_IN_MONTH = 6;

        /**
         * The array of days of week (in column order).
         */
        private final DayOfWeek[] theDaysOfWk = new DayOfWeek[DAYS_IN_WEEK];

        /**
         * The Array of Day Names.
         */
        private final JLabel[] theHdrs = new JLabel[DAYS_IN_WEEK];

        /**
         * The Array of Day Labels.
         */
        private final PanelDay[][] theDays = new PanelDay[MAX_WEEKS_IN_MONTH][DAYS_IN_WEEK];

        /**
         * The Dialog.
         */
        private final transient TethysSwingDateDialog theDialog;

        /**
         * The Date Configuration.
         */
        private final transient TethysDateConfig theConfig;

        /**
         * The number of currently visible rows.
         */
        private int theNumRows = MAX_WEEKS_IN_MONTH;

        /**
         * Constructor.
         * @param pDialog the owning dialog
         */
        private PanelMonth(final TethysSwingDateDialog pDialog) {
            /* Store the dialog */
            theDialog = pDialog;

            /* Store the Date Configuration */
            theConfig = pDialog.getConfig();

            /* Set this as a 7x7 GridLayout */
            GridLayout myLayout = new GridLayout();
            myLayout.setColumns(DAYS_IN_WEEK);
            myLayout.setRows(0);
            setLayout(myLayout);

            /* Loop through the labels */
            for (int iCol = 0; iCol < DAYS_IN_WEEK; iCol++) {
                /* Access the label */
                JLabel myLabel = new JLabel();
                theHdrs[iCol] = myLabel;

                /* Set colour */
                myLabel.setHorizontalAlignment(SwingConstants.CENTER);
                myLabel.setBackground(Color.lightGray);
                myLabel.setOpaque(true);

                /* Add to the grid */
                add(myLabel);
            }

            /* Add the Days to the layout */
            for (int iRow = 0; iRow < MAX_WEEKS_IN_MONTH; iRow++) {
                for (int iCol = 0; iCol < DAYS_IN_WEEK; iCol++) {
                    PanelDay myDay = new PanelDay(pDialog);
                    theDays[iRow][iCol] = myDay;
                    add(myDay);
                }
            }
        }

        /**
         * ReSize the number of visible rows.
         * @param iNumRows number of visible rows
         */
        private void reSizeRows(final int iNumRows) {
            /* Hide any visible rows that should now be hidden */
            while (iNumRows < theNumRows) {
                /* Decrement number of rows */
                theNumRows--;

                /* Loop through remaining rows */
                for (PanelDay day : theDays[theNumRows]) {
                    /* Remove from panel */
                    remove(day);
                }
            }

            /* Show any hidden rows that should now be visible */
            while (iNumRows > theNumRows) {
                /* Loop through remaining rows */
                for (PanelDay day : theDays[theNumRows]) {
                    /* Add to panel */
                    add(day);
                }

                /* Increment number of rows */
                theNumRows++;
            }

            /* RePack the Dialog */
            theDialog.reSizeDialog();
        }

        /**
         * Is the DayOfWeek a Weekend day.
         * @param pDoW the day of the week
         * @return true/false
         */
        private static boolean isWeekend(final DayOfWeek pDoW) {
            switch (pDoW) {
                case SATURDAY:
                case SUNDAY:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * obtain column number for DayOfWeek.
         * @param pDoW the day of the week
         * @return the column number
         */
        private int getDayColumn(final DayOfWeek pDoW) {
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                if (theDaysOfWk[i] == pDoW) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * build the month display for the requested month.
         */
        private void buildMonth() {
            int iRow = 0;
            int iCol = 0;

            /* Access the current month */
            TethysDate myCurr = new TethysDate(theConfig.getCurrentMonth());
            int iMonth = myCurr.getMonth();

            /* Access the Weekday of the 1st of the month */
            DayOfWeek myWeekDay = myCurr.getDayOfWeek();
            int iFirstCol = getDayColumn(myWeekDay);

            /* Access the interesting days of the month */
            int iCurrent = theConfig.getCurrentDay();
            int iSelected = theConfig.getSelectedDay();
            int iEarliest = theConfig.getEarliestDay();
            int iLatest = theConfig.getLatestDay();

            /* Adjust the day to beginning of week if required */
            if (iFirstCol > 0) {
                myCurr.adjustDay(-iFirstCol);
            }

            /* Loop through initial columns */
            for (int iDay = myCurr.getDay(); iCol < iFirstCol; iCol++, iDay++, myCurr.adjustDay(1)) {
                /* Access the label */
                PanelDay myLabel = theDays[0][iCol];

                /* Reset the day and set no day */
                myLabel.resetDay(false);
                myLabel.setDay(iDay, false);
            }

            /* Loop through the days of the month */
            for (int iDay = 1; iMonth == myCurr.getMonth(); iCol++, iDay++, myCurr.adjustDay(1)) {
                /* Reset column if necessary */
                if (iCol > MAX_WEEKS_IN_MONTH) {
                    iRow++;
                    iCol = 0;
                }

                /* Access the label */
                PanelDay myLabel = theDays[iRow][iCol];

                /* Set initial parts of the day */
                myLabel.resetDay(true);
                if (isWeekend(myCurr.getDayOfWeek())) {
                    myLabel.setWeekend();
                }
                if (iCurrent == iDay) {
                    myLabel.setCurrent();
                }
                if (iSelected == iDay) {
                    myLabel.setSelected();
                }

                /* Determine whether the day is select-able */
                boolean isSelectable = true;
                if (iEarliest > 0) {
                    isSelectable &= iDay >= iEarliest;
                }
                if (iLatest > 0) {
                    isSelectable &= iDay <= iLatest;
                }

                /* Set the day */
                myLabel.setDay(iDay, isSelectable);
            }

            /* Loop through remaining columns */
            for (int iDay = 1; iCol < DAYS_IN_WEEK; iCol++, iDay++) {
                /* Access the label */
                PanelDay myLabel = theDays[iRow][iCol];

                /* Reset the day and set no day */
                myLabel.resetDay(false);
                myLabel.setDay(iDay, false);
            }

            /* Ensure correct number of rows are visible */
            reSizeRows(iRow + 1);
        }

        /**
         * build Day names.
         */
        private void buildDayNames() {
            /* Get todays date */
            Locale myLocale = theConfig.getLocale();
            Calendar myDate = Calendar.getInstance(myLocale);
            int myStart = myDate.getFirstDayOfWeek();
            if (myStart == Calendar.SUNDAY) {
                myStart += DAYS_IN_WEEK;
            }

            /* Build the array of the days of the week */
            DayOfWeek myDoW = DayOfWeek.of(myStart - 1);
            for (int iDay = 0; iDay < DAYS_IN_WEEK; iDay++, myDoW = myDoW.plus(1)) {
                /* Store the day into the array */
                theDaysOfWk[iDay] = myDoW;
            }

            /* Loop through the labels */
            for (int iCol = 0; iCol < DAYS_IN_WEEK; iCol++) {
                /* Access the label */
                JLabel myLabel = theHdrs[iCol];

                /* Access the required name */
                myDoW = theDaysOfWk[iCol];
                TextStyle myStyle = theConfig.showNarrowDays()
                                                               ? TextStyle.NARROW
                                                               : TextStyle.SHORT;
                String myName = myDoW.getDisplayName(myStyle, myLocale);

                /* Set the name */
                myLabel.setText(myName);

                /* Set colour */
                myLabel.setForeground(isWeekend(myDoW)
                                                       ? Color.red
                                                       : Color.black);
            }
        }
    }

    /**
     * Panel class representing a single day in the panel.
     */
    private static final class PanelDay
            extends JLabel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6287784334912236592L;

        /**
         * Owning dialog.
         */
        private final transient TethysSwingDateDialog theDialog;

        /**
         * The standard border.
         */
        private static final Border BORDER_STD = BorderFactory.createEmptyBorder();

        /**
         * The selected border.
         */
        private static final Border BORDER_SEL = BorderFactory.createLineBorder(Color.green.darker());

        /**
         * The highlighted border.
         */
        private static final Border BORDER_HLT = BorderFactory.createLineBorder(Color.orange);

        /**
         * The Day that this Label represents.
         */
        private int theDay = -1;

        /**
         * Is the day select-able.
         */
        private boolean isSelectable;

        /**
         * The font.
         */
        private Font theFont;

        /**
         * The foreground colour.
         */
        private Color theForeGround;

        /**
         * The background colour.
         */
        private Color theBackGround;

        /**
         * The border.
         */
        private transient Border theBorder;

        /**
         * The toolTip.
         */
        private String theToolTip;

        /**
         * Constructor.
         * @param pDialog the owning dialog
         */
        private PanelDay(final TethysSwingDateDialog pDialog) {
            /* Store the parameter */
            theDialog = pDialog;

            /* Initialise values */
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            addMouseListener(new CalendarMouse(this));
        }

        /**
         * Set day for label.
         * @param pDay the Day number
         * @param pSelectable is the day select-able
         */
        private void setDay(final int pDay,
                            final boolean pSelectable) {
            /* Record the day */
            theDay = pDay;
            isSelectable = pSelectable;

            /* Set the text for the item */
            if (pDay > 0) {
                setText(Integer.toString(theDay));
            } else {
                setText("");
            }

            /* Set Characteristics */
            setFont(theFont);
            setForeground(theForeGround);
            setBackground(theBackGround);
            setBorder(theBorder);
            setToolTipText(theToolTip);

            /* Enable/Disable the label */
            setEnabled(isSelectable);
        }

        /**
         * Reset a Day Label.
         * @param isActive true/false
         */
        private void resetDay(final boolean isActive) {
            /* Record detail */
            theFont = isActive
                               ? FONT_STANDARD
                               : FONT_INACTIVE;
            theForeGround = Color.black;
            theBackGround = Color.white;
            theBorder = BORDER_STD;
            theToolTip = null;
            isSelectable = isActive;
        }

        /**
         * Set a day as a Weekend.
         */
        private void setWeekend() {
            /* Record detail */
            theForeGround = Color.red;
        }

        /**
         * Set a day as Current Day.
         */
        private void setCurrent() {
            /* Record detail */
            theForeGround = Color.blue;
            theBackGround = Color.gray;
            theToolTip = NLS_CURRENTDAY;
        }

        /**
         * Set a day as Selected Day.
         */
        private void setSelected() {
            /* Record detail */
            theFont = FONT_SELECTED;
            theForeGround = Color.green.darker();
            theBackGround = Color.green.brighter();
            theBorder = BORDER_SEL;
            theToolTip = NLS_SELECTEDDAY;
        }

        /**
         * CalendarMouse.
         */
        private final class CalendarMouse
                extends MouseAdapter {
            /**
             * The Day Panel.
             */
            private final PanelDay theOwner;

            /**
             * Constructor.
             * @param pOwner the owning panel
             */
            private CalendarMouse(final PanelDay pOwner) {
                /* Store parameters */
                theOwner = pOwner;
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                /* If item is select-able */
                if (isSelectable) {
                    theDialog.setSelected(theDay);
                }
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                /* Highlight the border of a select-able item */
                if (isSelectable) {
                    theOwner.setBorder(BORDER_HLT);
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                /* Reset border to standard for label that has changed */
                if (isSelectable) {
                    theOwner.setBorder(theBorder);
                }
            }
        }
    }
}
