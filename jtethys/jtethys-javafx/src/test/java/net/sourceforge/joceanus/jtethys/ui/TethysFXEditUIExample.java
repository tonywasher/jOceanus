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
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataEditField.TethysFXUnitsTextField;
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
    private static final int PADDING = 5;

    /**
     * The value width.
     */
    private static final int VALUE_WIDTH = 200;

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
     * The Decimal formatter.
     */
    private final TethysDecimalFormatter theFormatter;

    /**
     * Constructor.
     */
    public TethysFXEditUIExample() {
        /* Create formatters/parsers */
        theFormatter = new TethysDecimalFormatter();
        TethysDecimalParser myParser = new TethysDecimalParser();

        /* Create resources */
        theStringField = new TethysFXStringTextField();
        theStringField.showCommandButton(true);
        theShortField = new TethysFXShortTextField(theFormatter, myParser);
        theIntegerField = new TethysFXIntegerTextField(theFormatter, myParser);
        theLongField = new TethysFXLongTextField(theFormatter, myParser);
        theMoneyField = new TethysFXMoneyTextField(theFormatter, myParser);
        thePriceField = new TethysFXPriceTextField(theFormatter, myParser);
        theDilutedPriceField = new TethysFXDilutedPriceTextField(theFormatter, myParser);
        theDilutionField = new TethysFXDilutionTextField(theFormatter, myParser);
        theUnitsField = new TethysFXUnitsTextField(theFormatter, myParser);
        theRateField = new TethysFXRateTextField(theFormatter, myParser);
        theRatioField = new TethysFXRatioTextField(theFormatter, myParser);
        theSource = new Label();
        theClass = new Label();
        theValue = new Label();
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
        pStage.setTitle("DateFXTextField Demo");
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
        theStringField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theStringField, pEvent);
            }
        });

        /* Create Short field line */
        myLabel = new Label("Short:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theShortField.getNode());
        theShortField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theShortField, pEvent);
            }
        });

        /* Create Integer field line */
        myLabel = new Label("Integer:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theIntegerField.getNode());
        theIntegerField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theIntegerField, pEvent);
            }
        });

        /* Create Long field line */
        myLabel = new Label("Long:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theLongField.getNode());
        theLongField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theLongField, pEvent);
            }
        });

        /* Create Money field line */
        myLabel = new Label("Money:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theMoneyField.getNode());
        theMoneyField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theMoneyField, pEvent);
            }
        });

        /* Create Price field line */
        myLabel = new Label("Price:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, thePriceField.getNode());
        thePriceField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(thePriceField, pEvent);
            }
        });

        /* Create Units field line */
        myLabel = new Label("Units:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theUnitsField.getNode());
        theUnitsField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theUnitsField, pEvent);
            }
        });

        /* Create Rate field line */
        myLabel = new Label("Rate:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theRateField.getNode());
        theRateField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theRateField, pEvent);
            }
        });

        /* Create Ratio field line */
        myLabel = new Label("Ratio:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theRatioField.getNode());
        theRatioField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theRatioField, pEvent);
            }
        });

        /* Create Dilution field line */
        myLabel = new Label("Dilution:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theDilutionField.getNode());
        theDilutionField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theDilutionField, pEvent);
            }
        });

        /* Create DilutedPrice field line */
        myLabel = new Label("DilutedPrice:");
        GridPane.setHalignment(myLabel, HPos.RIGHT);
        myPane.addRow(myRowNo++, myLabel, theDilutedPriceField.getNode());
        theDilutedPriceField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                processAction(theDilutedPriceField, pEvent);
            }
        });

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
        myCurrencyMgr.getEventRegistrar().addFilteredActionListener(TethysFXScrollButtonManager.ACTION_NEW_VALUE, new TethysActionEventListener() {
            @Override
            public void processActionEvent(final TethysActionEvent pEvent) {
                setCurrency(pEvent.getDetails(Currency.class));
            }
        });

        /* Create a spacer region */
        Region mySpacer = new Region();
        HBox.setHgrow(mySpacer, Priority.ALWAYS);

        /* Create an HBox for buttons */
        HBox myBox = new HBox();
        myBox.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        myBox.getChildren().addAll(myEditButton, mySpacer, myCurrencyMgr.getButton());

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
     * @param pSource the source of the action
     * @param pResults the results
     */
    private void processAction(final TethysDataEditField<?, Node, Node> pField,
                               final TethysActionEvent pEvent) {
        /* Determine source */
        String mySource = pField.getClass().getSimpleName();

        /* Switch on action */
        switch (pEvent.getActionId()) {
            case TethysDataEditField.ACTION_NEW_VALUE:
                setResults(mySource, pEvent.getDetails());
                break;
            case TethysDataEditField.ACTION_NEW_COMMAND:
                setResults(mySource + "-Cmd", pEvent.getDetails());
                break;
            case TethysDataEditField.ACTION_CMDMENU_BUILD:
                pField.getMenu().removeAllItems();
                pField.getMenu().addItem("TestCmd");
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
            theValue.setText(theFormatter.formatShort((Short) pResults));
        } else if (pResults instanceof Integer) {
            theValue.setText(theFormatter.formatInteger((Integer) pResults));
        } else if (pResults instanceof Long) {
            theValue.setText(theFormatter.formatLong((Long) pResults));
        } else if (pResults instanceof TethysDecimal) {
            theValue.setText(theFormatter.formatDecimal((TethysDecimal) pResults));
        } else {
            theValue.setText(null);
        }
    }
}
