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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;

/**
 * Metis VersionedList Edit Session.
 */
public class MetisListEditSession {
    /**
     * The ListSet.
     */
    private final MetisListSetVersioned theListSet;

    /**
     * The active session lists.
     */
    private final List<MetisListKey> theSessionLists;

    /**
     * The active version lists.
     */
    private final List<MetisListKey> theVersionLists;

    /**
     * The new version.
     */
    private int theNewVersion;

    /**
     * Constructor.
     * @param pListSet the listSet
     */
    protected MetisListEditSession(final MetisListSetVersioned pListSet) {
        /* Store parameters */
        theListSet = pListSet;
        theNewVersion = -1;

        /* Create the active lists */
        theSessionLists = new ArrayList<>();
        theVersionLists = new ArrayList<>();
    }

    /**
     * Start a new version.
     */
    private void newVersion() {
        theNewVersion = theListSet.getVersion() + 1;
    }

    /**
     * Is the session active?.
     * @return true/false
     */
    public boolean activeSession() {
        return !theSessionLists.isEmpty();
    }

    /**
     * Is the version active?.
     * @return true/false
     */
    public boolean activeVersion() {
        return !theVersionLists.isEmpty();
    }

    /**
     * Ensure that the list contains the itemType.
     * @param pList the list
     * @param pItemType the itemType
     */
    public static void ensureActive(final List<MetisListKey> pList,
                                    final MetisListKey pItemType) {
        /* Ensure that this list is registered as active */
        if (pList.contains(pItemType)) {
            pList.add(pItemType);
        }
    }

    /**
     * Prepare item for edit.
     * @param <T> the item type
     * @param pItemType the item type
     * @param pItem the item
     */
    public <T extends MetisFieldVersionedItem> void prepareItemForEdit(final MetisListKey pItemType,
                                                                       final T pItem) {
        /* Start editing */
        newVersion();

        /* Ensure that this list is registered as active */
        ensureActive(theVersionLists, pItemType);

        /* Start editing the item */
        if (pItem.getVersion() != theNewVersion) {
            pItem.pushHistory(theNewVersion);
        }
    }

    /**
     * Create a new item.
     * @param <T> the item type
     * @param pItemType the item type
     * @return the new item
     */
    public <T extends MetisFieldVersionedItem> T createNewItem(final MetisListKey pItemType) {
        /* Start editing */
        newVersion();

        /* Ensure that this list is registered as active */
        ensureActive(theVersionLists, pItemType);

        /* Access the list */
        final MetisListVersioned<T> myList = theListSet.getList(pItemType);

        /* Create the new item */
        final T myNew = myList.newListItem(myList.getNextId());

        /* Start editing */
        final MetisFieldVersionValues myValues = myNew.getValueSet();
        myValues.setVersion(theNewVersion);
        myNew.adjustState();

        /* Return the item */
        return myNew;
    }

    /**
     * Delete an item.
     * @param <T> the item type
     * @param pItemType the item type
     * @param pItem the item
     */
    public <T extends MetisFieldVersionedItem> void deleteItem(final MetisListKey pItemType,
                                                               final T pItem) {
        /* Start editing */
        prepareItemForEdit(pItemType, pItem);

        /* Set the item as deleted */
        pItem.getValueSet().setDeletion(true);
    }

    /**
     * Cancel the editVersion.
     */
    public void cancelVersion() {
        /* If we are currently editing */
        if (activeVersion()) {
            /* Loop through the lists */
            final Iterator<MetisListKey> myIterator = theVersionLists.iterator();
            while (myIterator.hasNext()) {
                final MetisListKey myKey = myIterator.next();
                final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

                /* Cancel the edit version */
                doCancelEditVersion(myList);
                myIterator.remove();
            }

            /* Reset the version */
            theNewVersion = -1;
        }
    }

