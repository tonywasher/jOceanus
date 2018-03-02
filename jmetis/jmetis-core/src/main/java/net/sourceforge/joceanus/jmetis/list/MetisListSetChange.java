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
package net.sourceforge.joceanus.jmetis.list;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;

/**
 * Metis VersionedListSet Event.
 */
public class MetisListSetChange {
    /**
     * The event type.
     */
    private final MetisListEvent theEventType;

    /**
     * The items that are being added.
     */
    private final Map<MetisListKey, MetisListChange<? extends MetisFieldVersionedItem>> theChanges;

    /**
     * Constructor.
     * @param pEventType the event type
     */
    protected MetisListSetChange(final MetisListEvent pEventType) {
        theEventType = pEventType;
        theChanges = new HashMap<>();
    }

    /**
     * Obtain the event type.
     * @return the event type
     */
    public MetisListEvent getEventType() {
        return theEventType;
    }

    /**
     * Is this an empty changeSet?
     * @return true/false
     */
    public boolean isEmpty() {
        return theChanges.isEmpty();
    }

    /**
     * Obtain the change for the specified list.
     * @param <T> the item type
     * @param pItemType the item type
     * @return the change (or null)
     */
    @SuppressWarnings("unchecked")
    public <T extends MetisFieldVersionedItem> MetisListChange<T> getListChange(final MetisListKey pItemType) {
        return (MetisListChange<T>) theChanges.get(pItemType);
    }

    /**
     * Register changed list.
     * @param <T> the item type
     * @param pChange the changes to the list
     */
    protected <T extends MetisFieldVersionedItem> void registerChangedList(final MetisListChange<T> pChange) {
        theChanges.put(pChange.getItemType(), pChange);
    }
}