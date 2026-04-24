/*
 * Themis: Java Project Framework
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

package io.github.tonywasher.joceanus.themis.gui.stats;

import io.github.tonywasher.joceanus.themis.stats.ThemisStatsElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ThemisUIStatsEntry {
    /**
     * Entry name prefix.
     */
    private static final String ENTRY_PREFIX = "TreeItem";

    /**
     * The Next entryId.
     */
    private static final AtomicInteger NEXT_ENTRY_ID = new AtomicInteger(1);

    /**
     * The Parent.
     */
    private final ThemisUIStatsEntry theParent;

    /**
     * The id of the entry.
     */
    private final int theId;

    /**
     * The Child List.
     */
    private List<ThemisUIStatsEntry> theChildList;

    /**
     * The unique name of the entry.
     */
    private final String theUniqueName;

    /**
     * The display name of the entry.
     */
    private final String theDisplayName;

    /**
     * The object for the entry.
     */
    private final ThemisStatsElement theObject;

    /**
     * Constructor.
     *
     * @param pElement the statsElement
     */
    ThemisUIStatsEntry(final ThemisStatsElement pElement) {
        this(null, pElement);
    }

    /**
     * Constructor.
     *
     * @param pParent  the parent entry
     * @param pElement the statsElement
     */
    ThemisUIStatsEntry(final ThemisUIStatsEntry pParent,
                       final ThemisStatsElement pElement) {
        /* Store parameters */
        theParent = pParent;
        theObject = pElement;

        /* Allocate id and unique name */
        theId = NEXT_ENTRY_ID.getAndIncrement();
        theUniqueName = ENTRY_PREFIX + theId;
        theDisplayName = theObject.getName();

        /* If we have a parent */
        if (pParent != null) {
            /* Add the entry to the child list */
            pParent.addChild(this);
        }
    }

    /**
     * Get parent.
     *
     * @return the parent
     */
    ThemisUIStatsEntry getParent() {
        return theParent;
    }

    /**
     * Get unique name.
     *
     * @return the name
     */
    String getUniqueName() {
        return theUniqueName;
    }

    /**
     * Get object.
     *
     * @return the object
     */
    ThemisStatsElement getObject() {
        return theObject;
    }

    @Override
    public String toString() {
        return theDisplayName;
    }

    /**
     * Get child iterator.
     *
     * @return the iterator
     */
    Iterator<ThemisUIStatsEntry> childIterator() {
        return theChildList == null
                ? Collections.emptyIterator()
                : theChildList.iterator();
    }

    /**
     * Add child.
     *
     * @param pChild the child to add
     */
    private void addChild(final ThemisUIStatsEntry pChild) {
        if (theChildList == null) {
            theChildList = new ArrayList<>();
        }
        theChildList.add(pChild);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof ThemisUIStatsEntry myThat)) {
            return false;
        }

        /* Must have same id */
        return theId == myThat.theId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(theId);
    }
}
