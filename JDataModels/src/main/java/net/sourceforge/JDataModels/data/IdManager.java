/*******************************************************************************
 * JDataModels: Data models
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataModels.data;

import net.sourceforge.JSortedList.OrderedIdIndex;

/**
 * Id Manager for data list. Allocates new IDs and checks for uniqueness.
 * @author Tony Washer
 * @param <T> the dataType
 */
public class IdManager<T extends DataItem & Comparable<T>> extends OrderedIdIndex<Integer, T> {
    /**
     * The maximum id.
     */
    private int theMaxId = 0;

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
    protected int getMaxId() {
        return theMaxId;
    }

    /**
     * Set Max Id.
     * @param uMaxId the Maximum Id
     */
    protected void setMaxId(final int uMaxId) {
        if (uMaxId > theMaxId) {
            theMaxId = uMaxId;
        }
    }

    /**
     * Is the Id unique in this list.
     * @param uId the Id to check
     * @return Whether the id is unique <code>true/false</code>
     */
    protected boolean isIdUnique(final int uId) {
        /* Its unique if its unassigned or greater than the max id */
        if ((uId == 0) || (uId > theMaxId)) {
            return true;
        }

        /* Check in index */
        return !isIdPresent(uId);
    }

    /**
     * Generate/Record new id.
     * @param pItem the item
     */
    protected void setNewId(final DataItem pItem) {
        int myId = pItem.getId();

        /* If we need to generate a new id */
        if (myId == 0) {
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