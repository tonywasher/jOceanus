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

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWiseDepositPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Deposit Table.
 */
public class MoneyWiseDepositTable
        extends MoneyWiseAssetTable<Deposit> {
    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<DepositInfo> theInfoEntry;

    /**
     * The Rate UpdateEntry.
     */
    private final UpdateEntry<DepositRate> theRateEntry;

    /**
     * The Deposit dialog.
     */
    private final MoneyWiseDepositPanel theActiveDeposit;

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
                          final UpdateSet pUpdateSet,
                          final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.DEPOSIT);

        /* register the info/rateEntries */
        theInfoEntry = getUpdateSet().registerType(MoneyWiseDataType.DEPOSITINFO);
        theRateEntry = getUpdateSet().registerType(MoneyWiseDataType.DEPOSITRATE);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a Deposit panel */
        theActiveDeposit = new MoneyWiseDepositPanel(myGuiFactory, pView, pUpdateSet, pError);
        declareItemPanel(theActiveDeposit);

        /* Finish the table */
        finishTable(true, true, true);

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
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Deposits");

        /* Access list */
        final MoneyWiseData myData = (MoneyWiseData) getView().getData();
        final DepositList myBase = myData.getDeposits();
        theDeposits = myBase.deriveEditList(getUpdateSet());
        getTable().setItems(theDeposits.getUnderlyingList());
        getUpdateEntry().setDataList(theDeposits);
        final DepositInfoList myInfo = theDeposits.getDepositInfo();
        theInfoEntry.setDataList(myInfo);

        /* Get the Deposit rates list */
        DepositRateList myRates = myData.getDepositRates();
        myRates = myRates.deriveEditList(getUpdateSet());
        theRateEntry.setDataList(myRates);

        /* Notify panel of refresh */
        theActiveDeposit.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
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
            getTable().selectRow(pDeposit);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveDeposit.isEditing()) {
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
        if (!theActiveDeposit.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            final Deposit myDeposit = theActiveDeposit.getSelectedItem();
            updateTableData();
            if (myDeposit != null) {
                getTable().selectRow(myDeposit);
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
    protected void buildCategoryMenu(final Deposit pDeposit,
                                     final TethysUIScrollMenu<AssetCategory> pMenu) {
        /* Build the menu */
        theActiveDeposit.buildCategoryMenu(pMenu, pDeposit);
    }

    @Override
    protected void buildParentMenu(final Deposit pDeposit,
                                   final TethysUIScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActiveDeposit.buildParentMenu(pMenu, pDeposit);
    }

    @Override
    protected void buildCurrencyMenu(final Deposit pDeposit,
                                     final TethysUIScrollMenu<AssetCurrency> pMenu) {
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
            setTableEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new deposit", e);

            /* Show the error */
            setError(myError);
        }
    }
}

