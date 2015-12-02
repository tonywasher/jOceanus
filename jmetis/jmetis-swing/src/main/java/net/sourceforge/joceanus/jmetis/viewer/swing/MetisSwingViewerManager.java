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

import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;

/**
 * Data Manager.
 * @author Tony Washer
 */
public class MetisSwingViewerManager
        extends MetisViewerManager {
    /**
     * HTML formatter.
     */
    private MetisViewerHTML theHTMLFormatter = null;

    /**
     * Tree Model.
     */
    private final DefaultTreeModel theModel;

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldManager;

    /**
     * The owning window.
     */
    private MetisViewerWindow theWindow = null;

    /**
     * Constructor.
     * @param pFieldManager the field manager
     */
    public MetisSwingViewerManager(final MetisFieldManager pFieldManager) {
        /* Store the field manager */
        theFieldManager = pFieldManager;

        /* Create the tree model */
        theModel = new DefaultTreeModel(getRoot().getNode());

        /* Create the formatters */
        theHTMLFormatter = new MetisViewerHTML(pFieldManager.getConfig(), getDataFormatter());

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
    protected MetisSwingViewerEntry getRoot() {
        return (MetisSwingViewerEntry) super.getRoot();
    }

    @Override
    protected MetisSwingViewerEntry getFocus() {
        return (MetisSwingViewerEntry) super.getFocus();
    }

    /**
     * Get HTML formatter.
     * @return the formatter
     */
    protected MetisViewerHTML getHTMLFormatter() {
        return theHTMLFormatter;
    }

    /**
     * Declare window object.
     * @param pWindow the window
     */
    public void declareWindow(final MetisViewerWindow pWindow) {
        /* Store window */
        theWindow = pWindow;
    }

    @Override
    protected void setFocus(final MetisViewerEntry pEntry) {
        /* Record the focus */
        super.setFocus(pEntry);

        /* Update the window */
        updateWindow(pEntry);
    }

    /**
     * update window for the entry.
     * @param pEntry the entry
     */
    protected void updateWindow(final MetisViewerEntry pEntry) {
        /* If we have a window */
        if (theWindow != null) {
            /* Set selection path and ensure visibility */
            theWindow.displayData((MetisSwingViewerEntry) pEntry);
        }
    }

    @Override
    public synchronized MetisSwingViewerEntry newEntry(final String pName) {
        return new MetisSwingViewerEntry(this, pName, nextId());
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
        public void processChange(final TethysChangeEvent pEvent) {
            processFieldConfig();
        }
    }
}
