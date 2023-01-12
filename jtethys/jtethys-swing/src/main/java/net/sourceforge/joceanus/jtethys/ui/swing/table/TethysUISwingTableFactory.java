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
package net.sourceforge.joceanus.jtethys.ui.swing.table;

import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableFactory;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Swing table Factory.
 */
public class TethysUISwingTableFactory
        implements TethysUITableFactory {
    /**
     * The factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public TethysUISwingTableFactory(final TethysUICoreFactory<?> pFactory) {
        theFactory = pFactory;
    }

    @Override
    public <C, R> TethysUITableManager<C, R> newTable() {
        return new TethysUISwingTableManager<>(theFactory);
    }
}

