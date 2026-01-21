/*
 * Tethys: GUI Utilities
 * Copyright 2026. Tony Washer
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

package net.sourceforge.joceanus.tethys.api.factory;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.api.base.TethysUIProgram;

import java.io.InputStream;

/**
 * Launch Program interface.
 */
public abstract class TethysUILaunchProgram
        extends TethysUIProgram {
    /**
     * Constructor.
     *
     * @param pProperties the inputStream of the properties
     */
    protected TethysUILaunchProgram(final InputStream pProperties) {
        super(pProperties);
    }

    /**
     * create a new mainPanel.
     *
     * @param pFactory the factory
     * @return the main panel
     * @throws OceanusException on error
     */
    public abstract TethysUIMainPanel createMainPanel(TethysUIFactory<?> pFactory) throws OceanusException;
}
