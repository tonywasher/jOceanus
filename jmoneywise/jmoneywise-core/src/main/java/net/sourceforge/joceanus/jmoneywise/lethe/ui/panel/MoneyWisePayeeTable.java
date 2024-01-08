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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.panel;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseXAssetTable;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.MoneyWisePayeePanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseXView;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Payee Table.
 */
public class MoneyWisePayeeTable
        extends MoneyWiseXAssetTable<Payee> {
    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<PayeeInfo> theInfoEntry;

    /**
     * The Payee dialog.
     */
    private final MoneyWisePayeePanel theActivePayee;

    /**
     * The edit list.
     */
    private PayeeList thePayees;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWisePayeeTable(final MoneyWiseXView pView,
                        final UpdateSet pUpdateSet,
                        final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.PAYEE);

        /* register the infoEntry */
        theInfoEntry = getUpdateSet().registerType(MoneyWiseDataType.PAYEEINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a payee panel */
        theActivePayee = new MoneyWisePayeePanel(myGuiFactory, pUpdateSet, pError);
        declareItemPanel(theActivePayee);

        /* Finish the table */
        finishTable(false, false, true);

        /* Add listeners */
        theActivePayee.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActivePayee.isEditing();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Payees");

        /* Access list */
        final MoneyWiseData myData = (MoneyWiseData) getView().getData();
        final PayeeList myBase = myData.getPayees();
        thePayees = myBase.deriveEditList();
        getTable().setItems(thePayees.getUnderlyingList());
        getUpdateEntry().setDataList(thePayees);
        final PayeeInfoList myInfo = thePayees.getPayeeInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActivePayee.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActivePayee.setEditable(false);
    }

    /**
     * Select payee.
     * @param pPayee the payee to select
     */
    void selectPayee(final Payee pPayee) {
        /* Check whether we need to showAll */
        checkShowAll(pPayee);

        /* If we are changing the selection */
        final Payee myCurrent = theActivePayee.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pPayee)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRow(pPayee);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActivePayee.isEditing()) {
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
        if (!theActivePayee.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            final Payee myPayee = theActivePayee.getSelectedItem();
            updateTableData();
            if (myPayee != null) {
                getTable().selectRow(myPayee);
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
    protected void buildCategoryMenu(final Payee pPayee,
                                     final TethysUIScrollMenu<AssetCategory> pMenu) {
        /* Build the menu */
        theActivePayee.buildPayeeTypeMenu(pMenu, pPayee);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new category */
            final Payee myPayee = thePayees.addNewItem();
            myPayee.setDefaults();

            /* Set as new and adjust map */
            myPayee.setNewVersion();
            myPayee.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            myPayee.validate();
            theActivePayee.setNewItem(myPayee);

            /* Lock the table */
            setTableEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new payee", e);

            /* Show the error */
            setError(myError);
        }
    }
}
