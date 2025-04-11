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
import net.sourceforge.joceanus.prometheus.maps.PrometheusMapsInstanceElement.PrometheusMapsInstanceElementItem;
import net.sourceforge.joceanus.prometheus.maps.PrometheusMapsInstanceElement.PrometheusMapsInstanceElementList;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * InstanceMaps Base.
 */
public abstract class PrometheusMapsBaseInstance
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsBaseInstance> FIELD_DEFS
            = MetisFieldSet.newFieldSet(PrometheusMapsBaseInstance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_INSTANCEMAP, PrometheusMapsBaseInstance::getMap);
    }

    /**
     * The map.
     */
    private final Map<Object, PrometheusMapsInstanceElement> theMap;

    /**
     * Constructor.
     */
    PrometheusMapsBaseInstance() {
        theMap = new LinkedHashMap<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return PrometheusMapsBaseInstance.class.getSimpleName();
    }

    /**
     * Obtain the map.
     * @return the map
     */
    private Map<Object, PrometheusMapsInstanceElement> getMap() {
        return theMap;
    }

    /**
     * add item to map.
     * @param pKey the key for the item
     * @param pItem the item
     */
    void addItemToMap(final Object pKey,
                      final PrometheusDataItem pItem) {
        final PrometheusMapsInstanceElement myCurr = theMap.get(pKey);
        if (myCurr != null) {
            theMap.put(pKey, new PrometheusMapsInstanceElementList(myCurr, pItem));
        } else {
            theMap.put(pKey, new PrometheusMapsInstanceElementItem(pItem));
        }
    }

    /**
     * Is the key duplicate?
     * @param pKey the key
     * @return true/false
     */
    boolean isKeyDuplicate(final Object pKey) {
        return theMap.get(pKey) instanceof PrometheusMapsInstanceElementList;
    }

    /**
     * Is the key available?
     * @param pKey the key
     * @return true/false
     */
    boolean isKeyAvailable(final Object pKey) {
        return theMap.get(pKey) == null;
    }

    /**
     * Obtain the item for the key.
     * @param pKey the key
     * @return the item
     */
    PrometheusDataItem getItemForKey(final Object pKey) {
        final PrometheusMapsInstanceElement myElement = theMap.get(pKey);
        if (myElement instanceof PrometheusMapsInstanceElementItem myItem) {
            return myItem.getItem();
        }
        if (myElement instanceof PrometheusMapsInstanceElementList) {
            return myElement.getList().get(0);
        }
        return null;
    }

    /**
     * Reset Maps.
     */
    void resetMap() {
        theMap.clear();
    }
}
