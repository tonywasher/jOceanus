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
package net.sourceforge.joceanus.jmetis.lethe.newlist;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.newlist.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.lethe.newlist.MetisUpdateList.MetisUpdatePhase;

/**
 * Set of UpdateLists.
 * @param <E> the list type identifier
 */
public class MetisUpdateListSet<E extends Enum<E>>
        extends MetisVersionedListSet<E, MetisUpdateList<MetisIndexedItem>> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisUpdateListSet.class.getSimpleName(), MetisVersionedListSet.getBaseFields());

    /**
     * Constructor.
     * @param pClass the enum class
     */
    protected MetisUpdateListSet(final Class<E> pClass) {
        super(MetisListType.UPDATE, pClass, FIELD_DEFS);
    }

    /**
     * Commit items.
     * @param pPhase the update phase
     * @param pNumItems the number of items to commit
     * @return the number of commit items remaining
     */
    public int commitUpdateBatch(final MetisUpdatePhase pPhase,
                                 final int pNumItems) {
        /* Access the number of items */
        int myNumItems = pNumItems;

        /* Loop through the lists */
        Iterator<MetisUpdateList<MetisIndexedItem>> myIterator = listIterator();
        while (myIterator.hasNext()) {
            MetisUpdateList<MetisIndexedItem> myList = myIterator.next();

            /* If the list is non-empty */
            if (!myList.isEmpty()) {
                /* Commit the items */
                myNumItems = myList.commitUpdateBatch(pPhase, pNumItems);

                /* Break loop if we have finished */
                if (myNumItems == 0) {
                    break;
                }
            }
        }

        /* Make sure that the version is correct */
        setVersion(isEmpty()
                             ? 0
                             : 1);

        /* return the remaining number of items */
        return myNumItems;
    }
}
