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
package net.sourceforge.joceanus.metis.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;

/**
 * Metis VersionedList Edit Session.
 */
public class MetisListEditSession
        implements MetisFieldItem, OceanusEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisListEditSession> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListEditSession.class);

    /*
     * Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_LISTSET, MetisListEditSession::getListSet);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE, MetisListEditSession::getBaseListSet);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListEditSession::getVersion);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_ERROR, MetisListEditSession::getError);
    }

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisListEvent> theEventManager;

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
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     * @param pListSet the listSet
     */
    public MetisListEditSession(final MetisListSetVersioned pListSet) {
        /* Only allowed for Edit ListSets */
        if (!MetisListSetType.EDIT.equals(pListSet.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* Store parameters */
        theListSet = pListSet;
        theNewVersion = -1;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the active lists */
        theSessionLists = new ArrayList<>();
        theVersionLists = new ArrayList<>();

        /* Listen to list events */
        final OceanusEventRegistrar<MetisListEvent> myRegistrar = theListSet.getEventRegistrar();
        myRegistrar.addEventListener(this::handleListSetEvent);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public OceanusEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the listSet.
     * @return the listSet
     */
    private MetisListSetVersioned getListSet() {
        return theListSet;
    }

    /**
     * Obtain the baseListSet.
     * @return the baseSet
     */
    private MetisListSetVersioned getBaseListSet() {
        return theListSet.getBaseListSet();
    }

    /**
     * Obtain the version.
     * @return the version
     */
    private Integer getVersion() {
        return theNewVersion;
    }

    /**
     * Obtain the error.
     * @return the error
     */
    public OceanusException getError() {
        return theError;
    }

    /**
     * Reset the error.
     */
    public void resetError() {
        theError = null;
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
     * Is the base session active?.
     * @return true/false
     */
    public boolean activeBaseSession() {
        return getBaseListSet().getVersion() != 0;
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
    private static void ensureActive(final List<MetisListKey> pList,
                                     final MetisListKey pItemType) {
        /* Ensure that this list is registered as active */
        if (!pList.contains(pItemType)) {
            pList.add(pItemType);
        }
    }

    /**
     * Obtain the relevant list.
     * @param <T> the item type
     * @param pListKey the list key
     * @return the list (or null)
     */
    public <T extends MetisFieldTableItem> MetisListIndexed<T> getList(final MetisListKey pListKey) {
        return theListSet.getIndexedList(pListKey);
    }

    /**
     * Set field for item.
     * @param <T> the item type
     * @param pItem the item
     * @param pField the field
     * @param pValue the value
     */
    public <T extends MetisFieldTableItem> void setFieldForItem(final T pItem,
                                                                final MetisFieldVersionedDef pField,
                                                                final Object pValue) {
        /* Make sure that item is a VersionedItem */
        final MetisFieldVersionedItem myItem = (MetisFieldVersionedItem) pItem;

        /* Reset any error */
        resetError();

        /* Protect against exceptions */
        try {
            /* Prepare the item for edit */
            prepareItemForEdit(myItem);

            /* Set the value */
            pField.setFieldValue(myItem, pValue);

            /* TODO autoCorrect the item */

            /* Commit the version */
            commitEditVersion();

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Store error and cancel version */
            theError = e;
            cancelVersion();
            theEventManager.fireEvent(MetisListEvent.ERROR, theError);
        }
    }

    /**
     * Prepare item for edit.
     * @param <T> the item type
     * @param pItem the item
     */
    private <T extends MetisFieldVersionedItem> void prepareItemForEdit(final T pItem) {
        /* Obtain the listKey */
        final MetisListKey myItemType = (MetisListKey) pItem.getItemType();

        /* Start editing */
        newVersion();

        /* Ensure that this list is registered as active */
        ensureActive(theVersionLists, myItemType);

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
        /* Protect against exceptions try S */

        /* Start editing */
        newVersion();

        /* Ensure that this list is registered as active */
        ensureActive(theVersionLists, pItemType);

        /* Access the list */
        final MetisListVersioned<T> myList = theListSet.getList(pItemType);

        /* Create the new item */
        final T myNew = myList.newListItem(myList.getNextId());

        /* Start editing */
        final MetisFieldVersionValues myValues = myNew.getValues();
        myValues.setVersion(theNewVersion);
        myNew.adjustState();

        /* TODO autoCorrect the item */

        /* Add the item to the list */
        myList.add(myNew);

        /* Commit the version */
        commitEditVersion();

        /* Return the item */
        return myNew;

        /* Handle exceptions
         E catch (OceanusException e) S
         Store error and cancel version
         theError = e;
         cancelVersion();
         theEventManager.fireEvent(MetisListEvent.ERROR, theError);
         return null;
         E */
    }

    /**
     * Delete an item.
     * @param <T> the item type
     * @param pItem the item
     */
    public <T extends MetisFieldVersionedItem> void deleteItem(final T pItem) {
        /* Reset any error */
        resetError();

        /* Start editing */
        prepareItemForEdit(pItem);

        /* Set the item as deleted */
        pItem.getValues().setDeletion(true);

        /* Commit the version */
        commitEditVersion();
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
                    myCurr.popHistory();
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
            final MetisListSetChange myChanges = new MetisListSetChange(theNewVersion);

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
                theEventManager.fireEvent(MetisListEvent.VERSION);
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
        final MetisListChange<T> myChange = new MetisListChange<>(pList.getItemType(), MetisListEvent.VERSION);

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
                    /* If deletion flag is unchanged */
                    if (myCurr.isDeleted()) {
                        /* Register as a hidden item */
                        myChange.registerHidden(myCurr);
                    } else {
                        /* Register as a changed item */
                        myChange.registerChanged(myCurr);
                    }
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
        /* If we have an active version */
        if (activeVersion()) {
            /* Commit the active version */
            commitEditVersion();
        }

        /* If we are currently editing */
        if (activeSession()) {
            /* Determine the new version in base */
            theNewVersion = theListSet.getBaseListSet().getVersion() + 1;

            /* Create new Change Details */
            final MetisListSetChange myChanges = new MetisListSetChange(MetisListEvent.UPDATE);
            final MetisListSetChange myBaseChanges = new MetisListSetChange(theNewVersion);

            /* Loop through the lists */
            final Iterator<MetisListKey> myIterator = theSessionLists.iterator();
            while (myIterator.hasNext()) {
                final MetisListKey myKey = myIterator.next();
                final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

                /* Remove the list from session lists */
                myIterator.remove();

                /* Create new Change Details */
                final MetisListChange<MetisFieldVersionedItem> myChange = new MetisListChange<>(myKey, MetisListEvent.VERSION);
                final MetisListChange<MetisFieldVersionedItem> myBaseChange = new MetisListChange<>(myKey, MetisListEvent.UPDATE);

                /* Commit the edit version */
                doCommitEditSession(myList, myChange, myBaseChange);

                /* If there are changes to the base list */
                if (!myBaseChange.isEmpty()) {
                    /* Update the version and record changes */
                    myList.getBaseList().setVersion(theNewVersion);
                    myBaseChange.setVersion(theNewVersion);
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
                    handleCommitOfDelNewItem(pList, theListSet, pChange, myCurr);
                    break;
                case CHANGED:
                case DELETED:
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
        myItem.getValues().setVersion(theNewVersion);
        myBaseList.add(myItem);
        myItem.adjustState();

        /* Ensure links are correct in base */
        MetisListEditManager.ensureLinks(pItem, theListSet.getBaseListSet());

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
     * @param pEditSet the edit ListSet
     * @param pChange the edit change
     * @param pItem the item
     */
    private static <T extends MetisFieldVersionedItem> void handleCommitOfDelNewItem(final MetisListVersioned<T> pList,
                                                                                     final MetisListSetVersioned pEditSet,
                                                                                     final MetisListChange<T> pChange,
                                                                                     final T pItem) {
        /* Remove from the list and add to changes */
        pList.removeFromList(pItem);
        pChange.registerDeleted(pItem);

        /* Cleanup after the deleted item */
        pEditSet.cleanupDeletedItem(pItem);
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
            final MetisFieldVersionValues myBaseSet = myBase.getValues();
            final MetisFieldVersionValues mySet = pItem.getValues();
            myBaseSet.copyFrom(mySet);
            myBase.adjustState();

            /* Add to the base changes */
            if (pItem.isDeleted()) {
                pBaseChange.registerHidden(myBase);
            } else {
                /* Ensure links are correct in base */
                MetisListEditManager.ensureLinks(pItem, theListSet.getBaseListSet());
                pBaseChange.registerChanged(myBase);
            }
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
        final MetisFieldVersionValues mySet = myNew.getValues();

        /* Obtain a clone of the value set as the base value */
        final MetisFieldVersionValues myBaseSet = pBase.getValues();
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
     * Undo the last change to the listSet.
     */
    public void undoLastChange() {
        /* Ignore if there are no changes */
        final int myVersion = theListSet.getVersion();
        if (myVersion == 0) {
            return;
        }

        /* Cancel any active version */
        cancelVersion();

        /* Reset the list */
        MetisListBaseManager.undoLastChange(theListSet);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = theSessionLists.iterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();
            final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

            /* If there are no longer any changes for the list */
            if (myList.getVersion() == 0) {
                /* Remove the list */
                myIterator.remove();
            }
        }
    }

    /**
     * Reset the base listSet to version zero.
     */
    public void resetBase() {
        /* If there is no active Session */
        if (!activeSession()) {
            /* Reset the base */
            MetisListBaseManager.reset(getBaseListSet());
        }
    }

    /**
     * Undo the last change to the base listSet.
     */
    public void undoLastBaseChange() {
        /* If there is no active Session */
        if (!activeSession()) {
            /* Reset the base */
            MetisListBaseManager.undoLastChange(getBaseListSet());
        }
    }

    /**
     * Handle listSetEvent.
     * @param pEvent the event
     */
    private void handleListSetEvent(final OceanusEvent<MetisListEvent> pEvent) {
        if (MetisListEvent.REFRESH.equals(pEvent.getEventId())) {
            handleRefreshEvent();
        } else {
            handleStandardEvent(pEvent);
        }
    }

    /**
     * Handle refresh Event.
     */
    private void handleRefreshEvent() {
        /* Create a new ListEvent */
        final MetisListChange<MetisFieldVersionedItem> myChange = new MetisListChange<>(null, MetisListEvent.REFRESH);

        /* Loop through the lists */
        final Iterator<MetisListVersioned<MetisFieldVersionedItem>> myIterator = theListSet.listIterator();
        while (myIterator.hasNext()) {
            final MetisListVersioned<MetisFieldVersionedItem> myList = myIterator.next();

            /* Fire a refresh event */
            myList.fireEvent(myChange);
        }
    }

    /**
     * Handle standard Event.
     * @param pEvent the event
     */
    private void handleStandardEvent(final OceanusEvent<MetisListEvent> pEvent) {
        /* Access the event */
        final MetisListSetChange myChanges = pEvent.getDetails(MetisListSetChange.class);

        /* Loop through the changes */
        final Iterator<MetisListChange<MetisFieldVersionedItem>> myIterator = myChanges.changeIterator();
        while (myIterator.hasNext()) {
            final MetisListChange<MetisFieldVersionedItem> myChange = myIterator.next();
            final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myChange.getItemType());

            /* Fire the event */
            myList.fireEvent(myChange);
        }
    }
}
