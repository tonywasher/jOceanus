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
package io.github.tonywasher.joceanus.metis.preference;

import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceKey.MetisPreferenceId;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Enum class for preference types.
 */
public enum MetisPreferenceType
        implements MetisPreferenceId {
    /**
     * String.
     */
    STRING,

    /**
     * Integer.
     */
    INTEGER,

    /**
     * Boolean.
     */
    BOOLEAN,

    /**
     * Date.
     */
    DATE,

    /**
     * File.
     */
    FILE,

    /**
     * Directory.
     */
    DIRECTORY,

    /**
     * Enum.
     */
    ENUM,

    /**
     * Colour.
     */
    COLOR;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForPreferenceType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the resource bundleId for the preferenceType.
     *
     * @param pType the type
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForPreferenceType(final MetisPreferenceType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case STRING -> MetisPreferenceResource.TYPE_STRING;
            case INTEGER -> MetisPreferenceResource.TYPE_INTEGER;
            case BOOLEAN -> MetisPreferenceResource.TYPE_BOOLEAN;
            case DATE -> MetisPreferenceResource.TYPE_DATE;
            case FILE -> MetisPreferenceResource.TYPE_FILE;
            case DIRECTORY -> MetisPreferenceResource.TYPE_DIRECTORY;
            case ENUM -> MetisPreferenceResource.TYPE_ENUM;
            case COLOR -> MetisPreferenceResource.TYPE_COLOR;
            default -> throw new IllegalArgumentException();
        };
    }
}
