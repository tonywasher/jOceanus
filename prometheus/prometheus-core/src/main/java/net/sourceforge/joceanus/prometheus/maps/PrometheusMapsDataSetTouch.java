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
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

import java.util.HashMap;
import java.util.Map;

/**
 * DataTouchMap for dataSet/editSet.
 */
public class PrometheusMapsDataSetTouch
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsDataSetTouch> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusMapsDataSetTouch.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_TOUCHMAP, PrometheusMapsDataSetTouch::getTouchMap);
    }

    /**
     * The map of listKey to listMap.
     */
    private final Map<PrometheusListKey, PrometheusMapsListTouch> theListMap;

    /**
     * Constructor.
     */
    PrometheusMapsDataSetTouch() {
        theListMap = new HashMap<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return PrometheusMapsDataSetTouch.class.getSimpleName();
    }

    /**
     * Obtain the list map.
     * @return the map
     */
    private Map<PrometheusListKey, PrometheusMapsListTouch> getTouchMap() {
        return theListMap;
    }

    /**
     * Record touch.
     * @param pTouchedItem the item that is touched
     * @param pTouchingItem the item that touches
     */
    void recordTouch(final PrometheusDataItem pTouchedItem,
                     final PrometheusDataItem pTouchingItem) {
        /* Access correct map and record the touch */
        final PrometheusListKey myKey = pTouchedItem.getItemType();
        final PrometheusMapsListTouch myMap = theListMap.computeIfAbsent(myKey,
                PrometheusMapsListTouch::new);
        myMap.recordTouch(pTouchedItem, pTouchingItem);
    }

    /**
     * Is the item touched?
     * @param pItem the item
     * @return true/false
     */
    boolean isTouched(final PrometheusDataItem pItem) {
        final PrometheusMapsListTouch myMap = theListMap.get(pItem.getItemType());
        return myMap != null && myMap.isTouched(pItem);
    }

    /**
     * Reset the map.
     */
    void resetMap() {
        theListMap.clear();
    }
 }
