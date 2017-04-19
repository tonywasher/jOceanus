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
package net.sourceforge.joceanus.jmetis.atlas.list;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionHistory;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisDataVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Edit List.
 * @param <T> the item type
 */
public class MetisEditList<T extends MetisDataVersionedItem>
        extends MetisVersionedList<T> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisEditList.class);

    /**
     * Bad update error.
     */
    private static final String ERROR_BADUPDATE = "Versioned List being updated";

    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisEditList.class.getSimpleName(), MetisVersionedList.getBaseFields());

    /**
     * Base Field Id.
     */
    private static final MetisDataField FIELD_BASE = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE.getValue());

    /**
     * EditVersion Field Id.
     */
    private static final MetisDataField FIELD_EDITVERSION = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_EDITVERSION.getValue());

    /**
     * The base list.
     */
    private final MetisBaseList<T> theBase;

    /**
     * The change report.
     */
    private MetisListChange<T> theChange;

    /**
     * The base change report.
     */
    private MetisListChange<T> theBaseChange;

    /**
     * The edit version of the list.
     */
    private int theEditVersion;

    /**
     * The new version of the base list.
     */
    private int theNewVersion;

    /**
     * Constructor.
     * @param pBase the base list
     */
    protected MetisEditList(final MetisBaseList<T> pBase) {
        /* Initialise underlying class */
        super(MetisListType.EDIT, pBase.getTheClass(), pBase.getItemFieldSet());

        /* Copy the comparator from the base list */
        setComparator(pBase.getComparator());

        /* Store base and initialise the edit list */
        theBase = pBase;
        deriveEdit();

        /* Register listeners on underlying class */
        TethysEventRegistrar<MetisListEvent> myRegistrar = theBase.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> deriveEdit());
        myRegistrar.addEventListener(MetisListEvent.REBASE, e -> deriveEdit());
        myRegistrar.addEventListener(MetisListEvent.REWIND, this::handleReWindOfBase);
        myRegistrar.addEventListener(MetisListEvent.UPDATE, this::handleUpdateOfBase);
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_BASE.equals(pField)) {
            return theBase.isEmpty()
                                     ? MetisDataFieldValue.SKIP
                                     : theBase;
        }
        if (FIELD_EDITVERSION.equals(pField)) {
            return theEditVersion != 0
                                       ? theEditVersion
                                       : MetisDataFieldValue.SKIP;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFields() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the base list.
     * @return the base list
     */
    protected MetisBaseList<T> getBaseList() {
        return theBase;
    }

    /**
     * Reset the list.
     */
    public void reset() {
        /* Commit any pending editVersion */
        commitEditVersion();

        /* If we have changes */
        if (getVersion() != 0) {
            /* ReWind to initial version */
            doReWindToVersion(0);
        }
    }

    /**
     * ReWind the list to a particular version.
     * @param pVersion the version to reWind to
     */
    public void reWindToVersion(final int pVersion) {
        /* Check that the rewind version is valid */
        checkReWindVersion(pVersion);

        /* Commit any pending editVersion */
        commitEditVersion();

        /* ReWind it */
        doReWindToVersion(pVersion);
    }

    /**
     * Is the list Editing?
     * @return true/false
     */
    public boolean isEditing() {
        return theEditVersion != 0;
    }

    /**
     * Start Edit Version.
     */
    public void startEditVersion() {
        /* Not supported for readOnly lists */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* If we are not currently editing */
        if (!isEditing()) {
            /* Set next edit version */
            startEditVersion(getVersion() + 1);
        }
    }

    /**
     * Do Start Edit Version.
     * @param pVersion the edit version
     */
    protected void startEditVersion(final int pVersion) {
        /* Set next edit version */
        theEditVersion = pVersion;
    }

    /**
     * Cancel Edit Version.
     */
    public void cancelEditVersion() {
        /* If we are currently editing */
        if (isEditing()) {
            /* Perform the cancel */
            doCancelEditVersion();
        }
    }

    /**
     * Cancel Edit Version.
     */
    protected void doCancelEditVersion() {
        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* If the item is being edited */
            MetisDataVersionHistory myHistory = myCurr.getVersionHistory();
            if (myHistory.getValueSet().getVersion() == theEditVersion) {
                /* If the item is newly created */
                if (myHistory.getOriginalValues().getVersion() == theEditVersion) {
                    /* Remove from list */
                    myIterator.remove();

                    /* else just pop the history */
                } else {
                    myHistory.popTheHistory();
                }
            }
        }

        /* Clear editing version */
        theEditVersion = 0;
    }

    /**
     * Commit Edit Version.
     */
    public void commitEditVersion() {
        /* If we are currently editing */
        if (isEditing()) {
            /* Perform the commit */
            doCommitEditVersion();
        }
    }

    /**
     * Commit Edit Version.
     */
    protected void doCommitEditVersion() {
        /* Create a new Change Detail */
        theChange = new MetisListChange<>(MetisListEvent.UPDATE);

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* If the item is being edited */
            MetisDataVersionHistory myHistory = myCurr.getVersionHistory();
            if (myHistory.getValueSet().getVersion() == theEditVersion) {
                /* If the item is newly created */
                if (myHistory.getOriginalValues().getVersion() == theEditVersion) {
                    /* Register as a new item */
                    theChange.registerAdded(myCurr);

                    /* else just register as changed item */
                } else {
                    theChange.registerChanged(myCurr);
                }

                /* adjust the state */
                myHistory.adjustState();
            }
        }

        /* Fire the event */
        fireEvent(theChange);
        theChange = null;

        /* Commit the edit version */
        setVersion(theEditVersion);
        theEditVersion = 0;
    }

    /**
     * Commit Edit Session.
     */
    public void commitEditSession() {
        /* Commit any pending version */
        commitEditVersion();

        /* If we have changes */
        if (getVersion() > 0) {
            /* Perform the commit */
            doCommitEditSession(theBase.getVersion() + 1);
        }
    }

    /**
     * Prepare item for edit.
     * @param pItem the item
     */
    public void prepareItemForEdit(final T pItem) {
        /* Start editing */
        startEditVersion();

        /* Start editing */
        MetisDataVersionHistory myHistory = pItem.getVersionHistory();
        if (myHistory.getValueSet().getVersion() != theEditVersion) {
            myHistory.pushHistory(theEditVersion);
        }
    }

    /**
     * Prepare item for edit.
     * @param pItem the item
     */
    protected void prepareItemForEdit(final Object pItem) {
        prepareItemForEdit(getTheClass().cast(pItem));
    }

    /**
     * Prepare item for edit.
     * @return the new item
     */
    public T createNewItem() {
        /* Start editing */
        startEditVersion();

        /* Create the new item */
        T myNew = newListItem(getNextId());

        /* Start editing */
        MetisDataVersionHistory myHistory = myNew.getVersionHistory();
        MetisDataVersionValues myValues = myHistory.getValueSet();
        myValues.setVersion(theEditVersion);
        myHistory.adjustState();

        /* Return the item */
        return myNew;
    }

    /**
     * Derive the edit list.
     */
    private void deriveEdit() {
        /* Clear the list */
        clear();

        /* Loop through the list */
        Iterator<T> myIterator = theBase.iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

            /* Create the new item */
            T myItem = newItemFromBase(myCurr);
            addToList(myItem);
        }

        /* Reset the version */
        setVersion(0);

        /* Report the refresh */
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.REFRESH);
        fireEvent(myChange);
    }

    /**
     * Commit Edit Version.
     * @param pNewVersion the new base version
     */
    protected void doCommitEditSession(final int pNewVersion) {
        /* Record the new version */
        theNewVersion = pNewVersion;

        /* Create a new Change Detail */
        theChange = new MetisListChange<>(MetisListEvent.UPDATE);
        theBaseChange = new MetisListChange<>(MetisListEvent.COMMIT);

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            MetisDataVersionHistory myHistory = myCurr.getVersionHistory();

            /* Switch on state */
            switch (myHistory.getState()) {
                case NEW:
                    handleCommitOfNewItem(myCurr);
                    break;
                case DELNEW:
                    handleCommitOfDelNewItem(myCurr);
                    break;
                case CHANGED:
                case DELETED:
                case RECOVERED:
                    handleCommitOfChangedItem(myCurr);
                    break;
                default:
                    break;
            }
        }

        /* If there are changes to the base list */
        if (!theBaseChange.isEmpty()) {
            /* Update the version */
            theBase.setVersion(theNewVersion);
        }

        /* Reset the version */
        setVersion(0);
        theEditVersion = 0;

        /* Fire the events */
        fireEvent(theChange);
        theBase.fireEvent(theBaseChange);

        /* Reset values */
        theChange = null;
        theBaseChange = null;
    }

    /**
     * handle delNew commit.
     * @param pItem the item
     */
    private void handleCommitOfNewItem(final T pItem) {
        /* Commit the item */
        T myItem = newItemFromBase(pItem);
        MetisDataVersionHistory myHistory = myItem.getVersionHistory();
        myHistory.getValueSet().setVersion(theNewVersion);
        theBase.addToList(myItem);

        /* Reset history on item */
        myHistory = pItem.getVersionHistory();
        myHistory.clearHistory();

        /* Add to the changes */
        theChange.registerChanged(pItem);
        theBaseChange.registerAdded(myItem);
    }

    /**
     * handle delNew commit.
     * @param pItem the item
     */
    private void handleCommitOfDelNewItem(final T pItem) {
        /* Remove from the list and add to changes */
        removeFromList(pItem);
        theChange.registerDeleted(pItem);
    }

    /**
     * handle changed commit.
     * @param pItem the item
     */
    private void handleCommitOfChangedItem(final T pItem) {
        /* Obtain the base item */
        T myBase = theBase.getItemById(pItem.getIndexedId());

        /* Clear history in item and obtain the valueSet */
        MetisDataVersionHistory myHistory = pItem.getVersionHistory();
        myHistory.clearHistory();
        MetisDataVersionValues mySet = myHistory.getValueSet();

        /* Set values in changed item */
        myHistory = myBase.getVersionHistory();
        myHistory.pushHistory(theNewVersion);
        MetisDataVersionValues myBaseSet = myHistory.getValueSet();
        myBaseSet.copyFrom(mySet);

        /* Add to the changes */
        theChange.registerChanged(pItem);
        theBaseChange.registerChanged(myBase);
    }

    /**
     * Create a New item with same values as the base.
     * @param pBase the base item
     * @return the new item
     */
    private T newItemFromBase(final T pBase) {
        /* Obtain a new item */
        T myNew = newListItem(pBase.getIndexedId());

        /* Access the valueSet */
        MetisDataVersionHistory myHistory = myNew.getVersionHistory();
        MetisDataVersionValues mySet = myHistory.getValueSet();

        /* Obtain a clone of the value set as the base value */
        myHistory = pBase.getVersionHistory();
        MetisDataVersionValues myBaseSet = myHistory.getValueSet();
        mySet.copyFrom(myBaseSet);
        myHistory.adjustState();

        /* Return the new item */
        return myNew;
    }

    /**
     * Handle reWind of Base.
     * <p>
     * This method is called when the underlying list has been reWound, and hence items may be
     * deleted or changed.
     * @param pChange the change
     */
    private void handleReWindOfBase(final TethysEvent<MetisListEvent> pChange) {
        /* List version must be 0 */
        if (getVersion() != 0) {
            LOGGER.error(ERROR_BADUPDATE);
        }

        /* Access the change detail */
        @SuppressWarnings("unchecked")
        MetisListChange<T> myBaseChange = (MetisListChange<T>) pChange.getDetails(MetisListChange.class);

        /* Create a new change list */
        theChange = new MetisListChange<>(MetisListEvent.UPDATE);

        /* Handle underlying deleted items */
        handleBaseDeletedItems(myBaseChange.deletedIterator());

        /* Handle base changed items */
        handleBaseChangedItems(myBaseChange.changedIterator());

        /* Fire the events and release the changes */
        fireEvent(theChange);
        theChange = null;
    }

    /**
     * Handle Update of Base.
     * <p>
     * This method is called when the underlying list has been committed to the dataBase, and hence
     * deleted items are permanently deleted.
     * @param pChange the change
     */
    private void handleUpdateOfBase(final TethysEvent<MetisListEvent> pChange) {
        /* List version must be 0 */
        if (getVersion() != 0) {
            LOGGER.error(ERROR_BADUPDATE);
        }

        /* Access the change detail */
        @SuppressWarnings("unchecked")
        MetisListChange<T> myBaseChange = (MetisListChange<T>) pChange.getDetails(MetisListChange.class);

        /* Create a new change list */
        theChange = new MetisListChange<>(MetisListEvent.UPDATE);

        /* Handle underlying deleted items */
        handleBaseDeletedItems(myBaseChange.deletedIterator());

        /* Fire the events and release the changes */
        fireEvent(theChange);
        theChange = null;
    }

    /**
     * Handle changed items in the base.
     * @param pIterator the iterator
     */
    private void handleBaseChangedItems(final Iterator<T> pIterator) {
        /* Loop through the changed items */
        while (pIterator.hasNext()) {
            T myCurr = pIterator.next();
            Integer myId = myCurr.getIndexedId();

            /* Obtain the item to be changed */
            T myItem = getItemById(myId);

            /* Access set to be changed */
            MetisDataVersionHistory myHistory = myItem.getVersionHistory();
            MetisDataVersionValues mySet = myHistory.getValueSet();

            /* Access base set */
            myHistory = myCurr.getVersionHistory();
            MetisDataVersionValues myBase = myHistory.getValueSet();

            /* Reset values in the item */
            mySet.copyFrom(myBase);

            /* Record change */
            theChange.registerChanged(myItem);
        }
    }

    /**
     * Handle Underlying deleted changes.
     * @param pIterator the iterator
     */
    private void handleBaseDeletedItems(final Iterator<Integer> pIterator) {
        /* Loop through the deleted items */
        while (pIterator.hasNext()) {
            Integer myId = pIterator.next();

            /* Obtain the item to be deleted */
            T myItem = getItemById(myId);
            removeFromList(myItem);

            /* Record deletion */
            theChange.registerDeleted(myItem);
        }
    }
}
