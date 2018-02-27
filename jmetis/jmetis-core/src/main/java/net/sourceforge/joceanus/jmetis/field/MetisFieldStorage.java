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
package net.sourceforge.joceanus.jmetis.field;

/**
 * Field Storage.
 */
public enum MetisFieldStorage {
    /**
     * Local.
     */
    LOCAL,

    /**
     * Versioned.
     */
    VERSIONED,

    /**
     * Paired.
     */
    PAIRED,

    /**
     * Calculated.
     */
    CALCULATED;

    /**
     * Is the field versioned?
     * @return true/false
     */
    public boolean isVersioned() {
        switch (this) {
            case VERSIONED:
            case PAIRED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the field paired?
     * @return true/false
     */
    public boolean isPaired() {
        return this == PAIRED;
    }

    /**
     * Is the field calculated?
     * @return true/false
     */
    public boolean isCalculated() {
        return this == CALCULATED;
    }
}
