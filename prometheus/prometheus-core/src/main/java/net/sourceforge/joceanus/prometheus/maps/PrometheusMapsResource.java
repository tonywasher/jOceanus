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

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleLoader;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;

import java.util.ResourceBundle;

/**
 * Resource IDs for jPrometheus Data Fields.
 */
public enum PrometheusMapsResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * Maps Item.
     */
    MAPS_ITEM("Item"),

    /**
     * Maps ItemList.
     */
    MAPS_ITEMLIST("ItemList"),

    /**
     * Maps Field.
     */
    MAPS_FIELD("Field"),

    /**
     * Map.
     */
    MAPS_DATEMAP("DateMap"),

    /**
     * Map.
     */
    MAPS_TOUCHMAP("TouchMap"),

    /**
     * Map.
     */
    MAPS_INSTANCEMAP("InstanceMap"),

    /**
     * Touched By Map.
     */
    TOUCH_TOUCHEDBY("Touch.TouchedBy"),

    /**
     * Touches Map.
     */
    TOUCH_TOUCHES("Touch.Touches");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(PrometheusDataSet.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     *
     * @param pKeyName the key name
     */
    PrometheusMapsResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "Prometheus.maps";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }

    @Override
    public String toString() {
        return getValue();
    }
}
