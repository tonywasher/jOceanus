/*******************************************************************************
 * jFieldSet: Java Swing Field Set
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jFieldSet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayButton;
import net.sourceforge.jOceanus.jFieldSet.JFieldModel.JModelBoolean;
import net.sourceforge.jOceanus.jFieldSet.JFieldModel.JModelDateDay;
import net.sourceforge.jOceanus.jFieldSet.JFieldModel.JModelObject;
import net.sourceforge.jOceanus.jFieldSet.JFieldModel.JModelString;

/**
 * Component classes for jFieldSet.
 * @param <T> the Data Item type
 */
public abstract class JFieldComponent<T extends JFieldSetItem> {
    /**
     * The Scroll Component.
     */
    private JScrollPane theScroll = null;

    /**
     * The Component.
     */
    private final JComponent theComponent;

    /**
     * The DataModel.
     */
    private final JFieldModel<T> theModel;

    /**
     * The Standard border.
     */
    private final Border theBorder;

    /**
     * Obtain component.
     * @return the component
     */
    protected JComponent getComponent() {
        return theComponent;
    }

    /**
     * Obtain model.
     * @return the model
     */
    protected JFieldModel<T> getModel() {
        return theModel;
    }

    /**
     * Set scroll pane.
     * @param pScroll the scroll pane
     */
    protected void setScroll(final JScrollPane pScroll) {
        theScroll = pScroll;
    }

    /**
     * Derive component.
     * @param <X> Data item type
     * @param pElement the element
     * @param pComponent the component
     * @param pClass the data type
     * @return the field component
     */
    protected static <X extends JFieldSetItem> JFieldComponent<X> deriveComponent(final JFieldElement<X> pElement,
                                                                                  final JComponent pComponent,
                                                                                  final DataType pClass) {
        /* Handle invalid child */
        if (pComponent == null) {
            throw new IllegalArgumentException("Null Component");
        }

        /* If the component is a scroll Pane */
        if (pComponent instanceof JScrollPane) {
            /* Split into scrollPane and child */
            JScrollPane myScroll = (JScrollPane) pComponent;
            JViewport myViewPort = myScroll.getViewport();
            Component myComp = myViewPort.getView();

            /* Handle invalid child */
            if (myComp == null) {
                throw new IllegalArgumentException("Null ScrollPane view");
            }
            if (!(myComp instanceof JComponent)) {
                throw new IllegalArgumentException("Invalid ScrollPane view class: "
                                                   + myComp.getClass());
            }

            /* Derive component based on child and record scroll pane */
            JFieldComponent<X> myResult = deriveComponent(pElement, (JComponent) myComp, pClass);
            myResult.setScroll(myScroll);

            /* Return the result */
            return myResult;
        }

        /* Obtain FieldSet and Field */
        JFieldSet<X> mySet = pElement.getFieldSet();
        JDataField myField = pElement.getField();

        /* If we have a JTextField */
        if (pComponent instanceof JTextField) {
            JModelString<X> myModel = new JModelString<X>(mySet, myField, pClass);
            return new JFieldText<X>((JTextField) pComponent, myModel);
        }
        if (pComponent instanceof JTextArea) {
            JModelString<X> myModel = new JModelString<X>(mySet, myField, pClass);
            return new JFieldArea<X>((JTextArea) pComponent, myModel);
        }
        if (pComponent instanceof JDateDayButton) {
            JModelDateDay<X> myModel = new JModelDateDay<X>(mySet, myField, pClass);
            return new JFieldDate<X>((JDateDayButton) pComponent, myModel);
        }
        if (pComponent instanceof JCheckBox) {
            JModelBoolean<X> myModel = new JModelBoolean<X>(mySet, myField, pClass);
            return new JFieldCheck<X>((JCheckBox) pComponent, myModel);
        }

        /* Handle invalid component */
        throw new IllegalArgumentException("Invalid component class: "
                                           + pComponent.getClass());
    }

    /**
     * Derive component.
     * @param <I> ComboBox element type
     * @param <X> Data item type
     * @param pElement the element
     * @param pComboBox the comboBox
     * @param pClass the class of the comboBox elements.
     * @return the field component
     */
    protected static <I, X extends JFieldSetItem> JFieldComponent<X> deriveComponent(final JFieldElement<X> pElement,
                                                                                     final JComboBox<I> pComboBox,
                                                                                     final Class<I> pClass) {
        /* Obtain FieldSet and Field */
        JFieldSet<X> mySet = pElement.getFieldSet();
        JDataField myField = pElement.getField();

        /* Allocate component */
        JModelObject<I, X> myModel = new JModelObject<I, X>(mySet, myField, pClass);
        return new JFieldCombo<I, X>(pComboBox, myModel);
    }

