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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Base List implementation.
 * @param <T> the item type
 */
public class MetisVersionedList<T extends MetisVersionedItem>
        extends MetisIndexedList<T>
        implements MetisDataContents, TethysEventProvider<MetisListEvent> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisVersionedList.class);

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisVersionedList.class.getSimpleName());

    /**
     * Size Field Id.
     */
    private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

    /**
     * Version Field Id.
     */
    private static final MetisField FIELD_VERSION = FIELD_DEFS.declareLocalField("Version");

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisListEvent> theEventManager;

    /**
     * The class of the item.
     */
    private final Class<T> theClass;

    /**
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pClass the class of the item
     */
    protected MetisVersionedList(final Class<T> pClass) {
        this(new ArrayList<>(), pClass);
    }

    /**
     * Constructor.
     * @param pClass the class of the item
     * @param pList the list
     */
    protected MetisVersionedList(final List<T> pList,
                                 final Class<T> pClass) {
        /* Initialise underlying class */
        super(pList);
        theClass = pClass;
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_SIZE.equals(pField)) {
            return getSize();
        }
        if (FIELD_VERSION.equals(pField)) {
            return theVersion != 0
                                   ? theVersion
                                   : MetisFieldValue.SKIP;
        }
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName() + "(" + getSize() + ")";
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public TethysEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Obtain the class.
     * @return the class
     */
    public Class<T> getTheClass() {
        return theClass;
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    protected void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * ReBase the list.
     * @param pBase the base list
     */
    public void reBase(final MetisVersionedList<T> pBase) {
        /* Access a copy of the idMap of the base list */
        Map<Integer, T> myOld = new HashMap<>(pBase.getIdMap());
        boolean hasChanges = false;

        /* List versions must be 0 */
        if ((theVersion != 0)
            || (pBase.getVersion() != 0)) {
            LOGGER.error("Versioned List being reBased");
        }

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            T myCurr = myIterator.next();
            Integer myId = myCurr.getIndexedId();
            T myItem = myOld.get(myId);
            MetisValueSetHistory myHistory = myCurr.getValueSetHistory();

            /* If the item does not exist in the old list */
            if (myItem == null) {
                /* Set the version to 1 */
                myHistory.getValueSet().setVersion(1);
                hasChanges = true;

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* ReBase the history */
                    MetisValueSet myBase = myCurr.getValueSet().cloneIt();
                    myHistory.setHistory(myBase);
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
            T myCurr = myIterator.next();
            T myItem = newDeletedItem(myCurr);
            addToList(myItem);
            hasChanges = true;
        }

        /* Note changes */
        if (hasChanges) {
            theVersion = 1;
        }
    }

    /**
     * ReWind the list to a version.
     */
    public void reset() {
        reWindToVersion(0);
    }

    /**
     * ReWind the list to a version.
     * @param pVersion the version to reWind to
     */
    public void reWindToVersion(final int pVersion) {
        /* Version must be less than current version and positive */
        if ((theVersion < pVersion)
            || (pVersion < 0)) {
            throw new IllegalArgumentException("Invalid Version");
        }

        /* Create a new Change Detail */
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.REWIND);

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            MetisValueSetHistory myHistory = myCurr.getValueSetHistory();

            /* If the version is later than the required version */
            if (myHistory.getValueSet().getVersion() > pVersion) {
                /* If the item was created after the required version */
                if (myHistory.getOriginalValues().getVersion() > pVersion) {
                    /* Remove from list */
                    myIterator.remove();
                    myChange.registerDeleted(myCurr);
                    continue;
                }

                /* Loop while version is too high */
                while (myHistory.getValueSet().getVersion() > pVersion) {
                    /* Pop history */
                    myHistory.popTheHistory();
                }

                /* Register the change */
                myChange.registerChanged(myCurr);
            }
        }

        /* Fire the event */
        fireEvent(myChange);

        /* Set the version */
        theVersion = pVersion;
    }

    /**
     * Fire event.
     * @param pEvent the event
     */
    protected void fireEvent(final MetisListChange<T> pEvent) {
        /* If the change is non-empty */
        if (!pEvent.isEmpty()) {
            theEventManager.fireEvent(pEvent.getEventType(), pEvent);
        }
    }

    /**
     * Create a New "deleted" item for an update/difference/reBased list.
     * @param pBase the base item
     * @return the new item
     */
    protected T newDeletedItem(final T pBase) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Obtain a deleted values set as the current value */
        MetisValueSet myBase = pBase.getValueSet();
        MetisValueSet mySet = myBase.cloneIt();
        mySet.setDeletion(true);

        /* Obtain an undeleted set as the base value */
        myBase = mySet.cloneIt();
        myBase.setDeletion(false);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);
        myHistory.setHistory(myBase);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New "delNew" item for an update list.
     * @param pBase the base item
     * @return the new item
     */
    protected T newDelNewItem(final T pBase) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Obtain a deleted values set as the current value */
        MetisValueSet myBase = pBase.getValueSet();
        MetisValueSet mySet = myBase.cloneIt();
        mySet.setDeletion(true);
        mySet.setVersion(1);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New "changed" item for a difference list.
     * @param pCurr the current item
     * @param pBase the base item
     * @return the new item
     */
    protected T newChangedItem(final T pCurr,
                               final T pBase) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the value set as the base value */
        MetisValueSet myBase = pBase.getValueSet();
        myBase = myBase.cloneIt();

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);
        myHistory.setHistory(myBase);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New "changed" item for an update list.
     * @param pCurr the current item
     * @return the new item
     */
    protected T newChangedItem(final T pCurr) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the original value set as the base value */
        MetisValueSetHistory myHistory = pCurr.getValueSetHistory();
        MetisValueSet myBase = myHistory.getOriginalValues();
        myBase = myBase.cloneIt();

        /* Record as the history of the item */
        myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);
        myHistory.setHistory(myBase);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New "added" item for an update/difference list.
     * @param pCurr the current item
     * @return the new item
     */
    protected T newAddedItem(final T pCurr) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();
        mySet.setVersion(1);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New "added" item from an edit list.
     * @param pCurr the current item
     * @return the new item
     */
    protected T newCommittedItem(final T pCurr) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = myItem.getValueSet();
        mySet = mySet.cloneIt();
        mySet.copyFrom(pCurr.getValueSet());
        mySet.setVersion(theVersion + 1);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);

        /* Return the new item */
        return myItem;
    }

    /**
     * Commit new values from an edit list.
     * @param pNew the new item
     * @return the updated item
     */
    protected T newItemValues(final T pNew) {
        /* Obtain the item to be updated */
        Integer myId = pNew.getIndexedId();
        T myBase = getItemById(myId);

        /* Adjust the history */
        MetisValueSetHistory myHistory = myBase.getValueSetHistory();
        myHistory.pushHistory(theVersion + 1);
        MetisValueSet mySet = myBase.getValueSet();
        MetisValueSet myNew = pNew.getValueSet();
        mySet.copyFrom(myNew);

        /* Return the updated item */
        return myBase;
    }

    /**
     * Create a New "added" item.
     * @param pCurr the current item
     * @return the new item
     */
    protected T newCopyItem(final T pCurr) {
        /* Obtain a new item */
        T myItem = newItem();

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();
        mySet.setVersion(0);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New item.
     * @return the new item
     */
    protected T newItem() {
        /* Protect against exceptions */
        try {
            return theClass.newInstance();
        } catch (InstantiationException
                | IllegalAccessException e) {
            LOGGER.error("Failed to instantiate object", e);
            return null;
        }
    }
}
