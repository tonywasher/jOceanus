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

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import net.sourceforge.jdatebutton.javafx.ArrowIcon;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 */
public abstract class TethysFXDataTextField<T>
        extends TethysDataEditField<T, Node, Node> {
    /**
     * The padding size to expand a label to match a TextField/Button.
     */
    protected static final int PADDING = 4;

    /**
     * The error style class.
     */
    private static final String STYLE_ERROR = "-jtethys-datafield-error";

    /**
     * The node.
     */
    private final BorderPane theNode;

    /**
     * The edit Control.
     */
    private final Control theEditControl;

    /**
     * The label.
     */
    private final Label theLabel;

    /**
     * The command button.
     */
    private final Button theCmdButton;

    /**
     * Do we show the command button?
     */
    private boolean doShowCmdButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditControl the edit Control
     */
    protected TethysFXDataTextField(final TethysFXGuiFactory pFactory,
                                    final Node pEditControl) {
        /* Initialise the underlying class */
        super(pFactory);

        /* Create resources */
        theNode = new BorderPane();
        theLabel = new Label();
        theEditControl = (Control) pEditControl;

        /* Set maximum widths for fields */
        theLabel.setMaxWidth(Integer.MAX_VALUE);
        theEditControl.setMaxWidth(Integer.MAX_VALUE);

        /* Create the command button */
        theCmdButton = new Button();
        theCmdButton.setGraphic(ArrowIcon.DOWN.getArrow());
        theCmdButton.setFocusTraversable(false);

        /* declare the menu */
        declareCmdMenu(new TethysFXScrollContextMenu<String>());

        /* Default to readOnly */
        theNode.setCenter(theLabel);

        /* handle command button action */
        theCmdButton.setOnAction(e -> handleCmdMenuRequest());

        /* Set context menu listener */
        getCmdMenu().addEventHandler(TethysFXContextEvent.MENU_SELECT, e -> handleCmdMenuClosed());
    }

    /**
     * Obtain the label.
     * @return the label
     */
    protected Label getLabel() {
        return theLabel;
    }

    /**
     * Obtain the editControl.
     * @return the editControl
     */
    protected Control getEditControl() {
        return theEditControl;
    }

    @Override
    public TethysFXScrollContextMenu<String> getCmdMenu() {
        return (TethysFXScrollContextMenu<String>) super.getCmdMenu();
    }

    @Override
    protected void showCmdMenu() {
        getCmdMenu().showMenuAtPosition(theCmdButton, Side.RIGHT);
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* Apply to the nodes */
        theLabel.setDisable(!pEnabled);
        theEditControl.setDisable(!pEnabled);
        theCmdButton.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    // @Override
    // public void setTextFill(final Color pColor) {
    // /* Apply font to the two nodes */
    // theLabel.setTextFill(pColor);
    // theEditControl.setStyle("-fx-text-inner-color: " +
    // TethysFXGuiUtils.colorToHexString(pColor));
    // }

    // @Override
    // public void setBackground(final Color pColor) {
    // /* Apply colour to the label only */
    // theLabel.setStyle("-fx-background-color: " + TethysFXGuiUtils.colorToHexString(pColor));
    // }

    @Override
    public void showCmdButton(final boolean pShow) {
        /* Remove any button that is displaying */
        theNode.setRight(null);
        doShowCmdButton = pShow;

        /* If we have a button to display */
        if (isEditable() && doShowCmdButton) {
            theNode.setRight(theCmdButton);
        }
    }

    @Override
    public void setEditable(final boolean pEditable) {
        /* Obtain current setting */
        boolean isEditable = isEditable();

        /* If we are changing */
        if (pEditable != isEditable) {
            /* If we are setting editable */
            if (pEditable) {
                theNode.setCenter(theEditControl);
                if (doShowCmdButton) {
                    theNode.setRight(theCmdButton);
                }
            } else {
                theNode.setCenter(theLabel);
                theNode.setRight(null);
            }

            /* Pass call on */
            super.setEditable(pEditable);
        }
    }

    /**
     * Start cell editing.
     * @param pCell the cell
     */
    public abstract void startCellEditing(final Node pCell);

    /**
     * TextField class.
     * @param <T> the data type
     */
    public abstract static class TethysFXTextEditField<T>
            extends TethysFXDataTextField<T> {
        /**
         * The converterControl.
         */
        private final TethysDataEditTextFieldControl<T> theControl;

        /**
         * The textField.
         */
        private final TextField theTextField;

        /**
         * The error text.
         */
        private String theErrorText;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pConverter the text converter
         */
        protected TethysFXTextEditField(final TethysFXGuiFactory pFactory,
                                        final TethysDataEditConverter<T> pConverter) {
            /* Initialise underlying class */
            super(pFactory, new TextField());

            /* Create the converter control */
            theControl = new TethysDataEditTextFieldControl<>(this, pConverter);

            /* Access the fields */
            Label myLabel = getLabel();
            theTextField = getEditControl();

            /* Set Padding */
            myLabel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

            /* Set alignment */
            Pos myAlignment = pConverter.rightAlignFields()
                                                            ? Pos.CENTER_RIGHT
                                                            : Pos.CENTER_LEFT;
            myLabel.setAlignment(myAlignment);
            theTextField.setAlignment(myAlignment);

            /* Add listener to handle change of focus */
            theTextField.focusedProperty().addListener((v, o, n) -> handleFocusChange(n));

            /* handle enter/escape keys */
            theTextField.setOnKeyPressed(this::handleKeyPressed);
        }

        /**
         * handle focusChange.
         * @param pFocused is the field focused?
         */
        private void handleFocusChange(final boolean pFocused) {
            if (pFocused) {
                handleFocusGained();
            } else {
                processValue();
                if (theErrorText == null) {
                    handleFocusLost();
                }
            }
        }

        /**
         * handle keyPressed.
         * @param pEvent the event
         */
        private void handleKeyPressed(final KeyEvent pEvent) {
            switch (pEvent.getCode()) {
                case ENTER:
                    handleEnterKey();
                    break;
                case ESCAPE:
                    handleEscapeKey();
                    break;
                default:
                    break;
            }
        }

        /**
         * handle enterKey.
         */
        private void handleEnterKey() {
            processValue();
            haltCellEditing();
        }

        /**
         * handle escapeKey.
         */
        private void handleEscapeKey() {
            theTextField.setText(theControl.getEditText());
            clearError();
            haltCellEditing();
        }

        @Override
        public void startCellEditing(final Node pCell) {
            isCellEditing = true;
            setEditable(true);
            theControl.clearNewValue();
            theTextField.requestFocus();
        }

        /**
         * Halt cell editing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                setEditable(false);
                if (!theControl.parsedNewValue()) {
                    fireEvent(TethysUIEvent.WINDOWCLOSED);
                }
            }
            isCellEditing = false;
        }

        @Override
        protected TextField getEditControl() {
            return (TextField) super.getEditControl();
        }

        /**
         * Obtain the converter.
         * @return the converter.
         */
        protected TethysDataEditConverter<T> getConverter() {
            return theControl.getConverter();
        }

        // @Override
        // public void setFont(final Font pFont) {
        // /* Apply font to the two nodes */
        // getLabel().setFont(pFont);
        // theTextField.setFont(pFont);
        // }

        /**
         * Process value.
         */
        private void processValue() {
            /* If we failed to process the value */
            String myText = theTextField.getText();
            if (!theControl.processValue(myText)) {
                /* Set toolTip, save error text and retain the focus */
                theTextField.setTooltip(new Tooltip(TOOLTIP_BAD_VALUE));
                theErrorText = myText;
                theTextField.requestFocus();

                /* add an error style */
                ObservableList<String> myStyles = theTextField.getStyleClass();
                if (!myStyles.contains(STYLE_ERROR)) {
                    myStyles.add(STYLE_ERROR);
                }

                /* else value was OK */
            } else {
                /* Clear error indications */
                clearError();
            }
        }

        /**
         * Clear error indication.
         */
        private void clearError() {
            theTextField.setTooltip(null);
            theErrorText = null;
            ObservableList<String> myStyles = theTextField.getStyleClass();
            myStyles.remove(STYLE_ERROR);
        }

        /**
         * Handle focusGained.
         */
        protected void handleFocusGained() {
            theTextField.setText(theErrorText == null
                                                      ? theControl.getEditText()
                                                      : theErrorText);
            theTextField.selectAll();
        }

        /**
         * Handle focusLost.
         */
        private void handleFocusLost() {
            theTextField.setText(theControl.getDisplayText());
            haltCellEditing();
        }

        @Override
        public void setValue(final T pValue) {
            /* Store the value */
            theControl.setValue(pValue);

            /* Update nodes */
            getLabel().setText(theControl.getDisplayText());
            theTextField.setText(theTextField.isFocused()
                                                          ? theControl.getEditText()
                                                          : theControl.getDisplayText());
        }
    }

    /**
     * FXStringTextField class.
     */
    public static class TethysFXStringTextField
            extends TethysFXTextEditField<String> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXStringTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysStringEditConverter());
        }
    }

    /**
     * FXShortTextField class.
     */
    public static class TethysFXShortTextField
            extends TethysFXTextEditField<Short> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXShortTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysShortEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXIntegerTextField class.
     */
    public static class TethysFXIntegerTextField
            extends TethysFXTextEditField<Integer> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXIntegerTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysIntegerEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXLongTextField class.
     */
    public static class TethysFXLongTextField
            extends TethysFXTextEditField<Long> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXLongTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysLongEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * CurrencyFXTextField base class.
     * @param <T> the data type
     */
    protected abstract static class TethysFXCurrencyTextFieldBase<T extends TethysMoney>
            extends TethysFXTextEditField<T>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pConverter the converter
         */
        protected TethysFXCurrencyTextFieldBase(final TethysFXGuiFactory pFactory,
                                                final TethysMoneyEditConverterBase<T> pConverter) {
            super(pFactory, pConverter);
        }

        @Override
        protected TethysMoneyEditConverterBase<T> getConverter() {
            return (TethysMoneyEditConverterBase<T>) super.getConverter();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getConverter().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * FXMoneyTextField class.
     */
    public static class TethysFXMoneyTextField
            extends TethysFXCurrencyTextFieldBase<TethysMoney> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXMoneyTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysMoneyEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXPriceTextField class.
     */
    public static class TethysFXPriceTextField
            extends TethysFXCurrencyTextFieldBase<TethysPrice> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXPriceTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysPriceEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXDilutedPriceTextField class.
     */
    public static class TethysFXDilutedPriceTextField
            extends TethysFXCurrencyTextFieldBase<TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXDilutedPriceTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysDilutedPriceEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXRateTextField class.
     */
    public static class TethysFXRateTextField
            extends TethysFXTextEditField<TethysRate> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXRateTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysRateEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXUnitsTextField class.
     */
    public static class TethysFXUnitsTextField
            extends TethysFXTextEditField<TethysUnits> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXUnitsTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysUnitsEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXDilutionTextField class.
     */
    public static class TethysFXDilutionTextField
            extends TethysFXTextEditField<TethysDilution> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXDilutionTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysDilutionEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXRatioTextField class.
     */
    public static class TethysFXRatioTextField
            extends TethysFXTextEditField<TethysRatio> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXRatioTextField(final TethysFXGuiFactory pFactory) {
            super(pFactory, new TethysRatioEditConverter(pFactory.getDataFormatter()));
        }
    }
}
