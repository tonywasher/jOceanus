/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.swing;

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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableCell;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableSorter.TethysSwingTableSorterModel;

/**
 * JavaSwing Table manager.
 * @param <C> the column identity
 * @param <R> the row type
 */
public class TethysSwingTableManager<C, R>
        extends TethysTableManager<C, R, JComponent, Icon> {
    /**
     * The Scroll Pane.
     */
    private final JScrollPane theScroll;

    /**
     * The TableView.
     */
    private final JTable theTable;

    /**
     * The Columns.
     */
    private final TableColumnModel theColumns;

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
     * @param pFactory the GUI factory
     */
    protected TethysSwingTableManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create fields */
        theColumnList = new ArrayList<>();
        theModel = new TethysSwingTableModel();
        theTable = new JTable(theModel);
        theColumns = theTable.getColumnModel();
        theCellFactory = new TethysSwingTableCellFactory<>(pFactory);
        theSorter = new TethysSwingTableSorter<>(theModel);
        theTable.setRowSorter(theSorter);

        /* Listen to factory */
        theCellFactory.getEventRegistrar().addEventListener(this::cascadeEvent);

        /* Listen to the valueSet */
        pFactory.getValueSet().getEventRegistrar().addEventListener(e -> theModel.fireTableDataChanged());

        /* Create the scrollPane */
        theScroll = new JScrollPane();
        theScroll.setViewportView(theTable);
    }

    @Override
    public JComponent getNode() {
        return theScroll;
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
    public void setFilter(final Predicate<R> pFilter) {
        super.setFilter(pFilter);
        theSorter.setFilter(pFilter);
    }

    @Override
    public void setComparator(final Comparator<R> pComparator) {
        super.setComparator(pComparator);
        theSorter.setComparator(getComparator());
    }

    @Override
    public TethysSwingTableColumn<?, C, R> getColumn(final C pId) {
        return (TethysSwingTableColumn<?, C, R>) super.getColumn(pId);
    }

    /**
     * Obtain the table model.
     * @return the table model
     */
    protected TethysSwingTableModel getTableModel() {
        return theModel;
    }

    /**
     * Obtain the column for the model index.
     * @param pIndex the index of the column
     * @return the table column
     */
    private TethysSwingTableColumn<?, C, R> getIndexedColumn(final int pIndex) {
        if ((pIndex < 0)
            || (pIndex > theColumnList.size())) {
            throw new IllegalArgumentException();
        }
        return theColumnList.get(pIndex);
    }

    /**
     * Obtain the row for the model index.
     * @param pIndex the index of the column
     * @return the table column
     */
    private R getIndexedRow(final int pIndex) {
        if (theItems == null) {
            return null;
        }
        if ((pIndex < 0)
            || (pIndex > theItems.size())) {
            throw new IllegalArgumentException();
        }
        return theItems.get(pIndex);
    }

    /**
     * Set the table items.
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
                                                                         final Class<T> pClass) {
        return new TethysSwingTableScrollColumn<>(this, pId, pClass);
    }

    @Override
    public <T> TethysSwingTableListColumn<T, C, R> declareListColumn(final C pId,
                                                                     final Class<T> pClass) {
        return new TethysSwingTableListColumn<>(this, pId, pClass);
    }

    @Override
    public <T> TethysSwingTableIconColumn<T, C, R> declareIconColumn(final C pId,
                                                                     final Class<T> pClass) {
        return new TethysSwingTableIconColumn<>(this, pId, pClass);
    }

    /**
     * Fire TableData changed.
     */
    public void fireTableDataChanged() {
        theModel.fireTableDataChanged();
    }

    /**
     * Fire TableRow added.
     * @param pRowIndex the index of the row that has been added
     */
    public void fireTableRowAdded(final int pRowIndex) {
        theModel.fireTableRowsInserted(pRowIndex, pRowIndex);
    }

    /**
     * Fire TableRow changed.
     * @param pRowIndex the index of the row that has changed
     */
    public void fireTableRowChanged(final int pRowIndex) {
        theModel.fireTableRowUpdated(pRowIndex);
    }

    /**
     * Fire TableRow deleted.
     * @param pRowIndex the index of the row that has been deleted
     */
    public void fireTableRowDeleted(final int pRowIndex) {
        theModel.fireTableRowsDeleted(pRowIndex, pRowIndex);
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
            return theItems == null
                                    ? 0
                                    : theItems.size();
        }

        @Override
        public int getColumnCount() {
            return theColumnList.size();
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
            theSorter.reportMappingChanged();
        }

        /**
         * Notify model that the row has been updated.
         * @param pRowIndex the row index
         */
        protected void fireTableRowUpdated(final int pRowIndex) {
            fireTableRowsUpdated(pRowIndex, pRowIndex);
        }
    }

    /**
     * Column Definition.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysSwingTableColumn<T, C, R>
            extends TethysBaseTableColumn<T, C, R, JComponent, Icon> {
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
         * Cell commit factory.
         */
        private BiConsumer<R, T> theCommitFactory;

        /**
         * Constructor.
         * @param pTable the owning table
         * @param pId the column id
         * @param pType the type of the column
         */
        protected TethysSwingTableColumn(final TethysSwingTableManager<C, R> pTable,
                                         final C pId,
                                         final TethysFieldType pType) {
            /* Initialise underlying class */
            super(pTable, pId, pType);

            /* Determine the model index of the column */
            int myIndex = pTable.theColumnList.size();

            /* Create the column and add to the table */
            theColumn = new TableColumn(myIndex);
            theColumn.setHeaderValue(pId);
            pTable.theColumns.addColumn(theColumn);
            pTable.theColumnList.add(this);

            /* Configure the column */
            setColumnWidth(getCellType().getDefaultWidth());

            /* Set default value factory */
            theValueFactory = e -> null;
        }

        /**
         * Set cell value Factory.
         * @param pFactory the cell factory
         */
        public void setCellValueFactory(final Function<R, T> pFactory) {
            theValueFactory = pFactory;
        }

        /**
         * Set cell commit Factory.
         * @param pFactory the cell factory
         */
        public void setCellCommitFactory(final BiConsumer<R, T> pFactory) {
            theCommitFactory = pFactory;
        }

        /**
         * Declare cell.
         * @param pCell the cell
         */
        protected void declareCell(final TethysSwingTableCell<T, C, R> pCell) {
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
            TableColumnModel myColumns = getTable().theColumns;
            myColumns.addColumn(theColumn);

            /* Determine the intended new index and current index */
            int myNewIndex = countPreviousVisibleSiblings();
            int myCurIndex = myColumns.getColumnCount() - 1;

            /* Move column if necessary */
            if (myCurIndex != myNewIndex) {
                myColumns.moveColumn(myCurIndex, myNewIndex);
            }
        }

        @Override
        protected void detachFromTable() {
            getTable().theColumns.removeColumn(theColumn);
        }

        /**
         * Is the cell editable?
         * @param pIndex the row index
         * @return true/false
         */
        private boolean isCellEditable(final int pIndex) {
            return theCell.isCellEditable(pIndex);
        }

        /**
         * Obtain the cell value.
         * @param pIndex the row index
         * @return the value
         */
        private T getCellValue(final int pIndex) {
            theCell.setActiveRow(pIndex);
            return theValueFactory.apply(theCell.getActiveRow());
        }

        /**
         * Set the cell value.
         * @param pIndex the row index
         * @param pValue the new value
         */
        private void setCellValue(final int pIndex,
                                  final Object pValue) {
            theCell.setActiveRow(pIndex);
            if (theCommitFactory != null) {
                theCommitFactory.accept(theCell.getActiveRow(), theCell.getCastValue(pValue));
            }
        }

        /**
         * Obtain the row for the index.
         * @param pIndex the row index
         * @return true/false
         */
        protected R getRowForIndex(final int pIndex) {
            return getTable().getIndexedRow(pIndex);
        }

        @Override
        public void setColumnWidth(final int pWidth) {
            theColumn.setPreferredWidth(pWidth);
        }

        @Override
        public void setName(final String pName) {
            super.setName(pName);
            theColumn.setHeaderValue(pName);
        }

        /**
         * Obtain the column index.
         * @return the index
         */
        protected int getColumnIndex() {
            return theColumn.getModelIndex();
        }
    }

    /**
     * Column Definition.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysSwingTableValidatedColumn<T, C, R>
            extends TethysSwingTableColumn<T, C, R>
            implements TethysTableValidatedColumn<T, C, R, JComponent, Icon> {
        /**
         * The validator.
         */
        private BiFunction<T, R, String> theValidator;

        /**
         * Constructor.
         * @param pTable the containing table
         * @param pId the id of the column
         * @param pType the type of the column
         */
        protected TethysSwingTableValidatedColumn(final TethysSwingTableManager<C, R> pTable,
                                                  final C pId,
                                                  final TethysFieldType pType) {
            /* Call super-constructor */
            super(pTable, pId, pType);

            /* Initialise validator */
            theValidator = (t, r) -> null;
        }

        @Override
        public void setValidator(final BiFunction<T, R, String> pValidator) {
            theValidator = pValidator;
        }

        @Override
        public BiFunction<T, R, String> getValidator() {
            return theValidator;
        }
    }

    /**
     * String Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableStringColumn<C, R>
            extends TethysSwingTableValidatedColumn<String, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableStringColumn(final TethysSwingTableManager<C, R> pTable,
                                               final C pId) {
            super(pTable, pId, TethysFieldType.STRING);
            declareCell(getTable().theCellFactory.stringCell(this));
        }
    }

    /**
     * CharArray Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableCharArrayColumn<C, R>
            extends TethysSwingTableValidatedColumn<char[], C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableCharArrayColumn(final TethysSwingTableManager<C, R> pTable,
                                                  final C pId) {
            super(pTable, pId, TethysFieldType.CHARARRAY);
            declareCell(getTable().theCellFactory.charArrayCell(this));
        }
    }

    /**
     * Short Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableShortColumn<C, R>
            extends TethysSwingTableValidatedColumn<Short, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableShortColumn(final TethysSwingTableManager<C, R> pTable,
                                              final C pId) {
            super(pTable, pId, TethysFieldType.SHORT);
            declareCell(getTable().theCellFactory.shortCell(this));
        }
    }

    /**
     * Integer Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIntegerColumn<C, R>
            extends TethysSwingTableValidatedColumn<Integer, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableIntegerColumn(final TethysSwingTableManager<C, R> pTable,
                                                final C pId) {
            super(pTable, pId, TethysFieldType.INTEGER);
            declareCell(getTable().theCellFactory.integerCell(this));
        }
    }

    /**
     * Long Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableLongColumn<C, R>
            extends TethysSwingTableValidatedColumn<Long, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableLongColumn(final TethysSwingTableManager<C, R> pTable,
                                             final C pId) {
            super(pTable, pId, TethysFieldType.LONG);
            declareCell(getTable().theCellFactory.longCell(this));
        }
    }

    /**
     * RawDecimal Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRawDecimalColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysDecimal, C, R>
            implements TethysTableRawDecimalColumn<C, R, JComponent, Icon> {
        /**
         * Raw decimals supplier.
         */
        private Function<R, Integer> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableRawDecimalColumn(final TethysSwingTableManager<C, R> pTable,
                                                   final C pId) {
            super(pTable, pId, TethysFieldType.DECIMAL);
            declareCell(getTable().theCellFactory.rawDecimalCell(this));
            theSupplier = p -> TethysRawDecimalEditConverter.DEFAULT_DECIMALS;
        }

        @Override
        public void setNumDecimals(final Function<R, Integer> pSupplier) {
            theSupplier = pSupplier;
        }

        /**
         * Obtain the raw decimals supplier.
         * @return the supplier
         */
        protected Function<R, Integer> getNumDecimals() {
            return theSupplier;
        }
    }

    /**
     * Money Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableMoneyColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysMoney, C, R>
            implements TethysTableCurrencyColumn<TethysMoney, C, R, JComponent, Icon> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableMoneyColumn(final TethysSwingTableManager<C, R> pTable,
                                              final C pId) {
            super(pTable, pId, TethysFieldType.MONEY);
            declareCell(getTable().theCellFactory.moneyCell(this));
            theSupplier = p -> null;
        }

        @Override
        public void setDeemedCurrency(final Function<R, Currency> pSupplier) {
            theSupplier = pSupplier;
        }

        /**
         * Obtain the currency supplier.
         * @return the supplier
         */
        protected Function<R, Currency> getDeemedCurrency() {
            return theSupplier;
        }
    }

    /**
     * Price Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTablePriceColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysPrice, C, R>
            implements TethysTableCurrencyColumn<TethysPrice, C, R, JComponent, Icon> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTablePriceColumn(final TethysSwingTableManager<C, R> pTable,
                                              final C pId) {
            super(pTable, pId, TethysFieldType.PRICE);
            declareCell(getTable().theCellFactory.priceCell(this));
            theSupplier = p -> null;
        }

        @Override
        public void setDeemedCurrency(final Function<R, Currency> pSupplier) {
            theSupplier = pSupplier;
        }

        /**
         * Obtain the currency supplier.
         * @return the supplier
         */
        protected Function<R, Currency> getDeemedCurrency() {
            return theSupplier;
        }
    }

    /**
     * Rate Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRateColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysRate, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableRateColumn(final TethysSwingTableManager<C, R> pTable,
                                             final C pId) {
            super(pTable, pId, TethysFieldType.RATE);
            declareCell(getTable().theCellFactory.rateCell(this));
        }
    }

    /**
     * Units Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableUnitsColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysUnits, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableUnitsColumn(final TethysSwingTableManager<C, R> pTable,
                                              final C pId) {
            super(pTable, pId, TethysFieldType.UNITS);
            declareCell(getTable().theCellFactory.unitsCell(this));
        }
    }

    /**
     * Dilution Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutionColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysDilution, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableDilutionColumn(final TethysSwingTableManager<C, R> pTable,
                                                 final C pId) {
            super(pTable, pId, TethysFieldType.DILUTION);
            declareCell(getTable().theCellFactory.dilutionCell(this));
        }
    }

    /**
     * Ratio Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRatioColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysRatio, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableRatioColumn(final TethysSwingTableManager<C, R> pTable,
                                              final C pId) {
            super(pTable, pId, TethysFieldType.RATIO);
            declareCell(getTable().theCellFactory.ratioCell(this));
        }
    }

    /**
     * DilutedPrice Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutedPriceColumn<C, R>
            extends TethysSwingTableValidatedColumn<TethysDilutedPrice, C, R>
            implements TethysTableCurrencyColumn<TethysDilutedPrice, C, R, JComponent, Icon> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableDilutedPriceColumn(final TethysSwingTableManager<C, R> pTable,
                                                     final C pId) {
            super(pTable, pId, TethysFieldType.DILUTEDPRICE);
            declareCell(getTable().theCellFactory.dilutedPriceCell(this));
            theSupplier = p -> null;
        }

        @Override
        public void setDeemedCurrency(final Function<R, Currency> pSupplier) {
            theSupplier = pSupplier;
        }

        /**
         * Obtain the currency supplier.
         * @return the supplier
         */
        protected Function<R, Currency> getDeemedCurrency() {
            return theSupplier;
        }
    }

    /**
     * Date Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDateColumn<C, R>
            extends TethysSwingTableColumn<TethysDate, C, R>
            implements TethysTableDateColumn<C, R, JComponent, Icon> {
        /**
         * Date configurator.
         */
        private BiConsumer<R, TethysDateConfig> theConfigurator;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableDateColumn(final TethysSwingTableManager<C, R> pTable,
                                             final C pId) {
            super(pTable, pId, TethysFieldType.DATE);
            declareCell(getTable().theCellFactory.dateCell(this));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public void setDateConfigurator(final BiConsumer<R, TethysDateConfig> pConfigurator) {
            theConfigurator = pConfigurator;
        }

        /**
         * Obtain the date configurator.
         * @return the configurator
         */
        protected BiConsumer<R, TethysDateConfig> getDateConfigurator() {
            return theConfigurator;
        }
    }

    /**
     * Scroll Column.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableScrollColumn<T, C, R>
            extends TethysSwingTableColumn<T, C, R>
            implements TethysTableScrollColumn<T, C, R, JComponent, Icon> {
        /**
         * Menu configurator.
         */
        private BiConsumer<R, TethysScrollMenu<T, Icon>> theConfigurator;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysSwingTableScrollColumn(final TethysSwingTableManager<C, R> pTable,
                                               final C pId,
                                               final Class<T> pClass) {
            super(pTable, pId, TethysFieldType.SCROLL);
            declareCell(getTable().theCellFactory.scrollCell(this, pClass));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public void setMenuConfigurator(final BiConsumer<R, TethysScrollMenu<T, Icon>> pConfigurator) {
            theConfigurator = pConfigurator;
        }

        /**
         * Obtain the menu configurator.
         * @return the configurator
         */
        protected BiConsumer<R, TethysScrollMenu<T, Icon>> getMenuConfigurator() {
            return theConfigurator;
        }
    }

    /**
     * List Column.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableListColumn<T, C, R>
            extends TethysSwingTableColumn<TethysItemList<T>, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysSwingTableListColumn(final TethysSwingTableManager<C, R> pTable,
                                             final C pId,
                                             final Class<T> pClass) {
            super(pTable, pId, TethysFieldType.LIST);
            declareCell(getTable().theCellFactory.listCell(this, pClass));
        }
    }

    /**
     * Icon Column.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIconColumn<T, C, R>
            extends TethysSwingTableColumn<T, C, R>
            implements TethysTableIconColumn<T, C, R, JComponent, Icon> {
        /**
         * IconMapSet supplier.
         */
        private Function<R, TethysIconMapSet<T>> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysSwingTableIconColumn(final TethysSwingTableManager<C, R> pTable,
                                             final C pId,
                                             final Class<T> pClass) {
            super(pTable, pId, TethysFieldType.ICON);
            declareCell(getTable().theCellFactory.iconCell(this, pClass));
            theSupplier = p -> null;
        }

        @Override
        public void setIconMapSet(final Function<R, TethysIconMapSet<T>> pSupplier) {
            theSupplier = pSupplier;
        }

        /**
         * Obtain the mapSet supplier.
         * @return the supplier
         */
        protected Function<R, TethysIconMapSet<T>> getIconMapSet() {
            return theSupplier;
        }
    }
}
