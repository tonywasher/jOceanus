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

import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.DepositPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Deposit Table.
 */
public class MoneyWiseDepositTable
        extends MoneyWiseAssetTable<Deposit> {
    /**
     * The Deposit dialog.
     */
    private final DepositPanel theActiveDeposit;

    /**
     * The edit list.
     */
    private DepositList theDeposits;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseDepositTable(final MoneyWiseView pView,
                          final UpdateSet<MoneyWiseDataType> pUpdateSet,
                          final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.DEPOSIT);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, Deposit> myTable = getTable();

        /* Create a Deposit panel */
        theActiveDeposit = new DepositPanel(myGuiFactory, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActiveDeposit);

        /* Set table configuration */
        myTable.setDisabled(Deposit::isDisabled)
                .setComparator(Deposit::compareTo)
                .setOnSelect(theActiveDeposit::setItem);

        /* Create the name column */
        myTable.declareStringColumn(Deposit.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(Deposit::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Deposit::setName, r, v));

        /* Create the Deposit type column */
        myTable.declareScrollColumn(Deposit.FIELD_CATEGORY, DepositCategory.class)
                .setMenuConfigurator(this::buildCategoryMenu)
                .setCellValueFactory(Deposit::getCategory)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Deposit::setCategory, r, v));

        /* Create the description column */
        myTable.declareStringColumn(Deposit.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Deposit::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Deposit::setDescription, r, v));

        /* Create the parent column */
        myTable.declareScrollColumn(Deposit.FIELD_PARENT, Payee.class)
                .setMenuConfigurator(this::buildParentMenu)
                .setCellValueFactory(Deposit::getParent)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Deposit::setParent, r, v));

        /* Create the currency column */
        myTable.declareScrollColumn(Deposit.FIELD_CURRENCY, AssetCurrency.class)
                .setMenuConfigurator(this::buildCurrencyMenu)
                .setCellValueFactory(Deposit::getAssetCurrency)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Deposit::setAssetCurrency, r, v));

        /* Create the Closed column */
        final Map<Boolean, TethysIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton();
        final TethysTableColumn<Boolean, MetisLetheField, Deposit> myClosedColumn
                = myTable.declareIconColumn(Deposit.FIELD_CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(Deposit::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setOnCommit((r, v) -> updateField(Deposit::setClosed, r, v));
        declareClosedColumn(myClosedColumn);

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(Deposit.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Create the latest event column */
        myTable.declareDateColumn(Deposit.FIELD_EVTLAST)
                .setCellValueFactory(this::getLatestTranDate)
                .setName(MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue())
                .setEditable(false);

        /* Add listeners */
        theActiveDeposit.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveDeposit.isEditing();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Deposits");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final DepositList myBase = myData.getDataList(DepositList.class);
        theDeposits = (DepositList) myBase.deriveList(ListStyle.EDIT);
        theDeposits.mapData();
        getTable().setItems(theDeposits.getUnderlyingList());
        getUpdateEntry().setDataList(theDeposits);

        /* Notify panel of refresh */
        theActiveDeposit.refreshData();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void cancelEditing() {
        super.cancelEditing();
        theActiveDeposit.setEditable(false);
    }

    /**
     * Select Deposit.
     * @param pDeposit the Deposit to select
     */
    void selectDeposit(final Deposit pDeposit) {
        /* Check whether we need to showAll */
        checkShowAll(pDeposit);

        /* If we are changing the selection */
        final Deposit myCurrent = theActiveDeposit.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pDeposit)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRowWithScroll(pDeposit);
            theActiveDeposit.setItem(pDeposit);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveDeposit.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectDeposit(theActiveDeposit.getSelectedItem());
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveDeposit.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectDeposit(theActiveDeposit.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * Build the Deposit type list for the item.
     * @param pDeposit the item
     * @param pMenu the menu to build
     */
    private void buildCategoryMenu(final Deposit pDeposit,
                                   final TethysScrollMenu<DepositCategory> pMenu) {
        /* Build the menu */
        theActiveDeposit.buildCategoryMenu(pMenu, pDeposit);
    }

    /**
     * Build the Deposit type list for the item.
     * @param pDeposit the item
     * @param pMenu the menu to build
     */
    private void buildParentMenu(final Deposit pDeposit,
                                 final TethysScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActiveDeposit.buildParentMenu(pMenu, pDeposit);
    }

    /**
     * Build the currency list for the item.
     * @param pDeposit the item
     * @param pMenu the menu to build
     */
    private void buildCurrencyMenu(final Deposit pDeposit,
                                   final TethysScrollMenu<AssetCurrency> pMenu) {
        /* Build the menu */
        theActiveDeposit.buildCurrencyMenu(pMenu, pDeposit);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new asset */
            final Deposit myDeposit = theDeposits.addNewItem();
            myDeposit.setDefaults(getUpdateSet());

            /* Set as new and adjust map */
            myDeposit.setNewVersion();
            myDeposit.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            myDeposit.validate();
            theActiveDeposit.setNewItem(myDeposit);

            /* Lock the table */
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new deposit", e);

            /* Show the error */
            setError(myError);
        }
    }
}

