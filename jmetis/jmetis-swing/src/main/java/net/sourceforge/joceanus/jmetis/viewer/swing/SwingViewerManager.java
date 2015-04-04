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
package net.sourceforge.joceanus.jmetis.viewer.swing;

import java.awt.Color;

import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;

/**
 * Data Manager.
 * @author Tony Washer
 */
public class SwingViewerManager
        extends ViewerManager {
    /**
     * HTML formatter.
     */
    private ViewerHTML theHTMLFormatter = null;

    /**
     * Tree Model.
     */
    private final DefaultTreeModel theModel;

    /**
     * The owning window.
     */
    private ViewerWindow theWindow = null;

    /**
     * Constructor.
     */
    public SwingViewerManager() {
        /* Create the tree model */
        theModel = new DefaultTreeModel(getRoot().getNode());

        /* Create the formatters */
        theHTMLFormatter = new ViewerHTML(getDataFormatter());
    }

    /**
     * Get tree model.
     * @return the model
     */
    protected DefaultTreeModel getModel() {
        return theModel;
    }

    @Override
    protected SwingViewerEntry getRoot() {
        return (SwingViewerEntry) super.getRoot();
    }

    @Override
    protected SwingViewerEntry getFocus() {
        return (SwingViewerEntry) super.getFocus();
    }

    /**
     * Get HTML formatter.
     * @return the formatter
     */
    protected ViewerHTML getHTMLFormatter() {
        return theHTMLFormatter;
    }

    @Override
    public void setFormatter(final Color pStandard,
                             final Color pChanged,
                             final Color pLink,
                             final Color pChgLink) {
        /* Set the colours */
        theHTMLFormatter.setColors(pStandard, pChanged, pLink, pChgLink);

        /* If we have a data window */
        if (theWindow != null) {
            /* Set the new formatter */
            theWindow.setFormatter(theHTMLFormatter);
        }
    }

    /**
     * Declare window object.
     * @param pWindow the window
     */
    public void declareWindow(final ViewerWindow pWindow) {
        /* Store window */
        theWindow = pWindow;
    }

    @Override
    protected void setFocus(final ViewerEntry pEntry) {
        /* Record the focus */
        super.setFocus(pEntry);

        /* Update the window */
        updateWindow(pEntry);
    }

    /**
     * update window for the entry.
     * @param pEntry the entry
     */
    protected void updateWindow(final ViewerEntry pEntry) {
        /* If we have a window */
        if (theWindow != null) {
            /* Set selection path and ensure visibility */
            theWindow.displayData((SwingViewerEntry) pEntry);
        }
    }

    @Override
    public synchronized SwingViewerEntry newEntry(final String pName) {
        return new SwingViewerEntry(this, pName, nextId());
    }
}
