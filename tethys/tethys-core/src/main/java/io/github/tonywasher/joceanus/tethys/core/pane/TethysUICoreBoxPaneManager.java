/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.tethys.core.pane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.core.base.TethysUICoreComponent;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory.TethysUIParentComponent;

/**
 * Core Box Pane Manager.
 */
public abstract class TethysUICoreBoxPaneManager
        extends TethysUICoreComponent
        implements TethysUIBoxPaneManager, TethysUIParentComponent {
    /**
     * Strut Size.
     */
    private static final int STRUT_SIZE = 4;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The ChildManager.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Node List.
     */
    private final List<TethysUIComponent> theNodeList;

    /**
     * The Horizontal Gap.
     */
    private Integer theGap;

    /**
     * Constructor.
     *
     * @param pFactory the Factory
     */
    protected TethysUICoreBoxPaneManager(final TethysUICoreFactory<?> pFactory) {
        theFactory = pFactory;
        theId = theFactory.getNextId();
        theNodeList = new ArrayList<>();
        theGap = STRUT_SIZE;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public Integer getGap() {
        return theGap;
    }

    @Override
    public void setGap(final Integer pGap) {
        theGap = pGap;
    }

    /**
     * Add Spacer Node.
     *
     * @param pNode the node
     */
    protected void addSpacerNode(final TethysUIComponent pNode) {
        theNodeList.add(pNode);
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

