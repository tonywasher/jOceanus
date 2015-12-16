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

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 * @param <T> the item type
 * @param <N> the Node type
 */
public abstract class TethysSplitTreeManager<T, N>
        implements TethysEventProvider {
    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * The Tree Manager.
     */
    private final TethysTreeManager<T, N> theTreeManager;

    /**
     * The HTML Manager.
     */
    private final TethysHTMLManager<N> theHTMLManager;

    /**
     * Constructor.
     * @param pTreeManager the tree manager
     * @param pHTMLManager the html manager
     */
    protected TethysSplitTreeManager(final TethysTreeManager<T, N> pTreeManager,
                                     final TethysHTMLManager<N> pHTMLManager) {
        /* Store parameters */
        theTreeManager = pTreeManager;
        theHTMLManager = pHTMLManager;

        /* Create the event manager */
        theEventManager = new TethysEventManager();

        /* Listen to the TreeManager */
        theTreeManager.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                theEventManager.cascadeActionEvent(pEvent);
            }
        });

        /* Listen to the HTMLManager */
        theHTMLManager.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                handleReferenceLookup(pEvent);
            }
        });
    }

    /**
     * Obtain the Tree Manager.
     * @return the tree manager
     */
    public TethysTreeManager<T, N> getTreeManager() {
        return theTreeManager;
    }

    /**
     * Obtain the HTML Manager.
     * @return the HTML manager
     */
    public TethysHTMLManager<N> getHTMLManager() {
        return theHTMLManager;
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the node.
     * @return the node.
     */
    public abstract N getNode();

    /**
     * Handle HTML reference lookUp.
     * @param pEvent the action event
     */
    private void handleReferenceLookup(final TethysActionEvent pEvent) {
        /* Obtain the reference */
        String myRef = pEvent.getDetails(String.class);

        /* Try to lookup as a tree item */
        if (!theTreeManager.lookUpAndSelectItem(myRef)) {
            /* Cascade the event to perform further lookup */
            theEventManager.cascadeActionEvent(pEvent);
        }
    }
}
