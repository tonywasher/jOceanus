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
package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import java.util.Currency;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jtethys.TethysLogConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.test.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.test.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXButton;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXColorButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXCharArrayTextField;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXLabel;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButtonManager;
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
    private static final int VALUE_WIDTH = 300;

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
     * The password edit field.
     */
    private final TethysFXCharArrayTextField thePassField;
    
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
     * The colour edit field.
     */
    private final TethysFXColorButtonField theColorField;

    /**
     * The icon button field.
     */
    private final TethysFXIconButtonField<Boolean> theIconField;

    /**
     * The scroll button field.
     */
    private final TethysFXScrollButtonField<String> theScrollField;

    /**
     * The date button field.
     */
    private final TethysFXDateButtonField theDateField;

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
     * The edit mode.
     */
    private boolean isEditing;

    /**
     * Constructor.
     */
    public TethysFXEditUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create GUI Factory */
        theGuiFactory = new TethysFXGuiFactory();

        /* Access formatters */
        final TethysDataFormatter myFormatter = theGuiFactory.getDataFormatter();
        theDecimalFormatter = myFormatter.getDecimalFormatter();
        theDateFormatter = myFormatter.getDateFormatter();

        /* Create resources */
        theStringField = theGuiFactory.newStringField();
        configureCmdMenu(theStringField);
        thePassField = theGuiFactory.newCharArrayField();
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
        theColorField = theGuiFactory.newColorField();
        theScrollField = theGuiFactory.newScrollField();
        theDateField = theGuiFactory.newDateField();
        theIconField = theGuiFactory.newIconField();
        theListField = theGuiFactory.newListField();
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
        /* Configure log4j */
        TethysLogConfig.configureLog4j();

        /* Create the panel */
        final Node myMain = buildPanel();

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        theGuiFactory.registerScene(myScene);
        myPane.setCenter(myMain);
        pStage.setTitle("JavaFXEdit Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Build the panel.
     * @return the panel
     */
    private Node buildPanel() {
        /* Create a GridPane for the fields */
        final TethysFXGridPaneManager myPane = buildFieldPane();

        /* Create a BoxPane for the buttons */
        final TethysFXBoxPaneManager myControls = buildControlPane();

        /* Create a GridPane for the results */
        final TethysFXGridPaneManager myResults = buildResultsPane();

        /* Create borderPane for the window */
        final TethysFXBoxPaneManager myMain = theGuiFactory.newVBoxPane();
        myControls.setBorderTitle("Controls");
        myMain.addNode(myControls);
        myPane.setBorderTitle("FieldArea");
        myMain.addNode(myPane);
        myResults.setBorderTitle("Results");
        myMain.addNode(myResults);
        myMain.setBorderPadding(PADDING);
        myMain.setPreferredWidth(VALUE_WIDTH);

        /* Return the panel */
        return myMain.getNode();
    }

    /**
     * Build field pane.
     * @return the field pane
     */
    private TethysFXGridPaneManager buildFieldPane() {
        /* Create a GridPane for the fields */
        final TethysFXGridPaneManager myGrid = theGuiFactory.newGridPane();

        /* Create String field line */
        TethysFXLabel myLabel = theGuiFactory.newLabel("String:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theStringField);
        myGrid.allowCellGrowth(theStringField);
        myGrid.newRow();
        theStringField.getEventRegistrar().addEventListener(e -> processActionEvent(theStringField, e));
        theStringField.setValue("Test");

        /* Create Password field line */
        myLabel = theGuiFactory.newLabel("Password:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(thePassField);
        myGrid.allowCellGrowth(thePassField);
        myGrid.newRow();
        thePassField.getEventRegistrar().addEventListener(e -> processActionEvent(thePassField, e));
        thePassField.setValue(TethysScrollUITestHelper.getPassword());

        /* Create Short field line */
        myLabel = theGuiFactory.newLabel("Short:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theShortField);
        myGrid.allowCellGrowth(theShortField);
        myGrid.newRow();
        theShortField.getEventRegistrar().addEventListener(e -> processActionEvent(theShortField, e));
        theShortField.setValue(TethysScrollUITestHelper.SHORT_DEF);

        /* Create Integer field line */
        myLabel = theGuiFactory.newLabel("Integer:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theIntegerField);
        myGrid.allowCellGrowth(theIntegerField);
        myGrid.newRow();
        theIntegerField.getEventRegistrar().addEventListener(e -> processActionEvent(theIntegerField, e));
        theIntegerField.setValue(TethysScrollUITestHelper.INT_DEF);

        /* Create Long field line */
        myLabel = theGuiFactory.newLabel("Long:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theLongField);
        myGrid.allowCellGrowth(theLongField);
        myGrid.newRow();
        theLongField.getEventRegistrar().addEventListener(e -> processActionEvent(theLongField, e));
        theLongField.setValue(TethysScrollUITestHelper.LONG_DEF);

        /* Create Money field line */
        myLabel = theGuiFactory.newLabel("Money:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theMoneyField);
        myGrid.allowCellGrowth(theMoneyField);
        myGrid.newRow();
        theMoneyField.getEventRegistrar().addEventListener(e -> processActionEvent(theMoneyField, e));
        theMoneyField.setValue(TethysScrollUITestHelper.MONEY_DEF);

        /* Create Price field line */
        myLabel = theGuiFactory.newLabel("Price:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(thePriceField);
        myGrid.allowCellGrowth(thePriceField);
        myGrid.newRow();
        thePriceField.getEventRegistrar().addEventListener(e -> processActionEvent(thePriceField, e));
        thePriceField.setValue(TethysScrollUITestHelper.PRICE_DEF);

        /* Create Units field line */
        myLabel = theGuiFactory.newLabel("Units:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theUnitsField);
        myGrid.allowCellGrowth(theUnitsField);
        myGrid.newRow();
        theUnitsField.getEventRegistrar().addEventListener(e -> processActionEvent(theUnitsField, e));
        theUnitsField.setValue(TethysScrollUITestHelper.UNITS_DEF);

        /* Create Rate field line */
        myLabel = theGuiFactory.newLabel("Rate:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theRateField);
        myGrid.allowCellGrowth(theRateField);
        myGrid.newRow();
        theRateField.getEventRegistrar().addEventListener(e -> processActionEvent(theRateField, e));
        theRateField.setValue(TethysScrollUITestHelper.RATE_DEF);

        /* Create Ratio field line */
        myLabel = theGuiFactory.newLabel("Ratio:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theRatioField);
        myGrid.allowCellGrowth(theRatioField);
        myGrid.newRow();
        theRatioField.getEventRegistrar().addEventListener(e -> processActionEvent(theRatioField, e));
        theRatioField.setValue(TethysScrollUITestHelper.RATIO_DEF);

        /* Create Dilution field line */
        myLabel = theGuiFactory.newLabel("Dilution:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theDilutionField);
        myGrid.allowCellGrowth(theDilutionField);
        myGrid.newRow();
        theDilutionField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutionField, e));
        theDilutionField.setValue(TethysScrollUITestHelper.DILUTION_DEF);
        theDilutionField.setValidator(TethysScrollUITestHelper::validateDilution);

        /* Create DilutedPrice field line */
        myLabel = theGuiFactory.newLabel("DilutedPrice:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theDilutedPriceField);
        myGrid.allowCellGrowth(theDilutedPriceField);
        myGrid.newRow();
        theDilutedPriceField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutedPriceField, e));

        /* Create ColorButton field line */
        myLabel = theGuiFactory.newLabel("ColorButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theColorField);
        myGrid.allowCellGrowth(theColorField);
        myGrid.newRow();
        theColorField.getEventRegistrar().addEventListener(e -> processActionEvent(theColorField, e));
        theColorField.setValue("#000000");

        /* Create ScrollButton field line */
        myLabel = theGuiFactory.newLabel("ScrollButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theScrollField);
        myGrid.allowCellGrowth(theScrollField);
        myGrid.newRow();
        theScrollField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theScrollField, e));
        theScrollField.setMenuConfigurator(theHelper::buildContextMenu);
        theScrollField.setValue("First");

        /* Create DateButton field line */
        myLabel = theGuiFactory.newLabel("DateButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theDateField);
        myGrid.allowCellGrowth(theDateField);
        myGrid.newRow();
        theDateField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theDateField, e));
        theDateField.setValue(new TethysDate());

        /* Create ListButton field line */
        myLabel = theGuiFactory.newLabel("ListButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theListField);
        myGrid.allowCellGrowth(theListField);
        myGrid.newRow();
        theListField.setValue(theHelper.buildSelectedList());
        theListField.setSelectables(theHelper::buildSelectableList);
        theListField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theListField, e));

        /* Create IconButton field line */
        myLabel = theGuiFactory.newLabel("IconButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theIconField);
        myGrid.allowCellGrowth(theIconField);
        myGrid.newRow();
        final TethysIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
        theIconField.setIconMapSet(() -> myMapSet);
        theIconField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theIconField, e));
        theIconField.setValue(Boolean.FALSE);

        /* Return the pane */
        return myGrid;
    }

    /**
     * Build Result pane.
     * @return the result pane
     */
    private TethysFXGridPaneManager buildResultsPane() {
        /* Create a GridPane for the results */
        final TethysFXGridPaneManager myGrid = theGuiFactory.newGridPane();

        /* Build the grid */
        TethysFXLabel myLabel = theGuiFactory.newLabel("Source:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theSource);
        myGrid.allowCellGrowth(theSource);
        myGrid.newRow();
        myLabel = theGuiFactory.newLabel("Class:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theClass);
        myGrid.allowCellGrowth(theClass);
        myGrid.newRow();
        myLabel = theGuiFactory.newLabel("Value:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
        myGrid.addCell(theValue);
        myGrid.allowCellGrowth(theValue);

        /* Return the pane */
        return myGrid;
    }

    /**
     * Build Control pane.
     * @return the control pane
     */
    private TethysFXBoxPaneManager buildControlPane() {
        /* Create Toggle button for edit mode */
        final TethysFXButton myEditButton = theGuiFactory.newButton();
        myEditButton.setText("Edit");
        myEditButton.getEventRegistrar().addEventListener(e -> {
            if (!isEditing) {
                myEditButton.setText("Freeze");
                setEditMode(true);
            } else {
                myEditButton.setText("Edit");
                setEditMode(false);
            }
            isEditing = !isEditing;
        });

        /* Create ScrollButton button for currency */
        final TethysFXScrollButtonManager<Currency> myCurrencyMgr = theGuiFactory.newScrollButton();
        final TethysFXScrollContextMenu<Currency> myMenu = myCurrencyMgr.getMenu();
        final Currency myDefault = Currency.getInstance("GBP");
        myMenu.addItem(myDefault, "Pounds");
        myMenu.addItem(Currency.getInstance("USD"), "Dollars");
        myMenu.addItem(Currency.getInstance("JPY"), "Yen");
        myCurrencyMgr.setValue(myDefault, "Pounds");
        setCurrency(myDefault);
        myCurrencyMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setCurrency(e.getDetails(Currency.class)));

        /* Create an HBox for buttons */
        final TethysFXBoxPaneManager myBox = theGuiFactory.newHBoxPane();
        myBox.addNode(myEditButton);
        myBox.addSpacer();
        myBox.addNode(myCurrencyMgr);

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
        thePassField.setEditable(pDoEdit);
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
        theColorField.setEditable(pDoEdit);
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
        theMoneyField.setDeemedCurrency(() -> pCurrency);
        thePriceField.setDeemedCurrency(() -> pCurrency);
        theDilutedPriceField.setDeemedCurrency(() -> pCurrency);
    }

    /**
     * Process action.
     * @param pField the source of the action
     * @param pEvent the event
     */
    private void processActionEvent(final TethysFXDataTextField<?> pField,
                                    final TethysEvent<TethysUIEvent> pEvent) {
        /* Determine source */
        final String mySource = pField.getClass().getSimpleName();

        /* Switch on action */
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                setResults(mySource, pEvent.getDetails());
                break;
            case NEWCOMMAND:
                setResults(mySource + "-Cmd", pEvent.getDetails());
                break;
            default:
                break;
        }
    }

    /**
     * Configure a cmdMenu.
     * @param pField the command menu to configure
     */
    private void configureCmdMenu(final TethysFXDataTextField<?> pField) {
        /* Configure the command menu */
        pField.showCmdButton(true);
        pField.setCmdMenuConfigurator(c -> {
            c.removeAllItems();
            c.addItem("TestCmd");
        });
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
        } else if (pResults instanceof char[]) {
            theValue.setText(new String((char[]) pResults));
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
        } else if (pResults instanceof List) {
            theValue.setText(((List<?>) pResults).toString());
        } else if (pResults instanceof TethysDate) {
            theValue.setText(theDateFormatter.formatDate((TethysDate) pResults));
        } else {
            theValue.setText(null);
        }
    }
}
