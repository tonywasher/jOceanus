/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Named Elliptic Curves.
 */
public enum GordianElliptic {
    /**
     * sect571r1.
     */
    SECT571R1("sect571r1"),

    /**
     * sect571k1.
     */
    SECT571K1("sect571k1"),

    /**
     * secp521r1.
     */
    SECP521R1("secp521r1"),

    /**
     * c2tnb431r1.
     */
    C2TNB431R1("c2tnb431r1"),

    /**
     * sect409k1.
     */
    SECT409K1("sect409k1"),

    /**
     * sect409r1.
     */
    SECT409R1("sect409r1"),

    /**
     * secp384r1.
     */
    SECP384R1("secp384r1"),

    /**
     * c2pnb368w1.
     */
    C2PNB368W1("c2pnb368w1"),

    /**
     * c2tnb359r1.
     */
    C2TNB359R1("c2tnb359r1"),

    /**
     * c2pnb304w1.
     */
    C2PNB304W1("c2pnb304w1"),

    /**
     * sect283k1.
     */
    SECT283K1("sect283k1"),

    /**
     * sect283r1.
     */
    SECT283R1("sect283r1"),

    /**
     * c2pnb272w1.
     */
    C2PNB272W1("c2pnb272w1"),

    /**
     * secp256r1.
     */
    SECP256R1("secp256r1"),

    /**
     * secp256k1.
     */
    SECP256K1("secp256k1"),

    /**
     * prime256v1.
     */
    PRIME256V1("prime256v1"),

    /**
     * brainpoolp512r1.
     */
    BRAINPOOLP512R1("brainpoolp512r1"),

    /**
     * brainpoolp512t1.
     */
    BRAINPOOLP512T1("brainpoolp512t1"),

    /**
     * brainpoolp384r1.
     */
    BRAINPOOLP384R1("brainpoolp384r1"),

    /**
     * brainpoolp384t1.
     */
    BRAINPOOLP384T1("brainpoolp384t1"),

    /**
     * brainpoolp320r1.
     */
    BRAINPOOLP320R1("brainpoolp320r1"),

    /**
     * brainpoolp320t1.
     */
    BRAINPOOLP320T1("brainpoolp320t1"),

    /**
     * brainpoolP256r1.
     */
    BRAINPOOLP256R1("brainpoolp256r1"),

    /**
     * brainpoolP256t1.
     */
    BRAINPOOLP256T1("brainpoolp256t1");

    /**
     * The curve name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pName the name of the curve
     */
    GordianElliptic(final String pName) {
        theName = pName;
    }

    /**
     * Obtain the name of the curve.
     * @return the name
     */
    public String getCurveName() {
        return theName;
    }

    @Override
    public String toString() {
        return theName;
    }
}
