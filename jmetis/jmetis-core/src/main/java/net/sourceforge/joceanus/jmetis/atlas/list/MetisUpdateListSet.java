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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisUpdateList.MetisUpdatePhase;

/**
 * Set of UpdateLists.
 */
public final class MetisUpdateListSet
        extends MetisVersionedListSet {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisUpdateListSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisUpdateListSet.class);

    /**
     * Constructor.
     */
    protected MetisUpdateListSet() {
        super();
    }

    @Override
    public MetisFieldSet<MetisUpdateListSet> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MetisUpdateList<MetisFieldVersionedItem> getList(final MetisListKey pListKey) {
        return (MetisUpdateList<MetisFieldVersionedItem>) super.getList(pListKey);
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
        final Iterator<MetisListKey> myIterator = MetisUpdatePhase.DELETE.equals(pPhase)
                                                                                         ? reverseKeyIterator()
                                                                                         : keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* If the list is non-empty */
            final MetisUpdateList<MetisFieldVersionedItem> myList = getList(myKey);
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
