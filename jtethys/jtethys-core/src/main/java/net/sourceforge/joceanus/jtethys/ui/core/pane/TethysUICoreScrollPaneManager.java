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
package net.sourceforge.joceanus.jtethys.ui.core.pane;

import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Scroll Pane Manager.
 */
public abstract class TethysUICoreScrollPaneManager
        extends TethysUICoreComponent
        implements TethysUIScrollPaneManager {
    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected TethysUICoreScrollPaneManager(final TethysUICoreFactory<?> pFactory) {
        theId = pFactory.getNextId();
    }

    @Override
    public Integer getId() {
        return theId;
    }
}

