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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Currency;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
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
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton.TethysFXSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXListButton.TethysFXListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButton.TethysFXScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu;

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
    private final TethysFXSimpleIconButtonManager<Boolean> theIconButtonMgr;

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
     * The date button manager.
     */
    private final TethysFXDateButtonManager theDateButtonMgr;

    /**
     * The date button field.
     */
    private final TethysFXDateButtonField theDateField;

    /**
     * The list button manager.
     */
    private final TethysFXListButtonManager<String> theListButtonMgr;

    /**
     * The list button field.
     */
    private final TethysFXListButtonField<String> theListField;

    /**
     * The source.
     */
    private final Label theSource;

    /**
     * The result.
     */
    private final Label theClass;

    /**
     * The result.
     */
    private final Label theValue;

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

        /* Create formatter */
        TethysDataFormatter myFormatter = new TethysDataFormatter();
        theDecimalFormatter = myFormatter.getDecimalFormatter();
        theDateFormatter = myFormatter.getDateFormatter();

        /* Create resources */
        theStringField = new TethysFXStringTextField();
        theStringField.showCmdButton(true);
        theShortField = new TethysFXShortTextField(myFormatter);
        theIntegerField = new TethysFXIntegerTextField(myFormatter);
        theLongField = new TethysFXLongTextField(myFormatter);
        theMoneyField = new TethysFXMoneyTextField(myFormatter);
        thePriceField = new TethysFXPriceTextField(myFormatter);
        theDilutedPriceField = new TethysFXDilutedPriceTextField(myFormatter);
        theDilutionField = new TethysFXDilutionTextField(myFormatter);
        theUnitsField = new TethysFXUnitsTextField(myFormatter);
        theRateField = new TethysFXRateTextField(myFormatter);
        theRatioField = new TethysFXRatioTextField(myFormatter);
        theSource = new Label();
        theClass = new Label();
        theValue = new Label();

        /* Create button fields */
        theScrollButtonMgr = new TethysFXScrollButtonManager<String>();
        theScrollField = new TethysFXScrollButtonField<String>(theScrollButtonMgr);
        theDateButtonMgr = new TethysFXDateButtonManager(myFormatter);
        theDateField = new TethysFXDateButtonField(theDateButtonMgr);
        theIconButtonMgr = new TethysFXSimpleIconButtonManager<Boolean>();
        theIconField = new TethysFXIconButtonField<Boolean>(theIconButtonMgr);
        theListButtonMgr = new TethysFXListButtonManager<String>();
        theListField = new TethysFXListButtonField<String>(theListButtonMgr);
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
        Scene myScene = new Scene(new Group());
        ((Group) myScene.getRoot()).getChildren().addAll(myMain);
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
        theShortField.setValue((short) 1);

        /* Create Integer field line */
        myLabel = new Label("Integer:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theIntegerField.getNode());
        theIntegerField.getEventRegistrar().addEventListener(e -> processActionEvent(theIntegerField, e));
        theIntegerField.setValue(2);

        /* Create Long field line */
        myLabel = new Label("Long:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theLongField.getNode());
        theLongField.getEventRegistrar().addEventListener(e -> processActionEvent(theLongField, e));
        theLongField.setValue((long) 3);

        /* Create Money field line */
        myLabel = new Label("Money:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theMoneyField.getNode());
        theMoneyField.getEventRegistrar().addEventListener(e -> processActionEvent(theMoneyField, e));
        theMoneyField.setValue(new TethysMoney("12.45"));

        /* Create Price field line */
        myLabel = new Label("Price:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, thePriceField.getNode());
        thePriceField.getEventRegistrar().addEventListener(e -> processActionEvent(thePriceField, e));
        thePriceField.setValue(new TethysPrice("2.2"));

        /* Create Units field line */
        myLabel = new Label("Units:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theUnitsField.getNode());
        theUnitsField.getEventRegistrar().addEventListener(e -> processActionEvent(theUnitsField, e));
        theUnitsField.setValue(new TethysUnits("1"));

        /* Create Rate field line */
        myLabel = new Label("Rate:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theRateField.getNode());
        theRateField.getEventRegistrar().addEventListener(e -> processActionEvent(theRateField, e));
        theRateField.setValue(new TethysRate(".10"));

        /* Create Ratio field line */
        myLabel = new Label("Ratio:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theRatioField.getNode());
        theRatioField.getEventRegistrar().addEventListener(e -> processActionEvent(theRatioField, e));
        theRatioField.setValue(new TethysRatio("1.6"));

        /* Create Dilution field line */
        myLabel = new Label("Dilution:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theDilutionField.getNode());
        theDilutionField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutionField, e));
        theDilutionField.setValue(new TethysDilution("0.5"));

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
        theHelper.buildSimpleIconState(theIconButtonMgr,
                TethysFXGuiUtils.getIconAtSize(TethysHelperIcon.OPENFALSE, DEFAULT_ICONWIDTH),
                TethysFXGuiUtils.getIconAtSize(TethysHelperIcon.OPENTRUE, DEFAULT_ICONWIDTH));
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
        myGrid.addRow(myRowNo++, myLabel, theSource);
        myLabel = new Label("Class:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myGrid.addRow(myRowNo++, myLabel, theClass);
        myLabel = new Label("Value:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myGrid.addRow(myRowNo++, myLabel, theValue);

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
        TethysFXScrollButtonManager<Currency> myCurrencyMgr = new TethysFXScrollButtonManager<Currency>();
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
