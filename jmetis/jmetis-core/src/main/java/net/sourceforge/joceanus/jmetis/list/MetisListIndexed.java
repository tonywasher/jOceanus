/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmetis.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataIndexedItem;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Indexed List.
 * @param <T> the item type
 */
public class MetisListIndexed<T extends MetisDataIndexedItem>
        implements MetisDataList<T>, MetisFieldItem, TethysEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MetisListIndexed> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListIndexed.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_SIZE, MetisListIndexed::size);
    }

    /**
     * Already Present error.
     */
    private static final String ERROR_PRESENT = "Already present";

    /**
     * The First Id.
     */
    private static final Integer ID_FIRST = 2;

    /**
     * The underlying list.
     */
    private final List<T> theList;

    /**
     * The id Map.
     */
    private final Map<Integer, T> theIdMap;

    /**
     * The nextId.
     */
    private Integer theNextId = ID_FIRST;

    /**
     * The Event Manager.
     */
    private TethysEventManager<MetisListEvent> theEventManager;

    /**
     * The comparator.
     */
    private Comparator<T> theComparator;

    /**
     * Constructor.
     */
    public MetisListIndexed() {
        /* Create the list and map */
        theList = new ArrayList<>();
        theIdMap = new HashMap<>();
    }

    /**
     * Access the event manager.
     * @return the event manager.
     */
    private TethysEventManager<MetisListEvent> getEventManager() {
        /* Access the event manager and create it if it does not exist */
        synchronized (this) {
            if (theEventManager == null) {
                theEventManager = new TethysEventManager<>();
            }
        }
        return theEventManager;
    }

    @Override
    public TethysEventRegistrar<MetisListEvent> getEventRegistrar() {
        return getEventManager().getEventRegistrar();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getDataFieldSet().getName();
    }

    /**
     * Obtain the idMap.
     * @return the map
     */
    protected Map<Integer, T> getIdMap() {
        return theIdMap;
    }

    /**
     * Copy the idMap.
     * @return the copy of the map
     */
    public Map<Integer, T> copyIdMap() {
        return new HashMap<>(theIdMap);
    }

    @Override
    public List<T> getUnderlyingList() {
        return theList;
    }

    /**
     * Set the comparator.
     * @param pComparator the comparator
     */
    public void setComparator(final Comparator<T> pComparator) {
        theComparator = pComparator;
    }

    /**
     * Obtain the comparator.
     * @return the comparator
     */
    public Comparator<T> getComparator() {
        return theComparator;
    }

    /**
     * Obtain the next Id.
     * @return the next Id
     */
    public Integer getNextId() {
        return theNextId;
    }

    /**
     * Allocate the next Id.
     * @return the next allocated Id
     */
    public Integer allocateNextId() {
        final Integer myId = theNextId;
        checkId(myId);
        return myId;
    }

    @Override
    public boolean add(final T pItem) {
        /* Access the id */
        final Integer myId = pItem.getIndexedId();

        /* Check that the id is not currently present */
        if (theIdMap.get(myId) != null) {
            throw new IllegalArgumentException(ERROR_PRESENT);
        }

        /* Check and adjust for id */
        checkId(myId);

        /* Add to the list */
        theIdMap.put(myId, pItem);
        theList.add(pItem);
        return true;
    }

    @Override
    public void add(final int pIndex,
                    final T pItem) {
        /* Access the id */
        final Integer myId = pItem.getIndexedId();

        /* Check that the id is not currently present */
        if (theIdMap.get(myId) != null) {
            throw new IllegalArgumentException(ERROR_PRESENT);
        }

        /* Check and adjust for id */
        checkId(myId);

        /* Add to the list */
        theIdMap.put(myId, pItem);
        theList.add(pIndex, pItem);
    }

    /**
     * Remove item from the list.
     * @param pItem the item to remove
     */
    public void removeFromList(final T pItem) {
        removeById(pItem.getIndexedId());
    }

    /**
     * Remove item from the list.
     * @param pId the id of the item to remove
     */
    public void removeById(final Integer pId) {
        /* If the item is present */
        final T myItem = theIdMap.get(pId);
        if (myItem != null) {
            /* Remove from the list */
            theIdMap.remove(pId);
            theList.remove(myItem);
        }
    }

    @Override
    public boolean remove(final Object pItem) {
        if (pItem instanceof MetisDataIndexedItem) {
            final Integer myId = ((MetisDataIndexedItem) pItem).getIndexedId();
            final T myItem = theIdMap.get(myId);
            if (pItem.equals(myItem)) {
                /* Remove from the list */
                theIdMap.remove(myId);
                theList.remove(myItem);
                return true;
            }
        }
        return false;
    }

    /**
     * Obtain item by Id.
     * @param pId the id
     * @return the item or null
     */
    public T getItemById(final Integer pId) {
        return theIdMap.get(pId);
    }

    /**
     * Determine whether an item is in the list.
     * @param pItem the item
     * @return true/false
     */
    public boolean containsItem(final T pItem) {
        return getItemById(pItem.getIndexedId()) != null;
    }

    /**
     * Is ID present in list?
     * @param pId the id to lookup
     * @return true/false
     */
    public boolean containsId(final Integer pId) {
        return getItemById(pId) != null;
    }

    @Override
    public void clear() {
        theList.clear();
        theIdMap.clear();
        theNextId = ID_FIRST;
    }

    /**
     * Obtain the index of the item.
     * @param pItem the item
     * @return the index or -1 if not present
     */
    public int indexOfItem(final T pItem) {
        return theList.indexOf(pItem);
    }

    /**
     * Obtain the item at the index.
     * @param pIndex the index of the item
     * @return the item
     */
    public T getAtIndex(final int pIndex) {
        return theList.get(pIndex);
    }

    /**
     * Remove the item at the index.
     * @param pIndex the index of the item
     * @return the item that was removed
     */
    public T removeAtIndex(final int pIndex) {
        final T myItem = theList.remove(pIndex);
        theIdMap.remove(myItem.getIndexedId());
        return myItem;
    }

    /**
     * Insert the item at the index.
     * @param pItem the item to insert
     * @param pIndex the index to insert at
     */
    public void insertAtIndex(final T pItem,
                              final int pIndex) {
        /* Access the id */
        final Integer myId = pItem.getIndexedId();

        /* Check that the id is not currently present */
        if (theIdMap.get(myId) != null) {
            throw new IllegalArgumentException(ERROR_PRESENT);
        }

        /* Add to the list */
        theList.add(pIndex, pItem);
        theIdMap.put(myId, pItem);
    }

    @Override
    public Iterator<T> iterator() {
        return new MetisListIterator<>(this);
    }

    /**
     * Obtain a list iterator.
     * @return the iterator
     */
    public ListIterator<T> listIterator() {
        return new MetisListIterator<>(this);
    }

    /**
     * Obtain a reverse list iterator.
     * @return the iterator
     */
    public Iterator<T> reverseIterator() {
        return new MetisListReverseIterator<>(listIterator(size()));
    }

    @Override
    public ListIterator<T> listIterator(final int pIndex) {
        return new MetisListIterator<>(this, pIndex);
    }

    /**
     * Sort the list.
     */
    public void sortList() {
        if (theComparator != null) {
            theList.sort(theComparator);
        }
    }

    /**
     * Check the Id.
     * @param pId the id
     */
    protected void checkId(final Integer pId) {
        /* Check that the id is non-zero and positive */
        if (pId <= 0) {
            throw new IllegalArgumentException("Invalid Id");
        }

        /* If this id is the largest so far */
        if (pId >= theNextId) {
            /* Adjust the nextId */
            theNextId = pId + 1;
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof MetisListIndexed)) {
            return false;
        }

        /* Cast as list */
        final MetisListIndexed<?> myThat = (MetisListIndexed<?>) pThat;

        /* Check list */
        return theList.equals(myThat.theList);
    }

    @Override
    public int hashCode() {
        return theList.hashCode();
    }

    /**
     * Fire event.
     * @param pEvent the event
     */
    protected void fireEvent(final MetisListChange<T> pEvent) {
        /* If the change is non-empty */
        if (MetisListEvent.REFRESH.equals(pEvent.getEventType())
            || !pEvent.isEmpty()) {
            getEventManager().fireEvent(pEvent.getEventType(), pEvent);
        }
    }
}
