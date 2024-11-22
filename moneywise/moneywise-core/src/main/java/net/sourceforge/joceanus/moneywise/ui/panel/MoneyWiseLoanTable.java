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
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.moneywise.ui.dialog.MoneyWiseLoanPanel;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Loan Table.
 */
public class MoneyWiseLoanTable
        extends MoneyWiseAssetTable<MoneyWiseLoan> {
    /**
     * The Loan dialog.
     */
    private final MoneyWiseLoanPanel theActiveLoan;

    /**
     * The edit list.
     */
    private MoneyWiseLoanList theLoans;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    MoneyWiseLoanTable(final MoneyWiseView pView,
                       final PrometheusEditSet pEditSet,
                       final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.LOAN);

        /* register the infoEntry */
        getEditSet().registerType(MoneyWiseBasicDataType.LOANINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a Loan panel */
        theActiveLoan = new MoneyWiseLoanPanel(myGuiFactory, pEditSet, this);
        declareItemPanel(theActiveLoan);

        /* Finish the table */
        finishTable(true, true, true);

        /* Add listeners */
        theActiveLoan.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveLoan.isEditing();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Loans");

        /* Access list */
        final MoneyWiseDataSet myData = getView().getData();
        final MoneyWiseLoanList myBase = myData.getLoans();
        theLoans = myBase.deriveEditList(getEditSet());
        getTable().setItems(theLoans.getUnderlyingList());

        /* Notify panel of refresh */
        theActiveLoan.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveLoan.setEditable(false);
    }

    /**
     * Select Loan.
     * @param pLoan the Loan to select
     */
    void selectLoan(final MoneyWiseLoan pLoan) {
        /* Check whether we need to showAll */
        checkShowAll(pLoan);

        /* If we are changing the selection */
        final MoneyWiseLoan myCurrent = theActiveLoan.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pLoan)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRow(pLoan);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveLoan.isEditing()) {
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
        if (!theActiveLoan.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            final MoneyWiseLoan myLoan = theActiveLoan.getSelectedItem();
            updateTableData();
            if (myLoan != null) {
                getTable().selectRow(myLoan);
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
    protected void buildCategoryMenu(final MoneyWiseLoan pLoan,
                                     final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu) {
        /* Build the menu */
        theActiveLoan.buildCategoryMenu(pMenu, pLoan);
    }

    @Override
    protected void buildParentMenu(final MoneyWiseLoan pLoan,
                                   final TethysUIScrollMenu<MoneyWisePayee> pMenu) {
        /* Build the menu */
        theActiveLoan.buildParentMenu(pMenu, pLoan);
    }

    @Override
    protected void buildCurrencyMenu(final MoneyWiseLoan pLoan,
                                     final TethysUIScrollMenu<MoneyWiseCurrency> pMenu) {
        /* Build the menu */
        theActiveLoan.buildCurrencyMenu(pMenu, pLoan);
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
            final MoneyWiseLoan myLoan = theLoans.addNewItem();
            myLoan.setDefaults(getEditSet());

            /* Set as new and adjust map */
            myTask.startTask("incrementVersion");
            myLoan.setNewVersion();
            myLoan.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item */
            myTask.startTask("validate");
            myLoan.validate();

            /* update panel */
            myTask.startTask("setItem");
            theActiveLoan.setNewItem(myLoan);

            /* Lock the table */
            setTableEnabled(false);
            myTask.end();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new loan", e);

            /* Show the error */
            setError(myError);
        }
    }

    @Override
    protected Iterator<PrometheusDataItem> nameSpaceIterator() {
        return assetNameSpaceIterator();
    }
}
