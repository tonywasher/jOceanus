/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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

import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
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
public abstract class TethysUISwingTableColumn<T, C, R>
        extends TethysUICoreTableColumn<T, C, R> {
    /**
     * The underlying column.
     */
    private final TableColumn theColumn;

    /**
     * The table cell.
     */
    private TethysUISwingTableCell<T, C, R> theCell;

    /**
     * Constructor.
     *
     * @param pTable the owning table
     * @param pId    the column id
     * @param pType  the type of the column
     */
    TethysUISwingTableColumn(final TethysUISwingTableManager<C, R> pTable,
                             final C pId,
                             final TethysUIFieldType pType) {
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
    }

    /**
     * Declare cell.
     *
     * @param pCell the cell
     */
    void declareCell(final TethysUISwingTableCell<T, C, R> pCell) {
        theCell = pCell;
        theColumn.setCellRenderer(theCell.getRenderer());
        theColumn.setCellEditor(theCell.getEditor());
    }

    @Override
    public TethysUISwingTableManager<C, R> getTable() {
        return (TethysUISwingTableManager<C, R>) super.getTable();
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
        return getCellValueFactory().apply(theCell.getActiveRow());
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
    public TethysUISwingTableColumn<T, C, R> setColumnWidth(final int pWidth) {
        theColumn.setPreferredWidth(pWidth);
        return this;
    }

    @Override
    public TethysUISwingTableColumn<T, C, R> setName(final String pName) {
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

    /**
     * Column Definition.
     *
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysUISwingTableValidatedColumn<T, C, R>
            extends TethysUISwingTableColumn<T, C, R>
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
        TethysUISwingTableValidatedColumn(final TethysUISwingTableManager<C, R> pTable,
                                          final C pId,
                                          final TethysUIFieldType pType) {
            /* Call super-constructor */
            super(pTable, pId, pType);

            /* Initialise validator */
            theValidator = (t, r) -> null;
        }

        @Override
        public TethysUISwingTableValidatedColumn<T, C, R> setValidator(final BiFunction<T, R, String> pValidator) {
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
    public static class TethysUISwingTableStringColumn<C, R>
            extends TethysUISwingTableValidatedColumn<String, C, R>
            implements TethysUITableStringColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableStringColumn(final TethysUISwingTableManager<C, R> pTable,
                                       final C pId) {
            super(pTable, pId, TethysUIFieldType.STRING);
            declareCell(getTable().getCellFactory().stringCell(this));
        }

        @Override
        public TethysUISwingTableStringColumn<C, R> setValidator(final BiFunction<String, R, String> pValidator) {
            return (TethysUISwingTableStringColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * CharArray Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableCharArrayColumn<C, R>
            extends TethysUISwingTableValidatedColumn<char[], C, R>
            implements TethysUITableCharArrayColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableCharArrayColumn(final TethysUISwingTableManager<C, R> pTable,
                                          final C pId) {
            super(pTable, pId, TethysUIFieldType.CHARARRAY);
            declareCell(getTable().getCellFactory().charArrayCell(this));
        }

        @Override
        public TethysUISwingTableCharArrayColumn<C, R> setValidator(final BiFunction<char[], R, String> pValidator) {
            return (TethysUISwingTableCharArrayColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Short Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableShortColumn<C, R>
            extends TethysUISwingTableValidatedColumn<Short, C, R>
            implements TethysUITableShortColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableShortColumn(final TethysUISwingTableManager<C, R> pTable,
                                      final C pId) {
            super(pTable, pId, TethysUIFieldType.SHORT);
            declareCell(getTable().getCellFactory().shortCell(this));
        }

        @Override
        public TethysUISwingTableShortColumn<C, R> setValidator(final BiFunction<Short, R, String> pValidator) {
            return (TethysUISwingTableShortColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Integer Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableIntegerColumn<C, R>
            extends TethysUISwingTableValidatedColumn<Integer, C, R>
            implements TethysUITableIntegerColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableIntegerColumn(final TethysUISwingTableManager<C, R> pTable,
                                        final C pId) {
            super(pTable, pId, TethysUIFieldType.INTEGER);
            declareCell(getTable().getCellFactory().integerCell(this));
        }

        @Override
        public TethysUISwingTableIntegerColumn<C, R> setValidator(final BiFunction<Integer, R, String> pValidator) {
            return (TethysUISwingTableIntegerColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Long Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableLongColumn<C, R>
            extends TethysUISwingTableValidatedColumn<Long, C, R>
            implements TethysUITableLongColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableLongColumn(final TethysUISwingTableManager<C, R> pTable,
                                     final C pId) {
            super(pTable, pId, TethysUIFieldType.LONG);
            declareCell(getTable().getCellFactory().longCell(this));
        }

        @Override
        public TethysUISwingTableLongColumn<C, R> setValidator(final BiFunction<Long, R, String> pValidator) {
            return (TethysUISwingTableLongColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * RawDecimal Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableRawDecimalColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysDecimal, C, R>
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
        TethysUISwingTableRawDecimalColumn(final TethysUISwingTableManager<C, R> pTable,
                                           final C pId) {
            super(pTable, pId, TethysUIFieldType.DECIMAL);
            declareCell(getTable().getCellFactory().rawDecimalCell(this));
            theSupplier = p -> TethysUICoreRawDecimalEditConverter.DEFAULT_DECIMALS;
        }

        @Override
        public TethysUISwingTableRawDecimalColumn<C, R> setValidator(final BiFunction<TethysDecimal, R, String> pValidator) {
            return (TethysUISwingTableRawDecimalColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUISwingTableRawDecimalColumn<C, R> setNumDecimals(final ToIntFunction<R> pSupplier) {
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
    public static class TethysUISwingTableMoneyColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysMoney, C, R>
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
        TethysUISwingTableMoneyColumn(final TethysUISwingTableManager<C, R> pTable,
                                      final C pId) {
            super(pTable, pId, TethysUIFieldType.MONEY);
            declareCell(getTable().getCellFactory().moneyCell(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysUISwingTableMoneyColumn<C, R> setValidator(final BiFunction<TethysMoney, R, String> pValidator) {
            return (TethysUISwingTableMoneyColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUISwingTableMoneyColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
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
    public static class TethysUISwingTablePriceColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysPrice, C, R>
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
        TethysUISwingTablePriceColumn(final TethysUISwingTableManager<C, R> pTable,
                                      final C pId) {
            super(pTable, pId, TethysUIFieldType.PRICE);
            declareCell(getTable().getCellFactory().priceCell(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysUISwingTablePriceColumn<C, R> setValidator(final BiFunction<TethysPrice, R, String> pValidator) {
            return (TethysUISwingTablePriceColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUISwingTablePriceColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
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
    public static class TethysUISwingTableRateColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysRate, C, R>
            implements TethysUITableRateColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableRateColumn(final TethysUISwingTableManager<C, R> pTable,
                                     final C pId) {
            super(pTable, pId, TethysUIFieldType.RATE);
            declareCell(getTable().getCellFactory().rateCell(this));
        }

        @Override
        public TethysUISwingTableRateColumn<C, R> setValidator(final BiFunction<TethysRate, R, String> pValidator) {
            return (TethysUISwingTableRateColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Units Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableUnitsColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysUnits, C, R>
            implements TethysUITableUnitsColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableUnitsColumn(final TethysUISwingTableManager<C, R> pTable,
                                      final C pId) {
            super(pTable, pId, TethysUIFieldType.UNITS);
            declareCell(getTable().getCellFactory().unitsCell(this));
        }

        @Override
        public TethysUISwingTableUnitsColumn<C, R> setValidator(final BiFunction<TethysUnits, R, String> pValidator) {
            return (TethysUISwingTableUnitsColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Dilution Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableDilutionColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysDilution, C, R>
            implements TethysUITableDilutionColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableDilutionColumn(final TethysUISwingTableManager<C, R> pTable,
                                         final C pId) {
            super(pTable, pId, TethysUIFieldType.DILUTION);
            declareCell(getTable().getCellFactory().dilutionCell(this));
        }

        @Override
        public TethysUISwingTableDilutionColumn<C, R> setValidator(final BiFunction<TethysDilution, R, String> pValidator) {
            return (TethysUISwingTableDilutionColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * Ratio Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableRatioColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysRatio, C, R>
            implements TethysUITableRatioColumn<C, R> {
        /**
         * Constructor.
         *
         * @param pTable the table
         * @param pId    the id
         */
        TethysUISwingTableRatioColumn(final TethysUISwingTableManager<C, R> pTable,
                                      final C pId) {
            super(pTable, pId, TethysUIFieldType.RATIO);
            declareCell(getTable().getCellFactory().ratioCell(this));
        }

        @Override
        public TethysUISwingTableRatioColumn<C, R> setValidator(final BiFunction<TethysRatio, R, String> pValidator) {
            return (TethysUISwingTableRatioColumn<C, R>) super.setValidator(pValidator);
        }
    }

    /**
     * DilutedPrice Column.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableDilutedPriceColumn<C, R>
            extends TethysUISwingTableValidatedColumn<TethysDilutedPrice, C, R>
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
        TethysUISwingTableDilutedPriceColumn(final TethysUISwingTableManager<C, R> pTable,
                                             final C pId) {
            super(pTable, pId, TethysUIFieldType.DILUTEDPRICE);
            declareCell(getTable().getCellFactory().dilutedPriceCell(this));
            theSupplier = p -> null;
        }

        @Override
        public TethysUISwingTableDilutedPriceColumn<C, R> setValidator(final BiFunction<TethysDilutedPrice, R, String> pValidator) {
            return (TethysUISwingTableDilutedPriceColumn<C, R>) super.setValidator(pValidator);
        }

        @Override
        public TethysUISwingTableDilutedPriceColumn<C, R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
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
    public static class TethysUISwingTableDateColumn<C, R>
            extends TethysUISwingTableColumn<TethysDate, C, R>
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
        TethysUISwingTableDateColumn(final TethysUISwingTableManager<C, R> pTable,
                                     final C pId) {
            super(pTable, pId, TethysUIFieldType.DATE);
            declareCell(getTable().getCellFactory().dateCell(this));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public TethysUISwingTableDateColumn<C, R> setDateConfigurator(final BiConsumer<R, TethysDateConfig> pConfigurator) {
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
    public static class TethysUISwingTableScrollColumn<T, C, R>
            extends TethysUISwingTableColumn<T, C, R>
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
        TethysUISwingTableScrollColumn(final TethysUISwingTableManager<C, R> pTable,
                                       final C pId,
                                       final Class<T> pClazz) {
            super(pTable, pId, TethysUIFieldType.SCROLL);
            declareCell(getTable().getCellFactory().scrollCell(this, pClazz));
            theConfigurator = (r, c) -> {
            };
        }

        @Override
        public TethysUISwingTableScrollColumn<T, C, R> setMenuConfigurator(final BiConsumer<R, TethysUIScrollMenu<T>> pConfigurator) {
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
    public static class TethysUISwingTableListColumn<T extends Comparable<? super T>, C, R>
            extends TethysUISwingTableColumn<List<T>, C, R>
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
        TethysUISwingTableListColumn(final TethysUISwingTableManager<C, R> pTable,
                                     final C pId,
                                     final Class<T> pClazz) {
            super(pTable, pId, TethysUIFieldType.LIST);
            declareCell(getTable().getCellFactory().listCell(this));
            theSelectables = r -> Collections.emptyIterator();
        }

        @Override
        public TethysUISwingTableListColumn<T, C, R> setSelectables(final Function<R, Iterator<T>> pSelectables) {
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
    public static class TethysUISwingTableIconColumn<T, C, R>
            extends TethysUISwingTableColumn<T, C, R>
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
        TethysUISwingTableIconColumn(final TethysUISwingTableManager<C, R> pTable,
                                     final C pId,
                                     final Class<T> pClazz) {
            super(pTable, pId, TethysUIFieldType.ICON);
            declareCell(getTable().getCellFactory().iconCell(this, pClazz));
            theSupplier = p -> null;
        }

        @Override
        public TethysUISwingTableIconColumn<T, C, R> setIconMapSet(final Function<R, TethysUIIconMapSet<T>> pSupplier) {
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
