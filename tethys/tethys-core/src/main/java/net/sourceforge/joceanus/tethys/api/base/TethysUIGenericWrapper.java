/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.api.base;

/**
 * Scroll Wrapper class.
 * <p>Used to wrap a generic class for IconButton/ScrollButton</p>
 */
public class TethysUIGenericWrapper {
    /**
     * The wrapped data.
     */
    private final Object theData;

    /**
     * Constructor.
     * @param pData th data
     */
    public TethysUIGenericWrapper(final Object pData) {
        theData = pData;
    }

    /**
     * Obtain the data.
     * @return the data
     */
    public Object getData() {
        return theData;
    }

    @Override
    public String toString() {
        return theData.toString();
    }
}
