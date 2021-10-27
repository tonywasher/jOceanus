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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.CashPanel;
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
 * MoneyWise Cash Table.
 */
public class MoneyWiseCashTable
        extends MoneyWiseAssetTable<Cash> {
    /**
     * The Cash dialog.
     */
    private final CashPanel theActiveCash;

    /**
     * The edit list.
     */
    private CashList theCashs;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseCashTable(final MoneyWiseView pView,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.CASH);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, Cash> myTable = getTable();

        /* Create a Cash panel */
        theActiveCash = new CashPanel(myGuiFactory, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActiveCash);

        /* Set table configuration */
        myTable.setDisabled(Cash::isDisabled)
                .setComparator(Cash::compareTo)
                .setOnSelect(theActiveCash::setItem);

        /* Create the name column */
        myTable.declareStringColumn(Cash.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(Cash::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Cash::setName, r, v));

        /* Create the Cash type column */
        myTable.declareScrollColumn(Cash.FIELD_CATEGORY, CashCategory.class)
                .setMenuConfigurator(this::buildCategoryMenu)
                .setCellValueFactory(Cash::getCategory)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Cash::setCashCategory, r, v));

        /* Create the description column */
        myTable.declareStringColumn(Cash.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Cash::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Cash::setDescription, r, v));

        /* Create the currency column */
        myTable.declareScrollColumn(Cash.FIELD_CURRENCY, AssetCurrency.class)
                .setMenuConfigurator(this::buildCurrencyMenu)
                .setCellValueFactory(Cash::getAssetCurrency)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Cash::setAssetCurrency, r, v));

        /* Create the Closed column */
        final Map<Boolean, TethysIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton();
        final TethysTableColumn<Boolean, MetisLetheField, Cash> myClosedColumn
                = myTable.declareIconColumn(Cash.FIELD_CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(Cash::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setOnCommit((r, v) -> updateField(Cash::setClosed, r, v));
        declareClosedColumn(myClosedColumn);

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(Cash.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Create the latest event column */
        myTable.declareDateColumn(Cash.FIELD_EVTLAST)
                .setCellValueFactory(this::getLatestTranDate)
                .setName(MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue())
                .setEditable(false);

        /* Add listeners */
        theActiveCash.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveCash.isEditing();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Cashs");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final CashList myBase = myData.getDataList(CashList.class);
        theCashs = (CashList) myBase.deriveList(ListStyle.EDIT);
        theCashs.mapData();
        getTable().setItems(theCashs.getUnderlyingList());
        getUpdateEntry().setDataList(theCashs);

        /* Notify panel of refresh */
        theActiveCash.refreshData();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void cancelEditing() {
        super.cancelEditing();
        theActiveCash.setEditable(false);
    }

    /**
     * Select Cash.
     * @param pCash the Cash to select
     */
    void selectCash(final Cash pCash) {
        /* Check whether we need to showAll */
        checkShowAll(pCash);

        /* If we are changing the selection */
        final Cash myCurrent = theActiveCash.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCash)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRowWithScroll(pCash);
            theActiveCash.setItem(pCash);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveCash.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectCash(theActiveCash.getSelectedItem());
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveCash.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectCash(theActiveCash.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * Build the Cash type list for the item.
     * @param pCash the item
     * @param pMenu the menu to build
     */
    private void buildCategoryMenu(final Cash pCash,
                                   final TethysScrollMenu<CashCategory> pMenu) {
        /* Build the menu */
        theActiveCash.buildCategoryMenu(pMenu, pCash);
    }

    /**
     * Build the currency list for the item.
     * @param pCash the item
     * @param pMenu the menu to build
     */
    private void buildCurrencyMenu(final Cash pCash,
                                   final TethysScrollMenu<AssetCurrency> pMenu) {
        /* Build the menu */
        theActiveCash.buildCurrencyMenu(pMenu, pCash);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new asset */
            final Cash myCash = theCashs.addNewItem();
            myCash.setDefaults(getUpdateSet());

            /* Set as new and adjust map */
            myCash.setNewVersion();
            myCash.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            myCash.validate();
            theActiveCash.setNewItem(myCash);

            /* Lock the table */
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new cash", e);

            /* Show the error */
            setError(myError);
        }
    }
}

