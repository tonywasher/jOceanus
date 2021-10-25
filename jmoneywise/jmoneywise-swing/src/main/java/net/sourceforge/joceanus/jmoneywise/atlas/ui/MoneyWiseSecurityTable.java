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
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.SecurityPanel;
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
 * MoneyWise Security Table.
 */
public class MoneyWiseSecurityTable
        extends MoneyWiseAssetTable<Security> {
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
        super(pView, pUpdateSet, pError, MoneyWiseDataType.SECURITY);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, Security> myTable = getTable();

        /* Create a security panel */
        theActiveSecurity = new SecurityPanel(myGuiFactory, pView, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActiveSecurity);

        /* Set table configuration */
        myTable.setDisabled(Security::isDisabled)
               .setComparator(Security::compareTo)
               .setOnSelect(theActiveSecurity::setItem);

        /* Create the name column */
        myTable.declareStringColumn(Security.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(Security::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Security::setName, r, v));

        /* Create the portfolio type column */
        myTable.declareScrollColumn(Security.FIELD_SECTYPE, SecurityType.class)
                .setMenuConfigurator(this::buildSecurityTypeMenu)
                .setCellValueFactory(Security::getSecurityType)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Security::setSecurityType, r, v));

        /* Create the description column */
        myTable.declareStringColumn(Security.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Security::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Security::setDescription, r, v));

        /* Create the parent column */
        myTable.declareScrollColumn(Security.FIELD_PARENT, Payee.class)
                .setMenuConfigurator(this::buildParentMenu)
                .setCellValueFactory(Security::getParent)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Security::setParent, r, v));

        /* Create the description column */
        myTable.declareStringColumn(SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL))
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Security::getSymbol)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Security::setSymbol, r, v));

        /* Create the currency column */
        myTable.declareScrollColumn(Security.FIELD_CURRENCY, AssetCurrency.class)
                .setMenuConfigurator(this::buildCurrencyMenu)
                .setCellValueFactory(Security::getAssetCurrency)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Security::setAssetCurrency, r, v));

        /* Create the Closed column */
        final Map<Boolean, TethysIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton();
        final TethysTableColumn<Boolean, MetisLetheField, Security> myClosedColumn
                = myTable.declareIconColumn(Security.FIELD_CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(Security::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setOnCommit((r, v) -> updateField(Security::setClosed, r, v));
        declareClosedColumn(myClosedColumn);

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(Security.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

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
        myTask = myTask.startTask("Portfolios");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final SecurityList myBase = myData.getDataList(SecurityList.class);
        theSecurities = (SecurityList) myBase.deriveList(ListStyle.EDIT);
        theSecurities.mapData();
        getTable().setItems(theSecurities.getUnderlyingList());
        getUpdateEntry().setDataList(theSecurities);

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

    /**
     * Build the security type list for the item.
     * @param pSecurity the item
     * @param pMenu the menu to build
     */
    private void buildSecurityTypeMenu(final Security pSecurity,
                                       final TethysScrollMenu<SecurityType> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildSecTypeMenu(pMenu, pSecurity);
    }

    /**
     * Build the security type list for the item.
     * @param pSecurity the item
     * @param pMenu the menu to build
     */
    private void buildParentMenu(final Security pSecurity,
                                 final TethysScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActiveSecurity.buildParentMenu(pMenu, pSecurity);
    }

    /**
     * Build the currency list for the item.
     * @param pSecurity the item
     * @param pMenu the menu to build
     */
    private void buildCurrencyMenu(final Security pSecurity,
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
