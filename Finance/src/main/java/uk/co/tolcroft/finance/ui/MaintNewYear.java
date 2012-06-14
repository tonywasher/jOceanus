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

/**
 * NewYear maintenance panel.
 * @author Tony Washer
 */
public class MaintNewYear extends DataTable<Event> implements ActionListener {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7406051901546832781L;

    /**
     * Self reference.
     */
    private final MaintNewYear theTable = this;

    /**
     * The Data View.
     */
    private final View theView;

    /**
     * The events.
     */
    private EventList theEvents = null;

    /**
     * The parent.
     */
    private final MaintenanceTab theParent;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * Column Model.
     */
    private final YearColumnModel theColumns;

    /**
     * Pattern button.
     */
    private final JButton thePattern;

    /**
     * Data Year.
     */
    private final JDataEntry theDataYear;

    /**
     * Data Events.
     */
    private final JDataEntry theDataEvents;

    /**
     * View Set.
     */
    private final ViewList theViewSet;

    /**
     * Year View.
     */
    private final ListClass theYearView;

    /**
     * Event View.
     */
    private final ListClass theEventView;

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
        return theDataYear;
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
     * Transaction Type column title.
     */
    private static final String TITLE_TRANTYPE = "TransactionType";

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
     * Date column id.
     */
    private static final int COLUMN_DATE = 0;

    /**
     * Description column id.
     */
    private static final int COLUMN_DESC = 1;

    /**
     * Transaction Type column id.
     */
    private static final int COLUMN_TRANTYP = 2;

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
     * Date column width.
     */
    private static final int WIDTH_DATE = 80;

    /**
     * Description column width.
     */
    private static final int WIDTH_DESC = 150;

    /**
     * Transaction Type column width.
     */
    private static final int WIDTH_TRANTYP = 110;

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
     * Panel width.
     */
    private static final int PANEL_WIDTH = 800;

    /**
     * Panel height.
     */
    private static final int PANEL_HEIGHT = 200;

    /**
     * Constructor for New Year Window.
     * @param pParent the parent window
     */
    public MaintNewYear(final MaintenanceTab pParent) {
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
        PatternYearModel myModel = new PatternYearModel();
        setModel(myModel);

        /* Create the data column model and declare it */
        theColumns = new YearColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

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
                                                                  GroupLayout.DEFAULT_SIZE, PANEL_WIDTH,
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
    public void performCommand(final stdCommand pCmd) {
    }

    @Override
    public void notifySelection(final Object pObj) {
    }

    @Override
    public void lockOnError(final boolean isError) {
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JDataException on error
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
     * Set Selection.
     * @param pTaxYear the last active tax year
     * @throws JDataException on error
     */
    public void setSelection(final TaxYear pTaxYear) throws JDataException {
        TaxYearList myTaxYears = null;
        theEvents = null;
        thePattern.setVisible(false);
        if (pTaxYear != null) {
            FinanceData myData = theView.getData();
            myTaxYears = myData.getTaxYears().getNewEditList();
            theEvents = myData.getEvents().getEditList(myTaxYears.getNewYear());
            thePattern.setVisible(true);
            thePattern.setEnabled(!theEvents.isEmpty());
        }
        setList(theEvents);
        theYearView.setDataList(myTaxYears);
        theEventView.setDataList(theEvents);
        theDataYear.setObject(myTaxYears);
        theDataEvents.setObject(theEvents);
        theParent.setVisibility();
    }

    /**
     * Perform actions for controls/pop-ups on this table.
     * @param evt the event
     */
    @Override
    public void actionPerformed(final ActionEvent evt) {
        /* If this event relates to the pattern button */
        if (thePattern.equals(evt.getSource())) {
            /* Apply the extract changes */
            theViewSet.applyChanges();
        }
    }

    /**
     * PatternYear table model.
     */
    public final class PatternYearModel extends DataTableModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 4796112294536415723L;

        /**
         * Constructor.
         */
        private PatternYearModel() {
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
                    return TITLE_TRANTYPE;
                case COLUMN_AMOUNT:
                    return TITLE_AMOUNT;
                case COLUMN_CREDIT:
                    return TITLE_CREDIT;
                case COLUMN_DEBIT:
                    return TITLE_DEBIT;
                default:
                    return null;
            }
        }

        /**
         * Obtain the Field id associated with the column.
         * @param row the row
         * @param column the column
         * @return the field id
         */
        @Override
        public JDataField getFieldForCell(final int row,
                                          final int column) {
            /* Switch on column */
            switch (column) {
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
         * Is the cell at (row, col) editable?
         * @param row the row
         * @param col the column
         * @return true/false
         */
        @Override
        public boolean isCellEditable(final int row,
                                      final int col) {
            return false;
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
                    if ((o != null) & (((String) o).length() == 0)) {
                        o = null;
                    }
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myEvent.hasErrors(getFieldForCell(row, col)))) {
                o = Renderer.getError();
            }

            /* Return to caller */
            return o;
        }
    }

    /**
     * Column Model class.
     */
    private final class YearColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -894489367275603586L;

        /**
         * Date Renderer.
         */
        private final CalendarRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalRenderer theDecimalRenderer;

        /**
         * String Renderer.
         */
        private final StringRenderer theStringRenderer;

        /**
         * Constructor.
         */
        private YearColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDecimalRenderer = new DecimalRenderer();
            theStringRenderer = new StringRenderer();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, null));
            addColumn(new DataColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_TRANTYP, WIDTH_TRANTYP, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_AMOUNT, WIDTH_AMOUNT, theDecimalRenderer, null));
            addColumn(new DataColumn(COLUMN_DEBIT, WIDTH_DEBIT, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_CREDIT, WIDTH_CREDIT, theStringRenderer, null));
        }
    }
}
