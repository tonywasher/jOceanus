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
package net.sourceforge.joceanus.jmetis.newlist;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jmetis.newlist.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Update List.
 * @param <T> the item type
 */
public class MetisUpdateList<T extends MetisIndexedItem>
        extends MetisVersionedList<T> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisUpdateList.class.getSimpleName(), MetisVersionedList.getBaseFields());

    /**
     * Base Field Id.
     */
    private static final MetisField FIELD_BASE = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE.getValue());

    /**
     * The base list.
     */
    private final MetisBaseList<T> theBase;

    /**
     * Constructor.
     * @param pBase the base list
     */
    protected MetisUpdateList(final MetisBaseList<T> pBase) {
        /* Initialise underlying class */
        super(MetisListType.UPDATE, pBase.getTheClass(), pBase.getItemFields());

        /* Copy the comparator from the base list */
        setComparator(pBase.getComparator());

        /* Store source and initialise the update list */
        theBase = pBase;
        doDeriveUpdates();

        /* Listen for updates */
        TethysEventRegistrar<MetisListEvent> myRegistrar = theBase.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> doDeriveUpdates());
        myRegistrar.addEventListener(MetisListEvent.REBASE, e -> doDeriveUpdates());
        myRegistrar.addEventListener(MetisListEvent.COMMIT, this::handleChangesInBase);
        myRegistrar.addEventListener(MetisListEvent.REWIND, this::handleChangesInBase);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_BASE.equals(pField)) {
            return theBase.isEmpty()
                                     ? MetisFieldValue.SKIP
                                     : theBase;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Derive update items.
     */
    private void doDeriveUpdates() {
        /* Clear the list */
        clear();

        /* Loop through the base list */
        Iterator<T> myIterator = theBase.iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* process update item */
            processUpdate(myCurr);
        }

        /* Make sure that the version is correct */
        boolean isEmpty = isEmpty();
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
        /* Obtain the valueSet history */
        MetisDataValues myBase = (MetisDataValues) pBase;
        MetisValueSetHistory myHistory = myBase.getValueSetHistory();
        MetisDataState myState = MetisDataState.determineState(myHistory);

        /* Switch on the state */
        switch (myState) {
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
        T myItem = newDiffAddedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle changed update.
     * @param pItem the item
     */
    private void handleChangedUpdate(final T pItem) {
        T myItem = newUpdateChangedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle deleted update.
     * @param pItem the item
     */
    private void handleDeletedUpdate(final T pItem) {
        T myItem = newDiffDeletedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle delNew update.
     * @param pItem the item
     */
    private void handleDelNewUpdate(final T pItem) {
        T myItem = newUpdateDelNewItem(pItem);
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
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.UPDATE);

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()
               && myNumItems > 0) {
            T myCurr = myIterator.next();

            /* Obtain the state */
            MetisDataValues myVersioned = (MetisDataValues) myCurr;
            MetisValueSetHistory myHistory = myVersioned.getValueSetHistory();
            MetisDataState myState = MetisDataState.determineState(myHistory);

            /* If this is to be handled in this phase */
            if (checkStateInPhase(pPhase, myState)) {
                /* Access further details */
                MetisValueSet myValues = myVersioned.getValueSet();
                int myId = myCurr.getIndexedId();
                T myBase = theBase.getItemById(myId);

                /* Commit the underlying item */
                if (myValues.isDeletion()) {
                    theBase.removeFromList(myBase);
                    myChange.registerDeleted(myBase);
                } else {
                    MetisDataValues myBaseVersioned = (MetisDataValues) myBase;
                    myBaseVersioned.getValueSetHistory().clearHistory();
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
     * Check State for action in this phase.
     * @param pPhase the update phase
     * @param pState the State of the item
     * @return true/false is the item to be committed in this phase
     */
    private boolean checkStateInPhase(final MetisUpdatePhase pPhase,
                                      final MetisDataState pState) {
        /* Switch on the state */
        switch (pState) {
            case NEW:
            case DELNEW:
                return MetisUpdatePhase.INSERT.equals(pPhase);
            case CHANGED:
                return MetisUpdatePhase.UPDATE.equals(pPhase);
            case DELETED:
                return MetisUpdatePhase.DELETE.equals(pPhase);
            default:
                return false;
        }
    }

    /**
     * Derive update items as a result of reWind/Commit in the base list.
     * @param pEvent the event
     */
    private void handleChangesInBase(final TethysEvent<MetisListEvent> pEvent) {
        /* Access the change details */
        @SuppressWarnings("unchecked")
        MetisListChange<T> myChange = (MetisListChange<T>) pEvent.getDetails(MetisListChange.class);

        /* Process added entries (can only happen from a commit) */
        boolean doSort = false;
        Iterator<T> myIterator = myChange.addedIterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            handleNewUpdate(myCurr);
            doSort = true;
        }

        /* Obtain changed entries */
        myIterator = myChange.changedIterator();
        while (myIterator.hasNext()) {
            T myBase = myIterator.next();
            int myId = myBase.getIndexedId();
            T myCurr = getItemById(myId);
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
        Iterator<Integer> myIdIterator = myChange.deletedIterator();
        while (myIdIterator.hasNext()) {
            Integer myId = myIdIterator.next();
            T myCurr = getItemById(myId);
            removeFromList(myCurr);
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
        MetisDataValues myBase = (MetisDataValues) pBase;
        MetisValueSetHistory myHistory = myBase.getValueSetHistory();
        MetisDataState myState = MetisDataState.determineState(myHistory);

        /* If we are now clean */
        if (MetisDataState.CLEAN.equals(myState)) {
            /* Delete the entry */
            removeFromList(pCurr);
        } else {
            /* Replace the current values */
            MetisDataValues myCurr = (MetisDataValues) pCurr;
            MetisValueSet mySet = myCurr.getValueSet();
            mySet.copyFrom(myBase.getValueSet());
        }
    }

    /**
     * Create a New "changed" item for an update list.
     * @param pCurr the current item
     * @return the new item
     */
    private T newUpdateChangedItem(final T pCurr) {
        /* Obtain a new item */
        T myNew = newListItem(pCurr.getIndexedId());

        /* Access versioned controls */
        MetisDataValues myCurr = (MetisDataValues) pCurr;
        MetisDataValues myItem = (MetisDataValues) myNew;

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = myCurr.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the original value set as the base value */
        MetisValueSetHistory myHistory = myCurr.getValueSetHistory();
        MetisValueSet myBase = myHistory.getOriginalValues();
        myBase = myBase.cloneIt();

        /* Record as the history of the item */
        myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);
        myHistory.setHistory(myBase);

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
        T myNew = newListItem(pBase.getIndexedId());

        /* Access versioned controls */
        MetisDataValues myBase = (MetisDataValues) pBase;
        MetisDataValues myItem = (MetisDataValues) myNew;

        /* Obtain a deleted values set as the current value */
        MetisValueSet myBaseSet = myBase.getValueSet();
        MetisValueSet mySet = myBaseSet.cloneIt();
        mySet.setDeletion(true);
        mySet.setVersion(1);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);

        /* Return the new item */
        return myNew;
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
        DELETE;
    }
}
