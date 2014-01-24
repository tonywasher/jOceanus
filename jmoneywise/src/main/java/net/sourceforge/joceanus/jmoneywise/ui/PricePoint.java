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
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.PriceCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.ui.controls.SpotSelect;
import net.sourceforge.joceanus.jmoneywise.views.SpotPrices;
import net.sourceforge.joceanus.jmoneywise.views.SpotPrices.SpotList;
import net.sourceforge.joceanus.jmoneywise.views.SpotPrices.SpotPrice;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableMouse;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

/**
 * SpotPrices panel.
 * @author Tony Washer
 */
public class PricePoint
        extends JDataTable<SpotPrice, MoneyWiseList> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5826211763056873599L;

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
    private final transient UpdateSet<MoneyWiseList> theUpdateSet;

    /**
     * The update entry.
     */
    private final transient UpdateEntry<SpotPrice, MoneyWiseList> theUpdateEntry;

    /**
     * The Spot prices.
     */
    private transient SpotPrices theSnapshot = null;

    /**
     * The account price list.
     */
    private transient SpotList thePrices = null;

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
    private transient Account thePortfolio = null;

    /**
     * The Spot selection panel.
     */
    private final SpotSelect theSelect;

    /**
     * The save buttons.
     */
    private final SaveButtons theSaveButtons;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataPrice;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PricePoint.class.getName());

    /**
     * The Asset column name.
     */
    private static final String TITLE_ASSET = NLS_BUNDLE.getString("TitleAsset");

    /**
     * The Price column name.
     */
    private static final String TITLE_PRICE = NLS_BUNDLE.getString("TitlePrice");

    /**
     * The previous price column name.
     */
    private static final String TITLE_PREVPRICE = NLS_BUNDLE.getString("TitlePrevPrice");

    /**
     * The previous date column name.
     */
    private static final String TITLE_PREVDATE = NLS_BUNDLE.getString("TitlePrevDate");

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
     * The column width.
     */
    private static final int WIDTH_COLUMN = 130;

    /**
     * The Panel height.
     */
    private static final int HEIGHT_PANEL = 200;

    /**
     * The Panel width.
     */
    private static final int WIDTH_PANEL = 900;

    /**
     * Constructor.
     * @param pView the data view
     */
    public PricePoint(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entry */
        theUpdateSet = new UpdateSet<MoneyWiseList>(theView);
        theUpdateEntry = theUpdateSet.registerClass(SpotPrice.class);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_EDIT);
        theDataPrice = myDataMgr.new JDataEntry(SpotPrices.class.getSimpleName());
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
        setAutoResizeMode(AUTO_RESIZE_OFF);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Add the mouse listener */
        SpotViewMouse myMouse = new SpotViewMouse(this);
        addMouseListener(myMouse);

        /* Create the sub panels */
        theSelect = new SpotSelect(theView);
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataPrice);

        /* Create the listener */
        SpotViewListener myListener = new SpotViewListener();
        theSelect.addChangeListener(myListener);
        theError.addChangeListener(myListener);
        theSaveButtons.addActionListener(myListener);
        theUpdateSet.addActionListener(myListener);
        theView.addChangeListener(myListener);

        /* Create the panel */
        thePanel = new JEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theSelect);
        thePanel.add(theError);
        thePanel.add(getScrollPane());
        thePanel.add(theSaveButtons);
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
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Update the table buttons */
        theSaveButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Notify listeners */
        fireStateChanged();
    }

    /**
     * Set Selection to the specified portfolio and date.
     * @param pPortfolio the portfolio
     * @param pDate the Date for the extract
     * @throws JOceanusException on error
     */
    public void setSelection(final Account pPortfolio,
                             final JDateDay pDate) throws JOceanusException {
        /* Record selection */
        theDate = pDate;
        thePortfolio = pPortfolio;

        /* If selection is valid */
        if ((theDate != null) && (thePortfolio != null)) {
            /* Create the new list */
            theSnapshot = new SpotPrices(theView, pPortfolio, pDate);
            thePrices = theSnapshot.getPrices();

            /* Update Next/Previous values */
            theSelect.setAdjacent(theSnapshot.getPrev(), theSnapshot.getNext());

            /* else invalid selection */
        } else {
            /* Set no selection */
            theSnapshot = null;
            thePrices = null;
            theSelect.setAdjacent(null, null);
        }

        /* Update other details */
        setList(thePrices);
        theUpdateEntry.setDataList(thePrices);
        theSaveButtons.setEnabled(true);
        theSelect.setEnabled(true);
        fireStateChanged();
    }

    /**
     * Check whether insert is allowed for this table.
     * @return insert allowed (true/false)
     */
    @Override
    protected boolean insertAllowed() {
        return false;
    }

    /**
     * Check whether a row is deletable.
     * @param pRow the row
     * @return is the row deletable
     */
    @Override
    protected boolean isRowDeletable(final SpotPrice pRow) {
        /* Switch on the Data State */
        switch (pRow.getState()) {
            case CLEAN:
                DataItem<?> myBase = pRow.getBase();
                if ((myBase != null) && (myBase.isDeleted())) {
                    return false;
                }
                return true;
            case NEW:
            case CHANGED:
            case RECOVERED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check whether a row is recoverable.
     * @param pRow the row
     * @return is the row recoverable
     */
    @Override
    protected boolean isRowRecoverable(final SpotPrice pRow) {
        /* Switch on the Data State */
        switch (pRow.getState()) {
        /* Recoverable if there are changes */
            case DELNEW:
                return pRow.hasHistory();
                /* Recoverable if date is the same */
            case DELETED:
                return !pRow.getDate().equals(theDate);
                /* DELCHG must be recoverable */
            case DELCHG:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check whether we can duplicate a row.
     * @param pRow the row
     * @return false
     */
    @Override
    protected boolean isRowDuplicatable(final SpotPrice pRow) {
        return false;
    }

    /**
     * Check whether we duplicate a row.
     * @param pRow the row
     * @return true
     */
    @Override
    protected boolean disableShowAll(final SpotPrice pRow) {
        return true;
    }

    /**
     * Extract listener class.
     */
    private final class SpotViewListener
            implements ActionListener, ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent evt) {
            Object o = evt.getSource();

            /* If this is the selection panel */
            if (theSelect.equals(o)) {
                /* Set the deleted option */
                setShowAll(theSelect.getShowClosed());

                /* Access selection */
                Account myPortfolio = theSelect.getPortfolio();
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

                /* If this is the error panel */
            } else if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelect.setVisible(!isError);

                /* Lock scroll area */
                getScrollPane().setEnabled(!isError);

                /* Lock Save Buttons */
                theSaveButtons.setEnabled(!isError);
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the save buttons */
            if (theSaveButtons.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Perform the command */
                theUpdateSet.processCommand(evt.getActionCommand(), theError);

                /* Notify listeners of changes */
                notifyChanges();

                /* If we are performing a rewind */
            } else if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }
        }
    }

    /**
     * SpotView table model.
     */
    public final class SpotViewModel
            extends JDataTableModel<SpotPrice, MoneyWiseList> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2520681944053000625L;

        /**
         * Constructor.
         */
        private SpotViewModel() {
            /* call constructor */
            super(PricePoint.this);
        }

        @Override
        public SpotPrice getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return thePrices.get(pRowIndex);
        }

        @Override
        public boolean includeRow(final SpotPrice pRow) {
            /* Return visibility of row */
            return showAll() || !pRow.getSecurity().isClosed();
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                       ? 0
                                       : theColumns.getColumnCount();
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
            switch (pColIndex) {
                case COLUMN_ASSET:
                    return TITLE_ASSET;
                case COLUMN_PRICE:
                    return TITLE_PRICE;
                case COLUMN_PREVPRICE:
                    return TITLE_PREVPRICE;
                case COLUMN_PREVDATE:
                    return TITLE_PREVDATE;
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_ASSET:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public JDataField getFieldForCell(final SpotPrice pSpot,
                                          final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_ASSET:
                    return SecurityPrice.FIELD_SECURITY;
                case COLUMN_PRICE:
                    return SecurityPrice.FIELD_PRICE;
                case COLUMN_PREVPRICE:
                    return SpotPrice.FIELD_PREVPRICE;
                case COLUMN_PREVDATE:
                    return SpotPrice.FIELD_PREVDATE;
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(final SpotPrice pSpot,
                                      final int pColIndex) {
            /* switch on column */
            switch (pColIndex) {
                case COLUMN_ASSET:
                case COLUMN_PREVPRICE:
                case COLUMN_PREVDATE:
                    return false;
                case COLUMN_PRICE:
                default:
                    return true;
            }
        }

        @Override
        public Object getItemValue(final SpotPrice pSpot,
                                   final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_ASSET:
                    return pSpot.getSecurity().getName();
                case COLUMN_PRICE:
                    return pSpot.getPrice();
                case COLUMN_PREVPRICE:
                    return pSpot.getPrevPrice();
                case COLUMN_PREVDATE:
                    return pSpot.getPrevDate();
                default:
                    return null;
            }
        }

        @Override
        public void setItemValue(final SpotPrice pSpot,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_PRICE:
                    pSpot.setPrice((JPrice) pValue);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * SpotView mouse listener.
     */
    private static final class SpotViewMouse
            extends JDataTableMouse<SpotPrice, MoneyWiseList> {
        /**
         * Constructor.
         * @param pTable the table
         */
        private SpotViewMouse(final PricePoint pTable) {
            /* Call super-constructor */
            super(pTable);
        }
    }

    /**
     * Column Model class.
     */
    private final class SpotViewColumnModel
            extends JDataTableColumnModel<MoneyWiseList> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5102715203937500181L;

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
         * Constructor.
         */
        private SpotViewColumnModel() {
            /* call constructor */
            super(PricePoint.this);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            thePriceEditor = theFieldMgr.allocatePriceCellEditor();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_ASSET, WIDTH_COLUMN, theStringRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_PRICE, WIDTH_COLUMN, theDecimalRenderer, thePriceEditor));
            addColumn(new JDataTableColumn(COLUMN_PREVPRICE, WIDTH_COLUMN, theDecimalRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_PREVDATE, WIDTH_COLUMN, theDateRenderer, null));
        }
    }
}
