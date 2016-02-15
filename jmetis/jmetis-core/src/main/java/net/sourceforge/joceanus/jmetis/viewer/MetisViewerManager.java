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
package net.sourceforge.joceanus.jmetis.viewer;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;

/**
 * Data Manager.
 * @author Tony Washer
 */
public abstract class MetisViewerManager {
    /**
     * The title.
     */
    private static final String WINDOW_TITLE = MetisViewerResource.VIEWER_TITLE.getValue();

    /**
     * Data formatter.
     */
    private final MetisDataFormatter theDataFormatter;

    /**
     * The root of the tree.
     */
    private final MetisViewerEntry theRoot;

    /**
     * The focused entry.
     */
    private MetisViewerEntry theFocus = null;

    /**
     * The next entry index.
     */
    private Integer theNextIndex = 0;

    /**
     * Constructor.
     */
    public MetisViewerManager() {
        /* Create the root node */
        theRoot = newEntry(WINDOW_TITLE);

        /* Create the formatters */
        theDataFormatter = new MetisDataFormatter();
    }

    /**
     * Get title.
     * @return the title
     */
    public String getTitle() {
        return WINDOW_TITLE;
    }

    /**
     * Get root.
     * @return the root
     */
    protected MetisViewerEntry getRoot() {
        return theRoot;
    }

    /**
     * Get focus.
     * @return the focus
     */
    protected MetisViewerEntry getFocus() {
        return theFocus;
    }

    /**
     * Get Data formatter.
     * @return the formatter
     */
    protected MetisDataFormatter getDataFormatter() {
        return theDataFormatter;
    }

    /**
     * Obtain next id.
     * @return the next id
     */
    public synchronized Integer nextId() {
        Integer myId = theNextIndex;
        theNextIndex = theNextIndex + 1;
        return myId;
    }

    /**
     * Set Focus onto the entry.
     * @param pEntry the entry
     */
    protected void setFocus(final MetisViewerEntry pEntry) {
        /* Record the focus */
        theFocus = pEntry;
    }

    /**
     * Create a new root entry.
     * @param pName the name of the new entry
     * @return the new entry
     */
    public abstract MetisViewerEntry newEntry(final String pName);
}
