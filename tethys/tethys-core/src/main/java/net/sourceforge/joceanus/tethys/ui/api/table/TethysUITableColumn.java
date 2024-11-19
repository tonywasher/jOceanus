/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.api.table;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.date.TethysDate;
import net.sourceforge.joceanus.tethys.date.TethysDateConfig;
import net.sourceforge.joceanus.tethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.tethys.decimal.TethysMoney;
import net.sourceforge.joceanus.tethys.decimal.TethysPrice;
import net.sourceforge.joceanus.tethys.decimal.TethysRate;
import net.sourceforge.joceanus.tethys.decimal.TethysRatio;
import net.sourceforge.joceanus.tethys.decimal.TethysUnits;
import net.sourceforge.joceanus.tethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIScrollMenu;

/**
 * Column Definition.
 * @param <T> the data type
 * @param <C> the column identity
 * @param <R> the row type
 */
public interface TethysUITableColumn<T, C, R>
        extends TethysEventProvider<TethysUIEvent> {
    /**
     * Obtain the table manager.
     * @return the table manager
     */
    TethysUITableManager<C, R> getTable();

    /**
     * Obtain the id of the column.
     * @return the column id
     */
    C getId();

    /**
     * Obtain the type of the column.
     * @return the column type
     */
    TethysUIFieldType getCellType();

    /**
     * Obtain the name of the column.
     * @return the column name
     */
    String getName();

    /**
     * Set the name of the column.
     * @param pName the column name
     * @return the column
     */
    TethysUITableColumn<T, C, R> setName(String pName);

    /**
     * Set the column width.
     * @param pWidth the width
     * @return the column
     */
    TethysUITableColumn<T, C, R> setColumnWidth(int pWidth);

    /**
     * Is the column visible?
     * @return true/false
     */
    boolean isVisible();

    /**
     * Set the visibility of the column.
     * @param pVisible true/false
     * @return the column
     */
    TethysUITableColumn<T, C, R> setVisible(boolean pVisible);

    /**
     * Is the column editable?
     * @return true/false
     */
    boolean isEditable();

    /**
     * Set the edit-ability of the column.
     * @param pEditable true/false
     * @return the column
     */
    TethysUITableColumn<T, C, R> setEditable(boolean pEditable);

    /**
     * Set cell value Factory.
     *
     * @param pFactory the cell factory
     * @return the column
     */
    TethysUITableColumn<T, C, R> setCellValueFactory(Function<R, T> pFactory);

    /**
     * Obtain value for row.
     *
     * @param pRow the row
     * @return the value
     */
    T getValueForRow(R pRow);

    /**
     * Set the cell-editable tester.
     * @param pEditable the editable tester
     * @return the column
     */
    TethysUITableColumn<T, C, R> setCellEditable(Predicate<R> pEditable);

    /**
     * Get the cell-editable tester.
     * @return the current tester
     */
    Predicate<R> getCellEditable();

    /**
     * Set the on-commit consumer.
     * @param pOnCommit the consumer
     * @return the column
     */
    TethysUITableColumn<T, C, R> setOnCommit(TethysUIOnCellCommit<R, T> pOnCommit);

    /**
     * do we rePaintColumn on commit?
     * @return true/false
     */
    boolean doRePaintColumnOnCommit();

    /**
     * Set repaintColumn on Commit.
     * @param pRePaint the flag
     * @return the column
     */
    TethysUITableColumn<T, C, R> setRepaintColumnOnCommit(boolean pRePaint);

    /**
     * get the column id which forces a rePaint.
     * @return the column id
     */
    C getRePaintColumnId();

    /**
     * Set repaintColumnId.
     * @param pRePaintId the repaint id
     * @return the column
     */
    TethysUITableColumn<T, C, R> setRepaintColumnId(C pRePaintId);

    /**
     * OnCell commit callback.
     * @param <R> the row type
     * @param <T> the value type
     */
    @FunctionalInterface
    interface TethysUIOnCellCommit<R, T> {
        /**
         * CallBack on a columnCommit.
         * @param pRow the row that is being committed
         * @param pValue the new value
         * @throws OceanusException on error
         */
        void commitCell(R pRow, T pValue) throws OceanusException;
    }
    /**
     * Validated Column Definition.
     * @param <T> the data type
     * @param <R> the row type
     */
    interface TethysUITableValidatedColumn<T, R> {
        /**
         * Set the validity tester.
         * @param pValidator the validator
         * @return the column
         */
        TethysUITableValidatedColumn<T, R> setValidator(BiFunction<T, R, String> pValidator);
    }

    /**
     * String Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableStringColumn<C, R>
            extends TethysUITableValidatedColumn<String, R>,
            TethysUITableColumn<String, C, R> {
        @Override
        TethysUITableStringColumn<C, R> setValidator(BiFunction<String, R, String> pValidator);
    }

    /**
     * CharArray Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableCharArrayColumn<C, R>
            extends TethysUITableValidatedColumn<char[], R>,
            TethysUITableColumn<char[], C, R> {
        @Override
        TethysUITableCharArrayColumn<C, R> setValidator(BiFunction<char[], R, String> pValidator);
    }

    /**
     * Short Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableShortColumn<C, R>
            extends TethysUITableValidatedColumn<Short, R>,
            TethysUITableColumn<Short, C, R> {
        @Override
        TethysUITableShortColumn<C, R> setValidator(BiFunction<Short, R, String> pValidator);
    }

    /**
     * Integer Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableIntegerColumn<C, R>
            extends TethysUITableValidatedColumn<Integer, R>,
            TethysUITableColumn<Integer, C, R> {
        @Override
        TethysUITableIntegerColumn<C, R> setValidator(BiFunction<Integer, R, String> pValidator);
    }

    /**
     * Long Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableLongColumn<C, R>
            extends TethysUITableValidatedColumn<Long, R>,
            TethysUITableColumn<Long, C, R> {
        @Override
        TethysUITableLongColumn<C, R> setValidator(BiFunction<Long, R, String> pValidator);
    }

    /**
     * DecimalTableColumn.
     * @param <R> the row type
     */
    interface TethysUITableDecimalColumn<R>
            extends TethysUITableValidatedColumn<TethysDecimal, R> {
        /**
         * Set the Number of decimals supplier.
         * @param pSupplier the supplier
         * @return the column
         */
        TethysUITableDecimalColumn<R> setNumDecimals(ToIntFunction<R> pSupplier);
    }

    /**
     * CurrencyTableColumn.
     * @param <T> the money type
     * @param <R> the row type
     */
    interface TethysUITableCurrencyColumn<T extends TethysMoney, R>
            extends TethysUITableValidatedColumn<T, R> {
        /**
         * Set the Deemed Currency supplier.
         * @param pSupplier the supplier
         * @return the column
         */
        TethysUITableCurrencyColumn<T, R> setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * RawDecimal Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableRawDecimalColumn<C, R>
            extends TethysUITableDecimalColumn<R>,
            TethysUITableColumn<TethysDecimal, C, R> {
        @Override
        TethysUITableRawDecimalColumn<C, R> setValidator(BiFunction<TethysDecimal, R, String> pValidator);

        @Override
        TethysUITableRawDecimalColumn<C, R> setNumDecimals(ToIntFunction<R> pSupplier);
    }

    /**
     * Money Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableMoneyColumn<C, R>
            extends TethysUITableCurrencyColumn<TethysMoney, R>,
            TethysUITableColumn<TethysMoney, C, R> {
        @Override
        TethysUITableMoneyColumn<C, R> setValidator(BiFunction<TethysMoney, R, String> pValidator);

        @Override
        TethysUITableMoneyColumn<C, R> setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * Price Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITablePriceColumn<C, R>
            extends TethysUITableCurrencyColumn<TethysPrice, R>,
            TethysUITableColumn<TethysPrice, C, R> {
        @Override
        TethysUITablePriceColumn<C, R> setValidator(BiFunction<TethysPrice, R, String> pValidator);

        @Override
        TethysUITablePriceColumn<C, R> setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * Units Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableUnitsColumn<C, R>
            extends TethysUITableValidatedColumn<TethysUnits, R>,
            TethysUITableColumn<TethysUnits, C, R> {
        @Override
        TethysUITableUnitsColumn<C, R> setValidator(BiFunction<TethysUnits, R, String> pValidator);
    }

    /**
     * Rate Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableRateColumn<C, R>
            extends TethysUITableValidatedColumn<TethysRate, R>,
            TethysUITableColumn<TethysRate, C, R> {
        @Override
        TethysUITableRateColumn<C, R> setValidator(BiFunction<TethysRate, R, String> pValidator);
    }

    /**
     * Ratio Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableRatioColumn<C, R>
            extends TethysUITableValidatedColumn<TethysRatio, R>,
            TethysUITableColumn<TethysRatio, C, R> {
        @Override
        TethysUITableRatioColumn<C, R> setValidator(BiFunction<TethysRatio, R, String> pValidator);
    }

    /**
     * IconTableColumn.
     * @param <T> the data type
     * @param <R> the row type
     */
    interface TethysUITableIconConfig<T, R> {
        /**
         * Set the IconMapSet supplier.
         * @param pSupplier the supplier
         * @return the configurator
         */
        TethysUITableIconConfig<T, R> setIconMapSet(Function<R, TethysUIIconMapSet<T>> pSupplier);
    }

    /**
     * IconTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableIconColumn<T, C, R>
            extends TethysUITableIconConfig<T, R>,
            TethysUITableColumn<T, C, R> {
        @Override
        TethysUITableIconColumn<T, C, R> setIconMapSet(Function<R, TethysUIIconMapSet<T>> pSupplier);
    }

    /**
     * DateTableColumn.
     * @param <R> the row type
     */
    interface TethysUITableDateConfig<R> {
        /**
         * Set the Date configurator.
         * @param pConfigurator the configurator
         * @return the configurator
         */
        TethysUITableDateConfig<R> setDateConfigurator(BiConsumer<R, TethysDateConfig> pConfigurator);
    }

    /**
     * DateTableColumn.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableDateColumn<C, R>
            extends TethysUITableDateConfig<R>,
            TethysUITableColumn<TethysDate, C, R> {
        @Override
        TethysUITableDateColumn<C, R> setDateConfigurator(BiConsumer<R, TethysDateConfig> pConfigurator);
    }

    /**
     * ScrollTableColumn.
     * @param <T> the data type
     * @param <R> the row type
     */
    interface TethysUITableScrollConfig<T, R> {
        /**
         * Set the Menu configurator.
         * @param pConfigurator the configurator
         * @return the configurator
         */
        TethysUITableScrollConfig<T, R> setMenuConfigurator(BiConsumer<R, TethysUIScrollMenu<T>> pConfigurator);
    }

    /**
     * ScrollTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableScrollColumn<T, C, R>
            extends TethysUITableScrollConfig<T, R>,
            TethysUITableColumn<T, C, R> {
        @Override
        TethysUITableScrollColumn<T, C, R> setMenuConfigurator(BiConsumer<R, TethysUIScrollMenu<T>> pConfigurator);
    }

    /**
     * ListTableColumn.
     * @param <T> the data type
     * @param <R> the row type
     */
    interface TethysUITableListConfig<T extends Comparable<? super T>, R> {
        /**
         * Set the selectable supplier.
         * @param pSelectables the supplier
         * @return the configurator
         */
        TethysUITableListConfig<T, R> setSelectables(Function<R, Iterator<T>> pSelectables);
    }

    /**
     * ListTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableListColumn<T extends Comparable<? super T>, C, R>
            extends TethysUITableListConfig<T, R>,
            TethysUITableColumn<List<T>, C, R> {
        @Override
        TethysUITableListColumn<T, C, R> setSelectables(Function<R, Iterator<T>> pSelectables);
    }
}
