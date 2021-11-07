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

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.LoanPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Loan Table.
 */
public class MoneyWiseLoanTable
        extends MoneyWiseAssetTable<Loan, LoanCategory> {
    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<LoanInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The Loan dialog.
     */
    private final LoanPanel theActiveLoan;

    /**
     * The edit list.
     */
    private LoanList theLoans;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseLoanTable(final MoneyWiseView pView,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.LOAN, LoanCategory.class);

        /* register the infoEntry */
        theInfoEntry = getUpdateSet().registerType(MoneyWiseDataType.LOANINFO);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, Loan> myTable = getTable();

        /* Create a Loan panel */
        theActiveLoan = new LoanPanel(myGuiFactory, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActiveLoan);

        /* Set table configuration */
        myTable.setOnSelect(theActiveLoan::setItem);

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
        MetisProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Loans");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final LoanList myBase = myData.getLoans();
        theLoans = myBase.deriveEditList(getUpdateSet());
        getTable().setItems(theLoans.getUnderlyingList());
        getUpdateEntry().setDataList(theLoans);
        final LoanInfoList myInfo = theLoans.getLoanInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActiveLoan.refreshData();

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
    void selectLoan(final Loan pLoan) {
        /* Check whether we need to showAll */
        checkShowAll(pLoan);

        /* If we are changing the selection */
        final Loan myCurrent = theActiveLoan.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pLoan)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRowWithScroll(pLoan);
            theActiveLoan.setItem(pLoan);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveLoan.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectLoan(theActiveLoan.getSelectedItem());
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
            getTable().fireTableDataChanged();
            selectLoan(theActiveLoan.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    @Override
    protected void buildCategoryMenu(final Loan pLoan,
                                     final TethysScrollMenu<LoanCategory> pMenu) {
        /* Build the menu */
        theActiveLoan.buildCategoryMenu(pMenu, pLoan);
    }

    @Override
    protected void buildParentMenu(final Loan pLoan,
                                   final TethysScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActiveLoan.buildParentMenu(pMenu, pLoan);
    }

    @Override
    protected void buildCurrencyMenu(final Loan pLoan,
                                     final TethysScrollMenu<AssetCurrency> pMenu) {
        /* Build the menu */
        theActiveLoan.buildCurrencyMenu(pMenu, pLoan);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new asset */
            final Loan myLoan = theLoans.addNewItem();
            myLoan.setDefaults(getUpdateSet());

            /* Set as new and adjust map */
            myLoan.setNewVersion();
            myLoan.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            myLoan.validate();
            theActiveLoan.setNewItem(myLoan);

            /* Lock the table */
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new loan", e);

            /* Show the error */
            setError(myError);
        }
    }
}
