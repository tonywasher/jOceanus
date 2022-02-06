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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.panel;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseSpotPricesSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jmoneywise.lethe.views.SpotSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.views.SpotSecurityPrice.SpotSecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.views.YQLDownloader;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise SpotPrices Table.
 */
public class MoneyWiseSpotPricesTable
        extends MoneyWiseBaseTable<SpotSecurityPrice> {
    /**
     * The SpotPrices selection panel.
     */
    private final MoneyWiseSpotPricesSelect theSelect;

    /**
     * The account price list.
     */
    private SpotSecurityList thePrices;

    /**
     * The selected date.
     */
    private TethysDate theDate;

    /**
     * The Portfolio.
     */
    private Portfolio thePortfolio;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseSpotPricesTable(final MoneyWiseView pView,
                             final UpdateSet<MoneyWiseDataType> pUpdateSet,
                             final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.SECURITYPRICE);

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, SpotSecurityPrice> myTable = getTable();

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a selection panel */
        theSelect = new MoneyWiseSpotPricesSelect(myGuiFactory, pView);

        /* Set table configuration */
        myTable.setDisabled(SpotSecurityPrice::isDisabled)
               .setComparator(SpotSecurityPrice::compareTo);

        /* Create the asset column */
        myTable.declareStringColumn(SecurityPrice.FIELD_SECURITY)
               .setCellValueFactory(SpotSecurityPrice::getSecurityName)
               .setEditable(false)
               .setColumnWidth(WIDTH_NAME);

        /* Create the price column */
        myTable.declarePriceColumn(SecurityPrice.FIELD_PRICE)
               .setCellValueFactory(SecurityPrice::getPrice)
               .setEditable(true)
               .setCellEditable(r -> !r.isDisabled())
               .setColumnWidth(WIDTH_PRICE)
               .setOnCommit((r, v) -> updateField(SpotSecurityPrice::setPrice, r, v));

        /* Create the previous price column */
        myTable.declarePriceColumn(SpotSecurityPrice.FIELD_PREVPRICE)
               .setCellValueFactory(SpotSecurityPrice::getPrevPrice)
               .setEditable(false)
               .setColumnWidth(WIDTH_PRICE);

        /* Create the previous date column */
        myTable.declareDateColumn(SpotSecurityPrice.FIELD_PREVDATE)
               .setCellValueFactory(SpotSecurityPrice::getPrevDate)
               .setEditable(false)
               .setColumnWidth(WIDTH_DATE);

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(SecurityPrice.FIELD_TOUCH, MetisAction.class)
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
        pUpdateSet.getEventRegistrar().addEventListener(e -> myTable.fireTableDataChanged());
    }

    /**
     * Obtain the selection panel.
     * @return the select panel
     */
    MoneyWiseSpotPricesSelect getSelect() {
        return theSelect;
    }

    @Override
    protected void refreshData() {
        /* Obtain the active profile */
        MetisProfile myTask = getView().getActiveTask();
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
     * @param pPortfolio the portfolio
     * @param pDate the Date for the extract
     */
    public void setSelection(final Portfolio pPortfolio,
                             final TethysDate pDate) {
        /* Record selection */
        theDate = pDate;
        thePortfolio = pPortfolio;

        /* If selection is valid */
        if (theDate != null
                && thePortfolio != null) {
            /* Create the new list */
            thePrices = new SpotSecurityList(getView(), pPortfolio, pDate);

            /* Update Next/Previous values */
            theSelect.setAdjacent(thePrices.getPrev(), thePrices.getNext());

            /* else invalid selection */
        } else {
            /* Set no selection */
            thePrices = null;
            theSelect.setAdjacent(null, null);
        }

        /* Update other details */
        getTable().setItems(thePrices.getUnderlyingList());
        getUpdateEntry().setDataList(thePrices);
        theSelect.setEnabled(true);
    }

    /**
     * handle new selection.
     */
    private void handleNewSelection() {
        /* Set the deleted option */
        setShowAll();

        /* Access selection */
        final Portfolio myPortfolio = theSelect.getPortfolio();
        final TethysDate myDate = theSelect.getDate();

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
    protected boolean isFiltered(final SpotSecurityPrice pRow) {
        /* Handle filter */
        return theSelect.getShowClosed() || !pRow.isDisabled();
    }

    @Override
    protected void deleteRow(final SpotSecurityPrice pRow,
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
            if (YQLDownloader.downloadPrices(thePrices)) {
                /* Increment data version */
                getUpdateSet().incrementVersion();

                /* Update components to reflect changes */
                getTable().fireTableDataChanged();
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
            implements TethysComponent, TethysEventProvider<PrometheusDataEvent> {
        /**
         * Text for DataEntry Title.
         */
        private static final String NLS_DATAENTRY = MoneyWiseUIResource.PRICES_DATAENTRY.getValue();

        /**
         * The Event Manager.
         */
        private final TethysEventManager<PrometheusDataEvent> theEventManager;

        /**
         * The updateSet.
         */
        private final UpdateSet<MoneyWiseDataType> theUpdateSet;

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
        private final MoneyWiseSpotPricesTable theTable;

        /**
         * The panel.
         */
        private final TethysBorderPaneManager thePanel;

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
            theUpdateSet = new UpdateSet<>(pView, MoneyWiseDataType.class);

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Create the top level viewer entry for this view */
            final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
            final MetisViewerManager myViewer = pView.getViewerManager();
            theViewerPrice = myViewer.newEntry(mySection, NLS_DATAENTRY);
            theViewerPrice.setTreeObject(theUpdateSet);

            /* Create the error panel for this view */
            theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerPrice);

            /* Create the table */
            theTable = new MoneyWiseSpotPricesTable(pView, theUpdateSet, theError);

            /* Create the action buttons */
            final TethysGuiFactory myGuiFactory = pView.getGuiFactory();
            theActionButtons = new PrometheusActionButtons(myGuiFactory, theUpdateSet);
            theActionButtons.setVisible(false);

            /* Create the header panel */
            final TethysBorderPaneManager myHeader = myGuiFactory.newBorderPane();
            myHeader.setCentre(theTable.getSelect());
            myHeader.setNorth(theError);
            myHeader.setEast(theActionButtons);

            /* Create the panel */
            thePanel = myGuiFactory.newBorderPane();
            thePanel.setNorth(myHeader);
            thePanel.setCentre(theTable);

            /* Add listeners */
            theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
            theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
            theTable.getEventRegistrar().addEventListener(e -> notifyChanges());
        }

        @Override
        public Integer getId() {
            return thePanel.getId();
        }

        @Override
        public TethysNode getNode() {
            return thePanel.getNode();
        }

        @Override
        public void setEnabled(boolean pEnabled) {
            thePanel.setEnabled(pEnabled);
        }

        @Override
        public void setVisible(boolean pVisible) {
            thePanel.setVisible(pVisible);
        }

        @Override
        public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
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
         * @param pEvent the event
         */
        private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
            /* Cancel editing */
            theTable.cancelEditing();

            /* Perform the command */
            theUpdateSet.processCommand(pEvent.getEventId(), theError);

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
         * @return true/false
         */
        public boolean hasUpdates() {
            return theTable.hasUpdates();
        }

        /**
         * Does the panel have a session?
         * @return true/false
         */
        public boolean hasSession() {
            return theTable.hasUpdates();
        }

        /**
         * Does the panel have errors?
         * @return true/false
         */
        public boolean hasErrors() {
            return theTable.hasErrors();
        }
    }
}