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

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityInfo;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog.MoneyWiseSecurityPanel;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditEntry;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Security Table.
 */
public class MoneyWiseSecurityTable
        extends MoneyWiseAssetTable<MoneyWiseSecurity> {
    /**
     * The Info UpdateEntry.
     */
    private final PrometheusEditEntry<MoneyWiseSecurityInfo> theInfoEntry;

    /**
     * The Info UpdateEntry.
     */
    private final PrometheusEditEntry<MoneyWiseSecurityPrice> thePriceEntry;

    /**
     * The Security dialog.
     */
    private final MoneyWiseSecurityPanel theActiveSecurity;

    /**
     * The edit list.
     */
    private MoneyWiseSecurityList theSecurities;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    MoneyWiseSecurityTable(final MoneyWiseView pView,
                           final PrometheusEditSet pEditSet,
                           final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.SECURITY);

        /* register the info/priceEntries */
        theInfoEntry = getEditSet().registerType(MoneyWiseBasicDataType.SECURITYINFO);
        thePriceEntry = getEditSet().registerType(MoneyWiseBasicDataType.SECURITYPRICE);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, MoneyWiseSecurity> myTable = getTable();

        /* Create a security panel */
        theActiveSecurity = new MoneyWiseSecurityPanel(myGuiFactory, pView, pEditSet, pError);
        declareItemPanel(theActiveSecurity);

        /* Create the symbol column */
        myTable.declareStringColumn(MoneyWiseAccountInfoClass.SYMBOL)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(MoneyWiseSecurity::getSymbol)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseSecurity::setSymbol, r, v));

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
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) getView().getData();
        final MoneyWiseSecurityList myBase = myData.getSecurities();
        theSecurities = myBase.deriveEditList(getEditSet());
        getTable().setItems(theSecurities.getUnderlyingList());

        /* Get the Security prices list */
        MoneyWiseSecurityPriceList myPrices = myData.getSecurityPrices();
        myPrices = myPrices.deriveEditList(getEditSet());
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
    void selectSecurity(final MoneyWiseSecurity pSecurity) {
        /* Check whether we need to showAll */
        checkShowAll(pSecurity);

        /* If we are changing the selection */
        final MoneyWiseSecurity myCurrent = theActiveSecurity.getSelectedItem();
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
            final MoneyWiseSecurity mySecurity = theActiveSecurity.getSelectedItem();
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
    protected void buildCategoryMenu(final MoneyWiseSecurity pSecurity,
                                     final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildSecTypeMenu(pMenu, pSecurity);
    }

    @Override
    protected void buildParentMenu(final MoneyWiseSecurity pSecurity,
                                   final TethysUIScrollMenu<MoneyWisePayee> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildParentMenu(pMenu, pSecurity);
    }

    @Override
    protected void buildCurrencyMenu(final MoneyWiseSecurity pSecurity,
                                     final TethysUIScrollMenu<MoneyWiseCurrency> pMenu) {
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
            final MoneyWiseSecurity mySecurity = theSecurities.addNewItem();
            mySecurity.setDefaults(getEditSet());

            /* Set as new and adjust map */
            mySecurity.setNewVersion();
            mySecurity.adjustMapForItem();
            getEditSet().incrementVersion();

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