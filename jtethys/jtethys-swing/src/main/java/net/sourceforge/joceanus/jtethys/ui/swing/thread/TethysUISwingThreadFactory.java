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
package net.sourceforge.joceanus.jtethys.ui.swing.thread;

import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.thread.TethysUICoreThreadFactory;
import net.sourceforge.joceanus.jtethys.ui.core.thread.TethysUICoreThreadManager;

/**
 * swing Thread Factory.
 */
public class TethysUISwingThreadFactory
        implements TethysUICoreThreadFactory {
    /**
     * The factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
   public TethysUISwingThreadFactory(final TethysUICoreFactory<?> pFactory) {
        theFactory = pFactory;
    }

    @Override
    public TethysUIThreadManager newThreadManager(boolean pSlider) {
        return new TethysUISwingThreadManager(theFactory, pSlider);
    }

    @Override
    public TethysUISwingThreadProgressStatus newThreadSliderStatus(final TethysUICoreThreadManager pManager) {
        return new TethysUISwingThreadProgressStatus(pManager, theFactory);
    }

    /**
     * Create a Thread TextArea Status.
     * @param pManager the thread manager
     * @return the thread status manager
     */
    public TethysUISwingThreadTextAreaStatus newThreadTextAreaStatus(final TethysUICoreThreadManager pManager) {
        return new TethysUISwingThreadTextAreaStatus(pManager, theFactory);
    }
}

