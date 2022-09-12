/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.swing;

import net.sourceforge.joceanus.jmetis.launch.swing.MetisSwingState;
import net.sourceforge.joceanus.jmetis.launch.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Prometheus Swing Toolkit.
 */
public class PrometheusSwingToolkit
        extends PrometheusToolkit {
    /**
     * Constructor.
     * @param pInfo the program info
     * @throws OceanusException on error
     */
    public PrometheusSwingToolkit(final MetisSwingState pInfo) throws OceanusException {
        /* Create Toolkit */
        this(new MetisSwingToolkit(pInfo));
    }

    /**
     * Constructor.
     * @param pToolkit the metis toolkit
     * @throws OceanusException on error
     */
    public PrometheusSwingToolkit(final MetisSwingToolkit pToolkit) throws OceanusException {
        /* Create Toolkit */
        super(pToolkit);
    }

    @Override
    public MetisSwingToolkit getToolkit() {
        return (MetisSwingToolkit) super.getToolkit();
    }
}
