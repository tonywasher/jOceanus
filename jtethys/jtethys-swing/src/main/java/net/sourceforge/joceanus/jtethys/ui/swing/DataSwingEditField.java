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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Currency;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.decimal.JDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jtethys.ui.DataEditField.DataEditTextFieldBase;

/**
 * Generic class for displaying and editing a data field.
 */
public abstract class DataSwingEditField {
    /**
     * Private constructor.
     */
    private DataSwingEditField() {
    }

    /**
     * DataEditTextField class.
     * @param <T> the data type
     */
    public abstract static class DataSwingEditTextField<T>
            extends DataEditTextFieldBase<T, JPanel> {
        /**
         * The padding size to expand a label to match a TextField.
         */
        private static final int PADDING = 10;

        /**
         * The label name.
         */
        private static final String NAME_LABEL = "Label";

        /**
         * The edit name.
         */
        private static final String NAME_EDIT = "Edit";

        /**
         * The error colour.
         */
        private static final Color COLOR_ERROR = Color.decode("#DC381F");

        /**
         * The panel.
         */
        private final JPanel theNode;

        /**
         * The label.
         */
        private final JTextField theEditNode;

        /**
         * The label.
         */
        private final JLabel theLabel;

        /**
         * The card layout.
         */
        private final CardLayout theLayout;

        /**
         * The error text.
         */
        private String theErrorText;

        /**
         * The cache colour.
         */
        private Color theCacheColor;

        /**
         * Constructor.
         * @param pConverter the data converter
         */
        protected DataSwingEditTextField(final DataEditConverter<T> pConverter) {
            /* Call super-constructor */
            super(pConverter);

            /* Create resources */
            theNode = new JPanel();
            theLabel = new JLabel();
            theEditNode = new JTextField();

            /* Set alignment */
            int myAlignment = pConverter.rightAlignFields()
                                                            ? SwingConstants.RIGHT
                                                            : SwingConstants.LEFT;
            theLabel.setHorizontalAlignment(myAlignment);
            theEditNode.setHorizontalAlignment(myAlignment);
            theLabel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

            /* Default to readOnly */
            theLayout = new CardLayout();
            theNode.setLayout(theLayout);
            theNode.add(theLabel, NAME_LABEL);
            theNode.add(theEditNode, NAME_EDIT);

            /* Add listener to handle change of focus */
            theEditNode.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(final FocusEvent e) {
                    handleFocusGained();
                }

                @Override
                public void focusLost(final FocusEvent e) {
                    processValue();
                    if (theErrorText == null) {
                        handleFocusLost();
                    }
                }
            });

            /* handle enter key */
            theEditNode.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(final KeyEvent e) {
                    /* NoOp */
                }

