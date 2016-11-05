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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Difference List.
 * @param <T> the item type
 */
public class MetisDifferenceList<T extends MetisVersionedItem>
        extends MetisVersionedList<T> {
    /**
     * Constructor.
     * @param pClass the item type
     */
    protected MetisDifferenceList(final Class<T> pClass) {
        /* Initialise underlying class */
        super(MetisListType.DIFFERENCE, pClass);
    }

    /**
     * Generate difference items.
     * @param pNew the new list
     * @param pOld the old list
     */
    protected void deriveTheDifferences(final MetisBaseList<T> pNew,
                                        final MetisBaseList<T> pOld) {
        /* Access a copy of the idMap of the old list */
        Map<Integer, T> myOld = new HashMap<>(pOld.getIdMap());

        /* Loop through the new list */
        Iterator<T> myIterator = pNew.iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            T myCurr = myIterator.next();
            Integer myId = myCurr.getIndexedId();
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
            T myCurr = myIterator.next();
            T myItem = pNew.newDiffDeletedItem(myCurr);
            addToList(myItem);
        }
    }
}
