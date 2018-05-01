/*******************************************************************************
 * Tethys: Java Utilities Copyright 2012,2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.util.Callback;
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
     * The Sort Comparator.
     */
    private ObjectProperty<Comparator<R>> theCompValue;

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

        /* Configure the table */
        theTable.setEditable(true);

        /* Set single Selection and listen for selection changes */
        final TableViewSelectionModel<R> myModel = theTable.getSelectionModel();
        myModel.setSelectionMode(SelectionMode.SINGLE);
        myModel.selectedItemProperty().addListener((v, o, n) -> processOnSelect(n));

        theCompValue = new SimpleObjectProperty<>();
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
        final List<R> myItems = theTable.getItems();
        return myItems == null
                               ? Collections.emptyIterator()
                               : myItems.iterator();
    }

    @Override
    public TethysFXTableColumn<?, C, R> getColumn(final C pId) {
        return (TethysFXTableColumn<?, C, R>) super.getColumn(pId);
    }

    /**
     * Obtain the columns.
     * @return the columns
     */
    ObservableList<TableColumn<R, ?>> getColumns() {
        return theColumns;
    }

    /**
     * Obtain the cell factory.
     * @return the cell factory
     */
    TethysFXTableCellFactory<C, R> getCellFactory() {
        return theCellFactory;
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
            final Comparator<R> myComparator = getComparator();
            if (myComparator != null) {
                myItems = myItems.sorted(myComparator);
                theSorted = myItems;
                theCompValue.setValue(myComparator);
                ((SortedList<R>) theSorted).comparatorProperty().bind(theCompValue);
            }

            /* Apply filter if specified */
            final Predicate<R> myFilter = getFilter();
            if (myFilter != null) {
                myItems = myItems.filtered(myFilter);
            }
        }

        /* Declare the items */
        theTable.setItems(myItems);
    }

    /**
     * Force a sort operation.
     */
    public void forceSort() {
        /* Apply sort if specified */
        final Comparator<R> myComparator = getComparator();
        if (myComparator != null) {
            theCompValue.setValue(null);
            theCompValue.setValue(myComparator);
        }
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
               || theActiveCell != null && theActiveCell.isCellInError();
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
                                                                      final Class<T> pClazz) {
        return new TethysFXTableScrollColumn<>(this, pId, pClazz);
    }

    @Override
    public <T extends Comparable<T>> TethysFXTableListColumn<T, C, R> declareListColumn(final C pId) {
        return new TethysFXTableListColumn<>(this, pId);
    }

    @Override
    public <T> TethysFXTableIconColumn<T, C, R> declareIconColumn(final C pId,
                                                                  final Class<T> pClazz) {
        return new TethysFXTableIconColumn<>(this, pId, pClazz);
    }

    @Override
    public void selectRowWithScroll(final R pItem) {
        theTable.getSelectionModel().select(pItem);
        theTable.scrollTo(pItem);
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
            pTable.getColumns().add(theColumn);

            /* Configure the column */
            theColumn.setSortable(false);
            theColumn.setEditable(true);
            setColumnWidth(getCellType().getDefaultWidth());
        }

        /**
         * Obtain the cell factory.
         * @return the cell factory
         */
        TethysFXTableCellFactory<C, R> getCellFactory() {
            return getTable().getCellFactory();
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
     * Column Definition.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysFXTableValidatedColumn<T, C, R>
            extends TethysFXTableColumn<T, C, R>
            implements TethysTableValidatedColumn<T, R> {
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
        protected TethysFXTableValidatedColumn(final TethysFXTableManager<C, R> pTable,
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

        /**
         * Get the validity tester.
         * @return the current tester
         */
        protected BiFunction<T, R, String> getValidator() {
            return theValidator;
        }
    }

    /**
     * String Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableStringColumn<C, R>
            extends TethysFXTableValidatedColumn<String, C, R>
            implements TethysTableStringColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableStringColumn(final TethysFXTableManager<C, R> pTable,
                                            final C pId) {
            super(pTable, pId, TethysFieldType.STRING);
            declareCellFactory(super.getCellFactory().stringCellFactory(this));
        }
    }

    /**
     * CharArray Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableCharArrayColumn<C, R>
            extends TethysFXTableValidatedColumn<char[], C, R>
            implements TethysTableCharArrayColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableCharArrayColumn(final TethysFXTableManager<C, R> pTable,
                                               final C pId) {
            super(pTable, pId, TethysFieldType.CHARARRAY);
            declareCellFactory(super.getCellFactory().charArrayCellFactory(this));
        }
    }

    /**
     * Short Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableShortColumn<C, R>
            extends TethysFXTableValidatedColumn<Short, C, R>
            implements TethysTableShortColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableShortColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.SHORT);
            declareCellFactory(super.getCellFactory().shortCellFactory(this));
        }
    }

    /**
     * Integer Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableIntegerColumn<C, R>
            extends TethysFXTableValidatedColumn<Integer, C, R>
            implements TethysTableIntegerColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableIntegerColumn(final TethysFXTableManager<C, R> pTable,
                                             final C pId) {
            super(pTable, pId, TethysFieldType.INTEGER);
            declareCellFactory(super.getCellFactory().integerCellFactory(this));
        }
    }

    /**
     * Long Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableLongColumn<C, R>
            extends TethysFXTableValidatedColumn<Long, C, R>
            implements TethysTableLongColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableLongColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysFieldType.LONG);
            declareCellFactory(super.getCellFactory().longCellFactory(this));
        }
    }

    /**
     * RawDecimal Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRawDecimalColumn<C, R>
            extends TethysFXTableValidatedColumn<TethysDecimal, C, R>
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
            declareCellFactory(super.getCellFactory().rawDecimalCellFactory(this));
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
            extends TethysFXTableValidatedColumn<TethysMoney, C, R>
            implements TethysTableMoneyColumn<C, R, Node, Node> {
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
            declareCellFactory(super.getCellFactory().moneyCellFactory(this));
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
            extends TethysFXTableValidatedColumn<TethysPrice, C, R>
            implements TethysTablePriceColumn<C, R, Node, Node> {
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
            declareCellFactory(super.getCellFactory().priceCellFactory(this));
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
            extends TethysFXTableValidatedColumn<TethysRate, C, R>
            implements TethysTableRateColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableRateColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysFieldType.RATE);
            declareCellFactory(super.getCellFactory().rateCellFactory(this));
        }
    }

    /**
     * Units Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableUnitsColumn<C, R>
            extends TethysFXTableValidatedColumn<TethysUnits, C, R>
            implements TethysTableUnitsColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableUnitsColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.UNITS);
            declareCellFactory(super.getCellFactory().unitsCellFactory(this));
        }
    }

    /**
     * Dilution Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutionColumn<C, R>
            extends TethysFXTableValidatedColumn<TethysDilution, C, R>
            implements TethysTableDilutionColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDilutionColumn(final TethysFXTableManager<C, R> pTable,
                                              final C pId) {
            super(pTable, pId, TethysFieldType.DILUTION);
            declareCellFactory(super.getCellFactory().dilutionCellFactory(this));
        }
    }

    /**
     * Ratio Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRatioColumn<C, R>
            extends TethysFXTableValidatedColumn<TethysRatio, C, R>
            implements TethysTableRatioColumn<C, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableRatioColumn(final TethysFXTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysFieldType.RATIO);
            declareCellFactory(super.getCellFactory().ratioCellFactory(this));
        }
    }

    /**
     * DilutedPrice Column.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutedPriceColumn<C, R>
            extends TethysFXTableValidatedColumn<TethysDilutedPrice, C, R>
            implements TethysTableDilutedPriceColumn<C, R, Node, Node> {
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
            declareCellFactory(super.getCellFactory().dilutedPriceCellFactory(this));
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
            extends TethysFXTableColumn<TethysDate, C, R>
            implements TethysTableDateColumn<C, R, Node, Node> {
        /**
         * Date configurator.
         */
        private BiConsumer<R, TethysDateConfig> theConfigurator;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDateColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysFieldType.DATE);
            declareCellFactory(super.getCellFactory().dateCellFactory(this));
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
    public static class TethysFXTableScrollColumn<T, C, R>
            extends TethysFXTableColumn<T, C, R>
            implements TethysTableScrollColumn<T, C, R, Node, Node> {
        /**
         * Menu configurator.
         */
        private BiConsumer<R, TethysScrollMenu<T, Node>> theConfigurator;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClazz the item class
         */
        protected TethysFXTableScrollColumn(final TethysFXTableManager<C, R> pTable,
                                            final C pId,
                                            final Class<T> pClazz) {
            super(pTable, pId, TethysFieldType.SCROLL);
            declareCellFactory(super.getCellFactory().scrollCellFactory(this, pClazz));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public void setMenuConfigurator(final BiConsumer<R, TethysScrollMenu<T, Node>> pConfigurator) {
            theConfigurator = pConfigurator;
        }

        /**
         * Obtain the menu configurator.
         * @return the configurator
         */
        protected BiConsumer<R, TethysScrollMenu<T, Node>> getMenuConfigurator() {
            return theConfigurator;
        }
    }

    /**
     * List Column.
     * @param <T> the column type
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableListColumn<T extends Comparable<T>, C, R>
            extends TethysFXTableColumn<List<T>, C, R>
            implements TethysTableListColumn<T, C, R, Node, Node> {
        /**
         * Selectable supplier.
         */
        private Function<R, Iterator<T>> theSelectables;

        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableListColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysFieldType.LIST);
            declareCellFactory(super.getCellFactory().listCellFactory(this));
            theSelectables = r -> Collections.emptyIterator();
        }

        @Override
        public void setSelectables(final Function<R, Iterator<T>> pSelectables) {
            theSelectables = pSelectables;
        }

        /**
         * Obtain the selectable supplier.
         * @return the supplier
         */
        protected Function<R, Iterator<T>> getSelectables() {
            return theSelectables;
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
         * @param pClazz the item class
         */
        protected TethysFXTableIconColumn(final TethysFXTableManager<C, R> pTable,
                                          final C pId,
                                          final Class<T> pClazz) {
            super(pTable, pId, TethysFieldType.ICON);
            declareCellFactory(super.getCellFactory().iconCellFactory(this, pClazz));
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
