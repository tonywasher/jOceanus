/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.ui.ErrorPanel;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTable;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableColumn;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableModel;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableMouse;
import net.sourceforge.joceanus.jdatamodels.ui.SaveButtons;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jdatamodels.views.UpdateEntry;
import net.sourceforge.joceanus.jdatamodels.views.UpdateSet;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jeventmanager.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.ComboBoxCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.DilutionCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.IntegerCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.MoneyCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.UnitsCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.IntegerCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldManager;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.EventInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.MainTab.ActionRequest;
import net.sourceforge.joceanus.jmoneywise.ui.controls.ComboSelect;
import net.sourceforge.joceanus.jmoneywise.views.View;

/**
 * Event Register Table.
 * @author Tony Washer
 */
public class Register
        extends JDataTable<Event> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5531752729052421790L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Register.class.getName());

    /**
     * Date column title.
     */
    protected static final String TITLE_DATE = NLS_BUNDLE.getString("TitleDate");

    /**
     * Description column title.
     */
    protected static final String TITLE_DESC = NLS_BUNDLE.getString("TitleDesc");

    /**
     * CategoryType column title.
     */
    protected static final String TITLE_CATEGORY = NLS_BUNDLE.getString("TitleCategory");

    /**
     * Amount column title.
     */
    protected static final String TITLE_AMOUNT = NLS_BUNDLE.getString("TitleAmount");

    /**
     * Debit column title.
     */
    protected static final String TITLE_DEBIT = NLS_BUNDLE.getString("TitleDebit");

    /**
     * Credit column title.
     */
    protected static final String TITLE_CREDIT = NLS_BUNDLE.getString("TitleCredit");

    /**
     * DebitUnits column title.
     */
    protected static final String TITLE_DEBUNITS = NLS_BUNDLE.getString("TitleDebitUnits");

    /**
     * CreditUnits column title.
     */
    protected static final String TITLE_CREDUNITS = NLS_BUNDLE.getString("TitleCreditUnits");

    /**
     * Dilution column title.
     */
    protected static final String TITLE_DILUTE = NLS_BUNDLE.getString("TitleDilution");

    /**
     * TaxCredit column title.
     */
    protected static final String TITLE_TAXCRED = NLS_BUNDLE.getString("TitleTax");

    /**
     * Years column title.
     */
    protected static final String TITLE_YEARS = NLS_BUNDLE.getString("TitleYears");

    /**
     * PopUp viewAccount.
     */
    private static final String POPUP_VIEW = NLS_BUNDLE.getString("PopUpViewAccount");

    /**
     * PopUp maintAccount.
     */
    private static final String POPUP_MAINT = NLS_BUNDLE.getString("PopUpMaintAccount");

    /**
     * PopUp nullDebitUnits.
     */
    protected static final String POPUP_NULLDEBUNITS = NLS_BUNDLE.getString("PopUpNullDebitUnits");

    /**
     * PopUp nullCreditUnits.
     */
    protected static final String POPUP_NULLCREDUNITS = NLS_BUNDLE.getString("PopUpNullCreditUnits");

    /**
     * PopUp nullTaxCredit.
     */
    protected static final String POPUP_NULLTAX = NLS_BUNDLE.getString("PopUpNullTax");

    /**
     * PopUp nullYears.
     */
    protected static final String POPUP_NULLYEARS = NLS_BUNDLE.getString("PopUpNullYears");

    /**
     * PopUp nullDilution.
     */
    protected static final String POPUP_NULLDILUTE = NLS_BUNDLE.getString("PopUpNullDilute");

    /**
     * PopUp calcTax.
     */
    protected static final String POPUP_CALCTAX = NLS_BUNDLE.getString("PopUpCalcTax");

    /**
     * Data View.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * Update Set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * Event Update Entry.
     */
    private final transient UpdateEntry<Event> theEventEntry;

    /**
     * EventInfo Update Entry.
     */
    private final transient UpdateEntry<EventInfo> theInfoEntry;

    /**
     * Table Model.
     */
    private final RegisterModel theModel;

    /**
     * Events.
     */
    private transient EventList theEvents = null;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * Self Reference.
     */
    private final Register theTable = this;

    /**
     * Column Model.
     */
    private final RegisterColumnModel theColumns;

    /**
     * Selected range.
     */
    private transient JDateDayRange theRange = null;

    /**
     * Range selection panel.
     */
    private JDateDayRangeSelect theSelect = null;

    /**
     * Save Buttons.
     */
    private final SaveButtons theSaveButtons;

    /**
     * Data Entry.
     */
    private final transient JDataEntry theDataRegister;

    /**
     * Error Panel.
     */
    private final ErrorPanel theError;

    /**
     * ComboList.
     */
    private final transient ComboSelect theComboList;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    protected void setError(final JDataException pError) {
        theError.addError(pError);
    }

    /**
     * Date column id.
     */
    private static final int COLUMN_DATE = 0;

    /**
     * Category column id.
     */
    private static final int COLUMN_CATEGORY = 1;

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
     * DebitUnits column id.
     */
    private static final int COLUMN_DEBUNITS = 6;

    /**
     * CreditUnits column id.
     */
    private static final int COLUMN_CREDUNITS = 7;

    /**
     * Dilution column id.
     */
    private static final int COLUMN_DILUTE = 8;

    /**
     * Tax Credit column id.
     */
    private static final int COLUMN_TAXCRED = 9;

    /**
     * Years column id.
     */
    private static final int COLUMN_YEARS = 10;

    /**
     * Date column width.
     */
    private static final int WIDTH_DATE = 90;

    /**
     * Category column width.
     */
    private static final int WIDTH_CATEGORY = 110;

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
    private static final int PANEL_WIDTH = 980;

    /**
     * Panel height.
     */
    private static final int PANEL_HEIGHT = 200;

    /**
     * Constructor for Register Window.
     * @param pView the data view
     * @param pCombo the combo manager
     */
    public Register(final View pView,
                    final ComboSelect pCombo) {
        /* Record the passed details */
        theView = pView;
        theComboList = pCombo;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet(theView);
        theEventEntry = theUpdateSet.registerClass(Event.class);
        theInfoEntry = theUpdateSet.registerClass(EventInfo.class);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_EDIT);
        theDataRegister = myDataMgr.new JDataEntry(Register.class.getSimpleName());
        theDataRegister.addAsChildOf(mySection);

        /* Create the model and declare it to our superclass */
        theModel = new RegisterModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new RegisterColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        /* Add the mouse listener */
        RegisterMouse myMouse = new RegisterMouse();
        addMouseListener(myMouse);

        /* Create the sub panels */
        theSelect = new JDateDayRangeSelect(false);
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataRegister);

        /* Create listener */
        RegisterListener myListener = new RegisterListener();
        theSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, myListener);
        theError.addChangeListener(myListener);
        theSaveButtons.addActionListener(myListener);
        theUpdateSet.addActionListener(myListener);
        theView.addChangeListener(myListener);

        /* Create the panel */
        thePanel = new JEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theError);
        thePanel.add(theSelect);
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
        theDataRegister.setFocus();
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Protect against exceptions */
        try {
            /* Access range */
            JDateDayRange myRange = theView.getRange();
            theSelect.setOverallRange(myRange);
            theRange = theSelect.getRange();
            setSelection(theRange);

            /* Create SavePoint */
            theSelect.createSavePoint();

            /* Touch the updateSet */
            theDataRegister.setObject(theUpdateSet);
        } catch (JDataException e) {
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
        /* Find the edit state */
        if (theEvents != null) {
            theEvents.findEditState();
        }

        /* Update the table buttons */
        theSaveButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Update the top level tabs */
        fireStateChanged();
    }

    /**
     * Set Selection to the specified date range.
     * @param pRange the Date range for the extract
     * @throws JDataException on error
     */
    public void setSelection(final JDateDayRange pRange) throws JDataException {
        theRange = pRange;
        theEvents = null;
        EventInfoList myInfo = null;
        if (theRange != null) {
            /* Get the Rates edit list */
            FinanceData myData = theView.getData();
            EventList myEvents = myData.getEvents();
            theEvents = myEvents.deriveEditList(pRange);
            myInfo = theEvents.getEventInfo();

            theColumns.setDateEditorRange(theRange);
        }
        setList(theEvents);
        theEventEntry.setDataList(theEvents);
        theInfoEntry.setDataList(myInfo);
        theSaveButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());
        fireStateChanged();

        /* Touch the updateSet */
        theDataRegister.setObject(theUpdateSet);
    }

    /**
     * Set selection to the period designated by the referenced control.
     * @param pSource the source control
     */
    public void selectPeriod(final JDateDayRangeSelect pSource) {
        /* Protect against exceptions */
        try {
            /* Adjust the period selection (this will not call back) */
            theSelect.setSelection(pSource);

            /* Utilise the selection */
            setSelection(theSelect.getRange());

            /* Catch exceptions */
        } catch (JDataException e) {
            /* Build the error */
            JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to select Range", e);

            /* Show the error */
            setError(myError);

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
    public JComboBox<?> getComboBox(final int row,
                                    final int column) {
        /* Access the event */
        Event myEvent = theEvents.get(row);

        /* Switch on column */
        switch (column) {
            case COLUMN_CATEGORY:
                return theComboList.getAllCategories();
            case COLUMN_CREDIT:
                return theComboList.getCreditAccounts(myEvent.getCategory(), myEvent.getDebit());
            case COLUMN_DEBIT:
                return theComboList.getDebitAccounts(myEvent.getCategory());
            default:
                return null;
        }
    }

    /**
     * Register listener class.
     */
    private final class RegisterListener
            implements ActionListener, PropertyChangeListener, ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent e) {
            Object o = e.getSource();

            /* If this is the error panel */
            if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelect.setVisible(!isError);

                /* Lock scroll area */
                getScrollPane().setEnabled(!isError);

                /* Lock Save Buttons */
                theSaveButtons.setEnabled(!isError);

                /* If this is the data view */
            } else if (theView.equals(o)) {
                /* Refresh Data */
                refreshData();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            /* If this is the range select panel */
            if (theSelect.equals(evt.getSource())) {
                /* Protect against exceptions */
                try {
                    /* Set the new range */
                    setSelection(theSelect.getRange());

                    /* Create SavePoint */
                    theSelect.createSavePoint();

                    /* Catch Exceptions */
                } catch (JDataException e) {
                    /* Build the error */
                    JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to change selection", e);

                    /* Show the error */
                    setError(myError);

                    /* Restore SavePoint */
                    theSelect.restoreSavePoint();
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If this event relates to the save buttons */
            if (theSaveButtons.equals(o)) {
                /* Cancel any editing */
                cancelEditing();

                /* Perform the command */
                theUpdateSet.processCommand(e.getActionCommand(), theError);

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
     * Register table model.
     */
    public final class RegisterModel
            extends JDataTableModel<Event> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 7997087757206121152L;

        /**
         * Constructor.
         */
        private RegisterModel() {
            /* call constructor */
            super(theTable);
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
            return (theEvents == null)
                    ? 0
                    : theEvents.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DATE:
                    return TITLE_DATE;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_CATEGORY:
                    return TITLE_CATEGORY;
                case COLUMN_AMOUNT:
                    return TITLE_AMOUNT;
                case COLUMN_CREDIT:
                    return TITLE_CREDIT;
                case COLUMN_DEBIT:
                    return TITLE_DEBIT;
                case COLUMN_DEBUNITS:
                    return TITLE_DEBUNITS;
                case COLUMN_CREDUNITS:
                    return TITLE_CREDUNITS;
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

        @Override
        public Class<?> getColumnClass(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DESC:
                    return String.class;
                case COLUMN_CATEGORY:
                    return String.class;
                case COLUMN_CREDIT:
                    return String.class;
                case COLUMN_DEBIT:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public Event getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theEvents.get(pRowIndex);
        }

        @Override
        public JDataField getFieldForCell(final Event pEvent,
                                          final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return Event.FIELD_DATE;
                case COLUMN_DESC:
                    return EventInfoSet.getFieldForClass(EventInfoClass.Comments);
                case COLUMN_CATEGORY:
                    return Event.FIELD_CATEGORY;
                case COLUMN_AMOUNT:
                    return Event.FIELD_AMOUNT;
                case COLUMN_CREDIT:
                    return Event.FIELD_CREDIT;
                case COLUMN_DEBIT:
                    return Event.FIELD_DEBIT;
                case COLUMN_DEBUNITS:
                    return EventInfoSet.getFieldForClass(EventInfoClass.DebitUnits);
                case COLUMN_CREDUNITS:
                    return EventInfoSet.getFieldForClass(EventInfoClass.CreditUnits);
                case COLUMN_DILUTE:
                    return EventInfoSet.getFieldForClass(EventInfoClass.Dilution);
                case COLUMN_TAXCRED:
                    return EventInfoSet.getFieldForClass(EventInfoClass.TaxCredit);
                case COLUMN_YEARS:
                    return EventInfoSet.getFieldForClass(EventInfoClass.QualifyYears);
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(final Event pEvent,
                                      final int pColIndex) {
            /* Cannot edit if row is deleted or locked */
            if (pEvent.isDeleted()
                || pEvent.isLocked()) {
                return false;
            }

            /* switch on column */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return true;
                case COLUMN_CATEGORY:
                    return (pEvent.getDate() != null);
                case COLUMN_DESC:
                    return ((pEvent.getDate() != null) && (pEvent.getCategory() != null));
                default:
                    if ((pEvent.getDate() == null)
                        || (pEvent.getComments() == null)
                        || (pEvent.getCategory() == null)) {
                        return false;
                    }
                    switch (pColIndex) {
                        case COLUMN_DEBUNITS:
                            return ((pEvent.getDebit() != null) && (pEvent.getDebit().hasUnits()));
                        case COLUMN_CREDUNITS:
                            return ((pEvent.getCredit() != null) && (pEvent.getCredit().hasUnits()));
                            // case COLUMN_YEARS:
                            // return pEvent.isCategoryClass(EventCategoryClass.TaxableGain);
                        case COLUMN_TAXCRED:
                            return Event.needsTaxCredit(pEvent.getCategory(), pEvent.getDebit());
                        case COLUMN_DILUTE:
                            return Event.needsDilution(pEvent.getCategory());
                        default:
                            return true;
                    }
            }
        }

        @Override
        public Object getItemValue(final Event pEvent,
                                   final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pEvent.getDate();
                case COLUMN_CATEGORY:
                    return pEvent.getCategory();
                case COLUMN_CREDIT:
                    return pEvent.getCredit();
                case COLUMN_DEBIT:
                    return pEvent.getDebit();
                case COLUMN_AMOUNT:
                    return pEvent.getAmount();
                case COLUMN_DILUTE:
                    return pEvent.getDilution();
                case COLUMN_TAXCRED:
                    return pEvent.getTaxCredit();
                case COLUMN_DEBUNITS:
                    return pEvent.getDebitUnits();
                case COLUMN_CREDUNITS:
                    return pEvent.getCreditUnits();
                case COLUMN_YEARS:
                    return pEvent.getYears();
                case COLUMN_DESC:
                    return pEvent.getComments();
                default:
                    return null;
            }
        }

        @Override
        public void setItemValue(final Event pEvent,
                                 final int pColIndex,
                                 final Object pValue) throws JDataException {
            /* Determine whether the line needs a tax credit */
            boolean needsTaxCredit = Event.needsTaxCredit(pEvent.getCategory(), pEvent.getDebit());

            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pEvent.setDate((JDateDay) pValue);
                    break;
                case COLUMN_DESC:
                    pEvent.setComments((String) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pEvent.setCategory((EventCategory) pValue);
                    /* If the need for a tax credit has changed */
                    if (needsTaxCredit != Event.needsTaxCredit(pEvent.getCategory(), pEvent.getDebit())) {
                        /* Determine new Tax Credit */
                        if (needsTaxCredit) {
                            pEvent.setTaxCredit(null);
                        } else {
                            pEvent.setTaxCredit(pEvent.calculateTaxCredit());
                        }
                    }
                    break;
                case COLUMN_AMOUNT:
                    pEvent.setAmount((JMoney) pValue);
                    /* Determine new Tax Credit if required */
                    if (needsTaxCredit) {
                        pEvent.setTaxCredit(pEvent.calculateTaxCredit());
                    }
                    break;
                case COLUMN_DILUTE:
                    pEvent.setDilution((JDilution) pValue);
                    break;
                case COLUMN_TAXCRED:
                    pEvent.setTaxCredit((JMoney) pValue);
                    break;
                case COLUMN_YEARS:
                    pEvent.setYears((Integer) pValue);
                    break;
                case COLUMN_DEBUNITS:
                    pEvent.setDebitUnits((JUnits) pValue);
                    break;
                case COLUMN_CREDUNITS:
                    pEvent.setCreditUnits((JUnits) pValue);
                    break;
                case COLUMN_CREDIT:
                    pEvent.setCredit((Account) pValue);
                    break;
                case COLUMN_DEBIT:
                    pEvent.setDebit((Account) pValue);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Register mouse listener.
     */
    private final class RegisterMouse
            extends JDataTableMouse<Event> {
        /**
         * Constructor.
         */
        private RegisterMouse() {
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
            boolean enableNullDebUnits = false;
            boolean enableNullCredUnits = false;
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
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as event */
                Event myEvent = (Event) myRow;

                /* Enable null Units if we have units */
                if (myEvent.getDebitUnits() != null) {
                    enableNullDebUnits = true;
                }

                /* Enable null CreditUnits if we have units */
                if (myEvent.getCreditUnits() != null) {
                    enableNullCredUnits = true;
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
            boolean nullItem = enableNullDebUnits
                               || enableNullCredUnits
                               || enableNullTax;
            nullItem = nullItem
                       || enableNullYears
                       || enableNullDilution;
            if ((nullItem)
                && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set null debit units */
            if (enableNullDebUnits) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLDEBUNITS);
                myItem.setActionCommand(POPUP_NULLDEBUNITS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null credit units */
            if (enableNullCredUnits) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLCREDUNITS);
                myItem.setActionCommand(POPUP_NULLCREDUNITS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null tax */
            if (enableNullTax) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLTAX);
                myItem.setActionCommand(POPUP_NULLTAX);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null years */
            if (enableNullYears) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLYEARS);
                myItem.setActionCommand(POPUP_NULLYEARS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null dilution */
            if (enableNullDilution) {
                /* Add the null choice */
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
            boolean enableCalcTax = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as event */
                Event myEvent = (Event) myRow;
                JMoney myTax = myEvent.getTaxCredit();
                EventCategory myCat = myEvent.getCategory();

                /* If we have a calculable tax credit that is null/zero */
                boolean isTaxable = ((myCat != null) && ((myEvent.isInterest()) || (myEvent.isDividend())));
                if ((isTaxable)
                    && ((myTax == null) || (!myTax.isNonZero()))) {
                    enableCalcTax = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableCalcTax)
                && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can calculate tax */
            if (enableCalcTax) {
                /* Add the undo change choice */
                JMenuItem myItem = new JMenuItem(POPUP_CALCTAX);
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
            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Access the popUp row/column and ignore if not valid */
            int myRow = getPopupRow();
            int myCol = getPopupCol();
            if (myRow < 0) {
                return;
            }

            /* Access the event */
            Event myEvent = theModel.getItemAtIndex(myRow);

            /* If the column is Credit */
            Account myAccount;
            if (myCol == COLUMN_CREDIT) {
                myAccount = myEvent.getCredit();
            } else if (myCol == COLUMN_DEBIT) {
                myAccount = myEvent.getDebit();
            } else {
                myAccount = null;
            }

            /* If we have an account we can navigate */
            boolean enableNavigate = (myAccount != null);

            /* If there is something to add and there are already items in the menu */
            if ((enableNavigate)
                && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can navigate */
            if (enableNavigate) {
                /* Create the View account choice */
                JMenuItem myItem = new JMenuItem(POPUP_VIEW
                                                 + ": "
                                                 + myAccount.getName());

                /* Set the command and add to menu */
                myItem.setActionCommand(POPUP_VIEW
                                        + ":"
                                        + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);

                /* Create the Maintain account choice */
                myItem = new JMenuItem(POPUP_MAINT
                                       + ": "
                                       + myAccount.getName());

                /* Set the command and add to menu */
                myItem.setActionCommand(POPUP_MAINT
                                        + ":"
                                        + myAccount.getName());
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

            /* If this is a set null debit units command */
            if (myCmd.equals(POPUP_NULLDEBUNITS)) {
                /* Set Units column to null */
                setColumnToNull(COLUMN_DEBUNITS);

                /* If this is a set null credit units command */
            } else if (myCmd.equals(POPUP_NULLCREDUNITS)) {
                /* Set Credit Units column to null */
                setColumnToNull(COLUMN_CREDUNITS);

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
            } else if ((myCmd.startsWith(POPUP_VIEW))
                       || (myCmd.startsWith(POPUP_MAINT))) {
                /* perform the navigation */
                performNavigation(myCmd);

                /* else we do not recognise the action */
            } else {
                /* Pass it to the superclass */
                super.actionPerformed(evt);
                return;
            }

            /* Notify of any changes */
            theModel.fireTableDataChanged();
            notifyChanges();
        }

        /**
         * Calculate tax credits.
         */
        private void calculateTaxCredits() {
            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Determine row */
                int row = myRow.indexOf();
                if (theTable.hasHeader()) {
                    row--;
                }

                /* Access the event */
                Event myEvent = (Event) myRow;
                EventCategory myCat = myEvent.getCategory();
                JMoney myTax = myEvent.getTaxCredit();

                /* Ignore rows with invalid category type */
                if ((myCat == null)
                    || ((!myEvent.isInterest()) && (!myEvent.isDividend()))) {
                    continue;
                }

                /* Ignore rows with tax credit already set */
                if ((myTax != null)
                    && (myTax.isNonZero())) {
                    continue;
                }

                /* Calculate the tax credit */
                myTax = myEvent.calculateTaxCredit();

                /* set the tax credit value */
                theModel.setValueAt(myTax, row, COLUMN_TAXCRED);
            }

            /* Increment version */
            theUpdateSet.incrementVersion();
        }

        /**
         * Perform a navigation command.
         * @param pCmd the navigation command
         */
        private void performNavigation(final String pCmd) {
            /* Access the action command */
            String[] tokens = pCmd.split(":");
            String myCmd = tokens[0];
            String myName = null;
            if (tokens.length > 1) {
                myName = tokens[1];
            }

            /* Access the correct account */
            Account myAccount = theView.getData().getAccounts().findItemByName(myName);

            /* If this is an account view request */
            if (myCmd.compareTo(POPUP_VIEW) == 0) {
                /* Switch view */
                fireActionEvent(MainTab.ACTION_VIEWACCOUNT, new ActionRequest(myAccount, theSelect));

                /* If this is an account maintenance request */
            } else if (myCmd.compareTo(POPUP_MAINT) == 0) {
                /* Switch view */
                fireActionEvent(MainTab.ACTION_MAINTACCOUNT, new ActionRequest(myAccount));
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class RegisterColumnModel
            extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7502445487118370020L;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Date Editor.
         */
        private final CalendarCellEditor theDateEditor;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * Money Editor.
         */
        private final MoneyCellEditor theMoneyEditor;

        /**
         * Units Editor.
         */
        private final UnitsCellEditor theUnitsEditor;

        /**
         * Integer Renderer.
         */
        private final IntegerCellRenderer theIntegerRenderer;

        /**
         * Integer Editor.
         */
        private final IntegerCellEditor theIntegerEditor;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Dilution Editor.
         */
        private final DilutionCellEditor theDiluteEditor;

        /**
         * comboBox Editor.
         */
        private final ComboBoxCellEditor theComboEditor;

        /**
         * Constructor.
         */
        private RegisterColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theMoneyEditor = theFieldMgr.allocateMoneyCellEditor();
            theUnitsEditor = theFieldMgr.allocateUnitsCellEditor();
            theIntegerRenderer = theFieldMgr.allocateIntegerCellRenderer();
            theIntegerEditor = theFieldMgr.allocateIntegerCellEditor();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theDiluteEditor = theFieldMgr.allocateDilutionCellEditor();
            theComboEditor = theFieldMgr.allocateComboBoxCellEditor();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            addColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_CATEGORY, theStringRenderer, theComboEditor));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new JDataTableColumn(COLUMN_AMOUNT, WIDTH_AMOUNT, theDecimalRenderer, theMoneyEditor));
            addColumn(new JDataTableColumn(COLUMN_DEBIT, WIDTH_DEBIT, theStringRenderer, theComboEditor));
            addColumn(new JDataTableColumn(COLUMN_CREDIT, WIDTH_CREDIT, theStringRenderer, theComboEditor));
            addColumn(new JDataTableColumn(COLUMN_DEBUNITS, WIDTH_UNITS, theDecimalRenderer, theUnitsEditor));
            addColumn(new JDataTableColumn(COLUMN_CREDUNITS, WIDTH_UNITS, theDecimalRenderer, theUnitsEditor));
            addColumn(new JDataTableColumn(COLUMN_DILUTE, WIDTH_DILUTE, theDecimalRenderer, theDiluteEditor));
            addColumn(new JDataTableColumn(COLUMN_TAXCRED, WIDTH_TAXCRED, theDecimalRenderer, theMoneyEditor));
            addColumn(new JDataTableColumn(COLUMN_YEARS, WIDTH_YEARS, theIntegerRenderer, theIntegerEditor));
        }

        /**
         * Set the date editor range.
         * @param pRange the range
         */
        private void setDateEditorRange(final JDateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }
    }
}
