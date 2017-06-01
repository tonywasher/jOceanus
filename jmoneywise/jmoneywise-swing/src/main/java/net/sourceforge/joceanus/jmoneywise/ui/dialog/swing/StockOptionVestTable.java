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
package net.sourceforge.joceanus.jmoneywise.ui.dialog.swing;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.DateDayCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.UnitsCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest.StockOptionVestList;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.lethe.date.swing.TethysSwingDateConfig;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Panel to display a list of Vests associated with a StockOption.
 */
public class StockOptionVestTable
        extends JDataTable<StockOptionVest, MoneyWiseDataType> {
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
    private static final String TITLE_ACTION = MoneyWiseUIResource.COLUMN_ACTION.getValue();

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldMgr;

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
    private final StockOptionVestTableModel theModel;

    /**
     * The Column Model.
     */
    private final StockOptionVestColumnModel theColumns;

    /**
     * Vest Header.
     */
    private StockOptionVest theHeader;

    /**
     * Option.
     */
    private StockOption theOption;

    /**
     * StockOptionVests.
     */
    private StockOptionVestList theVests;

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
    protected StockOptionVestTable(final TethysSwingGuiFactory pFactory,
                                   final MetisFieldManager pFieldMgr,
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
        theModel = new StockOptionVestTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StockOptionVestColumnModel(this);
        JTable myTable = getTable();
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
     * Refresh data.
     */
    protected void refreshData() {
        /* Access the prices list */
        theVests = theUpdateSet.getDataList(MoneyWiseDataType.STOCKOPTIONVEST, StockOptionVestList.class);
        theHeader = new VestHeader(theVests);
        setList(theVests);
    }

    /**
     * Set the option.
     * @param pOption the option
     */
    protected void setOption(final StockOption pOption) {
        /* Store the option */
        if (!MetisDifference.isEqual(pOption, theOption)) {
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
        public MetisField getFieldForCell(final StockOptionVest pItem,
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
                myVest.setDate(new TethysDate());
                myVest.setUnits(TethysUnits.getWholeUnits(1));

                /* Add to the list */
                myVest.setNewVersion();
                theVests.append(myVest);

                /* Validate the vest */
                myVest.validate();

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                OceanusException myError = new MoneyWiseDataException("Failed to create new vest", e);

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
        private final transient TethysSwingDateConfig theDateConfig;

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
         * @throws OceanusException on error
         */
        private void setItemValue(final StockOptionVest pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pItem.setDate((TethysDate) pValue);
                    break;
                case COLUMN_UNITS:
                    pItem.setUnits((TethysUnits) pValue);
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
        protected MetisField getFieldForCell(final int pColIndex) {
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
