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
package net.sourceforge.joceanus.jmetis.eos.list;

import java.util.Comparator;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * List Update Generator.
 */
public final class MetisEosListUpdateManager {
    /**
     * Private constructor.
     */
    private MetisEosListUpdateManager() {
    }

    /**
     * Derive an update listSet.
     * @param pBase the base listSet
     * @return the updateSet
     */
    public static MetisEosListSetVersioned deriveUpdateListSet(final MetisEosListSetVersioned pBase) {
        /* Create a new updateSet */
        final MetisEosListSetVersioned myUpdates = new MetisEosListSetVersioned(pBase);

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisEosListKey> myIterator = pBase.keyIterator();
        while (myIterator.hasNext()) {
            final MetisEosListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisEosListVersioned<MetisFieldVersionedItem> myBase = pBase.getList(myKey);

            /* Create the new list */
            final MetisEosListVersioned<MetisFieldVersionedItem> myUpdate = new MetisEosListVersioned<>(myUpdates, myBase);

            /* configure the update list */
            configureUpdateList(myUpdate);

            /* Add the list to the listSet */
            myUpdates.declareList(myKey, myUpdate);
            if (!myUpdate.isEmpty()) {
                myNewVersion = 1;
            }
        }

        /* Register event handlers */
        final TethysEventRegistrar<MetisEosListEvent> myRegistrar = pBase.getEventRegistrar();
        myRegistrar.addEventListener(MetisEosListEvent.REFRESH, e -> deriveUpdates(myUpdates));
        myRegistrar.addEventListener(MetisEosListEvent.REBASE, e -> handleChangesInBaseSet(myUpdates, e));
        myRegistrar.addEventListener(MetisEosListEvent.COMMIT, e -> handleChangesInBaseSet(myUpdates, e));
        myRegistrar.addEventListener(MetisEosListEvent.REWIND, e -> handleChangesInBaseSet(myUpdates, e));

        /* Return the updateSet */
        myUpdates.setVersion(myNewVersion);
        return myUpdates;
    }

    /**
     * Commit update batch.
     * @param pUpdates the updates listSet
     * @param pNumItems the number of items to commit
     */
    public static void commitUpdateBatch(final MetisEosListSetVersioned pUpdates,
                                         final int pNumItems) {
        /* Access the number of items */
        int myNumItems = pNumItems;

        /* Loop through the phases */
        MetisUpdatePhase myPhase = MetisUpdatePhase.INSERT;
        while (!MetisUpdatePhase.NONE.equals(myPhase)) {
            /* Process the next phase */
            myNumItems = commitUpdateBatch(pUpdates, myPhase, myNumItems);

            /* Break the loop if we have finished */
            if (myNumItems == 0) {
                break;
            }

            /* Switch to next phase */
            myPhase = myPhase.getNextPhase();
        }

        /* If we have no updates for this list anymore */
        if (pUpdates.isEmpty()) {
            /* Ensure that both listSets are version zero */
            pUpdates.setVersion(0);
            pUpdates.getBaseListSet().setVersion(0);
        }
    }

    /**
     * Commit update batch.
     * @param pUpdates the updates listSet
     * @param pPhase the update phase
     * @param pNumItems the number of items to commit
     * @return the number of commit items remaining
     */
    private static int commitUpdateBatch(final MetisEosListSetVersioned pUpdates,
                                         final MetisUpdatePhase pPhase,
                                         final int pNumItems) {
        /* Access the number of items */
        int myNumItems = pNumItems;

        /* Loop through the lists */
        final Iterator<MetisEosListKey> myIterator = MetisUpdatePhase.DELETE.equals(pPhase)
                                                                                            ? pUpdates.reverseKeyIterator()
                                                                                            : pUpdates.keyIterator();
        while (myIterator.hasNext()) {
            final MetisEosListKey myKey = myIterator.next();

            /* If the list is non-empty */
            final MetisEosListVersioned<MetisFieldVersionedItem> myList = pUpdates.getList(myKey);
            if (!myList.isEmpty()) {
                /* Commit the items */
                myNumItems = commitUpdateBatch(myList, pPhase, pNumItems);

                /* Break loop if we have finished */
                if (myNumItems == 0) {
                    break;
                }
            }
        }

        /* return the remaining number of items */
        return myNumItems;
    }

