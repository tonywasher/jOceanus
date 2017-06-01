/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.RatioCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.lethe.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseSpotRatesSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.views.SpotExchangeRate;
import net.sourceforge.joceanus.jmoneywise.views.SpotExchangeRate.SpotExchangeList;
import net.sourceforge.joceanus.jmoneywise.views.YQLDownloader;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * SpotRates panel.
 * @author Tony Washer
 */
public class SpotRatesTable
        extends JDataTable<SpotExchangeRate, MoneyWiseDataType> {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.RATES_DATAENTRY.getValue();

    /**
     * The Currency column name.
     */
    private static final String TITLE_CURRENCY = SpotExchangeRate.FIELD_TO.getName();

    /**
     * The Symbol column name.
     */
    private static final String TITLE_SYMBOL = MoneyWiseUIResource.SPOTRATE_COLUMN_SYMBOL.getValue();

    /**
     * The Price column name.
     */
    private static final String TITLE_RATE = SpotExchangeRate.FIELD_RATE.getName();

    /**
     * The previous price column name.
     */
    private static final String TITLE_PREVRATE = SpotExchangeRate.FIELD_PREVRATE.getName();

    /**
     * The previous date column name.
     */
    private static final String TITLE_PREVDATE = SpotExchangeRate.FIELD_PREVDATE.getName();

    /**
     * Action Column Title.
     */
    private static final String TITLE_ACTION = MoneyWiseUIResource.COLUMN_ACTION.getValue();

    /**
     * The data view.
     */
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The update entry.
     */
    private final UpdateEntry<SpotExchangeRate, MoneyWiseDataType> theUpdateEntry;

    /**
     * Table Model.
     */
    private final SpotViewModel theModel;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The column model.
     */
    private final SpotViewColumnModel theColumns;

    /**
     * The SpotRates selection panel.
     */
    private final MoneyWiseSpotRatesSelect<JComponent, Icon> theSelect;

    /**
     * The action buttons.
     */
    private final PrometheusActionButtons<JComponent, Icon> theActionButtons;

    /**
     * The viewer entry.
     */
    private final MetisViewerEntry theViewerPrice;

    /**
     * The error panel.
     */
    private final MetisErrorPanel<JComponent, Icon> theError;

    /**
     * The exchange rates list.
     */
    private SpotExchangeList<JComponent, Icon> theRates;

    /**
     * The selected date.
     */
    private TethysDate theDate;

    /**
     * Constructor.
     * @param pView the data view
     */
    public SpotRatesTable(final SwingView pView) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Record the passed details */
        theView = pView;
        theFieldMgr = pView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entry */
        theUpdateSet = new UpdateSet<>(theView, MoneyWiseDataType.class);
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.EXCHANGERATE);
        setUpdateSet(theUpdateSet);

        /* Create the top level viewer entry for this view */
        MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
        MetisViewerManager myViewer = theView.getViewerManager();
        theViewerPrice = myViewer.newEntry(mySection, NLS_DATAENTRY);
        theViewerPrice.setTreeObject(theUpdateSet);

        /* Create the model and declare it to our superclass */
        theModel = new SpotViewModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new SpotViewColumnModel();
        JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the sub panels */
        TethysSwingGuiFactory myFactory = pView.getGuiFactory();
        theSelect = new MoneyWiseSpotRatesSelect<>(myFactory, theView);
        theActionButtons = new PrometheusActionButtons<>(myFactory, theUpdateSet);

        /* Create the error panel for this view */
        theError = theView.getToolkit().newErrorPanel(theViewerPrice);

        /* Create the header panel */
        JPanel myHeader = new TethysSwingEnablePanel();
        myHeader.setLayout(new BorderLayout());
        myHeader.add(theSelect.getNode(), BorderLayout.CENTER);
        myHeader.add(theError.getNode(), BorderLayout.PAGE_START);
        myHeader.add(theActionButtons.getNode(), BorderLayout.LINE_END);

        /* Create the panel */
        thePanel = new TethysSwingEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BorderLayout());
        thePanel.add(myHeader, BorderLayout.PAGE_START);
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> theModel.fireNewDataEvents());
        theView.getEventRegistrar().addEventListener(e -> refreshData());
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.SELECTIONCHANGED, e -> handleNewSelection());
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.DOWNLOAD, e -> downloadRates());
    }

    @Override
    public JComponent getNode() {
        return thePanel;
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

        /* Protect against exceptions */
        try {
            /* Refresh the data */
            theSelect.refreshData();

            /* Access the selection details */
            setSelection(theSelect.getDate());

            /* Create SavePoint */
            theSelect.createSavePoint();
        } catch (OceanusException e) {
            /* Show the error */
            theView.addError(e);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();

        /* Update the table buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates);
        theSelect.setEnabled(!hasUpdates);

        /* Notify listeners */
        fireStateChanged();
    }

    /**
     * Set Selection to the date.
     * @param pDate the Date for the extract
     * @throws OceanusException on error
     */
    public void setSelection(final TethysDate pDate) throws OceanusException {
        /* Record selection */
        theDate = pDate;

        /* If selection is valid */
        if (theDate != null) {
            /* Create the new list */
            theRates = new SpotExchangeList<>(theView, pDate);

            /* Update Next/Previous values */
            theSelect.setAdjacent(theRates.getPrev(), theRates.getNext());

            /* else invalid selection */
        } else {
            /* Set no selection */
            theRates = null;
            theSelect.setAdjacent(null, null);
        }

        /* Update other details */
        setList(theRates);
        theUpdateEntry.setDataList(theRates);
        theActionButtons.setEnabled(true);
        theSelect.setEnabled(true);
        fireStateChanged();
    }

    @Override
    protected boolean disableShowAll(final SpotExchangeRate pRow) {
        return true;
    }

    /**
     * Download rates.
     */
    private void downloadRates() {
        /* Cancel editing */
        cancelEditing();

        /* Protect against exceptions */
        try {
            /* Download Rates */
            if (YQLDownloader.downloadRates(theRates)) {
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
        boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelect.setVisible(!isError);

        /* Lock scroll area */
        super.getNode().setEnabled(!isError);

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
        /* Access selection */
        TethysDate myDate = theSelect.getDate();

        /* If the selection differs */
        if (!MetisDifference.isEqual(theDate, myDate)) {
            /* Protect against exceptions */
            try {
                /* Set selection */
                setSelection(myDate);

                /* Create SavePoint */
                theSelect.createSavePoint();

                /* Catch Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                OceanusException myError = new MoneyWiseDataException("Failed to change selection", e);

                /* Show the error */
                setError(myError);

                /* Restore SavePoint */
                theSelect.restoreSavePoint();
            }
        }
    }

    /**
     * SpotView table model.
     */
    public final class SpotViewModel
            extends JDataTableModel<SpotExchangeRate, MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 866525049069552200L;

        /**
         * Constructor.
         */
        private SpotViewModel() {
            /* call constructor */
            super(SpotRatesTable.this);
        }

        @Override
        public SpotExchangeRate getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theRates.get(pRowIndex);
        }

        @Override
        public boolean includeRow(final SpotExchangeRate pRow) {
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
            return (theRates == null)
                                      ? 0
                                      : theRates.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public MetisField getFieldForCell(final SpotExchangeRate pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final SpotExchangeRate pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Object getItemValue(final SpotExchangeRate pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final SpotExchangeRate pItem,
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
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 8982766908862220152L;

        /**
         * The Currency column id.
         */
        private static final int COLUMN_CURRENCY = 0;

        /**
         * The Symbol column id.
         */
        private static final int COLUMN_SYMBOL = 1;

        /**
         * The Price column id.
         */
        private static final int COLUMN_RATE = 2;

        /**
         * The Previous price column id.
         */
        private static final int COLUMN_PREVRATE = 3;

        /**
         * The Previous Date column id.
         */
        private static final int COLUMN_PREVDATE = 4;

        /**
         * The Action column id.
         */
        private static final int COLUMN_ACTION = 5;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * Ratio Editor.
         */
        private final RatioCellEditor theRatioEditor;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Action Icon Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theActionIconRenderer;

        /**
         * Action Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theActionIconEditor;

        /**
         * Constructor.
         */
        private SpotViewColumnModel() {
            /* call constructor */
            super(SpotRatesTable.this);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theRatioEditor = theFieldMgr.allocateRatioCellEditor();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theActionIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theActionIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_CURRENCY, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_SYMBOL, WIDTH_PRICE, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_RATE, WIDTH_PRICE, theDecimalRenderer, theRatioEditor));
            declareColumn(new JDataTableColumn(COLUMN_PREVRATE, WIDTH_PRICE, theDecimalRenderer));
            declareColumn(new JDataTableColumn(COLUMN_PREVDATE, WIDTH_DATE, theDateRenderer));
            declareColumn(new JDataTableColumn(COLUMN_ACTION, WIDTH_ICON, theActionIconRenderer, theActionIconEditor));
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        public String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_CURRENCY:
                    return TITLE_CURRENCY;
                case COLUMN_SYMBOL:
                    return TITLE_SYMBOL;
                case COLUMN_RATE:
                    return TITLE_RATE;
                case COLUMN_PREVRATE:
                    return TITLE_PREVRATE;
                case COLUMN_PREVDATE:
                    return TITLE_PREVDATE;
                case COLUMN_ACTION:
                    return TITLE_ACTION;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the rate column.
         * @param pItem spot rate
         * @param pColIndex column index
         * @return the value
         */
        public Object getItemValue(final SpotExchangeRate pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_CURRENCY:
                    return pItem.getToCurrency().getDesc();
                case COLUMN_SYMBOL:
                    return pItem.getToCurrency().getName();
                case COLUMN_RATE:
                    return pItem.getExchangeRate();
                case COLUMN_PREVRATE:
                    return pItem.getPrevRate();
                case COLUMN_PREVDATE:
                    return pItem.getPrevDate();
                case COLUMN_ACTION:
                    return pItem.getExchangeRate() != null && !pItem.isDisabled()
                                                                                  ? ActionType.DELETE
                                                                                  : ActionType.DO;
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
        public void setItemValue(final SpotExchangeRate pItem,
                                 final int pColIndex,
                                 final Object pValue) throws OceanusException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_RATE:
                    pItem.setExchangeRate((TethysRatio) pValue);
                    break;
                case COLUMN_ACTION:
                    pItem.setExchangeRate(null);
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
        public boolean isCellEditable(final SpotExchangeRate pItem,
                                      final int pColIndex) {
            /* switch on column */
            switch (pColIndex) {
                case COLUMN_RATE:
                    return !pItem.isDisabled();
                case COLUMN_ACTION:
                    return pItem.getExchangeRate() != null && !pItem.isDisabled();
                case COLUMN_CURRENCY:
                case COLUMN_SYMBOL:
                case COLUMN_PREVRATE:
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
        public MetisField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_CURRENCY:
                case COLUMN_SYMBOL:
                    return ExchangeRate.FIELD_TO;
                case COLUMN_RATE:
                    return ExchangeRate.FIELD_RATE;
                case COLUMN_PREVRATE:
                    return SpotExchangeRate.FIELD_PREVRATE;
                case COLUMN_PREVDATE:
                    return SpotExchangeRate.FIELD_PREVDATE;
                default:
                    return null;
            }
        }
    }
}
