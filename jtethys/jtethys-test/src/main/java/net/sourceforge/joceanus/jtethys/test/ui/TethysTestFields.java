/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.util.Currency;
import java.util.List;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIColorButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDilutedPriceEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDilutionEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIntegerEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUILongEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIPriceEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRateEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRatioEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIShortEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIUnitsEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIScrollPaneManager;

/**
 * Scroll utilities examples.
 */
public class TethysTestFields {
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
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * The Test helper.
     */
    private final TethysTestHelper theHelper;

    /**
     * The string edit field.
     */
    private final TethysUIStringEditField theStringField;

    /**
     * The textArea field.
     */
    private final TethysUIStringTextAreaField theTextAreaField;

    /**
     * The password edit field.
     */
    private final TethysUICharArrayEditField thePassField;

    /**
     * The short edit field.
     */
    private final TethysUIShortEditField theShortField;

    /**
     * The integer edit field.
     */
    private final TethysUIIntegerEditField theIntegerField;

    /**
     * The long edit field.
     */
    private final TethysUILongEditField theLongField;

    /**
     * The money edit field.
     */
    private final TethysUIMoneyEditField theMoneyField;

    /**
     * The price edit field.
     */
    private final TethysUIPriceEditField thePriceField;

    /**
     * The diluted price edit field.
     */
    private final TethysUIDilutedPriceEditField theDilutedPriceField;

    /**
     * The dilution edit field.
     */
    private final TethysUIDilutionEditField theDilutionField;

    /**
     * The units edit field.
     */
    private final TethysUIUnitsEditField theUnitsField;

    /**
     * The rate edit field.
     */
    private final TethysUIRateEditField theRateField;

    /**
     * The ratio edit field.
     */
    private final TethysUIRatioEditField theRatioField;

    /**
     * The colour edit field.
     */
    private final TethysUIColorButtonField theColorField;

    /**
     * The icon button field.
     */
    private final TethysUIIconButtonField<Boolean> theIconField;

    /**
     * The scroll button field.
     */
    private final TethysUIScrollButtonField<String> theScrollField;

    /**
     * The date button field.
     */
    private final TethysUIDateButtonField theDateField;

    /**
     * The list button field.
     */
    private final TethysUIListButtonField<TethysTestListId> theListField;

    /**
     * The source.
     */
    private final TethysUILabel theSource;

    /**
     * The result.
     */
    private final TethysUILabel theClass;

    /**
     * The result.
     */
    private final TethysUILabel theValue;

    /**
     * The Date formatter.
     */
    private final TethysDateFormatter theDateFormatter;

    /**
     * The Decimal formatter.
     */
    private final TethysDecimalFormatter theDecimalFormatter;

    /**
     * The Panel.
     */
    private final TethysUIComponent thePane;

    /**
     * The edit mode.
     */
    private boolean isEditing;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysTestFields(final TethysUIFactory<?> pFactory) {
        /* Create GUI Factory */
        theGuiFactory = pFactory;

        /* Create helper */
        theHelper = new TethysTestHelper(theGuiFactory);

        /* Access formatters */
        final TethysUIDataFormatter myFormatter = theGuiFactory.getDataFormatter();
        theDecimalFormatter = myFormatter.getDecimalFormatter();
        theDateFormatter = myFormatter.getDateFormatter();

        /* Create resources */
        final TethysUIFieldFactory myFields = theGuiFactory.fieldFactory();
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
        theStringField = myFields.newStringField();
        configureCmdMenu(theStringField);
        theTextAreaField = myFields.newStringAreaField();
        thePassField = myFields.newCharArrayField();
        theShortField = myFields.newShortField();
        theIntegerField = myFields.newIntegerField();
        theLongField = myFields.newLongField();
        theMoneyField = myFields.newMoneyField();
        thePriceField = myFields.newPriceField();
        theDilutedPriceField = myFields.newDilutedPriceField();
        theDilutionField = myFields.newDilutionField();
        theUnitsField = myFields.newUnitsField();
        theRateField = myFields.newRateField();
        theRatioField = myFields.newRatioField();
        theSource = myControls.newLabel();
        theClass = myControls.newLabel();
        theValue = myControls.newLabel();

        /* Create button fields */
        theColorField = myFields.newColorField();
        theScrollField = myFields.newScrollField(String.class);
        theDateField = myFields.newDateField();
        theIconField = myFields.newIconField(Boolean.class);
        theListField = myFields.newListField();

        /* Create the main panel */
        thePane = buildPanel();
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return thePane;
    }

