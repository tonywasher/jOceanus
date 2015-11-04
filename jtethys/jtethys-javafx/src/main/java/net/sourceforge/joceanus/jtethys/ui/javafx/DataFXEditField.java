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
import net.sourceforge.joceanus.jtethys.decimal.JDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jtethys.javafx.GuiUtils;
import net.sourceforge.joceanus.jtethys.ui.DataEditField.DataEditTextFieldBase;
import net.sourceforge.joceanus.jtethys.ui.javafx.ScrollFXContextMenu.ContextEvent;

/**
 * Generic class for displaying and editing a data field.
 */
public abstract class DataFXEditField {
    /**
     * Private constructor.
     */
    private DataFXEditField() {
    }

    /**
     * DataEditTextField class.
     * @param <T> the data type
     */
    public abstract static class DataFXEditTextField<T>
            extends DataEditTextFieldBase<T, Node, Node> {
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
        protected DataFXEditTextField(final DataEditConverter<T> pConverter) {
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
            declareMenu(new ScrollFXContextMenu<String>());

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
            getMenu().addEventHandler(ContextEvent.MENU_SELECT, new EventHandler<ContextEvent<?>>() {
                @Override
                public void handle(final ContextEvent<?> e) {
                    /* Handle the close of the menu */
                    handleMenuClosed();
                }
            });
        }

        @Override
        public ScrollFXContextMenu<String> getMenu() {
            return (ScrollFXContextMenu<String>) super.getMenu();
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
            theEditNode.setStyle("-fx-text-inner-color: " + GuiUtils.colorToHexString(pColor));
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
    public static class StringFXTextField
            extends DataFXEditTextField<String> {
        /**
         * Constructor.
         */
        public StringFXTextField() {
            super(new StringEditConverter());
        }
    }

    /**
     * ShortFXTextField class.
     */
    public static class ShortFXTextField
            extends DataFXEditTextField<Short> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public ShortFXTextField(final JDecimalFormatter pFormatter,
                                final JDecimalParser pParser) {
            super(new ShortEditConverter(pFormatter, pParser));
        }
    }

    /**
     * IntegerFXTextField class.
     */
    public static class IntegerFXTextField
            extends DataFXEditTextField<Integer> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public IntegerFXTextField(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
            super(new IntegerEditConverter(pFormatter, pParser));
        }
    }

    /**
     * LongFXTextField class.
     */
    public static class LongFXTextField
            extends DataFXEditTextField<Long> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public LongFXTextField(final JDecimalFormatter pFormatter,
                               final JDecimalParser pParser) {
            super(new LongEditConverter(pFormatter, pParser));
        }
    }

    /**
     * MoneyFXTextField base class.
     * @param <T> the data type
     */
    protected abstract static class MoneyFXTextFieldBase<T extends JMoney>
            extends DataFXEditTextField<T> {
        /**
         * Constructor.
         * @param pConverter the converter
         */
        public MoneyFXTextFieldBase(final MoneyEditConverterBase<T> pConverter) {
            super(pConverter);
        }

        @Override
        protected MoneyEditConverterBase<T> getConverter() {
            return (MoneyEditConverterBase<T>) super.getConverter();
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
    public static class MoneyFXTextField
            extends MoneyFXTextFieldBase<JMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public MoneyFXTextField(final JDecimalFormatter pFormatter,
                                final JDecimalParser pParser) {
            super(new MoneyEditConverter(pFormatter, pParser));
        }
    }

    /**
     * PriceFXTextField class.
     */
    public static class PriceFXTextField
            extends MoneyFXTextFieldBase<JPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public PriceFXTextField(final JDecimalFormatter pFormatter,
                                final JDecimalParser pParser) {
            super(new PriceEditConverter(pFormatter, pParser));
        }
    }

    /**
     * DilutedPriceFXTextField class.
     */
    public static class DilutedPriceFXTextField
            extends MoneyFXTextFieldBase<JDilutedPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public DilutedPriceFXTextField(final JDecimalFormatter pFormatter,
                                       final JDecimalParser pParser) {
            super(new DilutedPriceEditConverter(pFormatter, pParser));
        }
    }

    /**
     * RateFXTextField class.
     */
    public static class RateFXTextField
            extends DataFXEditTextField<JRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public RateFXTextField(final JDecimalFormatter pFormatter,
                               final JDecimalParser pParser) {
            super(new RateEditConverter(pFormatter, pParser));
        }
    }

    /**
     * UnitsFXTextField class.
     */
    public static class UnitsFXTextField
            extends DataFXEditTextField<JUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public UnitsFXTextField(final JDecimalFormatter pFormatter,
                                final JDecimalParser pParser) {
            super(new UnitsEditConverter(pFormatter, pParser));
        }
    }

    /**
     * DilutionFXTextField class.
     */
    public static class DilutionFXTextField
            extends DataFXEditTextField<JDilution> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public DilutionFXTextField(final JDecimalFormatter pFormatter,
                                   final JDecimalParser pParser) {
            super(new DilutionEditConverter(pFormatter, pParser));
        }
    }

    /**
     * RatioFXTextField class.
     */
    public static class RatioFXTextField
            extends DataFXEditTextField<JRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public RatioFXTextField(final JDecimalFormatter pFormatter,
                                final JDecimalParser pParser) {
            super(new RatioEditConverter(pFormatter, pParser));
        }
    }
}
