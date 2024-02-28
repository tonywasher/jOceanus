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
package net.sourceforge.joceanus.jtethys.ui.core.thread;

import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadFactory;

/**
 * Core Thread Factory API.
 */
public interface TethysUICoreThreadFactory
    extends TethysUIThreadFactory {
    /**
     * Create a Thread Slider Status.
     * @param pManager the thread manager
     * @return the thread status manager
     */
    TethysUICoreThreadProgressStatus newThreadSliderStatus(TethysUICoreThreadManager pManager);

    /**
     * Create a Thread TextArea Status.
     * @param pManager the thread manager
     * @return the thread status manager
     */
    TethysUICoreThreadTextAreaStatus newThreadTextAreaStatus(TethysUICoreThreadManager pManager);
}
