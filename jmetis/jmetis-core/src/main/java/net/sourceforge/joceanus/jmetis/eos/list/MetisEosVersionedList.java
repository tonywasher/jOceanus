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
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListResource;
import net.sourceforge.joceanus.jmetis.eos.list.MetisEosListChange.MetisEosListEvent;

/**
 * Versioned List implementation.
 * @param <T> the item type
 */
public class MetisEosVersionedList<T extends MetisFieldVersionedItem>
        extends MetisEosIndexedList<T> {
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
        // FIELD_DEFS.declareLocalField(MetisListResource.FIELD_CLASS,
        // MetisEosVersionedList::getClazz);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisEosVersionedList::getVersion);
    }

    /**
     * The listSet.
     */
    private final MetisEosVersionedListSet theListSet;

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
     * @param pConstructor the constructor
     */
    protected MetisEosVersionedList(final MetisEosVersionedListSet pListSet,
                                    final MetisEosItemType<T> pItemType) {
        /* Store parameters */
        theListSet = pListSet;
        theItemType = pItemType;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
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
     * Produce a new list of the same type.
     * @return the new list
     */
    // protected MetisEosVersionedList<T> newList() {
    // return new MetisEosVersionedList<>(theClazz, theConstructor);
    // }

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
     * Check reWind version.
     * @param pVersion the version to reWind to
     */
    protected void checkReWindVersion(final int pVersion) {
        /* Version must be less than current version and positive */
        if (theVersion < pVersion
            || pVersion < 0) {
            throw new IllegalArgumentException("Invalid Version");
        }
    }

    /**
     * ReWind the list to a particular version.
     * @param pVersion the version to reWind to
     */
    protected void doReWindToVersion(final int pVersion) {
        /* Create a new Change Detail */
        final MetisEosListChange<T> myChange = new MetisEosListChange<>(MetisEosListEvent.REWIND);

        /* Note maximum version */
        int myMaxVersion = 0;

        /* Loop through the list */
        final Iterator<T> myIterator = iterator();
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

        /* Fire the event */
        fireEvent(myChange);

        /* Set the version */
        theVersion = myMaxVersion;
    }

    /**
     * Create a New item representing a deletion.
     * @param pBase the base item
     * @return the new item
     */
    protected T newDiffDeletedItem(final T pBase) {
        /* Obtain a new item */
        final T myNew = newListItem(pBase.getIndexedId());

        /* Obtain a deleted values set as the current value */
        MetisFieldVersionValues myBaseSet = pBase.getValueSet();
        final MetisFieldVersionValues mySet = myBaseSet.cloneIt();
        mySet.setDeletion(true);

        /* Obtain an undeleted set as the base value */
        myBaseSet = mySet.cloneIt();
        myBaseSet.setDeletion(false);

        /* Record as the history of the item */
        myNew.setValues(mySet);
        myNew.setHistory(myBaseSet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "changed" item for a difference list.
     * @param pCurr the current item
     * @param pBase the base item
     * @return the new item
     */
    protected T newDiffChangedItem(final T pCurr,
                                   final T pBase) {
        /* Obtain a new item */
        final T myNew = newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisFieldVersionValues mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the value set as the base value */
        MetisFieldVersionValues myBaseSet = pBase.getValueSet();
        myBaseSet = myBaseSet.cloneIt();

        /* Record as the history of the item */
        myNew.setValues(mySet);
        myNew.setHistory(myBaseSet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "added" item for an update/difference list.
     * @param pCurr the current item
     * @return the new item
     */
    protected T newDiffAddedItem(final T pCurr) {
        /* Obtain a new item */
        final T myNew = newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisFieldVersionValues mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();
        mySet.setVersion(1);

        /* Record as the history of the item */
        myNew.setValues(mySet);

        /* Return the new item */
        return myNew;
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
