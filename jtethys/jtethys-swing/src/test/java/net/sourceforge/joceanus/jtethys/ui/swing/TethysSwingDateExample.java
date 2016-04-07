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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableCell;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;

/**
 * <p>
 * Provides a simple application that illustrates the features of JDateDay.
 * @author Tony Washer
 */
public class TethysSwingDateExample
        extends JApplet {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2036674133513416250L;

    /**
     * Inset depth.
     */
    private static final int INSET_DEPTH = 5;

    /**
     * First Column Width.
     */
    private static final int COL_1_WIDTH = 100;

    /**
     * Second Column Width.
     */
    private static final int COL_2_WIDTH = 200;

    /**
     * Scroll Width.
     */
    private static final int SCROLL_WIDTH = 500;

    /**
     * Scroll Height.
     */
    private static final int SCROLL_HEIGHT = 120;

    /**
     * Neutral Weight.
     */
    private static final double WEIGHT_NEUTRAL = 0.5f;

    /**
     * Start sample date.
     */
    private static final TethysDate DATE_START = makeDate(2007, Month.JANUARY, 25);

    /**
     * End sample date.
     */
    private static final TethysDate DATE_END = makeDate(2018, Month.AUGUST, 9);

    /**
     * First sample date.
     */
    private static final TethysDate DATE_FIRST = makeDate(2011, Month.JULY, 1);

    /**
     * Second sample date.
     */
    private static final TethysDate DATE_SECOND = makeDate(2012, Month.MARCH, 14);

    /**
     * Third sample date.
     */
    private static final TethysDate DATE_THIRD = makeDate(2014, Month.NOVEMBER, 19);

    /**
     * Fourth sample date.
     */
    private static final TethysDate DATE_FOURTH = makeDate(2015, Month.MAY, 31);

    /**
     * Fifth sample date.
     */
    private static final TethysDate DATE_FIFTH = makeDate(2018, Month.FEBRUARY, 28);

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysSwingDateExample.class);

    /**
     * The first date format.
     */
    private static final String DATEFORMAT_1 = "dd-MMM-yyyy";

    /**
     * The selected format.
     */
    private static final String DATEFORMAT_2 = "dd/MMM/yy";

    /**
     * The selected format.
     */
    private static final String DATEFORMAT_3 = "yyyy/MMM/dd";

    /**
     * The GUI Factory.
     */
    private transient TethysSwingGuiFactory theGuiFactory = new TethysSwingGuiFactory();

    /**
     * The table.
     */
    private TethysSwingTableManager<String, DateItem> theTable;

    /**
     * The list of locales.
     */
    private JComboBox<ShortLocale> theLocaleList;

    /**
     * The list of formats.
     */
    private JComboBox<String> theFormatList;

    /**
     * The start date.
     */
    private transient TethysSwingDateButtonManager theStartDate;

    /**
     * The end date.
     */
    private transient TethysSwingDateButtonManager theEndDate;

    /**
     * The Null Select checkBox.
     */
    private JCheckBox theNullSelect;

    /**
     * The ShowNarrow checkBox.
     */
    private JCheckBox theNarrowSelect;

    /**
     * The selected range.
     */
    private JLabel theSelectedRange;

    /**
     * The selected locale.
     */
    private Locale theLocale = Locale.UK;

    /**
     * The selected format.
     */
    private String theFormat = DATEFORMAT_1;

    /**
     * The listener.
     */
    private transient DateListener theListener = new DateListener();

    /**
     * The range selection.
     */
    private transient TethysSwingDateRangeSelector theRangeSelect;

    /**
     * The formatter.
     */
    private transient TethysDataFormatter theFormatter = theGuiFactory.getDataFormatter();

    /**
     * The formatter.
     */
    private transient TethysDateFormatter theDateFormatter = theFormatter.getDateFormatter();

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("TethysDate Swing Demo");

            /* Create the Example program */
            TethysSwingDateExample myProgram = new TethysSwingDateExample();

            /* Create the panel */
            JPanel myPanel = myProgram.makePanel();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    @Override
    public void init() {
        // Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    /* Create the panel */
                    JPanel myPanel = makePanel();
                    setContentPane(myPanel);
                }
            });
        } catch (InvocationTargetException e) {
            LOGGER.error("Failed to invoke thread", e);
        } catch (InterruptedException e) {
            LOGGER.error("Thread was interrupted", e);
        }
    }

    /**
     * Create the panel.
     * @return the panel
     */
    private JPanel makePanel() {
        /* Create the table */
        makeTable();

        /* Create the range buttons */
        makeRangeButtons();

        /* Create the locale list */
        makeLocaleList();

        /* Create the format list */
        makeFormatList();

        /* Create the additional labels */
        JLabel myFormat = new JLabel("Format:");
        JLabel myLocale = new JLabel("Locale:");
        JLabel myStart = new JLabel("Start:");
        JLabel myEnd = new JLabel("End:");
        JLabel mySelRange = new JLabel("SelectedRange:");

        /* Create a Range sub-panel */
        JPanel myRange = new JPanel(new GridBagLayout());
        GridBagConstraints myConstraints = new GridBagConstraints();
        myRange.setBorder(BorderFactory.createTitledBorder("Range Selection"));
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
        myRange.add(myStart, myConstraints);
        myConstraints.gridx = 1;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 1.0;
        myRange.add(theStartDate.getNode(), myConstraints);
        myConstraints.gridx = 0;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
        myRange.add(myEnd, myConstraints);
        myConstraints.gridx = 1;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 1.0;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myRange.add(theEndDate.getNode(), myConstraints);

        /* Create a Style sub-panel */
        JPanel myStyle = new JPanel(new GridBagLayout());
        myStyle.setBorder(BorderFactory.createTitledBorder("Format Selection"));
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
        myStyle.add(myLocale, myConstraints);
        myConstraints.gridx = 1;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 1.0;
        myStyle.add(theLocaleList, myConstraints);
        myConstraints.gridx = 0;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
        myStyle.add(myFormat, myConstraints);
        myConstraints.gridx = 1;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 1.0;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myStyle.add(theFormatList, myConstraints);

        /* Create a range select sub-panel */
        JPanel myRangeSelect = new JPanel(new GridBagLayout());
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 2;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myConstraints.weightx = 1.0;
        theRangeSelect.setBorderTitle("Range Selection");
        myRangeSelect.add(theRangeSelect.getNode(), myConstraints);
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myRangeSelect.add(mySelRange, myConstraints);
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 1;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 1.0;
        myRangeSelect.add(theSelectedRange, myConstraints);

        /* Create the panel */
        JPanel myPanel = new JPanel(new GridBagLayout());
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = WEIGHT_NEUTRAL;
        myPanel.add(myRange, myConstraints);
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 1;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = WEIGHT_NEUTRAL;
        myPanel.add(myStyle, myConstraints);

        /* Build options panel */
        JPanel myOptions = makeOptionsPanel();
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 2;
        myConstraints.fill = GridBagConstraints.BOTH;
        myPanel.add(myOptions, myConstraints);

        theTable.getNode().setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 2;
        myConstraints.gridwidth = 2;
        myConstraints.fill = GridBagConstraints.BOTH;
        myPanel.add(theTable.getNode(), myConstraints);

        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 3;
        myConstraints.gridwidth = 2;
        myConstraints.gridheight = GridBagConstraints.REMAINDER;
        myConstraints.weightx = WEIGHT_NEUTRAL;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myPanel.add(myRangeSelect, myConstraints);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Create the Table.
     */
    private void makeTable() {
        /* Create the table */
        theTable = theGuiFactory.newTable();

        /* Create the list */
        List<DateItem> myList = new ArrayList<DateItem>();
        myList.add(new DateItem(DATE_FIRST, "First Entry"));
        myList.add(new DateItem(DATE_SECOND, "Second Entry"));
        myList.add(new DateItem(DATE_THIRD, "Third Entry"));
        myList.add(new DateItem(DATE_FOURTH, "Fourth Entry"));
        myList.add(new DateItem(DATE_FIFTH, "Fifth Entry"));

        /* Declare the list to the table */
        theTable.setItems(myList);

        /* Create the date column */
        TethysSwingTableDateColumn<String, DateItem> myDateColumn = theTable.declareDateColumn(DateItem.PROP_DATE);
        myDateColumn.setCellValueFactory(p -> p.getDate());
        myDateColumn.setCellCommitFactory((p, v) -> p.setDate(v));
        myDateColumn.setColumnWidth(COL_1_WIDTH);

        /* Create the comments column */
        TethysSwingTableStringColumn<String, DateItem> myCommentsColumn = theTable.declareStringColumn(DateItem.PROP_COMMENTS);
        myCommentsColumn.setCellValueFactory(p -> p.getComments());
        myCommentsColumn.setCellCommitFactory((p, v) -> p.setComments(v));
        myCommentsColumn.setColumnWidth(COL_2_WIDTH);

        /* Listen to preEdit requests */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theTable.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.CELLPREEDIT, this::handlePreEdit);
    }

    /**
     * Handle preEdit event.
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handlePreEdit(final TethysEvent<TethysUIEvent> pEvent) {
        TethysSwingTableCell<String, DateItem, ?> myCell = pEvent.getDetails(TethysSwingTableCell.class);

        /* If this is the Date column */
        if (DateItem.PROP_DATE.equals(myCell.getColumnId())) {
            /* Configure the button */
            TethysDateField<?, ?> myDateField = (TethysDateField<?, ?>) myCell;
            TethysDateButtonManager<?, ?> myManager = myDateField.getDateManager();
            myManager.setEarliestDate(theStartDate.getSelectedDate());
            myManager.setLatestDate(theEndDate.getSelectedDate());
            myManager.setShowNarrowDays(theNarrowSelect.isSelected());
        }
    }

    /**
     * Create the list of available locales.
     */
    private void makeLocaleList() {
        /* Create the Combo box and populate it */
        theLocaleList = new JComboBox<ShortLocale>();
        for (ShortLocale myLocale : ShortLocale.values()) {
            /* Add the Locale to the list */
            theLocaleList.addItem(myLocale);
        }

        /* Add Listener to List */
        theLocaleList.addItemListener(theListener);

        /* Set the default item */
        theLocaleList.setSelectedItem(ShortLocale.UK);
    }

    /**
     * Create the list of available formats.
     */
    private void makeFormatList() {
        /* Create the Combo box and populate it */
        theFormatList = new JComboBox<String>();
        theFormatList.addItem(DATEFORMAT_1);
        theFormatList.addItem(DATEFORMAT_2);
        theFormatList.addItem(DATEFORMAT_3);

        /* Add Listener to List */
        theFormatList.addItemListener(theListener);

        /* Set the default item */
        theFormatList.setSelectedItem(theFormat);
    }

    /**
     * Create the options panel.
     * @return the panel
     */
    private JPanel makeOptionsPanel() {
        /* Create the checkBoxes */
        makeCheckBoxes();

        /* Create an options sub-panel */
        JPanel myOptions = new JPanel();
        myOptions.setLayout(new BoxLayout(myOptions, BoxLayout.X_AXIS));
        myOptions.add(Box.createHorizontalGlue());
        myOptions.add(theNullSelect);
        myOptions.add(Box.createHorizontalGlue());
        myOptions.add(theNarrowSelect);
        myOptions.add(Box.createHorizontalGlue());

        /* Return the panel */
        myOptions.setBorder(BorderFactory.createTitledBorder("Options"));
        return myOptions;
    }

    /**
     * Create the range buttons.
     */
    private void makeRangeButtons() {
        /* Create the buttons */
        theStartDate = theGuiFactory.newDateButton();
        theEndDate = theGuiFactory.newDateButton();

        /* Create the range selection */
        theRangeSelect = theGuiFactory.newDateRangeSelector(true);
        theRangeSelect.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewRange());

        /* Initialise the values */
        TethysDate myStart = DATE_START;
        TethysDate myEnd = DATE_END;

        /* Set the values */
        theStartDate.setSelectedDate(myStart);
        theEndDate.setSelectedDate(myEnd);

        /* Apply the range */
        applyRange();

        /* Add Listener to buttons */
        theStartDate.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> applyRange());
        theEndDate.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> applyRange());

        /* Create the range report label */
        theSelectedRange = new JLabel(theRangeSelect.getRange().toString());
    }

    /**
     * Convenience method to create a date from Year, Month, Day.
     * @param pYear the year
     * @param pMonth the month
     * @param pDay the day
     * @return the requested date
     */
    private static TethysDate makeDate(final int pYear,
                                       final Month pMonth,
                                       final int pDay) {
        return new TethysDate(pYear, pMonth, pDay);
    }

    /**
     * Create the checkBox.
     */
    private void makeCheckBoxes() {
        /* Create the check boxes */
        theNullSelect = new JCheckBox("Null Date Select");
        theNarrowSelect = new JCheckBox("Show Narrow Days");

        /* Action selections */
        theNullSelect.addItemListener(e -> applyNullOption());
        theNarrowSelect.addItemListener(e -> applyNarrowOption());

        /* Initialise */
        applyNullOption();
        applyNarrowOption();
    }

    /**
     * Apply Options to underlying objects.
     */
    private void applyNullOption() {
        /* Set Null Date options */
        theStartDate.setAllowNullDateSelection(theNullSelect.isSelected());
        theEndDate.setAllowNullDateSelection(theNullSelect.isSelected());
    }

    /**
     * Apply Options to underlying objects.
     */
    private void applyNarrowOption() {
        /* Set Narrow Date options */
        theStartDate.setShowNarrowDays(theNarrowSelect.isSelected());
        theEndDate.setShowNarrowDays(theNarrowSelect.isSelected());
    }

    /**
     * Apply Locale to underlying objects.
     */
    private void applyLocale() {
        /* Set locale for formatter */
        theFormatter.setLocale(theLocale);
        theRangeSelect.setLocale(theLocale);

        /* Set range locale */
        theSelectedRange.setText(theDateFormatter.formatDateDayRange(theRangeSelect.getRange()));

        /* Note that we should redraw the table */
        theTable.repaintColumn(DateItem.PROP_DATE);
    }

    /**
     * Apply format to underlying objects.
     */
    private void applyFormat() {
        /* Set format for formatter */
        theFormatter.setFormat(theFormat);

        /* Set range format */
        theSelectedRange.setText(theDateFormatter.formatDateDayRange(theRangeSelect.getRange()));

        /* Note that we should redraw the table */
        theTable.repaintColumn(DateItem.PROP_COMMENTS);
    }

    /**
     * Apply Range to underlying objects.
     */
    private void applyRange() {
        /* Access the Start/End Dates */
        TethysDate myStart = theStartDate.getSelectedDate();
        TethysDate myEnd = theEndDate.getSelectedDate();

        /* Set the select-able range for the start/end buttons */
        theStartDate.setLatestDate(myEnd);
        theEndDate.setEarliestDate(myStart);

        /* set the range select range */
        theRangeSelect.setOverallRange(new TethysDateRange(myStart, myEnd));
    }

    /**
     * Handle the new range.
     */
    private void handleNewRange() {
        TethysDateRange myRange = theRangeSelect.getRange();
        if (theSelectedRange != null) {
            theSelectedRange.setText(theDateFormatter.formatDateDayRange(myRange));
        }
    }

    /**
     * Listener class.
     */
    private class DateListener
            implements ItemListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            /* Access source object */
            Object o = evt.getSource();

            /* Ignore if we are not selecting */
            if (evt.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            /* If this is the Locale list */
            if (theLocaleList.equals(o)) {
                /* Store the new locale */
                ShortLocale myLocale = (ShortLocale) evt.getItem();
                theLocale = myLocale.getLocale();

                /* Apply the new locale */
                applyLocale();

                /* If this is the Format list */
            } else if (theFormatList.equals(o)) {
                /* Store the new format */
                theFormat = (String) evt.getItem();

                /* Apply the new format */
                applyFormat();
            }
        }
    }

    /**
     * DateItem class.
     */
    public static final class DateItem {
        /**
         * Date Property Name.
         */
        private static final String PROP_DATE = "Date";

        /**
         * Date Property Name.
         */
        private static final String PROP_COMMENTS = "Comments";

        /**
         * Date Property.
         */
        private TethysDate theDate;

        /**
         * Comments Property.
         */
        private String theComments;

        /**
         * Constructor.
         * @param pDate the date
         * @param pComments the comments
         */
        private DateItem(final TethysDate pDate,
                         final String pComments) {
            /* Store parameters */
            theDate = pDate;
            theComments = pComments;
        }

        /**
         * Obtain the Date.
         * @return the name
         */
        public TethysDate getDate() {
            return theDate;
        }

        /**
         * Set the Date.
         * @param pDate the Date
         */
        public void setDate(final TethysDate pDate) {
            theDate = pDate;
        }

        /**
         * Obtain the Comments.
         * @return the Comment
         */
        public String getComments() {
            return theComments;
        }

        /**
         * Set the Comments.
         * @param pComments the Comments
         */
        public void setComments(final String pComments) {
            theComments = pComments;
        }
    }

    /**
     * Some useful locales.
     */
    private enum ShortLocale {
        /**
         * China (shorten day names to one character, and shrink from the right to make sure they
         * are different).
         */
        CHINA(Locale.CHINA),

        /**
         * Germany.
         */
        GERMANY(Locale.GERMANY),

        /**
         * France.
         */
        FRANCE(Locale.FRANCE),

        /**
         * Italy.
         */
        ITALY(Locale.ITALY),

        /**
         * Japan (shorten day names to one character).
         */
        JAPAN(Locale.JAPAN),

        /**
         * Korea (shorten day names to one character).
         */
        KOREA(Locale.KOREA),

        /**
         * US.
         */
        US(Locale.US),

        /**
         * UK (shorten day names to two characters).
         */
        UK(Locale.UK);

        /**
         * Locale property.
         */
        private final Locale theLocale;

        /**
         * Constructor.
         * @param pLocale the locale
         */
        private ShortLocale(final Locale pLocale) {
            /* Store the Locale */
            theLocale = pLocale;
        }

        /**
         * Obtain locale value.
         * @return the locale
         */
        public Locale getLocale() {
            return theLocale;
        }

        @Override
        public String toString() {
            switch (this) {
                case CHINA:
                    return "China";
                case FRANCE:
                    return "France";
                case GERMANY:
                    return "Germany";
                case ITALY:
                    return "Italy";
                case JAPAN:
                    return "Japan";
                case KOREA:
                    return "Korea";
                case US:
                    return "UnitedStates";
                case UK:
                    return "UnitedKingdom";
                default:
                    return "Unknown";
            }
        }
    }
}
