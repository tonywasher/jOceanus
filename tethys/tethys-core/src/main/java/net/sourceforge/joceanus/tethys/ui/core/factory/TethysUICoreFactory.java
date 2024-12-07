/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.core.factory;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUILogTextArea;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreValueSet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core factory.
 * @param <C> the color
 */
public abstract class TethysUICoreFactory<C>
        implements TethysUIFactory<C>, TethysEventProvider<TethysUIEvent> {
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
     * Program Definition.
     */
    private final TethysUIProgram theProgram;

    /**
     * The Data Formatter.
     */
    private final OceanusDataFormatter theFormatter;

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
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * LogSink.
     */
    private TethysUICoreLogTextArea theLogSink;

    /**
     * The Active Profile.
     */
    private OceanusProfile theProfile;

    /**
     * Constructor.
     * @param pProgram the program definitions
     */
    protected TethysUICoreFactory(final TethysUIProgram pProgram) {
        /* Store the program */
        theProgram = pProgram;
        theProfile = new OceanusProfile("StartUp");

        /* Create base items */
        theFormatter = new OceanusDataFormatter();
        theNextNodeId = new AtomicInteger(1);
        theParentMap = new HashMap<>();
        theValueSet = new TethysUICoreValueSet();
        theEventManager = new OceanusEventManager<>();
    }

    /**
     * Establish logSink.
     */
    protected void establishLogSink() {
        theLogSink = new TethysUICoreLogTextArea(this);
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public OceanusDataFormatter getDataFormatter() {
        return theFormatter;
    }

    @Override
    public OceanusDataFormatter newDataFormatter() {
        return new OceanusDataFormatter();
    }

    @Override
    public TethysUIProgram getProgramDefinitions() {
        return theProgram;
    }

    /**
     * Obtain the next id.
     * @return the next id
     */
    public Integer getNextId() {
        return theNextNodeId.getAndIncrement();
    }

    @Override
    public TethysUILogTextArea getLogSink() {
        return theLogSink;
    }

    @Override
    public void activateLogSink() {
        OceanusLogManager.setSink(theLogSink);
    }

    @Override
    public OceanusProfile getNewProfile(final String pTask) {
        /* Create a new profile */
        theProfile = new OceanusProfile(pTask);

        /* Return the new profile */
        return theProfile;
    }

    @Override
    public OceanusProfile getActiveProfile() {
        return theProfile;
    }

    @Override
    public OceanusProfile getActiveTask() {
        /* Create a new profile */
        return theProfile == null
                ? null
                : theProfile.getActiveTask();
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
    protected void fireEvent(final TethysUIEvent pEvent) {
        theEventManager.fireEvent(pEvent);
    }
}
