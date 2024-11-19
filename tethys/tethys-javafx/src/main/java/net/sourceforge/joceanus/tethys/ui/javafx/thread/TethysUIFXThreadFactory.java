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
package net.sourceforge.joceanus.tethys.ui.javafx.thread;

import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadFactory;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadManager;

/**
 * javaFX Thread Factory.
 */
public class TethysUIFXThreadFactory
    implements TethysUICoreThreadFactory {
    /**
     * The factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * use slider.
     */
    private final boolean useSlider;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSlider use slider
     */
    public TethysUIFXThreadFactory(final TethysUICoreFactory<?> pFactory,
                                   final boolean pSlider) {
        theFactory = pFactory;
        useSlider = pSlider;
    }

    @Override
    public TethysUIThreadManager newThreadManager() {
        return new TethysUIFXThreadManager(theFactory, useSlider);
    }

    @Override
    public TethysUIFXThreadProgressStatus newThreadSliderStatus(final TethysUICoreThreadManager pManager) {
        return new TethysUIFXThreadProgressStatus(pManager, theFactory);
    }

    @Override
    public TethysUIFXThreadTextAreaStatus newThreadTextAreaStatus(final TethysUICoreThreadManager pManager) {
        return new TethysUIFXThreadTextAreaStatus(pManager, theFactory);
    }
}
