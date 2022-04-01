/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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
 * Delta class.
 */
public class MetisDataDelta {
    /**
     * The object itself.
     */
    private final Object theObject;

    /**
     * The difference.
     */
    private final MetisDataDifference theDifference;

    /**
     * Constructor.
     * @param pObject the object
     * @param pDifference the difference
     */
    public MetisDataDelta(final Object pObject,
                          final MetisDataDifference pDifference) {
        theObject = pObject;
        theDifference = pDifference;
    }

    /**
     * Obtain the object.
     * @return the object
     */
    public Object getObject() {
        return theObject;
    }

    /**
     * Obtain the difference.
     * @return the difference
     */
    public MetisDataDifference getDifference() {
        return theDifference;
    }
}
