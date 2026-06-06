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
package io.github.tonywasher.joceanus.prometheus.views;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;

import java.util.ResourceBundle;

/**
 * Resource IDs for jPrometheus View Fields.
 */
public enum PrometheusViewResource implements OceanusBundleId {
    /**
     * ViewerEntry DataSet.
     */
    VIEWERENTRY_DATASET("ViewerEntry.DataSet"),

    /**
     * ViewerEntry Updates.
     */
    VIEWERENTRY_UPDATES("ViewerEntry.Updates"),

    /**
     * ViewerEntry Analysis.
     */
    VIEWERENTRY_ANALYSIS("ViewerEntry.Analysis"),

    /**
     * ViewerEntry Maintenance.
     */
    VIEWERENTRY_MAINT("ViewerEntry.Maint"),

    /**
     * ViewerEntry StaticData.
     */
    VIEWERENTRY_STATIC("ViewerEntry.Static"),

    /**
     * UpdateSet Name.
     */
    UPDATESET_NAME("UpdateSet.Name");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(PrometheusDataSet.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     *
     * @param pKeyName the key name
     */
    PrometheusViewResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "Prometheus.views";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
