/*******************************************************************************
 * jFieldSet: Java Swing Field Set
 * Copyright 2012 Tony Washer
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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;

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
     * @param pLabel the label for the component
     * @param pComponent the component
     */
    protected JFieldElement(final JFieldSet<T> pFieldSet,
                            final JDataField pField,
                            final DataType pClass,
                            final JComponent pLabel,
                            final JComponent pComponent) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;
        theLabel = pLabel;

        /* Create the component */
        theComponent = JFieldComponent.deriveComponent(this, pComponent, pClass);

        /* Access the model */
        theModel = theComponent.getModel();
    }

    /**
     * Constructor.
     * @param <I> ComboBox element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the combo box elements
     * @param pLabel the label for the component
     * @param pComboBox the comboBox
     */
    protected <I> JFieldElement(final JFieldSet<T> pFieldSet,
                                final JDataField pField,
                                final Class<I> pClass,
                                final JComponent pLabel,
                                final JComboBox<I> pComboBox) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;
        theLabel = pLabel;

        /* Create the component */
        theComponent = JFieldComponent.deriveComponent(this, pComboBox, pClass);

        /* Access the model */
        theModel = theComponent.getModel();
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
