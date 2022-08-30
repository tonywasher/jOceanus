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
package net.sourceforge.joceanus.jprometheus.javafx;

import net.sourceforge.joceanus.jmetis.profile.MetisState;
import net.sourceforge.joceanus.jmetis.launch.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Prometheus JavaFX Toolkit.
 */
public class PrometheusFXToolkit
        extends PrometheusToolkit {
    /**
     * Constructor.
     * @param pInfo the program info
     * @throws OceanusException on error
     */
    public PrometheusFXToolkit(final MetisState pInfo) throws OceanusException {
        /* Create Toolkit */
        this(new MetisFXToolkit(pInfo));
    }

    /**
     * Constructor.
     * @param pToolkit the metis toolkit
     * @throws OceanusException on error
     */
    public PrometheusFXToolkit(final MetisFXToolkit pToolkit) throws OceanusException {
        /* Create Toolkit */
        super(pToolkit);
    }

    @Override
    public MetisFXToolkit getToolkit() {
        return (MetisFXToolkit) super.getToolkit();
    }
}
