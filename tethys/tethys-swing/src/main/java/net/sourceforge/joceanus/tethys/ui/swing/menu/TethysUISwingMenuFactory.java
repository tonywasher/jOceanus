/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.ui.swing.menu;

import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIMenuBarManager;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIMenuFactory;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIToolBarManager;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;

/**
 * Swing Menu Factory.
 */
public class TethysUISwingMenuFactory
        implements TethysUIMenuFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUISwingMenuFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public <T> TethysUIScrollMenu<T> newContextMenu() {
        return new TethysUISwingScrollMenu<>();
    }

    @Override
    public TethysUIMenuBarManager newMenuBar() {
        return new TethysUISwingMenuBarManager();
    }

    @Override
    public TethysUIToolBarManager newToolBar() {
        return new TethysUISwingToolBarManager(theFactory);
    }
}
