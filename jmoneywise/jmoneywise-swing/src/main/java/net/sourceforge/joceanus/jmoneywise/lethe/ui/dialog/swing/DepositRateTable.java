/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldCalendarCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldRateCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldCalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldDecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusAction;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Panel to display a list of DepositRates associated with a Deposit.
 */
public class DepositRateTable
        extends PrometheusDataTable<DepositRate, MoneyWiseDataType> {
    /**
     * Rate Column Title.
     */
    private static final String TITLE_RATE = DepositRate.FIELD_RATE.getName();

    /**
     * Bonus Column Title.
     */
    private static final String TITLE_BONUS = DepositRate.FIELD_BONUS.getName();

    /**
     * EndDate Column Title.
     */
    private static final String TITLE_ENDDATE = DepositRate.FIELD_ENDDATE.getName();

    /**
     * Action Column Title.
     */
    private static final String TITLE_ACTION = MoneyWiseUIResource.COLUMN_ACTION.getValue();

    /**
     * The field manager.
     */
    private final MetisSwingFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The error panel.
     */
    private final MetisErrorPanel<JComponent, Icon> theError;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The Table Model.
     */
    private final DepositRateTableModel theModel;

    /**
     * The Column Model.
     */
    private final DepositRateColumnModel theColumns;

    /**
     * Date Range.
     */
    private TethysDateRange theRange;

    /**
     * Rate Header.
     */
    private DepositRate theHeader;

    /**
     * Deposit.
     */
    private Deposit theDeposit;

    /**
     * DepositRates.
     */
    private DepositRateList theRates;

    /**
     * Editable flag.
     */
    private boolean isEditable;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected DepositRateTable(final TethysSwingGuiFactory pFactory,
                               final MetisSwingFieldManager pFieldMgr,
                               final UpdateSet<MoneyWiseDataType> pUpdateSet,
                               final MetisErrorPanel<JComponent, Icon> pError) {
        /* initialise the underlying class */
        super(pFactory);

        /* Record the passed details */
        theError = pError;
        theFieldMgr = pFieldMgr;
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new DepositRateTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new DepositRateColumnModel(this);
        final JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL >> 1, HEIGHT_PANEL >> 2));

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(super.getNode());
    }

    @Override
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * is the table empty?
     * @return true/false
     */
    protected boolean isViewEmpty() {
        return getTable().getRowCount() == 0;
    }

    /**
     * Refresh data.
     */
    protected void refreshData() {
        /* Access the rates list */
        theRates = theUpdateSet.getDataList(MoneyWiseDataType.DEPOSITRATE, DepositRateList.class);
        theHeader = new RateHeader(theRates);
        theRange = theRates.getDataSet().getDateRange();
        setList(theRates);
    }

    /**
     * Set the deposit.
     * @param pDeposit the deposit
     */
    protected void setDeposit(final Deposit pDeposit) {
        /* Store the deposit */
        if (!MetisDataDifference.isEqual(pDeposit, theDeposit)) {
            theDeposit = pDeposit;
            theModel.fireNewDataEvents();
        }
    }

    /**
     * Set whether the table is editable.
     * @param pEditable true/false
     */
    protected void setEditable(final boolean pEditable) {
        /* Store the value */
        isEditable = pEditable;
        theModel.adjustHeader();
        theColumns.setColumns();
    }

    /**
     * Refresh the table after an updateSet reWind.
     */
    protected void refreshAfterUpdate() {
        theModel.fireNewDataEvents();
    }

    /**
     * Add a new rate for a deposit.
     * @param pDeposit the deposit
     * @throws OceanusException on error
     */
    protected void addNewRate(final Deposit pDeposit) throws OceanusException {
        /* Create the new rate */
        final DepositRate myRate = new DepositRate(theRates);

        /* Set the item value */
        myRate.setDeposit(pDeposit);
        myRate.setRate(TethysRate.getWholePercentage(0));

        /* Access iterator and skip the header */
        final Iterator<DepositRate> myIterator = theModel.viewIterator();
        if (myIterator.hasNext()) {
            myIterator.next();
        }

        /* Access the last rate and look to see whether we have a null date */
        final DepositRate myLast = myIterator.hasNext()
                                                        ? myIterator.next()
                                                        : null;
        final boolean hasNullDate = myLast != null && myLast.getEndDate() == null;

        /* If we have a null date in the last element */
        if (hasNullDate) {
            /* Access the previous element */
            final DepositRate myLatest = myIterator.hasNext()
                                                              ? myIterator.next()
                                                              : null;

            /* Assume that we can use todays date */
            TethysDate myDate = new TethysDate();
            if (myLatest != null) {
                /* Obtain the date that is one after the latest date used */
                final TethysDate myNew = new TethysDate(myLatest.getEndDate());
                myNew.adjustDay(1);

                /* Use the latest of the two dates */
                if (myDate.compareTo(myNew) < 0) {
                    myDate = myNew;
                }
            }

            /* Adjust the last date */
            myLast.pushHistory();
            myLast.setNewVersion();
            myLast.setEndDate(myDate);
        }

        /* Add the new item */
        myRate.setNewVersion();
        theRates.add(myRate);

        /*
         * Don't validate the rate yet. We need to take care such that we can only add a new price
         * when there is a slot available, and that we validate the entire list after an update
         */
    }

    /**
     * JTable Data Model.
     */
    private final class DepositRateTableModel
            extends PrometheusDataTableModel<DepositRate, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 8640120388835333524L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private DepositRateTableModel(final DepositRateTable pTable) {
            /* call constructor */
            super(pTable);
        }

        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                        ? 0
                                        : theColumns.getDeclaredCount();
        }

        @Override
        public int getRowCount() {
            return (theRates == null)
                                      ? 0
                                      : 1 + theRates.size();
        }

        @Override
        public MetisField getFieldForCell(final DepositRate pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final DepositRate pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public DepositRate getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return pRowIndex == 0
                                  ? theHeader
                                  : theRates.get(pRowIndex - 1);
        }

        @Override
        public Object getItemValue(final DepositRate pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return pItem.isHeader()
                                    ? theColumns.getHeaderValue(pColIndex)
                                    : theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final DepositRate pItem,
                                 final int pColIndex,
                                 final Object pValue) throws OceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final DepositRate pRow) {
            /* Ignore deleted rows and all rows if no deposit is selected */
            if (pRow.isDeleted() || theDeposit == null) {
                return false;
            }

            /* Handle filter */
            return pRow.isHeader()
                                   ? isEditable
                                   : theDeposit.equals(pRow.getDeposit());
        }

        /**
         * Adjust header.
         */
        private void adjustHeader() {
            if (theDeposit != null) {
                fireUpdateRowEvents(0);
            }
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Add a new rate */
                addNewRate(theDeposit);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                final OceanusException myError = new MoneyWiseDataException("Failed to create new rate", e);

                /* Show the error */
                setError(myError);
                return;
            }

            /* notify that the row has been inserted */
            final int myRow = theModel.getRowCount() - 1;
            theModel.fireTableRowsInserted(myRow, myRow);

            /* Shift display to line */
            selectRowWithScroll(1);
            incrementVersion();
            notifyChanges();
        }
    }

    /**
     * Column Model class.
     */
    private final class DepositRateColumnModel
            extends PrometheusDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6629043017566713861L;

        /**
         * Rate column id.
         */
        private static final int COLUMN_RATE = 0;

        /**
         * Bonus column id.
         */
        private static final int COLUMN_BONUS = 1;

        /**
         * EndDate column id.
         */
        private static final int COLUMN_ENDDATE = 2;

        /**
         * Action column id.
         */
        private static final int COLUMN_ACTION = 3;

        /**
         * Action column.
         */
        private final PrometheusDataTableColumn theActionColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private DepositRateColumnModel(final DepositRateTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            final MetisFieldRateCellEditor myRateEditor = theFieldMgr.allocateRateCellEditor();
            final MetisFieldCalendarCellEditor myDateEditor = theFieldMgr.allocateCalendarCellEditor();
            final MetisFieldIconButtonCellEditor<PrometheusAction> myActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(PrometheusAction.class);
            final MetisFieldCalendarCellRenderer myDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            final MetisFieldDecimalCellRenderer myDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            final MetisFieldIconButtonCellRenderer<PrometheusAction> myActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(PrometheusAction.class);

            /* Configure the iconButton */
            final TethysIconMapSet<PrometheusAction> myActionMapSet = PrometheusIcon.configureStatusIconButton();
            myActionIconRenderer.setIconMapSet(r -> myActionMapSet);
            myActionIconEditor.setIconMapSet(r -> myActionMapSet);

            /* Create the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_RATE, WIDTH_RATE, myDecimalRenderer, myRateEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_BONUS, WIDTH_RATE, myDecimalRenderer, myRateEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_ENDDATE, WIDTH_DATE, myDateRenderer, myDateEditor));
            theActionColumn = new PrometheusDataTableColumn(COLUMN_ACTION, WIDTH_ICON, myActionIconRenderer, myActionIconEditor);
            declareColumn(theActionColumn);

            /* Initialise the columns */
            setColumns();

            /* Add configurator */
            myDateEditor.setDateConfigurator(this::handleDateEvent);
        }

        /**
         * handle Date event.
         * @param pRowIndex the rowIndex
         * @param pConfig the dateConfig
         */
        private void handleDateEvent(final Integer pRowIndex,
                                     final TethysDateConfig pConfig) {
            /* Determine whether this is the latest entry */
            final int myCurrRow = getTable().convertRowIndexToView(pRowIndex);
            final DepositRate myCurrRate = theModel.getItemAtIndex(pRowIndex);
            final boolean bAllowNull = myCurrRow == 1;
            pConfig.setAllowNullDateSelection(bAllowNull);

            /* Set earliest date */
            pConfig.setEarliestDate(theRange == null
                                                     ? null
                                                     : theRange.getStart());
            pConfig.setLatestDate(null);

            /* Loop through the viewable items */
            final List<TethysDate> myActive = new ArrayList<>();
            final Iterator<DepositRate> myIterator = theModel.viewIterator();
            while (myIterator.hasNext()) {
                final DepositRate myRate = myIterator.next();

                /* Ignore the header and the current row */
                if (myRate instanceof RateHeader
                    || myRate.equals(myCurrRate)) {
                    continue;
                }

                /* Add the date to the list */
                final TethysDate myDate = myRate.getEndDate();
                if (myDate != null) {
                    myActive.add(myDate);
                }
            }

            /* Set the allowed dates */
            pConfig.setAllowed(d -> !myActive.contains(d));
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on mode */
            if (isEditable) {
                revealColumn(theActionColumn);
            } else {
                hideColumn(theActionColumn);
            }
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_RATE:
                    return TITLE_RATE;
                case COLUMN_BONUS:
                    return TITLE_BONUS;
                case COLUMN_ENDDATE:
                    return TITLE_ENDDATE;
                case COLUMN_ACTION:
                    return TITLE_ACTION;
                default:
                    return null;
            }
        }

        /**
         * Obtain the header value for the column.
         * @param pColIndex column index
         * @return the value
         */
        private Object getHeaderValue(final int pColIndex) {
            return pColIndex == COLUMN_ACTION
                                              ? PrometheusAction.INSERT
                                              : null;
        }

        /**
         * Obtain the value for the depositRate column.
         * @param pItem the depositRate
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final DepositRate pItem,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_RATE:
                    return pItem.getRate();
                case COLUMN_BONUS:
                    return pItem.getBonus();
                case COLUMN_ENDDATE:
                    return pItem.getEndDate();
                case COLUMN_ACTION:
                    return PrometheusAction.DELETE;
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws OceanusException on error
         */
        private void setItemValue(final DepositRate pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_RATE:
                    pItem.setRate((TethysRate) pValue);
                    break;
                case COLUMN_BONUS:
                    pItem.setBonus((TethysRate) pValue);
                    break;
                case COLUMN_ENDDATE:
                    pItem.setEndDate((TethysDate) pValue);
                    break;
                case COLUMN_ACTION:
                    if (pItem.isHeader()) {
                        theModel.addNewItem();
                    } else {
                        pItem.setDeleted(true);
                    }
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the cell editable?
         * @param pRate the rate
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final DepositRate pRate,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_RATE:
                case COLUMN_BONUS:
                case COLUMN_ENDDATE:
                    return isEditable && !pRate.isHeader();
                case COLUMN_ACTION:
                    return isEditable;
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        protected MetisField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_RATE:
                    return DepositRate.FIELD_RATE;
                case COLUMN_BONUS:
                    return DepositRate.FIELD_BONUS;
                case COLUMN_ENDDATE:
                    return DepositRate.FIELD_ENDDATE;
                default:
                    return null;
            }
        }
    }

    /**
     * Rate Header class.
     */
    private static class RateHeader
            extends DepositRate {
        /**
         * Constructor.
         * @param pList the DepositRate list
         */
        protected RateHeader(final DepositRateList pList) {
            super(pList);
            setHeader(true);
        }
    }
}
