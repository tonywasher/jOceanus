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
package net.sourceforge.joceanus.jtethys.ui.javafx.table;

import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
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
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.core.field.TethysUICoreDataEditConverter.TethysUICoreRawDecimalEditConverter;
import net.sourceforge.joceanus.jtethys.ui.core.table.TethysUICoreTableColumn;

/**
 * Column Definition.
 *
 * @param <T> the column type
 * @param <C> the column identity
 * @param <R> the row type
 */
public class TethysUIFXTableColumn<T, C, R>
        extends TethysUICoreTableColumn<T, C, R> {
    /**
     * The underlying column.
     */
    private final TableColumn<R, T> theColumn;

    /**
     * Constructor.
     *
     * @param pTable the owning table
     * @param pId    the column id
     * @param pType  the type of the column
     */
    TethysUIFXTableColumn(final TethysUIFXTableManager<C, R> pTable,
                          final C pId,
                          final TethysUIFieldType pType) {
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
     *
     * @return the cell factory
     */
    TethysUIFXTableCellFactory<C, R> getCellFactory() {
        return getTable().getCellFactory();
    }

    @Override
    public TethysUICoreTableColumn<T, C, R> setCellValueFactory(final Function<R, T> pFactory) {
        super.setCellValueFactory(pFactory);
        theColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(pFactory.apply(p.getValue())));
        return this;
    }

    /**
     * Declare cell Factory.
     *
     * @param pFactory the cell factory
     */
    void declareCellFactory(final Callback<TableColumn<R, T>, TableCell<R, T>> pFactory) {
        theColumn.setCellFactory(pFactory);
    }

    @Override
    public TethysUIFXTableManager<C, R> getTable() {
        return (TethysUIFXTableManager<C, R>) super.getTable();
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
    public TethysUIFXTableColumn<T, C, R> setColumnWidth(final int pWidth) {
        theColumn.setPrefWidth(pWidth);
        return this;
    }

    @Override
    public TethysUIFXTableColumn<T, C, R> setName(final String pName) {
        super.setName(pName);
        theColumn.setText(pName);
        return this;
    }

    /**
     * Column Definition.
     *
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysUIFXTableValidatedColumn<T, C, R>
            extends TethysUIFXTableColumn<T, C, R>
            implements TethysUITableValidatedColumn<T, R> {
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
        TethysUIFXTableValidatedColumn(final TethysUIFXTableManager<C, R> pTable,
                                       final C pId,
                                       final TethysUIFieldType pType) {
            /* Call super-constructor */
            super(pTable, pId, pType);

            /* Initialise validator */
            theValidator = (t, r) -> null;
        }

        @Override
        public TethysUIFXTableValidatedColumn<T, C, R> setValidator(final BiFunction<T, R, String> pValidator) {
            theValidator = pValidator;
            return this;
        }

        /**
         * Get the validity tester.
         *
         * @return the current tester
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
    public static class TethysUIFXTableStringColumn<C, R>
            extends TethysUIFXTableValidatedColumn<String, C, R>
            implements TethysUITableStringColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableStringColumn(final TethysUIFXTableManager<C, R> pTable,
                                    final C pId) {
            super(pTable, pId, TethysUIFieldType.STRING);
            declareCellFactory(super.getCellFactory().stringCellFactory(this));
        }

        @Override
        public TethysUIFXTableStringColumn<C, R> setValidator(final BiFunction<String, R, String> pValidator) {
            return (TethysUIFXTableStringColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * CharArray Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableCharArrayColumn<C, R>
            extends TethysUIFXTableValidatedColumn<char[], C, R>
            implements TethysUITableCharArrayColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableCharArrayColumn(final TethysUIFXTableManager<C, R> pTable,
                                       final C pId) {
            super(pTable, pId, TethysUIFieldType.CHARARRAY);
            declareCellFactory(super.getCellFactory().charArrayCellFactory(this));
        }

        @Override
        public TethysUIFXTableCharArrayColumn<C, R> setValidator(final BiFunction<char[], R, String> pValidator) {
            return (TethysUIFXTableCharArrayColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Short Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableShortColumn<C, R>
            extends TethysUIFXTableValidatedColumn<Short, C, R>
            implements TethysUITableShortColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableShortColumn(final TethysUIFXTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysUIFieldType.SHORT);
            declareCellFactory(super.getCellFactory().shortCellFactory(this));
        }

        @Override
        public TethysUIFXTableShortColumn<C, R> setValidator(final BiFunction<Short, R, String> pValidator) {
            return (TethysUIFXTableShortColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Integer Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableIntegerColumn<C, R>
            extends TethysUIFXTableValidatedColumn<Integer, C, R>
            implements TethysUITableIntegerColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableIntegerColumn(final TethysUIFXTableManager<C, R> pTable,
                                     final C pId) {
            super(pTable, pId, TethysUIFieldType.INTEGER);
            declareCellFactory(super.getCellFactory().integerCellFactory(this));
        }

        @Override
        public TethysUIFXTableIntegerColumn<C, R> setValidator(final BiFunction<Integer, R, String> pValidator) {
            return (TethysUIFXTableIntegerColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Long Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableLongColumn<C, R>
            extends TethysUIFXTableValidatedColumn<Long, C, R>
            implements TethysUITableLongColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableLongColumn(final TethysUIFXTableManager<C, R> pTable,
                                  final C pId) {
            super(pTable, pId, TethysUIFieldType.LONG);
            declareCellFactory(super.getCellFactory().longCellFactory(this));
        }

        @Override
        public TethysUIFXTableLongColumn<C, R> setValidator(final BiFunction<Long, R, String> pValidator) {
            return (TethysUIFXTableLongColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * RawDecimal Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableRawDecimalColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysDecimal, C, R>
            implements TethysUITableRawDecimalColumn<C, R> {
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
        TethysUIFXTableRawDecimalColumn(final TethysUIFXTableManager<C, R> pTable,
                                        final C pId) {
            super(pTable, pId, TethysUIFieldType.DECIMAL);
            declareCellFactory(super.getCellFactory().rawDecimalCellFactory(this));
            theSupplier = p -> TethysUICoreRawDecimalEditConverter.DEFAULT_DECIMALS;
        }

        @Override
        public TethysUIFXTableRawDecimalColumn<C, R> setValidator(final BiFunction<TethysDecimal, R, String> pValidator) {
            return (TethysUIFXTableRawDecimalColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUIFXTableRawDecimalColumn<C, R> setNumDecimals(final ToIntFunction<R> pSupplier) {
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
    public static class TethysUIFXTableMoneyColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysMoney, C, R>
            implements TethysUITableMoneyColumn<C, R> {
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
        TethysUIFXTableMoneyColumn(final TethysUIFXTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysUIFieldType.MONEY);
            declareCellFactory(super.getCellFactory().moneyCellFactory(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysUIFXTableMoneyColumn<C, R> setValidator(final BiFunction<TethysMoney, R, String> pValidator) {
            return (TethysUIFXTableMoneyColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUIFXTableMoneyColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
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
    public static class TethysUIFXTablePriceColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysPrice, C, R>
            implements TethysUITablePriceColumn<C, R> {
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
        TethysUIFXTablePriceColumn(final TethysUIFXTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysUIFieldType.PRICE);
            declareCellFactory(super.getCellFactory().priceCellFactory(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysUIFXTablePriceColumn<C, R> setValidator(final BiFunction<TethysPrice, R, String> pValidator) {
            return (TethysUIFXTablePriceColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUIFXTablePriceColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
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
    public static class TethysUIFXTableRateColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysRate, C, R>
            implements TethysUITableRateColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableRateColumn(final TethysUIFXTableManager<C, R> pTable,
                                  final C pId) {
            super(pTable, pId, TethysUIFieldType.RATE);
            declareCellFactory(super.getCellFactory().rateCellFactory(this));
        }

        @Override
        public TethysUIFXTableRateColumn<C, R> setValidator(final BiFunction<TethysRate, R, String> pValidator) {
            return (TethysUIFXTableRateColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Units Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableUnitsColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysUnits, C, R>
            implements TethysUITableUnitsColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableUnitsColumn(final TethysUIFXTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysUIFieldType.UNITS);
            declareCellFactory(super.getCellFactory().unitsCellFactory(this));
        }

        @Override
        public TethysUIFXTableUnitsColumn<C, R> setValidator(final BiFunction<TethysUnits, R, String> pValidator) {
            return (TethysUIFXTableUnitsColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Dilution Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableDilutionColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysDilution, C, R>
            implements TethysUITableDilutionColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableDilutionColumn(final TethysUIFXTableManager<C, R> pTable,
                                      final C pId) {
            super(pTable, pId, TethysUIFieldType.DILUTION);
            declareCellFactory(super.getCellFactory().dilutionCellFactory(this));
        }

        @Override
        public TethysUIFXTableDilutionColumn<C, R> setValidator(final BiFunction<TethysDilution, R, String> pValidator) {
            return (TethysUIFXTableDilutionColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Ratio Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableRatioColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysRatio, C, R>
            implements TethysUITableRatioColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUIFXTableRatioColumn(final TethysUIFXTableManager<C, R> pTable,
                                   final C pId) {
            super(pTable, pId, TethysUIFieldType.RATIO);
            declareCellFactory(super.getCellFactory().ratioCellFactory(this));
        }

        @Override
        public TethysUIFXTableRatioColumn<C, R> setValidator(final BiFunction<TethysRatio, R, String> pValidator) {
            return (TethysUIFXTableRatioColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * DilutedPrice Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableDilutedPriceColumn<C, R>
            extends TethysUIFXTableValidatedColumn<TethysDilutedPrice, C, R>
            implements TethysUITableDilutedPriceColumn<C, R> {
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
        TethysUIFXTableDilutedPriceColumn(final TethysUIFXTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysUIFieldType.DILUTEDPRICE);
            declareCellFactory(super.getCellFactory().dilutedPriceCellFactory(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysUIFXTableDilutedPriceColumn<C, R> setValidator(final BiFunction<TethysDilutedPrice, R, String> pValidator) {
            return (TethysUIFXTableDilutedPriceColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUIFXTableDilutedPriceColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
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
    public static class TethysUIFXTableDateColumn<C, R>
            extends TethysUIFXTableColumn<TethysDate, C, R>
            implements TethysUITableDateColumn<C, R> {
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
        TethysUIFXTableDateColumn(final TethysUIFXTableManager<C, R> pTable,
                                  final C pId) {
            super(pTable, pId, TethysUIFieldType.DATE);
            declareCellFactory(super.getCellFactory().dateCellFactory(this));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public TethysUIFXTableDateColumn<C, R> setDateConfigurator(final BiConsumer<R, TethysDateConfig> pConfigurator) {
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
    public static class TethysUIFXTableScrollColumn<T, C, R>
            extends TethysUIFXTableColumn<T, C, R>
            implements TethysUITableScrollColumn<T, C, R> {
        /**
         * Menu configurator.
         */
        private BiConsumer<R, TethysUIScrollMenu<T>> theConfigurator;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         * @param pClazz the item class
         */
        TethysUIFXTableScrollColumn(final TethysUIFXTableManager<C, R> pTable,
                                    final C pId,
                                    final Class<T> pClazz) {
            super(pTable, pId, TethysUIFieldType.SCROLL);
            declareCellFactory(super.getCellFactory().scrollCellFactory(this, pClazz));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public TethysUIFXTableScrollColumn<T, C, R> setMenuConfigurator(final BiConsumer<R, TethysUIScrollMenu<T>> pConfigurator) {
            theConfigurator = pConfigurator;
            return this;
        }

        /**
         * Obtain the menu configurator.
         *
         * @return the configurator
         */
        BiConsumer<R, TethysUIScrollMenu<T>> getMenuConfigurator() {
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
    public static class TethysUIFXTableListColumn<T extends Comparable<T>, C, R>
            extends TethysUIFXTableColumn<List<T>, C, R>
            implements TethysUITableListColumn<T, C, R> {
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
        TethysUIFXTableListColumn(final TethysUIFXTableManager<C, R> pTable,
                                  final C pId,
                                  final Class<T> pClazz) {
            super(pTable, pId, TethysUIFieldType.LIST);
            declareCellFactory(super.getCellFactory().listCellFactory(this));
            theSelectables = r -> Collections.emptyIterator();
        }

        @Override
        public TethysUIFXTableListColumn<T, C, R> setSelectables(final Function<R, Iterator<T>> pSelectables) {
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
    public static class TethysUIFXTableIconColumn<T, C, R>
            extends TethysUIFXTableColumn<T, C, R>
            implements TethysUITableIconColumn<T, C, R> {
        /**
         * IconMapSet supplier.
         */
        private Function<R, TethysUIIconMapSet<T>> theSupplier;

        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         * @param pClazz the item class
         */
        TethysUIFXTableIconColumn(final TethysUIFXTableManager<C, R> pTable,
                                  final C pId,
                                  final Class<T> pClazz) {
            super(pTable, pId, TethysUIFieldType.ICON);
            declareCellFactory(super.getCellFactory().iconCellFactory(this, pClazz));
            theSupplier = p -> null;
        }

        @Override
        public TethysUIFXTableIconColumn<T, C, R> setIconMapSet(final Function<R, TethysUIIconMapSet<T>> pSupplier) {
            theSupplier = pSupplier;
            return this;
        }

        /**
         * Obtain the mapSet supplier.
         *
         * @return the supplier
         */
        Function<R, TethysUIIconMapSet<T>> getIconMapSet() {
            return theSupplier;
        }
    }
}
