/* *****************************************************************************
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
package net.sourceforge.joceanus.jprometheus.ui.fieldset;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * FieldSetEvent.
 */
public class PrometheusFieldSetEvent {
    /**
     * The field.
     */
    private final MetisDataFieldId theFieldId;

    /**
     * The new value.
     */
    private final Object theValue;

    /**
     * Constructor.
     * @param pFieldId the source fieldId
     * @param pNewValue the new Value
     */
    public PrometheusFieldSetEvent(final MetisDataFieldId pFieldId,
                                   final Object pNewValue) {
        theFieldId = pFieldId;
        theValue = pNewValue;
    }

    /**
     * Obtain the source field.
     * @return the field
     */
    public MetisDataFieldId getFieldId() {
        return theFieldId;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public Object getValue() {
        return theValue;
    }

    /**
     * Obtain the value as specific type.
     * @param <T> the value class
     * @param pClass the required class
     * @return the value
     * @throws OceanusException on error
     */
    public <T> T getValue(final Class<T> pClass) throws OceanusException {
        try {
            return pClass.cast(theValue);
        } catch (ClassCastException e) {
            throw new PrometheusDataException("Invalid dataType", e);
        }
    }
}
