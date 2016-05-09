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
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Currency;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 */
public abstract class TethysSwingDataTextField<T>
        extends TethysDataEditField<T, JComponent, Icon> {
    /**
     * The label name.
     */
    private static final String NAME_LABEL = "Label";

    /**
     * The edit name.
     */
    private static final String NAME_EDIT = "Edit";

    /**
     * The GUI factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The adjuster.
     */
    private final TethysSwingDataFieldAdjust theAdjuster;

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
     * @param pFactory the GUI factory
     * @param pEditControl the edit Control
     * @param pLabel the label
     */
    protected TethysSwingDataTextField(final TethysSwingGuiFactory pFactory,
                                       final JComponent pEditControl,
                                       final JLabel pLabel) {
        /* Initialise the underlying class */
        super(pFactory);
        theGuiFactory = pFactory;

        /* Create resources */
        theNode = new JPanel();
        theLabel = pLabel;
        theEditNode = new JPanel();
        theEditControl = pEditControl;

        /* Obtain the field adjuster */
        theAdjuster = theGuiFactory.getFieldAdjuster();

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
     * Obtain the error colour.
     * @return the colour.
     */
    protected Color getErrorColour() {
        return theAdjuster.getErrorColor();
    }

    /**
     * Obtain the GuiFactory.
     * @return the factory
     */
    protected TethysSwingGuiFactory getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the label.
     * @return the label
     */
    public JLabel getLabel() {
        return theLabel;
    }

    /**
     * Obtain the editControl.
     * @return the editControl
     */
    public JComponent getEditControl() {
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
     * Start cell editing.
     * @param pCell the cell rectangle
     */
    public abstract void startCellEditing(final Rectangle pCell);

    @Override
    public void adjustField() {
        theAdjuster.adjustField(this);
    }

    /**
     * Clone the dataField.
     * @param pLabel the label
     * @return the cloned data field
     */
    protected abstract TethysSwingDataTextField<T> cloneField(final JLabel pLabel);

    /**
     * TextField class.
     * @param <T> the data type
     */
    public abstract static class TethysSwingTextEditField<T>
            extends TethysSwingDataTextField<T> {
        /**
         * The standard border.
         */
        private static final Border BORDER_STD = BorderFactory.createLineBorder(Color.decode("#add8e6"));

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
         * The errorBorder.
         */
        private Border theErrorBorder;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pConverter the text converter
         * @param pLabel the label
         */
        protected TethysSwingTextEditField(final TethysSwingGuiFactory pFactory,
                                           final TethysDataEditConverter<T> pConverter,
                                           final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, new JTextField(), pLabel);

            /* Create the converter control */
            theControl = new TethysDataEditTextFieldControl<>(this, pConverter);

            /* Access the fields */
            JLabel myLabel = getLabel();
            theTextField = getEditControl();
            theTextField.setBorder(BORDER_STD);

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
        public JTextField getEditControl() {
            return (JTextField) super.getEditControl();
        }

        /**
         * Obtain the converter.
         * @return the converter.
         */
        protected TethysDataEditConverter<T> getConverter() {
            return theControl.getConverter();
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
                if (theErrorBorder == null) {
                    theErrorBorder = BorderFactory.createLineBorder(getErrorColour());
                }
                theTextField.setBorder(theErrorBorder);
                setTheAttribute(TethysFieldAttribute.ERROR);

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
            theTextField.setBorder(BORDER_STD);
            clearTheAttribute(TethysFieldAttribute.ERROR);
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
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
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
         * @param pFactory the GUI factory
         */
        protected TethysSwingStringTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingStringTextField(final TethysSwingGuiFactory pFactory,
                                           final JLabel pLabel) {
            super(pFactory, new TethysStringEditConverter(), pLabel);
        }

        @Override
        protected TethysSwingStringTextField cloneField(final JLabel pLabel) {
            return new TethysSwingStringTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingCharArrayTextField class.
     */
    public static class TethysSwingCharArrayTextField
            extends TethysSwingTextEditField<char[]> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingCharArrayTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingCharArrayTextField(final TethysSwingGuiFactory pFactory,
                                              final JLabel pLabel) {
            super(pFactory, new TethysCharArrayEditConverter(), pLabel);
        }

        @Override
        protected TethysSwingCharArrayTextField cloneField(final JLabel pLabel) {
            return new TethysSwingCharArrayTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * ShortSwingTextField class.
     */
    public static class TethysSwingShortTextField
            extends TethysSwingTextEditField<Short> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingShortTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingShortTextField(final TethysSwingGuiFactory pFactory,
                                          final JLabel pLabel) {
            super(pFactory, new TethysShortEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingShortTextField cloneField(final JLabel pLabel) {
            return new TethysSwingShortTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingIntegerTextField class.
     */
    public static class TethysSwingIntegerTextField
            extends TethysSwingTextEditField<Integer> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingIntegerTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingIntegerTextField(final TethysSwingGuiFactory pFactory,
                                            final JLabel pLabel) {
            super(pFactory, new TethysIntegerEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingIntegerTextField cloneField(final JLabel pLabel) {
            return new TethysSwingIntegerTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingLongTextField class.
     */
    public static class TethysSwingLongTextField
            extends TethysSwingTextEditField<Long> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingLongTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingLongTextField(final TethysSwingGuiFactory pFactory,
                                         final JLabel pLabel) {
            super(pFactory, new TethysLongEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingLongTextField cloneField(final JLabel pLabel) {
            return new TethysSwingLongTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingMoneyTextField base class.
     * @param <T> the data type
     */
    protected abstract static class TethysSwingCurrencyTextFieldBase<T extends TethysMoney>
            extends TethysSwingTextEditField<T>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pConverter the converter
         * @param pLabel the label
         */
        protected TethysSwingCurrencyTextFieldBase(final TethysSwingGuiFactory pFactory,
                                                   final TethysMoneyEditConverterBase<T> pConverter,
                                                   final JLabel pLabel) {
            super(pFactory, pConverter, pLabel);
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
            extends TethysSwingCurrencyTextFieldBase<TethysMoney> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingMoneyTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingMoneyTextField(final TethysSwingGuiFactory pFactory,
                                          final JLabel pLabel) {
            super(pFactory, new TethysMoneyEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingMoneyTextField cloneField(final JLabel pLabel) {
            return new TethysSwingMoneyTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingPriceTextField class.
     */
    public static class TethysSwingPriceTextField
            extends TethysSwingCurrencyTextFieldBase<TethysPrice> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingPriceTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingPriceTextField(final TethysSwingGuiFactory pFactory,
                                          final JLabel pLabel) {
            super(pFactory, new TethysPriceEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingPriceTextField cloneField(final JLabel pLabel) {
            return new TethysSwingPriceTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingDilutedPriceTextField class.
     */
    public static class TethysSwingDilutedPriceTextField
            extends TethysSwingCurrencyTextFieldBase<TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingDilutedPriceTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingDilutedPriceTextField(final TethysSwingGuiFactory pFactory,
                                                 final JLabel pLabel) {
            super(pFactory, new TethysDilutedPriceEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingDilutedPriceTextField cloneField(final JLabel pLabel) {
            return new TethysSwingDilutedPriceTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingRateTextField class.
     */
    public static class TethysSwingRateTextField
            extends TethysSwingTextEditField<TethysRate> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingRateTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingRateTextField(final TethysSwingGuiFactory pFactory,
                                         final JLabel pLabel) {
            super(pFactory, new TethysRateEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingRateTextField cloneField(final JLabel pLabel) {
            return new TethysSwingRateTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingUnitsTextField class.
     */
    public static class TethysSwingUnitsTextField
            extends TethysSwingTextEditField<TethysUnits> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingUnitsTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingUnitsTextField(final TethysSwingGuiFactory pFactory,
                                          final JLabel pLabel) {
            super(pFactory, new TethysUnitsEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingUnitsTextField cloneField(final JLabel pLabel) {
            return new TethysSwingUnitsTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingDilutionTextField class.
     */
    public static class TethysSwingDilutionTextField
            extends TethysSwingTextEditField<TethysDilution> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingDilutionTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingDilutionTextField(final TethysSwingGuiFactory pFactory,
                                             final JLabel pLabel) {
            super(pFactory, new TethysDilutionEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingDilutionTextField cloneField(final JLabel pLabel) {
            return new TethysSwingDilutionTextField(getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingRatioTextField class.
     */
    public static class TethysSwingRatioTextField
            extends TethysSwingTextEditField<TethysRatio> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingRatioTextField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingRatioTextField(final TethysSwingGuiFactory pFactory,
                                          final JLabel pLabel) {
            super(pFactory, new TethysRatioEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        protected TethysSwingRatioTextField cloneField(final JLabel pLabel) {
            return new TethysSwingRatioTextField(getGuiFactory(), pLabel);
        }
    }
}
