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

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.DateDayCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.UnitsCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest.StockOptionVestList;
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
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

/**
 * Panel to display a list of Vests associated with a StockOption.
 */
public class StockOptionVestTable
        extends JDataTable<StockOptionVest, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -4616312420617272420L;

    /**
     * Date Column Title.
     */
    private static final String TITLE_DATE = StockOptionVest.FIELD_DATE.getName();

    /**
     * Units Column Title.
     */
    private static final String TITLE_UNITS = StockOptionVest.FIELD_UNITS.getName();

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
    private final StockOptionVestTableModel theModel;

    /**
     * The Column Model.
     */
    private final StockOptionVestColumnModel theColumns;

    /**
     * Vest Header.
     */
    private transient StockOptionVest theHeader;

    /**
     * Option.
     */
    private transient StockOption theOption = null;

    /**
     * StockOptionVests.
     */
    private transient StockOptionVestList theVests = null;

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
    protected StockOptionVestTable(final JFieldManager pFieldMgr,
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
        theModel = new StockOptionVestTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StockOptionVestColumnModel(this);
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
        /* Access the prices list */
        theVests = theUpdateSet.findDataList(MoneyWiseDataType.STOCKOPTIONVEST, StockOptionVestList.class);
        theHeader = new VestHeader(theVests);
        setList(theVests);
    }

    /**
     * Set the option.
     * @param pOption the option
     */
    protected void setOption(final StockOption pOption) {
        /* Store the option */
        if (!Difference.isEqual(pOption, theOption)) {
            theOption = pOption;
            theColumns.setDateRange();
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
    }

    /**
     * Refresh the table after an updateSet reWind.
     */
    protected void refreshAfterUpdate() {
        theColumns.setDateRange();
        theModel.fireNewDataEvents();
    }

    /**
     * JTable Data Model.
     */
    private final class StockOptionVestTableModel
            extends JDataTableModel<StockOptionVest, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -1777194319234010821L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private StockOptionVestTableModel(final StockOptionVestTable pTable) {
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
            return (theVests == null)
                                     ? 0
                                     : 1 + theVests.size();
        }

        @Override
        public JDataField getFieldForCell(final StockOptionVest pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final StockOptionVest pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pColIndex);
        }

        @Override
        public StockOptionVest getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return pRowIndex == 0
                                 ? theHeader
                                 : theVests.get(pRowIndex - 1);
        }

        @Override
        public Object getItemValue(final StockOptionVest pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return pItem.isHeader()
                                   ? theColumns.getHeaderValue(pColIndex)
                                   : theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final StockOptionVest pItem,
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
        public boolean includeRow(final StockOptionVest pRow) {
            /* Ignore deleted rows and all rows if no option is selected */
            if (pRow.isDeleted() || theOption == null) {
                return false;
            }

            /* Handle filter */
            return pRow.isHeader()
                                  ? isEditable
                                  : theOption.equals(pRow.getStockOption());
        }

        /**
         * Adjust header.
         */
        private void adjustHeader() {
            if (theOption != null) {
                fireUpdateRowEvents(0);
            }
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Create the new vest */
                StockOptionVest myVest = new StockOptionVest(theVests);

                /* Set the item value */
                myVest.setStockOption(theOption);
                myVest.setDate(new JDateDay());
                myVest.setUnits(JUnits.getWholeUnits(1));

                /* Add to the list */
                myVest.setNewVersion();
                theVests.append(myVest);

                /* Validate the vest */
                myVest.validate();

                /* Handle Exceptions */
            } catch (JOceanusException e) {
                /* Build the error */
                JOceanusException myError = new JMoneyWiseDataException("Failed to create new vest", e);

                /* Show the error */
                setError(myError);
                return;
            }

            /* notify of the changes */
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
    private final class StockOptionVestColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2667655530691267129L;

        /**
         * Date column id.
         */
        private static final int COLUMN_DATE = 0;

        /**
         * Units column id.
         */
        private static final int COLUMN_UNITS = 1;

        /**
         * Action column id.
         */
        private static final int COLUMN_ACTION = 2;

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
         * Units editor.
         */
        private final UnitsCellEditor theUnitsEditor;

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
        private StockOptionVestColumnModel(final StockOptionVestTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theUnitsEditor = theFieldMgr.allocateUnitsCellEditor();
            theDateEditor = theFieldMgr.allocateDateDayCellEditor();
            theDateConfig = theDateEditor.getDateConfig();
            theActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theActionIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theActionIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            declareColumn(new JDataTableColumn(COLUMN_UNITS, WIDTH_UNITS, theDecimalRenderer, theUnitsEditor));
            theActionColumn = new JDataTableColumn(COLUMN_ACTION, WIDTH_ICON, theActionIconRenderer, theActionIconEditor);
            declareColumn(theActionColumn);
        }

        /**
         * Adjust date range.
         */
        private void setDateRange() {
            /* if we have an option */
            if (theOption != null) {
                /* Adjust editor range */
                theDateConfig.setEarliestDateDay(theOption.getGrantDate());
                theDateConfig.setLatestDateDay(theOption.getExpiryDate());
            }
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DATE:
                    return TITLE_DATE;
                case COLUMN_UNITS:
                    return TITLE_UNITS;
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
         * Obtain the value for the securityPrice column.
         * @param pItem the securityPrice
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final StockOptionVest pItem,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pItem.getDate();
                case COLUMN_UNITS:
                    return pItem.getUnits();
                case COLUMN_ACTION:
                    return theModel.getViewRowCount() > 1
                                                         ? ActionType.DELETE
                                                         : ActionType.DO;
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
        private void setItemValue(final StockOptionVest pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pItem.setDate((JDateDay) pValue);
                    break;
                case COLUMN_UNITS:
                    pItem.setUnits((JUnits) pValue);
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
                case COLUMN_DATE:
                case COLUMN_UNITS:
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
                case COLUMN_DATE:
                    return StockOptionVest.FIELD_DATE;
                case COLUMN_UNITS:
                    return StockOptionVest.FIELD_UNITS;
                default:
                    return null;
            }
        }
    }

    /**
     * Vest Header class.
     */
    private static class VestHeader
            extends StockOptionVest {
        /**
         * Constructor.
         * @param pList the StockOptionVest list
         */
        protected VestHeader(final StockOptionVestList pList) {
            super(pList);
            setHeader(true);
        }
    }
}