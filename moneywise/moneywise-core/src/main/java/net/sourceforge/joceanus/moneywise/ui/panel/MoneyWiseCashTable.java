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
package net.sourceforge.joceanus.moneywise.ui.panel;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.moneywise.ui.dialog.MoneyWiseCashPanel;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Cash Table.
 */
public class MoneyWiseCashTable
        extends MoneyWiseAssetTable<MoneyWiseCash> {
    /**
     * The Cash dialog.
     */
    private final MoneyWiseCashPanel theActiveCash;

    /**
     * The edit list.
     */
    private MoneyWiseCashList theCash;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
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
        theActiveCash = new MoneyWiseCashPanel(myGuiFactory, pEditSet, this);
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
            myCash.setDefaults(getEditSet());

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

