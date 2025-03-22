/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.maps;

import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

import java.util.HashMap;
import java.util.Map;

/**
 * DataTouchMap for data/EditSet.
 */
public class PrometheusMapsTouchCtl
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsTouchCtl> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusMapsTouchCtl.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_VERSION, PrometheusMapsTouchCtl::getVersion);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_TOUCHMAP, PrometheusMapsTouchCtl::getTouchMap);
    }

    /**
     * The history.
     */
    private final Map<Integer, PrometheusMapsDataSetTouch> theHistory;

    /**
     * The current version.
     */
    private Integer theVersion;

    /**
     * The current dataSetTouchMap.
     */
    private PrometheusMapsDataSetTouch theTouchMap;

    /**
     * Constructor.
     */
    PrometheusMapsTouchCtl() {
        theHistory = new HashMap<>();
        theVersion = 0;
        setVersion(0);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return PrometheusMapsTouchCtl.class.getSimpleName();
    }

    /**
     * Obtain the version.
     * @return the version
     */
    private Integer getVersion() {
        return theVersion;
    }

    /**
     * Obtain the touch map.
     * @return the map
     */
    private PrometheusMapsDataSetTouch getTouchMap() {
        return theTouchMap;
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    void setVersion(final Integer pVersion) {
        /* If we are rolling back */
        if (pVersion < theVersion) {
            /* Delete any redundant versions */
            theHistory.entrySet().removeIf(myEntry -> myEntry.getKey() > pVersion);
        }

        /* Obtain the map */
        theTouchMap = theHistory.computeIfAbsent(pVersion, i -> new PrometheusMapsDataSetTouch());
        theVersion = pVersion;
    }

    /**
     * Record touch.
     * @param pTouchedItem the item that is touched
     * @param pTouchingItem the item that touches
     */
    void recordTouch(final PrometheusDataItem pTouchedItem,
                     final PrometheusDataItem pTouchingItem) {
        /* Access correct map and record the touch */
        theTouchMap.recordTouch(pTouchedItem, pTouchingItem);
    }

    /**
     * Is the item touched?
     * @param pItem the item
     * @return true/false
     */
    boolean isTouched(final PrometheusDataItem pItem) {
        return theTouchMap.isTouched(pItem);
    }

    /**
     * Reset the map.
     */
    void resetMap() {
        theTouchMap.resetMap();
    }
}