    /**
     * Commit update batch.
     * @param <T> the itemType for the list
     * @param pUpdates the list of updates
     * @param pPhase the update phase
     * @param pNumItems the number of items to commit
     * @return the number of commit items remaining
     */
    private static <T extends MetisFieldVersionedItem> int commitUpdateBatch(final MetisEosListVersioned<T> pUpdates,
                                                                             final MetisUpdatePhase pPhase,
                                                                             final int pNumItems) {
        /* Access the base list */
        final MetisEosListVersioned<T> myBaseList = pUpdates.getBaseList();

        /* Access the item count */
        int myNumItems = pNumItems;

        /* Loop through the list */
        final Iterator<T> myIterator = MetisUpdatePhase.DELETE.equals(pPhase)
                                                                              ? pUpdates.reverseIterator()
                                                                              : pUpdates.iterator();
        while (myIterator.hasNext()
               && myNumItems > 0) {
            final T myCurr = myIterator.next();

            /* Obtain the state */
            final MetisDataState myState = myCurr.getState();

            /* If this is to be handled in this phase */
            if (pPhase.checkStateInPhase(myState)) {
                /* Access further details */
                final MetisFieldVersionValues myValues = myCurr.getValueSet();
                final int myId = myCurr.getIndexedId();
                final T myBase = myBaseList.getItemById(myId);

                /* Commit the underlying item */
                if (myValues.isDeletion()) {
                    myBaseList.removeFromList(myBase);
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

        /* If we have no updates for this list anymore */
        if (pUpdates.isEmpty()) {
            /* Ensure that both lists are version zero */
            pUpdates.setVersion(0);
            myBaseList.setVersion(0);
        }

        /* Return the new count */
        return myNumItems;
    }

    /**
     * Constructor.
     * @param <T> the itemType for the list
     * @param pUpdates the updates list
     */
    private static <T extends MetisFieldVersionedItem> void configureUpdateList(final MetisEosListVersioned<T> pUpdates) {
        /* Access the base list */
        final MetisEosListVersioned<T> myBaseList = pUpdates.getBaseList();

        /* Copy the comparator from the base list */
        pUpdates.setComparator(new UpdateComparator<>(myBaseList.getComparator()));

        /* Initialise the update list */
        doDeriveUpdates(pUpdates);
    }

    /**
     * Amend update items as a result of reWind/Commit in the base listSet.
     * @param pUpdates the updates listSet
     * @param pEvent the event
     */
    private static void handleChangesInBaseSet(final MetisEosListSetVersioned pUpdates,
                                               final TethysEvent<MetisEosListEvent> pEvent) {
        /* Access the change details */
        final MetisEosListSetChange myChanges = pEvent.getDetails(MetisEosListSetChange.class);

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisEosListKey> myIterator = pUpdates.keyIterator();
        while (myIterator.hasNext()) {
            final MetisEosListKey myKey = myIterator.next();

            /* Obtain the update list and associated change */
            final MetisEosListVersioned<MetisFieldVersionedItem> myUpdates = pUpdates.getList(myKey);
            final MetisEosListChange<MetisFieldVersionedItem> myChange = myChanges.getListChange(myKey);

            /* If there are changes */
            if (myChange != null) {
                /* handle changes in the base list */
                doHandleChangesInBase(myUpdates, myChange);
            }

            /* Add the list to the listSet */
            if (!myUpdates.isEmpty()) {
                myNewVersion = 1;
            }
        }

        /* Update the listSet version */
        pUpdates.setVersion(myNewVersion);
    }

    /**
     * Derive update items as a result of reWind/Commit in the base list.
     * @param <T> the itemType for the list
     * @param pUpdates the updates list
     * @param pChange the change event
     */
    private static <T extends MetisFieldVersionedItem> void doHandleChangesInBase(final MetisEosListVersioned<T> pUpdates,
                                                                                  final MetisEosListChange<T> pChange) {
        /* Process added entries (can only happen from a commit) */
        boolean doSort = false;
        Iterator<T> myIterator = pChange.addedIterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();
            handleNewUpdate(pUpdates, myCurr);
            doSort = true;
        }

        /* Obtain changed entries */
        myIterator = pChange.changedIterator();
        while (myIterator.hasNext()) {
            final T myBase = myIterator.next();
            final int myId = myBase.getIndexedId();
            final T myCurr = pUpdates.getItemById(myId);
            doSort = true;

            /* If we do not currently have the item */
            if (myCurr == null) {
                /* Handle as newly discovered item */
                processUpdate(pUpdates, myBase);
            } else {
                /* Process as a changed update */
                processChangedUpdate(pUpdates, myCurr, myBase);
            }
        }

        /* Process deleted entries (can only happen from a rewind of DelNew) */
        myIterator = pChange.deletedIterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();
            final Integer myId = myCurr.getIndexedId();
            pUpdates.removeById(myId);
        }

        /* Make sure that the version is correct */
        pUpdates.setVersion(pUpdates.isEmpty()
                                               ? 0
                                               : 1);

        /* Sort the list if necessary */
        if (doSort) {
            pUpdates.sortList();
        }
    }

    /**
     * Derive update items as a result of refresh/reBase in the base listSet.
     * @param pUpdates the updates listSet
     */
    private static void deriveUpdates(final MetisEosListSetVersioned pUpdates) {
        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisEosListKey> myIterator = pUpdates.keyIterator();
        while (myIterator.hasNext()) {
            final MetisEosListKey myKey = myIterator.next();

            /* Obtain the update and base lists */
            final MetisEosListVersioned<MetisFieldVersionedItem> myUpdates = pUpdates.getList(myKey);

            /* handle changes in the base list */
            doDeriveUpdates(myUpdates);

            /* Add the list to the listSet */
            if (!myUpdates.isEmpty()) {
                myNewVersion = 1;
            }
        }

        /* Update the listSet version */
        pUpdates.setVersion(myNewVersion);
    }

    /**
     * Derive update items.
     * @param <T> the itemType for the list
     * @param pList the update list
     */
    private static <T extends MetisFieldVersionedItem> void doDeriveUpdates(final MetisEosListVersioned<T> pList) {
        /* Clear the list */
        pList.clear();

        /* Access the base list */
        final MetisEosListVersioned<T> myBaseList = pList.getBaseList();

        /* Loop through the base list */
        final Iterator<T> myIterator = myBaseList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* process update item */
            processUpdate(pList, myCurr);
        }

        /* Make sure that the version is correct */
        final boolean isEmpty = pList.isEmpty();
        pList.setVersion(isEmpty
                                 ? 0
                                 : 1);

        /* Sort the list if necessary */
        if (!isEmpty) {
            pList.sortList();
        }
    }

