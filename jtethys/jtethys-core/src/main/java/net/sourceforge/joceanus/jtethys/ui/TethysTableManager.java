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
     * Is the table editable?
     * @return true/false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Set the edit-ability of the table.
     * @param pEditable true/false
     */
    public void setEditable(final boolean pEditable) {
        isEditable = pEditable;
    }

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
     */
    public void setRepaintRowOnCommit(final boolean pRePaint) {
        doRePaintRowOnCommit = pRePaint;
    }

    /**
     * Set the error predicate.
     * @param pError the error predicate
     */
    public void setError(final BiPredicate<C, R> pError) {
        theError = pError;
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
     */
    public void setChanged(final BiPredicate<C, R> pChanged) {
        theChanged = pChanged;
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
     */
    public void setDisabled(final Predicate<R> pDisabled) {
        theDisabled = pDisabled;
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
     */
    public void setFilter(final Predicate<R> pFilter) {
        theFilter = pFilter;
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
     */
    public void setComparator(final Comparator<R> pComparator) {
        theComparator = pComparator;
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
     */
    public void setOnCommit(final TethysOnRowCommit<R> pOnCommit) {
        theOnCommit = pOnCommit;
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
     */
    public void setOnSelect(final TethysOnRowSelect<R> pOnSelect) {
        theOnSelect = pOnSelect;
    }

    /**
     * process onCommit.
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
     * Set the on-commitError consumer.
     * @param pOnCommitError the consumer
     */
    public void setOnCommitError(final Consumer<OceanusException> pOnCommitError) {
        theOnCommitError = pOnCommitError;
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
     * @return the column
     */
    public abstract <T extends Comparable<T>> TethysTableListColumn<T, C, R> declareListColumn(C pId);

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
         */
        void setName(String pName);

        /**
         * Set the column width.
         * @param pWidth the width
         */
        void setColumnWidth(int pWidth);

        /**
         * Is the column visible?
         * @return true/false
         */
        boolean isVisible();

        /**
         * Set the visibility of the column.
         * @param pVisible true/false
         */
        void setVisible(boolean pVisible);

        /**
         * Is the column editable?
         * @return true/false
         */
        boolean isEditable();

        /**
         * Set the edit-ability of the column.
         * @param pEditable true/false
         */
        void setEditable(boolean pEditable);

        /**
         * Set the cell-editable tester.
         * @param pEditable the editable tester
         */
        void setCellEditable(Predicate<R> pEditable);

        /**
         * Get the cell-editable tester.
         * @return the current tester
         */
        Predicate<R> getCellEditable();

        /**
         * Set the on-commit consumer.
         * @param pOnCommit the consumer
         */
        void setOnCommit(TethysOnColumnCommit<R, T> pOnCommit);

        /**
         * do we rePaintColumn on commit?
         * @return true/false
         */
        boolean doRePaintColumnOnCommit();

        /**
         * Set repaintColumn on Commit.
         * @param pRePaint the flag
         */
        void setRepaintColumnOnCommit(boolean pRePaint);

        /**
         * get the column id which forces a rePaint.
         * @return the column id
         */
        C getRePaintColumnId();

        /**
         * Set repaintColumnId.
         * @param pRePaintId the repaint id
         */
        void setRepaintColumnId(C pRePaintId);
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
         */
        void setValidator(BiFunction<T, R, String> pValidator);
    }

    /**
     * String Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableStringColumn<C, R>
            extends TethysTableValidatedColumn<String, R>,
            TethysTableColumn<String, C, R> {
    }

    /**
     * CharArray Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableCharArrayColumn<C, R>
            extends TethysTableValidatedColumn<char[], R>,
            TethysTableColumn<char[], C, R> {
    }

    /**
     * Short Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableShortColumn<C, R>
            extends TethysTableValidatedColumn<Short, R>,
            TethysTableColumn<Short, C, R> {
    }

    /**
     * Integer Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableIntegerColumn<C, R>
            extends TethysTableValidatedColumn<Integer, R>,
            TethysTableColumn<Integer, C, R> {
    }

    /**
     * Long Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableLongColumn<C, R>
            extends TethysTableValidatedColumn<Long, R>,
            TethysTableColumn<Long, C, R> {
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
         */
        void setNumDecimals(ToIntFunction<R> pSupplier);
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
         */
        void setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * RawDecimal Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableRawDecimalColumn<C, R>
            extends TethysTableDecimalColumn<R>,
            TethysTableColumn<TethysDecimal, C, R> {
    }

    /**
     * Money Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableMoneyColumn<C, R>
            extends TethysTableCurrencyColumn<TethysMoney, R>,
            TethysTableColumn<TethysMoney, C, R> {
    }

    /**
     * Price Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTablePriceColumn<C, R>
            extends TethysTableCurrencyColumn<TethysPrice, R>,
            TethysTableColumn<TethysPrice, C, R> {
    }

    /**
     * Units Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableUnitsColumn<C, R>
            extends TethysTableValidatedColumn<TethysUnits, R>,
            TethysTableColumn<TethysUnits, C, R> {
    }

    /**
     * Rate Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableRateColumn<C, R>
            extends TethysTableValidatedColumn<TethysRate, R>,
            TethysTableColumn<TethysRate, C, R> {
    }

    /**
     * Dilution Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableDilutionColumn<C, R>
            extends TethysTableValidatedColumn<TethysDilution, R>,
            TethysTableColumn<TethysDilution, C, R> {
    }

    /**
     * Ratio Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableRatioColumn<C, R>
            extends TethysTableValidatedColumn<TethysRatio, R>,
            TethysTableColumn<TethysRatio, C, R> {
    }

    /**
     * DilutedPrice Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableDilutedPriceColumn<C, R>
            extends TethysTableCurrencyColumn<TethysDilutedPrice, R>,
            TethysTableColumn<TethysDilutedPrice, C, R> {
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
         */
        void setIconMapSet(Function<R, TethysIconMapSet<T>> pSupplier);
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
    }

    /**
     * DateTableColumn.
     * @param <R> the row type
     */
    public interface TethysTableDateConfig<R> {
        /**
         * Set the Date configurator.
         * @param pConfigurator the configurator
         */
        void setDateConfigurator(BiConsumer<R, TethysDateConfig> pConfigurator);
    }

    /**
     * DateTableColumn.
     * @param <C> the column identity
     * @param <R> the row type
     */
    public interface TethysTableDateColumn<C, R>
            extends TethysTableDateConfig<R>,
            TethysTableColumn<TethysDate, C, R> {
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
         */
        void setMenuConfigurator(BiConsumer<R, TethysScrollMenu<T>> pConfigurator);
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
         */
        void setSelectables(Function<R, Iterator<T>> pSelectables);
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
        private TethysEventManager<TethysUIEvent> theEventManager;

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
        private TethysOnColumnCommit<R, T> theOnCommit;

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
        public void setName(final String pName) {
            theName = pName;
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
        public void setRepaintColumnOnCommit(final boolean pRePaint) {
            doRePaintColOnCommit = pRePaint;
        }

        @Override
        public C getRePaintColumnId() {
            return theRePaintId;
        }

        @Override
        public void setRepaintColumnId(final C pRePaintId) {
            theRePaintId = pRePaintId;
        }

        @Override
        public void setVisible(final boolean pVisible) {
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
        }

        @Override
        public void setEditable(final boolean pEditable) {
            isEditable = pEditable;
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
        public void setCellEditable(final Predicate<R> pEditable) {
            isCellEditable = pEditable;
        }

        @Override
        public Predicate<R> getCellEditable() {
            return isCellEditable;
        }

        @Override
        public void setOnCommit(final TethysOnColumnCommit<R, T> pOnCommit) {
            theOnCommit = pOnCommit;
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
                theOnCommit.commitColumn(pRow, pValue);
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
     * OnColumn commit callback.
     * @param <R> the row type
     * @param <T> the value type
     */
    @FunctionalInterface
    public interface TethysOnColumnCommit<R, T> {
        /**
         * CallBack on a columnCommit.
         * @param pRow the row that is being committed
         * @param pValue the new value
         * @throws OceanusException on error
         */
        void commitColumn(R pRow, T pValue) throws OceanusException;
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
