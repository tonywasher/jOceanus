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
package io.github.tonywasher.joceanus.prometheus.maps;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;

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
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_INSTANCEMAP, PrometheusMapsListInstance::getFieldMap);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_DATEMAP, PrometheusMapsListInstance::getDateMap);
    }

    /**
     * listKey.
     */
    private final MetisListKey theListKey;

    /**
     * The field maps.
     */
    private final Map<MetisDataFieldId, PrometheusMapsFieldInstance> theFieldMap;

    /**
     * The date maps.
     */
    private PrometheusMapsDateInstance theDateMap;

    /**
     * Constructor.
     *
     * @param pKey the listKey
     */
    PrometheusMapsListInstance(final MetisListKey pKey) {
        theListKey = pKey;
        theFieldMap = new LinkedHashMap<>();
    }

    /**
     * Constructor.
     *
     * @param pDataSet the new dataSet maps
     * @param pSource  the source list map
     */
    PrometheusMapsListInstance(final PrometheusMapsDataSetInstance pDataSet,
                               final PrometheusMapsListInstance pSource) {
        /* Initialise class */
        this(pSource.getListKey());

        /* Recreate underlying maps */
        for (PrometheusMapsFieldInstance myMap : pSource.getFieldMap().values()) {
            /* Access details */
            final MetisDataFieldId myFieldId = myMap.getFieldId();
            final MetisListKey myListKey = myMap.getListKey();

            /* If the map is not shared */
            if (theListKey.equals(myListKey)) {
                /* Create a new FieldMap */
                theFieldMap.put(myFieldId, new PrometheusMapsFieldInstance(myMap));

                /* else this is a shared map */
            } else {
                /* Obtain the relevant list map and field map */
                final PrometheusMapsListInstance mySharedList = pDataSet.getList(myListKey);
                final PrometheusMapsFieldInstance mySharedField = mySharedList.getFieldMap().get(myFieldId);
                theFieldMap.put(myFieldId, mySharedField);
            }
        }

        /* Recreate dateMap if required */
        if (theDateMap != null) {
            theDateMap = new PrometheusMapsDateInstance(theDateMap);
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
     *
     * @return the listKey
     */
    MetisListKey getListKey() {
        return theListKey;
    }

    /**
     * Obtain the field map.
     *
     * @return the map
     */
    private Map<MetisDataFieldId, PrometheusMapsFieldInstance> getFieldMap() {
        return theFieldMap;
    }

    /**
     * Obtain the date map.
     *
     * @return the map
     */
    private PrometheusMapsDateInstance getDateMap() {
        return theDateMap;
    }

    /**
     * Declare fieldId map.
     *
     * @param pFieldId the fieldId
     */
    void declareFieldIdMap(final MetisDataFieldId pFieldId) {
        theFieldMap.put(pFieldId, new PrometheusMapsFieldInstance(theListKey, pFieldId));
    }

    /**
     * Declare fieldId map.
     *
     * @param pFieldId the fieldId
     * @param pFilter  the filter
     */
    void declareFieldIdMap(final MetisDataFieldId pFieldId,
                           final Function<PrometheusDataItem, Boolean> pFilter) {
        theFieldMap.put(pFieldId, new PrometheusMapsFieldInstance(theListKey, pFieldId, pFilter));
    }

    /**
     * Declare shared fieldId map.
     *
     * @param pFieldId the fieldId
     * @param pMap     the shared map
     */
    void declareFieldIdMap(final MetisDataFieldId pFieldId,
                           final PrometheusMapsListInstance pMap) {
        theFieldMap.put(pFieldId, pMap.getFieldMap().get(pFieldId));
    }

    /**
     * Declare dateId map.
     *
     * @param pOwnerId   the ownerId
     * @param pDateId    the dateId
     * @param pAllowNull do we allow null value?
     */
    void declareDateIdMap(final MetisDataFieldId pOwnerId,
                          final MetisDataFieldId pDateId,
                          final boolean pAllowNull) {
        theDateMap = new PrometheusMapsDateInstance(theListKey, pOwnerId, pDateId, pAllowNull);
    }

    /**
     * add item to maps.
     *
     * @param pItem the item
     */
    void addItemToMaps(final PrometheusDataItem pItem) {
        /* Loop through the field maps */
        for (PrometheusMapsFieldInstance myMap : theFieldMap.values()) {
            myMap.addItemToMap(pItem);
        }

        /* If the date map exists */
        if (theDateMap != null) {
            theDateMap.addItemToMap(pItem);
        }
    }

    /**
     * Is the key duplicate?
     *
     * @param pFieldId the fieldId
     * @param pItem    the item
     * @return true/false
     */
    boolean isKeyDuplicate(final MetisDataFieldId pFieldId,
                           final PrometheusDataItem pItem) {
        final PrometheusMapsFieldInstance myMap = theFieldMap.get(pFieldId);
        return myMap != null && myMap.isKeyDuplicate(pItem);
    }

    /**
     * Is the key available?
     *
     * @param pFieldId the fieldId
     * @param pKey     the key
     * @return true/false
     */
    boolean isKeyAvailable(final MetisDataFieldId pFieldId,
                           final Object pKey) {
        final PrometheusMapsFieldInstance myMap = theFieldMap.get(pFieldId);
        return myMap == null || myMap.isKeyAvailable(pKey);
    }

    /**
     * Obtain the item for the key.
     *
     * @param pFieldId the fieldId
     * @param pKey     the key
     * @return the item
     */
    PrometheusDataItem getItemForKey(final MetisDataFieldId pFieldId,
                                     final Object pKey) {
        final PrometheusMapsFieldInstance myMap = theFieldMap.get(pFieldId);
        return myMap == null ? null : myMap.findItemInMap(pKey);
    }

    /**
     * Reset Maps.
     */
    void resetMaps() {
        /* Reset each field map */
        for (PrometheusMapsFieldInstance myMap : theFieldMap.values()) {
            myMap.resetMap();
        }

        /* Reset the date map */
        if (theDateMap != null) {
            theDateMap.resetMap();
        }
    }

    /**
     * Is the date available?
     *
     * @param pOwner the owner
     * @param pDate  the date
     * @return true/false
     */
    boolean isDateAvailable(final PrometheusDataItem pOwner,
                            final OceanusDate pDate) {
        return theDateMap != null && theDateMap.isDateAvailable(pOwner, pDate);
    }

    /**
     * Is the date duplicate?
     *
     * @param pItem the item
     * @return true/false
     */
    boolean isDateDuplicate(final PrometheusDataItem pItem) {
        return theDateMap != null && theDateMap.isDateDuplicate(pItem);
    }
}
