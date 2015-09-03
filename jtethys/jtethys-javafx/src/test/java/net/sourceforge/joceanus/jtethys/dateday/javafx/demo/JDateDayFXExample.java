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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.dateday.javafx.demo;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sourceforge.jdatebutton.javafx.JDateConfig;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.javafx.JDateDayButton;
import net.sourceforge.joceanus.jtethys.dateday.javafx.JDateDayCell;
import net.sourceforge.joceanus.jtethys.dateday.javafx.JDateDayConfig;
import net.sourceforge.joceanus.jtethys.dateday.javafx.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.ui.javafx.GuiUtils;

/**
 * <p>
 * Provides a simple application that illustrates the features of JDateDay.
 * @author Tony Washer
 */
public class JDateDayFXExample
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
     * Table Height.
     */
    private static final int TABLE_HEIGHT = 150;

    /**
     * Start sample date.
     */
    private static final JDateDay DATE_START = makeDate(2007, Month.JANUARY, 25);

    /**
     * End sample date.
     */
    private static final JDateDay DATE_END = makeDate(2018, Month.AUGUST, 9);

    /**
     * First sample date.
     */
    private static final JDateDay DATE_FIRST = makeDate(2011, Month.JULY, 1);

    /**
     * Second sample date.
     */
    private static final JDateDay DATE_SECOND = makeDate(2012, Month.MARCH, 14);

    /**
     * Third sample date.
     */
    private static final JDateDay DATE_THIRD = makeDate(2014, Month.NOVEMBER, 19);

    /**
     * Fourth sample date.
     */
    private static final JDateDay DATE_FOURTH = makeDate(2015, Month.MAY, 31);

    /**
     * Fifth sample date.
     */
    private static final JDateDay DATE_FIFTH = makeDate(2018, Month.FEBRUARY, 28);

    @Override
    public void start(final Stage pStage) {
        Scene myScene = new Scene(new Group());
        GuiUtils.addStyleSheet(myScene);
        GridPane myPane = makePanel();
        ((Group) myScene.getRoot()).getChildren().addAll(myPane);
        pStage.setTitle("JDateDayButton JavaFX Demo");
        pStage.setScene(myScene);
        pStage.show();

        /* Add a listener for width changes */
        myPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(final ObservableValue<? extends Number> pProperty,
                                final Number pOldValue,
                                final Number pNewValue) {
                Double myChange = (Double) pNewValue - (Double) pOldValue;
                Double myWidth = pStage.getWidth() + myChange;
                pStage.setWidth(myWidth);
            }
        });
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    /**
     * The table.
     */
    private DateTable theTable;

    /**
     * The list of locales.
     */
    private ChoiceBox<ShortLocale> theLocaleList;

    /**
     * The list of formats.
     */
    private ChoiceBox<String> theFormatList;

    /**
     * The start date.
     */
    private JDateDayButton theStartDate;

    /**
     * The end date.
     */
    private JDateDayButton theEndDate;

    /**
     * The Null Select checkBox.
     */
    private CheckBox theNullSelect;

    /**
     * The selected locale.
     */
    private SimpleObjectProperty<Locale> theLocale = new SimpleObjectProperty<Locale>(Locale.UK);

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
    private final JDateDayFormatter theFormatter = new JDateDayFormatter();

    /**
     * The range selection.
     */
    private final JDateDayRangeSelect theRangeSelect = new JDateDayRangeSelect(theFormatter, true);

    /**
     * The selected range.
     */
    private final Label theSelectedRange = new Label();

    /**
     * The Cell Date Config.
     */
    private final JDateDayConfig theDateConfig = new JDateDayConfig(theFormatter);

    /**
     * Create the panel.
     * @return the panel
     */
    private GridPane makePanel() {
        /* Create the table */
        theTable = new DateTable();

        /* Create the range panel */
        Node myRange = makeRangePanel();

        /* Create the style panel */
        Node myStyle = makeStylePanel();

        /* Create the options panel */
        Node myOptions = makeOptionsPanel();

        /* Create the panel */
        int iRow = 0;
        GridPane myPanel = new GridPane();
        myPanel.setHgap(GRID_GAP);
        myPanel.setVgap(GRID_GAP);
        myPanel.setPadding(new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH));
        myPanel.add(myRange, 0, iRow);
        myPanel.add(myStyle, 1, iRow++);
        myPanel.add(myOptions, 0, iRow++, 2, 1);
        GridPane.setHalignment(myOptions, HPos.CENTER);
        myPanel.add(theTable, 0, iRow++, 2, 1);
        GridPane.setHalignment(theTable, HPos.CENTER);
        Node myRangeSel = GuiUtils.getTitledPane("Explicit Range Selection", theRangeSelect);
        myPanel.add(myRangeSel, 0, iRow++, 2, 1);
        GridPane.setHalignment(myRangeSel, HPos.CENTER);
        GridPane.setFillWidth(myRangeSel, true);
        Label mySelRange = new Label("SelectedRange:");
        myPanel.add(mySelRange, 0, iRow);
        myPanel.add(theSelectedRange, 1, iRow++);

        myPanel.setMaxWidth(Double.MAX_VALUE);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Create the range panel.
     * @return the panel
     */
    private Node makeRangePanel() {
        /* Create the range buttons */
        makeRangeButtons();

        /* Create the additional labels */
        Label myStart = new Label("Start:");
        Label myEnd = new Label("End:");

        /* Create a Range sub-panel */
        GridPane myRange = new GridPane();
        myRange.setHgap(GRID_GAP);
        myRange.setVgap(GRID_GAP);
        myRange.setPadding(new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH));

        /* Position the contents */
        myRange.add(myStart, 0, 0);
        GridPane.setHalignment(myStart, HPos.RIGHT);
        myRange.add(theStartDate, 1, 0);
        myRange.add(myEnd, 0, 1);
        GridPane.setHalignment(myEnd, HPos.RIGHT);
        myRange.add(theEndDate, 1, 1);

        /* Return the panel */
        return GuiUtils.getTitledPane("Range Selection", myRange);
    }

    /**
     * Create the range panel.
     * @return the panel
     */
    private Node makeStylePanel() {
        /* Create the locale list */
        makeLocaleList();

        /* Create the format list */
        makeFormatList();

        /* Create the additional labels */
        Label myFormat = new Label("Format:");
        Label myLocale = new Label("Locale:");

        /* Create a Style sub-panel */
        GridPane myStyle = new GridPane();
        myStyle.setHgap(GRID_GAP);
        myStyle.setVgap(GRID_GAP);
        myStyle.setPadding(new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH));

        /* Position the contents */
        myStyle.add(myLocale, 0, 0);
        GridPane.setHalignment(myLocale, HPos.RIGHT);
        myStyle.add(theLocaleList, 1, 0);
        myStyle.add(myFormat, 0, 1);
        GridPane.setHalignment(myFormat, HPos.RIGHT);
        myStyle.add(theFormatList, 1, 1);

        /* Ensure that the button is same width */
        theLocaleList.setMaxWidth(Integer.MAX_VALUE);
        theFormatList.setMaxWidth(Integer.MAX_VALUE);

        /* Return the panel */
        return GuiUtils.getTitledPane("Format Selection", myStyle);
    }

    /**
     * Create the options panel.
     * @return the panel
     */
    private Node makeOptionsPanel() {
        /* Create the checkBox */
        makeCheckBox();

        /* Create an options sub-panel */
        HBox myOptions = new HBox();
        Region myStrut1 = new Region();
        Region myStrut2 = new Region();
        HBox.setHgrow(myStrut1, Priority.ALWAYS);
        HBox.setHgrow(myStrut2, Priority.ALWAYS);
        myOptions.getChildren().addAll(myStrut1, theNullSelect, myStrut2);

        /* Return the panel */
        return GuiUtils.getTitledPane("Options", myOptions);
    }

    /**
     * Create the list of available locales.
     */
    private void makeLocaleList() {
        /* Create the Combo box and populate it */
        theLocaleList = new ChoiceBox<ShortLocale>();
        ObservableList<ShortLocale> myItems = theLocaleList.getItems();
        for (ShortLocale myLocale : ShortLocale.values()) {
            /* Add the Locale to the list */
            myItems.add(myLocale);
        }

        /* Set the default item */
        theLocaleList.setValue(ShortLocale.UK);

        /* Action selections */
        ReadOnlyObjectProperty<ShortLocale> myProperty = theLocaleList.getSelectionModel().selectedItemProperty();
        myProperty.addListener(new ChangeListener<ShortLocale>() {
            @Override
            public void changed(final ObservableValue<? extends ShortLocale> pValue,
                                final ShortLocale pOldValue,
                                final ShortLocale pNewValue) {
                /* Store the new locale */
                ShortLocale myLocale = pNewValue;
                theLocale.setValue(myLocale.getLocale());
                showNarrowDays.setValue(myLocale.showNarrowDays());
                theTable.rePaint();
            }
        });
    }

    /**
     * Create the list of available formats.
     */
    private void makeFormatList() {
        /* Create the Combo box and populate it */
        theFormatList = new ChoiceBox<String>();
        ObservableList<String> myItems = theFormatList.getItems();
        myItems.add(DATEFORMAT_1);
        myItems.add(DATEFORMAT_2);
        myItems.add(DATEFORMAT_3);

        /* Set the default item */
        theFormatList.setValue(theFormat.getValue());

        /* Action selections */
        ReadOnlyObjectProperty<String> myProperty = theFormatList.getSelectionModel().selectedItemProperty();
        myProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> pValue,
                                final String pOldValue,
                                final String pNewValue) {
                /* Store the new format */
                theFormat.setValue(pNewValue);
                theTable.rePaint();
            }
        });
    }

    /**
     * Create the range buttons.
     */
    private void makeRangeButtons() {
        /* Create the buttons */
        theStartDate = new JDateDayButton(theFormatter);
        theEndDate = new JDateDayButton(theFormatter);

        /* Initialise the values */
        JDateDay myStart = DATE_START;
        JDateDay myEnd = DATE_END;
        JDateDayRange myRange = new JDateDayRange(myStart, myEnd);

        /* Set the values */
        theStartDate.setSelectedDateDay(myStart);
        theEndDate.setSelectedDateDay(myEnd);
        theDateConfig.setEarliestDateDay(myStart);
        theDateConfig.setLatestDateDay(myEnd);

        /* Set the range */
        theRangeSelect.setOverallRange(myRange);
        theSelectedRange.setText(theFormatter.formatDateDayRange(theRangeSelect.getRange()));

        /* Set the listeners */
        setListeners();
    }

    /**
     * Set Listeners.
     */
    private void setListeners() {
        /* Handle changes to locale */
        theLocale.addListener(new ChangeListener<Locale>() {
            @Override
            public void changed(final ObservableValue<? extends Locale> pValue,
                                final Locale pOldValue,
                                final Locale pNewValue) {
                /* Set locale for formatter */
                theFormatter.setLocale(pNewValue);
                theRangeSelect.setLocale(pNewValue);
                theSelectedRange.setText(theFormatter.formatDateDayRange(theRangeSelect.getRange()));
            }
        });

        /* Handle changes to format */
        theFormat.addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> pValue,
                                final String pOldValue,
                                final String pNewValue) {
                /* Set format for formatter */
                theFormatter.setFormat(pNewValue);
                theSelectedRange.setText(theFormatter.formatDateDayRange(theRangeSelect.getRange()));
            }
        });

        /* Handle changes to allow nullDate */
        allowNullDate.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> pValue,
                                final Boolean pOldValue,
                                final Boolean pNewValue) {
                /* Store the new flag */
                JDateConfig myConfig = theStartDate.getDateConfig();
                myConfig.setAllowNullDateSelection(pNewValue);
                myConfig = theEndDate.getDateConfig();
                myConfig.setAllowNullDateSelection(pNewValue);
                theDateConfig.setAllowNullDateSelection(pNewValue);
            }
        });

        /* Handle changes to allow narrowDays */
        showNarrowDays.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> pValue,
                                final Boolean pOldValue,
                                final Boolean pNewValue) {
                /* Store the new flag */
                JDateConfig myConfig = theStartDate.getDateConfig();
                myConfig.setShowNarrowDays(pNewValue);
                myConfig = theEndDate.getDateConfig();
                myConfig.setShowNarrowDays(pNewValue);
                theDateConfig.setShowNarrowDays(pNewValue);
            }
        });

        /* Handle changes to startDate */
        ObjectProperty<JDateDay> myProperty = theStartDate.selectedDateDayProperty();
        myProperty.addListener(new ChangeListener<JDateDay>() {
            @Override
            public void changed(final ObservableValue<? extends JDateDay> pValue,
                                final JDateDay pOldValue,
                                final JDateDay pNewValue) {
                /* Store the new earliestDate of endDate */
                theEndDate.setEarliestDateDay(pNewValue);
                theDateConfig.setEarliestDateDay(pNewValue);
                theRangeSelect.setOverallRange(new JDateDayRange(pNewValue, theDateConfig.getLatestDateDay()));
            }
        });

        /* Handle changes to endDate */
        myProperty = theEndDate.selectedDateDayProperty();
        myProperty.addListener(new ChangeListener<JDateDay>() {
            @Override
            public void changed(final ObservableValue<? extends JDateDay> pValue,
                                final JDateDay pOldValue,
                                final JDateDay pNewValue) {
                /* Store the new latestDate of startDate */
                theStartDate.setLatestDateDay(pNewValue);
                theDateConfig.setLatestDateDay(pNewValue);
                theRangeSelect.setOverallRange(new JDateDayRange(theDateConfig.getEarliestDateDay(), pNewValue));
            }
        });

        /* Handle changes to startDate */
        ObjectProperty<JDateDayRange> myRangeProperty = theRangeSelect.rangeProperty();
        myRangeProperty.addListener(new ChangeListener<JDateDayRange>() {
            @Override
            public void changed(final ObservableValue<? extends JDateDayRange> pValue,
                                final JDateDayRange pOldValue,
                                final JDateDayRange pNewValue) {
                /* Store the new range text */
                theSelectedRange.setText(theFormatter.formatDateDayRange(pNewValue));
            }
        });
    }

    /**
     * Create the checkBox.
     */
    private void makeCheckBox() {
        /* Create the check boxes */
        theNullSelect = new CheckBox("Null Date Select");

        /* Action selections */
        BooleanProperty myProperty = theNullSelect.selectedProperty();
        myProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> pValue,
                                final Boolean pOldValue,
                                final Boolean pNewValue) {
                /* Set the value */
                allowNullDate.set(pNewValue);
            }
        });
    }

    /**
     * Convenience method to create a date from Year, Month, Day.
     * @param pYear the year
     * @param pMonth the month
     * @param pDay the day
     * @return the requested date
     */
    private static JDateDay makeDate(final int pYear,
                                     final Month pMonth,
                                     final int pDay) {
        return new JDateDay(pYear, pMonth, pDay);
    }

    /**
     * DateItem class.
     */
    private final class DateTable
            extends TableView<DateItem> {
        /**
         * Date Column.
         */
        private final TableColumn<DateItem, JDateDay> theDateCol;

        /**
         * Repaint.
         */
        private void rePaint() {
            theDateCol.setVisible(false);
            theDateCol.setVisible(true);
        }

        /**
         * Constructor.
         */
        private DateTable() {
            /* Allow editing */
            setEditable(true);

            /* Create the columns */
            theDateCol = new TableColumn<DateItem, JDateDay>(DateItem.PROP_DATE);
            theDateCol.setCellValueFactory(new DateCellValueFactory());
            theDateCol.setCellFactory(new DateCellFactory());
            theDateCol.setPrefWidth(COL_DATE_WIDTH);
            theDateCol.setSortable(true);
            TableColumn<DateItem, String> myCommentCol = new TableColumn<DateItem, String>(DateItem.PROP_COMMENTS);
            myCommentCol.setCellValueFactory(new StringCellValueFactory());
            myCommentCol.setCellFactory(new StringCellFactory());
            myCommentCol.setSortable(false);
            myCommentCol.setPrefWidth(COL_COMMENT_WIDTH);
            getColumns().add(theDateCol);
            getColumns().add(myCommentCol);

            /* Create the list */
            List<DateItem> myList = new ArrayList<DateItem>();
            myList.add(new DateItem(DATE_FIRST, "First Entry"));
            myList.add(new DateItem(DATE_SECOND, "Second Entry"));
            myList.add(new DateItem(DATE_THIRD, "Third Entry"));
            myList.add(new DateItem(DATE_FOURTH, "Fourth Entry"));
            myList.add(new DateItem(DATE_FIFTH, "Fifth Entry"));

            /* Create the Observable list */
            ObservableList<DateItem> data = FXCollections.observableArrayList(myList);
            setItems(data);

            /* Set table height */
            setPrefHeight(TABLE_HEIGHT);
        }
    }

    /**
     * String Cell Value Factory class.
     */
    private static class StringCellValueFactory
            implements Callback<CellDataFeatures<DateItem, String>, ObservableValue<String>> {
        @Override
        public ObservableValue<String> call(final CellDataFeatures<DateItem, String> p) {
            DateItem myItem = p.getValue();
            return myItem.commentsProperty();
        }
    }

    /**
     * String TableCell Factory class.
     */
    private static class StringCellFactory
            implements Callback<TableColumn<DateItem, String>, TableCell<DateItem, String>> {
        @Override
        public TableCell<DateItem, String> call(final TableColumn<DateItem, String> p) {
            return new StringTableCell();
        }
    }

    /**
     * Date Cell Value Factory class.
     */
    private static class DateCellValueFactory
            implements Callback<CellDataFeatures<DateItem, JDateDay>, ObservableValue<JDateDay>> {
        @Override
        public ObservableValue<JDateDay> call(final CellDataFeatures<DateItem, JDateDay> p) {
            DateItem myItem = p.getValue();
            return myItem.dateProperty();
        }
    }

    /**
     * Date TableCell Factory class.
     */
    private class DateCellFactory
            implements Callback<TableColumn<DateItem, JDateDay>, TableCell<DateItem, JDateDay>> {
        @Override
        public TableCell<DateItem, JDateDay> call(final TableColumn<DateItem, JDateDay> p) {
            return new JDateDayCell<DateItem>(theDateConfig);
        }
    }

    /**
     * String TableCell class.
     */
    private static final class StringTableCell
            extends TableCell<DateItem, String> {
        /**
         * The Text Field.
         */
        private final TextField textField;

        /**
         * Constructor.
         */
        private StringTableCell() {
            textField = new TextField();
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(final KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    } else if (t.getCode() == KeyCode.TAB) {
                        commitEdit(textField.getText());
                    }
                }
            });
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(final ObservableValue<? extends Boolean> observable,
                                    final Boolean oldValue,
                                    final Boolean newValue) {
                    if (!newValue) {
                        commitEdit(textField.getText());
                    }
                }
            });
        }

        @Override
        public void startEdit() {
            if (isEditable()) {
                super.startEdit();
                textField.setText(getItem());
                textField.setMinWidth(getWidth() - getGraphicTextGap() * 2);
                setGraphic(textField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textField.requestFocus();
                        textField.selectAll();
                    }
                });
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void updateItem(final String pValue,
                               final boolean pEmpty) {
            /* Determine whether we are truly empty */
            int iRow = getIndex();
            boolean isEmpty = iRow < 0 || iRow >= getTableView().getItems().size();

            super.updateItem(pValue, isEmpty);
            if (isEmpty) {
                setText(null);
                setGraphic(null);
                setEditable(false);
            } else {
                /* Set Text details */
                setText(pValue);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
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
        private SimpleObjectProperty<JDateDay> theDate = new SimpleObjectProperty<JDateDay>(this, PROP_DATE);

        /**
         * Comments Property.
         */
        private SimpleStringProperty theComments = new SimpleStringProperty(this, PROP_COMMENTS);

        /**
         * Obtain the Date.
         * @return the name
         */
        public JDateDay getDate() {
            return theDate.get();
        }

        /**
         * Set the Date.
         * @param pDate the Date
         */
        public void setDate(final JDateDay pDate) {
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
        public ObjectProperty<JDateDay> dateProperty() {
            return theDate;
        }

        /**
         * Get Comment property.
         * @return the property
         */
        public StringProperty commentsProperty() {
            return theComments;
        }

        /**
         * Constructor.
         * @param pDate the date
         * @param pComments the comments
         */
        private DateItem(final JDateDay pDate,
                         final String pComments) {
            /* Store parameters */
            theDate.set(pDate);
            theComments.set(pComments);
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
