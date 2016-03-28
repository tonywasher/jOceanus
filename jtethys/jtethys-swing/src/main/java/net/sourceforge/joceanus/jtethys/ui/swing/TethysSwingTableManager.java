/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldType;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableCell;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableSorter.TethysSwingTableSorterModel;

/**
 * JavaSwing Table manager.
 * @param <I> the column identity
 * @param <R> the row type
 */
public class TethysSwingTableManager<I, R>
        extends TethysTableManager<I, R, JComponent> {
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
    private final List<TethysSwingTableColumn<I, R, ?>> theColumnList;

    /**
     * The CellFactory.
     */
    private final TethysSwingTableCellFactory<I, R> theCellFactory;

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
     */
    public TethysSwingTableManager() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public TethysSwingTableManager(final TethysDataFormatter pFormatter) {
        /* Create fields */
        theColumnList = new ArrayList<>();
        theModel = new TethysSwingTableModel();
        theTable = new JTable(theModel);
        theColumns = theTable.getColumnModel();
        theCellFactory = new TethysSwingTableCellFactory<>(pFormatter);
        theSorter = new TethysSwingTableSorter<>(theModel);

        /* Listen to factory */
        theCellFactory.getEventRegistrar().addEventListener(this::cascadeEvent);

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
    public void setFilter(final Predicate<R> pFilter) {
        theSorter.setFilter(pFilter);
    }

    @Override
    public void setComparator(final Comparator<R> pComparator) {
        theSorter.setComparator(pComparator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TethysSwingTableColumn<I, R, ?> getColumn(final I pId) {
        return (TethysSwingTableColumn<I, R, ?>) super.getColumn(pId);
    }

    /**
     * Obtain the column for the model index.
     * @param pIndex the index of the column
     * @return the table column
     */
    private TethysSwingTableColumn<I, R, ?> getIndexedColumn(final int pIndex) {
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
    public TethysSwingTableStringColumn<I, R> declareStringColumn(final I pId) {
        return new TethysSwingTableStringColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableShortColumn<I, R> declareShortColumn(final I pId) {
        return new TethysSwingTableShortColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableIntegerColumn<I, R> declareIntegerColumn(final I pId) {
        return new TethysSwingTableIntegerColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableLongColumn<I, R> declareLongColumn(final I pId) {
        return new TethysSwingTableLongColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableMoneyColumn<I, R> declareMoneyColumn(final I pId) {
        return new TethysSwingTableMoneyColumn<>(this, pId);
    }

    @Override
    public TethysSwingTablePriceColumn<I, R> declarePriceColumn(final I pId) {
        return new TethysSwingTablePriceColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableRateColumn<I, R> declareRateColumn(final I pId) {
        return new TethysSwingTableRateColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableUnitsColumn<I, R> declareUnitsColumn(final I pId) {
        return new TethysSwingTableUnitsColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableDilutionColumn<I, R> declareDilutionColumn(final I pId) {
        return new TethysSwingTableDilutionColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableRatioColumn<I, R> declareRatioColumn(final I pId) {
        return new TethysSwingTableRatioColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableDilutedPriceColumn<I, R> declareDilutedPriceColumn(final I pId) {
        return new TethysSwingTableDilutedPriceColumn<>(this, pId);
    }

    @Override
    public TethysSwingTableDateColumn<I, R> declareDateColumn(final I pId) {
        return new TethysSwingTableDateColumn<>(this, pId);
    }

    @Override
    public <C> TethysSwingTableScrollColumn<I, R, C> declareScrollColumn(final I pId,
                                                                         final Class<C> pClass) {
        return new TethysSwingTableScrollColumn<>(this, pId, pClass);
    }

    @Override
    public <C> TethysSwingTableListColumn<I, R, C> declareListColumn(final I pId,
                                                                     final Class<C> pClass) {
        return new TethysSwingTableListColumn<>(this, pId, pClass);
    }

    @Override
    public <C> TethysSwingTableIconColumn<I, R, C> declareIconColumn(final I pId,
                                                                     final Class<C> pClass) {
        return new TethysSwingTableIconColumn<>(this, pId, pClass);
    }

    @Override
    public <C> TethysSwingTableStateIconColumn<I, R, C> declareStateIconColumn(final I pId,
                                                                               final Class<C> pClass) {
        return new TethysSwingTableStateIconColumn<>(this, pId, pClass);
    }

    /**
     * Table Model.
     */
    private class TethysSwingTableModel
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
    }

    /**
     * Column Definition.
     * @param <I> the column identity
     * @param <R> the row type
     * @param <C> the column type
     */
    public abstract static class TethysSwingTableColumn<I, R, C>
            extends TethysTableColumn<I, R, JComponent> {
        /**
         * The underlying column.
         */
        private final TableColumn theColumn;

        /**
         * The table cell.
         */
        private TethysSwingTableCell<I, R, C> theCell;

        /**
         * Cell value factory.
         */
        private Function<R, C> theValueFactory;

        /**
         * Cell commit factory.
         */
        private BiConsumer<R, C> theCommitFactory;

        /**
         * Constructor.
         * @param pTable the owning table
         * @param pId the column id
         * @param pType the type of the column
         */
        protected TethysSwingTableColumn(final TethysSwingTableManager<I, R> pTable,
                                         final I pId,
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
        public void setCellValueFactory(final Function<R, C> pFactory) {
            theValueFactory = pFactory;
        }

        /**
         * Set cell commit Factory.
         * @param pFactory the cell factory
         */
        public void setCellCommitFactory(final BiConsumer<R, C> pFactory) {
            theCommitFactory = pFactory;
        }

        /**
         * Declare cell.
         * @param pCell the cell
         */
        protected void declareCell(final TethysSwingTableCell<I, R, C> pCell) {
            theCell = pCell;
            theColumn.setCellRenderer(theCell.getRenderer());
            theColumn.setCellEditor(theCell.getEditor());
        }

        @Override
        public TethysSwingTableManager<I, R> getTable() {
            return (TethysSwingTableManager<I, R>) super.getTable();
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
        private C getCellValue(final int pIndex) {
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

        /**
         * Set the column width.
         * @param pWidth the width
         */
        public void setColumnWidth(final int pWidth) {
            theColumn.setPreferredWidth(pWidth);
        }
    }

    /**
     * String Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableStringColumn<I, R>
            extends TethysSwingTableColumn<I, R, String> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableStringColumn(final TethysSwingTableManager<I, R> pTable,
                                               final I pId) {
            super(pTable, pId, TethysFieldType.STRING);
            declareCell(getTable().theCellFactory.stringCell(this));
        }
    }

    /**
     * Short Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableShortColumn<I, R>
            extends TethysSwingTableColumn<I, R, Short> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableShortColumn(final TethysSwingTableManager<I, R> pTable,
                                              final I pId) {
            super(pTable, pId, TethysFieldType.SHORT);
            declareCell(getTable().theCellFactory.shortCell(this));
        }
    }

    /**
     * Integer Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIntegerColumn<I, R>
            extends TethysSwingTableColumn<I, R, Integer> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableIntegerColumn(final TethysSwingTableManager<I, R> pTable,
                                                final I pId) {
            super(pTable, pId, TethysFieldType.INTEGER);
            declareCell(getTable().theCellFactory.integerCell(this));
        }
    }

    /**
     * Long Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableLongColumn<I, R>
            extends TethysSwingTableColumn<I, R, Long> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableLongColumn(final TethysSwingTableManager<I, R> pTable,
                                             final I pId) {
            super(pTable, pId, TethysFieldType.LONG);
            declareCell(getTable().theCellFactory.longCell(this));
        }
    }

    /**
     * Money Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableMoneyColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysMoney> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableMoneyColumn(final TethysSwingTableManager<I, R> pTable,
                                              final I pId) {
            super(pTable, pId, TethysFieldType.MONEY);
            declareCell(getTable().theCellFactory.moneyCell(this));
        }
    }

    /**
     * Price Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTablePriceColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysPrice> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTablePriceColumn(final TethysSwingTableManager<I, R> pTable,
                                              final I pId) {
            super(pTable, pId, TethysFieldType.PRICE);
            declareCell(getTable().theCellFactory.priceCell(this));
        }
    }

    /**
     * Rate Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRateColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysRate> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableRateColumn(final TethysSwingTableManager<I, R> pTable,
                                             final I pId) {
            super(pTable, pId, TethysFieldType.RATE);
            declareCell(getTable().theCellFactory.rateCell(this));
        }
    }

    /**
     * Units Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableUnitsColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysUnits> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableUnitsColumn(final TethysSwingTableManager<I, R> pTable,
                                              final I pId) {
            super(pTable, pId, TethysFieldType.UNITS);
            declareCell(getTable().theCellFactory.unitsCell(this));
        }
    }

    /**
     * Dilution Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutionColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysDilution> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableDilutionColumn(final TethysSwingTableManager<I, R> pTable,
                                                 final I pId) {
            super(pTable, pId, TethysFieldType.DILUTION);
            declareCell(getTable().theCellFactory.dilutionCell(this));
        }
    }

    /**
     * Ratio Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRatioColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysRatio> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableRatioColumn(final TethysSwingTableManager<I, R> pTable,
                                              final I pId) {
            super(pTable, pId, TethysFieldType.RATIO);
            declareCell(getTable().theCellFactory.ratioCell(this));
        }
    }

    /**
     * DilutedPrice Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutedPriceColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableDilutedPriceColumn(final TethysSwingTableManager<I, R> pTable,
                                                     final I pId) {
            super(pTable, pId, TethysFieldType.DILUTEDPRICE);
            declareCell(getTable().theCellFactory.dilutedPriceCell(this));
        }
    }

    /**
     * Date Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDateColumn<I, R>
            extends TethysSwingTableColumn<I, R, TethysDate> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysSwingTableDateColumn(final TethysSwingTableManager<I, R> pTable,
                                             final I pId) {
            super(pTable, pId, TethysFieldType.DATE);
            declareCell(getTable().theCellFactory.dateCell(this));
        }
    }

    /**
     * Scroll Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysSwingTableScrollColumn<I, R, C>
            extends TethysSwingTableColumn<I, R, C> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysSwingTableScrollColumn(final TethysSwingTableManager<I, R> pTable,
                                               final I pId,
                                               final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.SCROLL);
            declareCell(getTable().theCellFactory.scrollCell(this, pClass));
        }
    }

    /**
     * List Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysSwingTableListColumn<I, R, C>
            extends TethysSwingTableColumn<I, R, TethysItemList<C>> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysSwingTableListColumn(final TethysSwingTableManager<I, R> pTable,
                                             final I pId,
                                             final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.LIST);
            declareCell(getTable().theCellFactory.listCell(this, pClass));
        }
    }

    /**
     * Icon Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysSwingTableIconColumn<I, R, C>
            extends TethysSwingTableColumn<I, R, C> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysSwingTableIconColumn(final TethysSwingTableManager<I, R> pTable,
                                             final I pId,
                                             final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.ICON);
            declareCell(getTable().theCellFactory.iconCell(this, pClass));
        }
    }

    /**
     * StateIcon Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysSwingTableStateIconColumn<I, R, C>
            extends TethysSwingTableColumn<I, R, C> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysSwingTableStateIconColumn(final TethysSwingTableManager<I, R> pTable,
                                                  final I pId,
                                                  final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.STATEICON);
            declareCell(getTable().theCellFactory.stateIconCell(this, pClass));
        }
    }
}
