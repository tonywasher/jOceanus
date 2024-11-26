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
package net.sourceforge.joceanus.tethys.ui.core.pane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIFlowPaneManager;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory.TethysUIParentComponent;

/**
 * Core Flow Pane Manager.
 */
public abstract class TethysUICoreFlowPaneManager
        extends TethysUICoreComponent
        implements TethysUIFlowPaneManager, TethysUIParentComponent {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Node List.
     */
    private final List<TethysUIComponent> theNodeList;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected TethysUICoreFlowPaneManager(final TethysUICoreFactory<?> pFactory) {
        theFactory = pFactory;
        theId = theFactory.getNextId();
        theNodeList = new ArrayList<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public void addNode(final TethysUIComponent pNode) {
        theNodeList.add(pNode);
        theFactory.registerChild(this, pNode);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysUIComponent myNode : theNodeList) {
            myNode.setEnabled(pEnabled);
        }
    }

    @Override
    public Iterator<TethysUIComponent> iterator() {
        return theNodeList.iterator();
    }
}