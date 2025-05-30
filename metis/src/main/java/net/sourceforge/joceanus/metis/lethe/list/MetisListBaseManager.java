/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.lethe.list;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataIndexedItem;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;

import java.util.Iterator;
import java.util.Map;

/**
 * List Base Methods.
 */
public final class MetisListBaseManager {
    /**
     * Private constructor.
     */
    private MetisListBaseManager() {
    }

    /**
     * Create a new base listSet.
     * @return the new ListSet
     */
    public static MetisLetheListSetVersioned newListSet() {
        return new MetisLetheListSetVersioned(MetisListSetType.BASE);
    }

    /**
     * Refresh the listSet and dependents.
     * @param pListSet the listSet
     */
    public static void refresh(final MetisLetheListSetVersioned pListSet) {
        /* Only allowed for Edit ListSets */
        if (!MetisListSetType.BASE.equals(pListSet.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* ListSet version must be 0 */
        if (pListSet.getVersion() != 0) {
            throw new IllegalStateException("Versioned ListSet being refreshed");
        }

        /* Create a new ListSet event */
        final MetisLetheListSetChange myChanges = new MetisLetheListSetChange(MetisLetheListEvent.REFRESH);

        /* Fire the event */
        pListSet.fireEvent(myChanges);
    }

    /**
     * Reset the listSet to version zero.
     * @param pListSet the listSet
     */
    public static void reset(final MetisLetheListSetVersioned pListSet) {
        /* If we have changes */
        if (pListSet.getVersion() != 0) {
            /* ReWind to initial version */
            reWindToVersion(pListSet, 0);
        }
    }

    /**
     * Undo the last change to the listSet.
     * @param pListSet the listSet
     */
    public static void undoLastChange(final MetisLetheListSetVersioned pListSet) {
        /* If we have changes */
        final int myVersion = pListSet.getVersion();
        if (myVersion > 0) {
            /* ReWind to previous version */
            reWindToVersion(pListSet, myVersion - 1);
        }
    }

    /**
     * ReWind the listSet to a particular version.
     * @param pListSet the listSet
     * @param pVersion the version to reWind to
     */
    private static void reWindToVersion(final MetisLetheListSetVersioned pListSet,
                                        final int pVersion) {
        /* Only allowed for Base/Edit ListSets */
        final MetisListSetType myType = pListSet.getListSetType();
        if (!MetisListSetType.BASE.equals(myType)
            && !MetisListSetType.EDIT.equals(myType)) {
            throw new IllegalArgumentException();
        }

        /* Check that the rewind version is valid */
        if (pListSet.getVersion() < pVersion
            || pVersion < 0) {
            throw new IllegalArgumentException("Invalid Version");
        }

        /* Create a new ListSet event */
        final MetisLetheListSetChange myChanges = new MetisLetheListSetChange(pVersion);

        /* Loop through the lists */
        final Iterator<MetisLetheListVersioned<MetisFieldVersionedItem>> myIterator = pListSet.listIterator();
        while (myIterator.hasNext()) {
            final MetisLetheListVersioned<MetisFieldVersionedItem> myList = myIterator.next();

            /* If the list needs reWinding */
            if (myList.getVersion() > pVersion) {
                /* ReWind it and register the changes */
                final MetisLetheListChange<MetisFieldVersionedItem> myChange = doReWindToVersion(myList, pListSet, pVersion);
                myChanges.registerChangedList(myChange);
            }
        }

        /* Set the version correctly */
        pListSet.setVersion(pVersion);

        /* Fire the event */
        pListSet.fireEvent(myChanges);
    }

    /**
     * ReWind the list to a particular version.
     * @param <T> the item type
     * @param pList the list
     * @param pListSet the listSet
     * @param pVersion the version to reWind to
     * @return the change for the list
     */
    private static <T extends MetisFieldVersionedItem> MetisLetheListChange<T> doReWindToVersion(final MetisLetheListVersioned<T> pList,
                                                                                                 final MetisLetheListSetVersioned pListSet,
                                                                                                 final int pVersion) {
        /* Create a new Change Detail */
        final MetisLetheListChange<T> myChange = new MetisLetheListChange<>(pList.getItemType(), MetisLetheListEvent.VERSION);

        /* Note maximum version */
        int myMaxVersion = 0;

        /* Loop through the list */
        final Iterator<T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* If the version is later than the required version */
            int myVersion = myCurr.getVersion();
            if (myVersion > pVersion) {
                /* If the item was created after the required version */
                if (myCurr.getOriginalVersion() > pVersion) {
                    /* Remove from list */
                    myIterator.remove();
                    myChange.registerDeleted(myCurr);
                    pListSet.cleanupDeletedItem(myCurr);
                    continue;
                }

                /* Note the current deleted status */
                final boolean isDeleted = myCurr.isDeleted();

                /* Loop while version is too high */
                while (myVersion > pVersion) {
                    /* Pop history */
                    myCurr.popHistory();
                    myVersion = myCurr.getVersion();
                }

                /* Adjust the state */
                myCurr.adjustState();

                /* Note maximum version */
                myMaxVersion = Math.max(myMaxVersion, myVersion);

                /* Register the change */
                if (isDeleted == myCurr.isDeleted()) {
                    myChange.registerChanged(myCurr);
                } else if (isDeleted) {
                    myChange.registerRestored(myCurr);
                } else {
                    myChange.registerHidden(myCurr);
                }
            }
        }

        /* Record the new maxVersion */
        pList.setVersion(myMaxVersion);
        myChange.setVersion(myMaxVersion);

        /* Return the change */
        return myChange;
    }

    /**
     * Reset the content of a listSet.
     * @param pTarget the target listSet
     * @param pSource the source content to reset to
     */
    public static void resetContent(final MetisLetheListSetVersioned pTarget,
                                    final MetisLetheListSetVersioned pSource) {
        /* Only allowed for Base ListSets */
        if (!MetisListSetType.BASE.equals(pTarget.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* Create a new ListSet event */
        final MetisLetheListSetChange myChanges = new MetisLetheListSetChange(MetisLetheListEvent.REFRESH);

        /* Clone paired items to target */
        pTarget.clonePairedItems(pSource);

        /* Loop through the lists */
        final Iterator<MetisLetheListKey> myIterator = pTarget.keyIterator();
        while (myIterator.hasNext()) {
            final MetisLetheListKey myKey = myIterator.next();

            /* Obtain the lists */
            final MetisLetheListVersioned<MetisFieldVersionedItem> mySource = pSource.getList(myKey);
            final MetisLetheListVersioned<MetisFieldVersionedItem> myTarget = pTarget.getList(myKey);

            /* Reset the content */
            doResetContent(myTarget, mySource.iterator());
        }

        /* Set the version correctly */
        pTarget.setVersion(pSource.getVersion());

        /* Fire the event */
        pTarget.fireEvent(myChanges);
    }

    /**
     * Reset content.
     * @param <T> the item type
     * @param pTarget the target list
     * @param pIterator the source list iterator
     */
    private static <T extends MetisDataIndexedItem> void doResetContent(final MetisListIndexed<T> pTarget,
                                                                        final Iterator<T> pIterator) {
        /* Clear the list */
        pTarget.clear();

        /* Loop through the list */
        while (pIterator.hasNext()) {
            final T myCurr = pIterator.next();

            /* Add the item to the list */
            pTarget.add(myCurr);
        }
    }

    /**
     * Reset content.
     * @param <T> the item type
     * @param pTarget the target list
     * @param pSource the source list
     */
    public static <T extends MetisDataIndexedItem> void resetContent(final MetisListIndexed<T> pTarget,
                                                                     final Iterator<T> pSource) {
        /* Clear the list */
        doResetContent(pTarget, pSource);

        /* Fire the event */
        final MetisLetheListChange<T> myChange = new MetisLetheListChange<>(null, MetisLetheListEvent.REFRESH);
        //pTarget.fireEvent(myChange);
    }

    /**
     * ReBase the listSet.
     * @param pTarget the target listSet
     * @param pBase the base listSet
     */
    public static void reBaseListSet(final MetisLetheListSetVersioned pTarget,
                                     final MetisLetheListSetVersioned pBase) {
        /* Only allowed for Base ListSets */
        if (!MetisListSetType.BASE.equals(pTarget.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* ListSet versions must be 0 */
        if (pTarget.getVersion() != 0
            || pBase.getVersion() != 0) {
            throw new IllegalStateException("Versioned ListSet being reBased");
        }

        /* Create a new ListSet event */
        final MetisLetheListSetChange myChanges = new MetisLetheListSetChange(MetisLetheListEvent.UPDATE);

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisLetheListKey> myIterator = pTarget.keyIterator();
        while (myIterator.hasNext()) {
            final MetisLetheListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisLetheListVersioned<MetisFieldVersionedItem> myBase = pBase.getList(myKey);
            final MetisLetheListVersioned<MetisFieldVersionedItem> myTarget = pTarget.getList(myKey);

            /* reBase the list */
            final MetisLetheListChange<MetisFieldVersionedItem> myChange = doReBaseList(myTarget, myBase);
            if (!myChanges.isEmpty()) {
                myChanges.registerChangedList(myChange);
            }

            /* Note maximum version */
            myNewVersion = Math.max(myNewVersion, myTarget.getVersion());
        }

        /* Set the version correctly */
        pTarget.setVersion(myNewVersion);

        /* Fire the event */
        if (!myChanges.isEmpty()) {
            pTarget.fireEvent(myChanges);
        }
    }

    /**
     * Rebase content.
     * @param <T> the item type
     * @param pTarget the base list
     * @param pBase the base list
     * @return the change for the list
     */
    private static <T extends MetisFieldVersionedItem> MetisLetheListChange<T> doReBaseList(final MetisLetheListVersioned<T> pTarget,
                                                                                            final MetisLetheListVersioned<T> pBase) {
        /* Create a new Change Detail */
        final MetisLetheListChange<T> myChange = new MetisLetheListChange<>(pTarget.getItemType(), MetisLetheListEvent.UPDATE);

        /* Access a copy of the idMap of the base list */
        final Map<Integer, T> myOld = pBase.copyIdMap();
        boolean hasChanges = false;

        /* Loop through the list */
        Iterator<T> myIterator = pTarget.iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            final T myCurr = myIterator.next();
            final Integer myId = myCurr.getIndexedId();
            final T myItem = myOld.get(myId);

            /* If the item does not exist in the old list */
            if (myItem == null) {
                /* Set the version to 1 */
                myCurr.getValues().setVersion(1);
                myChange.registerChanged(myCurr);
                hasChanges = true;

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* ReBase the history */
                    final MetisFieldVersionValues myBase = myItem.getValues().cloneIt();
                    myCurr.setHistory(myBase);
                    myChange.registerChanged(myItem);
                    hasChanges = true;
                }

                /* Remove the item from the map */
                myOld.remove(myId);
            }
        }

        /* Loop through the remaining items in the old list */
        myIterator = myOld.values().iterator();
        while (myIterator.hasNext()) {
            /* Insert a new item */
            final T myCurr = myIterator.next();
            final T myItem = MetisListDiffManager.newDiffDeletedItem(pTarget, myCurr);
            pTarget.add(myItem);
            myChange.registerAdded(myItem);
            hasChanges = true;
        }

        /* Note changes */
        if (hasChanges) {
            pTarget.setVersion(1);
        }

        /* Return the changes */
        return myChange;
    }
}
