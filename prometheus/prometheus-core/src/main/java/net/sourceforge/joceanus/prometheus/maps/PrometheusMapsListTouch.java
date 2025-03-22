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

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

import java.util.HashMap;
import java.util.Map;

/**
 * DataTouchMap for List.
 */
public class PrometheusMapsListTouch
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsListTouch> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsListTouch.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_TYPE, PrometheusMapsListTouch::getListKey);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_TOUCHMAP, PrometheusMapsListTouch::getTouchMap);
    }

    /**
     * listKey.
     */
    private final PrometheusListKey theListKey;

    /**
     * The map of itemId to touch map.
     */
    private final Map<Integer, PrometheusMapsItemTouch> theTouchMap;

    /**
     * Constructor.
     * @param pKey the listKey
     */
    PrometheusMapsListTouch(final PrometheusListKey pKey) {
        theListKey = pKey;
        theTouchMap = new HashMap<>();
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
    private PrometheusListKey getListKey() {
        return theListKey;
    }

    /**
     * Obtain the touchedBy map.
     * @return the map
     */
    private Map<Integer, PrometheusMapsItemTouch> getTouchMap() {
        return theTouchMap;
    }

    /**
     * Record touch.
     * @param pTouchedItem the item that is touched
     * @param pTouchingItem the item that touches
     */
    void recordTouch(final PrometheusDataItem pTouchedItem,
                     final PrometheusDataItem pTouchingItem) {
        /* Access correct touchedBy map */
        PrometheusMapsItemTouch myMap = theTouchMap.computeIfAbsent(pTouchedItem.getIndexedId(),
                i -> new PrometheusMapsItemTouch(pTouchedItem));
        myMap.touchedByItem(pTouchingItem);

        /* Access correct touches map */
        myMap = theTouchMap.computeIfAbsent(pTouchingItem.getIndexedId(),
                i -> new PrometheusMapsItemTouch(pTouchedItem));
        myMap.touchesItem(pTouchedItem);
    }

    /**
     * Is the item touched?
     * @param pItem the item
     * @return true/false
     */
    boolean isTouched(final PrometheusDataItem pItem) {
        final PrometheusMapsItemTouch myMap = theTouchMap.get(pItem.getIndexedId());
        return myMap != null && myMap.isTouched();
    }
}
