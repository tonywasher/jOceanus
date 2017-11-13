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
package net.sourceforge.joceanus.jmetis.atlas.list;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosVersionedItem;

/**
 * Difference List.
 * @param <T> the item type
 */
public final class MetisDifferenceList<T extends MetisDataEosVersionedItem>
        extends MetisVersionedList<T> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisDataEosFieldSet<MetisDifferenceList> FIELD_DEFS = MetisDataEosFieldSet.newFieldSet(MetisDifferenceList.class);

    /**
     * Constructor.
     * @param pClass the item type
     */
    protected MetisDifferenceList(final Class<T> pClass) {
        /* Initialise underlying class */
        super(pClass);
    }

    @Override
    public MetisDataEosFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Generate difference items.
     * @param pNew the new list
     * @param pOld the old list
     */
    protected void deriveTheDifferences(final MetisBaseList<T> pNew,
                                        final MetisBaseList<T> pOld) {
        /* Access a copy of the idMap of the old list */
        final Map<Integer, T> myOld = new HashMap<>(pOld.getIdMap());

        /* Set the sort comparator */
        setComparator(pNew.getComparator());

        /* Loop through the new list */
        Iterator<T> myIterator = pNew.iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            final T myCurr = myIterator.next();
            final Integer myId = myCurr.getIndexedId();
            T myItem = myOld.get(myId);

            /* If the item does not exist in the old list */
            if (myItem == null) {
                /* Insert a new item */
                myItem = pNew.newDiffAddedItem(myCurr);
                addToList(myItem);

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* Copy the item */
                    myItem = pNew.newDiffChangedItem(myCurr, myItem);
                    addToList(myItem);
                }

                /* Remove the item from the map */
                myOld.remove(myId);
            }
        }

        /* Loop through the remaining items in the old list */
        myIterator = myOld.values().iterator();
        while (myIterator.hasNext()) {
            /* Insert a new item */
            final T myCurr = myIterator.next();
            final T myItem = pNew.newDiffDeletedItem(myCurr);
            addToList(myItem);
        }

        /* Make sure that the version is correct */
        setVersion(isEmpty()
                             ? 0
                             : 1);

        /* Sort the list */
        sortList();
    }
}
