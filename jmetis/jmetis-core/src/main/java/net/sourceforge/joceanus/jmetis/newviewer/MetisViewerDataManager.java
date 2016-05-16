/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.newviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Viewer Data Manager.
 */
public class MetisViewerDataManager
        implements TethysEventProvider<MetisViewerEvent> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisViewerEvent> theEventManager;

    /**
     * The Root List.
     */
    private final List<MetisViewerEntry> theRootList;

    /**
     * The Next entryId.
     */
    private static AtomicInteger theNextEntryId = new AtomicInteger(1);

    /**
     * The Focused Entry.
     */
    private MetisViewerEntry theFocused;

    /**
     * Constructor.
     */
    public MetisViewerDataManager() {
        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the root list */
        theRootList = new ArrayList<>();
    }

    @Override
    public TethysEventRegistrar<MetisViewerEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Get root iterator.
     * @return the iterator
     */
    public Iterator<MetisViewerEntry> rootIterator() {
        return theRootList.iterator();
    }

    /**
     * Set focused entry.
     * @param pEntry the entry to focus on
     */
    protected void setFocus(final MetisViewerEntry pEntry) {
        /* If this is a change in focus */
        if (!pEntry.equals(theFocused)) {
            /* Record and report the change */
            theFocused = pEntry;
            fireEvent(MetisViewerEvent.FOCUS, pEntry);
        }
    }

    /**
     * Fire event.
     * @param pEventId the eventId
     * @param pValue the relevant value
     */
    protected void fireEvent(final MetisViewerEvent pEventId,
                             final Object pValue) {
        theEventManager.fireEvent(pEventId, pValue);
    }

    /**
     * Get NextId.
     * @return the next id
     */
    protected int getNextId() {
        return theNextEntryId.getAndIncrement();
    }

    /**
     * Create a new root entry.
     * @param pName the name of the new entry
     * @return the new entry
     */
    public MetisViewerEntry newEntry(final String pName) {
        /* Create the entry and add to root List */
        MetisViewerEntry myEntry = new MetisViewerEntry(this, null, pName);
        theRootList.add(myEntry);
        return myEntry;
    }

    /**
     * Create a new entry under parent.
     * @param pParent the parent entry
     * @param pName the name of the new entry
     * @return the new entry
     */
    public MetisViewerEntry newEntry(final MetisViewerEntry pParent,
                                     final String pName) {
        /* Create the entry under the parent */
        return new MetisViewerEntry(this, pParent, pName);
    }
}
