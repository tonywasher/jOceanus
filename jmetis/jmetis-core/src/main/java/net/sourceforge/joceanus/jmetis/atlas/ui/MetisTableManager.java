/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.atlas.ui;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableCharArrayColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableDateColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableDilutedPriceColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableDilutionColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableIconColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableIntegerColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableListColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableLongColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableMoneyColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTablePriceColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableRateColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableRatioColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableRawDecimalColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableScrollColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableShortColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableStringColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableUnitsColumn;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataDisableItem;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysOnRowCommit;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Table Manager.
 *
 * @param <R> the item type
 */
public abstract class MetisTableManager<R extends MetisFieldTableItem>
        implements TethysComponent {
    /**
     * The underlying table.
     */
    private final TethysTableManager<MetisDataFieldId, R> theTable;

    /**
     * The fieldSet for the item.
     */
    private final MetisFieldSetDef theFieldSet;

    /**
     * Constructor.
     *
     * @param pFactory  the GUI factory
     * @param pFieldSet the fieldSet
     */
    protected MetisTableManager(final TethysGuiFactory pFactory,
                                final MetisFieldSetDef pFieldSet) {
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
    public TethysNode getNode() {
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
     * Set editable.
     *
     * @param pEditable true/false
     */
    public void setEditable(final boolean pEditable) {
        theTable.setEditable(pEditable);
    }

    /**
     * Set the disabled predicate.
     *
     * @param pDisabled the disabled predicate
     */
    public void setDisabled(final Predicate<R> pDisabled) {
        theTable.setDisabled(pDisabled);
    }

    /**
     * Obtain the field for a fieldId.
     *
     * @param pId the field id
     * @return the field
     */
    public MetisFieldDef getFieldForId(final MetisDataFieldId pId) {
        return theFieldSet.getField(pId);
    }

    /**
     * Is the row disabled?
     *
     * @param pRow the row
     * @return true/false
     */
    public boolean isDisabled(final R pRow) {
        return theTable.isDisabled(pRow);
    }

    /**
     * Set the on-commit consumer.
     *
     * @param pOnCommit the consumer
     */
    public void setOnCommit(final TethysOnRowCommit<R> pOnCommit) {
        theTable.setOnCommit(pOnCommit);
    }

    /**
     * do we rePaintRow on commit?
     *
     * @return true/false
     */
    public boolean doRePaintRowOnCommit() {
        return theTable.doRePaintRowOnCommit();
    }

    /**
     * Set repaintRow on Commit.
     *
     * @param pRePaint the flag
     */
    public void setRepaintRowOnCommit(final boolean pRePaint) {
        theTable.setRepaintRowOnCommit(pRePaint);
    }

    /**
     * Set the filter.
     *
     * @param pFilter the filter
     */
    public void setFilter(final Predicate<R> pFilter) {
        theTable.setFilter(pFilter);
    }

    /**
     * Set the comparator.
     *
     * @param pComparator the comparator
     */
    public void setComparator(final Comparator<R> pComparator) {
        theTable.setComparator(pComparator);
    }

    /**
     * Obtain an iterator over the unsorted items.
     *
     * @return the iterator.
     */
    public Iterator<R> itemIterator() {
        return theTable.itemIterator();
    }

    /**
     * Obtain an iterator over the sorted items.
     *
     * @return the iterator.
     */
    public Iterator<R> sortedIterator() {
        return theTable.sortedIterator();
    }

    /**
     * Obtain an iterator over the sorted and filtered items.
     *
     * @return the iterator.
     */
    public Iterator<R> viewIterator() {
        return theTable.viewIterator();
    }

    /**
     * Obtain the column for the id.
     *
     * @param pId the id of the column
     * @return the table column
     */
    public TethysTableColumn<?, MetisDataFieldId, R> getColumn(final MetisDataFieldId pId) {
        return theTable.getColumn(pId);
    }

    /**
     * Repaint the column.
     *
     * @param pId the column id
     */
    public void repaintColumn(final MetisDataFieldId pId) {
        theTable.repaintColumn(pId);
    }

    /**
     * Obtain the table.
     *
     * @return the table
     */
    protected TethysTableManager<MetisDataFieldId, R> getTable() {
        return theTable;
    }

    /**
     * Set the table calculator.
     *
     * @param pCalculator the calculator
     */
    public abstract void setCalculator(MetisTableCalculator<R> pCalculator);

    /**
     * Declare string column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableStringColumn<R> declareStringColumn(MetisDataFieldId pId);

    /**
     * Declare charArray column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableCharArrayColumn<R> declareCharArrayColumn(MetisDataFieldId pId);

    /**
     * Declare short column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableShortColumn<R> declareShortColumn(MetisDataFieldId pId);

    /**
     * Declare integer column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableIntegerColumn<R> declareIntegerColumn(MetisDataFieldId pId);

    /**
     * Declare long column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableLongColumn<R> declareLongColumn(MetisDataFieldId pId);

    /**
     * Declare rawDecimal column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableRawDecimalColumn<R> declareRawDecimalColumn(MetisDataFieldId pId);

    /**
     * Declare money column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableMoneyColumn<R> declareMoneyColumn(MetisDataFieldId pId);

    /**
     * Declare price column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTablePriceColumn<R> declarePriceColumn(MetisDataFieldId pId);

    /**
     * Declare rate column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableRateColumn<R> declareRateColumn(MetisDataFieldId pId);

    /**
     * Declare units column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableUnitsColumn<R> declareUnitsColumn(MetisDataFieldId pId);

    /**
     * Declare dilution column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableDilutionColumn<R> declareDilutionColumn(MetisDataFieldId pId);

    /**
     * Declare ratio column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableRatioColumn<R> declareRatioColumn(MetisDataFieldId pId);

    /**
     * Declare dilutedPrice column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableDilutedPriceColumn<R> declareDilutedPriceColumn(MetisDataFieldId pId);

    /**
     * Declare date column.
     *
     * @param pId the column id
     * @return the column
     */
    public abstract MetisTableDateColumn<R> declareDateColumn(MetisDataFieldId pId);

    /**
     * Declare scroll column.
     *
     * @param <T>    the column type
     * @param pId    the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> MetisTableScrollColumn<T, R> declareScrollColumn(MetisDataFieldId pId,
                                                                         Class<T> pClass);

    /**
     * Declare list column.
     *
     * @param <T> the column type
     * @param pId the column id
     * @return the column
     */
    public abstract <T extends Comparable<T>> MetisTableListColumn<T, R> declareListColumn(MetisDataFieldId pId);

    /**
     * Declare icon column.
     *
     * @param <T>    the column type
     * @param pId    the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> MetisTableIconColumn<T, R> declareIconColumn(MetisDataFieldId pId,
                                                                     Class<T> pClass);

    /**
     * is field in error?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private static boolean isFieldInError(final MetisDataFieldId pField,
                                          final MetisFieldTableItem pItem) {
        if (pItem instanceof MetisFieldVersionedItem) {
            final MetisFieldVersionedItem myVersioned = (MetisFieldVersionedItem) pItem;
            final MetisFieldDef myField = myVersioned.getDataFieldSet().getField(pField);
            return myVersioned.getValidation().hasErrors(myField);
        }
        return false;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private static boolean isFieldChanged(final MetisDataFieldId pField,
                                          final MetisFieldTableItem pItem) {
        if (pItem instanceof MetisFieldVersionedItem) {
            final MetisFieldVersionedItem myVersioned = (MetisFieldVersionedItem) pItem;
            final MetisFieldDef myField = myVersioned.getDataFieldSet().getField(pField);
            return myVersioned.fieldChanged(myField).isDifferent();
        }
        return false;
    }

    /**
     * is item disabled?
     *
     * @param pItem the item
     * @return true/false
     */
    private static boolean isItemDisabled(final MetisFieldTableItem pItem) {
        if (pItem instanceof MetisDataDisableItem) {
            final MetisDataDisableItem myItem = (MetisDataDisableItem) pItem;
            return myItem.isDisabled();
        }
        return false;
    }
}
