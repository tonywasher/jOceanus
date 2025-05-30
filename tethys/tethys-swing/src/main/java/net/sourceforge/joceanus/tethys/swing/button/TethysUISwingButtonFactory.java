/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.swing.button;

import java.awt.Color;

import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateRangeSelector;
import net.sourceforge.joceanus.tethys.api.button.TethysUIIconButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIListButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.core.button.TethysUICoreButtonFactory;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * swing Button factory.
 */
public class TethysUISwingButtonFactory
        extends TethysUICoreButtonFactory<Color> {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUISwingButtonFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUIButton newButton() {
        return new TethysUISwingButton(theFactory);
    }

    @Override
    public TethysUIDateButtonManager newDateButton() {
        return new TethysUISwingDateButtonManager(theFactory);
    }

    @Override
    public TethysUIDateRangeSelector newDateRangeSelector(final boolean pBaseIsStart) {
        return new TethysUISwingDateRangeSelector(theFactory, pBaseIsStart);
    }

    @Override
    public <T> TethysUIIconButtonManager<T> newIconButton(final Class<T> pClazz) {
        return new TethysUISwingIconButtonManager<>(theFactory, pClazz);
    }

    @Override
    public <T> TethysUIScrollButtonManager<T> newScrollButton(final Class<T> pClazz) {
        return new TethysUISwingScrollButtonManager<>(theFactory, pClazz);
    }

    @Override
    public <T extends Comparable<? super T>> TethysUIListButtonManager<T> newListButton() {
        return new TethysUISwingListButtonManager<>(theFactory);
    }

    @Override
    public TethysUISwingColorPicker newColorPicker() {
        return new TethysUISwingColorPicker(theFactory);
    }
}
