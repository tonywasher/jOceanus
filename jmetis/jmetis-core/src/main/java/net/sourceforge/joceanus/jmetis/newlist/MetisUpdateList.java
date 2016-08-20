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

import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Update List.
 * @param <T> the item type
 */
public class MetisUpdateList<T extends MetisVersionedItem>
        extends MetisVersionedList<T> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisUpdateList.class.getSimpleName(), MetisVersionedList.getBaseFields());

    /**
     * Size Field Id.
     */
    private static final MetisField FIELD_SOURCE = FIELD_DEFS.declareLocalField("Source");

    /**
     * The source list.
     */
    private MetisVersionedList<T> theSource;

    /**
     * Constructor.
     * @param pSource the source list
     */
    public MetisUpdateList(final MetisVersionedList<T> pSource) {
        super(pSource.getTheClass());
        setSource(pSource);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_SOURCE.equals(pField)) {
            return theSource == null || theSource.isEmpty()
                                                            ? MetisFieldValue.SKIP
                                                            : theSource;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Derive update items.
     * @param pSource the source list
     */
    public void setSource(final MetisVersionedList<T> pSource) {
        /* Store source and initialise the update list */
        theSource = pSource;
        deriveUpdates();

        /* Listen for updates */
        TethysEventRegistrar<MetisListEvent> myRegistrar = theSource.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.COMMIT, this::handleBaseChanges);
        myRegistrar.addEventListener(MetisListEvent.REWIND, this::handleBaseChanges);
    }

    /**
     * Derive update items.
     */
    public void deriveUpdates() {
        /* Clear the list */
        clear();

        /* Loop through the list */
        Iterator<T> myIterator = theSource.iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* process update item */
            processUpdate(myCurr);
        }

        /* Make sure that the version is correct */
        setVersion(isEmpty()
                             ? 0
                             : 1);
    }

    /**
     * process update item.
     * @param pBase the base update
     */
    public void processUpdate(final T pBase) {
        /* Obtain the valueSet history */
        MetisValueSetHistory myHistory = pBase.getValueSetHistory();
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
        T myItem = newAddedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle changed update.
     * @param pItem the item
     */
    private void handleChangedUpdate(final T pItem) {
        T myItem = newChangedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle deleted update.
     * @param pItem the item
     */
    private void handleDeletedUpdate(final T pItem) {
        T myItem = newDeletedItem(pItem);
        addToList(myItem);
    }

    /**
     * handle delNew update.
     * @param pItem the item
     */
    private void handleDelNewUpdate(final T pItem) {
        T myItem = newDelNewItem(pItem);
        addToList(myItem);
    }

    /**
     * Commit items.
     * @param pPhase the update phase
     * @param pNumItems the number of items to commit
     * @return the number of commit items remaining
     */
    public int commitItems(final MetisUpdatePhase pPhase,
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
            MetisValueSetHistory myHistory = myCurr.getValueSetHistory();
            MetisDataState myState = MetisDataState.determineState(myHistory);

            /* If this is to be handled in this phase */
            if (checkStateInPhase(pPhase, myState)) {
                /* Access further details */
                MetisValueSet myValues = myCurr.getValueSet();
                int myId = myCurr.getIndexedId();
                T myBase = theSource.getItemById(myId);

                /* Commit the underlying item */
                if (myValues.isDeletion()) {
                    theSource.removeFromList(myBase);
                    myChange.registerDeleted(myBase);
                } else {
                    myBase.getValueSetHistory().clearHistory();
                }

                /* Adjust update list */
                myIterator.remove();
                if (!MetisDataState.DELNEW.equals(myState)) {
                    myNumItems--;
                }
            }
        }

        /* Fire the event */
        theSource.fireEvent(myChange);

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
    private void handleBaseChanges(final TethysEvent<MetisListEvent> pEvent) {
        /* Access the change details */
        @SuppressWarnings("unchecked")
        MetisListChange<T> myChange = (MetisListChange<T>) pEvent.getDetails(MetisListChange.class);

        /* Process added entries (can only happen from a commit) */
        Iterator<T> myIterator = myChange.addedIterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            handleNewUpdate(myCurr);
        }

        /* Obtain changed entries */
        myIterator = myChange.changedIterator();
        while (myIterator.hasNext()) {
            T myBase = myIterator.next();
            int myId = myBase.getIndexedId();
            T myCurr = getItemById(myId);

            /* If we do not currently have the item */
            if (myCurr == null) {
                /* Handle as newly discovered item */
                processUpdate(myBase);
            } else {
                /* Process as a changed update */
                processChangedUpdate(myCurr, myBase);
            }
        }

        /* Process deleted entries (can only happen from a rewind) */
        Iterator<Integer> myIdIterator = myChange.deletedIterator();
        while (myIdIterator.hasNext()) {
            Integer myId = myIdIterator.next();
            T myCurr = getItemById(myId);
            removeFromList(myCurr);
        }
    }

    /**
     * process update item.
     * @param pCurr the current update
     * @param pBase the base update
     */
    public void processChangedUpdate(final T pCurr,
                                     final T pBase) {
        /* Obtain the valueSet history */
        MetisValueSetHistory myHistory = pBase.getValueSetHistory();
        MetisDataState myState = MetisDataState.determineState(myHistory);

        /* If we are now clean */
        if (MetisDataState.CLEAN.equals(myState)) {
            /* Delete the entry */
            removeFromList(pCurr);
        } else {
            /* Replace the current values */
            MetisValueSet mySet = pCurr.getValueSet();
            mySet.copyFrom(pBase.getValueSet());
        }
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