    /**
     * Cancel EditVersion for a list.
     * @param <T> the item type
     * @param pList the list to cancel edits on
     */
    private <T extends MetisFieldVersionedItem> void doCancelEditVersion(final MetisListVersioned<T> pList) {
        /* Loop through the list */
        final Iterator<T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* If the item is being edited */
            if (myCurr.getVersion() == theNewVersion) {
                /* If the item is newly created */
                if (myCurr.getOriginalVersion() == theNewVersion) {
                    /* Remove from list */
                    myIterator.remove();

                    /* else just pop the history */
                } else {
                    myCurr.popTheHistory();
                }
            }
        }
    }

    /**
     * Commit the edit Version.
     */
    public void commitEditVersion() {
        /* If we are currently editing */
        if (activeVersion()) {
            /* Create new Change Details */
            final MetisListSetChange myChanges = new MetisListSetChange(MetisListEvent.UPDATE);

            /* Loop through the lists */
            final Iterator<MetisListKey> myIterator = theVersionLists.iterator();
            while (myIterator.hasNext()) {
                final MetisListKey myKey = myIterator.next();
                final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

                /* Remove the list from version lists */
                myIterator.remove();

                /* Commit the edit version */
                final MetisListChange<MetisFieldVersionedItem> myChange = doCommitEditVersion(myList);

                /* If there are changes */
                if (!myChange.isEmpty()) {
                    /* Add to the list of changes */
                    myChanges.registerChangedList(myChange);
                    ensureActive(theSessionLists, myKey);

                    /* Commit the edit version */
                    myList.setVersion(theNewVersion);
                }
            }

            /* Commit the edit version */
            theListSet.setVersion(theNewVersion);
            theNewVersion = -1;

            /* If there are changes */
            if (!myChanges.isEmpty()) {
                /* report the changes */
                theListSet.fireEvent(myChanges);
            }
        }
    }

    /**
     * Commit Edit Version.
     * @param <T> the item type
     * @param pList the list to cancel edits on
     * @return the change to report
     */
    private <T extends MetisFieldVersionedItem> MetisListChange<T> doCommitEditVersion(final MetisListVersioned<T> pList) {
        /* Create a new Change Detail */
        final MetisListChange<T> myChange = new MetisListChange<>(pList.getItemType(), MetisListEvent.UPDATE);

        /* Loop through the list */
        final Iterator<T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* If the item is being edited */
            if (myCurr.getVersion() == theNewVersion) {
                /* If the item is newly created */
                if (myCurr.getOriginalVersion() == theNewVersion) {
                    /* Register as a new item */
                    myChange.registerAdded(myCurr);

                    /* else if there is actually a change */
                } else if (myCurr.maybePopHistory()) {
                    /* Register as a changed item */
                    myChange.registerChanged(myCurr);
                }

                /* adjust the state */
                myCurr.adjustState();
            }
        }

        /* Return the change */
        return myChange;
    }

    /**
     * Commit the edit Session.
     */
    public void commitEditSession() {
        /* If we are currently editing */
        if (activeSession()) {
            /* Create new Change Details */
            final MetisListSetChange myChanges = new MetisListSetChange(MetisListEvent.UPDATE);
            final MetisListSetChange myBaseChanges = new MetisListSetChange(MetisListEvent.COMMIT);

            /* Determine the new version in base */
            theNewVersion = theListSet.getBaseListSet().getVersion() + 1;

            /* Loop through the lists */
            final Iterator<MetisListKey> myIterator = theSessionLists.iterator();
            while (myIterator.hasNext()) {
                final MetisListKey myKey = myIterator.next();
                final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

                /* Remove the list from session lists */
                myIterator.remove();

                /* Create new Change Details */
                final MetisListChange<MetisFieldVersionedItem> myChange = new MetisListChange<>(myKey, MetisListEvent.UPDATE);
                final MetisListChange<MetisFieldVersionedItem> myBaseChange = new MetisListChange<>(myKey, MetisListEvent.COMMIT);

                /* Commit the edit version */
                doCommitEditSession(myList, myChange, myBaseChange);

                /* If there are changes to the base list */
                if (!myBaseChange.isEmpty()) {
                    /* Update the version and record changes */
                    myList.getBaseList().setVersion(theNewVersion);
                    myBaseChanges.registerChangedList(myBaseChange);
                }

                /* If there are changes to the edit list */
                if (!myChange.isEmpty()) {
                    /* Record changes */
                    myChanges.registerChangedList(myChange);
                }
            }

            /* If there are changes */
            if (!myChanges.isEmpty()) {
                /* report the changes */
                theListSet.fireEvent(myChanges);
            }

            /* If there are changes */
            if (!myBaseChanges.isEmpty()) {
                /* report the changes */
                theListSet.getBaseListSet().fireEvent(myBaseChanges);
                theListSet.getBaseListSet().setVersion(theNewVersion);
            }

            /* Commit the edit version */
            theListSet.setVersion(0);
            theNewVersion = -1;
        }
    }

    /**
     * Commit Edit Version.
     * @param <T> the item type
     * @param pList the list
     * @param pChange the edit change
     * @param pBaseChange the base change
     */
    protected <T extends MetisFieldVersionedItem> void doCommitEditSession(final MetisListVersioned<T> pList,
                                                                           final MetisListChange<T> pChange,
                                                                           final MetisListChange<T> pBaseChange) {
        /* Loop through the list */
        final Iterator<T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* Switch on state */
            switch (myCurr.getState()) {
                case NEW:
                    handleCommitOfNewItem(pList, pChange, pBaseChange, myCurr);
                    break;
                case DELNEW:
                    handleCommitOfDelNewItem(pList, pChange, myCurr);
                    break;
                case CHANGED:
                case DELETED:
                case RECOVERED:
                    handleCommitOfChangedItem(pList, pChange, pBaseChange, myCurr);
                    break;
                default:
                    break;
            }
        }

        /* If there are changes to the base list */
        if (!pBaseChange.isEmpty()) {
            /* Update the version */
            pList.getBaseList().setVersion(theNewVersion);
        }

        /* Reset the version */
        pList.setVersion(0);
    }

    /**
     * handle new commit.
     * @param <T> the item type
     * @param pList the list
     * @param pChange the edit change
     * @param pBaseChange the base change
     * @param pItem the item
     */
    private <T extends MetisFieldVersionedItem> void handleCommitOfNewItem(final MetisListVersioned<T> pList,
                                                                           final MetisListChange<T> pChange,
                                                                           final MetisListChange<T> pBaseChange,
                                                                           final T pItem) {
        /* Obtain the base list */
        final MetisListVersioned<T> myBaseList = pList.getBaseList();

        /* Commit the item */
        final T myItem = newItemFromBase(myBaseList, pItem);
        myItem.getValueSet().setVersion(theNewVersion);
        myBaseList.add(myItem);

        /* Reset history on item */
        pItem.clearHistory();

        /* Add to the changes */
        pChange.registerChanged(pItem);
        pBaseChange.registerAdded(myItem);
    }

    /**
     * handle delNew commit.
     * @param <T> the item type
     * @param pList the list
     * @param pChange the edit change
     * @param pItem the item
     */
    private static <T extends MetisFieldVersionedItem> void handleCommitOfDelNewItem(final MetisListVersioned<T> pList,
                                                                                     final MetisListChange<T> pChange,
                                                                                     final T pItem) {
        /* Remove from the list and add to changes */
        pList.removeFromList(pItem);
        pChange.registerDeleted(pItem);
    }

    /**
     * handle changed commit.
     * @param <T> the item type
     * @param pList the list
     * @param pChange the edit change
     * @param pBaseChange the base change
     * @param pItem the item
     */
    private <T extends MetisFieldVersionedItem> void handleCommitOfChangedItem(final MetisListVersioned<T> pList,
                                                                               final MetisListChange<T> pChange,
                                                                               final MetisListChange<T> pBaseChange,
                                                                               final T pItem) {
        /* Obtain the base item */
        final MetisListVersioned<T> myBaseList = pList.getBaseList();
        final T myBase = myBaseList.getItemById(pItem.getIndexedId());

        /* Clear history in item */
        pItem.clearHistory();

        /* If there has really been a change in the item */
        if (!myBase.equals(pItem)) {
            /* Set values in changed item */
            myBase.pushHistory(theNewVersion);
            final MetisFieldVersionValues myBaseSet = myBase.getValueSet();
            final MetisFieldVersionValues mySet = pItem.getValueSet();
            myBaseSet.copyFrom(mySet);

            /* Add to the base changes */
            pBaseChange.registerChanged(myBase);
        }

        /* Add to the edit changes */
        pChange.registerChanged(pItem);
    }

    /**
     * Create a New item with same values as the base.
     * @param <T> the item type
     * @param pList the list
     * @param pBase the base item
     * @return the new item
     */
    private static <T extends MetisFieldVersionedItem> T newItemFromBase(final MetisListVersioned<T> pList,
                                                                         final T pBase) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pBase.getIndexedId());

        /* Access the valueSet */
        final MetisFieldVersionValues mySet = myNew.getValueSet();

        /* Obtain a clone of the value set as the base value */
        final MetisFieldVersionValues myBaseSet = pBase.getValueSet();
        mySet.copyFrom(myBaseSet);
        pBase.adjustState();

        /* Return the new item */
        return myNew;
    }

    /**
     * Reset the listSet to version zero.
     */
    public void reset() {
        /* Cancel any active version */
        cancelVersion();

        /* Clear sessionLists */
        theSessionLists.clear();

        /* Reset the list */
        MetisListBaseManager.reset(theListSet);
    }

    /**
     * ReWind the listSet to a particular version.
     * @param pVersion the version to reWind to
     */
    public void reWindToVersion(final int pVersion) {
        /* Check that the rewind version is valid */
        if (theListSet.getVersion() < pVersion
            || pVersion < 0) {
            throw new IllegalArgumentException("Invalid Version");
        }

        /* Cancel any active version */
        cancelVersion();

        /* Reset the list */
        MetisListBaseManager.reWindToVersion(theListSet, pVersion);
    }
}
