/*******************************************************************************
 * Tethys: GUI Utilities
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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableCharArrayColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableDateColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableIconColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableIntegerColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableListColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableLongColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableMoneyColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITablePriceColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableRateColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableRatioColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableRawDecimalColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableScrollColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableShortColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableStringColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn.TethysUITableUnitsColumn;

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
     * Select a row.
     * @param pItem the row to select
     */
    void selectRow(R pItem);

    /**
     * Scroll the selected item (if any) to view.
     */
    void scrollSelectedToView();

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
     * Set the table items.
     *
     * @param pItems the items
     */
    void setItems(List<R> pItems);

    /**
     * fire TableDataChanged.
     */
    void fireTableDataChanged();

    /**
     * Obtain an iterator over the unsorted items.
     * @return the iterator.
     */
    Iterator<R> itemIterator();

    /**
     * Obtain an iterator over the sorted and filtered items.
     * @return the iterator.
     */
    Iterator<R> viewIterator();

    /**
     * Obtain an iterator over the column ids.
     * @return the iterator.
     */
    Iterator<C> columnIterator();

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
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    TethysUITableRatioColumn<C, R> declareRatioColumn(C pId);

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
    <T extends Comparable<? super T>> TethysUITableListColumn<T, C, R> declareListColumn(C pId,
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
