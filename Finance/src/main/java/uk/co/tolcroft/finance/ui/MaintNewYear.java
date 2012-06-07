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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.data.TaxYear.TaxYearList;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.ui.Renderer.StringRenderer;
import uk.co.tolcroft.models.ui.StdInterfaces.stdCommand;
import uk.co.tolcroft.models.views.ViewList;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class MaintNewYear extends DataTable<Event> implements ActionListener {
    private static final long serialVersionUID = 7406051901546832781L;

    private MaintNewYear theTable = this;
    private View theView = null;
    private TaxYearList theTaxYears = null;
    private EventList theEvents = null;
    private MaintenanceTab theParent = null;
    private JPanel thePanel = null;
    private YearColumnModel theColumns = null;
    private PatternYearModel theModel = null;
    private JButton thePattern = null;
    private JDataEntry theDataYear = null;
    private JDataEntry theDataEvents = null;
    private ViewList theViewSet = null;
    private ListClass theYearView = null;
    private ListClass theEventView = null;

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
        return theDataYear;
    }

    /* Table headers */
    private static final String titleDate = "Date";
    private static final String titleDesc = "Description";
    private static final String titleTrans = "TransactionType";
    private static final String titleAmount = "Amount";
    private static final String titleDebit = "Debit";
    private static final String titleCredit = "Credit";

    /* Table columns */
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_DESC = 1;
    private static final int COLUMN_TRANTYP = 2;
    private static final int COLUMN_AMOUNT = 3;
    private static final int COLUMN_DEBIT = 4;
    private static final int COLUMN_CREDIT = 5;

    /**
     * Constructor for New Year Window
     * @param pParent the parent window
     */
    public MaintNewYear(MaintenanceTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataManager());

        /* Declare variables */
        GroupLayout myLayout;

        /* Record the passed details */
        theParent = pParent;
        theView = pParent.getView();

        /* Build the View set and List */
        theViewSet = new ViewList(theView);
        theYearView = theViewSet.registerClass(TaxYear.class);
        theEventView = theViewSet.registerClass(Event.class);

        /* Set the table model */
        theModel = new PatternYearModel();

        /* Set the table model */
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new YearColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(800, 200));

        /* Create the button */
        thePattern = new JButton("Apply");
        thePattern.addActionListener(this);

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
                                                    .addComponent(getScrollPane(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 800,
                                                                  Short.MAX_VALUE).addComponent(thePattern))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(getScrollPane())
                                  .addComponent(thePattern)));

        /* Create the debug entry, attach to MaintenanceDebug entry */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry myEntry = myDataMgr.new JDataEntry("NewYear");
        myEntry.addAsChildOf(pParent.getDataEntry());
        theDataYear = myDataMgr.new JDataEntry("Year");
        theDataYear.addAsChildOf(myEntry);
        theDataEvents = myDataMgr.new JDataEntry("Events");
        theDataEvents.addAsChildOf(myEntry);
    }

    /* Stubs */
    @Override
    public void saveData() {
    }

    @Override
    public void notifyChanges() {
    }

    @Override
    public boolean hasUpdates() {
        return false;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public EditState getEditState() {
        return EditState.CLEAN;
    }

    @Override
    public void performCommand(stdCommand pCmd) {
    }

    @Override
    public void notifySelection(Object pObj) {
    }

    @Override
    public void lockOnError(boolean isError) {
    }

    /**
     * Refresh views/controls after a load/update of underlying data
     * @throws JDataException
     */
    public void refreshData() throws JDataException {
        FinanceData myData = theView.getData();
        TaxYear.TaxYearList myList = myData.getTaxYears();
        DataListIterator<TaxYear> myIterator;

        myIterator = myList.listIterator();
        // theYear = myIterator.peekLast();
        setSelection(myIterator.peekLast());
    }

    /**
     * Set Selection
     * @param pTaxYear the last active tax year
     * @throws JDataException
     */
    public void setSelection(TaxYear pTaxYear) throws JDataException {
        theTaxYears = null;
        theEvents = null;
        thePattern.setVisible(false);
        if (pTaxYear != null) {
            FinanceData myData = theView.getData();
            theTaxYears = myData.getTaxYears().getNewEditList();
            theEvents = myData.getEvents().getEditList(theTaxYears.getNewYear());
            thePattern.setVisible(true);
            thePattern.setEnabled(!theEvents.isEmpty());
        }
        setList(theEvents);
        theYearView.setDataList(theTaxYears);
        theEventView.setDataList(theEvents);
        theDataYear.setObject(theTaxYears);
        theDataEvents.setObject(theEvents);
        theParent.setVisibility();
    }

    /**
     * Perform actions for controls/pop-ups on this table
     * @param evt the event
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        /* If this event relates to the pattern button */
        if (evt.getSource() == (Object) thePattern) {
            /* Apply the extract changes */
            theViewSet.applyChanges();
        }
    }

    /* PatternYear table model */
    public class PatternYearModel extends DataTableModel {
        private static final long serialVersionUID = 4796112294536415723L;

        /**
         * Constructor
         */
        private PatternYearModel() {
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
            return (theEvents == null) ? 0 : theEvents.size();
        }

        /**
         * Get the name of the column
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(int col) {
            switch (col) {
                case COLUMN_DATE:
                    return titleDate;
                case COLUMN_DESC:
                    return titleDesc;
                case COLUMN_TRANTYP:
                    return titleTrans;
                case COLUMN_AMOUNT:
                    return titleAmount;
                case COLUMN_CREDIT:
                    return titleCredit;
                case COLUMN_DEBIT:
                    return titleDebit;
                default:
                    return null;
            }
        }

        /**
         * Obtain the Field id associated with the column
         * @param row the row
         * @param column the column
         */
        @Override
        public JDataField getFieldForCell(int row,
                                          int column) {
            /* Switch on column */
            switch (column) {
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
         * Is the cell at (row, col) editable
         */
        @Override
        public boolean isCellEditable(int row,
                                      int col) {
            return false;
        }

        /**
         * Get the value at (row, col)
         * @return the object value
         */
        @Override
        public Object getValueAt(int row,
                                 int col) {
            Event myEvent;
            Object o;

            /* Access the event */
            myEvent = theEvents.get(row);

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_DATE:
                    o = myEvent.getDate();
                    break;
                case COLUMN_TRANTYP:
                    o = (myEvent.getTransType() == null) ? null : myEvent.getTransType().getName();
                    break;
                case COLUMN_CREDIT:
                    o = (myEvent.getCredit() == null) ? null : myEvent.getCredit().getName();
                    break;
                case COLUMN_DEBIT:
                    o = (myEvent.getDebit() == null) ? null : myEvent.getDebit().getName();
                    break;
                case COLUMN_AMOUNT:
                    o = myEvent.getAmount();
                    break;
                case COLUMN_DESC:
                    o = myEvent.getDesc();
                    if ((o != null) & (((String) o).length() == 0))
                        o = null;
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myEvent.hasErrors(getFieldForCell(row, col))))
                o = Renderer.getError();

            /* Return to caller */
            return o;
        }
    }

    /**
     * Column Model class
     */
    private class YearColumnModel extends DataColumnModel {
        private static final long serialVersionUID = -894489367275603586L;

        /* Renderers/Editors */
        private CalendarRenderer theDateRenderer = null;
        private DecimalRenderer theDecimalRenderer = null;
        private StringRenderer theStringRenderer = null;

        /**
         * Constructor
         */
        private YearColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDecimalRenderer = new DecimalRenderer();
            theStringRenderer = new StringRenderer();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_DATE, 80, theDateRenderer, null));
            addColumn(new DataColumn(COLUMN_DESC, 150, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_TRANTYP, 110, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_AMOUNT, 90, theDecimalRenderer, null));
            addColumn(new DataColumn(COLUMN_DEBIT, 130, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_CREDIT, 130, theStringRenderer, null));
        }
    }
}
