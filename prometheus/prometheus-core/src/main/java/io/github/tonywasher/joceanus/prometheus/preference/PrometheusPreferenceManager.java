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
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceSet;
import io.github.tonywasher.joceanus.metis.viewer.MetisViewerManager;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

/**
 * Prometheus Preference Manager.
 */
public class PrometheusPreferenceManager
        extends MetisPreferenceManager {
    /**
     * The Security Manager.
     */
    private final PrometheusPreferenceSecurity theSecurity;

    /**
     * Constructor.
     *
     * @param pViewer the viewer manager
     * @throws OceanusException on error
     */
    public PrometheusPreferenceManager(final MetisViewerManager pViewer) throws OceanusException {
        super(pViewer);
        theSecurity = new PrometheusPreferenceSecurity(this);
    }

    @Override
    protected <X extends MetisPreferenceSet> X newPreferenceSet(final String pName,
                                                                final Class<X> pClazz) {
        final X mySet = super.newPreferenceSet(pName, pClazz);
        if (mySet instanceof PrometheusPreferenceSet mySecureSet) {
            mySecureSet.setSecurity(theSecurity);
        }
        return mySet;
    }
}