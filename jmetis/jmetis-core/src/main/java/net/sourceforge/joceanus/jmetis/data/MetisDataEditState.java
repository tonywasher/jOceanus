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
package net.sourceforge.joceanus.jmetis.data;

/**
 * Enumeration of edit states of DataItem and DataList objects in a view.
 */
public enum MetisDataEditState {
    /**
     * No changes made.
     */
    CLEAN,

    /**
     * Non-validated changes made.
     */
    DIRTY,

    /**
     * Only valid changes made.
     */
    VALID,

    /**
     * Object is in error.
     */
    ERROR;

    /**
     * Combine With another edit state.
     * @param pState edit state to combine with
     * @return the combined state
     */
    public MetisDataEditState combineState(final MetisDataEditState pState) {
        switch (this) {
            case ERROR:
                return this;
            case DIRTY:
                return pState == ERROR
                                       ? pState
                                       : this;
            case VALID:
                return pState != CLEAN
                                       ? pState
                                       : this;
            case CLEAN:
            default:
                return pState;
        }
    }
}
