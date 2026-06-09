/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.prometheus.preference;

import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceManager;
import io.github.tonywasher.joceanus.metis.viewer.MetisViewerManager;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

/**
 * Prometheus Preference Manager.
 */
public class PrometheusPreferenceManager
        extends MetisPreferenceManager {
    /**
     * Constructor.
     *
     * @param pViewer the viewer manager
     * @throws OceanusException on error
     */
    public PrometheusPreferenceManager(final MetisViewerManager pViewer) throws OceanusException {
        super(pViewer);
        final PrometheusPreferenceSecurity mySecurity = new PrometheusPreferenceSecurity(this);
        setParameters(new PrometheusPreferenceParams(getParameters(), mySecurity));
    }
}
