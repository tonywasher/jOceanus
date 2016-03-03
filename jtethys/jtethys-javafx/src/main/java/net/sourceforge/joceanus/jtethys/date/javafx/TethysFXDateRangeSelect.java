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
package net.sourceforge.joceanus.jtethys.date.javafx;

import java.util.Locale;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import net.sourceforge.jdatebutton.javafx.ArrowIcon;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDatePeriod;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysDateRangeState;
import net.sourceforge.joceanus.jtethys.date.TethysDateResource;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateRangeSelector;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 * @author Tony Washer
 * @deprecated as of 1.5.0 use {@link TethysFXDateRangeSelector}
 */
@Deprecated
public class TethysFXDateRangeSelect
        extends HBox {
    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

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
    private final TethysDateFormatter theFormatter;

    /**
     * The Start Date button.
     */
    private final TethysFXDateButton theStartButton;

    /**
     * The End Date button.
     */
    private final TethysFXDateButton theEndButton;

    /**
     * The Base Date button.
     */
    private final TethysFXDateButton theBaseButton;

    /**
     * The Period Button.
     */
    private final ChoiceBox<TethysDatePeriod> thePeriodButton;

    /**
     * The Period Box.
     */
    private final HBox thePeriodBox;

    /**
     * The Standard Box.
     */
    private final HBox theStandardBox;

    /**
     * The Period Box.
     */
    private final HBox theCustomBox;

    /**
     * The Spacer.
     */
    private final Region theSpacer;

    /**
     * The Standard Label.
     */
    private final Label theStandardLabel;

    /**
     * The Next button.
     */
    private final Button theNextButton;

    /**
     * The Previous button.
     */
    private final Button thePrevButton;

    /**
     * The range property.
     */
    private final ObjectProperty<TethysDateRange> theSelectedRange;

    /**
     * The Active state.
     */
    private TethysDateRangeState theState = null;

    /**
     * The Saved state.
     */
    private TethysDateRangeState theSavePoint = null;

    /**
     * Constructor.
     */
    public TethysFXDateRangeSelect() {
        /* Call standard constructor */
        this(false);
    }

    /**
     * Constructor.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysFXDateRangeSelect(final boolean pBaseIsStart) {
        /* Call standard constructor */
        this(new TethysDateFormatter(), pBaseIsStart);
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysFXDateRangeSelect(final TethysDateFormatter pFormatter,
                                   final boolean pBaseIsStart) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Create the range property */
        theSelectedRange = new SimpleObjectProperty<>(this, PROPERTY_RANGE);

        /* Create initial state */
        theState = new TethysDateRangeState(pBaseIsStart);

        /* Create the period button */
        thePeriodButton = new ChoiceBox<>();
        buildPeriodMenu(thePeriodButton.getItems());
        thePeriodButton.setMaxHeight(Double.MAX_VALUE);
        thePeriodButton.valueProperty().addListener((v, o, n) -> {
            theState.setPeriod(n);
            notifyChangedRange();
        });

        /* Create the period box */
        Label myPeriodLabel = new Label(NLS_PERIOD);
        thePeriodBox = new HBox();
        thePeriodBox.setAlignment(Pos.CENTER);
        thePeriodBox.setSpacing(STRUT_WIDTH);
        thePeriodBox.getChildren().addAll(myPeriodLabel, thePeriodButton);

        /* Create the DateButtons */
        theStartButton = new TethysFXDateButton(theFormatter);
        theStartButton.setMaxHeight(Double.MAX_VALUE);
        theStartButton.selectedDateDayProperty().addListener((v, o, n) -> {
            theState.setStartDate(n);
            notifyChangedRange();
        });
        theEndButton = new TethysFXDateButton(theFormatter);
        theEndButton.setMaxHeight(Double.MAX_VALUE);
        theEndButton.selectedDateDayProperty().addListener((v, o, n) -> {
            theState.setEndDate(n);
            notifyChangedRange();
        });
        theBaseButton = new TethysFXDateButton(theFormatter);
        theBaseButton.setMaxHeight(Double.MAX_VALUE);
        theBaseButton.selectedDateDayProperty().addListener((v, o, n) -> {
            theState.setBaseDate(n);
            notifyChangedRange();
        });

        /* Create the next button */
        theNextButton = new Button();
        theNextButton.setGraphic(ArrowIcon.RIGHT.getArrow());
        theNextButton.setTooltip(new Tooltip(NLS_NEXTTIP));
        theNextButton.setOnAction(e -> {
            theState.setNextDate();
            notifyChangedRange();
        });

        /* Create the Previous button */
        thePrevButton = new Button();
        thePrevButton.setGraphic(ArrowIcon.LEFT.getArrow());
        thePrevButton.setTooltip(new Tooltip(NLS_PREVTIP));
        thePrevButton.setOnAction(e -> {
            theState.setPreviousDate();
            notifyChangedRange();
        });

        /* Create the Custom HBox */
        theCustomBox = new HBox();
        theCustomBox.setAlignment(Pos.CENTER);
        theCustomBox.setSpacing(STRUT_WIDTH);
        Label myStartLabel = new Label(NLS_START);
        Label myEndLabel = new Label(NLS_END);
        theCustomBox.getChildren().addAll(myStartLabel, theStartButton, myEndLabel, theEndButton);

        /* Create the Standard HBox */
        theStandardBox = new HBox();
        theStandardBox.setAlignment(Pos.CENTER);
        theStandardBox.setSpacing(STRUT_WIDTH);
        theStandardLabel = new Label();
        theStandardBox.getChildren().addAll(theStandardLabel, thePrevButton, theBaseButton, theNextButton);

        /* Create a small region for the centre */
        theSpacer = new Region();
        theSpacer.setPrefWidth(STRUT_WIDTH << 2);
        HBox.setHgrow(theSpacer, Priority.ALWAYS);

        /* Create the full sub-panel */
        applyState();
    }

    /**
     * Create titled pane wrapper around panel.
     * @return the titled pane
     */
    public StackPane getTitledSelectionPane() {
        /* Create the panel */
        return TethysFXGuiUtils.getTitledPane(NLS_TITLE, this);
    }

    /**
     * Obtain selected DateRange.
     * @return the selected date range
     */
    public TethysDateRange getRange() {
        return theSelectedRange.get();
    }

    /**
     * Obtain DateRange property.
     * @return the selected date range
     */
    public ObjectProperty<TethysDateRange> rangeProperty() {
        return theSelectedRange;
    }

    /**
     * Obtain current state.
     * @return the current state
     */
    private TethysDateRangeState getState() {
        return theState;
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public void setLocale(final Locale pLocale) {
        theFormatter.setLocale(pLocale);
        theState.setLocale(pLocale);
        applyState();
    }

    /**
     * Build period menu.
     * @param pItems the period items
     */
    private void buildPeriodMenu(final ObservableList<TethysDatePeriod> pItems) {
        /* Loop through the periods */
        for (TethysDatePeriod myPeriod : TethysDatePeriod.values()) {
            /* Add as long as it is not the datesUpTo period */
            if (!myPeriod.datesUpTo()) {
                /* Create a new JMenuItem for the period */
                pItems.add(myPeriod);
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
    public void setSelection(final TethysFXDateRangeSelect pSource) {
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

    /**
     * Notify changes to selected range.
     */
    private void notifyChangedRange() {
        /* Make sure that the state has been applied */
        applyState();
        theSelectedRange.set(theState.getRange());
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

        /* Access the children */
        ObservableList<Node> myChildren = getChildren();

        /* Set the period value */
        thePeriodButton.setValue(theState.getPeriod());
        if (isUpTo) {
            myChildren.remove(thePeriodBox);
        } else if (!myChildren.contains(thePeriodBox)) {
            myChildren.clear();
            myChildren.addAll(thePeriodBox, theSpacer);
        }

        /* If this is a custom state */
        if (theState.isCustom()) {
            /* Set values for buttons */
            theStartButton.setSelectedDateDay(theState.getStartDate());
            theEndButton.setSelectedDateDay(theState.getEndDate());

            /* If the custom box is not displaying */
            if (!myChildren.contains(theCustomBox)) {
                /* Make sure correct box is displayed */
                myChildren.remove(theStandardBox);
                myChildren.add(theCustomBox);
            }

            /* else is this is a full dates state */
        } else if (theState.isFull()) {
            /* Make sure boxes are removed */
            myChildren.removeAll(theStandardBox, theCustomBox);

            /* else this is a standard state */
        } else {
            /* Set value for button */
            theBaseButton.setSelectedDateDay(theState.getBaseDate());

            /* Enable/disable the adjustment buttons */
            theNextButton.setDisable(!theState.isNextOK());
            thePrevButton.setDisable(!theState.isPrevOK());

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

            /* If the standard box is not displaying */
            if (!myChildren.contains(theStandardBox)) {
                /* Make sure correct box is displayed */
                myChildren.remove(theCustomBox);
                myChildren.add(theStandardBox);
            }
        }
    }
}
