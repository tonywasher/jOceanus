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
package net.sourceforge.joceanus.jtethys.ui.javafx.field;

import java.util.Currency;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javafx.css.PseudoClass;
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

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreCharArrayEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreDilutedPriceEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreDilutionEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreIntegerEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreLongEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreMoneyEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreMoneyEditConverterBase;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICorePriceEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreRateEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreRatioEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreRawDecimalEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreShortEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreStringEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreUnitsEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditField;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUIDataEditTextFieldControl;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.menu.TethysUIFXScrollMenu;

/**
 * Generic class for displaying and editing a data field.
 *
 * @param <T> the data type
 */
public abstract class TethysUIFXDataTextField<T>
        extends TethysUICoreDataEditField<T> {
    /**
     * The padding size to expand a label to match a TextField/Button.
     */
    static final int PADDING = 4;

    /**
     * The dataField style.
     */
    private static final String STYLE_FIELD = TethysUIFXUtils.CSS_STYLE_BASE + "-datafield";

    /**
     * The numeric style class.
     */
    private static final String STYLE_NUMERIC = STYLE_FIELD + "-numeric";

    /**
     * The selected style class.
     */
    private static final String STYLE_SELECTED = STYLE_FIELD + "-selected";

    /**
     * The changed style class.
     */
    private static final String STYLE_CHANGED = STYLE_FIELD + "-changed";

    /**
     * The disabled style class.
     */
    private static final String STYLE_DISABLED = STYLE_FIELD + "-disabled";

    /**
     * The error style class.
     */
    static final String STYLE_ERROR = STYLE_FIELD + "-error";

    /**
     * The alternate style class.
     */
    private static final String STYLE_ALTERNATE = STYLE_FIELD + "-alternate";

    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The pane.
     */
    private final BorderPane thePane;

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
     *
     * @param pFactory     the GUI factory
     * @param pEditControl the edit Control
     */
    TethysUIFXDataTextField(final TethysUICoreFactory<?> pFactory,
                            final Node pEditControl) {
        /* Initialise the underlying class */
        super(pFactory);

        /* Create resources */
        thePane = new BorderPane();
        theNode = new TethysUIFXNode(thePane);
        theLabel = new Label();
        theEditControl = (Control) pEditControl;

        /* Declare the label and edit control to be dataField */
        theLabel.getStyleClass().add(TethysUIFXUtils.CSS_STYLE_BASE);
        theLabel.getStyleClass().add(STYLE_FIELD);
        theEditControl.getStyleClass().add(TethysUIFXUtils.CSS_STYLE_BASE);
        theEditControl.getStyleClass().add(STYLE_FIELD);

        /* Set maximum widths for fields */
        theLabel.setMaxWidth(Integer.MAX_VALUE);
        theEditControl.setMaxWidth(Integer.MAX_VALUE);

        /* Create the command button */
        theCmdButton = new Button();
        theCmdButton.setGraphic(TethysUIFXArrowIcon.DOWN.getArrow());
        theCmdButton.setFocusTraversable(false);

        /* declare the menu */
        declareCmdMenu(pFactory.menuFactory().newContextMenu());

        /* Default to readOnly */
        thePane.setCenter(theLabel);

        /* handle command button action */
        theCmdButton.setOnAction(e -> handleCmdMenuRequest());

        /* Set context menu listener */
        getCmdMenu().getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> handleCmdMenuClosed());
    }

    @Override
    protected TethysUIFXScrollMenu<String> getCmdMenu() {
        return (TethysUIFXScrollMenu<String>) super.getCmdMenu();
    }

    /**
     * Obtain the label.
     *
     * @return the label
     */
    Label getLabel() {
        return theLabel;
    }

    /**
     * Obtain the editControl.
     *
     * @return the editControl
     */
    protected Control getEditControl() {
        return theEditControl;
    }

    @Override
    protected void showCmdMenu() {
        getCmdMenu().showMenuAtPosition(theCmdButton, Side.RIGHT);
    }

    @Override
    public TethysUIFXNode getNode() {
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
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        thePane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        thePane.setPrefHeight(pHeight);
    }

    @Override
    public void showCmdButton(final boolean pShow) {
        /* Remove any button that is displaying */
        thePane.setRight(null);
        doShowCmdButton = pShow;

        /* If we have a button to display */
        if (isEditable() && doShowCmdButton) {
            thePane.setRight(theCmdButton);
        }
    }

    @Override
    public void setEditable(final boolean pEditable) {
        /* Obtain current setting */
        final boolean isEditable = isEditable();

        /* If we are changing */
        if (pEditable != isEditable) {
            /* If we are setting editable */
            if (pEditable) {
                thePane.setCenter(theEditControl);
                if (doShowCmdButton) {
                    thePane.setRight(theCmdButton);
                }
            } else {
                thePane.setCenter(theLabel);
                thePane.setRight(null);
            }

            /* Pass call on */
            super.setEditable(pEditable);
        }
    }

    /**
     * Start cell editing.
     *
     * @param pCell the cell
     */
    public abstract void startCellEditing(Node pCell);

    @Override
    public void setTheAttribute(final TethysUIFieldAttribute pAttr) {
        if (!isAttributeSet(pAttr)) {
            super.setTheAttribute(pAttr);
            theLabel.getStyleClass().add(getStyleForAttribute(pAttr));
        }
    }

    @Override
    public void clearTheAttribute(final TethysUIFieldAttribute pAttr) {
        if (isAttributeSet(pAttr)) {
            super.clearTheAttribute(pAttr);
            theLabel.getStyleClass().remove(getStyleForAttribute(pAttr));
        }
    }

    @Override
    public void adjustField() {
        /* Do nothing - already done */
    }

    /**
     * Obtain the style-class for the attribute.
     *
     * @param pAttr the attribute
     * @return the style class
     */
    private static String getStyleForAttribute(final TethysUIFieldAttribute pAttr) {
        switch (pAttr) {
            case NUMERIC:
                return STYLE_NUMERIC;
            case SELECTED:
                return STYLE_SELECTED;
            case CHANGED:
                return STYLE_CHANGED;
            case DISABLED:
                return STYLE_DISABLED;
            case ALTERNATE:
            default:
                return STYLE_ALTERNATE;
        }
    }

    /**
     * TextField class.
     *
     * @param <T> the data type
     */
    public abstract static class TethysUIFXTextEditField<T>
            extends TethysUIFXDataTextField<T>
            implements TethysUIValidatedEditField<T> {
        /**
         * The error pseudoClass.
         */
        private static final PseudoClass ERROR_CLASS = PseudoClass.getPseudoClass(STYLE_ERROR);

        /**
         * The converterControl.
         */
        private final TethysUIDataEditTextFieldControl<T> theControl;

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
         *
         * @param pFactory   the GUI factory
         * @param pConverter the text converter
         */
        TethysUIFXTextEditField(final TethysUICoreFactory<?> pFactory,
                                final TethysUICoreDataEditConverter<T> pConverter) {
            /* Initialise underlying class */
            super(pFactory, new TextField());

            /* Create the converter control */
            theControl = new TethysUIDataEditTextFieldControl<>(this, pConverter);

            /* Access the fields */
            final Label myLabel = getLabel();
            theTextField = getEditControl();

            /* Set Padding */
            myLabel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

            /* Set alignment */
            final Pos myAlignment = pConverter.rightAlignFields()
                    ? Pos.CENTER_RIGHT
                    : Pos.CENTER_LEFT;
            myLabel.setAlignment(myAlignment);
            theTextField.setAlignment(myAlignment);

            /* Add listener to handle change of focus */
            theTextField.focusedProperty().addListener((v, o, n) -> handleFocusChange(n));

            /* handle enter/escape keys */
            theTextField.setOnKeyPressed(this::handleKeyPressed);
        }

        @Override
        public void setValidator(final Function<T, String> pValidator) {
            theControl.setValidator(pValidator);
        }

        @Override
        public void setReporter(final Consumer<String> pReporter) {
            theControl.setReporter(pReporter);
        }

        /**
         * handle focusChange.
         *
         * @param pFocused is the field focused?
         */
        private void handleFocusChange(final boolean pFocused) {
            if (pFocused) {
                handleFocusGained();
            } else {
                handleEscapeKey();
            }
        }

        /**
         * handle keyPressed.
         *
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
            if (!isAttributeSet(TethysUIFieldAttribute.ERROR)) {
                haltCellEditing();
            }
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
                    fireEvent(TethysUIEvent.EDITFOCUSLOST);
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
         *
         * @return the converter.
         */
        protected TethysUICoreDataEditConverter<T> getConverter() {
            return theControl.getConverter();
        }

        /**
         * Process value.
         */
        private void processValue() {
            /* If we failed to process the value */
            final String myText = theTextField.getText();
            if (!theControl.processValue(myText)) {
                /* Set toolTip, save error text and retain the focus */
                theTextField.setTooltip(new Tooltip(theControl.getErrorText()));
                theErrorText = myText;

                /* add an error style */
                theTextField.pseudoClassStateChanged(ERROR_CLASS, true);
                setTheAttribute(TethysUIFieldAttribute.ERROR);
                theTextField.requestFocus();

                /* else value was OK */
            } else {
                /* Clear error indications */
                clearError();
                getLabel().setText(theControl.getDisplayText());
            }
        }

        /**
         * Clear error indication.
         */
        private void clearError() {
            theTextField.setTooltip(null);
            theErrorText = null;
            theTextField.pseudoClassStateChanged(ERROR_CLASS, false);
            clearTheAttribute(TethysUIFieldAttribute.ERROR);
        }

        /**
         * Handle focusGained.
         */
        void handleFocusGained() {
            theTextField.setText(theErrorText == null
                    ? theControl.getEditText()
                    : theErrorText);
            theTextField.selectAll();
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
    public static class TethysUIFXStringTextField
            extends TethysUIFXTextEditField<String> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXStringTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreStringEditConverter());
        }
    }

    /**
     * FXCharArrayTextField class.
     */
    public static class TethysUIFXCharArrayTextField
            extends TethysUIFXTextEditField<char[]> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXCharArrayTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreCharArrayEditConverter());
        }
    }

    /**
     * FXShortTextField class.
     */
    public static class TethysUIFXShortTextField
            extends TethysUIFXTextEditField<Short> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXShortTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreShortEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }
    }

    /**
     * FXIntegerTextField class.
     */
    public static class TethysUIFXIntegerTextField
            extends TethysUIFXTextEditField<Integer> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXIntegerTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreIntegerEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }
    }

    /**
     * FXLongTextField class.
     */
    public static class TethysUIFXLongTextField
            extends TethysUIFXTextEditField<Long> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXLongTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreLongEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }
    }

    /**
     * FXRawDecimalTextField class.
     */
    public static class TethysUIFXRawDecimalTextField
            extends TethysUIFXTextEditField<TethysDecimal>
            implements TethysUIRawDecimalEditField {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXRawDecimalTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreRawDecimalEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }

        @Override
        protected TethysUICoreRawDecimalEditConverter getConverter() {
            return (TethysUICoreRawDecimalEditConverter) super.getConverter();
        }

        @Override
        public void setNumDecimals(final IntSupplier pSupplier) {
            getConverter().setNumDecimals(pSupplier);
        }
    }

    /**
     * CurrencyFXTextField base class.
     *
     * @param <T> the data type
     */
    protected abstract static class TethysUIFXCurrencyTextFieldBase<T extends TethysMoney>
            extends TethysUIFXTextEditField<T>
            implements TethysUICurrencyEditField<T> {
        /**
         * Constructor.
         *
         * @param pFactory   the GUI factory
         * @param pConverter the converter
         */
        TethysUIFXCurrencyTextFieldBase(final TethysUICoreFactory<?> pFactory,
                                        final TethysUICoreMoneyEditConverterBase<T> pConverter) {
            super(pFactory, pConverter);
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }

        @Override
        protected TethysUICoreMoneyEditConverterBase<T> getConverter() {
            return (TethysUICoreMoneyEditConverterBase<T>) super.getConverter();
        }

        @Override
        public void setDeemedCurrency(final Supplier<Currency> pSupplier) {
            getConverter().setDeemedCurrency(pSupplier);
        }
    }

    /**
     * FXMoneyTextField class.
     */
    public static class TethysUIFXMoneyTextField
            extends TethysUIFXCurrencyTextFieldBase<TethysMoney> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXMoneyTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreMoneyEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXPriceTextField class.
     */
    public static class TethysUIFXPriceTextField
            extends TethysUIFXCurrencyTextFieldBase<TethysPrice> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXPriceTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICorePriceEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXDilutedPriceTextField class.
     */
    public static class TethysUIFXDilutedPriceTextField
            extends TethysUIFXCurrencyTextFieldBase<TethysDilutedPrice> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXDilutedPriceTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreDilutedPriceEditConverter(pFactory.getDataFormatter()));
        }
    }

    /**
     * FXRateTextField class.
     */
    public static class TethysUIFXRateTextField
            extends TethysUIFXTextEditField<TethysRate> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXRateTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreRateEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }
    }

    /**
     * FXUnitsTextField class.
     */
    public static class TethysUIFXUnitsTextField
            extends TethysUIFXTextEditField<TethysUnits> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXUnitsTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreUnitsEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }
    }

    /**
     * FXDilutionTextField class.
     */
    public static class TethysUIFXDilutionTextField
            extends TethysUIFXTextEditField<TethysDilution> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXDilutionTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreDilutionEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }
    }

    /**
     * FXRatioTextField class.
     */
    public static class TethysUIFXRatioTextField
            extends TethysUIFXTextEditField<TethysRatio> {
        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXRatioTextField(final TethysUICoreFactory<?> pFactory) {
            super(pFactory, new TethysUICoreRatioEditConverter(pFactory.getDataFormatter()));
            super.setTheAttribute(TethysUIFieldAttribute.NUMERIC);
        }
    }
}
