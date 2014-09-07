/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.joceanus.jmetis.field.JFieldData;
import net.sourceforge.joceanus.jmetis.field.JFieldManager.PopulateFieldData;
import net.sourceforge.joceanus.jmetis.field.JFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;
import net.sourceforge.joceanus.jtethys.swing.TableFilter;
import net.sourceforge.joceanus.jtethys.swing.TableFilter.TableFilterModel;

/**
 * Data Table model class.
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public abstract class JDataTableModel<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends AbstractTableModel
        implements PopulateFieldData, TableFilterModel<T> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 3815818983288519203L;

    /**
     * Table header.
     */
    private static final String TITLE_ROW = ResourceMgr.getString(PrometheusUIResource.TABLE_TITLE_ROW);

    /**
     * The Table.
     */
    private final JDataTable<T, E> theTable;

    /**
     * The RowHdrModel.
     */
    private final RowTableModel<E> theRowHdrModel;

    /**
     * Should we show all items.
     */
    private boolean showAll = false;

    /**
     * The table filter.
     */
    private transient TableFilter<T> theFilter = null;

    /**
     * Obtain the table filter.
     * @return the filter
     */
    public TableFilter<T> getFilter() {
        return theFilter;
    }

    /**
     * Obtain the filtered row count.
     * @return the row count
     */
    public int getViewRowCount() {
        return theFilter == null
                                ? getRowCount()
                                : theFilter.getViewRowCount();
    }

    /**
     * Get showAll value.
     * @return true/false
     */
    public boolean showAll() {
        return showAll;
    }

    /**
     * Set showAll value.
     * @param doShowAll true/false
     */
    public void setShowAll(final boolean doShowAll) {
        /* Ignore if no change */
        if (doShowAll == showAll) {
            return;
        }

        /* Record the value and alert to the change */
        showAll = doShowAll;
        fireNewDataEvents();
    }

    @Override
    public boolean includeRow(final T pRow) {
        /* Return visibility of row */
        return showAll || !pRow.isDeleted();
    }

    @Override
    public boolean isCellEditable(final int pRowIndex,
                                  final int pColIndex) {
        /* Access the item */
        T myItem = getItemAtIndex(pRowIndex);

        /* Determine whether the cell is editable */
        return isCellEditable(myItem, pColIndex);
    }

    /**
     * Determine whether the cell is editable.
     * @param pItem the item
     * @param pColIndex the column
     * @return true/false
     */
    public abstract boolean isCellEditable(final T pItem,
                                           final int pColIndex);

    /**
     * Get the field associated with the cell.
     * @param pItem the item
     * @param pColIndex the column
     * @return the field
     */
    public abstract JDataField getFieldForCell(final T pItem,
                                               final int pColIndex);

    @Override
    public Object getValueAt(final int pRowIndex,
                             final int pColIndex) {
        /* Access the item */
        T myItem = getItemAtIndex(pRowIndex);

        /* Obtain the value for the item */
        Object o = getItemValue(myItem, pColIndex);

        /* If we have a null value for an error field, set error description */
        if ((o == null) && (myItem.hasErrors(getFieldForCell(myItem, pColIndex)))) {
            o = JFieldValue.ERROR;
        }

        /* Return to caller */
        return o;
    }

    /**
     * Obtain value for item.
     * @param pItem the item
     * @param pColIndex the column
     * @return the value
     */
    public abstract Object getItemValue(final T pItem,
                                        final int pColIndex);

    /**
     * Set the value at (row, column).
     * @param obj the object value to set
     * @param pRowIndex the row
     * @param pColIndex the column
     */
    @Override
    public void setValueAt(final Object obj,
                           final int pRowIndex,
                           final int pColIndex) {
        /* Access the item */
        T myItem = getItemAtIndex(pRowIndex);

        /* Push history */
        myItem.pushHistory();

        /* Protect against Exceptions */
        try {
            /* Set the item value */
            setItemValue(myItem, pColIndex, obj);

            /* Handle Exceptions */
        } catch (JOceanusException e) {
            /* Reset values */
            myItem.popHistory();

            /* Build the error */
            JOceanusException myError = new JPrometheusDataException("Failed to update field at (" + pRowIndex + "," + pColIndex + ")", e);

            /* Show the error */
            theTable.setError(myError);
            return;
        }

        /* Check for changes */
        if (myItem.checkForHistory()) {
            /* Increment data version */
            theTable.incrementVersion();

            /* Update components to reflect changes */
            fireNewDataEvents();
            theTable.notifyChanges();
        }
    }

    /**
     * Set value for item.
     * @param pItem the item
     * @param pColIndex the column
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setItemValue(final T pItem,
                             final int pColIndex,
                             final Object pValue) throws JOceanusException {
    }

    /**
     * Register the data filter.
     * @param pFilter the filter
     */
    public void registerFilter(final TableFilter<T> pFilter) {
        theFilter = pFilter;
    }

    /**
     * Constructor.
     * @param pTable the table with which this model is associated
     */
    protected JDataTableModel(final JDataTable<T, E> pTable) {
        /* Access rowHdrModel */
        theTable = pTable;
        theRowHdrModel = pTable.getRowTableModel();
    }

    /**
     * fire events for insertion of a row.
     * @param pNewRow the inserted row
     */
    protected void fireInsertRowEvents(final int pNewRow) {
        /* Note that we have an inserted row */
        fireTableRowsInserted(pNewRow, pNewRow);

        /* Update the row table */
        theRowHdrModel.fireTableDataChanged();
    }

    /**
     * fire events for deletion of a row.
     * @param pOldRow the deleted row
     */
    protected void fireDeleteRowEvents(final int pOldRow) {
        /* Note that we have an deleted row */
        fireTableRowsDeleted(pOldRow, pOldRow);

        /* Update the row table */
        theRowHdrModel.fireTableDataChanged();
    }

    /**
     * fire row updated events.
     * @param pRowIndex the updated row
     */
    protected void fireUpdateRowEvents(final int pRowIndex) {
        /* Note that the data for this row and header has changed */
        fireTableRowsUpdated(pRowIndex, pRowIndex);
        theFilter.reportMappingChanged();

        /* Update the row table */
        theRowHdrModel.fireTableDataChanged();
    }

    /**
     * fire column updated events.
     * @param pColIndex the updated column
     */
    public void fireUpdateColEvent(final int pColIndex) {
        /* Access the size of the table */
        int mySize = getRowCount();
        if (mySize == 0) {
            return;
        }

        /* Create the table event */
        TableModelEvent myEvent = new TableModelEvent(this, 0, mySize - 1, pColIndex);

        /* Note that the data for this row and header has changed */
        fireTableChanged(myEvent);
        theFilter.reportMappingChanged();
    }

    /**
     * fire events for new data view.
     */
    public void fireNewDataEvents() {
        /* Note that the data for table and row header has changed */
        fireTableDataChanged();
        theRowHdrModel.fireTableDataChanged();
        theFilter.reportMappingChanged();
    }

    @Override
    public void populateFieldData(final JFieldData pData) {

        /* If we have a header decrement the index */
        int iRow = pData.getRow();
        iRow = theTable.convertRowIndexToModel(iRow);
        T myRow = getItemAtIndex(iRow);

        /* If this is a data row */
        if (!myRow.isHeader()) {
            /* Access the field */
            int iCol = pData.getCol();
            iCol = theTable.convertColumnIndexToModel(iCol);
            JDataField iField = getFieldForCell(myRow, iCol);

            /* Has the field changed */
            pData.processTableRow(myRow, iField);

            /* else set default values */
        } else {
            pData.setDefaults();
        }
    }

    /**
     * Obtain an iterator over Model rows. Note that this iterator is for a self-contained snapshot of the table mapping. It will not be affected or invalidated
     * by subsequent changes.
     * @return the iterator
     */
    public Iterator<T> modelIterator() {
        /* Allocate iterator */
        return theFilter.modelIterator();
    }

    /**
     * Obtain an iterator over View rows. Note that this iterator is for a self-contained snapshot of the table mapping. It will not be affected or invalidated
     * by subsequent changes.
     * @return the iterator
     */
    public Iterator<T> viewIterator() {
        /* Allocate iterator */
        return theFilter.viewIterator();
    }

    /**
     * Obtain an iterator over View rows starting at the end of the list. Note that this iterator is for a self-contained snapshot of the table mapping. It will
     * not be affected or invalidated by subsequent changes.
     * @return the iterator
     */
    public ListIterator<T> viewEndIterator() {
        /* Allocate iterator */
        return theFilter.viewEndIterator();
    }

    /**
     * Row Table model class.
     * @param <E> the data type enum class
     */
    protected static class RowTableModel<E extends Enum<E>>
            extends AbstractTableModel
            implements PopulateFieldData {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7172213268168894124L;

        /**
         * The DataTable.
         */
        private JDataTable<?, E> theTable = null;

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        protected RowTableModel(final JDataTable<?, E> pTable) {
            /* Access rowHdrModel */
            theTable = pTable;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public int getRowCount() {
            return theTable.getRowCount();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            return TITLE_ROW;
        }

        @Override
        public Class<?> getColumnClass(final int pColIndex) {
            return Integer.class;
        }

        @Override
        public Object getValueAt(final int pRowIndex,
                                 final int pColIndex) {
            return 1 + pRowIndex;
        }

        @Override
        public void populateFieldData(final JFieldData pData) {
            /* Convert our row # into that of the table */
            int iRow = pData.getRow();
            iRow = theTable.convertRowIndexToModel(iRow);

            /* Obtain defaults from table header */
            pData.initFromHeader(theTable.getTableHeader());

            /* If this is a data row */
            if (iRow >= 0) {
                /* Access the row */
                JDataTableModel<?, E> myModel = theTable.getTableModel();
                DataItem<E> myRow = myModel.getItemAtIndex(iRow);
                @SuppressWarnings("unchecked")
                JDataTableColumnModel<E> myColModel = (JDataTableColumnModel<E>) theTable.getColumnModel();
                JDataField[] iFields = myColModel.getColumnFields();

                /* Has the row changed */
                pData.processRowHeader(myRow, iFields);
            }
        }
    }
}
