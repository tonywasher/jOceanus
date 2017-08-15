/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.field.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldModel;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldModel.TethysFieldModelDate;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldModel.TethysFieldModelObject;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldModel.TethysFieldModelObjectList;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldModel.TethysFieldModelString;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTextArea;

/**
 * Component classes for jFieldSet.
 * @param <T> the Data Item type
 */
public abstract class MetisFieldComponent<T extends MetisFieldSetItem> {
    /**
     * The Scroll Component.
     */
    private JScrollPane theScroll;

    /**
     * The Component.
     */
    private final JComponent theComponent;

    /**
     * The ReadOnly Label.
     */
    private final JLabel theReadOnlyLabel;

    /**
     * The DataModel.
     */
    private final MetisFieldModel<T> theModel;

    /**
     * The Standard border.
     */
    private final Border theBorder;

    /**
     * Constructor.
     * @param pComponent the component
     * @param pModel the data model.
     */
    protected MetisFieldComponent(final JComponent pComponent,
                                  final MetisFieldModel<T> pModel) {
        /* Store the parameters */
        theComponent = pComponent;
        theModel = pModel;

        /* Store the standard border */
        theBorder = pComponent.getBorder();

        /* Create a readOnly label */
        theReadOnlyLabel = new JLabel();
    }

    /**
     * Obtain component.
     * @return the component
     */
    protected JComponent getComponent() {
        return theScroll == null
                                 ? theComponent
                                 : theScroll;
    }

    /**
     * Obtain underlying component.
     * @return the component
     */
    protected JComponent getUnderlyingComponent() {
        return theComponent;
    }

    /**
     * Obtain readOnly label.
     * @return the label
     */
    protected JLabel getReadOnlyLabel() {
        return theReadOnlyLabel;
    }

