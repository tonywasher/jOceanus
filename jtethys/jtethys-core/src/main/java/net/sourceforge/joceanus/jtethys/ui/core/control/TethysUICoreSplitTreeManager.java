/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.control;

import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUISplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUITreeManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 * @param <T> the item type
 */
public abstract class TethysUICoreSplitTreeManager<T>
        extends TethysUICoreComponent
        implements TethysUISplitTreeManager<T> {
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
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The HTMLPane.
     */
    private final TethysUIBorderPaneManager theHTMLPane;

    /**
     * The Tree Manager.
     */
    private final TethysUITreeManager<T> theTreeManager;

    /**
     * The HTML Manager.
     */
    private final TethysUIHTMLManager theHTMLManager;

    /**
     * The Weight.
     */
    private double theWeight;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreSplitTreeManager(final TethysUICoreFactory<?> pFactory) {
        /* Create instances */
        theId = pFactory.getNextId();
        theTreeManager = pFactory.controlFactory().newTreeManager();
        theHTMLManager = pFactory.controlFactory().newHTMLManager();
        theHTMLPane = pFactory.paneFactory().newBorderPane();

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

    @Override
    public TethysUITreeManager<T> getTreeManager() {
        return theTreeManager;
    }

    @Override
    public TethysUIHTMLManager getHTMLManager() {
        return theHTMLManager;
    }

    /**
     * Obtain the HTML Pane.
     * @return the HTML pane
     */
    protected TethysUIBorderPaneManager getHTMLPane() {
        return theHTMLPane;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setControlPane(final TethysUIComponent pPane) {
        theHTMLPane.setNorth(pPane);
    }

    @Override
    public void setWeight(final double pWeight) {
        theWeight = pWeight;
    }

    @Override
    public double getWeight() {
        return theWeight;
    }

    /**
     * Handle HTML reference lookUp.
     * @param pEvent the action event
     */
    private void handleReferenceLookup(final TethysEvent<TethysUIEvent> pEvent) {
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
