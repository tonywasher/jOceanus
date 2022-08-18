/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing.table;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.table.TethysUICoreTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableUnitsColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableSorter.TethysUISwingTableSorterModel;

/**
 * JavaSwing Table manager.
 *
 * @param <C> the column identity
 * @param <R> the row type
 */
public class TethysUISwingTableManager<C, R>
        extends TethysUICoreTableManager<C, R> {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The Scroll Pane.
     */
    private final JScrollPane theScroll;

    /**
     * The TableView.
     */
    private final JTable theTable;

    /**
     * The ColumnModel.
     */
    private final TableColumnModel theColumnModel;

    /**
     * The ColumnList.
     */
    private final List<TethysUISwingTableColumn<?, C, R>> theColumnList;

    /**
     * The CellFactory.
     */
    private final TethysUISwingTableCellFactory<C, R> theCellFactory;

    /**
     * The model.
     */
    private final TethysUISwingTableModel theModel;

    /**
     * The sorter.
     */
    private final TethysUISwingTableSorter<R> theSorter;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    public TethysUISwingTableManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create fields */
        theColumnList = new ArrayList<>();
        theModel = new TethysUISwingTableModel();
        theTable = new JTable(theModel);
        theColumnModel = theTable.getColumnModel();
        theCellFactory = new TethysUISwingTableCellFactory<>(pFactory);
        theSorter = new TethysUISwingTableSorter<>(theModel);
        theTable.setRowSorter(theSorter);

        /* Listen to the valueSet */
        pFactory.getValueSet().getEventRegistrar().addEventListener(e -> theModel.fireTableDataChanged());
        final ListSelectionModel myModel = theTable.getSelectionModel();
        myModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myModel.addListSelectionListener(this::handleSelection);

        /* Create the scrollPane */
        theScroll = new JScrollPane();
        theScroll.setViewportView(theTable);

        /* Create the node */
        theNode = new TethysUISwingNode(theScroll);
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        final Dimension curSize = theTable.getPreferredScrollableViewportSize();
        final Dimension prefSize = new Dimension(pWidth, curSize.height);
        theTable.setPreferredScrollableViewportSize(prefSize);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        final Dimension curSize = theTable.getPreferredScrollableViewportSize();
        final Dimension prefSize = new Dimension(curSize.width, pHeight);
        theTable.setPreferredScrollableViewportSize(prefSize);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theScroll.setEnabled(pEnabled);
        theTable.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theScroll.setVisible(pVisible);
    }

    @Override
    public Iterator<R> sortedIterator() {
        return getItems() == null
                ? Collections.emptyIterator()
                : theSorter.sortIterator();
    }

    @Override
    public Iterator<R> viewIterator() {
        return getItems() == null
                ? Collections.emptyIterator()
                : theSorter.viewIterator();
    }

    @Override
    public TethysUISwingTableManager<C, R> setFilter(final Predicate<R> pFilter) {
        super.setFilter(pFilter);
        theSorter.setFilter(pFilter);
        return this;
    }

    @Override
    public TethysUISwingTableManager<C, R> setComparator(final Comparator<R> pComparator) {
        super.setComparator(pComparator);
        theSorter.setComparator(getComparator());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TethysUISwingTableColumn<?, C, R> getColumn(final C pId) {
        return (TethysUISwingTableColumn<?, C, R>) super.getColumn(pId);
    }

    @Override
    public void requestFocus() {
        theTable.requestFocus();
    }

    /**
     * Obtain the table model.
     *
     * @return the table model
     */
    TethysUISwingTableModel getTableModel() {
        return theModel;
    }

    /**
     * Obtain the columnList.
     *
     * @return the columnModel
     */
    List<TethysUISwingTableColumn<?, C, R>> getColumnList() {
        return theColumnList;
    }

    /**
     * Obtain the columnModel.
     *
     * @return the columnModel
     */
    TableColumnModel getColumnModel() {
        return theColumnModel;
    }

    /**
     * Obtain the cell factory.
     *
     * @return the cell factory
     */
    TethysUISwingTableCellFactory<C, R> getCellFactory() {
        return theCellFactory;
    }

    /**
     * Obtain the sorter.
     *
     * @return the sorter
     */
    TethysUISwingTableSorter<R> getSorter() {
        return theSorter;
    }

    /**
     * Obtain the column for the model index.
     *
     * @param pIndex the index of the column
     * @return the table column
     */
    TethysUISwingTableColumn<?, C, R> getIndexedColumn(final int pIndex) {
        if (pIndex < 0
                || pIndex > theColumnList.size()) {
            throw new IllegalArgumentException();
        }
        return theColumnList.get(pIndex);
    }

    /**
     * Obtain the row for the model index.
     *
     * @param pIndex the index of the column
     * @return the table column
     */
    R getIndexedRow(final int pIndex) {
        final List<R> myItems = getItems();
        if (myItems == null) {
            return null;
        }
        if (pIndex < 0
                || pIndex > myItems.size()) {
            throw new IllegalArgumentException();
        }
        return myItems.get(pIndex);
    }

    @Override
    public void cancelEditing() {
        if (theTable.isEditing()) {
            theTable.getCellEditor().cancelCellEditing();
        }
    }

    @Override
    public void setItems(final List<R> pItems) {
        super.setItems(pItems);
        theModel.fireTableDataChanged();
    }

    @Override
    public TethysUISwingTableStringColumn<C, R> declareStringColumn(final C pId) {
        return new TethysUISwingTableStringColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableCharArrayColumn<C, R> declareCharArrayColumn(final C pId) {
        return new TethysUISwingTableCharArrayColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableShortColumn<C, R> declareShortColumn(final C pId) {
        return new TethysUISwingTableShortColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableIntegerColumn<C, R> declareIntegerColumn(final C pId) {
        return new TethysUISwingTableIntegerColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableLongColumn<C, R> declareLongColumn(final C pId) {
        return new TethysUISwingTableLongColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableRawDecimalColumn<C, R> declareRawDecimalColumn(final C pId) {
        return new TethysUISwingTableRawDecimalColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableMoneyColumn<C, R> declareMoneyColumn(final C pId) {
        return new TethysUISwingTableMoneyColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTablePriceColumn<C, R> declarePriceColumn(final C pId) {
        return new TethysUISwingTablePriceColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableRateColumn<C, R> declareRateColumn(final C pId) {
        return new TethysUISwingTableRateColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableUnitsColumn<C, R> declareUnitsColumn(final C pId) {
        return new TethysUISwingTableUnitsColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableDilutionColumn<C, R> declareDilutionColumn(final C pId) {
        return new TethysUISwingTableDilutionColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableRatioColumn<C, R> declareRatioColumn(final C pId) {
        return new TethysUISwingTableRatioColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableDilutedPriceColumn<C, R> declareDilutedPriceColumn(final C pId) {
        return new TethysUISwingTableDilutedPriceColumn<>(this, pId);
    }

    @Override
    public TethysUISwingTableDateColumn<C, R> declareDateColumn(final C pId) {
        return new TethysUISwingTableDateColumn<>(this, pId);
    }

    @Override
    public <T> TethysUISwingTableScrollColumn<T, C, R> declareScrollColumn(final C pId,
                                                                           final Class<T> pClazz) {
        return new TethysUISwingTableScrollColumn<>(this, pId, pClazz);
    }

    @Override
    public <T extends Comparable<T>> TethysUISwingTableListColumn<T, C, R> declareListColumn(final C pId,
                                                                                             final Class<T> pClazz) {
        return new TethysUISwingTableListColumn<>(this, pId, pClazz);
    }

    @Override
    public <T> TethysUISwingTableIconColumn<T, C, R> declareIconColumn(final C pId,
                                                                       final Class<T> pClazz) {
        return new TethysUISwingTableIconColumn<>(this, pId, pClazz);
    }

    @Override
    public void fireTableDataChanged() {
        theModel.fireTableDataChanged();
    }

    /**
     * Fire TableRow added.
     *
     * @param pRowIndex the index of the row that has been added
     */
    public void fireTableRowAdded(final int pRowIndex) {
        theModel.fireTableRowsInserted(pRowIndex, pRowIndex);
    }

    /**
     * Fire TableRow changed.
     *
     * @param pRowIndex the index of the row that has changed
     */
    public void fireTableRowChanged(final int pRowIndex) {
        final int myIndex = theTable.convertRowIndexToView(pRowIndex);
        theModel.fireTableRowUpdated(myIndex);
    }

    /**
     * Fire TableRow deleted.
     *
     * @param pRowIndex the index of the row that has been deleted
     */
    public void fireTableRowDeleted(final int pRowIndex) {
        final int myIndex = theTable.convertRowIndexToView(pRowIndex);
        theModel.fireTableRowsDeleted(myIndex, myIndex);
    }

    /**
     * Fire TableCell updated.
     *
     * @param pRowIndex the row index of the cell that has been updated
     * @param pColIndex the column index of the cell that has been updated
     */
    public void fireTableCellUpdated(final int pRowIndex,
                                     final int pColIndex) {
        final int myRowIndex = theTable.convertRowIndexToView(pRowIndex);
        final int myColIndex = theTable.convertColumnIndexToView(pColIndex);
        theModel.fireTableCellUpdated(myRowIndex, myColIndex);
    }

    /**
     * handle listSelection.
     *
     * @param pEvent the event
     */
    private void handleSelection(final ListSelectionEvent pEvent) {
        final ListSelectionModel myModel = (ListSelectionModel) pEvent.getSource();
        final int myIndex = myModel.getMinSelectionIndex();
        final R mySelected = myModel.isSelectionEmpty()
                ? null
                : theModel.getItemAtIndex(theTable.convertRowIndexToModel(myIndex));
        processOnSelect(mySelected);
    }

    @Override
    public void selectRowWithScroll(final R pItem) {
        /* Determine the index of the item */
        final int iModel = getItems().indexOf(pItem);
        final int iView = iModel == -1 ? -1 : theTable.convertRowIndexToView(iModel);

        /* If we have a row to display */
        if (iView != -1) {
            /* Shift display to row */
            final Rectangle rect = theTable.getCellRect(iView, 0, true);
            final JViewport viewport = (JViewport) theTable.getParent();
            final Point pt = viewport.getViewPosition();
            rect.setLocation(rect.x - pt.x, rect.y - pt.y);
            viewport.scrollRectToVisible(rect);

            /* Select the row */
            theTable.setRowSelectionInterval(iView, iView);

            /* else clear the selection */
        } else {
            theTable.clearSelection();
        }
    }

    /**
     * Table Model.
     */
    protected class TethysUISwingTableModel
            extends AbstractTableModel
            implements TethysUISwingTableSorterModel<R> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 9100612548589774432L;

        @Override
        public int getRowCount() {
            return getItems() == null
                    ? 0
                    : getItems().size();
        }

        @Override
        public int getColumnCount() {
            return getColumnList().size();
        }

        @Override
        public boolean isCellEditable(final int pRowIndex,
                                      final int pColIndex) {
            return getIndexedColumn(pColIndex).isCellEditable(pRowIndex);
        }

        @Override
        public Object getValueAt(final int pRowIndex,
                                 final int pColIndex) {
            return getIndexedColumn(pColIndex).getCellValue(pRowIndex);
        }

        @Override
        public void setValueAt(final Object pValue,
                               final int pRowIndex,
                               final int pColIndex) {
            getIndexedColumn(pColIndex).setCellValue(pRowIndex, pValue);
        }

        @Override
        public R getItemAtIndex(final int pRowIndex) {
            return getIndexedRow(pRowIndex);
        }

        @Override
        public void fireTableCellUpdated(final int pRowIndex,
                                         final int pColIndex) {
            super.fireTableCellUpdated(pRowIndex, pColIndex);
            getSorter().reportMappingChanged();
        }

        /**
         * Notify model that the row has been updated.
         *
         * @param pRowIndex the view row index
         */
        void fireTableRowUpdated(final int pRowIndex) {
            fireTableRowsUpdated(pRowIndex, pRowIndex);
            getSorter().reportMappingChanged();
        }
    }
}
