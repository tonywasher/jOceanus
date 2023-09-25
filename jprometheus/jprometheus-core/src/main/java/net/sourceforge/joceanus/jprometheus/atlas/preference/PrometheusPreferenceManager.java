/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.preference;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Prometheus Preference Manager,
 */
public class PrometheusPreferenceManager
    extends MetisPreferenceManager {
    /**
     * The Security Manager.
     */
    private PrometheusPreferenceSecurity theSecurity;

    /**
     * Constructor.
     * @param pViewer the viewer manager
     * @throws OceanusException on error
     */
    public PrometheusPreferenceManager(final MetisViewerManager pViewer) throws OceanusException {
        super(pViewer);
    }

    /**
     * Obtain the security manager.
     * @return the security manager
     * @throws OceanusException on error
     */
    protected PrometheusPreferenceSecurity getSecurity() throws OceanusException {
        /* If we have not created security and are not in the middle of creating security */
        if (theSecurity == null) {
            theSecurity = new PrometheusPreferenceSecurity(this);
        }
        return theSecurity;
    }
}
