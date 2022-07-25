/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 * @param <T> the item type
 */
public abstract class TethysSplitTreeManager<T>
        implements TethysEventProvider<TethysXUIEvent>, TethysComponent {
    /**
     * The default Weight.
     */
    protected static final double DEFAULT_WEIGHT = 0.2;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysXUIEvent> theEventManager;

    /**
     * The HTMLPane.
     */
    private final TethysBorderPaneManager theHTMLPane;

    /**
     * The Tree Manager.
     */
    private final TethysTreeManager<T> theTreeManager;

    /**
     * The HTML Manager.
     */
    private final TethysHTMLManager theHTMLManager;

    /**
     * The Weight.
     */
    private double theWeight;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSplitTreeManager(final TethysGuiFactory pFactory) {
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
    public TethysTreeManager<T> getTreeManager() {
        return theTreeManager;
    }

    /**
     * Obtain the HTML Manager.
     * @return the HTML manager
     */
    public TethysHTMLManager getHTMLManager() {
        return theHTMLManager;
    }

    /**
     * Obtain the HTML Pane.
     * @return the HTML pane
     */
    protected TethysBorderPaneManager getHTMLPane() {
        return theHTMLPane;
    }

    @Override
    public TethysEventRegistrar<TethysXUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set control Pane.
     * @param pPane the control Pane
     */
    public void setControlPane(final TethysComponent pPane) {
        theHTMLPane.setNorth(pPane);
    }

    /**
     * Set weight.
     * @param pWeight the weight
     */
    public void setWeight(final double pWeight) {
        theWeight = pWeight;
    }

    /**
     * Get weight.
     * @return the weight
     */
    public double getWeight() {
        return theWeight;
    }

    /**
     * Handle HTML reference lookUp.
     * @param pEvent the action event
     */
    private void handleReferenceLookup(final TethysEvent<TethysXUIEvent> pEvent) {
        /* Obtain the reference */
        final String myRef = pEvent.getDetails(String.class);

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
