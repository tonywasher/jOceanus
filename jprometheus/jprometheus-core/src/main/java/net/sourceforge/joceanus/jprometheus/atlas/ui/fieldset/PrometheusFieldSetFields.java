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
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;

/**
 * FieldSet Panel.
 * @param <T> the item type
 */
public class PrometheusFieldSetFields<T>
        implements PrometheusFieldSetPanel<T> {
    /**
     * The gui factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<T> theFieldSet;

    /**
     * The holding panel.
     */
    private final TethysUIBorderPaneManager thePane;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

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
    PrometheusFieldSetFields(final TethysUIFactory<?> pFactory,
                             final PrometheusFieldSet<T> pFieldSet) {
        /* Store the factory */
        theFactory = pFactory;
        theFieldSet = pFieldSet;

        /* Create the panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        thePane = myPanes.newBorderPane();
        thePanel = myPanes.newVBoxPane();
        thePane.setNorth(thePanel);

        /* Create the maps */
        theElements = new HashMap<>();
        theValues = new HashMap<>();
    }


    @Override
    public TethysUIComponent getUnderlying() {
        return thePane;
    }

    /**
     * Add field to panel.
     * @param pFieldId the fieldId
     * @param pField the edit field
     * @param pValueFactory the valueFactory
     */
    public void addField(final PrometheusDataFieldId pFieldId,
                         final TethysUIDataEditField<?> pField,
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
        pField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theFieldSet.newData(pFieldId, e.getDetails()));
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

    @Override
    public boolean isVisible() {
        /* Loop through the elements looking for a visible element */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            if (myElement.isVisible()) {
                return true;
            }
        }

        /* No visible element found */
        return false;
    }

    @Override
    public void setItem(final T pItem) {
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

    @Override
    public void adjustChanged() {
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

    @Override
    public void setEditable(final boolean isEditable) {
        /* Loop through the elements */
        for (PrometheusFieldSetElement<?> myElement: theElements.values()) {
            myElement.setEditable(isEditable);
        }
    }

    @Override
    public void setEditable(final PrometheusDataFieldId pFieldId,
                            final boolean pEditable) {
        /* adjust the element */
        final PrometheusFieldSetElement<?> myElement = theElements.get(pFieldId);
        myElement.setEditable(pEditable);
    }

    @Override
    public void setVisible(final PrometheusDataFieldId pFieldId,
                           final boolean pVisible) {
        /* adjust the element */
        final PrometheusFieldSetElement<?> myElement = theElements.get(pFieldId);
        myElement.setVisible(pVisible);
    }
}
