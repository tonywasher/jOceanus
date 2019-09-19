/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.list;

import java.util.Iterator;
import java.util.LinkedHashMap;
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
     * The version.
     */
    private final int theVersion;

    /**
     * The items that are being added.
     */
    private final Map<MetisListKey, MetisListChange<MetisFieldVersionedItem>> theChanges;

    /**
     * Constructor.
     * @param pEventType the event type
     */
    MetisListSetChange(final MetisListEvent pEventType) {
        this(pEventType, 0);
    }

    /**
     * Constructor.
     * @param pVersion the version
     */
    MetisListSetChange(final int pVersion) {
        this(MetisListEvent.VERSION, pVersion);
    }

    /**
     * Constructor.
     * @param pEventType the event type
     * @param pVersion the version
     */
    private MetisListSetChange(final MetisListEvent pEventType,
                               final int pVersion) {
        theEventType = pEventType;
        theVersion = pVersion;
        theChanges = new LinkedHashMap<>();
    }

    /**
     * Obtain the event type.
     * @return the event type
     */
    public MetisListEvent getEventType() {
        return theEventType;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
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
     * @param pChange the changes to the list
     */
    protected void registerChangedList(final MetisListChange<MetisFieldVersionedItem> pChange) {
        theChanges.put(pChange.getItemType(), pChange);
    }

    /**
     * Obtain the change iterator.
     * @return the iterator
     */
    public Iterator<MetisListChange<MetisFieldVersionedItem>> changeIterator() {
        return theChanges.values().iterator();
    }
}
