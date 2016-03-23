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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Currency;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 */
public abstract class TethysSwingDataTextField<T>
        extends TethysDataEditField<T, JComponent, Color, Font, Icon> {
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
     * The edit node.
     */
    private final JPanel theEditNode;

    /**
     * The edit control.
     */
    private final JComponent theEditControl;

    /**
     * The label.
     */
    private final JLabel theLabel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * The command button.
     */
    private final JButton theCmdButton;

    /**
     * Do we show the command button?
     */
    private boolean doShowCmdButton;

    /**
     * Constructor.
     * @param pEditControl the edit Control
     */
    protected TethysSwingDataTextField(final JComponent pEditControl) {
        /* Create resources */
        theNode = new JPanel();
        theLabel = new JLabel();
        theEditNode = new JPanel();
        theEditControl = pEditControl;

        /* Create the command button */
        theCmdButton = new JButton();
        theCmdButton.setIcon(TethysSwingArrowIcon.DOWN);
        theCmdButton.setMargin(new Insets(0, 0, 0, 0));
        theCmdButton.setFocusable(false);

        /* declare the command menu */
        declareCmdMenu(new TethysSwingScrollContextMenu<String>());

        /* Build the edit node */
        theEditNode.setLayout(new BorderLayout());
        theEditNode.add(theEditControl, BorderLayout.CENTER);

        /* Default to readOnly */
        theLayout = new CardLayout();
        theNode.setLayout(theLayout);
        theNode.add(theLabel, NAME_LABEL);
        theNode.add(theEditNode, NAME_EDIT);

        /* Set command button action handler */
        theCmdButton.addActionListener(e -> handleCmdMenuRequest());

        /* Set command menu listener */
        getCmdMenu().getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> handleCmdMenuClosed());
    }

    @Override
    public JPanel getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    /**
     * Obtain the label.
     * @return the label
     */
    protected JLabel getLabel() {
        return theLabel;
    }

    /**
     * Obtain the editControl.
     * @return the editControl
     */
    protected JComponent getEditControl() {
        return theEditControl;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* Apply to the nodes */
        theLabel.setEnabled(pEnabled);
        theEditControl.setEnabled(pEnabled);
        theCmdButton.setEnabled(pEnabled);
    }

    @Override
    public void setTextFill(final Color pColor) {
        /* Apply colour to the two nodes */
        theLabel.setForeground(pColor);
        theEditNode.setForeground(pColor);
    }

    @Override
    public void setBackground(final Color pColor) {
        /* Apply colour to the label only */
        theLabel.setBackground(pColor);
    }

    @Override
    public TethysSwingScrollContextMenu<String> getCmdMenu() {
        return (TethysSwingScrollContextMenu<String>) super.getCmdMenu();
    }

    @Override
    protected void showCmdMenu() {
        getCmdMenu().showMenuAtPosition(theCmdButton, SwingConstants.RIGHT);
    }

    @Override
    public void showCmdButton(final boolean pShow) {
        /* Remove any button that is displaying */
        theEditNode.remove(theCmdButton);
        doShowCmdButton = pShow;

        /* If we have a button to display */
        if (doShowCmdButton) {
            theEditNode.add(theCmdButton, BorderLayout.LINE_END);
        }
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

    /**
     * TextField class.
     * @param <T> the data type
     */
    public abstract static class TethysSwingTextEditField<T>
            extends TethysSwingDataTextField<T> {
        /**
         * The converterControl.
         */
        private final TethysDataEditTextFieldControl<T> theControl;
        /**
         * The textField.
         */
        private final JTextField theTextField;

        /**
         * The error text.
         */
        private String theErrorText;

        /**
         * The cache colour.
         */
        private Color theCacheColor;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pConverter the text converter
         */
        public TethysSwingTextEditField(final TethysDataEditConverter<T> pConverter) {
            /* Initialise underlying class */
            super(new JTextField());

            /* Create the converter control */
            theControl = new TethysDataEditTextFieldControl<>(this, pConverter);

            /* Access the fields */
            JLabel myLabel = getLabel();
            theTextField = getEditControl();

            /* Set alignment */
            int myAlignment = pConverter.rightAlignFields()
                                                            ? SwingConstants.RIGHT
                                                            : SwingConstants.LEFT;
            myLabel.setHorizontalAlignment(myAlignment);
            theTextField.setHorizontalAlignment(myAlignment);

            /* Add listener to handle change of focus */
            theTextField.addFocusListener(new FocusListener() {
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

            /* handle enter/escape keys */
            theTextField.addKeyListener(new DataKeyListener());
        }

        @Override
        protected JTextField getEditControl() {
            return (JTextField) super.getEditControl();
        }

        /**
         * Obtain the converter.
         * @return the converter.
         */
        protected TethysDataEditConverter<T> getConverter() {
            return theControl.getConverter();
        }

        @Override
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            getLabel().setFont(pFont);
            theTextField.setFont(pFont);
        }

        /**
         * Process value.
         */
        private void processValue() {
            /* Convert zero-length string to null */
            String myText = theTextField.getText();
            if (myText.length() == 0) {
                myText = null;
            }

            /* If we failed to process the value */
            if (!theControl.processValue(myText)) {
                /* Set error border */
                theTextField.setToolTipText(TOOLTIP_BAD_VALUE);
                theErrorText = myText;

                /* Cache the background colour */
                if (theCacheColor == null) {
                    theCacheColor = theTextField.getBackground();
                }
                theTextField.setBackground(COLOR_ERROR);

                /* request focus again */
                SwingUtilities.invokeLater(() -> theTextField.requestFocus());

                /* else value was OK */
            } else {
                /* Clear error indications */
                clearError();
            }

        }

        /**
         * Clear Error indication.
         */
        private void clearError() {
            /* Clear error indications */
            theTextField.setToolTipText(null);
            theErrorText = null;

            /* Restore cached background colour */
            if (theCacheColor != null) {
                theTextField.setBackground(theCacheColor);
            }
            theCacheColor = null;
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
            theTextField.setText(theTextField.hasFocus()
                                                         ? theControl.getEditText()
                                                         : theControl.getDisplayText());
        }

        /**
         * Key Listener class.
         */
        private class DataKeyListener
                implements KeyListener {
            @Override
            public void keyTyped(final KeyEvent e) {
                /* NoOp */
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        handleEnterKey();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        handleEscapeKey();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                /* NoOp */
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
        public void startCellEditing() {
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
    }

    /**
     * SwingStringTextField class.
     */
    public static class TethysSwingStringTextField
            extends TethysSwingTextEditField<String> {
        /**
         * Constructor.
         */
        public TethysSwingStringTextField() {
            super(new TethysStringEditConverter());
        }
    }

    /**
     * ShortSwingTextField class.
     */
    public static class TethysSwingShortTextField
            extends TethysSwingTextEditField<Short> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingShortTextField(final TethysDataFormatter pFormatter) {
            super(new TethysShortEditConverter(pFormatter));
        }
    }

    /**
     * SwingIntegerTextField class.
     */
    public static class TethysSwingIntegerTextField
            extends TethysSwingTextEditField<Integer> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingIntegerTextField(final TethysDataFormatter pFormatter) {
            super(new TethysIntegerEditConverter(pFormatter));
        }
    }

    /**
     * SwingLongTextField class.
     */
    public static class TethysSwingLongTextField
            extends TethysSwingTextEditField<Long> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingLongTextField(final TethysDataFormatter pFormatter) {
            super(new TethysLongEditConverter(pFormatter));
        }
    }

    /**
     * SwingMoneyTextField base class.
     * @param <T> the data type
     */
    protected abstract static class TethysSwingMoneyTextFieldBase<T extends TethysMoney>
            extends TethysSwingTextEditField<T>
            implements TethysCurrencyItem {
        /**
         * Constructor.
         * @param pConverter the converter
         */
        public TethysSwingMoneyTextFieldBase(final TethysMoneyEditConverterBase<T> pConverter) {
            super(pConverter);
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
     * SwingMoneyTextField class.
     */
    public static class TethysSwingMoneyTextField
            extends TethysSwingMoneyTextFieldBase<TethysMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingMoneyTextField(final TethysDataFormatter pFormatter) {
            super(new TethysMoneyEditConverter(pFormatter));
        }
    }

    /**
     * SwingPriceTextField class.
     */
    public static class TethysSwingPriceTextField
            extends TethysSwingMoneyTextFieldBase<TethysPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingPriceTextField(final TethysDataFormatter pFormatter) {
            super(new TethysPriceEditConverter(pFormatter));
        }
    }

    /**
     * SwingDilutedPriceTextField class.
     */
    public static class TethysSwingDilutedPriceTextField
            extends TethysSwingMoneyTextFieldBase<TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingDilutedPriceTextField(final TethysDataFormatter pFormatter) {
            super(new TethysDilutedPriceEditConverter(pFormatter));
        }
    }

    /**
     * SwingRateTextField class.
     */
    public static class TethysSwingRateTextField
            extends TethysSwingTextEditField<TethysRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingRateTextField(final TethysDataFormatter pFormatter) {
            super(new TethysRateEditConverter(pFormatter));
        }
    }

    /**
     * SwingUnitsTextField class.
     */
    public static class TethysSwingUnitsTextField
            extends TethysSwingTextEditField<TethysUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter [
         */
        public TethysSwingUnitsTextField(final TethysDataFormatter pFormatter) {
            super(new TethysUnitsEditConverter(pFormatter));
        }
    }

    /**
     * SwingDilutionTextField class.
     */
    public static class TethysSwingDilutionTextField
            extends TethysSwingTextEditField<TethysDilution> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingDilutionTextField(final TethysDataFormatter pFormatter) {
            super(new TethysDilutionEditConverter(pFormatter));
        }
    }

    /**
     * SwingRatioTextField class.
     */
    public static class TethysSwingRatioTextField
            extends TethysSwingTextEditField<TethysRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysSwingRatioTextField(final TethysDataFormatter pFormatter) {
            super(new TethysRatioEditConverter(pFormatter));
        }
    }
}
