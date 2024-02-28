/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.panel;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseRegion.MoneyWiseRegionList;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWiseRegionPanel;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Region Table.
 */
public class MoneyWiseRegionTable
        extends MoneyWiseBaseTable<MoneyWiseRegion> {
    /**
     * The filter panel.
     */
    private final TethysUIBoxPaneManager theFilterPanel;

    /**
     * The Region dialog.
     */
    private final MoneyWiseRegionPanel theActiveRegion;

    /**
     * The edit list.
     */
    private MoneyWiseRegionList theRegions;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    MoneyWiseRegionTable(final MoneyWiseView pView,
                         final PrometheusEditSet pEditSet,
                         final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.REGION);

        /* Access the GUI factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, MoneyWiseRegion> myTable = getTable();

        /* Create new button */
        final TethysUIButton myNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.paneFactory().newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Create a region panel */
        theActiveRegion = new MoneyWiseRegionPanel(myGuiFactory, pEditSet, pError);
        declareItemPanel(theActiveRegion);

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseRegion::isDisabled)
                .setComparator(MoneyWiseRegion::compareTo);

        /* Create the name column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(MoneyWiseRegion::getName)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseRegion::setName, r, v));

        /* Create the description column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(MoneyWiseRegion::getDesc)
                .setEditable(true)
                .setColumnWidth(WIDTH_DESC)
                .setOnCommit((r, v) -> updateField(MoneyWiseRegion::setDescription, r, v));

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(pView.getGuiFactory());
        myTable.declareIconColumn(PrometheusDataResource.DATAITEM_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theActiveRegion.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveRegion.isEditing();
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysUIBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Regions");

        /* Access list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) getView().getData();
        final MoneyWiseRegionList myBase = myData.getRegions();
        theRegions = myBase.deriveEditList(getEditSet());
        getTable().setItems(theRegions.getUnderlyingList());

        /* Notify panel of refresh */
        theActiveRegion.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveRegion.setEditable(false);
    }

    /**
     * Select region.
     * @param pRegion the region to select
     */
    void selectRegion(final MoneyWiseRegion pRegion) {
        /* Select the row and ensure that it is visible */
        getTable().selectRow(pRegion);
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveRegion.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            super.handleRewind();
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
            setEnabled(true);
            final MoneyWiseRegion myRegion = theActiveRegion.getSelectedItem();
            updateTableData();
            if (myRegion != null) {
                getTable().selectRow(myRegion);
            } else {
                restoreSelected();
            }
        } else {
            getTable().cancelEditing();
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
            final MoneyWiseRegion myRegion = theRegions.addNewItem();
            myRegion.setDefaults();

            /* Set as new and adjust map */
            myRegion.setNewVersion();
            myRegion.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item and update panel */
            myRegion.validate();
            theActiveRegion.setNewItem(myRegion);

            /* Lock the table */
            setTableEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new region", e);

            /* Show the error */
            setError(myError);
        }
    }
}
