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
package net.sourceforge.joceanus.jtethys.ui.core.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreValueSet;

/**
 * Core factory.
 * @param <C> the color
 */
public abstract class TethysUICoreFactory<C>
        implements TethysUIFactory<C>, TethysEventProvider<TethysUIXEvent> {
    /**
     * Parent Component definition.
     */
    @FunctionalInterface
    public interface TethysUIParentComponent {
        /**
         * Set child visibility.
         * @param pChild the child
         * @param pVisible the visibility
         */
        void setChildVisible(TethysUIComponent pChild,
                             boolean pVisible);
    }

    /**
     * The Data Formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * The Next nodeId.
     */
    private final AtomicInteger theNextNodeId;

    /**
     * The node Map.
     */
    private final Map<Integer, TethysUIParentComponent> theParentMap;

    /**
     * ValueSet.
     */
    private final TethysUIValueSet theValueSet;

    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

    /**
     * Constructor.
     */
    protected TethysUICoreFactory() {
        theFormatter = new TethysUICoreDataFormatter();
        theNextNodeId = new AtomicInteger(1);
        theParentMap = new HashMap<>();
        theValueSet = new TethysUICoreValueSet();
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the next id.
     * @return the next id
     */
    public Integer getNextId() {
        return theNextNodeId.getAndIncrement();
    }

    /**
     * Set visibility of node.
     * @param pNode the node
     * @param pVisible true/false
     */
    public void setNodeVisible(final TethysUIComponent pNode,
                               final boolean pVisible) {
        /* Lookup parent */
        final TethysUIParentComponent myParent = theParentMap.get(pNode.getId());
        if (myParent != null) {
            myParent.setChildVisible(pNode, pVisible);
        }
    }

    /**
     * Register Child.
     * @param pParent the parent
     * @param pChild the child node
     */
    public void registerChild(final TethysUIParentComponent pParent,
                              final TethysUIComponent pChild) {
        theParentMap.put(pChild.getId(), pParent);
    }

    /**
     * DeRegister Child.
     * @param pChild the child node
     */
    public void deRegisterChild(final TethysUIComponent pChild) {
        theParentMap.remove(pChild.getId());
    }

    @Override
    public TethysUIValueSet getValueSet() {
        return theValueSet;
    }

    /**
     * fire event.
     * @param pEvent the event
     */
    protected void fireEvent(final TethysUIXEvent pEvent) {
        theEventManager.fireEvent(pEvent);
    }
}
