/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Price;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.data.AccountPrice.AccountPriceList;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.ui.controls.SpotSelect;
import uk.co.tolcroft.finance.views.SpotPrices;
import uk.co.tolcroft.finance.views.SpotPrices.SpotPrice;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.PriceEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.ui.Renderer.RendererFieldValue;
import uk.co.tolcroft.models.ui.Renderer.StringRenderer;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.models.views.UpdateSet;
import uk.co.tolcroft.models.views.UpdateSet.UpdateEntry;

/**
 * SpotPrices panel.
 * @author Tony Washer
 */
public class PricePoint extends DataTable<AccountPrice> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5826211763056873599L;

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The View list.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The update entry.
     */
    private final transient UpdateEntry theUpdateEntry;

    /**
     * The Spot prices.
     */
    private transient SpotPrices theSnapshot = null;

    /**
     * The account price list.
     */
    private transient AccountPriceList thePrices = null;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * Self reference.
     */
    private final PricePoint theTable = this;

    /**
     * The column model.
     */
    private final SpotViewColumnModel theColumns;

    /**
     * The selected date.
     */
    private transient DateDay theDate = null;

    /**
     * The Account type.
     */
    private transient AccountType theAccountType = null;

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
    public boolean hasHeader() {
        return false;
    }

    /**
     * The Asset column name.
     */
    private static final String TITLE_ASSET = "Asset";

    /**
     * The Price column name.
     */
    private static final String TITLE_PRICE = "Price";

    /**
     * The previous price column name.
     */
    private static final String TITLE_PREVPRICE = "Previous Price";

    /**
     * The previous date column name.
     */
    private static final String TITLE_PREVDATE = "Previous Date";

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

        /* Build the Update set and entry */
        theUpdateSet = new UpdateSet(theView);
        theUpdateEntry = theUpdateSet.registerClass(SpotPrice.class);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataPrice = myDataMgr.new JDataEntry("SpotPrices");
        theDataPrice.addAsChildOf(mySection);

        /* Create the model and declare it to our superclass */
        SpotViewModel myModel = new SpotViewModel();
        setModel(myModel);

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

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theSelect);
        thePanel.add(theError);
        thePanel.add(getScrollPane());
        thePanel.add(theSaveButtons);

        /* Create the layout for the panel */
        // myLayout = new GroupLayout(thePanel);
        // thePanel.setLayout(myLayout);

        /* Set the layout */
        // myLayout.setHorizontalGroup(myLayout
        // .createParallelGroup(GroupLayout.Alignment.LEADING)
        // .addGroup(myLayout.createSequentialGroup()
        // .addContainerGap()
        // .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
        // .addComponent(theError, GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        // .addComponent(theSelect, GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        // .addComponent(getScrollPane(),
        // GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE, WIDTH_PANEL,
        // Short.MAX_VALUE)
        // .addComponent(theSaveButtons,
        // GroupLayout.Alignment.LEADING,
        // GroupLayout.DEFAULT_SIZE,
        // GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        // .addContainerGap()));
        // myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        // .addGroup(GroupLayout.Alignment.TRAILING,
        // myLayout.createSequentialGroup().addComponent(theError).addComponent(theSelect)
        // .addComponent(getScrollPane()).addComponent(theSaveButtons)));
    }

    /**
     * Perform a command.
     * @param pCmd the command to perform
     */
    public void performCommand(final String pCmd) {
        /* Cancel any editing */
        cancelEditing();

        /* Process the command */
        theUpdateSet.processCommand(pCmd);

        /* Access any error */
        JDataException myError = theView.getError();

        /* Show the error */
        if (myError != null) {
            theError.setError(myError);
        }

        /* Notify listeners of changes */
        notifyChanges();
    }

    /**
     * Notify table that there has been a change in selection by an underlying control.
     * @param obj the underlying control that has changed selection
     */
    // @Override
    // public void notifySelection(final Object obj) {
    /* if this is a change from the date */
    // if (theSelect.equals(obj)) {
    /* Set the deleted option */
    // if (getList().getShowDeleted() != theSelect.getShowClosed()) {
    // setShowDeleted(theSelect.getShowClosed());
    // }

    /* Access selection */
    // AccountType myType = theSelect.getAccountType();
    // DateDay myDate = theSelect.getDate();

    /* If the selection differs */
    // if (((!Difference.isEqual(theDate, myDate))) || (!Difference.isEqual(theAccountType, myType))) {
    /* Protect against exceptions */
    // try {
    /* Set selection */
    // setSelection(myType, myDate);

    /* Create SavePoint */
    // theSelect.createSavePoint();

    /* Catch Exceptions */
    // } catch (JDataException e) {
    /* Build the error */
    // JDataException myError = new JDataException(ExceptionClass.DATA,
    // "Failed to change selection", e);

    /* Show the error */
    // theError.setError(myError);

    /* Restore SavePoint */
    // theSelect.restoreSavePoint();
    // }
    // }
    // }
    // }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JDataException on error
     */
    public void refreshData() throws JDataException {
        /* Refresh the data */
        theSelect.refreshData();

        /* Access the selection details */
        setSelection(theSelect.getAccountType(), theSelect.getDate());

        /* Create SavePoint */
        theSelect.createSavePoint();
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (thePrices != null) {
            thePrices.findEditState();
        }

        /* Update the table buttons */
        theSaveButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Notify listeners */
        fireStateChanged();
    }

    /**
     * Set Selection to the specified account type and date.
     * @param pType the account type
     * @param pDate the Date for the extract
     * @throws JDataException on error
     */
    public void setSelection(final AccountType pType,
                             final DateDay pDate) throws JDataException {
        /* Record selection */
        theDate = pDate;
        theAccountType = pType;

        /* If selection is valid */
        if ((theDate != null) && (theAccountType != null)) {
            /* Create the new list */
            theSnapshot = new SpotPrices(theView, pType, pDate);
            thePrices = theSnapshot.getPrices();

            /* Update Next/Prev values */
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
    protected boolean isRowDeletable(final SpotPrice pRow) {
        /* Switch on the Data State */
        switch (pRow.getState()) {
            case CLEAN:
                if (pRow.getBase().isDeleted()) {
                    return false;
                }
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
    protected boolean isRowRecoverable(final SpotPrice pRow) {
        /* Switch on the Data State */
        switch (pRow.getState()) {
        /* Recoverable if there are changes */
            case DELNEW:
                return (pRow.hasHistory());
                /* Recoverable if date is the same */
            case DELETED:
                return (!pRow.getDate().equals(theDate));
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
    protected boolean isRowDuplicatable(final SpotPrice pRow) {
        return false;
    }

    /**
     * Check whether we should hide deleted rows.
     * @return false
     */
    @Override
    protected boolean hideDeletedRows() {
        return false;
    }

    /**
     * Check whether we duplicate a row.
     * @param pRow the row
     * @return true
     */
    protected boolean disableShowDeleted(final SpotPrice pRow) {
        return true;
    }

    /**
     * Extract listener class.
     */
    private final class SpotViewListener implements ActionListener, ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent e) {
            /* If this is the error panel */
            if (theError.equals(e.getSource())) {
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
        public void actionPerformed(final ActionEvent e) {
            /* If this event relates to the save buttons */
            if (theSaveButtons.equals(e.getSource())) {
                /* Perform the save command */
                performCommand(e.getActionCommand());
            }
        }
    }

    /**
     * SpotView table model.
     */
    public final class SpotViewModel extends DataTableModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2520681944053000625L;

        /**
         * Constructor.
         */
        private SpotViewModel() {
            /* call constructor */
            super(theTable);
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null) ? 0 : theColumns.getColumnCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (thePrices == null) ? 0 : thePrices.size();
        }

        /**
         * Get the name of the column.
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(final int col) {
            switch (col) {
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

        /**
         * Get the object class of the column.
         * @param col the column
         * @return the class of the objects associated with the column
         */
        @Override
        public Class<?> getColumnClass(final int col) {
            switch (col) {
                case COLUMN_ASSET:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        /**
         * Obtain the Field id associated with the row.
         * @param pRow the row
         * @param pCol the column
         * @return the field id
         */
        @Override
        public JDataField getFieldForCell(final int pRow,
                                          final int pCol) {
            /* Switch on column */
            switch (pCol) {
                case COLUMN_ASSET:
                    return AccountPrice.FIELD_ACCOUNT;
                case COLUMN_PRICE:
                    return AccountPrice.FIELD_PRICE;
                default:
                    return null;
            }
        }

        /**
         * Is the cell at (row, col) editable?
         * @param row the row
         * @param col the column
         * @return true/false
         */
        @Override
        public boolean isCellEditable(final int row,
                                      final int col) {
            /* switch on column */
            switch (col) {
                case COLUMN_ASSET:
                case COLUMN_PREVPRICE:
                case COLUMN_PREVDATE:
                    return false;
                case COLUMN_PRICE:
                default:
                    return true;
            }
        }

        /**
         * Get the value at (row, col).
         * @param row the row
         * @param col the column
         * @return the object value
         */
        @Override
        public Object getValueAt(final int row,
                                 final int col) {
            /* Access the spot price */
            SpotPrice mySpot = (SpotPrice) thePrices.get(row);
            Object o;

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_ASSET:
                    o = mySpot.getAccount().getName();
                    break;
                case COLUMN_PRICE:
                    o = mySpot.getPrice();
                    break;
                case COLUMN_PREVPRICE:
                    o = mySpot.getPrevPrice();
                    break;
                case COLUMN_PREVDATE:
                    o = mySpot.getPrevDate();
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value */
            if ((o == null) && (mySpot.hasErrors(getFieldForCell(row, col)))) {
                o = RendererFieldValue.Error;
            }

            /* Return to caller */
            return o;
        }

        /**
         * Set the value at (row, col).
         * @param obj the object value to set
         * @param row the row
         * @param col the column
         */
        @Override
        public void setValueAt(final Object obj,
                               final int row,
                               final int col) {
            /* Access the line */
            SpotPrice mySpot = (SpotPrice) thePrices.get(row);

            /* Push history */
            mySpot.pushHistory();

            /* Protect against Exceptions */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_PRICE:
                        mySpot.setPrice((Price) obj);
                        break;
                    default:
                        break;
                }

                /* Handle Exceptions */
            } catch (JDataException e) {
                /* Reset values */
                mySpot.popHistory();
                mySpot.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to update field at (" + row + "," + col + ")", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (mySpot.checkForHistory()) {
                /* Increment data version */
                theUpdateSet.incrementVersion();

                /* Update components to reflect changes */
                fireTableDataChanged();
                notifyChanges();
            }
        }
    }

    /**
     * SpotView mouse listener.
     */
    private static final class SpotViewMouse extends DataMouse<AccountPrice> {
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
    private final class SpotViewColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5102715203937500181L;

        /**
         * Date Renderer.
         */
        private final CalendarRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalRenderer theDecimalRenderer;

        /**
         * Price Editor.
         */
        private final PriceEditor thePriceEditor;

        /**
         * String Renderer.
         */
        private final StringRenderer theStringRenderer;

        /**
         * Constructor.
         */
        private SpotViewColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDecimalRenderer = new DecimalRenderer();
            thePriceEditor = new PriceEditor();
            theStringRenderer = new StringRenderer();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_ASSET, WIDTH_COLUMN, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_PRICE, WIDTH_COLUMN, theDecimalRenderer, thePriceEditor));
            addColumn(new DataColumn(COLUMN_PREVPRICE, WIDTH_COLUMN, theDecimalRenderer, null));
            addColumn(new DataColumn(COLUMN_PREVDATE, WIDTH_COLUMN, theDateRenderer, null));
        }
    }
}
