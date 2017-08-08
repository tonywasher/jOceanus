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

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisEosFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisSwingFieldCellEditor.MetisFieldCalendarCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisSwingFieldCellEditor.MetisFieldPriceCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisSwingFieldCellRenderer.MetisFieldCalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisSwingFieldCellRenderer.MetisFieldDecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DilutionEvent.DilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.ViewSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.views.ViewSecurityPrice.ViewSecurityPriceList;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusAction;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.ui.eos.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.eos.PrometheusDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.eos.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.eos.PrometheusDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Panel to display a list of SecurityPrices associated with a Security.
 */
public class SecurityPriceTable
        extends PrometheusDataTable<ViewSecurityPrice, MoneyWiseDataType> {
    /**
     * Date Column Title.
     */
    private static final String TITLE_DATE = SecurityPrice.FIELD_DATE.getName();

    /**
     * Price Column Title.
     */
    private static final String TITLE_PRICE = SecurityPrice.FIELD_PRICE.getName();

    /**
     * Dilution Column Title.
     */
    private static final String TITLE_DILUTION = ViewSecurityPrice.FIELD_DILUTION.getName();

    /**
     * DilutedPrice Column Title.
     */
    private static final String TITLE_DILUTEDPRICE = ViewSecurityPrice.FIELD_DILUTEDPRICE.getName();

    /**
     * Action Column Title.
     */
    private static final String TITLE_ACTION = MoneyWiseUIResource.COLUMN_ACTION.getValue();

    /**
     * The data view.
     */
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisEosFieldManager theFieldMgr;

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
    private final SecurityPriceTableModel theModel;

    /**
     * The Column Model.
     */
    private final SecurityPriceColumnModel theColumns;

    /**
     * Date Range.
     */
    private TethysDateRange theRange;

    /**
     * Price Header.
     */
    private ViewSecurityPrice theHeader;

    /**
     * Security.
     */
    private Security theSecurity;

    /**
     * SecurityPrices.
     */
    private ViewSecurityPriceList thePrices;

    /**
     * Dilutions.
     */
    private DilutionEventMap theDilutions;

    /**
     * Editable flag.
     */
    private boolean isEditable;

    /**
     * Constructor.
     * @param pView the data view
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected SecurityPriceTable(final SwingView pView,
                                 final MetisEosFieldManager pFieldMgr,
                                 final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                 final MetisErrorPanel<JComponent, Icon> pError) {
        /* initialise the underlying class */
        super((TethysSwingGuiFactory) pView.getUtilitySet().getGuiFactory());

        /* Record the passed details */
        theView = pView;
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
        thePrices = theUpdateSet.getDataList(MoneyWiseDataType.SECURITYPRICE, ViewSecurityPriceList.class);
        theHeader = new PriceHeader(thePrices);
        theDilutions = theView.getDilutions();
        theRange = thePrices.getDataSet().getDateRange();
        setList(thePrices);
    }

    /**
     * Set the security.
     * @param pSecurity the security
     */
    protected void setSecurity(final Security pSecurity) {
        /* Store the security */
        if (!MetisDifference.isEqual(pSecurity, theSecurity)) {
            theSecurity = pSecurity;
            theModel.fireNewDataEvents();
        }
    }

    /**
     * Add a new price for a new security.
     * @param pSecurity the security
     * @throws OceanusException on error
     */
    protected void addNewPrice(final Security pSecurity) throws OceanusException {
        /* Create the new price */
        ViewSecurityPrice myPrice = new ViewSecurityPrice(thePrices);

        /* Set the item value */
        myPrice.setSecurity(pSecurity);
        myPrice.setDate(new TethysDate());
        myPrice.setPrice(TethysPrice.getWholeUnits(1, pSecurity.getCurrency()));

        /* Add to the list */
        myPrice.setNewVersion();
        thePrices.append(myPrice);

        /* Validate the price */
        myPrice.validate();
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
            extends PrometheusDataTableModel<ViewSecurityPrice, MoneyWiseDataType> {
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
        public MetisField getFieldForCell(final ViewSecurityPrice pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final ViewSecurityPrice pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pColIndex);
        }

        @Override
        public ViewSecurityPrice getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return pRowIndex == 0
                                  ? theHeader
                                  : thePrices.get(pRowIndex - 1);
        }

        @Override
        public Object getItemValue(final ViewSecurityPrice pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return pItem.isHeader()
                                    ? theColumns.getHeaderValue(pColIndex)
                                    : theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final ViewSecurityPrice pItem,
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
        public boolean includeRow(final ViewSecurityPrice pRow) {
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
            if (theSecurity != null) {
                fireUpdateRowEvents(0);
            }
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Add a new price */
                addNewPrice(theSecurity);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                OceanusException myError = new MoneyWiseDataException("Failed to create new price", e);

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
    private final class SecurityPriceColumnModel
            extends PrometheusDataTableColumnModel<MoneyWiseDataType> {
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
         * Dilution column id.
         */
        private static final int COLUMN_DILUTION = 2;

        /**
         * DilutedPrice column id.
         */
        private static final int COLUMN_DILUTEDPRICE = 3;

        /**
         * Action column id.
         */
        private static final int COLUMN_ACTION = 4;

        /**
         * Dilution column.
         */
        private final PrometheusDataTableColumn theDilutionColumn;

        /**
         * DilutedPrice column.
         */
        private final PrometheusDataTableColumn theDilutedColumn;

        /**
         * Action column.
         */
        private final PrometheusDataTableColumn theActionColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private SecurityPriceColumnModel(final SecurityPriceTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            MetisFieldPriceCellEditor myPriceEditor = theFieldMgr.allocatePriceCellEditor();
            MetisFieldCalendarCellEditor myDateEditor = theFieldMgr.allocateCalendarCellEditor();
            MetisFieldIconButtonCellEditor<PrometheusAction> myActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(PrometheusAction.class);
            MetisFieldCalendarCellRenderer myDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            MetisFieldDecimalCellRenderer myDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            MetisFieldIconButtonCellRenderer<PrometheusAction> myActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(PrometheusAction.class);

            /* Configure the iconButton */
            TethysIconMapSet<PrometheusAction> myActionMapSet = PrometheusIcon.configureStatusIconButton();
            myActionIconRenderer.setIconMapSet(r -> myActionMapSet);
            myActionIconEditor.setIconMapSet(r -> myActionMapSet);

            /* Create the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_DATE, WIDTH_DATE, myDateRenderer, myDateEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_PRICE, WIDTH_PRICE, myDecimalRenderer, myPriceEditor));
            theDilutionColumn = new PrometheusDataTableColumn(COLUMN_DILUTION, WIDTH_PRICE, myDecimalRenderer);
            declareColumn(theDilutionColumn);
            theDilutedColumn = new PrometheusDataTableColumn(COLUMN_DILUTEDPRICE, WIDTH_PRICE, myDecimalRenderer);
            declareColumn(theDilutedColumn);
            theActionColumn = new PrometheusDataTableColumn(COLUMN_ACTION, WIDTH_ICON, myActionIconRenderer, myActionIconEditor);
            declareColumn(theActionColumn);

            /* Initialise the columns */
            setColumns();

            /* Add configurator */
            myDateEditor.setDateConfigurator((r, c) -> handleDateEvent(c));
            myPriceEditor.setDeemedCurrency(r -> theSecurity.getCurrency());
        }

        /**
         * handle Date event.
         * @param pConfig the dateConfig
         */
        private void handleDateEvent(final TethysDateConfig pConfig) {
            /* Adjust the range */
            pConfig.setEarliestDate(theRange == null
                                                     ? null
                                                     : theRange.getStart());
            pConfig.setLatestDate(theRange == null
                                                   ? null
                                                   : theRange.getEnd());
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Determine whether the security is diluted */
            boolean isDiluted = theDilutions != null && theDilutions.hasDilution(theSecurity);
            if (isDiluted) {
                revealColumn(theDilutionColumn);
                revealColumn(theDilutedColumn);
            } else {
                hideColumn(theDilutionColumn);
                hideColumn(theDilutedColumn);
            }

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
                case COLUMN_DILUTION:
                    return TITLE_DILUTION;
                case COLUMN_DILUTEDPRICE:
                    return TITLE_DILUTEDPRICE;
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
                    return PrometheusAction.INSERT;
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
        protected Object getItemValue(final ViewSecurityPrice pItem,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pItem.getDate();
                case COLUMN_PRICE:
                    return pItem.getPrice();
                case COLUMN_DILUTION:
                    return pItem.getDilution();
                case COLUMN_DILUTEDPRICE:
                    return pItem.getDilutedPrice();
                case COLUMN_ACTION:
                    return theModel.getViewRowCount() > 1
                                                          ? PrometheusAction.DELETE
                                                          : PrometheusAction.DO;
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
        private void setItemValue(final ViewSecurityPrice pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pItem.setDate((TethysDate) pValue);
                    break;
                case COLUMN_PRICE:
                    pItem.setPrice((TethysPrice) pValue);
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
                case COLUMN_PRICE:
                    return isEditable;
                case COLUMN_DILUTION:
                case COLUMN_DILUTEDPRICE:
                    return false;
                case COLUMN_ACTION:
                    return pColIndex == 0
                                          ? isEditable
                                          : isEditable && theModel.getViewRowCount() > 2;
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
                    return SecurityPrice.FIELD_DATE;
                case COLUMN_PRICE:
                    return SecurityPrice.FIELD_PRICE;
                case COLUMN_DILUTION:
                    return ViewSecurityPrice.FIELD_DILUTION;
                case COLUMN_DILUTEDPRICE:
                    return ViewSecurityPrice.FIELD_DILUTEDPRICE;
                default:
                    return null;
            }
        }
    }

    /**
     * Price Header class.
     */
    private static class PriceHeader
            extends ViewSecurityPrice {
        /**
         * Constructor.
         * @param pList the SecurityPrice list
         */
        protected PriceHeader(final ViewSecurityPriceList pList) {
            super(pList);
            setHeader(true);
        }
    }
}
