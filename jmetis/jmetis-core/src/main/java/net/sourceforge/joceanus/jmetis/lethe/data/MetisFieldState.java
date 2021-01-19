/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.data;

/**
 * Enumeration of FieldSet states.
 * @author Tony Washer
 */
public enum MetisFieldState {
    /**
     * Normal state.
     */
    NORMAL,

    /**
     * Changed state.
     */
    CHANGED,

    /**
     * New state.
     */
    NEW,

    /**
     * Deleted state.
     */
    DELETED,

    /**
     * Restored state.
     */
    RESTORED,

    /**
     * Error state.
     */
    ERROR;

    /**
     * Is the state in error?
     * @return true/false
     */
    public boolean isError() {
        return this == ERROR;
    }

    /**
     * Is the state changed?
     * @return true/false
     */
    public boolean isChanged() {
        switch (this) {
            case CHANGED:
            case NEW:
            case RESTORED:
            case ERROR:
                return true;
            default:
                return false;
        }
    }
}
