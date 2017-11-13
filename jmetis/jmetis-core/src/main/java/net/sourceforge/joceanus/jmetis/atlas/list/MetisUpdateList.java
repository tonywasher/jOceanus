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
package net.sourceforge.joceanus.jmetis.atlas.list;

import java.util.Comparator;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosVersionValues;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Update List.
 * @param <T> the item type
 */
public final class MetisUpdateList<T extends MetisDataEosVersionedItem>
        extends MetisVersionedList<T> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisDataEosFieldSet<MetisUpdateList> FIELD_DEFS = MetisDataEosFieldSet.newFieldSet(MetisUpdateList.class);

    /**
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE, MetisUpdateList::getBaseList);
    }

    /**
     * The base list.
     */
    private final MetisBaseList<T> theBase;

    /**
     * The base comparator.
     */
    private final Comparator<T> theComparator;

    /**
     * Constructor.
     * @param pBase the base list
     */
    protected MetisUpdateList(final MetisBaseList<T> pBase) {
        /* Initialise underlying class */
        super(pBase.getClazz());

        /* Copy the comparator from the base list */
        theComparator = pBase.getComparator();
        setComparator(this::doCompare);

        /* Store source and initialise the update list */
        theBase = pBase;
        doDeriveUpdates();

        /* Listen for updates */
        final TethysEventRegistrar<MetisListEvent> myRegistrar = theBase.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> doDeriveUpdates());
        myRegistrar.addEventListener(MetisListEvent.REBASE, e -> doDeriveUpdates());
        myRegistrar.addEventListener(MetisListEvent.COMMIT, this::handleChangesInBase);
        myRegistrar.addEventListener(MetisListEvent.REWIND, this::handleChangesInBase);
    }

    @Override
    public MetisDataEosFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the base list.
     * @return the list
     */
    private MetisBaseList<T> getBaseList() {
        return theBase;
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
        if (!(pThat instanceof MetisUpdateList)) {
            return false;
        }

        /* Cast as list */
        final MetisUpdateList<?> myThat = (MetisUpdateList<?>) pThat;

        /* Check local fields */
        if (!theBase.equals(myThat.theBase)) {
            return false;
        }

        /* Pass call onwards */
        return super.equals(pThat);
    }

    @Override
    public int hashCode() {
        int myHash = super.hashCode();
        myHash *= HASH_PRIME;
        return myHash + theBase.hashCode();
    }

    /**
     * Derive update items.
     */
    private void doDeriveUpdates() {
        /* Clear the list */
        clear();

        /* Loop through the base list */
        final Iterator<T> myIterator = theBase.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* process update item */
            processUpdate(myCurr);
        }

        /* Make sure that the version is correct */
        final boolean isEmpty = isEmpty();
        setVersion(isEmpty
                           ? 0
                           : 1);

        /* Sort the list if necessary */
        if (!isEmpty) {
            sortList();
        }
    }

    /**
     * process update item.
     * @param pBase the base update
     */
    private void processUpdate(final T pBase) {
        /* Switch on the state */
        switch (pBase.getState()) {
            case NEW:
                handleNewUpdate(pBase);
                break;
            case CHANGED:
                handleChangedUpdate(pBase);
                break;
            case DELETED:
                handleDeletedUpdate(pBase);
                break;
            case DELNEW:
                handleDelNewUpdate(pBase);
                break;
            default:
                break;
        }
    }

    /**
     * handle new update.
     * @param pItem the item
     */
    private void handleNewUpdate(final T pItem) {
        final T myItem = newDiffAddedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle changed update.
     * @param pItem the item
     */
    private void handleChangedUpdate(final T pItem) {
        final T myItem = newUpdateChangedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle deleted update.
     * @param pItem the item
     */
    private void handleDeletedUpdate(final T pItem) {
        final T myItem = newDiffDeletedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle delNew update.
     * @param pItem the item
     */
    private void handleDelNewUpdate(final T pItem) {
        final T myItem = newUpdateDelNewItem(pItem);
        addToList(myItem);
    }

    /**
     * Commit update batch.
     * @param pPhase the update phase
     * @param pNumItems the number of items to commit
     * @return the number of commit items remaining
     */
    public int commitUpdateBatch(final MetisUpdatePhase pPhase,
                                 final int pNumItems) {
        /* The item count */
        int myNumItems = pNumItems;

        /* Create a new Change Detail */
        final MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.UPDATE);

        /* Loop through the list */
        final Iterator<T> myIterator = MetisUpdatePhase.DELETE.equals(pPhase)
                                                                              ? reverseIterator()
                                                                              : iterator();
        while (myIterator.hasNext()
               && myNumItems > 0) {
            final T myCurr = myIterator.next();

            /* Obtain the state */
            final MetisDataState myState = myCurr.getState();

            /* If this is to be handled in this phase */
            if (pPhase.checkStateInPhase(myState)) {
                /* Access further details */
                final MetisDataEosVersionValues myValues = myCurr.getValueSet();
                final int myId = myCurr.getIndexedId();
                final T myBase = theBase.getItemById(myId);

                /* Commit the underlying item */
                if (myValues.isDeletion()) {
                    theBase.removeFromList(myBase);
                    myChange.registerDeleted(myBase);
                } else {
                    myBase.clearHistory();
                }

                /* Adjust update list */
                myIterator.remove();
                if (!MetisDataState.DELNEW.equals(myState)) {
                    myNumItems--;
                }
            }
        }

        /* Make sure that the version is correct */
        setVersion(isEmpty()
                             ? 0
                             : 1);

        /* Fire the event */
        theBase.fireEvent(myChange);

        /* Return the new count */
        return myNumItems;
    }

    /**
     * Derive update items as a result of reWind/Commit in the base list.
     * @param pEvent the event
     */
    private void handleChangesInBase(final TethysEvent<MetisListEvent> pEvent) {
        /* Access the change details */
        @SuppressWarnings("unchecked")
        final MetisListChange<T> myChange = (MetisListChange<T>) pEvent.getDetails(MetisListChange.class);

        /* Process added entries (can only happen from a commit) */
        boolean doSort = false;
        Iterator<T> myIterator = myChange.addedIterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();
            handleNewUpdate(myCurr);
            doSort = true;
        }

        /* Obtain changed entries */
        myIterator = myChange.changedIterator();
        while (myIterator.hasNext()) {
            final T myBase = myIterator.next();
            final int myId = myBase.getIndexedId();
            final T myCurr = getItemById(myId);
            doSort = true;

            /* If we do not currently have the item */
            if (myCurr == null) {
                /* Handle as newly discovered item */
                processUpdate(myBase);
            } else {
                /* Process as a changed update */
                processChangedUpdate(myCurr, myBase);
            }
        }

        /* Process deleted entries (can only happen from a rewind of DelNew) */
        myIterator = myChange.deletedIterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();
            final Integer myId = myCurr.getIndexedId();
            removeById(myId);
        }

        /* Make sure that the version is correct */
        setVersion(isEmpty()
                             ? 0
                             : 1);

        /* Sort the list if necessary */
        if (doSort) {
            sortList();
        }
    }

    /**
     * process changed update item.
     * @param pCurr the current update
     * @param pBase the base update
     */
    private void processChangedUpdate(final T pCurr,
                                      final T pBase) {
        /* Obtain the valueSet history */
        final MetisDataState myState = pBase.getState();

        /* If we are now clean */
        if (MetisDataState.CLEAN.equals(myState)) {
            /* Delete the entry */
            removeFromList(pCurr);
        } else {
            /* Replace the current values */
            final MetisDataEosVersionValues myBase = pBase.getValueSet();
            final MetisDataEosVersionValues mySet = pCurr.getValueSet();
            mySet.copyFrom(myBase);
        }
    }

    /**
     * Create a New "changed" item for an update list.
     * @param pCurr the current item
     * @return the new item
     */
    private T newUpdateChangedItem(final T pCurr) {
        /* Obtain a new item */
        final T myNew = newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisDataEosVersionValues mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the original value set as the base value */
        MetisDataEosVersionValues myBase = pCurr.getOriginalValues();
        myBase = myBase.cloneIt();

        /* Record as the history of the item */
        myNew.setValues(mySet);
        myNew.setHistory(myBase);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "delNew" item for an update list.
     * @param pBase the base item
     * @return the new item
     */
    private T newUpdateDelNewItem(final T pBase) {
        /* Obtain a new item */
        final T myNew = newListItem(pBase.getIndexedId());

        /* Obtain a deleted values set as the current value */
        final MetisDataEosVersionValues myBaseSet = pBase.getValueSet();
        final MetisDataEosVersionValues mySet = myBaseSet.cloneIt();
        mySet.setDeletion(true);
        mySet.setVersion(1);

        /* Record as the history of the item */
        myNew.setValues(mySet);

        /* Return the new item */
        return myNew;
    }

    /**
     * the comparator.
     * @param pFirst the first item to compare
     * @param pSecond the second item to compare
     * @return (-1,0,1) as to whether first is less than, equal to or greater than second
     */
    protected int doCompare(final T pFirst,
                            final T pSecond) {
        /* Compare on update phase */
        final MetisUpdatePhase myFirst = MetisUpdatePhase.getPhaseForItem(pFirst);
        final MetisUpdatePhase mySecond = MetisUpdatePhase.getPhaseForItem(pSecond);
        final int iCompare = myFirst.compareTo(mySecond);
        if (iCompare != 0) {
            return iCompare;
        }

        /* Sort on comparator */
        return theComparator == null
                                     ? 0
                                     : theComparator.compare(pFirst, pSecond);
    }

    /**
     * Update phase.
     */
    public enum MetisUpdatePhase {
        /**
         * Insert.
         */
        INSERT,

        /**
         * Update.
         */
        UPDATE,

        /**
         * Delete.
         */
        DELETE,

        /**
         * None.
         */
        NONE;

        /**
         * Check State for action in this phase.
         * @param pState the State of the item
         * @return true/false is the item to be committed in this phase
         */
        private boolean checkStateInPhase(final MetisDataState pState) {
            return this == getPhaseForState(pState);
        }

        /**
         * Determine the updatePhase corresponding to the dataState.
         * @param pState the State of the item
         * @return the corresponding update phase
         */
        private static MetisUpdatePhase getPhaseForState(final MetisDataState pState) {
            /* Switch on the state */
            switch (pState) {
                case NEW:
                case DELNEW:
                    return INSERT;
                case CHANGED:
                    return UPDATE;
                case DELETED:
                    return DELETE;
                default:
                    return NONE;
            }
        }

        /**
         * Determine the updatePhase corresponding to the item.
         * @param pItem the item
         * @return the corresponding update phase
         */
        private static MetisUpdatePhase getPhaseForItem(final MetisDataEosVersionedItem pItem) {
            return getPhaseForState(pItem.getState());
        }
    }
}
