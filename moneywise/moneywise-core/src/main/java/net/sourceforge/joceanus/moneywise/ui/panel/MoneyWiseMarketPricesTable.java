/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.ui.MetisAction;
import io.github.tonywasher.joceanus.metis.ui.MetisErrorPanel;
import io.github.tonywasher.joceanus.metis.ui.MetisIcon;
import io.github.tonywasher.joceanus.metis.viewer.MetisViewerEntry;
import io.github.tonywasher.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.controls.MoneyWiseSpotPricesSelect;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseSpotSecurityPrice;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseSpotSecurityPrice.MoneyWiseSpotSecurityList;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseViewResource;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseYQLDownloader;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.ui.PrometheusActionButtons;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusUIEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

/**
 * MoneyWise SpotPrices Table.
 */
public class MoneyWiseMarketPricesTable
        extends MoneyWiseBaseTable<MoneyWiseSpotSecurityPrice> {
    /**
     * The SpotPrices selection panel.
     */
    private final MoneyWiseSpotPricesSelect theSelect;

    /**
     * The account price list.
     */
    private MoneyWiseSpotSecurityList thePrices;

    /**
     * The selected date.
     */
    private OceanusDate theDate;

    /**
     * The Portfolio.
     */
    private MoneyWisePortfolio thePortfolio;

    /**
     * Constructor.
     *
     * @param pView    the view
     * @param pEditSet the editSet
     * @param pError   the error panel
     */
    MoneyWiseMarketPricesTable(final MoneyWiseView pView,
                               final PrometheusEditSet pEditSet,
                               final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.SECURITYPRICE);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, MoneyWiseSpotSecurityPrice> myTable = getTable();

        /* Create new button */
        final TethysUIButton myNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a selection panel */
        theSelect = new MoneyWiseSpotPricesSelect(myGuiFactory, pView);

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseSpotSecurityPrice::isDisabled)
                .setComparator(MoneyWiseSpotSecurityPrice::compareTo);

        /* Create the asset column */
        myTable.declareStringColumn(MoneyWiseBasicDataType.SECURITY)
                .setCellValueFactory(MoneyWiseSpotSecurityPrice::getSecurityName)
                .setEditable(false)
                .setColumnWidth(WIDTH_NAME);

        /* Create the price column */
        myTable.declarePriceColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE)
                .setCellValueFactory(MoneyWiseSpotSecurityPrice::getPrice)
                .setEditable(true)
                .setCellEditable(r -> !r.isDisabled())
                .setColumnWidth(WIDTH_PRICE)
                .setOnCommit((r, v) -> updateField(MoneyWiseSpotSecurityPrice::setPrice, r, v));

        /* Create the previous price column */
        myTable.declarePriceColumn(MoneyWiseViewResource.SPOTPRICE_PREVPRICE)
                .setCellValueFactory(MoneyWiseSpotSecurityPrice::getPrevPrice)
                .setName(MoneyWiseViewResource.SPOTPRICE_PREVPRICE.getValue())
                .setEditable(false)
                .setColumnWidth(WIDTH_PRICE);

        /* Create the previous date column */
        myTable.declareDateColumn(MoneyWiseViewResource.SPOTEVENT_PREVDATE)
                .setCellValueFactory(MoneyWiseSpotSecurityPrice::getPrevDate)
                .setName(MoneyWiseViewResource.SPOTEVENT_PREVDATE.getValue())
                .setEditable(false)
                .setColumnWidth(WIDTH_DATE);

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(myGuiFactory);
        myTable.declareIconColumn(PrometheusDataResource.DATAITEM_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.getPrice() != null && !r.isDisabled() ? MetisAction.DELETE : MetisAction.DO)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> r.getPrice() != null && !r.isDisabled())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.SELECTIONCHANGED, e -> handleNewSelection());
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.DOWNLOAD, e -> downloadPrices());
        pView.getEventRegistrar().addEventListener(e -> refreshData());
        pEditSet.getEventRegistrar().addEventListener(e -> updateTableData());
    }

    /**
     * Obtain the selection panel.
     *
     * @return the select panel
     */
    MoneyWiseSpotPricesSelect getSelect() {
        return theSelect;
    }

    @Override
    protected void refreshData() {
        /* Obtain the active profile */
        OceanusProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("SpotPrices1");

        /* Refresh the data */
        theSelect.refreshData();

        /* Access the selection details */
        setSelection(theSelect.getPortfolio(), theSelect.getDate());

        /* Create SavePoint */
        theSelect.createSavePoint();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Set Selection to the specified portfolio and date.
     *
     * @param pPortfolio the portfolio
     * @param pDate      the Date for the extract
     */
    public void setSelection(final MoneyWisePortfolio pPortfolio,
                             final OceanusDate pDate) {
        /* Record selection */
        theDate = pDate;
        thePortfolio = pPortfolio;

        /* If selection is valid */
        if (theDate != null
                && thePortfolio != null) {
            /* Create the new list */
            thePrices = new MoneyWiseSpotSecurityList(getView(), pPortfolio, pDate);

            /* Update Next/Previous values */
            theSelect.setAdjacent(thePrices.getPrev(), thePrices.getNext());

            /* else invalid selection */
        } else {
            /* Set no selection */
            thePrices = null;
            theSelect.setAdjacent(null, null);
        }

        /* Update other details */
        getTable().setItems(thePrices == null ? null : thePrices.getUnderlyingList());
        getEditEntry().setDataList(thePrices);
        theSelect.setEnabled(true);
    }

    /**
     * handle new selection.
     */
    private void handleNewSelection() {
        /* Set the deleted option */
        setShowAll();

        /* Access selection */
        final MoneyWisePortfolio myPortfolio = theSelect.getPortfolio();
        final OceanusDate myDate = theSelect.getDate();

        /* If the selection differs */
        if (!MetisDataDifference.isEqual(theDate, myDate)
                || !MetisDataDifference.isEqual(thePortfolio, myPortfolio)) {
            /* Set selection */
            setSelection(myPortfolio, myDate);

            /* Create SavePoint */
            theSelect.createSavePoint();
        }
    }

    /**
     * Set the showAll indicator.
     */
    private void setShowAll() {
        cancelEditing();
        getTable().setFilter(this::isFiltered);
    }

    @Override
    protected boolean isFiltered(final MoneyWiseSpotSecurityPrice pRow) {
        /* Handle filter */
        return theSelect.getShowClosed() || !pRow.isDisabled();
    }

    @Override
    protected void deleteRow(final MoneyWiseSpotSecurityPrice pRow,
                             final Object pValue) throws OceanusException {
        pRow.setPrice(null);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        getTable().requestFocus();
    }

    @Override
    protected void notifyChanges() {
        theSelect.setEnabled(!hasUpdates());
        super.notifyChanges();
    }

    /**
     * Download prices.
     */
    private void downloadPrices() {
        /* Cancel editing */
        cancelEditing();

        /* Protect against exceptions */
        try {
            /* Download Prices */
            if (MoneyWiseYQLDownloader.downloadPrices(thePrices)) {
                /* Increment data version */
                getEditSet().incrementVersion();

                /* Update components to reflect changes */
                updateTableData();
                notifyChanges();
            }
        } catch (OceanusException e) {
            /* Show the error */
            setError(e);
        }
    }

    /**
     * SpotPrices Panel.
     */
    public static class MoneyWiseSpotPricesPanel
            implements TethysUIComponent, OceanusEventProvider<PrometheusDataEvent> {
        /**
         * Text for DataEntry Title.
         */
        private static final String NLS_DATAENTRY = MoneyWiseUIResource.PRICES_DATAENTRY.getValue();

        /**
         * The Event Manager.
         */
        private final OceanusEventManager<PrometheusDataEvent> theEventManager;

        /**
         * The updateSet.
         */
        private final PrometheusEditSet theEditSet;

        /**
         * The error panel.
         */
        private final MetisErrorPanel theError;

        /**
         * The action buttons.
         */
        private final PrometheusActionButtons theActionButtons;

        /**
         * The table.
         */
        private final MoneyWiseMarketPricesTable theTable;

        /**
         * The panel.
         */
        private final TethysUIBorderPaneManager thePanel;

        /**
         * The viewer entry.
         */
        private final MetisViewerEntry theViewerPrice;

        /**
         * Constructor.
         *
         * @param pView the data view
         */
        public MoneyWiseSpotPricesPanel(final MoneyWiseView pView) {
            /* Build the Update set and entry */
            theEditSet = new PrometheusEditSet(pView);

            /* Create the event manager */
            theEventManager = new OceanusEventManager<>();

            /* Create the top level viewer entry for this view */
            final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
            final MetisViewerManager myViewer = pView.getViewerManager();
            theViewerPrice = myViewer.newEntry(mySection, NLS_DATAENTRY);
            theViewerPrice.setTreeObject(theEditSet);

            /* Create the error panel for this view */
            theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerPrice);

            /* Create the table */
            theTable = new MoneyWiseMarketPricesTable(pView, theEditSet, theError);

            /* Create the action buttons */
            final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
            theActionButtons = new PrometheusActionButtons(myGuiFactory, theEditSet);
            theActionButtons.setVisible(false);

            /* Create the header panel */
            final TethysUIPaneFactory myPanes = myGuiFactory.paneFactory();
            final TethysUIBorderPaneManager myHeader = myPanes.newBorderPane();
            myHeader.setCentre(theTable.getSelect());
            myHeader.setNorth(theError);
            myHeader.setEast(theActionButtons);

            /* Create the panel */
            thePanel = myPanes.newBorderPane();
            thePanel.setNorth(myHeader);
            thePanel.setCentre(theTable);

            /* Add listeners */
            theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
            theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
            theTable.getEventRegistrar().addEventListener(e -> notifyChanges());
        }

        @Override
        public TethysUIComponent getUnderlying() {
            return thePanel;
        }

        @Override
        public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * handleErrorPane.
         */
        private void handleErrorPane() {
            /* Determine whether we have an error */
            final boolean isError = theError.hasError();

            /* Hide selection panel on error */
            theTable.getSelect().setVisible(!isError);

            /* Lock scroll area */
            theTable.setEnabled(!isError);

            /* Lock Action Buttons */
            theActionButtons.setEnabled(!isError);
        }

        /**
         * handle Action Buttons.
         *
         * @param pEvent the event
         */
        private void handleActionButtons(final OceanusEvent<PrometheusUIEvent> pEvent) {
            /* Cancel editing */
            theTable.cancelEditing();

            /* Perform the command */
            theEditSet.processCommand(pEvent.getEventId(), theError);

            /* Adjust for changes */
            theTable.notifyChanges();
        }

        /**
         * Determine Focus.
         */
        public void determineFocus() {
            /* Request the focus */
            theTable.determineFocus();

            /* Focus on the Data entry */
            theViewerPrice.setFocus();
        }

        /**
         * Call underlying controls to take notice of changes in view/selection.
         */
        private void notifyChanges() {
            /* Determine whether we have updates */
            final boolean hasUpdates = hasUpdates();

            /* Update the table buttons */
            theActionButtons.setEnabled(true);
            theActionButtons.setVisible(hasUpdates);

            /* Notify listeners */
            theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
        }

        /**
         * Does the panel have updates?
         *
         * @return true/false
         */
        public boolean hasUpdates() {
            return theTable.hasUpdates();
        }

        /**
         * Does the panel have a session?
         *
         * @return true/false
         */
        public boolean hasSession() {
            return theTable.hasUpdates();
        }

        /**
         * Does the panel have errors?
         *
         * @return true/false
         */
        public boolean hasErrors() {
            return theTable.hasErrors();
        }
    }
}
