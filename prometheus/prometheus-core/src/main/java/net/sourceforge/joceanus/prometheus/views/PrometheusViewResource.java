/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.views;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.metis.viewer.MetisViewerResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.tethys.resource.TethysBundleId;
import net.sourceforge.joceanus.tethys.resource.TethysBundleLoader;

/**
 * Resource IDs for jPrometheus View Fields.
 */
public enum PrometheusViewResource implements TethysBundleId {
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
     * The Entry Map.
     */
    private static final Map<PrometheusViewerEntryId, TethysBundleId> ENTRY_MAP = buildEntryMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(PrometheusDataSet.class.getCanonicalName(),
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
        return "jPrometheus.views";
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

    /**
     * Build entry map.
     * @return the map
     */
    private static Map<PrometheusViewerEntryId, TethysBundleId> buildEntryMap() {
        /* Create the map and return it */
        final Map<PrometheusViewerEntryId, TethysBundleId> myMap = new EnumMap<>(PrometheusViewerEntryId.class);
        myMap.put(PrometheusViewerEntryId.ERROR, MetisViewerResource.VIEWER_ENTRY_ERROR);
        myMap.put(PrometheusViewerEntryId.PROFILE, MetisViewerResource.VIEWER_ENTRY_PROFILE);
        myMap.put(PrometheusViewerEntryId.DATA, MetisViewerResource.VIEWER_ENTRY_DATA);
        myMap.put(PrometheusViewerEntryId.VIEW, MetisViewerResource.VIEWER_ENTRY_VIEW);
        myMap.put(PrometheusViewerEntryId.DATASET, VIEWERENTRY_DATASET);
        myMap.put(PrometheusViewerEntryId.UPDATES, VIEWERENTRY_UPDATES);
        myMap.put(PrometheusViewerEntryId.ANALYSIS, VIEWERENTRY_ANALYSIS);
        myMap.put(PrometheusViewerEntryId.MAINTENANCE, VIEWERENTRY_MAINT);
        myMap.put(PrometheusViewerEntryId.STATIC, VIEWERENTRY_STATIC);
        return myMap;
    }

    /**
     * Obtain key for stdEntry.
     * @param pEntry the entry
     * @return the resource key
     */
    protected static TethysBundleId getKeyForViewerEntry(final PrometheusViewerEntryId pEntry) {
        return TethysBundleLoader.getKeyForEnum(ENTRY_MAP, pEntry);
    }
}
