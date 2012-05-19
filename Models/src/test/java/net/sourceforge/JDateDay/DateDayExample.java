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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Locale;

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

/**
 * <p>
 * Provides a simple application that illustrates the features of JDateButton
 * 
 * @author Tony Washer
 */
public class DateDayExample extends JApplet {
    /* Serial Id */
    private static final long serialVersionUID = 2036674133513416250L;

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
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
    }

    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("DateDayButton Demo");

            /* Create the Example program */
            DateDayExample myProgram = new DateDayExample();

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
        } catch (Exception e) {
            System.out.println("Help");
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /* Members */
    private JTable theTable = null;
    private DateDayCellRenderer theRenderer = null;
    private DateDayCellEditor theEditor = null;
    private JComboBox theLocaleList = null;
    private JComboBox theFormatList = null;
    private DateDayButton theStartDate = null;
    private DateDayButton theEndDate = null;
    private DateDayRangeSelect theRangeSelect = null;
    private Locale theLocale = Locale.UK;
    private int theMaxDayLen = 3;
    private boolean doShrinkFromRight = true;
    private boolean doPretty = true;
    private String theFormat = "dd-MMM-yyyy";
    private DateListener theListener = new DateListener();

    /**
     * Create the panel
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

        /* Create a Range sub-panel */
        JPanel myRange = new JPanel(new GridBagLayout());
        GridBagConstraints myConstraints = new GridBagConstraints();
        myRange.setBorder(javax.swing.BorderFactory.createTitledBorder("Range Selection"));
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myConstraints.insets = new Insets(5, 5, 5, 5);
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
        myConstraints.insets = new Insets(5, 5, 5, 5);
        myRange.add(myEnd, myConstraints);
        myConstraints.gridx = 1;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 1.0;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myRange.add(theEndDate, myConstraints);

        /* Create a Style sub-panel */
        JPanel myStyle = new JPanel(new GridBagLayout());
        myStyle.setBorder(javax.swing.BorderFactory.createTitledBorder("Format Selection"));
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.0;
        myConstraints.anchor = GridBagConstraints.LINE_END;
        myConstraints.insets = new Insets(5, 5, 5, 5);
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
        myConstraints.insets = new Insets(5, 5, 5, 5);
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
        myOptions.add(theRangeSelect, myConstraints);

        /* Create the panel */
        JPanel myPanel = new JPanel(new GridBagLayout());
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.5;
        myPanel.add(myRange, myConstraints);
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 1;
        myConstraints.gridy = 0;
        myConstraints.gridwidth = 1;
        myConstraints.weightx = 0.5;
        myPanel.add(myStyle, myConstraints);

        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 1;
        myConstraints.gridwidth = 2;
        myConstraints.weightx = 0.5;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myPanel.add(myOptions, myConstraints);

        theTable.setPreferredScrollableViewportSize(new Dimension(400, 90));
        myConstraints = new GridBagConstraints();
        myConstraints.gridx = 0;
        myConstraints.gridy = 2;
        myConstraints.gridwidth = 2;
        myConstraints.gridheight = GridBagConstraints.REMAINDER;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myPanel.add(new JScrollPane(theTable), myConstraints);
        theTable.setPreferredScrollableViewportSize(new Dimension(400, 90));

        /* Return the panel */
        return myPanel;
    }

    /**
     * Table Model
     */
    private class DateTable extends AbstractTableModel {
        /* Serial Id */
        private static final long serialVersionUID = -4842107163051671807L;

        /* Create the data for a table */
        String[] myColumns = { "Date", "Description" };
        Object[][] myData = { { makeDate(2011, Calendar.JULY, 1), "First Entry" },
                { makeDate(2012, Calendar.MARCH, 14), "Second Entry" },
                { makeDate(2012, Calendar.NOVEMBER, 19), "Third Entry" },
                { makeDate(2013, Calendar.MAY, 31), "Fourth Entry" },
                { makeDate(2014, Calendar.FEBRUARY, 28), "Fifth Entry" } };

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return 5;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return myColumns[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
                return DateDay.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex,
                                      int columnIndex) {
            return (columnIndex == 0);
        }

        @Override
        public Object getValueAt(int rowIndex,
                                 int columnIndex) {
            return myData[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(Object pValue,
                               int rowIndex,
                               int columnIndex) {
            myData[rowIndex][columnIndex] = pValue;
        }
    }

    /**
     * Create the Table
     */
    private void makeTable() {
        /* Create the table */
        TableModel myModel = new DateTable();
        theTable = new JTable(myModel);

        /* Create the renderer and editor */
        theRenderer = new DateDayCellRenderer();
        theEditor = new DateDayCellEditor();

        /* Set sorting on the table */
        theTable.setAutoCreateRowSorter(true);

        /* Format the first column */
        TableColumnModel myColModel = theTable.getColumnModel();

        /* Set the width of the column */
        TableColumn myFirstCol = myColModel.getColumn(0);
        myFirstCol.setPreferredWidth(100);

        /* Set the renderer */
        myFirstCol.setCellRenderer(theRenderer);
        myFirstCol.setCellEditor(theEditor);

        /* Set the width of the column */
        TableColumn mySecondCol = myColModel.getColumn(1);
        mySecondCol.setPreferredWidth(200);
    }

    /**
     * Create the list of available locales
     */
    private void makeLocaleList() {
        /* Create the Combo box and populate it */
        theLocaleList = new JComboBox();
        for (shortLocale myLocale : shortLocale.values()) {
            /* Add the Locale to the list */
            theLocaleList.addItem(myLocale);
        }

        /* Add Listener to List */
        theLocaleList.addItemListener(theListener);

        /* Set the default item */
        theLocaleList.setSelectedItem(shortLocale.UnitedKingdom);
    }

    /**
     * Create the list of available formats
     */
    private void makeFormatList() {
        /* Create the Combo box and populate it */
        theFormatList = new JComboBox();
        theFormatList.addItem("dd-MMM-yyyy");
        theFormatList.addItem("dd/MMM/yy");
        theFormatList.addItem("yyyy/MMM/dd");

        /* Add Listener to List */
        theFormatList.addItemListener(theListener);

        /* Set the default item */
        theFormatList.setSelectedItem(theFormat);
    }

    /**
     * Create the range buttons
     */
    private void makeRangeButtons() {
        /* Create the buttons */
        theStartDate = new DateDayButton();
        theEndDate = new DateDayButton();

        /* Create the range selection */
        theRangeSelect = new DateDayRangeSelect();
        theRangeSelect.addPropertyChangeListener(DateDayRangeSelect.propertyRANGE, theListener);

        /* Initialise the values */
        DateDay myStart = makeDate(2007, Calendar.JANUARY, 25);
        DateDay myEnd = makeDate(2014, Calendar.AUGUST, 9);

        /* Set the values */
        theStartDate.setSelectedDateDay(myStart);
        theEndDate.setSelectedDateDay(myEnd);

        /* Apply the range */
        applyRange();

        /* Add Listener to buttons */
        theStartDate.addPropertyChangeListener(DateDayButton.propertyDATE, theListener);
        theEndDate.addPropertyChangeListener(DateDayButton.propertyDATE, theListener);
    }

    /**
     * Convenience method to create a date from Year, Month, Day
     * @param pYear the year
     * @param pMonth the month,
     * @param pDay
     * @return the requested date
     */
    private static DateDay makeDate(int pYear,
                                    int pMonth,
                                    int pDay) {
        DateDay myDate = new DateDay(pYear, pMonth, pDay);
        return myDate;
    }

    /**
     * Apply Options to underlying objects
     */
    private void applyOptions() {
        /* Set Null Date options */
        DateDayConfig myConfig = theEditor.getDateConfig();
        myConfig.setAllowNullDateSelection(false);
        myConfig = theStartDate.getDateConfig();
        myConfig.setAllowNullDateSelection(true);
        myConfig = theEndDate.getDateConfig();
        myConfig.setAllowNullDateSelection(true);
    }

    /**
     * Apply Locale to underlying objects
     */
    private void applyLocale() {
        /* Set the Renderer and Editor Locale */
        theRenderer.setLocale(theLocale);
        theEditor.setLocale(theLocale);

        /* Set the Start/End Date button locale */
        theStartDate.setLocale(theLocale);
        theEndDate.setLocale(theLocale);

        /* Set range locale */
        theRangeSelect.setLocale(theLocale);

        /* Set format options */
        DateDayConfig myConfig = theEditor.getDateConfig();
        myConfig.setFormatOptions(theMaxDayLen, doShrinkFromRight, doPretty);
        myConfig = theStartDate.getDateConfig();
        myConfig.setFormatOptions(theMaxDayLen, doShrinkFromRight, doPretty);
        myConfig = theEndDate.getDateConfig();
        myConfig.setFormatOptions(theMaxDayLen, doShrinkFromRight, doPretty);

        /* Note that we should redraw the table */
        theTable.repaint();
    }

    /**
     * Apply format to underlying objects
     */
    private void applyFormat() {
        /* Set the Renderer and Editor Format */
        theRenderer.setFormat(theFormat);
        theEditor.setFormat(theFormat);

        /* Set the Start/End Date button format */
        theStartDate.setFormat(theFormat);
        theEndDate.setFormat(theFormat);

        /* Set range format */
        theRangeSelect.setFormat(theFormat);

        /* Note that we should redraw the table */
        theTable.repaint();
    }

    /**
     * Apply Range to underlying objects
     */
    private void applyRange() {
        /* Access the Start/End Dates */
        DateDay myStart = theStartDate.getSelectedDateDay();
        DateDay myEnd = theEndDate.getSelectedDateDay();

        /* Set the select-able range for the start/end buttons */
        theStartDate.setLatestDateDay(myEnd);
        theEndDate.setEarliestDateDay(myStart);

        /* Set the Editor range */
        theEditor.setEarliestDateDay(myStart);
        theEditor.setLatestDateDay(myEnd);

        /* set the range select range */
        theRangeSelect.setOverallRange(new DateDayRange(myStart, myEnd));
    }

    /**
     * Listener class
     */
    private class DateListener implements PropertyChangeListener, ItemListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            /* Access source object */
            Object o = evt.getSource();

            /* If this is the start/end date */
            if ((o == theStartDate) || (o == theEndDate)) {
                /* Apply the new range */
                applyRange();
            }

            /* If this is the selectable range */
            if (o == theRangeSelect) {
                /* Apply the new range */
                DateDayRange myRange = theRangeSelect.getRange();
                System.out.println(myRange);
            }
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            /* Access source object */
            Object o = evt.getSource();

            /* Ignore if we are not selecting */
            if (evt.getStateChange() != ItemEvent.SELECTED)
                return;

            /* If this is the Locale list */
            if (o == theLocaleList) {
                /* Store the new locale */
                shortLocale myLocale = (shortLocale) evt.getItem();
                theLocale = myLocale.getLocale();
                theMaxDayLen = myLocale.getMaxDayLen();
                doShrinkFromRight = myLocale.doShrinkFromRight();

                /* Apply the new locale */
                applyLocale();
            }

            /* If this is the Format list */
            else if (o == theFormatList) {
                /* Store the new format */
                theFormat = (String) evt.getItem();

                /* Apply the new format */
                applyFormat();
            }
        }
    }

    /**
     * Some useful locales
     */
    private enum shortLocale {
        /**
         * China (shorten day names to one character, and shrink from the right to make sure they are
         * different)
         */
        China(Locale.CHINA, 1, false),

        /**
         * Germany
         */
        Germany(Locale.GERMANY),

        /**
         * France
         */
        France(Locale.FRANCE),

        /**
         * Italy
         */
        Italy(Locale.ITALY),

        /**
         * Japan (shorten day names to one character)
         */
        Japan(Locale.JAPAN, 1),

        /**
         * Korea (shorten day names to one character)
         */
        Korea(Locale.KOREA, 1),

        /**
         * US
         */
        UnitedStates(Locale.US),

        /**
         * UK (shorten day names to two characters)
         */
        UnitedKingdom(Locale.UK, 2);

        /**
         * Locale property
         */
        private final Locale theLocale;

        /**
         * Maximum day length
         */
        private final int theMaxDayLen;

        /**
         * Shrink from right
         */
        private final boolean doShrinkFromRight;

        /**
         * Obtain locale value
         * @return the locale
         */
        public Locale getLocale() {
            return theLocale;
        }

        /**
         * Obtain maximum day length
         * @return the maximum day length
         */
        public int getMaxDayLen() {
            return theMaxDayLen;
        }

        /**
         * Shrink names from right
         * @return true/false
         */
        public boolean doShrinkFromRight() {
            return doShrinkFromRight;
        }

        /**
         * Constructor
         * @param pLocale the locale
         */
        private shortLocale(Locale pLocale) {
            /* Store the Locale */
            theLocale = pLocale;
            theMaxDayLen = 3;
            doShrinkFromRight = true;
        }

        /**
         * Constructor
         * @param pLocale the locale
         * @param iMaxDayLen the maximum day length
         */
        private shortLocale(Locale pLocale, int iMaxDayLen) {
            /* Store the Locale */
            theLocale = pLocale;
            theMaxDayLen = iMaxDayLen;
            doShrinkFromRight = true;
        }

        /**
         * Constructor
         * @param pLocale the locale
         * @param iMaxDayLen the maximum day length
         * @param bShrinkFromRight shrink day names from right
         */
        private shortLocale(Locale pLocale, int iMaxDayLen, boolean bShrinkFromRight) {
            /* Store the Locale */
            theLocale = pLocale;
            theMaxDayLen = iMaxDayLen;
            doShrinkFromRight = bShrinkFromRight;
        }
    }
}
