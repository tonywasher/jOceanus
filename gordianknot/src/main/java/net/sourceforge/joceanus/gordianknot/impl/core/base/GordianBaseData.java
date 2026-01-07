/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * Base factory.
 */
public abstract class GordianBaseData {
    /**
     *  BCFactoryOID.
     */
    public static final ASN1ObjectIdentifier BCFACTORYOID = GordianASN1Util.FACTORYOID.branch("1");

    /**
     *  JCAFactoryOID.
     */
    public static final ASN1ObjectIdentifier JCAFACTORYOID = GordianASN1Util.FACTORYOID.branch("2");

    /**
     * RC5 rounds.
     */
    public static final int RC5_ROUNDS = 12;

    /**
     * Private constructor.
     */
    private GordianBaseData() {
    }
    /**
     * Build Invalid text string.
     * @param pValue the parameter
     * @return the text
     */
    public static String getInvalidText(final Object pValue) {
        /* Create initial string */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Invalid ");

        /* Build details */
        if (pValue != null) {
            myBuilder.append(pValue.getClass().getSimpleName());
            myBuilder.append(" :- ");
            myBuilder.append(pValue);
        } else {
            myBuilder.append("null value");
        }

        /* Return the string */
        return myBuilder.toString();
    }
}
