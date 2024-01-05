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
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.MoneyWiseSecurityPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseXView;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Security Table.
 */
public class MoneyWiseSecurityTable
        extends MoneyWiseAssetTable<Security> {
    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<SecurityInfo> theInfoEntry;

    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<SecurityPrice> thePriceEntry;

    /**
     * The Security dialog.
     */
    private final MoneyWiseSecurityPanel theActiveSecurity;

    /**
     * The edit list.
     */
    private SecurityList theSecurities;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseSecurityTable(final MoneyWiseXView pView,
                           final UpdateSet pUpdateSet,
                           final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.SECURITY);

        /* register the info/priceEntries */
        theInfoEntry = getUpdateSet().registerType(MoneyWiseDataType.SECURITYINFO);
        thePriceEntry = getUpdateSet().registerType(MoneyWiseDataType.SECURITYPRICE);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<PrometheusDataFieldId, Security> myTable = getTable();

        /* Create a security panel */
        theActiveSecurity = new MoneyWiseSecurityPanel(myGuiFactory, pView, pUpdateSet, pError);
        declareItemPanel(theActiveSecurity);

        /* Create the symbol column */
        myTable.declareStringColumn(MoneyWiseAssetDataId.SECURITYSYMBOL)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Security::getSymbol)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(Security::setSymbol, r, v));

        /* Finish the table */
        finishTable(true, true, false);

        /* Add listeners */
        theActiveSecurity.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveSecurity.isEditing();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Securities");

        /* Access list */
        final MoneyWiseData myData = (MoneyWiseData) getView().getData();
        final SecurityList myBase = myData.getSecurities();
        theSecurities = myBase.deriveEditList(getUpdateSet());
        getTable().setItems(theSecurities.getUnderlyingList());
        getUpdateEntry().setDataList(theSecurities);
        final SecurityInfoList myInfo = theSecurities.getSecurityInfo();
        theInfoEntry.setDataList(myInfo);

        /* Get the Security prices list */
        SecurityPriceList myPrices = myData.getSecurityPrices();
        myPrices = myPrices.deriveEditList(getUpdateSet());
        thePriceEntry.setDataList(myPrices);

        /* Notify panel of refresh */
        theActiveSecurity.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveSecurity.setEditable(false);
    }

    /**
     * Select security.
     * @param pSecurity the security to select
     */
    void selectSecurity(final Security pSecurity) {
        /* Check whether we need to showAll */
        checkShowAll(pSecurity);

        /* If we are changing the selection */
        final Security myCurrent = theActiveSecurity.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pSecurity)) {
            /* Select the row */
            getTable().selectRow(pSecurity);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveSecurity.isEditing()) {
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
        if (!theActiveSecurity.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            final Security mySecurity = theActiveSecurity.getSelectedItem();
            updateTableData();
            if (mySecurity != null) {
                getTable().selectRow(mySecurity);
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
    protected void buildCategoryMenu(final Security pSecurity,
                                     final TethysUIScrollMenu<AssetCategory> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildSecTypeMenu(pMenu, pSecurity);
    }

    @Override
    protected void buildParentMenu(final Security pSecurity,
                                   final TethysUIScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildParentMenu(pMenu, pSecurity);
    }

    @Override
    protected void buildCurrencyMenu(final Security pSecurity,
                                     final TethysUIScrollMenu<AssetCurrency> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildCurrencyMenu(pMenu, pSecurity);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new asset */
            final Security mySecurity = theSecurities.addNewItem();
            mySecurity.setDefaults(getUpdateSet());

            /* Set as new and adjust map */
            mySecurity.setNewVersion();
            mySecurity.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            mySecurity.validate();
            theActiveSecurity.setNewItem(mySecurity);

            /* Lock the table */
            setTableEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new security", e);

            /* Show the error */
            setError(myError);
        }
    }
}
