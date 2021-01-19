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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Currency;

import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldPriceCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldCalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldDecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldStringCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseSpotPricesSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jmoneywise.lethe.views.SpotSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.views.SpotSecurityPrice.SpotSecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.views.YQLDownloader;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;

/**
 * SpotPrices panel.
 */
public class SpotPricesTable
        extends PrometheusDataTable<SpotSecurityPrice, MoneyWiseDataType> {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.PRICES_DATAENTRY.getValue();

    /**
     * The Asset column name.
     */
    private static final String TITLE_ASSET = SpotSecurityPrice.FIELD_SECURITY.getName();

    /**
     * The Price column name.
     */
    private static final String TITLE_PRICE = SpotSecurityPrice.FIELD_PRICE.getName();

    /**
     * The previous price column name.
     */
    private static final String TITLE_PREVPRICE = SpotSecurityPrice.FIELD_PREVPRICE.getName();

    /**
     * The previous date column name.
     */
    private static final String TITLE_PREVDATE = SpotSecurityPrice.FIELD_PREVDATE.getName();

    /**
     * Action Column Title.
     */
    private static final String TITLE_ACTION = MoneyWiseUIResource.COLUMN_ACTION.getValue();

    /**
     * The data view.
     */
    private final MoneyWiseView theView;

    /**
     * The field manager.
     */
    private final MetisSwingFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The update entry.
     */
    private final UpdateEntry<SpotSecurityPrice, MoneyWiseDataType> theUpdateEntry;

    /**
     * Table Model.
     */
    private final SpotViewModel theModel;

    /**
     * The panel.
     */
    private final TethysSwingEnablePanel thePanel;

    /**
     * The column model.
     */
    private final SpotViewColumnModel theColumns;

    /**
     * The SpotPrices selection panel.
     */
    private final MoneyWiseSpotPricesSelect theSelect;

    /**
     * The action buttons.
     */
    private final PrometheusActionButtons theActionButtons;

    /**
     * The viewer entry.
     */
    private final MetisViewerEntry theViewerPrice;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

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
     * @param pView the data view
     */
    public SpotPricesTable(final MoneyWiseView pView) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Record the passed details */
        theView = pView;
        theFieldMgr = ((PrometheusSwingToolkit) theView.getToolkit()).getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entry */
        theUpdateSet = new UpdateSet<>(theView, MoneyWiseDataType.class);
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.SECURITYPRICE);
        setUpdateSet(theUpdateSet);

        /* Create the top level viewer entry for this view */
        final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
        final MetisViewerManager myViewer = theView.getViewerManager();
        theViewerPrice = myViewer.newEntry(mySection, NLS_DATAENTRY);
        theViewerPrice.setTreeObject(theUpdateSet);

        /* Create the model and declare it to our superclass */
        theModel = new SpotViewModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new SpotViewColumnModel();
        final JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the sub panels */
        final TethysSwingGuiFactory myFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        theSelect = new MoneyWiseSpotPricesSelect(myFactory, theView);
        theActionButtons = new PrometheusActionButtons(myFactory, theUpdateSet);

        /* Create the error panel for this view */
        theError = theView.getToolkit().getToolkit().newErrorPanel(theViewerPrice);

        /* Create the header panel */
        final TethysSwingEnablePanel myHeader = new TethysSwingEnablePanel();
        myHeader.setLayout(new BorderLayout());
        myHeader.add(TethysSwingNode.getComponent(theSelect), BorderLayout.CENTER);
        myHeader.add(TethysSwingNode.getComponent(theError), BorderLayout.PAGE_START);
        myHeader.add(TethysSwingNode.getComponent(theActionButtons), BorderLayout.LINE_END);

        /* Create the panel */
        thePanel = new TethysSwingEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BorderLayout());
        thePanel.add(myHeader, BorderLayout.PAGE_START);
        thePanel.add(((TethysSwingNode) super.getNode()).getNode(), BorderLayout.CENTER);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the listeners */
        theUpdateSet.getEventRegistrar().addEventListener(e -> theModel.fireNewDataEvents());
        theView.getEventRegistrar().addEventListener(e -> refreshData());
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.SELECTIONCHANGED, e -> handleNewSelection());
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.DOWNLOAD, e -> downloadPrices());
    }

    @Override
    public TethysSwingNode getNode() {
        return thePanel.getNode();
    }

    @Override
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Determine focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Focus on the Data entry */
        theViewerPrice.setFocus();
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("SpotPrices");

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
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Determine whether we have updates */
        final boolean hasUpdates = hasUpdates();

        /* Update the table buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates);
        theSelect.setEnabled(!hasUpdates);

        /* Notify listeners */
        fireStateChanged();
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
            thePrices = new SpotSecurityList(theView, pPortfolio, pDate);

            /* Update Next/Previous values */
            theSelect.setAdjacent(thePrices.getPrev(), thePrices.getNext());

            /* else invalid selection */
        } else {
            /* Set no selection */
            thePrices = null;
            theSelect.setAdjacent(null, null);
        }

        /* Update other details */
        setList(thePrices);
        theUpdateEntry.setDataList(thePrices);
        theActionButtons.setEnabled(true);
        theSelect.setEnabled(true);
        fireStateChanged();
    }

    @Override
    protected boolean disableShowAll(final SpotSecurityPrice pRow) {
        return true;
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
                incrementVersion();

                /* Update components to reflect changes */
                theModel.fireNewDataEvents();
                notifyChanges();
            }
        } catch (OceanusException e) {
            /* Show the error */
            setError(e);
        }
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        final boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelect.setVisible(!isError);

        /* Lock scroll area */
        ((TethysSwingNode) super.getNode()).getNode().setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    /**
     * handle Action Buttons.
     * @param pEvent the event
     */
    private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
        /* Cancel editing */
        cancelEditing();

        /* Perform the command */
        theUpdateSet.processCommand(pEvent.getEventId(), theError);

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * handle new selection.
     */
    private void handleNewSelection() {
        /* Set the deleted option */
        setShowAll(theSelect.getShowClosed());

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
     * SpotView table model.
     */
    private final class SpotViewModel
            extends PrometheusDataTableModel<SpotSecurityPrice, MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2520681944053000625L;

        /**
         * Constructor.
         */
        private SpotViewModel() {
            /* call constructor */
            super(SpotPricesTable.this);
        }

        @Override
        public SpotSecurityPrice getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return thePrices.get(pRowIndex);
        }

        @Override
        public boolean includeRow(final SpotSecurityPrice pRow) {
            /* Return visibility of row */
            return showAll() || !pRow.isDisabled();
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                        ? 0
                                        : theColumns.getDeclaredCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (thePrices == null)
                                       ? 0
                                       : thePrices.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public MetisLetheField getFieldForCell(final SpotSecurityPrice pItem,
                                               final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final SpotSecurityPrice pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Object getItemValue(final SpotSecurityPrice pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final SpotSecurityPrice pItem,
                                 final int pColIndex,
                                 final Object pValue) throws OceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }
    }

    /**
     * Column Model class.
     */
    private final class SpotViewColumnModel
            extends PrometheusDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5102715203937500181L;

        /**
         * The Asset column id.
         */
        private static final int COLUMN_ASSET = 0;

        /**
         * The Price column id.
         */
        private static final int COLUMN_PRICE = 1;

        /**
         * The Previous price column id.
         */
        private static final int COLUMN_PREVPRICE = 2;

        /**
         * The Previous Date column id.
         */
        private static final int COLUMN_PREVDATE = 3;

        /**
         * The Action column id.
         */
        private static final int COLUMN_ACTION = 4;

        /**
         * Constructor.
         */
        private SpotViewColumnModel() {
            /* call constructor */
            super(SpotPricesTable.this);

            /* Create the relevant formatters/editors */
            final MetisFieldCalendarCellRenderer myDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            final MetisFieldDecimalCellRenderer myDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            final MetisFieldPriceCellEditor myPriceEditor = theFieldMgr.allocatePriceCellEditor();
            final MetisFieldStringCellRenderer myStringRenderer = theFieldMgr.allocateStringCellRenderer();
            final MetisFieldIconButtonCellEditor<MetisAction> myActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(MetisAction.class);
            final MetisFieldIconButtonCellRenderer<MetisAction> myActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(MetisAction.class);

            /* Configure the iconButton */
            final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
            myActionIconRenderer.setIconMapSet(r -> myActionMapSet);
            myActionIconEditor.setIconMapSet(r -> myActionMapSet);

            /* Create the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_ASSET, WIDTH_NAME, myStringRenderer));
            declareColumn(new PrometheusDataTableColumn(COLUMN_PRICE, WIDTH_PRICE, myDecimalRenderer, myPriceEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_PREVPRICE, WIDTH_PRICE, myDecimalRenderer));
            declareColumn(new PrometheusDataTableColumn(COLUMN_PREVDATE, WIDTH_DATE, myDateRenderer));
            declareColumn(new PrometheusDataTableColumn(COLUMN_ACTION, WIDTH_ICON, myActionIconRenderer, myActionIconEditor));

            /* Add listeners */
            myPriceEditor.setDeemedCurrency(this::determineCurrency);
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        public String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_ASSET:
                    return TITLE_ASSET;
                case COLUMN_PRICE:
                    return TITLE_PRICE;
                case COLUMN_PREVPRICE:
                    return TITLE_PREVPRICE;
                case COLUMN_PREVDATE:
                    return TITLE_PREVDATE;
                case COLUMN_ACTION:
                    return TITLE_ACTION;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the price column.
         * @param pItem spot price
         * @param pColIndex column index
         * @return the value
         */
        public Object getItemValue(final SpotSecurityPrice pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_ASSET:
                    return pItem.getSecurity();
                case COLUMN_PRICE:
                    return pItem.getPrice();
                case COLUMN_PREVPRICE:
                    return pItem.getPrevPrice();
                case COLUMN_PREVDATE:
                    return pItem.getPrevDate();
                case COLUMN_ACTION:
                    return pItem.getPrice() != null && !pItem.isDisabled()
                                                                           ? MetisAction.DELETE
                                                                           : MetisAction.DO;
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws OceanusException on error
         */
        public void setItemValue(final SpotSecurityPrice pItem,
                                 final int pColIndex,
                                 final Object pValue) throws OceanusException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_PRICE:
                    pItem.setPrice((TethysPrice) pValue);
                    break;
                case COLUMN_ACTION:
                    pItem.setPrice(null);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        public boolean isCellEditable(final SpotSecurityPrice pItem,
                                      final int pColIndex) {
            /* switch on column */
            switch (pColIndex) {
                case COLUMN_PRICE:
                    return !pItem.isDisabled();
                case COLUMN_ACTION:
                    return pItem.getPrice() != null && !pItem.isDisabled();
                case COLUMN_ASSET:
                case COLUMN_PREVPRICE:
                case COLUMN_PREVDATE:
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        public MetisLetheField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_ASSET:
                    return SecurityPrice.FIELD_SECURITY;
                case COLUMN_PRICE:
                    return SecurityPrice.FIELD_PRICE;
                case COLUMN_PREVPRICE:
                    return SpotSecurityPrice.FIELD_PREVPRICE;
                case COLUMN_PREVDATE:
                    return SpotSecurityPrice.FIELD_PREVDATE;
                default:
                    return null;
            }
        }

        /**
         * determine currency.
         * @param pRowIndex the row index
         * @return the assumed currency
         */
        private Currency determineCurrency(final Integer pRowIndex) {
            /* Update to allow correct currency */
            final SpotSecurityPrice myPrice = theModel.getItemAtIndex(pRowIndex);
            final Security mySecurity = myPrice.getSecurity();
            final AssetCurrency myCurrency = mySecurity.getAssetCurrency();
            return myCurrency.getCurrency();
        }
    }
}
