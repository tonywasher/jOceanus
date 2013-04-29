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
package net.sourceforge.jOceanus.jMoneyWise.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataManager;
import net.sourceforge.jOceanus.jDataManager.JDataManager.JDataEntry;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTable;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTableColumn;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTableModel;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jDataModels.views.UpdateEntry;
import net.sourceforge.jOceanus.jDataModels.views.UpdateSet;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldManager;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.jOceanus.jMoneyWise.views.View;
import net.sourceforge.jOceanus.jSortedList.OrderedListIterator;

/**
 * NewYear maintenance panel.
 * @author Tony Washer
 */
public class MaintNewYear
        extends JDataTable<Event>
        implements ActionListener {
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
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

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
     * Table Model.
     */
    private final PatternYearModel theModel;

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
    private final transient UpdateEntry<TaxYear> theYearEntry;

    /**
     * Year Info View.
     */
    private final transient UpdateEntry<TaxYearInfo> theYearInfoEntry;

    /**
     * Event View.
     */
    private final transient UpdateEntry<Event> theEventEntry;

    /**
     * EventInfor View.
     */
    private final transient UpdateEntry<EventInfo> theEventInfoEntry;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    protected void setError(final JDataException pError) {
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
     * Category column title.
     */
    private static final String TITLE_CATEGORY = Extract.TITLE_CATEGORY;

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
     * Category column id.
     */
    private static final int COLUMN_CATEGORY = 2;

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
     * Category column width.
     */
    private static final int WIDTH_CATEGORY = 110;

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
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet(theView);
        theYearEntry = theUpdateSet.registerClass(TaxYear.class);
        theYearInfoEntry = theUpdateSet.registerClass(TaxYearInfo.class);
        theEventEntry = theUpdateSet.registerClass(Event.class);
        theEventInfoEntry = theUpdateSet.registerClass(EventInfo.class);

        /* Set the table model */
        theModel = new PatternYearModel();
        setModel(theModel);

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
        theUpdateSet.addActionListener(this);

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
     */
    protected void refreshData() {
        /* Protect against exceptions */
        try {
            FinanceData myData = theView.getData();
            TaxYearList myList = myData.getTaxYears();

            OrderedListIterator<TaxYear> myIterator = myList.listIterator();
            setSelection(myIterator.peekLast());
        } catch (JDataException e) {
            /* TODO Show the error */
            // setError(e);
        }
    }

    /**
     * Set Selection.
     * @param pTaxYear the last active tax year
     * @throws JDataException on error
     */
    public void setSelection(final TaxYear pTaxYear) throws JDataException {
        TaxYearList myTaxYears = null;
        TaxInfoList myTaxInfo = null;
        EventInfoList myEventInfo = null;
        theEvents = null;
        thePattern.setVisible(false);
        if (pTaxYear != null) {
            FinanceData myData = theView.getData();
            myTaxYears = myData.getTaxYears().deriveNewEditList();
            myTaxInfo = myTaxYears.getTaxInfo();
            theEvents = myData.getEvents().deriveEditList(myTaxYears.getNewYear());
            myEventInfo = theEvents.getEventInfo();
            thePattern.setVisible(true);
            thePattern.setEnabled(!theEvents.isEmpty());
        }
        setList(theEvents);
        theYearEntry.setDataList(myTaxYears);
        theYearInfoEntry.setDataList(myTaxInfo);
        theEventEntry.setDataList(theEvents);
        theEventInfoEntry.setDataList(myEventInfo);
        fireStateChanged();
    }

    /**
     * Perform actions for controls/pop-ups on this table.
     * @param evt the event
     */
    @Override
    public void actionPerformed(final ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the pattern button */
        if (thePattern.equals(o)) {
            /* Apply the extract changes */
            theUpdateSet.applyChanges();

            /* If we have failed an update */
        } else if (theUpdateSet.equals(o)) {
            /* Refresh the model */
            theModel.fireNewDataEvents();
        }
    }

    /**
     * PatternYear table model.
     */
    public final class PatternYearModel
            extends JDataTableModel<Event> {
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

        @Override
        public Event getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theEvents.get(pRowIndex);
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
                default:
                    return null;
            }
        }

        @Override
        public JDataField getFieldForCell(final Event pEvent,
                                          final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DATE:
                    return Event.FIELD_DATE;
                case COLUMN_DESC:
                    return Event.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return Event.FIELD_CATEGORY;
                case COLUMN_AMOUNT:
                    return Event.FIELD_AMOUNT;
                case COLUMN_CREDIT:
                    return Event.FIELD_CREDIT;
                case COLUMN_DEBIT:
                    return Event.FIELD_DEBIT;
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
        public boolean isCellEditable(final Event pEvent,
                                      final int pColIndex) {
            return false;
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
                case COLUMN_DESC:
                    return pEvent.getDesc();
                default:
                    return null;
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class YearColumnModel
            extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -894489367275603586L;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Constructor.
         */
        private YearColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_CATEGORY, theStringRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_AMOUNT, WIDTH_AMOUNT, theDecimalRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_DEBIT, WIDTH_DEBIT, theStringRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_CREDIT, WIDTH_CREDIT, theStringRenderer, null));
        }
    }
}
