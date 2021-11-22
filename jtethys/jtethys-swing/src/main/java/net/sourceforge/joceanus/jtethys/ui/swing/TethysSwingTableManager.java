/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditConverter.TethysRawDecimalEditConverter;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldType;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableCell;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableSorter.TethysSwingTableSorterModel;

/**
 * JavaSwing Table manager.
 *
 * @param <C> the column identity
 * @param <R> the row type
 */
public class TethysSwingTableManager<C, R>
        extends TethysTableManager<C, R> {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

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
    private final List<TethysSwingTableColumn<?, C, R>> theColumnList;

    /**
     * The CellFactory.
     */
    private final TethysSwingTableCellFactory<C, R> theCellFactory;

    /**
     * The model.
     */
    private final TethysSwingTableModel theModel;

    /**
     * The sorter.
     */
    private final TethysSwingTableSorter<R> theSorter;

    /**
     * The item list.
     */
    private List<R> theItems;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    protected TethysSwingTableManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create fields */
        theColumnList = new ArrayList<>();
        theModel = new TethysSwingTableModel();
        theTable = new JTable(theModel);
        theColumnModel = theTable.getColumnModel();
        theCellFactory = new TethysSwingTableCellFactory<>(pFactory);
        theSorter = new TethysSwingTableSorter<>(theModel);
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
        theNode = new TethysSwingNode(theScroll);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setPreferredWidthAndHeight(final Integer pWidth,
                                           final Integer pHeight) {
        theTable.setPreferredScrollableViewportSize(new Dimension(pWidth, pHeight));
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
    public Iterator<R> itemIterator() {
        return theItems == null
               ? Collections.emptyIterator()
               : theItems.iterator();
    }

    @Override
    public Iterator<R> sortedIterator() {
        return theItems == null
               ? Collections.emptyIterator()
               : theSorter.sortIterator();
    }

    @Override
    public Iterator<R> viewIterator() {
        return theItems == null
               ? Collections.emptyIterator()
               : theSorter.viewIterator();
    }

    @Override
    public TethysSwingTableManager<C, R> setFilter(final Predicate<R> pFilter) {
        super.setFilter(pFilter);
        theSorter.setFilter(pFilter);
        return this;
    }

    @Override
    public TethysSwingTableManager<C, R> setComparator(final Comparator<R> pComparator) {
        super.setComparator(pComparator);
        theSorter.setComparator(getComparator());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TethysSwingTableColumn<?, C, R> getColumn(final C pId) {
        return (TethysSwingTableColumn<?, C, R>) super.getColumn(pId);
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
    TethysSwingTableModel getTableModel() {
        return theModel;
    }

    /**
     * Obtain the columnList.
     *
     * @return the columnModel
     */
    List<R> getItems() {
        return theItems;
    }

    /**
     * Obtain the columnList.
     *
     * @return the columnModel
     */
    List<TethysSwingTableColumn<?, C, R>> getColumnList() {
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
    TethysSwingTableCellFactory<C, R> getCellFactory() {
        return theCellFactory;
    }

    /**
     * Obtain the sorter.
     *
     * @return the sorter
     */
    TethysSwingTableSorter<R> getSorter() {
        return theSorter;
    }

    /**
     * Obtain the column for the model index.
     *
     * @param pIndex the index of the column
     * @return the table column
     */
    TethysSwingTableColumn<?, C, R> getIndexedColumn(final int pIndex) {
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
        if (theItems == null) {
            return null;
        }
        if (pIndex < 0
                || pIndex > theItems.size()) {
            throw new IllegalArgumentException();
        }
        return theItems.get(pIndex);
    }

    @Override
    public void cancelEditing() {
        if (theTable.isEditing()) {
            theTable.getCellEditor().cancelCellEditing();
        }
    }

    /**
     * Set the table items.
     *
     * @param pItems the items
     */
    public void setItems(final List<R> pItems) {
        theItems = pItems;
        theModel.fireTableDataChanged();
    }

    @Override
    public TethysSwingTableStringColumn<C, R> declareStringColumn(final C pId) {
        return new TethysSwingTableStringColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableCharArrayColumn<C, R> declareCharArrayColumn(final C pId) {
        return new TethysSwingTableCharArrayColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableShortColumn<C, R> declareShortColumn(final C pId) {
        return new TethysSwingTableShortColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableIntegerColumn<C, R> declareIntegerColumn(final C pId) {
        return new TethysSwingTableIntegerColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableLongColumn<C, R> declareLongColumn(final C pId) {
        return new TethysSwingTableLongColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableRawDecimalColumn<C, R> declareRawDecimalColumn(final C pId) {
        return new TethysSwingTableRawDecimalColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableMoneyColumn<C, R> declareMoneyColumn(final C pId) {
        return new TethysSwingTableMoneyColumn<>(this, pId);
    }

    @Override
    public TethysSwingTablePriceColumn<C, R> declarePriceColumn(final C pId) {
        return new TethysSwingTablePriceColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableRateColumn<C, R> declareRateColumn(final C pId) {
        return new TethysSwingTableRateColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableUnitsColumn<C, R> declareUnitsColumn(final C pId) {
        return new TethysSwingTableUnitsColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableDilutionColumn<C, R> declareDilutionColumn(final C pId) {
        return new TethysSwingTableDilutionColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableRatioColumn<C, R> declareRatioColumn(final C pId) {
        return new TethysSwingTableRatioColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableDilutedPriceColumn<C, R> declareDilutedPriceColumn(final C pId) {
        return new TethysSwingTableDilutedPriceColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableDateColumn<C, R> declareDateColumn(final C pId) {
        return new TethysSwingTableDateColumn<>(this, pId);
    }

    @Override
    public <T> TethysSwingTableScrollColumn<T, C, R> declareScrollColumn(final C pId,
                                                                         final Class<T> pClazz) {
        return new TethysSwingTableScrollColumn<>(this, pId, pClazz);
    }

    @Override
    public <T extends Comparable<T>> TethysSwingTableListColumn<T, C, R> declareListColumn(final C pId,
                                                                                           final Class<T> pClazz) {
        return new TethysSwingTableListColumn<>(this, pId, pClazz);
    }

    @Override
    public <T> TethysSwingTableIconColumn<T, C, R> declareIconColumn(final C pId,
                                                                     final Class<T> pClazz) {
        return new TethysSwingTableIconColumn<>(this, pId, pClazz);
    }

    /**
     * Fire TableData changed.
     */
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
        final int iModel = theItems.indexOf(pItem);
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
    protected class TethysSwingTableModel
            extends AbstractTableModel
            implements TethysSwingTableSorterModel<R> {
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

    /**
     * Column Definition.
     *
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysSwingTableColumn<T, C, R>
            extends TethysBaseTableColumn<T, C, R> {
        /**
         * The underlying column.
         */
        private final TableColumn theColumn;

        /**
         * The table cell.
         */
        private TethysSwingTableCell<T, C, R> theCell;

        /**
         * Cell value factory.
         */
        private Function<R, T> theValueFactory;

        /**
         * Constructor.
         *
         * @param pTable the owning table
         * @param pId    the column id
         * @param pType  the type of the column
         */
        TethysSwingTableColumn(final TethysSwingTableManager<C, R> pTable,
                               final C pId,
                               final TethysFieldType pType) {
            /* Initialise underlying class */
            super(pTable, pId, pType);

            /* Determine the model index of the column */
            final int myIndex = pTable.getColumnList().size();

            /* Create the column and add to the table */
            theColumn = new TableColumn(myIndex);
            theColumn.setHeaderValue(pId);
            pTable.getColumnModel().addColumn(theColumn);
            pTable.getColumnList().add(this);

            /* Configure the column */
            setColumnWidth(getCellType().getDefaultWidth());

            /* Set default value factory */
            theValueFactory = e -> null;
        }

        /**
         * Set cell value Factory.
         *
         * @param pFactory the cell factory
         * @return the column
         */
        public TethysSwingTableColumn<T, C, R> setCellValueFactory(final Function<R, T> pFactory) {
            theValueFactory = pFactory;
            return this;
        }

        /**
         * Declare cell.
         *
         * @param pCell the cell
         */
        void declareCell(final TethysSwingTableCell<T, C, R> pCell) {
            theCell = pCell;
            theColumn.setCellRenderer(theCell.getRenderer());
            theColumn.setCellEditor(theCell.getEditor());
        }

        @Override
        public TethysSwingTableManager<C, R> getTable() {
            return (TethysSwingTableManager<C, R>) super.getTable();
        }

        @Override
        protected void attachToTable() {
            /* Add the column to the end of the list */
            final TableColumnModel myColumns = getTable().getColumnModel();
            myColumns.addColumn(theColumn);

            /* Determine the intended new index and current index */
            final int myNewIndex = countPreviousVisibleSiblings();
            final int myCurIndex = myColumns.getColumnCount() - 1;

            /* Move column if necessary */
            if (myCurIndex != myNewIndex) {
                myColumns.moveColumn(myCurIndex, myNewIndex);
            }
        }

        @Override
        protected void detachFromTable() {
            getTable().getColumnModel().removeColumn(theColumn);
        }

        /**
         * Is the cell editable?
         *
         * @param pIndex the row index
         * @return true/false
         */
        boolean isCellEditable(final int pIndex) {
            return theCell.isCellEditable(pIndex);
        }

        /**
         * Obtain the cell value.
         *
         * @param pIndex the row index
         * @return the value
         */
        T getCellValue(final int pIndex) {
            theCell.setActiveRow(pIndex);
            return theValueFactory.apply(theCell.getActiveRow());
        }

        /**
         * Set the cell value.
         *
         * @param pIndex the row index
         * @param pValue the new value
         */
        void setCellValue(final int pIndex,
                          final Object pValue) {
            /* Set the active row */
            theCell.setActiveRow(pIndex);

            /* Handle exceptions */
            try {
                /* Call the commit hook */
                processOnCommit(theCell.getActiveRow(), theCell.getCastValue(pValue));
                getTable().processOnCellEditState(Boolean.FALSE);

                /* If we had an exception, report it */
            } catch (OceanusException e) {
                getTable().processOnCommitError(e);
            }
        }

        /**
         * Obtain the row for the index.
         *
         * @param pIndex the row index
         * @return true/false
         */
        R getRowForIndex(final int pIndex) {
            return getTable().getIndexedRow(pIndex);
        }

        @Override
        public TethysSwingTableColumn<T, C, R> setColumnWidth(final int pWidth) {
            theColumn.setPreferredWidth(pWidth);
            return this;
        }

        @Override
        public TethysSwingTableColumn<T, C, R> setName(final String pName) {
            super.setName(pName);
            theColumn.setHeaderValue(pName);
            return this;
        }

        /**
         * Obtain the column index.
         *
         * @return the index
         */
        int getColumnIndex() {
            return theColumn.getModelIndex();
        }
    }

    /**
     * Column Definition.
     *
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysSwingTableValidatedColumn<T, C, R>
            extends TethysSwingTableColumn<T, C, R>
            implements TethysTableValidatedColumn<T, R> {
        /**
         * The validator.
         */
        private BiFunction<T, R, String> theValidator;

        /**
         * Constructor.
         *
         * @param pTable the containing table
         * @param pId    the id of the column
         * @param pType  the type of the column
         */
        TethysSwingTableValidatedColumn(final TethysSwingTableManager<C, R> pTable,
                                        final C pId,
                                        final TethysFieldType pType) {
            /* Call super-constructor */
            super(pTable, pId, pType);

            /* Initialise validator */
            theValidator = (t, r) -> null;
        }

        @Override
        public TethysSwingTableValidatedColumn<T, C, R> setValidator(final BiFunction<T, R, String> pValidator) {
            theValidator = pValidator;
            return this;
        }

        /**
         * Obtain the validator.
         *
         * @return the validator
         */
        BiFunction<T, R, String> getValidator() {
            return theValidator;
        }
    }

    /**
     * String Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableStringColumn<C, R>
            extends TethysSwingTableValidatedColumn<String, C, R>
            implements TethysTableStringColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableStringColumn(final TethysSwingTableManager<C, R> pTable,
                                     final C pId) {
            super(pTable, pId, TethysFieldType.STRING);
            declareCell(getTable().getCellFactory().stringCell(this));
        }

        @Override
        public TethysSwingTableStringColumn<C, R> setValidator(final BiFunction<String, R, String> pValidator) {
            return (TethysSwingTableStringColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * CharArray Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableCharArrayColumn<C, R>
            extends TethysSwingTableValidatedColumn<char[], C, R>
            implements TethysTableCharArrayColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableCharArrayColumn(final TethysSwingTableManager<C, R> pTable,
                                        final C pId) {
            super(pTable, pId, TethysFieldType.CHARARRAY);
            declareCell(getTable().getCellFactory().charArrayCell(this));
        }

        @Override
        public TethysSwingTableCharArrayColumn<C, R> setValidator(final BiFunction<char[], R, String> pValidator) {
            return (TethysSwingTableCharArrayColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Short Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableShortColumn<C, R>
            extends TethysSwingTableValidatedColumn<Short, C, R>
            implements TethysTableShortColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableShortColumn(final TethysSwingTableManager<C, R> pTable,
                                    final C pId) {
            super(pTable, pId, TethysFieldType.SHORT);
            declareCell(getTable().getCellFactory().shortCell(this));
        }

        @Override
        public TethysSwingTableShortColumn<C, R> setValidator(final BiFunction<Short, R, String> pValidator) {
            return (TethysSwingTableShortColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Integer Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIntegerColumn<C, R>
            extends TethysSwingTableValidatedColumn<Integer, C, R>
            implements TethysTableIntegerColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableIntegerColumn(final TethysSwingTableManager<C, R> pTable,
                                      final C pId) {
            super(pTable, pId, TethysFieldType.INTEGER);
            declareCell(getTable().getCellFactory().integerCell(this));
        }

        @Override
        public TethysSwingTableIntegerColumn<C, R> setValidator(final BiFunction<Integer, R, String> pValidator) {
            return (TethysSwingTableIntegerColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Long Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableLongColumn<C, R>
            extends TethysSwingTableValidatedColumn<Long, C, R>
            implements TethysTableLongColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableLongColumn(final TethysSwingTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysFieldType.LONG);
            declareCell(getTable().getCellFactory().longCell(this));
        }

        @Override
        public TethysSwingTableLongColumn<C, R> setValidator(final BiFunction<Long, R, String> pValidator) {
            return (TethysSwingTableLongColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * RawDecimal Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRawDecimalColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysDecimal, C, R>
            implements TethysTableRawDecimalColumn<C, R> {
        /**
         * Raw decimals supplier.
         */
        private ToIntFunction<R> theSupplier;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableRawDecimalColumn(final TethysSwingTableManager<C, R> pTable,
                                         final C pId) {
            super(pTable, pId, TethysFieldType.DECIMAL);
            declareCell(getTable().getCellFactory().rawDecimalCell(this));
            theSupplier = p -> TethysRawDecimalEditConverter.DEFAULT_DECIMALS;
        }

        @Override
        public TethysSwingTableRawDecimalColumn<C, R> setValidator(final BiFunction<TethysDecimal, R, String> pValidator) {
            return (TethysSwingTableRawDecimalColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysSwingTableRawDecimalColumn<C, R> setNumDecimals(final ToIntFunction<R> pSupplier) {
            theSupplier = pSupplier;
            return this;
        }

        /**
         * Obtain the raw decimals supplier.
         *
         * @return the supplier
         */
        ToIntFunction<R> getNumDecimals() {
            return theSupplier;
        }
    }

    /**
     * Money Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableMoneyColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysMoney, C, R>
            implements TethysTableMoneyColumn<C, R> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableMoneyColumn(final TethysSwingTableManager<C, R> pTable,
                                    final C pId) {
            super(pTable, pId, TethysFieldType.MONEY);
            declareCell(getTable().getCellFactory().moneyCell(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysSwingTableMoneyColumn<C, R> setValidator(final BiFunction<TethysMoney, R, String> pValidator) {
            return (TethysSwingTableMoneyColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysSwingTableMoneyColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
            theSupplier = pSupplier;
            return this;
        }

        /**
         * Obtain the currency supplier.
         *
         * @return the supplier
         */
        Function<R, Currency> getDeemedCurrency() {
            return theSupplier;
        }
    }

    /**
     * Price Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTablePriceColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysPrice, C, R>
            implements TethysTablePriceColumn<C, R> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTablePriceColumn(final TethysSwingTableManager<C, R> pTable,
                                    final C pId) {
            super(pTable, pId, TethysFieldType.PRICE);
            declareCell(getTable().getCellFactory().priceCell(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysSwingTablePriceColumn<C, R> setValidator(final BiFunction<TethysPrice, R, String> pValidator) {
            return (TethysSwingTablePriceColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysSwingTablePriceColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
            theSupplier = pSupplier;
            return this;
        }

        /**
         * Obtain the currency supplier.
         *
         * @return the supplier
         */
        Function<R, Currency> getDeemedCurrency() {
            return theSupplier;
        }
    }

    /**
     * Rate Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRateColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysRate, C, R>
            implements TethysTableRateColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableRateColumn(final TethysSwingTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysFieldType.RATE);
            declareCell(getTable().getCellFactory().rateCell(this));
        }

        @Override
        public TethysSwingTableRateColumn<C, R> setValidator(final BiFunction<TethysRate, R, String> pValidator) {
            return (TethysSwingTableRateColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Units Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableUnitsColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysUnits, C, R>
            implements TethysTableUnitsColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableUnitsColumn(final TethysSwingTableManager<C, R> pTable,
                                    final C pId) {
            super(pTable, pId, TethysFieldType.UNITS);
            declareCell(getTable().getCellFactory().unitsCell(this));
        }

        @Override
        public TethysSwingTableUnitsColumn<C, R> setValidator(final BiFunction<TethysUnits, R, String> pValidator) {
            return (TethysSwingTableUnitsColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Dilution Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutionColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysDilution, C, R>
            implements TethysTableDilutionColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableDilutionColumn(final TethysSwingTableManager<C, R> pTable,
                                       final C pId) {
            super(pTable, pId, TethysFieldType.DILUTION);
            declareCell(getTable().getCellFactory().dilutionCell(this));
        }

        @Override
        public TethysSwingTableDilutionColumn<C, R> setValidator(final BiFunction<TethysDilution, R, String> pValidator) {
            return (TethysSwingTableDilutionColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Ratio Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRatioColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysRatio, C, R>
            implements TethysTableRatioColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableRatioColumn(final TethysSwingTableManager<C, R> pTable,
                                    final C pId) {
            super(pTable, pId, TethysFieldType.RATIO);
            declareCell(getTable().getCellFactory().ratioCell(this));
        }

        @Override
        public TethysSwingTableRatioColumn<C, R> setValidator(final BiFunction<TethysRatio, R, String> pValidator) {
            return (TethysSwingTableRatioColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * DilutedPrice Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutedPriceColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysDilutedPrice, C, R>
            implements TethysTableDilutedPriceColumn<C, R> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableDilutedPriceColumn(final TethysSwingTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.DILUTEDPRICE);
            declareCell(getTable().getCellFactory().dilutedPriceCell(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysSwingTableDilutedPriceColumn<C, R> setValidator(final BiFunction<TethysDilutedPrice, R, String> pValidator) {
            return (TethysSwingTableDilutedPriceColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysSwingTableDilutedPriceColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
            theSupplier = pSupplier;
            return this;
        }

        /**
         * Obtain the currency supplier.
         *
         * @return the supplier
         */
        Function<R, Currency> getDeemedCurrency() {
            return theSupplier;
        }
    }

    /**
     * Date Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDateColumn<C, R>
            extends TethysSwingTableColumn<TethysDate, C, R>
            implements TethysTableDateColumn<C, R> {
        /**
         * Date configurator.
         */
        private BiConsumer<R, TethysDateConfig> theConfigurator;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysSwingTableDateColumn(final TethysSwingTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysFieldType.DATE);
            declareCell(getTable().getCellFactory().dateCell(this));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public TethysSwingTableDateColumn<C, R> setDateConfigurator(final BiConsumer<R, TethysDateConfig> pConfigurator) {
            theConfigurator = pConfigurator;
            return this;
        }

        /**
         * Obtain the date configurator.
         *
         * @return the configurator
         */
        BiConsumer<R, TethysDateConfig> getDateConfigurator() {
            return theConfigurator;
        }
    }

    /**
     * Scroll Column.
     *
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableScrollColumn<T, C, R>
            extends TethysSwingTableColumn<T, C, R>
            implements TethysTableScrollColumn<T, C, R> {
        /**
         * Menu configurator.
         */
        private BiConsumer<R, TethysScrollMenu<T>> theConfigurator;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         * @param pClazz the item class
         */
        TethysSwingTableScrollColumn(final TethysSwingTableManager<C, R> pTable,
                                     final C pId,
                                     final Class<T> pClazz) {
            super(pTable, pId, TethysFieldType.SCROLL);
            declareCell(getTable().getCellFactory().scrollCell(this, pClazz));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public TethysSwingTableScrollColumn<T, C, R> setMenuConfigurator(final BiConsumer<R, TethysScrollMenu<T>> pConfigurator) {
            theConfigurator = pConfigurator;
            return this;
        }

        /**
         * Obtain the menu configurator.
         *
         * @return the configurator
         */
        BiConsumer<R, TethysScrollMenu<T>> getMenuConfigurator() {
            return theConfigurator;
        }
    }

    /**
     * List Column.
     *
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableListColumn<T extends Comparable<T>, C, R>
            extends TethysSwingTableColumn<List<T>, C, R>
            implements TethysTableListColumn<T, C, R> {
        /**
         * Selectable supplier.
         */
        private Function<R, Iterator<T>> theSelectables;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         * @param pClazz the data class
         */
        TethysSwingTableListColumn(final TethysSwingTableManager<C, R> pTable,
                                   final C pId,
                                   final Class<T> pClazz) {
            super(pTable, pId, TethysFieldType.LIST);
            declareCell(getTable().getCellFactory().listCell(this));
            theSelectables = r -> Collections.emptyIterator();
        }

        @Override
        public TethysSwingTableListColumn<T, C, R> setSelectables(final Function<R, Iterator<T>> pSelectables) {
            theSelectables = pSelectables;
            return this;
        }

        /**
         * Obtain the selectable supplier.
         *
         * @return the supplier
         */
        Function<R, Iterator<T>> getSelectables() {
            return theSelectables;
        }
    }

    /**
     * Icon Column.
     *
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIconColumn<T, C, R>
            extends TethysSwingTableColumn<T, C, R>
            implements TethysTableIconColumn<T, C, R> {
        /**
         * IconMapSet supplier.
         */
        private Function<R, TethysIconMapSet<T>> theSupplier;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         * @param pClazz the item class
         */
        TethysSwingTableIconColumn(final TethysSwingTableManager<C, R> pTable,
                                   final C pId,
                                   final Class<T> pClazz) {
            super(pTable, pId, TethysFieldType.ICON);
            declareCell(getTable().getCellFactory().iconCell(this, pClazz));
            theSupplier = p -> null;
        }

        @Override
        public TethysSwingTableIconColumn<T, C, R> setIconMapSet(final Function<R, TethysIconMapSet<T>> pSupplier) {
            theSupplier = pSupplier;
            return this;
        }

        /**
         * Obtain the mapSet supplier.
         *
         * @return the supplier
         */
        Function<R, TethysIconMapSet<T>> getIconMapSet() {
            return theSupplier;
        }
    }
}
