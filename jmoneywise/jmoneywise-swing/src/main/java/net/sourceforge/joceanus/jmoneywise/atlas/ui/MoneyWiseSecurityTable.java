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
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.SecurityPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jmoneywise.lethe.views.ViewSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.views.ViewSecurityPrice.ViewSecurityPriceList;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Security Table.
 */
public class MoneyWiseSecurityTable
        extends MoneyWiseAssetTable<Security, SecurityType> {
    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<SecurityInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<ViewSecurityPrice, MoneyWiseDataType> thePriceEntry;

    /**
     * The Security dialog.
     */
    private final SecurityPanel theActiveSecurity;

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
    MoneyWiseSecurityTable(final MoneyWiseView pView,
                           final UpdateSet<MoneyWiseDataType> pUpdateSet,
                           final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.SECURITY, SecurityType.class);

        /* register the info/priceEntries */
        theInfoEntry = getUpdateSet().registerType(MoneyWiseDataType.SECURITYINFO);
        thePriceEntry = getUpdateSet().registerType(MoneyWiseDataType.SECURITYPRICE);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, Security> myTable = getTable();

        /* Create a security panel */
        theActiveSecurity = new SecurityPanel(myGuiFactory, pView, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActiveSecurity);

        /* Set table configuration */
        myTable.setOnSelect(theActiveSecurity::setItem);

        /* Create the symbol column */
        myTable.declareStringColumn(SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL))
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Security::getSymbol)
                .setEditable(true)
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
        MetisProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Securities");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final SecurityList myBase = myData.getSecurities();
        theSecurities = myBase.deriveEditList(getUpdateSet());
        getTable().setItems(theSecurities.getUnderlyingList());
        getUpdateEntry().setDataList(theSecurities);
        final SecurityInfoList myInfo = theSecurities.getSecurityInfo();
        theInfoEntry.setDataList(myInfo);

        /* Get the Security prices list */
        final ViewSecurityPriceList myPrices = new ViewSecurityPriceList(getView(), getUpdateSet());
        thePriceEntry.setDataList(myPrices);

        /* Notify panel of refresh */
        theActiveSecurity.refreshData();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void cancelEditing() {
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
            /* Select the row and ensure that it is visible */
            getTable().selectRowWithScroll(pSecurity);
            theActiveSecurity.setItem(pSecurity);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveSecurity.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectSecurity(theActiveSecurity.getSelectedItem());
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
            getTable().fireTableDataChanged();
            selectSecurity(theActiveSecurity.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    @Override
    protected void buildCategoryMenu(final Security pSecurity,
                                     final TethysScrollMenu<SecurityType> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildSecTypeMenu(pMenu, pSecurity);
    }

    @Override
    protected void buildParentMenu(final Security pSecurity,
                                   final TethysScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildParentMenu(pMenu, pSecurity);
    }

    @Override
    protected void buildCurrencyMenu(final Security pSecurity,
                                     final TethysScrollMenu<AssetCurrency> pMenu) {
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
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new security", e);

            /* Show the error */
            setError(myError);
        }
    }
}
