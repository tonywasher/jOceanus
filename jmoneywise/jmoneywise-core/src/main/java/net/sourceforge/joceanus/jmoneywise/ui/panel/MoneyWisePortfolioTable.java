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
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolioInfo;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWisePortfolioPanel;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditEntry;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * MoneyWise Portfolio Table.
 */
public class MoneyWisePortfolioTable
        extends MoneyWiseAssetTable<MoneyWisePortfolio> {
    /**
     * The Info UpdateEntry.
     */
    private final PrometheusEditEntry<MoneyWisePortfolioInfo> theInfoEntry;

    /**
     * The Portfolio dialog.
     */
    private final MoneyWisePortfolioPanel theActivePortfolio;

    /**
     * The edit list.
     */
    private MoneyWisePortfolioList thePortfolios;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    MoneyWisePortfolioTable(final MoneyWiseView pView,
                            final PrometheusEditSet pEditSet,
                            final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.PORTFOLIO);

        /* register the infoEntry */
        theInfoEntry = getEditSet().registerType(MoneyWiseBasicDataType.PORTFOLIOINFO);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a portfolio panel */
        theActivePortfolio = new MoneyWisePortfolioPanel(myGuiFactory, pEditSet, pError);
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
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) getView().getData();
        final MoneyWisePortfolioList myBase = myData.getPortfolios();
        thePortfolios = myBase.deriveEditList(getEditSet());
        getTable().setItems(thePortfolios.getUnderlyingList());

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
    void selectPortfolio(final MoneyWisePortfolio pPortfolio) {
        /* Check whether we need to showAll */
        checkShowAll(pPortfolio);

        /* If we are changing the selection */
        final MoneyWisePortfolio myCurrent = theActivePortfolio.getSelectedItem();
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
            final MoneyWisePortfolio myPortfolio = theActivePortfolio.getSelectedItem();
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
    protected void buildCategoryMenu(final MoneyWisePortfolio pPortfolio,
                                     final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu) {
        /* Build the menu */
        theActivePortfolio.buildTypeMenu(pMenu, pPortfolio);
    }

    @Override
    protected void buildParentMenu(final MoneyWisePortfolio pPortfolio,
                                   final TethysUIScrollMenu<MoneyWisePayee> pMenu) {
        /* Build the menu */
        theActivePortfolio.buildParentMenu(pMenu, pPortfolio);
    }

    @Override
    protected void buildCurrencyMenu(final MoneyWisePortfolio pPortfolio,
                                     final TethysUIScrollMenu<MoneyWiseCurrency> pMenu) {
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
            final MoneyWisePortfolio myPortfolio = thePortfolios.addNewItem();
            myPortfolio.setDefaults(getEditSet());

            /* Set as new and adjust map */
            myPortfolio.setNewVersion();
            myPortfolio.adjustMapForItem();
            getEditSet().incrementVersion();

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
