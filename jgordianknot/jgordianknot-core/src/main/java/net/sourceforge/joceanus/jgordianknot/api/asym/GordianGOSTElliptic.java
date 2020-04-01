/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.asym;

/**
 * Named GOST-2012 Elliptic Curves.
 */
public enum GordianGOSTElliptic implements GordianElliptic {
    /**
     * 512-paramSetA.
     */
    GOST512A("Tc26-Gost-3410-12-512-paramSetA", 512),

    /**
     * 512-paramSetB.
     */
    GOST512B("Tc26-Gost-3410-12-512-paramSetB", 512),

    /**
     * 512-paramSetC.
     */
    GOST512C("Tc26-Gost-3410-12-512-paramSetC", 512),

    /**
     * 256-paramSetA.
     */
    GOST256A("Tc26-Gost-3410-12-256-paramSetA", 256);

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
    GordianGOSTElliptic(final String pName,
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
    public static GordianGOSTElliptic getCurveForName(final String pName) {
        /* Loop through the curves */
        for (GordianGOSTElliptic myCurve: values()) {
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

