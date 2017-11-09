/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDisableItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosTableItem;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosVersionedItem;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableCurrencyColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableValidatedColumn;

/**
 * Table Manager.
 * @param <R> the item type
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class MetisTableManager<R extends MetisDataEosTableItem, N, I>
        implements TethysNode<N> {
    /**
     * The underlying table.
     */
    private final TethysTableManager<MetisFieldId, R, N, I> theTable;

    /**
     * The fieldSet for the item.
     */
    private final MetisDataEosFieldSet<R> theFieldSet;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldSet the fieldSet
     */
    protected MetisTableManager(final TethysGuiFactory<N, I> pFactory,
                                final MetisDataEosFieldSet<R> pFieldSet) {
        /* Store parameters */
        theFieldSet = pFieldSet;

        /* Create the table */
        theTable = pFactory.newTable();

        /* Set the changed, disabled and error tests */
        theTable.setChanged(MetisTableManager::isFieldChanged);
        theTable.setDisabled(MetisTableManager::isItemDisabled);
        theTable.setError(MetisTableManager::isFieldInError);
    }

    @Override
    public Integer getId() {
        return theTable.getId();
    }

    @Override
    public N getNode() {
        return theTable.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theTable.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theTable.setEnabled(pEnabled);
    }

    /**
     * Set the disabled predicate.
     * @param pDisabled the disabled predicate
     */
    public void setDisabled(final Predicate<R> pDisabled) {
        theTable.setDisabled(pDisabled);
    }

    /**
     * Obtain the field for a fieldId.
     * @param pId the field id
     * @return the field
     */
    public MetisDataEosFieldDef getFieldForId(final MetisFieldId pId) {
        return theFieldSet.getField(pId);
    }

    /**
     * Is the row disabled?
     * @param pRow the row
     * @return true/false
     */
    public boolean isDisabled(final R pRow) {
        return theTable.isDisabled(pRow);
    }

    /**
     * Set the on-commit consumer.
     * @param pOnCommit the consumer
     */
    public void setOnCommit(final Consumer<R> pOnCommit) {
        theTable.setOnCommit(pOnCommit);
    }

    /**
     * do we rePaintRow on commit?
     * @return true/false
     */
    public boolean doRePaintRowOnCommit() {
        return theTable.doRePaintRowOnCommit();
    }

    /**
     * Set repaintRow on Commit.
     * @param pRePaint the flag
     */
    public void setRepaintRowOnCommit(final boolean pRePaint) {
        theTable.setRepaintRowOnCommit(pRePaint);
    }

    /**
     * Set the filter.
     * @param pFilter the filter
     */
    public void setFilter(final Predicate<R> pFilter) {
        theTable.setFilter(pFilter);
    }

    /**
     * Set the comparator.
     * @param pComparator the comparator
     */
    public void setComparator(final Comparator<R> pComparator) {
        theTable.setComparator(pComparator);
    }

    /**
     * Obtain an iterator over the unsorted items.
     * @return the iterator.
     */
    public Iterator<R> itemIterator() {
        return theTable.itemIterator();
    }

    /**
     * Obtain an iterator over the sorted items.
     * @return the iterator.
     */
    public Iterator<R> sortedIterator() {
        return theTable.sortedIterator();
    }

    /**
     * Obtain an iterator over the sorted and filtered items.
     * @return the iterator.
     */
    public Iterator<R> viewIterator() {
        return theTable.viewIterator();
    }

    /**
     * Obtain the column for the id.
     * @param pId the id of the column
     * @return the table column
     */
    public TethysTableColumn<?, MetisFieldId, R, N, I> getColumn(final MetisFieldId pId) {
        return theTable.getColumn(pId);
    }

    /**
     * Repaint the column.
     * @param pId the column id
     */
    public void repaintColumn(final MetisFieldId pId) {
        theTable.repaintColumn(pId);
    }

    /**
     * Obtain the table.
     * @return the table
     */
    protected TethysTableManager<MetisFieldId, R, N, I> getTable() {
        return theTable;
    }

    /**
     * Set the table calculator.
     * @param pCalculator the calculator
     */
    public abstract void setCalculator(MetisTableCalculator<R> pCalculator);

    /**
     * Declare string column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<String, MetisFieldId, R, N, I> declareStringColumn(MetisFieldId pId);

    /**
     * Declare charArray column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<char[], MetisFieldId, R, N, I> declareCharArrayColumn(MetisFieldId pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<Short, MetisFieldId, R, N, I> declareShortColumn(MetisFieldId pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<Integer, MetisFieldId, R, N, I> declareIntegerColumn(MetisFieldId pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<Long, MetisFieldId, R, N, I> declareLongColumn(MetisFieldId pId);

    /**
     * Declare rawDecimal column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableRawDecimalColumn<MetisFieldId, R, N, I> declareRawDecimalColumn(MetisFieldId pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableCurrencyColumn<TethysMoney, MetisFieldId, R, N, I> declareMoneyColumn(MetisFieldId pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableCurrencyColumn<TethysPrice, MetisFieldId, R, N, I> declarePriceColumn(MetisFieldId pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<TethysRate, MetisFieldId, R, N, I> declareRateColumn(MetisFieldId pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<TethysUnits, MetisFieldId, R, N, I> declareUnitsColumn(MetisFieldId pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<TethysDilution, MetisFieldId, R, N, I> declareDilutionColumn(MetisFieldId pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableValidatedColumn<TethysRatio, MetisFieldId, R, N, I> declareRatioColumn(MetisFieldId pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableCurrencyColumn<TethysDilutedPrice, MetisFieldId, R, N, I> declareDilutedPriceColumn(MetisFieldId pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableDateColumn<MetisFieldId, R, N, I> declareDateColumn(MetisFieldId pId);

    /**
     * Declare scroll column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableScrollColumn<T, MetisFieldId, R, N, I> declareScrollColumn(MetisFieldId pId,
                                                                                              Class<T> pClass);

    /**
     * Declare list column.
     * @param <T> the column type
     * @param pId the column id
     * @return the column
     */
    public abstract <T extends Comparable<T>> TethysTableListColumn<T, MetisFieldId, R, N, I> declareListColumn(MetisFieldId pId);

    /**
     * Declare icon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableIconColumn<T, MetisFieldId, R, N, I> declareIconColumn(MetisFieldId pId,
                                                                                          Class<T> pClass);

    /**
     * is field in error?
     * @param pField the field
     * @param pItem the item
     * @return true/false
     */
    private static boolean isFieldInError(final MetisFieldId pField,
                                          final MetisDataEosTableItem pItem) {
        if (pItem instanceof MetisDataEosVersionedItem) {
            final MetisDataEosVersionedItem myVersioned = (MetisDataEosVersionedItem) pItem;
            MetisDataEosFieldDef myField = myVersioned.getDataFieldSet().getField(pField);
            return myVersioned.getValidation().hasErrors(myField);
        }
        return false;
    }

    /**
     * is field changed?
     * @param pField the field
     * @param pItem the item
     * @return true/false
     */
    private static boolean isFieldChanged(final MetisFieldId pField,
                                          final MetisDataEosTableItem pItem) {
        if (pItem instanceof MetisDataEosVersionedItem) {
            final MetisDataEosVersionedItem myVersioned = (MetisDataEosVersionedItem) pItem;
            MetisDataEosFieldDef myField = myVersioned.getDataFieldSet().getField(pField);
            return myVersioned.fieldChanged(myField).isDifferent();
        }
        return false;
    }

    /**
     * is item disabled?
     * @param pItem the item
     * @return true/false
     */
    private static boolean isItemDisabled(final MetisDataEosTableItem pItem) {
        if (pItem instanceof MetisDisableItem) {
            final MetisDisableItem myItem = (MetisDisableItem) pItem;
            return myItem.isDisabled();
        }
        return false;
    }
}
