/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.data;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;

/**
 * Template for a Data Instance Map.
 * @author Tony Washer
 * @param <T> the data type
 * @param <E> the data type enum class
 * @param <K> the instance key
 */
public abstract class DataInstanceMap<T extends DataItem<E>, E extends Enum<E>, K>
        implements DataMapItem<T, E>, JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(PrometheusDataResource.DATAMAP_NAME.getValue());

    /**
     * NameMap Field Id.
     */
    private static final JDataField FIELD_KEYS = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAMAP_KEYS.getValue());

    /**
     * NameCountMap Field Id.
     */
    private static final JDataField FIELD_KEYCOUNTS = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAMAP_KEYCOUNTS.getValue());

    /**
     * Standard integer ONE.
     */
    public static final Integer ONE = Integer.valueOf(1);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_KEYS.equals(pField)) {
            return theKeyMap;
        }
        if (FIELD_KEYCOUNTS.equals(pField)) {
            return theKeyCountMap;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

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
    protected DataInstanceMap() {
        /* Create the maps */
        theKeyMap = new HashMap<K, T>();
        theKeyCountMap = new HashMap<K, Integer>();
    }

    /**
     * Reset the map.
     */
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
        Integer myCount = theKeyCountMap.get(pKey);
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
        Integer myResult = theKeyCountMap.get(pKey);
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