/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 * @param <T> the item type
 * @param <N> the Node type
 * @param <I> the icon type
 */
public abstract class TethysSplitTreeManager<T, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The HTMLPane.
     */
    private final TethysBorderPaneManager<N, I> theHTMLPane;

    /**
     * The Tree Manager.
     */
    private final TethysTreeManager<T, N, I> theTreeManager;

    /**
     * The HTML Manager.
     */
    private final TethysHTMLManager<N, I> theHTMLManager;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSplitTreeManager(final TethysGuiFactory<N, I> pFactory) {
        /* Create instances */
        theId = pFactory.getNextId();
        theTreeManager = pFactory.newTreeManager();
        theHTMLManager = pFactory.newHTMLManager();
        theHTMLPane = pFactory.newBorderPane();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Listen to the TreeManager */
        theTreeManager.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);

        /* Listen to the HTMLManager */
        theHTMLManager.getEventRegistrar().addEventListener(this::handleReferenceLookup);
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain the Tree Manager.
     * @return the tree manager
     */
    public TethysTreeManager<T, N, I> getTreeManager() {
        return theTreeManager;
    }

    /**
     * Obtain the HTML Manager.
     * @return the HTML manager
     */
    public TethysHTMLManager<N, I> getHTMLManager() {
        return theHTMLManager;
    }

    /**
     * Obtain the HTML Pane.
     * @return the HTML pane
     */
    protected TethysBorderPaneManager<N, I> getHTMLPane() {
        return theHTMLPane;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set control Pane.
     * @param pPane the control Pane
     */
    public void setControlPane(final TethysNode<N> pPane) {
        theHTMLPane.setNorth(pPane);
    }

    /**
     * Handle HTML reference lookUp.
     * @param pEvent the action event
     */
    private void handleReferenceLookup(final TethysEvent<TethysUIEvent> pEvent) {
        /* Obtain the reference */
        String myRef = pEvent.getDetails(String.class);

        /* Try to lookup as a tree item */
        if (!theTreeManager.lookUpAndSelectItem(myRef)) {
            /* Cascade the event to perform further lookup */
            theEventManager.cascadeEvent(pEvent);

            /* Consume event if honoured */
        } else {
            pEvent.consume();
        }
    }
}