                @Override
                public void keyPressed(final KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_ENTER:
                            processValue();
                            break;
                        case KeyEvent.VK_ESCAPE:
                            theEditNode.setText(getEditText());
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void keyReleased(final KeyEvent e) {
                    /* NoOp */
                }
            });
        }

        /**
         * Process value.
         */
        private void processValue() {
            /* If we failed to process the value */
            String myText = theEditNode.getText();
            if (!processValue(theEditNode.getText())) {
                /* Set error border */
                theEditNode.setToolTipText(TOOLTIP_BAD_VALUE);
                theErrorText = myText;

                /* Cache the background colour */
                if (theCacheColor == null) {
                    theCacheColor = theEditNode.getBackground();
                }
                theEditNode.setBackground(COLOR_ERROR);

                /* request focus again */
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        theEditNode.requestFocus();
                    }
                });

                /* else value was OK */
            } else {
                /* Clear error indications */
                theEditNode.setToolTipText(null);
                theErrorText = null;

                /* Restore cached background color */
                if (theCacheColor != null) {
                    theEditNode.setBackground(theCacheColor);
                }
                theCacheColor = null;
            }
        }

        @Override
        public JPanel getNode() {
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
         * Set the foreground colour.
         * @param pColor the colour
         */
        public void setForeground(final Color pColor) {
            /* Apply colour to the two nodes */
            theLabel.setForeground(pColor);
            theEditNode.setForeground(pColor);
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
            theEditNode.setText(theEditNode.hasFocus()
                                                       ? getEditText()
                                                       : getDisplayText());
        }

        @Override
        public void setEditable(final boolean pEditable) {
            /* Obtain current setting */
            boolean isEditable = isEditable();

            /* If we are changing */
            if (pEditable != isEditable) {
                /* Set correct component */
                theLayout.show(theNode, pEditable
                                                  ? NAME_EDIT
                                                  : NAME_LABEL);

                /* Pass call on */
                super.setEditable(pEditable);
            }
        }
    }

    /**
     * StringSwingTextField class.
     */
    public static class StringSwingTextField
            extends DataSwingEditTextField<String> {
        /**
         * Constructor.
         */
        public StringSwingTextField() {
            super(new StringEditConverter());
        }
    }

    /**
     * ShortSwingTextField class.
     */
    public static class ShortSwingTextField
            extends DataSwingEditTextField<Short> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public ShortSwingTextField(final JDecimalFormatter pFormatter,
                                   final JDecimalParser pParser) {
            super(new ShortEditConverter(pFormatter, pParser));
        }
    }

    /**
     * IntegerSwingTextField class.
     */
    public static class IntegerSwingTextField
            extends DataSwingEditTextField<Integer> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public IntegerSwingTextField(final JDecimalFormatter pFormatter,
                                     final JDecimalParser pParser) {
            super(new IntegerEditConverter(pFormatter, pParser));
        }
    }

    /**
     * LongSwingTextField class.
     */
    public static class LongSwingTextField
            extends DataSwingEditTextField<Long> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public LongSwingTextField(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
            super(new LongEditConverter(pFormatter, pParser));
        }
    }

    /**
     * MoneySwingTextField base class.
     * @param <T> the data type
     */
    protected abstract static class MoneySwingTextFieldBase<T extends JMoney>
            extends DataSwingEditTextField<T> {
        /**
         * Constructor.
         * @param pConverter the converter
         */
        public MoneySwingTextFieldBase(final MoneyEditConverterBase<T> pConverter) {
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
     * MoneySwingTextField class.
     */
    public static class MoneySwingTextField
            extends MoneySwingTextFieldBase<JMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public MoneySwingTextField(final JDecimalFormatter pFormatter,
                                   final JDecimalParser pParser) {
            super(new MoneyEditConverter(pFormatter, pParser));
        }
    }

    /**
     * PriceSwingTextField class.
     */
    public static class PriceSwingTextField
            extends MoneySwingTextFieldBase<JPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public PriceSwingTextField(final JDecimalFormatter pFormatter,
                                   final JDecimalParser pParser) {
            super(new PriceEditConverter(pFormatter, pParser));
        }
    }

    /**
     * DilutedPriceSwingTextField class.
     */
    public static class DilutedPriceSwingTextField
            extends MoneySwingTextFieldBase<JDilutedPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public DilutedPriceSwingTextField(final JDecimalFormatter pFormatter,
                                          final JDecimalParser pParser) {
            super(new DilutedPriceEditConverter(pFormatter, pParser));
        }
    }

    /**
     * RateSwingTextField class.
     */
    public static class RateSwingTextField
            extends DataSwingEditTextField<JRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public RateSwingTextField(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
            super(new RateEditConverter(pFormatter, pParser));
        }
    }

    /**
     * UnitsSwingTextField class.
     */
    public static class UnitsSwingTextField
            extends DataSwingEditTextField<JUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public UnitsSwingTextField(final JDecimalFormatter pFormatter,
                                   final JDecimalParser pParser) {
            super(new UnitsEditConverter(pFormatter, pParser));
        }
    }

    /**
     * DilutionSwingTextField class.
     */
    public static class DilutionSwingTextField
            extends DataSwingEditTextField<JDilution> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public DilutionSwingTextField(final JDecimalFormatter pFormatter,
                                      final JDecimalParser pParser) {
            super(new DilutionEditConverter(pFormatter, pParser));
        }
    }

    /**
     * RatioSwingTextField class.
     */
    public static class RatioSwingTextField
            extends DataSwingEditTextField<JRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public RatioSwingTextField(final JDecimalFormatter pFormatter,
                                   final JDecimalParser pParser) {
            super(new RatioEditConverter(pFormatter, pParser));
        }
    }
}
