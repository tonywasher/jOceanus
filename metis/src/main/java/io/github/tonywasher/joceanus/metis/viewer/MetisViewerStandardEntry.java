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
package io.github.tonywasher.joceanus.metis.viewer;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Standard Viewer Entries.
 */
public enum MetisViewerStandardEntry {
    /**
     * Error entry.
     */
    ERROR,

    /**
     * Profile entry.
     */
    PROFILE,

    /**
     * Data entry.
     */
    DATA,

    /**
     * Updates entry.
     */
    UPDATES,

    /**
     * View entry.
     */
    VIEW,

    /**
     * Preferences entry.
     */
    PREFERENCES;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForEntry(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the resource bundleId for the entry.
     *
     * @param pEntry the entry
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForEntry(final MetisViewerStandardEntry pEntry) {
        /* Create the map and return it */
        return switch (pEntry) {
            case ERROR -> MetisViewerResource.VIEWER_ENTRY_ERROR;
            case PROFILE -> MetisViewerResource.VIEWER_ENTRY_PROFILE;
            case DATA -> MetisViewerResource.VIEWER_ENTRY_DATA;
            case UPDATES -> MetisViewerResource.VIEWER_ENTRY_UPDATES;
            case VIEW -> MetisViewerResource.VIEWER_ENTRY_VIEW;
            case PREFERENCES -> MetisViewerResource.VIEWER_ENTRY_PREF;
            default -> throw new IllegalArgumentException();
        };
    }
}
