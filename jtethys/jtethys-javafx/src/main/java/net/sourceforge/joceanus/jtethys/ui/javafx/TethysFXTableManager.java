/*******************************************************************************
        @Override
        public void setVisible(final boolean pVisible) {
            getNode().setVisible(pVisible);
        }

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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
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
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableCellFactory.TethysFXTableCell;

/**
 * JavaFX Table manager.
 * @param <C> the column identity
 * @param <R> the row type
 */
public class TethysFXTableManager<C, R>
        extends TethysTableManager<C, R, Node, Node> {
    /**
     * Base StyleSheet Class.
     */
    private static final String CSS_STYLE_BASE = TethysFXGuiFactory.CSS_STYLE_BASE + "-table";

    /**
     * The TableView.
     */
    private final TableView<R> theTable;

    /**
     * The Columns.
     */
    private final ObservableList<TableColumn<R, ?>> theColumns;

    /**
     * The CellFactory.
     */
    private final TethysFXTableCellFactory<C, R> theCellFactory;

    /**
     * The Items.
     */
    private ObservableList<R> theItems;

    /**
     * The Sorted Items.
     */
    private ObservableList<R> theSorted;

    /**
     * The Active Cell.
     */
    private TethysFXTableCell<?, C, R> theActiveCell;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXTableManager(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create fields */
        theTable = new TableView<>();
        theColumns = theTable.getColumns();
        theCellFactory = new TethysFXTableCellFactory<>(pFactory);
        theTable.getStyleClass().add(CSS_STYLE_BASE);

        /* Listen to factory */
        theCellFactory.getEventRegistrar().addEventListener(this::cascadeEvent);

        /* Configure the table */
        theTable.setEditable(true);
    }

    @Override
    public Node getNode() {
        return theTable;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theTable.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theTable.setManaged(pVisible);
        theTable.setVisible(pVisible);
    }

    @Override
    public Iterator<R> itemIterator() {
        return theItems == null
                                ? Collections.emptyIterator()
                                : theItems.iterator();
    }

    @Override
    public Iterator<R> sortedIterator() {
        return theSorted == null
                                 ? Collections.emptyIterator()
                                 : theSorted.iterator();
    }

    @Override
    public Iterator<R> viewIterator() {
        List<R> myItems = theTable.getItems();
        return myItems == null
                               ? Collections.emptyIterator()
                               : myItems.iterator();
    }

    @Override
    public TethysFXTableColumn<?, C, R> getColumn(final C pId) {
        return (TethysFXTableColumn<?, C, R>) super.getColumn(pId);
    }

    /**
     * Set the table items.
     * @param pItems the items
     */
    public void setItems(final ObservableList<R> pItems) {
        theItems = pItems;
        setTheItems();
    }

    @Override
    public void setFilter(final Predicate<R> pFilter) {
        super.setFilter(pFilter);
        setTheItems();
    }

    @Override
    public void setComparator(final Comparator<R> pComparator) {
        super.setComparator(pComparator);
        setTheItems();
    }

    /**
     * Set the table items.
     */
    private void setTheItems() {
        /* Access the underlying copy */
        ObservableList<R> myItems = theItems;
        theSorted = myItems;

        /* If we have any items */
        if (myItems != null) {
            /* Apply sort if specified */
            Comparator<R> myComparator = getComparator();
            if (myComparator != null) {
                myItems = myItems.sorted(myComparator);
                theSorted = myItems;
            }

            /* Apply filter if specified */
            Predicate<R> myFilter = getFilter();
            if (myFilter != null) {
                myItems = myItems.filtered(myFilter);
            }
        }

        /* Declare the items */
        theTable.setItems(myItems);
    }

    /**
     * Set the active cell.
     * @param pCell the actively editing cell
     */
    protected void setActiveCell(final TethysFXTableCell<?, C, R> pCell) {
        theActiveCell = pCell;
    }

    /**
     * Is the table locked for editing.
     * @return true/false
     */
    protected boolean isEditLocked() {
        return !isEditable()
               || (theActiveCell != null && theActiveCell.isCellInError());
    }

    @Override
    public TethysFXTableStringColumn<C, R> declareStringColumn(final C pId) {
        return new TethysFXTableStringColumn<>(this, pId);
    }

    @Override
    public TethysFXTableCharArrayColumn<C, R> declareCharArrayColumn(final C pId) {
        return new TethysFXTableCharArrayColumn<>(this, pId);
    }

    @Override
    public TethysFXTableShortColumn<C, R> declareShortColumn(final C pId) {
        return new TethysFXTableShortColumn<>(this, pId);
    }

    @Override
    public TethysFXTableIntegerColumn<C, R> declareIntegerColumn(final C pId) {
        return new TethysFXTableIntegerColumn<>(this, pId);
    }

    @Override
    public TethysFXTableLongColumn<C, R> declareLongColumn(final C pId) {
        return new TethysFXTableLongColumn<>(this, pId);
    }

    @Override
    public TethysFXTableRawDecimalColumn<C, R> declareRawDecimalColumn(final C pId) {
        return new TethysFXTableRawDecimalColumn<>(this, pId);
    }

    @Override
    public TethysFXTableMoneyColumn<C, R> declareMoneyColumn(final C pId) {
        return new TethysFXTableMoneyColumn<>(this, pId);
    }

    @Override
    public TethysFXTablePriceColumn<C, R> declarePriceColumn(final C pId) {
        return new TethysFXTablePriceColumn<>(this, pId);
    }

    @Override
    public TethysFXTableRateColumn<C, R> declareRateColumn(final C pId) {
        return new TethysFXTableRateColumn<>(this, pId);
    }

    @Override
    public TethysFXTableUnitsColumn<C, R> declareUnitsColumn(final C pId) {
        return new TethysFXTableUnitsColumn<>(this, pId);
    }

    @Override
    public TethysFXTableDilutionColumn<C, R> declareDilutionColumn(final C pId) {
        return new TethysFXTableDilutionColumn<>(this, pId);
    }

    @Override
    public TethysFXTableRatioColumn<C, R> declareRatioColumn(final C pId) {
        return new TethysFXTableRatioColumn<>(this, pId);
    }

    @Override
    public TethysFXTableDilutedPriceColumn<C, R> declareDilutedPriceColumn(final C pId) {
        return new TethysFXTableDilutedPriceColumn<>(this, pId);
    }

    @Override
    public TethysFXTableDateColumn<C, R> declareDateColumn(final C pId) {
        return new TethysFXTableDateColumn<>(this, pId);
    }

    @Override
    public <T> TethysFXTableScrollColumn<T, C, R> declareScrollColumn(final C pId,
                                                                      final Class<T> pClass) {
        return new TethysFXTableScrollColumn<>(this, pId, pClass);
    }

    @Override
    public <T> TethysFXTableListColumn<T, C, R> declareListColumn(final C pId,
                                                                  final Class<T> pClass) {
        return new TethysFXTableListColumn<>(this, pId, pClass);
    }

    @Override
    public <T> TethysFXTableIconColumn<T, C, R> declareIconColumn(final C pId,
                                                                  final Class<T> pClass) {
        return new TethysFXTableIconColumn<>(this, pId, pClass);
    }

    /**
     * Column Definition.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public static class TethysFXTableColumn<T, C, R>
            extends TethysBaseTableColumn<T, C, R, Node, Node> {
        /**
         * The underlying column.
         */
        private final TableColumn<R, T> theColumn;

        /**
         * Constructor.
         * @param pTable the owning table
         * @param pId the column id
         * @param pType the type of the column
         */
        protected TethysFXTableColumn(final TethysFXTableManager<C, R> pTable,
                                      final C pId,
                                      final TethysFieldType pType) {
            /* Initialise underlying class */
            super(pTable, pId, pType);

            /* Create the column and add to the table */
            theColumn = new TableColumn<>(getName());
            pTable.theColumns.add(theColumn);

            /* Configure the column */
            theColumn.setSortable(false);
            theColumn.setEditable(true);
            setColumnWidth(getCellType().getDefaultWidth());
        }

        /**
         * Set cell value Factory.
         * @param pFactory the cell factory
         */
        public void setCellValueFactory(final Callback<CellDataFeatures<R, T>, ObservableValue<T>> pFactory) {
            theColumn.setCellValueFactory(pFactory);
        }

        /**
         * Declare cell Factory.
         * @param pFactory the cell factory
         */
        protected void declareCellFactory(final Callback<TableColumn<R, T>, TableCell<R, T>> pFactory) {
            theColumn.setCellFactory(pFactory);
        }

        @Override
        public TethysFXTableManager<C, R> getTable() {
            return (TethysFXTableManager<C, R>) super.getTable();
        }

        @Override
        protected void attachToTable() {
            theColumn.setVisible(true);
        }

        @Override
        protected void detachFromTable() {
            theColumn.setVisible(false);
        }

        @Override
        public void setColumnWidth(final int pWidth) {
            theColumn.setPrefWidth(pWidth);
        }

        @Override
        public void setName(final String pName) {
            super.setName(pName);
            theColumn.setText(pName);
        }
    }

    /**
     * String Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableStringColumn<C, R>
            extends TethysFXTableColumn<String, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableStringColumn(final TethysFXTableManager<C, R> pTable,
                                            final C pId) {
            super(pTable, pId, TethysFieldType.STRING);
            declareCellFactory(getTable().theCellFactory.stringCellFactory(this));
        }
    }

    /**
     * CharArray Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableCharArrayColumn<C, R>
            extends TethysFXTableColumn<char[], C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableCharArrayColumn(final TethysFXTableManager<C, R> pTable,
                                               final C pId) {
            super(pTable, pId, TethysFieldType.CHARARRAY);
            declareCellFactory(getTable().theCellFactory.charArrayCellFactory(this));
        }
    }

    /**
     * Short Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableShortColumn<C, R>
            extends TethysFXTableColumn<Short, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableShortColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.SHORT);
            declareCellFactory(getTable().theCellFactory.shortCellFactory(this));
        }
    }

    /**
     * Integer Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableIntegerColumn<C, R>
            extends TethysFXTableColumn<Integer, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableIntegerColumn(final TethysFXTableManager<C, R> pTable,
                                             final C pId) {
            super(pTable, pId, TethysFieldType.INTEGER);
            declareCellFactory(getTable().theCellFactory.integerCellFactory(this));
        }
    }

    /**
     * Long Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableLongColumn<C, R>
            extends TethysFXTableColumn<Long, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableLongColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysFieldType.LONG);
            declareCellFactory(getTable().theCellFactory.longCellFactory(this));
        }
    }

    /**
     * RawDecimal Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRawDecimalColumn<C, R>
            extends TethysFXTableColumn<TethysDecimal, C, R>
            implements TethysTableRawDecimalColumn<C, R, Node, Node> {
        /**
         * Raw decimals supplier.
         */
        private Function<R, Integer> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableRawDecimalColumn(final TethysFXTableManager<C, R> pTable,
                                                final C pId) {
            super(pTable, pId, TethysFieldType.DECIMAL);
            declareCellFactory(getTable().theCellFactory.rawDecimalCellFactory(this));
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
    public static class TethysFXTableMoneyColumn<C, R>
            extends TethysFXTableColumn<TethysMoney, C, R>
            implements TethysTableCurrencyColumn<TethysMoney, C, R, Node, Node> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableMoneyColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.MONEY);
            declareCellFactory(getTable().theCellFactory.moneyCellFactory(this));
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
    public static class TethysFXTablePriceColumn<C, R>
            extends TethysFXTableColumn<TethysPrice, C, R>
            implements TethysTableCurrencyColumn<TethysPrice, C, R, Node, Node> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTablePriceColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.PRICE);
            declareCellFactory(getTable().theCellFactory.priceCellFactory(this));
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
    public static class TethysFXTableRateColumn<C, R>
            extends TethysFXTableColumn<TethysRate, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableRateColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysFieldType.RATE);
            declareCellFactory(getTable().theCellFactory.rateCellFactory(this));
        }
    }

    /**
     * Units Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableUnitsColumn<C, R>
            extends TethysFXTableColumn<TethysUnits, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableUnitsColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.UNITS);
            declareCellFactory(getTable().theCellFactory.unitsCellFactory(this));
        }
    }

    /**
     * Dilution Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutionColumn<C, R>
            extends TethysFXTableColumn<TethysDilution, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDilutionColumn(final TethysFXTableManager<C, R> pTable,
                                              final C pId) {
            super(pTable, pId, TethysFieldType.DILUTION);
            declareCellFactory(getTable().theCellFactory.dilutionCellFactory(this));
        }
    }

    /**
     * Ratio Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRatioColumn<C, R>
            extends TethysFXTableColumn<TethysRatio, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableRatioColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.RATIO);
            declareCellFactory(getTable().theCellFactory.ratioCellFactory(this));
        }
    }

    /**
     * DilutedPrice Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutedPriceColumn<C, R>
            extends TethysFXTableColumn<TethysDilutedPrice, C, R>
            implements TethysTableCurrencyColumn<TethysDilutedPrice, C, R, Node, Node> {
        /**
         * Currency supplier.
         */
        private Function<R, Currency> theSupplier;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDilutedPriceColumn(final TethysFXTableManager<C, R> pTable,
                                                  final C pId) {
            super(pTable, pId, TethysFieldType.DILUTEDPRICE);
            declareCellFactory(getTable().theCellFactory.dilutedPriceCellFactory(this));
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
    public static class TethysFXTableDateColumn<C, R>
            extends TethysFXTableColumn<TethysDate, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDateColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysFieldType.DATE);
            declareCellFactory(getTable().theCellFactory.dateCellFactory(this));
        }
    }

    /**
     * Scroll Column.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableScrollColumn<T, C, R>
            extends TethysFXTableColumn<T, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysFXTableScrollColumn(final TethysFXTableManager<C, R> pTable,
                                            final C pId,
                                            final Class<T> pClass) {
            super(pTable, pId, TethysFieldType.SCROLL);
            declareCellFactory(getTable().theCellFactory.scrollCellFactory(this, pClass));
        }
    }

    /**
     * List Column.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableListColumn<T, C, R>
            extends TethysFXTableColumn<TethysItemList<T>, C, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysFXTableListColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId,
                                          final Class<T> pClass) {
            super(pTable, pId, TethysFieldType.LIST);
            declareCellFactory(getTable().theCellFactory.listCellFactory(this, pClass));
        }
    }

    /**
     * Icon Column.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableIconColumn<T, C, R>
            extends TethysFXTableColumn<T, C, R>
            implements TethysTableIconColumn<T, C, R, Node, Node> {
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
        protected TethysFXTableIconColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId,
                                          final Class<T> pClass) {
            super(pTable, pId, TethysFieldType.ICON);
            declareCellFactory(getTable().theCellFactory.iconCellFactory(this, pClass));
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
