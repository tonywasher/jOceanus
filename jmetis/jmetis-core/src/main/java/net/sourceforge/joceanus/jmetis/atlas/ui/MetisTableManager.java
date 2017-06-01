/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
import java.util.function.Predicate;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDisableItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionControl;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisDataVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Table Manager.
 * @param <R> the item type
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class MetisTableManager<R extends MetisDataVersionedItem, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The underlying table.
     */
    private final TethysTableManager<MetisDataField, R, N, I> theTable;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MetisTableManager(final TethysGuiFactory<N, I> pFactory) {
        /* Create the table */
        theTable = pFactory.newTable();

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Handle events */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theTable.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.CELLCREATE, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.CELLPREEDIT, this::handlePreEditEvent);
        myRegistrar.addEventListener(TethysUIEvent.CELLPRECOMMIT, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.CELLFORMAT, theEventManager::cascadeEvent);

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

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set the header predicate.
     * @param pHeader the header predicate
     */
    public void setHeader(final Predicate<R> pHeader) {
        theTable.setHeader(pHeader);
    }

    /**
     * Is the row a header?
     * @param pRow the row
     * @return true/false
     */
    public boolean isHeader(final R pRow) {
        return theTable.isHeader(pRow);
    }

    /**
     * Set the disabled predicate.
     * @param pDisabled the disabled predicate
     */
    public void setDisabled(final Predicate<R> pDisabled) {
        theTable.setDisabled(pDisabled);
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
    public TethysTableColumn<MetisDataField, R, N, I> getColumn(final MetisDataField pId) {
        return theTable.getColumn(pId);
    }

    /**
     * Repaint the column.
     * @param pId the column id
     */
    public void repaintColumn(final MetisDataField pId) {
        theTable.repaintColumn(pId);
    }

    /**
     * Obtain the table.
     * @return the table
     */
    protected TethysTableManager<MetisDataField, R, N, I> getTable() {
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
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareStringColumn(MetisDataField pId);

    /**
     * Declare charArray column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareCharArrayColumn(MetisDataField pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareShortColumn(MetisDataField pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareIntegerColumn(MetisDataField pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareLongColumn(MetisDataField pId);

    /**
     * Declare rawDecimal column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareRawDecimalColumn(MetisDataField pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareMoneyColumn(MetisDataField pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declarePriceColumn(MetisDataField pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareRateColumn(MetisDataField pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareUnitsColumn(MetisDataField pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareDilutionColumn(MetisDataField pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareRatioColumn(MetisDataField pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareDilutedPriceColumn(MetisDataField pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisDataField, R, N, I> declareDateColumn(MetisDataField pId);

    /**
     * Declare scroll column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisDataField, R, N, I> declareScrollColumn(MetisDataField pId,
                                                                                       Class<T> pClass);

    /**
     * Declare list column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisDataField, R, N, I> declareListColumn(MetisDataField pId,
                                                                                     Class<T> pClass);

    /**
     * Declare icon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisDataField, R, N, I> declareIconColumn(MetisDataField pId,
                                                                                     Class<T> pClass);

    /**
     * Declare stateIcon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisDataField, R, N, I> declareStateIconColumn(MetisDataField pId,
                                                                                          Class<T> pClass);

    /**
     * Is the table readOnly?
     * @return true/false
     */
    public abstract boolean isReadOnly();

    /**
     * is field in error?
     * @param pField the field
     * @param pItem the item
     * @return true/false
     */
    private static boolean isFieldInError(final MetisDataField pField,
                                          final MetisDataVersionedItem pItem) {
        if (pItem.isVersioned()) {
            MetisDataVersionControl myControl = pItem.getVersionControl();
            return myControl.getValidation().hasErrors(pField);
        }
        return false;
    }

    /**
     * is field changed?
     * @param pField the field
     * @param pItem the item
     * @return true/false
     */
    private static boolean isFieldChanged(final MetisDataField pField,
                                          final MetisDataVersionedItem pItem) {
        if (pItem.isVersioned()) {
            MetisDataVersionControl myControl = pItem.getVersionControl();
            return myControl.fieldChanged(pField).isDifferent();
        }
        return false;
    }

    /**
     * is item disabled?
     * @param pItem the item
     * @return true/false
     */
    private static boolean isItemDisabled(final MetisDataVersionedItem pItem) {
        if (pItem instanceof MetisDisableItem) {
            MetisDisableItem myItem = (MetisDisableItem) pItem;
            return myItem.isDisabled();
        }
        return false;
    }

    /**
     * handle preEdit events?
     * @param pEvent the event
     */
    private void handlePreEditEvent(final TethysEvent<TethysUIEvent> pEvent) {
        /* Consume readOnly events else cascade them */
        if (isReadOnly()) {
            pEvent.consume();
        } else {
            theEventManager.cascadeEvent(pEvent);
        }
    }
}
