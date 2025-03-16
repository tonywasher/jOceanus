/* *****************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.ui.fieldset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetTableTab.PrometheusFieldSetTable;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIGridPaneManager;

/**
 * FieldSet.
 * @param <T> the item type
 */
public class PrometheusFieldSet<T>
        implements OceanusEventProvider<TethysUIEvent> {
    /**
     * The gui factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The map of Fields to panels.
     */
    private final Map<MetisDataFieldId, PrometheusFieldSetPanel<T>> theFieldMap;

    /**
     * The list of panels.
     */
    private final List<PrometheusFieldSetPanel<T>> thePanels;

    /**
     * The panel.
     */
    private final TethysUIGridPaneManager thePanel;

    /**
     * The currently active panel.
     */
    private PrometheusFieldSetFields<T> theCurrentPanel;

    /**
     * The tabPanel.
     */
    private PrometheusFieldSetTabs theTabs;

    /**
     * is field changed predicate.
     */
    private BiPredicate<T, MetisDataFieldId> theChanged;

    /**
     * Reporter consumer.
     */
    private Consumer<String> theReporter;

    /**
     * Is the data being refreshed?
     */
    private boolean isRefreshing;

    /**
     * Labels adjusted?
     */
    private boolean areLabelsAdjusted;

    /**
     * Constructor.
     * @param pFactory the gui factory
     */
    public PrometheusFieldSet(final TethysUIFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create maps and lists */
        theFieldMap = new HashMap<>();
        thePanels = new ArrayList<>();

        /* Create the initial panel */
        theCurrentPanel = new PrometheusFieldSetFields<>(pFactory, this);
        thePanels.add(theCurrentPanel);

        /* Create the main panel */
        thePanel = theFactory.paneFactory().newGridPane();
        thePanel.addCell(theCurrentPanel);
        thePanel.allowCellGrowth(theCurrentPanel);
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
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
        theCurrentPanel = new PrometheusFieldSetFields<>(theFactory, this);
        thePanels.add(theCurrentPanel);
        theTabs.addPanel(pName, theCurrentPanel);
    }

    /**
     * Add a textArea tab.
     * @param pName the name of the tab.
     * @param pFieldId the fieldId
     * @param pField the edit field
     * @param pValueFactory the valueFactory
     */
    public void newTextArea(final String pName,
                            final MetisDataFieldId pFieldId,
                            final TethysUICharArrayTextAreaField pField,
                            final Function<T, char[]> pValueFactory) {
        /* If we do not currently have any tabs */
        if (theTabs == null) {
            /* Create the tab pane and add to main panel */
            theTabs = new PrometheusFieldSetTabs(theFactory);
            thePanel.addCell(theTabs.getComponent());
            thePanel.allowCellGrowth(theTabs.getComponent());
        }

        /* Create the new panel and add as tab */
        final PrometheusFieldSetTextArea<T> myTextArea = new PrometheusFieldSetTextArea<>(theFactory, this);
        thePanels.add(myTextArea);
        theTabs.addPanel(pName, myTextArea);

        /* Add the field */
        myTextArea.addField(pFieldId, pField, pValueFactory);
    }

    /**
     * Add a table tab.
     * @param pName the name of the tab.
     * @param pTable the table
     */
    public void newTable(final String pName,
                         final PrometheusFieldSetTable<T> pTable) {
        /* If we do not currently have any tabs */
        if (theTabs == null) {
            /* Create the tab pane and add to main panel */
            theTabs = new PrometheusFieldSetTabs(theFactory);
            thePanel.addCell(theTabs.getComponent());
            thePanel.allowCellGrowth(theTabs.getComponent());
        }

        /* Create the new panel and add as tab */
        final PrometheusFieldSetTableTab<T> myTable = new PrometheusFieldSetTableTab<>(theFactory, pTable);
        thePanels.add(myTable);
        theTabs.addPanel(pName, myTable);
    }

    /**
     * Add field to current panel.
     * @param pFieldId the fieldId
     * @param pField the edit field
     * @param pValueFactory the valueFactory
     */
    public void addField(final MetisDataFieldId pFieldId,
                         final TethysUIDataEditField<?> pField,
                         final Function<T, Object> pValueFactory) {
        theCurrentPanel.addField(pFieldId, pField, pValueFactory);
    }

    /**
     * Set the changed predicate.
     * @param pChanged the changed predicate
     */
    public void setChanged(final BiPredicate<T, MetisDataFieldId> pChanged) {
        theChanged = pChanged;
    }

    /**
     * Is the cell changed?
     * @param pItem the item
     * @param pField the field id
     * @return true/false
     */
    boolean isChanged(final T pItem,
                      final MetisDataFieldId pField) {
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
            myPanel.setReporter(theReporter);
            if (!areLabelsAdjusted) {
                myPanel.adjustLabelWidth();
            }
        }

        /* Note that we have finished refreshing */
        isRefreshing = false;
        areLabelsAdjusted = true;
    }

    /**
     * Register a field.
     * @param pFieldId the field.
     * @param pPanel the panel.
     */
    void registerField(final MetisDataFieldId pFieldId,
                       final PrometheusFieldSetPanel<T> pPanel) {
        theFieldMap.put(pFieldId, pPanel);
    }

    /**
     * Set field visibility.
     * @param pFieldId the field.
     * @param pVisible is visible true/false?
     */
    public void setFieldVisible(final MetisDataFieldId pFieldId,
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
            theTabs.adjustVisibility();
        }
        adjustChanged();
    }

    /**
     * Set field editability.
     * @param pFieldId the fieldId.
     * @param pEditable is editable true/false?
     */
    public void setFieldEditable(final MetisDataFieldId pFieldId,
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
    void newData(final MetisDataFieldId pFieldId,
                 final Object pNewValue) {
        /* If we are not refreshing data */
        if (!isRefreshing) {
            /* Create the notification */
            final PrometheusFieldSetEvent myUpdate = new PrometheusFieldSetEvent(pFieldId, pNewValue);

            /* Fire the notification */
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, myUpdate);
        }
    }

    /**
     * Set preferred width and height.
     * @param pWidth the width.
     * @param pHeight the height
     */
    public void setPreferredWidthAndHeight(final int pWidth,
                                           final int pHeight) {
        /* If we have Tabs */
        int myWidth = pWidth;
        if (theTabs != null) {
            /* Everything is half width */
            myWidth >>= 1;

            /* Set all tabs visible */
            theTabs.setAllVisible();

            /* Set the tab size */
            final TethysUIComponent myComponent = theTabs.getComponent();
            myComponent.setPreferredWidth(myWidth);
            myComponent.setPreferredHeight(pHeight);
        }

        /* Update the initial panel */
        final PrometheusFieldSetPanel<T> myPanel = thePanels.iterator().next();
        final TethysUIComponent myComponent = myPanel.getUnderlying();
        myComponent.setPreferredWidth(myWidth);
        myComponent.setPreferredHeight(pHeight);
    }

    /**
     * Set reporter.
     * @param pReporter the reporter
     */
    public void setReporter(final Consumer<String> pReporter) {
        theReporter = pReporter;
    }
}
