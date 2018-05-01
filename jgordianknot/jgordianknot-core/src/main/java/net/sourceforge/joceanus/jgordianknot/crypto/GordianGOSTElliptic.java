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
 * Named GOST-2012 Elliptic Curves.
 */
public enum GordianGOSTElliptic implements GordianElliptic {
    /**
     * CryptoPro-A.
     */
    CRYPTOPROA("GostR3410-2001-CryptoPro-A", 257),

    /**
     * CryptoPro-B.
     */
    CRYPTOPROB("GostR3410-2001-CryptoPro-B", 254),

    /**
     * CryptoPro-C.
     */
    CRYPTOPROC("GostR3410-2001-CryptoPro-C", 255),

    /**
     * CryptoPro-XchA.
     */
    CRYPTOPROXCHA("GostR3410-2001-CryptoPro-XchA", 257),

    /**
     * CryptoPro-XchB.
     */
    CRYPTOPROXCHB("GostR3410-2001-CryptoPro-XchB", 256);

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

    @Override
    public String toString() {
        return theName;
    }
}
