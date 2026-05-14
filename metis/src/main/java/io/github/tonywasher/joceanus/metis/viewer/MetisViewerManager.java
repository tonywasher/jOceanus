/*
 * Metis: Java Data Framework
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.metis.viewer;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;

import java.util.Iterator;

/**
 * Viewer Data Manager.
 */
public interface MetisViewerManager
        extends OceanusEventProvider<MetisViewerEvent> {
    /**
     * Set focused entry.
     *
     * @param pEntry the entry to focus on
     */
    void setFocus(MetisViewerEntry pEntry);

    /**
     * Get focused entry.
     *
     * @return the focused entry
     */
    MetisViewerEntry getFocus();

    /**
     * Fire event.
     *
     * @param pEventId the eventId
     * @param pValue   the relevant value
     */
    void fireEvent(MetisViewerEvent pEventId,
                   Object pValue);

    /**
     * Get NextId.
     *
     * @return the next id
     */
    int getNextId();

    /**
     * Obtain standard entry.
     *
     * @param pEntry the standard entry id
     * @return the viewer entry
     */
    MetisViewerEntry getStandardEntry(MetisViewerStandardEntry pEntry);

    /**
     * Create a new entry.
     *
     * @param pName the name of the new entry
     * @return the new entry
     */
    MetisViewerEntry newEntry(String pName);

    /**
     * Create a new entry under parent.
     *
     * @param pParent the parent entry
     * @param pName   the name of the new entry
     * @return the new entry
     */
    MetisViewerEntry newEntry(MetisViewerEntry pParent,
                              String pName);

    /**
     * Get root iterator.
     *
     * @return the iterator
     */
    Iterator<MetisViewerEntry> rootIterator();
}
