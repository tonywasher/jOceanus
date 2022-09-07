/* *****************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;

/**
 * FieldSet Panel.
 * @param <T> the item type
 */
public class PrometheusFieldSetPanel<T>
    implements TethysComponent {
    /**
     * The gui factory.
     */
    private final TethysGuiFactory theFactory;

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<T> theFieldSet;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager thePanel;

    /**
     * The map of elements.
     */
    private final Map<PrometheusDataFieldId, PrometheusFieldSetElement<?>> theElements;

    /**
     * The map of Fields to value factory.
     */
    private final Map<PrometheusDataFieldId, Function<T, Object>> theValues;

    /**
     * The current item.
     */
    private T theItem;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pFieldSet the fieldSet
     */
    PrometheusFieldSetPanel(final TethysGuiFactory pFactory,
                            final PrometheusFieldSet<T> pFieldSet) {
        /* Store the factory */
        theFactory = pFactory;
        theFieldSet = pFieldSet;

        /* Create the panel */
        thePanel = theFactory.newVBoxPane();

        /* Create the maps */
        theElements = new HashMap<>();
        theValues = new HashMap<>();
    }


    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    /**
     * Add field to panel.
     * @param pFieldId the fieldId
     * @param pField the edit field
     * @param pValueFactory the valueFactory
     */
    public void addField(final PrometheusDataFieldId pFieldId,
                         final TethysDataEditField<?> pField,
                         final Function<T, Object> pValueFactory) {
        /* create the element */
        final PrometheusFieldSetElement<?> myElement = new PrometheusFieldSetElement<>(theFactory, pFieldId, pField);
        theElements.put(pFieldId, myElement);
        theValues.put(pFieldId, pValueFactory);

        /* Add to the panel and adjust label widths */
        thePanel.addNode(myElement.getComponent());
        adjustLabelWidth();

        /* Register the field with the fieldSet */
        theFieldSet.registerField(pFieldId, this);

        /* Pass newData event to fieldSet */
        pField.getEventRegistrar().addEventListener(TethysXUIEvent.NEWVALUE, e -> theFieldSet.newData(pFieldId, e.getDetails()));
    }

    /**
     * Adjust the label width.
     */
    private void adjustLabelWidth() {
        /* Initialise counters */
        int myWidth = 0;
        int myHeight = 0;

        /* Loop through the elements getting the max width */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            final int myLabelWidth = myElement.getLabelWidth();
            final int myFieldHeight = myElement.getFieldHeight();
            myWidth = Math.max(myWidth, myLabelWidth);
            myHeight = Math.max(myHeight, myFieldHeight);
        }

        /* Loop through the elements setting the width */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            myElement.setLabelWidth(myWidth);
            myElement.setFieldHeight(myHeight);
        }
    }

    /**
     * Are there any visible elements?
     * @return true/false
     */
    boolean isVisible() {
        /* Loop through the elements looking for a visible element */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            if (myElement.isVisible()) {
                return true;
            }
        }

        /* No visible element found */
        return false;
    }

    /**
     * Set item.
     * @param pItem the item
     */
    void setItem(final T pItem) {
        /* Store the item */
        theItem = pItem;

        /* Loop through the elements */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            /* Obtain the value factory */
            final Function<T, Object> myValueFactory = theValues.get(myElement.getFieldId());
            final Object myValue = myValueFactory.apply(pItem);
            myElement.setValue(myValue);
        }
    }

    /**
     * Adjust changed indications.
     */
    void adjustChanged() {
        /* Ignore if we have no item */
        if (theItem == null) {
            return;
        }

        /* Loop through the elements */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            final boolean isChanged = theFieldSet.isChanged(theItem, myElement.getFieldId());
            myElement.adjustChanged(isChanged);
        }
    }

    /**
     * Set editable item.
     * @param isEditable true/false
     */
    void setEditable(final boolean isEditable) {
        /* Loop through the elements */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            myElement.setEditable(isEditable);
        }
    }

    /**
     * Set editable.
     * @param pFieldId the fieldId
     * @param pEditable true/false
     */
    void setEditable(final PrometheusDataFieldId pFieldId,
                     final boolean pEditable) {
        /* adjust the element */
        final PrometheusFieldSetElement<?> myElement = theElements.get(pFieldId);
        myElement.setEditable(pEditable);
    }

    /**
     * Set visible.
     * @param pFieldId the fieldId
     * @param pVisible true/false
     */
    void setVisible(final PrometheusDataFieldId pFieldId,
                    final boolean pVisible) {
        /* adjust the element */
        final PrometheusFieldSetElement<?> myElement = theElements.get(pFieldId);
        myElement.setVisible(pVisible);
    }
}
