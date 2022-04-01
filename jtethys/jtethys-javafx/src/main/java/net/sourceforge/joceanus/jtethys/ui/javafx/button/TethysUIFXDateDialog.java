/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.button;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateResource;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateDialog;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;

/**
 * FX Date Dialog.
 */
public final class TethysUIFXDateDialog
        implements TethysEventProvider<TethysUIXEvent> {
    /**
     * StyleSheet Name.
     */
    private static final String CSS_STYLE_NAME = "jtethys-javafx-datedialog.css";

    /**
     * StyleSheet.
     */
    private static final String CSS_STYLE = TethysFXDateDialog.class.getResource(CSS_STYLE_NAME).toExternalForm();

    /**
     * The dialog style.
     */
    static final String STYLE_DIALOG = TethysUIFXUtils.CSS_STYLE_BASE + "-datedialog";

    /**
     * Null Date selection text.
     */
    private static final String NLS_NULLSELECT = TethysDateResource.DIALOG_NULL.getValue();

    /**
     * The stage.
     */
    private final Stage theStage;

    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

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
    private final Button theNullButton;

    /**
     * Is the Null button active.
     */
    private boolean isNullActive;

    /**
     * The Date Configuration.
     */
    private final TethysDateConfig theConfig;

    /**
     * Should we build names?
     */
    private boolean doBuildNames = true;

    /**
     * The container box.
     */
    private final BorderPane theContainer;

    /**
     * Have we selected a date?
     */
    private boolean haveSelected;

    /**
     * Constructor.
     * @param pConfig the configuration for the dialog
     */
    public TethysUIFXDateDialog(final TethysDateConfig pConfig) {
        /* Create Non-Modal and undecorated stage */
        theStage = new Stage(StageStyle.UNDECORATED);
        theStage.initModality(Modality.NONE);

        /* Store the DateConfig */
        theConfig = pConfig;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the panels */
        theDaysPanel = new PanelMonth(this);
        theNavigation = new PanelNavigation(this);

        /* Build the Null Select */
        theNullButton = new Button(NLS_NULLSELECT);
        theNullButton.setMaxWidth(Double.MAX_VALUE);
        theNullButton.addEventHandler(ActionEvent.ACTION, e -> setSelected(-1));

        /* Add listener to shut dialog on loss of focus */
        theStage.focusedProperty().addListener((v, o, n) -> {
            if (!n) {
                closeNonModal();
            }
        });

        /* Create the scene */
        theContainer = new BorderPane();
        theContainer.setTop(theNavigation.getHBox());
        theContainer.setCenter(theDaysPanel.getGridPane());
        theContainer.getStyleClass().add(STYLE_DIALOG);
        final Scene myScene = new Scene(theContainer);
        final ObservableList<String> mySheets = myScene.getStylesheets();
        mySheets.add(CSS_STYLE);
        theStage.setScene(myScene);

        /* Initialise the month */
        initialiseMonth();

        /* Add listener to shut dialog on escape key */
        theContainer.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                closeNonModal();
            }
        });

        /* Add listener to Configuration to reBuild Names */
        theConfig.getEventRegistrar().addEventListener(e -> doBuildNames());
    }

    @Override
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
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
    public TethysDateConfig getDateConfig() {
        return theConfig;
    }

    /**
     * Build the month.
     */
    void buildMonth() {
        /* Build the month */
        theNavigation.buildMonth();
        theDaysPanel.buildMonth();
    }

    /**
     * Set Selected Date.
     * @param pDay the Selected day
     */
    void setSelected(final int pDay) {
        /* Set the selected day */
        theConfig.setSelectedDay(pDay);

        /* Note that we have selected */
        haveSelected = true;

        /* Close the dialog */
        theStage.close();

        /* Note that selection has been made */
        theEventManager.fireEvent(TethysUIXEvent.NEWVALUE, theConfig.getSelectedDate());
    }

    /**
     * Resize the dialog.
     */
    void reSizeDialog() {
        theStage.sizeToScene();
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
                theContainer.setBottom(theNullButton);
            } else {
                theContainer.setBottom(null);
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
    public void showDialogUnderNode(final Node pNode) {
        /* Allow configuration to be updated */
        theEventManager.fireEvent(TethysUIXEvent.PREPAREDIALOG, theConfig);

        /* Determine the relevant bounds */
        final Bounds myBounds = pNode.localToScreen(pNode.getLayoutBounds());

        /* Position the dialog just below the node */
        theStage.setX(myBounds.getMinX());
        theStage.setY(myBounds.getMaxY());

        /* Note that we have not selected */
        haveSelected = false;

        /* Initialise the current month and show the dialog */
        initialiseMonth();
        theStage.show();
    }

    /**
     * Close non-Modal.
     */
    private void closeNonModal() {
        /* Close the window */
        theStage.close();

        /* Note that no selection has been made */
        theEventManager.fireEvent(TethysUIXEvent.WINDOWCLOSED);
    }

    /**
     * Panel Navigation class allowing navigation between months.
     */
    private static final class PanelNavigation {
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
         * The button style.
         */
        private static final String STYLE_BUTTON = STYLE_DIALOG + "-button";

        /**
         * The title style.
         */
        private static final String STYLE_TITLE = STYLE_DIALOG + "-title";

        /**
         * The owning dialog.
         */
        private final TethysUIFXDateDialog theDialog;

        /**
         * The Date Configuration.
         */
        private final TethysDateConfig theConfig;

        /**
         * The HBox.
         */
        private final HBox theHBox;

        /**
         * The Date Label.
         */
        private final Label theDateLabel;

        /**
         * The Previous Month Button.
         */
        private final Button thePrevMonthButton;

        /**
         * The Next Month Button.
         */
        private final Button theNextMonthButton;

        /**
         * The Previous Year Button.
         */
        private final Button thePrevYearButton;

        /**
         * The Next Year Button.
         */
        private final Button theNextYearButton;

        /**
         * Constructor.
         * @param pDialog the owning dialog
         */
        PanelNavigation(final TethysUIFXDateDialog pDialog) {
            /* Create the hBox with single space */
            theHBox = new HBox(1.0);

            /* Record the dialog */
            theDialog = pDialog;

            /* Store the Date Configuration */
            theConfig = pDialog.getDateConfig();

            /* Create the label */
            theDateLabel = new Label();
            theDateLabel.getStyleClass().add(STYLE_TITLE);

            /* Create the buttons */
            thePrevMonthButton = new Button();
            theNextMonthButton = new Button();
            thePrevYearButton = new Button();
            theNextYearButton = new Button();

            /* Set the icons */
            thePrevMonthButton.setGraphic(TethysUIFXArrowIcon.LEFT.getArrow());
            theNextMonthButton.setGraphic(TethysUIFXArrowIcon.RIGHT.getArrow());
            thePrevYearButton.setGraphic(TethysUIFXArrowIcon.DOUBLELEFT.getArrow());
            theNextYearButton.setGraphic(TethysUIFXArrowIcon.DOUBLERIGHT.getArrow());

            /* Add ToopTips */
            theNextMonthButton.setTooltip(new Tooltip(NLS_NEXTMONTH));
            thePrevMonthButton.setTooltip(new Tooltip(NLS_PREVMONTH));
            theNextYearButton.setTooltip(new Tooltip(NLS_NEXTYEAR));
            thePrevYearButton.setTooltip(new Tooltip(NLS_PREVYEAR));

            /* Listen for button events */
            thePrevMonthButton.addEventHandler(ActionEvent.ACTION, e -> {
                theConfig.previousMonth();
                theDialog.buildMonth();
            });
            theNextMonthButton.addEventHandler(ActionEvent.ACTION, e -> {
                theConfig.nextMonth();
                theDialog.buildMonth();
            });
            thePrevYearButton.addEventHandler(ActionEvent.ACTION, e -> {
                theConfig.previousYear();
                theDialog.buildMonth();
            });
            theNextYearButton.addEventFilter(ActionEvent.ACTION, e -> {
                theConfig.nextYear();
                theDialog.buildMonth();
            });

            /* Restrict the margins */
            thePrevMonthButton.getStyleClass().add(STYLE_BUTTON);
            theNextMonthButton.getStyleClass().add(STYLE_BUTTON);
            thePrevYearButton.getStyleClass().add(STYLE_BUTTON);
            theNextYearButton.getStyleClass().add(STYLE_BUTTON);

            /* Create the struts */
            final Region myStrut1 = new Region();
            final Region myStrut2 = new Region();
            HBox.setHgrow(myStrut1, Priority.ALWAYS);
            HBox.setHgrow(myStrut2, Priority.ALWAYS);

            /* Add these elements into the HBox */
            final ObservableList<Node> myChildren = theHBox.getChildren();
            myChildren.add(thePrevYearButton);
            myChildren.add(thePrevMonthButton);
            myChildren.add(myStrut1);
            myChildren.add(theDateLabel);
            myChildren.add(myStrut2);
            myChildren.add(theNextMonthButton);
            myChildren.add(theNextYearButton);
        }

        /**
         * Obtain the hBox.
         * @return the hBox
         */
        HBox getHBox() {
            return theHBox;
        }

        /**
         * Build month details.
         */
        private void buildMonth() {
            /* Obtain the active month */
            final TethysDate myBase = theConfig.getCurrentMonth();
            final Locale myLocale = theConfig.getLocale();

            /* Determine the display for the label */
            final String myMonth = myBase.getMonthValue().getDisplayName(TextStyle.FULL, myLocale);
            final String myYear = Integer.toString(myBase.getYear());

            /* Set the label */
            theDateLabel.setText(myMonth
                    + ", "
                    + myYear);

            /* Access boundary dates */
            final TethysDate myEarliest = theConfig.getEarliestDate();
            final TethysDate myLatest = theConfig.getLatestDate();

            /* Enable/Disable buttons as required */
            thePrevMonthButton.setDisable(TethysDateConfig.isSameMonth(myEarliest, myBase));
            thePrevYearButton.setDisable(TethysDateConfig.isSameYear(myEarliest, myBase));
            theNextMonthButton.setDisable(TethysDateConfig.isSameMonth(myLatest, myBase));
            theNextYearButton.setDisable(TethysDateConfig.isSameYear(myLatest, myBase));
        }
    }

    /**
     * Month Panel.
     */
    private static final class PanelMonth {
        /**
         * The panel style.
         */
        private static final String STYLE_PANEL = STYLE_DIALOG + "-month";

        /**
         * The header style.
         */
        private static final String STYLE_HEADER = STYLE_DIALOG + "-hdr";

        /**
         * Number of days in week.
         */
        private static final int DAYS_IN_WEEK = 7;

        /**
         * Maximum # of weeks in month.
         */
        private static final int MAX_WEEKS_IN_MONTH = 6;

        /**
         * Tile Width.
         */
        private static final int WIDTH_TILE = 22;

        /**
         * The Dialog.
         */
        private final TethysUIFXDateDialog theDialog;

        /**
         * The Date Configuration.
         */
        private final TethysDateConfig theConfig;

        /**
         * The GridPane.
         */
        private final GridPane theGridPane;

        /**
         * The Array of Days.
         */
        private final DayOfWeek[] theDaysOfWk = new DayOfWeek[DAYS_IN_WEEK];

        /**
         * The Array of Day Names.
         */
        private final Label[] theHdrs = new Label[DAYS_IN_WEEK];

        /**
         * The Array of Day Labels.
         */
        private final PanelDay[][] theDays = new PanelDay[MAX_WEEKS_IN_MONTH][DAYS_IN_WEEK];

        /**
         * The Active number of rows.
         */
        private int theNumRows = MAX_WEEKS_IN_MONTH;

        /**
         * Constructor.
         * @param pDialog the owning dialog
         */
        PanelMonth(final TethysUIFXDateDialog pDialog) {
            /* Store parameters */
            theDialog = pDialog;

            /* Create the gridPane */
            theGridPane = new GridPane();

            /* Store the Date Configuration */
            theConfig = pDialog.getDateConfig();

            /* Define style of GridPane */
            theGridPane.getStyleClass().add(STYLE_PANEL);

            /* Add the Names to the layout */
            for (int iCol = 0; iCol < DAYS_IN_WEEK; iCol++) {
                final Label myDay = new Label();
                myDay.setMaxWidth(Double.MAX_VALUE);
                theHdrs[iCol] = myDay;
                theGridPane.add(myDay, iCol, 0);
            }

            /* Add the Days to the layout */
            for (int iRow = 0; iRow < MAX_WEEKS_IN_MONTH; iRow++) {
                for (int iCol = 0; iCol < DAYS_IN_WEEK; iCol++) {
                    final PanelDay myDay = new PanelDay(theDialog);
                    theDays[iRow][iCol] = myDay;
                    theGridPane.add(myDay.getLabel(), iCol, iRow + 1);
                }
            }

            /* Build the Day Names */
            buildDayNames();
        }

        /**
         * Obtain the gridPane.
         * @return the gridPane
         */
        GridPane getGridPane() {
            return theGridPane;
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
         * Build the month.
         */
        void buildMonth() {
            /* Obtain the active month */
            final TethysDate myCurr = new TethysDate(theConfig.getCurrentMonth());

            /* Access the interesting days of the month */
            final int iCurrent = theConfig.getCurrentDay();
            final int iSelected = theConfig.getSelectedDay();
            final int iEarliest = theConfig.getEarliestDay();
            final int iLatest = theConfig.getLatestDay();

            /* Move to the start of the week */
            final DayOfWeek myWeekDay = myCurr.getDayOfWeek();
            final int iStart = getDayColumn(myWeekDay);
            if (iStart > 0) {
                myCurr.adjustDay(-iStart);
            }

            /* Loop through initial columns */
            int iCol = 0;
            for (int iDay = myCurr.getDay(); iCol < iStart; iCol++, iDay++, myCurr.adjustDay(1)) {
                /* Access the label */
                final PanelDay myLabel = theDays[0][iCol];

                /* Reset the day and set no day */
                myLabel.resetDay(false);
                myLabel.setDay(iDay, false);
            }

            /* Loop through the days of the month */
            int iRow = 0;
            final int iMonth = myCurr.getMonth();
            for (int iDay = 1; iMonth == myCurr.getMonth(); iCol++, iDay++, myCurr.adjustDay(1)) {
                /* Reset column if necessary */
                if (iCol >= DAYS_IN_WEEK) {
                    iRow++;
                    iCol = 0;
                }

                /* Access the label */
                final PanelDay myLabel = theDays[iRow][iCol];

                /* Reset the day */
                myLabel.resetDay(true);

                if (iSelected == iDay) {
                    myLabel.setSelected();
                } else if (iCurrent == iDay) {
                    myLabel.setCurrent();
                } else if (isWeekend(myCurr.getDayOfWeek())) {
                    myLabel.setWeekend();
                }

                /* Determine whether the day is select-able */
                boolean isSelectable = true;
                if (iEarliest > 0) {
                    isSelectable &= iDay >= iEarliest;
                }
                if (iLatest > 0) {
                    isSelectable &= iDay <= iLatest;
                }

                /* Check for allowed date */
                isSelectable &= theConfig.isAllowed(iDay);

                /* Set text */
                myLabel.setDay(iDay, isSelectable);
            }

            /* Loop through remaining columns in row */
            for (int iDay = 1; iCol < DAYS_IN_WEEK; iCol++, iDay++) {
                /* Access the label */
                final PanelDay myLabel = theDays[iRow][iCol];

                /* Reset the day and set no day */
                myLabel.resetDay(false);
                myLabel.setDay(iDay, false);
            }

            /* Resize to the number of rows */
            reSizeRows(iRow + 1);
        }

        /**
         * build Day names.
         */
        void buildDayNames() {
            /* Get todays date */
            final Locale myLocale = theConfig.getLocale();
            final Calendar myDate = Calendar.getInstance(myLocale);
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
                final Label myLabel = theHdrs[iCol];

                /* Reset classes */
                final ObservableList<String> myStyles = myLabel.getStyleClass();
                myStyles.clear();
                myStyles.add(STYLE_HEADER);

                /* Access the required name */
                myDoW = theDaysOfWk[iCol];
                final TextStyle myStyle = theConfig.showNarrowDays()
                        ? TextStyle.NARROW
                        : TextStyle.SHORT;
                final String myName = myDoW.getDisplayName(myStyle, myLocale);

                /* Set the name */
                myLabel.setText(myName);

                /* Set weekend if required */
                if (isWeekend(myDoW)) {
                    myStyles.add(PanelDay.STYLE_WEEKEND);
                }
            }
        }

        /**
         * ReSize the number of visible rows.
         * @param iNumRows number of visible rows
         */
        private void reSizeRows(final int iNumRows) {
            /* Access the children */
            final ObservableList<Node> myNodes = theGridPane.getChildren();

            /* Hide any visible rows that should now be hidden */
            while (iNumRows < theNumRows) {
                /* Decrement number of rows */
                theNumRows--;

                /* Loop through remaining rows */
                for (final PanelDay myDay : theDays[theNumRows]) {
                    /* Remove from panel */
                    myNodes.remove(myDay.getLabel());
                }
            }

            /* Show any hidden rows that should now be visible */
            while (iNumRows > theNumRows) {
                /* Increment number of rows */
                theNumRows++;

                /* Loop through remaining rows */
                int iCol = 0;
                for (final PanelDay myDay : theDays[theNumRows - 1]) {
                    /* Add to panel */
                    theGridPane.add(myDay.getLabel(), iCol++, theNumRows);
                }
            }

            /* RePack the Dialog */
            theDialog.reSizeDialog();
        }
    }

    /**
     * Panel class representing a single day in the panel.
     */
    private static final class PanelDay {
        /**
         * ToolTip for Current Day.
         */
        private static final String NLS_CURRENTDAY = TethysDateResource.DIALOG_CURRENT.getValue();

        /**
         * ToolTip for Selected Day.
         */
        private static final String NLS_SELECTEDDAY = TethysDateResource.DIALOG_SELECTED.getValue();

        /**
         * The panel style.
         */
        private static final String STYLE_PANEL = STYLE_DIALOG + "-day";

        /**
         * The inactive style.
         */
        private static final String STYLE_INACTIVE = STYLE_DIALOG + "-inactive";

        /**
         * The selected style.
         */
        private static final String STYLE_SELECTED = STYLE_DIALOG + "-selected";

        /**
         * The current style.
         */
        private static final String STYLE_CURRENT = STYLE_DIALOG + "-current";

        /**
         * The weekend style.
         */
        private static final String STYLE_WEEKEND = STYLE_DIALOG + "-weekend";

        /**
         * The Dialog.
         */
        private final TethysUIFXDateDialog theDialog;

        /**
         * The Label.
         */
        private final Label theLabel;

        /**
         * The day of the month.
         */
        private int theDay = -1;

        /**
         * Constructor.
         * @param pDialog the owning dialog
         */
        PanelDay(final TethysUIFXDateDialog pDialog) {
            /* Store parameters */
            theDialog = pDialog;

            /* Create the label */
            theLabel = new Label();

            /* Set width */
            theLabel.setPrefWidth(PanelMonth.WIDTH_TILE);
            theLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> theDialog.setSelected(theDay));
        }

        /**
         * Obtain the label.
         * @return the label
         */
        Label getLabel() {
            return theLabel;
        }

        /**
         * Reset a Day Label.
         * @param isActive true/false
         */
        void resetDay(final boolean isActive) {
            theLabel.setTooltip(null);
            theLabel.getStyleClass().clear();
            theLabel.getStyleClass().add(STYLE_PANEL);
            if (!isActive) {
                theLabel.getStyleClass().add(STYLE_INACTIVE);
            }
        }

        /**
         * Set day for label.
         * @param pDay the Day number
         * @param pSelectable is the day select-able
         */
        void setDay(final int pDay,
                    final boolean pSelectable) {
            /* Record the day */
            theDay = pDay;

            /* Set the text for the item */
            if (pDay > 0) {
                theLabel.setText(Integer.toString(theDay));
            } else {
                theLabel.setText("");
            }

            /* Enable/Disable the label */
            theLabel.setDisable(!pSelectable);
        }

        /**
         * Set weekend.
         */
        void setWeekend() {
            theLabel.getStyleClass().add(STYLE_WEEKEND);
        }

        /**
         * Set selected day.
         */
        void setSelected() {
            theLabel.getStyleClass().add(STYLE_SELECTED);
            theLabel.setTooltip(new Tooltip(NLS_SELECTEDDAY));
        }

        /**
         * Set current day.
         */
        void setCurrent() {
            theLabel.getStyleClass().add(STYLE_CURRENT);
            theLabel.setTooltip(new Tooltip(NLS_CURRENTDAY));
        }
    }
}
