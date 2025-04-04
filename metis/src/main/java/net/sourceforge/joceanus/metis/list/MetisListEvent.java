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
package net.sourceforge.joceanus.metis.list;

/**
 * The Event Types.
 */
public enum MetisListEvent {
    /**
     * Increment.
     */
    INCREMENT,

    /**
     * Rewind.
     */
    REWIND,

    /**
     * Version.
     */
    VERSION,

    /**
     * Refresh.
     */
    REFRESH,

    /**
     * Error.
     */
    ERROR;

    /**
     * Does the event have content?
     * @return true/false
     */
    public boolean hasContent() {
        switch (this) {
            case INCREMENT:
            case VERSION:
                return true;
            default:
                return false;
        }
    }
}