    /**
     * process changed update item.
     * @param <T> the itemType for the list
     * @param pUpdates the update list
     * @param pCurr the current update
     * @param pBase the base update
     */
    private static <T extends MetisFieldVersionedItem> void processChangedUpdate(final MetisEosListVersioned<T> pUpdates,
                                                                                 final T pCurr,
                                                                                 final T pBase) {
        /* Obtain the valueSet history */
        final MetisDataState myState = pBase.getState();

        /* If we are now clean */
        if (MetisDataState.CLEAN.equals(myState)) {
            /* Delete the entry */
            pUpdates.removeFromList(pCurr);
        } else {
            /* Replace the current values */
            final MetisFieldVersionValues myBase = pBase.getValueSet();
            final MetisFieldVersionValues mySet = pCurr.getValueSet();
            mySet.copyFrom(myBase);
        }
    }

    /**
     * process update item.
     * @param <T> the itemType for the list
     * @param pList the update list
     * @param pBase the base update item
     */
    private static <T extends MetisFieldVersionedItem> void processUpdate(final MetisEosListVersioned<T> pList,
                                                                          final T pBase) {
        /* Switch on the state */
        switch (pBase.getState()) {
            case NEW:
                handleNewUpdate(pList, pBase);
                break;
            case CHANGED:
                handleChangedUpdate(pList, pBase);
                break;
            case DELETED:
                handleDeletedUpdate(pList, pBase);
                break;
            case DELNEW:
                handleDelNewUpdate(pList, pBase);
                break;
            default:
                break;
        }
    }

