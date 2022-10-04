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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWisePortfolioPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Portfolio Table.
 */
public class MoneyWisePortfolioTable
        extends MoneyWiseAssetTable<Portfolio, PortfolioType> {
    /**
     * The Info UpdateEntry.
     */
    private final UpdateEntry<PortfolioInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The Portfolio dialog.
     */
    private final MoneyWisePortfolioPanel theActivePortfolio;

    /**
     * The edit list.
     */
    private PortfolioList thePortfolios;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWisePortfolioTable(final MoneyWiseView pView,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.PORTFOLIO, PortfolioType.class);

        /* register the infoEntry */
        theInfoEntry = getUpdateSet().registerType(MoneyWiseDataType.PORTFOLIOINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a portfolio panel */
        theActivePortfolio = new MoneyWisePortfolioPanel(myGuiFactory, pUpdateSet, pError);
        declareItemPanel(theActivePortfolio);

        /* Finish the table */
        finishTable(true, true, true);

        /* Add listeners */
        theActivePortfolio.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActivePortfolio.isEditing();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Portfolios");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final PortfolioList myBase = myData.getPortfolios();
        thePortfolios = myBase.deriveEditList(getUpdateSet());
        getTable().setItems(thePortfolios.getUnderlyingList());
        getUpdateEntry().setDataList(thePortfolios);
        final PortfolioInfoList myInfo = thePortfolios.getPortfolioInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActivePortfolio.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActivePortfolio.setEditable(false);
    }

    /**
     * Select portfolio.
     * @param pPortfolio the portfolio to select
     */
    void selectPortfolio(final Portfolio pPortfolio) {
        /* Check whether we need to showAll */
        checkShowAll(pPortfolio);

        /* If we are changing the selection */
        final Portfolio myCurrent = theActivePortfolio.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pPortfolio)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRow(pPortfolio);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActivePortfolio.isEditing()) {
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
        if (!theActivePortfolio.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            final Portfolio myPortfolio = theActivePortfolio.getSelectedItem();
            updateTableData();
            if (myPortfolio != null) {
                getTable().selectRow(myPortfolio);
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
    protected void buildCategoryMenu(final Portfolio pPortfolio,
                                     final TethysUIScrollMenu<PortfolioType> pMenu) {
        /* Build the menu */
        theActivePortfolio.buildTypeMenu(pMenu, pPortfolio);
    }

    @Override
    protected void buildParentMenu(final Portfolio pPortfolio,
                                   final TethysUIScrollMenu<Payee> pMenu) {
        /* Build the menu */
        theActivePortfolio.buildParentMenu(pMenu, pPortfolio);
    }

    @Override
    protected void buildCurrencyMenu(final Portfolio pPortfolio,
                                     final TethysUIScrollMenu<AssetCurrency> pMenu) {
        /* Build the menu */
        theActivePortfolio.buildCurrencyMenu(pMenu, pPortfolio);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new asset */
            final Portfolio myPortfolio = thePortfolios.addNewItem();
            myPortfolio.setDefaults(getUpdateSet());

            /* Set as new and adjust map */
            myPortfolio.setNewVersion();
            myPortfolio.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            myPortfolio.validate();
            theActivePortfolio.setNewItem(myPortfolio);

            /* Lock the table */
            setTableEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new portfolio", e);

            /* Show the error */
            setError(myError);
        }
    }
}
