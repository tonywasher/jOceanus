/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.data;

import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdIndex;

/**
 * Id Manager for data list. Allocates new IDs and checks for uniqueness.
 * @author Tony Washer
 * @param <T> the dataType
 * @param <E> the data type enum class
 */
public class IdManager<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends MetisOrderedIdIndex<Integer, T> {
    /**
     * The maximum id.
     */
    private Integer theMaxId = 0;

    /**
     * Constructor.
     * @param pGranularity the index granularity
     */
    protected IdManager(final int pGranularity) {
        super(pGranularity);
    }

    /**
     * Get Max Id.
     * @return the Maximum Id
     */
    protected Integer getMaxId() {
        return theMaxId;
    }

    /**
     * Set Max Id.
     * @param uMaxId the Maximum Id
     */
    protected void setMaxId(final Integer uMaxId) {
        if (uMaxId > theMaxId) {
            theMaxId = uMaxId;
        }
    }

    /**
     * Is the Id unique in this list.
     * @param uId the Id to check
     * @return Whether the id is unique <code>true/false</code>
     */
    protected boolean isIdUnique(final Integer uId) {
        /* Its unique if its unassigned or greater than the max id */
        if ((uId == null) || (uId == 0) || (uId > theMaxId)) {
            return true;
        }

        /* Check in index */
        return !isIdPresent(uId);
    }

    /**
     * Generate/Record new id.
     * @param pItem the item
     */
    protected void setNewId(final DataItem<E> pItem) {
        Integer myId = pItem.getId();

        /* If we need to generate a new id */
        if ((myId == null) || (myId == 0)) {
            /* Increment and use the max Id */
            theMaxId++;
            pItem.setId(theMaxId);

            /* else id is already known */
        } else {
            /* Update the max Id if required */
            if (theMaxId < myId) {
                theMaxId = myId;
            }
        }
    }

    @Override
    public void clear() {
        /* Call super-class */
        super.clear();

        /* Reset the maximum id */
        theMaxId = 0;
    }
}
