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
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

import java.util.function.Function;

/**
 * InstanceMaps for Field.
 */
public class PrometheusMapsFieldInstance
        extends PrometheusMapsBaseInstance {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsFieldInstance> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsFieldInstance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE, PrometheusMapsFieldInstance::getListKey);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_FIELD, PrometheusMapsFieldInstance::getFieldId);
    }

    /**
     * listKey.
     */
    private final PrometheusListKey theListKey;

    /**
     * The fieldId.
     */
    private final MetisDataFieldId theFieldId;

    /**
     * The filter.
     */
    private final Function<PrometheusDataItem, Boolean> theFilter;

    /**
     * Constructor.
     * @param pKey the listKey
     * @param pFieldId the fieldId
     */
    PrometheusMapsFieldInstance(final PrometheusListKey pKey,
                                final MetisDataFieldId pFieldId) {
        this(pKey, pFieldId, i -> true);
    }

    /**
     * Constructor.
     * @param pKey the listKey
     * @param pFieldId the fieldId
     * @param pFilter the filter
     */
    PrometheusMapsFieldInstance(final PrometheusListKey pKey,
                                final MetisDataFieldId pFieldId,
                                final Function<PrometheusDataItem, Boolean> pFilter) {
        theListKey = pKey;
        theFieldId = pFieldId;
        theFilter = pFilter;
    }

    /**
     * Constructor.
     * @param pSource the source fieldMap
     */
    PrometheusMapsFieldInstance(final PrometheusMapsFieldInstance pSource) {
        theListKey = pSource.theListKey;
        theFieldId = pSource.theFieldId;
        theFilter = pSource.theFilter;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return PrometheusMapsFieldInstance.class.getSimpleName();
    }

    /**
     * Obtain the listKey.
     * @return the listKey
     */
    PrometheusListKey getListKey() {
        return theListKey;
    }

    /**
     * Obtain the fieldId.
     * @return the fieldId
     */
    MetisDataFieldId getFieldId() {
        return theFieldId;
    }

    /**
     * add item to map.
     * @param pItem the item
     */
    void addItemToMap(final PrometheusDataItem pItem) {
        if (theFilter.apply(pItem)) {
            final MetisFieldSetDef myFieldSet = pItem.getDataFieldSet();
            final MetisFieldDef myField = myFieldSet.getField(theFieldId);
            final Object myValue = myField.getFieldValue(pItem);
            if (myValue != null) {
                addItemToMap(myValue, pItem);
            }
        }
    }

    /**
     * Is the key duplicate?
     * @param pItem the item
     * @return true/false
     */
    boolean isKeyDuplicate(final PrometheusDataItem pItem) {
        if (theFilter.apply(pItem)) {
            final MetisFieldSetDef myFieldSet = pItem.getDataFieldSet();
            final MetisFieldDef myField = myFieldSet.getField(theFieldId);
            final Object myValue = myField.getFieldValue(pItem);
            return isKeyDuplicate(myValue);
        }
        return false;
    }

    /**
     * Find item in map.
     * @param pKey the key
     * @return the item
     */
    public PrometheusDataItem findItemInMap(final Object pKey) {
        return getItemForKey(pKey);
    }
}
