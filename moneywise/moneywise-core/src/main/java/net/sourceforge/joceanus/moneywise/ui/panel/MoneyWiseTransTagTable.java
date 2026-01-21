/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.ui.MetisAction;
import io.github.tonywasher.joceanus.metis.ui.MetisErrorPanel;
import io.github.tonywasher.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTransTagList;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.dialog.MoneyWiseTagDialog;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

/**
 * MoneyWise Tag Table.
 */
public class MoneyWiseTransTagTable
        extends MoneyWiseBaseTable<MoneyWiseTransTag> {
    /**
     * The filter panel.
     */
    private final TethysUIBoxPaneManager theFilterPanel;

    /**
     * The tag dialog.
     */
    private final MoneyWiseTagDialog theActiveTag;

    /**
     * The edit list.
     */
    private MoneyWiseTransTagList theTags;

    /**
     * Constructor.
     *
     * @param pView    the view
     * @param pEditSet the editSet
     * @param pError   the error panel
     */
    MoneyWiseTransTagTable(final MoneyWiseView pView,
                           final PrometheusEditSet pEditSet,
                           final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.TRANSTAG);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, MoneyWiseTransTag> myTable = getTable();

        /* Create new button */
        final TethysUIButton myNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.paneFactory().newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Create a tag panel */
        theActiveTag = new MoneyWiseTagDialog(myGuiFactory, pEditSet, this);
        declareItemPanel(theActiveTag);

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseTransTag::isDisabled)
                .setComparator(MoneyWiseTransTag::compareTo);

        /* Create the name column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(MoneyWiseTransTag::getName)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseTransTag::setName, r, v));

        /* Create the description column */
        myTable.declareStringColumn(PrometheusDataResource.DATAITEM_FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(MoneyWiseTransTag::getDesc)
                .setEditable(true)
                .setColumnWidth(WIDTH_DESC)
                .setOnCommit((r, v) -> updateField(MoneyWiseTransTag::setDescription, r, v));

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
        theActiveTag.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveTag.isEditing();
    }

    /**
     * Obtain the filter panel.
     *
     * @return the filter panel
     */
    protected TethysUIBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Tags");

        /* Access list */
        final MoneyWiseDataSet myData = getView().getData();
        final MoneyWiseTransTagList myBase = myData.getTransactionTags();
        theTags = myBase.deriveEditList(getEditSet());
        getTable().setItems(theTags.getUnderlyingList());

        /* Notify panel of refresh */
        theActiveTag.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveTag.setEditable(false);
    }

    /**
     * Select tag.
     *
     * @param pTag the tag to select
     */
    void selectTag(final MoneyWiseTransTag pTag) {
        /* Select the row */
        getTable().selectRow(pTag);
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveTag.isEditing()) {
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
        if (!theActiveTag.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            final MoneyWiseTransTag myTag = theActiveTag.getSelectedItem();
            updateTableData();
            if (myTag != null) {
                getTable().selectRow(myTag);
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

            /* Create a new profile */
            final OceanusProfile myTask = getView().getNewProfile("addNewItem");

            /* Create the new tag */
            myTask.startTask("buildItem");
            final MoneyWiseTransTag myTag = theTags.addNewItem();
            myTag.setDefaults();

            /* Set as new and adjust map */
            myTask.startTask("incrementVersion");
            myTag.setNewVersion();
            myTag.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item */
            myTask.startTask("validate");
            myTag.validate();

            /* update panel */
            myTask.startTask("setItem");
            theActiveTag.setNewItem(myTag);

            /* Lock the table */
            setTableEnabled(false);
            myTask.end();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new tag", e);

            /* Show the error */
            setError(myError);
        }
    }

    @Override
    protected String getInvalidNameChars() {
        return ",";
    }
}
