/*******************************************************************************
 * jTethys: Java Utilities
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JIconButton.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Tree Manager.
 * @param <N> the Node type
 */
public abstract class TethysHTMLManager<N>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysHTMLManager.class);

    /**
     * Reference Separator.
     */
    private static final String REF_SEPARATOR = "#";

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Current reference.
     */
    private String theCurrentRef;

    /**
     * Are we waiting for a page to be loaded?
     */
    private boolean waitingForPage;

    /**
     * Constructor.
     */
    protected TethysHTMLManager() {
        theEventManager = new TethysEventManager<>();
        waitingForPage = false;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the node.
     * @return the node.
     */
    public abstract N getNode();

    /**
     * Process selected reference.
     * @param pReference the reference
     */
    public void processReference(final String pReference) {
        /* Access local copy of reference */
        String myRef = pReference;
        String myInternal = null;

        /* If the name has a # in it */
        if (myRef.contains(REF_SEPARATOR)) {
            /* Split on the # */
            String[] myTokens = myRef.split(REF_SEPARATOR);

            /* Allocate the values */
            myRef = myTokens[0];
            myInternal = myTokens[1];

            /* Handle an internal reference */
            if (myRef.length() == 0) {
                myRef = null;
            }
        }

        /* If we need to switch pages */
        if ((myRef != null)
            && !myRef.equals(theCurrentRef)) {
            /* Request load of page */
            waitingForPage = true;
            loadNewPage(myRef);

            /* If we did not load a fresh page */
            if (waitingForPage) {
                /* Don't attempt to scroll to internal reference */
                myInternal = null;
                LOGGER.error("Failed to load page <" + myRef + ">");
            }
        }

        /* If we have an internal reference */
        if (myInternal != null) {
            /* Scroll to the reference */
            scrollToReference(myInternal);
        }
    }

    /**
     * Set the HTML.
     * @param pHTMLString the HTML content.
     * @param pReference the reference
     */
    public void setHTMLContent(final String pHTMLString,
                               final String pReference) {
        /* Store the reference */
        theCurrentRef = pReference;
        waitingForPage = false;
    }

    /**
     * Scroll to reference.
     * @param pReference the reference
     */
    protected abstract void scrollToReference(final String pReference);

    /**
     * Load new page.
     * @param pPageRef the page reference
     */
    private void loadNewPage(final String pPageRef) {
        theEventManager.fireEvent(TethysUIEvent.BUILDPAGE, pPageRef);
    }
}
