/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.panel;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseTagDataId;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog.MoneyWiseTagPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;

/**
 * MoneyWise Tag Table.
 */
public class MoneyWiseTransTagTable
        extends MoneyWiseBaseTable<TransactionTag> {
    /**
     * The filter panel.
     */
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The tag dialog.
     */
    private final MoneyWiseTagPanel theActiveTag;

    /**
     * The edit list.
     */
    private TransactionTagList theTags;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseTransTagTable(final MoneyWiseView pView,
                           final UpdateSet<MoneyWiseDataType> pUpdateSet,
                           final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.TRANSTAG);

        /* Access Gui factory */
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();
        final TethysTableManager<PrometheusDataFieldId, TransactionTag> myTable = getTable();

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Create a tag panel */
        theActiveTag = new MoneyWiseTagPanel(myGuiFactory, pUpdateSet, pError);
        declareItemPanel(theActiveTag);

        /* Set table configuration */
        myTable.setDisabled(TransactionTag::isDisabled)
               .setComparator(TransactionTag::compareTo)
               .setOnSelect(theActiveTag::setItem);

        /* Create the name column */
        myTable.declareStringColumn(MoneyWiseTagDataId.NAME)
               .setValidator(this::isValidName)
               .setCellValueFactory(TransactionTag::getName)
               .setEditable(true)
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(TransactionTag::setName, r, v));

        /* Create the description column */
        myTable.declareStringColumn(MoneyWiseTagDataId.DESC)
               .setValidator(this::isValidDesc)
               .setCellValueFactory(TransactionTag::getDesc)
               .setEditable(true)
               .setColumnWidth(WIDTH_DESC)
               .setOnCommit((r, v) -> updateField(TransactionTag::setDescription, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(PrometheusDataId.TOUCH, MetisAction.class)
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
    protected TethysBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Tags");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final TransactionTagList myBase = myData.getTransactionTags();
        theTags = myBase.deriveEditList();
        getTable().setItems(theTags.getUnderlyingList());
        getUpdateEntry().setDataList(theTags);

        /* Notify panel of refresh */
        theActiveTag.refreshData();

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
    void selectTag(final TransactionTag pTag) {
        /* Select the row and ensure that it is visible */
        getTable().selectRowWithScroll(pTag);
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveTag.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectTag(theActiveTag.getSelectedItem());
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
            getTable().fireTableDataChanged();
            selectTag(theActiveTag.getSelectedItem());
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
            final TransactionTag myTag = theTags.addNewItem();
            myTag.setDefaults();

            /* Set as new and adjust map */
            myTag.setNewVersion();
            myTag.adjustMapForItem();
            getUpdateSet().incrementVersion();

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
