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

import java.awt.Color;

import net.sourceforge.joceanus.jmetis.data.JDataFormatter;

/**
 * Data Manager.
 * @author Tony Washer
 */
public abstract class ViewerManager {
    /**
     * The title.
     */
    private static final String WINDOW_TITLE = ViewerResource.VIEWER_TITLE.getValue();

    /**
     * Data formatter.
     */
    private final JDataFormatter theDataFormatter;

    /**
     * The root of the tree.
     */
    private final ViewerEntry theRoot;

    /**
     * The focused entry.
     */
    private ViewerEntry theFocus = null;

    /**
     * The next entry index.
     */
    private int theNextIndex = 0;

    /**
     * Constructor.
     */
    public ViewerManager() {
        /* Create the root node */
        theRoot = newEntry(WINDOW_TITLE);

        /* Create the formatters */
        theDataFormatter = new JDataFormatter();
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
    protected ViewerEntry getRoot() {
        return theRoot;
    }

    /**
     * Get focus.
     * @return the focus
     */
    protected ViewerEntry getFocus() {
        return theFocus;
    }

    /**
     * Get Data formatter.
     * @return the formatter
     */
    protected JDataFormatter getDataFormatter() {
        return theDataFormatter;
    }

    /**
     * Obtain next id.
     * @return the next id
     */
    public synchronized int nextId() {
        return theNextIndex++;
    }

    /**
     * Set new formatter.
     * @param pStandard the standard colour
     * @param pChanged the changed colour
     * @param pLink the link colour
     * @param pChgLink the changed link colour
     */
    public abstract void setFormatter(final Color pStandard,
                                      final Color pChanged,
                                      final Color pLink,
                                      final Color pChgLink);

    /**
     * Set Focus onto the entry.
     * @param pEntry the entry
     */
    protected void setFocus(final ViewerEntry pEntry) {
        /* Record the focus */
        theFocus = pEntry;
    }

    /**
     * Create a new entry.
     * @param pName the name of the new entry
     * @return the new entry
     */
    public abstract ViewerEntry newEntry(final String pName);
}
