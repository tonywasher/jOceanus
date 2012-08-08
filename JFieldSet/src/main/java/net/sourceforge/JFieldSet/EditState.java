/*******************************************************************************
 * JDataModels: Data models
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataModels.data;

/**
 * Enumeration of edit states of DataItem and DataList objects in a view.
 */
public enum EditState {
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
    public EditState combineState(final EditState pState) {
        if ((this == ERROR) || (pState == ERROR)) {
            return ERROR;
        }
        if ((this == DIRTY) || (pState == DIRTY)) {
            return DIRTY;
        }
        if ((this == VALID) || (pState == VALID)) {
            return VALID;
        }
        return CLEAN;
    }
}
