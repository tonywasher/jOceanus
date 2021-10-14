/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.ui;

import java.awt.BorderLayout;
import java.util.Objects;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region.RegionList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.RegionPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysOnCellCommit;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Region Table.
 */
public class MoneyWiseRegionTable
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UpdateSet associated with the table.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final UpdateEntry<Region, MoneyWiseDataType> theUpdateEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The filter panel.
     */
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The underlying table.
     */
    private final TethysSwingTableManager<MetisLetheField, Region> theTable;

    /**
     * The Region dialog.
     */
    private final RegionPanel theActiveRegion;

    /**
     * The edit list.
     */
    private RegionList theRegions;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseRegionTable(final MoneyWiseView pView,
                         final UpdateSet<MoneyWiseDataType> pUpdateSet,
                         final MetisErrorPanel pError) {
        /* Store parameters */
        theView = pView;
        theError = pError;

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) theView.getToolkit()).getFieldManager();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.REGION);

        /* Create the panel */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        thePanel = myGuiFactory.newBorderPane();

        /* Create the table */
        theTable = myGuiFactory.newTable();
        thePanel.setCentre(theTable);

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Create a region panel */
        theActiveRegion = new RegionPanel(myGuiFactory, myFieldMgr, theUpdateSet, theError);
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(TethysSwingNode.getComponent(theActiveRegion), BorderLayout.CENTER);
        thePanel.setSouth(myPanel);

        /* Set disabled indication and filter */
        theTable.setOnCommitError(this::setError);
        theTable.setOnValidateError(this::showValidateError);
        theTable.setOnCellEditState(this::handleEditState);
        theTable.setDisabled(Region::isDisabled);
        theTable.setChanged(this::isFieldChanged);
        theTable.setError(this::isFieldInError);
        theTable.setComparator(Region::compareTo);
        theTable.setEditable(true);
        theTable.setOnSelect(theActiveRegion::setItem);

        /* Create the name column */
        theTable.declareStringColumn(Region.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(Region::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Region::setName, r, v));

        /* Create the description column */
        theTable.declareStringColumn(Region.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Region::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Region::setDescription, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        theTable.declareIconColumn(Region.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveRegion.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveRegion.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, theEventManager::cascadeEvent);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveRegion.isEditing();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Refresh data.
     */
    void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Regions");

        /* Access list */
        final MoneyWiseData myData = theView.getData();
        final RegionList myBase = myData.getDataList(RegionList.class);
        theRegions = (RegionList) myBase.deriveList(ListStyle.EDIT);
        theRegions.mapData();
        theTable.setItems(theRegions.getUnderlyingList());
        theUpdateEntry.setDataList(theRegions);

        /* Notify panel of refresh */
        theActiveRegion.refreshData();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Cancel editing.
     */
    void cancelEditing() {
        theTable.cancelEditing();
        theActiveRegion.setEditable(false);
    }

    /**
     * Does the panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    /**
     * Does the panel have a session?
     * @return true/false
     */
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
    }

    /**
     * Does the panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        theTable.requestFocus();

        /* Set the required focus */
        pEntry.setFocus(theUpdateEntry.getName());
    }

    /**
     * Select region.
     * @param pRegion the region to select
     */
    void selectRegion(final Region pRegion) {
        /* Select the row and ensure that it is visible */
        theTable.selectRowWithScroll(pRegion);
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     */
    private void deleteRow(final Region pRow,
                           final Object pValue) {
        pRow.setDeleted(true);
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveRegion.isEditing()) {
            /* Handle the reWind */
            theTable.fireTableDataChanged();
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveRegion.isEditing()) {
            /* handle the edit transition */
            theTable.fireTableDataChanged();
            selectRegion(theActiveRegion.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * New item.
     */
    private void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new region */
            final Region myRegion = theRegions.addNewItem();
            myRegion.setDefaults();

            /* Set as new and adjust map */
            myRegion.setNewVersion();
            myRegion.adjustMapForItem();
            theUpdateSet.incrementVersion();

            /* Validate the new item and update panel */
            myRegion.validate();
            theActiveRegion.setNewItem(myRegion);

            /* Lock the table */
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new region", e);

            /* Show the error */
            setError(myError);
        }
    }

    /**
     * Update value.
     * @param <V> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    private <V> void updateField(final TethysOnCellCommit<Region, V> pOnCommit,
                                 final Region pRow,
                                 final V pValue) throws OceanusException {
        /* Push history */
        pRow.pushHistory();

        /* Protect against Exceptions */
        try {
            /* Set the item value */
            pOnCommit.commitCell(pRow, pValue);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Reset values */
            pRow.popHistory();

            /* Throw the error */
            throw new PrometheusDataException("Failed to update field", e);
        }

        /* Check for changes */
        if (pRow.checkForHistory()) {
            /* Increment data version */
            theUpdateSet.incrementVersion();

            /* Update components to reflect changes */
            theTable.fireTableDataChanged();
            notifyChanges();
        }
    }

    /**
     * Set the error.
     * @param pError the error
     */
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Notify that there have been changes to this list.
     */
    protected void notifyChanges() {
        /* Adjust enable of the table */
        theTable.setEnabled(!theActiveRegion.isEditing());

        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * is field in error?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldInError(final MetisLetheField pField,
                                   final Region pItem) {
        return pItem.getFieldErrors(pField) != null;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldChanged(final MetisLetheField pField,
                                   final Region pItem) {
        return pItem.fieldChanged(pField).isDifferent();
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @param pRow the row
     * @return error message or null
     */
    private String isValidName(final String pNewName,
                               final Region pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Reject invalid name */
        if (!DataItem.validString(pNewName, null)) {
            return "Invalid characters in name";
        }

        /* Reject name that is too long */
        if (DataItem.byteLength(pNewName) > Region.NAMELEN) {
            return "Name too long";
        }

        /* Loop through the existing values */
        for (Region myValue : theRegions.getUnderlyingList()) {
            /* Ignore self and deleted */
            if (myValue.isDeleted() || myValue.equals(pRow)) {
                continue;
            }

            /* Check for duplicate */
            if (pNewName.equals(myValue.getName())) {
                return "Duplicate name";
            }
        }

        /* Valid name */
        return null;
    }

    /**
     * is Valid description?
     * @param pNewDesc the new description
     * @param pRow the row
     * @return error message or null
     */
    private String isValidDesc(final String pNewDesc,
                               final Region pRow) {
        /* Reject description that is too long */
        if (pNewDesc != null
                && DataItem.byteLength(pNewDesc) > Region.DESCLEN) {
            return "Description too long";
        }

        /* Valid description */
        return null;
    }

    /**
     * Show validation error TODO use panel.
     * @param pError the error message
     */
    private void showValidateError(final String pError) {
        System.out.println(Objects.requireNonNullElse(pError, "Error cleared"));
    }

    /**
     * Check edit state.
     * @param pState the new state
     */
    private void handleEditState(final Boolean pState) {
        notifyChanges();
    }
}
