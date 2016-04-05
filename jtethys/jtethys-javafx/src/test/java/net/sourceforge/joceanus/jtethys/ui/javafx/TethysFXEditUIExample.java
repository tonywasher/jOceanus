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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.Currency;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;

/**
 * Scroll utilities examples.
 */
public class TethysFXEditUIExample
        extends Application {
    /**
     * The padding.
     */
    private static final int PADDING = 3;

    /**
     * The value width.
     */
    private static final int VALUE_WIDTH = 200;

    /**
     * Default icon width.
     */
    private static final int DEFAULT_ICONWIDTH = 24;

    /**
     * The GuiFactory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<Node, Node> theHelper;

    /**
     * The string edit field.
     */
    private final TethysFXStringTextField theStringField;

    /**
     * The short edit field.
     */
    private final TethysFXShortTextField theShortField;

    /**
     * The integer edit field.
     */
    private final TethysFXIntegerTextField theIntegerField;

    /**
     * The long edit field.
     */
    private final TethysFXLongTextField theLongField;

    /**
     * The money edit field.
     */
    private final TethysFXMoneyTextField theMoneyField;

    /**
     * The price edit field.
     */
    private final TethysFXPriceTextField thePriceField;

    /**
     * The diluted price edit field.
     */
    private final TethysFXDilutedPriceTextField theDilutedPriceField;

    /**
     * The dilution edit field.
     */
    private final TethysFXDilutionTextField theDilutionField;

    /**
     * The units edit field.
     */
    private final TethysFXUnitsTextField theUnitsField;

    /**
     * The rate edit field.
     */
    private final TethysFXRateTextField theRateField;

    /**
     * The ratio edit field.
     */
    private final TethysFXRatioTextField theRatioField;

    /**
     * The icon button manager.
     */
    private final TethysSimpleIconButtonManager<Boolean, ?, ?> theIconButtonMgr;

    /**
     * The icon button field.
     */
    private final TethysFXIconButtonField<Boolean> theIconField;

    /**
     * The scroll button manager.
     */
    private final TethysFXScrollButtonManager<String> theScrollButtonMgr;

    /**
     * The scroll button field.
     */
    private final TethysFXScrollButtonField<String> theScrollField;

    /**
     * The date button field.
     */
    private final TethysFXDateButtonField theDateField;

    /**
     * The list button manager.
     */
    private final TethysFXListButtonManager<TethysListId> theListButtonMgr;

    /**
     * The list button field.
     */
    private final TethysFXListButtonField<TethysListId> theListField;

    /**
     * The source.
     */
    private final TethysFXLabel theSource;

    /**
     * The result.
     */
    private final TethysFXLabel theClass;

    /**
     * The result.
     */
    private final TethysFXLabel theValue;

    /**
     * The Date formatter.
     */
    private final TethysDateFormatter theDateFormatter;

    /**
     * The Decimal formatter.
     */
    private final TethysDecimalFormatter theDecimalFormatter;

    /**
     * Constructor.
     */
    public TethysFXEditUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create GUI Factory */
        theGuiFactory = new TethysFXGuiFactory();

        /* Access formatters */
        TethysDataFormatter myFormatter = theGuiFactory.getDataFormatter();
        theDecimalFormatter = myFormatter.getDecimalFormatter();
        theDateFormatter = myFormatter.getDateFormatter();

        /* Create resources */
        theStringField = theGuiFactory.newStringField();
        theStringField.showCmdButton(true);
        theShortField = theGuiFactory.newShortField();
        theIntegerField = theGuiFactory.newIntegerField();
        theLongField = theGuiFactory.newLongField();
        theMoneyField = theGuiFactory.newMoneyField();
        thePriceField = theGuiFactory.newPriceField();
        theDilutedPriceField = theGuiFactory.newDilutedPriceField();
        theDilutionField = theGuiFactory.newDilutionField();
        theUnitsField = theGuiFactory.newUnitsField();
        theRateField = theGuiFactory.newRateField();
        theRatioField = theGuiFactory.newRatioField();
        theSource = theGuiFactory.newLabel();
        theClass = theGuiFactory.newLabel();
        theValue = theGuiFactory.newLabel();

        /* Create button fields */
        theScrollField = theGuiFactory.newScrollField();
        theScrollButtonMgr = theScrollField.getScrollManager();
        theDateField = theGuiFactory.newDateField();
        theIconField = theGuiFactory.newSimpleIconField();
        theIconButtonMgr = theIconField.getIconManager();
        theListField = theGuiFactory.newListField();
        theListButtonMgr = theListField.getListManager();
    }

    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage pStage) {
        /* Create the panel */
        Node myMain = buildPanel();

        /* Create scene */
        BorderPane myPane = new BorderPane();
        Scene myScene = new Scene(myPane);
        theGuiFactory.applyStyleSheets(myScene);
        myPane.setCenter(myMain);
        pStage.setTitle("JavaFXEdit Demo");
        TethysFXGuiUtils.addStyleSheet(myScene);
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Build the panel.
     * @return the panel
     */
    private Node buildPanel() {
        /* Create a GridPane for the fields */
        GridPane myPane = buildFieldPane();

        /* Create a ControlPane for the buttons */
        HBox myControls = buildControlPane();

        /* Create a GridPane for the results */
        GridPane myResults = buildResultsPane();

        /* Create borderPane for the window */
        BorderPane myMain = new BorderPane();
        StackPane myStack = TethysFXGuiUtils.getTitledPane("FieldArea", myPane);
        myMain.setCenter(myStack);
        myStack = TethysFXGuiUtils.getTitledPane("Controls", myControls);
        myMain.setTop(myStack);
        myStack = TethysFXGuiUtils.getTitledPane("Results", myResults);
        myMain.setBottom(myStack);
        myMain.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

        /* Return the panel */
        return myMain;
    }

    /**
     * Build field pane.
     * @return the field pane
     */
    private GridPane buildFieldPane() {
        /* Create a GridPane for the fields */
        GridPane myPane = new GridPane();
        int myRowNo = 0;
        myPane.setHgap(PADDING);
        myPane.setVgap(PADDING << 1);
        myPane.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        myPane.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(VALUE_WIDTH));

        /* Create String field line */
        Label myLabel = new Label("String:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theStringField.getNode());
        theStringField.getEventRegistrar().addEventListener(e -> processActionEvent(theStringField, e));
        theStringField.setValue("Test");

        /* Create Short field line */
        myLabel = new Label("Short:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theShortField.getNode());
        theShortField.getEventRegistrar().addEventListener(e -> processActionEvent(theShortField, e));
        theShortField.setValue(TethysScrollUITestHelper.SHORT_DEF);

        /* Create Integer field line */
        myLabel = new Label("Integer:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theIntegerField.getNode());
        theIntegerField.getEventRegistrar().addEventListener(e -> processActionEvent(theIntegerField, e));
        theIntegerField.setValue(TethysScrollUITestHelper.INT_DEF);

        /* Create Long field line */
        myLabel = new Label("Long:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theLongField.getNode());
        theLongField.getEventRegistrar().addEventListener(e -> processActionEvent(theLongField, e));
        theLongField.setValue(TethysScrollUITestHelper.LONG_DEF);

        /* Create Money field line */
        myLabel = new Label("Money:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theMoneyField.getNode());
        theMoneyField.getEventRegistrar().addEventListener(e -> processActionEvent(theMoneyField, e));
        theMoneyField.setValue(TethysScrollUITestHelper.MONEY_DEF);

        /* Create Price field line */
        myLabel = new Label("Price:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, thePriceField.getNode());
        thePriceField.getEventRegistrar().addEventListener(e -> processActionEvent(thePriceField, e));
        thePriceField.setValue(TethysScrollUITestHelper.PRICE_DEF);

        /* Create Units field line */
        myLabel = new Label("Units:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theUnitsField.getNode());
        theUnitsField.getEventRegistrar().addEventListener(e -> processActionEvent(theUnitsField, e));
        theUnitsField.setValue(TethysScrollUITestHelper.UNITS_DEF);

        /* Create Rate field line */
        myLabel = new Label("Rate:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theRateField.getNode());
        theRateField.getEventRegistrar().addEventListener(e -> processActionEvent(theRateField, e));
        theRateField.setValue(TethysScrollUITestHelper.RATE_DEF);

        /* Create Ratio field line */
        myLabel = new Label("Ratio:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theRatioField.getNode());
        theRatioField.getEventRegistrar().addEventListener(e -> processActionEvent(theRatioField, e));
        theRatioField.setValue(TethysScrollUITestHelper.RATIO_DEF);

        /* Create Dilution field line */
        myLabel = new Label("Dilution:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theDilutionField.getNode());
        theDilutionField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutionField, e));
        theDilutionField.setValue(TethysScrollUITestHelper.DILUTION_DEF);

        /* Create DilutedPrice field line */
        myLabel = new Label("DilutedPrice:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theDilutedPriceField.getNode());
        theDilutedPriceField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutedPriceField, e));

        /* Create ScrollButton field line */
        myLabel = new Label("ScrollButton:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theScrollField.getNode());
        theScrollField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theScrollField, e));
        theScrollField.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> theHelper.buildContextMenu(theScrollButtonMgr.getMenu()));
        theScrollField.setValue("First");

        /* Create DateButton field line */
        myLabel = new Label("DateButton:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theDateField.getNode());
        theDateField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theDateField, e));
        theDateField.setValue(new TethysDate());

        /* Create IconButton field line */
        myLabel = new Label("IconButton:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theIconField.getNode());
        theIconButtonMgr.setWidth(DEFAULT_ICONWIDTH);
        theHelper.buildSimpleIconState(theIconButtonMgr, TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
        theIconField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theIconField, e));
        theIconField.setValue(false);

        /* Create ListButton field line */
        myLabel = new Label("ListButton:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theListField.getNode());
        theListField.setValue(theHelper.buildToggleList(theListButtonMgr));
        theListField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theListField, e));

        /* Return the pane */
        return myPane;
    }

    /**
     * Build Result pane.
     * @return the result pane
     */
    private GridPane buildResultsPane() {
        /* Create a GridPane for the results */
        GridPane myGrid = new GridPane();
        int myRowNo = 0;
        myGrid.setHgap(PADDING);
        myGrid.setVgap(PADDING << 1);
        myGrid.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        myGrid.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(VALUE_WIDTH));

        /* Build the grid */
        Label myLabel = new Label("Source:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myGrid.addRow(myRowNo++, myLabel, theSource.getNode());
        myLabel = new Label("Class:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myGrid.addRow(myRowNo++, myLabel, theClass.getNode());
        myLabel = new Label("Value:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myGrid.addRow(myRowNo++, myLabel, theValue.getNode());

        /* Return the pane */
        return myGrid;
    }

    /**
     * Build Control pane.
     * @return the control pane
     */
    private HBox buildControlPane() {
        /* Create Toggle button for edit mode */
        ToggleButton myEditButton = new ToggleButton("Edit");
        myEditButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> pObservable,
                                final Boolean pOldValue,
                                final Boolean pNewValue) {
                if (pNewValue) {
                    myEditButton.setText("Freeze");
                    setEditMode(true);
                } else {
                    myEditButton.setText("Edit");
                    setEditMode(false);
                }
            }
        });

        /* Create ScrollButton button for currency */
        TethysFXScrollButtonManager<Currency> myCurrencyMgr = theGuiFactory.newScrollButton();
        TethysFXScrollContextMenu<Currency> myMenu = myCurrencyMgr.getMenu();
        Currency myDefault = Currency.getInstance("GBP");
        myMenu.addItem(myDefault, "Pounds");
        myMenu.addItem(Currency.getInstance("USD"), "Dollars");
        myMenu.addItem(Currency.getInstance("JPY"), "Yen");
        myCurrencyMgr.setValue(myDefault, "Pounds");
        setCurrency(myDefault);
        myCurrencyMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setCurrency(e.getDetails(Currency.class)));

        /* Create a spacer region */
        Region mySpacer = new Region();
        HBox.setHgrow(mySpacer, Priority.ALWAYS);

        /* Create an HBox for buttons */
        HBox myBox = new HBox();
        myBox.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        myBox.getChildren().addAll(myEditButton, mySpacer, myCurrencyMgr.getNode());

        /* Return the pane */
        return myBox;
    }

    /**
     * Set edit mode.
     * @param pDoEdit true/false
     */
    private void setEditMode(final boolean pDoEdit) {
        /* Set the editable indication */
        theStringField.setEditable(pDoEdit);
        theShortField.setEditable(pDoEdit);
        theIntegerField.setEditable(pDoEdit);
        theLongField.setEditable(pDoEdit);
        theMoneyField.setEditable(pDoEdit);
        thePriceField.setEditable(pDoEdit);
        theUnitsField.setEditable(pDoEdit);
        theRateField.setEditable(pDoEdit);
        theRatioField.setEditable(pDoEdit);
        theDilutionField.setEditable(pDoEdit);
        theDilutedPriceField.setEditable(pDoEdit);
        theScrollField.setEditable(pDoEdit);
        theDateField.setEditable(pDoEdit);
        theIconField.setEditable(pDoEdit);
        theListField.setEditable(pDoEdit);
    }

    /**
     * Set default currency.
     * @param pCurrency the default currency
     */
    private void setCurrency(final Currency pCurrency) {
        /* Set the deemed currency */
        theMoneyField.setDeemedCurrency(pCurrency);
        thePriceField.setDeemedCurrency(pCurrency);
        theDilutedPriceField.setDeemedCurrency(pCurrency);
    }

    /**
     * Process action.
     * @param pField the source of the action
     * @param pEvent the event
     */
    private void processActionEvent(final TethysFXDataTextField<?> pField,
                                    final TethysEvent<TethysUIEvent> pEvent) {
        /* Determine source */
        String mySource = pField.getClass().getSimpleName();

        /* Switch on action */
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                setResults(mySource, pEvent.getDetails());
                break;
            case NEWCOMMAND:
                setResults(mySource + "-Cmd", pEvent.getDetails());
                break;
            case PREPARECMDDIALOG:
                pField.getCmdMenu().removeAllItems();
                pField.getCmdMenu().addItem("TestCmd");
                break;
            default:
                break;
        }
    }

    /**
     * Set the results.
     * @param pSource the source of the results
     * @param pResults the results
     */
    private void setResults(final String pSource,
                            final Object pResults) {
        /* Record the source */
        theSource.setText(pSource);

        /* Record class of results */
        theClass.setText(pResults == null
                                          ? "Null"
                                          : pResults.getClass().getSimpleName());

        /* Record results */
        if (pResults instanceof String) {
            theValue.setText((String) pResults);
        } else if (pResults instanceof Short) {
            theValue.setText(theDecimalFormatter.formatShort((Short) pResults));
        } else if (pResults instanceof Integer) {
            theValue.setText(theDecimalFormatter.formatInteger((Integer) pResults));
        } else if (pResults instanceof Long) {
            theValue.setText(theDecimalFormatter.formatLong((Long) pResults));
        } else if (pResults instanceof TethysDecimal) {
            theValue.setText(theDecimalFormatter.formatDecimal((TethysDecimal) pResults));
        } else if (pResults instanceof Boolean) {
            theValue.setText(pResults.toString());
        } else if (pResults instanceof Boolean) {
            theValue.setText(pResults.toString());
        } else if (pResults instanceof TethysItemList) {
            theValue.setText(((TethysItemList<?>) pResults).toString());
        } else if (pResults instanceof TethysDate) {
            theValue.setText(theDateFormatter.formatDateDay((TethysDate) pResults));
        } else {
            theValue.setText(null);
        }
    }
}
