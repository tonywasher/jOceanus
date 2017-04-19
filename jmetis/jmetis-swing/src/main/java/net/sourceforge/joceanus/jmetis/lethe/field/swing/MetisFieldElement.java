/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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

import java.awt.CardLayout;
import java.util.Currency;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldModel;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldModel.JModelString;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollListButton;

/**
 * Field Set. This handles a fields for an item, populating the field, rendering and parsing the
 * data.
 * @param <T> the Data Item type
 */
public class MetisFieldElement<T extends MetisFieldSetItem> {
    /**
     * The colon indicator.
     */
    public static final String STR_COLON = ":";

    /**
     * The fieldSet.
     */
    private final MetisFieldSet<T> theFieldSet;

    /**
     * The field.
     */
    private final MetisField theField;

    /**
     * The label.
     */
    private final JComponent theLabel;

    /**
     * The card panel.
     */
    private final MetisFieldCardPanel theCardPanel;

    /**
     * The component.
     */
    private final MetisFieldComponent<T> theComponent;

    /**
     * The model.
     */
    private final MetisFieldModel<T> theModel;

    /**
     * Is the element visible?
     */
    private boolean isVisible = true;

    /**
     * Constructor.
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the data type of the value
     * @param pComponent the component
     */
    protected MetisFieldElement(final MetisFieldSet<T> pFieldSet,
                                final MetisField pField,
                                final MetisDataType pClass,
                                final JComponent pComponent) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the component */
        theComponent = MetisFieldComponent.deriveComponent(this, pComponent, pClass);

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
            theLabel = new JLabel(myName + STR_COLON, SwingConstants.TRAILING);
        }

        /* Access the model */
        theModel = theComponent.getModel();

        /* Create card panel */
        theCardPanel = new MetisFieldCardPanel();
    }

    /**
     * Constructor.
     * @param <I> ComboBox element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the combo box elements
     * @param pComboBox the comboBox
     */
    protected <I> MetisFieldElement(final MetisFieldSet<T> pFieldSet,
                                    final MetisField pField,
                                    final Class<I> pClass,
                                    final JComboBox<I> pComboBox) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the label */
        String myName = pField.getName();
        theLabel = new JLabel(myName + STR_COLON, SwingConstants.TRAILING);

        /* Create the component */
        theComponent = MetisFieldComponent.deriveComponent(this, pComboBox, pClass);

        /* Access the model */
        theModel = theComponent.getModel();

        /* Create card panel */
        theCardPanel = new MetisFieldCardPanel();
    }

    /**
     * Constructor.
     * @param <I> ScrollButton element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the button elements
     * @param pButton the scroll button
     */
    protected <I> MetisFieldElement(final MetisFieldSet<T> pFieldSet,
                                    final MetisField pField,
                                    final Class<I> pClass,
                                    final JScrollButton<I> pButton) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the label */
        String myName = pField.getName();
        theLabel = new JLabel(myName + STR_COLON, SwingConstants.TRAILING);

        /* Create the component */
        theComponent = MetisFieldComponent.deriveComponent(this, pButton, pClass);

        /* Access the model */
        theModel = theComponent.getModel();

        /* Create card panel */
        theCardPanel = new MetisFieldCardPanel();
    }

    /**
     * Constructor.
     * @param <I> ScrollButton element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pButton the scroll button
     */
    protected <I> MetisFieldElement(final MetisFieldSet<T> pFieldSet,
                                    final MetisField pField,
                                    final JScrollListButton<I> pButton) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the label */
        String myName = pField.getName();
        theLabel = new JLabel(myName + STR_COLON, SwingConstants.TRAILING);

        /* Create the component */
        theComponent = MetisFieldComponent.deriveComponent(this, pButton);

        /* Access the model */
        theModel = theComponent.getModel();

        /* Create card panel */
        theCardPanel = new MetisFieldCardPanel();
    }

    /**
     * Constructor.
     * @param <I> IconButton element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the button elements
     * @param pButton the icon button
     */
    protected <I> MetisFieldElement(final MetisFieldSet<T> pFieldSet,
                                    final MetisField pField,
                                    final Class<I> pClass,
                                    final JIconButton<I> pButton) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;

        /* Create the label */
        String myName = pField.getName();
        theLabel = new JLabel(myName + STR_COLON, SwingConstants.TRAILING);

        /* Create the component */
        theComponent = MetisFieldComponent.deriveComponent(this, pButton, pClass);

        /* Access the model */
        theModel = theComponent.getModel();

        /* Create card panel */
        theCardPanel = new MetisFieldCardPanel();
    }

    /**
     * Obtain FieldSet.
     * @return the fieldSet
     */
    protected MetisFieldSet<T> getFieldSet() {
        return theFieldSet;
    }

    /**
     * Obtain Field.
     * @return the field
     */
    protected MetisField getField() {
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
     * Add element to panel.
     * @param pPanel the panel to add to
     */
    protected void addToPanel(final JPanel pPanel) {
        /* Add label if it exists */
        if (theLabel != null) {
            pPanel.add(theLabel);
        }

        /* Add the actual element */
        pPanel.add(theCardPanel);
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
        theCardPanel.setVisible(setVisible);
    }

    /**
     * Set editable.
     * @param setEditable true/false
     */
    protected void setEditable(final boolean setEditable) {
        /* Set the visibility of the component */
        theCardPanel.setEditable(setEditable);
    }

    /**
     * Set the assumed currency for a field.
     * @param pCurrency the assumed currency
     */
    protected void setAssumedCurrency(final Currency pCurrency) {
        /* If the model is a string model */
        if (theModel instanceof JModelString) {
            /* Pass call onwards */
            JModelString<?> myModel = (JModelString<?>) theModel;
            myModel.setAssumedCurrency(pCurrency);
        }
    }

    /**
     * RenderField for normal data.
     * @param pRender the render data.
     * @param pItem the item to render
     */
    protected void renderData(final MetisFieldData pRender,
                              final T pItem) {
        /* Load data from the item */
        theModel.loadValue(pItem);

        /* Render the component */
        theComponent.renderData(pRender, pItem);

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

        /* Set visibility */
        setVisible(isVisible);
    }

    /**
     * Field CardPanel class.
     */
    private final class MetisFieldCardPanel
            extends JPanel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2524938035631511826L;

        /**
         * The Editable name.
         */
        private static final String NAME_EDITABLE = "Editable";

        /**
         * The ReadOnly name.
         */
        private static final String NAME_READONLY = "ReadOnly";

        /**
         * The card layout.
         */
        private final CardLayout theCardLayout;

        /**
         * Constructor.
         */
        private MetisFieldCardPanel() {
            /* Create the card layout */
            theCardLayout = new CardLayout();
            setLayout(theCardLayout);

            /* Access the component and ReadOnly label */
            JComponent myComponent = theComponent.getComponent();
            JLabel myLabel = theComponent.getReadOnlyLabel();

            /* Add the component and ReadOnly label */
            add(myComponent, NAME_EDITABLE);
            add(myLabel, NAME_READONLY);

            /* Set maximum size */
            setMaximumSize(myComponent.getMaximumSize());
        }

        /**
         * Set editable state.
         * @param setEditable true/false
         */
        private void setEditable(final boolean setEditable) {
            theCardLayout.show(this, setEditable
                                                 ? NAME_EDITABLE
                                                 : NAME_READONLY);
        }
    }
}
