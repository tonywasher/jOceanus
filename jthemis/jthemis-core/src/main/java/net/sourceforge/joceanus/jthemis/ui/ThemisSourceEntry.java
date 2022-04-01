/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisElement;

/**
 * Source Panel Tree Entry.
 */
public class ThemisSourceEntry {
    /**
     * Entry name prefix.
     */
    private static final String ENTRY_PREFIX = "TreeItem";

    /**
     * The Parent.
     */
    private final ThemisSourceEntry theParent;

    /**
     * The id of the entry.
     */
    private final int theId;

    /**
     * The Header List.
     */
    private List<ThemisSourceEntry> theHeaderList;

    /**
     * The Child List.
     */
    private List<ThemisSourceEntry> theChildList;

    /**
     * The Trailer List.
     */
    private List<ThemisSourceEntry> theTrailerList;

    /**
     * The unique name of the entry.
     */
    private final String theUniqueName;

    /**
     * The name of the entry.
     */
    private final String theDisplayName;

    /**
     * The object for the entry.
     */
    private final ThemisAnalysisElement theObject;

    /**
     * Constructor.
     * @param pElement the sourceElement
     */
    ThemisSourceEntry(final ThemisAnalysisElement pElement) {
        this(null, pElement);
    }

    /**
     * Constructor.
     * @param pParent the parent entry
     * @param pElement the sourceElement
     */
    ThemisSourceEntry(final ThemisSourceEntry pParent,
                      final ThemisAnalysisElement pElement) {
        /* Store parameters */
        theParent = pParent;
        theObject = pElement;

        /* Allocate id and unique name */
        theId = ThemisSourcePanel.getNextId();
        theUniqueName = ENTRY_PREFIX + theId;
        theDisplayName = pElement.toString();

        /* If we have a parent */
        if (pParent != null) {
            /* Add the entry to the child list */
            pParent.addChild(this);
        }
    }

    /**
     * Get parent.
     * @return the parent
     */
    ThemisSourceEntry getParent() {
        return theParent;
    }

    /**
     * Get unique name.
     * @return the name
     */
    String getUniqueName() {
        return theUniqueName;
    }

    /**
     * Get object.
     * @return the object
     */
    ThemisAnalysisElement getObject() {
        return theObject;
    }

    @Override
    public String toString() {
        return theDisplayName;
    }

    /**
     * Get child iterator.
     * @return the iterator
     */
    Iterator<ThemisSourceEntry> childIterator() {
        return theChildList == null
                ? Collections.emptyIterator()
                : theChildList.iterator();
    }

    /**
     * Add header.
     * @param pHeader the header to add
     */
    private void addHeader(final ThemisSourceEntry pHeader) {
        if (theHeaderList == null) {
            theHeaderList = new ArrayList<>();
        }
        theHeaderList.add(pHeader);
    }

    /**
     * Add child.
     * @param pChild the child to add
     */
    private void addChild(final ThemisSourceEntry pChild) {
        if (theChildList == null) {
            theChildList = new ArrayList<>();
        }
        theChildList.add(pChild);
    }

    /**
     * Add trailer.
     * @param pTrailer the trailer to add
     */
    private void addTrailer(final ThemisSourceEntry pTrailer) {
        if (theTrailerList == null) {
            theTrailerList = new ArrayList<>();
        }
        theTrailerList.add(pTrailer);
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
        if (!(pThat instanceof ThemisSourceEntry)) {
            return false;
        }

        /* Access as ThemisSourceEntry */
        final ThemisSourceEntry myThat = (ThemisSourceEntry) pThat;

        /* Must have same id */
        return theId == myThat.theId;
    }

    @Override
    public int hashCode() {
        return theId;
    }
}
