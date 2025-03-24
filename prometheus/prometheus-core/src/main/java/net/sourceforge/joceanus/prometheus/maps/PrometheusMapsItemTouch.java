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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DataTouchMap for Item.
 */
public class PrometheusMapsItemTouch
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsItemTouch> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsItemTouch.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_ITEM, PrometheusMapsItemTouch::getItem);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.TOUCH_TOUCHEDBY, PrometheusMapsItemTouch::getTouchedByMap);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.TOUCH_TOUCHES, PrometheusMapsItemTouch::getTouchesMap);
    }

    /**
     * Item itself.
     */
    private final PrometheusDataItem theItem;

    /**
     * The map of items that touch this item.
     */
    private final Map<PrometheusMapsItemId, PrometheusDataItem> theTouchedBy;

    /**
     * The map of items that this item touches.
     */
    private final Map<PrometheusMapsItemId, PrometheusDataItem> theTouches;

    /**
     * Constructor.
     * @param pItem the item
     */
    PrometheusMapsItemTouch(final PrometheusDataItem pItem) {
        theItem = pItem;
        theTouchedBy = new LinkedHashMap<>();
        theTouches = new LinkedHashMap<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return theItem.formatObject(pFormatter);
    }

    /**
     * Obtain the item.
     * @return the item
     */
    private PrometheusDataItem getItem() {
        return theItem;
    }

    /**
     * Obtain the touchedBy map.
     * @return the map
     */
    private Map<PrometheusMapsItemId, PrometheusDataItem> getTouchedByMap() {
        return theTouchedBy;
    }

    /**
     * Obtain the touches map.
     * @return the map
     */
    private Map<PrometheusMapsItemId, PrometheusDataItem> getTouchesMap() {
        return theTouches;
    }

    /**
     * Register touchedBy item.
     * @param pItem the item that touches this item
     */
    void touchedByItem(final PrometheusDataItem pItem) {
        final PrometheusMapsItemId myId = new PrometheusMapsItemId(pItem);
        theTouchedBy.put(myId, pItem);
    }

    /**
     * Register touches item.
     * @param pItem the item that is touched by this item
     */
    void touchesItem(final PrometheusDataItem pItem) {
        final PrometheusMapsItemId myId = new PrometheusMapsItemId(pItem);
        theTouches.put(myId, pItem);
    }

    /**
     * Is the item touched?
     * @return true/false
     */
    boolean isTouched() {
        return !theTouchedBy.isEmpty();
    }
}
