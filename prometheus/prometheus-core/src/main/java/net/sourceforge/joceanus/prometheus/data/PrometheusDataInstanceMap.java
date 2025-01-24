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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Template for a Data Instance Map.
 * @author Tony Washer
 * @param <T> the data type
 * @param <K> the instance key
 */
public abstract class PrometheusDataInstanceMap<T extends PrometheusDataItem, K>
        implements PrometheusDataMapItem, MetisFieldItem {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<PrometheusDataInstanceMap> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusDataInstanceMap.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAMAP_KEYS, PrometheusDataInstanceMap::getKeyMap);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAMAP_KEYCOUNTS, PrometheusDataInstanceMap::getKeyCountMap);
    }

    /**
     * Standard integer ONE.
     */
    public static final Integer ONE = Integer.valueOf(1);

    /**
     * Map of keys.
     */
    private final Map<K, T> theKeyMap;

    /**
     * Map of key counts.
     */
    private final Map<K, Integer> theKeyCountMap;

    /**
     * Constructor.
     */
    protected PrometheusDataInstanceMap() {
        /* Create the maps */
        theKeyMap = new HashMap<>();
        theKeyCountMap = new HashMap<>();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetisFieldSet<? extends PrometheusDataInstanceMap> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the keyMap.
     * @return the map
     */
    private Map<K, T> getKeyMap() {
        return theKeyMap;
    }

    /**
     * Obtain the keyCountMap.
     * @return the map
     */
    private Map<K, Integer> getKeyCountMap() {
        return theKeyCountMap;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public void resetMap() {
        theKeyMap.clear();
        theKeyCountMap.clear();
    }

    /**
     * adjust maps for item.
     * @param pItem the item to map
     * @param pKey the key
     */
    protected void adjustForItem(final T pItem,
                                 final K pKey) {
        /* Adjust key count */
        final Integer myCount = theKeyCountMap.get(pKey);
        theKeyCountMap.put(pKey, myCount == null
                ? ONE
                : myCount + 1);

        /* Adjust key map */
        theKeyMap.put(pKey, pItem);
    }

    /**
     * find item by key.
     * @param pKey the key to look up
     * @return the matching item
     */
    public T findItemByKey(final K pKey) {
        return theKeyMap.get(pKey);
    }

    /**
     * Check validity of key.
     * @param pKey the key to look up
     * @return true/false
     */
    public boolean validKeyCount(final K pKey) {
        final Integer myResult = theKeyCountMap.get(pKey);
        return ONE.equals(myResult);
    }

    /**
     * Check availability of key.
     * @param pKey the key to look up
     * @return true/false
     */
    public boolean availableKey(final K pKey) {
        return theKeyCountMap.get(pKey) == null;
    }
}
