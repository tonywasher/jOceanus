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

import java.security.InvalidParameterException;

import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListResource;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Versioned List implementation.
 * @param <T> the item type
 */
public class MetisEosVersionedList<T extends MetisFieldVersionedItem>
        extends MetisEosIndexedList<T>
        implements TethysEventProvider<MetisEosListEvent> {
    /**
     * Prime for hashing.
     */
    protected static final int HASH_PRIME = 67;

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MetisEosVersionedList> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisEosVersionedList.class);

    /**
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_CLASS, MetisEosVersionedList::getItemType);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisEosVersionedList::getVersion);
    }

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisEosListEvent> theEventManager;

    /**
     * The listSet.
     */
    private final MetisEosVersionedListSet theListSet;

    /**
     * The Underlying list (if any).
     */
    private final MetisEosVersionedList<T> theBaseList;

    /**
     * The itemType.
     */
    private final MetisEosItemType<T> theItemType;

    /**
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pItemType the itemType
     */
    protected MetisEosVersionedList(final MetisEosVersionedListSet pListSet,
                                    final MetisEosItemType<T> pItemType) {
        this(pListSet, null, pItemType);
    }

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pBaseList the baseList
     */
    protected MetisEosVersionedList(final MetisEosVersionedListSet pListSet,
                                    final MetisEosVersionedList<T> pBaseList) {
        this(pListSet, pBaseList, pBaseList.getItemType());
    }

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pBaseList the baseList
     * @param pItemType the itemType
     */
    protected MetisEosVersionedList(final MetisEosVersionedListSet pListSet,
                                    final MetisEosVersionedList<T> pBaseList,
                                    final MetisEosItemType<T> pItemType) {
        /* Store parameters */
        theListSet = pListSet;
        theBaseList = pBaseList;
        theItemType = pItemType;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public TethysEventRegistrar<MetisEosListEvent> getEventRegistrar() {
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
     * Obtain the baseList.
     * @return the baseList
     */
    public MetisEosVersionedList<T> getBaseList() {
        return theBaseList;
    }

    /**
     * Obtain the itemType.
     * @return the itemType
     */
    public MetisEosItemType<T> getItemType() {
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
    protected MetisEosVersionedList<T> castList(final MetisEosVersionedList<?> pSource) {
        /* Class must be the same */
        if (!theItemType.equals(pSource.getItemType())) {
            throw new InvalidParameterException("Inconsistent class");
        }

        /* Access as correctly cast list */
        return (MetisEosVersionedList<T>) pSource;
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
        if (!(pThat instanceof MetisEosVersionedList)) {
            return false;
        }

        /* Cast as list */
        final MetisEosVersionedList<?> myThat = (MetisEosVersionedList<?>) pThat;

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
     * Fire event.
     * @param pEvent the event
     */
    protected void fireEvent(final MetisEosListChange<T> pEvent) {
        /* If the change is non-empty */
        if (MetisEosListEvent.REFRESH.equals(pEvent.getEventType())
            || !pEvent.isEmpty()) {
            theEventManager.fireEvent(pEvent.getEventType(), pEvent);
        }
    }

    /**
     * Create a New item for the list with the given id.
     * @param pId the id
     * @return the new item
     */
    protected T newListItem(final Integer pId) {
        final T myItem = theItemType.newItem(theListSet);
        myItem.setIndexedId(pId);
        return myItem;
    }
}
