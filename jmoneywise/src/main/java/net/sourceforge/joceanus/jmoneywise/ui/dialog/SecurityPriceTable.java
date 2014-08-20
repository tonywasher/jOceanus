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

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.PriceCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

/**
 * Panel to display a list of SecurityPrices associated with a Security.
 */
public class SecurityPriceTable
        extends JDataTable<SecurityPrice, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -153651020207724175L;

    /**
     * Date Column Title.
     */
    private static final String TITLE_DATE = SecurityPrice.FIELD_DATE.getName();

    /**
     * Price Column Title.
     */
    private static final String TITLE_PRICE = SecurityPrice.FIELD_PRICE.getName();

    /**
     * Action Column Title.
     */
    private static final String TITLE_ACTION = "Action";

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
    private final SecurityPriceTableModel theModel;

    /**
     * The Column Model.
     */
    private final SecurityPriceColumnModel theColumns;

    /**
     * Price Header.
     */
    private transient SecurityPrice theHeader;

    /**
     * Security.
     */
    private transient Security theSecurity = null;

    /**
     * SecurityPrices.
     */
    private transient SecurityPriceList thePrices = null;

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
    protected SecurityPriceTable(final JFieldManager pFieldMgr,
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
        theModel = new SecurityPriceTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new SecurityPriceColumnModel(this);
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
        thePrices = theUpdateSet.findDataList(MoneyWiseDataType.SECURITYPRICE, SecurityPriceList.class);
        theHeader = new PriceHeader(thePrices);
        setList(thePrices);
        fireStateChanged();
    }

    /**
     * Set the security.
     * @param pSecurity the security
     */
    protected void setSecurity(final Security pSecurity) {
        /* Store the security */
        theSecurity = pSecurity;
        theModel.fireNewDataEvents();
        fireStateChanged();
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
    private final class SecurityPriceTableModel
            extends JDataTableModel<SecurityPrice, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 8841302608840657428L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private SecurityPriceTableModel(final SecurityPriceTable pTable) {
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
            return (thePrices == null)
                                      ? 0
                                      : 1 + thePrices.size();
        }

        @Override
        public JDataField getFieldForCell(final SecurityPrice pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final SecurityPrice pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public SecurityPrice getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return pRowIndex == 0
                                 ? theHeader
                                 : thePrices.get(pRowIndex - 1);
        }

        @Override
        public Object getItemValue(final SecurityPrice pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return pItem.isHeader()
                                   ? theColumns.getHeaderValue(pColIndex)
                                   : theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final SecurityPrice pItem,
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
        public boolean includeRow(final SecurityPrice pRow) {
            /* Ignore deleted rows and all rows if no security is selected */
            if (pRow.isDeleted() || theSecurity == null) {
                return false;
            }

            /* Handle filter */
            return pRow.isHeader()
                                  ? isEditable
                                  : theSecurity.equals(pRow.getSecurity());
        }

        /**
         * Adjust header.
         */
        private void adjustHeader() {
            fireUpdateRowEvents(0);
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Create the new price */
            SecurityPrice myPrice = new SecurityPrice(thePrices);

            /* Protect against Exceptions */
            try {
                /* Set the item value */
                myPrice.setSecurity(theSecurity);
                myPrice.setDate(new JDateDay());
                myPrice.setPrice(JPrice.getWholeUnits(1, theSecurity.getSecurityCurrency().getCurrency()));

                /* Handle Exceptions */
            } catch (JOceanusException e) {
                /* Build the error */
                JOceanusException myError = new JMoneyWiseDataException("Failed to create new price", e);

                /* Show the error */
                setError(myError);
                return;
            }

            /* Add the new item */
            myPrice.setNewVersion();
            thePrices.append(myPrice);

            /* Validate the new item and notify of the changes */
            myPrice.validate();
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
    private final class SecurityPriceColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6629043017566713861L;

        /**
         * Date column id.
         */
        private static final int COLUMN_DATE = 0;

        /**
         * Price column id.
         */
        private static final int COLUMN_PRICE = 1;

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
         * Price editor.
         */
        private final PriceCellEditor thePriceEditor;

        /**
         * Date editor.
         */
        private final CalendarCellEditor theDateEditor;

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
        private SecurityPriceColumnModel(final SecurityPriceTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            thePriceEditor = theFieldMgr.allocatePriceCellEditor();
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theActionIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theActionIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            declareColumn(new JDataTableColumn(COLUMN_PRICE, WIDTH_PRICE, theDecimalRenderer, thePriceEditor));
            theActionColumn = new JDataTableColumn(COLUMN_ACTION, WIDTH_ICON, theActionIconRenderer, theActionIconEditor);
            declareColumn(theActionColumn);

            /* Initialise the columns */
            setColumns();
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
                case COLUMN_DATE:
                    return TITLE_DATE;
                case COLUMN_PRICE:
                    return TITLE_PRICE;
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
        protected Object getItemValue(final SecurityPrice pItem,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pItem.getDate();
                case COLUMN_PRICE:
                    return pItem.getPrice();
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
        private void setItemValue(final SecurityPrice pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pItem.setDate((JDateDay) pValue);
                    break;
                case COLUMN_PRICE:
                    pItem.setPrice((JPrice) pValue);
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
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final SecurityPrice pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DATE:
                case COLUMN_PRICE:
                    return isEditable;
                case COLUMN_ACTION:
                    return isEditable && theModel.getViewRowCount() > 1;
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
                    return SecurityPrice.FIELD_DATE;
                case COLUMN_PRICE:
                    return SecurityPrice.FIELD_PRICE;
                default:
                    return null;
            }
        }
    }

    /**
     * Price Header class.
     */
    private static class PriceHeader
            extends SecurityPrice {
        /**
         * Constructor.
         * @param pList the SecurityPrice list
         */
        protected PriceHeader(final SecurityPriceList pList) {
            super(pList);
            setHeader(true);
        }
    }
}