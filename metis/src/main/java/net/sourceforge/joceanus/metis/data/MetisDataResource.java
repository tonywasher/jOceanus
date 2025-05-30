/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.metis.data;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for JMetis viewer.
 */
public enum MetisDataResource
        implements OceanusBundleId, MetisDataFieldId {
    /**
     * Difference IDENTICAL.
     */
    DIFFERENCE_IDENTICAL("difference.IDENTICAL"),

    /**
     * Difference SECURITY.
     */
    DIFFERENCE_SECURITY("difference.SECURITY"),

    /**
     * Difference DIFFERENT.
     */
    DIFFERENCE_DIFFERENT("difference.DIFFERENT"),

    /**
     * FieldValue UNKNOWN.
     */
    FIELDVALUE_UNKNOWN("field.UNKNOWN"),

    /**
     * FieldValue SKIP.
     */
    FIELDVALUE_SKIP("field.SKIP"),

    /**
     * Profile Object Name.
     */
    PROFILE_NAME("profile.Name"),

    /**
     * Profile Task Field.
     */
    PROFILE_TASK("profile.Task"),

    /**
     * Profile Status Field.
     */
    PROFILE_STATUS("profile.Status"),

    /**
     * Profile Elapsed Field.
     */
    PROFILE_ELAPSED("profile.Elapsed"),

    /**
     * Profile Hidden Field.
     */
    PROFILE_HIDDEN("profile.Hidden"),

    /**
     * List Size.
     */
    LIST_SIZE("List.Size"),

    /**
     * ErrorList Name.
     */
    ERRORLIST_NAME("ErrorList.Name"),

    /**
     * DataItem Id.
     */
    DATA_ID("item.Id"),

    /**
     * DataItem Type.
     */
    DATA_ITEMTYPE("item.Type"),

    /**
     * DataItem Version.
     */
    DATA_VERSION("item.Version"),

    /**
     * DataItem Deleted.
     */
    DATA_DELETED("item.Deleted"),

    /**
     * DataItem State.
     */
    DATA_STATE("item.State"),

    /**
     * DataItem EditState.
     */
    DATA_EDITSTATE("item.EditState"),

    /**
     * DataItem Errors.
     */
    DATA_ERRORS("item.Errors"),

    /**
     * DataItem History.
     */
    DATA_HISTORY("item.History"),

    /**
     * DataItem Parent.
     */
    DATA_PARENT("item.Parent"),

    /**
     * DataItem Child.
     */
    DATA_CHILD("item.Child");

    /**
     * The Difference Map.
     */
    private static final Map<MetisDataDifference, OceanusBundleId> DIFF_MAP = buildDifferenceMap();

    /**
     * The FieldValue Map.
     */
    private static final Map<MetisDataFieldValue, OceanusBundleId> VALUE_MAP = buildValueMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(MetisDataResource.class.getCanonicalName(),
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
     * @param pKeyName the key name
     */
    MetisDataResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "Metis.data";
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
    public String toString() {
        return getValue();
    }

    @Override
    public String getId() {
        return getValue();
    }

    /**
     * Build difference map.
     * @return the map
     */
    private static Map<MetisDataDifference, OceanusBundleId> buildDifferenceMap() {
        /* Create the map and return it */
        final Map<MetisDataDifference, OceanusBundleId> myMap = new EnumMap<>(MetisDataDifference.class);
        myMap.put(MetisDataDifference.IDENTICAL, DIFFERENCE_IDENTICAL);
        myMap.put(MetisDataDifference.SECURITY, DIFFERENCE_SECURITY);
        myMap.put(MetisDataDifference.DIFFERENT, DIFFERENCE_DIFFERENT);
        return myMap;
    }

    /**
     * Obtain key for difference.
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForDifference(final MetisDataDifference pValue) {
        return OceanusBundleLoader.getKeyForEnum(DIFF_MAP, pValue);
    }

    /**
     * Build value map.
     * @return the map
     */
    private static Map<MetisDataFieldValue, OceanusBundleId> buildValueMap() {
        /* Create the map and return it */
        final Map<MetisDataFieldValue, OceanusBundleId> myMap = new EnumMap<>(MetisDataFieldValue.class);
        myMap.put(MetisDataFieldValue.UNKNOWN, FIELDVALUE_UNKNOWN);
        myMap.put(MetisDataFieldValue.SKIP, FIELDVALUE_SKIP);
        return myMap;
    }

    /**
     * Obtain key for fieldValue.
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForFieldValue(final MetisDataFieldValue pValue) {
        return OceanusBundleLoader.getKeyForEnum(VALUE_MAP, pValue);
    }
}
