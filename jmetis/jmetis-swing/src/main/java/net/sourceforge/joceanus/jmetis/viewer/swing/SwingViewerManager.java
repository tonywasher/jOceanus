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

import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;

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
     * The field manager.
     */
    private final JFieldManager theFieldManager;

    /**
     * The owning window.
     */
    private ViewerWindow theWindow = null;

    /**
     * Constructor.
     * @param pFieldManager the field manager
     */
    public SwingViewerManager(final JFieldManager pFieldManager) {
        /* Store the field manager */
        theFieldManager = pFieldManager;

        /* Create the tree model */
        theModel = new DefaultTreeModel(getRoot().getNode());

        /* Create the formatters */
        theHTMLFormatter = new ViewerHTML(pFieldManager.getConfig(), getDataFormatter());

        /* Create the listener */
        new ViewerListener();
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

    /**
     * Process field configuration.
     */
    private void processFieldConfig() {
        /* Process the configuration */
        theHTMLFormatter.processConfig(theFieldManager.getConfig());

        /* If we have a data window */
        if (theWindow != null) {
            /* Set the new formatter */
            theWindow.setFormatter(theHTMLFormatter);
        }
    }

    /**
     * Listener class.
     */
    private final class ViewerListener
            implements TethysChangeEventListener {
        /**
         * Constructor.
         */
        private ViewerListener() {
            theFieldManager.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChangeEvent(final TethysChangeEvent pEvent) {
            processFieldConfig();
        }
    }
}
