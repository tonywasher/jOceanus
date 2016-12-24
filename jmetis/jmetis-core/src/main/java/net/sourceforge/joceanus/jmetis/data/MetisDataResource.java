/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.data;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for JMetis viewer.
 */
public enum MetisDataResource implements TethysResourceId {
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
    ERRORLIST_NAME("ErrorList.Name");

    /**
     * The Difference Map.
     */
    private static final Map<MetisDifference, TethysResourceId> DIFF_MAP = buildDifferenceMap();

    /**
     * The FieldValue Map.
     */
    private static final Map<MetisFieldValue, TethysResourceId> VALUE_MAP = buildValueMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(MetisDataException.class.getCanonicalName());

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
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build difference map.
     * @return the map
     */
    private static Map<MetisDifference, TethysResourceId> buildDifferenceMap() {
        /* Create the map and return it */
        Map<MetisDifference, TethysResourceId> myMap = new EnumMap<>(MetisDifference.class);
        myMap.put(MetisDifference.IDENTICAL, DIFFERENCE_IDENTICAL);
        myMap.put(MetisDifference.SECURITY, DIFFERENCE_SECURITY);
        myMap.put(MetisDifference.DIFFERENT, DIFFERENCE_DIFFERENT);
        return myMap;
    }

    /**
     * Obtain key for difference.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForDifference(final MetisDifference pValue) {
        return TethysResourceBuilder.getKeyForEnum(DIFF_MAP, pValue);
    }

    /**
     * Build value map.
     * @return the map
     */
    private static Map<MetisFieldValue, TethysResourceId> buildValueMap() {
        /* Create the map and return it */
        Map<MetisFieldValue, TethysResourceId> myMap = new EnumMap<>(MetisFieldValue.class);
        myMap.put(MetisFieldValue.UNKNOWN, FIELDVALUE_UNKNOWN);
        myMap.put(MetisFieldValue.SKIP, FIELDVALUE_SKIP);
        return myMap;
    }

    /**
     * Obtain key for fieldValue.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForFieldValue(final MetisFieldValue pValue) {
        return TethysResourceBuilder.getKeyForEnum(VALUE_MAP, pValue);
    }
}
