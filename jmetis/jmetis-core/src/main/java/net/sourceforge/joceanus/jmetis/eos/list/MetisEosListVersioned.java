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
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Versioned List implementation.
 * @param <T> the item type
 */
public class MetisEosListVersioned<T extends MetisFieldVersionedItem>
        extends MetisEosListIndexed<T>
        implements TethysEventProvider<MetisEosListEvent> {
    /**
     * Prime for hashing.
     */
    protected static final int HASH_PRIME = 67;

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MetisEosListVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisEosListVersioned.class);

    /**
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_CLASS, MetisEosListVersioned::getItemType);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisEosListVersioned::getVersion);
    }

    /**
     * The listSet.
     */
    private final MetisEosListSetVersioned theListSet;

    /**
     * The Underlying list (if any).
     */
    private final MetisEosListVersioned<T> theBaseList;

    /**
     * The itemType.
     */
    private final MetisEosListKey theItemType;

    /**
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pItemType the itemType
     */
    protected MetisEosListVersioned(final MetisEosListSetVersioned pListSet,
                                    final MetisEosListKey pItemType) {
        this(pListSet, null, pItemType);
    }

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pBaseList the baseList
     */
    protected MetisEosListVersioned(final MetisEosListSetVersioned pListSet,
                                    final MetisEosListVersioned<T> pBaseList) {
        this(pListSet, pBaseList, pBaseList.getItemType());
    }

    /**
     * Constructor.
     * @param pListSet the listSet
     * @param pBaseList the baseList
     * @param pItemType the itemType
     */
    protected MetisEosListVersioned(final MetisEosListSetVersioned pListSet,
                                    final MetisEosListVersioned<T> pBaseList,
                                    final MetisEosListKey pItemType) {
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
    public MetisEosListVersioned<T> getBaseList() {
        return theBaseList;
    }

    /**
     * Obtain the itemType.
     * @return the itemType
     */
    public MetisEosListKey getItemType() {
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
    protected MetisEosListVersioned<T> castList(final MetisEosListVersioned<?> pSource) {
        /* Class must be the same */
        if (!theItemType.equals(pSource.getItemType())) {
            throw new InvalidParameterException("Inconsistent class");
        }

        /* Access as correctly cast list */
        return (MetisEosListVersioned<T>) pSource;
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
        if (!(pThat instanceof MetisEosListVersioned)) {
            return false;
        }

        /* Cast as list */
        final MetisEosListVersioned<?> myThat = (MetisEosListVersioned<?>) pThat;

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
    protected T newListItem(final Integer pId) {
        final T myItem = theItemType.newItem(theListSet);
        myItem.setIndexedId(pId);
        return myItem;
    }
}
