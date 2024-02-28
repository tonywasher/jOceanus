/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersioned;

/**
 * Prometheus Data fields.
 * @param <T> the data type
 */
public class PrometheusEncryptedField<T extends PrometheusEncryptedDataItem>
        extends MetisFieldVersioned<T> {
    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     */
    PrometheusEncryptedField(final PrometheusEncryptedFieldSet<T> pAnchor,
                             final MetisDataFieldId pId,
                             final MetisDataType pDataType,
                             final Integer pMaxLength) {
        /* Initialise underlying class */
        super(pAnchor, pId, pDataType, pMaxLength, true);
    }

    @Override
    protected void checkValidity() {
        /* Check underlying options */
        super.checkValidity();

        /* Object/ByteArray/Links are not valid for Encryption */
        switch (getDataType()) {
            case OBJECT:
            case BYTEARRAY:
            case LINK:
            case LINKSET:
                throw new IllegalArgumentException("Invalid encrypted object");
            default:
                break;
        }
    }
}
