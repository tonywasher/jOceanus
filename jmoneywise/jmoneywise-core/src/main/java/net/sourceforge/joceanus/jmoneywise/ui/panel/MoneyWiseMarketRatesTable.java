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

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseSpotRatesSelect;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseSpotExchangeRate;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseSpotExchangeRate.MoneyWiseSpotExchangeList;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseViewResource;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseYQLDownloader;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jprometheus.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise SpotRates Table.
 */
public class MoneyWiseMarketRatesTable
        extends MoneyWiseBaseTable<MoneyWiseSpotExchangeRate> {
    /**
     * The SpotRates selection panel.
     */
    private final MoneyWiseSpotRatesSelect theSelect;

    /**
     * The exchangeRates list.
     */
    private MoneyWiseSpotExchangeList theRates;

    /**
     * The selected date.
     */
    private TethysDate theDate;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    MoneyWiseMarketRatesTable(final MoneyWiseView pView,
                              final PrometheusEditSet pEditSet,
                              final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.EXCHANGERATE);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, MoneyWiseSpotExchangeRate> myTable = getTable();

        /* Create new button */
        final TethysUIButton myNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create a selection panel */
        theSelect = new MoneyWiseSpotRatesSelect(myGuiFactory, pView);

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseSpotExchangeRate::isDisabled)
                .setComparator(MoneyWiseSpotExchangeRate::compareTo);

        /* Create the description column */
        myTable.declareStringColumn(MoneyWiseBasicResource.XCHGRATE_FROM)
                .setCellValueFactory(r -> r.getToCurrency().getDesc())
                .setEditable(false)
                .setColumnWidth(WIDTH_NAME);

        /* Create the symbol column */
        myTable.declareStringColumn(MoneyWiseBasicResource.XCHGRATE_TO)
                .setCellValueFactory(r -> r.getToCurrency().getName())
                .setName(MoneyWiseUIResource.SPOTRATE_COLUMN_SYMBOL.getValue())
                .setEditable(false)
                .setColumnWidth(WIDTH_NAME);

        /* Create the price column */
        myTable.declareRatioColumn(MoneyWiseBasicResource.XCHGRATE_RATE)
                .setCellValueFactory(MoneyWiseSpotExchangeRate::getExchangeRate)
                .setEditable(true)
                .setCellEditable(r -> !r.isDisabled())
                .setColumnWidth(WIDTH_PRICE)
                .setOnCommit((r, v) -> updateField(MoneyWiseSpotExchangeRate::setExchangeRate, r, v));

        /* Create the previous rate column */
        myTable.declareRatioColumn(MoneyWiseViewResource.SPOTRATE_PREVRATE)
                .setCellValueFactory(MoneyWiseSpotExchangeRate::getPrevRate)
                .setName(MoneyWiseViewResource.SPOTRATE_PREVRATE.getValue())
                .setEditable(false)
                .setColumnWidth(WIDTH_PRICE);

        /* Create the previous date column */
        myTable.declareDateColumn(MoneyWiseViewResource.SPOTEVENT_PREVDATE)
                .setCellValueFactory(MoneyWiseSpotExchangeRate::getPrevDate)
                .setName(MoneyWiseViewResource.SPOTEVENT_PREVDATE.getValue())
                .setEditable(false)
                .setColumnWidth(WIDTH_DATE);

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(myGuiFactory);
        myTable.declareIconColumn(PrometheusDataResource.DATAITEM_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.getExchangeRate() != null && !r.isDisabled() ? MetisAction.DELETE : MetisAction.DO)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> r.getExchangeRate() != null && !r.isDisabled())
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
     * @return the select panel
     */
    MoneyWiseSpotRatesSelect getSelect() {
        return theSelect;
    }

    @Override
    protected void refreshData() {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("SpotRates1");

        /* Refresh the data */
        theSelect.refreshData();

        /* Access the selection details */
        setSelection(theSelect.getDate());

        /* Create SavePoint */
        theSelect.createSavePoint();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Set Selection to the date.
     * @param pDate the Date for the extract
     */
    public void setSelection(final TethysDate pDate) {
        /* Record selection */
        theDate = pDate;

        /* If selection is valid */
        if (theDate != null) {
            /* Create the new list */
            theRates = new MoneyWiseSpotExchangeList(getView(), pDate);

            /* Update Next/Previous values */
            theSelect.setAdjacent(theRates.getPrev(), theRates.getNext());

            /* else invalid selection */
        } else {
            /* Set no selection */
            theRates = null;
            theSelect.setAdjacent(null, null);
        }

        /* Update other details */
        getTable().setItems(theRates.getUnderlyingList());
        getEditEntry().setDataList(theRates);
        theSelect.setEnabled(true);
    }

    /**
     * handle new selection.
     */
    private void handleNewSelection() {
        /* Access selection */
        final TethysDate myDate = theSelect.getDate();

        /* If the selection differs */
        if (!MetisDataDifference.isEqual(theDate, myDate)) {
            /* Set selection */
            setSelection(myDate);

            /* Create SavePoint */
            theSelect.createSavePoint();
        }
    }

    @Override
    protected boolean isFiltered(final MoneyWiseSpotExchangeRate pRow) {
        /* Handle filter */
        return !pRow.isDisabled();
    }

    @Override
    protected void deleteRow(final MoneyWiseSpotExchangeRate pRow,
                             final Object pValue) throws OceanusException {
        pRow.setExchangeRate(null);
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
            /* Download Rates */
            if (MoneyWiseYQLDownloader.downloadRates(theRates)) {
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
     * SpotRates Panel.
     */
    public static class MoneyWiseSpotRatesPanel
            implements TethysUIComponent, TethysEventProvider<PrometheusDataEvent> {
        /**
         * Text for DataEntry Title.
         */
        private static final String NLS_DATAENTRY = MoneyWiseUIResource.RATES_DATAENTRY.getValue();

        /**
         * The Event Manager.
         */
        private final TethysEventManager<PrometheusDataEvent> theEventManager;

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
        private final MoneyWiseMarketRatesTable theTable;

        /**
         * The panel.
         */
        private final TethysUIBorderPaneManager thePanel;

        /**
         * The viewer entry.
         */
        private final MetisViewerEntry theViewerRate;

        /**
         * Constructor.
         *
         * @param pView the data view
         */
        public MoneyWiseSpotRatesPanel(final MoneyWiseView pView) {
            /* Build the Update set and entry */
            theEditSet = new PrometheusEditSet(pView);

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Create the top level viewer entry for this view */
            final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
            final MetisViewerManager myViewer = pView.getViewerManager();
            theViewerRate = myViewer.newEntry(mySection, NLS_DATAENTRY);
            theViewerRate.setTreeObject(theEditSet);

            /* Create the error panel for this view */
            theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerRate);

            /* Create the table */
            theTable = new MoneyWiseMarketRatesTable(pView, theEditSet, theError);

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
        public void setEnabled(final boolean pEnabled) {
            thePanel.setEnabled(pEnabled);
        }

        @Override
        public void setVisible(final boolean pVisible) {
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
            theViewerRate.setFocus();
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

