/*
 * Metis: Java Data Framework
 * Copyright 2012-2026. Tony Washer
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

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Viewer Data Manager.
 */
public class MetisViewerManagerImpl
        implements MetisViewerManager {
    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisViewerEvent> theEventManager;

    /**
     * The Root List.
     */
    private final List<MetisViewerEntry> theRootList;

    /**
     * The Standard Entry Map.
     */
    private final Map<MetisViewerStandardEntry, MetisViewerEntry> theStdEntries;

    /**
     * The Next entryId.
     */
    private static final AtomicInteger NEXT_ENTRY_ID = new AtomicInteger(1);

    /**
     * The Focused Entry.
     */
    private MetisViewerEntry theFocused;

    /**
     * Constructor.
     */
    public MetisViewerManagerImpl() {
        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the root list */
        theRootList = new ArrayList<>();

        /* Create the standard entry map */
        theStdEntries = new EnumMap<>(MetisViewerStandardEntry.class);
        createStandardEntries();
    }

    @Override
    public OceanusEventRegistrar<MetisViewerEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public Iterator<MetisViewerEntry> rootIterator() {
        return theRootList.iterator();
    }

    @Override
    public MetisViewerEntry getFocus() {
        return theFocused;
    }

    @Override
    public void setFocus(final MetisViewerEntry pEntry) {
        /* If this is a change in focus */
        if (!pEntry.equals(theFocused)) {
            /* Record and report the change */
            theFocused = pEntry;
            fireEvent(MetisViewerEvent.FOCUS, pEntry);
        }
    }

    @Override
    public void fireEvent(final MetisViewerEvent pEventId,
                          final Object pValue) {
        theEventManager.fireEvent(pEventId, pValue);
    }

    @Override
    public int getNextId() {
        return NEXT_ENTRY_ID.getAndIncrement();
    }

    @Override
    public MetisViewerEntry newEntry(final String pName) {
        /* Create the entry and add to root List */
        final MetisViewerEntry myEntry = new MetisViewerEntryImpl(this, null, pName);
        theRootList.add(myEntry);
        fireEvent(MetisViewerEvent.ENTRY, myEntry);
        return myEntry;
    }

    @Override
    public MetisViewerEntry newEntry(final MetisViewerEntry pParent,
                                     final String pName) {
        /* Create the entry under the parent */
        final MetisViewerEntry myEntry = new MetisViewerEntryImpl(this, pParent, pName);
        fireEvent(MetisViewerEvent.ENTRY, myEntry);
        return myEntry;
    }

    /**
     * Create standard entries.
     */
    private void createStandardEntries() {
        /* Loop through the standard entries */
        for (MetisViewerStandardEntry myId : MetisViewerStandardEntry.values()) {
            /* Create invisible root entry and add to the map */
            final MetisViewerEntry myEntry = newEntry(myId.toString());
            myEntry.setVisible(false);
            theStdEntries.put(myId, myEntry);
        }
    }

    @Override
    public MetisViewerEntry getStandardEntry(final MetisViewerStandardEntry pEntry) {
        final MetisViewerEntry myEntry = theStdEntries.get(pEntry);
        myEntry.setVisible(true);
        return myEntry;
    }
}
