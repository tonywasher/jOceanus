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

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.metis.list.MetisListResource;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;

import java.security.InvalidParameterException;

/**
 * Versioned List implementation.
 * @param <T> the item type
 */
public class MetisLetheListVersioned<T extends MetisFieldVersionedItem>
        extends MetisListIndexed<T>
        implements OceanusEventProvider<MetisLetheListEvent> {
    /**
     * Prime for hashing.
     */
    protected static final int HASH_PRIME = 67;

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MetisLetheListVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisLetheListVersioned.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_ITEMTYPE, MetisLetheListVersioned::getItemType);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisLetheListVersioned::getVersion);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE, MetisLetheListVersioned::getBaseList);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_LISTSET, MetisLetheListVersioned::getListSet);
    }

    /**
     * The listSet.
     */
    private final MetisListSetVersioned theListSet;

    /**
     * The Underlying list (if any).
     */
    private final MetisLetheListVersioned<T> theBaseList;

    /**
     * The itemType.
     */
    private final MetisLetheListKey theItemType;

    /**
     * The Event Manager.
     */
    private OceanusEventManager<MetisLetheListEvent> theEventManager;

    /**
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pItemType the itemType
     */
    protected MetisLetheListVersioned(final MetisListSetVersioned pListSet,
                                      final MetisLetheListKey pItemType) {
        this(pListSet, null, pItemType);
    }

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pBaseList the baseList
     */
    protected MetisLetheListVersioned(final MetisListSetVersioned pListSet,
                                      final MetisLetheListVersioned<T> pBaseList) {
        this(pListSet, pBaseList, pBaseList.getItemType());
    }

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pBaseList the baseList
     * @param pItemType the itemType
     */
    protected MetisLetheListVersioned(final MetisListSetVersioned pListSet,
                                      final MetisLetheListVersioned<T> pBaseList,
                                      final MetisLetheListKey pItemType) {
        /* Store parameters */
        theListSet = pListSet;
        theBaseList = pBaseList;
        theItemType = pItemType;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Access the event manager.
     * @return the event manager.
     */
    private OceanusEventManager<MetisLetheListEvent> getEventManager() {
        /* Access the event manager and create it if it does not exist */
        synchronized (this) {
            if (theEventManager == null) {
                theEventManager = new OceanusEventManager<>();
            }
        }
        return theEventManager;
    }

    @Override
    public OceanusEventRegistrar<MetisLetheListEvent> getEventRegistrar() {
        return getEventManager().getEventRegistrar();
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Obtain the listSet.
     * @return the listSet
     */
    private MetisListSetVersioned getListSet() {
        return theListSet;
    }

    /**
     * Obtain the baseList.
     * @return the baseList
     */
    public MetisLetheListVersioned<T> getBaseList() {
        return theBaseList;
    }

    /**
     * Obtain the itemType.
     * @return the itemType
     */
    public MetisLetheListKey getItemType() {
        return theItemType;
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    protected void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Cast List to correct type if possible.
     * @param pSource the source list
     * @return the correctly cast list list
     */
    @SuppressWarnings("unchecked")
    protected MetisLetheListVersioned<T> castList(final MetisLetheListVersioned<?> pSource) {
        /* Class must be the same */
        if (!theItemType.equals(pSource.getItemType())) {
            throw new InvalidParameterException("Inconsistent class");
        }

        /* Access as correctly cast list */
        return (MetisLetheListVersioned<T>) pSource;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof MetisLetheListVersioned)) {
            return false;
        }

        /* Cast as list */
        final MetisLetheListVersioned<?> myThat = (MetisLetheListVersioned<?>) pThat;

        /* Check local fields */
        if (theVersion != myThat.getVersion()
            || !theItemType.equals(myThat.getItemType())) {
            return false;
        }

        /* Pass call onwards */
        return super.equals(pThat);
    }

    @Override
    public int hashCode() {
        int myHash = super.hashCode();
        myHash *= HASH_PRIME;
        myHash += theItemType.hashCode();
        myHash *= HASH_PRIME;
        return myHash + theVersion;
    }

    /**
     * Create a New item for the list with the given id.
     * @param pId the id
     * @return the new item
     */
    public T newListItem(final Integer pId) {
        /* Determine the id */
        Integer myId = pId;
        if (myId == null || myId == 0) {
            myId = allocateNextId();
        } else {
            checkId(myId);
        }

        /* Create the item and initialise it */
        final T myItem = theItemType.newItem(theListSet);
        myItem.setIndexedId(myId);
        myItem.setItemType(theItemType);
        myItem.adjustState();

        /* Return the new id */
        return myItem;
    }
}
