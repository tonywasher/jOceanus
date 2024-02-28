/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmetis.data;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for JMetis viewer.
 */
public enum MetisDataResource
        implements TethysBundleId, MetisDataFieldId {
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
    private static final Map<MetisDataDifference, TethysBundleId> DIFF_MAP = buildDifferenceMap();

    /**
     * The FieldValue Map.
     */
    private static final Map<MetisDataFieldValue, TethysBundleId> VALUE_MAP = buildValueMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(MetisDataException.class.getCanonicalName(),
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
        return "jMetis.data";
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
    private static Map<MetisDataDifference, TethysBundleId> buildDifferenceMap() {
        /* Create the map and return it */
        final Map<MetisDataDifference, TethysBundleId> myMap = new EnumMap<>(MetisDataDifference.class);
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
    protected static TethysBundleId getKeyForDifference(final MetisDataDifference pValue) {
        return TethysBundleLoader.getKeyForEnum(DIFF_MAP, pValue);
    }

    /**
     * Build value map.
     * @return the map
     */
    private static Map<MetisDataFieldValue, TethysBundleId> buildValueMap() {
        /* Create the map and return it */
        final Map<MetisDataFieldValue, TethysBundleId> myMap = new EnumMap<>(MetisDataFieldValue.class);
        myMap.put(MetisDataFieldValue.UNKNOWN, FIELDVALUE_UNKNOWN);
        myMap.put(MetisDataFieldValue.SKIP, FIELDVALUE_SKIP);
        return myMap;
    }

    /**
     * Obtain key for fieldValue.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysBundleId getKeyForFieldValue(final MetisDataFieldValue pValue) {
        return TethysBundleLoader.getKeyForEnum(VALUE_MAP, pValue);
    }
}
