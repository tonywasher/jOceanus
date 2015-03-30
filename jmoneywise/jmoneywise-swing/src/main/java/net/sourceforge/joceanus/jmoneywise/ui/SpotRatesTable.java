/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.RatioCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.SpotRatesSelect;
import net.sourceforge.joceanus.jmoneywise.views.SpotExchangeRate;
import net.sourceforge.joceanus.jmoneywise.views.SpotExchangeRate.SpotExchangeList;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnablePanel;

/**
 * SpotRates panel.
 * @author Tony Washer
 */
public class SpotRatesTable
        extends JDataTable<SpotExchangeRate, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -68651306151746587L;

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
    private static final String TITLE_ACTION = MoneyWiseUIControlResource.COLUMN_ACTION.getValue();

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The update entry.
     */
    private final transient UpdateEntry<SpotExchangeRate, MoneyWiseDataType> theUpdateEntry;

    /**
     * The exchange rates list.
     */
    private transient SpotExchangeList theRates = null;

    /**
     * Table Model.
     */
    private final SpotViewModel theModel;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The column model.
     */
    private final SpotViewColumnModel theColumns;

    /**
     * The selected date.
     */
    private transient JDateDay theDate = null;

    /**
     * The SpotRates selection panel.
     */
    private final SpotRatesSelect theSelect;

    /**
     * The action buttons.
     */
    private final ActionButtons theActionButtons;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataPrice;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * Constructor.
     * @param pView the data view
     */
    public SpotRatesTable(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entry */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView, MoneyWiseDataType.class);
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.EXCHANGERATE);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        ViewerManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataPrice = myDataMgr.new JDataEntry(NLS_DATAENTRY);
        theDataPrice.addAsChildOf(mySection);
        theDataPrice.setObject(theUpdateSet);

        /* Create the model and declare it to our superclass */
        theModel = new SpotViewModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new SpotViewColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the sub panels */
        theSelect = new SpotRatesSelect(theView);
        theActionButtons = new ActionButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataPrice);

        /* Create the header panel */
        JPanel myHeader = new JPanel();
        myHeader.setLayout(new BoxLayout(myHeader, BoxLayout.X_AXIS));
        myHeader.add(theSelect);
        myHeader.add(theError);
        myHeader.add(theActionButtons);

        /* Create the panel */
        thePanel = new JEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(myHeader);
        thePanel.add(getScrollPane());

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the listener */
        new SpotViewListener();
    }

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Determine focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Focus on the Data entry */
        theDataPrice.setFocus();
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("SpotPrices");

        /* Protect against exceptions */
        try {
            /* Refresh the data */
            theSelect.refreshData();

            /* Access the selection details */
            setSelection(theSelect.getDate());

            /* Create SavePoint */
            theSelect.createSavePoint();
        } catch (JOceanusException e) {
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
     * @throws JOceanusException on error
     */
    public void setSelection(final JDateDay pDate) throws JOceanusException {
        /* Record selection */
        theDate = pDate;

        /* If selection is valid */
        if (theDate != null) {
            /* Create the new list */
            theRates = new SpotExchangeList(theView, pDate);

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
     * SpotView listener class.
     */
    private final class SpotViewListener
            implements ActionListener, ChangeListener, JOceanusChangeEventListener {
        /**
         * UpdateSet Registration.
         */
        private final JOceanusChangeRegistration theUpdateSetReg;

        /**
         * View Registration.
         */
        private final JOceanusChangeRegistration theViewReg;

        /**
         * Constructor.
         */
        private SpotViewListener() {
            /* Register listeners */
            theUpdateSetReg = theUpdateSet.getEventRegistrar().addChangeListener(this);
            theViewReg = theView.getEventRegistrar().addChangeListener(this);

            /* Listen to swing events */
            theActionButtons.addActionListener(this);
            theError.addChangeListener(this);
            theSelect.addChangeListener(this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the data view */
            if (theViewReg.isRelevant(pEvent)) {
                /* Refresh Data */
                refreshData();

                /* If we are performing a rewind */
            } else if (theUpdateSetReg.isRelevant(pEvent)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            Object o = evt.getSource();

            /* If this is the selection panel */
            if (theSelect.equals(o)) {
                /* Access selection */
                JDateDay myDate = theSelect.getDate();

                /* If the selection differs */
                if (!Difference.isEqual(theDate, myDate)) {
                    /* Protect against exceptions */
                    try {
                        /* Set selection */
                        setSelection(myDate);

                        /* Create SavePoint */
                        theSelect.createSavePoint();

                        /* Catch Exceptions */
                    } catch (JOceanusException e) {
                        /* Build the error */
                        JOceanusException myError = new JMoneyWiseDataException("Failed to change selection", e);

                        /* Show the error */
                        setError(myError);

                        /* Restore SavePoint */
                        theSelect.restoreSavePoint();
                    }
                }

                /* If this is the error panel */
            } else if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelect.setVisible(!isError);

                /* Lock scroll area */
                getScrollPane().setEnabled(!isError);

                /* Lock Action Buttons */
                theActionButtons.setEnabled(!isError);
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the action buttons */
            if (theActionButtons.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Perform the command */
                theUpdateSet.processCommand(evt.getActionCommand(), theError);

                /* Adjust for changes */
                notifyChanges();
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
        public JDataField getFieldForCell(final SpotExchangeRate pItem,
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
                                 final Object pValue) throws JOceanusException {
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
         * @throws JOceanusException on error
         */
        public void setItemValue(final SpotExchangeRate pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_RATE:
                    pItem.setExchangeRate((JRatio) pValue);
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
        public JDataField getFieldForCell(final int pColIndex) {
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
