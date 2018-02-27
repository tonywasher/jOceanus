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

import java.awt.CardLayout;
import java.util.Currency;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldModel;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldModel.TethysFieldModelString;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;

/**
 * Field Set. This handles a fields for an item, populating the field, rendering and parsing the
 * data.
 * @param <T> the Data Item type
 */
public class MetisSwingFieldElement<T extends MetisFieldSetItem> {
    /**
     * The colon indicator.
     */
    public static final String STR_COLON = ":";

    /**
     * The fieldSet.
     */
    private final MetisSwingFieldSet<T> theFieldSet;

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
    private final MetisSwingFieldComponent<T> theComponent;

    /**
     * The model.
     */
    private final MetisLetheFieldModel<T> theModel;

    /**
     * Is the element visible?
     */
    private boolean isVisible;

    /**
     * Constructor for textField.
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the data type of the value
     * @param pTextField the textField
     */
    protected MetisSwingFieldElement(final MetisSwingFieldSet<T> pFieldSet,
                                     final MetisField pField,
                                     final MetisDataType pClass,
                                     final TethysSwingStringTextField pTextField) {
        /* Initialise with correct component */
        this(pFieldSet, pField, MetisSwingFieldComponent.deriveComponent(pFieldSet, pField, pTextField, pClass));
    }

    /**
     * Constructor for scrollPane.
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the data type of the value
     * @param pScrollPane the scrollPane
     */
    protected MetisSwingFieldElement(final MetisSwingFieldSet<T> pFieldSet,
                                     final MetisField pField,
                                     final MetisDataType pClass,
                                     final TethysSwingScrollPaneManager pScrollPane) {
        /* Initialise with correct component */
        this(pFieldSet, pField, MetisSwingFieldComponent.deriveComponent(pFieldSet, pField, pScrollPane, pClass));
    }

    /**
     * Constructor for dateButton.
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pButton the date button
     */
    protected MetisSwingFieldElement(final MetisSwingFieldSet<T> pFieldSet,
                                     final MetisField pField,
                                     final TethysSwingDateButtonManager pButton) {
        /* Initialise with correct component */
        this(pFieldSet, pField, MetisSwingFieldComponent.deriveComponent(pFieldSet, pField, pButton));
    }

    /**
     * Constructor.
     * @param <I> ScrollButton element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the button elements
     * @param pButton the scroll button
     */
    protected <I> MetisSwingFieldElement(final MetisSwingFieldSet<T> pFieldSet,
                                         final MetisField pField,
                                         final Class<I> pClass,
                                         final TethysSwingScrollButtonManager<I> pButton) {
        /* Initialise with correct component */
        this(pFieldSet, pField, MetisSwingFieldComponent.deriveComponent(pFieldSet, pField, pButton, pClass));
    }

    /**
     * Constructor.
     * @param <I> ListButton element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pButton the scroll button
     */
    protected <I extends Comparable<I>> MetisSwingFieldElement(final MetisSwingFieldSet<T> pFieldSet,
                                                               final MetisField pField,
                                                               final TethysSwingListButtonManager<I> pButton) {
        /* Initialise with correct component */
        this(pFieldSet, pField, MetisSwingFieldComponent.deriveComponent(pFieldSet, pField, pButton));
    }

    /**
     * Constructor.
     * @param <I> IconButton element type
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pClass the class of the button elements
     * @param pButton the icon button
     */
    protected <I> MetisSwingFieldElement(final MetisSwingFieldSet<T> pFieldSet,
                                         final MetisField pField,
                                         final Class<I> pClass,
                                         final TethysSwingIconButtonManager<I> pButton) {
        /* Initialise with correct component */
        this(pFieldSet, pField, MetisSwingFieldComponent.deriveComponent(pFieldSet, pField, pButton, pClass));
    }

    /**
     * Constructor.
     * @param pFieldSet the field set
     * @param pField the field id
     * @param pComponent the component
     */
    protected MetisSwingFieldElement(final MetisSwingFieldSet<T> pFieldSet,
                                     final MetisField pField,
                                     final MetisSwingFieldComponent<T> pComponent) {
        /* Store parameters */
        theFieldSet = pFieldSet;
        theField = pField;
        theComponent = pComponent;

        /* Create the component */
        isVisible = true;

        /* Create the label */
        final String myName = pField.getName();
        theLabel = new JLabel(myName + STR_COLON, SwingConstants.TRAILING);

        /* Access the model */
        theModel = theComponent.getModel();

        /* Create card panel */
        theCardPanel = new MetisFieldCardPanel();
    }

    /**
     * Obtain FieldSet.
     * @return the fieldSet
     */
    protected MetisSwingFieldSet<T> getFieldSet() {
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
     * is the element visible?
     * @return true/false.
     */
    protected boolean isVisible() {
        return isVisible;
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
        if (theModel instanceof TethysFieldModelString) {
            /* Pass call onwards */
            final TethysFieldModelString<?> myModel = (TethysFieldModelString<?>) theModel;
            myModel.setAssumedCurrency(pCurrency);
        }
    }

    /**
     * RenderField for normal data.
     * @param pRender the render data.
     * @param pItem the item to render
     */
    protected void renderData(final MetisSwingFieldData pRender,
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
            final JComponent myComponent = theComponent.getComponent();
            final JLabel myLabel = theComponent.getReadOnlyLabel();

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