    /**
     * Constructor.
     * @param pComponent the component
     * @param pModel the data model.
     */
    private JFieldComponent(final JComponent pComponent,
                            final JFieldModel<T> pModel) {
        /* Store the parameters */
        theComponent = pComponent;
        theModel = pModel;

        /* Store the standard border */
        theBorder = pComponent.getBorder();
    }

    /**
     * Render the data.
     * @param pRender the render data.
     * @param pItem the item
     */
    protected void renderData(final JFieldData pRender,
                              final T pItem) {
        /* Obtain details from render data */
        Color myFore = pRender.getForeGround();
        Color myBack = pRender.getBackGround();
        String myTip = pRender.getToolTip();
        Font myFont = pRender.getFont();
        JFieldState myState = pRender.getState();

        /* Set colours */
        theComponent.setForeground(myFore);
        if (!(theComponent instanceof JButton)) {
            theComponent.setBackground(myBack);
        }
        theComponent.setToolTipText(myTip);
        theComponent.setFont(myFont);

        /* Set the appropriate border */
        JComponent myComp = (theScroll != null) ? theScroll : theComponent;
        myComp.setBorder(myState.isError() ? pRender.getErrorBorder() : theBorder);

        /* Display the data */
        displayField();

        /* Enable the component */
        theComponent.setEnabled(pItem.isEditable());
    }

    /**
     * Render the null data.
     */
    protected void renderNullData() {
        /* Set colours and toolTip */
        if (!(theComponent instanceof JButton)) {
            theComponent.setBackground(Color.white);
        }
        theComponent.setToolTipText(null);

        /* Set the appropriate border */
        JComponent myComp = (theScroll != null) ? theScroll : theComponent;
        myComp.setBorder(theBorder);

        /* Display the data */
        displayField();

        /* Disable the component */
        theComponent.setEnabled(false);
    }

    /**
     * Set visible.
     * @param setVisible true/false
     */
    protected void setVisible(final boolean setVisible) {
        /* If we have a scroll pane */
        if (theScroll != null) {
            /* Set the visibility of the scroll */
            theScroll.setVisible(setVisible);

            /* else not a scroll */
        } else {
            /* Set the visibility of the component */
            theComponent.setVisible(setVisible);
        }
    }

    /**
     * display the field.
     */
    protected abstract void displayField();