    /**
     * Build the panel.
     * @return the panel
     */
    private TethysUIComponent buildPanel() {
        /* Create a GridPane for the fields */
        final TethysUIGridPaneManager myPane = buildFieldPane();

        /* Create a BoxPane for the buttons */
        final TethysUIBoxPaneManager myControls = buildControlPane();

        /* Create a GridPane for the results */
        final TethysUIGridPaneManager myResults = buildResultsPane();

        /* Create borderPane for the window */
        final TethysUIBoxPaneManager myMain = theGuiFactory.paneFactory().newVBoxPane();
        myControls.setBorderTitle("Controls");
        myMain.addNode(myControls);
        myPane.setBorderTitle("FieldArea");
        myMain.addNode(myPane);
        myResults.setBorderTitle("Results");
        myMain.addNode(myResults);
        myMain.setBorderPadding(PADDING);
        myMain.setPreferredWidth(VALUE_WIDTH);

        /* Return the panel */
        return myMain;
    }

    /**
     * Build field pane.
     * @return the field pane
     */
    private TethysUIGridPaneManager buildFieldPane() {
        /* Create a GridPane for the fields */
        final TethysUIGridPaneManager myGrid = theGuiFactory.paneFactory().newGridPane();
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();

        /* Create String field line */
        TethysUILabel myLabel = myControls.newLabel("String:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theStringField);
        myGrid.allowCellGrowth(theStringField);
        myGrid.newRow();
        theStringField.getEventRegistrar().addEventListener(e -> processActionEvent(theStringField, e));
        theStringField.setValue("Test");

        /* Create Password field line */
        myLabel = myControls.newLabel("Password:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(thePassField);
        myGrid.allowCellGrowth(thePassField);
        myGrid.newRow();
        thePassField.getEventRegistrar().addEventListener(e -> processActionEvent(thePassField, e));
        thePassField.setValue(TethysTestHelper.getPassword());

        /* Create Short field line */
        myLabel = myControls.newLabel("Short:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theShortField);
        myGrid.allowCellGrowth(theShortField);
        myGrid.newRow();
        theShortField.getEventRegistrar().addEventListener(e -> processActionEvent(theShortField, e));
        theShortField.setValue(TethysTestHelper.SHORT_DEF);

        /* Create Integer field line */
        myLabel = myControls.newLabel("Integer:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theIntegerField);
        myGrid.allowCellGrowth(theIntegerField);
        myGrid.newRow();
        theIntegerField.getEventRegistrar().addEventListener(e -> processActionEvent(theIntegerField, e));
        theIntegerField.setValue(TethysTestHelper.INT_DEF);

        /* Create Long field line */
        myLabel = myControls.newLabel("Long:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theLongField);
        myGrid.allowCellGrowth(theLongField);
        myGrid.newRow();
        theLongField.getEventRegistrar().addEventListener(e -> processActionEvent(theLongField, e));
        theLongField.setValue(TethysTestHelper.LONG_DEF);

        /* Create Money field line */
        myLabel = myControls.newLabel("Money:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theMoneyField);
        myGrid.allowCellGrowth(theMoneyField);
        myGrid.newRow();
        theMoneyField.getEventRegistrar().addEventListener(e -> processActionEvent(theMoneyField, e));
        theMoneyField.setValue(TethysTestHelper.MONEY_DEF);

        /* Create Price field line */
        myLabel = myControls.newLabel("Price:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(thePriceField);
        myGrid.allowCellGrowth(thePriceField);
        myGrid.newRow();
        thePriceField.getEventRegistrar().addEventListener(e -> processActionEvent(thePriceField, e));
        thePriceField.setValue(TethysTestHelper.PRICE_DEF);

        /* Create Units field line */
        myLabel = myControls.newLabel("Units:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theUnitsField);
        myGrid.allowCellGrowth(theUnitsField);
        myGrid.newRow();
        theUnitsField.getEventRegistrar().addEventListener(e -> processActionEvent(theUnitsField, e));
        theUnitsField.setValue(TethysTestHelper.UNITS_DEF);

        /* Create Rate field line */
        myLabel = myControls.newLabel("Rate:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theRateField);
        myGrid.allowCellGrowth(theRateField);
        myGrid.newRow();
        theRateField.getEventRegistrar().addEventListener(e -> processActionEvent(theRateField, e));
        theRateField.setValue(TethysTestHelper.RATE_DEF);

        /* Create Ratio field line */
        myLabel = myControls.newLabel("Ratio:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theRatioField);
        myGrid.allowCellGrowth(theRatioField);
        myGrid.newRow();
        theRatioField.getEventRegistrar().addEventListener(e -> processActionEvent(theRatioField, e));
        theRatioField.setValue(TethysTestHelper.RATIO_DEF);

        /* Create Dilution field line */
        myLabel = myControls.newLabel("Dilution:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theDilutionField);
        myGrid.allowCellGrowth(theDilutionField);
        myGrid.newRow();
        theDilutionField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutionField, e));
        theDilutionField.setValue(TethysTestHelper.DILUTION_DEF);
        theDilutionField.setValidator(TethysTestHelper::validateDilution);

        /* Create DilutedPrice field line */
        myLabel = myControls.newLabel("DilutedPrice:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theDilutedPriceField);
        myGrid.allowCellGrowth(theDilutedPriceField);
        myGrid.newRow();
        theDilutedPriceField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutedPriceField, e));

        /* Create ColorButton field line */
        myLabel = myControls.newLabel("ColorButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theColorField);
        myGrid.allowCellGrowth(theColorField);
        myGrid.newRow();
        theColorField.getEventRegistrar().addEventListener(e -> processActionEvent(theColorField, e));
        theColorField.setValue("#000000");

        /* Create ScrollButton field line */
        myLabel = myControls.newLabel("ScrollButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theScrollField);
        myGrid.allowCellGrowth(theScrollField);
        myGrid.newRow();
        theScrollField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theScrollField, e));
        theScrollField.setMenuConfigurator(theHelper::buildContextMenu);
        theScrollField.setValue("First");

        /* Create DateButton field line */
        myLabel = myControls.newLabel("DateButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theDateField);
        myGrid.allowCellGrowth(theDateField);
        myGrid.newRow();
        theDateField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theDateField, e));
        theDateField.setValue(new TethysDate());

        /* Create ListButton field line */
        myLabel = myControls.newLabel("ListButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theListField);
        myGrid.allowCellGrowth(theListField);
        myGrid.newRow();
        theListField.setValue(theHelper.buildSelectedList());
        theListField.setSelectables(theHelper::buildSelectableList);
        theListField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theListField, e));

        /* Create IconButton field line */
        myLabel = myControls.newLabel("IconButton:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theIconField);
        myGrid.allowCellGrowth(theIconField);
        myGrid.newRow();
        final TethysUIIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysTestIcon.OPENFALSE, TethysTestIcon.OPENTRUE);
        theIconField.setIconMapSet(() -> myMapSet);
        theIconField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theIconField, e));
        theIconField.setValue(Boolean.FALSE);

        /* Create TextArea field line */
        myLabel = myControls.newLabel("TextArea:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        final TethysUIScrollPaneManager myScroll = theGuiFactory.paneFactory().newScrollPane();
        myScroll.setContent(theTextAreaField);
        myGrid.addCell(myScroll);
        myGrid.allowCellGrowth(myScroll);
        myGrid.newRow();
        theTextAreaField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theTextAreaField, e));

        /* Return the pane */
        return myGrid;
    }

    /**
     * Build Result pane.
     * @return the result pane
     */
    private TethysUIGridPaneManager buildResultsPane() {
        /* Create a GridPane for the results */
        final TethysUIGridPaneManager myGrid = theGuiFactory.paneFactory().newGridPane();
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();

        /* Build the grid */
        TethysUILabel myLabel = myControls.newLabel("Source:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theSource);
        myGrid.allowCellGrowth(theSource);
        myGrid.newRow();
        myLabel = myControls.newLabel("Class:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theClass);
        myGrid.allowCellGrowth(theClass);
        myGrid.newRow();
        myLabel = myControls.newLabel("Value:");
        myGrid.addCell(myLabel);
        myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
        myGrid.addCell(theValue);
        myGrid.allowCellGrowth(theValue);

        /* Return the pane */
        return myGrid;
    }

    /**
     * Build Control pane.
     * @return the control pane
     */
    private TethysUIBoxPaneManager buildControlPane() {
        /* Create Toggle button for edit mode */
        final TethysUIButtonFactory<?> myButtons = theGuiFactory.buttonFactory();
        final TethysUIButton myEditButton = myButtons.newButton();
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
        final TethysUIScrollButtonManager<Currency> myCurrencyMgr = myButtons.newScrollButton(Currency.class);
        final TethysUIScrollMenu<Currency> myMenu = myCurrencyMgr.getMenu();
        final Currency myDefault = Currency.getInstance("GBP");
        myMenu.addItem(myDefault, "Pounds");
        myMenu.addItem(Currency.getInstance("USD"), "Dollars");
        myMenu.addItem(Currency.getInstance("JPY"), "Yen");
        myCurrencyMgr.setValue(myDefault, "Pounds");
        setCurrency(myDefault);
        myCurrencyMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setCurrency(e.getDetails(Currency.class)));

        /* Create an HBox for buttons */
        final TethysUIBoxPaneManager myBox = theGuiFactory.paneFactory().newHBoxPane();
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
        theTextAreaField.setEditable(pDoEdit);
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
    private void processActionEvent(final TethysUIDataEditField<?> pField,
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
    private static void configureCmdMenu(final TethysUIDataEditField<?> pField) {
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
            theValue.setText(pResults.toString());
        } else if (pResults instanceof TethysDate) {
            theValue.setText(theDateFormatter.formatDate((TethysDate) pResults));
        } else {
            theValue.setText(null);
        }
    }
}