    /**
     * handle new update.
     * @param <T> the itemType for the list
     * @param pList the update list
     * @param pItem the item
     */
    private static <T extends MetisFieldVersionedItem> void handleNewUpdate(final MetisEosListVersioned<T> pList,
                                                                            final T pItem) {
        final T myItem = MetisEosListDiffManager.newDiffAddedItem(pList, pItem);
        pList.add(myItem);
    }

    /**
     * handle changed update.
     * @param <T> the itemType for the list
     * @param pList the update list
     * @param pItem the item
     */
    private static <T extends MetisFieldVersionedItem> void handleChangedUpdate(final MetisEosListVersioned<T> pList,
                                                                                final T pItem) {
        final T myItem = newUpdateChangedItem(pList, pItem);
        pList.add(myItem);
    }

    /**
     * handle deleted update.
     * @param <T> the itemType for the list
     * @param pList the update list
     * @param pItem the item
     */
    private static <T extends MetisFieldVersionedItem> void handleDeletedUpdate(final MetisEosListVersioned<T> pList,
                                                                                final T pItem) {
        final T myItem = MetisEosListDiffManager.newDiffDeletedItem(pList, pItem);
        pList.add(myItem);
    }

    /**
     * handle delNew update.
     * @param <T> the itemType for the list
     * @param pList the update list
     * @param pItem the item
     */
    private static <T extends MetisFieldVersionedItem> void handleDelNewUpdate(final MetisEosListVersioned<T> pList,
                                                                               final T pItem) {
        final T myItem = newUpdateDelNewItem(pList, pItem);
        pList.add(myItem);
    }

    /**
     * Create a New "changed" item for an update list.
     * @param <T> the itemType for the list
     * @param pList the update list
     * @param pCurr the current item
     * @return the new item
     */
    private static <T extends MetisFieldVersionedItem> T newUpdateChangedItem(final MetisEosListVersioned<T> pList,
                                                                              final T pCurr) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisFieldVersionValues mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the original value set as the base value */
        MetisFieldVersionValues myBase = pCurr.getOriginalValues();
        myBase = myBase.cloneIt();

        /* Record as the history of the item */
        myNew.setValues(mySet);
        myNew.setHistory(myBase);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "delNew" item for an update list.
     * @param <T> the itemType for the list
     * @param pList the update list
     * @param pBase the base item
     * @return the new item
     */
    private static <T extends MetisFieldVersionedItem> T newUpdateDelNewItem(final MetisEosListVersioned<T> pList,
                                                                             final T pBase) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pBase.getIndexedId());

        /* Obtain a deleted values set as the current value */
        final MetisFieldVersionValues myBaseSet = pBase.getValueSet();
        final MetisFieldVersionValues mySet = myBaseSet.cloneIt();
        mySet.setDeletion(true);
        mySet.setVersion(1);

        /* Record as the history of the item */
        myNew.setValues(mySet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Comparator class.
     * @param <T> the itemType for the list
     */
    private static class UpdateComparator<T extends MetisFieldVersionedItem> implements Comparator<T> {
        /**
         * The base comparator.
         */
        private final Comparator<T> theComparator;

        /**
         * Constructor.
         * @param pComparator the underlying comparator
         */
        UpdateComparator(final Comparator<T> pComparator) {
            theComparator = pComparator;
        }

        @Override
        public int compare(final T pFirst,
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
    }

    /**
     * Update phase.
     */
    private enum MetisUpdatePhase {
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
         * Obtain the next phase.
         * @return the next phase
         */
        private MetisUpdatePhase getNextPhase() {
            /* Switch on the state */
            switch (this) {
                case INSERT:
                    return UPDATE;
                case UPDATE:
                    return DELETE;
                case DELETE:
                case NONE:
                default:
                    return NONE;
            }
        }

        /**
         * Determine the updatePhase corresponding to the item.
         * @param pItem the item
         * @return the corresponding update phase
         */
        private static MetisUpdatePhase getPhaseForItem(final MetisFieldVersionedItem pItem) {
            return getPhaseForState(pItem.getState());
        }
    }
}
