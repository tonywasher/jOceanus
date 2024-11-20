/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.metis.field;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;

/**
 * Simple class to convert a string into a FieldId.
 */
public class MetisFieldSimpleId
        implements MetisDataFieldId {
    /**
     * The Id.
     */
    private final String theId;

    /**
     * Constructor.
     * @param pId the Id
     */
    public MetisFieldSimpleId(final String pId) {
        theId = pId;
    }

    @Override
    public String getId() {
        return theId;
    }

    /**
     * Convert a resourceId to fieldId.
     * @param pId the resourceId
     * @return the fieldId
     */
    public static MetisDataFieldId convertResource(final OceanusBundleId pId) {
        return pId instanceof MetisDataFieldId
                                               ? (MetisDataFieldId) pId
                                               : new MetisFieldSimpleId(pId.getValue());
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof MetisFieldSimpleId)) {
            return false;
        }

        /* Access as MetisFieldSimpleId */
        final MetisFieldSimpleId myThat = (MetisFieldSimpleId) pThat;

        /* Check the Id is the same */
        return theId.equals(myThat.theId);
    }

    @Override
    public int hashCode() {
        return theId.hashCode();
    }
}
