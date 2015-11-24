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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.Currency;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import net.sourceforge.jdatebutton.javafx.ArrowIcon;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDataEditTextFieldBase;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollContextMenu.TethysFXContextEvent;

/**
 * Generic class for displaying and editing a data field.
 */
public abstract class TethysFXDataEditField {
    /**
     * Private constructor.
     */
    private TethysFXDataEditField() {
    }

    /**
     * DataEditTextField class.
     * @param <T> the data type
     */
    public abstract static class TethysFXDataEditTextField<T>
            extends TethysDataEditTextFieldBase<T, Node, Node> {
        /**
         * The error style class.
         */
        private static final String STYLE_ERROR = "-jtethys-datafield-error";

        /**
         * The padding size to expand a label to match a TextField.
         */
        private static final int PADDING = 4;

        /**
         * The node.
         */
        private final BorderPane theNode;

        /**
         * The text field.
         */
        private final TextField theEditNode;

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
         * The error text.
         */
        private String theErrorText;

        /**
         * Constructor.
         * @param pConverter the data converter
         */
        protected TethysFXDataEditTextField(final TethysDataEditConverter<T> pConverter) {
            /* Call super-constructor and store parameters */
            super(pConverter);

            /* Create resources */
            theNode = new BorderPane();
            theLabel = new Label();
            theEditNode = new TextField();

            /* Create the command button */
            theCmdButton = new Button();
            theCmdButton.setGraphic(ArrowIcon.DOWN.getArrow());
            theCmdButton.setFocusTraversable(false);

            /* declare the menu */
            declareMenu(new TethysFXScrollContextMenu<String>());

            /* Set maximum widths for fields */
            theLabel.setMaxWidth(Integer.MAX_VALUE);
            theLabel.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
            theEditNode.setMaxWidth(Integer.MAX_VALUE);

            /* Set alignment */
            Pos myAlignment = pConverter.rightAlignFields()
                                                            ? Pos.CENTER_RIGHT
                                                            : Pos.CENTER_LEFT;
            theLabel.setAlignment(myAlignment);
            theEditNode.setAlignment(myAlignment);

            /* Default to readOnly */
            theNode.setCenter(theLabel);

            /* Add listener to handle change of focus */
            theEditNode.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(final ObservableValue<? extends Boolean> observable,
                                    final Boolean oldValue,
                                    final Boolean newValue) {
                    if (newValue) {
                        handleFocusGained();
                    } else {
                        processValue();
                        if (theErrorText == null) {
                            handleFocusLost();
                        }
                    }
                }
            });

