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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;

/**
 * FieldSet.
 * @param <T> the item type
 */
public class PrometheusFieldSet<T>
        implements TethysEventProvider<TethysXUIEvent> {
    /**
     * The gui factory.
     */
    private final TethysGuiFactory theFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysXUIEvent> theEventManager;

    /**
     * The map of Fields to panels.
     */
    private final Map<PrometheusDataFieldId, PrometheusFieldSetPanel<T>> theFieldMap;

    /**
     * The list of panels.
     */
    private final List<PrometheusFieldSetPanel<T>> thePanels;

    /**
     * The panel.
     */
    private final TethysGridPaneManager thePanel;

    /**
     * The currently active panel.
     */
    private PrometheusFieldSetPanel<T> theCurrentPanel;

    /**
     * The tabPanel.
     */
    private PrometheusFieldSetTabs theTabs;

    /**
     * is field changed predicate.
     */
    private BiPredicate<T, PrometheusDataFieldId> theChanged;

    /**
     * Is the data being refreshed?
     */
    private boolean isRefreshing;

    /**
     * Constructor.
     * @param pFactory the gui factory
     */
    public PrometheusFieldSet(final TethysGuiFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Create maps and lists */
        theFieldMap = new HashMap<>();
        thePanels = new ArrayList<>();

        /* Create the initial panel */
        theCurrentPanel = new PrometheusFieldSetPanel<>(pFactory, this);
        thePanels.add(theCurrentPanel);

        /* Create the main panel */
        thePanel = theFactory.newGridPane();
        thePanel.addCell(theCurrentPanel);
        thePanel.allowCellGrowth(theCurrentPanel);
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysComponent getComponent() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<TethysXUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Switch to subsidiary panel.
     * @param pName the name of the tab.
     */
    public void newPanel(final String pName) {
        /* If we do not currently have any tabs */
        if (theTabs == null) {
            /* Create the tab pane and add to main panel */
            theTabs = new PrometheusFieldSetTabs(theFactory);
            thePanel.addCell(theTabs.getComponent());
            thePanel.allowCellGrowth(theTabs.getComponent());
        }

        /* Create the new panel and add as tab */
        theCurrentPanel = new PrometheusFieldSetPanel<>(theFactory, this);
        thePanels.add(theCurrentPanel);
        theTabs.addPanel(pName, theCurrentPanel);
    }

    /**
     * Add field to current panel.
     * @param pFieldId the fieldId
     * @param pField the edit field
     * @param pValueFactory the valueFactory
     */
    public void addField(final PrometheusDataFieldId pFieldId,
                         final TethysDataEditField<?> pField,
                         final Function<T, Object> pValueFactory) {
        theCurrentPanel.addField(pFieldId, pField, pValueFactory);
    }

    /**
     * Set the changed predicate.
     * @param pChanged the changed predicate
     */
    public void setChanged(final BiPredicate<T, PrometheusDataFieldId> pChanged) {
        theChanged = pChanged;
    }

    /**
     * Is the cell changed?
     * @param pItem the item
     * @param pField the field id
     * @return true/false
     */
    boolean isChanged(final T pItem,
                      final PrometheusDataFieldId pField) {
        return theChanged != null
                && theChanged.test(pItem, pField);
    }

    /**
     * Set editable item.
     * @param isEditable true/false
     */
    public void setEditable(final boolean isEditable) {
        /* Update all the panels */
        for (PrometheusFieldSetPanel<T> myPanel : thePanels) {
            myPanel.setEditable(isEditable);
        }
    }

    /**
     * Adjust changed indications.
     */
    public void adjustChanged() {
        /* Update all the panels */
        for (PrometheusFieldSetPanel<T> myPanel : thePanels) {
            myPanel.adjustChanged();
        }
    }

    /**
     * Set item.
     * @param pItem the item.
     */
    public void setItem(final T pItem) {
        /* Note that we are refreshing */
        isRefreshing = true;

        /* Update all the panels */
        for (PrometheusFieldSetPanel<T> myPanel : thePanels) {
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
    void registerField(final PrometheusDataFieldId pFieldId,
                       final PrometheusFieldSetPanel<T> pPanel) {
        theFieldMap.put(pFieldId, pPanel);
    }

    /**
     * Set field visibility.
     * @param pFieldId the field.
     * @param pVisible is visible true/false?
     */
    public void setFieldVisible(final PrometheusDataFieldId pFieldId,
                                final boolean pVisible) {
        /* Adjust visibility of field */
        final PrometheusFieldSetPanel<T> myPanel = theFieldMap.get(pFieldId);
        myPanel.setVisible(pFieldId, pVisible);
    }

    /**
     * Adjust tab visibility.
     */
    public void adjustTabVisibility() {
        /* Adjust visibility of tabs if present */
        if (theTabs != null) {
            theTabs.adjustVisibilty();
        }
        adjustChanged();
    }

    /**
     * Set field editability.
     * @param pFieldId the fieldId.
     * @param pEditable is editable true/false?
     */
    public void setFieldEditable(final PrometheusDataFieldId pFieldId,
                                 final boolean pEditable) {
        /* Adjust edit-ability of field */
        final PrometheusFieldSetPanel<T> myPanel = theFieldMap.get(pFieldId);
        myPanel.setEditable(pFieldId, pEditable);
    }

    /**
     * Notify listeners of new data.
     * @param pFieldId the fieldId.
     * @param pNewValue the new value
     */
    void newData(final PrometheusDataFieldId pFieldId,
                 final Object pNewValue) {
        /* If we are not refreshing data */
        if (!isRefreshing) {
            /* Create the notification */
            final PrometheusFieldSetEvent myUpdate = new PrometheusFieldSetEvent(pFieldId, pNewValue);

            /* Fire the notification */
            theEventManager.fireEvent(TethysXUIEvent.NEWVALUE, myUpdate);
        }
    }
}
