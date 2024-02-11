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
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashInfo;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog.MoneyWiseCashPanel;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditEntry;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Cash Table.
 */
public class MoneyWiseCashTable
        extends MoneyWiseAssetTable<MoneyWiseCash> {
    /**
     * The Info UpdateEntry.
     */
    private final PrometheusEditEntry<MoneyWiseCashInfo> theInfoEntry;

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
        theInfoEntry = getEditSet().registerType(MoneyWiseBasicDataType.CASHINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a Cash panel */
        theActiveCash = new MoneyWiseCashPanel(myGuiFactory, pEditSet, pError);
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
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Cashs");

        /* Access list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) getView().getData();
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

            /* Create the new asset */
            final MoneyWiseCash myCash = theCash.addNewItem();
            myCash.setDefaults(getEditSet());

            /* Set as new and adjust map */
            myCash.setNewVersion();
            myCash.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item and update panel */
            myCash.validate();
            theActiveCash.setNewItem(myCash);

            /* Lock the table */
            setTableEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new cash", e);

            /* Show the error */
            setError(myError);
        }
    }
}
