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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.panel;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDepositInfo;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDepositInfo.MoneyWiseDepositInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog.MoneyWiseDepositPanel;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditEntry;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Deposit Table.
 */
public class MoneyWiseDepositTable
        extends MoneyWiseAssetTable<MoneyWiseDeposit> {
    /**
     * The Info UpdateEntry.
     */
    private final PrometheusEditEntry<MoneyWiseDepositInfo> theInfoEntry;

    /**
     * The Rate UpdateEntry.
     */
    private final PrometheusEditEntry<MoneyWiseDepositRate> theRateEntry;

    /**
     * The Deposit dialog.
     */
    private final MoneyWiseDepositPanel theActiveDeposit;

    /**
     * The edit list.
     */
    private MoneyWiseDepositList theDeposits;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    MoneyWiseDepositTable(final MoneyWiseView pView,
                          final PrometheusEditSet pEditSet,
                          final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.DEPOSIT);

        /* register the info/rateEntries */
        theInfoEntry = getEditSet().registerType(MoneyWiseBasicDataType.DEPOSITINFO);
        theRateEntry = getEditSet().registerType(MoneyWiseBasicDataType.DEPOSITRATE);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a Deposit panel */
        theActiveDeposit = new MoneyWiseDepositPanel(myGuiFactory, pView, pEditSet, pError);
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
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) getView().getData();
        final MoneyWiseDepositList myBase = myData.getDeposits();
        theDeposits = myBase.deriveEditList(getEditSet());
        getTable().setItems(theDeposits.getUnderlyingList());

        /* Get the Deposit rates list */
        MoneyWiseDepositRateList myRates = myData.getDepositRates();
        myRates = myRates.deriveEditList(getEditSet());
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
    void selectDeposit(final MoneyWiseDeposit pDeposit) {
        /* Check whether we need to showAll */
        checkShowAll(pDeposit);

        /* If we are changing the selection */
        final MoneyWiseDeposit myCurrent = theActiveDeposit.getSelectedItem();
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
            final MoneyWiseDeposit myDeposit = theActiveDeposit.getSelectedItem();
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
    protected void buildCategoryMenu(final MoneyWiseDeposit pDeposit,
                                     final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu) {
        /* Build the menu */
        theActiveDeposit.buildCategoryMenu(pMenu, pDeposit);
    }

    @Override
    protected void buildParentMenu(final MoneyWiseDeposit pDeposit,
                                   final TethysUIScrollMenu<MoneyWisePayee> pMenu) {
        /* Build the menu */
        theActiveDeposit.buildParentMenu(pMenu, pDeposit);
    }

    @Override
    protected void buildCurrencyMenu(final MoneyWiseDeposit pDeposit,
                                     final TethysUIScrollMenu<MoneyWiseCurrency> pMenu) {
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
            final MoneyWiseDeposit myDeposit = theDeposits.addNewItem();
            myDeposit.setDefaults(getEditSet());

            /* Set as new and adjust map */
            myDeposit.setNewVersion();
            myDeposit.adjustMapForItem();
            getEditSet().incrementVersion();

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

