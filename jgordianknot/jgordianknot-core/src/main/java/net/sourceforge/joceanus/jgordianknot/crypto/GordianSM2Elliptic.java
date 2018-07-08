/*******************************************************************************
 * GordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Named SM2 Elliptic Curves.
 */
public enum GordianSM2Elliptic implements GordianElliptic {
    /**
     * sm2p256v1.
     */
    SM2P256V1("sm2p256v1", 256),

    /**
     * wapip192v1.
     */
    WAPIP192V1("wapip192v1", 192);

    /**
     * The curve name.
     */
    private final String theName;

    /**
     * The key size.
     */
    private final int theSize;

    /**
     * Constructor.
     * @param pName the name of the curve
     * @param pSize the bitSize of the curve
     */
    GordianSM2Elliptic(final String pName,
                       final int pSize) {
        theName = pName;
        theSize = pSize;
    }

    @Override
    public String getCurveName() {
        return theName;
    }

    @Override
    public int getKeySize() {
        return theSize;
    }

    /**
     * Obtain the curve for a Name.
     * @param pName the name
     * @return the curve
     */
    public static GordianSM2Elliptic getCurveForName(final String pName) {
        /* Loop through the curves */
        for (GordianSM2Elliptic myCurve: values()) {
            if (pName.equals(myCurve.getCurveName())) {
                return myCurve;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return theName;
    }
}
