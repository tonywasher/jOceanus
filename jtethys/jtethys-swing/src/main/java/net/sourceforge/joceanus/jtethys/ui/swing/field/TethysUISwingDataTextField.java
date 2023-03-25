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
package net.sourceforge.joceanus.jtethys.ui.swing.field;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Currency;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
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
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingDataFieldAdjust;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingDataFieldAdjust.TethysUISwingColorSet;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingDataFieldAdjust.TethysUISwingFieldAdjustSupplier;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingDataFieldAdjust.TethysUISwingFontSet;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.menu.TethysUISwingScrollMenu;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 */
public abstract class TethysUISwingDataTextField<T>
        extends TethysUICoreDataEditField<T> {
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
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * The adjuster.
     */
    private final TethysUISwingDataFieldAdjust theAdjuster;

    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The panel.
     */
    private final JPanel theCard;

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
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditControl the edit Control
     * @param pLabel the label
     */
    protected TethysUISwingDataTextField(final TethysUICoreFactory<?> pFactory,
                                         final JComponent pEditControl,
                                         final JLabel pLabel) {
        /* Initialise the underlying class */
        super(pFactory);
        theGuiFactory = pFactory;

        /* Create resources */
        theCard = new JPanel();
        theLabel = pLabel;
        theEditNode = new JPanel();
        theEditControl = pEditControl;

        /* Obtain the field adjuster */
        theAdjuster = ((TethysUISwingFieldAdjustSupplier) theGuiFactory).getFieldAdjuster();

        /* Create the command button */
        theCmdButton = new JButton();
        theCmdButton.setIcon(TethysUISwingArrowIcon.DOWN);
        theCmdButton.setMargin(new Insets(0, 0, 0, 0));
        theCmdButton.setFocusable(false);

        /* declare the command menu */
        declareCmdMenu(pFactory.menuFactory().newContextMenu());

        /* Build the edit node */
        theEditNode.setLayout(new BorderLayout());
        theEditNode.add(theEditControl, BorderLayout.CENTER);

        /* Default to readOnly */
        theLayout = new CardLayout();
        theCard.setLayout(theLayout);

        /* If we have a text area */
        if (theEditControl instanceof JTextArea) {
            /* Wrap the edit control in a ScrollPane and add as sole card */
            final JScrollPane myScroll = new JScrollPane();
            myScroll.setViewportView(theEditNode);
            theCard.add(theEditNode, NAME_EDIT);
            ((JTextArea) theEditControl).setEditable(false);

            /* Else for non-TextArea */
        } else {
            /* Add cards, defaulting to readOnly */
            theCard.add(theLabel, NAME_LABEL);
            theCard.add(theEditNode, NAME_EDIT);
        }

        /* Create the node */
        theNode = new TethysUISwingNode(theCard);

        /* Set command button action handler */
        theCmdButton.addActionListener(e -> handleCmdMenuRequest());

        /* Set command menu listener */
        getCmdMenu().getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> handleCmdMenuClosed());
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public Integer getHeight() {
        return (int) theCard.getPreferredSize().getHeight();
    }

    /**
     * Adjust field.
     * @param pDataField the dataField
     */
    private void adjustField(final TethysUISwingDataTextField<?> pDataField) {
        /* Determine the flags */
        final boolean isNumeric = pDataField.isAttributeSet(TethysUIFieldAttribute.NUMERIC);
        final boolean isSelected = pDataField.isAttributeSet(TethysUIFieldAttribute.SELECTED);
        final boolean isChanged = pDataField.isAttributeSet(TethysUIFieldAttribute.CHANGED);
        final boolean isDisabled = pDataField.isAttributeSet(TethysUIFieldAttribute.DISABLED);
        final boolean isAlternate = pDataField.isAttributeSet(TethysUIFieldAttribute.ALTERNATE);
        final boolean isFieldSet = pDataField.isAttributeSet(TethysUIFieldAttribute.FIELDSET);

        /* Obtain the label and the edit control */
        final JLabel myLabel = pDataField.getLabel();
        final JComponent myControl = pDataField.getEditControl();

        /* FieldSet is always left-aligned on the label */
        if (isFieldSet) {
            myLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }

        /* Access font and colorSets */
        final TethysUISwingFontSet myFontSet = theAdjuster.getFontSet();
        final TethysUISwingColorSet myColorSet = theAdjuster.getColorSet();

        /* Determine the font */
        final Font myFont;
        if (isNumeric) {
            if (isChanged) {
                myFont = isSelected
                            ? myFontSet.getBoldChangedNumeric()
                            : myFontSet.getChangedNumeric();
            } else {
                myFont = isSelected
                            ? myFontSet.getBoldNumeric()
                            : myFontSet.getNumeric();
            }
        } else {
            if (isChanged) {
                myFont = isSelected
                            ? myFontSet.getBoldChanged()
                            : myFontSet.getChanged();
            } else {
                myFont = isSelected
                            ? myFontSet.getBoldStandard()
                            : myFontSet.getStandard();
            }
        }
        myLabel.setFont(myFont);
        myControl.setFont(myFont);

        /* Determine the foreground */
        final Color myForeground;
        if (isChanged) {
            myForeground = myColorSet.getChanged();
        } else {
            myForeground = isDisabled
                                ? myColorSet.getDisabled()
                                : myColorSet.getStandard();

        }
        myLabel.setForeground(myForeground);
        myControl.setForeground(myForeground);

        /* Determine the background (don't set the control) */
        final Color myBackground = isAlternate
                ? myColorSet.getZebra()
                : myColorSet.getBackground();
        myLabel.setBackground(myBackground);
    }

    /**
     * Obtain the error colour.
     * @return the colour.
     */
    Color getErrorColour() {
        return theAdjuster.getErrorColor();
    }

    /**
     * Obtain the GuiFactory.
     * @return the factory
     */
    protected TethysUICoreFactory<?> getGuiFactory() {
        return theGuiFactory;
    }

    @Override
    protected TethysUISwingScrollMenu<String> getCmdMenu() {
        return (TethysUISwingScrollMenu<String>) super.getCmdMenu();
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
    protected void showCmdMenu() {
        getCmdMenu().showMenuAtPosition(theCmdButton, SwingConstants.RIGHT);
    }

    @Override
    public void showCmdButton(final boolean pShow) {
        /* Remove any button that is displaying */
        theEditNode.remove(theCmdButton);

        /* If we have a button to display */
        if (pShow) {
            theEditNode.add(theCmdButton, BorderLayout.LINE_END);
        }
    }

    @Override
    public void setEditable(final boolean pEditable) {
        /* Obtain current setting */
        final boolean isEditable = isEditable();

        /* If we are changing */
        if (pEditable != isEditable) {
            /* If we are JTextArea */
            if (theEditControl instanceof JTextArea) {
                /* Set editable */
                ((JTextArea) theEditControl).setEditable(pEditable);

            } else {
                /* Set correct component */
                theLayout.show(theCard, pEditable
                        ? NAME_EDIT
                        : NAME_LABEL);
            }

            /* Pass call on */
            super.setEditable(pEditable);
        }
    }

    /**
     * Start cell editing.
     * @param pCell the cell rectangle
     */
    public abstract void startCellEditing(Rectangle pCell);

    @Override
    public void adjustField() {
        adjustField(this);
    }

    /**
     * parse the data.
     */
    public void parseData() {
        /* NoOp */
    }

    /**
     * Clone the dataField.
     * @param pLabel the label
     * @return the cloned data field
     */
    public abstract TethysUISwingDataTextField<T> cloneField(JLabel pLabel);

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
        adjustSubFields();
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
        adjustSubFields();
    }

    /**
     * Adjust subFields to container size.
     */
    private void adjustSubFields() {
        final Dimension myBounds = theNode.getNode().getPreferredSize();
        theLabel.setPreferredSize(myBounds);
        theEditControl.setPreferredSize(myBounds);
    }

    /**
     * TextField class.
     * @param <T> the data type
     * @param <F> the field type
     */
    public abstract static class TethysUISwingTextEditField<T, F extends JTextComponent>
            extends TethysUISwingDataTextField<T>
            implements TethysUIValidatedEditField<T> {
        /**
         * The standard border.
         */
        private static final Border BORDER_STD = BorderFactory.createLineBorder(Color.decode("#add8e6"));

        /**
         * The converterControl.
         */
        private final TethysUIDataEditTextFieldControl<T> theControl;

        /**
         * The textField.
         */
        private final F theTextField;

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
         * @param pField the field
         */
        @SuppressWarnings("checkstyle:MethodParamPad")
        TethysUISwingTextEditField(final TethysUICoreFactory<?> pFactory,
                                   final TethysUICoreDataEditConverter<T> pConverter,
                                   final JLabel pLabel,
                                   final F pField) {
            /* Initialise underlying class */
            super(pFactory, pField, pLabel);

            /* Create the converter control */
            theControl = new TethysUIDataEditTextFieldControl<>(this, pConverter);

            /* Access the fields */
            final JLabel myLabel = getLabel();
            theTextField = getEditControl();
            theTextField.setBorder(BORDER_STD);

            /* Set alignment */
            final int myAlignment = pConverter.rightAlignFields()
                    ? SwingConstants.RIGHT
                    : SwingConstants.LEFT;
            myLabel.setHorizontalAlignment(myAlignment);
            if (theTextField instanceof JTextField) {
                ((JTextField) theTextField).setHorizontalAlignment(myAlignment);
            }
            theTextField.setEditable(true);

            /* Add listener to handle change of focus */
            theTextField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(final FocusEvent e) {
                    handleFocusGained();
                }

                @Override
                public void focusLost(final FocusEvent e) {
                    handleFocusLost();
                }
            });

            theTextField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "none");
            theTextField.addKeyListener(new DataKeyListener());
        }

        @Override
        @SuppressWarnings("unchecked")
        public F getEditControl() {
            return (F) super.getEditControl();
        }

        /**
         * Obtain the converter.
         * @return the converter.
         */
        protected TethysUICoreDataEditConverter<T> getConverter() {
            return theControl.getConverter();
        }

        @Override
        public void setValidator(final Function<T, String> pValidator) {
            theControl.setValidator(pValidator);
        }

        @Override
        public void setReporter(final Consumer<String> pReporter) {
            theControl.setReporter(pReporter);
        }

        @Override
        public void parseData() {
            processValue();
        }

        /**
         * Process value.
         */
        void processValue() {
            /* Convert zero-length string to null */
            String myText = theTextField.getText();
            if (myText.length() == 0) {
                myText = null;
            }

            /* If we failed to process the value */
            if (!theControl.processValue(myText)) {
                /* Set error border */
                theTextField.setToolTipText(theControl.getErrorText());
                theErrorText = myText;

                /* Cache the background colour */
                if (theErrorBorder == null) {
                    theErrorBorder = BorderFactory.createLineBorder(getErrorColour());
                }
                theTextField.setBorder(theErrorBorder);
                theTextField.setForeground(getErrorColour());
                setTheAttribute(TethysUIFieldAttribute.ERROR);

                /* request focus again */
                SwingUtilities.invokeLater(theTextField::requestFocus);

                /* else value was OK */
            } else {
                /* Clear error indications */
                clearError();
            }
        }

        /**
         * Clear Error indication.
         */
        void clearError() {
            /* Clear error indications */
            theTextField.setToolTipText(null);
            theErrorText = null;
            adjustField();

            /* Restore cached background colour */
            theTextField.setBorder(BORDER_STD);
            clearTheAttribute(TethysUIFieldAttribute.ERROR);
        }

        /**
         * Handle focusGained.
         */
        void handleFocusGained() {
            if (!isCellEditing) {
                theTextField.setText(theErrorText == null
                        ? theControl.getEditText()
                        : theErrorText);
                theTextField.selectAll();
            }
        }

        /**
         * Handle focusLost.
         */
        void handleFocusLost() {
            handleEscapeKey();
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
         * handle escapeKey.
         */
        private void handleEscapeKey() {
            resetEditText();
            clearError();
            fireEvent(TethysUIEvent.EDITFOCUSLOST);
            haltCellEditing();
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
                if (isCellEditing) {
                    return;
                }
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
                if (!isAttributeSet(TethysUIFieldAttribute.ERROR)) {
                    haltCellEditing();
                }
            }
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
            isCellEditing = true;
            setEditable(true);
            clearError();
            theControl.clearNewValue();
            theTextField.requestFocus();
        }

        /**
         * Halt cell editing.
         */
        void haltCellEditing() {
            if (isCellEditing) {
                setEditable(false);
            }
            isCellEditing = false;
        }

        /**
         * Reset edit text.
         */
        void resetEditText() {
            theTextField.setText(theControl.getEditText());
        }
    }

    /**
     * SwingStringTextField class.
     */
    public static class TethysUISwingStringTextField
            extends TethysUISwingTextEditField<String, JTextField>
            implements TethysUIStringEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysUISwingStringTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingStringTextField(final TethysUICoreFactory<?> pFactory,
                                             final JLabel pLabel) {
            super(pFactory, new TethysUICoreStringEditConverter(), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingStringTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingStringTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingStringTextAreaField class.
     */
    public static class TethysUISwingStringTextAreaField
            extends TethysUISwingTextEditField<String, JTextArea>
            implements TethysUIStringTextAreaField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysUISwingStringTextAreaField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingStringTextAreaField(final TethysUICoreFactory<?> pFactory,
                                                 final JLabel pLabel) {
            super(pFactory, new TethysUICoreStringEditConverter(), pLabel, new JTextArea());
        }

        @Override
        public TethysUISwingStringTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingStringTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingCharArrayTextField class.
     */
    public static class TethysUISwingCharArrayTextField
            extends TethysUISwingTextEditField<char[], JTextField>
            implements TethysUICharArrayEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingCharArrayTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingCharArrayTextField(final TethysUICoreFactory<?> pFactory,
                                                final JLabel pLabel) {
            super(pFactory, new TethysUICoreCharArrayEditConverter(), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingCharArrayTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingCharArrayTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingCharArrayTextAreaField class.
     */
    public static class TethysUISwingCharArrayTextAreaField
            extends TethysUISwingTextEditField<char[], JTextArea>
            implements TethysUICharArrayTextAreaField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysUISwingCharArrayTextAreaField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingCharArrayTextAreaField(final TethysUICoreFactory<?> pFactory,
                                                    final JLabel pLabel) {
            super(pFactory, new TethysUICoreCharArrayEditConverter(), pLabel, new JTextArea());
        }

        @Override
        public TethysUISwingCharArrayTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingCharArrayTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * ShortSwingTextField class.
     */
    public static class TethysUISwingShortTextField
            extends TethysUISwingTextEditField<Short, JTextField>
            implements TethysUIShortEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingShortTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingShortTextField(final TethysUICoreFactory<?> pFactory,
                                            final JLabel pLabel) {
            super(pFactory, new TethysUICoreShortEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingShortTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingShortTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingIntegerTextField class.
     */
    public static class TethysUISwingIntegerTextField
            extends TethysUISwingTextEditField<Integer, JTextField>
            implements TethysUIIntegerEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingIntegerTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingIntegerTextField(final TethysUICoreFactory<?> pFactory,
                                              final JLabel pLabel) {
            super(pFactory, new TethysUICoreIntegerEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingIntegerTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingIntegerTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingLongTextField class.
     */
    public static class TethysUISwingLongTextField
            extends TethysUISwingTextEditField<Long, JTextField>
            implements TethysUILongEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingLongTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingLongTextField(final TethysUICoreFactory<?> pFactory,
                                           final JLabel pLabel) {
            super(pFactory, new TethysUICoreLongEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingLongTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingLongTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingRawDecimalTextField class.
     */
    public static class TethysUISwingRawDecimalTextField
            extends TethysUISwingTextEditField<TethysDecimal, JTextField>
            implements TethysUIRawDecimalEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingRawDecimalTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingRawDecimalTextField(final TethysUICoreFactory<?> pFactory,
                                                 final JLabel pLabel) {
            super(pFactory, new TethysUICoreRawDecimalEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingRawDecimalTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingRawDecimalTextField(super.getGuiFactory(), pLabel);
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
     * SwingCurrencyTextField base class.
     * @param <T> the data type
     */
    protected abstract static class TethysUISwingCurrencyTextFieldBase<T extends TethysMoney>
            extends TethysUISwingTextEditField<T, JTextField>
            implements TethysUICurrencyEditField<T> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pConverter the converter
         * @param pLabel the label
         */
        TethysUISwingCurrencyTextFieldBase(final TethysUICoreFactory<?> pFactory,
                                           final TethysUICoreMoneyEditConverterBase<T> pConverter,
                                           final JLabel pLabel) {
            super(pFactory, pConverter, pLabel, new JTextField());
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
     * SwingMoneyTextField class.
     */
    public static class TethysUISwingMoneyTextField
            extends TethysUISwingCurrencyTextFieldBase<TethysMoney>
            implements TethysUIMoneyEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingMoneyTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingMoneyTextField(final TethysUICoreFactory<?> pFactory,
                                            final JLabel pLabel) {
            super(pFactory, new TethysUICoreMoneyEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        public TethysUISwingMoneyTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingMoneyTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingPriceTextField class.
     */
    public static class TethysUISwingPriceTextField
            extends TethysUISwingCurrencyTextFieldBase<TethysPrice>
            implements TethysUIPriceEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingPriceTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingPriceTextField(final TethysUICoreFactory<?> pFactory,
                                            final JLabel pLabel) {
            super(pFactory, new TethysUICorePriceEditConverter(pFactory.getDataFormatter()), pLabel);
        }

        @Override
        public TethysUISwingPriceTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingPriceTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingRateTextField class.
     */
    public static class TethysUISwingRateTextField
            extends TethysUISwingTextEditField<TethysRate, JTextField>
            implements TethysUIRateEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingRateTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingRateTextField(final TethysUICoreFactory<?> pFactory,
                                           final JLabel pLabel) {
            super(pFactory, new TethysUICoreRateEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingRateTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingRateTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingUnitsTextField class.
     */
    public static class TethysUISwingUnitsTextField
            extends TethysUISwingTextEditField<TethysUnits, JTextField>
            implements TethysUIUnitsEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingUnitsTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingUnitsTextField(final TethysUICoreFactory<?> pFactory,
                                            final JLabel pLabel) {
            super(pFactory, new TethysUICoreUnitsEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingUnitsTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingUnitsTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingDilutionTextField class.
     */
    public static class TethysUISwingDilutionTextField
            extends TethysUISwingTextEditField<TethysDilution, JTextField>
            implements TethysUIDilutionEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingDilutionTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingDilutionTextField(final TethysUICoreFactory<?> pFactory,
                                               final JLabel pLabel) {
            super(pFactory, new TethysUICoreDilutionEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingDilutionTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingDilutionTextField(super.getGuiFactory(), pLabel);
        }
    }

    /**
     * SwingRatioTextField class.
     */
    public static class TethysUISwingRatioTextField
            extends TethysUISwingTextEditField<TethysRatio, JTextField>
            implements TethysUIRatioEditField {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingRatioTextField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingRatioTextField(final TethysUICoreFactory<?> pFactory,
                                            final JLabel pLabel) {
            super(pFactory, new TethysUICoreRatioEditConverter(pFactory.getDataFormatter()), pLabel, new JTextField());
        }

        @Override
        public TethysUISwingRatioTextField cloneField(final JLabel pLabel) {
            return new TethysUISwingRatioTextField(super.getGuiFactory(), pLabel);
        }
    }
}
