/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.test.ui.swing;

import java.awt.HeadlessException;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingCheckBox;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingLabel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollContextMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;

/**
 * <p>
 * Provides a simple application that illustrates the features of JDateDay.
 * @author Tony Washer
 */
public class TethysSwingDateExample {
    /**
     * Inset depth.
     */
    private static final int INSET_DEPTH = 5;

    /**
     * Grid Gap.
     */
    private static final int GRID_GAP = 5;

    /**
     * First Column Width.
     */
    private static final int COL_1_WIDTH = 100;

    /**
     * Second Column Width.
     */
    private static final int COL_2_WIDTH = 250;

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
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingDateExample.class);

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
    private final TethysSwingGuiFactory theGuiFactory = new TethysSwingGuiFactory();

    /**
     * The table.
     */
    private TethysSwingTableManager<String, DateItem> theTable;

    /**
     * The list of locales.
     */
    private TethysSwingScrollButtonManager<ShortLocale> theLocaleList;

    /**
     * The list of formats.
     */
    private TethysSwingScrollButtonManager<String> theFormatList;

    /**
     * The start date.
     */
    private TethysSwingDateButtonManager theStartDate;

    /**
     * The end date.
     */
    private TethysSwingDateButtonManager theEndDate;

    /**
     * The Null Select checkBox.
     */
    private TethysSwingCheckBox theNullSelect;

    /**
     * The ShowNarrow checkBox.
     */
    private TethysSwingCheckBox theNarrowSelect;

    /**
     * The selected range.
     */
    private TethysSwingLabel theSelectedRange;

    /**
     * The selected locale.
     */
    private Locale theLocale = Locale.UK;

    /**
     * The selected format.
     */
    private String theFormat = DATEFORMAT_1;

    /**
     * The range selection.
     */
    private TethysSwingDateRangeSelector theRangeSelect;

    /**
     * The formatter.
     */
    private final TethysDataFormatter theFormatter = theGuiFactory.getDataFormatter();

    /**
     * The formatter.
     */
    private final TethysDateFormatter theDateFormatter = theFormatter.getDateFormatter();

    /**
     * Create and show the GUI.
     */
    static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("TethysDate Swing Demo");

            /* Create the Example program */
            final TethysSwingDateExample myProgram = new TethysSwingDateExample();

            /* Create the panel */
            final TethysSwingGridPaneManager myPanel = myProgram.makePanel();
            final JComponent myNode = TethysSwingNode.getComponent(myPanel);

            /* Attach the panel to the frame */
            myNode.setOpaque(true);
            myFrame.setContentPane(myNode);
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
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    /**
     * Create the panel.
     * @return the panel
     */
    private TethysSwingGridPaneManager makePanel() {
        /* Create the table */
        makeTable();

        /* Create the range buttons */
        makeRangeButtons();

        /* Create the locale list */
        makeLocaleList();

        /* Create the format list */
        makeFormatList();

        /* Create the additional labels */
        final TethysSwingLabel myFormat = theGuiFactory.newLabel("Format:");
        final TethysSwingLabel myLocale = theGuiFactory.newLabel("Locale:");
        final TethysSwingLabel myStart = theGuiFactory.newLabel("Start:");
        final TethysSwingLabel myEnd = theGuiFactory.newLabel("End:");
        final TethysSwingLabel mySelRange = theGuiFactory.newLabel("SelectedRange:");

        /* Create a Range sub-panel */
        final TethysSwingGridPaneManager myRange = theGuiFactory.newGridPane();
        myRange.setHGap(GRID_GAP);
        myRange.setVGap(GRID_GAP);
        myRange.setBorderPadding(INSET_DEPTH);
        myRange.setBorderTitle("Range Selection");

        /* Position the contents */
        myRange.addCell(myStart);
        myRange.setCellAlignment(myStart, TethysAlignment.EAST);
        myRange.addCell(theStartDate);
        myRange.allowCellGrowth(theStartDate);
        myRange.newRow();
        myRange.addCell(myEnd);
        myRange.setCellAlignment(myEnd, TethysAlignment.EAST);
        myRange.addCell(theEndDate);
        myRange.allowCellGrowth(theEndDate);

        /* Create a Style sub-panel */
        final TethysSwingGridPaneManager myStyle = theGuiFactory.newGridPane();
        myStyle.setHGap(GRID_GAP);
        myStyle.setVGap(GRID_GAP);
        myStyle.setBorderPadding(INSET_DEPTH);
        myStyle.setBorderTitle("Format Selection");

        /* Position the contents */
        myStyle.addCell(myLocale);
        myStyle.setCellAlignment(myLocale, TethysAlignment.EAST);
        myStyle.addCell(theLocaleList);
        myStyle.allowCellGrowth(theLocaleList);
        myStyle.newRow();
        myStyle.addCell(myFormat);
        myStyle.setCellAlignment(myFormat, TethysAlignment.EAST);
        myStyle.addCell(theFormatList);
        myStyle.allowCellGrowth(theFormatList);

        /* Create the options panel */
        final TethysSwingBoxPaneManager myOptions = makeOptionsPanel();

        /* Create the panel */
        final TethysSwingGridPaneManager myPanel = theGuiFactory.newGridPane();
        myPanel.setHGap(GRID_GAP);
        myPanel.setVGap(GRID_GAP);
        myPanel.setBorderPadding(INSET_DEPTH);

        /* Set the contents */
        myPanel.addCell(myRange);
        myPanel.addCell(myStyle);
        myPanel.allowCellGrowth(myStyle);
        myPanel.newRow();
        myPanel.addCell(myOptions, 2);
        myPanel.setCellAlignment(myOptions, TethysAlignment.CENTRE);
        myPanel.allowCellGrowth(myOptions);
        myPanel.newRow();
        myPanel.addCell(theTable, 2);
        myPanel.setCellAlignment(theTable, TethysAlignment.CENTRE);
        myPanel.allowCellGrowth(theTable);
        myPanel.newRow();
        theRangeSelect.setBorderTitle("Explicit Range Selection");
        myPanel.addCell(theRangeSelect, 2);
        myPanel.setCellAlignment(theRangeSelect, TethysAlignment.CENTRE);
        myPanel.allowCellGrowth(theRangeSelect);
        myPanel.newRow();
        myPanel.addCell(mySelRange);
        myPanel.addCell(theSelectedRange);
        myPanel.allowCellGrowth(theSelectedRange);

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
        final List<DateItem> myList = new ArrayList<>();
        myList.add(new DateItem(DATE_FIRST, "First Entry"));
        myList.add(new DateItem(DATE_SECOND, "Second Entry"));
        myList.add(new DateItem(DATE_THIRD, "Third Entry"));
        myList.add(new DateItem(DATE_FOURTH, "Fourth Entry"));
        myList.add(new DateItem(DATE_FIFTH, "Fifth Entry"));

        /* Declare the list to the table */
        theTable.setItems(myList);

        /* Create the date column */
        final TethysSwingTableDateColumn<String, DateItem> myDateColumn = theTable.declareDateColumn(DateItem.PROP_DATE);
        myDateColumn.setCellValueFactory(DateItem::getDate);
        myDateColumn.setOnCommit((p, v) -> p.setDate(v));
        myDateColumn.setColumnWidth(COL_1_WIDTH);
        myDateColumn.setDateConfigurator((r, c) -> {
            c.setEarliestDate(theStartDate.getSelectedDate());
            c.setLatestDate(theEndDate.getSelectedDate());
            c.setShowNarrowDays(theNarrowSelect.isSelected());
        });

        /* Create the comments column */
        final TethysSwingTableStringColumn<String, DateItem> myCommentsColumn = theTable.declareStringColumn(DateItem.PROP_COMMENTS);
        myCommentsColumn.setCellValueFactory(DateItem::getComments);
        myCommentsColumn.setOnCommit((p, v) -> p.setComments(v));
        myCommentsColumn.setColumnWidth(COL_2_WIDTH);
    }

    /**
     * Create the list of available locales.
     */
    private void makeLocaleList() {
        /* Create the Combo box and populate it */
        theLocaleList = theGuiFactory.newScrollButton();
        final TethysSwingScrollContextMenu<ShortLocale> myMenu = theLocaleList.getMenu();
        for (final ShortLocale myLocale : ShortLocale.values()) {
            /* Add the Locale to the list */
            myMenu.addItem(myLocale);
        }

        /* Set the default item */
        theLocaleList.setValue(ShortLocale.UK);

        /* Action selections */
        theLocaleList.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            /* Store the new locale */
            final ShortLocale myLocale = e.getDetails(ShortLocale.class);
            theLocale = myLocale.getLocale();
            applyLocale();
            theTable.repaintColumn(DateItem.PROP_DATE);
            theNarrowSelect.setSelected(myLocale.showNarrowDays());
        });
    }

    /**
     * Create the list of available formats.
     */
    private void makeFormatList() {
        /* Create the Combo box and populate it */
        theFormatList = theGuiFactory.newScrollButton();
        final TethysSwingScrollContextMenu<String> myMenu = theFormatList.getMenu();
        myMenu.addItem(DATEFORMAT_1);
        myMenu.addItem(DATEFORMAT_2);
        myMenu.addItem(DATEFORMAT_3);

        /* Set the default item */
        theFormatList.setValue(theFormat);

        /* Action selections */
        theFormatList.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            /* Store the new format */
            theFormat = e.getDetails(String.class);
            applyFormat();
            theTable.repaintColumn(DateItem.PROP_DATE);
        });
    }

    /**
     * Create the options panel.
     * @return the panel
     */
    private TethysSwingBoxPaneManager makeOptionsPanel() {
        /* Create the checkBoxes */
        makeCheckBoxes();

        /* Create an options sub-panel */
        final TethysSwingBoxPaneManager myOptions = theGuiFactory.newHBoxPane();
        myOptions.addSpacer();
        myOptions.addNode(theNullSelect);
        myOptions.addSpacer();
        myOptions.addNode(theNarrowSelect);
        myOptions.addSpacer();
        myOptions.setBorderTitle("Options");

        /* Return the panel */
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
        final TethysDate myStart = DATE_START;
        final TethysDate myEnd = DATE_END;

        /* Set the values */
        theStartDate.setSelectedDate(myStart);
        theEndDate.setSelectedDate(myEnd);

        /* Apply the range */
        applyRange();

        /* Add Listener to buttons */
        theStartDate.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> applyRange());
        theEndDate.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> applyRange());

        /* Create the range report label */
        theSelectedRange = theGuiFactory.newLabel(theRangeSelect.getRange().toString());
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
        theNullSelect = theGuiFactory.newCheckBox("Null Date Select");
        theNarrowSelect = theGuiFactory.newCheckBox("Show Narrow Days");

        /* Action selections */
        theNullSelect.getEventRegistrar().addEventListener(e -> applyNullOption());
        theNarrowSelect.getEventRegistrar().addEventListener(e -> applyNarrowOption());

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
        theSelectedRange.setText(theDateFormatter.formatDateRange(theRangeSelect.getRange()));

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
        theSelectedRange.setText(theDateFormatter.formatDateRange(theRangeSelect.getRange()));

        /* Note that we should redraw the table */
        theTable.repaintColumn(DateItem.PROP_COMMENTS);
    }

    /**
     * Apply Range to underlying objects.
     */
    private void applyRange() {
        /* Access the Start/End Dates */
        final TethysDate myStart = theStartDate.getSelectedDate();
        final TethysDate myEnd = theEndDate.getSelectedDate();

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
        final TethysDateRange myRange = theRangeSelect.getRange();
        if (theSelectedRange != null) {
            theSelectedRange.setText(theDateFormatter.formatDateRange(myRange));
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
        DateItem(final TethysDate pDate,
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
        CHINA(Locale.CHINA, true),

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
        JAPAN(Locale.JAPAN, true),

        /**
         * Korea (shorten day names to one character).
         */
        KOREA(Locale.KOREA, true),

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
         * Show narrow days.
         */
        private final boolean doShowNarrowDays;

        /**
         * Constructor.
         * @param pLocale the locale
         */
        ShortLocale(final Locale pLocale) {
            /* Store the Locale */
            this(pLocale, false);
        }

        /**
         * Constructor.
         * @param pLocale the locale
         * @param pShowNarrowDays true/false
         */
        ShortLocale(final Locale pLocale,
                    final boolean pShowNarrowDays) {
            /* Store the Locale */
            theLocale = pLocale;
            doShowNarrowDays = pShowNarrowDays;
        }

        /**
         * Obtain locale value.
         * @return the locale
         */
        public Locale getLocale() {
            return theLocale;
        }

        /**
         * Show narrow days.
         * @return true/false
         */
        public boolean showNarrowDays() {
            return doShowNarrowDays;
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
