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
package io.github.tonywasher.joceanus.tethys.swing.pane;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Swing Pane Factory.
 */
public class TethysUISwingPaneFactory
        implements TethysUIPaneFactory {
    /**
     * The factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory.
     */
    public TethysUISwingPaneFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUISwingBorderPaneManager newBorderPane() {
        return new TethysUISwingBorderPaneManager(theFactory);
    }

    @Override
    public TethysUISwingBoxPaneManager newHBoxPane() {
        return new TethysUISwingBoxPaneManager(theFactory, true);
    }

    @Override
    public TethysUISwingBoxPaneManager newVBoxPane() {
        return new TethysUISwingBoxPaneManager(theFactory, false);
    }

    @Override
    public <P extends TethysUIComponent> TethysUISwingCardPaneManager<P> newCardPane() {
        return new TethysUISwingCardPaneManager<>(theFactory);
    }

    @Override
    public TethysUISwingFlowPaneManager newFlowPane() {
        return new TethysUISwingFlowPaneManager(theFactory);
    }

    @Override
    public TethysUISwingGridPaneManager newGridPane() {
        return new TethysUISwingGridPaneManager(theFactory);
    }

    @Override
    public TethysUISwingScrollPaneManager newScrollPane() {
        return new TethysUISwingScrollPaneManager(theFactory);
    }

    @Override
    public TethysUISwingTabPaneManager newTabPane() {
        return new TethysUISwingTabPaneManager(theFactory);
    }
}
