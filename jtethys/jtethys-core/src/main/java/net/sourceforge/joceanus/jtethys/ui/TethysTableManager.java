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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;

/**
 * Tethys Table Manager.
 * @param <C> the column identity
 * @param <R> the row type
 */
public abstract class TethysTableManager<C, R>
        implements TethysComponent {
    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The map of columns.
     */
    private final Map<C, TethysBaseTableColumn<?, C, R>> theColumnMap;

    /**
     * The last child item.
     */
    private TethysBaseTableColumn<?, C, R> theLastChild;

    /**
     * The Error Predicate.
     */
    private BiPredicate<C, R> theError;

    /**
     * The Changed Predicate.
     */
    private BiPredicate<C, R> theChanged;

    /**
     * The Disabled Predicate.
     */
    private Predicate<R> theDisabled;

    /**
     * The OnCommit Consumer.
     */
    private TethysOnRowCommit<R> theOnCommit;

    /**
     * The OnSelect Consumer.
     */
    private TethysOnRowSelect<R> theOnSelect;

    /**
     * The OnCommitError Consumer.
     */
    private Consumer<OceanusException> theOnCommitError;

    /**
     * The OnValidateError Consumer.
     */
    private Consumer<String> theOnValidateError;

    /**
     * The OnCellEditState Consumer.
     */
    private Consumer<Boolean> theOnCellEditState;

    /**
     * The Comparator.
     */
    private Comparator<R> theComparator;

    /**
     * The Filter.
     */
    private Predicate<R> theFilter;

    /**
     * Is the table editable?
     */
    private boolean isEditable;

    /**
     * Repaint the row on commit?
     */
    private boolean doRePaintRowOnCommit;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysTableManager(final TethysGuiFactory pFactory) {
        theId = pFactory.getNextId();
        theColumnMap = new HashMap<>();
        isEditable = true;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Set the Preferred Width and Height.
     * @param pWidth the width
     * @param pHeight the height
     */
    public abstract void setPreferredWidthAndHeight(Integer pWidth,
                                                    Integer pHeight);

    /**
     * Is the table editable?
     * @return true/false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Set the edit-ability of the table.
     * @param pEditable true/false
     * @return the table
     */
    public TethysTableManager<C, R> setEditable(final boolean pEditable) {
        isEditable = pEditable;
        return this;
    }

    /**
     * RequestFocus.
     */
    public abstract void requestFocus();

    /**
     * do we rePaintRow on commit?
     * @return true/false
     */
    public boolean doRePaintRowOnCommit() {
        return doRePaintRowOnCommit;
    }

    /**
     * Set repaintRow on Commit.
     * @param pRePaint the flag
     * @return the table
     */
    public TethysTableManager<C, R> setRepaintRowOnCommit(final boolean pRePaint) {
        doRePaintRowOnCommit = pRePaint;
        return this;
    }

    /**
     * Set the error predicate.
     * @param pError the error predicate
     * @return the table
     */
    public TethysTableManager<C, R> setError(final BiPredicate<C, R> pError) {
        theError = pError;
        return this;
    }

    /**
     * Is the cell in error?
     * @param pId the column id
     * @param pRow the row
     * @return true/false
     */
    public boolean isError(final C pId,
                           final R pRow) {
        return theError != null
               && theError.test(pId, pRow);
    }

    /**
     * Set the changed predicate.
     * @param pChanged the changed predicate
     * @return the table
     */
    public TethysTableManager<C, R> setChanged(final BiPredicate<C, R> pChanged) {
        theChanged = pChanged;
        return this;
    }

    /**
     * Is the cell changed?
     * @param pId the column id
     * @param pRow the row
     * @return true/false
     */
    public boolean isChanged(final C pId,
                             final R pRow) {
        return theChanged != null
               && theChanged.test(pId, pRow);
    }

    /**
     * Set the disabled predicate.
     * @param pDisabled the disabled predicate
     * @return the table
     */
    public TethysTableManager<C, R> setDisabled(final Predicate<R> pDisabled) {
        theDisabled = pDisabled;
        return this;
    }

    /**
     * Is the row disabled?
     * @param pRow the row
     * @return true/false
     */
    public boolean isDisabled(final R pRow) {
        return theDisabled != null
               && theDisabled.test(pRow);
    }

    /**
     * Set the filter.
     * @param pFilter the filter
     * @return the table
     */
    public TethysTableManager<C, R> setFilter(final Predicate<R> pFilter) {
        theFilter = pFilter;
        return this;
    }

    /**
     * Obtain the filter.
     * @return the filter
     */
    protected Predicate<R> getFilter() {
        return theFilter;
    }

    /**
     * Set the comparator.
     * @param pComparator the comparator
     * @return the table
     */
    public TethysTableManager<C, R> setComparator(final Comparator<R> pComparator) {
        theComparator = pComparator;
        return this;
    }

    /**
     * Obtain the comparator.
     * @return the comparator
     */
    protected Comparator<R> getComparator() {
        return theComparator;
    }

    /**
     * Set the on-commit consumer.
     * @param pOnCommit the consumer
     * @return the table
     */
    public TethysTableManager<C, R> setOnCommit(final TethysOnRowCommit<R> pOnCommit) {
        theOnCommit = pOnCommit;
        return this;
    }

    /**
     * process onCommit.
     * @param pRow the row
     * @throws OceanusException on error
     */
    void processOnCommit(final R pRow) throws OceanusException {
        /* If we have an onCommit consumer */
        if (theOnCommit != null) {
            /* call it */
            theOnCommit.commitRow(pRow);
        }
    }

    /**
     * Set the on-select consumer.
     * @param pOnSelect the consumer
     * @return the table
     */
    public TethysTableManager<C, R> setOnSelect(final TethysOnRowSelect<R> pOnSelect) {
        theOnSelect = pOnSelect;
        return this;
    }

    /**
     * process onSelect.
     * @param pRow the row
     */
    protected void processOnSelect(final R pRow) {
        /* If we have an onSelect consumer */
        if (theOnSelect != null) {
            /* call it */
            theOnSelect.selectRow(pRow);
        }
    }

    /**
     * Select a row and ensure that it is visible.
     * @param pItem the row to select
     */
    public abstract void selectRowWithScroll(R pItem);

    /**
     * Set the on-celEditState consumer.
     * @param pOnCellEditState the consumer
     * @return the table
     */
    public TethysTableManager<C, R> setOnCellEditState(final Consumer<Boolean> pOnCellEditState) {
        theOnCellEditState = pOnCellEditState;
        return this;
    }

    /**
     * process onCellEditState.
     * @param pState the state
     */
    public void processOnCellEditState(final Boolean pState) {
        /* If we have an onCellEditState consumer */
        if (theOnCellEditState != null) {
            /* call it */
            theOnCellEditState.accept(pState);
        }
    }

    /**
     * Set the on-commitError consumer.
     * @param pOnCommitError the consumer
     * @return the table
     */
    public TethysTableManager<C, R> setOnCommitError(final Consumer<OceanusException> pOnCommitError) {
        theOnCommitError = pOnCommitError;
        return this;
    }

    /**
     * process onCommitError.
     * @param pError the error
     */
    public void processOnCommitError(final OceanusException pError) {
        /* If we have an onCommitError consumer */
        if (theOnCommitError != null) {
            /* call it */
            theOnCommitError.accept(pError);
        }
    }

    /**
     * Set the on-validateError consumer.
     * @param pOnValidateError the consumer
     * @return the tabke
     */
    public TethysTableManager<C, R> setOnValidateError(final Consumer<String> pOnValidateError) {
        theOnValidateError = pOnValidateError;
        return this;
    }

    /**
     * obtain onValidateError consumer.
     * @return the consumer
     */
    public Consumer<String> getOnValidateError() {
        return theOnValidateError;
    }

    /**
     * Cancel editing.
     */
    public abstract void cancelEditing();

    /**
     * Obtain an iterator over the unsorted items.
     * @return the iterator.
     */
    public abstract Iterator<R> itemIterator();

    /**
     * Obtain an iterator over the sorted items.
     * @return the iterator.
     */
    public abstract Iterator<R> sortedIterator();

    /**
     * Obtain an iterator over the sorted and filtered items.
     * @return the iterator.
     */
    public abstract Iterator<R> viewIterator();

    /**
     * Obtain the column for the id.
     * @param pId the id of the column
     * @return the table column
     */
    public TethysTableColumn<?, C, R> getColumn(final C pId) {
        return theColumnMap.get(pId);
    }

    /**
     * Repaint the column.
     * @param pId the column id
     */
    public void repaintColumn(final C pId) {
        final TethysTableColumn<?, C, R> myCol = theColumnMap.get(pId);
        if ((myCol != null)
            && (myCol.isVisible())) {
            myCol.setVisible(false);
            myCol.setVisible(true);
        }
    }

    /**
     * Register the column.
     * @param pColumn the column
     */
    void registerColumn(final TethysBaseTableColumn<?, C, R> pColumn) {
        theColumnMap.put(pColumn.getId(), pColumn);
    }

    /**
     * Repaint on Commit.
     * @param pCell the cell that was committed.
     */
    public void rePaintOnCommit(final TethysTableCell<?, C, R> pCell) {
        /* If we should rePaint the row on commit */
        if (doRePaintRowOnCommit) {
            /* Action the repaint */
            pCell.repaintCellRow();
        }

        /* Obtain the update id */
        final C myId = pCell.getColumnId();

        /* Loop through the columns */
        for (TethysBaseTableColumn<?, C, R> myColumn : theColumnMap.values()) {
            /* If we should rePaint the column on commit */
            if (myColumn.doRePaintColumnOnCommit()) {
                /* Action the repaint */
                repaintColumn(myColumn.getId());
            }

            /* If we should repaint this cell on commit */
            if (myId.equals(myColumn.getRePaintColumnId())) {
                /* Action the rePaint */
                pCell.repaintColumnCell(myColumn.getId());
            }
        }
    }

    /**
     * Declare string column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableStringColumn<C, R> declareStringColumn(C pId);

    /**
     * Declare charArray column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableCharArrayColumn<C, R> declareCharArrayColumn(C pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableShortColumn<C, R> declareShortColumn(C pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableIntegerColumn<C, R> declareIntegerColumn(C pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableLongColumn<C, R> declareLongColumn(C pId);

    /**
     * Declare rawDecimal column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableRawDecimalColumn<C, R> declareRawDecimalColumn(C pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableMoneyColumn<C, R> declareMoneyColumn(C pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTablePriceColumn<C, R> declarePriceColumn(C pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableRateColumn<C, R> declareRateColumn(C pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableUnitsColumn<C, R> declareUnitsColumn(C pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableDilutionColumn<C, R> declareDilutionColumn(C pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableRatioColumn<C, R> declareRatioColumn(C pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableDilutedPriceColumn<C, R> declareDilutedPriceColumn(C pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableDateColumn<C, R> declareDateColumn(C pId);

    /**
     * Declare scroll column.
     * @param <T> the data type
     * @param pId the column id
     * @param pClazz the column class
     * @return the column
     */
    public abstract <T> TethysTableScrollColumn<T, C, R> declareScrollColumn(C pId,
                                                                             Class<T> pClazz);

    /**
     * Declare list column.
     * @param <T> the data type
     * @param pId the column id
     * @param pClazz the data class
     * @return the column
     */
    public abstract <T extends Comparable<T>> TethysTableListColumn<T, C, R> declareListColumn(C pId,
                                                                                               Class<T> pClazz);

    /**
     * Declare icon column.
     * @param <T> the data type
     * @param pId the column id
     * @param pClazz the column class
     * @return the column
     */
    public abstract <T> TethysTableIconColumn<T, C, R> declareIconColumn(C pId,
                                                                         Class<T> pClazz);

    /**
     * Column Definition.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableColumn<T, C, R>
            extends TethysEventProvider<TethysUIEvent> {
        /**
         * Obtain the table manager.
         * @return the table manager
         */
        TethysTableManager<C, R> getTable();

        /**
         * Obtain the id of the column.
         * @return the column id
         */
        C getId();

        /**
         * Obtain the type of the column.
         * @return the column type
         */
        TethysFieldType getCellType();

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
        TethysTableColumn<T, C, R> setName(String pName);

        /**
         * Set the column width.
         * @param pWidth the width
         * @return the column
         */
        TethysTableColumn<T, C, R> setColumnWidth(int pWidth);

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
        TethysTableColumn<T, C, R> setVisible(boolean pVisible);

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
        TethysTableColumn<T, C, R> setEditable(boolean pEditable);

        /**
         * Set the cell-editable tester.
         * @param pEditable the editable tester
         * @return the column
         */
        TethysTableColumn<T, C, R> setCellEditable(Predicate<R> pEditable);

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
        TethysTableColumn<T, C, R> setOnCommit(TethysOnCellCommit<R, T> pOnCommit);

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
        TethysTableColumn<T, C, R> setRepaintColumnOnCommit(boolean pRePaint);

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
        TethysTableColumn<T, C, R> setRepaintColumnId(C pRePaintId);
    }

    /**
     * Validated Column Definition.
     * @param <T> the data type
     * @param <R> the row type
     */
    public interface TethysTableValidatedColumn<T, R> {
        /**
         * Set the validity tester.
         * @param pValidator the validator
         * @return the column
         */
        TethysTableValidatedColumn<T, R> setValidator(BiFunction<T, R, String> pValidator);
    }

    /**
     * String Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableStringColumn<C, R>
            extends TethysTableValidatedColumn<String, R>,
            TethysTableColumn<String, C, R> {
        @Override
        TethysTableStringColumn<C, R> setValidator(BiFunction<String, R, String> pValidator);
    }

    /**
     * CharArray Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableCharArrayColumn<C, R>
            extends TethysTableValidatedColumn<char[], R>,
            TethysTableColumn<char[], C, R> {
        @Override
        TethysTableCharArrayColumn<C, R> setValidator(BiFunction<char[], R, String> pValidator);
    }

    /**
     * Short Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableShortColumn<C, R>
            extends TethysTableValidatedColumn<Short, R>,
            TethysTableColumn<Short, C, R> {
        @Override
        TethysTableShortColumn<C, R> setValidator(BiFunction<Short, R, String> pValidator);
    }

    /**
     * Integer Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableIntegerColumn<C, R>
            extends TethysTableValidatedColumn<Integer, R>,
            TethysTableColumn<Integer, C, R> {
        @Override
        TethysTableIntegerColumn<C, R> setValidator(BiFunction<Integer, R, String> pValidator);
    }

    /**
     * Long Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableLongColumn<C, R>
            extends TethysTableValidatedColumn<Long, R>,
            TethysTableColumn<Long, C, R> {
        @Override
        TethysTableLongColumn<C, R> setValidator(BiFunction<Long, R, String> pValidator);
    }

    /**
     * DecimalTableColumn.
     * @param <R> the row type
     */
    public interface TethysTableDecimalColumn<R>
            extends TethysTableValidatedColumn<TethysDecimal, R> {
        /**
         * Set the Number of decimals supplier.
         * @param pSupplier the supplier
         * @return the column
         */
        TethysTableDecimalColumn<R> setNumDecimals(ToIntFunction<R> pSupplier);
    }

    /**
     * CurrencyTableColumn.
     * @param <T> the money type
     * @param <R> the row type
     */
    public interface TethysTableCurrencyColumn<T extends TethysMoney, R>
            extends TethysTableValidatedColumn<T, R> {
        /**
         * Set the Deemed Currency supplier.
         * @param pSupplier the supplier
         * @return the column
         */
        TethysTableCurrencyColumn<T, R> setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * RawDecimal Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableRawDecimalColumn<C, R>
            extends TethysTableDecimalColumn<R>,
            TethysTableColumn<TethysDecimal, C, R> {
        @Override
        TethysTableRawDecimalColumn<C, R> setValidator(BiFunction<TethysDecimal, R, String> pValidator);

        @Override
        TethysTableRawDecimalColumn<C, R> setNumDecimals(ToIntFunction<R> pSupplier);
    }

    /**
     * Money Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableMoneyColumn<C, R>
            extends TethysTableCurrencyColumn<TethysMoney, R>,
            TethysTableColumn<TethysMoney, C, R> {
        @Override
        TethysTableMoneyColumn<C, R> setValidator(BiFunction<TethysMoney, R, String> pValidator);

        @Override
        TethysTableMoneyColumn<C, R> setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * Price Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTablePriceColumn<C, R>
            extends TethysTableCurrencyColumn<TethysPrice, R>,
            TethysTableColumn<TethysPrice, C, R> {
        @Override
        TethysTablePriceColumn<C, R> setValidator(BiFunction<TethysPrice, R, String> pValidator);

        @Override
        TethysTablePriceColumn<C, R> setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * Units Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableUnitsColumn<C, R>
            extends TethysTableValidatedColumn<TethysUnits, R>,
            TethysTableColumn<TethysUnits, C, R> {
        @Override
        TethysTableUnitsColumn<C, R> setValidator(BiFunction<TethysUnits, R, String> pValidator);
    }

    /**
     * Rate Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableRateColumn<C, R>
            extends TethysTableValidatedColumn<TethysRate, R>,
            TethysTableColumn<TethysRate, C, R> {
        @Override
        TethysTableRateColumn<C, R> setValidator(BiFunction<TethysRate, R, String> pValidator);
    }

    /**
     * Dilution Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableDilutionColumn<C, R>
            extends TethysTableValidatedColumn<TethysDilution, R>,
            TethysTableColumn<TethysDilution, C, R> {
        @Override
        TethysTableDilutionColumn<C, R> setValidator(BiFunction<TethysDilution, R, String> pValidator);
    }

    /**
     * Ratio Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableRatioColumn<C, R>
            extends TethysTableValidatedColumn<TethysRatio, R>,
            TethysTableColumn<TethysRatio, C, R> {
        @Override
        TethysTableRatioColumn<C, R> setValidator(BiFunction<TethysRatio, R, String> pValidator);
    }

    /**
     * DilutedPrice Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableDilutedPriceColumn<C, R>
            extends TethysTableCurrencyColumn<TethysDilutedPrice, R>,
            TethysTableColumn<TethysDilutedPrice, C, R> {
        @Override
        TethysTableDilutedPriceColumn<C, R> setValidator(BiFunction<TethysDilutedPrice, R, String> pValidator);

        @Override
        TethysTableDilutedPriceColumn<C, R> setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * IconTableColumn.
     * @param <T> the data type
     * @param <R> the row type
     */
    public interface TethysTableIconConfig<T, R> {
        /**
         * Set the IconMapSet supplier.
         * @param pSupplier the supplier
         * @return the configurator
         */
        TethysTableIconConfig<T, R> setIconMapSet(Function<R, TethysIconMapSet<T>> pSupplier);
    }

    /**
     * IconTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableIconColumn<T, C, R>
            extends TethysTableIconConfig<T, R>,
            TethysTableColumn<T, C, R> {
        @Override
        TethysTableIconColumn<T, C, R> setIconMapSet(Function<R, TethysIconMapSet<T>> pSupplier);
    }

    /**
     * DateTableColumn.
     * @param <R> the row type
     */
    public interface TethysTableDateConfig<R> {
        /**
         * Set the Date configurator.
         * @param pConfigurator the configurator
         * @return the configurator
         */
        TethysTableDateConfig<R> setDateConfigurator(BiConsumer<R, TethysDateConfig> pConfigurator);
    }

    /**
     * DateTableColumn.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableDateColumn<C, R>
            extends TethysTableDateConfig<R>,
            TethysTableColumn<TethysDate, C, R> {
        @Override
        TethysTableDateColumn<C, R> setDateConfigurator(BiConsumer<R, TethysDateConfig> pConfigurator);
    }

    /**
     * ScrollTableColumn.
     * @param <T> the data type
     * @param <R> the row type
     */
    public interface TethysTableScrollConfig<T, R> {
        /**
         * Set the Menu configurator.
         * @param pConfigurator the configurator
         * @return the configurator
         */
        TethysTableScrollConfig<T, R> setMenuConfigurator(BiConsumer<R, TethysScrollMenu<T>> pConfigurator);
    }

    /**
     * ScrollTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableScrollColumn<T, C, R>
            extends TethysTableScrollConfig<T, R>,
            TethysTableColumn<T, C, R> {
        @Override
        TethysTableScrollColumn<T, C, R> setMenuConfigurator(BiConsumer<R, TethysScrollMenu<T>> pConfigurator);
    }

    /**
     * ListTableColumn.
     * @param <T> the data type
     * @param <R> the row type
     */
    public interface TethysTableListConfig<T extends Comparable<T>, R> {
        /**
         * Set the selectable supplier.
         * @param pSelectables the supplier
         * @return the configurator
         */
        TethysTableListConfig<T, R> setSelectables(Function<R, Iterator<T>> pSelectables);
    }

    /**
     * ListTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableListColumn<T extends Comparable<T>, C, R>
            extends TethysTableListConfig<T, R>,
            TethysTableColumn<List<T>, C, R> {
        @Override
        TethysTableListColumn<T, C, R> setSelectables(Function<R, Iterator<T>> pSelectables);
    }

    /**
     * Column Definition.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysBaseTableColumn<T, C, R>
            implements TethysTableColumn<T, C, R> {
        /**
         * The table.
         */
        private final TethysTableManager<C, R> theTable;

        /**
         * The event manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The id of the column.
         */
        private final C theId;

        /**
         * The cell type.
         */
        private final TethysFieldType theCellType;

        /**
         * The name of the column.
         */
        private String theName;

        /**
         * The previous sibling of this item.
         */
        private TethysBaseTableColumn<?, C, R> thePrevSibling;

        /**
         * Is the column visible?
         */
        private boolean isVisible;

        /**
         * Is the column editable?
         */
        private boolean isEditable;

        /**
         * The cell-editable predicate.
         */
        private Predicate<R> isCellEditable;

        /**
         * The cell-commit consumer.
         */
        private TethysOnCellCommit<R, T> theOnCommit;

        /**
         * Repaint the column on commit?
         */
        private boolean doRePaintColOnCommit;

        /**
         * The column that when updated, will force this column to update.
         */
        private C theRePaintId;

        /**
         * Constructor.
         * @param pTable the containing table
         * @param pId the id of the column
         * @param pType the type of the column
         */
        protected TethysBaseTableColumn(final TethysTableManager<C, R> pTable,
                                        final C pId,
                                        final TethysFieldType pType) {
            /* Store parameters */
            theTable = pTable;
            theId = pId;
            theCellType = pType;
            theName = pId.toString();
            isVisible = true;
            isEditable = true;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* If the table already has children */
            final TethysBaseTableColumn<?, C, R> myChild = theTable.theLastChild;
            if (myChild != null) {
                /* Link to last child */
                thePrevSibling = myChild;
            }

            /* Add as last child of pane */
            theTable.theLastChild = this;

            /* Register the column */
            theTable.registerColumn(this);

            /* Initialise editable */
            isCellEditable = p -> true;
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        @Override
        public TethysTableManager<C, R> getTable() {
            return theTable;
        }

        @Override
        public C getId() {
            return theId;
        }

        @Override
        public TethysFieldType getCellType() {
            return theCellType;
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public TethysBaseTableColumn<T, C, R> setName(final String pName) {
            theName = pName;
            return this;
        }

        @Override
        public boolean isVisible() {
            return isVisible;
        }

        @Override
        public boolean isEditable() {
            return isEditable;
        }

        @Override
        public boolean doRePaintColumnOnCommit() {
            return doRePaintColOnCommit;
        }

        @Override
        public TethysBaseTableColumn<T, C, R> setRepaintColumnOnCommit(final boolean pRePaint) {
            doRePaintColOnCommit = pRePaint;
            return this;
        }

        @Override
        public C getRePaintColumnId() {
            return theRePaintId;
        }

        @Override
        public TethysBaseTableColumn<T, C, R> setRepaintColumnId(final C pRePaintId) {
            theRePaintId = pRePaintId;
            return this;
        }

        @Override
        public TethysBaseTableColumn<T, C, R> setVisible(final boolean pVisible) {
            /* If we are changing visibility */
            if (pVisible != isVisible) {
                /* Set new visibility */
                isVisible = pVisible;

                /* If we are showing the column */
                if (pVisible) {
                    /* Attach to table at required position */
                    attachToTable();

                    /* else just detach column */
                } else {
                    detachFromTable();
                }
            }
            return this;
        }

        @Override
        public TethysBaseTableColumn<T, C, R> setEditable(final boolean pEditable) {
            isEditable = pEditable;
            return this;
        }

        /**
         * Attach to table.
         */
        protected abstract void attachToTable();

        /**
         * Detach from table.
         */
        protected abstract void detachFromTable();

        /**
         * Count previous visible items.
         * @return the count
         */
        protected int countPreviousVisibleSiblings() {
            /* Determine the previous visible sibling */
            int myCount = 0;
            TethysBaseTableColumn<?, C, R> mySibling = thePrevSibling;
            while (mySibling != null) {
                if (mySibling.isVisible) {
                    myCount++;
                }
                mySibling = mySibling.thePrevSibling;
            }
            return myCount;
        }

        @Override
        public TethysBaseTableColumn<T, C, R> setCellEditable(final Predicate<R> pEditable) {
            isCellEditable = pEditable;
            return this;
        }

        @Override
        public Predicate<R> getCellEditable() {
            return isCellEditable;
        }

        @Override
        public TethysBaseTableColumn<T, C, R> setOnCommit(final TethysOnCellCommit<R, T> pOnCommit) {
            theOnCommit = pOnCommit;
            return this;
        }

        /**
         * process onCommit.
         * @param pRow the row
         * @param pValue the value
         * @throws OceanusException on error
         */
        public void processOnCommit(final R pRow,
                                    final T pValue) throws OceanusException {
            /* If we have an onCommit consumer */
            if (theOnCommit != null) {
                /* call it */
                theOnCommit.commitCell(pRow, pValue);
            }

            /* Call the table onCommit */
            getTable().processOnCommit(pRow);
        }
    }

    /**
     * Cell interface.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableCell<T, C, R> {
        /**
         * Obtain the table.
         * @return the column
         */
        TethysTableManager<C, R> getTable();

        /**
         * Obtain the column.
         * @return the column
         */
        TethysTableColumn<T, C, R> getColumn();

        /**
         * Obtain the control.
         * @return the field
         */
        TethysDataEditField<T> getControl();

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
        TethysFieldType getCellType();

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
    public interface TethysOnCellCommit<R, T> {
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
    public interface TethysOnRowCommit<R> {
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
    public interface TethysOnRowSelect<R> {
        /**
         * CallBack on a rowSelect.
         * @param pRow the row that is being committed
         */
        void selectRow(R pRow);
    }
}
