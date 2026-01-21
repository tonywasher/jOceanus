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
package net.sourceforge.joceanus.prometheus.maps;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Instance maps for dates.
 */
public class PrometheusMapsDateInstance
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsDateInstance> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsDateInstance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE, PrometheusMapsDateInstance::getListKey);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_FIELD, PrometheusMapsDateInstance::getOwnerId);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_INSTANCEMAP, PrometheusMapsDateInstance::getMap);
    }

    /**
     * listKey.
     */
    private final MetisListKey theListKey;

    /**
     * The owner fieldId.
     */
    private final MetisDataFieldId theOwnerId;

    /**
     * The date fieldId.
     */
    private final MetisDataFieldId theDateId;

    /**
     * The map of owner to dateMap.
     */
    private final Map<PrometheusDataItem, PrometheusMapsFieldInstance> theMap;

    /**
     * Do we allow null value?
     */
    private final boolean allowNull;

    /**
     * Constructor.
     *
     * @param pListKey   the listKey
     * @param pOwnerId   the ownerFieldId
     * @param pDateId    the date fieldId
     * @param pAllowNull do we allow null value?
     */
    PrometheusMapsDateInstance(final MetisListKey pListKey,
                               final MetisDataFieldId pOwnerId,
                               final MetisDataFieldId pDateId,
                               final boolean pAllowNull) {
        theListKey = pListKey;
        theOwnerId = pOwnerId;
        theDateId = pDateId;
        allowNull = pAllowNull;
        theMap = new LinkedHashMap<>();
    }

    /**
     * Constructor.
     *
     * @param pSource the source map
     */
    PrometheusMapsDateInstance(final PrometheusMapsDateInstance pSource) {
        theListKey = pSource.getListKey();
        theOwnerId = pSource.getOwnerId();
        theDateId = pSource.getDateId();
        theMap = new LinkedHashMap<>();
        allowNull = pSource.allowNull;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return PrometheusMapsDateInstance.class.getSimpleName();
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
     * Obtain the ownerId.
     *
     * @return the ownerId
     */
    MetisDataFieldId getOwnerId() {
        return theOwnerId;
    }

    /**
     * Obtain the dateId.
     *
     * @return the dateId
     */
    MetisDataFieldId getDateId() {
        return theDateId;
    }

    /**
     * Obtain the map.
     *
     * @return the fieldId
     */
    Map<PrometheusDataItem, PrometheusMapsFieldInstance> getMap() {
        return theMap;
    }

    /**
     * add item to map.
     *
     * @param pItem the item
     */
    void addItemToMap(final PrometheusDataItem pItem) {
        /* Access the owner */
        final MetisFieldSetDef myFieldSet = pItem.getDataFieldSet();
        final MetisFieldDef myField = myFieldSet.getField(theOwnerId);
        final PrometheusDataItem myOwner = myField.getFieldValue(pItem, PrometheusDataItem.class);

        /* Add to the date map */
        final PrometheusMapsFieldInstance myMap = theMap.computeIfAbsent(myOwner,
                r -> new PrometheusMapsFieldInstance(theListKey, theDateId, allowNull));
        myMap.addItemToMap(pItem);
    }

    /**
     * Is the date duplicate?
     *
     * @param pItem the item
     * @return true/false
     */
    boolean isDateDuplicate(final PrometheusDataItem pItem) {
        /* Access the owner */
        final MetisFieldSetDef myFieldSet = pItem.getDataFieldSet();
        MetisFieldDef myField = myFieldSet.getField(theOwnerId);
        final PrometheusDataItem myOwner = myField.getFieldValue(pItem, PrometheusDataItem.class);

        /* Check the map for duplicate */
        final PrometheusMapsFieldInstance myMap = theMap.get(myOwner);
        if (myMap != null) {
            myField = myFieldSet.getField(theDateId);
            final Object myDate = myField.getFieldValue(pItem);
            return myMap.isKeyDuplicate(myDate);
        }
        return false;
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
        final PrometheusMapsFieldInstance myMap = theMap.get(pOwner);
        return myMap == null || myMap.isKeyAvailable(pDate);
    }

    /**
     * Reset Maps.
     */
    void resetMap() {
        theMap.clear();
    }
}
