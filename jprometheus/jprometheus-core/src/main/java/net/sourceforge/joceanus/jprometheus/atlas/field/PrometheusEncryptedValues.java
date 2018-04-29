/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.field;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Prometheus Set of versioned Values.
 */
public class PrometheusEncryptedValues
        extends MetisFieldVersionValues {
    /**
     * The KeySet.
     */
    private GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pItem the associated item
     */
    public PrometheusEncryptedValues(final PrometheusEncryptedItem pItem) {
        super(pItem);
    }

    @Override
    public PrometheusEncryptedValues cloneIt() {
        /* Create the valueSet and initialise to existing values */
        final PrometheusEncryptedValues mySet = new PrometheusEncryptedValues(getItem());
        mySet.copyFrom(this);
        return mySet;
    }

    @Override
    protected PrometheusEncryptedItem getItem() {
        return (PrometheusEncryptedItem) super.getItem();
    }

    @Override
    public void copyFrom(final MetisFieldVersionValues pPrevious) {
        /* Perform main copy */
        super.copyFrom(pPrevious);

        /* Copy the keySet */
        if (pPrevious instanceof PrometheusEncryptedValues) {
            final PrometheusEncryptedValues myPrevious = (PrometheusEncryptedValues) pPrevious;
            theKeySet = myPrevious.theKeySet;
        }
    }

    @Override
    protected void checkValueType(final MetisFieldDef pField,
                                  final Object pValue) throws OceanusException {
        /* Allow byteArray */
        if (pValue instanceof byte[]) {
            return;
        }

        /* Allow EncryptedValue if we have a keySet */
        if (pValue instanceof MetisFieldEncryptedValue
            && theKeySet != null) {
            return;
        }

        /* Pass on */
        super.checkValueType(pField, pValue);
    }
}
