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
package net.sourceforge.joceanus.jprometheus.atlas.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldEvent;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldSetBase.MetisLetheFieldUpdate;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;

/**
 * FieldSet.
 * @param <T> the item type
 * @param <F> the fieldId type
 */
public class PrometheusFieldSet<T, F>
        implements TethysEventProvider<TethysXUIEvent> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysXUIEvent> theEventManager;

    /**
     * The map of Fields to panels.
     */
    private final Map<F, PrometheusFieldSetPanel<T, F>> theFieldMap;

    /**
     * The list of panels.
     */
    private final List<PrometheusFieldSetPanel<T, F>> thePanelList;

    /**
     * Is the data being refreshed?
     */
    private boolean isRefreshing;

    /**
     * Constructor.
     */
    public PrometheusFieldSet() {
        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Create maps and lists */
        theFieldMap = new HashMap<>();
        thePanelList = new ArrayList<>();
    }


    @Override
    public TethysEventRegistrar<TethysXUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Add a fieldSet panel.
     * @param pPanel the fieldSetPanel.
     */
    public void addFieldSetPanel(final PrometheusFieldSetPanel<T, F> pPanel) {
        thePanelList.add(pPanel);
    }

    /**
     * Set item.
     * @param pItem the item.
     */
    public void setItem(final T pItem) {
        /* Note that we are refreshing */
        isRefreshing = true;

        /* Update all the panels */
        for (PrometheusFieldSetPanel<T, F> myPanel : thePanelList) {
            myPanel.setItem(pItem);
        }

        /* Note that we have finished refreshing */
        isRefreshing = false;
    }

    /**
     * Register a field.
     * @param pFieldId the field.
     * @param pPanel the panel.
     */
    void registerField(final F pFieldId,
                       final PrometheusFieldSetPanel<T, F> pPanel) {
        theFieldMap.put(pFieldId, pPanel);
    }

    /**
     * Set field visibility.
     * @param pFieldId the field.
     * @param pVisible is visible true/false?
     */
    public void setFieldVisible(final F pFieldId,
                                final boolean pVisible) {
        final PrometheusFieldSetPanel<T, F> myPanel = theFieldMap.get(pFieldId);
        myPanel.setVisible(pFieldId, pVisible);
    }

    /**
     * Set field editability.
     * @param pFieldId the fieldId.
     * @param pEditable is editable true/false?
     */
    public void setFieldEditable(final F pFieldId,
                                 final boolean pEditable) {
        final PrometheusFieldSetPanel<T, F> myPanel = theFieldMap.get(pFieldId);
        myPanel.setEditable(pFieldId, pEditable);
    }

    /**
     * Notify listeners of new data.
     * @param pFieldId the fieldId.
     * @param pNewValue the new value
     */
    void newData(final F pFieldId,
                 final Object pNewValue) {
        /* If we are not refreshing data */
        if (!isRefreshing) {
            /* Create the notification */
            final PrometheusFieldSetEvent<F> myUpdate = new PrometheusFieldSetEvent<>(pFieldId, pNewValue);

            /* Fire the notification */
            theEventManager.fireEvent(TethysXUIEvent.NEWVALUE, myUpdate);
        }
    }

    /**
     * FieldSetEvent.
     */
    public static class PrometheusFieldSetEvent<F> {
        /**
         * The field.
         */
        private final F theFieldId;

        /**
         * The new value.
         */
        private final Object theValue;

        /**
         * Constructor.
         * @param pFieldId the source fieldId
         * @param pNewValue the new Value
         */
        public PrometheusFieldSetEvent(final F pFieldId,
                                       final Object pNewValue) {
            theFieldId = pFieldId;
            theValue = pNewValue;
        }

        /**
         * Obtain the source field.
         * @return the field
         */
        public F getFieldId() {
            return theFieldId;
        }

        /**
         * Obtain the value.
         * @return the value
         */
        public Object getValue() {
            return theValue;
        }

        /**
         * Obtain the value as specific type.
         * @param <T> the value class
         * @param pClass the required class
         * @return the value
         * @throws OceanusException on error
         */
        public <T> T getValue(final Class<T> pClass) throws OceanusException {
            try {
                return pClass.cast(theValue);
            } catch (ClassCastException e) {
                throw new PrometheusDataException("Invalid dataType", e);
            }
        }
    }
}
