/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.launch;

import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Program Definition interface.
 */
public interface MetisProgram {
    /**
     * Obtain the dimensions (width, height) of the panel.
     * @return the width/height (or null for default)
     */
    default int[] getPanelDimensions() {
        return null;
    }

    /**
     * Does the panel use a slider status?
     * @return true/false
     */
    default boolean useSliderStatus() {
        return false;
    }

    /**
     * create a new mainPanel.
     * @param pToolkit the toolkit
     * @return the main panel
     * @throws OceanusException on error
     */
    MetisMainPanel createMainPanel(MetisToolkit pToolkit) throws OceanusException;
}
