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

import io.github.tonywasher.joceanus.metis.viewer.MetisViewerResource;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Standard Viewer Entries.
 */
public enum PrometheusViewerEntryId {
    /**
     * Error.
     */
    ERROR,

    /**
     * Profile.
     */
    PROFILE,

    /**
     * Data.
     */
    DATA,

    /**
     * View.
     */
    VIEW,

    /**
     * DataSet.
     */
    DATASET,

    /**
     * Updates.
     */
    UPDATES,

    /**
     * Analysis.
     */
    ANALYSIS,

    /**
     * Maintenance.
     */
    MAINTENANCE,

    /**
     * StaticData.
     */
    STATIC;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForEntryId(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the resource bundleId for the entryId.
     *
     * @param pId the id
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForEntryId(final PrometheusViewerEntryId pId) {
        return switch (pId) {
            case ERROR -> MetisViewerResource.VIEWER_ENTRY_ERROR;
            case PROFILE -> MetisViewerResource.VIEWER_ENTRY_PROFILE;
            case DATA -> MetisViewerResource.VIEWER_ENTRY_DATA;
            case VIEW -> MetisViewerResource.VIEWER_ENTRY_VIEW;
            case DATASET -> PrometheusViewResource.VIEWERENTRY_DATASET;
            case UPDATES -> PrometheusViewResource.VIEWERENTRY_UPDATES;
            case ANALYSIS -> PrometheusViewResource.VIEWERENTRY_ANALYSIS;
            case MAINTENANCE -> PrometheusViewResource.VIEWERENTRY_MAINT;
            case STATIC -> PrometheusViewResource.VIEWERENTRY_STATIC;
            default -> throw new IllegalArgumentException();
        };
    }
}
