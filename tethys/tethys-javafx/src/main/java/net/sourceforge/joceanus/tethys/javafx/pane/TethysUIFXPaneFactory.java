/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.pane;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * javaFX Pane factory.
 */
public class TethysUIFXPaneFactory
        implements TethysUIPaneFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUIFXPaneFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUIFXBorderPaneManager newBorderPane() {
        return new TethysUIFXBorderPaneManager(theFactory);
    }

    @Override
    public TethysUIFXBoxPaneManager newHBoxPane() {
        return new TethysUIFXBoxPaneManager(theFactory, true);
    }

    @Override
    public TethysUIFXBoxPaneManager newVBoxPane() {
        return new TethysUIFXBoxPaneManager(theFactory, false);
    }

    @Override
    public <P extends TethysUIComponent> TethysUIFXCardPaneManager<P> newCardPane() {
        return new TethysUIFXCardPaneManager<>(theFactory);
    }

    @Override
    public TethysUIFXFlowPaneManager newFlowPane() {
        return new TethysUIFXFlowPaneManager(theFactory);
    }

    @Override
    public TethysUIFXGridPaneManager newGridPane() {
        return new TethysUIFXGridPaneManager(theFactory);
    }

    @Override
    public TethysUIFXScrollPaneManager newScrollPane() {
        return new TethysUIFXScrollPaneManager(theFactory);
    }

    @Override
    public TethysUIFXTabPaneManager newTabPane() {
        return new TethysUIFXTabPaneManager(theFactory);
    }
}
