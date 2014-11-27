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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.DateDayCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.RateCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayConfig;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

/**
 * Panel to display a list of DepositRates associated with a Deposit.
 */
public class DepositRateTable
        extends JDataTable<DepositRate, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8477792618183018247L;

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
    private static final String TITLE_ACTION = MoneyWiseUIControlResource.COLUMN_ACTION.getValue();

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The Table Model.
     */
    private final DepositRateTableModel theModel;

    /**
     * The Column Model.
     */
    private final DepositRateColumnModel theColumns;

    /**
     * Rate Header.
     */
    private transient DepositRate theHeader;

    /**
     * Deposit.
     */
    private transient Deposit theDeposit = null;

    /**
     * DepositRates.
     */
    private transient DepositRateList theRates = null;

    /**
     * Editable flag.
     */
    private transient boolean isEditable = false;

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Obtain the panel.
     * @return the panel
     */
    protected JPanel getPanel() {
        return thePanel;
    }

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected DepositRateTable(final JFieldManager pFieldMgr,
                               final UpdateSet<MoneyWiseDataType> pUpdateSet,
                               final ErrorPanel pError) {
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
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL >> 1, HEIGHT_PANEL >> 2));

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(getScrollPane());
    }

    /**
     * Refresh data.
     */
    protected void refreshData() {
        /* Access the rates list */
        theRates = theUpdateSet.getDataList(MoneyWiseDataType.DEPOSITRATE, DepositRateList.class);
        theHeader = new RateHeader(theRates);
        theColumns.setDateRange();
        setList(theRates);
    }

    /**
     * Set the deposit.
     * @param pDeposit the deposit
     */
    protected void setDeposit(final Deposit pDeposit) {
        /* Store the deposit */
        if (!Difference.isEqual(pDeposit, theDeposit)) {
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
     * JTable Data Model.
     */
    private final class DepositRateTableModel
            extends JDataTableModel<DepositRate, MoneyWiseDataType> {
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
        public JDataField getFieldForCell(final DepositRate pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final DepositRate pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pColIndex);
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
                                 final Object pValue) throws JOceanusException {
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
            /* Create the new rate */
            DepositRate myRate = new DepositRate(theRates);

            /* Protect against Exceptions */
            try {
                /* Set the item value */
                myRate.setDeposit(theDeposit);
                myRate.setRate(JRate.getWholePercentage(0));

                /* Handle Exceptions */
            } catch (JOceanusException e) {
                /* Build the error */
                JOceanusException myError = new JMoneyWiseDataException("Failed to create new rate", e);

                /* Show the error */
                setError(myError);
                return;
            }

            /* Obtain latest element */
            Iterator<DepositRate> myIterator = theModel.viewIterator();
            myIterator.next();
            DepositRate myLatest = myIterator.hasNext()
                                                       ? myIterator.next()
                                                       : null;

            /* If we have a latest element with no date */
            if (myLatest != null
                && myLatest.getEndDate() == null) {
                /* Update date to todays date. */
                myLatest.pushHistory();
                myLatest.setNewVersion();
                myLatest.setEndDate(new JDateDay());
            }

            /* Add the new item */
            myRate.setNewVersion();
            theRates.append(myRate);

            /* Validate the new item and notify of the changes */
            myRate.validate();
            theModel.fireNewDataEvents();

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
            extends JDataTableColumnModel<MoneyWiseDataType> {
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
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * Action Icon Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theActionIconRenderer;

        /**
         * Rate editor.
         */
        private final RateCellEditor theRateEditor;

        /**
         * Date editor.
         */
        private final DateDayCellEditor theDateEditor;

        /**
         * Date configuration.
         */
        private final JDateDayConfig theDateConfig;

        /**
         * Action Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theActionIconEditor;

        /**
         * Action column.
         */
        private final JDataTableColumn theActionColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private DepositRateColumnModel(final DepositRateTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theRateEditor = theFieldMgr.allocateRateCellEditor();
            theDateEditor = theFieldMgr.allocateDateDayCellEditor();
            theDateConfig = theDateEditor.getDateConfig();
            theActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theActionIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theActionIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_RATE, WIDTH_RATE, theDecimalRenderer, theRateEditor));
            declareColumn(new JDataTableColumn(COLUMN_BONUS, WIDTH_RATE, theDecimalRenderer, theRateEditor));
            declareColumn(new JDataTableColumn(COLUMN_ENDDATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            theActionColumn = new JDataTableColumn(COLUMN_ACTION, WIDTH_ICON, theActionIconRenderer, theActionIconEditor);
            declareColumn(theActionColumn);

            /* Initialise the columns */
            setColumns();

            /* Add listener */
            new EditorListener();
        }

        /**
         * Adjust date range.
         */
        private void setDateRange() {
            /* Access date range */
            JDateDayRange myRange = theRates.getDataSet().getDateRange();

            /* Adjust editor range */
            theDateConfig.setEarliestDateDay(myRange.getStart());
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
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_ACTION:
                    return ActionType.INSERT;
                default:
                    return null;
            }
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
                    return ActionType.DELETE;
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws JOceanusException on error
         */
        private void setItemValue(final DepositRate pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_RATE:
                    pItem.setRate((JRate) pValue);
                    break;
                case COLUMN_BONUS:
                    pItem.setBonus((JRate) pValue);
                    break;
                case COLUMN_ENDDATE:
                    pItem.setEndDate((JDateDay) pValue);
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
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_RATE:
                case COLUMN_BONUS:
                case COLUMN_ENDDATE:
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
        protected JDataField getFieldForCell(final int pColIndex) {
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

        /**
         * EditorListener.
         */
        private final class EditorListener
                implements ChangeListener {
            /**
             * Constructor.
             */
            private EditorListener() {
                theDateEditor.addChangeListener(this);
            }

            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                /* Access details */
                Point myCell = theDateEditor.getPoint();

                /* Determine whether this is the latest entry */
                int i = convertRowIndexToView(myCell.y);
                boolean bAllowNull = i == 1;
                theDateConfig.setAllowNullDateSelection(bAllowNull);
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
