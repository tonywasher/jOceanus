/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfo.PayeeInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWisePayeePanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Payee Table.
 */
public class MoneyWisePayeeTable
        extends MoneyWiseAssetTable<Payee, PayeeType> {
    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<PayeeInfo, MoneyWiseDataType> theInfoEntry;

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
    MoneyWisePayeeTable(final MoneyWiseView pView,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet,
                        final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.PAYEE, PayeeType.class);

        /* register the infoEntry */
        theInfoEntry = getUpdateSet().registerType(MoneyWiseDataType.PAYEEINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<PrometheusDataFieldId, Payee> myTable = getTable();

        /* Create a payee panel */
        theActivePayee = new MoneyWisePayeePanel(myGuiFactory, pUpdateSet, pError);
        declareItemPanel(theActivePayee);

        /* Set table configuration */
        myTable.setOnSelect(theActivePayee::setItem);

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
        final MoneyWiseData myData = getView().getData();
        final PayeeList myBase = myData.getPayees();
        thePayees = myBase.deriveEditList();
        getTable().setItems(thePayees.getUnderlyingList());
        getUpdateEntry().setDataList(thePayees);
        final PayeeInfoList myInfo = thePayees.getPayeeInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActivePayee.refreshData();

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
            getTable().selectRowWithScroll(pPayee);
            theActivePayee.setItem(pPayee);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActivePayee.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectPayee(theActivePayee.getSelectedItem());
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
            getTable().fireTableDataChanged();
            selectPayee(theActivePayee.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    @Override
    protected void buildCategoryMenu(final Payee pPayee,
                                     final TethysUIScrollMenu<PayeeType> pMenu) {
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
