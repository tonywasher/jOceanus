/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.keypair;

/**
 * Named DSTU4145 Elliptic Curves.
 */
public enum GordianDSTU4145Elliptic implements GordianElliptic {
    /**
     * DSTU4145-9.
     */
    DSTU9(9, 431),

    /**
     * DSTU4145-1.
     */
    DSTU1(1, 167),

    /**
     * DSTU4145-2.
     */
    DSTU2(2, 171),

    /**
     * DSTU4145-3.
     */
    DSTU3(3, 177),

    /**
     * DSTU4145-4.
     */
    DSTU4(4, 188),

    /**
     * DSTU4145-5.
     */
    DSTU5(5, 233),

    /**
     * DSTU4145-6.
     */
    DSTU6(6, 248),

    /**
     * DSTU4145-7.
     */
    DSTU7(7, 306),

    /**
     * DSTU4145-8.
     */
    DSTU8(8, 366);

    /**
     * Curve Base.
     */
    private static final String BASE = "1.2.804.2.1.1.1.1.3.1.1.2.";

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
     * @param pIndex the index of the curve
     * @param pSize the bitSize of the curve
     */
    GordianDSTU4145Elliptic(final int pIndex,
                            final int pSize) {
        theName = BASE + pIndex;
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
    public static GordianDSTU4145Elliptic getCurveForName(final String pName) {
        /* Loop through the curves */
        for (GordianDSTU4145Elliptic myCurve: values()) {
            if (pName.equals(myCurve.getCurveName())) {
                return myCurve;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name();
    }
}
