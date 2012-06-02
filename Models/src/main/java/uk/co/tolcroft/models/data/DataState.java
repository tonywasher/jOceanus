/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.data;

/**
 * Enumeration of states of DataItem and DataList objects.
 */
public enum DataState {
    /**
     * No known state.
     */
    NOSTATE,

    /**
     * New object.
     */
    NEW,

    /**
     * Clean object with no changes.
     */
    CLEAN,

    /**
     * Changed object with history.
     */
    CHANGED,

    /**
     * Deleted Clean object.
     */
    DELETED,

    /**
     * Deleted New object.
     */
    DELNEW,

    /**
     * Deleted Changed object.
     */
    DELCHG,

    /**
     * Recovered deleted object.
     */
    RECOVERED;
}