    /**
     * The JTextField implementation.
     * @param <T> the Data Item type
     */
    protected static final class JFieldText<T extends JFieldSetItem>
            extends JFieldComponent<T> {
        /**
         * The Component.
         */
        private final JTextField theComponent;

        /**
         * The DataModel.
         */
        private final JModelString<T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        private JFieldText(final JTextField pComponent,
                           final JModelString<T> pModel) {
            /* Call super-constructor */
            super(pComponent, pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Create the listener and attach it */
            StringListener myListener = new StringListener();
            pComponent.addActionListener(myListener);
            pComponent.addFocusListener(myListener);

            /* Set correct alignment */
            int iAlignment = pModel.isFixedWidth() ? SwingConstants.RIGHT : SwingConstants.LEFT;
            pComponent.setHorizontalAlignment(iAlignment);
        }

        @Override
        protected void displayField() {
            /* Access display Text from model */
            String myDisplay = theModel.getDisplayString();

            /* Display it */
            theComponent.setText(myDisplay);
        }

        /**
         * Handle actions and focus change.
         */
        private final class StringListener
                extends FocusAdapter
                implements ActionListener {
            /**
             * Cached color.
             */
            private Color theCacheColor;

            @Override
            public void focusGained(final FocusEvent e) {
                startEdit();
            }

            @Override
            public void focusLost(final FocusEvent e) {
                finishEdit();
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                /* Check for finish of edit */
                finishEdit();

                /* Restart the edit, since we have not exited the field */
                startEdit();
            }

            /**
             * startEdit.
             */
            private void startEdit() {
                /* Show the edit text */
                theComponent.setText(theModel.getEditString());
            }

            /**
             * finishEdit.
             */
            private void finishEdit() {
                /* Clear any toolTip text */
                theComponent.setToolTipText(null);

                /* If we have a cached colour */
                if (theCacheColor != null) {
                    /* Restore colour and reset cache */
                    theComponent.setForeground(theCacheColor);
                    theCacheColor = null;
                }

                /* Process the new value */
                boolean bChange = theModel.processValue(theComponent.getText());

                /* If we have an invalid value */
                if (theModel.isError()) {
                    /* Cache the existing colour */
                    theCacheColor = theComponent.getForeground();

                    /* If the object is invalid */
                    theComponent.setToolTipText("Invalid Value");
                    theComponent.setForeground(Color.red);

                    /* Re-acquire the focus */
                    theComponent.requestFocusInWindow();

                    /* else if the value has not changed */
                } else if (!bChange) {
                    /* Redisplay the field */
                    displayField();
                }
            }
        }
    }

    /**
     * The JTextArea implementation.
     * @param <T> the Data Item type
     */
    protected static final class JFieldArea<T extends JFieldSetItem>
            extends JFieldComponent<T> {

        @Override
        protected JTextArea getComponent() {
            return (JTextArea) super.getComponent();
        }

        @Override
        protected JModelString<T> getModel() {
            return (JModelString<T>) super.getModel();
        }

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        private JFieldArea(final JTextArea pComponent,
                           final JModelString<T> pModel) {
            /* Call super-constructor */
            super(pComponent, pModel);
        }

        @Override
        protected void displayField() {
            /* Access display Text from model */
            String myDisplay = getModel().getDisplayString();

            /* Display it */
            getComponent().setText(myDisplay);
        }
    }

    /**
     * The JComboBox implementation.
     * @param <I> ComboBox element type
     */
    protected static final class JFieldCombo<I, T extends JFieldSetItem>
            extends JFieldComponent<T> {
        /**
         * The Component.
         */
        private final JComboBox<I> theComponent;

        /**
         * The DataModel.
         */
        private final JModelObject<I, T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        private JFieldCombo(final JComboBox<I> pComponent,
                            final JModelObject<I, T> pModel) {
            /* Call super-constructor */
            super(pComponent, pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Create the listener and attach it */
            ComboListener myListener = new ComboListener();
            pComponent.addItemListener(myListener);
        }

        @Override
        protected void displayField() {
            /* Access value from model */
            I myValue = theModel.getValue();

            /* Display it */
            if (myValue != null) {
                theComponent.setSelectedItem(myValue);
            } else {
                theComponent.setSelectedIndex(-1);
            }
        }

        /**
         * ComboListener class.
         */
        private final class ComboListener
                implements ItemListener {

            @Override
            public void itemStateChanged(final ItemEvent evt) {
                /* Ignore selection if not selecting item */
                if (evt.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }

                /* Set the new value */
                theModel.processValue(theComponent.getSelectedItem());
            }
        }
    }

    /**
     * The JDateDayButton implementation.
     * @param <T> the Data Item type
     */
    private static class JFieldDate<T extends JFieldSetItem>
            extends JFieldComponent<T> {
        /**
         * The Component.
         */
        private final JDateDayButton theComponent;

        /**
         * The DataModel.
         */
        private final JModelDateDay<T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        protected JFieldDate(final JDateDayButton pComponent,
                             final JModelDateDay<T> pModel) {
            /* Call super-constructor */
            super(pComponent, pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Create the listener and attach it */
            DateListener myListener = new DateListener();
            pComponent.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, myListener);
        }

        @Override
        protected void displayField() {
            /* Access value from model */
            JDateDay myValue = theModel.getValue();

            /* Display it */
            theComponent.setSelectedDateDay(myValue);
        }

        /**
         * DateListener class.
         */
        private final class DateListener
                implements PropertyChangeListener {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                /* Set the new value */
                theModel.processValue(theComponent.getSelectedDateDay());
            }
        }
    }

    /**
     * The JCheckBox implementation.
     * @param <T> the Data Item type
     */
    private static class JFieldCheck<T extends JFieldSetItem>
            extends JFieldComponent<T> {
        /**
         * The Component.
         */
        private final JCheckBox theComponent;

        /**
         * The DataModel.
         */
        private final JModelBoolean<T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        protected JFieldCheck(final JCheckBox pComponent,
                              final JModelBoolean<T> pModel) {
            /* Call super-constructor */
            super(pComponent, pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Create the listener and attach it */
            BooleanListener myListener = new BooleanListener();
            pComponent.addItemListener(myListener);
        }

        @Override
        protected void displayField() {
            /* Access value from model */
            Boolean myValue = theModel.getValue();

            /* Display it */
            theComponent.setSelected((myValue != null)
                                     && myValue);
        }

        /**
         * BooleanListener class.
         */
        private final class BooleanListener
                implements ItemListener {
            @Override
            public void itemStateChanged(final ItemEvent evt) {
                /* Set the new value */
                theModel.processValue(theComponent.isSelected());
            }
        }
    }
}
