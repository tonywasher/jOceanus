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
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * InstanceMaps for List.
 */
public class PrometheusMapsListInstance
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsListInstance> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsListInstance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE, PrometheusMapsListInstance::getListKey);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_INSTANCEMAP, PrometheusMapsListInstance::getMap);
    }

    /**
     * listKey.
     */
    private final PrometheusListKey theListKey;

    /**
     * The item.
     */
    private final Map<MetisDataFieldId, PrometheusMapsFieldInstance> theMap;

    /**
     * Constructor.
     * @param pKey the listKey
     */
    PrometheusMapsListInstance(final PrometheusListKey pKey) {
        theListKey = pKey;
        theMap = new LinkedHashMap<>();
    }

    /**
     * Constructor.
     * @param pDataSet the new dataSet maps
     * @param pSource the source list map
     */
    PrometheusMapsListInstance(final PrometheusMapsDataSetInstance pDataSet,
                               final PrometheusMapsListInstance pSource) {
        /* Initialise class */
        this(pSource.getListKey());

        /* Recreate underlying maps */
        for (PrometheusMapsFieldInstance myMap : pSource.getMap().values()) {
            /* Access details */
            final MetisDataFieldId myFieldId = myMap.getFieldId();
            final PrometheusListKey myListKey = myMap.getListKey();

            /* If the map is not shared */
            if (theListKey.equals(myListKey)) {
                /* Create a new FieldMap */
                theMap.put(myFieldId, new PrometheusMapsFieldInstance(myMap));

                /* else this is a shared map */
            } else {
                /* Obtain the relevant list map and field map */
                final PrometheusMapsListInstance mySharedList = pDataSet.getList(myListKey);
                final PrometheusMapsFieldInstance mySharedField = mySharedList.getMap().get(myFieldId);
                theMap.put(myFieldId, mySharedField);
            }
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return pFormatter.formatObject(theListKey);
    }

    /**
     * Obtain the listKey.
     * @return the listKey
     */
    PrometheusListKey getListKey() {
        return theListKey;
    }

    /**
     * Obtain the map.
     * @return the map
     */
    private Map<MetisDataFieldId, PrometheusMapsFieldInstance> getMap() {
        return theMap;
    }

    /**
     * Declare fieldId map.
     * @param pFieldId the fieldId
     */
    void declareFieldIdMap(final MetisDataFieldId pFieldId) {
        theMap.put(pFieldId, new PrometheusMapsFieldInstance(theListKey, pFieldId));
    }

    /**
     * Declare fieldId map.
     * @param pFieldId the fieldId
     * @param pFilter the filter
     */
    void declareFieldIdMap(final MetisDataFieldId pFieldId,
                           final Function<PrometheusDataItem, Boolean> pFilter) {
        theMap.put(pFieldId, new PrometheusMapsFieldInstance(theListKey, pFieldId, pFilter));
    }

    /**
     * Declare shared fieldId map.
     * @param pFieldId the fieldId
     * @param pMap the shared map
     */
    void declareFieldIdMap(final MetisDataFieldId pFieldId,
                           final PrometheusMapsListInstance pMap) {
        theMap.put(pFieldId, pMap.getMap().get(pFieldId));
    }

    /**
     * add item to map.
     * @param pItem the item
     */
    void addItemToMaps(final PrometheusDataItem pItem) {
        /* Loop through the maps */
        for (PrometheusMapsFieldInstance myMap: theMap.values()) {
            myMap.addItemToMap(pItem);
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
        final PrometheusMapsFieldInstance myMap = theMap.get(pFieldId);
        return myMap != null && myMap.isKeyDuplicate(pItem);
    }

    /**
     * Obtain the item for the key.
     * @param pFieldId the fieldId
     * @param pKey the key
     * @return the item
     */
    PrometheusDataItem getItemForKey(final MetisDataFieldId pFieldId,
                                     final Object pKey) {
        final PrometheusMapsFieldInstance myMap = theMap.get(pFieldId);
        return myMap == null ? null : myMap.findItemInMap(pKey);
    }

    /**
     * Reset Maps.
     */
    void resetMaps() {
        /* Reset each map */
        for (PrometheusMapsFieldInstance myMap : theMap.values()) {
            myMap.resetMap();
        }
    }
}
