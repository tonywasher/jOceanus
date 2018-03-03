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
package net.sourceforge.joceanus.jmetis.list;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataIndexedItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;

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
    public static MetisListSetVersioned newListSet() {
        return new MetisListSetVersioned(MetisListSetType.BASE);
    }

    /**
     * Reset the listSet to version zero.
     * @param pListSet the listSet
     */
    public static void reset(final MetisListSetVersioned pListSet) {
        /* If we have changes */
        if (pListSet.getVersion() != 0) {
            /* ReWind to initial version */
            reWindToVersion(pListSet, 0);
        }
    }

    /**
     * ReWind the listSet to a particular version.
     * @param pListSet the listSet
     * @param pVersion the version to reWind to
     */
    public static void reWindToVersion(final MetisListSetVersioned pListSet,
                                       final int pVersion) {
        /* Only allowed for Base/Edit ListSets */
        final MetisListSetType myType = pListSet.getListSetType();
        if (!MetisListSetType.BASE.equals(myType)
            || !MetisListSetType.EDIT.equals(myType)) {
            throw new IllegalArgumentException();
        }

        /* Check that the rewind version is valid */
        if (pListSet.getVersion() < pVersion
            || pVersion < 0) {
            throw new IllegalArgumentException("Invalid Version");
        }

        /* Create a new ListSet event */
        final MetisListSetChange myChanges = new MetisListSetChange(MetisListEvent.REWIND);

        /* Loop through the lists */
        final Iterator<MetisListVersioned<MetisFieldVersionedItem>> myIterator = pListSet.listIterator();
        while (myIterator.hasNext()) {
            final MetisListVersioned<MetisFieldVersionedItem> myList = myIterator.next();

            /* If the list needs reWinding */
            if (myList.getVersion() > pVersion) {
                /* ReWind it and register the changes */
                final MetisListChange<MetisFieldVersionedItem> myChange = doReWindToVersion(myList, pVersion);
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
     * @param pVersion the version to reWind to
     * @return the change for the list
     */
    private static <T extends MetisFieldVersionedItem> MetisListChange<T> doReWindToVersion(final MetisListVersioned<T> pList,
                                                                                            final int pVersion) {
        /* Create a new Change Detail */
        final MetisListChange<T> myChange = new MetisListChange<>(pList.getItemType(), MetisListEvent.REWIND);

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
                    continue;
                }

                /* Loop while version is too high */
                while (myVersion > pVersion) {
                    /* Pop history */
                    myCurr.popTheHistory();
                    myVersion = myCurr.getVersion();
                }

                /* Adjust the state */
                myCurr.adjustState();

                /* Note maximum version */
                myMaxVersion = Math.max(myMaxVersion, myVersion);

                /* Register the change */
                myChange.registerChanged(myCurr);
            }
        }

        /* Record the new maxVersion */
        pList.setVersion(myMaxVersion);

        /* Return the change */
        return myChange;
    }

    /**
     * Reset the content of a listSet.
     * @param pTarget the target listSet
     * @param pSource the source content to reset to
     */
    public static void resetContent(final MetisListSetVersioned pTarget,
                                    final MetisListSetVersioned pSource) {
        /* Only allowed for Base ListSets */
        if (!MetisListSetType.BASE.equals(pTarget.getListSetType())
            || !MetisListSetType.EDIT.equals(pSource.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* Create a new ListSet event */
        final MetisListSetChange myChanges = new MetisListSetChange(MetisListEvent.REFRESH);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = pTarget.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the lists */
            final MetisListVersioned<MetisFieldVersionedItem> mySource = pSource.getList(myKey);
            final MetisListVersioned<MetisFieldVersionedItem> myTarget = pTarget.getList(myKey);

            /* Reset the content */
            doResetContent(myTarget, mySource);
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
     * @param pSource the source list
     */
    private static <T extends MetisFieldVersionedItem> void doResetContent(final MetisListVersioned<T> pTarget,
                                                                           final MetisListVersioned<T> pSource) {
        /* Clear the list */
        pTarget.clear();

        /* Loop through the list */
        final Iterator<T> myIterator = pSource.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

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
        pTarget.clear();

        /* Loop through the list */
        while (pSource.hasNext()) {
            final T myCurr = pSource.next();

            /* Add the item to the list */
            pTarget.add(myCurr);
        }

        /* Fire the event */
        final MetisListChange<T> myChange = new MetisListChange<>(null, MetisListEvent.REFRESH);
        pTarget.fireEvent(myChange);
    }

    /**
     * ReBase the listSet.
     * @param pTarget the target listSet
     * @param pBase the base listSet
     */
    public static void reBaseListSet(final MetisListSetVersioned pTarget,
                                     final MetisListSetVersioned pBase) {
        /* Only allowed for Base ListSets */
        if (!MetisListSetType.BASE.equals(pTarget.getListSetType())
            || !MetisListSetType.EDIT.equals(pBase.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* ListSet versions must be 0 */
        if ((pTarget.getVersion() != 0)
            || (pBase.getVersion() != 0)) {
            throw new IllegalStateException("Versioned ListSet being reBased");
        }

        /* Create a new ListSet event */
        final MetisListSetChange myChanges = new MetisListSetChange(MetisListEvent.REBASE);

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = pTarget.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisListVersioned<MetisFieldVersionedItem> myBase = pBase.getList(myKey);
            final MetisListVersioned<MetisFieldVersionedItem> myTarget = pTarget.getList(myKey);

            /* reBase the list */
            final MetisListChange<MetisFieldVersionedItem> myChange = doReBaseList(myTarget, myBase);
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
    private static <T extends MetisFieldVersionedItem> MetisListChange<T> doReBaseList(final MetisListVersioned<T> pTarget,
                                                                                       final MetisListVersioned<T> pBase) {
        /* Create a new Change Detail */
        final MetisListChange<T> myChange = new MetisListChange<>(pTarget.getItemType(), MetisListEvent.REBASE);

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
                myCurr.getValueSet().setVersion(1);
                myChange.registerAdded(myCurr);
                hasChanges = true;

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* ReBase the history */
                    final MetisFieldVersionValues myBase = myItem.getValueSet().cloneIt();
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
            myChange.registerDeleted(myItem);
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
