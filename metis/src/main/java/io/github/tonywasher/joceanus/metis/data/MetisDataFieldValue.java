/*
 * Metis: Java Data Framework
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
package io.github.tonywasher.joceanus.metis.data;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Special values for return by getFieldValue.
 */
public enum MetisDataFieldValue {
    /**
     * Field not known.
     */
    UNKNOWN,

    /**
     * Field to be skipped.
     */
    SKIP;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForFieldValue(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the resource bundleId for the fieldValue.
     *
     * @param pValue the value
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForFieldValue(final MetisDataFieldValue pValue) {
        /* Create the map and return it */
        return switch (pValue) {
            case UNKNOWN -> MetisDataResource.FIELDVALUE_UNKNOWN;
            case SKIP -> MetisDataResource.FIELDVALUE_SKIP;
            default -> throw new IllegalArgumentException();
        };
    }
}
