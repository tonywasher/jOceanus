/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.data;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedField;

/**
 * Prometheus Set of versioned Values.
 */
public class PrometheusDataVersionValues
        extends MetisDataVersionValues {
    /**
     * The KeySet.
     */
    private GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pItem the associated item
     */
    public PrometheusDataVersionValues(final MetisDataVersionedItem pItem) {
        super(pItem);
    }

    @Override
    public PrometheusDataVersionValues cloneIt() {
        /* Create the valueSet and initialise to existing values */
        PrometheusDataVersionValues mySet = new PrometheusDataVersionValues(getItem());
        mySet.copyFrom(this);
        return mySet;
    }

    @Override
    public void copyFrom(final MetisDataVersionValues pPrevious) {
        /* Perform main copy */
        super.copyFrom(pPrevious);

        /* Copy the keySet */
        if (pPrevious instanceof PrometheusDataVersionValues) {
            PrometheusDataVersionValues myPrevious = (PrometheusDataVersionValues) pPrevious;
            theKeySet = myPrevious.theKeySet;
        }
    }

    @Override
    protected void checkValueType(final MetisDataField pField,
                                  final Object pValue) {
        /* Allow byteArray */
        if (pValue instanceof byte[]) {
            return;
        }

        /* Allow EncryptedField if we have a keySet */
        if (pValue instanceof MetisEncryptedField
            && theKeySet != null) {
            return;
        }

        /* Pass on */
        super.checkValueType(pField, pValue);
    }
}
