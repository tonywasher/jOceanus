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

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;

import java.util.Iterator;
import java.util.Map;

/**
 * Versioned List.
 * @param <T> the item type
 */
public class MetisListVersioned<T extends MetisFieldVersionedItem>
        extends MetisListIndexed<T>
        implements OceanusEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MetisListVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListVersioned.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_ITEMTYPE, MetisListVersioned::getItemType);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListVersioned::getVersion);
    }

    /**
     * The itemType.
     */
    private final MetisListKey theItemType;

    /**
     * The Event Manager.
     */
    private OceanusEventManager<MetisListEvent> theEventManager;

    /**
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pItemType the itemType
     */
    public MetisListVersioned(final MetisListKey pItemType) {
        theItemType = pItemType;
    }

    /**
     * Access the event manager.
     * @return the event manager.
     */
    private OceanusEventManager<MetisListEvent> getEventManager() {
        /* Access the event manager and create it if it does not exist */
        synchronized (this) {
            if (theEventManager == null) {
                theEventManager = new OceanusEventManager<>();
            }
        }
        return theEventManager;
    }

    @Override
    public OceanusEventRegistrar<MetisListEvent> getEventRegistrar() {
        return getEventManager().getEventRegistrar();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    public void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Obtain the itemType.
     * @return the itemType
     */
    public MetisListKey getItemType() {
        return theItemType;
    }

    /**
     * deriveChanges on version increment.
     * @return the changes
     */
    MetisListChange<T> deriveChangesOnIncrement() {
        /* Create the change */
        final MetisListChange<T> myChange = new MetisListChange<>(theItemType, MetisListEvent.INCREMENT);
        myChange.setVersion(theVersion);

        /* Loop through the items */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myItem = myIterator.next();

            /* Ignore items that are unchanged at this version */
            if (myItem.getVersion() != theVersion) {
                continue;
            }

            /* Register the change correctly */
            final MetisFieldVersionValues myLast = myItem.getValuesHistory().getLastValues();
            if (myLast == null) {
                myChange.registerAdded(myItem);
            } else if (myItem.isDeleted()) {
                myChange.registerDeleted(myItem);
            } else if (myLast.isDeletion()) {
                myChange.registerAdded(myItem);
            } else {
                myChange.registerChanged(myItem);
            }
        }

        /* Return the change if non-empty */
        return myChange.isEmpty() ? null : myChange;
    }

    /**
     * Rewind items to the required version.
     * @param pVersion the version to rewind to
     */
    void rewindToVersion(final int pVersion) {
        /* Loop through the elements */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* If the version is before required version */
            if (myCurr.getValues().getVersion() <= pVersion) {
                /* Ignore */
                continue;
            }

            /* If the item was created after the required version */
            if (myCurr.getOriginalValues().getVersion() > pVersion) {
                /* Remove from list */
                myIterator.remove();
                continue;
            }

            /* Adjust values */
            myCurr.rewindToVersion(pVersion);
        }

        /* Adjust version */
        setVersion(pVersion);
    }

    /**
     * Re-base the list against a database image. This method is used to re-synchronise between two
     * sources. Items that are in this list, but not in the base list will be viewed as inserted.
     * Items that are in the base list but not in this list will be viewed as deleted. Items
     * that are in both list but differ will be viewed as changed
     * @param pBase The base list to re-base on
     * @return are there any changes
     */
    boolean reBase(final MetisListVersioned<T> pBase) {
        /* Access an Id Map of the old list */
        final Map<Integer, T> myBase = pBase.copyIdMap();
        boolean bChanges = false;

        /* Loop through this list */
        final Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the base list */
            final T myCurr = myIterator.next();
            final MetisFieldVersionedItem myItem = myBase.get(myCurr.getIndexedId());

            /* If the underlying item does not exist */
            if (myItem == null) {
                /* Mark this as a new item */
                myCurr.getValues().setVersion(getVersion() + 1);
                bChanges = true;

                /* else the item exists in the old list */
            } else {
                /* if it has changed */
                if (!myCurr.equals(myItem)) {
                    /* Set correct history */
                    myCurr.setHistory(myItem.getValues());
                    bChanges = true;

                    /* else it is identical */
                } else {
                    /* Mark this as a clean item */
                    myCurr.clearHistory();
                }

                /* Remove the item from the base map */
                myBase.remove(myItem.getIndexedId());
            }
        }

        /* Loop through the remaining items in the base list */
        for (T myCurr : myBase.values()) {
            /* Access the item and remove it Obtain the new item */
            pBase.remove(myCurr);
            myCurr.pushHistory();
            myCurr.getValues().setDeletion(true);
            add(myCurr);
            bChanges = true;
        }

        /* Return flag */
        return bChanges;
    }

    /**
     * Fire event.
     * @param pEvent the event
     */
    public void fireEvent(final MetisListChange<T> pEvent) {
        /* If the change is non-empty */
        if (MetisListEvent.REFRESH.equals(pEvent.getEventType())
            || !pEvent.isEmpty()) {
            getEventManager().fireEvent(pEvent.getEventType(), pEvent);
        }
    }
}
