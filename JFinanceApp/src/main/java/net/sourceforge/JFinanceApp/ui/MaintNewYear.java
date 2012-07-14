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
package net.sourceforge.JFinanceApp.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataModels.data.EditState;
import net.sourceforge.JDataModels.ui.DataTable;
import net.sourceforge.JDataModels.ui.RenderManager;
import net.sourceforge.JDataModels.ui.Renderer.CalendarRenderer;
import net.sourceforge.JDataModels.ui.Renderer.DecimalRenderer;
import net.sourceforge.JDataModels.ui.Renderer.RendererFieldValue;
import net.sourceforge.JDataModels.ui.Renderer.StringRenderer;
import net.sourceforge.JDataModels.views.DataControl;
import net.sourceforge.JDataModels.views.UpdateSet;
import net.sourceforge.JDataModels.views.UpdateSet.UpdateEntry;
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.Event.EventList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.TaxYear;
import net.sourceforge.JFinanceApp.data.TaxYear.TaxYearList;
import net.sourceforge.JFinanceApp.views.View;
import net.sourceforge.JSortedList.OrderedListIterator;

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
    private final transient View theView;

    /**
     * The render manager.
     */
    private final transient RenderManager theRenderMgr;

    /**
     * The events.
     */
    private transient EventList theEvents = null;

    /**
     * The Data entry.
     */
    private final transient JDataEntry theDataEntry;
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
     * View Set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * Year View.
     */
    private final transient UpdateEntry theYearEntry;

    /**
     * Event View.
     */
    private final transient UpdateEntry theEventEntry;

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
     * Date column title.
     */
    private static final String TITLE_DATE = Extract.TITLE_DATE;

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = Extract.TITLE_DESC;

    /**
     * Transaction Type column title.
     */
    private static final String TITLE_TRANTYPE = Extract.TITLE_TRANS;

    /**
     * Amount column title.
     */
    private static final String TITLE_AMOUNT = Extract.TITLE_AMOUNT;

    /**
     * Debit column title.
     */
    private static final String TITLE_DEBIT = Extract.TITLE_DEBIT;

    /**
     * Credit column title.
     */
    private static final String TITLE_CREDIT = Extract.TITLE_CREDIT;

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
     * @param pView the data view
     */
    public MaintNewYear(final View pView) {
        /* Record the passed details */
        theView = pView;
        theRenderMgr = theView.getRenderMgr();
        setRenderMgr(theRenderMgr);

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet(theView);
        theYearEntry = theUpdateSet.registerClass(TaxYear.class);
        theEventEntry = theUpdateSet.registerClass(Event.class);

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
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(getScrollPane());
        thePanel.add(thePattern);

        /* Create the debug entry, attach to MaintenanceDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry("NewYear");
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataEntry.setFocus();
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

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JDataException on error
     */
    public void refreshData() throws JDataException {
        FinanceData myData = theView.getData();
        TaxYear.TaxYearList myList = myData.getTaxYears();

        OrderedListIterator<TaxYear> myIterator = myList.listIterator();
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
            myTaxYears = myData.getTaxYears().deriveNewEditList();
            theEvents = myData.getEvents().deriveEditList(myTaxYears.getNewYear());
            thePattern.setVisible(true);
            thePattern.setEnabled(!theEvents.isEmpty());
        }
        setList(theEvents);
        theYearEntry.setDataList(myTaxYears);
        theEventEntry.setDataList(theEvents);
        fireStateChanged();
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
            theUpdateSet.applyChanges();
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
            theDateRenderer = theRenderMgr.allocateCalendarRenderer();
            theDecimalRenderer = theRenderMgr.allocateDecimalRenderer();
            theStringRenderer = theRenderMgr.allocateStringRenderer();

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