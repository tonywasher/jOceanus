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
package net.sourceforge.joceanus.jtethys.dateday.demo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.sourceforge.jdatebutton.JDateConfig;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayCellEditor;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayCellRenderer;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayConfig;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRangeSelect;

/**
 * <p>
 * Provides a simple application that illustrates the features of JDateDay.
 * @author Tony Washer
 */
public class JDateDayExample
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
    private static final int SCROLL_WIDTH = 400;

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
    private static final JDateDay DATE_START = makeDate(2007, Calendar.JANUARY, 25);

    /**
     * End sample date.
     */
    private static final JDateDay DATE_END = makeDate(2014, Calendar.AUGUST, 9);

    /**
     * First sample date.
     */
    private static final JDateDay DATE_FIRST = makeDate(2011, Calendar.JULY, 1);

    /**
     * Second sample date.
     */
    private static final JDateDay DATE_SECOND = makeDate(2012, Calendar.MARCH, 14);

    /**
     * Third sample date.
     */
    private static final JDateDay DATE_THIRD = makeDate(2012, Calendar.NOVEMBER, 19);

    /**
     * Fourth sample date.
     */
    private static final JDateDay DATE_FOURTH = makeDate(2013, Calendar.MAY, 31);

    /**
     * Fifth sample date.
     */
    private static final JDateDay DATE_FIFTH = makeDate(2014, Calendar.FEBRUARY, 28);

    /**
     * Logger.
     */
    private static Logger theLogger = Logger.getAnonymousLogger();

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
            theLogger.log(Level.SEVERE, "Failed to invoke thread", e);
        } catch (InterruptedException e) {
            theLogger.log(Level.SEVERE, "Thread was interrupted", e);
        }
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("DateDayButton Demo");

            /* Create the Example program */
            JDateDayExample myProgram = new JDateDayExample();

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
            theLogger.log(Level.SEVERE, "createGUI didn't complete successfully", e);
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

    /**
     * The table.
     */
    private JTable theTable = null;

    /**
     * The cell editor.
     */
    private JDateDayCellEditor theEditor = null;

    /**
     * The list of locales.
     */
    private JComboBox<ShortLocale> theLocaleList = null;

    /**
     * The list of formats.
     */
    private JComboBox<String> theFormatList = null;

    /**
     * The start date.
     */
    private JDateDayButton theStartDate = null;

    /**
     * The end date.
     */
    private JDateDayButton theEndDate = null;

    /**
     * The selected range.
     */
    private JLabel theSelectedRange = null;

    /**
     * The selected locale.
     */
    private Locale theLocale = Locale.UK;

    /**
     * The maximum day length.
     */
    private int theMaxDayLen = JDateConfig.MAX_DAY_NAME_LEN;

    /**
     * shrink day name from right.
     */
    private boolean doShrinkFromRight = true;

    /**
     * Prettify the days and months.
     */
    private boolean doPretty = true;

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
    private JDateDayRangeSelect theRangeSelect = null;

    /**
     * The formatter.
     */
    private transient JDateDayFormatter theFormatter = new JDateDayFormatter();

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

        /* Apply options */
        applyOptions();

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
        myRange.add(theStartDate, myConstraints);
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
        myRange.add(theEndDate, myConstraints);

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
        JPanel myOptions = new JPanel(new GridBagLayout());
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 2;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myOptions.add(theRangeSelect, myConstraints);
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myOptions.add(mySelRange, myConstraints);
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 1;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 1.0;
        myOptions.add(theSelectedRange, myConstraints);

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

        JScrollPane myScroll = new JScrollPane(theTable);
        Dimension mySize = new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT);
        myScroll.setMinimumSize(mySize);
        myScroll.setMaximumSize(mySize);
        theTable.setPreferredScrollableViewportSize(mySize);

        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 2;
        myConstraints.fill = GridBagConstraints.BOTH;
        myPanel.add(myScroll, myConstraints);

        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 2;
        myConstraints.gridwidth = 2;
        myConstraints.gridheight = GridBagConstraints.REMAINDER;
        myConstraints.weightx = WEIGHT_NEUTRAL;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myPanel.add(myOptions, myConstraints);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Table Model.
     */
    private static class DateTable
            extends AbstractTableModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4842107163051671807L;

        /**
         * Column Names.
         */
        private static final String[] COLUMNS = { "Date", "Description" };

        /**
         * Data for table.
         */
        private static final Object[][] DATA = { { DATE_FIRST, "First Entry" }, { DATE_SECOND, "Second Entry" }, { DATE_THIRD, "Third Entry" },
                { DATE_FOURTH, "Fourth Entry" }, { DATE_FIFTH, "Fifth Entry" } };

        @Override
        public int getColumnCount() {
            return DATA[0].length;
        }

        @Override
        public int getRowCount() {
            return DATA.length;
        }

        @Override
        public String getColumnName(final int columnIndex) {
            return COLUMNS[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == 0) {
                return JDateDay.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(final int rowIndex,
                                      final int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Object getValueAt(final int rowIndex,
                                 final int columnIndex) {
            return DATA[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(final Object pValue,
                               final int rowIndex,
                               final int columnIndex) {
            DATA[rowIndex][columnIndex] = pValue;
        }
    }

    /**
     * Create the Table.
     */
    private void makeTable() {
        /* Create the table */
        TableModel myModel = new DateTable();
        theTable = new JTable(myModel);

        /* Create the renderer and editor */
        JDateDayCellRenderer myRenderer = new JDateDayCellRenderer(theFormatter);
        theEditor = new JDateDayCellEditor(theFormatter);

        /* Set sorting on the table */
        theTable.setAutoCreateRowSorter(true);

        /* Format the first column */
        TableColumnModel myColModel = theTable.getColumnModel();

        /* Set the width of the column */
        TableColumn myFirstCol = myColModel.getColumn(0);
        myFirstCol.setPreferredWidth(COL_1_WIDTH);

        /* Set the renderer */
        myFirstCol.setCellRenderer(myRenderer);
        myFirstCol.setCellEditor(theEditor);

        /* Set the width of the column */
        TableColumn mySecondCol = myColModel.getColumn(1);
        mySecondCol.setPreferredWidth(COL_2_WIDTH);
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
     * Create the range buttons.
     */
    private void makeRangeButtons() {
        /* Create the buttons */
        theStartDate = new JDateDayButton(theFormatter);
        theEndDate = new JDateDayButton(theFormatter);

        /* Create the range selection */
        theRangeSelect = new JDateDayRangeSelect(theFormatter, false);
        theRangeSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, theListener);

        /* Initialise the values */
        JDateDay myStart = DATE_START;
        JDateDay myEnd = DATE_END;

        /* Set the values */
        theStartDate.setSelectedDateDay(myStart);
        theEndDate.setSelectedDateDay(myEnd);

        /* Apply the range */
        applyRange();

        /* Add Listener to buttons */
        theStartDate.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, theListener);
        theEndDate.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, theListener);

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
    private static JDateDay makeDate(final int pYear,
                                     final int pMonth,
                                     final int pDay) {
        return new JDateDay(pYear, pMonth, pDay);
    }

    /**
     * Apply Options to underlying objects.
     */
    private void applyOptions() {
        /* Set Null Date options */
        JDateDayConfig myConfig = theEditor.getDateConfig();
        myConfig.setAllowNullDateSelection(false);
        myConfig = theStartDate.getDateConfig();
        myConfig.setAllowNullDateSelection(true);
        myConfig = theEndDate.getDateConfig();
        myConfig.setAllowNullDateSelection(true);
    }

    /**
     * Apply Locale to underlying objects.
     */
    private void applyLocale() {
        /* Set locale for formatter */
        theFormatter.setLocale(theLocale);

        /* Set range locale */
        theSelectedRange.setText(theFormatter.formatDateDayRange(theRangeSelect.getRange()));

        /* Set format options */
        JDateDayConfig myConfig = theEditor.getDateConfig();
        myConfig.setFormatOptions(theMaxDayLen, doShrinkFromRight, doPretty);
        myConfig = theStartDate.getDateConfig();
        myConfig.setFormatOptions(theMaxDayLen, doShrinkFromRight, doPretty);
        myConfig = theEndDate.getDateConfig();
        myConfig.setFormatOptions(theMaxDayLen, doShrinkFromRight, doPretty);
        myConfig = theRangeSelect.getDateConfig();
        myConfig.setFormatOptions(theMaxDayLen, doShrinkFromRight, doPretty);

        /* Note that we should redraw the table */
        theTable.repaint();
    }

    /**
     * Apply format to underlying objects.
     */
    private void applyFormat() {
        /* Set format for formatter */
        theFormatter.setFormat(theFormat);

        /* Set range format */
        theSelectedRange.setText(theFormatter.formatDateDayRange(theRangeSelect.getRange()));

        /* Note that we should redraw the table */
        theTable.repaint();
    }

    /**
     * Apply Range to underlying objects.
     */
    private void applyRange() {
        /* Access the Start/End Dates */
        JDateDay myStart = theStartDate.getSelectedDateDay();
        JDateDay myEnd = theEndDate.getSelectedDateDay();

        /* Set the select-able range for the start/end buttons */
        theStartDate.setLatestDateDay(myEnd);
        theEndDate.setEarliestDateDay(myStart);

        /* Set the Editor range */
        theEditor.setEarliestDateDay(myStart);
        theEditor.setLatestDateDay(myEnd);

        /* set the range select range */
        theRangeSelect.setOverallRange(new JDateDayRange(myStart, myEnd));
    }

    /**
     * Listener class.
     */
    private class DateListener
            implements PropertyChangeListener, ItemListener {
        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            /* Access source object */
            Object o = evt.getSource();

            /* If this is the start/end date */
            if ((theStartDate.equals(o))
                || (theEndDate.equals(o))) {
                /* Apply the new range */
                applyRange();

                /* If this is the selectable range */
            } else if (theRangeSelect.equals(o)) {
                /* Apply the new range */
                JDateDayRange myRange = theRangeSelect.getRange();
                if (theSelectedRange != null) {
                    theSelectedRange.setText(theFormatter.formatDateDayRange(myRange));
                }
            }
        }

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
                theMaxDayLen = myLocale.getMaxDayLen();
                doShrinkFromRight = myLocale.doShrinkFromRight();

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
     * Some useful locales.
     */
    private enum ShortLocale {
        /**
         * China (shorten day names to one character, and shrink from the right to make sure they are different).
         */
        CHINA(Locale.CHINA, 1, false),

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
        JAPAN(Locale.JAPAN, 1),

        /**
         * Korea (shorten day names to one character).
         */
        KOREA(Locale.KOREA, 1),

        /**
         * US.
         */
        US(Locale.US),

        /**
         * UK (shorten day names to two characters).
         */
        UK(Locale.UK, 2);

        /**
         * Locale property.
         */
        private final Locale theLocale;

        /**
         * Maximum day length.
         */
        private final int theMaxDayLen;

        /**
         * Shrink from right.
         */
        private final boolean doShrinkFromRight;

        /**
         * Obtain locale value.
         * @return the locale
         */
        public Locale getLocale() {
            return theLocale;
        }

        /**
         * Obtain maximum day length.
         * @return the maximum day length
         */
        public int getMaxDayLen() {
            return theMaxDayLen;
        }

        /**
         * Shrink names from right.
         * @return true/false
         */
        public boolean doShrinkFromRight() {
            return doShrinkFromRight;
        }

        /**
         * Constructor.
         * @param pLocale the locale
         */
        private ShortLocale(final Locale pLocale) {
            /* Store the Locale */
            theLocale = pLocale;
            theMaxDayLen = JDateConfig.MAX_DAY_NAME_LEN;
            doShrinkFromRight = true;
        }

        /**
         * Constructor.
         * @param pLocale the locale
         * @param iMaxDayLen the maximum day length
         */
        private ShortLocale(final Locale pLocale,
                            final int iMaxDayLen) {
            /* Store the Locale */
            theLocale = pLocale;
            theMaxDayLen = iMaxDayLen;
            doShrinkFromRight = true;
        }

        /**
         * Constructor.
         * @param pLocale the locale
         * @param iMaxDayLen the maximum day length
         * @param bShrinkFromRight shrink day names from right
         */
        private ShortLocale(final Locale pLocale,
                            final int iMaxDayLen,
                            final boolean bShrinkFromRight) {
            /* Store the Locale */
            theLocale = pLocale;
            theMaxDayLen = iMaxDayLen;
            doShrinkFromRight = bShrinkFromRight;
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