            /* handle enter/escape keys */
            theEditNode.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(final KeyEvent t) {
                    switch (t.getCode()) {
                        case ENTER:
                            processValue();
                            break;
                        case ESCAPE:
                            theEditNode.setText(getEditText());
                            clearError();
                            break;
                        default:
                            break;
                    }
                }
            });

            /* handle command button action */
            theCmdButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent t) {
                    handleMenuRequest();
                }
            });

            /* Set context menu listener */
            getMenu().addEventHandler(TethysFXContextEvent.MENU_SELECT, new EventHandler<TethysFXContextEvent<?>>() {
                @Override
                public void handle(final TethysFXContextEvent<?> e) {
                    /* Handle the close of the menu */
                    handleMenuClosed();
                }
            });
        }

        @Override
        public TethysFXScrollContextMenu<String> getMenu() {
            return (TethysFXScrollContextMenu<String>) super.getMenu();
        }

        @Override
        protected void showMenu() {
            getMenu().showMenuAtPosition(theCmdButton, Side.RIGHT);
        }

        /**
         * Process value.
         */
        private void processValue() {
            /* If we failed to process the value */
            String myText = theEditNode.getText();
            if (!processValue(myText)) {
                /* Set toolTip, save error text and retain the focus */
                theEditNode.setTooltip(new Tooltip(TOOLTIP_BAD_VALUE));
                theErrorText = myText;
                theEditNode.requestFocus();

                /* add an error style */
                ObservableList<String> myStyles = theEditNode.getStyleClass();
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
            theEditNode.setTooltip(null);
            theErrorText = null;
            ObservableList<String> myStyles = theEditNode.getStyleClass();
            myStyles.remove(STYLE_ERROR);
        }

        @Override
        public Node getNode() {
            return theNode;
        }

        /**
         * Set the font.
         * @param pFont the font for the field
         */
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            theLabel.setFont(pFont);
            theEditNode.setFont(pFont);
        }

        /**
         * Set the textFill colour.
         * @param pColor the colour
         */
        public void setTextFill(final Color pColor) {
            /* Apply font to the two nodes */
            theLabel.setTextFill(pColor);
            theEditNode.setStyle("-fx-text-inner-color: " + TethysFXGuiUtils.colorToHexString(pColor));
        }

        /**
         * Show the command button.
         * @param pShow true/false
         */
        public void showCommandButton(final boolean pShow) {
            /* Remove any button that is displaying */
            theNode.setRight(null);
            doShowCmdButton = pShow;

            /* If we have a button to display */
            if (isEditable() && doShowCmdButton) {
                theNode.setRight(theCmdButton);
            }
        }

        /**
         * Handle focusGained.
         */
        private void handleFocusGained() {
            theEditNode.setText(theErrorText == null
                                                     ? getEditText()
                                                     : theErrorText);
            theEditNode.selectAll();
        }

        /**
         * Handle focusLost.
         */
        private void handleFocusLost() {
            theEditNode.setText(getDisplayText());
        }

        @Override
        public void setValue(final T pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Update nodes */
            theLabel.setText(getDisplayText());
            theEditNode.setText(theEditNode.isFocused()
                                                        ? getEditText()
                                                        : getDisplayText());
        }

        @Override
        public void setEditable(final boolean pEditable) {
            /* Obtain current setting */
            boolean isEditable = isEditable();

            /* If we are changing */
            if (pEditable != isEditable) {
                /* If we are setting editable */
                if (pEditable) {
                    theNode.setCenter(theEditNode);
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
    }

    /**
     * StringFXTextField class.
     */
    public static class TethysFXStringTextField
            extends TethysFXDataEditTextField<String> {
        /**
         * Constructor.
         */
        public TethysFXStringTextField() {
            super(new TethysStringEditConverter());
        }
    }

    /**
     * ShortFXTextField class.
     */
    public static class TethysFXShortTextField
            extends TethysFXDataEditTextField<Short> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXShortTextField(final TethysDecimalFormatter pFormatter,
                                      final TethysDecimalParser pParser) {
            super(new TethysShortEditConverter(pFormatter, pParser));
        }
    }

    /**
     * IntegerFXTextField class.
     */
    public static class TethysFXIntegerTextField
            extends TethysFXDataEditTextField<Integer> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXIntegerTextField(final TethysDecimalFormatter pFormatter,
                                        final TethysDecimalParser pParser) {
            super(new TethysIntegerEditConverter(pFormatter, pParser));
        }
    }

    /**
     * LongFXTextField class.
     */
    public static class TethysFXLongTextField
            extends TethysFXDataEditTextField<Long> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXLongTextField(final TethysDecimalFormatter pFormatter,
                                     final TethysDecimalParser pParser) {
            super(new TethysLongEditConverter(pFormatter, pParser));
        }
    }

    /**
     * MoneyFXTextField base class.
     * @param <T> the data type
     */
    protected abstract static class TethysFXMoneyTextFieldBase<T extends TethysMoney>
            extends TethysFXDataEditTextField<T> {
        /**
         * Constructor.
         * @param pConverter the converter
         */
        public TethysFXMoneyTextFieldBase(final TethysMoneyEditConverterBase<T> pConverter) {
            super(pConverter);
        }

        @Override
        protected TethysMoneyEditConverterBase<T> getConverter() {
            return (TethysMoneyEditConverterBase<T>) super.getConverter();
        }

        /**
         * Set deemed currency.
         * @param pCurrency the deemed currency
         */
        public void setDeemedCurrency(final Currency pCurrency) {
            getConverter().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * MoneyFXTextField class.
     */
    public static class TethysFXMoneyTextField
            extends TethysFXMoneyTextFieldBase<TethysMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXMoneyTextField(final TethysDecimalFormatter pFormatter,
                                      final TethysDecimalParser pParser) {
            super(new TethysMoneyEditConverter(pFormatter, pParser));
        }
    }

    /**
     * PriceFXTextField class.
     */
    public static class TethysFXPriceTextField
            extends TethysFXMoneyTextFieldBase<TethysPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXPriceTextField(final TethysDecimalFormatter pFormatter,
                                      final TethysDecimalParser pParser) {
            super(new TethysPriceEditConverter(pFormatter, pParser));
        }
    }

    /**
     * DilutedPriceFXTextField class.
     */
    public static class TethysFXDilutedPriceTextField
            extends TethysFXMoneyTextFieldBase<TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXDilutedPriceTextField(final TethysDecimalFormatter pFormatter,
                                             final TethysDecimalParser pParser) {
            super(new TethysDilutedPriceEditConverter(pFormatter, pParser));
        }
    }

    /**
     * RateFXTextField class.
     */
    public static class TethysFXRateTextField
            extends TethysFXDataEditTextField<TethysRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXRateTextField(final TethysDecimalFormatter pFormatter,
                                     final TethysDecimalParser pParser) {
            super(new TethysRateEditConverter(pFormatter, pParser));
        }
    }

    /**
     * UnitsFXTextField class.
     */
    public static class TethysFXUnitsTextField
            extends TethysFXDataEditTextField<TethysUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXUnitsTextField(final TethysDecimalFormatter pFormatter,
                                      final TethysDecimalParser pParser) {
            super(new TethysUnitsEditConverter(pFormatter, pParser));
        }
    }

    /**
     * DilutionFXTextField class.
     */
    public static class TethysFXDilutionTextField
            extends TethysFXDataEditTextField<TethysDilution> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXDilutionTextField(final TethysDecimalFormatter pFormatter,
                                         final TethysDecimalParser pParser) {
            super(new TethysDilutionEditConverter(pFormatter, pParser));
        }
    }

    /**
     * RatioFXTextField class.
     */
    public static class TethysFXRatioTextField
            extends TethysFXDataEditTextField<TethysRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysFXRatioTextField(final TethysDecimalFormatter pFormatter,
                                      final TethysDecimalParser pParser) {
            super(new TethysRatioEditConverter(pFormatter, pParser));
        }
    }
}
