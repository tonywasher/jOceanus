/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.tethys.javafx.button;

import javafx.scene.paint.Color;

import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateRangeSelector;
import net.sourceforge.joceanus.tethys.api.button.TethysUIIconButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIListButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.core.button.TethysUICoreButtonFactory;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * javaFX Button factory.
 */
public class TethysUIFXButtonFactory
        extends TethysUICoreButtonFactory<Color> {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUIFXButtonFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUIButton newButton() {
        return new TethysUIFXButton(theFactory);
    }

    @Override
    public TethysUIDateButtonManager newDateButton() {
        return new TethysUIFXDateButtonManager(theFactory);
    }

    @Override
    public TethysUIDateRangeSelector newDateRangeSelector(final boolean pBaseIsStart) {
        return new TethysUIFXDateRangeSelector(theFactory, pBaseIsStart);
    }

    @Override
    public <T> TethysUIIconButtonManager<T> newIconButton(final Class<T> pClazz) {
        return new TethysUIFXIconButtonManager<>(theFactory, pClazz);
    }

    @Override
    public <T> TethysUIScrollButtonManager<T> newScrollButton(final Class<T> pClazz) {
        return new TethysUIFXScrollButtonManager<>(theFactory, pClazz);
    }

    @Override
    public <T extends Comparable<? super T>> TethysUIListButtonManager<T> newListButton() {
        return new TethysUIFXListButtonManager<>(theFactory);
    }

    @Override
    public TethysUIFXColorPicker newColorPicker() {
        return new TethysUIFXColorPicker(theFactory);
    }
}
