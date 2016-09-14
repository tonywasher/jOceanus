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
package net.sourceforge.joceanus.jmetis.ui;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newlist.MetisEditList;
import net.sourceforge.joceanus.jmetis.newlist.MetisVersionedItem;
import net.sourceforge.joceanus.jmetis.newlist.MetisVersionedList;
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
 * @param <B> the base type
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class MetisTableManager<R extends B, B extends MetisVersionedItem, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The underlying table.
     */
    private final TethysTableManager<MetisField, R, N, I> theTable;

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
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theEventManager.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.CELLCREATE, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.CELLPREEDIT, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.CELLPRECOMMIT, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.CELLFORMAT, this::formatCell);
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
     * Set the table items.
     * @param pItems the items
     */
    public abstract void setItems(final MetisVersionedList<B> pItems);

    /**
     * Obtain the table items.
     * @return the items
     */
    public abstract MetisEditList<R, B> getItems();

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
    public TethysTableColumn<MetisField, R, N, I> getColumn(final MetisField pId) {
        return theTable.getColumn(pId);
    }

    /**
     * Repaint the column.
     * @param pId the column id
     */
    public void repaintColumn(final MetisField pId) {
        theTable.repaintColumn(pId);
    }

    /**
     * Obtain the table.
     * @return the table
     */
    protected TethysTableManager<MetisField, R, N, I> getTable() {
        return theTable;
    }

    /**
     * Declare string column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareStringColumn(final MetisField pId);

    /**
     * Declare charArray column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareCharArrayColumn(final MetisField pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareShortColumn(final MetisField pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareIntegerColumn(final MetisField pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareLongColumn(final MetisField pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareMoneyColumn(final MetisField pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declarePriceColumn(final MetisField pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareRateColumn(final MetisField pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareUnitsColumn(final MetisField pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareDilutionColumn(final MetisField pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareRatioColumn(final MetisField pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareDilutedPriceColumn(final MetisField pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<MetisField, R, N, I> declareDateColumn(final MetisField pId);

    /**
     * Declare scroll column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisField, R, N, I> declareScrollColumn(final MetisField pId,
                                                                                   final Class<T> pClass);

    /**
     * Declare list column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisField, R, N, I> declareListColumn(final MetisField pId,
                                                                                 final Class<T> pClass);

    /**
     * Declare icon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisField, R, N, I> declareIconColumn(final MetisField pId,
                                                                                 final Class<T> pClass);

    /**
     * Declare stateIcon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<MetisField, R, N, I> declareStateIconColumn(final MetisField pId,
                                                                                      final Class<T> pClass);

    /**
     * Format the cell.
     * @param pEvent the event
     */
    private void formatCell(final TethysEvent<TethysUIEvent> pEvent) {
        /* TODO */
    }
}