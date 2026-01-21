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
package io.github.tonywasher.joceanus.moneywise.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.ui.MetisErrorPanel;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import io.github.tonywasher.joceanus.moneywise.ui.dialog.MoneyWiseCashDialog;
import io.github.tonywasher.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;

import java.util.Iterator;

/**
 * MoneyWise Cash Table.
 */
public class MoneyWiseCashTable
        extends MoneyWiseAssetTable<MoneyWiseCash> {
    /**
     * The Cash dialog.
     */
    private final MoneyWiseCashDialog theActiveCash;

    /**
     * The edit list.
     */
    private MoneyWiseCashList theCash;

    /**
     * Constructor.
     *
     * @param pView    the view
     * @param pEditSet the editSet
     * @param pError   the error panel
     */
    MoneyWiseCashTable(final MoneyWiseView pView,
                       final PrometheusEditSet pEditSet,
                       final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.CASH);

        /* register the infoEntry */
        getEditSet().registerType(MoneyWiseBasicDataType.CASHINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a Cash panel */
        theActiveCash = new MoneyWiseCashDialog(myGuiFactory, pEditSet, this);
        declareItemPanel(theActiveCash);

        /* Finish the table */
        finishTable(false, true, true);

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
        OceanusProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Cashs");

        /* Access list */
        final MoneyWiseDataSet myData = getView().getData();
        final MoneyWiseCashList myBase = myData.getCash();
        theCash = myBase.deriveEditList(getEditSet());
        getTable().setItems(theCash.getUnderlyingList());

        /* Notify panel of refresh */
        theActiveCash.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveCash.setEditable(false);
    }

    /**
     * Select Cash.
     *
     * @param pCash the Cash to select
     */
    void selectCash(final MoneyWiseCash pCash) {
        /* Check whether we need to showAll */
        checkShowAll(pCash);

        /* If we are changing the selection */
        final MoneyWiseCash myCurrent = theActiveCash.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCash)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRow(pCash);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveCash.isEditing()) {
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
        if (!theActiveCash.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            final MoneyWiseCash myCash = theActiveCash.getSelectedItem();
            updateTableData();
            if (myCash != null) {
                getTable().selectRow(myCash);
            } else {
                restoreSelected();
            }
        } else {
            getTable().cancelEditing();
        }

        /* Note changes */
        notifyChanges();
    }

    @Override
    protected void buildCategoryMenu(final MoneyWiseCash pCash,
                                     final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu) {
        /* Build the menu */
        theActiveCash.buildCategoryMenu(pMenu, pCash);
    }

    @Override
    protected void buildCurrencyMenu(final MoneyWiseCash pCash,
                                     final TethysUIScrollMenu<MoneyWiseCurrency> pMenu) {
        /* Build the menu */
        theActiveCash.buildCurrencyMenu(pMenu, pCash);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create a new profile */
            final OceanusProfile myTask = getView().getNewProfile("addNewItem");

            /* Create the new asset */
            myTask.startTask("buildItem");
            final MoneyWiseCash myCash = theCash.addNewItem();
            myCash.setDefaults();

            /* Set as new and adjust map */
            myTask.startTask("incrementVersion");
            myCash.setNewVersion();
            myCash.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item */
            myTask.startTask("validate");
            myCash.validate();

            /* update panel */
            myTask.startTask("setItem");
            theActiveCash.setNewItem(myCash);

            /* Lock the table */
            setTableEnabled(false);
            myTask.end();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new cash", e);

            /* Show the error */
            setError(myError);
        }
    }

    @Override
    protected Iterator<PrometheusDataItem> nameSpaceIterator() {
        return assetNameSpaceIterator();
    }
}

