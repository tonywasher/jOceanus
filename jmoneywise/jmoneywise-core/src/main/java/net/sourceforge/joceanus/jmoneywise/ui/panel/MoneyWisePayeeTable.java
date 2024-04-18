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
package net.sourceforge.joceanus.jmoneywise.ui.panel;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayeeInfo;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWisePayeePanel;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditEntry;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Payee Table.
 */
public class MoneyWisePayeeTable
        extends MoneyWiseAssetTable<MoneyWisePayee> {
    /**
     * The Info UpdateEntry.
     */
    private final PrometheusEditEntry<MoneyWisePayeeInfo> theInfoEntry;

    /**
     * The Payee dialog.
     */
    private final MoneyWisePayeePanel theActivePayee;

    /**
     * The edit list.
     */
    private MoneyWisePayeeList thePayees;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    MoneyWisePayeeTable(final MoneyWiseView pView,
                        final PrometheusEditSet pEditSet,
                        final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.PAYEE);

        /* register the infoEntry */
        theInfoEntry = getEditSet().registerType(MoneyWiseBasicDataType.PAYEEINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a payee panel */
        theActivePayee = new MoneyWisePayeePanel(myGuiFactory, pEditSet, this);
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
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) getView().getData();
        final MoneyWisePayeeList myBase = myData.getPayees();
        thePayees = myBase.deriveEditList(getEditSet());
        getTable().setItems(thePayees.getUnderlyingList());

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
    void selectPayee(final MoneyWisePayee pPayee) {
        /* Check whether we need to showAll */
        checkShowAll(pPayee);

        /* If we are changing the selection */
        final MoneyWisePayee myCurrent = theActivePayee.getSelectedItem();
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
            final MoneyWisePayee myPayee = theActivePayee.getSelectedItem();
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
    protected void buildCategoryMenu(final MoneyWisePayee pPayee,
                                     final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu) {
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
            final MoneyWisePayee myPayee = thePayees.addNewItem();
            myPayee.setDefaults();

            /* Set as new and adjust map */
            myPayee.setNewVersion();
            myPayee.adjustMapForItem();
            getEditSet().incrementVersion();

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
