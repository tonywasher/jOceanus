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
package net.sourceforge.joceanus.jtethys.ui.api.table;

import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

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
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * Tethys Table Manager.
 * @param <C> the column identity
 * @param <R> the row type
 */
public interface TethysUITableManager<C, R>
        extends TethysUIComponent {
    /**
     * Is the table editable?
     * @return true/false
     */
    boolean isEditable();

    /**
     * Set the edit-ability of the table.
     * @param pEditable true/false
     * @return the table
     */
    TethysUITableManager<C, R> setEditable(boolean pEditable);

    /**
     * RequestFocus.
     */
    void requestFocus();

    /**
     * do we rePaintRow on commit?
     * @return true/false
     */
    boolean doRePaintRowOnCommit();

    /**
     * Set repaintRow on Commit.
     * @param pRePaint the flag
     * @return the table
     */
    TethysUITableManager<C, R> setRepaintRowOnCommit(boolean pRePaint);

    /**
     * Set the error predicate.
     * @param pError the error predicate
     * @return the table
     */
    TethysUITableManager<C, R> setError(BiPredicate<C, R> pError);

    /**
     * Is the cell in error?
     * @param pId the column id
     * @param pRow the row
     * @return true/false
     */
    boolean isError(C pId,
                    R pRow);

    /**
     * Set the changed predicate.
     * @param pChanged the changed predicate
     * @return the table
     */
    TethysUITableManager<C, R> setChanged(BiPredicate<C, R> pChanged);

    /**
     * Is the cell changed?
     * @param pId the column id
     * @param pRow the row
     * @return true/false
     */
    boolean isChanged(C pId,
                      R pRow);

    /**
     * Set the disabled predicate.
     * @param pDisabled the disabled predicate
     * @return the table
     */
    TethysUITableManager<C, R> setDisabled(Predicate<R> pDisabled);

    /**
     * Is the row disabled?
     * @param pRow the row
     * @return true/false
     */
    boolean isDisabled(R pRow);

    /**
     * Set the filter.
     * @param pFilter the filter
     * @return the table
     */
    TethysUITableManager<C, R> setFilter(Predicate<R> pFilter);

    /**
     * Set the comparator.
     * @param pComparator the comparator
     * @return the table
     */
    TethysUITableManager<C, R> setComparator(Comparator<R> pComparator);

    /**
     * Set the on-commit consumer.
     * @param pOnCommit the consumer
     * @return the table
     */
    TethysUITableManager<C, R> setOnCommit(TethysUIOnRowCommit<R> pOnCommit);

    /**
     * Set the on-select consumer.
     * @param pOnSelect the consumer
     * @return the table
     */
    TethysUITableManager<C, R> setOnSelect(TethysUIOnRowSelect<R> pOnSelect);

    /**
     * Select a row and ensure that it is visible.
     * @param pItem the row to select
     */
    void selectRowWithScroll(R pItem);

    /**
     * Set the on-cellEditState consumer.
     * @param pOnCellEditState the consumer
     * @return the table
     */
    TethysUITableManager<C, R> setOnCellEditState(Consumer<Boolean> pOnCellEditState);

    /**
     * Set the on-commitError consumer.
     * @param pOnCommitError the consumer
     * @return the table
     */
    TethysUITableManager<C, R> setOnCommitError(Consumer<OceanusException> pOnCommitError);

    /**
     * Set the on-validateError consumer.
     * @param pOnValidateError the consumer
     * @return the tabke
     */
    TethysUITableManager<C, R> setOnValidateError(Consumer<String> pOnValidateError);

    /**
     * obtain onValidateError consumer.
     * @return the consumer
     */
    Consumer<String> getOnValidateError();

    /**
     * Cancel editing.
     */
    void cancelEditing();

    /**
     * Obtain an iterator over the unsorted items.
     * @return the iterator.
     */
    Iterator<R> itemIterator();

    /**
     * Obtain an iterator over the sorted items.
     * @return the iterator.
     */
    Iterator<R> sortedIterator();

    /**
     * Obtain an iterator over the sorted and filtered items.
     * @return the iterator.
     */
    Iterator<R> viewIterator();

    /**
     * Obtain the column for the id.
     * @param pId the id of the column
     * @return the table column
     */
    TethysUITableColumn<?, C, R> getColumn(C pId);

    /**
     * Repaint the column.
     * @param pId the column id
     */
    void repaintColumn(C pId);

    /**
     * Declare string column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableStringColumn<C, R> declareStringColumn(C pId);

    /**
     * Declare charArray column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableCharArrayColumn<C, R> declareCharArrayColumn(C pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableShortColumn<C, R> declareShortColumn(C pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableIntegerColumn<C, R> declareIntegerColumn(C pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableLongColumn<C, R> declareLongColumn(C pId);

    /**
     * Declare rawDecimal column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableRawDecimalColumn<C, R> declareRawDecimalColumn(C pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableMoneyColumn<C, R> declareMoneyColumn(C pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    TethysUITablePriceColumn<C, R> declarePriceColumn(C pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableRateColumn<C, R> declareRateColumn(C pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableUnitsColumn<C, R> declareUnitsColumn(C pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableDilutionColumn<C, R> declareDilutionColumn(C pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableRatioColumn<C, R> declareRatioColumn(C pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableDilutedPriceColumn<C, R> declareDilutedPriceColumn(C pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableDateColumn<C, R> declareDateColumn(C pId);

    /**
     * Declare scroll column.
     * @param <T> the data type
     * @param pId the column id
     * @param pClazz the column class
     * @return the column
     */
    <T> TethysUITableScrollColumn<T, C, R> declareScrollColumn(C pId,
                                                               Class<T> pClazz);

    /**
     * Declare list column.
     * @param <T> the data type
     * @param pId the column id
     * @param pClazz the data class
     * @return the column
     */
    <T extends Comparable<T>> TethysUITableListColumn<T, C, R> declareListColumn(C pId,
                                                                               Class<T> pClazz);

    /**
     * Declare icon column.
     * @param <T> the data type
     * @param pId the column id
     * @param pClazz the column class
     * @return the column
     */
    <T> TethysUITableIconColumn<T, C, R> declareIconColumn(C pId,
                                                           Class<T> pClazz);

    /**
     * Column Definition.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableColumn<T, C, R>
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
     * Dilution Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableDilutionColumn<C, R>
            extends TethysUITableValidatedColumn<TethysDilution, R>,
            TethysUITableColumn<TethysDilution, C, R> {
        @Override
        TethysUITableDilutionColumn<C, R> setValidator(BiFunction<TethysDilution, R, String> pValidator);
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
     * DilutedPrice Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableDilutedPriceColumn<C, R>
            extends TethysUITableCurrencyColumn<TethysDilutedPrice, R>,
            TethysUITableColumn<TethysDilutedPrice, C, R> {
        @Override
        TethysUITableDilutedPriceColumn<C, R> setValidator(BiFunction<TethysDilutedPrice, R, String> pValidator);

        @Override
        TethysUITableDilutedPriceColumn<C, R> setDeemedCurrency(Function<R, Currency> pSupplier);
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
    interface TethysUITableListConfig<T extends Comparable<T>, R> {
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
    interface TethysUITableListColumn<T extends Comparable<T>, C, R>
            extends TethysUITableListConfig<T, R>,
            TethysUITableColumn<List<T>, C, R> {
        @Override
        TethysUITableListColumn<T, C, R> setSelectables(Function<R, Iterator<T>> pSelectables);
    }

    /**
     * Cell interface.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     */
    interface TethysUITableCell<T, C, R> {
        /**
         * Obtain the table.
         * @return the column
         */
        TethysUITableManager<C, R> getTable();

        /**
         * Obtain the column.
         * @return the column
         */
        TethysUITableColumn<T, C, R> getColumn();

        /**
         * Obtain the control.
         * @return the field
         */
        TethysUIDataEditField<T> getControl();

        /**
         * obtain the current row.
         * @return the row (or null)
         */
        R getActiveRow();

        /**
         * Obtain the id of the column.
         * @return the column id
         */
        C getColumnId();

        /**
         * Obtain the type of the column.
         * @return the column type
         */
        TethysUIFieldType getCellType();

        /**
         * Cell changed during edit.
         * @param pId the column id
         */
        void repaintColumnCell(C pId);

        /**
         * Row changed during edit.
         */
        void repaintCellRow();
    }

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
     * OnRow commit callback.
     * @param <R> the row type
     */
    @FunctionalInterface
    interface TethysUIOnRowCommit<R> {
        /**
         * CallBack on a rowCommit.
         * @param pRow the row that is being committed
         * @throws OceanusException on error
         */
        void commitRow(R pRow) throws OceanusException;
    }

    /**
     * OnRow select callback.
     * @param <R> the row type
     */
    @FunctionalInterface
    interface TethysUIOnRowSelect<R> {
        /**
         * CallBack on a rowSelect.
         * @param pRow the row that is being committed
         */
        void selectRow(R pRow);
    }
}
