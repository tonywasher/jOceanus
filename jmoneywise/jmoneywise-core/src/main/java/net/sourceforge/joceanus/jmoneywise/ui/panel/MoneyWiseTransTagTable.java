/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransTag.MoneyWiseTransTagList;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWiseTagPanel;
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
    private final MoneyWiseTagPanel theActiveTag;

    /**
     * The edit list.
     */
    private MoneyWiseTransTagList theTags;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
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
        theActiveTag = new MoneyWiseTagPanel(myGuiFactory, pEditSet, this);
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
     * @return the filter panel
     */
    protected TethysUIBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Tags");

        /* Access list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) getView().getData();
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

            /* Create the new tag */
            final MoneyWiseTransTag myTag = theTags.addNewItem();
            myTag.setDefaults();

            /* Set as new and adjust map */
            myTag.setNewVersion();
            myTag.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item and update panel */
            myTag.validate();
            theActiveTag.setNewItem(myTag);

            /* Lock the table */
            setTableEnabled(false);

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
