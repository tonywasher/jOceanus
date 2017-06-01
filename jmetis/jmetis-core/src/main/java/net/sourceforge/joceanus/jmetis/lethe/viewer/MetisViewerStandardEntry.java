/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.viewer;

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
            theName = MetisViewerResource.getKeyForStdEntry(this).getValue();
        }

        /* return the name */
        return theName;
    }
}
