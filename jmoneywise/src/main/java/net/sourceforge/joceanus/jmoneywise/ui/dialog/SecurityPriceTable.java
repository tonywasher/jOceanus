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
     * Delete Column Title.
     */
    private static final String TITLE_DELETE = "Delete";

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
        // theUpdateSet.addChangeListener(myListener);

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
                                      : thePrices.size();
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
            return thePrices.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final SecurityPrice pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
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
            return theSecurity.equals(pRow.getSecurity());
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
         * Delete column id.
         */
        private static final int COLUMN_DELETE = 2;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * Delete Icon Renderer.
         */
        private final IconButtonCellRenderer<Boolean> theDeleteIconRenderer;

        /**
         * Price editor.
         */
        private final PriceCellEditor thePriceEditor;

        /**
         * Date editor.
         */
        private final CalendarCellEditor theDateEditor;

        /**
         * Delete Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theDeleteIconEditor;

        /**
         * Delete column.
         */
        private final JDataTableColumn theDeleteColumn;

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
            theDeleteIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, false);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theDeleteIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theDeleteIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildDeleteButton(theDeleteIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            declareColumn(new JDataTableColumn(COLUMN_PRICE, WIDTH_PRICE, theDecimalRenderer, thePriceEditor));
            theDeleteColumn = new JDataTableColumn(COLUMN_DELETE, WIDTH_ICON, theDeleteIconRenderer, theDeleteIconEditor);
            declareColumn(theDeleteColumn);

            /* Initialise the columns */
            setColumns();
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on mode */
            if (showAll()) {
                revealColumn(theDeleteColumn);
            } else {
                hideColumn(theDeleteColumn);
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
                case COLUMN_DELETE:
                    return TITLE_DELETE;
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
                case COLUMN_DELETE:
                    return theModel.getViewRowCount() > 1;
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
                case COLUMN_DELETE:
                    deleteRow(pItem);
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
                case COLUMN_DELETE:
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
}
