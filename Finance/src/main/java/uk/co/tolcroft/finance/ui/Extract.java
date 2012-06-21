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

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDateDay.DateDayRangeSelect;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Units;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.ui.controls.ComboSelect;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.CalendarEditor;
import uk.co.tolcroft.models.ui.Editor.ComboBoxEditor;
import uk.co.tolcroft.models.ui.Editor.DilutionEditor;
import uk.co.tolcroft.models.ui.Editor.IntegerEditor;
import uk.co.tolcroft.models.ui.Editor.MoneyEditor;
import uk.co.tolcroft.models.ui.Editor.StringEditor;
import uk.co.tolcroft.models.ui.Editor.UnitsEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.ui.Renderer.IntegerRenderer;
import uk.co.tolcroft.models.ui.Renderer.RendererFieldValue;
import uk.co.tolcroft.models.ui.Renderer.StringRenderer;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.models.views.UpdateSet;
import uk.co.tolcroft.models.views.UpdateSet.UpdateEntry;

/**
 * Event Extract Table.
 * @author Tony Washer
 */
public class Extract extends DataTable<Event> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5531752729052421790L;

    /**
     * Data View.
     */
    private final transient View theView;

    /**
     * Update Set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * Update Entry.
     */
    private final transient UpdateEntry theUpdateEntry;

    /**
     * Table Model.
     */
    private final ExtractModel theModel;

    /**
     * Events.
     */
    private transient EventList theEvents = null;

    /**
     * The parent.
     */
    private final transient MainTab theParent;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * Self Reference.
     */
    private final Extract theTable = this;

    /**
     * Column Model.
     */
    private final ExtractColumnModel theColumns;

    /**
     * Selected range.
     */
    private transient DateDayRange theRange = null;

    /**
     * Range selection panel.
     */
    private DateDayRangeSelect theSelect = null;

    /**
     * Save Buttons.
     */
    private final SaveButtons theTabButs;

    /**
     * Data Entry.
     */
    private final transient JDataEntry theDataExtract;

    /**
     * Error Panel.
     */
    private final ErrorPanel theError;

    /**
     * ComboList.
     */
    private transient ComboSelect theComboList = null;

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

    @Override
    public JDataEntry getDataEntry() {
        return theDataExtract;
    }

    /**
     * Date column title.
     */
    private static final String TITLE_DATE = "Date";

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = "Description";

    /**
     * TransType column title.
     */
    private static final String TITLE_TRANS = "TransactionType";

    /**
     * Amount column title.
     */
    private static final String TITLE_AMOUNT = "Amount";

    /**
     * Debit column title.
     */
    private static final String TITLE_DEBIT = "Debit";

    /**
     * Credit column title.
     */
    private static final String TITLE_CREDIT = "Credit";

    /**
     * Units column title.
     */
    private static final String TITLE_UNITS = "Units";

    /**
     * Dilution column title.
     */
    private static final String TITLE_DILUTE = "Dilution";

    /**
     * TaxCredit column title.
     */
    private static final String TITLE_TAXCRED = "TaxCredit";

    /**
     * Years column title.
     */
    private static final String TITLE_YEARS = "Yrs";

    /**
     * Date column id.
     */
    private static final int COLUMN_DATE = 0;

    /**
     * TransType column id.
     */
    private static final int COLUMN_TRANTYP = 1;

    /**
     * Description column id.
     */
    private static final int COLUMN_DESC = 2;

    /**
     * Amount column id.
     */
    private static final int COLUMN_AMOUNT = 3;

    /**
     * Debit column id.
     */
    private static final int COLUMN_DEBIT = 4;

    /**
     * Credit column id.
     */
    private static final int COLUMN_CREDIT = 5;

    /**
     * Units column id.
     */
    private static final int COLUMN_UNITS = 6;

    /**
     * Dilution column id.
     */
    private static final int COLUMN_DILUTE = 7;

    /**
     * Tax Credit column id.
     */
    private static final int COLUMN_TAXCRED = 8;

    /**
     * Years column id.
     */
    private static final int COLUMN_YEARS = 9;

    /**
     * Date column width.
     */
    private static final int WIDTH_DATE = 90;

    /**
     * TransType column width.
     */
    private static final int WIDTH_TRANTYP = 110;

    /**
     * Description column width.
     */
    private static final int WIDTH_DESC = 140;

    /**
     * Amount column width.
     */
    private static final int WIDTH_AMOUNT = 90;

    /**
     * Debit column width.
     */
    private static final int WIDTH_DEBIT = 130;

    /**
     * Credit column width.
     */
    private static final int WIDTH_CREDIT = 130;

    /**
     * Units column width.
     */
    private static final int WIDTH_UNITS = 80;

    /**
     * Dilution column width.
     */
    private static final int WIDTH_DILUTE = 70;

    /**
     * Tax Credit column width.
     */
    private static final int WIDTH_TAXCRED = 90;

    /**
     * Years column width.
     */
    private static final int WIDTH_YEARS = 30;

    /**
     * Panel width.
     */
    private static final int PANEL_WIDTH = 900;

    /**
     * Panel height.
     */
    private static final int PANEL_HEIGHT = 200;

    /**
     * Constructor for Extract Window.
     * @param pParent the parent window
     */
    public Extract(final MainTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataMgr());

        /* Declare variables */
        GroupLayout myLayout;
        JDataEntry mySection;

        /* Record the passed details */
        theParent = pParent;
        theView = pParent.getView();

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet(theView);
        theUpdateEntry = theUpdateSet.registerClass(Event.class);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        theDataExtract = myDataMgr.new JDataEntry("Extract");
        theDataExtract.addAsChildOf(mySection);

        /* Create the model and declare it to our superclass */
        theModel = new ExtractModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new ExtractColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        /* Add the mouse listener */
        ExtractMouse myMouse = new ExtractMouse();
        addMouseListener(myMouse);

        /* Create the sub panels */
        theSelect = new DateDayRangeSelect();
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
                                                                  GroupLayout.DEFAULT_SIZE, PANEL_WIDTH,
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
     * Save changes from the view into the underlying data.
     */
    @Override
    public void saveData() {
        if (theEvents != null) {
            super.validateAll();
            if (!theUpdateSet.hasErrors()) {
                theUpdateSet.applyChanges();
            }
        }
    }

    /**
     * Update Debug view.
     */
    @Override
    public void updateDebug() {
        theDataExtract.setObject(theEvents);
    }

    /**
     * Lock on error.
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(final boolean isError) {
        /* Hide selection panel */
        theSelect.setVisible(!isError);

        /* Lock scroll-able area */
        getScrollPane().setEnabled(!isError);

        /* Lock row/tab buttons area */
        theTabButs.setEnabled(!isError);
    }

    /**
     * Notify table that there has been a change in selection by an underlying control.
     * @param obj the underlying control that has changed selection
     */
    @Override
    public void notifySelection(final Object obj) {
        /* if this is a change from the range */
        if (theSelect.equals(obj)) {
            /* Protect against exceptions */
            try {
                /* Set the new range */
                setSelection(theSelect.getRange());

                /* Create SavePoint */
                theSelect.createSavePoint();

                /* Catch Exceptions */
            } catch (JDataException e) {
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

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JDataException on error
     */
    public void refreshData() throws JDataException {
        /* Access the combo list from parent */
        theComboList = theParent.getComboList();

        /* Access range */
        DateDayRange myRange = theView.getRange();
        theSelect.setOverallRange(myRange);
        theRange = theSelect.getRange();
        setSelection(theRange);

        /* Create SavePoint */
        theSelect.createSavePoint();
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (theEvents != null) {
            theEvents.findEditState();
        }

        /* Update the table buttons */
        theTabButs.setLockDown();
        theSelect.setEnabled(!hasUpdates());

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    /**
     * Set Selection to the specified date range.
     * @param pRange the Date range for the extract
     * @throws JDataException on error
     */
    public void setSelection(final DateDayRange pRange) throws JDataException {
        theRange = pRange;
        theEvents = null;
        if (theRange != null) {
            /* Get the Rates edit list */
            FinanceData myData = theView.getData();
            EventList myEvents = myData.getEvents();
            theEvents = myEvents.getEditList(pRange);

            theColumns.setDateEditorRange(theRange);
        }
        setList(theEvents);
        theUpdateEntry.setDataList(theEvents);
        theTabButs.setLockDown();
        theSelect.setEnabled(!hasUpdates());
        theParent.setVisibility();
    }

    /**
     * Set selection to the period designated by the referenced control.
     * @param pSource the source control
     */
    public void selectPeriod(final DateDayRangeSelect pSource) {
        /* Protect against exceptions */
        try {
            /* Adjust the period selection (this will not call back) */
            theSelect.setSelection(pSource);

            /* Utilise the selection */
            setSelection(theSelect.getRange());

            /* Catch exceptions */
        } catch (JDataException e) {
            /* Build the error */
            JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to select Period", e);

            /* Show the error */
            theError.setError(myError);

            /* Restore the original selection */
            theSelect.restoreSavePoint();
        }
    }

    /**
     * Obtain the correct ComboBox for the given row/column.
     * @param row the row
     * @param column the column
     * @return the comboBox
     */
    @Override
    public JComboBox getComboBox(final int row,
                                 final int column) {
        /* Access the event */
        Event myEvent = theEvents.get(row);

        /* Switch on column */
        switch (column) {
            case COLUMN_TRANTYP:
                return theComboList.getAllTransTypes();
            case COLUMN_CREDIT:
                return theComboList.getCreditAccounts(myEvent.getTransType(), myEvent.getDebit());
            case COLUMN_DEBIT:
                return theComboList.getDebitAccounts(myEvent.getTransType());
            default:
                return null;
        }
    }

    /**
     * Extract table model.
     */
    public final class ExtractModel extends DataTableModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 7997087757206121152L;

        /**
         * Constructor.
         */
        private ExtractModel() {
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
            return (theEvents == null) ? 0 : theEvents.size();
        }

        /**
         * Get the name of the column.
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(final int col) {
            switch (col) {
                case COLUMN_DATE:
                    return TITLE_DATE;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_TRANTYP:
                    return TITLE_TRANS;
                case COLUMN_AMOUNT:
                    return TITLE_AMOUNT;
                case COLUMN_CREDIT:
                    return TITLE_CREDIT;
                case COLUMN_DEBIT:
                    return TITLE_DEBIT;
                case COLUMN_UNITS:
                    return TITLE_UNITS;
                case COLUMN_DILUTE:
                    return TITLE_DILUTE;
                case COLUMN_TAXCRED:
                    return TITLE_TAXCRED;
                case COLUMN_YEARS:
                    return TITLE_YEARS;
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
                case COLUMN_DESC:
                    return String.class;
                case COLUMN_TRANTYP:
                    return String.class;
                case COLUMN_CREDIT:
                    return String.class;
                case COLUMN_DEBIT:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        /**
         * Obtain the Field id associated with the column.
         * @param row the row
         * @param column the column
         * @return the fieldId
         */
        @Override
        public JDataField getFieldForCell(final int row,
                                          final int column) {
            /* Switch on column */
            switch (column) {
                case COLUMN_DATE:
                    return Event.FIELD_DATE;
                case COLUMN_DESC:
                    return Event.FIELD_DESC;
                case COLUMN_TRANTYP:
                    return Event.FIELD_TRNTYP;
                case COLUMN_AMOUNT:
                    return Event.FIELD_AMOUNT;
                case COLUMN_CREDIT:
                    return Event.FIELD_CREDIT;
                case COLUMN_DEBIT:
                    return Event.FIELD_DEBIT;
                case COLUMN_UNITS:
                    return Event.FIELD_UNITS;
                case COLUMN_DILUTE:
                    return Event.FIELD_DILUTION;
                case COLUMN_TAXCRED:
                    return Event.FIELD_TAXCREDIT;
                case COLUMN_YEARS:
                    return Event.FIELD_YEARS;
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
            /* Access the event */
            Event myEvent = theEvents.get(row);

            /* Cannot edit if row is deleted or locked */
            if (myEvent.isDeleted() || myEvent.isLocked()) {
                return false;
            }

            /* switch on column */
            switch (col) {
                case COLUMN_DATE:
                    return true;
                case COLUMN_TRANTYP:
                    return (myEvent.getDate() != null);
                case COLUMN_DESC:
                    return ((myEvent.getDate() != null) && (myEvent.getTransType() != null));
                default:
                    if ((myEvent.getDate() == null) || (myEvent.getDesc() == null)
                            || (myEvent.getTransType() == null)) {
                        return false;
                    }
                    switch (col) {
                        case COLUMN_UNITS:
                            return ((myEvent.getDebit() != null) && (myEvent.getCredit() != null) && (myEvent
                                    .getCredit().isPriced() != myEvent.getDebit().isPriced()));
                        case COLUMN_YEARS:
                            return ((myEvent.getTransType() != null) && (myEvent.getTransType()
                                    .isTaxableGain()));
                        case COLUMN_TAXCRED:
                            return Event.needsTaxCredit(myEvent.getTransType(), myEvent.getDebit());
                        case COLUMN_DILUTE:
                            return Event.needsDilution(myEvent.getTransType());
                        default:
                            return true;
                    }
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
            Object o;

            /* Access the event */
            Event myEvent = theEvents.get(row);

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_DATE:
                    o = myEvent.getDate();
                    break;
                case COLUMN_TRANTYP:
                    o = myEvent.getTransType();
                    break;
                case COLUMN_CREDIT:
                    o = myEvent.getCredit();
                    break;
                case COLUMN_DEBIT:
                    o = myEvent.getDebit();
                    break;
                case COLUMN_AMOUNT:
                    o = myEvent.getAmount();
                    break;
                case COLUMN_DILUTE:
                    o = myEvent.getDilution();
                    break;
                case COLUMN_TAXCRED:
                    o = myEvent.getTaxCredit();
                    break;
                case COLUMN_UNITS:
                    o = myEvent.getUnits();
                    break;
                case COLUMN_YEARS:
                    o = myEvent.getYears();
                    break;
                case COLUMN_DESC:
                    o = myEvent.getDesc();
                    if ((o != null) && (((String) o).length() == 0)) {
                        o = null;
                    }
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myEvent.hasErrors(getFieldForCell(row, col)))) {
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
            Event myEvent;

            /* Access the line */
            myEvent = theEvents.get(row);

            /* Push history */
            myEvent.pushHistory();

            /* Determine whether the line needs a tax credit */
            boolean needsTaxCredit = Event.needsTaxCredit(myEvent.getTransType(), myEvent.getDebit());

            /* Protect against Exceptions */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_DATE:
                        myEvent.setDate((DateDay) obj);
                        break;
                    case COLUMN_DESC:
                        myEvent.setDescription((String) obj);
                        break;
                    case COLUMN_TRANTYP:
                        myEvent.setTransType((TransactionType) obj);
                        /* If the need for a tax credit has changed */
                        if (needsTaxCredit != Event
                                .needsTaxCredit(myEvent.getTransType(), myEvent.getDebit())) {
                            /* Determine new Tax Credit */
                            if (needsTaxCredit) {
                                myEvent.setTaxCredit(null);
                            } else {
                                myEvent.setTaxCredit(myEvent.calculateTaxCredit());
                            }
                        }
                        break;
                    case COLUMN_AMOUNT:
                        myEvent.setAmount((Money) obj);
                        /* Determine new Tax Credit if required */
                        if (needsTaxCredit) {
                            myEvent.setTaxCredit(myEvent.calculateTaxCredit());
                        }
                        break;
                    case COLUMN_DILUTE:
                        myEvent.setDilution((Dilution) obj);
                        break;
                    case COLUMN_TAXCRED:
                        myEvent.setTaxCredit((Money) obj);
                        break;
                    case COLUMN_YEARS:
                        myEvent.setYears((Integer) obj);
                        break;
                    case COLUMN_UNITS:
                        myEvent.setUnits((Units) obj);
                        break;
                    case COLUMN_CREDIT:
                        myEvent.setCredit((Account) obj);
                        break;
                    case COLUMN_DEBIT:
                        myEvent.setDebit((Account) obj);
                        break;
                    default:
                        break;
                }

                /* Handle Exceptions */
            } catch (Exception e) {
                /* Reset values */
                myEvent.popHistory();
                myEvent.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to update field at (" + row + "," + col + ")", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (myEvent.checkForHistory()) {
                /* Note that the item has changed */
                myEvent.clearErrors();
                myEvent.setState(DataState.CHANGED);

                /* Validate the item and update the edit state */
                myEvent.validate();
                theEvents.findEditState();

                /* Switch on the updated column */
                switch (col) {
                /* If we have changed a sorting column */
                    case COLUMN_DATE:
                    case COLUMN_DESC:
                    case COLUMN_TRANTYP:
                        /* Re-Sort the row */
                        theEvents.reSort(myEvent);

                        /* Determine new row # */
                        int myNewRowNo = myEvent.indexOf();

                        /* If the row # has changed */
                        if (myNewRowNo != row) {
                            /* Report the move of the row */
                            fireMoveRowEvents(row, myNewRowNo);
                            selectRowWithScroll(myNewRowNo);
                            break;
                        }

                        /* else fall through */

                        /* else note that we have updated this cell */
                    default:
                        fireTableRowsUpdated(row, row);
                        break;
                }

                /* Note that changes have occurred */
                notifyChanges();
                updateDebug();
            }
        }
    }

    /**
     * Extract mouse listener.
     */
    private final class ExtractMouse extends DataMouse<Event> {
        /**
         * PopUp viewAccount.
         */
        private static final String POPUP_VIEW = "View Account";

        /**
         * PopUp maintAccount.
         */
        private static final String POPUP_MAINT = "Maintain Account";

        /**
         * PopUp nullUnits.
         */
        private static final String POPUP_NULLUNITS = "Set Null Units";

        /**
         * PopUp nullTaxCredit.
         */
        private static final String POPUP_NULLTAX = "Set Null TaxCredit";

        /**
         * PopUp nullYears.
         */
        private static final String POPUP_NULLYEARS = "Set Null Years";

        /**
         * PopUp nullDilution.
         */
        private static final String POPUP_NULLDILUTE = "Set Null Dilution";

        /**
         * PopUp calcTax.
         */
        private static final String POPUP_CALCTAX = "Calculate Tax Credit";

        /**
         * Constructor.
         */
        private ExtractMouse() {
            /* Call super-constructor */
            super(theTable);
        }

        /**
         * Add Null commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addNullCommands(final JPopupMenu pMenu) {
            JMenuItem myItem;
            Event myEvent;
            boolean enableNullUnits = false;
            boolean enableNullTax = false;
            boolean enableNullYears = false;
            boolean enableNullDilution = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as event */
                myEvent = (Event) myRow;

                /* Enable null Units if we have units */
                if (myEvent.getUnits() != null) {
                    enableNullUnits = true;
                }

                /* Enable null Tax if we have tax */
                if (myEvent.getTaxCredit() != null) {
                    enableNullTax = true;
                }

                /* Enable null Years if we have years */
                if (myEvent.getYears() != null) {
                    enableNullYears = true;
                }

                /* Enable null Dilution if we have dilution */
                if (myEvent.getDilution() != null) {
                    enableNullDilution = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableNullUnits || enableNullTax || enableNullYears || enableNullDilution)
                    && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set null units */
            if (enableNullUnits) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLUNITS);
                myItem.setActionCommand(POPUP_NULLUNITS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null tax */
            if (enableNullTax) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLTAX);
                myItem.setActionCommand(POPUP_NULLTAX);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null years */
            if (enableNullYears) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLYEARS);
                myItem.setActionCommand(POPUP_NULLYEARS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null dilution */
            if (enableNullDilution) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLDILUTE);
                myItem.setActionCommand(POPUP_NULLDILUTE);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Add Special commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addSpecialCommands(final JPopupMenu pMenu) {
            JMenuItem myItem;
            Event myEvent;
            Money myTax;
            TransactionType myTrans;
            boolean enableCalcTax = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as event */
                myEvent = (Event) myRow;
                myTax = myEvent.getTaxCredit();
                myTrans = myEvent.getTransType();

                /* If we have a calculable tax credit that is null/zero */
                if ((myTrans != null) && ((myTrans.isInterest()) || (myTrans.isDividend()))
                        && ((myTax == null) || (!myTax.isNonZero()))) {
                    enableCalcTax = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableCalcTax) && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can calculate tax */
            if (enableCalcTax) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_CALCTAX);
                myItem.setActionCommand(POPUP_CALCTAX);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Add Navigation commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addNavigationCommands(final JPopupMenu pMenu) {
            JMenuItem myItem;
            Event myEvent;
            Account myAccount;
            int myRow;
            int myCol;
            boolean enableNavigate = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Access the popUp row/column and ignore if not valid */
            myRow = getPopupRow();
            myCol = getPopupCol();
            if (myRow < 0) {
                return;
            }

            /* Access the event */
            myEvent = theTable.extractItemAt(myRow);

            /* If the column is Credit */
            if (myCol == COLUMN_CREDIT) {
                myAccount = myEvent.getCredit();
            } else if (myCol == COLUMN_DEBIT) {
                myAccount = myEvent.getDebit();
            } else {
                myAccount = null;
            }

            /* If we have an account we can navigate */
            if (myAccount != null) {
                enableNavigate = true;
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableNavigate) && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can navigate */
            if (enableNavigate) {
                /* Create the View account choice */
                myItem = new JMenuItem(POPUP_VIEW + ": " + myAccount.getName());

                /* Set the command and add to menu */
                myItem.setActionCommand(POPUP_VIEW + ":" + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);

                /* Create the Maintain account choice */
                myItem = new JMenuItem(POPUP_MAINT + ": " + myAccount.getName());

                /* Set the command and add to menu */
                myItem.setActionCommand(POPUP_MAINT + ":" + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Perform actions for controls/pop-ups on this table.
         * @param evt the event
         */
        @Override
        public void actionPerformed(final ActionEvent evt) {
            String myCmd = evt.getActionCommand();

            /* Cancel any editing */
            theTable.cancelEditing();

            /* If this is a set null units command */
            if (myCmd.equals(POPUP_NULLUNITS)) {
                /* Set Units column to null */
                setColumnToNull(COLUMN_UNITS);

                /* else if this is a set null tax command */
            } else if (myCmd.equals(POPUP_NULLTAX)) {
                /* Set Tax column to null */
                setColumnToNull(COLUMN_TAXCRED);

                /* If this is a set null years command */
            } else if (myCmd.equals(POPUP_NULLYEARS)) {
                /* Set Years column to null */
                setColumnToNull(COLUMN_YEARS);

                /* If this is a set null dilute command */
            } else if (myCmd.equals(POPUP_NULLDILUTE)) {
                /* Set Dilution column to null */
                setColumnToNull(COLUMN_DILUTE);

                /* If this is a calculate Tax Credits command */
            } else if (myCmd.equals(POPUP_CALCTAX)) {
                /* Calculate the tax credits */
                calculateTaxCredits();

                /* If this is a navigate command */
            } else if ((myCmd.startsWith(POPUP_VIEW)) || (myCmd.startsWith(POPUP_MAINT))) {
                /* perform the navigation */
                performNavigation(myCmd);

                /* else we do not recognise the action */
            } else {
                /* Pass it to the superclass */
                super.actionPerformed(evt);
                return;
            }

            /* Notify of any changes */
            notifyChanges();
            updateDebug();
        }

        /**
         * Calculate tax credits.
         */
        private void calculateTaxCredits() {
            Event myEvent;
            TransactionType myTrans;
            Money myTax;
            int row;

            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Determine row */
                row = myRow.indexOf();
                if (theTable.hasHeader()) {
                    row--;
                }

                /* Access the event */
                myEvent = (Event) myRow;
                myTrans = myEvent.getTransType();
                myTax = myEvent.getTaxCredit();

                /* Ignore rows with invalid transaction type */
                if ((myTrans == null) || ((!myTrans.isInterest()) && (!myTrans.isDividend()))) {
                    continue;
                }

                /* Ignore rows with tax credit already set */
                if ((myTax != null) && (myTax.isNonZero())) {
                    continue;
                }

                /* Calculate the tax credit */
                myTax = myEvent.calculateTaxCredit();

                /* set the tax credit value */
                theModel.setValueAt(myTax, row, COLUMN_TAXCRED);
                theModel.fireTableCellUpdated(row, COLUMN_TAXCRED);
            }
        }

        /**
         * Perform a navigation command.
         * @param pCmd the navigation command
         */
        private void performNavigation(final String pCmd) {
            String[] tokens;
            String myName = null;
            Account myAccount;

            /* Access the action command */
            tokens = pCmd.split(":");
            String myCmd = tokens[0];
            if (tokens.length > 1) {
                myName = tokens[1];
            }

            /* Access the correct account */
            myAccount = theView.getData().getAccounts().findItemByName(myName);

            /* If this is an account view request */
            if (myCmd.compareTo(POPUP_VIEW) == 0) {
                /* Switch view */
                theParent.selectAccount(myAccount, theSelect);

                /* If this is an account maintenance request */
            } else if (myCmd.compareTo(POPUP_MAINT) == 0) {
                /* Switch view */
                theParent.selectAccountMaint(myAccount);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class ExtractColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7502445487118370020L;

        /**
         * Date Renderer.
         */
        private final CalendarRenderer theDateRenderer;

        /**
         * Date Editor.
         */
        private final CalendarEditor theDateEditor;

        /**
         * Decimal Renderer.
         */
        private final DecimalRenderer theDecimalRenderer;

        /**
         * Money Editor.
         */
        private final MoneyEditor theMoneyEditor;

        /**
         * Units Editor.
         */
        private final UnitsEditor theUnitsEditor;

        /**
         * Integer Renderer.
         */
        private final IntegerRenderer theIntegerRenderer;

        /**
         * Integer Editor.
         */
        private final IntegerEditor theIntegerEditor;

        /**
         * String Renderer.
         */
        private final StringRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringEditor theStringEditor;

        /**
         * Dilution Editor.
         */
        private final DilutionEditor theDiluteEditor;

        /**
         * comboBox Editor.
         */
        private final ComboBoxEditor theComboEditor;

        /**
         * Constructor.
         */
        private ExtractColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDateEditor = new CalendarEditor();
            theDecimalRenderer = new DecimalRenderer();
            theMoneyEditor = new MoneyEditor();
            theUnitsEditor = new UnitsEditor();
            theIntegerRenderer = new IntegerRenderer();
            theIntegerEditor = new IntegerEditor();
            theStringRenderer = new StringRenderer();
            theStringEditor = new StringEditor();
            theDiluteEditor = new DilutionEditor();
            theComboEditor = new ComboBoxEditor();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            addColumn(new DataColumn(COLUMN_TRANTYP, WIDTH_TRANTYP, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new DataColumn(COLUMN_AMOUNT, WIDTH_AMOUNT, theDecimalRenderer, theMoneyEditor));
            addColumn(new DataColumn(COLUMN_DEBIT, WIDTH_DEBIT, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_CREDIT, WIDTH_CREDIT, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_UNITS, WIDTH_UNITS, theDecimalRenderer, theUnitsEditor));
            addColumn(new DataColumn(COLUMN_DILUTE, WIDTH_DILUTE, theDecimalRenderer, theDiluteEditor));
            addColumn(new DataColumn(COLUMN_TAXCRED, WIDTH_TAXCRED, theDecimalRenderer, theMoneyEditor));
            addColumn(new DataColumn(COLUMN_YEARS, WIDTH_YEARS, theIntegerRenderer, theIntegerEditor));
        }

        /**
         * Set the date editor range.
         * @param pRange the range
         */
        private void setDateEditorRange(final DateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }
    }
}
