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
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * InstanceMaps for DataSet.
 */
public class PrometheusMapsDataSetInstance
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsDataSetInstance> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsDataSetInstance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_INSTANCEMAP, PrometheusMapsDataSetInstance::getMap);
    }

    /**
     * The item.
     */
    private final Map<PrometheusListKey, PrometheusMapsListInstance> theMap;

    /**
     * Constructor.
     */
    PrometheusMapsDataSetInstance() {
        theMap = new LinkedHashMap<>();
    }

    /**
     * Constructor.
     * @param pSource the source dataset map
     */
    PrometheusMapsDataSetInstance(final PrometheusMapsDataSetInstance pSource) {
        this();

        /* Recreate underlying maps */
        for (PrometheusMapsListInstance myMap : pSource.getMap().values()) {
            /* Create a new ListMap */
            theMap.put(myMap.getListKey(), new PrometheusMapsListInstance(this, myMap));
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return PrometheusMapsDataSetInstance.class.getSimpleName();
    }

    /**
     * Obtain the map.
     * @return the map
     */
    private Map<PrometheusListKey, PrometheusMapsListInstance> getMap() {
        return theMap;
    }

    /**
     * Obtain the list map for listKey.
     * @param pKey the listKey
     * @return the map
     */
    PrometheusMapsListInstance getList(final PrometheusListKey pKey) {
        return theMap.get(pKey);
    }

    /**
     * Declare fieldId map.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     */
    void declareFieldIdMap(final PrometheusListKey pListKey,
                           final MetisDataFieldId pFieldId) {
        final PrometheusMapsListInstance myMap = theMap.computeIfAbsent(pListKey, PrometheusMapsListInstance::new);
        myMap.declareFieldIdMap(pFieldId);
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
        final PrometheusMapsListInstance myMap = theMap.computeIfAbsent(pListKey, PrometheusMapsListInstance::new);
        myMap.declareFieldIdMap(pFieldId, pFilter);
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
        final PrometheusMapsListInstance myMap = theMap.computeIfAbsent(pListKey, PrometheusMapsListInstance::new);
        final PrometheusMapsListInstance mySharedMap = theMap.get(pSharedKey);
        myMap.declareFieldIdMap(pFieldId, mySharedMap);
    }

    /**
     * add item to map.
     * @param pItem the item
     */
    void addItemToMaps(final PrometheusDataItem pItem) {
        final PrometheusMapsListInstance myMap = theMap.get(pItem.getItemType());
        if (myMap != null) {
            myMap.addItemToMaps(pItem);
        }
    }

    /**
     * Is the key duplicate?
     * @param pFieldId the fieldId
     * @param pItem the item
     * @return true/false
     */
    boolean isKeyDuplicate(final MetisDataFieldId pFieldId,
                           final PrometheusDataItem pItem) {
        final PrometheusMapsListInstance myMap = theMap.get(pItem.getItemType());
        return myMap != null && myMap.isKeyDuplicate(pFieldId, pItem);
    }

    /**
     * Is the key available?
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     * @param pKey the key
     * @return true/false
     */
    boolean isKeyAvailable(final PrometheusListKey pListKey,
                           final MetisDataFieldId pFieldId,
                           final Object pKey) {
        final PrometheusMapsListInstance myMap = theMap.get(pListKey);
        return myMap == null || myMap.isKeyAvailable(pFieldId, pKey);
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
        final PrometheusMapsListInstance myMap = theMap.get(pListKey);
        return myMap == null ? null : myMap.getItemForKey(pFieldId, pKey);
    }

    /**
     * Reset Maps.
     */
    void resetMaps() {
        /* Reset each map */
        for (PrometheusMapsListInstance myMap : theMap.values()) {
            myMap.resetMaps();
        }
    }
}
