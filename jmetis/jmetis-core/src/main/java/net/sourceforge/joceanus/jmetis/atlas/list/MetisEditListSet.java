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

import net.sourceforge.joceanus.jmetis.atlas.list.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Set of EditLists.
 * @param <E> the list type identifier
 */
public class MetisEditListSet<E extends Enum<E>>
        extends MetisVersionedListSet<E, MetisEditList<MetisIndexedItem>>
        implements TethysEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisEditListSet.class.getSimpleName(), MetisVersionedListSet.getBaseFields());

    /**
     * EditVersion Field Id.
     */
    private static final MetisField FIELD_EDITVERSION = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_EDITVERSION.getValue());

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisListEvent> theEventManager;

    /**
     * The base list.
     */
    private final MetisBaseListSet<E> theBaseSet;

    /**
     * The edit version of the listSet.
     */
    private int theEditVersion;

    /**
     * Constructor.
     * @param pBase the baseSet
     */
    protected MetisEditListSet(final MetisBaseListSet<E> pBase) {
        super(MetisListType.EDIT, pBase.getEnumClass(), FIELD_DEFS);
        theBaseSet = pBase;
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_EDITVERSION.equals(pField)) {
            return theEditVersion != 0
                                       ? theEditVersion
                                       : MetisFieldValue.SKIP;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }

    @Override
    public TethysEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Reset the listSet.
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
     * ReWind the listSet to a particular version.
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
     * Is the listSet Editing?
     * @return true/false
     */
    public boolean isEditing() {
        return theEditVersion != 0;
    }

    /**
     * Start Edit Version.
     */
    public void startEditVersion() {
        /* If we are not currently editing */
        if (!isEditing()) {
            /* Set next edit version */
            theEditVersion = getVersion() + 1;
        }
    }

    /**
     * Cancel the edit Version.
     */
    public void cancelEditVersion() {
        /* If we are currently editing */
        if (isEditing()) {
            /* Loop through the lists */
            Iterator<MetisEditList<MetisIndexedItem>> myIterator = listIterator();
            while (myIterator.hasNext()) {
                MetisEditList<MetisIndexedItem> myList = myIterator.next();

                /* If the list is editing */
                if (myList.isEditing()) {
                    /* Cancel the edit version */
                    myList.doCancelEditVersion();
                }
            }

            /* Clear editing version */
            theEditVersion = 0;
        }
    }

    /**
     * Commit the edit Version.
     */
    public void commitEditVersion() {
        /* Commit the edit version */
        if (isEditing()) {
            /* Loop through the lists */
            Iterator<MetisEditList<MetisIndexedItem>> myIterator = listIterator();
            while (myIterator.hasNext()) {
                MetisEditList<MetisIndexedItem> myList = myIterator.next();

                /* If the list is editing */
                if (myList.isEditing()) {
                    /* Cancel the edit version */
                    myList.doCommitEditVersion();
                }
            }

            /* Commit the edit version */
            setVersion(theEditVersion);
            theEditVersion = 0;
        }
    }

    /**
     * Commit the edit Session.
     */
    public void commitEditSession() {
        /* If we have editing updates */
        if (getVersion() > 0) {
            /* Loop through the lists */
            Iterator<MetisEditList<MetisIndexedItem>> myIterator = listIterator();
            while (myIterator.hasNext()) {
                MetisEditList<MetisIndexedItem> myList = myIterator.next();

                /* Commit any pending version */
                myList.commitEditVersion();

                /* If the list is editing */
                if (myList.getVersion() > 0) {
                    /* Commit the edit session */
                    myList.doCommitEditSession(theEditVersion);
                }
            }

            /* Reset the version */
            setVersion(0);
            theEditVersion = 0;
        }
    }

    /**
     * Prepare item for edit.
     * @param pItemType the item type
     * @param pItem the item
     */
    public void prepareItemForEdit(final E pItemType,
                                   final Object pItem) {
        /* Start editing */
        startEditVersion();

        /* Obtain the list */
        MetisEditList<?> myList = getList(pItemType);
        myList.prepareItemForEdit(pItem);
    }

    @Override
    protected void declareList(final E pId,
                               final MetisEditList<MetisIndexedItem> pList) {
        /* Pass call through */
        super.declareList(pId, pList);

        /* Listen to Refresh of the list */
        TethysEventRegistrar<MetisListEvent> myRegistrar = pList.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> handleListRefresh(pList));
        myRegistrar.addEventListener(MetisListEvent.REWIND, this::handleListReWind);

        /* Obtain the base list */
        MetisBaseList<?> myBase = pList.getBaseList();
        myRegistrar = myBase.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.COMMIT, this::handleListCommit);
    }

    /**
     * Handle list Refresh.
     * @param pList the list
     */
    private void handleListRefresh(final MetisEditList<MetisIndexedItem> pList) {
        reLinkItems(pList.iterator());
    }

    /**
     * Handle list Rewind.
     * @param pChange the change
     */
    private void handleListReWind(final TethysEvent<MetisListEvent> pChange) {
        /* Access the change detail */
        @SuppressWarnings("unchecked")
        MetisListChange<MetisIndexedItem> myBaseChange = (MetisListChange<MetisIndexedItem>) pChange.getDetails(MetisListChange.class);

        /* ReLink Changed items */
        reLinkItems(myBaseChange.changedIterator());
    }

    /**
     * Handle list Commit.
     * @param pChange the change
     */
    private void handleListCommit(final TethysEvent<MetisListEvent> pChange) {
        /* Access the change detail */
        @SuppressWarnings("unchecked")
        MetisListChange<MetisIndexedItem> myBaseChange = (MetisListChange<MetisIndexedItem>) pChange.getDetails(MetisListChange.class);

        /* ReLink Added and Changed items */
        theBaseSet.reLinkItems(myBaseChange.addedIterator());
        theBaseSet.reLinkItems(myBaseChange.changedIterator());
    }
}
