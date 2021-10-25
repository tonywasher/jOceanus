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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.LoanPanel;
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
 * MoneyWise Loan Table.
 */
public class MoneyWiseLoanTable
        extends MoneyWiseAssetTable<Loan> {
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
        super(pView, pUpdateSet, pError, MoneyWiseDataType.LOAN);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, Loan> myTable = getTable();

        /* Create a Loan panel */
        theActiveLoan = new LoanPanel(myGuiFactory, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActiveLoan);

        /* Set table configuration */
        myTable.setDisabled(Loan::isDisabled)
                .setComparator(Loan::compareTo)
                .setOnSelect(theActiveLoan::setItem);

        /* Create the name column */
        myTable.declareStringColumn(Loan.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(Loan::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Loan::setName, r, v));

        /* Create the Loan type column */
        myTable.declareScrollColumn(Loan.FIELD_CATEGORY, LoanCategory.class)
                .setMenuConfigurator(this::buildCategoryMenu)
                .setCellValueFactory(Loan::getCategory)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Loan::setLoanCategory, r, v));

        /* Create the description column */
        myTable.declareStringColumn(Loan.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Loan::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Loan::setDescription, r, v));

        /* Create the parent column */
        myTable.declareScrollColumn(Loan.FIELD_PARENT, Payee.class)
                .setMenuConfigurator(this::buildParentMenu)
                .setCellValueFactory(Loan::getParent)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Loan::setParent, r, v));

        /* Create the currency column */
        myTable.declareScrollColumn(Loan.FIELD_CURRENCY, AssetCurrency.class)
                .setMenuConfigurator(this::buildCurrencyMenu)
                .setCellValueFactory(Loan::getAssetCurrency)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Loan::setAssetCurrency, r, v));

        /* Create the Closed column */
        final Map<Boolean, TethysIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton();
        final TethysTableColumn<Boolean, MetisLetheField, Loan> myClosedColumn
                = myTable.declareIconColumn(Loan.FIELD_CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(Loan::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setOnCommit((r, v) -> updateField(Loan::setClosed, r, v));
        declareClosedColumn(myClosedColumn);

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(Loan.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Create the latest event column */
        myTable.declareDateColumn(Loan.FIELD_EVTLAST)
                .setCellValueFactory(this::getLatestTranDate)
                .setName(MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue())
                .setEditable(false);

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
        final LoanList myBase = myData.getDataList(LoanList.class);
        theLoans = (LoanList) myBase.deriveList(ListStyle.EDIT);
        theLoans.mapData();
        getTable().setItems(theLoans.getUnderlyingList());
        getUpdateEntry().setDataList(theLoans);

        /* Notify panel of refresh */
        theActiveLoan.refreshData();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void cancelEditing() {
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

    /**
     * Build the Loan type list for the item.
     * @param pLoan the item
     * @param pMenu the menu to build
     */
    private void buildCategoryMenu(final Loan pLoan,
                                   final TethysScrollMenu<LoanCategory> pMenu) {
        /* Build the menu */
        theActiveLoan.buildCategoryMenu(pMenu, pLoan);
    }

    /**
     * Build the Loan type list for the item.
     * @param pLoan the item
     * @param pMenu the menu to build
     */
    private void buildParentMenu(final Loan pLoan,
                                 final TethysScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActiveLoan.buildParentMenu(pMenu, pLoan);
    }

    /**
     * Build the currency list for the item.
     * @param pLoan the item
     * @param pMenu the menu to build
     */
    private void buildCurrencyMenu(final Loan pLoan,
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
