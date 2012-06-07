/*******************************************************************************
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

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import net.sourceforge.JDataManager.Difference;
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
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.PriceEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.ui.Renderer.StringRenderer;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.models.views.ViewList;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class PricePoint extends DataTable<AccountPrice> {
    /* Members */
    private static final long serialVersionUID = 5826211763056873599L;

    private View theView = null;
    private ViewList theViewSet = null;
    private ListClass theViewList = null;
    private SpotViewModel theModel = null;
    private SpotPrices theSnapshot = null;
    private AccountPriceList thePrices = null;
    private MainTab theParent = null;
    private JPanel thePanel = null;
    private PricePoint theTable = this;
    private SpotViewMouse theMouse = null;
    private SpotViewColumnModel theColumns = null;
    private DateDay theDate = null;
    private AccountType theAccountType = null;
    private SpotSelect theSelect = null;
    private SaveButtons theTabButs = null;
    private JDataEntry theDataPrice = null;
    private ErrorPanel theError = null;

    /* Access methods */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    /* Access the debug entry */
    @Override
    public JDataEntry getDataEntry() {
        return theDataPrice;
    }

    @Override
    public JDataManager getDataManager() {
        return theParent.getDataMgr();
    }

    /* Table headers */
    private static final String titleAsset = "Asset";
    private static final String titlePrice = "Price";
    private static final String titlePrevPrice = "Previous Price";
    private static final String titlePrevDate = "Previous Date";

    /* Table columns */
    private static final int COLUMN_ASSET = 0;
    private static final int COLUMN_PRICE = 1;
    private static final int COLUMN_PREVPRICE = 2;
    private static final int COLUMN_PREVDATE = 3;

    /* Constructor */
    public PricePoint(MainTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataMgr());

        /* Declare variables */
        GroupLayout myLayout;
        JDataEntry mySection;

        /* Record the passed details */
        theParent = pParent;
        theView = pParent.getView();

        /* Build the View set */
        theViewSet = new ViewList(theView);
        theViewList = theViewSet.registerClass(SpotPrice.class);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataPrice = myDataMgr.new JDataEntry("SpotPrices");
        theDataPrice.addAsChildOf(mySection);

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
        setPreferredScrollableViewportSize(new Dimension(900, 200));

        /* Add the mouse listener */
        theMouse = new SpotViewMouse();
        addMouseListener(theMouse);

        /* Create the sub panels */
        theSelect = new SpotSelect(theView, this);
        theTabButs = new SaveButtons(this);

        /* Create the error panel for this view */
        theError = new ErrorPanel(this);

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
                                                    .addComponent(theError, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(theSelect, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(getScrollPane(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)
                                                    .addComponent(theTabButs, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theError).addComponent(theSelect)
                                  .addComponent(getScrollPane()).addComponent(theTabButs)));
    }

    /**
     * Save changes from the view into the underlying data
     */
    @Override
    public void saveData() {
        if (theSnapshot != null) {
            validateAll();
            if (!hasErrors())
                theViewSet.applyChanges();
        }
    }

    /**
     * Update Debug view
     */
    @Override
    public void updateDebug() {
        theDataPrice.setObject(theSnapshot);
    }

    /**
     * Lock on error
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(boolean isError) {
        /* Hide selection panel */
        theSelect.setVisible(!isError);

        /* Lock scroll-able area */
        getScrollPane().setEnabled(!isError);

        /* Lock tab buttons area */
        theTabButs.setEnabled(!isError);
    }

    /**
     * Notify table that there has been a change in selection by an underlying control
     * @param obj the underlying control that has changed selection
     */
    @Override
    public void notifySelection(Object obj) {
        /* if this is a change from the date */
        if (obj == (Object) theSelect) {
            /* Set the deleted option */
            if (getList().getShowDeleted() != theSelect.getShowClosed())
                setShowDeleted(theSelect.getShowClosed());

            /* Access selection */
            AccountType myType = theSelect.getAccountType();
            DateDay myDate = theSelect.getDate();

            /* If the selection differs */
            if (((!Difference.isEqual(theDate, myDate))) || (!Difference.isEqual(theAccountType, myType))) {
                /* Protect against exceptions */
                try {
                    /* Set selection */
                    setSelection(myType, myDate);

                    /* Create SavePoint */
                    theSelect.createSavePoint();
                }

                /* Catch Exceptions */
                catch (JDataException e) {
                    /* Build the error */
                    JDataException myError = new JDataException(ExceptionClass.DATA,
                            "Failed to change selection", e);

                    /* Show the error */
                    theError.setError(myError);

                    /* Restore SavePoint */
                    theSelect.restoreSavePoint();
                }
            }
        }
    }

    /**
     * Refresh views/controls after a load/update of underlying data
     * @throws JDataException
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
     * Call underlying controls to take notice of changes in view/selection
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (thePrices != null)
            thePrices.findEditState();

        /* Update the table buttons */
        theTabButs.setLockDown();
        theSelect.setLockDown();

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    /**
     * Set Selection to the specified account type and date
     * @param pType the account type
     * @param pDate the Date for the extract
     * @throws JDataException
     */
    public void setSelection(AccountType pType,
                             DateDay pDate) throws JDataException {
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
        }

        /* else invalid selection */
        else {
            /* Set no selection */
            theSnapshot = null;
            thePrices = null;
            theSelect.setAdjacent(null, null);
        }

        /* Update other details */
        setList(thePrices);
        theViewList.setDataList(thePrices);
        theTabButs.setLockDown();
        theSelect.setLockDown();
        theParent.setVisibility();
    }

    /**
     * Check whether insert is allowed for this table
     * @return insert allowed (true/false)
     */
    @Override
    protected boolean insertAllowed() {
        return false;
    }

    /**
     * Check whether a row is deletable
     * @param pRow the row
     * @return is the row deletable
     */
    protected boolean isRowDeletable(SpotPrice pRow) {
        /* Switch on the Data State */
        switch (pRow.getState()) {
            case CLEAN:
                if (pRow.getBase().isDeleted())
                    return false;
            case NEW:
            case CHANGED:
            case RECOVERED:
                return true;
        }

        /* Not Deletable */
        return false;
    }

    /**
     * Check whether a row is recoverable
     * @param pRow the row
     * @return is the row recoverable
     */
    protected boolean isRowRecoverable(SpotPrice pRow) {
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
        }

        /* Not Recoverable */
        return false;
    }

    /**
     * Check whether we duplicate a row
     * @param pRow the row
     * @return false
     */
    protected boolean isRowDuplicatable(SpotPrice pRow) {
        return false;
    }

    /**
     * Check whether we should hide deleted rows
     * @return false
     */
    @Override
    protected boolean hideDeletedRows() {
        return false;
    }

    /**
     * Check whether we duplicate a row
     * @param pRow the row
     * @return true
     */
    protected boolean disableShowDeleted(SpotPrice pRow) {
        return true;
    }

    /* SpotView table model */
    public class SpotViewModel extends DataTableModel {
        private static final long serialVersionUID = 2520681944053000625L;

        /**
         * Constructor
         */
        private SpotViewModel() {
            /* call constructor */
            super(theTable);
        }

        /**
         * Get the number of display columns
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null) ? 0 : theColumns.getColumnCount();
        }

        /**
         * Get the number of rows in the current table
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (thePrices == null) ? 0 : thePrices.size();
        }

        /**
         * Get the name of the column
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(int col) {
            switch (col) {
                case COLUMN_ASSET:
                    return titleAsset;
                case COLUMN_PRICE:
                    return titlePrice;
                case COLUMN_PREVPRICE:
                    return titlePrevPrice;
                case COLUMN_PREVDATE:
                    return titlePrevDate;
                default:
                    return null;
            }
        }

        /**
         * Get the object class of the column
         * @param col the column
         * @return the class of the objects associated with the column
         */
        @Override
        public Class<?> getColumnClass(int col) {
            switch (col) {
                case COLUMN_ASSET:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        /**
         * Obtain the Field id associated with the row
         * @param pRow the row
         * @param pCol the column
         */
        @Override
        public JDataField getFieldForCell(int pRow,
                                          int pCol) {
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
         * Is the cell at (row, col) editable
         */
        @Override
        public boolean isCellEditable(int row,
                                      int col) {
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
         * Get the value at (row, col)
         * @return the object value
         */
        @Override
        public Object getValueAt(int row,
                                 int col) {
            SpotPrice mySpot;
            Object o;

            /* Access the spot price */
            mySpot = (SpotPrice) thePrices.get(row);

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
            if ((o == null) && (mySpot.hasErrors(getFieldForCell(row, col))))
                o = Renderer.getError();

            /* Return to caller */
            return o;
        }

        /**
         * Set the value at (row, col)
         * @param obj the object value to set
         */
        @Override
        public void setValueAt(Object obj,
                               int row,
                               int col) {
            SpotPrice mySpot;

            /* Access the line */
            mySpot = (SpotPrice) thePrices.get(row);

            /* Push history */
            mySpot.pushHistory();

            /* Protect against Exceptions */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_PRICE:
                        mySpot.setPrice((Price) obj);
                        break;
                }

            }

            /* Handle Exceptions */
            catch (Throwable e) {
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
                /* Note that the item has changed */
                mySpot.clearErrors();
                mySpot.setState(DataState.CHANGED);

                /* Validate the item and update the edit state */
                mySpot.validate();
                thePrices.findEditState();

                /* note that we have updated this cell */
                fireTableRowsUpdated(row, row);

                /* Note that changes have occurred */
                notifyChanges();
                updateDebug();
            }
        }
    }

    /**
     * SpotView mouse listener
     */
    private class SpotViewMouse extends DataMouse<AccountPrice> {
        /**
         * Constructor
         */
        private SpotViewMouse() {
            /* Call super-constructor */
            super(theTable);
        }
    }

    /**
     * Column Model class
     */
    private class SpotViewColumnModel extends DataColumnModel {
        private static final long serialVersionUID = 5102715203937500181L;

        /* Renderers/Editors */
        private CalendarRenderer theDateRenderer = null;
        private DecimalRenderer theDecimalRenderer = null;
        private PriceEditor thePriceEditor = null;
        private StringRenderer theStringRenderer = null;

        /**
         * Constructor
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
            addColumn(new DataColumn(COLUMN_ASSET, 130, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_PRICE, 130, theDecimalRenderer, thePriceEditor));
            addColumn(new DataColumn(COLUMN_PREVPRICE, 130, theDecimalRenderer, null));
            addColumn(new DataColumn(COLUMN_PREVDATE, 130, theDateRenderer, null));
        }
    }
}