    /**
     * Obtain model.
     * @return the model
     */
    protected MetisFieldModel<T> getModel() {
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
     * Derive textField component.
     * @param <X> Data item type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pTextField the textField
     * @param pClass the class of item.
     * @return the field component
     */
    protected static <X extends MetisFieldSetItem> MetisFieldComponent<X> deriveComponent(final MetisFieldSet<X> pFieldSet,
                                                                                          final MetisField pField,
                                                                                          final TethysSwingStringTextField pTextField,
                                                                                          final MetisDataType pClass) {
        /* Allocate component */
        final TethysFieldModelString<X> myModel = new TethysFieldModelString<>(pFieldSet, pField, pClass);
        return new MetisFieldText<>(pTextField, myModel);
    }

    /**
     * Derive scrollPane component.
     * @param <X> Data item type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pScrollPane the scrollPane
     * @param pClass the data type
     * @return the field component
     */
    protected static <X extends MetisFieldSetItem> MetisFieldComponent<X> deriveComponent(final MetisFieldSet<X> pFieldSet,
                                                                                          final MetisField pField,
                                                                                          final TethysSwingScrollPaneManager pScrollPane,
                                                                                          final MetisDataType pClass) {
        /* Split into scrollPane and child */
        final JScrollPane myScroll = (JScrollPane) pScrollPane.getNode();
        final TethysNode<JComponent> myComp = pScrollPane.getContent();

        /* Handle invalid child */
        if (myComp == null) {
            throw new IllegalArgumentException("Null ScrollPane view");
        }
        if (!(myComp instanceof TethysSwingTextArea)) {
            throw new IllegalArgumentException("Invalid ScrollPane view class: "
                                               + myComp.getClass());
        }

        /* Derive component based on child and record scroll pane */
        final MetisFieldComponent<X> myResult = deriveComponent(pFieldSet, pField, (TethysSwingTextArea) myComp, pClass);
        myResult.setScroll(myScroll);

        /* Return the result */
        return myResult;
    }

    /**
     * Derive textArea component.
     * @param <X> Data item type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pTextArea the textArea
     * @param pClass the class of item.
     * @return the field component
     */
    private static <X extends MetisFieldSetItem> MetisFieldComponent<X> deriveComponent(final MetisFieldSet<X> pFieldSet,
                                                                                        final MetisField pField,
                                                                                        final TethysSwingTextArea pTextArea,
                                                                                        final MetisDataType pClass) {
        /* Allocate component */
        final TethysFieldModelString<X> myModel = new TethysFieldModelString<>(pFieldSet, pField, pClass);
        return new MetisFieldArea<>(pTextArea, myModel);
    }

    /**
     * Derive dateButton component.
     * @param <X> Data item type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pButton the button
     * @return the field component
     */
    protected static <X extends MetisFieldSetItem> MetisFieldComponent<X> deriveComponent(final MetisFieldSet<X> pFieldSet,
                                                                                          final MetisField pField,
                                                                                          final TethysSwingDateButtonManager pButton) {
        /* Allocate component */
        final TethysFieldModelDate<X> myModel = new TethysFieldModelDate<>(pFieldSet, pField);
        return new MetisFieldDate<>(pButton, myModel);
    }

    /**
     * Derive scrollButton component.
     * @param <I> ScrollButton element type
     * @param <X> Data item type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pButton the button
     * @param pClazz the class of the button elements.
     * @return the field component
     */
    protected static <I, X extends MetisFieldSetItem> MetisFieldComponent<X> deriveComponent(final MetisFieldSet<X> pFieldSet,
                                                                                             final MetisField pField,
                                                                                             final TethysSwingScrollButtonManager<I> pButton,
                                                                                             final Class<I> pClazz) {
        /* Allocate component */
        final TethysFieldModelObject<I, X> myModel = new TethysFieldModelObject<>(pFieldSet, pField, pClazz);
        return new MetisFieldScrollButton<>(pButton, myModel);
    }

    /**
     * Derive listButton component.
     * @param <I> ListButton element type
     * @param <X> Data item type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pButton the button
     * @return the field component
     */
    protected static <I extends Comparable<I>, X extends MetisFieldSetItem> MetisFieldComponent<X> deriveComponent(final MetisFieldSet<X> pFieldSet,
                                                                                                                   final MetisField pField,
                                                                                                                   final TethysSwingListButtonManager<I> pButton) {
        /* Allocate component */
        final TethysFieldModelObjectList<I, X> myModel = new TethysFieldModelObjectList<>(pFieldSet, pField);
        return new MetisFieldScrollListButton<>(pButton, myModel);
    }

    /**
     * Derive iconButton component.
     * @param <I> IconButton element type
     * @param <X> Data item type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pButton the button
     * @param pClazz the class of the button elements.
     * @return the field component
     */
    protected static <I, X extends MetisFieldSetItem> MetisFieldComponent<X> deriveComponent(final MetisFieldSet<X> pFieldSet,
                                                                                             final MetisField pField,
                                                                                             final TethysSwingIconButtonManager<I> pButton,
                                                                                             final Class<I> pClazz) {
        /* Allocate component */
        final TethysFieldModelObject<I, X> myModel = new TethysFieldModelObject<>(pFieldSet, pField, pClazz);
        return new MetisFieldIconButton<>(pButton, myModel);
    }

    /**
     * Render the data.
     * @param pRender the render data.
     * @param pItem the item
     */
    protected void renderData(final MetisFieldData pRender,
                              final T pItem) {
        /* Obtain details from render data */
        final Color myFore = pRender.getForeGround();
        final Color myBack = pRender.getBackGround();
        final String myTip = pRender.getToolTip();
        final Font myFont = pRender.getFont();
        final MetisFieldState myState = pRender.getState();

        /* Set colours */
        theComponent.setForeground(myFore);
        if (!(theComponent instanceof JButton)) {
            theComponent.setBackground(myBack);
        }
        theComponent.setToolTipText(myTip);
        theComponent.setFont(myFont);

        /* Set the appropriate border */
        final JComponent myComp = (theScroll != null)
                                                      ? theScroll
                                                      : theComponent;
        myComp.setBorder(myState.isError()
                                           ? pRender.getErrorBorder()
                                           : theBorder);

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
        final JComponent myComp = (theScroll != null)
                                                      ? theScroll
                                                      : theComponent;
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
     * The TextField implementation.
     * @param <T> the Data Item type
     */
    protected static final class MetisFieldText<T extends MetisFieldSetItem>
            extends MetisFieldComponent<T> {
        /**
         * The Component.
         */
        private final JTextField theComponent;

        /**
         * The DataModel.
         */
        private final TethysFieldModelString<T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        private MetisFieldText(final TethysSwingStringTextField pComponent,
                               final TethysFieldModelString<T> pModel) {
            /* Call super-constructor */
            super(pComponent.getEditControl(), pModel);

            /* Store parameters */
            theComponent = pComponent.getEditControl();
            theModel = pModel;
            pComponent.setEditable(true);

            /* Create the listener and attach it */
            final StringListener myListener = new StringListener();
            theComponent.addActionListener(myListener);
            theComponent.addFocusListener(myListener);

            /* Set correct alignment */
            final int iAlignment = pModel.isFixedWidth()
                                                         ? SwingConstants.RIGHT
                                                         : SwingConstants.LEFT;
            theComponent.setHorizontalAlignment(iAlignment);
        }

        @Override
        protected void displayField() {
            /* Access display Text from model */
            final String myDisplay = theModel.getDisplayString();

            /* Display it */
            theComponent.setText(myDisplay);
            getReadOnlyLabel().setText(myDisplay);
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
                final boolean bChange = theModel.processValue(theComponent.getText());

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
     * The TextArea implementation.
     * @param <T> the Data Item type
     */
    protected static final class MetisFieldArea<T extends MetisFieldSetItem>
            extends MetisFieldComponent<T> {
        /**
         * The Component.
         */
        private final JTextArea theComponent;

        /**
         * The DataModel.
         */
        private final TethysFieldModelString<T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        private MetisFieldArea(final TethysSwingTextArea pComponent,
                               final TethysFieldModelString<T> pModel) {
            /* Call super-constructor */
            super(pComponent.getNode(), pModel);

            /* Store parameters */
            theComponent = getUnderlyingComponent();
            theModel = pModel;
            theComponent.setEditable(true);

            /* Create the listener and attach it */
            final StringListener myListener = new StringListener();
            theComponent.addFocusListener(myListener);
        }

        @Override
        protected JTextArea getUnderlyingComponent() {
            return (JTextArea) super.getUnderlyingComponent();
        }

        @Override
        protected TethysFieldModelString<T> getModel() {
            return (TethysFieldModelString<T>) super.getModel();
        }

        @Override
        protected void displayField() {
            /* Access display Text from model */
            final String myDisplay = getModel().getDisplayString();

            /* Display it */
            getUnderlyingComponent().setText(myDisplay);
            getReadOnlyLabel().setText(myDisplay);
        }

        /**
         * Handle focus change.
         */
        private final class StringListener
                extends FocusAdapter {
            /**
             * Cached colour.
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
                final boolean bChange = theModel.processValue(theComponent.getText());

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
     * The DateButton implementation.
     * @param <T> the Data Item type
     */
    private static class MetisFieldDate<T extends MetisFieldSetItem>
            extends MetisFieldComponent<T> {
        /**
         * The Component.
         */
        private final TethysSwingDateButtonManager theComponent;

        /**
         * The DataModel.
         */
        private final TethysFieldModelDate<T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        protected MetisFieldDate(final TethysSwingDateButtonManager pComponent,
                                 final TethysFieldModelDate<T> pModel) {
            /* Call super-constructor */
            super(pComponent.getNode(), pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Handle new values */
            theComponent.getEventRegistrar().addEventListener(e -> theModel.processValue(theComponent.getSelectedDate()));
        }

        @Override
        protected void displayField() {
            /* Access value from model */
            final TethysDate myValue = theModel.getValue();

            /* Display it */
            theComponent.setSelectedDate(myValue);
            getReadOnlyLabel().setText(theComponent.getText());
        }
    }

    /**
     * The ScrollButton implementation.
     * @param <I> Button element type
     * @param <T> the Data Item type
     */
    private static class MetisFieldScrollButton<I, T extends MetisFieldSetItem>
            extends MetisFieldComponent<T> {
        /**
         * The Component.
         */
        private final TethysSwingScrollButtonManager<I> theComponent;

        /**
         * The DataModel.
         */
        private final TethysFieldModelObject<I, T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        protected MetisFieldScrollButton(final TethysSwingScrollButtonManager<I> pComponent,
                                         final TethysFieldModelObject<I, T> pModel) {
            /* Call super-constructor */
            super(pComponent.getNode(), pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Handle new Values */
            final TethysEventRegistrar<TethysUIEvent> myRegistrar = theComponent.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> theModel.processValue(theComponent.getValue()));
        }

        @Override
        protected void displayField() {
            /* Access value from model */
            final I myValue = theModel.getValue();

            /* Display it */
            theComponent.setValue(myValue);
            getReadOnlyLabel().setText(myValue == null
                                                       ? null
                                                       : myValue.toString());
        }
    }

    /**
     * The ListButton implementation.
     * @param <I> Button element type
     * @param <T> the Data Item type
     */
    private static class MetisFieldScrollListButton<I extends Comparable<I>, T extends MetisFieldSetItem>
            extends MetisFieldComponent<T> {
        /**
         * The Component.
         */
        private final TethysSwingListButtonManager<I> theComponent;

        /**
         * The DataModel.
         */
        private final TethysFieldModelObjectList<I, T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        protected MetisFieldScrollListButton(final TethysSwingListButtonManager<I> pComponent,
                                             final TethysFieldModelObjectList<I, T> pModel) {
            /* Call super-constructor */
            super(pComponent.getNode(), pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Handle new values */
            theComponent.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, theModel::processValue);
        }

        @Override
        protected void displayField() {
            /* Access value from model */
            final List<I> myValue = theModel.getValue();

            /* Display it */
            theComponent.setValue(myValue);
            getReadOnlyLabel().setText(myValue == null
                                                       ? null
                                                       : myValue.toString());
        }
    }

    /**
     * The IconButton implementation.
     * @param <I> Button element type
     * @param <T> the Data Item type
     */
    private static class MetisFieldIconButton<I, T extends MetisFieldSetItem>
            extends MetisFieldComponent<T> {
        /**
         * The Component.
         */
        private final TethysSwingIconButtonManager<I> theComponent;

        /**
         * The DataModel.
         */
        private final TethysFieldModelObject<I, T> theModel;

        /**
         * Constructor.
         * @param pComponent the component.
         * @param pModel the data model.
         */
        protected MetisFieldIconButton(final TethysSwingIconButtonManager<I> pComponent,
                                       final TethysFieldModelObject<I, T> pModel) {
            /* Call super-constructor */
            super(pComponent.getNode(), pModel);

            /* Store parameters */
            theComponent = pComponent;
            theModel = pModel;

            /* Handle new values */
            theComponent.getEventRegistrar().addEventListener(e -> theModel.processValue(theComponent.getValue()));
        }

        @Override
        protected void displayField() {
            /* Access value from model */
            final I myValue = theModel.getValue();

            /* Display it */
            theComponent.setValue(myValue);
            final Icon myIcon = ((JButton) theComponent.getNode()).getIcon();
            if (myIcon != null) {
                getReadOnlyLabel().setIcon(myIcon);
            }
        }
    }
}
