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

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * DataTouchMap for data/EditSet.
 */
public class PrometheusMapsInstanceCtl
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsInstanceCtl> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusMapsInstanceCtl.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.DATA_VERSION, PrometheusMapsInstanceCtl::getVersion);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_INSTANCEMAP, PrometheusMapsInstanceCtl::getInstanceMap);
    }

    /**
     * The history.
     */
    private final Map<Integer, PrometheusMapsDataSetInstance> theHistory;

    /**
     * The current version.
     */
    private Integer theVersion;

    /**
     * The current dataSetInstanceMap.
     */
    private PrometheusMapsDataSetInstance theInstanceMap;

    /**
     * Constructor.
     */
    PrometheusMapsInstanceCtl() {
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
        return PrometheusMapsInstanceCtl.class.getSimpleName();
    }

    /**
     * Obtain the version.
     * @return the version
     */
    private Integer getVersion() {
        return theVersion;
    }

    /**
     * Obtain the instance map.
     * @return the map
     */
    private PrometheusMapsDataSetInstance getInstanceMap() {
        return theInstanceMap;
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
        theInstanceMap = theInstanceMap == null
                ? new PrometheusMapsDataSetInstance()
                : theHistory.computeIfAbsent(pVersion, i -> new PrometheusMapsDataSetInstance(theInstanceMap));
        theVersion = pVersion;
    }

    /**
     * Declare fieldId map.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     */
    void declareFieldIdMap(final PrometheusListKey pListKey,
                           final MetisDataFieldId pFieldId) {
        theInstanceMap.declareFieldIdMap(pListKey, pFieldId);
    }

    /**
     * Declare fieldId map.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     * @param pFilter the filter
     */
    void declareFieldIdMap(final PrometheusListKey pListKey,
                           final MetisDataFieldId pFieldId,
                           final Function<PrometheusDataItem, Boolean> pFilter) {
        theInstanceMap.declareFieldIdMap(pListKey, pFieldId, pFilter);
    }

    /**
     * Declare shared fieldId map.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     * @param pSharedKey the shared listKey
     */
    void declareFieldIdMap(final PrometheusListKey pListKey,
                           final MetisDataFieldId pFieldId,
                           final PrometheusListKey pSharedKey) {
        theInstanceMap.declareFieldIdMap(pListKey, pFieldId, pSharedKey);
    }

    /**
     * add item to map.
     * @param pItem the item
     */
    void addItemToMaps(final PrometheusDataItem pItem) {
        theInstanceMap.addItemToMaps(pItem);
    }

    /**
     * Is the key duplicate?
     * @param pFieldId the fieldId
     * @param pItem the item
     * @return true/false
     */
    boolean isKeyDuplicate(final MetisDataFieldId pFieldId,
                           final PrometheusDataItem pItem) {
         return theInstanceMap.isKeyDuplicate(pFieldId, pItem);
    }

    /**
     * Obtain the item for the key.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     * @param pKey the key
     * @return the item
     */
    PrometheusDataItem getItemForKey(final PrometheusListKey pListKey,
                                     final MetisDataFieldId pFieldId,
                                     final Object pKey) {
        return theInstanceMap.getItemForKey(pListKey, pFieldId, pKey);
    }

    /**
     * Reset the maps.
     */
    void resetMaps() {
        theInstanceMap.resetMaps();
    }
}
