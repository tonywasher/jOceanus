/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.HeadlessException;
import java.util.Currency;
import java.util.List;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.test.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.test.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingColorButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingLabel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollContextMenu;

/**
 * Scroll utilities examples.
 */
public class TethysSwingEditUIExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingEditUIExample.class);

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
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper theHelper;

    /**
     * The string edit field.
     */
    private final TethysSwingStringTextField theStringField;

    /**
     * The password edit field.
     */
    private final TethysSwingCharArrayTextField thePassField;

    /**
     * The short edit field.
     */
    private final TethysSwingShortTextField theShortField;

    /**
     * The integer edit field.
     */
    private final TethysSwingIntegerTextField theIntegerField;

    /**
     * The long edit field.
     */
    private final TethysSwingLongTextField theLongField;

    /**
     * The money edit field.
     */
    private final TethysSwingMoneyTextField theMoneyField;

    /**
     * The price edit field.
     */
    private final TethysSwingPriceTextField thePriceField;

    /**
     * The diluted price edit field.
     */
    private final TethysSwingDilutedPriceTextField theDilutedPriceField;

    /**
     * The dilution edit field.
     */
    private final TethysSwingDilutionTextField theDilutionField;

    /**
     * The units edit field.
     */
    private final TethysSwingUnitsTextField theUnitsField;

    /**
     * The rate edit field.
     */
    private final TethysSwingRateTextField theRateField;

    /**
     * The ratio edit field.
     */
    private final TethysSwingRatioTextField theRatioField;

    /**
     * The colour edit field.
     */
    private final TethysSwingColorButtonField theColorField;

    /**
     * The icon button field.
     */
    private final TethysSwingIconButtonField<Boolean> theIconField;

    /**
     * The scroll button field.
     */
    private final TethysSwingScrollButtonField<String> theScrollField;

    /**
     * The date button field.
     */
    private final TethysSwingDateButtonField theDateField;

    /**
     * The list button field.
     */
    private final TethysSwingListButtonField<TethysListId> theListField;

    /**
     * The source.
     */
    private final TethysSwingLabel theSource;

    /**
     * The result.
     */
    private final TethysSwingLabel theClass;

    /**
     * The result.
     */
    private final TethysSwingLabel theValue;

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
    public TethysSwingEditUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper();

        /* Create GUI Factory */
        theGuiFactory = new TethysSwingGuiFactory();

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
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(TethysSwingEditUIExample::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("SwingEdit Demo");

            /* Create the UI */
            final TethysSwingEditUIExample myExample = new TethysSwingEditUIExample();

            /* Build the panel */
            final JComponent myPanel = myExample.buildPanel();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
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
     * Build the panel.
     * @return the panel
     */
    private JComponent buildPanel() {
        /* Create a GridPane for the fields */
        final TethysSwingGridPaneManager myFields = buildFieldPane();

        /* Create a ControlPane for the buttons */
        final TethysSwingBoxPaneManager myControls = buildControlPane();

        /* Create a GridPane for the results */
        final TethysSwingGridPaneManager myResults = buildResultsPane();

        /* Create borderPane for the window */
        final TethysSwingBoxPaneManager myMain = theGuiFactory.newVBoxPane();
        myControls.setBorderTitle("Controls");
        myMain.addNode(myControls);
        myFields.setBorderTitle("FieldArea");
        myMain.addNode(myFields);
        myResults.setBorderTitle("Results");
        myMain.addNode(myResults);
        myMain.setBorderPadding(PADDING);
        myMain.setPreferredWidth(VALUE_WIDTH);

        /* Return the panel */
        return TethysSwingNode.getComponent(myMain);
    }

    /**
     * Build field pane.
     * @return the field pane
     */
    private TethysSwingGridPaneManager buildFieldPane() {
        /* Create a GridPane for the fields */
        final TethysSwingGridPaneManager myGrid = theGuiFactory.newGridPane();

        /* Create String field line */
        TethysSwingLabel myLabel = theGuiFactory.newLabel("String:");
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
    private TethysSwingGridPaneManager buildResultsPane() {
        /* Create a GridPane for the results */
        final TethysSwingGridPaneManager myGrid = theGuiFactory.newGridPane();

        /* Build the grid */
        TethysSwingLabel myLabel = theGuiFactory.newLabel("Source:");
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

        /* Return the panel */
        return myGrid;
    }

    /**
     * Build Control pane.
     * @return the control pane
     */
    private TethysSwingBoxPaneManager buildControlPane() {
        /* Create Toggle button for edit mode */
        final TethysSwingButton myEditButton = theGuiFactory.newButton();
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
        final TethysSwingScrollButtonManager<Currency> myCurrencyMgr = theGuiFactory.newScrollButton();
        final TethysSwingScrollContextMenu<Currency> myMenu = myCurrencyMgr.getMenu();
        final Currency myDefault = Currency.getInstance("GBP");
        myMenu.addItem(myDefault, "Pounds");
        myMenu.addItem(Currency.getInstance("USD"), "Dollars");
        myMenu.addItem(Currency.getInstance("JPY"), "Yen");
        myCurrencyMgr.setValue(myDefault, "Pounds");
        setCurrency(myDefault);
        myCurrencyMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setCurrency(e.getDetails(Currency.class)));

        /* Create an HBox for buttons */
        final TethysSwingBoxPaneManager myBox = theGuiFactory.newHBoxPane();
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
        theListField.setEditable(pDoEdit);
        theIconField.setEditable(pDoEdit);
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
    private void processActionEvent(final TethysSwingDataTextField<?> pField,
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
    private static void configureCmdMenu(final TethysSwingDataTextField<?> pField) {
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
