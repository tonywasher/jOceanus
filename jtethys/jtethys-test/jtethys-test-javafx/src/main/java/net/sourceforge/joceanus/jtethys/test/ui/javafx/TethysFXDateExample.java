/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-javafx/src/test/java/net/sourceforge/joceanus/jtethys/ui/javafx/TethysFXDateExample.java $
 * $Revision: 842 $
 * $Author: Tony $
 * $Date: 2017-09-10 17:23:42 +0100 (Sun, 10 Sep 2017) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.TethysLogConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXCheckBox;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXLabel;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p>
 * Provides a simple application that illustrates the features of JDateDay.
 * @author Tony Washer
 */
public final class TethysFXDateExample
        extends Application {
    /**
     * Inset depth.
     */
    private static final int INSET_DEPTH = 5;

    /**
     * Grid Gap.
     */
    private static final int GRID_GAP = 5;

    /**
     * Date Column Width.
     */
    private static final int COL_DATE_WIDTH = 100;

    /**
     * Comment Column Width.
     */
    private static final int COL_COMMENT_WIDTH = 250;

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
     * The GUI Factory.
     */
    private final TethysFXGuiFactory theGuiFactory = new TethysFXGuiFactory();

    /**
     * The table.
     */
    private TethysFXTableManager<String, DateItem> theTable;

    /**
     * The list of locales.
     */
    private TethysFXScrollButtonManager<ShortLocale> theLocaleList;

    /**
     * The list of formats.
     */
    private TethysFXScrollButtonManager<String> theFormatList;

    /**
     * The start date.
     */
    private TethysFXDateButtonManager theStartDate;

    /**
     * The end date.
     */
    private TethysFXDateButtonManager theEndDate;

    /**
     * The Null Select checkBox.
     */
    private TethysFXCheckBox theNullSelect;

    /**
     * The ShowNarrow checkBox.
     */
    private TethysFXCheckBox theNarrowSelect;

    /**
     * The selected locale.
     */
    private final SimpleObjectProperty<Locale> theLocale = new SimpleObjectProperty<Locale>(Locale.UK);

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
    private final SimpleStringProperty theFormat = new SimpleStringProperty(DATEFORMAT_1);

    /**
     * The NullDate Option.
     */
    private final SimpleBooleanProperty allowNullDate = new SimpleBooleanProperty(false);

    /**
     * The NarrowDays Option.
     */
    private final SimpleBooleanProperty showNarrowDays = new SimpleBooleanProperty(false);

    /**
     * The formatter.
     */
    private final TethysDataFormatter theFormatter = theGuiFactory.getDataFormatter();

    /**
     * The formatter.
     */
    private final TethysDateFormatter theDateFormatter = theFormatter.getDateFormatter();

    /**
     * The range selection.
     */
    private final TethysFXDateRangeSelector theRangeSelect = theGuiFactory.newDateRangeSelector(true);

    /**
     * The selected range.
     */
    private final TethysFXLabel theSelectedRange = theGuiFactory.newLabel();

    @Override
    public void start(final Stage pStage) {
        final TethysFXGridPaneManager myGrid = makePanel();
        final Scene myScene = new Scene(myGrid.getNode());
        theGuiFactory.registerScene(myScene);
        pStage.setTitle("TethysDate JavaFX Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    /**
     * Create the panel.
     * @return the panel
     */
    private TethysFXGridPaneManager makePanel() {
        /* Configure log4j */
        TethysLogConfig.configureLog4j();

        /* Create the table */
        theTable = theGuiFactory.newTable();
        makeTable();

        /* Create the range panel */
        final TethysFXGridPaneManager myRange = makeRangePanel();

        /* Create the style panel */
        final TethysFXGridPaneManager myStyle = makeStylePanel();

        /* Create the options panel */
        final TethysFXBoxPaneManager myOptions = makeOptionsPanel();

        /* Create the panel */
        final TethysFXGridPaneManager myPanel = theGuiFactory.newGridPane();
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
        final TethysFXLabel mySelRange = theGuiFactory.newLabel("SelectedRange:");
        myPanel.addCell(mySelRange);
        myPanel.addCell(theSelectedRange);
        myPanel.allowCellGrowth(theSelectedRange);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Create the range panel.
     * @return the panel
     */
    private TethysFXGridPaneManager makeRangePanel() {
        /* Create the range buttons */
        makeRangeButtons();

        /* Create the additional labels */
        final TethysFXLabel myStart = theGuiFactory.newLabel("Start:");
        final TethysFXLabel myEnd = theGuiFactory.newLabel("End:");

        /* Create a Range sub-panel */
        final TethysFXGridPaneManager myRange = theGuiFactory.newGridPane();
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

        /* Return the panel */
        return myRange;
    }

    /**
     * Create the range panel.
     * @return the panel
     */
    private TethysFXGridPaneManager makeStylePanel() {
        /* Create the locale list */
        makeLocaleList();

        /* Create the format list */
        makeFormatList();

        /* Create the additional labels */
        final TethysFXLabel myFormat = theGuiFactory.newLabel("Format:");
        final TethysFXLabel myLocale = theGuiFactory.newLabel("Locale:");

        /* Create a Style sub-panel */
        final TethysFXGridPaneManager myStyle = theGuiFactory.newGridPane();
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

        /* Return the panel */
        return myStyle;
    }

    /**
     * Create the options panel.
     * @return the panel
     */
    private TethysFXBoxPaneManager makeOptionsPanel() {
        /* Create the checkBox */
        makeCheckBox();

        /* Create an options sub-panel */
        final TethysFXBoxPaneManager myOptions = theGuiFactory.newHBoxPane();
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
     * Create the list of available locales.
     */
    private void makeLocaleList() {
        /* Create the Combo box and populate it */
        theLocaleList = theGuiFactory.newScrollButton();
        final TethysFXScrollContextMenu<ShortLocale> myMenu = theLocaleList.getMenu();
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
            theLocale.setValue(myLocale.getLocale());
            showNarrowDays.setValue(myLocale.showNarrowDays());
            theTable.repaintColumn(DateItem.PROP_DATE);
        });
    }

    /**
     * Create the list of available formats.
     */
    private void makeFormatList() {
        /* Create the Combo box and populate it */
        theFormatList = theGuiFactory.newScrollButton();
        final TethysFXScrollContextMenu<String> myMenu = theFormatList.getMenu();
        myMenu.addItem(DATEFORMAT_1);
        myMenu.addItem(DATEFORMAT_2);
        myMenu.addItem(DATEFORMAT_3);

        /* Set the default item */
        theFormatList.setValue(theFormat.getValue());

        /* Action selections */
        theFormatList.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            /* Store the new format */
            theFormat.setValue(e.getDetails(String.class));
            theTable.repaintColumn(DateItem.PROP_DATE);
        });
    }

    /**
     * Create the range buttons.
     */
    private void makeRangeButtons() {
        /* Create the buttons */
        theStartDate = theGuiFactory.newDateButton();
        theEndDate = theGuiFactory.newDateButton();

        /* Initialise the values */
        final TethysDate myStart = DATE_START;
        final TethysDate myEnd = DATE_END;
        final TethysDateRange myRange = new TethysDateRange(myStart, myEnd);

        /* Set the values */
        theStartDate.setSelectedDate(myStart);
        theEndDate.setSelectedDate(myEnd);

        /* Set the range */
        theRangeSelect.setOverallRange(myRange);
        theSelectedRange.setText(theDateFormatter.formatDateRange(theRangeSelect.getRange()));

        /* Set the listeners */
        setListeners();
    }

    /**
     * Set Listeners.
     */
    private void setListeners() {
        /* Handle changes to locale */
        theLocale.addListener((v, o, n) -> {
            /* Set locale for formatter */
            theFormatter.setLocale(n);
            theRangeSelect.setLocale(n);
            theSelectedRange.setText(theDateFormatter.formatDateRange(theRangeSelect.getRange()));
        });

        /* Handle changes to format */
        theFormat.addListener((v, o, n) -> {
            theFormatter.setFormat(n);
            theSelectedRange.setText(theDateFormatter.formatDateRange(theRangeSelect.getRange()));
        });

        /* Handle changes to allow nullDate */
        allowNullDate.addListener((v, o, n) -> {
            theStartDate.setAllowNullDateSelection(n);
            theEndDate.setAllowNullDateSelection(n);
        });

        /* Handle changes to allow narrowDays */
        showNarrowDays.addListener((v, o, n) -> {
            theStartDate.setShowNarrowDays(n);
            theEndDate.setShowNarrowDays(n);
        });

        /* Handle changes to startDate */
        theStartDate.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            final TethysDate myDate = theStartDate.getSelectedDate();
            theEndDate.setEarliestDate(myDate);
            theRangeSelect.setOverallRange(new TethysDateRange(myDate, theEndDate.getSelectedDate()));
        });

        /* Handle changes to endDate */
        theEndDate.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            final TethysDate myDate = theEndDate.getSelectedDate();
            theEndDate.setLatestDate(myDate);
            theRangeSelect.setOverallRange(new TethysDateRange(theStartDate.getEarliestDate(), myDate));
        });

        /* Handle changes to range */
        theRangeSelect.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            final TethysDateRange myRange = theRangeSelect.getRange();
            theSelectedRange.setText(theDateFormatter.formatDateRange(myRange));
        });
    }

    /**
     * Create the checkBox.
     */
    private void makeCheckBox() {
        /* Create the check boxes */
        theNullSelect = theGuiFactory.newCheckBox("Null Date Select");
        theNarrowSelect = theGuiFactory.newCheckBox("Show Narrow Days");

        /* Action selections */
        theNullSelect.getEventRegistrar().addEventListener(e -> allowNullDate.set(theNullSelect.isSelected()));
        theNarrowSelect.getEventRegistrar().addEventListener(e -> showNarrowDays.set(theNarrowSelect.isSelected()));
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
     * Make the table.
     */
    private void makeTable() {
        /* Create the list */
        final List<DateItem> myList = new ArrayList<DateItem>();
        myList.add(new DateItem(DATE_FIRST, "First Entry"));
        myList.add(new DateItem(DATE_SECOND, "Second Entry"));
        myList.add(new DateItem(DATE_THIRD, "Third Entry"));
        myList.add(new DateItem(DATE_FOURTH, "Fourth Entry"));
        myList.add(new DateItem(DATE_FIFTH, "Fifth Entry"));

        /* Create the Observable list */
        final ObservableList<DateItem> data = FXCollections.observableArrayList(myList);
        theTable.setItems(data);

        /* Create the date column */
        final TethysFXTableDateColumn<String, DateItem> myDateColumn = theTable.declareDateColumn(DateItem.PROP_DATE);
        myDateColumn.setCellValueFactory(p -> p.getValue().dateProperty());
        myDateColumn.setOnCommit((p, v) -> p.setDate(v));
        myDateColumn.setColumnWidth(COL_DATE_WIDTH);
        myDateColumn.setDateConfigurator((r, c) -> {
            c.setEarliestDate(theStartDate.getSelectedDate());
            c.setLatestDate(theEndDate.getSelectedDate());
            c.setShowNarrowDays(showNarrowDays.get());
        });

        /* Create the comments column */
        final TethysFXTableStringColumn<String, DateItem> myCommentsColumn = theTable.declareStringColumn(DateItem.PROP_COMMENTS);
        myCommentsColumn.setCellValueFactory(p -> p.getValue().commentsProperty());
        myCommentsColumn.setOnCommit((p, v) -> p.setComments(v));
        myCommentsColumn.setColumnWidth(COL_COMMENT_WIDTH);
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
        private final SimpleObjectProperty<TethysDate> theDate = new SimpleObjectProperty<TethysDate>(this, PROP_DATE);

        /**
         * Comments Property.
         */
        private final SimpleStringProperty theComments = new SimpleStringProperty(this, PROP_COMMENTS);

        /**
         * Constructor.
         * @param pDate the date
         * @param pComments the comments
         */
        DateItem(final TethysDate pDate,
                 final String pComments) {
            /* Store parameters */
            theDate.set(pDate);
            theComments.set(pComments);
        }

        /**
         * Obtain the Date.
         * @return the name
         */
        public TethysDate getDate() {
            return theDate.get();
        }

        /**
         * Set the Date.
         * @param pDate the Date
         */
        public void setDate(final TethysDate pDate) {
            theDate.set(pDate);
        }

        /**
         * Obtain the Comments.
         * @return the Comment
         */
        public String getComments() {
            return theComments.get();
        }

        /**
         * Set the Comments.
         * @param pComments the Comments
         */
        public void setComments(final String pComments) {
            theComments.set(pComments);
        }

        /**
         * Get Date property.
         * @return the property
         */
        public ObjectProperty<TethysDate> dateProperty() {
            return theDate;
        }

        /**
         * Get Comment property.
         * @return the property
         */
        public StringProperty commentsProperty() {
            return theComments;
        }
    }

    /**
     * Some useful locales.
     */
    private enum ShortLocale {
        /**
         * China (shorten day names to one character).
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
         * UK.
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
