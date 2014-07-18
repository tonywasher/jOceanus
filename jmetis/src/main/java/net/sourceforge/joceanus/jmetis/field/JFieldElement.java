/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.field;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;

/**
 * Field Set. This handles a fields for an item, populating the field, rendering and parsing the data.
 * @param <T> the Data Item type
 */
public class JFieldElement<T extends JFieldSetItem> {
    /**
     * The fieldSet.
     */
    private final JFieldSet<T> theFieldSet;

    /**
     * The field.
     */
    private final JDataField theField;

    /**
     * The label.
     */
    private final JComponent theLabel;

    /**
     * The component.
     */
    private final JFieldComponent<T> theComponent;

    /**
     * The model.
     */
    private final JFieldModel<T> theModel;

    /**
     * Is the element visible?
     */
    private boolean isVisible = true;

    /**
     * Obtain FieldSet.
     * @return the fieldSet
     */
    protected JFieldSet<T> getFieldSet() {
        return theFieldSet;
    }

    /**
     * Obtain Field.
     * @return the field
     */
    protected JDataField getField() {
        return theField;
    }

    /**
     * is the value fixed width?
     * @return true/false
     */
    protected boolean isFixedWidth() {
        return theModel.isFixedWidth();
    }

    /**
     * Constructor.
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the data type of the value
     * @param pComponent the component
     */
    protected JFieldElement(final JFieldSet<T> pFieldSet,
                            final JDataField pField,
                            final DataType pClass,
                            final JComponent pComponent) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the component */
        theComponent = JFieldComponent.deriveComponent(this, pComponent, pClass);

        /* Access the name of the field */
        String myName = pField.getName();

        /* If the component is a CheckBox */
        if (pComponent instanceof JCheckBox) {
            /* We do not have a label */
            theLabel = null;

            /* Set text of checkBox */
            ((JCheckBox) pComponent).setText(myName);

            /* Else standard case */
        } else {
            /* Create the label */
            theLabel = new JLabel(myName + ":", SwingConstants.TRAILING);
        }

        /* Access the model */
        theModel = theComponent.getModel();
    }

    /**
     * Constructor.
     * @param <I> ComboBox element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the combo box elements
     * @param pComboBox the comboBox
     */
    protected <I> JFieldElement(final JFieldSet<T> pFieldSet,
                                final JDataField pField,
                                final Class<I> pClass,
                                final JComboBox<I> pComboBox) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the label */
        String myName = pField.getName();
        theLabel = new JLabel(myName + ":", SwingConstants.TRAILING);

        /* Create the component */
        theComponent = JFieldComponent.deriveComponent(this, pComboBox, pClass);

        /* Access the model */
        theModel = theComponent.getModel();
    }

    /**
     * Constructor.
     * @param <I> ScrollButton element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the combo box elements
     * @param pButton the scroll button
     */
    protected <I> JFieldElement(final JFieldSet<T> pFieldSet,
                                final JDataField pField,
                                final Class<I> pClass,
                                final JScrollButton<I> pButton) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the label */
        String myName = pField.getName();
        theLabel = new JLabel(myName + ":", SwingConstants.TRAILING);

        /* Create the component */
        theComponent = JFieldComponent.deriveComponent(this, pButton, pClass);

        /* Access the model */
        theModel = theComponent.getModel();
    }

    /**
     * Add element to panel.
     * @param pPanel the panel to add to
     */
    protected void addToPanel(final JPanel pPanel) {
        /* Add label if it exists */
        if (theLabel != null) {
            pPanel.add(theLabel);
        }

        /* Add the actual element */
        theComponent.addToPanel(pPanel);
    }

    /**
     * Set element visibility.
     * @param setVisible true/false.
     */
    protected void setVisibility(final boolean setVisible) {
        /* Record visibility */
        isVisible = setVisible;
    }

    /**
     * Set visible.
     * @param setVisible true/false
     */
    private void setVisible(final boolean setVisible) {
        /* If the label exists */
        if (theLabel != null) {
            /* Set label visibility */
            theLabel.setVisible(setVisible);
        }

        /* Set the visibility of the component */
        theComponent.setVisible(setVisible);
    }

    /**
     * RenderField for normal data.
     * @param pRender the render data.
     * @param pItem the item to render
     */
    protected void renderData(final JFieldData pRender,
                              final T pItem) {
        /* Load data from the item */
        theModel.loadValue(pItem);

        /* Render the component */
        theComponent.renderData(pRender, pItem);

        /* If the label is a button */
        if (theLabel instanceof JButton) {
            /* Enable it */
            theLabel.setEnabled(true);
        }

        /* Set visibility */
        setVisible(isVisible);
    }

    /**
     * RenderField for null data.
     */
    protected void renderNullData() {
        /* Load data from the item */
        theModel.loadNullValue();

        /* Render the component */
        theComponent.renderNullData();

        /* If the label is a button */
        if (theLabel instanceof JButton) {
            /* Disable it */
            theLabel.setEnabled(false);
        }

        /* Set visibility */
        setVisible(isVisible);
    }
}
