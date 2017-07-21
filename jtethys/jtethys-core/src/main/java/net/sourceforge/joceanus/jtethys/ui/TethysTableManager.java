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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;

/**
 * Tethys Table Manager.
 * @param <C> the column identity
 * @param <R> the row type
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysTableManager<C, R, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The map of columns.
     */
    private final Map<C, TethysTableColumn<?, C, R, N, I>> theColumnMap;

    /**
     * The last child item.
     */
    private TethysBaseTableColumn<?, C, R, N, I> theLastChild;

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
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysTableManager(final TethysGuiFactory<N, I> pFactory) {
        theEventManager = new TethysEventManager<>();
        theId = pFactory.getNextId();
        theColumnMap = new HashMap<>();
        isEditable = true;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Is the column editable?
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
     * Cascade event.
     * @param pEvent the event
     */
    protected void cascadeEvent(final TethysEvent<TethysUIEvent> pEvent) {
        theEventManager.cascadeEvent(pEvent);
    }

    /**
     * Obtain the column for the id.
     * @param pId the id of the column
     * @return the table column
     */
    public TethysTableColumn<?, C, R, N, I> getColumn(final C pId) {
        return theColumnMap.get(pId);
    }

    /**
     * Repaint the column.
     * @param pId the column id
     */
    public void repaintColumn(final C pId) {
        TethysTableColumn<?, C, R, N, I> myCol = theColumnMap.get(pId);
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
    private void registerColumn(final TethysTableColumn<?, C, R, N, I> pColumn) {
        theColumnMap.put(pColumn.getId(), pColumn);
    }

    /**
     * Declare string column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<String, C, R, N, I> declareStringColumn(C pId);

    /**
     * Declare charArray column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<char[], C, R, N, I> declareCharArrayColumn(C pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<Short, C, R, N, I> declareShortColumn(C pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<Integer, C, R, N, I> declareIntegerColumn(C pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<Long, C, R, N, I> declareLongColumn(C pId);

    /**
     * Declare rawDecimal column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableRawDecimalColumn<C, R, N, I> declareRawDecimalColumn(C pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableCurrencyColumn<TethysMoney, C, R, N, I> declareMoneyColumn(C pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableCurrencyColumn<TethysPrice, C, R, N, I> declarePriceColumn(C pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<TethysRate, C, R, N, I> declareRateColumn(C pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<TethysUnits, C, R, N, I> declareUnitsColumn(C pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<TethysDilution, C, R, N, I> declareDilutionColumn(C pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<TethysRatio, C, R, N, I> declareRatioColumn(C pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableCurrencyColumn<TethysDilutedPrice, C, R, N, I> declareDilutedPriceColumn(C pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<TethysDate, C, R, N, I> declareDateColumn(C pId);

    /**
     * Declare scroll column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<T, C, R, N, I> declareScrollColumn(C pId,
                                                                             Class<T> pClass);

    /**
     * Declare list column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<TethysItemList<T>, C, R, N, I> declareListColumn(C pId,
                                                                                           Class<T> pClass);

    /**
     * Declare icon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableIconColumn<T, C, R, N, I> declareIconColumn(C pId,
                                                                               Class<T> pClass);

    /**
     * Column Definition.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public interface TethysTableColumn<T, C, R, N, I>
            extends TethysEventProvider<TethysUIEvent> {
        /**
         * Obtain the table manager.
         * @return the table manager
         */
        TethysTableManager<C, R, N, I> getTable();

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
         * Set the validity tester.
         * @param pValidator the validator
         */
        void setValidator(BiFunction<T, R, String> pValidator);

        /**
         * Get the validity tester.
         * @return the current tester
         */
        BiFunction<T, R, String> getValidator();
    }

    /**
     * RawDecimalTableColumn.
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    public interface TethysTableRawDecimalColumn<C, R, N, I>
            extends TethysTableColumn<TethysDecimal, C, R, N, I> {
        /**
         * Set the Number of decimals supplier.
         * @param pSupplier the supplier
         */
        void setNumDecimals(Function<R, Integer> pSupplier);
    }

    /**
     * CurrencyTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    public interface TethysTableCurrencyColumn<T extends TethysMoney, C, R, N, I>
            extends TethysTableColumn<T, C, R, N, I> {
        /**
         * Set the Deemed Currency supplier.
         * @param pSupplier the supplier
         */
        void setDeemedCurrency(Function<R, Currency> pSupplier);
    }

    /**
     * IconTableColumn.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    public interface TethysTableIconColumn<T, C, R, N, I>
            extends TethysTableColumn<T, C, R, N, I> {
        /**
         * Set the IconMapSet supplier.
         * @param pSupplier the supplier
         */
        void setIconMapSet(Function<R, TethysIconMapSet<T>> pSupplier);
    }

    /**
     * Column Definition.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public abstract static class TethysBaseTableColumn<T, C, R, N, I>
            implements TethysTableColumn<T, C, R, N, I> {
        /**
         * The table.
         */
        private final TethysTableManager<C, R, N, I> theTable;

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
        private TethysBaseTableColumn<?, C, R, N, I> thePrevSibling;

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
         * The validator.
         */
        private BiFunction<T, R, String> theValidator;

        /**
         * Constructor.
         * @param pTable the containing table
         * @param pId the id of the column
         * @param pType the type of the column
         */
        protected TethysBaseTableColumn(final TethysTableManager<C, R, N, I> pTable,
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
            TethysBaseTableColumn<?, C, R, N, I> myChild = theTable.theLastChild;
            if (myChild != null) {
                /* Link to last child */
                thePrevSibling = myChild;
            }

            /* Add as last child of pane */
            theTable.theLastChild = this;

            /* Register the column */
            theTable.registerColumn(this);

            /* Initialise editable and validator */
            isCellEditable = p -> true;
            theValidator = (t, r) -> null;
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        @Override
        public TethysTableManager<C, R, N, I> getTable() {
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
            TethysBaseTableColumn<?, C, R, N, I> mySibling = thePrevSibling;
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
        public void setValidator(final BiFunction<T, R, String> pValidator) {
            theValidator = pValidator;
        }

        @Override
        public BiFunction<T, R, String> getValidator() {
            return theValidator;
        }
    }

    /**
     * Cell interface.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public interface TethysTableCell<T, C, R, N, I> {
        /**
         * Obtain the table.
         * @return the column
         */
        TethysTableManager<C, R, N, I> getTable();

        /**
         * Obtain the column.
         * @return the column
         */
        TethysTableColumn<T, C, R, N, I> getColumn();

        /**
         * Obtain the control.
         * @return the field
         */
        TethysDataEditField<T, N, I> getControl();

        /**
         * Obtain the new value.
         * @return the new value
         */
        T getNewValue();

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
}
