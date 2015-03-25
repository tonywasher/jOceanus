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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.PriceCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JDataProfile;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.SpotPricesSelect;
import net.sourceforge.joceanus.jmoneywise.views.SpotSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.views.SpotSecurityPrice.SpotSecurityList;
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
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnablePanel;

/**
 * SpotPrices panel.
 * @author Tony Washer
 */
public class SpotPricesTable
        extends JDataTable<SpotSecurityPrice, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5826211763056873599L;

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
    private final transient UpdateEntry<SpotSecurityPrice, MoneyWiseDataType> theUpdateEntry;

    /**
     * The account price list.
     */
    private transient SpotSecurityList thePrices = null;

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
     * The Portfolio.
     */
    private transient Portfolio thePortfolio = null;

    /**
     * The SpotPrices selection panel.
     */
    private final SpotPricesSelect theSelect;

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
    public SpotPricesTable(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entry */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView, MoneyWiseDataType.class);
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.SECURITYPRICE);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
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
        theSelect = new SpotPricesSelect(theView);
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
            setSelection(theSelect.getPortfolio(), theSelect.getDate());

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
     * Set Selection to the specified portfolio and date.
     * @param pPortfolio the portfolio
     * @param pDate the Date for the extract
     * @throws JOceanusException on error
     */
    public void setSelection(final Portfolio pPortfolio,
                             final JDateDay pDate) throws JOceanusException {
        /* Record selection */
        theDate = pDate;
        thePortfolio = pPortfolio;

        /* If selection is valid */
        if ((theDate != null) && (thePortfolio != null)) {
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
     * SpotView listener class.
     */
    private final class SpotViewListener
            implements ActionListener, ChangeListener {
        /**
         * Constructor.
         */
        private SpotViewListener() {
            /* Listen to correct events */
            theUpdateSet.addChangeListener(this);
            theActionButtons.addActionListener(this);
            theError.addChangeListener(this);
            theSelect.addChangeListener(this);
            theView.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            Object o = evt.getSource();

            /* If this is the selection panel */
            if (theSelect.equals(o)) {
                /* Set the deleted option */
                setShowAll(theSelect.getShowClosed());

                /* Access selection */
                Portfolio myPortfolio = theSelect.getPortfolio();
                JDateDay myDate = theSelect.getDate();

                /* If the selection differs */
                if (!Difference.isEqual(theDate, myDate) || !Difference.isEqual(thePortfolio, myPortfolio)) {
                    /* Protect against exceptions */
                    try {
                        /* Set selection */
                        setSelection(myPortfolio, myDate);

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

                /* If this is the data view */
            } else if (theView.equals(o)) {
                /* Refresh Data */
                refreshData();

                /* If we are performing a rewind */
            } else if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();

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
            extends JDataTableModel<SpotSecurityPrice, MoneyWiseDataType> {
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
        public JDataField getFieldForCell(final SpotSecurityPrice pItem,
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
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * Price Editor.
         */
        private final PriceCellEditor thePriceEditor;

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
            super(SpotPricesTable.this);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            thePriceEditor = theFieldMgr.allocatePriceCellEditor();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theActionIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theActionIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_ASSET, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_PRICE, WIDTH_PRICE, theDecimalRenderer, thePriceEditor));
            declareColumn(new JDataTableColumn(COLUMN_PREVPRICE, WIDTH_PRICE, theDecimalRenderer));
            declareColumn(new JDataTableColumn(COLUMN_PREVDATE, WIDTH_DATE, theDateRenderer));
            declareColumn(new JDataTableColumn(COLUMN_ACTION, WIDTH_ICON, theActionIconRenderer, theActionIconEditor));

            /* Add listeners */
            new EditorListener();
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
        public void setItemValue(final SpotSecurityPrice pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_PRICE:
                    pItem.setPrice((JPrice) pValue);
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
        public JDataField getFieldForCell(final int pColIndex) {
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
         * EditorListener.
         */
        private final class EditorListener
                implements ChangeListener {
            /**
             * Constructor.
             */
            private EditorListener() {
                thePriceEditor.addChangeListener(this);
            }

            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                /* Access details */
                Point myCell = thePriceEditor.getPoint();

                /* Update to allow correct currency */
                SpotSecurityPrice myPrice = thePrices.get(myCell.y);
                Security mySecurity = myPrice.getSecurity();
                AssetCurrency myCurrency = mySecurity.getAssetCurrency();
                thePriceEditor.setAssumedCurrency(myCurrency.getCurrency());
            }
        }
    }
}
